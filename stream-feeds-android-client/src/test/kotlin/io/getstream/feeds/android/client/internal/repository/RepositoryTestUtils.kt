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
package io.getstream.feeds.android.client.internal.repository

import io.mockk.MockKMatcherScope
import io.mockk.coEvery
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals

internal object RepositoryTestUtils {
    inline fun <T> testDelegation(
        crossinline apiFunction: suspend MockKMatcherScope.() -> T,
        crossinline repositoryCall: suspend () -> Result<T>,
        apiResult: T,
    ) = testDelegation(apiFunction, repositoryCall, apiResult, apiResult)

    inline fun <T, R> testDelegation(
        crossinline apiFunction: suspend MockKMatcherScope.() -> T,
        crossinline repositoryCall: suspend () -> Result<T>,
        apiResult: T,
        repositoryResult: R,
    ) = runTest {
        coEvery { apiFunction() } returns apiResult
        val result = repositoryCall()
        assertEquals(repositoryResult, result.getOrThrow())
    }
}
