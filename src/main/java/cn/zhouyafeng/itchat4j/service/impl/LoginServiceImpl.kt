package cn.zhouyafeng.itchat4j.service.impl

import cn.zhouyafeng.itchat4j.beans.*
import cn.zhouyafeng.itchat4j.core.Core
import cn.zhouyafeng.itchat4j.service.ILoginService
import cn.zhouyafeng.itchat4j.utils.SleepUtils
import cn.zhouyafeng.itchat4j.utils.enums.*
import cn.zhouyafeng.itchat4j.utils.enums.parameters.BaseParaEnum
import cn.zhouyafeng.itchat4j.utils.enums.parameters.LoginParaEnum
import cn.zhouyafeng.itchat4j.utils.enums.parameters.StatusNotifyParaEnum
import cn.zhouyafeng.itchat4j.utils.enums.parameters.UUIDParaEnum
import cn.zhouyafeng.itchat4j.utils.tools.CommonTools
import com.alibaba.fastjson.JSON
import org.apache.http.Consts
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * 登陆服务实现类
 *
 * @author https://github.com/yaphone
 * @version 1.0
 * @date 创建时间：2017年5月13日 上午12:09:35
 */
class LoginServiceImpl : ILoginService {

    private val core = Core.getInstance()
    private val httpClient = core.myHttpClient

    override fun login(): Boolean {

        var isLogin = false

        while (!isLogin) {
            val millis = System.currentTimeMillis()
            val params = ArrayList<BasicNameValuePair>()
            params.add(BasicNameValuePair(LoginParaEnum.LOGIN_ICON.para, LoginParaEnum.LOGIN_ICON.value))
            params.add(BasicNameValuePair(LoginParaEnum.UUID.para, core.uuid))
            params.add(BasicNameValuePair(LoginParaEnum.TIP.para, LoginParaEnum.TIP.value))
            params.add(BasicNameValuePair(BaseParaEnum.R.para, (millis / 1579L).toString()))
            params.add(BasicNameValuePair(BaseParaEnum.EMPTY.para, millis.toString()))
            val entity = httpClient.doGet(URLEnum.LOGIN_URL.getUrl(), params, true, null)

            try {
                val result = EntityUtils.toString(entity!!)
                val status = checkLogin(result)

                if (ResultEnum.SUCCESS.code == status) {
                    processLoginInfo(result)
                    isLogin = true
                    core.isAlive = true
                    break
                }
                if (ResultEnum.WAIT_CONFIRM.code == status) {
                    LOG.info("请点击微信确认按钮，进行登陆")
                }

            } catch (e: Exception) {
                LOG.error("微信登陆异常！", e)
            }
            SleepUtils.sleep(100, TimeUnit.MILLISECONDS)
        }
        return true
    }

    override // 组装参数和URL
    val uuid: String?
        get() {
            val params = ArrayList<BasicNameValuePair>()
            params.add(BasicNameValuePair(UUIDParaEnum.APP_ID.para, UUIDParaEnum.APP_ID.value))
            params.add(BasicNameValuePair(UUIDParaEnum.FUN.para, UUIDParaEnum.FUN.value))
            params.add(BasicNameValuePair(UUIDParaEnum.LANG.para, UUIDParaEnum.LANG.value))
            params.add(BasicNameValuePair(BaseParaEnum.EMPTY.para, System.currentTimeMillis().toString()))

            try {
                val entity = httpClient.doGet(URLEnum.UUID_URL.getUrl(), params, true, null)
                val matcher = CommonTools.getMatcher("window.QRLogin.code = (\\d+); window.QRLogin.uuid = \"(\\S+?)\";", EntityUtils.toString(entity!!))
                if (matcher.find()) {
                    if (ResultEnum.SUCCESS.code == matcher.group(1)) {
                        core.uuid = matcher.group(2)
                    }
                }
            } catch (e: Exception) {
                LOG.error("fetch uuid error", e)
            }
            return core.uuid
        }

    override fun getQR(): Boolean {
        return CommonTools.consolePrintQr(URLEnum.QRCODE_REAL_URL.getUrl(core.uuid!!))
    }

