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

import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.internal.test.TestData.activityData
import io.getstream.feeds.android.client.internal.test.TestData.bookmarkData
import io.getstream.feeds.android.client.internal.test.TestData.feedData
import io.getstream.feeds.android.client.internal.test.TestData.feedsReactionData
import org.junit.Assert.assertEquals
import org.junit.Test

internal class ActivityOperationsTest {

    @Test
    fun `update preserves data missing from WS events`() {
        val original =
            activityData(
                id = "a1",
                ownBookmarks = listOf(bookmarkData(activityId = "a1")),
                ownReactions = listOf(feedsReactionData(type = "like", userId = "me")),
                friendReactions = listOf(feedsReactionData(type = "love")),
                friendReactionCount = 2,
                currentFeed = feedData(),
            )
        val wsUpdate =
            activityData(id = "a1", feeds = listOf(FeedId("user", "u1"), FeedId("timeline", "u2")))

        val result = original.update(wsUpdate)

        assertEquals(original.ownBookmarks, result.ownBookmarks)
        assertEquals(original.ownReactions, result.ownReactions)
        assertEquals(original.friendReactions, result.friendReactions)
        assertEquals(original.friendReactionCount, result.friendReactionCount)
        assertEquals(original.currentFeed, result.currentFeed)
    }
}
