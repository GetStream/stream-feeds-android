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

package io.getstream.feeds.android.client.api.model

import io.getstream.feeds.android.network.models.Action
import io.getstream.feeds.android.network.models.Attachment
import io.getstream.feeds.android.network.models.Field
import io.getstream.feeds.android.network.models.GetOGResponse
import org.junit.Assert.assertEquals
import org.junit.Test

internal class AttachmentTest {

    @Test
    fun `toAttachment maps all fields correctly`() {
        // Given
        val actions = listOf(Action(name = "action1", text = "Action 1", type = "button"))
        val fields = listOf(Field(short = true, title = "Field 1", value = "Value 1"))
        val response =
            GetOGResponse(
                duration = "100ms",
                assetUrl = "https://example.com/asset.mp4",
                authorIcon = "https://example.com/icon.png",
                authorLink = "https://example.com/author",
                authorName = "Author",
                color = "#FF0000",
                fallback = "Fallback text",
                footer = "Footer text",
                footerIcon = "https://example.com/footer-icon.png",
                imageUrl = "https://example.com/image.png",
                ogScrapeUrl = "https://example.com",
                originalHeight = 600,
                originalWidth = 800,
                pretext = "Pretext",
                text = "Description text",
                thumbUrl = "https://example.com/thumb.png",
                title = "Page Title",
                titleLink = "https://example.com/page",
                type = "article",
                actions = actions,
                fields = fields,
            )

        // When
        val attachment = response.toAttachment()

        // Then
        val expected =
            Attachment(
                assetUrl = "https://example.com/asset.mp4",
                authorIcon = "https://example.com/icon.png",
                authorLink = "https://example.com/author",
                authorName = "Author",
                color = "#FF0000",
                fallback = "Fallback text",
                footer = "Footer text",
                footerIcon = "https://example.com/footer-icon.png",
                imageUrl = "https://example.com/image.png",
                ogScrapeUrl = "https://example.com",
                originalHeight = 600,
                originalWidth = 800,
                pretext = "Pretext",
                text = "Description text",
                thumbUrl = "https://example.com/thumb.png",
                title = "Page Title",
                titleLink = "https://example.com/page",
                type = "article",
                actions = actions,
                fields = fields,
            )
        assertEquals(expected, attachment)
    }
}
