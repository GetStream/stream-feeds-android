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

package io.getstream.feeds.android.client.internal.state

import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.model.FeedMemberData
import io.getstream.feeds.android.client.api.model.ModelUpdates
import io.getstream.feeds.android.client.api.state.query.MembersQuery
import io.getstream.feeds.android.client.api.state.query.MembersSort
import io.getstream.feeds.android.client.internal.state.query.MembersQueryConfig
import io.getstream.feeds.android.client.internal.test.TestData.defaultPaginationResult
import io.getstream.feeds.android.client.internal.test.TestData.feedMemberData
import java.util.Date
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

internal class MemberListStateImplTest {
    private val query = MembersQuery(fid = FeedId("user:test"), limit = 10)
    private val memberListState = MemberListStateImpl(query)

    @Test
    fun `on initial state, then return empty members and null pagination`() = runTest {
        assertEquals(emptyList<FeedMemberData>(), memberListState.members.value)
        assertNull(memberListState.pagination)
    }

    @Test
    fun `on queryMoreMembers, then update members and pagination`() = runTest {
        val members = listOf(feedMemberData(), feedMemberData("user-2"))
        val paginationResult = defaultPaginationResult(members)

        memberListState.onQueryMoreMembers(paginationResult, queryConfig)

        assertEquals(members, memberListState.members.value)
        assertEquals("next-cursor", memberListState.pagination?.next)
        assertEquals(queryConfig, memberListState.queryConfig)
    }

    @Test
    fun `on memberUpdated, then update and reposition member`() = runTest {
        // Initial members already sorted by createdAt desc
        val initialMembers =
            listOf(
                feedMemberData("user-2", createdAt = Date(2000)),
                feedMemberData("user-1", createdAt = Date(1000)),
            )
        setupInitialState(initialMembers)

        val updatedMember = feedMemberData("user-1", role = "admin", createdAt = Date(3000))
        memberListState.onMemberUpserted(updatedMember)

        // Member should be repositioned according to new sort criteria
        val expectedMembers = listOf(updatedMember, initialMembers[0])
        assertEquals(expectedMembers, memberListState.members.value)
    }

    @Test
    fun `on memberRemoved, then remove specific member`() = runTest {
        val initialMembers = listOf(feedMemberData(), feedMemberData("user-2"))
        setupInitialState(initialMembers)

        memberListState.onMemberRemoved(initialMembers[0].id)

        val remainingMembers = memberListState.members.value
        assertEquals(listOf(initialMembers[1]), remainingMembers)
    }

    @Test
    fun `on membersUpdated, then apply add, update, and remove operations`() = runTest {
        val initialMembers =
            listOf(
                feedMemberData("user-3", createdAt = Date(3000)),
                feedMemberData("user-2", createdAt = Date(2000)),
                feedMemberData("user-1", createdAt = Date(1000)),
            )
        setupInitialState(initialMembers)

        val updatedMember = feedMemberData("user-1", role = "admin", createdAt = Date(5000))
        val newMember = feedMemberData("user-4", createdAt = Date(4000))
        val updates =
            ModelUpdates(
                added = listOf(newMember),
                updated = listOf(updatedMember),
                removedIds = setOf("user-2"),
            )
        memberListState.onMembersUpdated(updates)

        // Members should be sorted by createdAt in descending order
        val expectedMembers = listOf(updatedMember, newMember, initialMembers[0])
        assertEquals(expectedMembers, memberListState.members.value)
    }

    @Test
    fun `on onMemberUpserted, then add member in sorted position`() = runTest {
        // Initial members already sorted by createdAt desc
        val initialMembers =
            listOf(
                feedMemberData("user-3", createdAt = Date(3000)),
                feedMemberData("user-1", createdAt = Date(1000)),
            )
        setupInitialState(initialMembers)

        val newMember = feedMemberData("user-2", createdAt = Date(2000))
        memberListState.onMemberUpserted(newMember)

        // Member should be inserted in correct sorted position
        val expectedMembers = listOf(initialMembers[0], newMember, initialMembers[1])
        assertEquals(expectedMembers, memberListState.members.value)
    }

    @Test
    fun `on onMemberUpserted with existing id, then update and reposition member`() = runTest {
        // Initial members already sorted by createdAt desc
        val initialMembers =
            listOf(
                feedMemberData("user-2", createdAt = Date(2000)),
                feedMemberData("user-1", createdAt = Date(1000)),
            )
        setupInitialState(initialMembers)

        // Add existing user-1 with newer createdAt that should move it to the front
        val updatedMember = feedMemberData("user-1", role = "admin", createdAt = Date(3000))
        memberListState.onMemberUpserted(updatedMember)

        // Member should be updated and repositioned according to new sort criteria (3000, 2000)
        val expectedMembers = listOf(updatedMember, initialMembers[0])
        assertEquals(expectedMembers, memberListState.members.value)
    }

    @Test
    fun `on clear, then remove all members`() = runTest {
        setupInitialState(listOf(feedMemberData(), feedMemberData("user-2")))

        memberListState.clear()

        assertEquals(emptyList<FeedMemberData>(), memberListState.members.value)
    }

    private fun setupInitialState(members: List<FeedMemberData>) {
        memberListState.onQueryMoreMembers(defaultPaginationResult(members), queryConfig)
    }

    companion object {
        private val queryConfig = MembersQueryConfig(filter = null, sort = MembersSort.Default)
    }
}
