package cn.zhouyafeng.itchat4j.demo.demo3

import cn.zhouyafeng.itchat4j.Wechat
import cn.zhouyafeng.itchat4j.api.WechatTools
import cn.zhouyafeng.itchat4j.beans.BaseMsg
import cn.zhouyafeng.itchat4j.core.Core
import cn.zhouyafeng.itchat4j.face.SimpleReplyMsgHandler
import cn.zhouyafeng.itchat4j.utils.enums.StorageLoginInfoKey
import org.apache.http.util.EntityUtils
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileOutputStream

/**
 * 此示例演示如何获取所有好友的头像
 *
 * @author https://github.com/yaphone
 * @version 1.0
 * @date 创建时间：2017年6月26日 下午11:27:46
 */
class PicYourFriends : SimpleReplyMsgHandler() {

    override fun textMsgHandle(msg: BaseMsg): String {

        if (!msg.groupMsg) { // 群消息不处理
            val text = msg.text // 发送文本消息，也可调用MessageTools.sendFileMsgByUserId(userId,text);
            val baseUrl = "https://" + core.indexUrl!! // 基础URL
            val skey = core.getLoginInfo(StorageLoginInfoKey.SKEY)!!.toString()
            if (text == "111") {
                LOG.info("开始下载好友头像")
                val friends = WechatTools.contactList
                for (i in friends.indices) {
                    val friend = friends[i]
                    val url = baseUrl + friend.getString("HeadImgUrl") + skey
                    val headPicPath = path + File.separator + i + ".jpg"

                    val entity = myHttpClient.doGet(url, null, true, null)
                    try {
                        FileOutputStream(headPicPath).use { out ->
                            val bytes = EntityUtils.toByteArray(entity!!)
                            out.write(bytes)
                            out.flush()
                        }
                    } catch (e: Exception) {
                        LOG.info(e.message)
                    }

                }
            }
        }
        return ""
    }

    override fun picMsgHandle(msg: BaseMsg): String {
        // TODO Auto-generated method stub
        return ""
    }

    override fun voiceMsgHandle(msg: BaseMsg): String {
        // TODO Auto-generated method stub
        return ""
    }

    override fun viedoMsgHandle(msg: BaseMsg): String {
        // TODO Auto-generated method stub
        return ""
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
        private val LOG = LoggerFactory.getLogger(PicYourFriends::class.java)
        private val core = Core.getInstance()
        private val myHttpClient = core.myHttpClient
        private val path = "/tmp" // 这里需要设置保存头像的路径

        @JvmStatic
        fun main(args: Array<String>) {
            val msgHandler = PicYourFriends() // 实现IMsgHandlerFace接口的类
            val wechat = Wechat(msgHandler) // 【注入】
            wechat.start() // 启动服务，会在qrPath下生成一张二维码图片，扫描即可登陆，注意，二维码图片如果超过一定时间未扫描会过期，过期时会自动更新，所以你可能需要重新打开图片
        }
    }

}
