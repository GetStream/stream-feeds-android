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
package io.getstream.feeds.android.client.api.state

import io.getstream.feeds.android.client.api.file.FeedUploadPayload
import io.getstream.feeds.android.client.api.model.ActivityData
import io.getstream.feeds.android.client.api.model.BookmarkData
import io.getstream.feeds.android.client.api.model.CommentData
import io.getstream.feeds.android.client.api.model.FeedAddActivityRequest
import io.getstream.feeds.android.client.api.model.FeedData
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.model.FeedMemberData
import io.getstream.feeds.android.client.api.model.FeedsReactionData
import io.getstream.feeds.android.client.api.model.FollowData
import io.getstream.feeds.android.client.api.model.ModelUpdates
import io.getstream.feeds.android.client.api.model.request.ActivityAddCommentRequest
import io.getstream.feeds.android.network.models.AddBookmarkRequest
import io.getstream.feeds.android.network.models.AddCommentReactionRequest
import io.getstream.feeds.android.network.models.AddReactionRequest
import io.getstream.feeds.android.network.models.CreatePollRequest
import io.getstream.feeds.android.network.models.FollowRequest
import io.getstream.feeds.android.network.models.MarkActivityRequest
import io.getstream.feeds.android.network.models.UpdateActivityRequest
import io.getstream.feeds.android.network.models.UpdateBookmarkRequest
import io.getstream.feeds.android.network.models.UpdateCommentRequest
import io.getstream.feeds.android.network.models.UpdateFeedMembersRequest
import io.getstream.feeds.android.network.models.UpdateFeedRequest

/**
 * A feed represents a collection of activities and provides methods to interact with them.
 *
 * The `Feed` class is the primary interface for working with feeds in the Stream Feeds SDK. It
 * provides functionality for:
 * - Creating and managing feed data
 * - Adding, updating, and deleting activities
 * - Managing comments, reactions, and bookmarks
 * - Handling follows and feed memberships
 * - Creating polls and managing poll interactions
 * - Pagination and querying of feed content
 *
 * Each feed instance is associated with a specific feed ID and maintains its own state that can be
 * observed for real-time updates. The feed state includes activities, followers, members, and other
 * feed-related data.
 */
public interface Feed {

    /**
     * The unique identifier for this feed.
     *
     * This property provides access to the feed's identifier, which is used to distinguish this
     * feed from other feeds in the system. The feed ID is composed of a group and an ID component
     * that together form a unique reference to this specific feed.
     */
    public val fid: FeedId

    /** An observable object representing the current state of the feed. */
    public val state: FeedState

    /**
     * Fetches or creates the feed based on the current feed query.
     *
     * This method will either retrieve an existing feed, or create a new one if it doesn't exist.
     * The feed state will be updated with the fetched data including activities, followers, and
     * other feed information.
     *
     * @return A [Result] containing the [FeedData] if successful, or an error if the operation
     *   fails.
     */
    public suspend fun getOrCreate(): Result<FeedData>

    /**
     * Stops watching the feed. When this method is called, you will not receive any web socket
     * events for the feed anymore.
     *
     * @return A [Result] indicating success or failure of the stop operation.
     */
    public suspend fun stopWatching(): Result<Unit>

    /**
     * Updates the feed with the provided request data.
     *
     * @param request The update request containing the new feed data.
     * @return A [Result] containing the updated [FeedData] if successful, or an error if the
     *   operation fails.
     */
    public suspend fun updateFeed(request: UpdateFeedRequest): Result<FeedData>

    /**
     * Deletes the feed.
     *
     * @param hardDelete If `true`, the feed will be permanently deleted. If `false`, it will be
     *   soft deleted. (default is `false`).
     * @return A [Result] indicating success or failure of the deletion operation.
     */
    public suspend fun deleteFeed(hardDelete: Boolean = false): Result<Unit>

    /**
     * Adds a new activity to the feed.
     *
     * @param request The request containing the activity data to add.
     * @return A [Result] containing the added [ActivityData] if successful, or an error if the
     *   operation fails.
     */
    public suspend fun addActivity(
        request: FeedAddActivityRequest,
        attachmentUploadProgress: ((FeedUploadPayload, Double) -> Unit)? = null,
    ): Result<ActivityData>

    /**
     * Updates an existing activity in the feed.
     *
     * @param id The unique identifier of the activity to update.
     * @param request The request containing the updated activity data.
     * @return A [Result] containing the updated [ActivityData] if successful, or an error if the
     *   operation fails.
     */
    public suspend fun updateActivity(
        id: String,
        request: UpdateActivityRequest,
    ): Result<ActivityData>

    /**
     * Deletes an activity from the feed.
     *
     * @param id The unique identifier of the activity to delete.
     * @param hardDelete If `true`, the activity will be permanently deleted. If `false`, it will be
     *   soft deleted. (default is `false`)
     * @return A [Result] indicating success or failure of the deletion operation.
     */
    public suspend fun deleteActivity(id: String, hardDelete: Boolean = false): Result<Unit>

