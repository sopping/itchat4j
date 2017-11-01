package cn.zhouyafeng.itchat4j.utils

import java.io.IOException
import kotlin.collections.Map.Entry
import java.util.logging.Logger

import org.apache.http.Consts
import org.apache.http.HttpEntity
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.CookieStore
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.cookie.Cookie
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.BasicCookieStore
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils

/**
 * HTTP访问类，对Apache HttpClient进行简单封装，适配器模式
 *
 * @author https://github.com/yaphone
 * @version 1.0
 * @date 创建时间：2017年4月9日 下午7:05:04
 */
class MyHttpClient private constructor() {

    private val logger = Logger.getLogger("MyHttpClient")

    /**
     * 处理GET请求
     *
     * @param url
     * @param params
     * @return
     * @author https://github.com/yaphone
     * @date 2017年4月9日 下午7:06:19
     */
    fun doGet(url: String, params: List<BasicNameValuePair>?, redirect: Boolean, headerMap: Map<String, String>?): HttpEntity? {
        var entity: HttpEntity? = null
        var httpGet = HttpGet()

        try {
            if (params != null) {
                val paramStr = EntityUtils.toString(UrlEncodedFormEntity(params, Consts.UTF_8))
                httpGet = HttpGet(url + "?" + paramStr)
            } else {
                httpGet = HttpGet(url)
            }
            if (!redirect) {
                httpGet.config = RequestConfig.custom().setRedirectsEnabled(false).build() // 禁止重定向
            }
            httpGet.setHeader("User-Agent", Config.USER_AGENT)
            if (headerMap != null) {
                val entries = headerMap.entries
                for ((key, value) in entries) {
                    httpGet.setHeader(key, value)
                }
            }
            val response = httpClient.execute(httpGet)
            entity = response.entity
        } catch (e: ClientProtocolException) {
            logger.info(e.message)
        } catch (e: IOException) {
            logger.info(e.message)
        }

        return entity
    }

    /**
     * 处理POST请求
     *
     * @param url
     * @param params
     * @return
     * @author https://github.com/yaphone
     * @date 2017年4月9日 下午7:06:35
     */
    fun doPost(url: String, paramsStr: String): HttpEntity? {
        var entity: HttpEntity? = null
        var httpPost = HttpPost()
        try {
            val params = StringEntity(paramsStr, Consts.UTF_8)
            httpPost = HttpPost(url)
            httpPost.entity = params
            httpPost.setHeader("Content-message", "application/json; charset=utf-8")
            httpPost.setHeader("User-Agent", Config.USER_AGENT)
            val response = httpClient.execute(httpPost)
            entity = response.entity
        } catch (e: ClientProtocolException) {
            logger.info(e.message)
        } catch (e: IOException) {
            logger.info(e.message)
        }

        return entity
    }

    /**
     * 上传文件到服务器
     *
     * @param url
     * @param reqEntity
     * @return
     * @author https://github.com/yaphone
     * @date 2017年5月7日 下午9:19:23
     */
    fun doPostFile(url: String, reqEntity: HttpEntity): HttpEntity? {
        var entity: HttpEntity? = null
        val httpPost = HttpPost(url)
        httpPost.setHeader("User-Agent", Config.USER_AGENT)
        httpPost.entity = reqEntity
        try {
            val response = httpClient.execute(httpPost)
            entity = response.entity

        } catch (e: Exception) {
            logger.info(e.message)
        }

        return entity
    }

    companion object {
        var httpClient = HttpClients.createDefault()
            private set
        private var INSTANCE: MyHttpClient? = null
        private var cookieStore: CookieStore? = null

        init {
            cookieStore = BasicCookieStore()

            // 将CookieStore设置到httpClient中
            httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build()
        }

        fun getCookie(name: String): String? {
            val cookies = cookieStore!!.cookies
            for (cookie in cookies) {
                if (cookie.name.equals(name, ignoreCase = true)) {
                    return cookie.value
                }
            }
            return null

        }

        /**
         * 获取cookies
         *
         * @return
         * @author https://github.com/yaphone
         * @date 2017年5月7日 下午8:37:17
         */
        fun getInstance(): MyHttpClient {
            if (INSTANCE == null) {
                synchronized(MyHttpClient::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = MyHttpClient()
                    }
                }
            }
            return INSTANCE!!
        }
    }

}