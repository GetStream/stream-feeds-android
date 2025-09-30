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

import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.state.query.MembersFilter
import io.getstream.feeds.android.client.internal.state.MemberListStateUpdates
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent
import io.getstream.feeds.android.client.internal.state.query.matches
import io.getstream.feeds.android.client.internal.subscribe.StateUpdateEventListener

internal class MemberListEventHandler(
    private val fid: FeedId,
    private val filter: MembersFilter?,
    private val state: MemberListStateUpdates,
) : StateUpdateEventListener {
    override fun onEvent(event: StateUpdateEvent) {
        when (event) {
            is StateUpdateEvent.FeedMemberAdded -> {
                if (event.fid == fid.rawValue && event.member matches filter) {
                    state.onMemberUpserted(event.member)
                }
            }

            is StateUpdateEvent.FeedMemberRemoved -> {
                if (event.fid == fid.rawValue) {
                    state.onMemberRemoved(event.memberId)
                }
            }

            is StateUpdateEvent.FeedMemberUpdated -> {
                if (event.fid == fid.rawValue && event.member matches filter) {
                    state.onMemberUpserted(event.member)
                }
            }

            else -> {}
        }
    }
}
