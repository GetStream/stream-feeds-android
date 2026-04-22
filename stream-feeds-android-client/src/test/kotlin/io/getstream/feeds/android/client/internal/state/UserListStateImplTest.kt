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

import io.getstream.feeds.android.client.api.model.UserData
import io.getstream.feeds.android.client.api.state.query.UsersQuery
import io.getstream.feeds.android.client.internal.test.TestData.userData
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

internal class UserListStateImplTest {
    private val query = UsersQuery(limit = 10)
    private val state = UserListStateImpl(query)

    @Test
    fun `on initial state, then return empty users and canLoadMore true`() = runTest {
        assertEquals(emptyList<UserData>(), state.users.value)
        assertTrue(state.canLoadMore)
    }

    @Test
    fun `on queryUsers, then replace users and set offset`() = runTest {
        val users = List(10) { userData("user-$it") }

        state.onQueryUsers(users)

        assertEquals(users, state.users.value)
        assertTrue(state.canLoadMore)
        assertEquals(10, state.currentOffset)
    }

    @Test
    fun `on queryUsers called twice, then replace users and reset offset`() = runTest {
        val firstPage = List(10) { userData("user-$it") }
        val secondPage = List(5) { userData("user-$it") }

        state.onQueryUsers(firstPage)
        state.onQueryUsers(secondPage)

        assertEquals(secondPage, state.users.value)
        assertTrue(state.canLoadMore)
        assertEquals(5, state.currentOffset)
    }

    @Test
    fun `on queryUsers with empty result, then canLoadMore false`() = runTest {
        state.onQueryUsers(emptyList())

        assertFalse(state.canLoadMore)
        assertEquals(0, state.currentOffset)
    }

    @Test
    fun `on queryMoreUsers with non-empty result, then update users and canLoadMore true`() =
        runTest {
            val users = List(10) { userData("user-$it") }

            state.onQueryMoreUsers(users)

            assertEquals(users, state.users.value)
            assertTrue(state.canLoadMore)
            assertEquals(10, state.currentOffset)
        }

    @Test
    fun `on queryMoreUsers with empty result, then canLoadMore false`() = runTest {
        state.onQueryMoreUsers(emptyList())

        assertFalse(state.canLoadMore)
        assertEquals(0, state.currentOffset)
    }

    @Test
    fun `on queryMoreUsers called twice, then merge users and advance offset`() = runTest {
        val firstPage = List(10) { userData("user-$it") }
        val secondPage = List(5) { userData("user-${10 + it}") }

        state.onQueryMoreUsers(firstPage)
        state.onQueryMoreUsers(secondPage)

        assertEquals(15, state.users.value.size)
        assertTrue(state.canLoadMore)
        assertEquals(15, state.currentOffset)
    }
}
