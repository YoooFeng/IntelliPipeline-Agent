/**
 * Created by Summer on 2018/3/7.
 */
package com.iscas.yf.IntelliPipeline

@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7')
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.RESTClient
import groovyx.net.http.HttpResponseDecorator
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*



public class intelliAgent implements Serializable{

    def scripts
    def currentBuild

    // 构造函数
    intelliAgent(scripts, currentBuild){
        this.scripts = scripts
        this.currentBuild = currentBuild
    }

    def keepGetting() {
        // 持续发送HTTP请求的指示器
        def count = 0
        def flag = false
        def info = "Nothing"
        def myExecutor = new scriptExecutor(this.scripts, this.currentBuild)
        def myConverter = new stepConverter(this.scripts, this.currentBuild)

//        // 循环的设置有问题，
//        while(flag) {
//
//        }
        try {
            // TODO: 如何接受返回来的字符串？
            // 创建一个Http对象，向服务端发送请求
            def http = new HTTPBuilder()
            http.request('http://localhost:8180', GET, TEXT) { req ->
                // 设置url相关信息 - http://localhost:8180/IntelliPipeline/upload
                uri.path='/IntelliPipeline/upload'

                // 设置请求头信息
                headers.'User-Agent' = "Mozilla/5.0 Firefox/3.0.4"

                // 设置成功响应的处理闭包
                response.success= {resp,reader->
                    // 成功响应返回的200状态码
                    assert resp.statusLine.statusCode == 200

                    // 打印返回的详细信息
                    logger resp.status
                    logger resp.statusLine.statusCode
                    logger resp.headers.'content-length'

                    logger "INTENTION! RETURNED TEXT IS BELOW!"
                    logger reader.text

                    // 把返回来的字符串赋值给变量info
                    info = reader.text

                    // TODO: 测试看能否收到成功返回就执行scripts中的步骤
                    myExecutor.execution()

                    // 执行5个stage就退出
                    count += 1
                    if(count == 5) {
                        flag = false
                    }

                    logger "My response handler got response: ${resp.statusLine}"
                    logger "Response length: ${resp.headers.'Content-Length'}"
                    System.out << reader // print response stream
                }

                // 404
                response.'404' = {
                    logger 'not found'
                }

                // 401
                http.handler.'401' = { resp ->
                    logger "Access denied"
                }

                // 未根据响应码指定的失败处理闭包
                response.failure = { println "Unexpected failure: ${resp.statusLine}" }
            }
        } catch (err) {
            logger "Error occurred:" + err
            throw err
        }
    }

    // 控制台打印信息
    def logger(msg) {
        this.scripts.steps.prinln(msg)
    }
}



