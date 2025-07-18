package io.getstream.feeds.android.client.api

import android.content.Context
import io.getstream.android.core.user.ApiKey
import io.getstream.android.core.user.User
import io.getstream.android.core.user.UserToken
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.state.Feed
import io.getstream.feeds.android.client.api.state.FeedQuery
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