    override fun init(): Boolean {
        core.isAlive = true
        core.lastNormalRetcodeTime = System.currentTimeMillis()
        // 组装请求URL和参数
        val url = URLEnum.INIT_URL.getUrl(core.loginInfo)

        val paramMap = core.paramMap

        try {
            // 请求初始化接口
            val entity = httpClient.doPost(url, JSON.toJSONString(paramMap))
            val response = JSON.parseObject(EntityUtils.toString(entity!!, Consts.UTF_8), InitResponse::class.java)
            core.setLoginInfo(StorageLoginInfoKey.INVITE_START_COUNT, response.inviteStartCount.toString())
            core.setLoginInfo(StorageLoginInfoKey.SYNC_KEY, JSON.toJSONString(response.syncKey))
            core.setLoginInfo(StorageLoginInfoKey.SYNC_CHECK_KEY, response.getSyncKeyString())
            core.userSelf = response.user
            core.userName = response.user.userName
            core.nickName = response.user.nickName
            core.groupIdSet.addAll(response.getChatSetList().filter { Contact.isGroupChat(it) })
        } catch (e: Exception) {
            LOG.error("wx init error", e)
            return false
        }

        return true
    }

    override fun statusNotify() {
        val paramMap = core.paramMap
        paramMap.put(StatusNotifyParaEnum.CODE.para, StatusNotifyParaEnum.CODE.value)
        paramMap.put(StatusNotifyParaEnum.FROM_USERNAME.para, core.userName)
        paramMap.put(StatusNotifyParaEnum.TO_USERNAME.para, core.userName)
        paramMap.put(StatusNotifyParaEnum.CLIENT_MSG_ID.para, System.currentTimeMillis())
        try {
            httpClient.doPost(URLEnum.STATUS_NOTIFY_URL.getUrl(core.loginInfo), JSON.toJSONString(paramMap))
        } catch (e: Exception) {
            LOG.error("status notify exception", e)
        }
    }

    override fun startReceiving() {
        core.isAlive = true
        Executors.newSingleThreadExecutor().submit(object : Runnable {
            internal var retryCount = 0

            override fun run() {
                receive@ while (core.isAlive) {
                    try {
                        val syncResult = checkNewMessage()
                        LOG.debug("syncMessage check: {}", syncResult)

                        val retcode = syncResult.getRestCodeEum()
                        val selector = syncResult.getSelectorEnum()
                        when (retcode) {
                            RetCodeEnum.UNKOWN -> {
                                LOG.info(retcode.message)
                                continue@receive
                            }
                            RetCodeEnum.LOGIN_OUT, RetCodeEnum.LOGIN_OTHERWHERE, RetCodeEnum.MOBILE_LOGIN_OUT -> {
                                LOG.info(retcode.message)
                                break@receive
                            }
                            RetCodeEnum.NORMAL -> {
                                core.lastNormalRetcodeTime = System.currentTimeMillis()
                                val message = syncMessage()

                                when (selector) {
                                    SelectorEnum.NORMAL, SelectorEnum.OTHER_MOD_CONTACT -> {
                                        if (message.baseResponse.isNormal()) {
                                            core.msgQueue.addAll(message.addMsgList)
                                            if (!message.modContactList.isEmpty()) {
                                                core.contactList.addAll(message.modContactList)
                                            }
                                        }
                                    }
                                    SelectorEnum.OTHER_7 -> syncMessage()
                                    SelectorEnum.OTHER_4, SelectorEnum.OTHER_3, SelectorEnum.OTHER_ALL -> continue@receive
                                }
                            }
                            else -> syncMessage()
                        }
                    } catch (e: Exception) {
                        LOG.error("receive message error", e)
                        retryCount += 1
                        if (core.receivingRetryCount < retryCount) {
                            core.isAlive = false
                        }
                    }
                    SleepUtils.sleep(100, TimeUnit.MILLISECONDS)
                }
            }
        })
    }

    override fun getUserContact() {
        val url = URLEnum.WEB_WX_GET_CONTACT.getUrl(core.loginInfo)

        try {

            var contactResponse: ContactResponse
            var seq: Long
            val memberList: MutableList<Contact> = ArrayList()
            var params: Any = core.paramMap
            do {
                contactResponse = JSON.parseObject(EntityUtils.toString(httpClient.doPost(url, JSON.toJSONString(params))!!, Consts.UTF_8), ContactResponse::class.java)
                memberList.addAll(contactResponse.memberList)
                seq = contactResponse.seq
                params = listOf(BasicNameValuePair(BaseParaEnum.R.para, System.currentTimeMillis().toString()), BasicNameValuePair(BaseParaEnum.SEQ.para, seq.toString()))
                SleepUtils.sleep(100, TimeUnit.MILLISECONDS)

            } while (seq > 0)

            core.memberCount = memberList.size
            memberList.forEach {
                when {
                    it.isPublicUser() -> core.publicUsersList.add(it)
                    it.isSpecialUser() -> core.specialUsersList.add(it)
                    it.isGroupChat() -> {
                        if (!core.groupIdSet.contains(it.userName)) {
                            core.groupNickNameList.add(it.nickName)
                            core.groupIdSet.add(it.userName)
                            core.groupList.add(it)
                        }
                    }
                    it.userName == core.userName -> core.contactList.remove(it)
                    else -> core.contactList.add(it)
                }
            }
        } catch (e: Exception) {
            LOG.error(e.message, e)
        }

    }

