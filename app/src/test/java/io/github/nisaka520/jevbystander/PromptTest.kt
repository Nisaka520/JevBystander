package io.github.nisaka520.jevbystander

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PromptTest {

    private val state = Json.write(mapOf("关系" to "同事", "待分析消息" to "在吗？"))

    @Test
    fun requestIsValidJsonWithSevenQuestions() {
        val o = Json.obj(Json.parse(Prompt.requestJson(state, "jev-latest", "zh")))
        assertEquals("jev-latest", Json.str(o["model"]))
        val q = Json.obj(o["questions"])
        assertEquals(
            listOf("intent", "urgency", "need_reply", "risk", "advice", "emotion", "reply_style").sorted(),
            q.keys.sorted()
        )
        assertNotNull(o["state"])
    }

    @Test
    fun criteriaSizesMatchTheFrozenGrids() {
        for (lang in Prompt.LANGS) {
            val q = Json.obj(Json.at(Json.parse(Prompt.requestJson(state, "jev-latest", lang)), "questions"))
            assertEquals(10, Json.obj(Json.at(q["intent"], "criteria")).size)
            assertEquals(4, Json.list(Json.at(q["urgency"], "criteria")).size)
            assertEquals(8, Json.obj(Json.at(q["advice"], "criteria")).size)
            assertEquals(9, Json.obj(Json.at(q["emotion"], "criteria")).size)
            assertEquals(11, Json.obj(Json.at(q["reply_style"], "criteria")).size)
            assertEquals("choice", Json.str(Json.at(q["emotion"], "type")))
            assertEquals("score", Json.str(Json.at(q["urgency"], "type")))
            assertEquals("noul", Json.str(Json.at(q["risk"], "type")))
        }
    }

    @Test
    fun zhKeepsChineseCriteriaAndEnUsesEnglishKeys() {
        val zh = Json.obj(Json.at(Json.parse(Prompt.requestJson(state, "m", "zh")), "questions"))
        assertTrue(Json.obj(Json.at(zh["emotion"], "criteria")).containsKey("平静"))

        val en = Json.obj(Json.at(Json.parse(Prompt.requestJson(state, "m", "en")), "questions"))
        assertTrue(Json.obj(Json.at(en["emotion"], "criteria")).containsKey("calm"))
        assertTrue(Json.list(Json.at(en["urgency"], "criteria")).contains("extremely urgent"))

        // mix：英文提问 + 中文选项
        val mix = Json.obj(Json.at(Json.parse(Prompt.requestJson(state, "m", "mix")), "questions"))
        assertTrue(Json.obj(Json.at(mix["emotion"], "criteria")).containsKey("平静"))
        assertTrue(Json.str(Json.at(mix["emotion"], "instructions")).startsWith("Considering"))
    }

    @Test
    fun instructionsDifferPerLanguage() {
        val zh = Json.str(Json.at(Json.parse(Prompt.requestJson(state, "m", "zh")), "questions.emotion.instructions"))
        val en = Json.str(Json.at(Json.parse(Prompt.requestJson(state, "m", "en")), "questions.emotion.instructions"))
        assertTrue(zh.contains("情绪"))
        assertTrue(en.contains("emotion"))
        assertFalse(zh == en)
    }

    @Test
    fun englishKeysMapBackToChineseLabels() {
        assertEquals("平静", Prompt.toZh("emotion", "calm"))
        assertEquals("建议温和婉拒", Prompt.toZh("reply_style", "gentle_decline"))
        assertEquals("催促进度或催回复", Prompt.toZh("intent", "pressing_progress"))
        // 已是中文 / 未知键原样返回
        assertEquals("平静", Prompt.toZh("emotion", "平静"))
        assertEquals("???", Prompt.toZh("emotion", "???"))
    }

    @Test
    fun everyEnglishKeyHasAChineseTwin() {
        assertEquals(Prompt.EMOTIONS.size, Prompt.EN_OF["emotion"]!!.size)
        assertEquals(Prompt.INTENTS.size, Prompt.EN_OF["intent"]!!.size)
        assertEquals(Prompt.ADVICES.size, Prompt.EN_OF["advice"]!!.size)
        assertEquals(Prompt.STYLES.size, Prompt.EN_OF["reply_style"]!!.size)
    }
}
