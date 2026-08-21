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

import io.getstream.android.core.api.log.StreamLogger
import io.getstream.android.core.api.log.StreamLoggerProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

internal class CustomHeadersTest {

    @Test
    fun `on no headers, then no interceptor`() {
        assertNull(createCustomHeadersInterceptor(emptyMap(), RecordingLogger().asProvider()))
    }

    @Test
    fun `on only reserved headers, then no interceptor but a warning for each`() {
        val logger = RecordingLogger()

        val interceptor =
            createCustomHeadersInterceptor(
                // Reserved, spelled with a different case on purpose.
                mapOf("authorization" to "spoofed", "ACCEPT-ENCODING" to "identity"),
                logger.asProvider(),
            )

        assertNull(interceptor)
        assertEquals(2, logger.warnings.size)
    }

    @Test
    fun `on reserved names differing only in case, then drop them without complaining of a duplicate`() {
        // Both are dropped anyway, so failing as a duplicate would misdiagnose the cause.
        val logger = RecordingLogger()

        val interceptor =
            createCustomHeadersInterceptor(
                mapOf("Authorization" to "a", "authorization" to "b", "x-stream-ext" to "keep"),
                logger.asProvider(),
            )

        assertNotNull(interceptor)
        assertEquals(2, logger.warnings.size)
    }

    @Test
    fun `on valid headers, then an interceptor and no warnings`() {
        val logger = RecordingLogger()

        val interceptor =
            createCustomHeadersInterceptor(
                mapOf("x-stream-ext" to "version=1.2.3"),
                logger.asProvider(),
            )

        assertNotNull(interceptor)
        assertEquals(emptyList<String>(), logger.warnings)
    }

    @Test
    fun `on an invalid header name, then throw`() {
        // A space is not legal in a header name.
        val error =
            assertThrows(IllegalArgumentException::class.java) {
                createCustomHeadersInterceptor(mapOf("bad name" to "value"), noLogger())
            }

        assertTrue(error.message!!.contains("bad name"))
    }

    @Test
    fun `on an invalid header value, then throw without echoing the value`() {
        // A newline is not legal in a header value. The value may be a secret, so it must not
        // appear in the message.
        val error =
            assertThrows(IllegalArgumentException::class.java) {
                createCustomHeadersInterceptor(
                    mapOf("x-api-secret" to "s3cret\nsmuggled"),
                    noLogger(),
                )
            }

        assertTrue(error.message!!.contains("x-api-secret"))
        assertFalse(error.message!!.contains("s3cret"))
    }

    @Test
    fun `on names differing only in case, then throw rather than pick one`() {
        // Which one won would depend on the iteration order of a map the caller supplied.
        val error =
            assertThrows(IllegalArgumentException::class.java) {
                createCustomHeadersInterceptor(
                    mapOf("x-stream-ext" to "first", "X-Stream-Ext" to "second"),
                    noLogger(),
                )
            }

        assertTrue(error.message!!.contains("X-Stream-Ext"))
    }

    @Test
    fun `on intercept, then add the headers to the request`() {
        val request = intercept(mapOf("x-stream-ext" to "version=1.2.3"))

        assertEquals("version=1.2.3", request.header("x-stream-ext"))
    }

    @Test
    fun `on intercept, then leave the headers already on the request alone`() {
        val original = requestBuilder().addHeader("Authorization", "real-token").build()

        val request = intercept(mapOf("x-stream-ext" to "version=1.2.3"), original)

        assertEquals("real-token", request.header("Authorization"))
        assertEquals("version=1.2.3", request.header("x-stream-ext"))
    }

    @Test
    fun `on intercept with a header already on the request, then replace it instead of appending`() {
        val original = requestBuilder().addHeader("x-stream-ext", "old").build()

        val request = intercept(mapOf("x-stream-ext" to "new"), original)

        assertEquals(listOf("new"), request.headers("x-stream-ext"))
    }

    private fun requestBuilder() = Request.Builder().url("https://example.com/api/v2/feeds")

    private fun intercept(
        customHeaders: Map<String, String>,
        original: Request = requestBuilder().build(),
    ): Request {
        val chain = mockk<Interceptor.Chain>()
        val proceeded = slot<Request>()
        every { chain.request() } returns original
        every { chain.proceed(capture(proceeded)) } returns mockk<Response>(relaxed = true)

        createCustomHeadersInterceptor(customHeaders, noLogger())!!.intercept(chain)

        return proceeded.captured
    }

    private fun noLogger(): StreamLoggerProvider = mockk(relaxed = true)

    /** Captures warnings, which a relaxed mock would swallow. */
    private class RecordingLogger : StreamLogger {
        val warnings = mutableListOf<String>()

        override fun log(
            level: StreamLogger.LogLevel,
            throwable: Throwable?,
            message: () -> String,
        ) {
            if (level == StreamLogger.LogLevel.Warning) warnings += message()
        }

        fun asProvider() =
            object : StreamLoggerProvider {
                override fun taggedLogger(tag: String): StreamLogger = this@RecordingLogger
            }
    }
}
