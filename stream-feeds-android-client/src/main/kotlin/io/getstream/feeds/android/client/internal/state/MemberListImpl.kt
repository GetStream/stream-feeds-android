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
import io.getstream.feeds.android.client.api.model.FeedMemberData
import io.getstream.feeds.android.client.api.model.QueryConfiguration
import io.getstream.feeds.android.client.api.state.MemberList
import io.getstream.feeds.android.client.api.state.MemberListState
import io.getstream.feeds.android.client.api.state.query.MembersQuery
import io.getstream.feeds.android.client.api.state.query.toRequest
import io.getstream.feeds.android.client.internal.common.StreamSubscriptionManager
import io.getstream.feeds.android.client.internal.repository.FeedsRepository
import io.getstream.feeds.android.client.internal.socket.FeedsSocketListener
import io.getstream.feeds.android.client.internal.state.event.handler.MemberListEventHandler
import io.getstream.feeds.android.core.generated.models.WSEvent

/**
 * A class that manages a paginated list of feed members.
 *
 * [MemberList] provides functionality to query and paginate through members of a specific feed. It
 * maintains the current state of the member list and provides methods to load more members when
 * available.
 *
 * @property query The query configuration used to fetch members.
 * @property feedsRepository The repository used to perform network requests for members.
 * @property subscriptionManager The manager for WebSocket subscriptions to receive real-time.
 */
internal class MemberListImpl(
    override val query: MembersQuery,
    private val feedsRepository: FeedsRepository,
    private val subscriptionManager: StreamSubscriptionManager<FeedsSocketListener>,
) : MemberList {

    init {
        subscriptionManager.subscribe(
            object : FeedsSocketListener {
                override fun onState(state: WebSocketConnectionState) {
                    // Not relevant, rethink this
                }

                override fun onEvent(event: WSEvent) {
                    eventHandler.handleEvent(event)
                }
            }
        )
    }

    private val _state: MemberListStateImpl = MemberListStateImpl(query)

    private val eventHandler = MemberListEventHandler(_state)

    override val state: MemberListState
        get() = _state

    override suspend fun get(): Result<List<FeedMemberData>> {
        return queryMembers(query)
    }

    override suspend fun queryMoreMembers(limit: Int?): Result<List<FeedMemberData>> {
        val next = _state.pagination?.next
        if (next == null) {
            // If there is no next cursor, return an empty list.
            return Result.success(emptyList())
        }
        val nextQuery =
            MembersQuery(
                fid = query.fid,
                filter = _state.queryConfig?.filter,
                sort = _state.queryConfig?.sort,
                limit = limit ?: query.limit,
                next = next,
                previous = null,
            )
        return queryMembers(nextQuery)
    }

    /** Internal property to access the mutable state of the member list. */
    internal val mutableState: MemberListMutableState
        get() = _state

    private suspend fun queryMembers(query: MembersQuery): Result<List<FeedMemberData>> {
        return feedsRepository
            .queryFeedMembers(
                feedGroupId = query.fid.group,
                feedId = query.fid.id,
                request = query.toRequest(),
            )
            .onSuccess {
                _state.onQueryMoreMembers(it, QueryConfiguration(query.filter, query.sort))
            }
            .map { it.models }
    }
}
