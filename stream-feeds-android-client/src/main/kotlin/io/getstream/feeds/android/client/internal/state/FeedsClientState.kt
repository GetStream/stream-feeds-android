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
package io.getstream.feeds.android.client.internal.state

import io.getstream.android.core.websocket.WebSocketConnectionState

internal class FeedsClientStateImpl : FeedsClientMutableState {

    private var _connectionState: WebSocketConnectionState = WebSocketConnectionState.Initialized

    override val connectionState: WebSocketConnectionState
        get() = _connectionState

    override fun setConnectionState(state: WebSocketConnectionState) {
        _connectionState = state
    }
}

internal interface FeedsClientMutableState : FeedsClientState, FeedsClientStateUpdates

internal interface FeedsClientState {
    val connectionState: WebSocketConnectionState
}

internal interface FeedsClientStateUpdates {

    fun setConnectionState(state: WebSocketConnectionState)
}
