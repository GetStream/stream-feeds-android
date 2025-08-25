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

import kotlinx.coroutines.CancellationException

/**
 * Runs a block of code and returns a [Result] containing the outcome.
 *
 * If the block completes successfully, the result is a success with the value returned by the
 * block. If an exception is thrown, it is caught and wrapped in a failure result. Cancellation
 * exceptions are rethrown to allow coroutine cancellation to propagate.
 *
 * @param block The block of code to execute.
 * @return A [Result] containing either the successful value or the exception.
 */
public inline fun <T> runSafely(block: () -> T): Result<T> {
    return try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }
}
