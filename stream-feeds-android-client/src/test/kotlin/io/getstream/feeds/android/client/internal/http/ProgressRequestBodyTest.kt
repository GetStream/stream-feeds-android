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

import io.getstream.feeds.android.client.internal.file.ProgressCallback
import io.mockk.mockk
import io.mockk.verify
import okhttp3.RequestBody
import okio.BufferedSink
import org.junit.Test

internal class ProgressRequestBodyTest {
    private val delegate: RequestBody = TestRequestBody(1000)
    private val callback: ProgressCallback = mockk(relaxed = true)

    private val progressRequestBody = ProgressRequestBody(delegate = delegate, callback = callback)

    @Test
    fun `on writeTo, write to delegate and notify progress`() {
        val sink: BufferedSink = mockk(relaxed = true)

        progressRequestBody.writeTo(sink)

        verify {
            sink.write(any(), any())
            callback.onProgress(uploaded = 1000, total = 1000)
        }
    }

    private class TestRequestBody(private val length: Int) : RequestBody() {
        override fun contentType() = null

        override fun contentLength() = length.toLong()

        override fun writeTo(sink: BufferedSink) {
            sink.write(ByteArray(length), 0, length)
        }
    }
}
