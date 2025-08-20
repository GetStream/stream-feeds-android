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
package io.getstream.feeds.android.client.api

import android.content.Context
import io.getstream.android.core.user.ApiKey
import io.getstream.android.core.user.User
import io.getstream.android.core.user.UserTokenProvider
import io.getstream.feeds.android.client.api.model.ActivityData
import io.getstream.feeds.android.client.api.model.AppData
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.model.FeedsConfig
import io.getstream.feeds.android.client.api.model.PushNotificationsProvider
import io.getstream.feeds.android.client.api.state.Activity
import io.getstream.feeds.android.client.api.state.ActivityCommentList
import io.getstream.feeds.android.client.api.state.ActivityList
import io.getstream.feeds.android.client.api.state.ActivityReactionList
import io.getstream.feeds.android.client.api.state.BookmarkFolderList
import io.getstream.feeds.android.client.api.state.BookmarkList
import io.getstream.feeds.android.client.api.state.CommentList
import io.getstream.feeds.android.client.api.state.CommentReactionList
import io.getstream.feeds.android.client.api.state.CommentReplyList
import io.getstream.feeds.android.client.api.state.Feed
import io.getstream.feeds.android.client.api.state.FeedList
import io.getstream.feeds.android.client.api.state.FollowList
import io.getstream.feeds.android.client.api.state.MemberList
import io.getstream.feeds.android.client.api.state.ModerationConfigList
import io.getstream.feeds.android.client.api.state.PollList
import io.getstream.feeds.android.client.api.state.PollVoteList
import io.getstream.feeds.android.client.api.state.query.ActivitiesQuery
import io.getstream.feeds.android.client.api.state.query.ActivityCommentsQuery
import io.getstream.feeds.android.client.api.state.query.ActivityReactionsQuery
import io.getstream.feeds.android.client.api.state.query.BookmarkFoldersQuery
import io.getstream.feeds.android.client.api.state.query.BookmarksQuery
import io.getstream.feeds.android.client.api.state.query.CommentReactionsQuery
import io.getstream.feeds.android.client.api.state.query.CommentRepliesQuery
import io.getstream.feeds.android.client.api.state.query.CommentsQuery
import io.getstream.feeds.android.client.api.state.query.FeedQuery
import io.getstream.feeds.android.client.api.state.query.FeedsQuery
import io.getstream.feeds.android.client.api.state.query.FollowsQuery
import io.getstream.feeds.android.client.api.state.query.MembersQuery
import io.getstream.feeds.android.client.api.state.query.ModerationConfigsQuery
import io.getstream.feeds.android.client.api.state.query.PollVotesQuery
import io.getstream.feeds.android.client.api.state.query.PollsQuery
import io.getstream.feeds.android.client.internal.client.createFeedsClient
import io.getstream.feeds.android.core.generated.models.ActivityRequest
import io.getstream.feeds.android.core.generated.models.AddActivityRequest
import io.getstream.feeds.android.core.generated.models.DeleteActivitiesRequest
import io.getstream.feeds.android.core.generated.models.DeleteActivitiesResponse
import io.getstream.feeds.android.core.generated.models.ListDevicesResponse

/** Single entry point for interacting with the Stream Feeds service. */
public interface FeedsClient {

    /**
     * Establishes a connection to the Stream service.
     *
     * This method sets up authentication and initializes the WebSocket connection for real-time
     * updates. It should be called before using any other client functionality.
     *
     * @return A [Result] indicating success or failure of the connection attempt.
     */
    public suspend fun connect(): Result<Unit>

    /** Disconnects the current [FeedsClient]. */
    public suspend fun disconnect(): Result<Unit>

    /**
     * Creates a feed instance for the specified group and id.
     *
     * This method creates a [Feed] object that represents a specific feed. The feed can be used to
     * fetch activities, manage follows, and receive real-time updates.
     *
     * Example:
     * ```kotlin
     * val userFeed = client.feed(group = "user", id = "john")
     * val timelineFeed = client.feed(group = "timeline", id = "flat")
     * ```
     *
     * @param group The feed group identifier (e.g., "user", "timeline", "notification").
     * @param id The specific feed identifier within the group (e.g., "john", "flat").
     * @return A [Feed] instance that can be used to interact with the specified feed.
     */
    public fun feed(group: String, id: String): Feed

