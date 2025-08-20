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

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import org.junit.Test

internal class ConnectionIdInterceptorTest {
    private var connectionId = ""

    private val interceptor = ConnectionIdInterceptor(::connectionId)

    @Test
    fun `intercept when connectionId is empty, then proceed with the same request`() {
        connectionId = ""
        val originalRequest = Request.Builder().url("http://getstream.io").build()
        val chain = mockChain(originalRequest)

        interceptor.intercept(chain)

        verify { chain.proceed(originalRequest) }
    }

    @Test
    fun `intercept when connectionId is not empty, then proceed with request with connectionId`() {
        connectionId = "12345"
        val originalRequest = Request.Builder().url("http://getstream.io").build()
        val expectedUrl = "http://getstream.io?connection_id=12345".toHttpUrl()
        val chain = mockChain(originalRequest)

        interceptor.intercept(chain)

        verify { chain.proceed(match { it.url == expectedUrl }) }
    }

    private fun mockChain(request: Request): Interceptor.Chain = mockk {
        every { request() } returns request
        every { proceed(any()) } returns mockk()
    }
}
