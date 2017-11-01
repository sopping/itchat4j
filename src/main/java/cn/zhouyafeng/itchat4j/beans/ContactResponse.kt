package cn.zhouyafeng.itchat4j.beans

/**
 *
 * Created by sopping on 2017/10/31.
 * @author sopping
 */
class ContactResponse: Response(){
    var seq: Long = 0
    var contactList: List<Contact> = ArrayList()
    var memberCount: Int = 0
    var memberList: List<Contact> = ArrayList()
}