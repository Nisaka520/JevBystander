package io.github.nisaka520.jevbystander

import android.accessibilityservice.AccessibilityService
import android.graphics.Rect
import android.view.accessibility.AccessibilityNodeInfo

/**
 * 把微信当前窗口的无障碍树变成 Digest。
 *
 * 只做"读"：不点击、不注入、不截屏。节点里没有文字就什么都没有 —— 这是本项目的
 * 明确取舍（另一条路是截图 + OCR，我们不做，包体和权限都省下来）。
 */
object WeChatReader {

    private const val MAX_NODES = 1500
    private const val MAX_DEPTH = 30

    fun capture(service: AccessibilityService, linkQuotes: Boolean): Digest? {
        val root = service.rootInActiveWindow ?: return null
        val pkg = root.packageName?.toString().orEmpty()
        if (pkg != WECHAT_PKG) {
            AppLog.add("当前前台不是微信（$pkg），跳过")
            return null
        }
        root.refresh()
        val dm = service.resources.displayMetrics
        val lines = ArrayList<RawLine>(64)
        walk(root, lines, 0, service.packageName.toString())
        AppLog.add("抓到 ${lines.size} 行文本（窗口=${root.windowId}）")
        return DigestBuilder.build(
            raw = lines,
            screenW = dm.widthPixels,
            screenH = dm.heightPixels,
            linkQuotes = linkQuotes
        )
    }

    private fun walk(node: AccessibilityNodeInfo?, out: MutableList<RawLine>, depth: Int, ownPkg: String) {
        if (node == null || depth > MAX_DEPTH || out.size >= MAX_NODES) return
        try {
            val pkg = node.packageName?.toString().orEmpty()
            if (pkg == ownPkg) return
            if (node.isVisibleToUser) {
                val text = node.text?.toString().orEmpty().ifEmpty { node.contentDescription?.toString().orEmpty() }
                if (text.isNotBlank()) {
                    val r = Rect()
                    node.getBoundsInScreen(r)
                    if (r.width() > 0 && r.height() > 0) {
                        out.add(RawLine(text, r.left, r.top, r.right, r.bottom))
                    }
                }
            }
            val n = node.childCount
            for (i in 0 until n) {
                if (out.size >= MAX_NODES) break
                walk(node.getChild(i), out, depth + 1, ownPkg)
            }
        } catch (e: Exception) {
            AppLog.add("遍历节点异常（depth=$depth）：${e.javaClass.simpleName}")
        }
    }

    const val WECHAT_PKG = "com.tencent.mm"
}
