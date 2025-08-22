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

import io.getstream.feeds.android.client.api.Moderation
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

/**
 * Default implementation of [Moderation] interface. Uses [ModerationRepository] to perform
 * moderation operations.
 *
 * @param moderationRepository The repository used for moderation operations.
 */
internal class ModerationImpl(private val moderationRepository: ModerationRepository) : Moderation {

    override suspend fun ban(banRequest: BanRequest): Result<BanResponse> {
        return moderationRepository.ban(banRequest)
    }

    override suspend fun mute(muteRequest: MuteRequest): Result<MuteResponse> {
        return moderationRepository.mute(muteRequest)
    }

    override suspend fun blockUser(
        blockUserRequest: BlockUsersRequest
    ): Result<BlockUsersResponse> {
        return moderationRepository.blockUser(blockUserRequest)
    }

    override suspend fun unblockUser(
        unblockUserRequest: UnblockUsersRequest
    ): Result<UnblockUsersResponse> {
        return moderationRepository.unblockUser(unblockUserRequest)
    }

    override suspend fun getBlockedUsers(): Result<GetBlockedUsersResponse> {
        return moderationRepository.getBlockedUsers()
    }

    override suspend fun flag(flagRequest: FlagRequest): Result<FlagResponse> {
        return moderationRepository.flag(flagRequest)
    }

    override suspend fun submitAction(
        submitActionRequest: SubmitActionRequest
    ): Result<SubmitActionResponse> {
        return moderationRepository.submitAction(submitActionRequest)
    }

    override suspend fun queryReviewQueue(
        queryReviewQueueRequest: QueryReviewQueueRequest
    ): Result<QueryReviewQueueResponse> {
        return moderationRepository.queryReviewQueue(queryReviewQueueRequest)
    }

    override suspend fun upsertConfig(
        upsertConfigRequest: UpsertConfigRequest
    ): Result<UpsertConfigResponse> {
        return moderationRepository.upsertConfig(upsertConfigRequest)
    }

    override suspend fun deleteConfig(
        key: String,
        team: String?,
    ): Result<DeleteModerationConfigResponse> {
        return moderationRepository.deleteConfig(key, team)
    }

    override suspend fun getConfig(key: String, team: String?): Result<GetConfigResponse> {
        return moderationRepository.getConfig(key, team)
    }

    override suspend fun queryModerationConfigs(
        queryModerationConfigsRequest: QueryModerationConfigsRequest
    ): Result<QueryModerationConfigsResponse> {
        return moderationRepository.queryModerationConfigs(queryModerationConfigsRequest)
    }
}
