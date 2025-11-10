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

package io.getstream.feeds.android.client.internal.http

import io.getstream.feeds.android.client.api.logging.HttpLoggingLevel
import io.getstream.feeds.android.client.api.logging.LoggingConfig
import io.getstream.feeds.android.client.api.model.FeedsConfig
import io.getstream.feeds.android.client.internal.client.EndpointConfig
import io.mockk.mockk
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

internal class HttpClientFactoriesTest {

    @Test
    fun `on createHttpConfig, then return StreamHttpConfig with correct configuration`() {
        val okHttpBuilder = OkHttpClient.Builder()
        val config =
            FeedsConfig(loggingConfig = LoggingConfig(httpLoggingLevel = HttpLoggingLevel.Basic))

        val result =
            createHttpConfig(
                okHttpBuilder = okHttpBuilder,
                logProvider = mockk(relaxed = true),
                config = config,
            )

        assertEquals(okHttpBuilder, result.httpBuilder)
        assertTrue(result.automaticInterceptors)
        assertEquals(1, result.configuredInterceptors.size)
        assertTrue(result.configuredInterceptors.first() is HttpLoggingInterceptor)
    }

    @Test
    fun `on createRetrofit, then return Retrofit with correct configuration`() {
        val testEndpointConfig =
            EndpointConfig(
                httpUrl = "https://test.example.com",
                wsUrl = "wss://test.example.com/ws",
            )
        val okHttpClient = mockk<OkHttpClient>(relaxed = true)

        val result = createRetrofit(testEndpointConfig, okHttpClient)

        assertEquals("https://test.example.com/", result.baseUrl().toString())
        assertEquals(okHttpClient, result.callFactory())
        assertTrue(
            "Retrofit should have the scalar converter factory",
            result.converterFactories().any { it is ScalarsConverterFactory },
        )
        assertTrue(
            "Retrofit should have the Moshi converter factory",
            result.converterFactories().any { it is MoshiConverterFactory },
        )
    }
}
