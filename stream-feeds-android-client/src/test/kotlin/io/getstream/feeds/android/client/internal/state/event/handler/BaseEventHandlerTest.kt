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

package io.getstream.feeds.android.client.internal.state.event.handler

import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent
import io.getstream.feeds.android.client.internal.subscribe.StateUpdateEventListener
import io.mockk.MockKVerificationScope
import io.mockk.verify
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
internal abstract class BaseEventHandlerTest<State>(
    protected val testName: String,
    protected val event: StateUpdateEvent,
    protected val verifyBlock: MockKVerificationScope.(State) -> Unit,
) {
    protected abstract val state: State

    protected abstract val handler: StateUpdateEventListener

    @Test
    internal fun `handle event`() {
        handler.onEvent(event)
        verify { verifyBlock(state) }
    }

    companion object {
        fun <S> testParams(
            name: String,
            event: StateUpdateEvent,
            verifyBlock: MockKVerificationScope.(S) -> Unit,
        ): Array<Any> = arrayOf(name, event, verifyBlock)
    }
}
