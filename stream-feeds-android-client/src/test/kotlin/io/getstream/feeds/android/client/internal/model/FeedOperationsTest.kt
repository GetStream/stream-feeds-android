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

import io.getstream.feeds.android.client.internal.test.TestData.feedData
import io.getstream.feeds.android.client.internal.test.TestData.feedMemberData
import io.getstream.feeds.android.client.internal.test.TestData.followData
import io.getstream.feeds.android.network.models.FeedOwnCapability
import org.junit.Assert.assertEquals
import org.junit.Test

internal class FeedOperationsTest {
    @Test
    fun `on update with different own values, then preserve original`() {
        val originalCapabilities =
            setOf(FeedOwnCapability.AddActivity, FeedOwnCapability.DeleteFeed)
        val originalFollows = listOf(followData(sourceFid = "user:u1", targetFid = "user:u2"))
        val originalMembership = feedMemberData(userId = "u1", role = "admin")
        val originalFeed =
            feedData(
                id = "feed-1",
                name = "Original Name",
                description = "Original Description",
                ownCapabilities = originalCapabilities,
                ownFollows = originalFollows,
                ownMembership = originalMembership,
            )

        val newCapabilities = setOf(FeedOwnCapability.UpdateFeed)
        val newFollows = listOf(followData(sourceFid = "user:u3", targetFid = "user:u4"))
        val newMembership = feedMemberData(userId = "u2", role = "member")
        val updatedFeed =
            feedData(
                id = "feed-1",
                name = "Updated Name",
                description = "Updated Description",
                ownCapabilities = newCapabilities,
                ownFollows = newFollows,
                ownMembership = newMembership,
            )

        val result = originalFeed.update(updatedFeed)

        val expected =
            updatedFeed.copy(
                ownCapabilities = originalCapabilities,
                ownFollows = originalFollows,
                ownMembership = originalMembership,
            )
        assertEquals(expected, result)
    }
}