    /**
     * Marks an activity as read or unread.
     *
     * @param request The request containing the mark activity data.
     * @return A [Result] indicating success or failure of the operation.
     */
    public suspend fun markActivity(request: MarkActivityRequest): Result<Unit>

    /**
     * Creates a repost of an existing activity.
     *
     * @param activityId The unique identifier of the activity to repost.
     * @param text Optional text to add to the repost.
     * @return A [Result] containing the reposted [ActivityData] if successful, or an error if the
     *   operation fails.
     */
    public suspend fun repost(activityId: String, text: String?): Result<ActivityData>

    /**
     * Loads more activities using the next page token from the previous query.
     *
     * @param limit Optional limit for the number of activities to load. If `null`, uses the default
     *   limit.
     * @return A [Result] containing a list of additional [ActivityData] if successful, or an error
     *   if the operation fails.
     */
    public suspend fun queryMoreActivities(limit: Int? = null): Result<List<ActivityData>>

    /**
     * Adds an activity to the user's bookmarks.
     *
     * @param activityId The unique identifier of the activity to bookmark.
     * @param request Additional details of for the bookmark.
     * @return A [Result] containing the created [BookmarkData] if successful, or an error if the
     *   operation fails.
     */
    public suspend fun addBookmark(
        activityId: String,
        request: AddBookmarkRequest = AddBookmarkRequest(),
    ): Result<BookmarkData>

    /**
     * Updates an existing bookmark for an activity.
     *
     * This method allows you to modify the properties of an existing bookmark, such as changing the
     * folder it belongs to or updating custom data associated with the bookmark.
     *
     * Example:
     * ```kotlin
     * // Move a bookmark to a different folder
     * val updateRequest = UpdateBookmarkRequest(folderId = "new-folder-id")
     * val updatedBookmark = feed.updateBookmark("activity-123", updateRequest)
     *
     * // Update bookmark with custom data
     * val customUpdateRequest = UpdateBookmarkRequest(
     *   folderId = "favorites",
     *   custom = mapOf("note" to "Important article")
     * )
     * val bookmark = feed.updateBookmark("activity-456", customUpdateRequest)
     * ```
     *
     * @param activityId The unique identifier of the activity whose bookmark should be updated.
     * @param request The update request containing the new bookmark properties to apply.
     * @return A [Result] containing the updated [BookmarkData] if successful, or an error if the
     *   operation fails.
     */
    public suspend fun updateBookmark(
        activityId: String,
        request: UpdateBookmarkRequest,
    ): Result<BookmarkData>

    /**
     * Removes an activity from the user's bookmarks.
     *
     * @param activityId The unique identifier of the activity to remove from bookmarks.
     * @param folderId Optional folder identifier. If provided, removes the bookmark from the
     *   specific folder.
     * @return A [Result] containing the deleted [BookmarkData] if successful, or an error if the
     *   operation fails.
     */
    public suspend fun deleteBookmark(
        activityId: String,
        folderId: String? = null,
    ): Result<BookmarkData>

    /**
     * Gets a specific comment by its identifier.
     *
     * @param commentId The unique identifier of the comment to retrieve.
     * @return A [Result] containing the [CommentData] if successful, or an error if the operation
     *   fails.
     */
    public suspend fun getComment(commentId: String): Result<CommentData>

    /**
     * Adds a new comment to activity with id.
     *
     * @param request The request containing the comment data to add.
     * @return A [Result] containing the added [CommentData] if successful, or an error if the
     *   operation fails.
     */
    public suspend fun addComment(
        request: ActivityAddCommentRequest,
        attachmentUploadProgress: ((FeedUploadPayload, Double) -> Unit)? = null,
    ): Result<CommentData>

    /**
     * Updates an existing comment with the provided request data.
     *
     * This method allows you to modify the content and properties of an existing comment. You can
     * update the comment text, attachments, or other comment-specific data.
     *
     * @param commentId The unique identifier of the comment to update.
     * @param request The request containing the updated comment data.
     * @return A [Result] containing the updated [CommentData] if successful, or an error if the
     *   operation fails.
     */
    public suspend fun updateComment(
        commentId: String,
        request: UpdateCommentRequest,
    ): Result<CommentData>

    /**
     * Removes a comment for id.
     *
     * @param commentId The unique identifier of the comment to remove.
     * @param hardDelete If `true`, the comment will be permanently deleted. Otherwise, it will be
     *   soft-deleted.
     * @return A [Result] indicating success or failure of the deletion operation.
     */
    public suspend fun deleteComment(commentId: String, hardDelete: Boolean? = null): Result<Unit>

    /**
     * Queries for feed suggestions that the current user might want to follow.
     *
     * @param limit Optional limit for the number of suggestions to return. If `null`, uses the
     *   default limit.
     * @return A [Result] containing a list of [FeedData] representing the suggested feeds if
     *   successful, or an error if the operation fails.
     */
    public suspend fun queryFollowSuggestions(limit: Int?): Result<List<FeedData>>

