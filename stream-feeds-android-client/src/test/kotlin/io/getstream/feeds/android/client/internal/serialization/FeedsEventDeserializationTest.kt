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

package io.getstream.feeds.android.client.internal.serialization

import io.getstream.feeds.android.network.infrastructure.Serializer
import io.getstream.feeds.android.network.models.FeedDeletedEvent
import io.getstream.feeds.android.network.models.FeedVisibility
import io.getstream.feeds.android.network.models.UnsupportedWSEvent
import java.util.Date
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Parses events with the real [Serializer], so a serializer that no longer knows how to read
 * WebSocket events fails here.
 */
internal class FeedsEventDeserializationTest {

    private val parser = FeedsEventParser(FeedsMoshiJsonParser(Serializer.moshi))

    @Test
    fun `deserialize a known event, return the typed event`() {
        val json =
            """
            {
              "type": "feeds.feed.deleted",
              "created_at": 1700000000000000000,
              "fid": "user:john",
              "feed_visibility": "public"
            }
            """
                .trimIndent()

        val event = parser.deserialize(json).getOrThrow()

        val expected =
            FeedDeletedEvent(
                createdAt = Date(1_700_000_000_000),
                fid = "user:john",
                feedVisibility = FeedVisibility.Public,
            )
        assertEquals(expected, event)
    }

    @Test
    fun `deserialize an unknown event type, return an unsupported event`() {
        val event = parser.deserialize("""{"type": "something.new"}""").getOrThrow()

        assertTrue(event is UnsupportedWSEvent)
        assertEquals("something.new", event.getWSEventType())
    }
}
