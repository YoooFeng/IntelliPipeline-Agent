package com.iscas.yf.IntelliPipeline

/**
 * Created by Summer on 2018/3/7.
 */

class stepConverter {
    def scripts
    def currentBuild
    def info

    /**
     * @Param：scripts Jenkins的pipeline执行引擎实例
     * @Param：currentBuild 当前构建对象的实例
     * @Param：info 从IntelliPipeline发来的需要操作的信息
     * */
    stepConverter(scripts, currentBuild) {
        this.scripts = scripts
        this.currentBuild = currentBuild
        this.info = info
    }



}
