package cn.zhouyafeng.itchat4j.service

/**
 * 登陆服务接口
 *
 * @author https://github.com/yaphone
 * @version 1.0
 * @date 创建时间：2017年5月13日 上午12:07:21
 */
interface ILoginService {

    /**
     * 登陆
     *
     * @return
     * @author https://github.com/yaphone
     * @date 2017年5月13日 上午12:14:07
     */
    fun login(): Boolean

    /**
     * 获取UUID
     *
     * @param qrPath
     * @return
     * @author https://github.com/yaphone
     * @date 2017年5月13日 上午12:21:40
     */
    val uuid: String?

    /**
     * 获取二维码图片
     *
     * @return
     * @author https://github.com/yaphone
     * @date 2017年5月13日 上午12:13:51
     */
    fun getQR(): Boolean

    /**
     * web初始化
     *
     * @return
     * @author https://github.com/yaphone
     * @date 2017年5月13日 上午12:14:13
     */
    fun init(): Boolean

    /**
     * 微信状态通知
     *
     * @author https://github.com/yaphone
     * @date 2017年5月13日 上午12:14:24
     */
    fun statusNotify()

    /**
     * 接收消息
     *
     * @author https://github.com/yaphone
     * @date 2017年5月13日 上午12:14:37
     */
    fun startReceiving()

    /**
     * 获取微信联系人
     *
     * @author https://github.com/yaphone
     * @date 2017年5月13日 下午2:26:18
     */
    fun getUserContact()

    /**
     * 获取群联系人信息
     *
     * @date 2017年6月22日 下午11:24:35
     */
    fun getGroupContact()

}
