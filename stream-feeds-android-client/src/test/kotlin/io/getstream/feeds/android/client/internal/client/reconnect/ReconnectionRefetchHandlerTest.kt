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

package io.getstream.feeds.android.client.internal.client.reconnect

import io.getstream.android.core.api.model.connection.StreamConnectionState
import io.getstream.feeds.android.client.internal.test.TestSubscriptionManager
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class ReconnectionRefetchHandlerTest {
    private val connectionState = MutableSharedFlow<StreamConnectionState>()
    private val scope = TestScope(UnconfinedTestDispatcher())

    @Test
    fun `on first connection, then do not refetch`() = runTest {
        val refetchable = CountingRefetchable()
        createHandler().register(refetchable)

        connectionState.emit(connected("connection-1"))

        assertEquals(0, refetchable.count)
    }

    @Test
    fun `on reconnection, then refetch every registered object`() = runTest {
        val first = CountingRefetchable()
        val second = CountingRefetchable()
        val handler = createHandler()
        handler.register(first)
        handler.register(second)

        connectionState.emit(connected("connection-1"))
        connectionState.emit(StreamConnectionState.Disconnected())
        connectionState.emit(connected("connection-2"))

        assertEquals(1, first.count)
        assertEquals(1, second.count)
    }

    @Test
    fun `on the same connection emitted again, then do not refetch`() = runTest {
        val refetchable = CountingRefetchable()
        createHandler().register(refetchable)

        connectionState.emit(connected("connection-1"))
        connectionState.emit(connected("connection-1"))

        assertEquals(0, refetchable.count)
    }

    @Test
    fun `on reconnection when one refetch fails, then still refetch the others`() = runTest {
        val refetchable = CountingRefetchable()
        val handler = createHandler()
        handler.register { error("failed") }
        handler.register(refetchable)

        connectionState.emit(connected("connection-1"))
        connectionState.emit(connected("connection-2"))
        connectionState.emit(connected("connection-3"))

        assertEquals(2, refetchable.count)
    }

    @Test
    fun `on reconnection while a refetch is running, then cancel it`() = runTest {
        val gate = CompletableDeferred<Unit>()
        var started = 0
        var finished = 0
        createHandler().register {
            started++
            gate.await()
            finished++
        }

        connectionState.emit(connected("connection-1"))
        connectionState.emit(connected("connection-2"))
        connectionState.emit(connected("connection-3"))
        gate.complete(Unit)

        assertEquals(2, started)
        assertEquals(1, finished)
    }

    @Test
    fun `on reconnection when disabled, then do not refetch`() = runTest {
        val refetchable = CountingRefetchable()
        createHandler(enabled = false).register(refetchable)

        connectionState.emit(connected("connection-1"))
        connectionState.emit(connected("connection-2"))

        assertEquals(0, refetchable.count)
    }

    private fun createHandler(enabled: Boolean = true) =
        ReconnectionRefetchHandler(
            connectionState = connectionState,
            refetchables = TestSubscriptionManager(),
            logger = mockk(relaxed = true),
            scope = scope,
            enabled = enabled,
        )

    private fun connected(connectionId: String) =
        StreamConnectionState.Connected(mockk(), connectionId)

    private class CountingRefetchable : Refetchable {
        var count = 0

        override suspend fun refetch() {
            count++
        }
    }
}
