package cn.zhouyafeng.itchat4j.core

import cn.zhouyafeng.itchat4j.beans.BaseMsg
import cn.zhouyafeng.itchat4j.beans.Contact
import cn.zhouyafeng.itchat4j.face.IMsgHandlerFace
import cn.zhouyafeng.itchat4j.utils.enums.MsgTypeEnum
import cn.zhouyafeng.itchat4j.utils.enums.MsgTypeGroupEnum
import cn.zhouyafeng.itchat4j.utils.tools.CommonTools
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

/**
 * 消息处理中心
 *
 * @author https://github.com/yaphone
 * @version 1.0
 * @date 创建时间：2017年5月14日 下午12:47:50
 */
object MsgCenter {
    private val LOG = LoggerFactory.getLogger(MsgCenter::class.java)

    private val core = Core.getInstance()

    /**
     * 消息处理
     *
     * @param msgHandler
     * @author https://github.com/yaphone
     * @date 2017年5月14日 上午10:52:34
     */
    fun handleMsg(msgHandler: IMsgHandlerFace) {

        try{
            while (true) {
                val msg: BaseMsg? = core.msgQueue.poll(1, TimeUnit.SECONDS)
                if (msg == null || msg.content.isEmpty()) {
                    continue
                }
                msg.preHandle(core)
                msgHandler.handle(msg)
            }
        }catch (e:Exception){
            LOG.error("handle message error", e)
        }
    }
}
