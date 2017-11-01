package cn.zhouyafeng.itchat4j

import cn.zhouyafeng.itchat4j.controller.LoginController
import cn.zhouyafeng.itchat4j.core.MsgCenter
import cn.zhouyafeng.itchat4j.face.IMsgHandlerFace
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors

class Wechat(private val msgHandler: IMsgHandlerFace) {

    init {
        System.setProperty("jsse.enableSNIExtension", "false") // 防止SSL错误

        // 登陆
        LoginController().login()
    }

    fun start() {
        LOG.info("+++++++++++++++++++开始消息处理+++++++++++++++++++++")
        Executors.newSingleThreadExecutor().submit { MsgCenter.handleMsg(msgHandler) }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(Wechat::class.java)
    }

}