    /**
     * Creates a feed instance for the specified feed ID.
     *
     * This method creates a [Feed] object that represents a specific feed. The feed can be used to
     * fetch activities, manage follows, and receive real-time updates.
     *
     * Example:
     * ```kotlin
     * val feedId = FeedId(group = "user", id = "john")
     * val userFeed = client.feed(feedId)
     * ```
     *
     * @param fid The [FeedId] identifier containing the group and ID.
     * @return A [Feed] instance that can be used to interact with the specified feed.
     */
    public fun feed(fid: FeedId): Feed

    /**
     * Fetches a feed based on the provided [FeedQuery].
     *
     * This method creates a [Feed] object using a [FeedQuery] that can include additional
     * configuration such as activity filters, limits, and feed data for creation.
     *
     * Example:
     * ```kotlin
     * val query = FeedQuery(
     *   fid = FeedId(group = "user", id = "john"),
     *   activityLimit = 20,
     *   data = FeedInput(name = "John's Feed")
     * )
     * val feed = client.feed(query)
     * ```
     *
     * @param query The feed query containing the feed identifier and optional configuration.
     * @return A [Feed] instance that can be used to interact with the specified feed.
     */
    public fun feed(query: FeedQuery): Feed

    /**
     * Creates a feed list instance based on the provided query.
     *
     * This method creates a [FeedList] object that represents a collection of feeds matching the
     * specified query. The feed list can be used to fetch multiple feeds, manage feed groups, and
     * receive real-time updates for all feeds in the list.
     *
     * @param query The feeds query containing filtering and pagination parameters.
     * @return A [FeedList] instance that can be used to interact with the collection of feeds.
     */
    public fun feedList(query: FeedsQuery): FeedList

    /**
     * Creates a follow list instance based on the provided query.
     *
     * This method creates a [FollowList] object that represents a collection of follow
     * relationships matching the specified query. The follow list can be used to fetch followers,
     * following relationships, and manage follow data with pagination support.
     *
     * @param query The follows query containing filtering, sorting, and pagination parameters.
     * @return A [FollowList] instance that can be used to interact with the collection of follow
     *   relationships
     */
    public fun followList(query: FollowsQuery): FollowList

    // TODO: Event subscription

    /**
     * Creates an activity instance for the specified activity ID and feed ID.
     *
     * This method creates an [Activity] object that represents a specific activity within a feed.
     * The activity can be used to manage comments, reactions, and other activity-specific
     * operations.
     *
     * Example:
     * ```kotlin
     * val feedId = FeedId(group = "user", id = "john")
     * val activity = client.activity(activityId = "activity-123", fid = feedId)
     * ```
     *
     * @param activityId The unique identifier of the activity.
     * @param fid The feed identifier where the activity belongs.
     */
    public fun activity(activityId: String, fid: FeedId): Activity

    /**
     * Creates an activity list instance based on the provided query.
     *
     * This method creates an [ActivityList] object that represents a collection of activities
     * matching the specified query. The activity list can be used to fetch activities, manage
     * activity pagination, and receive real-time updates for activity-related events.
     *
     * @param query The activities query containing filtering, sorting, and pagination parameters.
     * @return A [ActivityList] instance that can be used to interact with the collection of
     *   activities.
     */
    public fun activityList(query: ActivitiesQuery): ActivityList

    /**
     * Creates an activity reaction list instance based on the provided query.
     *
     * This method creates an `ActivityReactionList` object that represents a collection of
     * reactions for a specific activity. The activity reaction list can be used to fetch reactions
     * for an activity, manage reaction pagination, and receive real-time updates for
     * reaction-related events.
     *
     * @param query The activity reactions query containing the activity identifier and pagination
     *   parameters.
     * @return An [ActivityReactionList] instance that can be used to interact with the collection
     *   of activity reactions.
     */
    public fun activityReactionList(query: ActivityReactionsQuery): ActivityReactionList

    /**
     * Adds a new activity to the specified feeds.
     *
     * @param request The request containing the activity data to be added.
     * @return A [Result] containing the response with the added activity details if successful, or
     *   an error if the request fails.
     */
    public suspend fun addActivity(request: AddActivityRequest): Result<ActivityData>

    /**
     * Upserts (inserts or updates) multiple activities.
     *
     * @param activities The list of activities to be upserted.
     * @return A [Result] containing a list of [ActivityData] representing the upserted activities
     *   if successful, or an error if the request fails.
     */
    public suspend fun upsertActivities(
        activities: List<ActivityRequest>
    ): Result<List<ActivityData>>

