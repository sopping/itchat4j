package cn.zhouyafeng.itchat4j.beans

/**
 * Created by sopping on 2017/10/30.
 *
 * @author sopping
 */
class MessageResponse : Response() {

    var addMsgCount: Int = 0
    var addMsgList: List<BaseMsg> = ArrayList()
    var modContactCount: Int = 0
    var modContactList: List<Contact> = ArrayList()
    var delContactCount: Int = 0
    var delContactList: List<Contact> = ArrayList()
}


