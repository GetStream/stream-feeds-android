package io.getstream.feeds.android.client.internal.socket.common.factory

import io.getstream.android.core.http.XStreamClient
import io.getstream.android.core.user.ApiKey

/**
 * Internal configuration for the Stream socket.
 *
 * @param url The URL to connect to.
 * @param apiKey The API key for authentication.
 * @param authType The type of authentication used (e.g., "jwt").
 * @param xStreamClient The client identifier for the Stream service.
 */
internal class StreamSocketConfig(
    val url: String,
    val apiKey: ApiKey,
    val authType: String,
    val xStreamClient: XStreamClient,
)
