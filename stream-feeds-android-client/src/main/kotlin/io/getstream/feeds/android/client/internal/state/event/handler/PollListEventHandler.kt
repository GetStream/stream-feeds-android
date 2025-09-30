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
package io.getstream.feeds.android.client.internal.state.event.handler

import io.getstream.feeds.android.client.api.state.query.PollsFilter
import io.getstream.feeds.android.client.internal.state.PollListStateUpdates
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent
import io.getstream.feeds.android.client.internal.state.query.matches
import io.getstream.feeds.android.client.internal.subscribe.StateUpdateEventListener

/**
 * Handles events related to poll updates in the poll list state.
 *
 * @property state The instance that manages updates to the poll list state.
 */
internal class PollListEventHandler(
    private val filter: PollsFilter?,
    private val state: PollListStateUpdates,
) : StateUpdateEventListener {

    override fun onEvent(event: StateUpdateEvent) {
        when (event) {
            is StateUpdateEvent.PollDeleted -> {
                state.onPollDeleted(event.pollId)
            }

            is StateUpdateEvent.PollUpdated -> {
                if (event.poll matches filter) {
                    state.onPollUpdated(event.poll)
                }
            }

            is StateUpdateEvent.PollVoteCasted -> {
                state.onPollVoteUpserted(event.pollId, event.vote)
            }

            is StateUpdateEvent.PollVoteChanged -> {
                state.onPollVoteUpserted(event.pollId, event.vote)
            }

            is StateUpdateEvent.PollVoteRemoved -> {
                state.onPollVoteRemoved(event.pollId, event.vote)
            }

            else -> {}
        }
    }
}
