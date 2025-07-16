package io.getstream.android.core.http.interceptor

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Marker interface denoting [Interceptor] which provides authentication headers for requests.
 */
public interface AuthInterceptor : Interceptor {

    public companion object {
        internal const val HEADER_STREAM_AUTH_TYPE = "stream-auth-type"
        internal const val HEADER_AUTHORIZATION = "Authorization"
    }
}

/**
 * [AuthInterceptor] to be used for anonymous users.
 *
 * It adds the `stream-auth-type` header with value `anonymous` and optionally the `Authorization`
 * header with a token (if the provided [token] is not empty).
 *
 * @property token The token to be added to the `Authorization` header. If empty, the header will
 * not be added.
 */
public class AnonymousAuthInterceptor(private val token: String): AuthInterceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader(AuthInterceptor.HEADER_STREAM_AUTH_TYPE, "anonymous")
            .apply {
                if (token.isNotEmpty()) {
                    addHeader(AuthInterceptor.HEADER_AUTHORIZATION, token)
                }
            }
            .build()
        return chain.proceed(request)
    }
}

/**
 * [AuthInterceptor] to be used for authenticated users.
 *
 * It adds the `stream-auth-type` header with value `jwt` and the `Authorization` header with the
 * provided token.
 * It also appends the `connection_id` query parameter to the request URL if the `connectionId`.
 *
 * TODO: Consider introduction of a TokenManager
 *
 * @param token A function that returns the JWT token to be added to the `Authorization` header.
 * @param connectionId A function that returns the connection ID to be added as a query parameter.
 */
public class TokenAuthInterceptor(
    private val token: () -> String,
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
        // Build the request with the new URL and the token in the Authorization header
        val requestWithToken = request.newBuilder()
            .url(urlWithConnectionId)
            .addHeader(AuthInterceptor.HEADER_STREAM_AUTH_TYPE, "jwt")
            .addHeader(AuthInterceptor.HEADER_AUTHORIZATION, token())
            .build()
        // Proceed with the request
        return chain.proceed(requestWithToken)
    }

    private companion object {
        private const val QUERY_PARAM_CONNECTION_ID = "connection_id"
    }
}
