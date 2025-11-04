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

package io.getstream.feeds.android.client.internal.client

import io.getstream.feeds.android.client.internal.repository.ModerationRepository
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
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

internal class ModerationImplTest {
    private val moderationRepository: ModerationRepository = mockk(relaxed = true)
    private val moderation: ModerationImpl = ModerationImpl(moderationRepository)

    @Test
    fun `ban when given request, then delegates to repository`() = runTest {
        val banRequest =
            BanRequest(targetUserId = "user-123", reason = "Violation of terms", timeout = 3600)
        val banResponse = BanResponse(duration = "100ms")
        coEvery { moderationRepository.ban(banRequest) } returns Result.success(banResponse)

        val result = moderation.ban(banRequest)

        assertEquals(banResponse, result.getOrNull())
    }

    @Test
    fun `ban when repository fails, then returns failure`() = runTest {
        val banRequest =
            BanRequest(targetUserId = "user-123", reason = "Violation of terms", timeout = 3600)
        val exception = RuntimeException("Network error")
        coEvery { moderationRepository.ban(banRequest) } returns Result.failure(exception)

        val result = moderation.ban(banRequest)

        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `mute when given request, then delegates to repository`() = runTest {
        val muteRequest = MuteRequest(targetIds = listOf("user-123", "user-456"), timeout = 1800)
        val muteResponse = MuteResponse("duration")
        coEvery { moderationRepository.mute(muteRequest) } returns Result.success(muteResponse)

        val result = moderation.mute(muteRequest)

        assertEquals(muteResponse, result.getOrNull())
    }

    @Test
    fun `mute when repository fails, then returns failure`() = runTest {
        val muteRequest = MuteRequest(targetIds = listOf("user-123"), timeout = 1800)
        val exception = RuntimeException("Network error")
        coEvery { moderationRepository.mute(muteRequest) } returns Result.failure(exception)

        val result = moderation.mute(muteRequest)

        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `blockUser when given request, then delegates to repository`() = runTest {
        val blockRequest = BlockUsersRequest(blockedUserId = "user-123")
        val blockResponse =
            BlockUsersResponse(
                blockedByUserId = "admin-1",
                blockedUserId = "user-123",
                createdAt = java.util.Date(1000),
                duration = "30ms",
            )
        coEvery { moderationRepository.blockUser(blockRequest) } returns
            Result.success(blockResponse)

        val result = moderation.blockUser(blockRequest)

        assertEquals(blockResponse, result.getOrNull())
    }

    @Test
    fun `blockUser when repository fails, then returns failure`() = runTest {
        val blockRequest = BlockUsersRequest(blockedUserId = "user-123")
        val exception = RuntimeException("Network error")
        coEvery { moderationRepository.blockUser(blockRequest) } returns Result.failure(exception)

        val result = moderation.blockUser(blockRequest)

        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `unblockUser when given request, then delegates to repository`() = runTest {
        val unblockRequest = UnblockUsersRequest(blockedUserId = "user-123")
        val unblockResponse = UnblockUsersResponse(duration = "25ms")
        coEvery { moderationRepository.unblockUser(unblockRequest) } returns
            Result.success(unblockResponse)

        val result = moderation.unblockUser(unblockRequest)

        assertEquals(unblockResponse, result.getOrNull())
    }

    @Test
    fun `unblockUser when repository fails, then returns failure`() = runTest {
        val unblockRequest = UnblockUsersRequest(blockedUserId = "user-123")
        val exception = RuntimeException("Network error")
        coEvery { moderationRepository.unblockUser(unblockRequest) } returns
            Result.failure(exception)

        val result = moderation.unblockUser(unblockRequest)

        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `getBlockedUsers when called, then delegates to repository`() = runTest {
        val blockedUsersResponse = GetBlockedUsersResponse(duration = "20ms")
        coEvery { moderationRepository.getBlockedUsers() } returns
            Result.success(blockedUsersResponse)

        val result = moderation.getBlockedUsers()

        assertEquals(blockedUsersResponse, result.getOrNull())
    }

    @Test
    fun `getBlockedUsers when repository fails, then returns failure`() = runTest {
        val exception = RuntimeException("Network error")
        coEvery { moderationRepository.getBlockedUsers() } returns Result.failure(exception)

        val result = moderation.getBlockedUsers()

        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `flag when given request, then delegates to repository`() = runTest {
        val flagRequest = FlagRequest(entityId = "message-123", entityType = "message")
        val flagResponse = FlagResponse(duration = "15ms", itemId = "flag-123")
        coEvery { moderationRepository.flag(flagRequest) } returns Result.success(flagResponse)

        val result = moderation.flag(flagRequest)

        assertEquals(flagResponse, result.getOrNull())
    }

    @Test
    fun `flag when repository fails, then returns failure`() = runTest {
        val flagRequest = FlagRequest(entityId = "message-123", entityType = "message")
        val exception = RuntimeException("Network error")
        coEvery { moderationRepository.flag(flagRequest) } returns Result.failure(exception)

        val result = moderation.flag(flagRequest)

        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `submitAction when given request, then delegates to repository`() = runTest {
        val submitActionRequest =
            SubmitActionRequest(
                actionType = SubmitActionRequest.ActionType.Ban,
                itemId = "item-123",
            )
        val submitActionResponse = SubmitActionResponse("duration")
        coEvery { moderationRepository.submitAction(submitActionRequest) } returns
            Result.success(submitActionResponse)

        val result = moderation.submitAction(submitActionRequest)

        assertEquals(submitActionResponse, result.getOrNull())
    }

    @Test
    fun `submitAction when repository fails, then returns failure`() = runTest {
        val submitActionRequest =
            SubmitActionRequest(
                actionType = SubmitActionRequest.ActionType.Ban,
                itemId = "item-123",
            )
        val exception = RuntimeException("Network error")
        coEvery { moderationRepository.submitAction(submitActionRequest) } returns
            Result.failure(exception)

        val result = moderation.submitAction(submitActionRequest)

        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `queryReviewQueue when given request, then delegates to repository`() = runTest {
        val queryRequest = QueryReviewQueueRequest()
        val queryResponse = QueryReviewQueueResponse("duration")
        coEvery { moderationRepository.queryReviewQueue(queryRequest) } returns
            Result.success(queryResponse)

        val result = moderation.queryReviewQueue(queryRequest)

        assertEquals(queryResponse, result.getOrNull())
    }

    @Test
    fun `queryReviewQueue when repository fails, then returns failure`() = runTest {
        val queryRequest = QueryReviewQueueRequest()
        val exception = RuntimeException("Network error")
        coEvery { moderationRepository.queryReviewQueue(queryRequest) } returns
            Result.failure(exception)

        val result = moderation.queryReviewQueue(queryRequest)

        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `upsertConfig when given request, then delegates to repository`() = runTest {
        val upsertRequest = UpsertConfigRequest("key")
        val upsertResponse = UpsertConfigResponse("duration")
        coEvery { moderationRepository.upsertConfig(upsertRequest) } returns
            Result.success(upsertResponse)

        val result = moderation.upsertConfig(upsertRequest)

        assertEquals(upsertResponse, result.getOrNull())
    }

    @Test
    fun `upsertConfig when repository fails, then returns failure`() = runTest {
        val upsertRequest = UpsertConfigRequest("key")
        val exception = RuntimeException("Network error")
        coEvery { moderationRepository.upsertConfig(upsertRequest) } returns
            Result.failure(exception)

        val result = moderation.upsertConfig(upsertRequest)

        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `deleteConfig when given key and team, then delegates to repository`() = runTest {
        val key = "config-key"
        val team = "team-123"
        val deleteResponse = DeleteModerationConfigResponse("duration")
        coEvery { moderationRepository.deleteConfig(key, team) } returns
            Result.success(deleteResponse)

        val result = moderation.deleteConfig(key, team)

        assertEquals(deleteResponse, result.getOrNull())
    }

    @Test
    fun `deleteConfig when repository fails, then returns failure`() = runTest {
        val key = "config-key"
        val team = "team-123"
        val exception = RuntimeException("Network error")
        coEvery { moderationRepository.deleteConfig(key, team) } returns Result.failure(exception)

        val result = moderation.deleteConfig(key, team)

        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `getConfig when given key and team, then delegates to repository`() = runTest {
        val key = "config-key"
        val team = "team-123"
        val getResponse = GetConfigResponse("duration")
        coEvery { moderationRepository.getConfig(key, team) } returns Result.success(getResponse)

        val result = moderation.getConfig(key, team)

        assertEquals(getResponse, result.getOrNull())
    }

    @Test
    fun `getConfig when repository fails, then returns failure`() = runTest {
        val key = "config-key"
        val team = "team-123"
        val exception = RuntimeException("Network error")
        coEvery { moderationRepository.getConfig(key, team) } returns Result.failure(exception)

        val result = moderation.getConfig(key, team)

        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `queryModerationConfigs when given request, then delegates to repository`() = runTest {
        val queryRequest = QueryModerationConfigsRequest()
        val queryResponse = QueryModerationConfigsResponse("duration")
        coEvery { moderationRepository.queryModerationConfigs(queryRequest) } returns
            Result.success(queryResponse)

        val result = moderation.queryModerationConfigs(queryRequest)

        assertEquals(queryResponse, result.getOrNull())
    }

    @Test
    fun `queryModerationConfigs when repository fails, then returns failure`() = runTest {
        val queryRequest = QueryModerationConfigsRequest()
        val exception = RuntimeException("Network error")
        coEvery { moderationRepository.queryModerationConfigs(queryRequest) } returns
            Result.failure(exception)

        val result = moderation.queryModerationConfigs(queryRequest)

        assertEquals(exception, result.exceptionOrNull())
    }
}
