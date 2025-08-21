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
package io.getstream.feeds.android.client.internal.client

import app.cash.turbine.test
import io.getstream.android.core.user.ApiKey
import io.getstream.android.core.user.User
import io.getstream.feeds.android.client.api.subscribe.StreamSubscription
import io.getstream.feeds.android.client.internal.socket.FeedsSocket
import io.getstream.feeds.android.client.internal.socket.FeedsSocketListener
import io.getstream.feeds.android.network.models.WSEvent
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

internal class FeedsClientImplTest {
    private val socketListeners = mutableListOf<FeedsSocketListener>()
    private val socket: FeedsSocket = mockSocket()

    private val client = FeedsClientImpl(
        apiKey = ApiKey("test-api-key"),
        user = User(id = "user-id"),
        tokenManager = mockk(),
        socket = socket,
        connectionRecoveryHandler = mockk(),
        activitiesRepository = mockk(),
        appRepository = mockk(),
        bookmarksRepository = mockk(),
        commentsRepository = mockk(),
        devicesRepository = mockk(),
        feedsRepository = mockk(),
        filesRepository = mockk(),
        moderationRepository = mockk(),
        pollsRepository = mockk(),
        uploader = mockk(),
        moderation = mockk(),
        clientState = mockk(),
        logger = mockk(relaxed = true),
    )

    @Test
    fun `the events flow should emit socket events on listener invocations`() = runTest {
        client.events().test {
            repeat(5) { n ->
                socketListeners.forEach { it.onEvent(TestWSEvent(n)) }
            }

            repeat(5) { n ->
                assertEquals(TestWSEvent(n), awaitItem())
            }
        }
    }

    private fun mockSocket(): FeedsSocket = mockk<FeedsSocket> {
        every { subscribe(capture(socketListeners)) } answers {
            val listener = firstArg<FeedsSocketListener>()
            val subscription = object : StreamSubscription {
                override fun cancel() {
                    socketListeners.remove(listener)
                }
            }
            Result.success(subscription)
        }
    }

    private data class TestWSEvent(val id: Int) : WSEvent {
        override fun getWSEventType() = "feed.test"
    }
}
