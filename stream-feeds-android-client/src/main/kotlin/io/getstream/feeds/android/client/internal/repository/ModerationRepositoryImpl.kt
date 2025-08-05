package io.getstream.feeds.android.client.internal.repository

import io.getstream.feeds.android.client.api.model.ModerationConfigData
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.model.PaginationResult
import io.getstream.feeds.android.client.api.model.toModel
import io.getstream.feeds.android.client.api.state.query.ModerationConfigsQuery
import io.getstream.feeds.android.client.api.state.query.toRequest
import io.getstream.feeds.android.core.generated.apis.ApiService
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
 * Default implementation of [ModerationRepository].
 * Uses [ApiService] to perform network operations related to moderation.
 *
 * @param api The [ApiService] instance used for making API calls.
 */
internal class ModerationRepositoryImpl(private val api: ApiService) : ModerationRepository {

    override suspend fun queryModerationConfigs(
        query: ModerationConfigsQuery,
    ): Result<PaginationResult<ModerationConfigData>> = runCatching {
        val response = api.queryModerationConfigs(query.toRequest())
        PaginationResult(
            models = response.configs.map { it.toModel() },
            pagination = PaginationData(next = response.next, previous = response.prev)
        )
    }

    override suspend fun ban(banRequest: BanRequest): Result<BanResponse> = runCatching {
        api.ban(banRequest)
    }

    override suspend fun mute(muteRequest: MuteRequest): Result<MuteResponse> = runCatching {
        api.mute(muteRequest)
    }

    override suspend fun blockUser(
        blockUserRequest: BlockUsersRequest,
    ): Result<BlockUsersResponse> = runCatching {
        api.blockUsers(blockUserRequest)
    }

    override suspend fun unblockUser(
        unblockUserRequest: UnblockUsersRequest,
    ): Result<UnblockUsersResponse> = runCatching {
        api.unblockUsers(unblockUserRequest)
    }

    override suspend fun getBlockedUsers(): Result<GetBlockedUsersResponse> = runCatching {
        api.getBlockedUsers()
    }

    override suspend fun flag(flagRequest: FlagRequest): Result<FlagResponse> = runCatching {
        api.flag(flagRequest)
    }

    override suspend fun submitAction(
        submitActionRequest: SubmitActionRequest,
    ): Result<SubmitActionResponse> = runCatching {
        api.submitAction(submitActionRequest)
    }

    override suspend fun queryReviewQueue(
        queryReviewQueueRequest: QueryReviewQueueRequest,
    ): Result<QueryReviewQueueResponse> = runCatching {
        api.queryReviewQueue(queryReviewQueueRequest)
    }

    override suspend fun upsertConfig(
        upsertConfigRequest: UpsertConfigRequest,
    ): Result<UpsertConfigResponse> = runCatching {
        api.upsertConfig(upsertConfigRequest)
    }

    override suspend fun deleteConfig(
        key: String,
        team: String?
    ): Result<DeleteModerationConfigResponse> = runCatching {
        api.deleteConfig(key = key, team = team)
    }

    override suspend fun getConfig(
        key: String,
        team: String?
    ): Result<GetConfigResponse> = runCatching {
        api.getConfig(key = key, team = team)
    }

    override suspend fun queryModerationConfigs(
        queryModerationConfigsRequest: QueryModerationConfigsRequest,
    ): Result<QueryModerationConfigsResponse> = runCatching {
        api.queryModerationConfigs(queryModerationConfigsRequest)
    }
}