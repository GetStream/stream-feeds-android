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
     * Get App Settings
     * This Method returns the application settings
     */
    @GET("/api/v2/app")
    suspend fun getApp(
    ): io.getstream.feeds.android.core.generated.models.GetApplicationResponse
    
    /**
     * List block lists
     * Returns all available block lists
     */
    @GET("/api/v2/blocklists")
    suspend fun listBlockLists(
        @Query("team") team: kotlin.String? = null
    ): io.getstream.feeds.android.core.generated.models.ListBlockListResponse
    
    /**
     * Create block list
     * Creates a new application blocklist, once created the blocklist can be used by any channel type
     */
    @POST("/api/v2/blocklists")
    suspend fun createBlockList(
        @Body createBlockListRequest : io.getstream.feeds.android.core.generated.models.CreateBlockListRequest
    ): io.getstream.feeds.android.core.generated.models.CreateBlockListResponse
    
    /**
     * Delete block list
     * Deletes previously created application blocklist
     */
    @DELETE("/api/v2/blocklists/{name}")
    suspend fun deleteBlockList(
        @Path("name") name: kotlin.String,
        @Query("team") team: kotlin.String? = null
    ): io.getstream.feeds.android.core.generated.models.Response
    
    /**
     * Update block list
     * Updates contents of the block list
     */
    @PUT("/api/v2/blocklists/{name}")
    suspend fun updateBlockList(
        @Path("name") name: kotlin.String ,
        @Body updateBlockListRequest : io.getstream.feeds.android.core.generated.models.UpdateBlockListRequest
    ): io.getstream.feeds.android.core.generated.models.UpdateBlockListResponse
    
    /**
     * Update block list
     * Updates contents of the block list
     */
    @PUT("/api/v2/blocklists/{name}")
    suspend fun updateBlockList(
        @Path("name") name: kotlin.String
    ): io.getstream.feeds.android.core.generated.models.UpdateBlockListResponse
    
    /**
     * Delete device
     * Deletes one device
     */
    @DELETE("/api/v2/devices")
    suspend fun deleteDevice(
        @Query("id") id: kotlin.String
    ): io.getstream.feeds.android.core.generated.models.Response
    
    /**
     * List devices
     * Returns all available devices
     */
    @GET("/api/v2/devices")
    suspend fun listDevices(
    ): io.getstream.feeds.android.core.generated.models.ListDevicesResponse
    
    /**
     * Create device
     * Adds a new device to a user, if the same device already exists the call will have no effect
     */
    @POST("/api/v2/devices")
    suspend fun createDevice(
        @Body createDeviceRequest : io.getstream.feeds.android.core.generated.models.CreateDeviceRequest
    ): io.getstream.feeds.android.core.generated.models.Response
    
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
        @Body queryActivitiesRequest : io.getstream.feeds.android.core.generated.models.QueryActivitiesRequest
    ): io.getstream.feeds.android.core.generated.models.QueryActivitiesResponse
    
    /**
     * Query activities
     * Query activities based on filters with pagination and sorting options
     */
    @POST("/api/v2/feeds/activities/query")
    suspend fun queryActivities(
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
        @Body updateActivityPartialRequest : io.getstream.feeds.android.core.generated.models.UpdateActivityPartialRequest
    ): io.getstream.feeds.android.core.generated.models.UpdateActivityPartialResponse
    
    /**
     * Partially activity update
     * Updates certain fields of the activity
     */
    @PATCH("/api/v2/feeds/activities/{activity_id}")
    suspend fun updateActivityPartial(
        @Path("activity_id") activityId: kotlin.String
    ): io.getstream.feeds.android.core.generated.models.UpdateActivityPartialResponse
    
    /**
     * Full activity update
     * Replaces an activity with the provided data
     */
    @PUT("/api/v2/feeds/activities/{activity_id}")
    suspend fun updateActivity(
        @Path("activity_id") activityId: kotlin.String ,
        @Body updateActivityRequest : io.getstream.feeds.android.core.generated.models.UpdateActivityRequest
    ): io.getstream.feeds.android.core.generated.models.UpdateActivityResponse
    
    /**
     * Full activity update
     * Replaces an activity with the provided data
     */
    @PUT("/api/v2/feeds/activities/{activity_id}")
    suspend fun updateActivity(
        @Path("activity_id") activityId: kotlin.String
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
        @Body updateBookmarkRequest : io.getstream.feeds.android.core.generated.models.UpdateBookmarkRequest
    ): io.getstream.feeds.android.core.generated.models.UpdateBookmarkResponse
    
    /**
     * Update bookmark
     * Updates a bookmark for an activity
     */
    @PATCH("/api/v2/feeds/activities/{activity_id}/bookmarks")
    suspend fun updateBookmark(
        @Path("activity_id") activityId: kotlin.String
    ): io.getstream.feeds.android.core.generated.models.UpdateBookmarkResponse
    
    /**
     * Add bookmark
     * Adds a bookmark to an activity
     */
    @POST("/api/v2/feeds/activities/{activity_id}/bookmarks")
    suspend fun addBookmark(
        @Path("activity_id") activityId: kotlin.String ,
        @Body addBookmarkRequest : io.getstream.feeds.android.core.generated.models.AddBookmarkRequest
    ): io.getstream.feeds.android.core.generated.models.AddBookmarkResponse
    
    /**
     * Add bookmark
     * Adds a bookmark to an activity
     */
    @POST("/api/v2/feeds/activities/{activity_id}/bookmarks")
    suspend fun addBookmark(
        @Path("activity_id") activityId: kotlin.String
    ): io.getstream.feeds.android.core.generated.models.AddBookmarkResponse
    
    /**
     * Cast vote
     * Cast a vote on a poll
     */
    @POST("/api/v2/feeds/activities/{activity_id}/polls/{poll_id}/vote")
    suspend fun castPollVote(
        @Path("activity_id") activityId: kotlin.String,
        @Path("poll_id") pollId: kotlin.String ,
        @Body castPollVoteRequest : io.getstream.feeds.android.core.generated.models.CastPollVoteRequest
    ): io.getstream.feeds.android.core.generated.models.PollVoteResponse
    
    /**
     * Cast vote
     * Cast a vote on a poll
     */
    @POST("/api/v2/feeds/activities/{activity_id}/polls/{poll_id}/vote")
    suspend fun castPollVote(
        @Path("activity_id") activityId: kotlin.String,
        @Path("poll_id") pollId: kotlin.String
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
        @Body queryActivityReactionsRequest : io.getstream.feeds.android.core.generated.models.QueryActivityReactionsRequest
    ): io.getstream.feeds.android.core.generated.models.QueryActivityReactionsResponse
    
    /**
     * Query activity reactions
     * Query activity reactions
     */
    @POST("/api/v2/feeds/activities/{activity_id}/reactions/query")
    suspend fun queryActivityReactions(
        @Path("activity_id") activityId: kotlin.String
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
        @Body queryBookmarkFoldersRequest : io.getstream.feeds.android.core.generated.models.QueryBookmarkFoldersRequest
    ): io.getstream.feeds.android.core.generated.models.QueryBookmarkFoldersResponse
    
    /**
     * Query bookmark folders
     * Query bookmark folders with filter query
     */
    @POST("/api/v2/feeds/bookmark_folders/query")
    suspend fun queryBookmarkFolders(
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
        @Body updateBookmarkFolderRequest : io.getstream.feeds.android.core.generated.models.UpdateBookmarkFolderRequest
    ): io.getstream.feeds.android.core.generated.models.UpdateBookmarkFolderResponse
    
    /**
     * Update a bookmark folder
     * Update a bookmark folder by its ID
     */
    @PATCH("/api/v2/feeds/bookmark_folders/{folder_id}")
    suspend fun updateBookmarkFolder(
        @Path("folder_id") folderId: kotlin.String
    ): io.getstream.feeds.android.core.generated.models.UpdateBookmarkFolderResponse
    
    /**
     * Query bookmarks
     * Query bookmarks with filter query
     */
    @POST("/api/v2/feeds/bookmarks/query")
    suspend fun queryBookmarks(
        @Body queryBookmarksRequest : io.getstream.feeds.android.core.generated.models.QueryBookmarksRequest
    ): io.getstream.feeds.android.core.generated.models.QueryBookmarksResponse
    
    /**
     * Query bookmarks
     * Query bookmarks with filter query
     */
    @POST("/api/v2/feeds/bookmarks/query")
    suspend fun queryBookmarks(
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
        @Body updateCommentRequest : io.getstream.feeds.android.core.generated.models.UpdateCommentRequest
    ): io.getstream.feeds.android.core.generated.models.UpdateCommentResponse
    
    /**
     * Update a comment
     * Updates a comment on an object (e.g., activity) and broadcasts appropriate events
     */
    @PATCH("/api/v2/feeds/comments/{comment_id}")
    suspend fun updateComment(
        @Path("comment_id") commentId: kotlin.String
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
        @Body queryCommentReactionsRequest : io.getstream.feeds.android.core.generated.models.QueryCommentReactionsRequest
    ): io.getstream.feeds.android.core.generated.models.QueryCommentReactionsResponse
    
    /**
     * Query comment reactions
     * Query comment reactions
     */
    @POST("/api/v2/feeds/comments/{comment_id}/reactions/query")
    suspend fun queryCommentReactions(
        @Path("comment_id") commentId: kotlin.String
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
        @Body getOrCreateFeedRequest : io.getstream.feeds.android.core.generated.models.GetOrCreateFeedRequest
    ): io.getstream.feeds.android.core.generated.models.GetOrCreateFeedResponse
    
    /**
     * Create a new feed
     * Create a single feed for a given feed group
     */
    @POST("/api/v2/feeds/feed_groups/{feed_group_id}/feeds/{feed_id}")
    suspend fun getOrCreateFeed(
        @Path("feed_group_id") feedGroupId: kotlin.String,
        @Path("feed_id") feedId: kotlin.String,
        @Query("connection_id") connectionId: kotlin.String? = null
    ): io.getstream.feeds.android.core.generated.models.GetOrCreateFeedResponse
    
    /**
     * Update a feed
     * Update an existing feed
     */
    @PUT("/api/v2/feeds/feed_groups/{feed_group_id}/feeds/{feed_id}")
    suspend fun updateFeed(
        @Path("feed_group_id") feedGroupId: kotlin.String,
        @Path("feed_id") feedId: kotlin.String ,
        @Body updateFeedRequest : io.getstream.feeds.android.core.generated.models.UpdateFeedRequest
    ): io.getstream.feeds.android.core.generated.models.UpdateFeedResponse
    
    /**
     * Update a feed
     * Update an existing feed
     */
    @PUT("/api/v2/feeds/feed_groups/{feed_group_id}/feeds/{feed_id}")
    suspend fun updateFeed(
        @Path("feed_group_id") feedGroupId: kotlin.String,
        @Path("feed_id") feedId: kotlin.String
    ): io.getstream.feeds.android.core.generated.models.UpdateFeedResponse
    
    /**
     * Mark activities as read/seen/watched
     * Mark activities as read/seen/watched. Can mark by timestamp (seen), activity IDs (read), or all as read.
     */
    @POST("/api/v2/feeds/feed_groups/{feed_group_id}/feeds/{feed_id}/activities/mark/batch")
    suspend fun markActivity(
        @Path("feed_group_id") feedGroupId: kotlin.String,
        @Path("feed_id") feedId: kotlin.String ,
        @Body markActivityRequest : io.getstream.feeds.android.core.generated.models.MarkActivityRequest
    ): io.getstream.feeds.android.core.generated.models.Response
    
    /**
     * Mark activities as read/seen/watched
     * Mark activities as read/seen/watched. Can mark by timestamp (seen), activity IDs (read), or all as read.
     */
    @POST("/api/v2/feeds/feed_groups/{feed_group_id}/feeds/{feed_id}/activities/mark/batch")
    suspend fun markActivity(
        @Path("feed_group_id") feedGroupId: kotlin.String,
        @Path("feed_id") feedId: kotlin.String
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
        @Body pinActivityRequest : io.getstream.feeds.android.core.generated.models.PinActivityRequest
    ): io.getstream.feeds.android.core.generated.models.PinActivityResponse
    
    /**
     * Pin an activity to a feed
     * Pin an activity to a feed. Pinned activities are typically displayed at the top of a feed.
     */
    @POST("/api/v2/feeds/feed_groups/{feed_group_id}/feeds/{feed_id}/activities/{activity_id}/pin")
    suspend fun pinActivity(
        @Path("feed_group_id") feedGroupId: kotlin.String,
        @Path("feed_id") feedId: kotlin.String,
        @Path("activity_id") activityId: kotlin.String
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
        @Body acceptFeedMemberInviteRequest : io.getstream.feeds.android.core.generated.models.AcceptFeedMemberInviteRequest
    ): io.getstream.feeds.android.core.generated.models.AcceptFeedMemberInviteResponse
    
    /**
     * Accept a feed member request
     * Accepts a pending feed member request
     */
    @POST("/api/v2/feeds/feed_groups/{feed_group_id}/feeds/{feed_id}/members/accept")
    suspend fun acceptFeedMemberInvite(
        @Path("feed_id") feedId: kotlin.String,
        @Path("feed_group_id") feedGroupId: kotlin.String
    ): io.getstream.feeds.android.core.generated.models.AcceptFeedMemberInviteResponse
    
    /**
     * Query feed members
     * Query feed members based on filters with pagination and sorting options
     */
    @POST("/api/v2/feeds/feed_groups/{feed_group_id}/feeds/{feed_id}/members/query")
    suspend fun queryFeedMembers(
        @Path("feed_group_id") feedGroupId: kotlin.String,
        @Path("feed_id") feedId: kotlin.String ,
        @Body queryFeedMembersRequest : io.getstream.feeds.android.core.generated.models.QueryFeedMembersRequest
    ): io.getstream.feeds.android.core.generated.models.QueryFeedMembersResponse
    
    /**
     * Query feed members
     * Query feed members based on filters with pagination and sorting options
     */
    @POST("/api/v2/feeds/feed_groups/{feed_group_id}/feeds/{feed_id}/members/query")
    suspend fun queryFeedMembers(
        @Path("feed_group_id") feedGroupId: kotlin.String,
        @Path("feed_id") feedId: kotlin.String
    ): io.getstream.feeds.android.core.generated.models.QueryFeedMembersResponse
    
    /**
     * Reject an invite to become a feed member
     * Rejects a pending feed member request
     */
    @POST("/api/v2/feeds/feed_groups/{feed_group_id}/feeds/{feed_id}/members/reject")
    suspend fun rejectFeedMemberInvite(
        @Path("feed_group_id") feedGroupId: kotlin.String,
        @Path("feed_id") feedId: kotlin.String ,
        @Body rejectFeedMemberInviteRequest : io.getstream.feeds.android.core.generated.models.RejectFeedMemberInviteRequest
    ): io.getstream.feeds.android.core.generated.models.RejectFeedMemberInviteResponse
    
    /**
     * Reject an invite to become a feed member
     * Rejects a pending feed member request
     */
    @POST("/api/v2/feeds/feed_groups/{feed_group_id}/feeds/{feed_id}/members/reject")
    suspend fun rejectFeedMemberInvite(
        @Path("feed_group_id") feedGroupId: kotlin.String,
        @Path("feed_id") feedId: kotlin.String
    ): io.getstream.feeds.android.core.generated.models.RejectFeedMemberInviteResponse
    
    /**
     * Stop watching feed
     * Call this Method to stop receiving feed events
     */
    @DELETE("/api/v2/feeds/feed_groups/{feed_group_id}/feeds/{feed_id}/watch")
    suspend fun stopWatchingFeed(
        @Path("feed_group_id") feedGroupId: kotlin.String,
        @Path("feed_id") feedId: kotlin.String,
        @Query("connection_id") connectionId: kotlin.String? = null
    ): io.getstream.feeds.android.core.generated.models.Response
    
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
        @Body queryFeedsRequest : io.getstream.feeds.android.core.generated.models.QueryFeedsRequest
    ): io.getstream.feeds.android.core.generated.models.QueryFeedsResponse
    
    /**
     * Query feeds
     * Query feeds with filter query
     */
    @POST("/api/v2/feeds/feeds/query")
    suspend fun feedsQueryFeeds(
        @Query("connection_id") connectionId: kotlin.String? = null
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
        @Body queryFollowsRequest : io.getstream.feeds.android.core.generated.models.QueryFollowsRequest
    ): io.getstream.feeds.android.core.generated.models.QueryFollowsResponse
    
    /**
     * Query follows
     * Query follows based on filters with pagination and sorting options
     */
    @POST("/api/v2/feeds/follows/query")
    suspend fun queryFollows(
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
    
    /**
     * Create Guest
     * 
     */
    @POST("/api/v2/guest")
    suspend fun createGuest(
        @Body createGuestRequest : io.getstream.feeds.android.core.generated.models.CreateGuestRequest
    ): io.getstream.feeds.android.core.generated.models.CreateGuestResponse
    
    /**
     * Long Poll (Transport)
     * WebSocket fallback transport endpoint
     */
    @GET("/api/v2/longpoll")
    suspend fun longPoll(
        @Query("connection_id") connectionId: kotlin.String? = null,
        @Query("json") json: io.getstream.feeds.android.core.generated.models.WSAuthMessage? = null
    )
    
    /**
     * Ban
     * Ban a user from a channel or the entire app
     */
    @POST("/api/v2/moderation/ban")
    suspend fun ban(
        @Body banRequest : io.getstream.feeds.android.core.generated.models.BanRequest
    ): io.getstream.feeds.android.core.generated.models.BanResponse
    
    /**
     * Create or update moderation configuration
     * Create a new moderation configuration or update an existing one. Configure settings for content filtering, AI analysis, toxicity detection, and other moderation features.
     */
    @POST("/api/v2/moderation/config")
    suspend fun upsertConfig(
        @Body upsertConfigRequest : io.getstream.feeds.android.core.generated.models.UpsertConfigRequest
    ): io.getstream.feeds.android.core.generated.models.UpsertConfigResponse
    
    /**
     * Delete a moderation policy
     * Delete a specific moderation policy by its name
     */
    @DELETE("/api/v2/moderation/config/{key}")
    suspend fun deleteConfig(
        @Path("key") key: kotlin.String,
        @Query("team") team: kotlin.String? = null
    ): io.getstream.feeds.android.core.generated.models.DeleteModerationConfigResponse
    
    /**
     * Get moderation configuration
     * Retrieve a specific moderation configuration by its key and team. This configuration contains settings for various moderation features like toxicity detection, AI analysis, and filtering rules.
     */
    @GET("/api/v2/moderation/config/{key}")
    suspend fun getConfig(
        @Path("key") key: kotlin.String,
        @Query("team") team: kotlin.String? = null
    ): io.getstream.feeds.android.core.generated.models.GetConfigResponse
    
    /**
     * Query moderation configurations
     * Search and filter moderation configurations across your application. This endpoint is designed for building moderation dashboards and managing multiple configuration sets.
     */
    @POST("/api/v2/moderation/configs")
    suspend fun queryModerationConfigs(
        @Body queryModerationConfigsRequest : io.getstream.feeds.android.core.generated.models.QueryModerationConfigsRequest
    ): io.getstream.feeds.android.core.generated.models.QueryModerationConfigsResponse
    
    /**
     * Query moderation configurations
     * Search and filter moderation configurations across your application. This endpoint is designed for building moderation dashboards and managing multiple configuration sets.
     */
    @POST("/api/v2/moderation/configs")
    suspend fun queryModerationConfigs(
    ): io.getstream.feeds.android.core.generated.models.QueryModerationConfigsResponse
    
    /**
     * Flag content for moderation
     * Flag any type of content (messages, users, channels, activities) for moderation review. Supports custom content types and additional metadata for flagged content.
     */
    @POST("/api/v2/moderation/flag")
    suspend fun flag(
        @Body flagRequest : io.getstream.feeds.android.core.generated.models.FlagRequest
    ): io.getstream.feeds.android.core.generated.models.FlagResponse
    
    /**
     * Mute
     * Mute a user. Mutes are generally not visible to the user you mute, while block is something you notice.
     */
    @POST("/api/v2/moderation/mute")
    suspend fun mute(
        @Body muteRequest : io.getstream.feeds.android.core.generated.models.MuteRequest
    ): io.getstream.feeds.android.core.generated.models.MuteResponse
    
    /**
     * Query review queue items
     * Query review queue items allows you to filter the review queue items. This is used for building a moderation dashboard.
     */
    @POST("/api/v2/moderation/review_queue")
    suspend fun queryReviewQueue(
        @Body queryReviewQueueRequest : io.getstream.feeds.android.core.generated.models.QueryReviewQueueRequest
    ): io.getstream.feeds.android.core.generated.models.QueryReviewQueueResponse
    
    /**
     * Query review queue items
     * Query review queue items allows you to filter the review queue items. This is used for building a moderation dashboard.
     */
    @POST("/api/v2/moderation/review_queue")
    suspend fun queryReviewQueue(
    ): io.getstream.feeds.android.core.generated.models.QueryReviewQueueResponse
    
    /**
     * Submit moderation action
     * Take action on flagged content, such as marking content as safe, deleting content, banning users, or executing custom moderation actions. Supports various action types with configurable parameters.
     */
    @POST("/api/v2/moderation/submit_action")
    suspend fun submitAction(
        @Body submitActionRequest : io.getstream.feeds.android.core.generated.models.SubmitActionRequest
    ): io.getstream.feeds.android.core.generated.models.SubmitActionResponse
    
    /**
     * Get OG
     * Get an OpenGraph attachment for a link
     */
    @GET("/api/v2/og")
    suspend fun getOG(
        @Query("url") url: kotlin.String
    ): io.getstream.feeds.android.core.generated.models.GetOGResponse
    
    /**
     * Create poll
     * Creates a new poll
     */
    @POST("/api/v2/polls")
    suspend fun createPoll(
        @Body createPollRequest : io.getstream.feeds.android.core.generated.models.CreatePollRequest
    ): io.getstream.feeds.android.core.generated.models.PollResponse
    
    /**
     * Update poll
     * Updates a poll
     */
    @PUT("/api/v2/polls")
    suspend fun updatePoll(
        @Body updatePollRequest : io.getstream.feeds.android.core.generated.models.UpdatePollRequest
    ): io.getstream.feeds.android.core.generated.models.PollResponse
    
    /**
     * Query polls
     * Queries polls
     */
    @POST("/api/v2/polls/query")
    suspend fun queryPolls(
        @Query("user_id") userId: kotlin.String? = null ,
        @Body queryPollsRequest : io.getstream.feeds.android.core.generated.models.QueryPollsRequest
    ): io.getstream.feeds.android.core.generated.models.QueryPollsResponse
    
    /**
     * Query polls
     * Queries polls
     */
    @POST("/api/v2/polls/query")
    suspend fun queryPolls(
        @Query("user_id") userId: kotlin.String? = null
    ): io.getstream.feeds.android.core.generated.models.QueryPollsResponse
    
    /**
     * Delete poll
     * Deletes a poll
     */
    @DELETE("/api/v2/polls/{poll_id}")
    suspend fun deletePoll(
        @Path("poll_id") pollId: kotlin.String,
        @Query("user_id") userId: kotlin.String? = null
    ): io.getstream.feeds.android.core.generated.models.Response
    
    /**
     * Get poll
     * Retrieves a poll
     */
    @GET("/api/v2/polls/{poll_id}")
    suspend fun getPoll(
        @Path("poll_id") pollId: kotlin.String,
        @Query("user_id") userId: kotlin.String? = null
    ): io.getstream.feeds.android.core.generated.models.PollResponse
    
    /**
     * Partial update poll
     * Updates a poll partially
     */
    @PATCH("/api/v2/polls/{poll_id}")
    suspend fun updatePollPartial(
        @Path("poll_id") pollId: kotlin.String ,
        @Body updatePollPartialRequest : io.getstream.feeds.android.core.generated.models.UpdatePollPartialRequest
    ): io.getstream.feeds.android.core.generated.models.PollResponse
    
    /**
     * Partial update poll
     * Updates a poll partially
     */
    @PATCH("/api/v2/polls/{poll_id}")
    suspend fun updatePollPartial(
        @Path("poll_id") pollId: kotlin.String
    ): io.getstream.feeds.android.core.generated.models.PollResponse
    
    /**
     * Create poll option
     * Creates a poll option
     */
    @POST("/api/v2/polls/{poll_id}/options")
    suspend fun createPollOption(
        @Path("poll_id") pollId: kotlin.String ,
        @Body createPollOptionRequest : io.getstream.feeds.android.core.generated.models.CreatePollOptionRequest
    ): io.getstream.feeds.android.core.generated.models.PollOptionResponse
    
    /**
     * Update poll option
     * Updates a poll option
     */
    @PUT("/api/v2/polls/{poll_id}/options")
    suspend fun updatePollOption(
        @Path("poll_id") pollId: kotlin.String ,
        @Body updatePollOptionRequest : io.getstream.feeds.android.core.generated.models.UpdatePollOptionRequest
    ): io.getstream.feeds.android.core.generated.models.PollOptionResponse
    
    /**
     * Delete poll option
     * Deletes a poll option
     */
    @DELETE("/api/v2/polls/{poll_id}/options/{option_id}")
    suspend fun deletePollOption(
        @Path("poll_id") pollId: kotlin.String,
        @Path("option_id") optionId: kotlin.String,
        @Query("user_id") userId: kotlin.String? = null
    ): io.getstream.feeds.android.core.generated.models.Response
    
    /**
     * Get poll option
     * Retrieves a poll option
     */
    @GET("/api/v2/polls/{poll_id}/options/{option_id}")
    suspend fun getPollOption(
        @Path("poll_id") pollId: kotlin.String,
        @Path("option_id") optionId: kotlin.String,
        @Query("user_id") userId: kotlin.String? = null
    ): io.getstream.feeds.android.core.generated.models.PollOptionResponse
    
    /**
     * Query votes
     * Queries votes
     */
    @POST("/api/v2/polls/{poll_id}/votes")
    suspend fun queryPollVotes(
        @Path("poll_id") pollId: kotlin.String,
        @Query("user_id") userId: kotlin.String? = null ,
        @Body queryPollVotesRequest : io.getstream.feeds.android.core.generated.models.QueryPollVotesRequest
    ): io.getstream.feeds.android.core.generated.models.PollVotesResponse
    
    /**
     * Query votes
     * Queries votes
     */
    @POST("/api/v2/polls/{poll_id}/votes")
    suspend fun queryPollVotes(
        @Path("poll_id") pollId: kotlin.String,
        @Query("user_id") userId: kotlin.String? = null
    ): io.getstream.feeds.android.core.generated.models.PollVotesResponse
    
    /**
     * Delete file
     * Deletes previously uploaded file
     */
    @DELETE("/api/v2/uploads/file")
    suspend fun deleteFile(
        @Query("url") url: kotlin.String? = null
    ): io.getstream.feeds.android.core.generated.models.Response
    
    /**
     * Upload file
     * Uploads file
     */
    @POST("/api/v2/uploads/file")
    suspend fun uploadFile(
        @Body fileUploadRequest : io.getstream.feeds.android.core.generated.models.FileUploadRequest
    ): io.getstream.feeds.android.core.generated.models.FileUploadResponse
    
    /**
     * Upload file
     * Uploads file
     */
    @POST("/api/v2/uploads/file")
    suspend fun uploadFile(
    ): io.getstream.feeds.android.core.generated.models.FileUploadResponse
    
    /**
     * Delete image
     * Deletes previously uploaded image
     */
    @DELETE("/api/v2/uploads/image")
    suspend fun deleteImage(
        @Query("url") url: kotlin.String? = null
    ): io.getstream.feeds.android.core.generated.models.Response
    
    /**
     * Upload image
     * Uploads image
     */
    @POST("/api/v2/uploads/image")
    suspend fun uploadImage(
        @Body imageUploadRequest : io.getstream.feeds.android.core.generated.models.ImageUploadRequest
    ): io.getstream.feeds.android.core.generated.models.ImageUploadResponse
    
    /**
     * Upload image
     * Uploads image
     */
    @POST("/api/v2/uploads/image")
    suspend fun uploadImage(
    ): io.getstream.feeds.android.core.generated.models.ImageUploadResponse
    
    /**
     * Query users
     * Find and filter users
     */
    @GET("/api/v2/users")
    suspend fun queryUsers(
        @Query("payload") payload: io.getstream.feeds.android.core.generated.models.QueryUsersPayload? = null
    ): io.getstream.feeds.android.core.generated.models.QueryUsersResponse
    
    /**
     * Partially update user
     * Updates certain fields of the user
     */
    @PATCH("/api/v2/users")
    suspend fun updateUsersPartial(
        @Body updateUsersPartialRequest : io.getstream.feeds.android.core.generated.models.UpdateUsersPartialRequest
    ): io.getstream.feeds.android.core.generated.models.UpdateUsersResponse
    
    /**
     * Upsert users
     * Update or create users in bulk
     */
    @POST("/api/v2/users")
    suspend fun updateUsers(
        @Body updateUsersRequest : io.getstream.feeds.android.core.generated.models.UpdateUsersRequest
    ): io.getstream.feeds.android.core.generated.models.UpdateUsersResponse
    
    /**
     * Get list of blocked Users
     * Get list of blocked Users
     */
    @GET("/api/v2/users/block")
    suspend fun getBlockedUsers(
    ): io.getstream.feeds.android.core.generated.models.GetBlockedUsersResponse
    
    /**
     * Block user
     * Block users
     */
    @POST("/api/v2/users/block")
    suspend fun blockUsers(
        @Body blockUsersRequest : io.getstream.feeds.android.core.generated.models.BlockUsersRequest
    ): io.getstream.feeds.android.core.generated.models.BlockUsersResponse
    
    /**
     * Get user live locations
     * Retrieves all active live locations for a user
     */
    @GET("/api/v2/users/live_locations")
    suspend fun getUserLiveLocations(
    ): io.getstream.feeds.android.core.generated.models.SharedLocationsResponse
    
    /**
     * Update live location
     * Updates an existing live location with new coordinates or expiration time
     */
    @PUT("/api/v2/users/live_locations")
    suspend fun updateLiveLocation(
        @Body updateLiveLocationRequest : io.getstream.feeds.android.core.generated.models.UpdateLiveLocationRequest
    ): io.getstream.feeds.android.core.generated.models.SharedLocationResponse
    
    /**
     * Unblock user
     * Unblock users
     */
    @POST("/api/v2/users/unblock")
    suspend fun unblockUsers(
        @Body unblockUsersRequest : io.getstream.feeds.android.core.generated.models.UnblockUsersRequest
    ): io.getstream.feeds.android.core.generated.models.UnblockUsersResponse
    
}
