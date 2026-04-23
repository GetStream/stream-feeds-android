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
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

internal class UserListImplErrorTest {
    private val commonRepository: CommonRepository = mockk()
    private val query = UsersQuery(limit = 10)
    private val userList = UserListImpl(query = query, commonRepository = commonRepository)

    @Test
    fun `on get failure, error is propagated and state remains empty`() = runTest {
        val error = RuntimeException("network error")
        coEvery { commonRepository.queryUsers(query) } returns Result.failure(error)

        val result = userList.get()

        assertTrue(result.isFailure)
        assertEquals(error, result.exceptionOrNull())
        assertEquals(emptyList<UserData>(), userList.state.users.value)
    }

    @Test
    fun `on queryMoreUsers failure, error is propagated and state unchanged`() = runTest {
        val users = listOf(userData("user-1"), userData("user-2"))
        coEvery { commonRepository.queryUsers(query) } returns Result.success(users)
        userList.get()

        val error = RuntimeException("timeout")
        coEvery { commonRepository.queryUsers(any()) } returns Result.failure(error)

        val result = userList.queryMoreUsers()

        assertTrue(result.isFailure)
        assertEquals(users, userList.state.users.value)
        assertTrue(userList.state.canLoadMore)
    }

    @Test
    fun `on initial offset, currentOffset starts from query offset`() = runTest {
        val queryWithOffset = UsersQuery(limit = 10, offset = 50)
        val list = UserListImpl(query = queryWithOffset, commonRepository = commonRepository)

        assertEquals(50, list.state.currentOffset)
    }
}
