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

import io.getstream.android.core.api.filter.equal
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.model.ModelUpdates
import io.getstream.feeds.android.client.api.state.query.MembersFilterField
import io.getstream.feeds.android.client.internal.state.MemberListStateUpdates
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.FeedMemberAdded
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.FeedMemberBatchUpdate
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.FeedMemberRemoved
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent.FeedMemberUpdated
import io.getstream.feeds.android.client.internal.test.TestData.feedMemberData
import io.mockk.MockKVerificationScope
import io.mockk.called
import io.mockk.mockk
import org.junit.runners.Parameterized

internal class MemberListEventHandlerTest(
    testName: String,
    event: StateUpdateEvent,
    verifyBlock: MockKVerificationScope.(MemberListStateUpdates) -> Unit,
) : BaseEventHandlerTest<MemberListStateUpdates>(testName, event, verifyBlock) {

    override val state: MemberListStateUpdates = mockk(relaxed = true)
    override val handler = MemberListEventHandler(FeedId(fid), testFilter, state)

    companion object {
        private const val fid = "user:feed-1"
        private const val differentFeedId = "user:different-feed"
        private val testFilter = MembersFilterField.role.equal("admin")
        private val matchingMember = feedMemberData(role = "admin")
        private val nonMatchingMember = feedMemberData(role = "member")

        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): List<Array<Any>> =
            listOf(
                testParams<MemberListStateUpdates>(
                    name = "FeedMemberAdded matching feed and filter",
                    event = FeedMemberAdded(fid, matchingMember),
                    verifyBlock = { state -> state.onMemberUpserted(matchingMember) },
                ),
                testParams<MemberListStateUpdates>(
                    name = "FeedMemberAdded non-matching feed",
                    event = FeedMemberAdded(differentFeedId, matchingMember),
                    verifyBlock = { state -> state wasNot called },
                ),
                testParams<MemberListStateUpdates>(
                    name = "FeedMemberAdded non-matching filter",
                    event = FeedMemberAdded(fid, nonMatchingMember),
                    verifyBlock = { state -> state wasNot called },
                ),
                testParams<MemberListStateUpdates>(
                    name = "FeedMemberRemoved matching feed",
                    event = FeedMemberRemoved(fid, "member-1"),
                    verifyBlock = { state -> state.onMemberRemoved("member-1") },
                ),
                testParams<MemberListStateUpdates>(
                    name = "FeedMemberRemoved non-matching feed",
                    event = FeedMemberRemoved(differentFeedId, "member-1"),
                    verifyBlock = { state -> state wasNot called },
                ),
                testParams<MemberListStateUpdates>(
                    name = "FeedMemberUpdated matching feed and filter",
                    event = FeedMemberUpdated(fid, matchingMember),
                    verifyBlock = { state -> state.onMemberUpserted(matchingMember) },
                ),
                testParams<MemberListStateUpdates>(
                    name = "FeedMemberUpdated non-matching feed",
                    event = FeedMemberUpdated(differentFeedId, matchingMember),
                    verifyBlock = { state -> state wasNot called },
                ),
                testParams<MemberListStateUpdates>(
                    name = "FeedMemberUpdated non-matching filter",
                    event = FeedMemberUpdated(fid, nonMatchingMember),
                    verifyBlock = { state -> state.onMemberRemoved(nonMatchingMember.id) },
                ),
                run {
                    val anotherMatching = feedMemberData(userId = "user-2", role = "admin")
                    val anotherNonMatching = feedMemberData(userId = "user-3", role = "member")

                    testParams<MemberListStateUpdates>(
                        name = "FeedMemberBatchUpdate matching feed with mixed updates",
                        event =
                            FeedMemberBatchUpdate(
                                fid = fid,
                                updates =
                                    ModelUpdates(
                                        added = listOf(matchingMember, nonMatchingMember),
                                        updated = listOf(anotherMatching, anotherNonMatching),
                                        removedIds = setOf("removed-1", "removed-2"),
                                    ),
                            ),
                        verifyBlock = { state ->
                            state.onMembersUpdated(
                                ModelUpdates(
                                    added = listOf(matchingMember),
                                    updated = listOf(anotherMatching),
                                    removedIds =
                                        setOf("removed-1", "removed-2", anotherNonMatching.id),
                                )
                            )
                        },
                    )
                },
                testParams<MemberListStateUpdates>(
                    name = "FeedMemberBatchUpdate non-matching feed",
                    event =
                        FeedMemberBatchUpdate(
                            fid = differentFeedId,
                            updates =
                                ModelUpdates(
                                    added = listOf(matchingMember),
                                    updated = emptyList(),
                                    removedIds = setOf("removed-1"),
                                ),
                        ),
                    verifyBlock = { state -> state wasNot called },
                ),
            )
    }
}
