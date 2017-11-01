package cn.zhouyafeng.itchat4j.beans

/**
 *
 * Created by sopping on 2017/10/31.
 * @author sopping
 */
class InitResponse: Response() {
    var user: Contact = Contact()
    var chatSet: String = ""
    var inviteStartCount: Int = 0

    fun getChatSetList(): List<String>{
        return chatSet.split(",".toRegex()).dropLastWhile { it.isEmpty() }
    }
}