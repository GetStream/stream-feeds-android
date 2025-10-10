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
import io.getstream.feeds.android.network.models.OwnCapabilitiesBatchRequest
import io.getstream.feeds.android.network.models.OwnCapabilitiesBatchResponse
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
import io.getstream.feeds.android.network.models.UpsertPushPreferencesRequest
import io.getstream.feeds.android.network.models.UpsertPushPreferencesResponse
import io.getstream.feeds.android.network.models.WSAuthMessage

internal class FeedsSingleFlightApi(
    private val delegate: FeedsApi,
    private val singleFlightProcessor: StreamSingleFlightProcessor,
) : FeedsApi {

    override suspend fun getApp(): GetApplicationResponse =
        singleFlight("getApp") { delegate.getApp() }

    override suspend fun listBlockLists(team: String?): ListBlockListResponse =
        singleFlight("listBlockLists", team) { delegate.listBlockLists(team) }

    override suspend fun createBlockList(
        createBlockListRequest: CreateBlockListRequest
    ): CreateBlockListResponse =
        singleFlight("createBlockList", createBlockListRequest) {
            delegate.createBlockList(createBlockListRequest)
        }

    override suspend fun deleteBlockList(name: String, team: String?): Response =
        singleFlight("deleteBlockList", name, team) { delegate.deleteBlockList(name, team) }

    override suspend fun updateBlockList(
        name: String,
        updateBlockListRequest: UpdateBlockListRequest,
    ): UpdateBlockListResponse =
        singleFlight("updateBlockList", name, updateBlockListRequest) {
            delegate.updateBlockList(name, updateBlockListRequest)
        }

    override suspend fun updateBlockList(name: String): UpdateBlockListResponse =
        singleFlight("updateBlockList", name) { delegate.updateBlockList(name) }

    override suspend fun deleteDevice(id: String): Response =
        singleFlight("deleteDevice", id) { delegate.deleteDevice(id) }

    override suspend fun listDevices(): ListDevicesResponse =
        singleFlight("listDevices") { delegate.listDevices() }

    override suspend fun createDevice(createDeviceRequest: CreateDeviceRequest): Response =
        singleFlight("createDevice", createDeviceRequest) {
            delegate.createDevice(createDeviceRequest)
        }

    override suspend fun addActivity(addActivityRequest: AddActivityRequest): AddActivityResponse =
        singleFlight("addActivity", addActivityRequest) { delegate.addActivity(addActivityRequest) }

    override suspend fun upsertActivities(
        upsertActivitiesRequest: UpsertActivitiesRequest
    ): UpsertActivitiesResponse =
        singleFlight("upsertActivities", upsertActivitiesRequest) {
            delegate.upsertActivities(upsertActivitiesRequest)
        }

    override suspend fun deleteActivities(
        deleteActivitiesRequest: DeleteActivitiesRequest
    ): DeleteActivitiesResponse =
        singleFlight("deleteActivities", deleteActivitiesRequest) {
            delegate.deleteActivities(deleteActivitiesRequest)
        }

    override suspend fun queryActivities(
        queryActivitiesRequest: QueryActivitiesRequest
    ): QueryActivitiesResponse =
        singleFlight("queryActivities", queryActivitiesRequest) {
            delegate.queryActivities(queryActivitiesRequest)
        }

    override suspend fun queryActivities(): QueryActivitiesResponse =
        singleFlight("queryActivities") { delegate.queryActivities() }

    override suspend fun deleteBookmark(
        activityId: String,
        folderId: String?,
    ): DeleteBookmarkResponse =
        singleFlight("deleteBookmark", activityId, folderId) {
            delegate.deleteBookmark(activityId, folderId)
        }

    override suspend fun updateBookmark(
        activityId: String,
        updateBookmarkRequest: UpdateBookmarkRequest,
    ): UpdateBookmarkResponse =
        singleFlight("updateBookmark", activityId, updateBookmarkRequest) {
            delegate.updateBookmark(activityId, updateBookmarkRequest)
        }

    override suspend fun updateBookmark(activityId: String): UpdateBookmarkResponse =
        singleFlight("updateBookmark", activityId) { delegate.updateBookmark(activityId) }

    override suspend fun addBookmark(
        activityId: String,
        addBookmarkRequest: AddBookmarkRequest,
    ): AddBookmarkResponse =
        singleFlight("addBookmark", activityId, addBookmarkRequest) {
            delegate.addBookmark(activityId, addBookmarkRequest)
        }

    override suspend fun addBookmark(activityId: String): AddBookmarkResponse =
        singleFlight("addBookmark", activityId) { delegate.addBookmark(activityId) }

    override suspend fun activityFeedback(
        activityId: String,
        activityFeedbackRequest: ActivityFeedbackRequest,
    ): ActivityFeedbackResponse =
        singleFlight("activityFeedback", activityId, activityFeedbackRequest) {
            delegate.activityFeedback(activityId, activityFeedbackRequest)
        }

    override suspend fun activityFeedback(activityId: String): ActivityFeedbackResponse =
        singleFlight("activityFeedback", activityId) { delegate.activityFeedback(activityId) }

    override suspend fun castPollVote(
        activityId: String,
        pollId: String,
        castPollVoteRequest: CastPollVoteRequest,
    ): PollVoteResponse =
        singleFlight("castPollVote", activityId, pollId, castPollVoteRequest) {
            delegate.castPollVote(activityId, pollId, castPollVoteRequest)
        }

    override suspend fun castPollVote(activityId: String, pollId: String): PollVoteResponse =
        singleFlight("castPollVote", activityId, pollId) {
            delegate.castPollVote(activityId, pollId)
        }

    override suspend fun deletePollVote(
        activityId: String,
        pollId: String,
        voteId: String,
        userId: String?,
    ): PollVoteResponse =
        singleFlight("deletePollVote", activityId, pollId, voteId, userId) {
            delegate.deletePollVote(activityId, pollId, voteId, userId)
        }

    override suspend fun addActivityReaction(
        activityId: String,
        addReactionRequest: AddReactionRequest,
    ): AddReactionResponse =
        singleFlight("addActivityReaction", activityId, addReactionRequest) {
            delegate.addActivityReaction(activityId, addReactionRequest)
        }

    override suspend fun queryActivityReactions(
        activityId: String,
        queryActivityReactionsRequest: QueryActivityReactionsRequest,
    ): QueryActivityReactionsResponse =
        singleFlight("queryActivityReactions", activityId, queryActivityReactionsRequest) {
            delegate.queryActivityReactions(activityId, queryActivityReactionsRequest)
        }

    override suspend fun queryActivityReactions(
        activityId: String
    ): QueryActivityReactionsResponse =
        singleFlight("queryActivityReactions", activityId) {
            delegate.queryActivityReactions(activityId)
        }

    override suspend fun deleteActivityReaction(
        activityId: String,
        type: String,
    ): DeleteActivityReactionResponse =
        singleFlight("deleteActivityReaction", activityId, type) {
            delegate.deleteActivityReaction(activityId, type)
        }

    override suspend fun deleteActivity(id: String, hardDelete: Boolean?): DeleteActivityResponse =
        singleFlight("deleteActivity", id, hardDelete) { delegate.deleteActivity(id, hardDelete) }

    override suspend fun getActivity(id: String): GetActivityResponse =
        singleFlight("getActivity", id) { delegate.getActivity(id) }

    override suspend fun updateActivityPartial(
        id: String,
        updateActivityPartialRequest: UpdateActivityPartialRequest,
    ): UpdateActivityPartialResponse =
        singleFlight("updateActivityPartial", id, updateActivityPartialRequest) {
            delegate.updateActivityPartial(id, updateActivityPartialRequest)
        }

    override suspend fun updateActivityPartial(id: String): UpdateActivityPartialResponse =
        singleFlight("updateActivityPartial", id) { delegate.updateActivityPartial(id) }

    override suspend fun updateActivity(
        id: String,
        updateActivityRequest: UpdateActivityRequest,
    ): UpdateActivityResponse =
        singleFlight("updateActivity", id, updateActivityRequest) {
            delegate.updateActivity(id, updateActivityRequest)
        }

    override suspend fun updateActivity(id: String): UpdateActivityResponse =
        singleFlight("updateActivity", id) { delegate.updateActivity(id) }

    override suspend fun queryBookmarkFolders(
        queryBookmarkFoldersRequest: QueryBookmarkFoldersRequest
    ): QueryBookmarkFoldersResponse =
        singleFlight("queryBookmarkFolders", queryBookmarkFoldersRequest) {
            delegate.queryBookmarkFolders(queryBookmarkFoldersRequest)
        }

    override suspend fun queryBookmarkFolders(): QueryBookmarkFoldersResponse =
        singleFlight("queryBookmarkFolders") { delegate.queryBookmarkFolders() }

    override suspend fun deleteBookmarkFolder(folderId: String): DeleteBookmarkFolderResponse =
        singleFlight("deleteBookmarkFolder", folderId) { delegate.deleteBookmarkFolder(folderId) }

    override suspend fun updateBookmarkFolder(
        folderId: String,
        updateBookmarkFolderRequest: UpdateBookmarkFolderRequest,
    ): UpdateBookmarkFolderResponse =
        singleFlight("updateBookmarkFolder", folderId, updateBookmarkFolderRequest) {
            delegate.updateBookmarkFolder(folderId, updateBookmarkFolderRequest)
        }

    override suspend fun updateBookmarkFolder(folderId: String): UpdateBookmarkFolderResponse =
        singleFlight("updateBookmarkFolder", folderId) { delegate.updateBookmarkFolder(folderId) }

    override suspend fun queryBookmarks(
        queryBookmarksRequest: QueryBookmarksRequest
    ): QueryBookmarksResponse =
        singleFlight("queryBookmarks", queryBookmarksRequest) {
            delegate.queryBookmarks(queryBookmarksRequest)
        }

    override suspend fun queryBookmarks(): QueryBookmarksResponse =
        singleFlight("queryBookmarks") { delegate.queryBookmarks() }

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
        singleFlight(
            "getComments",
            objectId,
            objectType,
            depth,
            sort,
            repliesLimit,
            limit,
            prev,
            next,
        ) {
            delegate.getComments(objectId, objectType, depth, sort, repliesLimit, limit, prev, next)
        }

    override suspend fun addComment(addCommentRequest: AddCommentRequest): AddCommentResponse =
        singleFlight("addComment", addCommentRequest) { delegate.addComment(addCommentRequest) }

    override suspend fun addCommentsBatch(
        addCommentsBatchRequest: AddCommentsBatchRequest
    ): AddCommentsBatchResponse =
        singleFlight("addCommentsBatch", addCommentsBatchRequest) {
            delegate.addCommentsBatch(addCommentsBatchRequest)
        }

    override suspend fun queryComments(
        queryCommentsRequest: QueryCommentsRequest
    ): QueryCommentsResponse =
        singleFlight("queryComments", queryCommentsRequest) {
            delegate.queryComments(queryCommentsRequest)
        }

    override suspend fun deleteComment(id: String, hardDelete: Boolean?): DeleteCommentResponse =
        singleFlight("deleteComment", id, hardDelete) { delegate.deleteComment(id, hardDelete) }

    override suspend fun getComment(id: String): GetCommentResponse =
        singleFlight("getComment", id) { delegate.getComment(id) }

    override suspend fun updateComment(
        id: String,
        updateCommentRequest: UpdateCommentRequest,
    ): UpdateCommentResponse =
        singleFlight("updateComment", id, updateCommentRequest) {
            delegate.updateComment(id, updateCommentRequest)
        }

    override suspend fun updateComment(id: String): UpdateCommentResponse =
        singleFlight("updateComment", id) { delegate.updateComment(id) }

    override suspend fun addCommentReaction(
        id: String,
        addCommentReactionRequest: AddCommentReactionRequest,
    ): AddCommentReactionResponse =
        singleFlight("addCommentReaction", id, addCommentReactionRequest) {
            delegate.addCommentReaction(id, addCommentReactionRequest)
        }

    override suspend fun queryCommentReactions(
        id: String,
        queryCommentReactionsRequest: QueryCommentReactionsRequest,
    ): QueryCommentReactionsResponse =
        singleFlight("queryCommentReactions", id, queryCommentReactionsRequest) {
            delegate.queryCommentReactions(id, queryCommentReactionsRequest)
        }

    override suspend fun queryCommentReactions(id: String): QueryCommentReactionsResponse =
        singleFlight("queryCommentReactions", id) { delegate.queryCommentReactions(id) }

    override suspend fun deleteCommentReaction(
        id: String,
        type: String,
    ): DeleteCommentReactionResponse =
        singleFlight("deleteCommentReaction", id, type) { delegate.deleteCommentReaction(id, type) }

    override suspend fun getCommentReplies(
        id: String,
        depth: Int?,
        sort: String?,
        repliesLimit: Int?,
        limit: Int?,
        prev: String?,
        next: String?,
    ): GetCommentRepliesResponse =
        singleFlight("getCommentReplies", id, depth, sort, repliesLimit, limit, prev, next) {
            delegate.getCommentReplies(id, depth, sort, repliesLimit, limit, prev, next)
        }

    override suspend fun deleteFeed(
        feedGroupId: String,
        feedId: String,
        hardDelete: Boolean?,
    ): DeleteFeedResponse =
        singleFlight("deleteFeed", feedGroupId, feedId, hardDelete) {
            delegate.deleteFeed(feedGroupId, feedId, hardDelete)
        }

    override suspend fun getOrCreateFeed(
        feedGroupId: String,
        feedId: String,
        connectionId: String?,
        getOrCreateFeedRequest: GetOrCreateFeedRequest,
    ): GetOrCreateFeedResponse =
        singleFlight("getOrCreateFeed", feedGroupId, feedId, connectionId, getOrCreateFeedRequest) {
            delegate.getOrCreateFeed(feedGroupId, feedId, connectionId, getOrCreateFeedRequest)
        }

    override suspend fun getOrCreateFeed(
        feedGroupId: String,
        feedId: String,
        connectionId: String?,
    ): GetOrCreateFeedResponse =
        singleFlight("getOrCreateFeed", feedGroupId, feedId, connectionId) {
            delegate.getOrCreateFeed(feedGroupId, feedId, connectionId)
        }

    override suspend fun updateFeed(
        feedGroupId: String,
        feedId: String,
        updateFeedRequest: UpdateFeedRequest,
    ): UpdateFeedResponse =
        singleFlight("updateFeed", feedGroupId, feedId, updateFeedRequest) {
            delegate.updateFeed(feedGroupId, feedId, updateFeedRequest)
        }

    override suspend fun updateFeed(feedGroupId: String, feedId: String): UpdateFeedResponse =
        singleFlight("updateFeed", feedGroupId, feedId) { delegate.updateFeed(feedGroupId, feedId) }

    override suspend fun markActivity(
        feedGroupId: String,
        feedId: String,
        markActivityRequest: MarkActivityRequest,
    ): Response =
        singleFlight("markActivity", feedGroupId, feedId, markActivityRequest) {
            delegate.markActivity(feedGroupId, feedId, markActivityRequest)
        }

    override suspend fun markActivity(feedGroupId: String, feedId: String): Response =
        singleFlight("markActivity", feedGroupId, feedId) {
            delegate.markActivity(feedGroupId, feedId)
        }

    override suspend fun unpinActivity(
        feedGroupId: String,
        feedId: String,
        activityId: String,
    ): UnpinActivityResponse =
        singleFlight("unpinActivity", feedGroupId, feedId, activityId) {
            delegate.unpinActivity(feedGroupId, feedId, activityId)
        }

    override suspend fun pinActivity(
        feedGroupId: String,
        feedId: String,
        activityId: String,
        pinActivityRequest: PinActivityRequest,
    ): PinActivityResponse =
        singleFlight("pinActivity", feedGroupId, feedId, activityId, pinActivityRequest) {
            delegate.pinActivity(feedGroupId, feedId, activityId, pinActivityRequest)
        }

    override suspend fun pinActivity(
        feedGroupId: String,
        feedId: String,
        activityId: String,
    ): PinActivityResponse =
        singleFlight("pinActivity", feedGroupId, feedId, activityId) {
            delegate.pinActivity(feedGroupId, feedId, activityId)
        }

    override suspend fun updateFeedMembers(
        feedGroupId: String,
        feedId: String,
        updateFeedMembersRequest: UpdateFeedMembersRequest,
    ): UpdateFeedMembersResponse =
        singleFlight("updateFeedMembers", feedGroupId, feedId, updateFeedMembersRequest) {
            delegate.updateFeedMembers(feedGroupId, feedId, updateFeedMembersRequest)
        }

    override suspend fun acceptFeedMemberInvite(
        feedId: String,
        feedGroupId: String,
        acceptFeedMemberInviteRequest: AcceptFeedMemberInviteRequest,
    ): AcceptFeedMemberInviteResponse =
        singleFlight("acceptFeedMemberInvite", feedId, feedGroupId, acceptFeedMemberInviteRequest) {
            delegate.acceptFeedMemberInvite(feedId, feedGroupId, acceptFeedMemberInviteRequest)
        }

    override suspend fun acceptFeedMemberInvite(
        feedId: String,
        feedGroupId: String,
    ): AcceptFeedMemberInviteResponse =
        singleFlight("acceptFeedMemberInvite", feedId, feedGroupId) {
            delegate.acceptFeedMemberInvite(feedId, feedGroupId)
        }

    override suspend fun queryFeedMembers(
        feedGroupId: String,
        feedId: String,
        queryFeedMembersRequest: QueryFeedMembersRequest,
    ): QueryFeedMembersResponse =
        singleFlight("queryFeedMembers", feedGroupId, feedId, queryFeedMembersRequest) {
            delegate.queryFeedMembers(feedGroupId, feedId, queryFeedMembersRequest)
        }

    override suspend fun queryFeedMembers(
        feedGroupId: String,
        feedId: String,
    ): QueryFeedMembersResponse =
        singleFlight("queryFeedMembers", feedGroupId, feedId) {
            delegate.queryFeedMembers(feedGroupId, feedId)
        }

    override suspend fun rejectFeedMemberInvite(
        feedGroupId: String,
        feedId: String,
        rejectFeedMemberInviteRequest: RejectFeedMemberInviteRequest,
    ): RejectFeedMemberInviteResponse =
        singleFlight("rejectFeedMemberInvite", feedGroupId, feedId, rejectFeedMemberInviteRequest) {
            delegate.rejectFeedMemberInvite(feedGroupId, feedId, rejectFeedMemberInviteRequest)
        }

    override suspend fun rejectFeedMemberInvite(
        feedGroupId: String,
        feedId: String,
    ): RejectFeedMemberInviteResponse =
        singleFlight("rejectFeedMemberInvite", feedGroupId, feedId) {
            delegate.rejectFeedMemberInvite(feedGroupId, feedId)
        }

    override suspend fun stopWatchingFeed(
        feedGroupId: String,
        feedId: String,
        connectionId: String?,
    ): Response =
        singleFlight("stopWatchingFeed", feedGroupId, feedId, connectionId) {
            delegate.stopWatchingFeed(feedGroupId, feedId, connectionId)
        }

    override suspend fun getFollowSuggestions(
        feedGroupId: String,
        limit: Int?,
    ): GetFollowSuggestionsResponse =
        singleFlight("getFollowSuggestions", feedGroupId, limit) {
            delegate.getFollowSuggestions(feedGroupId, limit)
        }

    override suspend fun createFeedsBatch(
        createFeedsBatchRequest: CreateFeedsBatchRequest
    ): CreateFeedsBatchResponse =
        singleFlight("createFeedsBatch", createFeedsBatchRequest) {
            delegate.createFeedsBatch(createFeedsBatchRequest)
        }

    override suspend fun ownCapabilitiesBatch(
        connectionId: String?,
        ownCapabilitiesBatchRequest: OwnCapabilitiesBatchRequest,
    ): OwnCapabilitiesBatchResponse =
        singleFlight("ownCapabilitiesBatch", connectionId, ownCapabilitiesBatchRequest) {
            delegate.ownCapabilitiesBatch(connectionId, ownCapabilitiesBatchRequest)
        }

    override suspend fun queryFeeds(
        connectionId: String?,
        queryFeedsRequest: QueryFeedsRequest,
    ): QueryFeedsResponse =
        singleFlight("queryFeeds", connectionId, queryFeedsRequest) {
            delegate.queryFeeds(connectionId, queryFeedsRequest)
        }

    override suspend fun queryFeeds(connectionId: String?): QueryFeedsResponse =
        singleFlight("queryFeeds", connectionId) { delegate.queryFeeds(connectionId) }

    override suspend fun updateFollow(
        updateFollowRequest: UpdateFollowRequest
    ): UpdateFollowResponse =
        singleFlight("updateFollow", updateFollowRequest) {
            delegate.updateFollow(updateFollowRequest)
        }

    override suspend fun follow(followRequest: FollowRequest): SingleFollowResponse =
        singleFlight("follow", followRequest) { delegate.follow(followRequest) }

    override suspend fun acceptFollow(
        acceptFollowRequest: AcceptFollowRequest
    ): AcceptFollowResponse =
        singleFlight("acceptFollow", acceptFollowRequest) {
            delegate.acceptFollow(acceptFollowRequest)
        }

    override suspend fun followBatch(followBatchRequest: FollowBatchRequest): FollowBatchResponse =
        singleFlight("followBatch", followBatchRequest) { delegate.followBatch(followBatchRequest) }

    override suspend fun queryFollows(
        queryFollowsRequest: QueryFollowsRequest
    ): QueryFollowsResponse =
        singleFlight("queryFollows", queryFollowsRequest) {
            delegate.queryFollows(queryFollowsRequest)
        }

    override suspend fun queryFollows(): QueryFollowsResponse =
        singleFlight("queryFollows") { delegate.queryFollows() }

    override suspend fun rejectFollow(
        rejectFollowRequest: RejectFollowRequest
    ): RejectFollowResponse =
        singleFlight("rejectFollow", rejectFollowRequest) {
            delegate.rejectFollow(rejectFollowRequest)
        }

    override suspend fun unfollow(source: String, target: String): UnfollowResponse =
        singleFlight("unfollow", source, target) { delegate.unfollow(source, target) }

    override suspend fun createGuest(createGuestRequest: CreateGuestRequest): CreateGuestResponse =
        singleFlight("createGuest", createGuestRequest) { delegate.createGuest(createGuestRequest) }

    override suspend fun longPoll(connectionId: String?, json: WSAuthMessage?) {
        singleFlight("longPoll", connectionId, json) { delegate.longPoll(connectionId, json) }
    }

    override suspend fun ban(banRequest: BanRequest): BanResponse =
        singleFlight("ban", banRequest) { delegate.ban(banRequest) }

    override suspend fun upsertConfig(
        upsertConfigRequest: UpsertConfigRequest
    ): UpsertConfigResponse =
        singleFlight("upsertConfig", upsertConfigRequest) {
            delegate.upsertConfig(upsertConfigRequest)
        }

    override suspend fun deleteConfig(key: String, team: String?): DeleteModerationConfigResponse =
        singleFlight("deleteConfig", key, team) { delegate.deleteConfig(key, team) }

    override suspend fun getConfig(key: String, team: String?): GetConfigResponse =
        singleFlight("getConfig", key, team) { delegate.getConfig(key, team) }

    override suspend fun queryModerationConfigs(
        queryModerationConfigsRequest: QueryModerationConfigsRequest
    ): QueryModerationConfigsResponse =
        singleFlight("queryModerationConfigs", queryModerationConfigsRequest) {
            delegate.queryModerationConfigs(queryModerationConfigsRequest)
        }

    override suspend fun queryModerationConfigs(): QueryModerationConfigsResponse =
        singleFlight("queryModerationConfigs") { delegate.queryModerationConfigs() }

    override suspend fun flag(flagRequest: FlagRequest): FlagResponse =
        singleFlight("flag", flagRequest) { delegate.flag(flagRequest) }

    override suspend fun mute(muteRequest: MuteRequest): MuteResponse =
        singleFlight("mute", muteRequest) { delegate.mute(muteRequest) }

    override suspend fun queryReviewQueue(
        queryReviewQueueRequest: QueryReviewQueueRequest
    ): QueryReviewQueueResponse =
        singleFlight("queryReviewQueue", queryReviewQueueRequest) {
            delegate.queryReviewQueue(queryReviewQueueRequest)
        }

    override suspend fun queryReviewQueue(): QueryReviewQueueResponse =
        singleFlight("queryReviewQueue") { delegate.queryReviewQueue() }

    override suspend fun submitAction(
        submitActionRequest: SubmitActionRequest
    ): SubmitActionResponse =
        singleFlight("submitAction", submitActionRequest) {
            delegate.submitAction(submitActionRequest)
        }

    override suspend fun getOG(url: String): GetOGResponse =
        singleFlight("getOG", url) { delegate.getOG(url) }

    override suspend fun createPoll(createPollRequest: CreatePollRequest): PollResponse =
        singleFlight("createPoll", createPollRequest) { delegate.createPoll(createPollRequest) }

    override suspend fun updatePoll(updatePollRequest: UpdatePollRequest): PollResponse =
        singleFlight("updatePoll", updatePollRequest) { delegate.updatePoll(updatePollRequest) }

    override suspend fun queryPolls(
        userId: String?,
        queryPollsRequest: QueryPollsRequest,
    ): QueryPollsResponse =
        singleFlight("queryPolls", userId, queryPollsRequest) {
            delegate.queryPolls(userId, queryPollsRequest)
        }

    override suspend fun queryPolls(userId: String?): QueryPollsResponse =
        singleFlight("queryPolls", userId) { delegate.queryPolls(userId) }

    override suspend fun deletePoll(pollId: String, userId: String?): Response =
        singleFlight("deletePoll", pollId, userId) { delegate.deletePoll(pollId, userId) }

    override suspend fun getPoll(pollId: String, userId: String?): PollResponse =
        singleFlight("getPoll", pollId, userId) { delegate.getPoll(pollId, userId) }

    override suspend fun updatePollPartial(
        pollId: String,
        updatePollPartialRequest: UpdatePollPartialRequest,
    ): PollResponse =
        singleFlight("updatePollPartial", pollId, updatePollPartialRequest) {
            delegate.updatePollPartial(pollId, updatePollPartialRequest)
        }

    override suspend fun updatePollPartial(pollId: String): PollResponse =
        singleFlight("updatePollPartial", pollId) { delegate.updatePollPartial(pollId) }

    override suspend fun createPollOption(
        pollId: String,
        createPollOptionRequest: CreatePollOptionRequest,
    ): PollOptionResponse =
        singleFlight("createPollOption", pollId, createPollOptionRequest) {
            delegate.createPollOption(pollId, createPollOptionRequest)
        }

    override suspend fun updatePollOption(
        pollId: String,
        updatePollOptionRequest: UpdatePollOptionRequest,
    ): PollOptionResponse =
        singleFlight("updatePollOption", pollId, updatePollOptionRequest) {
            delegate.updatePollOption(pollId, updatePollOptionRequest)
        }

    override suspend fun deletePollOption(
        pollId: String,
        optionId: String,
        userId: String?,
    ): Response =
        singleFlight("deletePollOption", pollId, optionId, userId) {
            delegate.deletePollOption(pollId, optionId, userId)
        }

    override suspend fun getPollOption(
        pollId: String,
        optionId: String,
        userId: String?,
    ): PollOptionResponse =
        singleFlight("getPollOption", pollId, optionId, userId) {
            delegate.getPollOption(pollId, optionId, userId)
        }

    override suspend fun queryPollVotes(
        pollId: String,
        userId: String?,
        queryPollVotesRequest: QueryPollVotesRequest,
    ): PollVotesResponse =
        singleFlight("queryPollVotes", pollId, userId, queryPollVotesRequest) {
            delegate.queryPollVotes(pollId, userId, queryPollVotesRequest)
        }

    override suspend fun queryPollVotes(pollId: String, userId: String?): PollVotesResponse =
        singleFlight("queryPollVotes", pollId, userId) { delegate.queryPollVotes(pollId, userId) }

    override suspend fun updatePushNotificationPreferences(
        upsertPushPreferencesRequest: UpsertPushPreferencesRequest
    ): UpsertPushPreferencesResponse =
        singleFlight("updatePushNotificationPreferences", upsertPushPreferencesRequest) {
            delegate.updatePushNotificationPreferences(upsertPushPreferencesRequest)
        }

    override suspend fun deleteFile(url: String?): Response =
        singleFlight("deleteFile", url) { delegate.deleteFile(url) }

    override suspend fun uploadFile(fileUploadRequest: FileUploadRequest): FileUploadResponse =
        singleFlight("uploadFile", fileUploadRequest) { delegate.uploadFile(fileUploadRequest) }

    override suspend fun uploadFile(): FileUploadResponse =
        singleFlight("uploadFile") { delegate.uploadFile() }

    override suspend fun deleteImage(url: String?): Response =
        singleFlight("deleteImage", url) { delegate.deleteImage(url) }

    override suspend fun uploadImage(imageUploadRequest: ImageUploadRequest): ImageUploadResponse =
        singleFlight("uploadImage", imageUploadRequest) { delegate.uploadImage(imageUploadRequest) }

    override suspend fun uploadImage(): ImageUploadResponse =
        singleFlight("uploadImage") { delegate.uploadImage() }

    override suspend fun queryUsers(payload: QueryUsersPayload?): QueryUsersResponse =
        singleFlight("queryUsers", payload) { delegate.queryUsers(payload) }

    override suspend fun updateUsersPartial(
        updateUsersPartialRequest: UpdateUsersPartialRequest
    ): UpdateUsersResponse =
        singleFlight("updateUsersPartial", updateUsersPartialRequest) {
            delegate.updateUsersPartial(updateUsersPartialRequest)
        }

    override suspend fun updateUsers(updateUsersRequest: UpdateUsersRequest): UpdateUsersResponse =
        singleFlight("updateUsers", updateUsersRequest) { delegate.updateUsers(updateUsersRequest) }

    override suspend fun getBlockedUsers(): GetBlockedUsersResponse =
        singleFlight("getBlockedUsers") { delegate.getBlockedUsers() }

    override suspend fun blockUsers(blockUsersRequest: BlockUsersRequest): BlockUsersResponse =
        singleFlight("blockUsers", blockUsersRequest) { delegate.blockUsers(blockUsersRequest) }

    override suspend fun getUserLiveLocations(): SharedLocationsResponse =
        singleFlight("getUserLiveLocations") { delegate.getUserLiveLocations() }

    override suspend fun updateLiveLocation(
        updateLiveLocationRequest: UpdateLiveLocationRequest
    ): SharedLocationResponse =
        singleFlight("updateLiveLocation", updateLiveLocationRequest) {
            delegate.updateLiveLocation(updateLiveLocationRequest)
        }

    override suspend fun unblockUsers(
        unblockUsersRequest: UnblockUsersRequest
    ): UnblockUsersResponse =
        singleFlight("unblockUsers", unblockUsersRequest) {
            delegate.unblockUsers(unblockUsersRequest)
        }

    /**
     * Function to simplify wrapping API calls. Creates a cache key from the method name and
     * parameters, executes the block with single-flight processing.
     */
    private suspend inline fun <reified T> singleFlight(
        methodName: String,
        vararg params: Any?,
        crossinline block: suspend () -> T,
    ): T {
        val key =
            params.joinToString(separator = "-", prefix = "$methodName-").asStreamTypedKey<T>()

        return singleFlightProcessor.run(key) { block() }.getOrThrow()
    }
}
