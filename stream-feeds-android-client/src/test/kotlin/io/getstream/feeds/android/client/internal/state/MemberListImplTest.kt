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

package io.getstream.feeds.android.client.internal.state

import io.getstream.android.core.api.subscribe.StreamSubscriptionManager
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.model.FeedMemberData
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.state.query.MembersQuery
import io.getstream.feeds.android.client.internal.model.PaginationResult
import io.getstream.feeds.android.client.internal.repository.FeedsRepository
import io.getstream.feeds.android.client.internal.subscribe.StateUpdateEventListener
import io.getstream.feeds.android.client.internal.test.TestData.feedMemberData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

internal class MemberListImplTest {
    private val feedsRepository: FeedsRepository = mockk()
    private val subscriptionManager: StreamSubscriptionManager<StateUpdateEventListener> =
        mockk(relaxed = true)
    private val query = MembersQuery(fid = FeedId("user", "test"), limit = 10)

    private val memberList =
        MemberListImpl(
            query = query,
            feedsRepository = feedsRepository,
            subscriptionManager = subscriptionManager,
        )

    @Test
    fun `on get, then return members and update state`() = runTest {
        val members = listOf(feedMemberData(), feedMemberData())
        val paginationResult = createPaginationResult(members, next = "next-cursor")
        coEvery { feedsRepository.queryFeedMembers(any(), any(), any()) } returns
            Result.success(paginationResult)

        val result = memberList.get()

        assertEquals(members, result.getOrNull())
        coVerify { feedsRepository.queryFeedMembers(any(), any(), any()) }
    }

    @Test
    fun `on queryMoreMembers with next cursor, then query with next cursor`() = runTest {
        setupInitialState()

        val moreMembers = listOf(feedMemberData(), feedMemberData())
        val morePaginationResult =
            createPaginationResult(
                members = moreMembers,
                next = "next-cursor-2",
                previous = "next-cursor",
            )
        coEvery { feedsRepository.queryFeedMembers(any(), any(), any()) } returns
            Result.success(morePaginationResult)

        val result = memberList.queryMoreMembers()

        assertEquals(moreMembers, result.getOrNull())
        coVerify { feedsRepository.queryFeedMembers(any(), any(), any()) }
    }

    @Test
    fun `on queryMoreMembers with no next cursor, then return empty list`() = runTest {
        setupInitialState(nextCursor = null)

        val result = memberList.queryMoreMembers()

        assertEquals(emptyList<FeedMemberData>(), result.getOrNull())
        coVerify(exactly = 1) { feedsRepository.queryFeedMembers(any(), any(), any()) }
    }

    @Test
    fun `on queryMoreMembers with custom limit, then use custom limit`() = runTest {
        setupInitialState()

        val customLimit = 5
        val moreMembers = listOf(feedMemberData())
        val morePaginationResult =
            createPaginationResult(members = moreMembers, previous = "next-cursor")
        coEvery { feedsRepository.queryFeedMembers(any(), any(), any()) } returns
            Result.success(morePaginationResult)

        val result = memberList.queryMoreMembers(customLimit)

        assertEquals(moreMembers, result.getOrNull())
        coVerify { feedsRepository.queryFeedMembers(any(), any(), any()) }
    }

    private suspend fun setupInitialState(nextCursor: String? = "next-cursor") {
        val initialMembers = listOf(feedMemberData())
        val initialPaginationResult =
            PaginationResult(
                models = initialMembers,
                pagination = PaginationData(next = nextCursor, previous = null),
            )
        coEvery { feedsRepository.queryFeedMembers(any(), any(), any()) } returns
            Result.success(initialPaginationResult)
        memberList.get()
    }

    private fun createPaginationResult(
        members: List<FeedMemberData>,
        next: String? = null,
        previous: String? = null,
    ) =
        PaginationResult(
            models = members,
            pagination = PaginationData(next = next, previous = previous),
        )
}
