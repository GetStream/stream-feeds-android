/*
 * Copyright (c) 2014-2026 Stream.io Inc. All rights reserved.
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

@file:Suppress("ArrayInDataClass", "EnumEntryName", "RemoveRedundantQualifierName", "UnusedImport")

package io.getstream.feeds.android.network.apis

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

public interface FeedsApi {

    /** Get App Settings This Method returns the application settings */
    @GET("/api/v2/app")
    public suspend fun getApp(): io.getstream.feeds.android.network.models.GetApplicationResponse

    /** List block lists Returns all available block lists */
    @GET("/api/v2/blocklists")
    public suspend fun listBlockLists(
        @Query("team") team: String? = null,
        @Query("cursor") cursor: String? = null,
        @Query("limit") limit: Int? = null,
    ): io.getstream.feeds.android.network.models.ListBlockListResponse

    /**
     * Create block list Creates a new application blocklist, once created the blocklist can be used
     * by any channel type
     */
    @POST("/api/v2/blocklists")
    public suspend fun createBlockList(
        @Body
        createBlockListRequest: io.getstream.feeds.android.network.models.CreateBlockListRequest
    ): io.getstream.feeds.android.network.models.CreateBlockListResponse

    /** Delete block list Deletes previously created application blocklist */
    @DELETE("/api/v2/blocklists/{name}")
    public suspend fun deleteBlockList(
        @Path("name") name: String,
        @Query("team") team: String? = null,
    ): io.getstream.feeds.android.network.models.Response

    /** Update block list Updates contents of the block list */
    @PUT("/api/v2/blocklists/{name}")
    public suspend fun updateBlockList(
        @Path("name") name: String,
        @Body
        updateBlockListRequest: io.getstream.feeds.android.network.models.UpdateBlockListRequest,
    ): io.getstream.feeds.android.network.models.UpdateBlockListResponse

    /** Update block list Updates contents of the block list */
    @PUT("/api/v2/blocklists/{name}")
    public suspend fun updateBlockList(
        @Path("name") name: String
    ): io.getstream.feeds.android.network.models.UpdateBlockListResponse

    /** Delete device Deletes one device */
    @DELETE("/api/v2/devices")
    public suspend fun deleteDevice(
        @Query("id") id: String
    ): io.getstream.feeds.android.network.models.Response

    /** List devices Returns all available devices */
    @GET("/api/v2/devices")
    public suspend fun listDevices(): io.getstream.feeds.android.network.models.ListDevicesResponse

    /**
     * Create device Adds a new device to a user, if the same device already exists the call will
     * have no effect
     */
    @POST("/api/v2/devices")
    public suspend fun createDevice(
        @Body createDeviceRequest: io.getstream.feeds.android.network.models.CreateDeviceRequest
    ): io.getstream.feeds.android.network.models.Response

    /** Add a single activity Create a new activity or update an existing one */
    @POST("/api/v2/feeds/activities")
    public suspend fun addActivity(
        @Body addActivityRequest: io.getstream.feeds.android.network.models.AddActivityRequest
    ): io.getstream.feeds.android.network.models.AddActivityResponse

    /**
     * Upsert multiple activities Create new activities or update existing ones in a batch operation
     */
    @POST("/api/v2/feeds/activities/batch")
    public suspend fun upsertActivities(
        @Body
        upsertActivitiesRequest: io.getstream.feeds.android.network.models.UpsertActivitiesRequest
    ): io.getstream.feeds.android.network.models.UpsertActivitiesResponse

    /** Remove multiple activities Delete one or more activities by their IDs */
    @POST("/api/v2/feeds/activities/delete")
    public suspend fun deleteActivities(
        @Body
        deleteActivitiesRequest: io.getstream.feeds.android.network.models.DeleteActivitiesRequest
    ): io.getstream.feeds.android.network.models.DeleteActivitiesResponse

    /**
     * Track activity metrics Track metric events (views, clicks, impressions) for activities.
     * Supports batching up to 100 events per request. Each event is independently rate-limited per
     * user per activity per metric. Server-side calls must include user_id.
     */
    @POST("/api/v2/feeds/activities/metrics/track")
    public suspend fun trackActivityMetrics(
        @Body
        trackActivityMetricsRequest:
            io.getstream.feeds.android.network.models.TrackActivityMetricsRequest
    ): io.getstream.feeds.android.network.models.TrackActivityMetricsResponse

    /** Query activities Query activities based on filters with pagination and sorting options */
    @POST("/api/v2/feeds/activities/query")
    public suspend fun queryActivities(
        @Query("language") language: String? = null,
        @Query("translate_text") translateText: Boolean? = null,
        @Body
        queryActivitiesRequest: io.getstream.feeds.android.network.models.QueryActivitiesRequest,
    ): io.getstream.feeds.android.network.models.QueryActivitiesResponse

    /** Query activities Query activities based on filters with pagination and sorting options */
    @POST("/api/v2/feeds/activities/query")
    public suspend fun queryActivities(
        @Query("language") language: String? = null,
        @Query("translate_text") translateText: Boolean? = null,
    ): io.getstream.feeds.android.network.models.QueryActivitiesResponse

    /**
     * Batch query activity reactions Returns a single user's reactions across a set of activity
     * IDs, without activity payloads
     */
    @POST("/api/v2/feeds/activities/reactions/query")
    public suspend fun batchQueryActivityReactions(
        @Body
        batchQueryActivityReactionsRequest:
            io.getstream.feeds.android.network.models.BatchQueryActivityReactionsRequest
    ): io.getstream.feeds.android.network.models.BatchQueryActivityReactionsResponse

    /** Delete a bookmark Deletes a bookmark from an activity */
    @DELETE("/api/v2/feeds/activities/{activity_id}/bookmarks")
    public suspend fun deleteBookmark(
        @Path("activity_id") activityId: String,
        @Query("folder_id") folderId: String? = null,
    ): io.getstream.feeds.android.network.models.DeleteBookmarkResponse

    /** Update bookmark Updates a bookmark for an activity */
    @PATCH("/api/v2/feeds/activities/{activity_id}/bookmarks")
    public suspend fun updateBookmark(
        @Path("activity_id") activityId: String,
        @Body updateBookmarkRequest: io.getstream.feeds.android.network.models.UpdateBookmarkRequest,
    ): io.getstream.feeds.android.network.models.UpdateBookmarkResponse

    /** Update bookmark Updates a bookmark for an activity */
    @PATCH("/api/v2/feeds/activities/{activity_id}/bookmarks")
    public suspend fun updateBookmark(
        @Path("activity_id") activityId: String
    ): io.getstream.feeds.android.network.models.UpdateBookmarkResponse

    /** Add bookmark Adds a bookmark to an activity */
    @POST("/api/v2/feeds/activities/{activity_id}/bookmarks")
    public suspend fun addBookmark(
        @Path("activity_id") activityId: String,
        @Body addBookmarkRequest: io.getstream.feeds.android.network.models.AddBookmarkRequest,
    ): io.getstream.feeds.android.network.models.AddBookmarkResponse

    /** Add bookmark Adds a bookmark to an activity */
    @POST("/api/v2/feeds/activities/{activity_id}/bookmarks")
    public suspend fun addBookmark(
        @Path("activity_id") activityId: String
    ): io.getstream.feeds.android.network.models.AddBookmarkResponse

    /**
     * Provide feedback on an activity Submit feedback for an activity including options to show
     * less, hide, report, or mute the user
     */
    @POST("/api/v2/feeds/activities/{activity_id}/feedback")
    public suspend fun activityFeedback(
        @Path("activity_id") activityId: String,
        @Body
        activityFeedbackRequest: io.getstream.feeds.android.network.models.ActivityFeedbackRequest,
    ): io.getstream.feeds.android.network.models.ActivityFeedbackResponse

    /**
     * Provide feedback on an activity Submit feedback for an activity including options to show
     * less, hide, report, or mute the user
     */
    @POST("/api/v2/feeds/activities/{activity_id}/feedback")
    public suspend fun activityFeedback(
        @Path("activity_id") activityId: String
    ): io.getstream.feeds.android.network.models.ActivityFeedbackResponse

    /** Cast vote Cast a vote on a poll */
    @POST("/api/v2/feeds/activities/{activity_id}/polls/{poll_id}/vote")
    public suspend fun castPollVote(
        @Path("activity_id") activityId: String,
        @Path("poll_id") pollId: String,
        @Body castPollVoteRequest: io.getstream.feeds.android.network.models.CastPollVoteRequest,
    ): io.getstream.feeds.android.network.models.PollVoteResponse

    /** Cast vote Cast a vote on a poll */
    @POST("/api/v2/feeds/activities/{activity_id}/polls/{poll_id}/vote")
    public suspend fun castPollVote(
        @Path("activity_id") activityId: String,
        @Path("poll_id") pollId: String,
    ): io.getstream.feeds.android.network.models.PollVoteResponse

    /** Delete vote Delete a vote from a poll */
    @DELETE("/api/v2/feeds/activities/{activity_id}/polls/{poll_id}/vote/{vote_id}")
    public suspend fun deletePollVote(
        @Path("activity_id") activityId: String,
        @Path("poll_id") pollId: String,
        @Path("vote_id") voteId: String,
    ): io.getstream.feeds.android.network.models.PollVoteResponse

    /** Add reaction Adds a reaction to an activity */
    @POST("/api/v2/feeds/activities/{activity_id}/reactions")
    public suspend fun addActivityReaction(
        @Path("activity_id") activityId: String,
        @Body addReactionRequest: io.getstream.feeds.android.network.models.AddReactionRequest,
    ): io.getstream.feeds.android.network.models.AddReactionResponse

    /** Query activity reactions Query activity reactions */
    @POST("/api/v2/feeds/activities/{activity_id}/reactions/query")
    public suspend fun queryActivityReactions(
        @Path("activity_id") activityId: String,
        @Body
        queryActivityReactionsRequest:
            io.getstream.feeds.android.network.models.QueryActivityReactionsRequest,
    ): io.getstream.feeds.android.network.models.QueryActivityReactionsResponse

    /** Query activity reactions Query activity reactions */
    @POST("/api/v2/feeds/activities/{activity_id}/reactions/query")
    public suspend fun queryActivityReactions(
        @Path("activity_id") activityId: String
    ): io.getstream.feeds.android.network.models.QueryActivityReactionsResponse

    /** Remove reaction Removes a reaction from an activity */
    @DELETE("/api/v2/feeds/activities/{activity_id}/reactions/{type}")
    public suspend fun deleteActivityReaction(
        @Path("activity_id") activityId: String,
        @Path("type") type: String,
        @Query("delete_notification_activity") deleteNotificationActivity: Boolean? = null,
    ): io.getstream.feeds.android.network.models.DeleteActivityReactionResponse

    /** Query activity shares List the shares recorded for an activity, newest-first */
    @GET("/api/v2/feeds/activities/{activity_id}/shares")
    public suspend fun queryActivityShares(
        @Path("activity_id") activityId: String,
        @Query("limit") limit: Int? = null,
        @Query("prev") prev: String? = null,
        @Query("next") next: String? = null,
    ): io.getstream.feeds.android.network.models.QueryActivitySharesResponse

    /** Delete a single activity Delete a single activity by its ID */
    @DELETE("/api/v2/feeds/activities/{id}")
    public suspend fun deleteActivity(
        @Path("id") id: String,
        @Query("hard_delete") hardDelete: Boolean? = null,
        @Query("delete_notification_activity") deleteNotificationActivity: Boolean? = null,
    ): io.getstream.feeds.android.network.models.DeleteActivityResponse

    /** Get activity Returns activity by ID */
    @GET("/api/v2/feeds/activities/{id}")
    public suspend fun getActivity(
        @Path("id") id: String,
        @Query("comment_sort") commentSort: String? = null,
        @Query("comment_limit") commentLimit: Int? = null,
        @Query("skip_own_followings") skipOwnFollowings: Boolean? = null,
        @Query("language") language: String? = null,
        @Query("translate_text") translateText: Boolean? = null,
        @Query("include_top_level_comment_count") includeTopLevelCommentCount: Boolean? = null,
    ): io.getstream.feeds.android.network.models.GetActivityResponse

    /**
     * Partial activity update Updates certain fields of the activity. Use 'set' to update specific
     * fields and 'unset' to remove fields. This allows you to update only the fields you need
     * without replacing the entire activity. Useful for updating reply restrictions
     * ('restrict_replies'), mentioned users, or custom data. Changing `feeds` is a placement change
     * (add/delete on those feeds), not an activity content update.
     */
    @PATCH("/api/v2/feeds/activities/{id}")
    public suspend fun updateActivityPartial(
        @Path("id") id: String,
        @Body
        updateActivityPartialRequest:
            io.getstream.feeds.android.network.models.UpdateActivityPartialRequest,
    ): io.getstream.feeds.android.network.models.UpdateActivityPartialResponse

    /**
     * Partial activity update Updates certain fields of the activity. Use 'set' to update specific
     * fields and 'unset' to remove fields. This allows you to update only the fields you need
     * without replacing the entire activity. Useful for updating reply restrictions
     * ('restrict_replies'), mentioned users, or custom data. Changing `feeds` is a placement change
     * (add/delete on those feeds), not an activity content update.
     */
    @PATCH("/api/v2/feeds/activities/{id}")
    public suspend fun updateActivityPartial(
        @Path("id") id: String
    ): io.getstream.feeds.android.network.models.UpdateActivityPartialResponse

    /**
     * Full activity update Replaces an activity with the provided data. Use this to update text,
     * attachments, reply restrictions ('restrict_replies'), mentioned users, and other activity
     * fields. Note: This is a full update - any fields not provided will be cleared. Changing
     * `feeds` is a placement change (add/delete on those feeds), not an activity content update.
     */
    @PUT("/api/v2/feeds/activities/{id}")
    public suspend fun updateActivity(
        @Path("id") id: String,
        @Body updateActivityRequest: io.getstream.feeds.android.network.models.UpdateActivityRequest,
    ): io.getstream.feeds.android.network.models.UpdateActivityResponse

    /**
     * Full activity update Replaces an activity with the provided data. Use this to update text,
     * attachments, reply restrictions ('restrict_replies'), mentioned users, and other activity
     * fields. Note: This is a full update - any fields not provided will be cleared. Changing
     * `feeds` is a placement change (add/delete on those feeds), not an activity content update.
     */
    @PUT("/api/v2/feeds/activities/{id}")
    public suspend fun updateActivity(
        @Path("id") id: String
    ): io.getstream.feeds.android.network.models.UpdateActivityResponse

    /**
     * Restore an activity Restores a soft-deleted, moderation-removed, or shadow-blocked activity
     * by its ID. Deleted activities can be restored by the owner (client-side). Moderation-blocked
     * activities can only be restored server-side.
     */
    @POST("/api/v2/feeds/activities/{id}/restore")
    public suspend fun restoreActivity(
        @Path("id") id: String,
        @Query("enrich_own_fields") enrichOwnFields: Boolean? = null,
        @Body
        restoreActivityRequest: io.getstream.feeds.android.network.models.RestoreActivityRequest,
    ): io.getstream.feeds.android.network.models.RestoreActivityResponse

    /**
     * Restore an activity Restores a soft-deleted, moderation-removed, or shadow-blocked activity
     * by its ID. Deleted activities can be restored by the owner (client-side). Moderation-blocked
     * activities can only be restored server-side.
     */
    @POST("/api/v2/feeds/activities/{id}/restore")
    public suspend fun restoreActivity(
        @Path("id") id: String,
        @Query("enrich_own_fields") enrichOwnFields: Boolean? = null,
    ): io.getstream.feeds.android.network.models.RestoreActivityResponse

    /**
     * Translate activity Translates an activity's text to a given language using automated
     * translation
     */
    @POST("/api/v2/feeds/activities/{id}/translate")
    public suspend fun translateActivity(
        @Path("id") id: String,
        @Body
        translateActivityRequest: io.getstream.feeds.android.network.models.TranslateActivityRequest,
    ): io.getstream.feeds.android.network.models.TranslateActivityResponse

    /** Query bookmark folders Query bookmark folders with filter query */
    @POST("/api/v2/feeds/bookmark_folders/query")
    public suspend fun queryBookmarkFolders(
        @Body
        queryBookmarkFoldersRequest:
            io.getstream.feeds.android.network.models.QueryBookmarkFoldersRequest
    ): io.getstream.feeds.android.network.models.QueryBookmarkFoldersResponse

    /** Query bookmark folders Query bookmark folders with filter query */
    @POST("/api/v2/feeds/bookmark_folders/query")
    public suspend fun queryBookmarkFolders():
        io.getstream.feeds.android.network.models.QueryBookmarkFoldersResponse

    /** Delete a bookmark folder Delete a bookmark folder by its ID */
    @DELETE("/api/v2/feeds/bookmark_folders/{folder_id}")
    public suspend fun deleteBookmarkFolder(
        @Path("folder_id") folderId: String
    ): io.getstream.feeds.android.network.models.DeleteBookmarkFolderResponse

    /** Update a bookmark folder Update a bookmark folder by its ID */
    @PATCH("/api/v2/feeds/bookmark_folders/{folder_id}")
    public suspend fun updateBookmarkFolder(
        @Path("folder_id") folderId: String,
        @Body
        updateBookmarkFolderRequest:
            io.getstream.feeds.android.network.models.UpdateBookmarkFolderRequest,
    ): io.getstream.feeds.android.network.models.UpdateBookmarkFolderResponse

    /** Update a bookmark folder Update a bookmark folder by its ID */
    @PATCH("/api/v2/feeds/bookmark_folders/{folder_id}")
    public suspend fun updateBookmarkFolder(
        @Path("folder_id") folderId: String
    ): io.getstream.feeds.android.network.models.UpdateBookmarkFolderResponse

    /** Query bookmarks Query bookmarks with filter query */
    @POST("/api/v2/feeds/bookmarks/query")
    public suspend fun queryBookmarks(
        @Query("language") language: String? = null,
        @Query("translate_text") translateText: Boolean? = null,
        @Body queryBookmarksRequest: io.getstream.feeds.android.network.models.QueryBookmarksRequest,
    ): io.getstream.feeds.android.network.models.QueryBookmarksResponse

    /** Query bookmarks Query bookmarks with filter query */
    @POST("/api/v2/feeds/bookmarks/query")
    public suspend fun queryBookmarks(
        @Query("language") language: String? = null,
        @Query("translate_text") translateText: Boolean? = null,
    ): io.getstream.feeds.android.network.models.QueryBookmarksResponse

    /**
     * Delete multiple collections Delete collections in a batch operation. Users can only delete
     * their own collections.
     */
    @DELETE("/api/v2/feeds/collections")
    public suspend fun deleteCollections(
        @Query("collection_refs") collectionRefs: List<String>
    ): io.getstream.feeds.android.network.models.DeleteCollectionsResponse

    /**
     * Read collections Read collections by their references. By default, users can only read their
     * own collections.
     */
    @GET("/api/v2/feeds/collections")
    public suspend fun readCollections(
        @Query("collection_refs") collectionRefs: List<String>? = null
    ): io.getstream.feeds.android.network.models.ReadCollectionsResponse

    /**
     * Update multiple collections Update existing collections in a batch operation. Only the custom
     * data field is updatable. Users can only update their own collections.
     */
    @PATCH("/api/v2/feeds/collections")
    public suspend fun updateCollections(
        @Body
        updateCollectionsRequest: io.getstream.feeds.android.network.models.UpdateCollectionsRequest
    ): io.getstream.feeds.android.network.models.UpdateCollectionsResponse

    /**
     * Create multiple collections Create new collections in a batch operation. Collections are data
     * objects that can be attached to activities for managing shared data across multiple
     * activities.
     */
    @POST("/api/v2/feeds/collections")
    public suspend fun createCollections(
        @Body
        createCollectionsRequest: io.getstream.feeds.android.network.models.CreateCollectionsRequest
    ): io.getstream.feeds.android.network.models.CreateCollectionsResponse

    /** Query collections Query collections with filter query */
    @POST("/api/v2/feeds/collections/query")
    public suspend fun queryCollections(
        @Body
        queryCollectionsRequest: io.getstream.feeds.android.network.models.QueryCollectionsRequest
    ): io.getstream.feeds.android.network.models.QueryCollectionsResponse

    /** Query collections Query collections with filter query */
    @POST("/api/v2/feeds/collections/query")
    public suspend fun queryCollections():
        io.getstream.feeds.android.network.models.QueryCollectionsResponse

    /**
     * Get comments for an object Retrieve a threaded list of comments for a specific object (e.g.,
     * activity), with configurable depth, sorting, and pagination
     */
    @GET("/api/v2/feeds/comments")
    public suspend fun getComments(
        @Query("object_id") objectId: String,
        @Query("object_type") objectType: String,
        @Query("depth") depth: Int? = null,
        @Query("sort") sort: String? = null,
        @Query("replies_limit") repliesLimit: Int? = null,
        @Query("id_around") idAround: String? = null,
        @Query("language") language: String? = null,
        @Query("translate_text") translateText: Boolean? = null,
        @Query("limit") limit: Int? = null,
        @Query("prev") prev: String? = null,
        @Query("next") next: String? = null,
        @Query("include_top_level_comment_count") includeTopLevelCommentCount: Boolean? = null,
    ): io.getstream.feeds.android.network.models.GetCommentsResponse

    /**
     * Add a comment or reply Adds a comment to an object (e.g., activity) or a reply to an existing
     * comment, and broadcasts appropriate events
     */
    @POST("/api/v2/feeds/comments")
    public suspend fun addComment(
        @Body addCommentRequest: io.getstream.feeds.android.network.models.AddCommentRequest
    ): io.getstream.feeds.android.network.models.AddCommentResponse

    /**
     * Add a comment or reply Adds a comment to an object (e.g., activity) or a reply to an existing
     * comment, and broadcasts appropriate events
     */
    @POST("/api/v2/feeds/comments")
    public suspend fun addComment(): io.getstream.feeds.android.network.models.AddCommentResponse

    /**
     * Add multiple comments in a batch Adds multiple comments in a single request. Each comment
     * must specify the object type and ID.
     */
    @POST("/api/v2/feeds/comments/batch")
    public suspend fun addCommentsBatch(
        @Body
        addCommentsBatchRequest: io.getstream.feeds.android.network.models.AddCommentsBatchRequest
    ): io.getstream.feeds.android.network.models.AddCommentsBatchResponse

    /**
     * Query comments Query comments using MongoDB-style filters with pagination and sorting options
     */
    @POST("/api/v2/feeds/comments/query")
    public suspend fun queryComments(
        @Query("language") language: String? = null,
        @Query("translate_text") translateText: Boolean? = null,
        @Body queryCommentsRequest: io.getstream.feeds.android.network.models.QueryCommentsRequest,
    ): io.getstream.feeds.android.network.models.QueryCommentsResponse

    /**
     * Batch query comment reactions Returns a single user's reactions across a set of comment IDs,
     * without comment payloads
     */
    @POST("/api/v2/feeds/comments/reactions/query")
    public suspend fun batchQueryCommentReactions(
        @Body
        batchQueryCommentReactionsRequest:
            io.getstream.feeds.android.network.models.BatchQueryCommentReactionsRequest
    ): io.getstream.feeds.android.network.models.BatchQueryCommentReactionsResponse

    /** Delete a comment bookmark Deletes a bookmark from a comment */
    @DELETE("/api/v2/feeds/comments/{comment_id}/bookmarks")
    public suspend fun deleteCommentBookmark(
        @Path("comment_id") commentId: String,
        @Query("folder_id") folderId: String? = null,
    ): io.getstream.feeds.android.network.models.DeleteCommentBookmarkResponse

    /** Update comment bookmark Updates a bookmark for a comment */
    @PATCH("/api/v2/feeds/comments/{comment_id}/bookmarks")
    public suspend fun updateCommentBookmark(
        @Path("comment_id") commentId: String,
        @Body
        updateCommentBookmarkRequest:
            io.getstream.feeds.android.network.models.UpdateCommentBookmarkRequest,
    ): io.getstream.feeds.android.network.models.UpdateCommentBookmarkResponse

    /** Update comment bookmark Updates a bookmark for a comment */
    @PATCH("/api/v2/feeds/comments/{comment_id}/bookmarks")
    public suspend fun updateCommentBookmark(
        @Path("comment_id") commentId: String
    ): io.getstream.feeds.android.network.models.UpdateCommentBookmarkResponse

    /** Add comment bookmark Adds a bookmark to a comment */
    @POST("/api/v2/feeds/comments/{comment_id}/bookmarks")
    public suspend fun addCommentBookmark(
        @Path("comment_id") commentId: String,
        @Body
        addCommentBookmarkRequest:
            io.getstream.feeds.android.network.models.AddCommentBookmarkRequest,
    ): io.getstream.feeds.android.network.models.AddCommentBookmarkResponse

    /** Add comment bookmark Adds a bookmark to a comment */
    @POST("/api/v2/feeds/comments/{comment_id}/bookmarks")
    public suspend fun addCommentBookmark(
        @Path("comment_id") commentId: String
    ): io.getstream.feeds.android.network.models.AddCommentBookmarkResponse

    /**
     * Delete a comment Deletes a comment from an object (e.g., activity) and broadcasts appropriate
     * events
     */
    @DELETE("/api/v2/feeds/comments/{id}")
    public suspend fun deleteComment(
        @Path("id") id: String,
        @Query("hard_delete") hardDelete: Boolean? = null,
        @Query("delete_notification_activity") deleteNotificationActivity: Boolean? = null,
    ): io.getstream.feeds.android.network.models.DeleteCommentResponse

    /** Get comment Get a comment by ID */
    @GET("/api/v2/feeds/comments/{id}")
    public suspend fun getComment(
        @Path("id") id: String,
        @Query("language") language: String? = null,
        @Query("translate_text") translateText: Boolean? = null,
    ): io.getstream.feeds.android.network.models.GetCommentResponse

    /**
     * Update a comment Updates a comment on an object (e.g., activity) and broadcasts appropriate
     * events
     */
    @PATCH("/api/v2/feeds/comments/{id}")
    public suspend fun updateComment(
        @Path("id") id: String,
        @Body updateCommentRequest: io.getstream.feeds.android.network.models.UpdateCommentRequest,
    ): io.getstream.feeds.android.network.models.UpdateCommentResponse

    /**
     * Update a comment Updates a comment on an object (e.g., activity) and broadcasts appropriate
     * events
     */
    @PATCH("/api/v2/feeds/comments/{id}")
    public suspend fun updateComment(
        @Path("id") id: String
    ): io.getstream.feeds.android.network.models.UpdateCommentResponse

    /** Provide feedback on a comment Hide or unhide a comment for the current user */
    @POST("/api/v2/feeds/comments/{id}/feedback")
    public suspend fun commentFeedback(
        @Path("id") id: String,
        @Body
        commentFeedbackRequest: io.getstream.feeds.android.network.models.CommentFeedbackRequest,
    ): io.getstream.feeds.android.network.models.CommentFeedbackResponse

    /** Provide feedback on a comment Hide or unhide a comment for the current user */
    @POST("/api/v2/feeds/comments/{id}/feedback")
    public suspend fun commentFeedback(
        @Path("id") id: String
    ): io.getstream.feeds.android.network.models.CommentFeedbackResponse

    /**
     * Partial comment update Updates certain fields of the comment. Use 'set' to update specific
     * fields and 'unset' to remove fields.
     */
    @POST("/api/v2/feeds/comments/{id}/partial")
    public suspend fun updateCommentPartial(
        @Path("id") id: String,
        @Body
        updateCommentPartialRequest:
            io.getstream.feeds.android.network.models.UpdateCommentPartialRequest,
    ): io.getstream.feeds.android.network.models.UpdateCommentPartialResponse

    /**
     * Partial comment update Updates certain fields of the comment. Use 'set' to update specific
     * fields and 'unset' to remove fields.
     */
    @POST("/api/v2/feeds/comments/{id}/partial")
    public suspend fun updateCommentPartial(
        @Path("id") id: String
    ): io.getstream.feeds.android.network.models.UpdateCommentPartialResponse

    /** Add comment reaction Adds a reaction to a comment */
    @POST("/api/v2/feeds/comments/{id}/reactions")
    public suspend fun addCommentReaction(
        @Path("id") id: String,
        @Body
        addCommentReactionRequest:
            io.getstream.feeds.android.network.models.AddCommentReactionRequest,
    ): io.getstream.feeds.android.network.models.AddCommentReactionResponse

    /** Query comment reactions Query comment reactions */
    @POST("/api/v2/feeds/comments/{id}/reactions/query")
    public suspend fun queryCommentReactions(
        @Path("id") id: String,
        @Body
        queryCommentReactionsRequest:
            io.getstream.feeds.android.network.models.QueryCommentReactionsRequest,
    ): io.getstream.feeds.android.network.models.QueryCommentReactionsResponse

    /** Query comment reactions Query comment reactions */
    @POST("/api/v2/feeds/comments/{id}/reactions/query")
    public suspend fun queryCommentReactions(
        @Path("id") id: String
    ): io.getstream.feeds.android.network.models.QueryCommentReactionsResponse

    /** Delete comment reaction Deletes a reaction from a comment */
    @DELETE("/api/v2/feeds/comments/{id}/reactions/{type}")
    public suspend fun deleteCommentReaction(
        @Path("id") id: String,
        @Path("type") type: String,
        @Query("delete_notification_activity") deleteNotificationActivity: Boolean? = null,
    ): io.getstream.feeds.android.network.models.DeleteCommentReactionResponse

    /**
     * Get replies for a comment Retrieve a threaded list of replies for a single comment, with
     * configurable depth, sorting, and pagination
     */
    @GET("/api/v2/feeds/comments/{id}/replies")
    public suspend fun getCommentReplies(
        @Path("id") id: String,
        @Query("depth") depth: Int? = null,
        @Query("sort") sort: String? = null,
        @Query("replies_limit") repliesLimit: Int? = null,
        @Query("id_around") idAround: String? = null,
        @Query("language") language: String? = null,
        @Query("translate_text") translateText: Boolean? = null,
        @Query("limit") limit: Int? = null,
        @Query("prev") prev: String? = null,
        @Query("next") next: String? = null,
    ): io.getstream.feeds.android.network.models.GetCommentRepliesResponse

    /**
     * Restore a comment Restores a soft-deleted, moderation-removed, or shadow-blocked comment by
     * its ID. The comment and all its descendants are restored. Deleted comments can be restored
     * client-side. Moderation-blocked comments can only be restored server-side.
     */
    @POST("/api/v2/feeds/comments/{id}/restore")
    public suspend fun restoreComment(
        @Path("id") id: String,
        @Body restoreCommentRequest: io.getstream.feeds.android.network.models.RestoreCommentRequest,
    ): io.getstream.feeds.android.network.models.RestoreCommentResponse

    /**
     * Restore a comment Restores a soft-deleted, moderation-removed, or shadow-blocked comment by
     * its ID. The comment and all its descendants are restored. Deleted comments can be restored
     * client-side. Moderation-blocked comments can only be restored server-side.
     */
    @POST("/api/v2/feeds/comments/{id}/restore")
    public suspend fun restoreComment(
        @Path("id") id: String
    ): io.getstream.feeds.android.network.models.RestoreCommentResponse

    /**
     * Translate comment Translates a comment's text to a given language using automated translation
     */
    @POST("/api/v2/feeds/comments/{id}/translate")
    public suspend fun translateComment(
        @Path("id") id: String,
        @Body
        translateCommentRequest: io.getstream.feeds.android.network.models.TranslateCommentRequest,
    ): io.getstream.feeds.android.network.models.TranslateCommentResponse

    /** Delete a single feed Delete a single feed by its ID */
    @DELETE("/api/v2/feeds/feed_groups/{feed_group_id}/feeds/{feed_id}")
    public suspend fun deleteFeed(
        @Path("feed_group_id") feedGroupId: String,
        @Path("feed_id") feedId: String,
        @Query("hard_delete") hardDelete: Boolean? = null,
        @Query("purge_user_activities") purgeUserActivities: Boolean? = null,
    ): io.getstream.feeds.android.network.models.DeleteFeedResponse

    /** Create a new feed Create a single feed for a given feed group */
    @POST("/api/v2/feeds/feed_groups/{feed_group_id}/feeds/{feed_id}")
    public suspend fun getOrCreateFeed(
        @Path("feed_group_id") feedGroupId: String,
        @Path("feed_id") feedId: String,
        @Query("language") language: String? = null,
        @Query("translate_text") translateText: Boolean? = null,
        @Query("connection_id") connectionId: String? = null,
        @Body
        getOrCreateFeedRequest: io.getstream.feeds.android.network.models.GetOrCreateFeedRequest,
    ): io.getstream.feeds.android.network.models.GetOrCreateFeedResponse

    /** Create a new feed Create a single feed for a given feed group */
    @POST("/api/v2/feeds/feed_groups/{feed_group_id}/feeds/{feed_id}")
    public suspend fun getOrCreateFeed(
        @Path("feed_group_id") feedGroupId: String,
        @Path("feed_id") feedId: String,
        @Query("language") language: String? = null,
        @Query("translate_text") translateText: Boolean? = null,
        @Query("connection_id") connectionId: String? = null,
    ): io.getstream.feeds.android.network.models.GetOrCreateFeedResponse

    /** Update a feed Update an existing feed */
    @PUT("/api/v2/feeds/feed_groups/{feed_group_id}/feeds/{feed_id}")
    public suspend fun updateFeed(
        @Path("feed_group_id") feedGroupId: String,
        @Path("feed_id") feedId: String,
        @Body updateFeedRequest: io.getstream.feeds.android.network.models.UpdateFeedRequest,
    ): io.getstream.feeds.android.network.models.UpdateFeedResponse

    /** Update a feed Update an existing feed */
    @PUT("/api/v2/feeds/feed_groups/{feed_group_id}/feeds/{feed_id}")
    public suspend fun updateFeed(
        @Path("feed_group_id") feedGroupId: String,
        @Path("feed_id") feedId: String,
    ): io.getstream.feeds.android.network.models.UpdateFeedResponse

    /**
     * Mark activities as read/seen/watched Mark activities as read/seen/watched. Can mark by
     * timestamp (seen), activity IDs (read), or all as read.
     */
    @POST("/api/v2/feeds/feed_groups/{feed_group_id}/feeds/{feed_id}/activities/mark/batch")
    public suspend fun markActivity(
        @Path("feed_group_id") feedGroupId: String,
        @Path("feed_id") feedId: String,
        @Body markActivityRequest: io.getstream.feeds.android.network.models.MarkActivityRequest,
    ): io.getstream.feeds.android.network.models.Response

    /**
     * Mark activities as read/seen/watched Mark activities as read/seen/watched. Can mark by
     * timestamp (seen), activity IDs (read), or all as read.
     */
    @POST("/api/v2/feeds/feed_groups/{feed_group_id}/feeds/{feed_id}/activities/mark/batch")
    public suspend fun markActivity(
        @Path("feed_group_id") feedGroupId: String,
        @Path("feed_id") feedId: String,
    ): io.getstream.feeds.android.network.models.Response

    /**
     * Unpin an activity from a feed Unpin an activity from a feed. This removes the pin, so the
     * activity will no longer be displayed at the top of the feed.
     */
    @DELETE(
        "/api/v2/feeds/feed_groups/{feed_group_id}/feeds/{feed_id}/activities/{activity_id}/pin"
    )
    public suspend fun unpinActivity(
        @Path("feed_group_id") feedGroupId: String,
        @Path("feed_id") feedId: String,
        @Path("activity_id") activityId: String,
        @Query("enrich_own_fields") enrichOwnFields: Boolean? = null,
    ): io.getstream.feeds.android.network.models.UnpinActivityResponse

    /**
     * Pin an activity to a feed Pin an activity to a feed. Pinned activities are typically
     * displayed at the top of a feed.
     */
    @POST("/api/v2/feeds/feed_groups/{feed_group_id}/feeds/{feed_id}/activities/{activity_id}/pin")
    public suspend fun pinActivity(
        @Path("feed_group_id") feedGroupId: String,
        @Path("feed_id") feedId: String,
        @Path("activity_id") activityId: String,
        @Body pinActivityRequest: io.getstream.feeds.android.network.models.PinActivityRequest,
    ): io.getstream.feeds.android.network.models.PinActivityResponse

    /**
     * Pin an activity to a feed Pin an activity to a feed. Pinned activities are typically
     * displayed at the top of a feed.
     */
    @POST("/api/v2/feeds/feed_groups/{feed_group_id}/feeds/{feed_id}/activities/{activity_id}/pin")
    public suspend fun pinActivity(
        @Path("feed_group_id") feedGroupId: String,
        @Path("feed_id") feedId: String,
        @Path("activity_id") activityId: String,
    ): io.getstream.feeds.android.network.models.PinActivityResponse

    /**
     * Change a feed's visibility Changes the visibility of an existing feed. Follow reconciliation
     * (rewriting pending follows on loosening, or removing disallowed follows/members on
     * tightening) runs asynchronously in the background; the response returns optimistically with
     * the intended visibility.
     */
    @POST("/api/v2/feeds/feed_groups/{feed_group_id}/feeds/{feed_id}/change_visibility")
    public suspend fun changeFeedVisibility(
        @Path("feed_group_id") feedGroupId: String,
        @Path("feed_id") feedId: String,
        @Body
        changeFeedVisibilityRequest:
            io.getstream.feeds.android.network.models.ChangeFeedVisibilityRequest,
    ): io.getstream.feeds.android.network.models.ChangeFeedVisibilityResponse

    /**
     * Get feed counts Returns the number of activities in a feed, the total number of comments on
     * those activities (including nested replies), and the sum of both. The comment total is cached
     * for a few seconds on large feeds.
     */
    @GET("/api/v2/feeds/feed_groups/{feed_group_id}/feeds/{feed_id}/counts")
    public suspend fun getFeedCounts(
        @Path("feed_group_id") feedGroupId: String,
        @Path("feed_id") feedId: String,
    ): io.getstream.feeds.android.network.models.GetFeedCountsResponse

    /** Update feed members Add, remove, or set members for a feed */
    @PATCH("/api/v2/feeds/feed_groups/{feed_group_id}/feeds/{feed_id}/members")
    public suspend fun updateFeedMembers(
        @Path("feed_group_id") feedGroupId: String,
        @Path("feed_id") feedId: String,
        @Body
        updateFeedMembersRequest: io.getstream.feeds.android.network.models.UpdateFeedMembersRequest,
    ): io.getstream.feeds.android.network.models.UpdateFeedMembersResponse

    /** Accept a feed member request Accepts a pending feed member request */
    @POST("/api/v2/feeds/feed_groups/{feed_group_id}/feeds/{feed_id}/members/accept")
    public suspend fun acceptFeedMemberInvite(
        @Path("feed_id") feedId: String,
        @Path("feed_group_id") feedGroupId: String,
        @Body
        acceptFeedMemberInviteRequest:
            io.getstream.feeds.android.network.models.AcceptFeedMemberInviteRequest,
    ): io.getstream.feeds.android.network.models.AcceptFeedMemberInviteResponse

    /** Accept a feed member request Accepts a pending feed member request */
    @POST("/api/v2/feeds/feed_groups/{feed_group_id}/feeds/{feed_id}/members/accept")
    public suspend fun acceptFeedMemberInvite(
        @Path("feed_id") feedId: String,
        @Path("feed_group_id") feedGroupId: String,
    ): io.getstream.feeds.android.network.models.AcceptFeedMemberInviteResponse

    /**
     * Query feed members Query feed members based on filters with pagination and sorting options
     */
    @POST("/api/v2/feeds/feed_groups/{feed_group_id}/feeds/{feed_id}/members/query")
    public suspend fun queryFeedMembers(
        @Path("feed_group_id") feedGroupId: String,
        @Path("feed_id") feedId: String,
        @Body
        queryFeedMembersRequest: io.getstream.feeds.android.network.models.QueryFeedMembersRequest,
    ): io.getstream.feeds.android.network.models.QueryFeedMembersResponse

    /**
     * Query feed members Query feed members based on filters with pagination and sorting options
     */
    @POST("/api/v2/feeds/feed_groups/{feed_group_id}/feeds/{feed_id}/members/query")
    public suspend fun queryFeedMembers(
        @Path("feed_group_id") feedGroupId: String,
        @Path("feed_id") feedId: String,
    ): io.getstream.feeds.android.network.models.QueryFeedMembersResponse

    /** Reject an invite to become a feed member Rejects a pending feed member request */
    @POST("/api/v2/feeds/feed_groups/{feed_group_id}/feeds/{feed_id}/members/reject")
    public suspend fun rejectFeedMemberInvite(
        @Path("feed_group_id") feedGroupId: String,
        @Path("feed_id") feedId: String,
        @Body
        rejectFeedMemberInviteRequest:
            io.getstream.feeds.android.network.models.RejectFeedMemberInviteRequest,
    ): io.getstream.feeds.android.network.models.RejectFeedMemberInviteResponse

    /** Reject an invite to become a feed member Rejects a pending feed member request */
    @POST("/api/v2/feeds/feed_groups/{feed_group_id}/feeds/{feed_id}/members/reject")
    public suspend fun rejectFeedMemberInvite(
        @Path("feed_group_id") feedGroupId: String,
        @Path("feed_id") feedId: String,
    ): io.getstream.feeds.android.network.models.RejectFeedMemberInviteResponse

    /**
     * Get notification status Returns the unread and unseen counts of a notification feed, along
     * with the last seen and last read timestamps, without building a page.
     */
    @GET("/api/v2/feeds/feed_groups/{feed_group_id}/feeds/{feed_id}/notification_status")
    public suspend fun getNotificationStatus(
        @Path("feed_group_id") feedGroupId: String,
        @Path("feed_id") feedId: String,
        @Query("view") view: String? = null,
    ): io.getstream.feeds.android.network.models.GetNotificationStatusResponse

    /** Query pinned activities Query pinned activities for a feed with filter query */
    @POST("/api/v2/feeds/feed_groups/{feed_group_id}/feeds/{feed_id}/pinned_activities/query")
    public suspend fun queryPinnedActivities(
        @Path("feed_group_id") feedGroupId: String,
        @Path("feed_id") feedId: String,
        @Query("language") language: String? = null,
        @Query("translate_text") translateText: Boolean? = null,
        @Body
        queryPinnedActivitiesRequest:
            io.getstream.feeds.android.network.models.QueryPinnedActivitiesRequest,
    ): io.getstream.feeds.android.network.models.QueryPinnedActivitiesResponse

    /** Query pinned activities Query pinned activities for a feed with filter query */
    @POST("/api/v2/feeds/feed_groups/{feed_group_id}/feeds/{feed_id}/pinned_activities/query")
    public suspend fun queryPinnedActivities(
        @Path("feed_group_id") feedGroupId: String,
        @Path("feed_id") feedId: String,
        @Query("language") language: String? = null,
        @Query("translate_text") translateText: Boolean? = null,
    ): io.getstream.feeds.android.network.models.QueryPinnedActivitiesResponse

    /** Stop watching feed Call this Method to stop receiving feed events */
    @DELETE("/api/v2/feeds/feed_groups/{feed_group_id}/feeds/{feed_id}/watch")
    public suspend fun stopWatchingFeed(
        @Path("feed_group_id") feedGroupId: String,
        @Path("feed_id") feedId: String,
        @Query("connection_id") connectionId: String? = null,
    ): io.getstream.feeds.android.network.models.Response

    /** Get follow suggestions Get follow suggestions for a feed group */
    @GET("/api/v2/feeds/feed_groups/{feed_group_id}/follow_suggestions")
    public suspend fun getFollowSuggestions(
        @Path("feed_group_id") feedGroupId: String,
        @Query("limit") limit: Int? = null,
    ): io.getstream.feeds.android.network.models.GetFollowSuggestionsResponse

    /**
     * Create multiple feeds at once Create multiple feeds at once for a given feed group.
     * Client-side, feeds that already exist are returned unchanged. Server-side, an existing feed
     * keeps its owner and any fields the request omits. Feeds that have been deleted are rejected.
     */
    @POST("/api/v2/feeds/feeds/batch")
    public suspend fun createFeedsBatch(
        @Body
        createFeedsBatchRequest: io.getstream.feeds.android.network.models.CreateFeedsBatchRequest
    ): io.getstream.feeds.android.network.models.CreateFeedsBatchResponse

    /**
     * Get own fields for multiple feeds Retrieves own_follows, own_capabilities, and/or
     * own_membership for multiple feeds in a single request. If fields are not specified, all three
     * fields are returned.
     */
    @POST("/api/v2/feeds/feeds/own/batch")
    public suspend fun ownBatch(
        @Query("connection_id") connectionId: String? = null,
        @Body ownBatchRequest: io.getstream.feeds.android.network.models.OwnBatchRequest,
    ): io.getstream.feeds.android.network.models.OwnBatchResponse

    /** Query feeds Query feeds with filter query */
    @POST("/api/v2/feeds/feeds/query")
    public suspend fun queryFeeds(
        @Query("connection_id") connectionId: String? = null,
        @Body queryFeedsRequest: io.getstream.feeds.android.network.models.QueryFeedsRequest,
    ): io.getstream.feeds.android.network.models.QueryFeedsResponse

    /** Query feeds Query feeds with filter query */
    @POST("/api/v2/feeds/feeds/query")
    public suspend fun queryFeeds(
        @Query("connection_id") connectionId: String? = null
    ): io.getstream.feeds.android.network.models.QueryFeedsResponse

    /**
     * Update a follow Updates a follow's custom data, push preference, and follower role. Source
     * owner can update custom data and push preference. Follower role can only be updated via
     * server-side requests.
     */
    @PATCH("/api/v2/feeds/follows")
    public suspend fun updateFollow(
        @Body updateFollowRequest: io.getstream.feeds.android.network.models.UpdateFollowRequest
    ): io.getstream.feeds.android.network.models.UpdateFollowResponse

    /** Create a follow Creates a follow and broadcasts FollowAddedEvent */
    @POST("/api/v2/feeds/follows")
    public suspend fun follow(
        @Body followRequest: io.getstream.feeds.android.network.models.FollowRequest
    ): io.getstream.feeds.android.network.models.SingleFollowResponse

    /**
     * Accept a follow request Accepts a pending follow request. Follower role can only be set via
     * server-side requests.
     */
    @POST("/api/v2/feeds/follows/accept")
    public suspend fun acceptFollow(
        @Body acceptFollowRequest: io.getstream.feeds.android.network.models.AcceptFollowRequest
    ): io.getstream.feeds.android.network.models.AcceptFollowResponse

    /**
     * Create multiple follows at once Creates multiple follows at once and broadcasts
     * FollowAddedEvent for each follow. Deprecated: use GetOrCreateFollows (POST
     * /api/v2/feeds/follows/batch/upsert), which returns existing follows instead of erroring.
     */
    @POST("/api/v2/feeds/follows/batch")
    public suspend fun followBatch(
        @Body followBatchRequest: io.getstream.feeds.android.network.models.FollowBatchRequest
    ): io.getstream.feeds.android.network.models.FollowBatchResponse

    /**
     * Upsert multiple follows at once Creates or updates multiple follows at once. Does not return
     * an error if follows already exist. Broadcasts FollowAddedEvent only for newly created
     * follows.
     */
    @POST("/api/v2/feeds/follows/batch/upsert")
    public suspend fun getOrCreateFollows(
        @Body followBatchRequest: io.getstream.feeds.android.network.models.FollowBatchRequest
    ): io.getstream.feeds.android.network.models.FollowBatchResponse

    /**
     * Query follows Query follows based on filters with pagination and sorting options. Server-side
     * callers may set include_group_counts to also receive accepted following counts keyed by
     * followed feed group. Requires exactly one source_feed / source_fid string equality. HTTP 400
     * if target_feed appears, if two source FIDs are given, or if $in / $or mentions those keys.
     */
    @POST("/api/v2/feeds/follows/query")
    public suspend fun queryFollows(
        @Body queryFollowsRequest: io.getstream.feeds.android.network.models.QueryFollowsRequest
    ): io.getstream.feeds.android.network.models.QueryFollowsResponse

    /**
     * Query follows Query follows based on filters with pagination and sorting options. Server-side
     * callers may set include_group_counts to also receive accepted following counts keyed by
     * followed feed group. Requires exactly one source_feed / source_fid string equality. HTTP 400
     * if target_feed appears, if two source FIDs are given, or if $in / $or mentions those keys.
     */
    @POST("/api/v2/feeds/follows/query")
    public suspend fun queryFollows():
        io.getstream.feeds.android.network.models.QueryFollowsResponse

    /** Reject a follow request Rejects a pending follow request */
    @POST("/api/v2/feeds/follows/reject")
    public suspend fun rejectFollow(
        @Body rejectFollowRequest: io.getstream.feeds.android.network.models.RejectFollowRequest
    ): io.getstream.feeds.android.network.models.RejectFollowResponse

    /**
     * Upsert a follow Creates a follow if it does not exist, or returns the existing one.
     * Broadcasts feeds.follow.created (FollowCreatedEvent) only when the follow is newly created.
     */
    @POST("/api/v2/feeds/follows/upsert")
    public suspend fun getOrCreateFollow(
        @Body followRequest: io.getstream.feeds.android.network.models.FollowRequest
    ): io.getstream.feeds.android.network.models.GetOrCreateFollowResponse

    /** Unfollow a feed Removes a follow and broadcasts FollowRemovedEvent */
    @DELETE("/api/v2/feeds/follows/{source}/{target}")
    public suspend fun unfollow(
        @Path("source") source: String,
        @Path("target") target: String,
        @Query("delete_notification_activity") deleteNotificationActivity: Boolean? = null,
        @Query("keep_history") keepHistory: Boolean? = null,
        @Query("enrich_own_fields") enrichOwnFields: Boolean? = null,
    ): io.getstream.feeds.android.network.models.UnfollowResponse

    /**
     * Unfollow multiple feeds (idempotent) Removes multiple follows and broadcasts
     * FollowRemovedEvent for each. Does not return an error if follows don't exist.
     */
    @POST("/api/v2/feeds/unfollow/batch/upsert")
    public suspend fun getOrCreateUnfollows(
        @Body unfollowBatchRequest: io.getstream.feeds.android.network.models.UnfollowBatchRequest
    ): io.getstream.feeds.android.network.models.UnfollowBatchResponse

    /**
     * Unfollow a feed (idempotent) Removes a follow and broadcasts feeds.follow.deleted
     * (FollowDeletedEvent). Does not return an error if the follow does not exist.
     */
    @POST("/api/v2/feeds/unfollow/upsert")
    public suspend fun getOrCreateUnfollow(
        @Body
        getOrCreateUnfollowRequest:
            io.getstream.feeds.android.network.models.GetOrCreateUnfollowRequest
    ): io.getstream.feeds.android.network.models.GetOrCreateUnfollowResponse

    /**
     * Remove interests from a user Removes the given interest tags from a user, whether they were
     * set manually or computed from reactions. A removed computed tag returns on the next
     * recomputation if the user's reactions still support it; to keep a tag out of ranking for
     * good, set it with a weight of 0 or below instead. Client-side callers may only manage their
     * own interests; server-side callers may manage any user. Returns the user's remaining
     * interests.
     */
    @DELETE("/api/v2/feeds/users/{user_id}/interests")
    public suspend fun deleteUserInterests(
        @Path("user_id") userId: String,
        @Query("tags") tags: List<String>,
    ): io.getstream.feeds.android.network.models.DeleteUserInterestsResponse

    /**
     * Get a user's interests Returns the user's interest tags with their ranking weights: tags
     * computed from the activities the user reacted to and tags set manually through the API.
     * Client-side callers may only read their own interests; server-side callers may fetch any
     * user. Results are sorted by descending weight, then manually set tags before computed ones,
     * then descending count, then alphabetically by tag.
     */
    @GET("/api/v2/feeds/users/{user_id}/interests")
    public suspend fun getUserInterests(
        @Path("user_id") userId: String,
        @Query("limit") limit: Int? = null,
    ): io.getstream.feeds.android.network.models.GetUserInterestsResponse

    /**
     * Set a user's interests Adds or updates interest tags on a user with explicit ranking weights.
     * Tags set this way rank above the tags computed from the user's reactions at equal weight and
     * are never overwritten by them. Client-side callers may only manage their own interests;
     * server-side callers may manage any user. Returns the user's full interest list after the
     * write.
     */
    @PUT("/api/v2/feeds/users/{user_id}/interests")
    public suspend fun upsertUserInterests(
        @Path("user_id") userId: String,
        @Body
        upsertUserInterestsRequest:
            io.getstream.feeds.android.network.models.UpsertUserInterestsRequest,
    ): io.getstream.feeds.android.network.models.UpsertUserInterestsResponse

    /** Create Guest */
    @POST("/api/v2/guest")
    public suspend fun createGuest(
        @Body createGuestRequest: io.getstream.feeds.android.network.models.CreateGuestRequest
    ): io.getstream.feeds.android.network.models.CreateGuestResponse

    /** Long Poll (Transport) WebSocket fallback transport endpoint */
    @GET("/api/v2/longpoll")
    public suspend fun longPoll(
        @Query("connection_id") connectionId: String? = null,
        @Query("close") close: Boolean? = null,
        @Query("json")
        json:
            io.getstream.feeds.android.network.infrastructure.JsonQuery<
                io.getstream.feeds.android.network.models.WSAuthMessage
            >? =
            null,
    )

    /**
     * Get moderation action configs Returns moderation action configs grouped by entity type,
     * sorted by order ascending. Supports fetching DB-configured actions, hardcoded defaults, or
     * both.
     */
    @GET("/api/v2/moderation/action_config")
    public suspend fun getActionConfig(
        @Query("queue_type") queueType: String? = null,
        @Query("entity_type") entityType: String? = null,
        @Query("exclude_defaults") excludeDefaults: Boolean? = null,
        @Query("only_defaults") onlyDefaults: Boolean? = null,
    ): io.getstream.feeds.android.network.models.GetActionConfigResponse

    /**
     * Create or update a moderation action config Create a new moderation action config entry or
     * update an existing one. Action configs control the action buttons displayed in the moderation
     * dashboard for each entity type.
     */
    @POST("/api/v2/moderation/action_config")
    public suspend fun upsertActionConfig(
        @Body
        upsertActionConfigRequest:
            io.getstream.feeds.android.network.models.UpsertActionConfigRequest
    ): io.getstream.feeds.android.network.models.UpsertActionConfigResponse

    /**
     * Bulk create or update moderation action configs Create or update multiple moderation action
     * config entries in a single request. Omit the ID field to create; provide an ID to update.
     */
    @POST("/api/v2/moderation/action_config/bulk")
    public suspend fun bulkUpsertActionConfig(
        @Body
        bulkUpsertActionConfigRequest:
            io.getstream.feeds.android.network.models.BulkUpsertActionConfigRequest
    ): io.getstream.feeds.android.network.models.BulkUpsertActionConfigResponse

    /**
     * Bulk delete moderation action configs Delete multiple moderation action config entries by
     * UUID in a single request.
     */
    @POST("/api/v2/moderation/action_config/bulk_delete")
    public suspend fun bulkDeleteActionConfig(
        @Body
        bulkDeleteActionConfigRequest:
            io.getstream.feeds.android.network.models.BulkDeleteActionConfigRequest
    ): io.getstream.feeds.android.network.models.BulkDeleteActionConfigResponse

    /**
     * Delete a moderation action config Delete a specific moderation action config entry by its
     * UUID.
     */
    @DELETE("/api/v2/moderation/action_config/{id}")
    public suspend fun deleteActionConfig(
        @Path("id") id: String
    ): io.getstream.feeds.android.network.models.DeleteActionConfigResponse

    /** Appeal against the moderation decision Appeal against the moderation decision */
    @POST("/api/v2/moderation/appeal")
    public suspend fun appeal(
        @Body appealRequest: io.getstream.feeds.android.network.models.AppealRequest
    ): io.getstream.feeds.android.network.models.AppealResponse

    /** Get appeal item Retrieve a specific appeal item by its ID */
    @GET("/api/v2/moderation/appeal/{id}")
    public suspend fun getAppeal(
        @Path("id") id: String
    ): io.getstream.feeds.android.network.models.GetAppealResponse

    /** Query Appeals Query Appeals */
    @POST("/api/v2/moderation/appeals")
    public suspend fun queryAppeals(
        @Body queryAppealsRequest: io.getstream.feeds.android.network.models.QueryAppealsRequest
    ): io.getstream.feeds.android.network.models.QueryAppealsResponse

    /** Query Appeals Query Appeals */
    @POST("/api/v2/moderation/appeals")
    public suspend fun queryAppeals():
        io.getstream.feeds.android.network.models.QueryAppealsResponse

    /**
     * Bulk action appeals Process multiple appeals in a single request by applying the specified
     * action to each. Supported actions: unban, restore, unblock, mark_reviewed, reject_appeal.
     * Each appeal goes through the same path as a single submit_action call.
     */
    @POST("/api/v2/moderation/appeals/bulk_action")
    public suspend fun bulkActionAppeals(
        @Body
        bulkActionAppealsRequest: io.getstream.feeds.android.network.models.BulkActionAppealsRequest
    ): io.getstream.feeds.android.network.models.BulkActionAppealsResponse

    /** Ban Ban a user from a channel or the entire app */
    @POST("/api/v2/moderation/ban")
    public suspend fun ban(
        @Body banRequest: io.getstream.feeds.android.network.models.BanRequest
    ): io.getstream.feeds.android.network.models.ModerationBanResponse

    /**
     * Create or update moderation configuration Create a new moderation configuration or update an
     * existing one. Configure settings for content filtering, AI analysis, toxicity detection, and
     * other moderation features.
     */
    @POST("/api/v2/moderation/config")
    public suspend fun upsertConfig(
        @Body upsertConfigRequest: io.getstream.feeds.android.network.models.UpsertConfigRequest
    ): io.getstream.feeds.android.network.models.UpsertConfigResponse

    /**
     * Save moderation configuration version Save a moderation configuration as a new version
     * without making it live. The version takes effect once it is published.
     */
    @POST("/api/v2/moderation/config/versions")
    public suspend fun saveConfigVersion(
        @Body
        saveConfigVersionRequest: io.getstream.feeds.android.network.models.SaveConfigVersionRequest
    ): io.getstream.feeds.android.network.models.SaveConfigVersionResponse

    /** Delete a moderation policy Delete a specific moderation policy by its name */
    @DELETE("/api/v2/moderation/config/{key}")
    public suspend fun deleteConfig(
        @Path("key") key: String,
        @Query("team") team: String? = null,
    ): io.getstream.feeds.android.network.models.DeleteModerationConfigResponse

    /**
     * Get moderation configuration Retrieve a specific moderation configuration by its key and
     * team. This configuration contains settings for various moderation features like toxicity
     * detection, AI analysis, and filtering rules.
     */
    @GET("/api/v2/moderation/config/{key}")
    public suspend fun getConfig(
        @Path("key") key: String,
        @Query("team") team: String? = null,
    ): io.getstream.feeds.android.network.models.GetConfigResponse

    /**
     * List moderation configuration versions List the retained versions of a moderation
     * configuration, newest first, and which one is published.
     */
    @GET("/api/v2/moderation/config/{key}/versions")
    public suspend fun listConfigVersions(
        @Path("key") key: String,
        @Query("team") team: String? = null,
    ): io.getstream.feeds.android.network.models.ListConfigVersionsResponse

    /**
     * Get moderation configuration version Retrieve the full moderation configuration stored in a
     * specific version, whether or not it is published.
     */
    @GET("/api/v2/moderation/config/{key}/versions/{version}")
    public suspend fun getConfigVersion(
        @Path("key") key: String,
        @Path("version") version: Int,
        @Query("team") team: String? = null,
    ): io.getstream.feeds.android.network.models.GetConfigVersionResponse

    /**
     * Publish moderation configuration version Make a stored version the moderation configuration
     * in effect. Publishing an older version reverts to it without creating a new version.
     */
    @POST("/api/v2/moderation/config/{key}/versions/{version}/publish")
    public suspend fun publishConfigVersion(
        @Path("key") key: String,
        @Path("version") version: Int,
        @Body
        publishConfigVersionRequest:
            io.getstream.feeds.android.network.models.PublishConfigVersionRequest,
    ): io.getstream.feeds.android.network.models.PublishConfigVersionResponse

    /**
     * Publish moderation configuration version Make a stored version the moderation configuration
     * in effect. Publishing an older version reverts to it without creating a new version.
     */
    @POST("/api/v2/moderation/config/{key}/versions/{version}/publish")
    public suspend fun publishConfigVersion(
        @Path("key") key: String,
        @Path("version") version: Int,
    ): io.getstream.feeds.android.network.models.PublishConfigVersionResponse

    /**
     * Query moderation configurations Search and filter moderation configurations across your
     * application. This endpoint is designed for building moderation dashboards and managing
     * multiple configuration sets.
     */
    @POST("/api/v2/moderation/configs")
    public suspend fun queryModerationConfigs(
        @Body
        queryModerationConfigsRequest:
            io.getstream.feeds.android.network.models.QueryModerationConfigsRequest
    ): io.getstream.feeds.android.network.models.QueryModerationConfigsResponse

    /**
     * Query moderation configurations Search and filter moderation configurations across your
     * application. This endpoint is designed for building moderation dashboards and managing
     * multiple configuration sets.
     */
    @POST("/api/v2/moderation/configs")
    public suspend fun queryModerationConfigs():
        io.getstream.feeds.android.network.models.QueryModerationConfigsResponse

    /**
     * Flag content for moderation Flag any type of content (messages, users, channels, activities)
     * for moderation review. Supports custom content types and additional metadata for flagged
     * content.
     */
    @POST("/api/v2/moderation/flag")
    public suspend fun flag(
        @Body flagRequest: io.getstream.feeds.android.network.models.FlagRequest
    ): io.getstream.feeds.android.network.models.FlagItemResponse

    /**
     * Mute Mute a user. Mutes are generally not visible to the user you mute, while block is
     * something you notice.
     */
    @POST("/api/v2/moderation/mute")
    public suspend fun mute(
        @Body muteRequest: io.getstream.feeds.android.network.models.MuteRequest
    ): io.getstream.feeds.android.network.models.MuteResponse

    /** List queues */
    @GET("/api/v2/moderation/queues")
    public suspend fun listQueues(): io.getstream.feeds.android.network.models.ListQueuesResponse

    /** Create queue */
    @POST("/api/v2/moderation/queues")
    public suspend fun createQueue(
        @Body createQueueRequest: io.getstream.feeds.android.network.models.CreateQueueRequest
    ): io.getstream.feeds.android.network.models.QueueResponse

    /** Get queue */
    @GET("/api/v2/moderation/queues/{id}")
    public suspend fun getQueue(
        @Path("id") id: String
    ): io.getstream.feeds.android.network.models.QueueResponse

    /** Update queue */
    @PATCH("/api/v2/moderation/queues/{id}")
    public suspend fun updateQueue(
        @Path("id") id: String,
        @Body updateQueueRequest: io.getstream.feeds.android.network.models.UpdateQueueRequest,
    ): io.getstream.feeds.android.network.models.QueueResponse

    /** Update queue */
    @PATCH("/api/v2/moderation/queues/{id}")
    public suspend fun updateQueue(
        @Path("id") id: String
    ): io.getstream.feeds.android.network.models.QueueResponse

    /** Delete queue */
    @POST("/api/v2/moderation/queues/{id}/delete")
    public suspend fun deleteQueue(
        @Path("id") id: String,
        @Body deleteQueueRequest: io.getstream.feeds.android.network.models.DeleteQueueRequest,
    ): io.getstream.feeds.android.network.models.QueueResponse

    /** Delete queue */
    @POST("/api/v2/moderation/queues/{id}/delete")
    public suspend fun deleteQueue(
        @Path("id") id: String
    ): io.getstream.feeds.android.network.models.QueueResponse

    /**
     * Reveal review queue media Authorize revealing a review queue item's blurred media. CSAM can
     * only be revealed by users with the RevealProtectedMedia permission. Reveals of blurred CSAM
     * or gore are recorded in the moderation action logs.
     */
    @POST("/api/v2/moderation/reveal_media")
    public suspend fun revealMedia(
        @Body revealMediaRequest: io.getstream.feeds.android.network.models.RevealMediaRequest
    ): io.getstream.feeds.android.network.models.RevealMediaResponse

    /**
     * Query review queue items Query review queue items allows you to filter the review queue
     * items. This is used for building a moderation dashboard.
     */
    @POST("/api/v2/moderation/review_queue")
    public suspend fun queryReviewQueue(
        @Body
        queryReviewQueueRequest: io.getstream.feeds.android.network.models.QueryReviewQueueRequest
    ): io.getstream.feeds.android.network.models.QueryReviewQueueResponse

    /**
     * Query review queue items Query review queue items allows you to filter the review queue
     * items. This is used for building a moderation dashboard.
     */
    @POST("/api/v2/moderation/review_queue")
    public suspend fun queryReviewQueue():
        io.getstream.feeds.android.network.models.QueryReviewQueueResponse

    /**
     * Count review queue items Count the review queue items matching a filter, so a moderation
     * dashboard can show a total for a queue or view. Counts are kept up to date in the background
     * while they are being requested.
     */
    @POST("/api/v2/moderation/review_queue/count")
    public suspend fun countReviewQueue(
        @Body
        countReviewQueueRequest: io.getstream.feeds.android.network.models.CountReviewQueueRequest
    ): io.getstream.feeds.android.network.models.CountReviewQueueResponse

    /**
     * Count review queue items Count the review queue items matching a filter, so a moderation
     * dashboard can show a total for a queue or view. Counts are kept up to date in the background
     * while they are being requested.
     */
    @POST("/api/v2/moderation/review_queue/count")
    public suspend fun countReviewQueue():
        io.getstream.feeds.android.network.models.CountReviewQueueResponse

    /**
     * Submit moderation action Take action on flagged content, such as marking content as safe,
     * deleting content, banning users, or executing custom moderation actions. Supports various
     * action types with configurable parameters.
     */
    @POST("/api/v2/moderation/submit_action")
    public suspend fun submitAction(
        @Body submitActionRequest: io.getstream.feeds.android.network.models.SubmitActionRequest
    ): io.getstream.feeds.android.network.models.SubmitActionResponse

    /** Unban Unban a user from a channel or globally. */
    @POST("/api/v2/moderation/unban")
    public suspend fun unban(
        @Query("target_user_id") targetUserId: String,
        @Query("channel_cid") channelCid: String? = null,
        @Query("remove_future_channels_ban") removeFutureChannelsBan: Boolean? = null,
        @Body unbanRequest: io.getstream.feeds.android.network.models.UnbanRequest,
    ): io.getstream.feeds.android.network.models.UnbanResponse

    /** Unban Unban a user from a channel or globally. */
    @POST("/api/v2/moderation/unban")
    public suspend fun unban(
        @Query("target_user_id") targetUserId: String,
        @Query("channel_cid") channelCid: String? = null,
        @Query("remove_future_channels_ban") removeFutureChannelsBan: Boolean? = null,
    ): io.getstream.feeds.android.network.models.UnbanResponse

    /** Unmute a user Unmute a user */
    @POST("/api/v2/moderation/unmute")
    public suspend fun unmute(
        @Body unmuteRequest: io.getstream.feeds.android.network.models.UnmuteRequest
    ): io.getstream.feeds.android.network.models.UnmuteResponse

    /** Get OG Get an OpenGraph attachment for a link */
    @GET("/api/v2/og")
    public suspend fun getOG(
        @Query("url") url: String
    ): io.getstream.feeds.android.network.models.GetOGResponse

    /** Create poll Creates a new poll */
    @POST("/api/v2/polls")
    public suspend fun createPoll(
        @Body createPollRequest: io.getstream.feeds.android.network.models.CreatePollRequest
    ): io.getstream.feeds.android.network.models.PollResponse

    /** Update poll Updates a poll */
    @PUT("/api/v2/polls")
    public suspend fun updatePoll(
        @Body updatePollRequest: io.getstream.feeds.android.network.models.UpdatePollRequest
    ): io.getstream.feeds.android.network.models.PollResponse

    /** Query polls Queries polls */
    @POST("/api/v2/polls/query")
    public suspend fun queryPolls(
        @Body queryPollsRequest: io.getstream.feeds.android.network.models.QueryPollsRequest
    ): io.getstream.feeds.android.network.models.QueryPollsResponse

    /** Query polls Queries polls */
    @POST("/api/v2/polls/query")
    public suspend fun queryPolls(): io.getstream.feeds.android.network.models.QueryPollsResponse

    /** Delete poll Deletes a poll */
    @DELETE("/api/v2/polls/{poll_id}")
    public suspend fun deletePoll(
        @Path("poll_id") pollId: String
    ): io.getstream.feeds.android.network.models.Response

    /** Get poll Retrieves a poll */
    @GET("/api/v2/polls/{poll_id}")
    public suspend fun getPoll(
        @Path("poll_id") pollId: String
    ): io.getstream.feeds.android.network.models.PollResponse

    /** Partial update poll Updates a poll partially */
    @PATCH("/api/v2/polls/{poll_id}")
    public suspend fun updatePollPartial(
        @Path("poll_id") pollId: String,
        @Body
        updatePollPartialRequest: io.getstream.feeds.android.network.models.UpdatePollPartialRequest,
    ): io.getstream.feeds.android.network.models.PollResponse

    /** Partial update poll Updates a poll partially */
    @PATCH("/api/v2/polls/{poll_id}")
    public suspend fun updatePollPartial(
        @Path("poll_id") pollId: String
    ): io.getstream.feeds.android.network.models.PollResponse

    /** Create poll option Creates a poll option */
    @POST("/api/v2/polls/{poll_id}/options")
    public suspend fun createPollOption(
        @Path("poll_id") pollId: String,
        @Body
        createPollOptionRequest: io.getstream.feeds.android.network.models.CreatePollOptionRequest,
    ): io.getstream.feeds.android.network.models.PollOptionResponse

    /** Update poll option Updates a poll option */
    @PUT("/api/v2/polls/{poll_id}/options")
    public suspend fun updatePollOption(
        @Path("poll_id") pollId: String,
        @Body
        updatePollOptionRequest: io.getstream.feeds.android.network.models.UpdatePollOptionRequest,
    ): io.getstream.feeds.android.network.models.PollOptionResponse

    /** Delete poll option Deletes a poll option */
    @DELETE("/api/v2/polls/{poll_id}/options/{option_id}")
    public suspend fun deletePollOption(
        @Path("poll_id") pollId: String,
        @Path("option_id") optionId: String,
    ): io.getstream.feeds.android.network.models.Response

    /** Get poll option Retrieves a poll option */
    @GET("/api/v2/polls/{poll_id}/options/{option_id}")
    public suspend fun getPollOption(
        @Path("poll_id") pollId: String,
        @Path("option_id") optionId: String,
    ): io.getstream.feeds.android.network.models.PollOptionResponse

    /** Query votes Queries votes */
    @POST("/api/v2/polls/{poll_id}/votes")
    public suspend fun queryPollVotes(
        @Path("poll_id") pollId: String,
        @Body queryPollVotesRequest: io.getstream.feeds.android.network.models.QueryPollVotesRequest,
    ): io.getstream.feeds.android.network.models.PollVotesResponse

    /** Query votes Queries votes */
    @POST("/api/v2/polls/{poll_id}/votes")
    public suspend fun queryPollVotes(
        @Path("poll_id") pollId: String
    ): io.getstream.feeds.android.network.models.PollVotesResponse

    /**
     * Push notification preferences Upserts the push preferences for a user and or channel member.
     * Set to all, mentions or none
     */
    @POST("/api/v2/push_preferences")
    public suspend fun updatePushNotificationPreferences(
        @Body
        upsertPushPreferencesRequest:
            io.getstream.feeds.android.network.models.UpsertPushPreferencesRequest
    ): io.getstream.feeds.android.network.models.UpsertPushPreferencesResponse

    /**
     * Search roles Searches mentionable roles (user-assignable + channel-assignable, built-in and
     * custom) by name prefix for autocomplete
     */
    @GET("/api/v2/roles/search")
    public suspend fun searchRoles(
        @Query("query") query: String,
        @Query("limit") limit: Int? = null,
        @Query("name_gt") nameGt: String? = null,
        @Query("role_type") roleType: String? = null,
        @Query("include_global_roles") includeGlobalRoles: Boolean? = null,
    ): io.getstream.feeds.android.network.models.SearchRolesResponse

    /** Delete file Deletes previously uploaded file */
    @DELETE("/api/v2/uploads/file")
    public suspend fun deleteFile(
        @Query("url") url: String? = null
    ): io.getstream.feeds.android.network.models.Response

    /** Upload file Uploads file */
    @POST("/api/v2/uploads/file")
    public suspend fun uploadFile(
        @Body fileUploadRequest: io.getstream.feeds.android.network.models.FileUploadRequest
    ): io.getstream.feeds.android.network.models.FileUploadResponse

    /** Upload file Uploads file */
    @POST("/api/v2/uploads/file")
    public suspend fun uploadFile(): io.getstream.feeds.android.network.models.FileUploadResponse

    /** Delete image Deletes previously uploaded image */
    @DELETE("/api/v2/uploads/image")
    public suspend fun deleteImage(
        @Query("url") url: String? = null
    ): io.getstream.feeds.android.network.models.Response

    /** Upload image Uploads image */
    @POST("/api/v2/uploads/image")
    public suspend fun uploadImage(
        @Body imageUploadRequest: io.getstream.feeds.android.network.models.ImageUploadRequest
    ): io.getstream.feeds.android.network.models.ImageUploadResponse

    /** Upload image Uploads image */
    @POST("/api/v2/uploads/image")
    public suspend fun uploadImage(): io.getstream.feeds.android.network.models.ImageUploadResponse

    /** List user groups Lists user groups with cursor-based pagination */
    @GET("/api/v2/usergroups")
    public suspend fun listUserGroups(
        @Query("limit") limit: Int? = null,
        @Query("id_gt") idGt: String? = null,
        @Query("created_at_gt") createdAtGt: String? = null,
        @Query("team_id") teamId: String? = null,
    ): io.getstream.feeds.android.network.models.ListUserGroupsResponse

    /** Create user group Creates a new user group, optionally with initial members */
    @POST("/api/v2/usergroups")
    public suspend fun createUserGroup(
        @Body
        createUserGroupRequest: io.getstream.feeds.android.network.models.CreateUserGroupRequest
    ): io.getstream.feeds.android.network.models.CreateUserGroupResponse

    /** Search user groups Searches user groups by name prefix for autocomplete */
    @GET("/api/v2/usergroups/search")
    public suspend fun searchUserGroups(
        @Query("query") query: String,
        @Query("limit") limit: Int? = null,
        @Query("name_gt") nameGt: String? = null,
        @Query("id_gt") idGt: String? = null,
        @Query("team_id") teamId: String? = null,
    ): io.getstream.feeds.android.network.models.SearchUserGroupsResponse

    /** Delete user group Deletes a user group and all its members */
    @DELETE("/api/v2/usergroups/{id}")
    public suspend fun deleteUserGroup(
        @Path("id") id: String,
        @Query("team_id") teamId: String? = null,
    ): io.getstream.feeds.android.network.models.Response

    /** Get user group Gets a user group by ID, including its members */
    @GET("/api/v2/usergroups/{id}")
    public suspend fun getUserGroup(
        @Path("id") id: String,
        @Query("team_id") teamId: String? = null,
    ): io.getstream.feeds.android.network.models.GetUserGroupResponse

    /** Update user group Updates a user group's name and/or description. team_id is immutable. */
    @PUT("/api/v2/usergroups/{id}")
    public suspend fun updateUserGroup(
        @Path("id") id: String,
        @Body
        updateUserGroupRequest: io.getstream.feeds.android.network.models.UpdateUserGroupRequest,
    ): io.getstream.feeds.android.network.models.UpdateUserGroupResponse

    /** Update user group Updates a user group's name and/or description. team_id is immutable. */
    @PUT("/api/v2/usergroups/{id}")
    public suspend fun updateUserGroup(
        @Path("id") id: String
    ): io.getstream.feeds.android.network.models.UpdateUserGroupResponse

    /**
     * Add user group members Adds members to a user group. All user IDs must exist. The operation
     * is all-or-nothing.
     */
    @POST("/api/v2/usergroups/{id}/members")
    public suspend fun addUserGroupMembers(
        @Path("id") id: String,
        @Body
        addUserGroupMembersRequest:
            io.getstream.feeds.android.network.models.AddUserGroupMembersRequest,
    ): io.getstream.feeds.android.network.models.AddUserGroupMembersResponse

    /**
     * Remove user group members Removes members from a user group. Users already not in the group
     * are silently ignored.
     */
    @POST("/api/v2/usergroups/{id}/members/delete")
    public suspend fun removeUserGroupMembers(
        @Path("id") id: String,
        @Body
        removeUserGroupMembersRequest:
            io.getstream.feeds.android.network.models.RemoveUserGroupMembersRequest,
    ): io.getstream.feeds.android.network.models.RemoveUserGroupMembersResponse

    /** Query users Find and filter users */
    @GET("/api/v2/users")
    public suspend fun queryUsers(
        @Query("connection_id") connectionId: String? = null,
        @Query("payload")
        payload:
            io.getstream.feeds.android.network.infrastructure.JsonQuery<
                io.getstream.feeds.android.network.models.QueryUsersPayload
            >? =
            null,
    ): io.getstream.feeds.android.network.models.QueryUsersResponse

    /** Partially update user Updates certain fields of the user */
    @PATCH("/api/v2/users")
    public suspend fun updateUsersPartial(
        @Body
        updateUsersPartialRequest:
            io.getstream.feeds.android.network.models.UpdateUsersPartialRequest
    ): io.getstream.feeds.android.network.models.UpdateUsersResponse

    /** Upsert users Update or create users in bulk */
    @POST("/api/v2/users")
    public suspend fun updateUsers(
        @Body updateUsersRequest: io.getstream.feeds.android.network.models.UpdateUsersRequest
    ): io.getstream.feeds.android.network.models.UpdateUsersResponse

    /** Get list of blocked Users Get list of blocked Users */
    @GET("/api/v2/users/block")
    public suspend fun getBlockedUsers():
        io.getstream.feeds.android.network.models.GetBlockedUsersResponse

    /** Block user Block users */
    @POST("/api/v2/users/block")
    public suspend fun blockUsers(
        @Body blockUsersRequest: io.getstream.feeds.android.network.models.BlockUsersRequest
    ): io.getstream.feeds.android.network.models.BlockUsersResponse

    /** Get user live locations Retrieves all active live locations for a user */
    @GET("/api/v2/users/live_locations")
    public suspend fun getUserLiveLocations():
        io.getstream.feeds.android.network.models.SharedLocationsResponse

    /**
     * Update live location Updates an existing live location with new coordinates or expiration
     * time
     */
    @PUT("/api/v2/users/live_locations")
    public suspend fun updateLiveLocation(
        @Body
        updateLiveLocationRequest:
            io.getstream.feeds.android.network.models.UpdateLiveLocationRequest
    ): io.getstream.feeds.android.network.models.SharedLocationResponse

    /** Unblock user Unblock users */
    @POST("/api/v2/users/unblock")
    public suspend fun unblockUsers(
        @Body unblockUsersRequest: io.getstream.feeds.android.network.models.UnblockUsersRequest
    ): io.getstream.feeds.android.network.models.UnblockUsersResponse
}
