package cn.zhouyafeng.itchat4j.utils.enums.parameters

/**
 * 状态通知
 *
 *
 * Created by xiaoxiaomo on 2017/5/7.
 */
enum class StatusNotifyParaEnum(val para: String, val value: String) {

    CODE("Code", "3"),

    FROM_USERNAME("FromUserName", ""),

    TO_USERNAME("ToUserName", ""),

    CLIENT_MSG_ID("ClientMsgId", "");
}
