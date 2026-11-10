/*
 * Copyright (c) 2014-2026 Stream.io Inc. All rights reserved.
 *
 * Licensed under the Stream License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://github.com/GetStream/stream-feeds-android/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.getstream.feeds.android.client.internal.model

import io.getstream.feeds.android.client.api.model.ThreadedCommentData
import io.getstream.feeds.android.client.internal.test.TestData.activityData
import io.getstream.feeds.android.client.internal.test.TestData.activityResponse
import io.getstream.feeds.android.client.internal.test.TestData.commentData
import io.getstream.feeds.android.client.internal.test.TestData.commentResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

internal class TranslationOperationsTest {

    private val i18n = mapOf("language" to "en", "en_text" to "Hello", "fr_text" to "Bonjour")
    private val translations = mapOf("en" to "Hello", "fr" to "Bonjour")

    @Test
    fun `on originalLanguage, read the language key`() {
        assertEquals("en", i18n.originalLanguage())
        assertNull(emptyMap<String, String>().originalLanguage())
        assertNull((null as Map<String, String>?).originalLanguage())
    }

    @Test
    fun `on translations, key the text entries by language code`() {
        assertEquals(translations, i18n.translations())
        assertEquals(emptyMap<String, String>(), (null as Map<String, String>?).translations())
    }

    @Test
    fun `on ActivityResponse toModel, map i18n to translations and original language`() {
        val activity = activityResponse().copy(i18n = i18n).toModel()

        assertEquals("en", activity.originalLanguage)
        assertEquals(translations, activity.translations)
    }

    @Test
    fun `on CommentResponse toModel, map i18n to translations and original language`() {
        val comment = commentResponse().copy(i18n = i18n).toModel()

        assertEquals("en", comment.originalLanguage)
        assertEquals(translations, comment.translations)
    }

    @Test
    fun `on getTranslation, return the translation to another language`() {
        val activity = translatedActivity()

        assertEquals("Bonjour", activity.getTranslation("fr"))
    }

    @Test
    fun `on getTranslation with the original language, return null`() {
        val activity = translatedActivity()

        assertNull(activity.getTranslation("en"))
    }

    @Test
    fun `on getTranslation with a missing language, return null`() {
        val activity = translatedActivity()

        assertNull(activity.getTranslation("de"))
    }

    @Test
    fun `on getTranslation when the translation equals the text, return null`() {
        val comment =
            commentData("comment-1", text = "Hello")
                .copy(originalLanguage = "en", translations = mapOf("fr" to "Hello"))

        assertNull(comment.getTranslation("fr"))
        assertNull(ThreadedCommentData(comment).getTranslation("fr"))
    }

    @Test
    fun `on activity update without translations and same text, keep translations`() {
        val activity = translatedActivity()
        val updated = activityData("activity-1", text = "Hello").copy(popularity = 5)

        val result = activity.update(updated)

        assertEquals("en", result.originalLanguage)
        assertEquals(translations, result.translations)
        assertEquals(5, result.popularity)
    }

    @Test
    fun `on activity update with changed text, drop translations`() {
        val activity = translatedActivity()
        val updated = activityData("activity-1", text = "Goodbye")

        val result = activity.update(updated)

        assertNull(result.originalLanguage)
        assertEquals(emptyMap<String, String>(), result.translations)
    }

    @Test
    fun `on activity update with translations, use the updated ones`() {
        val activity = translatedActivity()
        val newTranslations = mapOf("de" to "Hallo")
        val updated =
            activityData("activity-1", text = "Hello")
                .copy(originalLanguage = "en", translations = newTranslations)

        val result = activity.update(updated)

        assertEquals(newTranslations, result.translations)
    }

    @Test
    fun `on comment update without translations and same text, keep translations`() {
        val comment = translatedComment()
        val updated = commentData("comment-1", text = "Hello").copy(replyCount = 3)

        val result = comment.update(updated)
        val threadedResult = ThreadedCommentData(comment).update(updated)

        assertEquals("en", result.originalLanguage)
        assertEquals(translations, result.translations)
        assertEquals(3, result.replyCount)
        assertEquals("en", threadedResult.originalLanguage)
        assertEquals(translations, threadedResult.translations)
        assertEquals(3, threadedResult.replyCount)
    }

    @Test
    fun `on comment update with changed text, drop translations`() {
        val comment = translatedComment()
        val updated = commentData("comment-1", text = "Goodbye")

        val result = comment.update(updated)
        val threadedResult = ThreadedCommentData(comment).update(updated)

        assertNull(result.originalLanguage)
        assertEquals(emptyMap<String, String>(), result.translations)
        assertNull(threadedResult.originalLanguage)
        assertEquals(emptyMap<String, String>(), threadedResult.translations)
    }

    private fun translatedActivity() =
        activityData("activity-1", text = "Hello")
            .copy(originalLanguage = "en", translations = translations)

    private fun translatedComment() =
        commentData("comment-1", text = "Hello")
            .copy(originalLanguage = "en", translations = translations)
}
