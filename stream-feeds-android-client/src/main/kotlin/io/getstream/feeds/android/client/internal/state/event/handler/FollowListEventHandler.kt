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

import io.getstream.feeds.android.client.api.model.FollowData
import io.getstream.feeds.android.client.api.model.ModelUpdates
import io.getstream.feeds.android.client.api.state.query.FollowsFilter
import io.getstream.feeds.android.client.internal.state.FollowListStateUpdates
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent
import io.getstream.feeds.android.client.internal.state.query.matches
import io.getstream.feeds.android.client.internal.subscribe.StateUpdateEventListener

internal class FollowListEventHandler(
    private val filter: FollowsFilter?,
    private val state: FollowListStateUpdates,
) : StateUpdateEventListener {

    override fun onEvent(event: StateUpdateEvent) {
        when (event) {
            is StateUpdateEvent.FollowAdded -> {
                if (event.follow matches filter) {
                    state.onFollowUpserted(event.follow)
                }
            }

            is StateUpdateEvent.FollowDeleted -> {
                state.onFollowRemoved(event.follow)
            }

            is StateUpdateEvent.FollowUpdated -> {
                if (event.follow matches filter) {
                    state.onFollowUpserted(event.follow)
                } else {
                    // We remove elements that used to match the filter but no longer do
                    state.onFollowRemoved(event.follow)
                }
            }

            is StateUpdateEvent.FollowBatchUpdate -> {
                val added = event.updates.added.filter { it matches filter }
                // We remove elements that used to match the filter but no longer do
                val (updated, removed) = event.updates.updated.partition { it matches filter }
                val removedIds = event.updates.removedIds.toMutableSet()
                removed.mapTo(removedIds, FollowData::id)

                state.onFollowsUpdated(
                    ModelUpdates(added = added, updated = updated, removedIds = removedIds)
                )
            }

            else -> {}
        }
    }
}
