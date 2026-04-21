/*
 * Copyright (c) 2014-2026 Stream.io Inc. All rights reserved.
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

package io.getstream.feeds.android.client.internal.client

import io.getstream.android.core.api.authentication.StreamTokenProvider
import io.getstream.android.core.api.http.StreamOkHttpInterceptors
import io.getstream.android.core.api.model.value.StreamApiKey
import io.getstream.android.core.api.model.value.StreamHttpClientInfoHeader
import io.getstream.android.core.api.model.value.StreamToken
import io.getstream.android.core.api.model.value.StreamUserId
import io.getstream.feeds.android.client.api.model.User
import io.getstream.feeds.android.network.apis.FeedsApi
import io.getstream.feeds.android.network.infrastructure.Serializer
import io.getstream.feeds.android.network.models.CreateGuestRequest
import io.getstream.feeds.android.network.models.UserRequest
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create

/**
 * A [StreamTokenProvider] that obtains a token by calling the `POST /api/v2/guest` endpoint.
 *
 * This provider creates a minimal, unauthenticated HTTP client (API key only) to make the guest API
 * call, then returns the access token from the response.
 */
internal class GuestTokenProvider(
    private val apiKey: StreamApiKey,
    private val user: User,
    private val clientInfoHeader: StreamHttpClientInfoHeader,
    private val endpointConfig: EndpointConfig,
) : StreamTokenProvider {
    private val api by lazy(::createGuestApi)

    override suspend fun loadToken(userId: StreamUserId): StreamToken {
        val response =
            api.createGuest(
                CreateGuestRequest(
                    user =
                        UserRequest(
                            id = user.id,
                            name = user.name,
                            image = user.imageURL,
                            custom = user.customData,
                        )
                )
            )
        return StreamToken.fromString(response.accessToken)
    }

    private fun createGuestApi(): FeedsApi {
        val client =
            OkHttpClient.Builder()
                .addInterceptor(StreamOkHttpInterceptors.apiKey(apiKey))
                .addInterceptor(StreamOkHttpInterceptors.clientInfo(clientInfoHeader))
                .addInterceptor { chain ->
                    val request =
                        chain
                            .request()
                            .newBuilder()
                            .addHeader("stream-auth-type", "anonymous")
                            .build()
                    chain.proceed(request)
                }
                .build()

        return Retrofit.Builder()
            .baseUrl(endpointConfig.httpUrl)
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(Serializer.moshi))
            .build()
            .create()
    }
}
