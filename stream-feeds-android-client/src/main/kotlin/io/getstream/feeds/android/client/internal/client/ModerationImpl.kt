package io.getstream.feeds.android.client.internal.client

import io.getstream.feeds.android.client.api.Moderation
import io.getstream.feeds.android.client.internal.repository.ModerationRepository
import io.getstream.feeds.android.core.generated.models.BanRequest
import io.getstream.feeds.android.core.generated.models.BanResponse
import io.getstream.feeds.android.core.generated.models.BlockUsersRequest
import io.getstream.feeds.android.core.generated.models.BlockUsersResponse
import io.getstream.feeds.android.core.generated.models.DeleteModerationConfigResponse
import io.getstream.feeds.android.core.generated.models.FlagRequest
import io.getstream.feeds.android.core.generated.models.FlagResponse
import io.getstream.feeds.android.core.generated.models.GetBlockedUsersResponse
import io.getstream.feeds.android.core.generated.models.GetConfigResponse
import io.getstream.feeds.android.core.generated.models.MuteRequest
import io.getstream.feeds.android.core.generated.models.MuteResponse
import io.getstream.feeds.android.core.generated.models.QueryModerationConfigsRequest
import io.getstream.feeds.android.core.generated.models.QueryModerationConfigsResponse
import io.getstream.feeds.android.core.generated.models.QueryReviewQueueRequest
import io.getstream.feeds.android.core.generated.models.QueryReviewQueueResponse
import io.getstream.feeds.android.core.generated.models.SubmitActionRequest
import io.getstream.feeds.android.core.generated.models.SubmitActionResponse
import io.getstream.feeds.android.core.generated.models.UnblockUsersRequest
import io.getstream.feeds.android.core.generated.models.UnblockUsersResponse
import io.getstream.feeds.android.core.generated.models.UpsertConfigRequest
import io.getstream.feeds.android.core.generated.models.UpsertConfigResponse

/**
 * Default implementation of [Moderation] interface.
 * Uses [ModerationRepository] to perform moderation operations.
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
        blockUserRequest: BlockUsersRequest,
    ): Result<BlockUsersResponse> {
        return moderationRepository.blockUser(blockUserRequest)
    }

    override suspend fun unblockUser(
        unblockUserRequest: UnblockUsersRequest,
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
        submitActionRequest: SubmitActionRequest,
    ): Result<SubmitActionResponse> {
        return moderationRepository.submitAction(submitActionRequest)
    }

    override suspend fun queryReviewQueue(
        queryReviewQueueRequest: QueryReviewQueueRequest,
    ): Result<QueryReviewQueueResponse> {
        return moderationRepository.queryReviewQueue(queryReviewQueueRequest)
    }

    override suspend fun upsertConfig(
        upsertConfigRequest: UpsertConfigRequest,
    ): Result<UpsertConfigResponse> {
        return moderationRepository.upsertConfig(upsertConfigRequest)
    }

    override suspend fun deleteConfig(
        key: String,
        team: String?
    ): Result<DeleteModerationConfigResponse> {
        return moderationRepository.deleteConfig(key, team)
    }

    override suspend fun getConfig(
        key: String,
        team: String?
    ): Result<GetConfigResponse> {
        return moderationRepository.getConfig(key, team)
    }

    override suspend fun queryModerationConfigs(
        queryModerationConfigsRequest: QueryModerationConfigsRequest,
    ): Result<QueryModerationConfigsResponse> {
        return moderationRepository.queryModerationConfigs(queryModerationConfigsRequest)
    }
}
