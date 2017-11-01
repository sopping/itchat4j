package cn.zhouyafeng.itchat4j.utils.enums

import java.util.*

/**
 * URL
 * Created by xiaoxiaomo on 2017/5/6.
 */
enum class URLEnum(private val url: String, private val msg: String) {

    BASE_URL("https://login.weixin.qq.com", "基本的URL"),

    UUID_URL(BASE_URL.url + "/jslogin", "UUIDLURL"),

    QRCODE_REAL_URL(BASE_URL.url + "/l/%s", "二维码实际的登录URL"),

    QRCODE_URL(BASE_URL.url + "/qrcode/", "初始化URL"),

    STATUS_NOTIFY_URL(BASE_URL.url + "/webwxstatusnotify?lang=zh_CN&pass_ticket=%s", "微信状态通知",
            listOf(StorageLoginInfoKey.PASS_TICKET)),

    LOGIN_URL(BASE_URL.url + "/cgi-bin/mmwebwx-bin/login", "登陆URL"),

    INIT_URL("%s/webwxinit?r=%s&pass_ticket=%s", "初始化URL") {
        override fun getUrl(loginInfo: Map<String, Any>): String {
            return super.getUrl(loginInfo[StorageLoginInfoKey.URL.key], (System.currentTimeMillis() / 3158L).toString(),
                    loginInfo[StorageLoginInfoKey.PASS_TICKET.key])
        }
    },

    SYNC_CHECK_URL("%s/synccheck", "检查心跳URL", listOf(StorageLoginInfoKey.SYNC_URL)),

    WEB_WX_SYNC_URL("%s/webwxsync?sid=%s&skey=%s&pass_ticket=%s", "web微信消息同步URL",
            Arrays.asList(StorageLoginInfoKey.URL, StorageLoginInfoKey.WXSID, StorageLoginInfoKey.SKEY, StorageLoginInfoKey.PASS_TICKET)),

    WEB_WX_GET_CONTACT("%s/webwxgetcontact", "web微信获取联系人信息URL", listOf(StorageLoginInfoKey.URL)),

    WEB_WX_SEND_MSG("%s/webwxsendmsg", "发送消息URL", listOf(StorageLoginInfoKey.URL)),

    WEB_WX_SEND_MSG_IMG("%s/webwxsendmsgimg?fun=async&f=json&pass_ticket=%s", "发送图片消息URL",
            Arrays.asList(StorageLoginInfoKey.URL, StorageLoginInfoKey.PASS_TICKET)),

    WEB_WX_SEND_APP_MSG("%s/webwxsendappmsg?fun=async&f=json&pass_ticket=%s", "发送APP消息URL",
            Arrays.asList(StorageLoginInfoKey.URL, StorageLoginInfoKey.PASS_TICKET)),

    WEB_WX_UPLOAD_MEDIA("%s/webwxuploadmedia?f=json", "上传文件到服务器", listOf(StorageLoginInfoKey.FILE_URL)),

    WEB_WX_GET_MSG_IMG("%s/webwxgetmsgimg", "下载图片消息", listOf(StorageLoginInfoKey.URL)),

    WEB_WX_GET_VOICE("%s/webwxgetvoice", "下载语音消息", listOf(StorageLoginInfoKey.URL)),

    WEB_WX_GET_VIEDO("%s/webwxgetvideo", "下载语音消息", listOf(StorageLoginInfoKey.URL)),

    WEB_WX_PUSH_LOGIN("%s/webwxpushloginurl", "不扫码登陆", listOf(StorageLoginInfoKey.URL)),

    WEB_WX_LOGOUT("%s/webwxlogout", "退出微信", listOf(StorageLoginInfoKey.URL)),

    WEB_WX_BATCH_GET_CONTACT("%s/webwxbatchgetcontact?code=ex&r=%s&lang=zh_CN&pass_ticket=%s", "查询群信息") {
        override fun getUrl(loginInfo: Map<String, Any>): String {
            return super.getUrl(loginInfo[StorageLoginInfoKey.URL.key], "" + System.currentTimeMillis(),
                    loginInfo[StorageLoginInfoKey.PASS_TICKET.key])
        }
    },

    WEB_WX_REMARKNAME("%s/webwxoplog?lang=zh_CN&pass_ticket=%s", "修改好友备注", Arrays.asList(StorageLoginInfoKey.URL, StorageLoginInfoKey.PASS_TICKET)),

    WEB_WX_VERIFYUSER("%s/webwxverifyuser?r=%s&lang=zh_CN&pass_ticket=%s", "被动添加好友") {
        override fun getUrl(loginInfo: Map<String, Any>): String {
            return super.getUrl(loginInfo[StorageLoginInfoKey.URL.key], "" + System.currentTimeMillis() / 3158L,
                    loginInfo[StorageLoginInfoKey.PASS_TICKET.key])
        }
    },

    WEB_WX_GET_MEDIA("%s/webwxgetmedia", "下载文件", listOf(StorageLoginInfoKey.FILE_URL));

    private var urlKeys: List<StorageLoginInfoKey>? = null

    constructor(url: String, msg: String, urlKeys: List<StorageLoginInfoKey>) : this(url, msg) {
        this.urlKeys = urlKeys
    }

    open fun getUrl(loginInfo: Map<String, Any>): String {
        if (urlKeys == null || urlKeys!!.isEmpty()) {
            return url
        }
        val params = arrayOfNulls<Any>(urlKeys!!.size)
        for (i in urlKeys!!.indices) {
            params[i] = loginInfo[urlKeys!![i].key]
        }
        return this.getUrl(*params)
    }

    /**
     * 获取URL
     *
     * @param params
     * @return
     */
    fun getUrl(vararg params: Any?): String {
        return if (params.isEmpty()) {
            url
        } else String.format(url, *params)

    }
}
