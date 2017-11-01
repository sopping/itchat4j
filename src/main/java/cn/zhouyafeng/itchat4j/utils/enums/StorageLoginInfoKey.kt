package cn.zhouyafeng.itchat4j.utils.enums

import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject

/**
 * Created by xiaoxiaomo on 2017/5/7.
 */
enum class StorageLoginInfoKey(val key: String, val clazz: Class<*>) {

    EMPTY(""),

    URL("url"),

    FILE_URL("fileUrl"),

    SYNC_URL("syncUrl"),

    DEVICEID("deviceid"), //生成15位随机数

    SKEY("skey"),

    WXSID("wxsid"),

    WXUIN("wxuin"),

    PASS_TICKET("pass_ticket"),

    INVITE_START_COUNT("InviteStartCount"),

    USER("User"),

    SYNC_KEY("SyncKey"),

    SYNC_CHECK_KEY("synckey"),

    MEMBER_COUNT("MemberCount"),

    MEMBER_LIST("MemberList"),

    BASE_REQUEST("BaseRequest");

    constructor(key: String) : this(key, String::class.java)
}
