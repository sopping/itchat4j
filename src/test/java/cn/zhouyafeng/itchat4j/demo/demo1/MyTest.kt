package cn.zhouyafeng.itchat4j.demo.demo1

import cn.zhouyafeng.itchat4j.Wechat

/**
 *
 * @author https://github.com/yaphone
 * @date 创建时间：2017年4月28日 上午12:44:10
 * @version 1.0
 */
object MyTest {
    @JvmStatic
    fun main(args: Array<String>) {
        Wechat(SimpleDemo()).start()
    }
}
