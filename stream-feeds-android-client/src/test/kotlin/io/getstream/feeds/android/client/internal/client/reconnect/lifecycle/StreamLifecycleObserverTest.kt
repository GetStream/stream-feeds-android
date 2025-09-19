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
package io.getstream.feeds.android.client.internal.client.reconnect.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class StreamLifecycleObserverTest {
    private val lifecycle: Lifecycle = mockk(relaxed = true)
    private val scope: CoroutineScope = TestScope(UnconfinedTestDispatcher())

    private val observer = StreamLifecycleObserver(scope, lifecycle)

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `on isResumed with resumed lifecycle, then return true`() {
        every { lifecycle.currentState } returns Lifecycle.State.RESUMED

        assertTrue(observer.isResumed())
    }

    @Test
    fun `on isResumed with started lifecycle, then return false`() {
        every { lifecycle.currentState } returns Lifecycle.State.STARTED

        assertFalse(observer.isResumed())
    }

    @Test
    fun `on isResumed with created lifecycle, then return false`() {
        every { lifecycle.currentState } returns Lifecycle.State.CREATED

        assertFalse(observer.isResumed())
    }

    @Test
    fun `on subscribe with first listener, then add observer to lifecycle`() = runTest {
        val listener: StreamLifecycleObserver.LifecycleListener = mockk(relaxed = true)

        observer.subscribe(listener)

        verify { lifecycle.addObserver(observer) }
    }

    @Test
    fun `on subscribe with multiple listeners, then add observer only once`() = runTest {
        val listener1: StreamLifecycleObserver.LifecycleListener = mockk(relaxed = true)
        val listener2: StreamLifecycleObserver.LifecycleListener = mockk(relaxed = true)

        observer.subscribe(listener1)
        observer.subscribe(listener2)

        verify(exactly = 1) { lifecycle.addObserver(observer) }
    }

    @Test
    fun `on unsubscribe with last listener, then remove observer from lifecycle`() = runTest {
        val listener: StreamLifecycleObserver.LifecycleListener = mockk(relaxed = true)

        observer.subscribe(listener)
        observer.unsubscribe(listener)

        verify { lifecycle.removeObserver(observer) }
    }

    @Test
    fun `on unsubscribe with remaining listeners, then don't remove observer`() = runTest {
        val listener1: StreamLifecycleObserver.LifecycleListener = mockk(relaxed = true)
        val listener2: StreamLifecycleObserver.LifecycleListener = mockk(relaxed = true)

        observer.subscribe(listener1)
        observer.subscribe(listener2)
        observer.unsubscribe(listener1)

        verify(exactly = 0) { lifecycle.removeObserver(observer) }
    }

    @Test
    fun `on onResume with first call, then don't notify listeners`() = runTest {
        val listener: StreamLifecycleObserver.LifecycleListener = mockk(relaxed = true)
        val owner: LifecycleOwner = mockk()

        observer.subscribe(listener)

        observer.onResume(owner)

        coVerify(exactly = 0) { listener.onResume() }
    }

    @Test
    fun `on onResume with second call, then notify listeners`() = runTest {
        val listener: StreamLifecycleObserver.LifecycleListener = mockk(relaxed = true)
        val owner: LifecycleOwner = mockk()

        observer.subscribe(listener)

        observer.onResume(owner)
        observer.onResume(owner)

        coVerify(exactly = 1) { listener.onResume() }
    }

    @Test
    fun `on onResume with multiple listeners, then notify all listeners`() = runTest {
        val listener1: StreamLifecycleObserver.LifecycleListener = mockk(relaxed = true)
        val listener2: StreamLifecycleObserver.LifecycleListener = mockk(relaxed = true)
        val owner: LifecycleOwner = mockk()

        observer.subscribe(listener1)
        observer.subscribe(listener2)

        observer.onResume(owner)
        observer.onResume(owner)

        coVerify(exactly = 1) { listener1.onResume() }
        coVerify(exactly = 1) { listener2.onResume() }
    }

    @Test
    fun `on onStop, then notify all listeners`() = runTest {
        val listener1: StreamLifecycleObserver.LifecycleListener = mockk(relaxed = true)
        val listener2: StreamLifecycleObserver.LifecycleListener = mockk(relaxed = true)
        val owner: LifecycleOwner = mockk()

        observer.subscribe(listener1)
        observer.subscribe(listener2)

        observer.onStop(owner)

        coVerify(exactly = 1) { listener1.onStop() }
        coVerify(exactly = 1) { listener2.onStop() }
    }

    @Test
    fun `on onStop with no listeners, then don't throw exception`() = runTest {
        val owner: LifecycleOwner = mockk()

        observer.onStop(owner)
    }

    @Test
    fun `on unsubscribe with non-existent listener, then have no effect`() = runTest {
        val listener1: StreamLifecycleObserver.LifecycleListener = mockk(relaxed = true)
        val listener2: StreamLifecycleObserver.LifecycleListener = mockk(relaxed = true)

        observer.subscribe(listener1)
        observer.unsubscribe(listener2)

        verify(exactly = 0) { lifecycle.removeObserver(observer) }
    }

    @Test
    fun `on unsubscribe and resubscribe, then reset resume event filtering`() = runTest {
        val listener: StreamLifecycleObserver.LifecycleListener = mockk(relaxed = true)
        val owner: LifecycleOwner = mockk()

        observer.subscribe(listener)
        observer.onResume(owner)
        observer.unsubscribe(listener)
        observer.subscribe(listener)
        observer.onResume(owner)

        coVerify(exactly = 0) { listener.onResume() }
    }
}
