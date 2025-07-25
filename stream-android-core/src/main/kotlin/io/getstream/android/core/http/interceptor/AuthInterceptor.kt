package io.getstream.android.core.http.interceptor

import io.getstream.android.core.error.APIError
import io.getstream.android.core.error.isTokenInvalidErrorCode
import io.getstream.android.core.parser.JsonParser
import io.getstream.android.core.user.TokenManager
import io.getstream.kotlin.base.annotation.marker.StreamInternalApi
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Base class for [AuthInterceptor] that provides common functionality for token-based
 * authentication.
 * It ensures the token is loaded from the [TokenManager] and handles token expiration.
 *
 * @param tokenManager The [TokenManager] to retrieve the token from.
 * @param jsonParser The [JsonParser] to parse potential error responses.
 */
@StreamInternalApi
public abstract class AuthInterceptor(
    private val tokenManager: TokenManager,
    private val jsonParser: JsonParser,
) : Interceptor {

    /**
     * The type of authentication used by this interceptor.
     */
    public abstract val authType: String

    override fun intercept(chain: Interceptor.Chain): Response {
        // Ensure the token is loaded from the TokenManager
        tokenManager.ensureTokenLoaded()
        // Get the original request
        val request = chain.request()
        // Build the request with the token in the Authorization header
        val token = tokenManager.getToken().rawValue
        val newRequest = request.newBuilder()
            .addHeader(HEADER_STREAM_AUTH_TYPE, authType)
            .addHeader(HEADER_AUTHORIZATION, token)
            .build()
        // Proceed with the request
        val response = chain.proceed(newRequest)
        if (!response.isSuccessful) {
            // If the response is not successful, check if the token has expired
            val body = response.peekBody(Long.MAX_VALUE).string()
            jsonParser.fromJsonOrError(body, APIError::class.java).map { apiError ->
                if (apiError.isTokenInvalidErrorCode) {
                    // If the token is invalid, expire it in the TokenManager
                    tokenManager.expireToken()
                    // Load a new token synchronously
                    val newToken = tokenManager.loadSync().rawValue
                    response.close()
                    // Rebuild the request with the new token and retry
                    val newRequest = request.newBuilder()
                        .header(HEADER_AUTHORIZATION, newToken)
                        .build()
                    return chain.proceed(newRequest)
                }
            }
        }
        // Proceed with success or different error responses
        return response
    }

    private companion object {
        const val HEADER_STREAM_AUTH_TYPE = "stream-auth-type"
        const val HEADER_AUTHORIZATION = "Authorization"
    }
}

/**
 * [AuthInterceptor] to be used for anonymous users.
 *
 * It adds the `stream-auth-type` header with value `anonymous` and the `Authorization` header with
 * the retrieved token.
 *
 * @property tokenManager The [TokenManager] to retrieve the token from.
 * @param jsonParser The [JsonParser] to parse potential error responses.
 */
@StreamInternalApi
public class AnonymousAuthInterceptor(
    tokenManager: TokenManager,
    jsonParser: JsonParser,
): AuthInterceptor(tokenManager, jsonParser) {

    override val authType: String
        get() = "anonymous"
}

/**
 * [AuthInterceptor] to be used for authenticated users.
 *
 * It adds the `stream-auth-type` header with value `jwt` and the `Authorization` header with
 *  * the retrieved token.
 *
 * @param tokenManager The [TokenManager] to retrieve the token from.
 * @param jsonParser The [JsonParser] to for parsing potential error responses.
 */
@StreamInternalApi
public class UserTokenAuthInterceptor(
    tokenManager: TokenManager,
    jsonParser: JsonParser,
) : AuthInterceptor(tokenManager, jsonParser) {

    override val authType: String
        get() = "jwt"
}
