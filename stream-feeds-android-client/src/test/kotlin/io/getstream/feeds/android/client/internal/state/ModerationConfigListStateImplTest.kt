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
import io.getstream.feeds.android.client.api.model.PaginationResult
import io.getstream.feeds.android.client.api.state.query.ModerationConfigSort
import io.getstream.feeds.android.client.api.state.query.ModerationConfigsQuery
import io.getstream.feeds.android.client.api.state.query.ModerationConfigsQueryConfig
import io.getstream.feeds.android.client.internal.test.TestData.moderationConfigData
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

internal class ModerationConfigListStateImplTest {
    private val query = ModerationConfigsQuery(limit = 10)
    private val moderationConfigListState = ModerationConfigListStateImpl(query)

    @Test
    fun `on initial state, then return empty configs and null pagination`() = runTest {
        assertEquals(emptyList<ModerationConfigData>(), moderationConfigListState.configs.value)
        assertNull(moderationConfigListState.pagination)
    }

    @Test
    fun `on loadMoreConfigs, then update configs and pagination`() = runTest {
        val configs = listOf(moderationConfigData(), moderationConfigData("config-2", "team-2"))
        val paginationResult =
            PaginationResult(
                models = configs,
                pagination = PaginationData(next = "next-cursor", previous = null),
            )
        val queryConfig =
            ModerationConfigsQueryConfig(filter = null, sort = ModerationConfigSort.Default)

        moderationConfigListState.onLoadMoreConfigs(paginationResult, queryConfig)

        assertEquals(configs, moderationConfigListState.configs.value)
        assertEquals("next-cursor", moderationConfigListState.pagination?.next)
        assertEquals(queryConfig, moderationConfigListState.queryConfig)
    }
}
