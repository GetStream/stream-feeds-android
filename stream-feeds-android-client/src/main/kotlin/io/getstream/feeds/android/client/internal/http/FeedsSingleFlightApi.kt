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
package io.getstream.feeds.android.client.internal.http

import io.getstream.android.core.api.model.StreamTypedKey.Companion.asStreamTypedKey
import io.getstream.android.core.api.processing.StreamSingleFlightProcessor
import io.getstream.feeds.android.network.apis.FeedsApi
import io.getstream.feeds.android.network.models.AcceptFeedMemberInviteRequest
import io.getstream.feeds.android.network.models.AcceptFeedMemberInviteResponse
import io.getstream.feeds.android.network.models.AcceptFollowRequest
import io.getstream.feeds.android.network.models.AcceptFollowResponse
import io.getstream.feeds.android.network.models.ActivityFeedbackRequest
import io.getstream.feeds.android.network.models.ActivityFeedbackResponse
import io.getstream.feeds.android.network.models.AddActivityRequest
import io.getstream.feeds.android.network.models.AddActivityResponse
import io.getstream.feeds.android.network.models.AddBookmarkRequest
import io.getstream.feeds.android.network.models.AddBookmarkResponse
import io.getstream.feeds.android.network.models.AddCommentReactionRequest
import io.getstream.feeds.android.network.models.AddCommentReactionResponse
import io.getstream.feeds.android.network.models.AddCommentRequest
import io.getstream.feeds.android.network.models.AddCommentResponse
import io.getstream.feeds.android.network.models.AddCommentsBatchRequest
import io.getstream.feeds.android.network.models.AddCommentsBatchResponse
import io.getstream.feeds.android.network.models.AddReactionRequest
import io.getstream.feeds.android.network.models.AddReactionResponse
import io.getstream.feeds.android.network.models.BanRequest
import io.getstream.feeds.android.network.models.BanResponse
import io.getstream.feeds.android.network.models.BlockUsersRequest
import io.getstream.feeds.android.network.models.BlockUsersResponse
import io.getstream.feeds.android.network.models.CastPollVoteRequest
import io.getstream.feeds.android.network.models.CreateBlockListRequest
import io.getstream.feeds.android.network.models.CreateBlockListResponse
import io.getstream.feeds.android.network.models.CreateDeviceRequest
import io.getstream.feeds.android.network.models.CreateFeedsBatchRequest
import io.getstream.feeds.android.network.models.CreateFeedsBatchResponse
import io.getstream.feeds.android.network.models.CreateGuestRequest
import io.getstream.feeds.android.network.models.CreateGuestResponse
import io.getstream.feeds.android.network.models.CreatePollOptionRequest
import io.getstream.feeds.android.network.models.CreatePollRequest
import io.getstream.feeds.android.network.models.DeleteActivitiesRequest
import io.getstream.feeds.android.network.models.DeleteActivitiesResponse
import io.getstream.feeds.android.network.models.DeleteActivityReactionResponse
import io.getstream.feeds.android.network.models.DeleteActivityResponse
import io.getstream.feeds.android.network.models.DeleteBookmarkFolderResponse
import io.getstream.feeds.android.network.models.DeleteBookmarkResponse
import io.getstream.feeds.android.network.models.DeleteCommentReactionResponse
import io.getstream.feeds.android.network.models.DeleteCommentResponse
import io.getstream.feeds.android.network.models.DeleteFeedResponse
import io.getstream.feeds.android.network.models.DeleteModerationConfigResponse
import io.getstream.feeds.android.network.models.FileUploadRequest
import io.getstream.feeds.android.network.models.FileUploadResponse
import io.getstream.feeds.android.network.models.FlagRequest
import io.getstream.feeds.android.network.models.FlagResponse
import io.getstream.feeds.android.network.models.FollowBatchRequest
import io.getstream.feeds.android.network.models.FollowBatchResponse
import io.getstream.feeds.android.network.models.FollowRequest
import io.getstream.feeds.android.network.models.GetActivityResponse
import io.getstream.feeds.android.network.models.GetApplicationResponse
import io.getstream.feeds.android.network.models.GetBlockedUsersResponse
import io.getstream.feeds.android.network.models.GetCommentRepliesResponse
import io.getstream.feeds.android.network.models.GetCommentResponse
import io.getstream.feeds.android.network.models.GetCommentsResponse
import io.getstream.feeds.android.network.models.GetConfigResponse
import io.getstream.feeds.android.network.models.GetFollowSuggestionsResponse
import io.getstream.feeds.android.network.models.GetOGResponse
import io.getstream.feeds.android.network.models.GetOrCreateFeedRequest
import io.getstream.feeds.android.network.models.GetOrCreateFeedResponse
import io.getstream.feeds.android.network.models.ImageUploadRequest
import io.getstream.feeds.android.network.models.ImageUploadResponse
import io.getstream.feeds.android.network.models.ListBlockListResponse
import io.getstream.feeds.android.network.models.ListDevicesResponse
import io.getstream.feeds.android.network.models.MarkActivityRequest
import io.getstream.feeds.android.network.models.MuteRequest
import io.getstream.feeds.android.network.models.MuteResponse
import io.getstream.feeds.android.network.models.PinActivityRequest
import io.getstream.feeds.android.network.models.PinActivityResponse
import io.getstream.feeds.android.network.models.PollOptionResponse
import io.getstream.feeds.android.network.models.PollResponse
import io.getstream.feeds.android.network.models.PollVoteResponse
import io.getstream.feeds.android.network.models.PollVotesResponse
import io.getstream.feeds.android.network.models.QueryActivitiesRequest
import io.getstream.feeds.android.network.models.QueryActivitiesResponse
import io.getstream.feeds.android.network.models.QueryActivityReactionsRequest
import io.getstream.feeds.android.network.models.QueryActivityReactionsResponse
import io.getstream.feeds.android.network.models.QueryBookmarkFoldersRequest
import io.getstream.feeds.android.network.models.QueryBookmarkFoldersResponse
import io.getstream.feeds.android.network.models.QueryBookmarksRequest
import io.getstream.feeds.android.network.models.QueryBookmarksResponse
import io.getstream.feeds.android.network.models.QueryCommentReactionsRequest
import io.getstream.feeds.android.network.models.QueryCommentReactionsResponse
import io.getstream.feeds.android.network.models.QueryCommentsRequest
import io.getstream.feeds.android.network.models.QueryCommentsResponse
import io.getstream.feeds.android.network.models.QueryFeedMembersRequest
import io.getstream.feeds.android.network.models.QueryFeedMembersResponse
import io.getstream.feeds.android.network.models.QueryFeedsRequest
import io.getstream.feeds.android.network.models.QueryFeedsResponse
import io.getstream.feeds.android.network.models.QueryFollowsRequest
import io.getstream.feeds.android.network.models.QueryFollowsResponse
import io.getstream.feeds.android.network.models.QueryModerationConfigsRequest
import io.getstream.feeds.android.network.models.QueryModerationConfigsResponse
import io.getstream.feeds.android.network.models.QueryPollVotesRequest
import io.getstream.feeds.android.network.models.QueryPollsRequest
import io.getstream.feeds.android.network.models.QueryPollsResponse
import io.getstream.feeds.android.network.models.QueryReviewQueueRequest
import io.getstream.feeds.android.network.models.QueryReviewQueueResponse
import io.getstream.feeds.android.network.models.QueryUsersPayload
import io.getstream.feeds.android.network.models.QueryUsersResponse
import io.getstream.feeds.android.network.models.RejectFeedMemberInviteRequest
import io.getstream.feeds.android.network.models.RejectFeedMemberInviteResponse
import io.getstream.feeds.android.network.models.RejectFollowRequest
import io.getstream.feeds.android.network.models.RejectFollowResponse
import io.getstream.feeds.android.network.models.Response
import io.getstream.feeds.android.network.models.SharedLocationResponse
import io.getstream.feeds.android.network.models.SharedLocationsResponse
import io.getstream.feeds.android.network.models.SingleFollowResponse
import io.getstream.feeds.android.network.models.SubmitActionRequest
import io.getstream.feeds.android.network.models.SubmitActionResponse
import io.getstream.feeds.android.network.models.UnblockUsersRequest
import io.getstream.feeds.android.network.models.UnblockUsersResponse
import io.getstream.feeds.android.network.models.UnfollowResponse
import io.getstream.feeds.android.network.models.UnpinActivityResponse
import io.getstream.feeds.android.network.models.UpdateActivityPartialRequest
import io.getstream.feeds.android.network.models.UpdateActivityPartialResponse
import io.getstream.feeds.android.network.models.UpdateActivityRequest
import io.getstream.feeds.android.network.models.UpdateActivityResponse
import io.getstream.feeds.android.network.models.UpdateBlockListRequest
import io.getstream.feeds.android.network.models.UpdateBlockListResponse
import io.getstream.feeds.android.network.models.UpdateBookmarkFolderRequest
import io.getstream.feeds.android.network.models.UpdateBookmarkFolderResponse
import io.getstream.feeds.android.network.models.UpdateBookmarkRequest
import io.getstream.feeds.android.network.models.UpdateBookmarkResponse
import io.getstream.feeds.android.network.models.UpdateCommentRequest
import io.getstream.feeds.android.network.models.UpdateCommentResponse
import io.getstream.feeds.android.network.models.UpdateFeedMembersRequest
import io.getstream.feeds.android.network.models.UpdateFeedMembersResponse
import io.getstream.feeds.android.network.models.UpdateFeedRequest
import io.getstream.feeds.android.network.models.UpdateFeedResponse
import io.getstream.feeds.android.network.models.UpdateFollowRequest
import io.getstream.feeds.android.network.models.UpdateFollowResponse
import io.getstream.feeds.android.network.models.UpdateLiveLocationRequest
import io.getstream.feeds.android.network.models.UpdatePollOptionRequest
import io.getstream.feeds.android.network.models.UpdatePollPartialRequest
import io.getstream.feeds.android.network.models.UpdatePollRequest
import io.getstream.feeds.android.network.models.UpdateUsersPartialRequest
import io.getstream.feeds.android.network.models.UpdateUsersRequest
import io.getstream.feeds.android.network.models.UpdateUsersResponse
import io.getstream.feeds.android.network.models.UpsertActivitiesRequest
import io.getstream.feeds.android.network.models.UpsertActivitiesResponse
import io.getstream.feeds.android.network.models.UpsertConfigRequest
import io.getstream.feeds.android.network.models.UpsertConfigResponse
import io.getstream.feeds.android.network.models.WSAuthMessage

