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
package io.getstream.feeds.android.client.internal.repository

import io.getstream.feeds.android.client.api.model.ModerationConfigData
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.model.PaginationResult
import io.getstream.feeds.android.client.api.state.query.ModerationConfigsQuery
import io.getstream.feeds.android.client.internal.repository.RepositoryTestUtils.testDelegation
import io.getstream.feeds.android.network.apis.FeedsApi
import io.getstream.feeds.android.network.models.BanRequest
import io.getstream.feeds.android.network.models.BanResponse
import io.getstream.feeds.android.network.models.BlockUsersRequest
import io.getstream.feeds.android.network.models.BlockUsersResponse
import io.getstream.feeds.android.network.models.DeleteModerationConfigResponse
import io.getstream.feeds.android.network.models.FlagRequest
import io.getstream.feeds.android.network.models.FlagResponse
import io.getstream.feeds.android.network.models.GetBlockedUsersResponse
import io.getstream.feeds.android.network.models.GetConfigResponse
import io.getstream.feeds.android.network.models.MuteRequest
import io.getstream.feeds.android.network.models.MuteResponse
import io.getstream.feeds.android.network.models.QueryModerationConfigsRequest
import io.getstream.feeds.android.network.models.QueryModerationConfigsResponse
import io.getstream.feeds.android.network.models.QueryReviewQueueRequest
import io.getstream.feeds.android.network.models.QueryReviewQueueResponse
import io.getstream.feeds.android.network.models.SubmitActionRequest
import io.getstream.feeds.android.network.models.SubmitActionResponse
import io.getstream.feeds.android.network.models.UnblockUsersRequest
import io.getstream.feeds.android.network.models.UnblockUsersResponse
import io.getstream.feeds.android.network.models.UpsertConfigRequest
import io.getstream.feeds.android.network.models.UpsertConfigResponse
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

internal class ModerationRepositoryImplTest {
    private val api: FeedsApi = mockk(relaxed = true)
    private val repository: ModerationRepositoryImpl = ModerationRepositoryImpl(api)

    @Test
    fun `on queryModerationConfigs, then delegate to api and map response`() = runTest {
        val query = ModerationConfigsQuery(
            limit = 10,
            next = "next-cursor",
            previous = "prev-cursor"
        )
        val queryResponse = QueryModerationConfigsResponse(
            duration = "100ms",
            configs = emptyList(),
            next = "next-cursor",
            prev = "prev-cursor"
        )
        val expectedPaginationResult = PaginationResult<ModerationConfigData>(
            models = emptyList(),
            pagination = PaginationData(next = "next-cursor", previous = "prev-cursor")
        )

        testDelegation(
            apiFunction = { api.queryModerationConfigs(any()) },
            repositoryCall = { repository.queryModerationConfigs(query) },
            apiResult = queryResponse,
            repositoryResult = expectedPaginationResult
        )
    }

    @Test
    fun `on ban, then delegate to api`() = runTest {
        val banRequest = BanRequest(targetUserId = "user-123", timeout = 3600)
        val banResponse = BanResponse(duration = "100ms")

        testDelegation(
            apiFunction = { api.ban(banRequest) },
            repositoryCall = { repository.ban(banRequest) },
            apiResult = banResponse
        )
    }

    @Test
    fun `on mute, then delegate to api`() = runTest {
        val muteRequest = MuteRequest(targetIds = listOf("user-123", "user-456"), timeout = 1800)
        val muteResponse = MuteResponse(duration = "50ms")

        testDelegation(
            apiFunction = { api.mute(muteRequest) },
            repositoryCall = { repository.mute(muteRequest) },
            apiResult = muteResponse
        )
    }

    @Test
    fun `on blockUser, then delegate to api`() = runTest {
        val blockRequest = BlockUsersRequest(blockedUserId = "user-123")
        val blockResponse = BlockUsersResponse(
            blockedByUserId = "admin-1",
            blockedUserId = "user-123",
            createdAt = java.util.Date(1000),
            duration = "30ms"
        )

        testDelegation(
            apiFunction = { api.blockUsers(blockRequest) },
            repositoryCall = { repository.blockUser(blockRequest) },
            apiResult = blockResponse
        )
    }

