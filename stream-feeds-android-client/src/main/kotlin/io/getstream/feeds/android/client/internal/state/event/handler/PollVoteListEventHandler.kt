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

import io.getstream.feeds.android.client.internal.state.PollVoteListStateUpdates
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent
import io.getstream.feeds.android.client.internal.subscribe.StateUpdateEventListener

/**
 * Handles events related to poll vote lists in the feed state.
 *
 * @property pollId The unique identifier for the poll this handler is associated with.
 * @property state The instance that manages updates to the poll vote list state.
 */
internal class PollVoteListEventHandler(
    private val pollId: String,
    private val state: PollVoteListStateUpdates,
) : StateUpdateEventListener {

    override fun onEvent(event: StateUpdateEvent) {
        when (event) {
            is StateUpdateEvent.PollDeleted -> {
                if (event.pollId != pollId) return
                state.onPollDeleted()
            }

            is StateUpdateEvent.PollVoteCasted -> {
                if (event.pollId != pollId) return
                state.pollVoteUpserted(event.vote)
            }

            is StateUpdateEvent.PollVoteChanged -> {
                if (event.pollId != pollId) return
                state.pollVoteUpserted(event.vote)
            }

            is StateUpdateEvent.PollVoteRemoved -> {
                if (event.pollId != pollId) return
                state.pollVoteRemoved(event.vote.id)
            }
            else -> {}
        }
    }
}
