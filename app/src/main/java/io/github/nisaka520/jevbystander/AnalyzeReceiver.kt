package io.github.nisaka520.jevbystander

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * 通知栏那条"判读一下"按钮的落地：不能直接起 Activity（会跳界面），
 * 所以走广播 → 找服务实例 → 立刻判读一次。
 */
class AnalyzeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != ACTION_NOW && intent?.action != null) return
        val svc = WatchService.instance
        if (svc == null) {
            Toast3.toast(context, "无障碍服务没在运行：设置里开启「旁观者 · 微信判读」", true)
            return
        }
        svc.analyzeNow(true)
    }

    companion object {
        const val ACTION_NOW = "io.github.nisaka520.jevbystander.NOW"
    }
}