    /**
     * Deletes multiple activities from the specified feeds.
     *
     * @param request The request containing the activities to delete.
     * @return A [Result] indicating success or failure of the deletion operation.
     */
    public suspend fun deleteActivities(
        request: DeleteActivitiesRequest
    ): Result<DeleteActivitiesResponse>

    /**
     * Creates a bookmark list instance based on the provided query.
     *
     * This method creates a [BookmarkList] object that represents a collection of bookmarks
     * matching the specified query. The bookmark list can be used to fetch user bookmarks, manage
     * bookmark folders, and receive real-time updates for bookmark-related events.
     */
    public fun bookmarkList(query: BookmarksQuery): BookmarkList

    /**
     * Creates a bookmark folder list instance based on the provided query.
     *
     * This method creates a [BookmarkFolderList] object that represents a collection of bookmark
     * folders matching the specified query. The bookmark folder list can be used to fetch user
     * bookmark folders, manage folder organization, and receive real-time updates for
     * folder-related events..
     *
     * @param query The bookmark folders query containing filtering, sorting, and pagination
     *   parameters.
     * @return A [BookmarkFolderList] instance that can be used to interact with the collection of
     *   bookmark folders.
     */
    public fun bookmarkFolderList(query: BookmarkFoldersQuery): BookmarkFolderList

    /**
     * Creates a comment list instance based on the provided query.
     *
     * This method creates a [CommentList] object that represents a collection of comments matching
     * the specified query. The comment list can be used to fetch comments, manage comment
     * pagination, and receive real-time updates for comment-related events.
     *
     * @param query The comments query containing filtering, sorting, and pagination parameters.
     * @return A [CommentList] instance that can be used to interact with a collection of comments.
     */
    public fun commentList(query: CommentsQuery): CommentList

    /**
     * Creates an activity comment list instance based on the provided query.
     *
     * This method creates an [ActivityCommentList] object that represents a collection of comments
     * for a specific activity. The activity comment list can be used to fetch comments for an
     * activity, manage comment pagination, and receive real-time updates for comment-related
     * events.
     *
     * @param query The activity comments query containing the activity identifier and pagination
     *   parameters.
     * @return An [ActivityCommentList] instance that can be used to interact with the collection of
     *   activity comments.
     */
    public fun activityCommentList(query: ActivityCommentsQuery): ActivityCommentList

    /**
     * Creates a comment reply list instance based on the provided query.
     *
     * This method creates a [CommentReplyList] object that represents a collection of replies for a
     * specific comment. The comment reply list can be used to fetch replies to a comment, manage
     * reply pagination, and receive real-time updates for reply-related events.
     *
     * @param query The comment replies query containing the comment identifier and pagination
     *   parameters.
     * @return A [CommentReplyList] instance that can be used to interact with the collection of
     *   comment replies.
     */
    public fun commentReplyList(query: CommentRepliesQuery): CommentReplyList

    /**
     * Creates a comment reaction list instance based on the provided query.
     *
     * This method creates a [CommentReactionList] object that represents a collection of reactions
     * for a specific comment. The comment reaction list can be used to fetch reactions for a
     * comment, manage reaction pagination, and receive real-time updates for reaction-related
     * events.
     *
     * @param query The comment reactions query containing the comment identifier and pagination
     *   parameters.
     * @return A [CommentReactionList] instance that can be used to interact with the collection of
     *   comment reactions.
     */
    public fun commentReactionList(query: CommentReactionsQuery): CommentReactionList

    /**
     * Creates a member list instance based on the provided query.
     *
     * This method creates a [MemberList] object that represents a collection of feed members
     * matching the specified query. The member list can be used to fetch feed members, manage
     * member pagination, and receive real-time updates for member-related events.
     *
     * @param query The members query containing filtering, sorting, and pagination parameters.
     * @return A [MemberList] instance that can be used to interact with the collection of feed
     *   members.
     */
    public fun memberList(query: MembersQuery): MemberList

    /**
     * Creates a poll vote list instance based on the provided query.
     *
     * This method creates a [PollVoteList] object that represents a collection of poll votes
     * matching the specified query. The poll vote list can be used to fetch poll votes, manage vote
     * pagination, and receive real-time updates for vote-related events.
     *
     * @param query The poll votes query containing filtering, sorting, and pagination parameters.
     * @return A [PollVoteList] instance that can be used to interact with the collection of poll
     *   votes.
     */
    public fun pollVoteList(query: PollVotesQuery): PollVoteList

