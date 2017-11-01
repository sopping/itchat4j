package cn.zhouyafeng.itchat4j.utils.enums

enum class RetCodeEnum private constructor(val code: Int, val message: String) {

    NORMAL(0, "普通"),

    ERROR(2, "错误"),

    LOGIN_OUT(1102, "退出"),

    LOGIN_OTHERWHERE(1101, "其它地方登陆"),

    MOBILE_LOGIN_OUT(1102, "移动端退出"),

    UNKOWN(9999, "未知");


    companion object {

        /**
         * fromCode
         *
         * @param code
         * @return
         */
        fun fromCode(code: Int): RetCodeEnum? {
            return RetCodeEnum.values().firstOrNull { it.code == code }
        }
    }
}
