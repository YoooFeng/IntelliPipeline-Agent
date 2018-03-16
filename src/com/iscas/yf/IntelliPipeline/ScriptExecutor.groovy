package com.iscas.yf.IntelliPipeline

/**
 * Created by Summer on 2018/3/9.
 */
import groovy.util.Eval

class ScriptExecutor {

    // 加一个static关键字是否后续的scripts都不用加this前缀？
    def scripts
    def currentBuild
    def info = "nothing"


    ScriptExecutor(scripts, currentBuild){
        this.scripts = scripts
        this.currentBuild = currentBuild
    }

    // TODO: 增加try-catch代码块进行错误处理，防止遇到错误之后pipeline engine将整个流程Abort掉
    // 这个方法不能声明为static
    def execution() {
        /**
         * 直接调用GroovyShell的evaluate方法执行代码段,
         * scripts没有传进去, 因为new的GroovyShell是在另一个进程空间的，相当于打开了一个新的控制台
         * Eval.me也不能用，原理跟new GroovyShell相同
         * */
        try{
            // 是否需要新建一个node来执行step操作? 不指定Node默认就在本机执行step
            // 通过直接传递step name和Map类型的参数的方式执行步骤！
            // invokeMethod验证通过
            def Map<String, String> param = new HashMap<>()
            param.put("message", "I am a param!!")
            this.scripts.steps.invokeMethod("echo", param)

        } catch(err) {
            // 先catch到步骤执行不成功的控制台输出
            this.currentBuild.result = 'FAILURE'
            // 这里不throw, Jenkins Pipeline就不会Abort
            throw err
        }
    }

    def execution(String stepName, Map<String, String> params){
        try{
            this.scripts.steps.invokeMethod(stepName, params)
        } catch(err) {
            this.currentBuild.result = 'FAILURE'
            throw err
        }
    }
}
