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

package io.getstream.feeds.android.client.internal.subscribe

import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent

/** Listener interface for state update events. */
internal interface StateUpdateEventListener {

    /**
     * Called when a new state update event is received.
     *
     * @param event The event.
     * @see [StateUpdateEvent]
     */
    fun onEvent(event: StateUpdateEvent)
}