    /**
     * Creates a poll list instance based on the provided query.
     *
     * This method creates a [PollList] object that represents a collection of polls matching the
     * specified query. The poll list can be used to fetch polls, manage poll pagination, and
     * receive real-time updates for poll-related events.
     *
     * @param query The polls query containing filtering, sorting, and pagination parameters.
     * @return A [PollList] instance that can be used to interact with the collection of polls.
     */
    public fun pollList(query: PollsQuery): PollList

    /**
     * Creates a moderation configuration list instance based on the provided query.
     *
     * This method creates a `ModerationConfigList` object that represents a collection of
     * moderation configurations matching the specified query. The moderation configuration list can
     * be used to fetch configurations, manage configuration pagination, and receive real-time
     * updates for configuration-related events.
     *
     * @param query The moderation configurations query containing filtering, sorting, and
     *   pagination parameters.
     * @return A [ModerationConfigList] instance that can be used to interact with the collection of
     *   moderation configurations.
     */
    public fun moderationConfigList(query: ModerationConfigsQuery): ModerationConfigList

    /**
     * Retrieves the application configuration and settings.
     *
     * This method fetches the current application data including configuration settings, file
     * upload configurations, and feature flags.
     *
     * The returned `AppData` contains:
     * - **Async URL Enrichment**: Whether automatic URL enrichment is enabled
     * - **Auto Translation**: Whether automatic translation is enabled
     * - **File Upload Config**: Configuration for file uploads including allowed extensions, MIME
     *   types, and size limits
     * - **Image Upload Config**: Configuration for image uploads including allowed extensions, MIME
     *   types, and size limits
     * - **Application Name**: The name of the application
     *
     * **Important**: The result is cached after the first successful request to avoid unnecessary
     * API calls.
     *
     * @return A [Result] containing the [AppData] if successful, or an error if the request fails.
     */
    public suspend fun getApp(): Result<AppData>

    /**
     * Queries all devices associated with the current user.
     *
     * @return A [Result] containing a list of devices if successful, or an error if the request
     *   fails.
     */
    public suspend fun queryDevices(): Result<ListDevicesResponse>

    /**
     * Creates a new device for push notifications.
     *
     * @param id The unique identifier for the device.
     * @param pushProvider The push notifications provider of the device (e.g. Firebase, Huawei).
     * @param pushProviderName The name of the push provider.
     * @return A [Result] indicating success or failure of the device creation operation.
     */
    public suspend fun createDevice(
        id: String,
        pushProvider: PushNotificationsProvider,
        pushProviderName: String,
    ): Result<Unit>

    /**
     * Deletes a device by its unique identifier.
     *
     * @param id The unique identifier of the device to be deleted.
     * @return A [Result] indicating success or failure of the deletion operation.
     */
    public suspend fun deleteDevice(id: String): Result<Unit>

    /**
     * Deletes a previously uploaded file from the CDN.
     *
     * This is typically used for videos, or other non-image attachments. The method makes an
     * asynchronous request to the global file deletion endpoint.
     *
     * @param url The URL of the file to be deleted.
     * @return A [Result] indicating success or failure of the deletion operation.
     */
    public suspend fun deleteFile(url: String): Result<Unit>

    /**
     * Deletes a previously uploaded image from the CDN.
     *
     * This is intended for removing images such as user-uploaded photos or thumbnails. The method
     * makes an asynchronous request to the global image deletion endpoint.
     *
     * @param url The URL of the image to be deleted.
     * @return A [Result] indicating success or failure of the deletion operation.
     */
    public suspend fun deleteImage(url: String): Result<Unit>

    /**
     * Provides an instance of [Moderation] for managing moderation-related operations.
     *
     * @return An instance of [Moderation] that can be used to perform moderation actions such as
     *   querying moderation configurations, banning users, muting users, blocking users, and
     *   unblocking users.
     */
    public val moderation: Moderation
}

/**
 * Initializes a new [FeedsClient] with the provided [apiKey], [user], and [token].
 *
 * @param context The Android [Context] for the client.
 * @param apiKey The API key for the client.
 * @param user The user associated with the client.
 * @param tokenProvider The provider for user tokens, used for refreshing tokens as needed.
 * @param config Configuration for the client, such as custom file uploader.
 */
public fun FeedsClient(
    context: Context,
    apiKey: ApiKey,
    user: User,
    tokenProvider: UserTokenProvider,
    config: FeedsConfig = FeedsConfig(),
): FeedsClient =
    createFeedsClient(
        context = context,
        apiKey = apiKey,
        user = user,
        tokenProvider = tokenProvider,
        config = config,
    )
