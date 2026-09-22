# 旁观者 · JevBystander

> 在微信里，把对方的这句话交给 [Jev](https://typesafe.ai) 判一下：**意图 / 情绪 / 着急程度 / 该用什么姿态回**，
> 然后**只弹 3 条提示**。不生成回复文案，不替你发消息，不悬浮窗，不修改微信。

安卓无障碍应用，**零第三方依赖**（只有一个 APK，无 AndroidX、无 ML Kit、无网络库）。

[![Release](https://img.shields.io/github/v/release/Nisaka520/JevBystander?color=4c9aff)](../../releases/latest)
[![build](https://github.com/Nisaka520/JevBystander/actions/workflows/build.yml/badge.svg)](../../actions/workflows/build.yml)
![Android](https://img.shields.io/badge/Android-8.0%2B-3DDC84)
![APK](https://img.shields.io/badge/APK-861%20KB-blue)
![依赖](https://img.shields.io/badge/依赖-零个-brightgreen)
[![License](https://img.shields.io/github/license/Nisaka520/JevBystander)](LICENSE)

**⬇ [下载最新 APK](https://github.com/Nisaka520/JevBystander/releases/latest/download/JevBystander-debug.apk)**（861 KB · Android 8.0+ 直装）
· 🌐 [产品页](https://nisaka520.github.io/JevBystander/) · 📝 [更新日志](CHANGELOG.md)

---

## 它是什么

长按不需要、悬浮窗不需要 —— 在微信里点一下（通知栏磁贴 / 无障碍快捷键 / 常驻通知按钮），
当前聊天窗口里**已经显示出来的**消息会被读出来，交给 Jev 判一次，结果按固定 3 条弹给你：

```text
意图：打招呼或闲聊 85%
情绪：平静 56% · 客套 42% · 着急 2%
———————————————
着急：不着急 · 0.48/3
———————————————
建议：正常交流 90%
```

**它不做什么**：不生成回复文案、不填入输入框、不点击、不发送、不截屏 OCR、不读其它 App、不上传任何东西。

## 跟另外两个项目的关系（先说清楚）

| | 是什么 | 与本项目的关系 |
|---|---|---|
| [jev-chat-jarvis](https://github.com/jev-chat/jev-chat-jarvis) | 「Jev 聊天助手」：无障碍 + 悬浮窗 + **候选回复生成** + OCR，支持微信/QQ/X/飞书 | **思路上的启发来源**：它证明了"无障碍只读屏幕 + Jev 判定"在安卓上可行。本项目的代码、文案、界面、命名全部自己写，没有复制其任何一行 |
| [JevIntent](https://github.com/Nisaka520/JevIntent) | Xposed/FkWeChat 插件：hook 微信，长按消息判读 | **同一套判定口径的姊妹项目**（同一个作者，两套完全独立的实现）。本项目不需要 FkWeChat/LSPosed，代价是只能看到屏幕上可见的文字 |

具体差异（也是"不是照抄"的地方）：

| 维度 | jev-chat-jarvis | 本项目 |
|---|---|---|
| 输出 | 悬浮窗 + 3 条候选回复（要第二个文本模型） | **只有 3 条 Toast**，不生成任何文案 |
| 采集 | 无障碍树 **+ 截图 OCR 兜底**（APK 27MB） | 只读无障碍树，**不做截图/OCR** |
| 依赖 | Kotlin + AndroidX + ML Kit | **零第三方依赖**（手写 JSON、`HttpURLConnection`、原生 View） |
| 关系 | 联系人表 + 知识库笔记 + 历史 | 联系人表（别名 → 关系/性别/备注），无知识库 |
| 场景 | 微信/QQ/X/飞书，含群聊 | **只做微信单聊** |
| 触发 | 前台自动 + 悬浮窗 | 磁贴 / 无障碍按钮 / 通知按钮 / 可选自动（防抖） |

## 安装

**方式 A：下载 APK**（不用装 Android SDK）

- 每次 push 都会在 [Actions](../../actions) 里构建，最新一次的 Artifacts 里有 `JevBystander-apk`；
- 打 tag（`v1.0.0` 这种）会自动发 [Release](../../releases) 并附上 APK。

> CI 和 Release 里挂的是 **debug 签名**的 APK（能直接装，适合自用）。
> 要正式签名自己出一版：`keytool` 生成 keystore 后配置 `signingConfigs`，
> 或把 keystore 用 GitHub Secrets 传给 CI。
>
> **升级须知**：仓库里的 `keystore/debug.keystore` 是**公开的调试签名**，本地构建和 CI 共用它，
> 所以从 v1.0.2 起可以直接覆盖安装。**v1.0.1 及更早的包签名不同，第一次升级请先卸载旧版。**

**方式 B：自己 build**（需要 JDK 17 + Android SDK 35）

```bash
git clone https://github.com/Nisaka520/JevBystander.git
cd JevBystander
./gradlew assembleDebug          # 产物：app/build/outputs/apk/debug/app-debug.apk
./gradlew testDebugUnitTest      # 纯逻辑层单测，不用真机
```

## 首次使用

1. **开启无障碍**：安装后打开 App → 「打开无障碍设置」→ 已下载的服务里找 **旁观者 · 微信判读** → 打开。
   （在系统无障碍设置里还能把它设成**无障碍快捷键/按钮**，之后三指长按就能触发）
2. **填密钥**：App 首页 → 接口密钥 → 贴上自己的 `apikey_…`。
   没有密钥？[console.typesafe.ai](https://console.typesafe.ai) 用 Google 或邮箱验证码登录（首次登录即注册，**不用等审批**）→
   [API Keys](https://console.typesafe.ai/api-keys) → 新建 → 复制。点「测试密钥」当场验证。
3. **在微信里用**：打开任意单聊 → 下拉通知栏点磁贴「旁观一下」（或按无障碍快捷键）→ 看 3 条提示。
4. 想让它自己动：设置里开 **自动判读**（检测到对方新消息、防抖 1.2s 后自动判读）。

## 关系表（比全局"默认关系"准得多）

设置页里一行一个：

```
妈妈,妈,老妈=家人/女/生日三月
张伟,伟哥,老张=同事/男/市场部
```

- 用**会话标题或昵称**去匹配，命中最长的别名生效（"小明妈妈"不会被"小明"抢走）。
- 自动忽略群名尾部人数（`项目组 (8)`）与大小写、空白差异。
- 匹配不到就当 `普通朋友/未知`。
- 备注会作为 `背景` 一起发给 Jev —— 同一句「我爱你」，情侣和同事的判定完全不同。

## 设置项一览

| 项 | 说明 |
|---|---|
| 题目语言 | `zh`（默认，实测客观分最高）/ `mix` / `en`，见下面实验 |
| 模型 | `jev-latest` / `jev-1.13.0` |
| 情绪显示条数 | 1 / 3 / 5（情绪是**一次判定的完整概率分布**前 N 名，不是分开判 N 次） |
| 上下文句数 | 0~10，屏幕上可见的最近几条 |
| 3 条间隔 | 1200 / 2000 / 3000 ms |
| 分析中提示 | 关掉就只有 3 条结果 |
| 自动判读 | 默认关；开了也不重复判读同一条消息 |
| 引用块合成 | 启发式，默认关（宁可不合并，也不误吃一条消息） |
| 常驻通知 | 服务运行时的顺手入口，可关 |

## 实验：题目该用中文还是英文？

有说法称"Jev 主训练语言是英文，题目用英文更准"。我们用同一批 10 个用例 × 3 变体 × 3 次（90 次调用）实测：

| 指标 | zh | en | mix |
|---|---|---|---|
| 合成客观分 | **0.945** | 0.938 | 0.940 |
| 着急 MAE（越小越好） | **0.36** | 0.44 | 0.42 |
| 风险 / 情绪 准确率 | 100% | 100% | 100% |
| 情绪 top1 平均置信度 | 0.808 | **0.905** | 0.822 |
| 3 次重复一致率 | **97%** | **97%** | 93% |
| 与 zh 的标签一致率 | — | **65%** | 95% |

**结论**：英文题目**并不会更准**，只是更"武断"（置信度上去了，标签还漂了 35%）。所以默认 `zh`，
但保留三档开关，谁都能自己复现这个实验。完整数据见 [`docs/实验-题目语言AB.md`](docs/实验-题目语言AB.md)。

## 隐私

- 只订阅 `com.tencent.mm` 的窗口事件，只读**当前窗口里已经显示的文字**。
- 密钥、联系人表、日志全部只在本机 `SharedPreferences`；没有云端、没有统计、没有第三方 SDK，卸载即消失。
- 判读时只把「关系 / 对方性别 / 会话类型 / 最近几条 / 待分析消息」发给 `api.typesafe.ai` —— 和插件版一样的口径。
- 仓库里没有任何密钥；`test/check_secrets.py` 是提交前自检。

## 已知限制（诚实版）

- **只看得到屏幕上可见的部分**：长消息被折叠、图片/语音/文件无法判读。
- **只做单聊**：群聊里"对方"和关系都对不准，本项目直接不判群聊。
- **微信更新可能失效**：无障碍树的结构是对方实现细节，微信改版后可能需要重新适配。
- **国产 ROM 后台限制**：省电策略可能杀掉无障碍服务，需要在系统设置里给自启动/后台白名单。
- **判定不是事实**：Jev 给的是概率分布，第一名低于 40% 时请**看前三名**，别只看第一名。
- **英文题目更自信但会漂**（见上），想稳就用默认中文。

## 读不到消息怎么办（排查三步）

1. **看日志**：设置页最下面的「日志」会写一行
   `抓屏：节点 320 / 带文字 41 / 采用 12 行（屏幕 1080x2400 …）`
   - 采用 **0 行** → 这版微信没把消息文字放进无障碍树（少见）；
   - 采用了一批但**没有对方消息** → 多半是左右判定（我方/对方）在这台机器上不一样。
   读不到时日志还会把前 14 行原文连坐标一起打出来，一眼能看出问题。
2. **抓诊断**：微信聊天页 → 拉下通知栏点「诊断抓屏」；
   或者在设置页点「3 秒后抓取微信窗口」再切回微信。
   它会导出当前窗口的完整节点树（类名 / viewId / 坐标 / 可见性 / 文字）。
3. **发给我**：设置页 →「分享最近一次诊断」→ 发到 [Issues](../../issues) 或 [Discussions](../../discussions)，
   我按你那份结构把适配规则调准。

> 诊断内容**只存在本机**，不会自动上传；只有你自己点「分享」才会发出去。
> 为什么不用 OCR：微信的消息文字本来就在无障碍树里（读屏软件就是靠它朗读的），
> 能用树就不会用截图 —— 少一个 27MB 的依赖，也少一次截屏权限。真遇到不给树的情况，
> 上面第 1 步会明确告诉我们，那时再谈 OCR 兜底。

## 常见问题

<details><summary><b>为什么只看得到一部分消息？</b></summary>

因为只读屏幕上**已经显示出来**的文字。长消息被折叠、图片/语音/文件都读不到。
想要无死角判读，看姊妹项目 [JevIntent](https://github.com/Nisaka520/JevIntent)（Xposed 插件版，能长按任意消息判）。

</details>

<details><summary><b>会被微信封号吗？</b></summary>

不改微信、不注入、不发送、不读非当前窗口的内容，只做系统级读屏（和读屏软件同一套机制），
风险远低于 hook 类插件。但任何第三方工具都不存在「官方保证」。

</details>

<details><summary><b>国产 ROM 上过一会儿就不动了？</b></summary>

省电策略把无障碍服务杀了。去系统设置给「旁观者」加自启动/后台白名单，电池优化设成「不限制」。

</details>

<details><summary><b>密钥要钱吗？怎么领？</b></summary>

在 [console.typesafe.ai](https://console.typesafe.ai/api-keys) 免费注册领取（Google 或邮箱验证码登录，**不用等审批**），
注册完直接建 key。用量计费以官方为准，本项目与之无隶属关系。

</details>

<details><summary><b>能不能加群聊 / QQ / 飞书？</b></summary>

群聊需要判断「这条是谁发的」，无障碍树里给不出可靠身份，所以本版本直接不判群聊 —— 宁可不做，也不给错的关系。
其它 App 需要各自适配，暂时没有计划。

</details>

## 反馈与交流

- 有 bug、有想法、想要新的关系表玩法 → [提 Issue](../../issues)
- 想聊怎么用、分享自己的关系表配置 → [Discussions](../../discussions)
- 觉得好用 → 给个 ⭐ 就是最大的支持（也方便别人搜到）

## 开发

```bash
./gradlew testDebugUnitTest     # 纯逻辑层：JSON/题目构造/解析/关系匹配/抓屏组装
python test/check_secrets.py    # 提交前自检

# 端到端冒烟（真的打一次 api.typesafe.ai，默认跳过，不设密钥不会发请求）
JEV_KEY=apikey_… ./gradlew testDebugUnitTest --tests '*LiveJevSmokeTest*'
```

本地实测（2026-09，真接口）：`意图：催促进度或催回复 86%` / `情绪：着急 66% · 平静 24% · 焦虑 9%` /
`着急：一般 · 1.37/3` / `建议：正常交流 98%`，耗时 **0.97 秒**。

代码布局（`app/src/main/java/io/github/nisaka520/jevbystander/`）：

| 文件 | 职责 | 依赖 Android？ |
|---|---|---|
| `Json.kt` | 手写 JSON 解析/生成 | 否 |
| `Prompt.kt` | 7 个问题 + 三档语言 | 否 |
| `Verdict.kt` | 解析答案 → 3 条提示的排版 | 否 |
| `Contacts.kt` | 联系人表与别名匹配 | 否 |
| `Digest.kt` / `DigestBuilder.kt` | 屏幕行 → 会话摘要 / 去噪去重 / 我方判定 | 否 |
| `JevHttp.kt` | 唯一网络出口（`HttpURLConnection`） | 是 |
| `WeChatReader.kt` | 无障碍树 → RawLine | 是 |
| `Analyzer.kt` | 编排 + 只允许一次在跑 | 是 |
| `WatchService.kt` / `TileTrigger.kt` / `AnalyzeReceiver.kt` | 触发入口 | 是 |
| `SettingsActivity.kt` | 唯一界面（代码搭 UI） | 是 |

前 5 个文件**不碰 Android SDK**，所以整套判定逻辑可以在电脑上跑单测 —— 这也是从姊姊项目 JevIntent 学来的做法。

## 构建说明（诚实交代）

- **本项目是在 AI 编程助手（DeepSeek Harness 上的编码 Agent）协作下完成的**：架构取舍、判定口径、
  边界（只读 / 不发送 / 只做单聊）与最终验收由作者决定，代码、文案、界面在人工评审后提交。
- 仓库里有 **35 个单元测试**（纯逻辑层不依赖 Android SDK）和一个真接口端到端冒烟测试，CI 每次 push 都跑。
- 它是 [JevIntent](https://github.com/Nisaka520/JevIntent) 的姊妹项目，两套实现完全独立；灵感来源与差异见上。

## License

MIT © Nisaka520 · 判读模型由 [TypeSafe Jev](https://typesafe.ai) 提供，本项目与之无隶属关系。

灵感来源 [jev-chat/jev-chat-jarvis](https://github.com/jev-chat/jev-chat-jarvis)（已在上面逐项列差异并致谢，实现完全独立）。
