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

import io.getstream.feeds.android.client.api.model.ModerationConfigData
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.state.query.ModerationConfigsQuery
import io.getstream.feeds.android.client.internal.model.PaginationResult
import io.getstream.feeds.android.client.internal.repository.ModerationRepository
import io.getstream.feeds.android.client.internal.test.TestData.moderationConfigData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

internal class ModerationConfigListImplTest {
    private val moderationRepository: ModerationRepository = mockk()
    private val query = ModerationConfigsQuery(limit = 10)

    private val moderationConfigList =
        ModerationConfigListImpl(query = query, moderationRepository = moderationRepository)

    @Test
    fun `on get, then return moderation configs and update state`() = runTest {
        val configs =
            listOf(moderationConfigData(), moderationConfigData(key = "config-2", team = "team-2"))
        val paginationResult =
            PaginationResult(
                models = configs,
                pagination = PaginationData(next = "next-cursor", previous = null),
            )
        coEvery { moderationRepository.queryModerationConfigs(query) } returns
            Result.success(paginationResult)

        val result = moderationConfigList.get()

        assertEquals(configs, result.getOrNull())
        coVerify { moderationRepository.queryModerationConfigs(query) }
    }

    @Test
    fun `on queryMoreConfigs with next cursor, then query with next cursor`() = runTest {
        setupInitialState()

        val moreConfigs =
            listOf(
                moderationConfigData(key = "config-3", team = "team-3"),
                moderationConfigData(key = "config-4", team = "team-4"),
            )
        val morePaginationResult =
            PaginationResult(
                models = moreConfigs,
                pagination = PaginationData(next = "next-cursor-2", previous = "next-cursor"),
            )
        coEvery {
            moderationRepository.queryModerationConfigs(any<ModerationConfigsQuery>())
        } returns Result.success(morePaginationResult)

        val result = moderationConfigList.queryMoreConfigs()

        assertEquals(moreConfigs, result.getOrNull())
        coVerify { moderationRepository.queryModerationConfigs(any<ModerationConfigsQuery>()) }
    }

    @Test
    fun `on queryMoreConfigs with no next cursor, then return empty list`() = runTest {
        setupInitialState(nextCursor = null)

        val result = moderationConfigList.queryMoreConfigs()

        assertEquals(emptyList<ModerationConfigData>(), result.getOrNull())
        coVerify(exactly = 1) {
            moderationRepository.queryModerationConfigs(any<ModerationConfigsQuery>())
        }
    }

    @Test
    fun `on queryMoreConfigs with custom limit, then use custom limit`() = runTest {
        setupInitialState()

        val customLimit = 5
        val moreConfigs = listOf(moderationConfigData(key = "config-3", team = "team-3"))
        val morePaginationResult =
            PaginationResult(
                models = moreConfigs,
                pagination = PaginationData(next = null, previous = "next-cursor"),
            )
        coEvery {
            moderationRepository.queryModerationConfigs(any<ModerationConfigsQuery>())
        } returns Result.success(morePaginationResult)

        val result = moderationConfigList.queryMoreConfigs(customLimit)

        assertEquals(moreConfigs, result.getOrNull())
        coVerify { moderationRepository.queryModerationConfigs(any<ModerationConfigsQuery>()) }
    }

    private suspend fun setupInitialState(nextCursor: String? = "next-cursor") {
        val initialConfigs = listOf(moderationConfigData())
        val initialPaginationResult =
            PaginationResult(
                models = initialConfigs,
                pagination = PaginationData(next = nextCursor, previous = null),
            )
        coEvery { moderationRepository.queryModerationConfigs(query) } returns
            Result.success(initialPaginationResult)
        moderationConfigList.get()
    }
}
