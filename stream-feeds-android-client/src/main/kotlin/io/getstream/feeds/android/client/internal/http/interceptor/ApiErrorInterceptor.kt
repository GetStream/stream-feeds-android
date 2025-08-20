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
package io.getstream.feeds.android.client.internal.http.interceptor

import io.getstream.android.core.error.APIError
import io.getstream.android.core.parser.JsonParser
import io.getstream.feeds.android.client.api.error.StreamApiException
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor that handles API errors by parsing the response body and throwing a
 * [StreamApiException] if the response is not successful.
 *
 * @param jsonParser The JSON parser to parse the error response.
 */
internal class ApiErrorInterceptor(private val jsonParser: JsonParser) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        if (!response.isSuccessful) {
            // Try to parse a Stream API error from the response body
            val errorBody = response.peekBody(Long.MAX_VALUE).string()
            jsonParser.fromJsonOrError(errorBody, APIError::class.java).map { apiError ->
                throw StreamApiException(apiError)
            }
        }
        return response
    }
}