internal class FeedsSingleFlightApi(
    private val delegate: FeedsApi,
    private val singleFlightProcessor: StreamSingleFlightProcessor,
) : FeedsApi {

    override suspend fun getApp(): GetApplicationResponse =
        singleFlightProcessor
            .run("getApp".asStreamTypedKey<GetApplicationResponse>()) { delegate.getApp() }
            .getOrThrow()

    override suspend fun listBlockLists(team: String?): ListBlockListResponse =
        singleFlightProcessor
            .run("listBlockLists-${team}".asStreamTypedKey<ListBlockListResponse>()) {
                delegate.listBlockLists(team)
            }
            .getOrThrow()

    override suspend fun createBlockList(
        createBlockListRequest: CreateBlockListRequest
    ): CreateBlockListResponse =
        singleFlightProcessor
            .run(
                "createBlockList-${createBlockListRequest}"
                    .asStreamTypedKey<CreateBlockListResponse>()
            ) {
                delegate.createBlockList(createBlockListRequest)
            }
            .getOrThrow()

    override suspend fun deleteBlockList(name: String, team: String?): Response =
        singleFlightProcessor
            .run("deleteBlockList-${name}-${team}".asStreamTypedKey<Response>()) {
                delegate.deleteBlockList(name, team)
            }
            .getOrThrow()

    override suspend fun updateBlockList(
        name: String,
        updateBlockListRequest: UpdateBlockListRequest,
    ): UpdateBlockListResponse =
        singleFlightProcessor
            .run(
                "updateBlockList-${name}-${updateBlockListRequest}"
                    .asStreamTypedKey<UpdateBlockListResponse>()
            ) {
                delegate.updateBlockList(name, updateBlockListRequest)
            }
            .getOrThrow()

    override suspend fun updateBlockList(name: String): UpdateBlockListResponse =
        singleFlightProcessor
            .run("updateBlockList-${name}".asStreamTypedKey<UpdateBlockListResponse>()) {
                delegate.updateBlockList(name)
            }
            .getOrThrow()

    override suspend fun deleteDevice(id: String): Response =
        singleFlightProcessor
            .run("deleteDevice-${id}".asStreamTypedKey<Response>()) { delegate.deleteDevice(id) }
            .getOrThrow()

    override suspend fun listDevices(): ListDevicesResponse =
        singleFlightProcessor
            .run("listDevices".asStreamTypedKey<ListDevicesResponse>()) { delegate.listDevices() }
            .getOrThrow()

    override suspend fun createDevice(createDeviceRequest: CreateDeviceRequest): Response =
        singleFlightProcessor
            .run("createDevice-${createDeviceRequest}".asStreamTypedKey<Response>()) {
                delegate.createDevice(createDeviceRequest)
            }
            .getOrThrow()

    override suspend fun addActivity(addActivityRequest: AddActivityRequest): AddActivityResponse =
        singleFlightProcessor
            .run("addActivity-${addActivityRequest}".asStreamTypedKey<AddActivityResponse>()) {
                delegate.addActivity(addActivityRequest)
            }
            .getOrThrow()

    override suspend fun upsertActivities(
        upsertActivitiesRequest: UpsertActivitiesRequest
    ): UpsertActivitiesResponse =
        singleFlightProcessor
            .run(
                "upsertActivities-${upsertActivitiesRequest}"
                    .asStreamTypedKey<UpsertActivitiesResponse>()
            ) {
                delegate.upsertActivities(upsertActivitiesRequest)
            }
            .getOrThrow()

    override suspend fun deleteActivities(
        deleteActivitiesRequest: DeleteActivitiesRequest
    ): DeleteActivitiesResponse =
        singleFlightProcessor
            .run(
                "deleteActivities-${deleteActivitiesRequest}"
                    .asStreamTypedKey<DeleteActivitiesResponse>()
            ) {
                delegate.deleteActivities(deleteActivitiesRequest)
            }
            .getOrThrow()

    override suspend fun queryActivities(
        queryActivitiesRequest: QueryActivitiesRequest
    ): QueryActivitiesResponse =
        singleFlightProcessor
            .run(
                "queryActivities-${queryActivitiesRequest}"
                    .asStreamTypedKey<QueryActivitiesResponse>()
            ) {
                delegate.queryActivities(queryActivitiesRequest)
            }
            .getOrThrow()

    override suspend fun queryActivities(): QueryActivitiesResponse =
        singleFlightProcessor
            .run("queryActivities".asStreamTypedKey<QueryActivitiesResponse>()) {
                delegate.queryActivities()
            }
            .getOrThrow()

    override suspend fun deleteBookmark(
        activityId: String,
        folderId: String?,
    ): DeleteBookmarkResponse =
        singleFlightProcessor
            .run(
                "deleteBookmark-${activityId}-${folderId}"
                    .asStreamTypedKey<DeleteBookmarkResponse>()
            ) {
                delegate.deleteBookmark(activityId, folderId)
            }
            .getOrThrow()

    override suspend fun updateBookmark(
        activityId: String,
        updateBookmarkRequest: UpdateBookmarkRequest,
    ): UpdateBookmarkResponse =
        singleFlightProcessor
            .run(
                "updateBookmark-${activityId}-${updateBookmarkRequest}"
                    .asStreamTypedKey<UpdateBookmarkResponse>()
            ) {
                delegate.updateBookmark(activityId, updateBookmarkRequest)
            }
            .getOrThrow()

    override suspend fun updateBookmark(activityId: String): UpdateBookmarkResponse =
        singleFlightProcessor
            .run("updateBookmark-${activityId}".asStreamTypedKey<UpdateBookmarkResponse>()) {
                delegate.updateBookmark(activityId)
            }
            .getOrThrow()

    override suspend fun addBookmark(
        activityId: String,
        addBookmarkRequest: AddBookmarkRequest,
    ): AddBookmarkResponse =
        singleFlightProcessor
            .run(
                "addBookmark-${activityId}-${addBookmarkRequest}"
                    .asStreamTypedKey<AddBookmarkResponse>()
            ) {
                delegate.addBookmark(activityId, addBookmarkRequest)
            }
            .getOrThrow()

    override suspend fun addBookmark(activityId: String): AddBookmarkResponse =
        singleFlightProcessor
            .run("addBookmark-${activityId}".asStreamTypedKey<AddBookmarkResponse>()) {
                delegate.addBookmark(activityId)
            }
            .getOrThrow()

    override suspend fun activityFeedback(
        activityId: String,
        activityFeedbackRequest: ActivityFeedbackRequest,
    ): ActivityFeedbackResponse =
        singleFlightProcessor
            .run(
                "activityFeedback-${activityId}-${activityFeedbackRequest}"
                    .asStreamTypedKey<ActivityFeedbackResponse>()
            ) {
                delegate.activityFeedback(activityId, activityFeedbackRequest)
            }
            .getOrThrow()

    override suspend fun activityFeedback(activityId: String): ActivityFeedbackResponse =
        singleFlightProcessor
            .run("activityFeedback-${activityId}".asStreamTypedKey<ActivityFeedbackResponse>()) {
                delegate.activityFeedback(activityId)
            }
            .getOrThrow()

    override suspend fun castPollVote(
        activityId: String,
        pollId: String,
        castPollVoteRequest: CastPollVoteRequest,
    ): PollVoteResponse =
        singleFlightProcessor
            .run(
                "castPollVote-${activityId}-${pollId}-${castPollVoteRequest}"
                    .asStreamTypedKey<PollVoteResponse>()
            ) {
                delegate.castPollVote(activityId, pollId, castPollVoteRequest)
            }
            .getOrThrow()

    override suspend fun castPollVote(activityId: String, pollId: String): PollVoteResponse =
        singleFlightProcessor
            .run("castPollVote-${activityId}-${pollId}".asStreamTypedKey<PollVoteResponse>()) {
                delegate.castPollVote(activityId, pollId)
            }
            .getOrThrow()

    override suspend fun deletePollVote(
        activityId: String,
        pollId: String,
        voteId: String,
        userId: String?,
    ): PollVoteResponse =
        singleFlightProcessor
            .run(
                "deletePollVote-${activityId}-${pollId}-${voteId}-${userId}"
                    .asStreamTypedKey<PollVoteResponse>()
            ) {
                delegate.deletePollVote(activityId, pollId, voteId, userId)
            }
            .getOrThrow()

    override suspend fun addReaction(
        activityId: String,
        addReactionRequest: AddReactionRequest,
    ): AddReactionResponse =
        singleFlightProcessor
            .run(
                "addReaction-${activityId}-${addReactionRequest}"
                    .asStreamTypedKey<AddReactionResponse>()
            ) {
                delegate.addReaction(activityId, addReactionRequest)
            }
            .getOrThrow()

    override suspend fun queryActivityReactions(
        activityId: String,
        queryActivityReactionsRequest: QueryActivityReactionsRequest,
    ): QueryActivityReactionsResponse =
        singleFlightProcessor
            .run(
                "queryActivityReactions-${activityId}-${queryActivityReactionsRequest}"
                    .asStreamTypedKey<QueryActivityReactionsResponse>()
            ) {
                delegate.queryActivityReactions(activityId, queryActivityReactionsRequest)
            }
            .getOrThrow()

    override suspend fun queryActivityReactions(
        activityId: String
    ): QueryActivityReactionsResponse =
        singleFlightProcessor
            .run(
                "queryActivityReactions-${activityId}"
                    .asStreamTypedKey<QueryActivityReactionsResponse>()
            ) {
                delegate.queryActivityReactions(activityId)
            }
            .getOrThrow()

    override suspend fun deleteActivityReaction(
        activityId: String,
        type: String,
    ): DeleteActivityReactionResponse =
        singleFlightProcessor
            .run(
                "deleteActivityReaction-${activityId}-${type}"
                    .asStreamTypedKey<DeleteActivityReactionResponse>()
            ) {
                delegate.deleteActivityReaction(activityId, type)
            }
            .getOrThrow()

    override suspend fun deleteActivity(id: String, hardDelete: Boolean?): DeleteActivityResponse =
        singleFlightProcessor
            .run("deleteActivity-${id}-${hardDelete}".asStreamTypedKey<DeleteActivityResponse>()) {
                delegate.deleteActivity(id, hardDelete)
            }
            .getOrThrow()

    override suspend fun getActivity(id: String): GetActivityResponse =
        singleFlightProcessor
            .run("getActivity-${id}".asStreamTypedKey<GetActivityResponse>()) {
                delegate.getActivity(id)
            }
            .getOrThrow()

    override suspend fun updateActivityPartial(
        id: String,
        updateActivityPartialRequest: UpdateActivityPartialRequest,
    ): UpdateActivityPartialResponse =
        singleFlightProcessor
            .run(
                "updateActivityPartial-${id}-${updateActivityPartialRequest}"
                    .asStreamTypedKey<UpdateActivityPartialResponse>()
            ) {
                delegate.updateActivityPartial(id, updateActivityPartialRequest)
            }
            .getOrThrow()

    override suspend fun updateActivityPartial(id: String): UpdateActivityPartialResponse =
        singleFlightProcessor
            .run("updateActivityPartial-${id}".asStreamTypedKey<UpdateActivityPartialResponse>()) {
                delegate.updateActivityPartial(id)
            }
            .getOrThrow()

    override suspend fun updateActivity(
        id: String,
        updateActivityRequest: UpdateActivityRequest,
    ): UpdateActivityResponse =
        singleFlightProcessor
            .run(
                "updateActivity-${id}-${updateActivityRequest}"
                    .asStreamTypedKey<UpdateActivityResponse>()
            ) {
                delegate.updateActivity(id, updateActivityRequest)
            }
            .getOrThrow()

    override suspend fun updateActivity(id: String): UpdateActivityResponse =
        singleFlightProcessor
            .run("updateActivity-${id}".asStreamTypedKey<UpdateActivityResponse>()) {
                delegate.updateActivity(id)
            }
            .getOrThrow()

    override suspend fun queryBookmarkFolders(
        queryBookmarkFoldersRequest: QueryBookmarkFoldersRequest
    ): QueryBookmarkFoldersResponse =
        singleFlightProcessor
            .run(
                "queryBookmarkFolders-${queryBookmarkFoldersRequest}"
                    .asStreamTypedKey<QueryBookmarkFoldersResponse>()
            ) {
                delegate.queryBookmarkFolders(queryBookmarkFoldersRequest)
            }
            .getOrThrow()

    override suspend fun queryBookmarkFolders(): QueryBookmarkFoldersResponse =
        singleFlightProcessor
            .run("queryBookmarkFolders".asStreamTypedKey<QueryBookmarkFoldersResponse>()) {
                delegate.queryBookmarkFolders()
            }
            .getOrThrow()

    override suspend fun deleteBookmarkFolder(folderId: String): DeleteBookmarkFolderResponse =
        singleFlightProcessor
            .run(
                "deleteBookmarkFolder-${folderId}".asStreamTypedKey<DeleteBookmarkFolderResponse>()
            ) {
                delegate.deleteBookmarkFolder(folderId)
            }
            .getOrThrow()

    override suspend fun updateBookmarkFolder(
        folderId: String,
        updateBookmarkFolderRequest: UpdateBookmarkFolderRequest,
    ): UpdateBookmarkFolderResponse =
        singleFlightProcessor
            .run(
                "updateBookmarkFolder-${folderId}-${updateBookmarkFolderRequest}"
                    .asStreamTypedKey<UpdateBookmarkFolderResponse>()
            ) {
                delegate.updateBookmarkFolder(folderId, updateBookmarkFolderRequest)
            }
            .getOrThrow()

    override suspend fun updateBookmarkFolder(folderId: String): UpdateBookmarkFolderResponse =
        singleFlightProcessor
            .run(
                "updateBookmarkFolder-${folderId}".asStreamTypedKey<UpdateBookmarkFolderResponse>()
            ) {
                delegate.updateBookmarkFolder(folderId)
            }
            .getOrThrow()

    override suspend fun queryBookmarks(
        queryBookmarksRequest: QueryBookmarksRequest
    ): QueryBookmarksResponse =
        singleFlightProcessor
            .run(
                "queryBookmarks-${queryBookmarksRequest}".asStreamTypedKey<QueryBookmarksResponse>()
            ) {
                delegate.queryBookmarks(queryBookmarksRequest)
            }
            .getOrThrow()

    override suspend fun queryBookmarks(): QueryBookmarksResponse =
        singleFlightProcessor
            .run("queryBookmarks".asStreamTypedKey<QueryBookmarksResponse>()) {
                delegate.queryBookmarks()
            }
            .getOrThrow()

    override suspend fun getComments(
        objectId: String,
        objectType: String,
        depth: Int?,
        sort: String?,
        repliesLimit: Int?,
        limit: Int?,
        prev: String?,
        next: String?,
    ): GetCommentsResponse =
        singleFlightProcessor
            .run(
                "getComments-${objectId}-${objectType}-${depth}-${sort}-${repliesLimit}-${limit}-${prev}-${next}"
                    .asStreamTypedKey<GetCommentsResponse>()
            ) {
                delegate.getComments(
                    objectId,
                    objectType,
                    depth,
                    sort,
                    repliesLimit,
                    limit,
                    prev,
                    next,
                )
            }
            .getOrThrow()

    override suspend fun addComment(addCommentRequest: AddCommentRequest): AddCommentResponse =
        singleFlightProcessor
            .run("addComment-${addCommentRequest}".asStreamTypedKey<AddCommentResponse>()) {
                delegate.addComment(addCommentRequest)
            }
            .getOrThrow()

    override suspend fun addCommentsBatch(
        addCommentsBatchRequest: AddCommentsBatchRequest
    ): AddCommentsBatchResponse =
        singleFlightProcessor
            .run(
                "addCommentsBatch-${addCommentsBatchRequest}"
                    .asStreamTypedKey<AddCommentsBatchResponse>()
            ) {
                delegate.addCommentsBatch(addCommentsBatchRequest)
            }
            .getOrThrow()

    override suspend fun queryComments(
        queryCommentsRequest: QueryCommentsRequest
    ): QueryCommentsResponse =
        singleFlightProcessor
            .run(
                "queryComments-${queryCommentsRequest}".asStreamTypedKey<QueryCommentsResponse>()
            ) {
                delegate.queryComments(queryCommentsRequest)
            }
            .getOrThrow()

    override suspend fun deleteComment(id: String, hardDelete: Boolean?): DeleteCommentResponse =
        singleFlightProcessor
            .run("deleteComment-${id}-${hardDelete}".asStreamTypedKey<DeleteCommentResponse>()) {
                delegate.deleteComment(id, hardDelete)
            }
            .getOrThrow()

    override suspend fun getComment(id: String): GetCommentResponse =
        singleFlightProcessor
            .run("getComment-${id}".asStreamTypedKey<GetCommentResponse>()) {
                delegate.getComment(id)
            }
            .getOrThrow()

    override suspend fun updateComment(
        id: String,
        updateCommentRequest: UpdateCommentRequest,
    ): UpdateCommentResponse =
        singleFlightProcessor
            .run(
                "updateComment-${id}-${updateCommentRequest}"
                    .asStreamTypedKey<UpdateCommentResponse>()
            ) {
                delegate.updateComment(id, updateCommentRequest)
            }
            .getOrThrow()

    override suspend fun updateComment(id: String): UpdateCommentResponse =
        singleFlightProcessor
            .run("updateComment-${id}".asStreamTypedKey<UpdateCommentResponse>()) {
                delegate.updateComment(id)
            }
            .getOrThrow()

    override suspend fun addCommentReaction(
        id: String,
        addCommentReactionRequest: AddCommentReactionRequest,
    ): AddCommentReactionResponse =
        singleFlightProcessor
            .run(
                "addCommentReaction-${id}-${addCommentReactionRequest}"
                    .asStreamTypedKey<AddCommentReactionResponse>()
            ) {
                delegate.addCommentReaction(id, addCommentReactionRequest)
            }
            .getOrThrow()

    override suspend fun queryCommentReactions(
        id: String,
        queryCommentReactionsRequest: QueryCommentReactionsRequest,
    ): QueryCommentReactionsResponse =
        singleFlightProcessor
            .run(
                "queryCommentReactions-${id}-${queryCommentReactionsRequest}"
                    .asStreamTypedKey<QueryCommentReactionsResponse>()
            ) {
                delegate.queryCommentReactions(id, queryCommentReactionsRequest)
            }
            .getOrThrow()

    override suspend fun queryCommentReactions(id: String): QueryCommentReactionsResponse =
        singleFlightProcessor
            .run("queryCommentReactions-${id}".asStreamTypedKey<QueryCommentReactionsResponse>()) {
                delegate.queryCommentReactions(id)
            }
            .getOrThrow()

    override suspend fun deleteCommentReaction(
        id: String,
        type: String,
    ): DeleteCommentReactionResponse =
        singleFlightProcessor
            .run(
                "deleteCommentReaction-${id}-${type}"
                    .asStreamTypedKey<DeleteCommentReactionResponse>()
            ) {
                delegate.deleteCommentReaction(id, type)
            }
            .getOrThrow()

    override suspend fun getCommentReplies(
        id: String,
        depth: Int?,
        sort: String?,
        repliesLimit: Int?,
        limit: Int?,
        prev: String?,
        next: String?,
    ): GetCommentRepliesResponse =
        singleFlightProcessor
            .run(
                "getCommentReplies-${id}-${depth}-${sort}-${repliesLimit}-${limit}-${prev}-${next}"
                    .asStreamTypedKey<GetCommentRepliesResponse>()
            ) {
                delegate.getCommentReplies(id, depth, sort, repliesLimit, limit, prev, next)
            }
            .getOrThrow()

    override suspend fun deleteFeed(
        feedGroupId: String,
        feedId: String,
        hardDelete: Boolean?,
    ): DeleteFeedResponse =
        singleFlightProcessor
            .run(
                "deleteFeed-${feedGroupId}-${feedId}-${hardDelete}"
                    .asStreamTypedKey<DeleteFeedResponse>()
            ) {
                delegate.deleteFeed(feedGroupId, feedId, hardDelete)
            }
            .getOrThrow()

    override suspend fun getOrCreateFeed(
        feedGroupId: String,
        feedId: String,
        connectionId: String?,
        getOrCreateFeedRequest: GetOrCreateFeedRequest,
    ): GetOrCreateFeedResponse =
        singleFlightProcessor
            .run(
                "getOrCreateFeed-${feedGroupId}-${feedId}-${connectionId}-${getOrCreateFeedRequest}"
                    .asStreamTypedKey<GetOrCreateFeedResponse>()
            ) {
                delegate.getOrCreateFeed(feedGroupId, feedId, connectionId, getOrCreateFeedRequest)
            }
            .getOrThrow()

    override suspend fun getOrCreateFeed(
        feedGroupId: String,
        feedId: String,
        connectionId: String?,
    ): GetOrCreateFeedResponse =
        singleFlightProcessor
            .run(
                "getOrCreateFeed-${feedGroupId}-${feedId}-${connectionId}"
                    .asStreamTypedKey<GetOrCreateFeedResponse>()
            ) {
                delegate.getOrCreateFeed(feedGroupId, feedId, connectionId)
            }
            .getOrThrow()

    override suspend fun updateFeed(
        feedGroupId: String,
        feedId: String,
        updateFeedRequest: UpdateFeedRequest,
    ): UpdateFeedResponse =
        singleFlightProcessor
            .run(
                "updateFeed-${feedGroupId}-${feedId}-${updateFeedRequest}"
                    .asStreamTypedKey<UpdateFeedResponse>()
            ) {
                delegate.updateFeed(feedGroupId, feedId, updateFeedRequest)
            }
            .getOrThrow()

    override suspend fun updateFeed(feedGroupId: String, feedId: String): UpdateFeedResponse =
        singleFlightProcessor
            .run("updateFeed-${feedGroupId}-${feedId}".asStreamTypedKey<UpdateFeedResponse>()) {
                delegate.updateFeed(feedGroupId, feedId)
            }
            .getOrThrow()

    override suspend fun markActivity(
        feedGroupId: String,
        feedId: String,
        markActivityRequest: MarkActivityRequest,
    ): Response =
        singleFlightProcessor
            .run(
                "markActivity-${feedGroupId}-${feedId}-${markActivityRequest}"
                    .asStreamTypedKey<Response>()
            ) {
                delegate.markActivity(feedGroupId, feedId, markActivityRequest)
            }
            .getOrThrow()

    override suspend fun markActivity(feedGroupId: String, feedId: String): Response =
        singleFlightProcessor
            .run("markActivity-${feedGroupId}-${feedId}".asStreamTypedKey<Response>()) {
                delegate.markActivity(feedGroupId, feedId)
            }
            .getOrThrow()

    override suspend fun unpinActivity(
        feedGroupId: String,
        feedId: String,
        activityId: String,
    ): UnpinActivityResponse =
        singleFlightProcessor
            .run(
                "unpinActivity-${feedGroupId}-${feedId}-${activityId}"
                    .asStreamTypedKey<UnpinActivityResponse>()
            ) {
                delegate.unpinActivity(feedGroupId, feedId, activityId)
            }
            .getOrThrow()

    override suspend fun pinActivity(
        feedGroupId: String,
        feedId: String,
        activityId: String,
        pinActivityRequest: PinActivityRequest,
    ): PinActivityResponse =
        singleFlightProcessor
            .run(
                "pinActivity-${feedGroupId}-${feedId}-${activityId}-${pinActivityRequest}"
                    .asStreamTypedKey<PinActivityResponse>()
            ) {
                delegate.pinActivity(feedGroupId, feedId, activityId, pinActivityRequest)
            }
            .getOrThrow()

    override suspend fun pinActivity(
        feedGroupId: String,
        feedId: String,
        activityId: String,
    ): PinActivityResponse =
        singleFlightProcessor
            .run(
                "pinActivity-${feedGroupId}-${feedId}-${activityId}"
                    .asStreamTypedKey<PinActivityResponse>()
            ) {
                delegate.pinActivity(feedGroupId, feedId, activityId)
            }
            .getOrThrow()

    override suspend fun updateFeedMembers(
        feedGroupId: String,
        feedId: String,
        updateFeedMembersRequest: UpdateFeedMembersRequest,
    ): UpdateFeedMembersResponse =
        singleFlightProcessor
            .run(
                "updateFeedMembers-${feedGroupId}-${feedId}-${updateFeedMembersRequest}"
                    .asStreamTypedKey<UpdateFeedMembersResponse>()
            ) {
                delegate.updateFeedMembers(feedGroupId, feedId, updateFeedMembersRequest)
            }
            .getOrThrow()

    override suspend fun acceptFeedMemberInvite(
        feedId: String,
        feedGroupId: String,
        acceptFeedMemberInviteRequest: AcceptFeedMemberInviteRequest,
    ): AcceptFeedMemberInviteResponse =
        singleFlightProcessor
            .run(
                "acceptFeedMemberInvite-${feedId}-${feedGroupId}-${acceptFeedMemberInviteRequest}"
                    .asStreamTypedKey<AcceptFeedMemberInviteResponse>()
            ) {
                delegate.acceptFeedMemberInvite(feedId, feedGroupId, acceptFeedMemberInviteRequest)
            }
            .getOrThrow()

    override suspend fun acceptFeedMemberInvite(
        feedId: String,
        feedGroupId: String,
    ): AcceptFeedMemberInviteResponse =
        singleFlightProcessor
            .run(
                "acceptFeedMemberInvite-${feedId}-${feedGroupId}"
                    .asStreamTypedKey<AcceptFeedMemberInviteResponse>()
            ) {
                delegate.acceptFeedMemberInvite(feedId, feedGroupId)
            }
            .getOrThrow()

    override suspend fun queryFeedMembers(
        feedGroupId: String,
        feedId: String,
        queryFeedMembersRequest: QueryFeedMembersRequest,
    ): QueryFeedMembersResponse =
        singleFlightProcessor
            .run(
                "queryFeedMembers-${feedGroupId}-${feedId}-${queryFeedMembersRequest}"
                    .asStreamTypedKey<QueryFeedMembersResponse>()
            ) {
                delegate.queryFeedMembers(feedGroupId, feedId, queryFeedMembersRequest)
            }
            .getOrThrow()

    override suspend fun queryFeedMembers(
        feedGroupId: String,
        feedId: String,
    ): QueryFeedMembersResponse =
        singleFlightProcessor
            .run(
                "queryFeedMembers-${feedGroupId}-${feedId}"
                    .asStreamTypedKey<QueryFeedMembersResponse>()
            ) {
                delegate.queryFeedMembers(feedGroupId, feedId)
            }
            .getOrThrow()

    override suspend fun rejectFeedMemberInvite(
        feedGroupId: String,
        feedId: String,
        rejectFeedMemberInviteRequest: RejectFeedMemberInviteRequest,
    ): RejectFeedMemberInviteResponse =
        singleFlightProcessor
            .run(
                "rejectFeedMemberInvite-${feedGroupId}-${feedId}-${rejectFeedMemberInviteRequest}"
                    .asStreamTypedKey<RejectFeedMemberInviteResponse>()
            ) {
                delegate.rejectFeedMemberInvite(feedGroupId, feedId, rejectFeedMemberInviteRequest)
            }
            .getOrThrow()

    override suspend fun rejectFeedMemberInvite(
        feedGroupId: String,
        feedId: String,
    ): RejectFeedMemberInviteResponse =
        singleFlightProcessor
            .run(
                "rejectFeedMemberInvite-${feedGroupId}-${feedId}"
                    .asStreamTypedKey<RejectFeedMemberInviteResponse>()
            ) {
                delegate.rejectFeedMemberInvite(feedGroupId, feedId)
            }
            .getOrThrow()

    override suspend fun stopWatchingFeed(
        feedGroupId: String,
        feedId: String,
        connectionId: String?,
    ): Response =
        singleFlightProcessor
            .run(
                "stopWatchingFeed-${feedGroupId}-${feedId}-${connectionId}"
                    .asStreamTypedKey<Response>()
            ) {
                delegate.stopWatchingFeed(feedGroupId, feedId, connectionId)
            }
            .getOrThrow()

    override suspend fun getFollowSuggestions(
        feedGroupId: String,
        limit: Int?,
    ): GetFollowSuggestionsResponse =
        singleFlightProcessor
            .run(
                "getFollowSuggestions-${feedGroupId}-${limit}"
                    .asStreamTypedKey<GetFollowSuggestionsResponse>()
            ) {
                delegate.getFollowSuggestions(feedGroupId, limit)
            }
            .getOrThrow()

    override suspend fun createFeedsBatch(
        createFeedsBatchRequest: CreateFeedsBatchRequest
    ): CreateFeedsBatchResponse =
        singleFlightProcessor
            .run(
                "createFeedsBatch-${createFeedsBatchRequest}"
                    .asStreamTypedKey<CreateFeedsBatchResponse>()
            ) {
                delegate.createFeedsBatch(createFeedsBatchRequest)
            }
            .getOrThrow()

    override suspend fun queryFeeds(
        connectionId: String?,
        queryFeedsRequest: QueryFeedsRequest,
    ): QueryFeedsResponse =
        singleFlightProcessor
            .run(
                "queryFeeds-${connectionId}-${queryFeedsRequest}"
                    .asStreamTypedKey<QueryFeedsResponse>()
            ) {
                delegate.queryFeeds(connectionId, queryFeedsRequest)
            }
            .getOrThrow()

    override suspend fun queryFeeds(connectionId: String?): QueryFeedsResponse =
        singleFlightProcessor
            .run("queryFeeds-${connectionId}".asStreamTypedKey<QueryFeedsResponse>()) {
                delegate.queryFeeds(connectionId)
            }
            .getOrThrow()

    override suspend fun updateFollow(
        updateFollowRequest: UpdateFollowRequest
    ): UpdateFollowResponse =
        singleFlightProcessor
            .run("updateFollow-${updateFollowRequest}".asStreamTypedKey<UpdateFollowResponse>()) {
                delegate.updateFollow(updateFollowRequest)
            }
            .getOrThrow()

    override suspend fun follow(followRequest: FollowRequest): SingleFollowResponse =
        singleFlightProcessor
            .run("follow-${followRequest}".asStreamTypedKey<SingleFollowResponse>()) {
                delegate.follow(followRequest)
            }
            .getOrThrow()

    override suspend fun acceptFollow(
        acceptFollowRequest: AcceptFollowRequest
    ): AcceptFollowResponse =
        singleFlightProcessor
            .run("acceptFollow-${acceptFollowRequest}".asStreamTypedKey<AcceptFollowResponse>()) {
                delegate.acceptFollow(acceptFollowRequest)
            }
            .getOrThrow()

    override suspend fun followBatch(followBatchRequest: FollowBatchRequest): FollowBatchResponse =
        singleFlightProcessor
            .run("followBatch-${followBatchRequest}".asStreamTypedKey<FollowBatchResponse>()) {
                delegate.followBatch(followBatchRequest)
            }
            .getOrThrow()

    override suspend fun queryFollows(
        queryFollowsRequest: QueryFollowsRequest
    ): QueryFollowsResponse =
        singleFlightProcessor
            .run("queryFollows-${queryFollowsRequest}".asStreamTypedKey<QueryFollowsResponse>()) {
                delegate.queryFollows(queryFollowsRequest)
            }
            .getOrThrow()

    override suspend fun queryFollows(): QueryFollowsResponse =
        singleFlightProcessor
            .run("queryFollows".asStreamTypedKey<QueryFollowsResponse>()) {
                delegate.queryFollows()
            }
            .getOrThrow()

    override suspend fun rejectFollow(
        rejectFollowRequest: RejectFollowRequest
    ): RejectFollowResponse =
        singleFlightProcessor
            .run("rejectFollow-${rejectFollowRequest}".asStreamTypedKey<RejectFollowResponse>()) {
                delegate.rejectFollow(rejectFollowRequest)
            }
            .getOrThrow()

    override suspend fun unfollow(source: String, target: String): UnfollowResponse =
        singleFlightProcessor
            .run("unfollow-${source}-${target}".asStreamTypedKey<UnfollowResponse>()) {
                delegate.unfollow(source, target)
            }
            .getOrThrow()

    override suspend fun createGuest(createGuestRequest: CreateGuestRequest): CreateGuestResponse =
        singleFlightProcessor
            .run("createGuest-${createGuestRequest}".asStreamTypedKey<CreateGuestResponse>()) {
                delegate.createGuest(createGuestRequest)
            }
            .getOrThrow()

    override suspend fun longPoll(connectionId: String?, json: WSAuthMessage?) {
        singleFlightProcessor
            .run("longPoll-${connectionId}-${json}".asStreamTypedKey<Unit>()) {
                delegate.longPoll(connectionId, json)
            }
            .getOrThrow()
    }

    override suspend fun ban(banRequest: BanRequest): BanResponse =
        singleFlightProcessor
            .run("ban-${banRequest}".asStreamTypedKey<BanResponse>()) { delegate.ban(banRequest) }
            .getOrThrow()

    override suspend fun upsertConfig(
        upsertConfigRequest: UpsertConfigRequest
    ): UpsertConfigResponse =
        singleFlightProcessor
            .run("upsertConfig-${upsertConfigRequest}".asStreamTypedKey<UpsertConfigResponse>()) {
                delegate.upsertConfig(upsertConfigRequest)
            }
            .getOrThrow()

    override suspend fun deleteConfig(key: String, team: String?): DeleteModerationConfigResponse =
        singleFlightProcessor
            .run("deleteConfig-${key}-${team}".asStreamTypedKey<DeleteModerationConfigResponse>()) {
                delegate.deleteConfig(key, team)
            }
            .getOrThrow()

    override suspend fun getConfig(key: String, team: String?): GetConfigResponse =
        singleFlightProcessor
            .run("getConfig-${key}-${team}".asStreamTypedKey<GetConfigResponse>()) {
                delegate.getConfig(key, team)
            }
            .getOrThrow()

    override suspend fun queryModerationConfigs(
        queryModerationConfigsRequest: QueryModerationConfigsRequest
    ): QueryModerationConfigsResponse =
        singleFlightProcessor
            .run(
                "queryModerationConfigs-${queryModerationConfigsRequest}"
                    .asStreamTypedKey<QueryModerationConfigsResponse>()
            ) {
                delegate.queryModerationConfigs(queryModerationConfigsRequest)
            }
            .getOrThrow()

    override suspend fun queryModerationConfigs(): QueryModerationConfigsResponse =
        singleFlightProcessor
            .run("queryModerationConfigs".asStreamTypedKey<QueryModerationConfigsResponse>()) {
                delegate.queryModerationConfigs()
            }
            .getOrThrow()

    override suspend fun flag(flagRequest: FlagRequest): FlagResponse =
        singleFlightProcessor
            .run("flag-${flagRequest}".asStreamTypedKey<FlagResponse>()) {
                delegate.flag(flagRequest)
            }
            .getOrThrow()

    override suspend fun mute(muteRequest: MuteRequest): MuteResponse =
        singleFlightProcessor
            .run("mute-${muteRequest}".asStreamTypedKey<MuteResponse>()) {
                delegate.mute(muteRequest)
            }
            .getOrThrow()

    override suspend fun queryReviewQueue(
        queryReviewQueueRequest: QueryReviewQueueRequest
    ): QueryReviewQueueResponse =
        singleFlightProcessor
            .run(
                "queryReviewQueue-${queryReviewQueueRequest}"
                    .asStreamTypedKey<QueryReviewQueueResponse>()
            ) {
                delegate.queryReviewQueue(queryReviewQueueRequest)
            }
            .getOrThrow()

    override suspend fun queryReviewQueue(): QueryReviewQueueResponse =
        singleFlightProcessor
            .run("queryReviewQueue".asStreamTypedKey<QueryReviewQueueResponse>()) {
                delegate.queryReviewQueue()
            }
            .getOrThrow()

    override suspend fun submitAction(
        submitActionRequest: SubmitActionRequest
    ): SubmitActionResponse =
        singleFlightProcessor
            .run("submitAction-${submitActionRequest}".asStreamTypedKey<SubmitActionResponse>()) {
                delegate.submitAction(submitActionRequest)
            }
            .getOrThrow()

    override suspend fun getOG(url: String): GetOGResponse =
        singleFlightProcessor
            .run("getOG-${url}".asStreamTypedKey<GetOGResponse>()) { delegate.getOG(url) }
            .getOrThrow()

    override suspend fun createPoll(createPollRequest: CreatePollRequest): PollResponse =
        singleFlightProcessor
            .run("createPoll-${createPollRequest}".asStreamTypedKey<PollResponse>()) {
                delegate.createPoll(createPollRequest)
            }
            .getOrThrow()

    override suspend fun updatePoll(updatePollRequest: UpdatePollRequest): PollResponse =
        singleFlightProcessor
            .run("updatePoll-${updatePollRequest}".asStreamTypedKey<PollResponse>()) {
                delegate.updatePoll(updatePollRequest)
            }
            .getOrThrow()

    override suspend fun queryPolls(
        userId: String?,
        queryPollsRequest: QueryPollsRequest,
    ): QueryPollsResponse =
        singleFlightProcessor
            .run(
                "queryPolls-${userId}-${queryPollsRequest}".asStreamTypedKey<QueryPollsResponse>()
            ) {
                delegate.queryPolls(userId, queryPollsRequest)
            }
            .getOrThrow()

    override suspend fun queryPolls(userId: String?): QueryPollsResponse =
        singleFlightProcessor
            .run("queryPolls-${userId}".asStreamTypedKey<QueryPollsResponse>()) {
                delegate.queryPolls(userId)
            }
            .getOrThrow()

    override suspend fun deletePoll(pollId: String, userId: String?): Response =
        singleFlightProcessor
            .run("deletePoll-${pollId}-${userId}".asStreamTypedKey<Response>()) {
                delegate.deletePoll(pollId, userId)
            }
            .getOrThrow()

    override suspend fun getPoll(pollId: String, userId: String?): PollResponse =
        singleFlightProcessor
            .run("getPoll-${pollId}-${userId}".asStreamTypedKey<PollResponse>()) {
                delegate.getPoll(pollId, userId)
            }
            .getOrThrow()

    override suspend fun updatePollPartial(
        pollId: String,
        updatePollPartialRequest: UpdatePollPartialRequest,
    ): PollResponse =
        singleFlightProcessor
            .run(
                "updatePollPartial-${pollId}-${updatePollPartialRequest}"
                    .asStreamTypedKey<PollResponse>()
            ) {
                delegate.updatePollPartial(pollId, updatePollPartialRequest)
            }
            .getOrThrow()

    override suspend fun updatePollPartial(pollId: String): PollResponse =
        singleFlightProcessor
            .run("updatePollPartial-${pollId}".asStreamTypedKey<PollResponse>()) {
                delegate.updatePollPartial(pollId)
            }
            .getOrThrow()

    override suspend fun createPollOption(
        pollId: String,
        createPollOptionRequest: CreatePollOptionRequest,
    ): PollOptionResponse =
        singleFlightProcessor
            .run(
                "createPollOption-${pollId}-${createPollOptionRequest}"
                    .asStreamTypedKey<PollOptionResponse>()
            ) {
                delegate.createPollOption(pollId, createPollOptionRequest)
            }
            .getOrThrow()

    override suspend fun updatePollOption(
        pollId: String,
        updatePollOptionRequest: UpdatePollOptionRequest,
    ): PollOptionResponse =
        singleFlightProcessor
            .run(
                "updatePollOption-${pollId}-${updatePollOptionRequest}"
                    .asStreamTypedKey<PollOptionResponse>()
            ) {
                delegate.updatePollOption(pollId, updatePollOptionRequest)
            }
            .getOrThrow()

    override suspend fun deletePollOption(
        pollId: String,
        optionId: String,
        userId: String?,
    ): Response =
        singleFlightProcessor
            .run("deletePollOption-${pollId}-${optionId}-${userId}".asStreamTypedKey<Response>()) {
                delegate.deletePollOption(pollId, optionId, userId)
            }
            .getOrThrow()

    override suspend fun getPollOption(
        pollId: String,
        optionId: String,
        userId: String?,
    ): PollOptionResponse =
        singleFlightProcessor
            .run(
                "getPollOption-${pollId}-${optionId}-${userId}"
                    .asStreamTypedKey<PollOptionResponse>()
            ) {
                delegate.getPollOption(pollId, optionId, userId)
            }
            .getOrThrow()

    override suspend fun queryPollVotes(
        pollId: String,
        userId: String?,
        queryPollVotesRequest: QueryPollVotesRequest,
    ): PollVotesResponse =
        singleFlightProcessor
            .run(
                "queryPollVotes-${pollId}-${userId}-${queryPollVotesRequest}"
                    .asStreamTypedKey<PollVotesResponse>()
            ) {
                delegate.queryPollVotes(pollId, userId, queryPollVotesRequest)
            }
            .getOrThrow()

    override suspend fun queryPollVotes(pollId: String, userId: String?): PollVotesResponse =
        singleFlightProcessor
            .run("queryPollVotes-${pollId}-${userId}".asStreamTypedKey<PollVotesResponse>()) {
                delegate.queryPollVotes(pollId, userId)
            }
            .getOrThrow()

    override suspend fun deleteFile(url: String?): Response =
        singleFlightProcessor
            .run("deleteFile-${url}".asStreamTypedKey<Response>()) { delegate.deleteFile(url) }
            .getOrThrow()

    override suspend fun uploadFile(fileUploadRequest: FileUploadRequest): FileUploadResponse =
        singleFlightProcessor
            .run("uploadFile-${fileUploadRequest}".asStreamTypedKey<FileUploadResponse>()) {
                delegate.uploadFile(fileUploadRequest)
            }
            .getOrThrow()

    override suspend fun uploadFile(): FileUploadResponse =
        singleFlightProcessor
            .run("uploadFile".asStreamTypedKey<FileUploadResponse>()) { delegate.uploadFile() }
            .getOrThrow()

    override suspend fun deleteImage(url: String?): Response =
        singleFlightProcessor
            .run("deleteImage-${url}".asStreamTypedKey<Response>()) { delegate.deleteImage(url) }
            .getOrThrow()

    override suspend fun uploadImage(imageUploadRequest: ImageUploadRequest): ImageUploadResponse =
        singleFlightProcessor
            .run("uploadImage-${imageUploadRequest}".asStreamTypedKey<ImageUploadResponse>()) {
                delegate.uploadImage(imageUploadRequest)
            }
            .getOrThrow()

    override suspend fun uploadImage(): ImageUploadResponse =
        singleFlightProcessor
            .run("uploadImage".asStreamTypedKey<ImageUploadResponse>()) { delegate.uploadImage() }
            .getOrThrow()

    override suspend fun queryUsers(payload: QueryUsersPayload?): QueryUsersResponse =
        singleFlightProcessor
            .run("queryUsers-${payload}".asStreamTypedKey<QueryUsersResponse>()) {
                delegate.queryUsers(payload)
            }
            .getOrThrow()

    override suspend fun updateUsersPartial(
        updateUsersPartialRequest: UpdateUsersPartialRequest
    ): UpdateUsersResponse =
        singleFlightProcessor
            .run(
                "updateUsersPartial-${updateUsersPartialRequest}"
                    .asStreamTypedKey<UpdateUsersResponse>()
            ) {
                delegate.updateUsersPartial(updateUsersPartialRequest)
            }
            .getOrThrow()

    override suspend fun updateUsers(updateUsersRequest: UpdateUsersRequest): UpdateUsersResponse =
        singleFlightProcessor
            .run("updateUsers-${updateUsersRequest}".asStreamTypedKey<UpdateUsersResponse>()) {
                delegate.updateUsers(updateUsersRequest)
            }
            .getOrThrow()

    override suspend fun getBlockedUsers(): GetBlockedUsersResponse =
        singleFlightProcessor
            .run("getBlockedUsers".asStreamTypedKey<GetBlockedUsersResponse>()) {
                delegate.getBlockedUsers()
            }
            .getOrThrow()

    override suspend fun blockUsers(blockUsersRequest: BlockUsersRequest): BlockUsersResponse =
        singleFlightProcessor
            .run("blockUsers-${blockUsersRequest}".asStreamTypedKey<BlockUsersResponse>()) {
                delegate.blockUsers(blockUsersRequest)
            }
            .getOrThrow()

    override suspend fun getUserLiveLocations(): SharedLocationsResponse =
        singleFlightProcessor
            .run("getUserLiveLocations".asStreamTypedKey<SharedLocationsResponse>()) {
                delegate.getUserLiveLocations()
            }
            .getOrThrow()

    override suspend fun updateLiveLocation(
        updateLiveLocationRequest: UpdateLiveLocationRequest
    ): SharedLocationResponse =
        singleFlightProcessor
            .run(
                "updateLiveLocation-${updateLiveLocationRequest}"
                    .asStreamTypedKey<SharedLocationResponse>()
            ) {
                delegate.updateLiveLocation(updateLiveLocationRequest)
            }
            .getOrThrow()

    override suspend fun unblockUsers(
        unblockUsersRequest: UnblockUsersRequest
    ): UnblockUsersResponse =
        singleFlightProcessor
            .run("unblockUsers-${unblockUsersRequest}".asStreamTypedKey<UnblockUsersResponse>()) {
                delegate.unblockUsers(unblockUsersRequest)
            }
            .getOrThrow()
}
