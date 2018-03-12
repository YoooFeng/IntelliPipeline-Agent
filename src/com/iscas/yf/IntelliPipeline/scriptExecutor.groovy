package com.iscas.yf.IntelliPipeline

/**
 * Created by Summer on 2018/3/9.
 */
class scriptExecutor {

    def scripts
    def currentBuild
    def info = "nothing"


    scriptExecutor(scripts, currentBuild){
        this.scripts = scripts
        this.currentBuild = currentBuild
    }

    public static execution() {

        // 新建一个node来执行step操作
        scripts.node{
            scripts.steps.echo("Received information from local server!")
        }
    }
}
