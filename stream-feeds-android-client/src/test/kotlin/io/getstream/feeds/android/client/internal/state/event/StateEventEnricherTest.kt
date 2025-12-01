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

package io.getstream.feeds.android.client.internal.state.event

import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.internal.repository.FeedOwnDataRepository
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.ActivityAdded
import io.getstream.feeds.android.client.internal.test.TestData.activityData
import io.getstream.feeds.android.client.internal.test.TestData.feedData
import io.getstream.feeds.android.network.models.FeedOwnCapability
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test

internal class StateEventEnricherTest {
    private val feedOwnDataRepository: FeedOwnDataRepository = mockk(relaxed = true)

    private val enricher = StateEventEnricher(feedOwnDataRepository)

    @Test
    fun `on enrich ActivityAdded with feed and capabilities exist, enrich feed`() {
        val feedId = FeedId("user:1")
        val feed = feedData(id = "1", groupId = "user", ownCapabilities = emptySet())
        val event = ActivityAdded(FidScope.of(feedId), activityData(currentFeed = feed))
        val capabilities = setOf(FeedOwnCapability.ReadFeed, FeedOwnCapability.AddActivity)
        val expectedFeed = feedData(id = "1", groupId = "user", ownCapabilities = capabilities)
        val expectedEvent = event.copy(activity = activityData(currentFeed = expectedFeed))

        every { feedOwnDataRepository.getOrRequest(feedId) } returns capabilities

        val result = enricher.enrich(event)

        assertEquals(expectedEvent, result)
    }

    @Test
    fun `on enrich ActivityAdded with feed but no capabilities, return unchanged event`() {
        val feedId = FeedId("user:1")
        val feed = feedData(id = "1", groupId = "user", ownCapabilities = emptySet())
        val activity = activityData().copy(currentFeed = feed)
        val event = ActivityAdded(FidScope.of(feedId), activity)

        every { feedOwnDataRepository.getOrRequest(feedId) } returns null

        val result = enricher.enrich(event)

        assertEquals(event, result)
    }

    @Test
    fun `on enrich ActivityAdded when activity no currentFeed, return unchanged event`() {
        val feedId = FeedId("user:1")
        val activity = activityData().copy(currentFeed = null)
        val event = ActivityAdded(FidScope.of(feedId), activity)

        val result = enricher.enrich(event)

        assertEquals(event, result)
    }

    @Test
    fun `on enrich different event, return it unchanged`() {
        val event = StateUpdateEvent.PollDeleted("poll-id")

        val result = enricher.enrich(event)

        assertEquals(event, result)
    }
}
