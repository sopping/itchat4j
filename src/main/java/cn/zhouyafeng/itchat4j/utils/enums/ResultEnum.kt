package cn.zhouyafeng.itchat4j.utils.enums

/**
 * 返回结构枚举类
 *
 *
 * Created by xiaoxiaomo on 2017/5/6.
 */
enum class ResultEnum private constructor(val code: String, private val msg: String) {

    SUCCESS("200", "成功"), WAIT_CONFIRM("201", "请在手机上点击确认"), WAIT_SCAN("400", "请扫描二维码");


    companion object {

        /**
         * fromCode
         *
         * @param code
         * @return
         */
        fun fromCode(code: String): ResultEnum? {
            return ResultEnum.values().firstOrNull { it.code == code }
        }
    }
}
