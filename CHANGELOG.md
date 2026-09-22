# 更新日志

格式参考 [Keep a Changelog](https://keepachangelog.com/zh-CN/1.1.0/)，版本号遵循 [语义化版本](https://semver.org/lang/zh-CN/)。

## [1.0.0] - 2026-09-22

第一个能用的版本。

### 新增
- 无障碍服务只读微信当前聊天窗口，抓出「关系 / 对方性别 / 会话类型 / 最近 N 条 / 待判读消息」
- 一次性问 Jev 7 个问题，输出固定 3 条提示：① 意图 + 情绪前 N 名 ② 着急程度 ③ 建议姿态
- 关系表：`别名1,别名2=关系/性别/备注`，按会话标题或昵称匹配，命中最长别名生效，备注作为背景进判定
- 四种触发方式：通知栏磁贴 / 系统无障碍按钮 / 常驻通知按钮 / 可选自动判读（防抖 + 同一条不重复判）
- 设置页：密钥、题目语言（zh/mix/en）、模型、情绪条数、上下文句数、提示间隔、引用块合成、常驻通知
- 零第三方依赖：手写 JSON、`HttpURLConnection`、原生 `View`（APK 861 KB，只申请 联网 + 通知 两个权限）
- 35 个单元测试，其中纯逻辑层不依赖 Android SDK；外加一个真接口端到端冒烟测试（默认跳过）
- GitHub Actions 自动构建 + 打 tag 自动发 Release

### 实验结论（决定了默认题目语言）
- 90 次真实调用 A/B：zh 合成客观分 0.945 / en 0.938 / mix 0.940
- 英文题目并不会更准，只是 top1 置信度更高（0.905 vs 0.808），且与中文标签一致率只有 65%
- 因此默认 `zh`，保留三档开关供复现 —— 详见 `docs/实验-题目语言AB.md`

### 已知限制
- 只能看到屏幕上已显示的文字；图片/语音/文件、被折叠的长消息都读不到
- 只做单聊：群聊里「对方」和关系都对不准，本版本直接不判群聊
- 微信改版可能让无障碍树结构变化，需要重新适配
- 国产 ROM 的省电策略可能杀掉无障碍服务

### 修复（构建期踩到的坑，留给后来人）
- Windows 上提交的 `gradlew` 会丢可执行位（git 记 `100644`），Linux CI 上直接 `Permission denied`；
  已用 `git update-index --chmod=+x gradlew` 修正，`.gitattributes` 里也锁了行尾
- `Json.at()` 原本只能下钻对象，补上数组下标（`a.b.2.c`）
- 无障碍按钮的回调接口是 `registerAccessibilityButtonCallback`（不是 `registerCallback`），
  且 `AccessibilityService` 没有 `onAccessibilityButtonClicked` 这个可覆写方法
