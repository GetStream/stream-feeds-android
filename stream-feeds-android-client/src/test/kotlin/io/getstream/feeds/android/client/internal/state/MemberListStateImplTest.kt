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

import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.model.FeedMemberData
import io.getstream.feeds.android.client.api.model.ModelUpdates
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.model.PaginationResult
import io.getstream.feeds.android.client.api.model.QueryConfiguration
import io.getstream.feeds.android.client.api.state.query.MembersFilterField
import io.getstream.feeds.android.client.api.state.query.MembersQuery
import io.getstream.feeds.android.client.api.state.query.MembersSort
import io.getstream.feeds.android.client.internal.test.TestData.feedMemberData
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
        val paginationResult =
            PaginationResult(
                models = members,
                pagination = PaginationData(next = "next-cursor", previous = null),
            )
        val queryConfig =
            QueryConfiguration<MembersFilterField, MembersSort>(
                filter = null,
                sort = MembersSort.Default,
            )

        memberListState.onQueryMoreMembers(paginationResult, queryConfig)

        assertEquals(members, memberListState.members.value)
        assertEquals("next-cursor", memberListState.pagination?.next)
        assertEquals(queryConfig, memberListState.queryConfig)
    }

    @Test
    fun `on memberUpdated, then update specific member`() = runTest {
        val initialMembers = listOf(feedMemberData(), feedMemberData("user-2"))
        val paginationResult =
            PaginationResult(
                models = initialMembers,
                pagination = PaginationData(next = "next-cursor", previous = null),
            )
        val queryConfig =
            QueryConfiguration<MembersFilterField, MembersSort>(
                filter = null,
                sort = MembersSort.Default,
            )
        memberListState.onQueryMoreMembers(paginationResult, queryConfig)

        val updatedMember = feedMemberData("user-1", role = "admin")
        memberListState.onMemberUpdated(updatedMember)

        val updatedMembers = memberListState.members.value
        assertEquals(updatedMember, updatedMembers.find { it.id == updatedMember.id })
        assertEquals(initialMembers[1], updatedMembers.find { it.id == initialMembers[1].id })
    }

    @Test
    fun `on memberRemoved, then remove specific member`() = runTest {
        val initialMembers = listOf(feedMemberData(), feedMemberData("user-2"))
        val paginationResult =
            PaginationResult(
                models = initialMembers,
                pagination = PaginationData(next = "next-cursor", previous = null),
            )
        val queryConfig =
            QueryConfiguration<MembersFilterField, MembersSort>(
                filter = null,
                sort = MembersSort.Default,
            )
        memberListState.onQueryMoreMembers(paginationResult, queryConfig)

        memberListState.onMemberRemoved(initialMembers[0].id)

        val remainingMembers = memberListState.members.value
        assertEquals(listOf(initialMembers[1]), remainingMembers)
    }

    @Test
    fun `on membersUpdated, then apply multiple updates`() = runTest {
        val initialMembers = listOf(feedMemberData(), feedMemberData("user-2"))
        val paginationResult =
            PaginationResult(
                models = initialMembers,
                pagination = PaginationData(next = "next-cursor", previous = null),
            )
        val queryConfig =
            QueryConfiguration<MembersFilterField, MembersSort>(
                filter = null,
                sort = MembersSort.Default,
            )
        memberListState.onQueryMoreMembers(paginationResult, queryConfig)

        val updatedMember = feedMemberData("user-1", role = "admin")
        val updates =
            ModelUpdates(
                added = emptyList(),
                updated = listOf(updatedMember),
                removedIds = listOf(initialMembers[1].id),
            )
        memberListState.onMembersUpdated(updates)

        val finalMembers = memberListState.members.value
        assertEquals(listOf(updatedMember), finalMembers)
    }

    @Test
    fun `on memberAdded, then add member`() = runTest {
        val initialMembers = listOf(feedMemberData(), feedMemberData("user-2"))
        val paginationResult =
            PaginationResult(models = initialMembers, pagination = PaginationData())
        val queryConfig =
            QueryConfiguration<MembersFilterField, MembersSort>(
                filter = null,
                sort = MembersSort.Default,
            )
        memberListState.onQueryMoreMembers(paginationResult, queryConfig)

        val newMember = feedMemberData("user-3")
        memberListState.onMemberAdded(newMember)

        assertEquals(initialMembers + newMember, memberListState.members.value)
    }

    @Test
    fun `on memberAdded with existing id, then update member`() = runTest {
        val initialMembers = listOf(feedMemberData(), feedMemberData("user-2"))
        val paginationResult =
            PaginationResult(
                models = initialMembers,
                pagination = PaginationData(next = "next-cursor", previous = null),
            )
        val queryConfig =
            QueryConfiguration<MembersFilterField, MembersSort>(
                filter = null,
                sort = MembersSort.Default,
            )
        memberListState.onQueryMoreMembers(paginationResult, queryConfig)

        val updatedMember = feedMemberData("user-1", role = "admin")
        memberListState.onMemberAdded(updatedMember)

        val updatedMembers = memberListState.members.value
        assertEquals(2, updatedMembers.size)
        assertEquals(updatedMember, updatedMembers.find { it.id == updatedMember.id })
        assertEquals(initialMembers[1], updatedMembers.find { it.id == initialMembers[1].id })
    }

    @Test
    fun `on clear, then remove all members`() = runTest {
        val initialMembers = listOf(feedMemberData(), feedMemberData("user-2"))
        val paginationResult =
            PaginationResult(
                models = initialMembers,
                pagination = PaginationData(next = "next-cursor", previous = null),
            )
        val queryConfig =
            QueryConfiguration<MembersFilterField, MembersSort>(
                filter = null,
                sort = MembersSort.Default,
            )
        memberListState.onQueryMoreMembers(paginationResult, queryConfig)

        memberListState.clear()

        assertEquals(emptyList<FeedMemberData>(), memberListState.members.value)
    }
}
