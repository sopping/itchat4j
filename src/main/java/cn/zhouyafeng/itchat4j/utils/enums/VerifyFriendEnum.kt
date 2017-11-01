package cn.zhouyafeng.itchat4j.utils.enums

/**
 * 确认添加好友Enum
 *
 * @author https://github.com/yaphone
 * @version 1.0
 * @date 创建时间：2017年6月29日 下午9:47:14
 */
enum class VerifyFriendEnum(val code: Int, private val desc: String) {

    ADD(2, "添加"), ACCEPT(3, "接受")

}
