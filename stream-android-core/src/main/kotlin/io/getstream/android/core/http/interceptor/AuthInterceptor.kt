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
 * authentication. It ensures the token is loaded from the [TokenManager] and handles token
 * expiration. It adds the `stream-auth-type` header with [authType] as value and the
 * `Authorization` header with the retrieved token.
 *
 * @param tokenManager The [TokenManager] to retrieve the token from.
 * @param jsonParser The [JsonParser] to parse potential error responses.
 */
@StreamInternalApi
public class AuthInterceptor(
    private val tokenManager: TokenManager,
    private val jsonParser: JsonParser,
    private val authType: String,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        // Ensure the token is loaded from the TokenManager
        tokenManager.ensureTokenLoaded()
        // Get the original request
        val request = chain.request()
        // Build the request with the token in the Authorization header
        val token = tokenManager.getToken().rawValue
        val newRequest =
            request
                .newBuilder()
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
                    val newRequest =
                        request.newBuilder().header(HEADER_AUTHORIZATION, newToken).build()
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
