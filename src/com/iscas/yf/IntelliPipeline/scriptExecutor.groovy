package com.iscas.yf.IntelliPipeline

/**
 * Created by Summer on 2018/3/9.
 */
class scriptExecutor {

    // 加一个static关键字是否后续的scripts都不用加this前缀？
    def scripts
    def currentBuild
    def info = "nothing"


    scriptExecutor(scripts, currentBuild){
        this.scripts = scripts
        this.currentBuild = currentBuild
    }

    // TODO: 增加try-catch代码块进行错误处理，防止遇到错误之后pipeline engine将整个流程Abort掉
    // 这个方法不能声明为static
    def execution() {
        try{
            // 新建一个node来执行step操作
            this.scripts.node{
                this.scripts.steps.echo("Received information from local server!")
            }
        } catch(err) {
            // 先catch到步骤执行不成功的控制台错误，进行处理
            this.currentBuild.result = 'FAILURE'
        }
    }
}
