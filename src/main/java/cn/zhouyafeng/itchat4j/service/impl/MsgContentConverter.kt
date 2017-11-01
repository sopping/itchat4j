package cn.zhouyafeng.itchat4j.service.impl

import cn.zhouyafeng.itchat4j.beans.BaseMsg
import cn.zhouyafeng.itchat4j.service.MsgConverter

/**
 * 消息转化为String，直接取content
 * Created by sopping on 2017/10/31.
 * @author sopping
 */
class MsgContentConverter : MsgConverter<String> {

    override fun convert(msg: BaseMsg): String {
        return msg.content
    }
}