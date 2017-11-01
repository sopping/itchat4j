package cn.zhouyafeng.itchat4j.api

import cn.zhouyafeng.itchat4j.beans.BaseMsg
import cn.zhouyafeng.itchat4j.beans.MessageResponse
import cn.zhouyafeng.itchat4j.core.Core
import cn.zhouyafeng.itchat4j.utils.Config
import cn.zhouyafeng.itchat4j.utils.MyHttpClient
import cn.zhouyafeng.itchat4j.utils.enums.MsgTypeEnum
import cn.zhouyafeng.itchat4j.utils.enums.StorageLoginInfoKey
import cn.zhouyafeng.itchat4j.utils.enums.URLEnum
import cn.zhouyafeng.itchat4j.utils.enums.VerifyFriendEnum
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import org.apache.commons.lang3.StringUtils
import org.apache.http.Consts
import org.apache.http.entity.ContentType
import org.apache.http.entity.mime.HttpMultipartMode
import org.apache.http.entity.mime.MultipartEntityBuilder
import org.apache.http.util.EntityUtils
import org.slf4j.LoggerFactory
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.activation.MimetypesFileTypeMap

/**
 * 消息处理类
 *
 * @author https://github.com/yaphone
 * @version 1.0
 * @date 创建时间：2017年4月23日 下午2:30:37
 */
object MessageTools {
    private val LOG = LoggerFactory.getLogger(MessageTools::class.java)
    private val core = Core.getInstance()
    private val myHttpClient = core.myHttpClient

    /**
     * 根据UserName发送文本消息
     *
     * @param text
     * @param toUserName
     * @author https://github.com/yaphone
     * @date 2017年5月4日 下午11:17:38
     */
    private fun sendMsg(text: String, toUserName: String) {
        if (text.isEmpty()) {
            return
        }
        LOG.info("发送消息给{}: {}", toUserName, text)
        webWxSendMsg(MsgTypeEnum.TEXT.code, text, toUserName)
    }

    /**
     * 根据ID发送文本消息
     *
     * @param text
     * @param userName
     * @author https://github.com/yaphone
     * @date 2017年5月6日 上午11:45:51
     */
    fun sendMsgByUserName(text: String, userName: String) {
        if (text.isEmpty()) {
            return
        }
        sendMsg(text, userName)
    }

    /**
     * 根据NickName发送文本消息
     *
     * @param text
     * @param nickName
     * @author https://github.com/yaphone
     * @date 2017年5月4日 下午11:17:38
     */
    fun sendMsgByNickName(text: String, nickName: String?): Boolean {
        if (nickName != null) {
            val toUserName = WechatTools.getUserNameByNickName(nickName)
            if (toUserName != null) {
                webWxSendMsg(MsgTypeEnum.TEXT.code, text, toUserName)
                return true
            }
        }
        return false

    }

    /**
     * 消息发送
     *
     * @param msgType
     * @param content
     * @param toUserName
     * @author https://github.com/yaphone
     * @date 2017年4月23日 下午2:32:02
     */
    private fun webWxSendMsg(msgType: Int, content: String, toUserName: String?) {
        val url = URLEnum.WEB_WX_SEND_MSG.getUrl(core.loginInfo)
        val msgMap = HashMap<String, Any>(6)
        msgMap.put("Type", msgType)
        msgMap.put("Content", content)
        msgMap.put("FromUserName", core.userName)
        msgMap.put("ToUserName", toUserName ?: core.userName)
        msgMap.put("LocalID", System.currentTimeMillis() * 10)
        msgMap.put("ClientMsgId", System.currentTimeMillis() * 10)
        val paramMap = core.paramMap
        paramMap.put("Msg", msgMap)
        paramMap.put("Scene", 0)
        try {
            val paramStr = JSON.toJSONString(paramMap)
            val entity = myHttpClient.doPost(url, paramStr)
            val messageResponse = JSONObject.parseObject(EntityUtils.toString(entity!!, Consts.UTF_8), MessageResponse::class.java)
            if (!messageResponse.baseResponse.isNormal()) {
                LOG.info("send msg result error: code = {}, msg = {}", messageResponse.baseResponse.ret, messageResponse.baseResponse.errMsg)
            }
        } catch (e: Exception) {
            LOG.error("webWxSendMsg", e)
        }

    }

