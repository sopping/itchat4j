package cn.zhouyafeng.itchat4j.utils.tools

import cn.zhouyafeng.itchat4j.beans.BaseMsg
import cn.zhouyafeng.itchat4j.core.Core
import cn.zhouyafeng.itchat4j.utils.enums.MsgTypeGroupEnum
import cn.zhouyafeng.itchat4j.utils.enums.StorageLoginInfoKey
import cn.zhouyafeng.itchat4j.utils.enums.URLEnum
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils
import org.slf4j.LoggerFactory
import java.io.FileOutputStream
import java.util.*

/**
 * 下载工具类
 *
 * @author https://github.com/yaphone
 * @version 1.0
 * @date 创建时间：2017年4月21日 下午11:18:46
 */
object DownloadTools {
    private val LOGGER = LoggerFactory.getLogger(DownloadTools::class.java)
    private val core = Core.getInstance()
    private val myHttpClient = core.myHttpClient

    /**
     * 处理下载任务
     *
     * @param path
     * @return
     * @author https://github.com/yaphone
     * @date 2017年4月21日 下午11:00:25
     */
    fun getDownloadFn(msg: BaseMsg, group: MsgTypeGroupEnum, path: String): Any? {
        val headerMap = HashMap<String, String>()
        val params = ArrayList<BasicNameValuePair>()
        var url = ""
        when (group) {
            MsgTypeGroupEnum.PIC -> url = URLEnum.WEB_WX_GET_MSG_IMG.getUrl(core.loginInfo)
            MsgTypeGroupEnum.VOICE -> url = URLEnum.WEB_WX_GET_VOICE.getUrl(core.loginInfo)
            MsgTypeGroupEnum.VIDEO -> {
                headerMap.put("Range", "bytes=0-")
                url = URLEnum.WEB_WX_GET_VIEDO.getUrl(core.loginInfo)
            }
            MsgTypeGroupEnum.MEDIA -> {
                url = URLEnum.WEB_WX_GET_MEDIA.getUrl(core.loginInfo)
                headerMap.put("Range", "bytes=0-")
                params.add(BasicNameValuePair("sender", msg.fromUserName))
                params.add(BasicNameValuePair("mediaid", msg.mediaId))
                params.add(BasicNameValuePair("filename", msg.fileName))
            }
            else -> LOGGER.warn("can't download for msg group type {}", group)
        }
        params.add(BasicNameValuePair("msgid", msg.newMsgId.toString()))
        params.add(BasicNameValuePair("skey", core.getLoginInfo(StorageLoginInfoKey.SKEY).toString()))
        val entity = myHttpClient.doGet(url, params, true, headerMap)
        try {
            val out = FileOutputStream(path)
            val bytes = EntityUtils.toByteArray(entity!!)
            out.write(bytes)
            out.flush()
            out.close()

        } catch (e: Exception) {
            LOGGER.info(e.message)
            return false
        }

        return null
    }

}
