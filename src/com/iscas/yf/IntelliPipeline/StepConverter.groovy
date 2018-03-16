package com.iscas.yf.IntelliPipeline

/**
 * Created by Summer on 2018/3/7.
 */
//import groovy.json.*

class StepConverter {
    def scripts
    def currentBuild

    /**
     * @Param：scripts Jenkins的pipeline执行引擎实例
     * @Param：currentBuild 当前构建对象的实例
     * */
    StepConverter(scripts, currentBuild) {
        this.scripts = scripts
        this.currentBuild = currentBuild
    }

    /**
     * 对返回的Response内容进行预处理
     * @Param: responseJson Server端返回的string格式的Json-Step信息
     */
    def responseResolver(String responseJson){
        // def parsedBody = new JsonSlurper().parseText(responseJson)
        // readJSON需要安装插件pipeline-utility-steps
        def parsedBody = this.scripts.steps.readJSON(text: responseJson)
        return parsedBody
    }

    // 枚举所有可执行的步骤？
    def convertToStep(step){

    }

    // 是否要支持stage粒度
    def convertToStage(stage) {

    }

}
