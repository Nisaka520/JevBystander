package io.github.nisaka520.jevbystander

import android.content.Context

/**
 * 本机设置。全部存在 SharedPreferences 里（`getSharedPreferences("jevbystander")`），
 * 密钥与联系人表都只在本机，随卸载一起消失；不写外部存储、不上传。
 */
class Config(ctx: Context) {

    private val sp = ctx.applicationContext.getSharedPreferences("jevbystander", Context.MODE_PRIVATE)

    var apiKey: String
        get() = sp.getString("api_key", "").orEmpty()
        set(v) = sp.edit().putString("api_key", v.trim()).apply()

    var model: String
        get() = sp.getString("model", Prompt.MODELS[0]).orEmpty().ifEmpty { Prompt.MODELS[0] }
        set(v) = sp.edit().putString("model", v).apply()

    /** 题目语言：zh / mix / en（见 docs/实验-题目语言AB.md） */
    var lang: String
        get() = sp.getString("lang", "zh").orEmpty().ifEmpty { "zh" }
        set(v) = sp.edit().putString("lang", v).apply()

    /** 情绪显示条数（1 / 3 / 5） */
    var emotionTop: Int
        get() = sp.getInt("emotion_top", 3).coerceIn(1, 5)
        set(v) = sp.edit().putInt("emotion_top", v.coerceIn(1, 5)).apply()

    /** 上下文句数 */
    var contextN: Int
        get() = sp.getInt("context_n", 3).coerceIn(0, 10)
        set(v) = sp.edit().putInt("context_n", v.coerceIn(0, 10)).apply()

    /** 3 条结果之间的间隔 */
    var toastGapMs: Int
        get() = sp.getInt("toast_gap_ms", 2000).coerceIn(600, 6000)
        set(v) = sp.edit().putInt("toast_gap_ms", v.coerceIn(600, 6000)).apply()

    /** 分析一开始先弹一条"分析中…"（第 0 条，可关；关了就只有 3 条结果） */
    var showAnalyzing: Boolean
        get() = sp.getBoolean("show_analyzing", true)
        set(v) = sp.edit().putBoolean("show_analyzing", v).apply()

    /** 检测到对方新消息就自动判读（默认关，避免打扰） */
    var autoAnalyze: Boolean
        get() = sp.getBoolean("auto_analyze", false)
        set(v) = sp.edit().putBoolean("auto_analyze", v).apply()

    /** 自动模式的防抖：新消息停下多久后才分析 */
    var autoDebounceMs: Int
        get() = sp.getInt("auto_debounce_ms", 1200).coerceIn(400, 8000)
        set(v) = sp.edit().putInt("auto_debounce_ms", v.coerceIn(400, 8000)).apply()

    /** 是否尝试把引用块合成一条（启发式，默认关） */
    var linkQuotes: Boolean
        get() = sp.getBoolean("link_quotes", false)
        set(v) = sp.edit().putBoolean("link_quotes", v).apply()

    /** 服务运行时是否挂一条常驻通知当手动入口 */
    var showNotification: Boolean
        get() = sp.getBoolean("show_notification", true)
        set(v) = sp.edit().putBoolean("show_notification", v).apply()

    /** 是否把最近一次结果留在设置页（方便回头细看） */
    var lastVerdict: String
        get() = sp.getString("last_verdict", "").orEmpty()
        set(v) = sp.edit().putString("last_verdict", v).apply()

    /** 最近一次抓屏诊断的原文（只在本机；分享与否由你决定） */
    var lastDump: String
        get() = sp.getString("last_dump", "").orEmpty()
        set(v) = sp.edit().putString("last_dump", v.take(400_000)).apply()

    /** 最近一次诊断文件的路径 */
    var lastDumpPath: String
        get() = sp.getString("last_dump_path", "").orEmpty()
        set(v) = sp.edit().putString("last_dump_path", v).apply()

    var contactsJson: String
        get() = sp.getString("contacts", "[]").orEmpty().ifEmpty { "[]" }
        set(v) = sp.edit().putString("contacts", v).apply()

    fun contacts(): List<Contact> = Contacts.fromJson(contactsJson)

    fun saveContacts(list: List<Contact>) {
        contactsJson = Contacts.toJson(list)
    }

    /** 密钥是否可用（与插件版同一判据：≥20 字符） */
    fun hasKey(): Boolean = apiKey.length >= 20

    fun clearAll() = sp.edit().clear().apply()
}