    /**
     * 上传多媒体文件到 微信服务器，目前应该支持3种类型: 1. pic 直接显示，包含图片，表情 2.video 3.doc 显示为文件，包含PDF等
     *
     * @param filePath
     * @return
     * @author https://github.com/yaphone
     * @date 2017年5月7日 上午12:41:13
     */
    private fun webWxUploadMedia(filePath: String): JSONObject? {
        val f = File(filePath)
        if (!f.exists() && f.isFile) {
            LOG.info("file is not exist")
            return null
        }
        var mimeType: String? = MimetypesFileTypeMap().getContentType(f)
        var mediaType = ""
        if (mimeType == null) {
            mimeType = "text/plain"
        } else {
            mediaType = if (mimeType.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0] == "image") "pic" else "doc"
        }
        val lastModifieDate = SimpleDateFormat("yyyy MM dd HH:mm:ss").format(Date())
        val fileSize = f.length()
        val passTicket = core.getLoginInfo(StorageLoginInfoKey.PASS_TICKET)!!.toString()
        val clientMediaId = System.currentTimeMillis().toString() + Random().nextLong().toString().substring(0, 4)
        val webwxDataTicket = MyHttpClient.getCookie("webwx_data_ticket")
        if (webwxDataTicket == null) {
            LOG.error("get cookie webwx_data_ticket error")
            return null
        }

        val paramMap = core.paramMap

        paramMap.put("ClientMediaId", clientMediaId)
        paramMap.put("TotalLen", fileSize)
        paramMap.put("StartPos", 0)
        paramMap.put("DataLen", fileSize)
        paramMap.put("MediaType", 4)

