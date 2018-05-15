/**
 * Created by Summer on 2018/3/7.
 */
package com.iscas.yf.IntelliPipeline

//@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7')
//import groovyx.net.http.HTTPBuilder
//import static groovyx.net.http.ContentType.*
//import static groovyx.net.http.Method.*



public class IntelliAgent{

    def scripts
    def currentBuild

    // 构造函数
    IntelliAgent(scripts, currentBuild){
        this.scripts = scripts
        this.currentBuild = currentBuild
    }

    @NonCPS
    def keepGetting() {

        // 持续发送HTTP请求的指示器
        def stepNumber = 1

        def flag = true

        // 没有执行step，request type为initializing
        def requestType = "INIT"
        def myExecutor = new ScriptExecutor(this.scripts, this.currentBuild)
        def myConverter = new StepConverter(this.scripts, this.currentBuild)

        // 将代码片段放入node代码段中
//        scripts.node{
//            logger "node block entering"
//
//            try {
//                logger "Try-catch block entering"
//                // TODO: 如何接受返回来的字符串？
//                // 创建一个Http对象，向服务端发送请求
//                def http = new HTTPBuilder('http://localhost:8180')
//                http.request(GET, TEXT) { req ->
//                    // 设置url相关信息 - http://localhost:8180/IntelliPipeline/upload
//                    uri.path='/IntelliPipeline/upload'
//
//                    // 设置请求头信息
//                    headers.'User-Agent' = "Mozilla/5.0 Firefox/3.0.4"
//
//                    // 设置成功响应的处理闭包
//                    response.success= { resp,reader->
//                        // 成功响应返回的200状态码
//                        assert resp.statusLine.statusCode == 200
//
//                        // 打印返回的详细信息
//                        logger resp.status
//                        logger resp.statusLine.statusCode
//                        logger resp.headers.'content-length'
//
//                        logger "INTENTION! RETURNED TEXT IS BELOW!"
//                        logger reader.text
//
//                        // 把返回来的字符串赋值给变量info
//                        info = reader.text
//
//                        // TODO: 测试看能否收到成功返回就执行scripts中的步骤
//                        myExecutor.execution()
//
//                        // 执行5个stage就退出
//                        count += 1
//                        if(count == 5) {
//                            flag = false
//                        }
//
//                        logger "My response handler got response: ${resp.statusLine}"
//                        logger "Response length: ${resp.headers.'Content-Length'}"
//                        System.out << reader // print response stream
//                    }
//
//                    // 404
//                    response.'404' = {
//                        logger 'not found'
//                    }
//
//                    // 401
//                    http.handler.'401' = { resp ->
//                        logger "Access denied"
//                    }
//
//                    // 未根据响应码指定的失败处理闭包
//                    response.failure = { logger "Unexpected failure: ${resp.statusLine}" }
//                    logger 'End of sending request'
//                }
//            } catch (err) {
//                logger "Error occurred:" + err
//                throw err
//            }
//        }

        try {
            while(flag){

                // changeSets，只需要在开始构建时发送一次
                // 直接把changeSets这个对象发回去，客户端再进行处理(ArrayList), 如何传输一个对象？不可行
                // changeSets是两次build之间
                def changeSets = this.scripts.currentBuild.changeSets

                def String commitSet = "";

                // 处理changeSets
                for(int i = 0; i < changeSets.size(); i++){
                    def entries = changeSets[i].items
                    for(int j = 0; j < entries.length; j++){
                        def entry = entries[j]
                        // 将所有的commit都加入到changeLog中, 不同的commit用[]分割
                        commitSet += "[${entry.commitId} : ${entry.author} : ${entry.msg}] "
                    }
                }

                // Jenkins当前构建的控制台输出. getLog(maxLine) 获取最新的指定行数的log， 类型List<String>
                def consoleOutput = this.scripts.currentBuild.rawBuild.getLogInputStream()
                byte[] bytes = new byte[consoleOutput.available()]
                consoleOutput.read(bytes);
                String consoleStr = new String(bytes)
                logger(consoleStr)

                // 当前构建的持续时间，单位毫秒
                def durationTime = this.scripts.currentBuild.duration

                def currentResult = this.scripts.currentBuild.currentResult
                logger(currentResult)

                def body = """ """

                // POST body, 只在第一次时发送changeSet? 网络传输优化
                // 参数都放到body里面来，不要放在url中！
                body = """
                    {"requestType": "$requestType",
                     "stepNumber": "$stepNumber",
                     "currentResult": "$currentResult",
                     "commitSet": "$commitSet",
                     "consoleOutput": "$consoleStr",
                     "durationTime": "$durationTime"}
                """


                // 发送POST Request
                def response = scripts.steps.httpRequest(
                        acceptType:'APPLICATION_JSON',
                        contentType:'APPLICATION_JSON',
                        httpMode:'POST',
                        requestBody: body,
                        url: "http://localhost:8180/IntelliPipeline/build_data/upload")

                logger('Status:' + response.status)

                // Response为空？
                logger('Response:' + response.content)

                // 先获取返回的decision
                def String decision = myConverter.responseResolverOfDecision(response.content)
                assert decision instanceof String
                if(decision == "END"){
                    break;
                }


                // 发送到converter进行解析, 分别获取stepName和stepParams
                def Map<String, Object> stepParams = myConverter.responseResolverOfParams(response.content)
                assert stepParams instanceof Map<String, Object>

                def String stepName = myConverter.responseResolverOfName(response.content)
                assert stepName instanceof String



                // 处理decision的各种情况
                // 执行下一个step
                if(decision.equals("NEXT")){
                    stepNumber++
                    requestType = "RUNNING"
                }
                // build流程结束
                else if(decision.equals("END")){
                    flag = false
                }
                // 重试当前步骤， 不作操作继续请求同一个step
                else if(decision.equals("RETRY")){

                }

                // mock as "continue"
//                def decision = parsedBody.decisionType;
//                logger "decision:" + decision
//
//                def exeStep = parsedBody.executionStep;
//                logger "step:" + exeStep


                // 返回码从100-399，200表示成功返回。状态码不是String类型，是int类型

                if(response.status == 200){
                    // 调用invokeMethod方法执行step
                    myExecutor.execution(stepName, stepParams)
                    // stageNumber += 1
                    // 执行step之后，返回json的分析数据，等待决策
                    requestType = "consulting"
                } else {
                    // 出现网络错误，暂时退出. 应重发
                    logger "Network connection error occurred"
                    break;
                }

//                if(stageNumber > 8) {
//                    flag = false
//                }
                // 不是GroovyShell类型
                // assert this.scripts instanceof GroovyShell
                // this.scripts.getClass() == intelliPipelineProxy
                // logger(this.scripts.steps.getClass().toString())

            }
        } catch(err) {
            logger "An error occurred: " + err
            // 执行出错了
            // requestType = "error"
            throw err
        }
    }

    // 控制台打印信息
    def logger(msg) {
        this.scripts.steps.echo(msg)
    }
}



