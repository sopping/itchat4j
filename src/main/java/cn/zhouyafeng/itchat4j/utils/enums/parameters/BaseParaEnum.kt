package cn.zhouyafeng.itchat4j.utils.enums.parameters

import cn.zhouyafeng.itchat4j.utils.enums.StorageLoginInfoKey
import java.util.*

/**
 * 基本请求参数
 * 1. init      初始化
 * 2. statusNotify 微信状态通知
 *
 * Created by xiaoxiaomo on 2017/5/7.
 */
enum class BaseParaEnum(val para: String, val key: StorageLoginInfoKey) {

    UIN("Uin", StorageLoginInfoKey.WXUIN),

    SID("Sid", StorageLoginInfoKey.WXSID),

    SKEY("Skey", StorageLoginInfoKey.SKEY),

    DEVICE_ID("DeviceID", StorageLoginInfoKey.PASS_TICKET),

    R("r", StorageLoginInfoKey.EMPTY),

    RR("rr", StorageLoginInfoKey.EMPTY),

    EMPTY("_", StorageLoginInfoKey.EMPTY),

    SYNC_CHECK_KEY("synckey", StorageLoginInfoKey.SYNC_CHECK_KEY),

    SYNC_KEY("SyncKey", StorageLoginInfoKey.SYNC_KEY),

    SEQ("seq", StorageLoginInfoKey.EMPTY),

    ;

    companion object {
        fun initEnumSet(): EnumSet<BaseParaEnum>{
            return EnumSet.of(UIN, SID, SKEY, DEVICE_ID)
        }
    }
}
