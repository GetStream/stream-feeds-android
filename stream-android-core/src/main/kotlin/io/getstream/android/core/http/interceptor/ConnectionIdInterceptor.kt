package io.getstream.android.core.http.interceptor

import io.getstream.kotlin.base.annotation.marker.StreamInternalApi
import okhttp3.Interceptor
import okhttp3.Response

/**
 * An [Interceptor] that adds a connection ID as a query parameter to the request URL.
 *
 * This interceptor is used to track requests associated with a specific connection.
 *
 * @param connectionId A lambda that provides the connection ID to be added to the request URL.
 */
@StreamInternalApi
public class ConnectionIdInterceptor(
    private val connectionId: () -> String,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        // Get the original request and its URL
        val request = chain.request()
        val url = request.url
        // Build the new URL with connection ID as query parameter
        val connectionId = connectionId()
        val urlWithConnectionId = if (connectionId.isNotEmpty()) {
            url.newBuilder()
                .addQueryParameter(QUERY_PARAM_CONNECTION_ID, connectionId)
                .build()
        } else {
            url
        }
        // Create a new request with the updated URL
        val newRequest = request.newBuilder()
            .url(urlWithConnectionId)
            .build()
        return chain.proceed(newRequest)
    }

    private companion object Companion {
        private const val QUERY_PARAM_CONNECTION_ID = "connection_id"
    }
}
