package cn.zhouyafeng.itchat4j.service

import cn.zhouyafeng.itchat4j.beans.BaseMsg

/**
 * 消息转化接口
 * Created by sopping on 2017/10/31.
 * @author sopping
 */
interface MsgConverter<out T> {

    /**
     * 消息转化
     */
    fun convert(msg: BaseMsg): T
}