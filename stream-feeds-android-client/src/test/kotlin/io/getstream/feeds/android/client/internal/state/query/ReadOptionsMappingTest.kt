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

package io.getstream.feeds.android.client.internal.state.query

import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.state.query.ActivitiesQuery
import io.getstream.feeds.android.client.api.state.query.BookmarksQuery
import io.getstream.feeds.android.client.api.state.query.FeedQuery
import io.getstream.feeds.android.client.api.state.query.FeedsQuery
import io.getstream.feeds.android.network.models.EnrichmentOptions
import io.getstream.feeds.android.network.models.FriendReactionsOptions
import io.getstream.feeds.android.network.models.Location
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

internal class ReadOptionsMappingTest {

    @Test
    fun `feed query maps the read options`() {
        val friendReactionsOptions = FriendReactionsOptions(enabled = true, limit = 3)
        val viewerLocation = Location(lat = 1.0, lng = 2.0)
        val query =
            FeedQuery(
                fid = FeedId("user:1"),
                friendReactionsOptions = friendReactionsOptions,
                overwriteInterestWeights = true,
                viewerLocation = viewerLocation,
            )

        val request = query.toRequest()

        assertEquals(friendReactionsOptions, request.friendReactionsOptions)
        assertEquals(true, request.overwriteInterestWeights)
        assertEquals(viewerLocation, request.viewerLocation)
    }

    @Test
    fun `feed query maps the around activity id`() {
        val request = FeedQuery(fid = FeedId("user:1"), activityIdAround = "activity-1").toRequest()

        assertEquals("activity-1", request.idAround)
        assertNull(FeedQuery(fid = FeedId("user:1")).toRequest().idAround)
        assertEquals(
            "activity-1",
            FeedQuery(group = "user", id = "1", activityIdAround = "activity-1").activityIdAround,
        )
    }

    @Test
    fun `feed query sends no read options by default`() {
        val request = FeedQuery(fid = FeedId("user:1")).toRequest()

        assertNull(request.friendReactionsOptions)
        assertNull(request.overwriteInterestWeights)
        assertNull(request.viewerLocation)
    }

    @Test
    fun `feed query convenience constructor passes the read options`() {
        val enrichmentOptions = EnrichmentOptions(includeReasons = true)
        val friendReactionsOptions = FriendReactionsOptions(enabled = true)
        val viewerLocation = Location(lat = 1.0, lng = 2.0)

        val query =
            FeedQuery(
                group = "user",
                id = "1",
                friendReactionsOptions = friendReactionsOptions,
                overwriteInterestWeights = true,
                viewerLocation = viewerLocation,
                enrichmentOptions = enrichmentOptions,
            )

        val expected =
            FeedQuery(
                fid = FeedId("user:1"),
                enrichmentOptions = enrichmentOptions,
                friendReactionsOptions = friendReactionsOptions,
                overwriteInterestWeights = true,
                viewerLocation = viewerLocation,
            )
        assertEquals(expected, query)
    }

    @Test
    fun `list queries map enrichOwnFields`() {
        assertEquals(true, ActivitiesQuery(enrichOwnFields = true).toRequest().enrichOwnFields)
        assertEquals(true, BookmarksQuery(enrichOwnFields = true).toRequest().enrichOwnFields)
        assertEquals(true, FeedsQuery(enrichOwnFields = true).toRequest().enrichOwnFields)
    }

    @Test
    fun `list queries send no enrichOwnFields by default`() {
        assertNull(ActivitiesQuery().toRequest().enrichOwnFields)
        assertNull(BookmarksQuery().toRequest().enrichOwnFields)
        assertNull(FeedsQuery().toRequest().enrichOwnFields)
    }
}
