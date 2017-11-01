package cn.zhouyafeng.itchat4j.face

import cn.zhouyafeng.itchat4j.api.MessageTools
import cn.zhouyafeng.itchat4j.beans.BaseMsg
import cn.zhouyafeng.itchat4j.utils.enums.MsgTypeGroupEnum
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * 直接回复发送者, 区分各种类型信息分别处理
 * Created by sopping on 2017/10/31.
 * @author sopping
 */
abstract class SimpleReplyMsgHandler : IMsgHandlerFace {
    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(SimpleReplyMsgHandler::class.java)
    }

    override fun handle(msg: BaseMsg) {
        when (msg.getMsgTypeEnum().group) {
            MsgTypeGroupEnum.TEXT -> MessageTools.sendMsgByUserName(textMsgHandle(msg), msg.fromUserName)
            MsgTypeGroupEnum.PIC -> MessageTools.sendMsgByUserName(picMsgHandle(msg), msg.fromUserName)
            MsgTypeGroupEnum.VOICE -> MessageTools.sendMsgByUserName(voiceMsgHandle(msg), msg.fromUserName)
            MsgTypeGroupEnum.VIDEO -> MessageTools.sendMsgByUserName(viedoMsgHandle(msg), msg.fromUserName)
            MsgTypeGroupEnum.NAMECARD -> MessageTools.sendMsgByUserName(nameCardMsgHandle(msg), msg.fromUserName)
            MsgTypeGroupEnum.SYS -> sysMsgHandle(msg)
            MsgTypeGroupEnum.VERIFYMSG -> MessageTools.sendMsgByUserName(verifyAddFriendMsgHandle(msg), msg.recommendInfo.userName)
            MsgTypeGroupEnum.MEDIA -> MessageTools.sendMsgByUserName(mediaMsgHandle(msg), msg.fromUserName)
            else -> LOG.warn("unused message: " + msg)
        }
    }

    /**
     * 处理文本信息
     * @param msg
     * @return
     * @author https://github.com/yaphone
     * @date 2017年4月20日 上午12:15:00
     */
    abstract fun textMsgHandle(msg: BaseMsg): String

    /**
     * 处理图片消息
     *
     * @param msg
     * @return
     * @author https://github.com/yaphone
     * @date 2017年4月21日 下午11:07:06
     */
    abstract fun picMsgHandle(msg: BaseMsg): String

    /**
     * 处理声音消息
     *
     * @param msg
     * @return
     * @author https://github.com/yaphone
     * @date 2017年4月22日 上午12:09:44
     */
    abstract fun voiceMsgHandle(msg: BaseMsg): String

    /**
     * 处理小视频消息
     *
     * @param msg
     * @return
     * @author https://github.com/yaphone
     * @date 2017年4月23日 下午12:19:50
     */
    abstract fun viedoMsgHandle(msg: BaseMsg): String

    /**
     * 处理名片消息
     *
     * @param msg
     * @return
     * @author https://github.com/yaphone
     * @date 2017年5月1日 上午12:50:50
     */
    abstract fun nameCardMsgHandle(msg: BaseMsg): String

    /**
     * 处理系统消息
     *
     * @param msg
     * @return
     * @author Relyn
     * @date 2017年6月21日17:43:51
     */
    abstract fun sysMsgHandle(msg: BaseMsg)

    /**
     * 处理确认添加好友消息
     *
     * @param msg
     * @return
     * @date 2017年6月28日 下午10:15:30
     */
    abstract fun verifyAddFriendMsgHandle(msg: BaseMsg): String

    /**
     * 处理收到的文件消息
     *
     * @param msg
     * @return
     * @date 2017年7月21日 下午11:59:14
     */
    abstract fun mediaMsgHandle(msg: BaseMsg): String
}