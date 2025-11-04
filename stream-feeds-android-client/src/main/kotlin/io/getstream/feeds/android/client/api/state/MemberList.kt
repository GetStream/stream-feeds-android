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

package io.getstream.feeds.android.client.api.state

import io.getstream.feeds.android.client.api.model.FeedMemberData
import io.getstream.feeds.android.client.api.state.query.MembersQuery

/**
 * A class that manages a paginated list of feed members.
 *
 * [MemberList] provides functionality to query and paginate through members of a specific feed. It
 * maintains the current state of the member list and provides methods to load more members when
 * available.
 *
 * ## Example:
 * ```kotlin
 * // Create a member list with a specific query
 * val query = MembersQuery(fid = FeedId("feed:123"))
 * val memberList = feedsClient.memberList(query)
 *
 * // Fetch initial members matching the query
 * val members = memberList.get()
 *
 * // Load more members if available
 * if (memberList.state.canLoadMore) {
 *    val moreMembers = memberList.queryMoreMembers()
 * }
 *
 * // Observe state changes
 * memberList.state.members.collect { members ->
 *    println("Updated members: ${members.size}")
 * }
 * ```
 */
public interface MemberList {

    /**
     * The query configuration used to fetch members.
     *
     * This contains the feed ID, filters, sorting options, and pagination parameters that define
     * which members are retrieved and how they are ordered.
     */
    public val query: MembersQuery

    /**
     * An observable object representing the current state of the member list.
     *
     * This property provides access to the current members, pagination state, and other state
     * information. The state is automatically updated when new members are loaded or when real-time
     * updates are received.
     */
    public val state: MemberListState

    /**
     * Fetches the initial list of members based on the current query configuration.
     *
     * This method loads the first page of members according to the query's filters, sorting, and
     * limit parameters. The results are stored in the state and can be accessed through the
     * [state.members] property.
     *
     * @return A [Result] containing a list of [FeedMemberData] if successful, or an error if the
     *   request fails.
     */
    public suspend fun get(): Result<List<FeedMemberData>>

    /**
     * Loads the next page of members if more are available.
     *
     * This method fetches additional members using the pagination information from the previous
     * request. If no more members are available, an empty array is returned.
     *
     * @param limit Optional limit for the number of members to return. If not specified, the API
     *   will use its default limit.
     * @return A [Result] containing a list of [FeedMemberData] if successful, or an error if the
     *   request fails or there are no more members to load.
     */
    public suspend fun queryMoreMembers(limit: Int? = null): Result<List<FeedMemberData>>
}
