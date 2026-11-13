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
import io.getstream.feeds.android.client.api.model.ShareData
import io.getstream.feeds.android.client.internal.test.TestData.activityData
import io.getstream.feeds.android.client.internal.test.TestData.activityResponse
import io.getstream.feeds.android.client.internal.test.TestData.bookmarkData
import io.getstream.feeds.android.client.internal.test.TestData.feedData
import io.getstream.feeds.android.client.internal.test.TestData.feedsReactionData
import io.getstream.feeds.android.client.internal.test.TestData.userResponse
import io.getstream.feeds.android.network.models.ShareResponse
import java.util.Date
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

    @Test
    fun `toModel maps position, reasons, shares and top-level comment count`() {
        val user = userResponse()
        val response =
            activityResponse()
                .copy(
                    position = 3,
                    reasons = listOf("following"),
                    topLevelCommentCount = 2,
                    latestShares = listOf(ShareResponse("a2", Date(1), user)),
                )

        val result = response.toModel()

        assertEquals(3, result.position)
        assertEquals(listOf("following"), result.reasons)
        assertEquals(2, result.topLevelCommentCount)
        assertEquals(listOf(ShareData("a2", Date(1), user.toModel())), result.latestShares)
    }

    @Test
    fun `toModel maps missing reasons and shares to empty lists`() {
        val result = activityResponse().toModel()

        assertEquals(null, result.position)
        assertEquals(emptyList<String>(), result.reasons)
        assertEquals(null, result.topLevelCommentCount)
        assertEquals(emptyList<ShareData>(), result.latestShares)
    }

    @Test
    fun `update keeps read-only fields the update does not carry`() {
        val original =
            activityData(id = "a1")
                .copy(position = 3, reasons = listOf("following"), topLevelCommentCount = 2)

        val result = original.update(activityData(id = "a1"))

        assertEquals(3, result.position)
        assertEquals(listOf("following"), result.reasons)
        assertEquals(2, result.topLevelCommentCount)
    }

    @Test
    fun `update takes read-only fields the update carries`() {
        val original =
            activityData(id = "a1")
                .copy(position = 3, reasons = listOf("following"), topLevelCommentCount = 2)
        val updated =
            activityData(id = "a1")
                .copy(position = 1, reasons = listOf("popular"), topLevelCommentCount = 5)

        val result = original.update(updated)

        assertEquals(1, result.position)
        assertEquals(listOf("popular"), result.reasons)
        assertEquals(5, result.topLevelCommentCount)
    }
}
