package cn.zhouyafeng.itchat4j.demo.demo1

import cn.zhouyafeng.itchat4j.api.MessageTools
import cn.zhouyafeng.itchat4j.api.WechatTools
import cn.zhouyafeng.itchat4j.beans.BaseMsg
import cn.zhouyafeng.itchat4j.core.Core
import cn.zhouyafeng.itchat4j.face.SimpleReplyMsgHandler
import cn.zhouyafeng.itchat4j.utils.enums.MsgTypeGroupEnum
import cn.zhouyafeng.itchat4j.utils.tools.DownloadTools
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.InetAddress
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.*

/**
 * 简单示例程序，收到文本信息自动回复原信息，收到图片、语音、小视频后根据路径自动保存
 *
 * @author https://github.com/yaphone
 * @date 创建时间：2017年4月25日 上午12:18:09
 * @version 1.0
 */
class SimpleDemo : SimpleReplyMsgHandler() {
    companion object {
        private val LOG = LoggerFactory.getLogger(SimpleDemo::class.java)
    }

    override fun textMsgHandle(msg: BaseMsg): String {
        // String docFilePath = "D:/itchat4j/pic/1.jpg"; // 这里是需要发送的文件的路径
        if (!msg.groupMsg) {
            // 发送文本消息，也可调用MessageTools.sendFileMsgByUserId(userId,text);
            val text = msg.content
            LOG.info(text)
            if (text == "111") {
                WechatTools.logout()
            }
            if (text == "222") {
                WechatTools.remarkNameByNickName("yaphone", "Hello")
            }
            if (text == "333") { // 测试群列表
                LOG.info("group nick names: {}", WechatTools.groupNickNameList)
                LOG.info("group ids: {}", WechatTools.groupIdList)
                LOG.info("group member map: {}", Core.getInstance().groupMemberMap)
            }

            if (text == "ip") {
                try {
                    return InetAddress.getLocalHost().hostAddress
                } catch (e: UnknownHostException) {
                    e.printStackTrace()
                }

            }
            // bash:开头直接执行指令，返回
            if (text.startsWith("bash:")) {
                val bash = text.substringAfter("bash:")
                return try {
                    val process = Runtime.getRuntime().exec(bash)
                    syncTask(process)
                } catch (e: Exception) {
                    LOG.error("execute bash fail", e)
                    "execute fail: " + e.message
                }
            }
        }
        return ""
    }

    private fun syncTask(process: Process): String {
        val stdInput = BufferedReader(InputStreamReader(process.inputStream))
        val stdError = BufferedReader(InputStreamReader(process.errorStream))
        val sb = StringBuffer()
        val code = process.waitFor()
        if (code != 0) {
            sb.append("out:\n").append(stdInput.readLines().joinToString("\n"))
            sb.append("error:\n").append(stdError.readLines().joinToString("\n"))
            sb.append("code: ").append(code)
        } else {
            return stdInput.readLines().joinToString("\n")
        }
        return sb.toString()
    }

    override fun picMsgHandle(msg: BaseMsg): String {
        val fileName = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(Date())// 这里使用收到图片的时间作为文件名
        val picPath = "D://itchat4j/pic" + File.separator + fileName + ".jpg" // 调用此方法来保存图片
        DownloadTools.getDownloadFn(msg, MsgTypeGroupEnum.PIC, picPath) // 保存图片的路径
        return "图片保存成功"
    }

    override fun voiceMsgHandle(msg: BaseMsg): String {
        val fileName = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(Date())
        val voicePath = "D://itchat4j/voice" + File.separator + fileName + ".mp3"
        DownloadTools.getDownloadFn(msg, MsgTypeGroupEnum.VOICE, voicePath)
        return "声音保存成功"
    }

    override fun viedoMsgHandle(msg: BaseMsg): String {
        val fileName = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(Date())
        val viedoPath = "D://itchat4j/viedo" + File.separator + fileName + ".mp4"
        DownloadTools.getDownloadFn(msg, MsgTypeGroupEnum.VIDEO, viedoPath)
        return "视频保存成功"
    }

    override fun nameCardMsgHandle(msg: BaseMsg): String {
        return "收到名片消息"
    }

    override fun sysMsgHandle(msg: BaseMsg) { // 收到系统消息
        val text = msg.content
        LOG.info(text)
    }

    override fun verifyAddFriendMsgHandle(msg: BaseMsg): String {
        MessageTools.addFriend(msg, true) // 同意好友请求，false为不接受好友请求
        val recommendInfo = msg.recommendInfo
        val nickName = recommendInfo.nickName
        val province = recommendInfo.province
        val city = recommendInfo.city
        return "你好，来自" + province + city + "的" + nickName + "， 欢迎添加我为好友！"
    }

    override fun mediaMsgHandle(msg: BaseMsg): String {
        val fileName = msg.fileName
        val filePath = "D://itchat4j/file" + File.separator + fileName // 这里是需要保存收到的文件路径，文件可以是任何格式如PDF，WORD，EXCEL等。
        DownloadTools.getDownloadFn(msg, MsgTypeGroupEnum.MEDIA, filePath)
        return "文件" + fileName + "保存成功"
    }
}
