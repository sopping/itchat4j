package cn.zhouyafeng.itchat4j.controller

import cn.zhouyafeng.itchat4j.api.WechatTools
import cn.zhouyafeng.itchat4j.core.Core
import cn.zhouyafeng.itchat4j.service.impl.LoginServiceImpl
import cn.zhouyafeng.itchat4j.thread.CheckLoginStatusThread
import cn.zhouyafeng.itchat4j.utils.SleepUtils
import cn.zhouyafeng.itchat4j.utils.tools.CommonTools
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

/**
 * 登陆控制器
 *
 * @author https://github.com/yaphone
 * @version 1.0
 * @date 创建时间：2017年5月13日 下午12:56:07
 */
class LoginController {
    private val loginService = LoginServiceImpl()

    fun login() {
        if (core.isAlive) { // 已登陆
            LOG.info("itchat4j已登陆")
            return
        }
        while (true) {
            for (count in 0..9) {
                LOG.info("获取UUID")
                while (loginService.uuid == null) {
                    LOG.info("1. 获取微信UUID")
                    while (loginService.uuid == null) {
                        LOG.warn("1.1. 获取微信UUID失败，两秒后重新获取")
                        SleepUtils.sleep(2, TimeUnit.SECONDS)
                    }
                }
                LOG.info("2. 获取登陆二维码图片")
                if (loginService.getQR()) {
                    break
                } else if (count == 10) {
                    LOG.error("2.2. 获取登陆二维码图片失败，系统退出")
                    System.exit(0)
                }
            }
            LOG.info("3. 请扫描二维码图片，并在手机上确认")
            if (!core.isAlive) {
                loginService.login()
                core.isAlive = true
                LOG.info("登陆成功")
                break
            }
            LOG.info("4. 登陆超时，请重新扫描二维码图片")
        }

        LOG.info("5. 登陆成功，微信初始化")
        if (!loginService.init()) {
            LOG.info("6. 微信初始化异常")
            System.exit(0)
        }

        LOG.info("6. 开启微信状态通知")
        loginService.statusNotify()

        LOG.info("7. 清除。。。。")
        CommonTools.clearScreen()
        LOG.info(String.format("欢迎回来， %s", core.nickName))

        LOG.info("8. 开始接收消息")
        loginService.startReceiving()

        LOG.info("9. 获取联系人信息")
        loginService.getUserContact()

        LOG.info("10. 获取群好友及群好友列表")
        loginService.getGroupContact()

        LOG.info("11. 缓存本次登陆好友相关消息")
        WechatTools.setUserInfo() // 登陆成功后缓存本次登陆好友相关消息（NickName, UserName）

        LOG.info("12.开启微信状态检测线程")
        Thread(CheckLoginStatusThread()).start()
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(LoginController::class.java)
        private val core = Core.getInstance()
    }
}