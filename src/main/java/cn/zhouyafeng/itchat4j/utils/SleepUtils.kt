package cn.zhouyafeng.itchat4j.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

/**
 * Created by xiaoxiaomo on 2017/5/6.
 */
object SleepUtils {
    private val LOG: Logger = LoggerFactory.getLogger(SleepUtils::class.java)
    /**
     * 毫秒为单位
     *
     * @param time
     */
    fun sleep(time: Long, unit: TimeUnit) {
        try {
            unit.sleep(time)
        } catch (e: InterruptedException) {
            LOG.error("sleep interrupted", e)
        }
    }
}
