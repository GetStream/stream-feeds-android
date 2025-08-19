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
package io.getstream.android.core.result

import kotlin.coroutines.cancellation.CancellationException
import org.junit.Assert.assertEquals
import org.junit.Test

internal class ResultUtilsTest {
    @Test
    fun `runSafely, when no error occurs, then return success`() {
        val result = runSafely { "Success" }

        assertEquals(Result.success("Success"), result)
    }

    @Test
    fun `runSafely, when an exception occurs, then return failure`() {
        val exception = RuntimeException("Test Exception")
        val result = runSafely { throw exception }

        assertEquals(Result.failure<String>(exception), result)
    }

    @Test(expected = CancellationException::class)
    fun `runSafely, when a CancellationException occurs, then rethrow it`() {
        runSafely { throw CancellationException("Cancellation") }
    }
}
