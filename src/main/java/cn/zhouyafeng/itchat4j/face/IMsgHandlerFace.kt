package cn.zhouyafeng.itchat4j.face

import cn.zhouyafeng.itchat4j.beans.BaseMsg

/**
 * 消息处理接口
 *
 * @author https://github.com/yaphone
 * @version 1.0
 * @date 创建时间：2017年4月20日 上午12:13:49
 */
interface IMsgHandlerFace {

    /**
     * 消息处理
     */
    fun handle(msg: BaseMsg)
}