    override fun getGroupContact() {
        val url = URLEnum.WEB_WX_BATCH_GET_CONTACT.getUrl(core.loginInfo)
        val paramMap = core.paramMap
        paramMap.put("Count", core.groupIdSet.size)
        paramMap.put("List", core.groupIdSet.map { mapOf("UserName" to it, "EncryChatRoomId" to "") })
        val entity = httpClient.doPost(url, JSON.toJSONString(paramMap))
        val text = EntityUtils.toString(entity!!, Consts.UTF_8)
        val contactResponse = JSON.parseObject(text, ContactResponse::class.java)
        contactResponse.contactList.filter { it.isGroupChat() }
                .forEach {
                    core.groupNickNameList.add(it.nickName)
                    core.groupList.add(it)
                    core.groupMemberMap.put(it.userName, it.memberList)
                }
    }

    /**
     * 检查登陆状态
     *
     * @param result
     * @return
     */
    private fun checkLogin(result: String): String? {
        val regEx = "window.code=(\\d+)"
        val matcher = CommonTools.getMatcher(regEx, result)
        return if (matcher.find()) matcher.group(1) else null
    }

    /**
     * 处理登陆信息
     *
     * @author https://github.com/yaphone
     * @date 2017年4月9日 下午12:16:26
     */
    private fun processLoginInfo(loginContent: String) {
        val regEx = "window.redirect_uri=\"(\\S+)\";"
        val matcher = CommonTools.getMatcher(regEx, loginContent)
        if (matcher.find()) {
            val originalUrl = matcher.group(1)
            val url = originalUrl.substring(0, originalUrl.lastIndexOf('/'))
            core.setLoginInfo(StorageLoginInfoKey.URL, url)

            val urlList = getUrlList(url)
            if (urlList != null) {
                core.indexUrl = url
                core.setLoginInfo(StorageLoginInfoKey.FILE_URL, urlList[0])
                core.setLoginInfo(StorageLoginInfoKey.SYNC_URL, urlList[1])
            } else {
                core.setLoginInfo(StorageLoginInfoKey.FILE_URL, url)
                core.setLoginInfo(StorageLoginInfoKey.SYNC_URL, url)
            }
            core.setLoginInfo(StorageLoginInfoKey.DEVICEID, "e" + Random().nextLong().toString().substring(1, 16))
            core.setLoginInfo(StorageLoginInfoKey.BASE_REQUEST, "")
            val text: String
            try {
                val entity = httpClient.doGet(originalUrl, null, false, null)
                text = EntityUtils.toString(entity!!)
            } catch (e: Exception) {
                LOG.error("login error", e)
                return
            }

            //add by 默非默 2017-08-01 22:28:09
            //如果登录被禁止时，则登录返回的message内容不为空，下面代码则判断登录内容是否为空，不为空则退出程序
            val msg = getLoginMessage(text)
            if ("" != msg) {
                LOG.info(msg)
                System.exit(0)
            }
            val doc = CommonTools.xmlParser(text)
            if (doc != null) {
                core.setLoginInfo(StorageLoginInfoKey.SKEY, doc.getElementsByTagName(StorageLoginInfoKey.SKEY.key).item(0).firstChild.nodeValue)
                core.setLoginInfo(StorageLoginInfoKey.WXSID, doc.getElementsByTagName(StorageLoginInfoKey.WXSID.key).item(0).firstChild.nodeValue)
                core.setLoginInfo(StorageLoginInfoKey.WXUIN, doc.getElementsByTagName(StorageLoginInfoKey.WXUIN.key).item(0).firstChild.nodeValue)
                core.setLoginInfo(StorageLoginInfoKey.PASS_TICKET, doc.getElementsByTagName(StorageLoginInfoKey.PASS_TICKET.key).item(0).firstChild.nodeValue)
            }

        }
    }

