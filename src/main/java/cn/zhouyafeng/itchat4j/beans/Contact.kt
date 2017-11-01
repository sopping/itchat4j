package cn.zhouyafeng.itchat4j.beans

import cn.zhouyafeng.itchat4j.utils.Config
import java.io.Serializable

/**
 *
 * Created by sopping on 2017/10/31.
 * @author sopping
 */
class Contact : Serializable {
    companion object {
        private val GROUP_CONTACT_PREFIX: String = "@@"
        /**
         * 公众账号"与"操作因子
         */
        private val PUBLIC_USER = 8

        fun isGroupChat(name: String): Boolean{
            return name.startsWith(GROUP_CONTACT_PREFIX)
        }
    }
    var memberList: List<Contact> = ArrayList()
    var verifyFlag: Int = 0
    var nickName: String = ""
    var userName: String = ""
    var content: String = ""
    var ticket: String = ""
    var remarkName: String = ""
    var province: String = ""
    var city: String = ""
    var sex: Int = 0
    var attrStatus: Long = 0
    var scene: Int = 0
    var alias: String = ""
    var signature: String = ""
    var opCode: Int = 0
    var qqNum: Int = 0

    fun isPublicUser(): Boolean {
        return verifyFlag and Contact.PUBLIC_USER != 0
    }

    fun isSpecialUser(): Boolean{
        return Config.API_SPECIAL_USER.contains(userName)
    }

    fun isGroupChat():Boolean{
        return isGroupChat(userName)
    }
}