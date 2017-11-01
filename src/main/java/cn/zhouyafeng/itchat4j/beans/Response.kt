package cn.zhouyafeng.itchat4j.beans

import cn.zhouyafeng.itchat4j.utils.enums.RetCodeEnum
import com.alibaba.fastjson.annotation.JSONField

/**
 *
 * Created by sopping on 2017/10/31.
 * @author sopping
 */
open class Response {
    var baseResponse: BaseResponse = BaseResponse()
    var sKey: String = ""
    var syncKey: SyncKeys = SyncKeys()
    var syncCheckKey: SyncKeys = SyncKeys()

    /**
     * 获取syncKey的组装字符串：每个SyncKey拆分成key_val，合并时使用|做分隔符
     */
    fun getSyncKeyString(): String {
        return syncKey.list.joinToString("|") { key -> key.key + "_" + key.Val }
    }

    class BaseResponse {
        var ret: Int = RetCodeEnum.NORMAL.code
        var errMsg: String = ""

        fun isNormal(): Boolean {
            return ret == RetCodeEnum.NORMAL.code
        }
    }

    class SyncKeys {
        @JSONField(name = "Count")
        var count: Int = 0
        @JSONField(name = "List")
        var list: List<SyncKey> = ArrayList()
    }

    class SyncKey {
        @JSONField(name = "Key")
        var key: String = ""
        /**
         * val是Kotlin保留字，使用大写Val
         */
        @JSONField(name = "Val")
        var Val: String = ""
    }
}