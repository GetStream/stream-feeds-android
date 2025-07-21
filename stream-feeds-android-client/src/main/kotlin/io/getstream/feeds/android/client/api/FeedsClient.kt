package io.getstream.feeds.android.client.api

import android.content.Context
import io.getstream.android.core.user.ApiKey
import io.getstream.android.core.user.User
import io.getstream.android.core.user.UserToken
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.state.BookmarkList
import io.getstream.feeds.android.client.api.state.BookmarksQuery
import io.getstream.feeds.android.client.api.state.Feed
import io.getstream.feeds.android.client.api.state.FeedList
import io.getstream.feeds.android.client.api.state.FeedQuery
import io.getstream.feeds.android.client.api.state.FeedsQuery
import io.getstream.feeds.android.client.api.state.FollowList
import io.getstream.feeds.android.client.api.state.FollowsQuery
import io.getstream.feeds.android.client.api.state.MemberList
import io.getstream.feeds.android.client.api.state.MembersQuery
import io.getstream.feeds.android.client.internal.client.createFeedsClient


/**
 * Single entry point for interacting with the Stream Feeds service.
 */
public interface FeedsClient {

    /**
     * Establishes a connection to the Stream service.
     *
     * This method sets up authentication and initializes the WebSocket connection for real-time
     * updates.
     * It should be called before using any other client functionality.
     *
     * @return A [Result] indicating success or failure of the connection attempt.
     */
    public suspend fun connect(): Result<Unit>

    /**
     * Disconnects the current [FeedsClient].
     */
    public suspend fun disconnect(): Result<Unit>

    /**
     * Creates a feed instance for the specified group and id.
     *
     * This method creates a [Feed] object that represents a specific feed.
     * The feed can be used to fetch activities, manage follows, and receive real-time updates.
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
     * This method creates a [Feed] object that represents a specific feed.
     * The feed can be used to fetch activities, manage follows, and receive real-time updates.
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
     * This method creates a `FeedList` object that represents a collection of feeds matching the
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
     * relationships
     */
    public fun followList(query: FollowsQuery): FollowList

    // TODO: Activities operations

    /**
     * Creates a bookmark list instance based on the provided query.
     *
     * This method creates a `BookmarkList` object that represents a collection of bookmarks
     * matching the specified query. The bookmark list can be used to fetch user bookmarks,
     * manage bookmark folders, and receive real-time updates for bookmark-related events.
     * */
    public fun bookmarkList(query: BookmarksQuery): BookmarkList

    /**
     * Creates a member list instance based on the provided query.
     *
     * This method creates a [MemberList] object that represents a collection of feed members
     * matching the specified query. The member list can be used to fetch feed members, manage
     * member pagination,  and receive real-time updates for member-related events.
     *
     * @param query The members query containing filtering, sorting, and pagination parameters.
     * @return A [MemberList] instance that can be used to interact with the collection of feed
     * members.
     */
    public fun memberList(query: MembersQuery): MemberList


}

/**
 * Initializes a new [FeedsClient] with the provided [apiKey], [user], and [token].
 *
 * @param context The Android [Context] for the client.
 * @param apiKey The API key for the client.
 * @param user The user associated with the client.
 * @param token The user token for authentication.
 */
public fun FeedsClient(
    context: Context,
    apiKey: ApiKey,
    user: User,
    token: UserToken,
): FeedsClient = createFeedsClient(
    context = context,
    apiKey = apiKey,
    user = user,
    token = token,
)
