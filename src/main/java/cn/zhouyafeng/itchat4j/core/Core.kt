package cn.zhouyafeng.itchat4j.core

import cn.zhouyafeng.itchat4j.beans.BaseMsg
import cn.zhouyafeng.itchat4j.beans.Contact
import cn.zhouyafeng.itchat4j.utils.MyHttpClient
import cn.zhouyafeng.itchat4j.utils.enums.StorageLoginInfoKey
import cn.zhouyafeng.itchat4j.utils.enums.parameters.BaseParaEnum
import com.alibaba.fastjson.JSONObject
import java.util.*
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import kotlin.collections.HashSet

/**
 * 核心存储类，全局只保存一份，单例模式
 *
 * @author https://github.com/yaphone
 * @version 1.0
 * @date 创建时间：2017年4月23日 下午2:33:56
 */
class Core private constructor() {
    //    var isUseHotReload = false
    //    var hotReloadDir = "itchat.pkl"
    @Volatile
    var isAlive = false
    var loginInfo: MutableMap<String, String> = HashMap()
    var myHttpClient = MyHttpClient.getInstance()
    var uuid: String? = null
    var receivingRetryCount = 5
    var memberCount = 0
    var indexUrl: String? = null
    var userName: String = ""
    var nickName: String = ""
    var msgQueue: BlockingQueue<BaseMsg> = LinkedBlockingQueue()
    /**
     * 登陆账号自身信息
     */
    var userSelf: Contact = Contact()
    /**
     * 好友+群聊+公众号+特殊账号
     */
    var memberList: MutableList<Contact> = ArrayList()
    /**
     * 好友
     */
    var contactList: MutableList<Contact> = ArrayList()
    /**
     * 群
     */
    var groupList: MutableList<Contact> = ArrayList()
    /**
     * 群聊成员字典
     */
    var groupMemberMap: MutableMap<String, List<Contact>> = HashMap()
    /**
     * 公众号／服务号
     */
    var publicUsersList: MutableList<Contact> = ArrayList()
    /**
     * 特殊账号
     */
    var specialUsersList: MutableList<Contact> = ArrayList()
    /**
     * 群ID列表
     */
    var groupIdSet: MutableSet<String> = HashSet()
    /**
     * 群NickName列表
     */
    var groupNickNameList: MutableList<String> = ArrayList()
    /**
     * 用户信息Map
     */
    var userInfoMap: MutableMap<String, Contact> = HashMap()
    /**
     * 最后一次收到正常retcode的时间戳
     */
    @Volatile
    var lastNormalRetcodeTime: Long = 0

    /**
     * 请求参数
     */
    /**
     *
     */
    val paramMap: MutableMap<String, Any>
        get() = object : HashMap<String, Any>(1) {
            init {
                put(StorageLoginInfoKey.BASE_REQUEST.key, BaseParaEnum.initEnumSet().map { it.para to getLoginInfo(it.key).toString() }.toMap())
            }
        }

    fun setLoginInfo(key: StorageLoginInfoKey, value: String) {
        loginInfo[key.key] = value
    }

    fun getLoginInfo(key: StorageLoginInfoKey): String? {
        return loginInfo[key.key]
    }

    companion object {

        private var INSTANCE: Core? = null

        fun getInstance(): Core {
            if (INSTANCE == null) {
                synchronized(Core::class.java) {
                    INSTANCE = Core()
                }
            }
            return INSTANCE!!
        }
    }

}