    /**
     * 同步消息 syncMessage the messages
     *
     * @return
     * @author https://github.com/yaphone
     * @date 2017年5月12日 上午12:24:55
     */
    private fun syncMessage(): MessageResponse {
        val url = URLEnum.WEB_WX_SYNC_URL.getUrl(core.loginInfo)
        val paramMap = core.paramMap
        paramMap.put(BaseParaEnum.SYNC_KEY.para, JSON.parseObject(core.getLoginInfo(StorageLoginInfoKey.SYNC_KEY)!!))
        paramMap.put(BaseParaEnum.RR.para, -System.currentTimeMillis() / 1000)
        val paramStr = JSON.toJSONString(paramMap)
        try {
            val entity = httpClient.doPost(url, paramStr)
            val text = EntityUtils.toString(entity!!, Consts.UTF_8)
            val messageResponse: MessageResponse = JSON.parseObject(text, MessageResponse::class.java)
            if (!messageResponse.baseResponse.isNormal()) {
                LOG.warn("syncMessage error: code = {}, msg = {}", messageResponse.baseResponse.ret, messageResponse.baseResponse.errMsg)
                return messageResponse
            }
            if (messageResponse.syncKey.count > 0) {
                core.setLoginInfo(StorageLoginInfoKey.SYNC_KEY, JSON.toJSONString(messageResponse.syncCheckKey))
                core.setLoginInfo(StorageLoginInfoKey.SYNC_CHECK_KEY, messageResponse.getSyncKeyString())
            }

            return messageResponse
        } catch (e: Exception) {
            LOG.info(e.message)
        }
        return MessageResponse()
    }

    /**
     * 检查是否有新消息 check whether there's a message
     *
     * @return
     * @author https://github.com/yaphone
     * @date 2017年4月16日 上午11:11:34
     */
    private fun checkNewMessage(): SyncResult {
        // 组装请求URL和参数
        val params = BaseParaEnum.initEnumSet().mapTo(ArrayList()) { BasicNameValuePair(it.para.toLowerCase(), core.getLoginInfo(it.key)) }
        params.add(BasicNameValuePair(BaseParaEnum.R.para, System.currentTimeMillis().toString()))
        params.add(BasicNameValuePair(BaseParaEnum.EMPTY.para, System.currentTimeMillis().toString()))
        params.add(BasicNameValuePair(BaseParaEnum.SYNC_CHECK_KEY.para, core.getLoginInfo(StorageLoginInfoKey.SYNC_CHECK_KEY)))
        val entity = httpClient.doGet(URLEnum.SYNC_CHECK_URL.getUrl(core.loginInfo), params, true, null) ?: return SyncResult(RetCodeEnum.UNKOWN.code, RetCodeEnum.UNKOWN.code)
        val text = EntityUtils.toString(entity)
        val regEx = "window.synccheck=\\{retcode:\"(\\d+)\",selector:\"(\\d+)\"\\}"
        val matcher = CommonTools.getMatcher(regEx, text)
        return if (!matcher.find() || RetCodeEnum.ERROR.code.toString() + "" == matcher.group(1)) {
            LOG.info(String.format("Unexpected syncMessage check result: %s", text))
            SyncResult(RetCodeEnum.UNKOWN.code, RetCodeEnum.UNKOWN.code)
        } else {
            SyncResult(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)))
        }

    }

    /**
     * 解析登录返回的消息，如果成功登录，则message为空
     *
     * @param result
     * @return
     */
    private fun getLoginMessage(result: String): String {
        val strArr = result.split("<message>".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val rs = strArr[1].split("</message>".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        return if (rs.size > 1) rs[0] else ""
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(LoginServiceImpl::class.java)

        private val POSSIBLE_URL_MAP = mapOf(
                "wx.qq.com" to listOf("https://file.wx.qq.com/cgi-bin/mmwebwx-bin", "https://webpush.wx.qq.com/cgi-bin/mmwebwx-bin"),
                "wx2.qq.com" to listOf("https://file.wx2.qq.com/cgi-bin/mmwebwx-bin", "https://webpush.wx2.qq.com/cgi-bin/mmwebwx-bin"),
                "wx8.qq.com" to listOf("https://file.wx8.qq.com/cgi-bin/mmwebwx-bin", "https://webpush.wx8.qq.com/cgi-bin/mmwebwx-bin"),
                "web2.wechat.com" to listOf("https://file.web2.wechat.com/cgi-bin/mmwebwx-bin", "https://webpush.web2.wechat.com/cgi-bin/mmwebwx-bin"),
                "wechat.com" to listOf("https://file.web.wechat.com/cgi-bin/mmwebwx-bin", "https://webpush.web.wechat.co/cgi-bin/mmwebwx-binm"))

        private fun getUrlList(url: String): List<String>? {
            return POSSIBLE_URL_MAP[url]
        }
    }
}
