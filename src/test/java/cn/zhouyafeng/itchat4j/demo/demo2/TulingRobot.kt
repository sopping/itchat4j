package cn.zhouyafeng.itchat4j.demo.demo2

import java.io.File
import java.util.Date
import java.util.HashMap
import java.util.logging.Logger

import cn.zhouyafeng.itchat4j.face.SimpleReplyMsgHandler
import org.apache.http.HttpEntity
import org.apache.http.util.EntityUtils

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject

import cn.zhouyafeng.itchat4j.Wechat
import cn.zhouyafeng.itchat4j.beans.BaseMsg
import cn.zhouyafeng.itchat4j.core.Core
import cn.zhouyafeng.itchat4j.face.IMsgHandlerFace
import cn.zhouyafeng.itchat4j.utils.MyHttpClient
import cn.zhouyafeng.itchat4j.utils.enums.MsgTypeGroupEnum
import cn.zhouyafeng.itchat4j.utils.tools.DownloadTools

/**
 * 图灵机器人示例
 *
 * @author https://github.com/yaphone
 * @date 创建时间：2017年4月24日 上午12:13:26
 * @version 1.0
 */
class TulingRobot : SimpleReplyMsgHandler() {
    internal var logger = Logger.getLogger("TulingRobot")
    internal var myHttpClient = Core.getInstance().myHttpClient
    internal var url = "http://www.tuling123.com/openapi/api"
    internal var apiKey = "597b34bea4ec4c85a775c469c84b6817" // 这里是我申请的图灵机器人API接口，每天只能5000次调用，建议自己去申请一个，免费的:)

    override fun textMsgHandle(msg: BaseMsg): String {
        var result = ""
        val text = msg.text
        val paramMap = HashMap<String, String>()
        paramMap.put("key", apiKey)
        paramMap.put("info", text)
        paramMap.put("userid", "123456")
        val paramStr = JSON.toJSONString(paramMap)
        try {
            val entity = myHttpClient.doPost(url, paramStr)
            result = EntityUtils.toString(entity!!, "UTF-8")
            val obj = JSON.parseObject(result)
            if (obj.getString("code") == "100000") {
                result = obj.getString("text")
            } else {
                result = "处理有误"
            }
        } catch (e: Exception) {
            logger.info(e.message)
        }

        return result
    }

    override fun picMsgHandle(msg: BaseMsg): String {
        return "收到图片"
    }

    override fun voiceMsgHandle(msg: BaseMsg): String {
        val fileName = Date().time.toString()
        val voicePath = "D://itchat4j/voice" + File.separator + fileName + ".mp3"
        DownloadTools.getDownloadFn(msg, MsgTypeGroupEnum.VOICE, voicePath)
        return "收到语音"
    }

    override fun viedoMsgHandle(msg: BaseMsg): String {
        val fileName = Date().time.toString()
        val viedoPath = "D://itchat4j/viedo" + File.separator + fileName + ".mp4"
        DownloadTools.getDownloadFn(msg, MsgTypeGroupEnum.VIDEO, viedoPath)
        return "收到视频"
    }


    override fun nameCardMsgHandle(msg: BaseMsg): String {
        // TODO Auto-generated method stub
        return ""
    }

    override fun sysMsgHandle(msg: BaseMsg) {
        // TODO Auto-generated method stub
    }

    override fun verifyAddFriendMsgHandle(msg: BaseMsg): String {
        // TODO Auto-generated method stub
        return ""
    }

    override fun mediaMsgHandle(msg: BaseMsg): String {
        // TODO Auto-generated method stub
        return ""
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val msgHandler = TulingRobot()
            val wechat = Wechat(msgHandler)
            wechat.start()
        }
    }

}
