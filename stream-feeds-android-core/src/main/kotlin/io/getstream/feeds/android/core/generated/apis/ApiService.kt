/*
 * Copyright (c) 2014-2024 Stream.io Inc. All rights reserved.
 *
 * Licensed under the Stream License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://github.com/GetStream/stream-video-android/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress(
    "ArrayInDataClass",
    "EnumEntryName",
    "RemoveRedundantQualifierName",
    "UnusedImport"
)

package io.getstream.feeds.android.core.generated.apis

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.PUT

interface ApiService {
    
    /**
     * Add a single activity
     * Create a new activity or update an existing one
     */
    @POST("/api/v2/feeds/activities")
    suspend fun addActivity(
        @Body addActivityRequest : io.getstream.feeds.android.core.generated.models.AddActivityRequest
    ): io.getstream.feeds.android.core.generated.models.AddActivityResponse
    
    /**
     * Upsert multiple activities
     * Create new activities or update existing ones in a batch operation
     */
    @POST("/api/v2/feeds/activities/batch")
    suspend fun upsertActivities(
        @Body upsertActivitiesRequest : io.getstream.feeds.android.core.generated.models.UpsertActivitiesRequest
    ): io.getstream.feeds.android.core.generated.models.UpsertActivitiesResponse
    
    /**
     * Remove multiple activities
     * Delete one or more activities by their IDs
     */
    @POST("/api/v2/feeds/activities/delete")
    suspend fun deleteActivities(
        @Body deleteActivitiesRequest : io.getstream.feeds.android.core.generated.models.DeleteActivitiesRequest
    ): io.getstream.feeds.android.core.generated.models.DeleteActivitiesResponse
    
    /**
     * Query activities
     * Query activities based on filters with pagination and sorting options
     */
    @POST("/api/v2/feeds/activities/query")
    suspend fun queryActivities(
        @Body queryActivitiesRequest : io.getstream.feeds.android.core.generated.models.QueryActivitiesRequest? = null
    ): io.getstream.feeds.android.core.generated.models.QueryActivitiesResponse
    
    /**
     * Delete a single activity
     * Delete a single activity by its ID
     */
    @DELETE("/api/v2/feeds/activities/{activity_id}")
    suspend fun deleteActivity(
        @Path("activity_id") activityId: kotlin.String,
        @Query("hard_delete") hardDelete: kotlin.Boolean? = null
    ): io.getstream.feeds.android.core.generated.models.DeleteActivityResponse
    
    /**
     * Get activity
     * Returns activity by ID
     */
    @GET("/api/v2/feeds/activities/{activity_id}")
    suspend fun getActivity(
        @Path("activity_id") activityId: kotlin.String
    ): io.getstream.feeds.android.core.generated.models.GetActivityResponse
    
    /**
     * Partially activity update
     * Updates certain fields of the activity
     */
    @PATCH("/api/v2/feeds/activities/{activity_id}")
    suspend fun updateActivityPartial(
        @Path("activity_id") activityId: kotlin.String ,
        @Body updateActivityPartialRequest : io.getstream.feeds.android.core.generated.models.UpdateActivityPartialRequest? = null
    ): io.getstream.feeds.android.core.generated.models.UpdateActivityPartialResponse
    
    /**
     * Full activity update
     * Replaces an activity with the provided data
     */
    @PUT("/api/v2/feeds/activities/{activity_id}")
    suspend fun updateActivity(
        @Path("activity_id") activityId: kotlin.String ,
        @Body updateActivityRequest : io.getstream.feeds.android.core.generated.models.UpdateActivityRequest? = null
    ): io.getstream.feeds.android.core.generated.models.UpdateActivityResponse
    
    /**
     * Delete a bookmark
     * Deletes a bookmark from an activity
     */
    @DELETE("/api/v2/feeds/activities/{activity_id}/bookmarks")
    suspend fun deleteBookmark(
        @Path("activity_id") activityId: kotlin.String,
        @Query("folder_id") folderId: kotlin.String? = null
    ): io.getstream.feeds.android.core.generated.models.DeleteBookmarkResponse
    
    /**
     * Update bookmark
     * Updates a bookmark for an activity
     */
    @PATCH("/api/v2/feeds/activities/{activity_id}/bookmarks")
    suspend fun updateBookmark(
        @Path("activity_id") activityId: kotlin.String ,
        @Body updateBookmarkRequest : io.getstream.feeds.android.core.generated.models.UpdateBookmarkRequest? = null
    ): io.getstream.feeds.android.core.generated.models.UpdateBookmarkResponse
    
    /**
     * Add bookmark
     * Adds a bookmark to an activity
     */
    @POST("/api/v2/feeds/activities/{activity_id}/bookmarks")
    suspend fun addBookmark(
        @Path("activity_id") activityId: kotlin.String ,
        @Body addBookmarkRequest : io.getstream.feeds.android.core.generated.models.AddBookmarkRequest? = null
    ): io.getstream.feeds.android.core.generated.models.AddBookmarkResponse
    
    /**
     * Cast vote
     * Cast a vote on a poll
     */
    @POST("/api/v2/feeds/activities/{activity_id}/polls/{poll_id}/vote")
    suspend fun castPollVote(
        @Path("activity_id") activityId: kotlin.String,
        @Path("poll_id") pollId: kotlin.String ,
        @Body castPollVoteRequest : io.getstream.feeds.android.core.generated.models.CastPollVoteRequest? = null
    ): io.getstream.feeds.android.core.generated.models.PollVoteResponse
    
    /**
     * Delete vote
     * Delete a vote from a poll
     */
    @DELETE("/api/v2/feeds/activities/{activity_id}/polls/{poll_id}/vote/{vote_id}")
    suspend fun deletePollVote(
        @Path("activity_id") activityId: kotlin.String,
        @Path("poll_id") pollId: kotlin.String,
        @Path("vote_id") voteId: kotlin.String,
        @Query("user_id") userId: kotlin.String? = null
    ): io.getstream.feeds.android.core.generated.models.PollVoteResponse
    
    /**
     * Add reaction
     * Adds a reaction to an activity
     */
    @POST("/api/v2/feeds/activities/{activity_id}/reactions")
    suspend fun addReaction(
        @Path("activity_id") activityId: kotlin.String ,
        @Body addReactionRequest : io.getstream.feeds.android.core.generated.models.AddReactionRequest
    ): io.getstream.feeds.android.core.generated.models.AddReactionResponse
    
    /**
     * Query activity reactions
     * Query activity reactions
     */
    @POST("/api/v2/feeds/activities/{activity_id}/reactions/query")
    suspend fun queryActivityReactions(
        @Path("activity_id") activityId: kotlin.String ,
        @Body queryActivityReactionsRequest : io.getstream.feeds.android.core.generated.models.QueryActivityReactionsRequest? = null
    ): io.getstream.feeds.android.core.generated.models.QueryActivityReactionsResponse
    
    /**
     * Remove reaction
     * Removes a reaction from an activity
     */
    @DELETE("/api/v2/feeds/activities/{activity_id}/reactions/{type}")
    suspend fun deleteActivityReaction(
        @Path("activity_id") activityId: kotlin.String,
        @Path("type") type: kotlin.String
    ): io.getstream.feeds.android.core.generated.models.DeleteActivityReactionResponse
    
    /**
     * Query bookmark folders
     * Query bookmark folders with filter query
     */
    @POST("/api/v2/feeds/bookmark_folders/query")
    suspend fun queryBookmarkFolders(
        @Body queryBookmarkFoldersRequest : io.getstream.feeds.android.core.generated.models.QueryBookmarkFoldersRequest? = null
    ): io.getstream.feeds.android.core.generated.models.QueryBookmarkFoldersResponse
    
    /**
     * Delete a bookmark folder
     * Delete a bookmark folder by its ID
     */
    @DELETE("/api/v2/feeds/bookmark_folders/{folder_id}")
    suspend fun deleteBookmarkFolder(
        @Path("folder_id") folderId: kotlin.String
    ): io.getstream.feeds.android.core.generated.models.DeleteBookmarkFolderResponse
    
    /**
     * Update a bookmark folder
     * Update a bookmark folder by its ID
     */
    @PATCH("/api/v2/feeds/bookmark_folders/{folder_id}")
    suspend fun updateBookmarkFolder(
        @Path("folder_id") folderId: kotlin.String ,
        @Body updateBookmarkFolderRequest : io.getstream.feeds.android.core.generated.models.UpdateBookmarkFolderRequest? = null
    ): io.getstream.feeds.android.core.generated.models.UpdateBookmarkFolderResponse
    
    /**
     * Query bookmarks
     * Query bookmarks with filter query
     */
    @POST("/api/v2/feeds/bookmarks/query")
    suspend fun queryBookmarks(
        @Body queryBookmarksRequest : io.getstream.feeds.android.core.generated.models.QueryBookmarksRequest? = null
    ): io.getstream.feeds.android.core.generated.models.QueryBookmarksResponse
    
    /**
     * Get comments for an object
     * Retrieve a threaded list of comments for a specific object (e.g., activity), with configurable depth, sorting, and pagination
     */
    @GET("/api/v2/feeds/comments")
    suspend fun getComments(
        @Query("object_id") objectId: kotlin.String,
        @Query("object_type") objectType: kotlin.String,
        @Query("depth") depth: kotlin.Int? = null,
        @Query("sort") sort: kotlin.String? = null,
        @Query("replies_limit") repliesLimit: kotlin.Int? = null,
        @Query("limit") limit: kotlin.Int? = null,
        @Query("prev") prev: kotlin.String? = null,
        @Query("next") next: kotlin.String? = null
    ): io.getstream.feeds.android.core.generated.models.GetCommentsResponse
    
    /**
     * Add a comment or reply
     * Adds a comment to an object (e.g., activity) or a reply to an existing comment, and broadcasts appropriate events
     */
    @POST("/api/v2/feeds/comments")
    suspend fun addComment(
        @Body addCommentRequest : io.getstream.feeds.android.core.generated.models.AddCommentRequest
    ): io.getstream.feeds.android.core.generated.models.AddCommentResponse
    
    /**
     * Add multiple comments in a batch
     * Adds multiple comments in a single request. Each comment must specify the object type and ID.
     */
    @POST("/api/v2/feeds/comments/batch")
    suspend fun addCommentsBatch(
        @Body addCommentsBatchRequest : io.getstream.feeds.android.core.generated.models.AddCommentsBatchRequest
    ): io.getstream.feeds.android.core.generated.models.AddCommentsBatchResponse
    
    /**
     * Query comments
     * Query comments using MongoDB-style filters with pagination and sorting options
     */
    @POST("/api/v2/feeds/comments/query")
    suspend fun queryComments(
        @Body queryCommentsRequest : io.getstream.feeds.android.core.generated.models.QueryCommentsRequest
    ): io.getstream.feeds.android.core.generated.models.QueryCommentsResponse
    
    /**
     * Delete a comment
     * Deletes a comment from an object (e.g., activity) and broadcasts appropriate events
     */
    @DELETE("/api/v2/feeds/comments/{comment_id}")
    suspend fun deleteComment(
        @Path("comment_id") commentId: kotlin.String
    ): io.getstream.feeds.android.core.generated.models.DeleteCommentResponse
    
    /**
     * Get comment
     * Get a comment by ID
     */
    @GET("/api/v2/feeds/comments/{comment_id}")
    suspend fun getComment(
        @Path("comment_id") commentId: kotlin.String
    ): io.getstream.feeds.android.core.generated.models.GetCommentResponse
    
    /**
     * Update a comment
     * Updates a comment on an object (e.g., activity) and broadcasts appropriate events
     */
    @PATCH("/api/v2/feeds/comments/{comment_id}")
    suspend fun updateComment(
        @Path("comment_id") commentId: kotlin.String ,
        @Body updateCommentRequest : io.getstream.feeds.android.core.generated.models.UpdateCommentRequest? = null
    ): io.getstream.feeds.android.core.generated.models.UpdateCommentResponse
    
    /**
     * Add comment reaction
     * Adds a reaction to a comment
     */
    @POST("/api/v2/feeds/comments/{comment_id}/reactions")
    suspend fun addCommentReaction(
        @Path("comment_id") commentId: kotlin.String ,
        @Body addCommentReactionRequest : io.getstream.feeds.android.core.generated.models.AddCommentReactionRequest
    ): io.getstream.feeds.android.core.generated.models.AddCommentReactionResponse
    
    /**
     * Query comment reactions
     * Query comment reactions
     */
    @POST("/api/v2/feeds/comments/{comment_id}/reactions/query")
    suspend fun queryCommentReactions(
        @Path("comment_id") commentId: kotlin.String ,
        @Body queryCommentReactionsRequest : io.getstream.feeds.android.core.generated.models.QueryCommentReactionsRequest? = null
    ): io.getstream.feeds.android.core.generated.models.QueryCommentReactionsResponse
    
    /**
     * Delete comment reaction
     * Deletes a reaction from a comment
     */
    @DELETE("/api/v2/feeds/comments/{comment_id}/reactions/{type}")
    suspend fun deleteCommentReaction(
        @Path("comment_id") commentId: kotlin.String,
        @Path("type") type: kotlin.String
    ): io.getstream.feeds.android.core.generated.models.DeleteCommentReactionResponse
    
    /**
     * Get replies for a comment
     * Retrieve a threaded list of replies for a single comment, with configurable depth, sorting, and pagination
     */
    @GET("/api/v2/feeds/comments/{comment_id}/replies")
    suspend fun getCommentReplies(
        @Path("comment_id") commentId: kotlin.String,
        @Query("depth") depth: kotlin.Int? = null,
        @Query("sort") sort: kotlin.String? = null,
        @Query("replies_limit") repliesLimit: kotlin.Int? = null,
        @Query("limit") limit: kotlin.Int? = null,
        @Query("prev") prev: kotlin.String? = null,
        @Query("next") next: kotlin.String? = null
    ): io.getstream.feeds.android.core.generated.models.GetCommentRepliesResponse
    
    /**
     * Delete a single feed
     * Delete a single feed by its ID
     */
    @DELETE("/api/v2/feeds/feed_groups/{feed_group_id}/feeds/{feed_id}")
    suspend fun deleteFeed(
        @Path("feed_group_id") feedGroupId: kotlin.String,
        @Path("feed_id") feedId: kotlin.String,
        @Query("hard_delete") hardDelete: kotlin.Boolean? = null
    ): io.getstream.feeds.android.core.generated.models.DeleteFeedResponse
    
    /**
     * Create a new feed
     * Create a single feed for a given feed group
     */
    @POST("/api/v2/feeds/feed_groups/{feed_group_id}/feeds/{feed_id}")
    suspend fun getOrCreateFeed(
        @Path("feed_group_id") feedGroupId: kotlin.String,
        @Path("feed_id") feedId: kotlin.String,
        @Query("connection_id") connectionId: kotlin.String? = null ,
        @Body getOrCreateFeedRequest : io.getstream.feeds.android.core.generated.models.GetOrCreateFeedRequest? = null
    ): io.getstream.feeds.android.core.generated.models.GetOrCreateFeedResponse
    
    /**
     * Update a feed
     * Update an existing feed
     */
    @PUT("/api/v2/feeds/feed_groups/{feed_group_id}/feeds/{feed_id}")
    suspend fun updateFeed(
        @Path("feed_group_id") feedGroupId: kotlin.String,
        @Path("feed_id") feedId: kotlin.String ,
        @Body updateFeedRequest : io.getstream.feeds.android.core.generated.models.UpdateFeedRequest? = null
    ): io.getstream.feeds.android.core.generated.models.UpdateFeedResponse
    
    /**
     * Mark activities as read/seen/watched
     * Mark activities as read/seen/watched. Can mark by timestamp (seen), activity IDs (read), or all as read.
     */
    @POST("/api/v2/feeds/feed_groups/{feed_group_id}/feeds/{feed_id}/activities/mark/batch")
    suspend fun markActivity(
        @Path("feed_group_id") feedGroupId: kotlin.String,
        @Path("feed_id") feedId: kotlin.String ,
        @Body markActivityRequest : io.getstream.feeds.android.core.generated.models.MarkActivityRequest? = null
    ): io.getstream.feeds.android.core.generated.models.Response
    
    /**
     * Unpin an activity from a feed
     * Unpin an activity from a feed. This removes the pin, so the activity will no longer be displayed at the top of the feed.
     */
    @DELETE("/api/v2/feeds/feed_groups/{feed_group_id}/feeds/{feed_id}/activities/{activity_id}/pin")
    suspend fun unpinActivity(
        @Path("feed_group_id") feedGroupId: kotlin.String,
        @Path("feed_id") feedId: kotlin.String,
        @Path("activity_id") activityId: kotlin.String
    ): io.getstream.feeds.android.core.generated.models.UnpinActivityResponse
    
    /**
     * Pin an activity to a feed
     * Pin an activity to a feed. Pinned activities are typically displayed at the top of a feed.
     */
    @POST("/api/v2/feeds/feed_groups/{feed_group_id}/feeds/{feed_id}/activities/{activity_id}/pin")
    suspend fun pinActivity(
        @Path("feed_group_id") feedGroupId: kotlin.String,
        @Path("feed_id") feedId: kotlin.String,
        @Path("activity_id") activityId: kotlin.String ,
        @Body pinActivityRequest : io.getstream.feeds.android.core.generated.models.PinActivityRequest? = null
    ): io.getstream.feeds.android.core.generated.models.PinActivityResponse
    
    /**
     * Update feed members
     * Add, remove, or set members for a feed
     */
    @PATCH("/api/v2/feeds/feed_groups/{feed_group_id}/feeds/{feed_id}/members")
    suspend fun updateFeedMembers(
        @Path("feed_group_id") feedGroupId: kotlin.String,
        @Path("feed_id") feedId: kotlin.String ,
        @Body updateFeedMembersRequest : io.getstream.feeds.android.core.generated.models.UpdateFeedMembersRequest
    ): io.getstream.feeds.android.core.generated.models.UpdateFeedMembersResponse
    
    /**
     * Accept a feed member request
     * Accepts a pending feed member request
     */
    @POST("/api/v2/feeds/feed_groups/{feed_group_id}/feeds/{feed_id}/members/accept")
    suspend fun acceptFeedMemberInvite(
        @Path("feed_id") feedId: kotlin.String,
        @Path("feed_group_id") feedGroupId: kotlin.String ,
        @Body acceptFeedMemberInviteRequest : io.getstream.feeds.android.core.generated.models.AcceptFeedMemberInviteRequest? = null
    ): io.getstream.feeds.android.core.generated.models.AcceptFeedMemberInviteResponse
    
    /**
     * Query feed members
     * Query feed members based on filters with pagination and sorting options
     */
    @POST("/api/v2/feeds/feed_groups/{feed_group_id}/feeds/{feed_id}/members/query")
    suspend fun queryFeedMembers(
        @Path("feed_group_id") feedGroupId: kotlin.String,
        @Path("feed_id") feedId: kotlin.String ,
        @Body queryFeedMembersRequest : io.getstream.feeds.android.core.generated.models.QueryFeedMembersRequest? = null
    ): io.getstream.feeds.android.core.generated.models.QueryFeedMembersResponse
    
    /**
     * Reject an invite to become a feed member
     * Rejects a pending feed member request
     */
    @POST("/api/v2/feeds/feed_groups/{feed_group_id}/feeds/{feed_id}/members/reject")
    suspend fun rejectFeedMemberInvite(
        @Path("feed_group_id") feedGroupId: kotlin.String,
        @Path("feed_id") feedId: kotlin.String ,
        @Body rejectFeedMemberInviteRequest : io.getstream.feeds.android.core.generated.models.RejectFeedMemberInviteRequest? = null
    ): io.getstream.feeds.android.core.generated.models.RejectFeedMemberInviteResponse
    
    /**
     * Get follow suggestions
     * Get follow suggestions for a feed group
     */
    @GET("/api/v2/feeds/feed_groups/{feed_group_id}/follow_suggestions")
    suspend fun getFollowSuggestions(
        @Path("feed_group_id") feedGroupId: kotlin.String,
        @Query("limit") limit: kotlin.Int? = null
    ): io.getstream.feeds.android.core.generated.models.GetFollowSuggestionsResponse
    
    /**
     * Create multiple feeds at once
     * Create multiple feeds at once for a given feed group
     */
    @POST("/api/v2/feeds/feeds/batch")
    suspend fun createFeedsBatch(
        @Body createFeedsBatchRequest : io.getstream.feeds.android.core.generated.models.CreateFeedsBatchRequest
    ): io.getstream.feeds.android.core.generated.models.CreateFeedsBatchResponse
    
    /**
     * Query feeds
     * Query feeds with filter query
     */
    @POST("/api/v2/feeds/feeds/query")
    suspend fun feedsQueryFeeds(
        @Query("connection_id") connectionId: kotlin.String? = null ,
        @Body queryFeedsRequest : io.getstream.feeds.android.core.generated.models.QueryFeedsRequest? = null
    ): io.getstream.feeds.android.core.generated.models.QueryFeedsResponse
    
    /**
     * Update a follow
     * Updates a follow's custom data, push preference, and follower role. Source owner can update custom data and push preference. Target owner can update follower role.
     */
    @PATCH("/api/v2/feeds/follows")
    suspend fun updateFollow(
        @Body updateFollowRequest : io.getstream.feeds.android.core.generated.models.UpdateFollowRequest
    ): io.getstream.feeds.android.core.generated.models.UpdateFollowResponse
    
    /**
     * Create a follow
     * Creates a follow and broadcasts FollowAddedEvent
     */
    @POST("/api/v2/feeds/follows")
    suspend fun follow(
        @Body singleFollowRequest : io.getstream.feeds.android.core.generated.models.SingleFollowRequest
    ): io.getstream.feeds.android.core.generated.models.SingleFollowResponse
    
    /**
     * Accept a follow request
     * Accepts a pending follow request
     */
    @POST("/api/v2/feeds/follows/accept")
    suspend fun acceptFollow(
        @Body acceptFollowRequest : io.getstream.feeds.android.core.generated.models.AcceptFollowRequest
    ): io.getstream.feeds.android.core.generated.models.AcceptFollowResponse
    
    /**
     * Create multiple follows at once
     * Creates multiple follows at once and broadcasts FollowAddedEvent for each follow
     */
    @POST("/api/v2/feeds/follows/batch")
    suspend fun followBatch(
        @Body followBatchRequest : io.getstream.feeds.android.core.generated.models.FollowBatchRequest
    ): io.getstream.feeds.android.core.generated.models.FollowBatchResponse
    
    /**
     * Query follows
     * Query follows based on filters with pagination and sorting options
     */
    @POST("/api/v2/feeds/follows/query")
    suspend fun queryFollows(
        @Body queryFollowsRequest : io.getstream.feeds.android.core.generated.models.QueryFollowsRequest? = null
    ): io.getstream.feeds.android.core.generated.models.QueryFollowsResponse
    
    /**
     * Reject a follow request
     * Rejects a pending follow request
     */
    @POST("/api/v2/feeds/follows/reject")
    suspend fun rejectFollow(
        @Body rejectFollowRequest : io.getstream.feeds.android.core.generated.models.RejectFollowRequest
    ): io.getstream.feeds.android.core.generated.models.RejectFollowResponse
    
    /**
     * Unfollow a feed
     * Removes a follow and broadcasts FollowRemovedEvent
     */
    @DELETE("/api/v2/feeds/follows/{source}/{target}")
    suspend fun unfollow(
        @Path("source") source: kotlin.String,
        @Path("target") target: kotlin.String
    ): io.getstream.feeds.android.core.generated.models.UnfollowResponse
    
}
