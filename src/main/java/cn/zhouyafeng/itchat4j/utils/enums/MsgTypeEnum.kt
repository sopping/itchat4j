package cn.zhouyafeng.itchat4j.utils.enums

/**
 * 消息类型
 *
 * @author https://github.com/yaphone
 * @version 1.0
 * @date 创建时间：2017年4月23日 下午12:15:00
 */
enum class MsgTypeEnum(val code: Int, val group: MsgTypeGroupEnum, val message: String) {


    TEXT(1, MsgTypeGroupEnum.TEXT, "文本消息"),

    IMAGE(3, MsgTypeGroupEnum.PIC, "图片消息"),

    VOICE(34, MsgTypeGroupEnum.VOICE, "语音消息"),

    VERIFY_MSG(37, MsgTypeGroupEnum.VERIFYMSG, "好友请求"),

    POSSIBLE_FRIEND_MSG(40, MsgTypeGroupEnum.EMPTY, ""),

    SHARE_CARD(42, MsgTypeGroupEnum.NAMECARD, "名片消息"),

    VIDEO(43, MsgTypeGroupEnum.VIDEO, "视频通话"),

    EMOTICON(47, MsgTypeGroupEnum.PIC, "表情消息"),

    LOCATION(48, MsgTypeGroupEnum.EMPTY, "位置信息"),

    MEDIA(49, MsgTypeGroupEnum.MEDIA, "文件/多媒体消息"),

    VOIP_MSG(50, MsgTypeGroupEnum.EMPTY, ""),

    STATUS_NOTIFY(51, MsgTypeGroupEnum.EMPTY, "微信初始化消息"),

    VOIP_NOTIFY(52, MsgTypeGroupEnum.EMPTY, ""),

    WEB_WX_VOIP_NOTIFY(53, MsgTypeGroupEnum.EMPTY, ""),

    MICRO_VIDEO(62, MsgTypeGroupEnum.VIDEO, "短视频消息"),

    SYS_NOTICE(9999, MsgTypeGroupEnum.EMPTY, ""),

    SYS(10000, MsgTypeGroupEnum.SYS, "系统消息"),

    RECALLED(10002, MsgTypeGroupEnum.EMPTY, "消息撤回"),

    MAP(-1, MsgTypeGroupEnum.MAP, "链接消息"),
}

/**
 * group
 */
enum class MsgTypeGroupEnum {
    TEXT, PIC, VOICE, VIDEO, NAMECARD, SYS, VERIFYMSG, MEDIA, MAP, EMPTY;
}