    @Test
    fun `on unblockUser, then delegate to api`() = runTest {
        val unblockRequest = UnblockUsersRequest(blockedUserId = "user-123")
        val unblockResponse = UnblockUsersResponse(duration = "25ms")

        testDelegation(
            apiFunction = { api.unblockUsers(unblockRequest) },
            repositoryCall = { repository.unblockUser(unblockRequest) },
            apiResult = unblockResponse
        )
    }

    @Test
    fun `on getBlockedUsers, then delegate to api`() = runTest {
        val blockedUsersResponse = GetBlockedUsersResponse(duration = "20ms")

        testDelegation(
            apiFunction = { api.getBlockedUsers() },
            repositoryCall = { repository.getBlockedUsers() },
            apiResult = blockedUsersResponse
        )
    }

    @Test
    fun `on flag, then delegate to api`() = runTest {
        val flagRequest = FlagRequest(entityId = "message-123", entityType = "message")
        val flagResponse = FlagResponse(duration = "15ms", itemId = "flag-123")

        testDelegation(
            apiFunction = { api.flag(flagRequest) },
            repositoryCall = { repository.flag(flagRequest) },
            apiResult = flagResponse
        )
    }

    @Test
    fun `on submitAction, then delegate to api`() = runTest {
        val submitActionRequest = SubmitActionRequest(
            actionType = SubmitActionRequest.ActionType.Ban,
            itemId = "item-123"
        )
        val submitActionResponse = SubmitActionResponse(duration = "10ms")

        testDelegation(
            apiFunction = { api.submitAction(submitActionRequest) },
            repositoryCall = { repository.submitAction(submitActionRequest) },
            apiResult = submitActionResponse
        )
    }

    @Test
    fun `on queryReviewQueue, then delegate to api`() = runTest {
        val queryRequest = QueryReviewQueueRequest()
        val queryResponse = QueryReviewQueueResponse(duration = "5ms")

        testDelegation(
            apiFunction = { api.queryReviewQueue(queryRequest) },
            repositoryCall = { repository.queryReviewQueue(queryRequest) },
            apiResult = queryResponse
        )
    }

    @Test
    fun `on upsertConfig, then delegate to api`() = runTest {
        val upsertRequest = UpsertConfigRequest(key = "config-key")
        val upsertResponse = UpsertConfigResponse(duration = "8ms")

        testDelegation(
            apiFunction = { api.upsertConfig(upsertRequest) },
            repositoryCall = { repository.upsertConfig(upsertRequest) },
            apiResult = upsertResponse
        )
    }

    @Test
    fun `on deleteConfig, then delegate to api`() = runTest {
        val key = "config-key"
        val team = "team-123"
        val deleteResponse = DeleteModerationConfigResponse(duration = "3ms")

        testDelegation(
            apiFunction = { api.deleteConfig(key = key, team = team) },
            repositoryCall = { repository.deleteConfig(key, team) },
            apiResult = deleteResponse
        )
    }

    @Test
    fun `on getConfig, then delegate to api`() = runTest {
        val key = "config-key"
        val team = "team-123"
        val getResponse = GetConfigResponse(duration = "2ms")

        testDelegation(
            apiFunction = { api.getConfig(key = key, team = team) },
            repositoryCall = { repository.getConfig(key, team) },
            apiResult = getResponse
        )
    }

    @Test
    fun `on queryModerationConfigs, then delegate to api`() = runTest {
        val queryRequest = QueryModerationConfigsRequest()
        val queryResponse = QueryModerationConfigsResponse(duration = "1ms")

        testDelegation(
            apiFunction = { api.queryModerationConfigs(queryRequest) },
            repositoryCall = { repository.queryModerationConfigs(queryRequest) },
            apiResult = queryResponse
        )
    }
}