        val builder = MultipartEntityBuilder.create()
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE)

        builder.addTextBody("id", "WU_FILE_0", ContentType.TEXT_PLAIN)
        builder.addTextBody("name", filePath, ContentType.TEXT_PLAIN)
        builder.addTextBody("message", mimeType, ContentType.TEXT_PLAIN)
        builder.addTextBody("lastModifieDate", lastModifieDate, ContentType.TEXT_PLAIN)
        builder.addTextBody("size", fileSize.toString(), ContentType.TEXT_PLAIN)
        builder.addTextBody("mediatype", mediaType, ContentType.TEXT_PLAIN)
        builder.addTextBody("uploadmediarequest", JSON.toJSONString(paramMap), ContentType.TEXT_PLAIN)
        builder.addTextBody("webwx_data_ticket", webwxDataTicket, ContentType.TEXT_PLAIN)
        builder.addTextBody("pass_ticket", passTicket, ContentType.TEXT_PLAIN)
        builder.addBinaryBody("filename", f, ContentType.create(mimeType), filePath)
        val reqEntity = builder.build()
        val entity = myHttpClient.doPostFile(URLEnum.WEB_WX_UPLOAD_MEDIA.getUrl(core.loginInfo), reqEntity)
        if (entity != null) {
            try {
                val result = EntityUtils.toString(entity, Consts.UTF_8)
                return JSON.parseObject(result)
            } catch (e: Exception) {
                LOG.error("webWxUploadMedia 错误： ", e)
            }

        }
        return null
    }

    /**
     * 根据NickName发送图片消息
     *
     * @param nickName
     * @return
     * @author https://github.com/yaphone
     * @date 2017年5月7日 下午10:32:45
     */
    fun sendPicMsgByNickName(nickName: String, filePath: String): Boolean {
        val toUserName = WechatTools.getUserNameByNickName(nickName)
        return if (toUserName != null) {
            sendPicMsgByUserId(toUserName, filePath)
        } else false
    }

    /**
     * 根据用户id发送图片消息
     *
     * @param userId
     * @param filePath
     * @return
     * @author https://github.com/yaphone
     * @date 2017年5月7日 下午10:34:24
     */
    private fun sendPicMsgByUserId(userId: String, filePath: String): Boolean {
        val responseObj = webWxUploadMedia(filePath)
        if (responseObj != null) {
            val mediaId = responseObj.getString("MediaId")
            if (mediaId != null) {
                return webWxSendMsgImg(userId, mediaId)
            }
        }
        return false
    }

    /**
     * 发送图片消息，内部调用
     *
     * @return
     * @author https://github.com/yaphone
     * @date 2017年5月7日 下午10:38:55
     */
    private fun webWxSendMsgImg(userId: String, mediaId: String): Boolean {
        val msgMap = HashMap<String, Any>()
        msgMap.put("Type", MsgTypeEnum.IMAGE.code)
        msgMap.put("MediaId", mediaId)
        msgMap.put("FromUserName", core.userName)
        msgMap.put("ToUserName", userId)
        val clientMsgId = System.currentTimeMillis().toString() + Random().nextLong().toString().substring(1, 5)
        msgMap.put("LocalID", clientMsgId)
        msgMap.put("ClientMsgId", clientMsgId)
        val paramMap = core.paramMap
        paramMap.put("BaseRequest", core.paramMap["BaseRequest"]!!)
        paramMap.put("Msg", msgMap)
        val paramStr = JSON.toJSONString(paramMap)
        val entity = myHttpClient.doPost(URLEnum.WEB_WX_SEND_MSG_IMG.getUrl(core.loginInfo), paramStr)
        if (entity != null) {
            try {
                return JSON.parseObject(EntityUtils.toString(entity, Consts.UTF_8), MessageResponse::class.java).baseResponse!!.isNormal()
            } catch (e: Exception) {
                LOG.error("webWxSendMsgImg 错误： ", e)
            }

        }
        return false

    }

    /**
     * 根据用户id发送文件
     *
     * @param userId
     * @param filePath
     * @return
     * @author https://github.com/yaphone
     * @date 2017年5月7日 下午11:57:36
     */
    private fun sendFileMsgByUserId(userId: String, filePath: String): Boolean {
        val title = File(filePath).name
        val data = HashMap<String, String>()
        data.put("appid", Config.API_WXAPPID)
        data.put("title", title)
        data.put("totallen", "")
        data.put("attachid", "")
        data.put("message", "6") // APPMSGTYPE_ATTACH
        data.put("fileext", title.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]) // 文件后缀
        val responseObj = webWxUploadMedia(filePath)
        if (responseObj != null) {
            data.put("totallen", responseObj.getString("StartPos"))
            data.put("attachid", responseObj.getString("MediaId"))
        } else {
            LOG.error("sednFileMsgByUserId 错误: ", data)
        }
        return webWxSendAppMsg(userId, data)
    }

    /**
     * 根据用户昵称发送文件消息
     *
     * @param nickName
     * @param filePath
     * @return
     * @author https://github.com/yaphone
     * @date 2017年5月10日 下午10:59:27
     */
    fun sendFileMsgByNickName(nickName: String, filePath: String): Boolean {
        val toUserName = WechatTools.getUserNameByNickName(nickName)
        return if (toUserName != null) {
            sendFileMsgByUserId(toUserName, filePath)
        } else false
    }

    /**
     * 内部调用
     *
     * @param userId
     * @param data
     * @return
     * @author https://github.com/yaphone
     * @date 2017年5月10日 上午12:21:28
     */
    private fun webWxSendAppMsg(userId: String, data: Map<String, String>): Boolean {
        val url = URLEnum.WEB_WX_SEND_APP_MSG.getUrl(core.loginInfo)
        val clientMsgId = System.currentTimeMillis().toString() + Random().nextLong().toString().substring(1, 5)
        val content = "<appmsg appid='wxeb7ec651dd0aefa9' sdkver=''><title>" +
                data["title"] + "</title><des></des><action></action><message>6</message><content></content><url></url><lowurl></lowurl>" +
                "<appattach><totallen>" + data["totallen"] + "</totallen><attachid>" + data["attachid"] + "</attachid><fileext>" +
                data["fileext"] + "</fileext></appattach><extinfo></extinfo></appmsg>"
        val msgMap = HashMap<String, Any>()
        msgMap.put("Type", data["message"].toString())
        msgMap.put("Content", content)
        msgMap.put("FromUserName", core.userName)
        msgMap.put("ToUserName", userId)
        msgMap.put("LocalID", clientMsgId)
        msgMap.put("ClientMsgId", clientMsgId)

        val paramMap = core.paramMap
        paramMap.put("Msg", msgMap)
        paramMap.put("Scene", 0)
        val paramStr = JSON.toJSONString(paramMap)
        val entity = myHttpClient.doPost(url, paramStr)
        if (entity != null) {
            try {
                return JSON.parseObject(EntityUtils.toString(entity, Consts.UTF_8), MessageResponse::class.java).baseResponse.isNormal()
            } catch (e: Exception) {
                LOG.error("内部调用错误: ", e)
            }

        }
        return false
    }

    /**
     * 被动添加好友
     *
     * @param msg
     * @param accept true 接受 false 拒绝
     * @date 2017年6月29日 下午10:08:43
     */
    fun addFriend(msg: BaseMsg, accept: Boolean) {
        if (!accept) {
            return
        }
        // 更新好友列表
        core.contactList.add(msg.recommendInfo)

        val url = URLEnum.WEB_WX_VERIFYUSER.getUrl(core.loginInfo)
        val body = HashMap<String, Any?>()
        body.put("BaseRequest", core.paramMap[StorageLoginInfoKey.BASE_REQUEST.key])
        body.put("Opcode", VerifyFriendEnum.ACCEPT.code)
        body.put("VerifyUserListSize", 1)
        body.put("VerifyUserList", listOf(mapOf("Value" to msg.recommendInfo.userName, "VerifyUserTicket" to msg.recommendInfo.ticket)))
        body.put("VerifyContent", "")
        body.put("SceneListCount", 1)
        body.put("SceneList", listOf(33))
        body.put(StorageLoginInfoKey.SKEY.key, core.getLoginInfo(StorageLoginInfoKey.SKEY))

        try {
            val result: String = EntityUtils.toString(myHttpClient.doPost(url, JSON.toJSONString(body))!!, Consts.UTF_8)
            if (StringUtils.isBlank(result)) {
                LOG.error("被动添加好友失败")
            }
            LOG.debug(result)
        } catch (e: Exception) {
            LOG.error("add friend error", e)
        }
    }
}
