package cn.zhouyafeng.itchat4j.beans

import cn.zhouyafeng.itchat4j.core.Core
import cn.zhouyafeng.itchat4j.utils.enums.MsgTypeEnum
import cn.zhouyafeng.itchat4j.utils.tools.CommonTools
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.Serializable

/**
 * 收到的微信消息
 *
 * @author https://github.com/yaphone
 * @version 1.0
 * @date 创建时间：2017年7月3日 下午10:28:06
 */
class BaseMsg : Serializable {
    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(BaseMsg::class.java)
    }

    /**
     * msgId
     */
    var msgId: String = ""
    /**
     * 消息发送者ID
     */
    var fromUserName: String = ""
    /**
     * 消息接收者
     */
    var toUserName: String = ""
    /**
     * 消息类型
     */
    var msgType: Int = 0
    /**
     * 消息体
     */
    var content: String = ""
    var status: Int = 0
    var imgStatus: Int = 0
    var createTime: Int = 0
    var voiceLength: Int = 0
    var playLength: Int = 0
    var fileName: String = ""
    var fileSize: String = ""
    var mediaId: String = ""
    var url: String = ""
    var appMsgType: Int = 0
    var statusNotifyCode: Int = 0
    var statusNotifyUserName: String = ""
    /**
     * 推荐消息报文
     */
    var recommendInfo: Contact = Contact()
    var forwardFlag: Int = 0
    var appInfo: AppInfo = AppInfo()
    var hasProductId: Int = 0
    var ticket: String = ""
    var imgHeight: Int = 0
    var imgWidth: Int = 0
    var subMsgType: Int = 0
    var newMsgId: Long = 0
    var oriContent: String = ""


    // 辅助属性
    /**
     * 文本消息内容
     */
    var text: String = ""
    /**
     * 是否群消息
     */
    var groupMsg: Boolean = false

    /**
     * 消息预处理
     */
    fun preHandle(core: Core) {
        if (isGroupChat()) {
            if (isGroupSend()) {
                core.groupIdSet.add(fromUserName)
            } else if (isGroupReceive()) {
                core.groupIdSet.add(toUserName)
            }
            // 群消息与普通消息不同的是在其消息体（Content）中会包含发送者id及":<br/>"消息，这里需要处理一下，去掉多余信息，只保留消息内容
            if (content.contains("<br/>")) {
                content = content.substring(content.indexOf("<br/>") + "<br/>".length)
                groupMsg = true
            }
        }
        content = CommonTools.msgFormatter(content)
        if (getMsgTypeEnum() == MsgTypeEnum.TEXT && !url.isEmpty()) {
            val regEx = "(.+?\\(.+?\\))"
            val matcher = CommonTools.getMatcher(regEx, content)
            var data = MsgTypeEnum.MAP.group.name
            if (matcher.find()) {
                data = matcher.group(1)
            }
            msgType = MsgTypeEnum.MAP.code
            content = data
        }
        LOGGER.info("收到消息一条，来自: " + fromUserName)
    }

    /**
     * 消息类型，默认TEXT
     */
    fun getMsgTypeEnum(): MsgTypeEnum {
        return MsgTypeEnum.values().firstOrNull { it.code == msgType } ?: MsgTypeEnum.TEXT
    }

    /**
     * 是否群内聊天：消息的接收方或发送方是否是@@开头
     */
    private fun isGroupChat(): Boolean {
        return isGroupSend() || isGroupReceive()
    }

    /**
     * 消息发送者是否是群
     */
    private fun isGroupSend(): Boolean {
        return Contact.isGroupChat(fromUserName)
    }

    /**
     * 消息接收者是否是群
     */
    private fun isGroupReceive(): Boolean {
        return Contact.isGroupChat(toUserName)
    }
}
