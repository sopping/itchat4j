package cn.zhouyafeng.itchat4j.thread

import cn.zhouyafeng.itchat4j.core.Core
import cn.zhouyafeng.itchat4j.utils.SleepUtils
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

/**
 * 检查微信在线状态
 *
 *
 * 如何来感知微信状态？
 * 微信会有心跳包，LoginServiceImpl.syncCheck()正常在线情况下返回的消息中retcode报文应该为"0"，心跳间隔一般在25秒，
 * 那么可以通过最后收到正常报文的时间来作为判断是否在线的依据。若报文间隔大于60秒，则认为已掉线。
 *
 *
 * @author https://github.com/yaphone
 * @version 1.0
 * @date 创建时间：2017年5月17日 下午10:53:15
 */
class CheckLoginStatusThread : Runnable {
    private val core = Core.getInstance()

    override fun run() {
        while (core.isAlive) {
            if (System.currentTimeMillis() - core.lastNormalRetcodeTime > TimeUnit.MINUTES.toMillis(1)) { // 超过60秒，判为离线
                core.isAlive = false
                LOG.info("微信已离线")
            }
            SleepUtils.sleep(10, TimeUnit.SECONDS) // 休眠10秒
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(CheckLoginStatusThread::class.java)
    }

}
