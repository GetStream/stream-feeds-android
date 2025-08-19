package io.getstream.feeds.android.client.internal.repository

import io.getstream.feeds.android.client.api.model.ModerationConfigData
import io.getstream.feeds.android.client.api.model.PaginationResult
import io.getstream.feeds.android.client.api.state.query.ModerationConfigsQuery
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
import io.getstream.feeds.android.core.generated.models.QueryReviewQueueRequest
import io.getstream.feeds.android.core.generated.models.QueryReviewQueueResponse
import io.getstream.feeds.android.core.generated.models.SubmitActionRequest
import io.getstream.feeds.android.core.generated.models.SubmitActionResponse
import io.getstream.feeds.android.core.generated.models.UnblockUsersRequest
import io.getstream.feeds.android.core.generated.models.UnblockUsersResponse
import io.getstream.feeds.android.core.generated.models.QueryModerationConfigsRequest
import io.getstream.feeds.android.core.generated.models.QueryModerationConfigsResponse
import io.getstream.feeds.android.core.generated.models.UpsertConfigRequest
import io.getstream.feeds.android.core.generated.models.UpsertConfigResponse

/**
 * Repository interface for managing moderation-related operations.
 */
internal interface ModerationRepository {

    /**
     * Queries moderation configurations based on the provided query configuration.
     *
     * This method fetches moderation configurations from the server using the specified query parameters,
     * including filtering, sorting, and pagination options.
     *
     * @param query The query configuration for fetching configurations.
     * @return A [Result] containing a [PaginationResult] of [ModerationConfigData] if the query is
     * successful, or an error if the query fails.
     */
    suspend fun queryModerationConfigs(
        query: ModerationConfigsQuery,
    ): Result<PaginationResult<ModerationConfigData>>

    /**
     * Bans a user from the platform.
     *
     * @param banRequest The ban request containing details about the user to ban and ban
     * parameters.
     * @return A [Result] containing a [BanResponse] if the ban is successful,
     * or an error if the ban fails.
     */
    suspend fun ban(banRequest: BanRequest): Result<BanResponse>

    /**
     * Mutes one or more users.
     *
     * @param muteRequest The mute request containing target user IDs and optional timeout.
     * @return A [Result] containing a [MuteResponse] if the mute operation is successful,
     * or an error if the operation fails.
     */
    suspend fun mute(muteRequest: MuteRequest): Result<MuteResponse>

    /**
     * Blocks a user.
     *
     * @param blockUserRequest The request containing the user ID to block.
     * @return A [Result] containing a [BlockUsersResponse] if the blocking is successful,
     * or an error if the operation fails.
     */
    suspend fun blockUser(blockUserRequest: BlockUsersRequest): Result<BlockUsersResponse>

    /**
     * Unblocks a user.
     *
     * @param unblockUserRequest The request containing the user ID to unblock.
     * @return A [Result] containing a [UnblockUsersResponse] if the unblocking is successful,
     * or an error if the operation fails.
     */
    suspend fun unblockUser(unblockUserRequest: UnblockUsersRequest): Result<UnblockUsersResponse>

    /**
     * Gets the list of blocked users.
     *
     * @return A [Result] containing a [GetBlockedUsersResponse] with the list of blocked users if
     * the operation is successful, or an error if it fails.
     */
    suspend fun getBlockedUsers(): Result<GetBlockedUsersResponse>

    /**
     * Flags content for moderation review.
     *
     * @param flagRequest The flag request containing details about the content to flag.
     * @return A [Result] containing a [FlagResponse] if the flagging is successful,
     * or an error if the operation fails.
     */
    suspend fun flag(flagRequest: FlagRequest): Result<FlagResponse>

    /**
     * Submits a moderation action.
     *
     * @param submitActionRequest The action request containing details about the moderation action.
     * @return A [Result] containing a [SubmitActionResponse] if the action submission is
     * successful, or an error if the submission fails.
     */
    suspend fun submitAction(submitActionRequest: SubmitActionRequest): Result<SubmitActionResponse>

    /**
     * Queries the moderation review queue.
     *
     * @param queryReviewQueueRequest The query request containing filters and pagination
     * parameters.
     * @return A [Result] containing a [QueryReviewQueueResponse] containing the review queue items,
     * or an error if the query fails.
     */
    suspend fun queryReviewQueue(
        queryReviewQueueRequest: QueryReviewQueueRequest,
    ): Result<QueryReviewQueueResponse>

    /**
     * Upserts a moderation configuration.
     *
     * @param upsertConfigRequest The configuration request containing the config to upsert.
     * @return A [Result] containing an [UpsertConfigResponse] if the upsert is successful,
     * or an error if the upsert fails.
     */
    suspend fun upsertConfig(upsertConfigRequest: UpsertConfigRequest): Result<UpsertConfigResponse>

    /**
     * Deletes a moderation configuration.
     *
     * @param key The key of the configuration to delete.
     * @param team Optional team identifier.
     * @return A [Result] containing a [DeleteModerationConfigResponse] if the deletion is
     * successful, or an error if the deletion fails.
     */
    suspend fun deleteConfig(
        key: String,
        team: String? = null,
    ): Result<DeleteModerationConfigResponse>

    /**
     * Gets a moderation configuration.
     *
     * @param key The key of the configuration to retrieve.
     * @param team Optional team identifier.
     * @return A [Result] containing a [GetConfigResponse] with the configuration data if the
     * retrieval is successful, or an error if the retrieval fails.
     */
    suspend fun getConfig(
        key: String,
        team: String? = null,
    ): Result<GetConfigResponse>

    /**
     * Queries moderation configurations.
     *
     * @param queryModerationConfigsRequest The query request containing filters and pagination
     * parameters.
     * @return A [Result] containing a [QueryModerationConfigsResponse] with the list of
     * moderation configurations, or an error if the query fails.
     */
    suspend fun queryModerationConfigs(
        queryModerationConfigsRequest: QueryModerationConfigsRequest,
    ): Result<QueryModerationConfigsResponse>
}