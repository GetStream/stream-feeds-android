/*
 * Copyright (c) 2014-2025 Stream.io Inc. All rights reserved.
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
import io.getstream.feeds.android.network.models.FeedOwnCapability
import org.junit.Assert.assertEquals
import org.junit.Test

internal class FeedOperationsTest {

    @Test
    fun `on update with different ownCapabilities, then preserve original ownCapabilities`() {
        val originalCapabilities =
            listOf(FeedOwnCapability.AddActivity, FeedOwnCapability.DeleteFeed)
        val originalFeed = feedData().copy(ownCapabilities = originalCapabilities)

        val newCapabilities = listOf(FeedOwnCapability.UpdateFeed)
        val updatedFeed = feedData().copy(ownCapabilities = newCapabilities, name = "Updated Name")

        val result = originalFeed.update(updatedFeed)

        val expected = updatedFeed.copy(ownCapabilities = originalCapabilities)
        assertEquals(expected, result)
    }

    @Test
    fun `on update, then apply all other field changes`() {
        val originalFeed =
            feedData(id = "feed-1", name = "Original Name", description = "Original Description")
                .copy(
                    ownCapabilities = listOf(FeedOwnCapability.AddActivity),
                    followerCount = 10,
                    followingCount = 20,
                    memberCount = 5,
                    pinCount = 2,
                )

        val updatedFeed =
            feedData(id = "feed-1", name = "Updated Name", description = "Updated Description")
                .copy(
                    ownCapabilities = emptyList(),
                    followerCount = 15,
                    followingCount = 25,
                    memberCount = 8,
                    pinCount = 3,
                )

        val result = originalFeed.update(updatedFeed)

        val expected = updatedFeed.copy(ownCapabilities = listOf(FeedOwnCapability.AddActivity))
        assertEquals(expected, result)
    }

    @Test
    fun `on update with no ownCapabilities in original, then use empty list`() {
        val originalFeed = feedData().copy(ownCapabilities = emptyList())
        val updatedFeed =
            feedData()
                .copy(ownCapabilities = listOf(FeedOwnCapability.AddActivity), name = "Updated")

        val result = originalFeed.update(updatedFeed)

        val expected = updatedFeed.copy(ownCapabilities = emptyList())
        assertEquals(expected, result)
    }
}
