/**
 * Created by Summer on 2018/3/12.
 */
import com.iscas.yf.IntelliPipeline.IntelliAgent

// 使用这个入口
def call(body) {
    def userConfig = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = userConfig
    body()

    // this的类型是WorkflowScripts
    myIntelliAgent = new IntelliAgent(this, currentBuild)
    myIntelliAgent.keepGetting()
}