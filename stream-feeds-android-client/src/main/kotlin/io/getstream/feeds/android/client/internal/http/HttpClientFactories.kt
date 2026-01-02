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

package io.getstream.feeds.android.client.internal.http

import io.getstream.android.core.api.log.StreamLoggerProvider
import io.getstream.android.core.api.model.config.StreamHttpConfig
import io.getstream.feeds.android.client.api.model.FeedsConfig
import io.getstream.feeds.android.client.internal.client.EndpointConfig
import io.getstream.feeds.android.client.internal.logging.createLoggingInterceptor
import io.getstream.feeds.android.network.infrastructure.Serializer
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

internal fun createHttpConfig(
    okHttpBuilder: OkHttpClient.Builder,
    logProvider: StreamLoggerProvider,
    config: FeedsConfig,
): StreamHttpConfig =
    StreamHttpConfig(
        httpBuilder = okHttpBuilder,
        automaticInterceptors = true,
        configuredInterceptors =
            setOf(createLoggingInterceptor(logProvider, config.loggingConfig.httpLoggingLevel)),
    )

internal fun createRetrofit(endpointConfig: EndpointConfig, okHttpClient: OkHttpClient): Retrofit =
    Retrofit.Builder()
        .baseUrl(endpointConfig.httpUrl)
        .client(okHttpClient)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create(Serializer.moshi))
        .build()
