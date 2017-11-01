package cn.zhouyafeng.itchat4j.beans

import cn.zhouyafeng.itchat4j.utils.enums.RetCodeEnum
import cn.zhouyafeng.itchat4j.utils.enums.SelectorEnum
import java.io.Serializable

/**
 *
 * Created by sopping on 2017/10/30.
 * @author sopping
 */
data class SyncResult(val retcode: Int, val selector: Int) : Serializable {

    fun getRestCodeEum(): RetCodeEnum {
        return RetCodeEnum.values().firstOrNull { it.code == retcode } ?: RetCodeEnum.UNKOWN
    }

    fun getSelectorEnum(): SelectorEnum {
        return SelectorEnum.values().firstOrNull { it.code == selector } ?: SelectorEnum.OTHER_ALL
    }
}
