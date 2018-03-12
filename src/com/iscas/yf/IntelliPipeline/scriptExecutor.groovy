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

    // TODO: 增加try-catch代码块进行错误处理，防止遇到错误之后pipeline engine将整个流程Abort掉
    public static execution() {

        // 新建一个node来执行step操作
        scripts.node{
            scripts.steps.echo("Received information from local server!")
        }
    }
}