    /**
     * Follows another feed.
     *
     * @param targetFid The target feed identifier.
     * @param createNotificationActivity Whether the action is added to the notification feed.
     * @param custom Additional data for the request.
     * @param pushPreference Push notification preferences for the follow request.
     */
    public suspend fun follow(
        targetFid: FeedId,
        createNotificationActivity: Boolean? = null,
        custom: Map<String, Any>? = null,
        pushPreference: FollowRequest.PushPreference? = null,
    ): Result<FollowData>

    /**
     * Unfollows another feed.
     *
     * @param targetFid The target feed identifier to unfollow.
     * @return A [Result] indicating success or failure of the unfollow operation.
     */
    public suspend fun unfollow(targetFid: FeedId): Result<Unit>

    /**
     * Accepts a follow request from another feed.
     *
     * @param sourceFid The feed identifier of the requested feed.
     * @param role The role for the requesting feed.
     * @return A [Result] containing the accepted[FollowData] if successful, or an error if the
     *   operation fails.
     */
    public suspend fun acceptFollow(sourceFid: FeedId, role: String? = null): Result<FollowData>

    /**
     * Rejects a follow request from another feed.
     *
     * @param sourceFid The feed identifier of the requested feed.
     * @return A [Result] containing the rejected [FollowData] if successful, or an error if the
     *   operation fails.
     */
    public suspend fun rejectFollow(sourceFid: FeedId): Result<FollowData>

    /**
     * Fetches the initial list of members based on the current query configuration.
     *
     * This method loads the first page of members according to the query's filters, sorting, and
     * limit parameters. The results are stored in the state and can be accessed through the
     * [state.members] property.
     *
     * @return A [Result] containing a list of [FeedMemberData] if successful, or an error if the
     *   operation fails.
     */
    public suspend fun queryFeedMembers(): Result<List<FeedMemberData>>

    /**
     * Loads the next page of members if more are available.
     *
     * This method fetches additional members using the pagination information from the previous
     * request. If no more members are available, an empty list is returned.
     *
     * @param limit Optional limit for the number of members to fetch. If not specified, uses the
     *   limit from the original query.
     * @return A [Result] containing a list of additional [FeedMemberData] if successful, or an
     *   error if the operation fails. Returns an empty array if no more members are available.
     */
    public suspend fun queryMoreFeedMembers(limit: Int? = null): Result<List<FeedMemberData>>

    /**
     * Updates feed members based on the provided request.
     *
     * @param request The update request containing the member changes to apply.
     * @return A [Result] containing the updated [FeedMemberData] if successful, or an error if the
     *   operation fails.
     */
    public suspend fun updateFeedMembers(
        request: UpdateFeedMembersRequest
    ): Result<ModelUpdates<FeedMemberData>>

    /**
     * Accepts a feed member invitation.
     *
     * @return A [Result] containing the accepted [FeedMemberData] if successful, or an error if the
     *   operation fails.
     */
    public suspend fun acceptFeedMember(): Result<FeedMemberData>

    /**
     * Rejects a feed member invitation.
     *
     * @return A [Result] containing the rejected [FeedMemberData] if successful, or an error if the
     *   operation fails.
     */
    public suspend fun rejectFeedMember(): Result<FeedMemberData>

    /**
     * Adds a reaction to an activity.
     *
     * @param activityId The unique identifier of the activity to react to.
     * @param request The request containing the reaction data.
     * @return A [Result] containing the added [FeedsReactionData] if successful, or an error if the
     *   operation fails.
     */
    public suspend fun addActivityReaction(
        activityId: String,
        request: AddReactionRequest,
    ): Result<FeedsReactionData>

    /**
     * Deletes a reaction from an activity.
     *
     * @param activityId The unique identifier of the activity from which to delete the reaction.
     * @param type The type of reaction to delete.
     * @return A [Result] containing the deleted [FeedsReactionData] if successful, or an error if
     *   the operation fails.
     */
    public suspend fun deleteActivityReaction(
        activityId: String,
        type: String,
    ): Result<FeedsReactionData>

    /**
     * Adds a reaction to a comment.
     *
     * @param commentId The unique identifier of the comment to react to.
     * @param request The request containing the reaction data.
     * @return A [Result] containing the added [FeedsReactionData] if successful, or an error if the
     *   operation fails.
     */
    public suspend fun addCommentReaction(
        commentId: String,
        request: AddCommentReactionRequest,
    ): Result<FeedsReactionData>

    /**
     * Deletes a reaction from a comment.
     *
     * @param commentId The unique identifier of the comment from which to delete the reaction.
     * @param type The type of reaction to delete.
     * @return A [Result] containing the deleted [FeedsReactionData] if successful, or an error if
     *   the operation fails.
     */
    public suspend fun deleteCommentReaction(
        commentId: String,
        type: String,
    ): Result<FeedsReactionData>

    /**
     * Creates a new poll and adds it as an activity to the feed.
     *
     * @param request The request containing the poll data to create.
     * @param activityType The type of activity to create for the poll.
     * @return A [Result] containing the created [ActivityData] with poll if successful, or an error
     *   if the operation fails.
     */
    public suspend fun createPoll(
        request: CreatePollRequest,
        activityType: String,
    ): Result<ActivityData>
}
