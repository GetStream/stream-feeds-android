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

import io.getstream.android.core.user.ApiKey
import io.getstream.kotlin.base.annotation.marker.StreamInternalApi
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor adding the API key to the request.
 *
 * @param apiKey The API key to be added to the request headers.
 */
@StreamInternalApi
public class ApiKeyInterceptor(private val apiKey: ApiKey) : Interceptor {

    private companion object {
        private const val API_KEY_PARAM = "api_key"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        // Get the original request URL
        val request = chain.request()
        val url = request.url
        // Add the API key to the URL
        val urlWithApiKey = url.newBuilder().addQueryParameter(API_KEY_PARAM, apiKey.value).build()
        // Build the request with the new URL
        val requestWithApiKey = request.newBuilder().url(urlWithApiKey).build()
        // Proceed with the request
        return chain.proceed(requestWithApiKey)
    }
}
