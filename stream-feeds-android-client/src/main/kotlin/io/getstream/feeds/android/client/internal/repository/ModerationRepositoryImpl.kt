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

import io.getstream.android.core.result.runSafely
import io.getstream.feeds.android.client.api.model.ModerationConfigData
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.state.query.ModerationConfigsQuery
import io.getstream.feeds.android.client.internal.model.PaginationResult
import io.getstream.feeds.android.client.internal.model.toModel
import io.getstream.feeds.android.client.internal.state.query.toRequest
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

/**
 * Default implementation of [ModerationRepository]. Uses [FeedsApi] to perform network operations
 * related to moderation.
 *
 * @param api The [FeedsApi] instance used for making API calls.
 */
internal class ModerationRepositoryImpl(private val api: FeedsApi) : ModerationRepository {

    override suspend fun queryModerationConfigs(
        query: ModerationConfigsQuery
    ): Result<PaginationResult<ModerationConfigData>> = runSafely {
        val response = api.queryModerationConfigs(query.toRequest())
        PaginationResult(
            models = response.configs.map { it.toModel() },
            pagination = PaginationData(next = response.next, previous = response.prev),
        )
    }

    override suspend fun ban(banRequest: BanRequest): Result<BanResponse> = runSafely {
        api.ban(banRequest)
    }

    override suspend fun mute(muteRequest: MuteRequest): Result<MuteResponse> = runSafely {
        api.mute(muteRequest)
    }

    override suspend fun blockUser(
        blockUserRequest: BlockUsersRequest
    ): Result<BlockUsersResponse> = runSafely { api.blockUsers(blockUserRequest) }

    override suspend fun unblockUser(
        unblockUserRequest: UnblockUsersRequest
    ): Result<UnblockUsersResponse> = runSafely { api.unblockUsers(unblockUserRequest) }

    override suspend fun getBlockedUsers(): Result<GetBlockedUsersResponse> = runSafely {
        api.getBlockedUsers()
    }

    override suspend fun flag(flagRequest: FlagRequest): Result<FlagResponse> = runSafely {
        api.flag(flagRequest)
    }

    override suspend fun submitAction(
        submitActionRequest: SubmitActionRequest
    ): Result<SubmitActionResponse> = runSafely { api.submitAction(submitActionRequest) }

    override suspend fun queryReviewQueue(
        queryReviewQueueRequest: QueryReviewQueueRequest
    ): Result<QueryReviewQueueResponse> = runSafely {
        api.queryReviewQueue(queryReviewQueueRequest)
    }

    override suspend fun upsertConfig(
        upsertConfigRequest: UpsertConfigRequest
    ): Result<UpsertConfigResponse> = runSafely { api.upsertConfig(upsertConfigRequest) }

    override suspend fun deleteConfig(
        key: String,
        team: String?,
    ): Result<DeleteModerationConfigResponse> = runSafely {
        api.deleteConfig(key = key, team = team)
    }

    override suspend fun getConfig(key: String, team: String?): Result<GetConfigResponse> =
        runSafely {
            api.getConfig(key = key, team = team)
        }

    override suspend fun queryModerationConfigs(
        queryModerationConfigsRequest: QueryModerationConfigsRequest
    ): Result<QueryModerationConfigsResponse> = runSafely {
        api.queryModerationConfigs(queryModerationConfigsRequest)
    }
}
