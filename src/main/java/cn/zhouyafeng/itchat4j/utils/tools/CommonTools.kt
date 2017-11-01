package cn.zhouyafeng.itchat4j.utils.tools

import cn.zhouyafeng.itchat4j.utils.Config
import cn.zhouyafeng.itchat4j.utils.enums.OsNameEnum
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.vdurmont.emoji.EmojiParser
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.w3c.dom.Document
import org.xml.sax.InputSource
import java.io.StringReader
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.xml.parsers.DocumentBuilderFactory

/**
 * 常用工具类
 *
 * @author https://github.com/yaphone
 * @version 1.0
 * @date 创建时间：2017年4月8日 下午10:59:55
 */
object CommonTools {
    private val LOG: Logger = LoggerFactory.getLogger(CommonTools::class.java)
    /**
     * 控制台打印二维码
     */
    fun consolePrintQr(url: String): Boolean {
        writeBarcode(url, 10)
        return true
    }

    /**
     * 直接console绘制二维码
     */
    private fun writeBarcode(content: String, size: Int) {
        val writer = QRCodeWriter()
        try {
            val encode = writer.encode(content, BarcodeFormat.QR_CODE, size, size)
            LOG.info("======>console print qrcode start")
            LOG.info(encode.toString("  ", "██"))
            LOG.info("<======console print qrcode end")
        } catch (e: WriterException) {
            LOG.error("print qrcode error", e)
        }

    }

    @Deprecated("暂时废弃，适用范围太窄")
    fun clearScreen(): Boolean {
        when (Config.osNameEnum) {
            OsNameEnum.WINDOWS -> if (Config.osNameEnum == OsNameEnum.WINDOWS) {
                val runtime = Runtime.getRuntime()
                try {
                    runtime.exec("cmd /c " + "cls")
                } catch (e: Exception) {
                    LOG.error("clean screen error", e)
                }

            }

            else -> {
            }
        }
        return true
    }

    /**
     * 正则表达式处理工具
     *
     * @return
     * @author https://github.com/yaphone
     * @date 2017年4月9日 上午12:27:10
     */
    fun getMatcher(regEx: String, text: String): Matcher {
        return Pattern.compile(regEx).matcher(text)
    }

    /**
     * xml解析器
     *
     * @param text
     * @return
     * @author https://github.com/yaphone
     * @date 2017年4月9日 下午6:24:25
     */
    fun xmlParser(text: String): Document? {
        var doc: Document? = null
        val sr = StringReader(text)
        val source = InputSource(sr)
        val factory = DocumentBuilderFactory.newInstance()
        try {
            doc = factory.newDocumentBuilder().parse(source)
        } catch (e: Exception) {
            LOG.error("xml parse error", e)
        }

        return doc
    }

    /**
     * 处理emoji表情
     *
     * @param input
     * @author https://github.com/yaphone
     * @date 2017年4月23日 下午2:39:04
     */
    private fun emojiFormatter(input: String): String {
        val matcher = getMatcher("<span class=\"emoji emoji(.{1,10})\"></span>", input)
        val sb = StringBuilder()
        var lastStart = 0
        while (matcher.find()) {
            var str = matcher.group(1)
            when {
                str.length == 6 -> {

                }
                str.length == 10 -> {

                }
                else -> {
                    str = "&#x$str;"
                    val tmp = input.substring(lastStart, matcher.start())
                    sb.append(tmp + str)
                    lastStart = matcher.end()
                }
            }
        }
        if (lastStart < input.length) {
            sb.append(input.substring(lastStart))
        }
        return if (sb.isNotEmpty()) {
            EmojiParser.parseToUnicode(sb.toString())
        } else {
            input
        }
    }

    /**
     * 消息格式化
     *
     * @param input
     * @author https://github.com/yaphone
     * @date 2017年4月23日 下午4:19:08
     */
    fun msgFormatter(input: String): String {
        // TODO 与emoji表情有部分兼容问题，目前暂未处理解码处理
        return emojiFormatter(input.replace("<br/>", "\n"))
    }

}
