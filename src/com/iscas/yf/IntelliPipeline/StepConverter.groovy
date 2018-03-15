package com.iscas.yf.IntelliPipeline

/**
 * Created by Summer on 2018/3/7.
 */
import groovy.json.*

class StepConverter {
    def scripts
    def currentBuild
    def info

    /**
     * @Param：scripts Jenkins的pipeline执行引擎实例
     * @Param：currentBuild 当前构建对象的实例
     * @Param：info 从IntelliPipeline发来的需要操作的信息
     * */
    StepConverter(scripts, currentBuild) {
        this.scripts = scripts
        this.currentBuild = currentBuild
    }

    def responseResolver(String responseJson){
        def parsedBody = new JsonSlurper().parseText(responseJson)
        return parsedBody
    }

    // 枚举所有可执行的步骤？
    def convertToStep(step){

    }

    // 是否要支持stage粒度
    def convertToStage(stage) {

    }

}
