package cn.zhouyafeng.itchat4j.beans

import java.io.Serializable

/**
 * RecommendInfo
 *
 * @author https://github.com/yaphone
 * @version 1.0
 * @date 创建时间：2017年7月3日 下午10:35:14
 */
class RecommendInfo : Serializable {

    var ticket: String = ""
    var userName: String = ""
    var city: String = ""
    var nickName: String = ""
    var province: String = ""
    var sex: Int = 0
    var attrStatus: Int = 0
    var scene: Int = 0
    var content: String = ""
    var alias: String = ""
    var signature: String = ""
    var opCode: Int = 0
    private var qqNum: Int = 0
    var verifyFlag: Int = 0

    fun getQqNum(): Int {
        return qqNum
    }

    fun setQqNum(qqNum: Int) {
        this.qqNum = qqNum
    }

}
