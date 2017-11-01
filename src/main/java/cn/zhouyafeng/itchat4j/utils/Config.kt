package cn.zhouyafeng.itchat4j.utils

import cn.zhouyafeng.itchat4j.utils.enums.OsNameEnum
import java.io.File
import java.io.IOException
import java.util.*

/**
 * 配置信息
 *
 * @author https://github.com/yaphone
 * @version 1.0
 * @date 创建时间：2017年4月23日 下午2:26:21
 */
object Config {

    val API_WXAPPID = "API_WXAPPID"

    val picDir = "D://itchat4j"
    val VERSION = "1.2.18"
    val BASE_URL = "https://login.weixin.qq.com"
    val OS = ""
    val DIR = ""
    val DEFAULT_QR = "QR.jpg"
    val USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36"

    val API_SPECIAL_USER: List<String> = Arrays.asList("filehelper", "weibo", "qqmail", "fmessage", "tmessage", "qmessage", "qqsync", "floatbottle", "lbsapp", "shakeapp",
            "medianote", "qqfriend", "readerapp", "blogapp", "facebookapp", "masssendapp", "meishiapp", "feedsapp", "voip", "blogappweixin",
            "brandsessionholder", "weixin", "weixinreminder", "officialaccounts", "wxitil", "notification_messages", "wxid_novlwrv3lqwv11",
            "gh_22b87fa7cb3c", "userexperience_alarm")

    /**
     * 获取文件目录
     *
     * @return
     * @author https://github.com/yaphone
     * @date 2017年4月8日 下午10:27:42
     */
    val localPath: String?
        get() {
            var localPath: String? = null
            try {
                localPath = File("").canonicalPath
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return localPath
        }

    /**
     * 获取系统平台
     *
     * @author https://github.com/yaphone
     * @date 2017年4月8日 下午10:27:53
     */
    val osNameEnum: OsNameEnum
        get() {
            val os = System.getProperty("os.name").toUpperCase()
            return when {
                os.contains(OsNameEnum.DARWIN.toString()) -> OsNameEnum.DARWIN
                os.contains(OsNameEnum.WINDOWS.toString()) -> OsNameEnum.WINDOWS
                os.contains(OsNameEnum.LINUX.toString()) -> OsNameEnum.LINUX
                os.contains(OsNameEnum.MAC.toString()) -> OsNameEnum.MAC
                else -> OsNameEnum.OTHER
            }
        }

}
