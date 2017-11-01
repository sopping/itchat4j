package cn.zhouyafeng.itchat4j.api

import cn.zhouyafeng.itchat4j.beans.Contact
import cn.zhouyafeng.itchat4j.core.Core
import cn.zhouyafeng.itchat4j.utils.enums.StorageLoginInfoKey
import cn.zhouyafeng.itchat4j.utils.enums.URLEnum
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import org.apache.http.Consts
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils
import org.slf4j.LoggerFactory
import java.util.*

/**
 * 微信小工具，如获好友列表等
 *
 * @author https://github.com/yaphone
 * @version 1.0
 * @date 创建时间：2017年5月4日 下午10:49:16
 */
object WechatTools {
    private val LOG = LoggerFactory.getLogger(WechatTools::class.java)

    private val core = Core.getInstance()

    /**
     * 根据用户名发送文本消息
     *
     * @param msg
     * @param toUserName
     * @author https://github.com/yaphone
     * @date 2017年5月4日 下午10:43:14
     */
    fun sendMsgByUserName(msg: String, toUserName: String) {
        MessageTools.sendMsgByUserName(msg, toUserName)
    }

    /**
     * 通过昵称发送消息
     * @param nickName
     * @return
     * @author https://github.com/yaphone
     * @date 2017年5月4日 下午10:56:31
     */
    fun getUserNameByNickName(nickName: String): String? {
        return core.contactList
                .firstOrNull { it.nickName == nickName }
                ?.userName
    }

    /**
     * 返回好友昵称列表
     *
     * @return
     * @author https://github.com/yaphone
     * @date 2017年5月4日 下午11:37:20
     */
    val contactNickNameList: List<String>
        get() {
            return core.contactList.map { it.nickName }
        }

    /**
     * 返回好友完整信息列表
     *
     * @return
     * @date 2017年6月26日 下午9:45:39
     */
    val contactList: List<JSONObject>
        get() = core.contactList.map { JSON.parseObject(JSON.toJSONString(it)) }

    /**
     * 返回群列表
     *
     * @return
     * @author https://github.com/yaphone
     * @date 2017年5月5日 下午9:55:21
     */
    val groupList: List<Contact>
        get() = core.groupList

    /**
     * 获取群ID列表
     *
     * @return
     * @date 2017年6月21日 下午11:42:56
     */
    val groupIdList: Set<String>
        get() = core.groupIdSet

    /**
     * 获取群NickName列表
     *
     * @return
     * @date 2017年6月21日 下午11:43:38
     */
    val groupNickNameList: List<String>
        get() = core.groupNickNameList

    /**
     * 根据groupIdList返回群成员列表
     *
     * @param groupId
     * @return
     * @date 2017年6月13日 下午11:12:31
     */
    fun getMemberListByGroupId(groupId: String): List<Contact>? {
        return core.groupMemberMap[groupId]
    }

    /**
     * 退出微信
     *
     * @author https://github.com/yaphone
     * @date 2017年5月18日 下午11:56:54
     */
    fun logout() {
        webWxLogout()
    }

    private fun webWxLogout(): Boolean {
        val params = ArrayList<BasicNameValuePair>()
        params.add(BasicNameValuePair("redirect", "1"))
        params.add(BasicNameValuePair("message", "1"))
        params.add(BasicNameValuePair("skey", core.getLoginInfo(StorageLoginInfoKey.SKEY)!!.toString()))
        try {
            val entity = core.myHttpClient.doGet(URLEnum.WEB_WX_LOGOUT.getUrl(core.loginInfo), params, false, null)
            val text = EntityUtils.toString(entity!!, Consts.UTF_8)
            LOG.debug("logout: {}", text)
            return true
        } catch (e: Exception) {
            LOG.debug(e.message)
        }

        return false
    }

    fun setUserInfo() {
        for (o in core.contactList) {
            core.userInfoMap.put(o.nickName, o)
            core.userInfoMap.put(o.userName, o)
        }
    }

    /**
     * 根据用户昵称设置备注名称
     *
     * @param nickName
     * @param remName
     * @date 2017年5月27日 上午12:21:40
     */
    fun remarkNameByNickName(nickName: String, remName: String) {
        val url = URLEnum.WEB_WX_REMARKNAME.getUrl(core.loginInfo)
        val msgMap = HashMap<String, Any?>()
        val baseRequest = HashMap<String, Any?>()
        msgMap.put("CmdId", 2)
        msgMap.put("RemarkName", remName)
        msgMap.put("UserName", core.userInfoMap[nickName]!!.userName)
        baseRequest.put("Uin", core.loginInfo[StorageLoginInfoKey.WXUIN.key])
        baseRequest.put("Sid", core.loginInfo[StorageLoginInfoKey.WXSID.key])
        baseRequest.put("Skey", core.loginInfo[StorageLoginInfoKey.SKEY.key])
        baseRequest.put("DeviceID", core.loginInfo[StorageLoginInfoKey.DEVICEID.key])
        msgMap.put("BaseRequest", baseRequest)
        try {
            val paramStr = JSON.toJSONString(msgMap)
            core.myHttpClient.doPost(url, paramStr)
            LOG.info("修改备注" + remName)
        } catch (e: Exception) {
            LOG.error("remarkNameByUserName", e)
        }

    }

    /**
     * 获取微信在线状态
     *
     * @return
     * @date 2017年6月16日 上午12:47:46
     */
    val wechatStatus: Boolean
        get() = core.isAlive

}
