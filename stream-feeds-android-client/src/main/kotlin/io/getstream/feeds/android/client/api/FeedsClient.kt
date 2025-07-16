package io.getstream.feeds.android.client.api

import android.content.Context
import io.getstream.android.core.user.ApiKey
import io.getstream.android.core.user.User
import io.getstream.android.core.user.UserToken
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
