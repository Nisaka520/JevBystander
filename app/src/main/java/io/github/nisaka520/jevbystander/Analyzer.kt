package io.github.nisaka520.jevbystander

import android.content.Context
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 判读流程的编排：抓屏结果 → 关系匹配 → state → 调 Jev → 3 条 Toast。
 *
 * 同一时刻只允许一次判读在跑（无障碍事件很密集，重复触发只会互相踩）；
 * 手动触发（磁贴/无障碍按钮）在忙的时候会明确告诉你"正在判读中"，
 * 而不是静默丢掉 —— 这类工具最忌讳"点了没反应"。
 */
object Analyzer {

    private val pool = Executors.newSingleThreadExecutor { r -> Thread(r, "jev-analyze") }
    private val running = AtomicBoolean(false)

    fun run(ctx: Context, digest: Digest, manual: Boolean = true) {
        val cfg = Config(ctx)
        if (!cfg.hasKey()) {
            Toast3.toast(ctx, "还没填接口密钥：打开「旁观者」设置 → 接口密钥", true)
            AppLog.add("未配置密钥，已拒绝判读")
            return
        }
        if (!running.compareAndSet(false, true)) {
            if (manual) Toast3.toast(ctx, "上一次还在判读中…")
            return
        }

        val target = digest.latestPeerMessage()
        if (target == null) {
            running.set(false)
            if (manual) Toast3.toast(ctx, "这个窗口里没看到对方发的消息", true)
            AppLog.add("抓屏结果里没有对方消息（标题=${digest.title}，共 ${digest.msgs.size} 行）")
            return
        }

        val contact = Contacts.match(cfg.contacts(), digest.title, digest.peer)
            ?: Contacts.fallback(digest.title)
        val state = digest.state(contact, cfg.contextN)
        if (state.isEmpty()) {
            running.set(false)
            Toast3.toast(ctx, "没拼出可判读的内容", true)
            return
        }

        if (cfg.showAnalyzing) Toast3.toast(ctx, "Jev 分析中…")
        AppLog.add("判读开始：标题=${digest.title} 关系=${contact.relation} 上下文=${cfg.contextN} 语言=${cfg.lang} 消息长度=${target.text.length}")

        pool.execute {
            val body = Prompt.requestJson(state, cfg.model, cfg.lang)
            val t0 = System.currentTimeMillis()
            val result = JevHttp.analyze(cfg.apiKey, body)
            val cost = System.currentTimeMillis() - t0
            when (result) {
                is JevResult.Err -> {
                    AppLog.add("判读失败（${cost}ms）：${result.message}")
                    Toast3.toast(ctx, "Jev 失败：" + result.message, true)
                }
                is JevResult.Ok -> {
                    val v = Verdicts.parse(result.body, cfg.lang, cfg.emotionTop)
                    if (v == null) {
                        AppLog.add("响应里没有可用答案（${cost}ms）：" + result.body.take(300))
                        Toast3.toast(ctx, "Jev 返回了空结果，看设置里的日志", true)
                    } else {
                        cfg.lastVerdict = v.detail() + "\n（" + cost + "ms · " + contact.relation + " · " + cfg.lang + "）"
                        AppLog.add("判读完成（${cost}ms）：" + v.lines(cfg.emotionTop).joinToString(" / ").replace("\n", " "))
                        Toast3.showLines(ctx, v.lines(cfg.emotionTop), cfg.toastGapMs)
                    }
                }
            }
            running.set(false)
        }
    }

    fun busy(): Boolean = running.get()
}
