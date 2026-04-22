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
import io.getstream.feeds.android.client.internal.repository.CommonRepository
import io.getstream.feeds.android.client.internal.test.TestData.userData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

internal class UserListImplTest {
    private val commonRepository: CommonRepository = mockk()
    private val query = UsersQuery(limit = 10)
    private val userList = UserListImpl(query = query, commonRepository = commonRepository)

    @Test
    fun `on get, then return users and update state`() = runTest {
        val users = listOf(userData("user-1"), userData("user-2"))
        coEvery { commonRepository.queryUsers(query) } returns Result.success(users)

        val result = userList.get()

        assertEquals(users, result.getOrNull())
        assertEquals(users, userList.state.users.value)
        coVerify { commonRepository.queryUsers(query) }
    }

    @Test
    fun `on queryMoreUsers when canLoadMore, then query with advanced offset`() = runTest {
        setupInitialState(pageSize = 10)

        val moreUsers = listOf(userData("user-11"), userData("user-12"))
        coEvery { commonRepository.queryUsers(any()) } returns Result.success(moreUsers)

        val result = userList.queryMoreUsers()

        assertEquals(moreUsers, result.getOrNull())
        coVerify { commonRepository.queryUsers(match { it.offset == 10 && it.limit == 10 }) }
    }

    @Test
    fun `on queryMoreUsers when not canLoadMore, then return empty list`() = runTest {
        setupInitialState(pageSize = 0)

        val result = userList.queryMoreUsers()

        assertEquals(emptyList<UserData>(), result.getOrNull())
        coVerify(exactly = 1) { commonRepository.queryUsers(any()) }
    }

    @Test
    fun `on queryMoreUsers with custom limit, then use custom limit`() = runTest {
        setupInitialState(pageSize = 10)

        val moreUsers = listOf(userData("user-11"))
        coEvery { commonRepository.queryUsers(any()) } returns Result.success(moreUsers)

        val result = userList.queryMoreUsers(limit = 5)

        assertEquals(moreUsers, result.getOrNull())
        coVerify { commonRepository.queryUsers(match { it.limit == 5 && it.offset == 10 }) }
    }

    private suspend fun setupInitialState(pageSize: Int) {
        val initialUsers = List(pageSize) { userData("user-$it") }
        coEvery { commonRepository.queryUsers(query) } returns Result.success(initialUsers)
        userList.get()
    }
}
