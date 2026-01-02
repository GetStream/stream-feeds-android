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

package io.getstream.feeds.android.client.internal.subscribe

import io.getstream.feeds.android.network.models.WSEvent

/** Listener interface for Feeds socket events. */
internal interface FeedsEventListener {

    /**
     * Called when a new event is received from the socket.
     *
     * @param event The event received from the WebSocket.
     */
    fun onEvent(event: WSEvent)
}
