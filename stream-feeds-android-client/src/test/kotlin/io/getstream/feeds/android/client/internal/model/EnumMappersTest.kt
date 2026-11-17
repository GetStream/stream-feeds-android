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

package io.getstream.feeds.android.client.internal.model

import io.getstream.feeds.android.client.api.model.ActivityDataVisibility
import io.getstream.feeds.android.client.api.model.CollectionStatus
import io.getstream.feeds.android.client.api.model.CommentStatus
import io.getstream.feeds.android.client.api.model.FeedMemberStatus
import io.getstream.feeds.android.client.api.model.FeedVisibility
import io.getstream.feeds.android.client.api.model.FollowStatus
import io.getstream.feeds.android.client.api.model.RestrictReplies
import io.getstream.feeds.android.network.models.ActivityVisibility
import io.getstream.feeds.android.network.models.CollectionStatus as NetworkCollectionStatus
import io.getstream.feeds.android.network.models.CommentStatus as NetworkCommentStatus
import io.getstream.feeds.android.network.models.EnrichedCollectionResponse
import io.getstream.feeds.android.network.models.FeedMemberStatus as NetworkFeedMemberStatus
import io.getstream.feeds.android.network.models.FeedVisibility as NetworkFeedVisibility
import io.getstream.feeds.android.network.models.FollowStatus as NetworkFollowStatus
import io.getstream.feeds.android.network.models.RestrictReplies as NetworkRestrictReplies
import java.util.Date
import org.junit.Assert.assertEquals
import org.junit.Test

internal class EnumMappersTest {

    @Test
    fun `on network FeedVisibility toModel, map every value`() {
        val expected =
            mapOf(
                NetworkFeedVisibility.Followers to FeedVisibility.Followers,
                NetworkFeedVisibility.Members to FeedVisibility.Members,
                NetworkFeedVisibility.Private to FeedVisibility.Private,
                NetworkFeedVisibility.Public to FeedVisibility.Public,
                NetworkFeedVisibility.Visible to FeedVisibility.Visible,
                NetworkFeedVisibility.Unknown("new") to FeedVisibility.Unknown("new"),
            )

        expected.forEach { (network, model) -> assertEquals(model, network.toModel()) }
    }

    @Test
    fun `on FeedVisibility toRequest, map every value`() {
        val expected =
            mapOf(
                FeedVisibility.Followers to NetworkFeedVisibility.Followers,
                FeedVisibility.Members to NetworkFeedVisibility.Members,
                FeedVisibility.Private to NetworkFeedVisibility.Private,
                FeedVisibility.Public to NetworkFeedVisibility.Public,
                FeedVisibility.Visible to NetworkFeedVisibility.Visible,
                FeedVisibility.Unknown("new") to NetworkFeedVisibility.Unknown("new"),
            )

        expected.forEach { (model, network) -> assertEquals(network, model.toRequest()) }
    }

    @Test
    fun `on network RestrictReplies toModel, map every value`() {
        val expected =
            mapOf(
                NetworkRestrictReplies.Everyone to RestrictReplies.Everyone,
                NetworkRestrictReplies.Nobody to RestrictReplies.Nobody,
                NetworkRestrictReplies.PeopleIFollow to RestrictReplies.PeopleIFollow,
                NetworkRestrictReplies.Unknown("new") to RestrictReplies.Unknown("new"),
            )

        expected.forEach { (network, model) -> assertEquals(model, network.toModel()) }
    }

    @Test
    fun `on ActivityVisibility toModel, map every value`() {
        val expected =
            mapOf(
                ActivityVisibility.Private to ActivityDataVisibility.Private,
                ActivityVisibility.Public to ActivityDataVisibility.Public,
                ActivityVisibility.Tag to ActivityDataVisibility.Tag,
                ActivityVisibility.Unknown("new") to ActivityDataVisibility.Unknown("new"),
            )

        expected.forEach { (network, model) -> assertEquals(model, network.toModel()) }
    }

    @Test
    fun `on network CommentStatus toModel, map every value`() {
        val expected =
            mapOf(
                NetworkCommentStatus.Active to CommentStatus.Active,
                NetworkCommentStatus.Deleted to CommentStatus.Deleted,
                NetworkCommentStatus.Hidden to CommentStatus.Hidden,
                NetworkCommentStatus.Removed to CommentStatus.Removed,
                NetworkCommentStatus.ShadowBlocked to CommentStatus.ShadowBlocked,
                NetworkCommentStatus.Unknown("new") to CommentStatus.Unknown("new"),
            )

        expected.forEach { (network, model) -> assertEquals(model, network.toModel()) }
    }

    @Test
    fun `on network FeedMemberStatus toModel, map every value`() {
        val expected =
            mapOf(
                NetworkFeedMemberStatus.Member to FeedMemberStatus.Member,
                NetworkFeedMemberStatus.Pending to FeedMemberStatus.Pending,
                NetworkFeedMemberStatus.Rejected to FeedMemberStatus.Rejected,
                NetworkFeedMemberStatus.Unknown("new") to FeedMemberStatus.Unknown("new"),
            )

        expected.forEach { (network, model) -> assertEquals(model, network.toModel()) }
    }

    @Test
    fun `on network FollowStatus toModel, map every value`() {
        val expected =
            mapOf(
                NetworkFollowStatus.Accepted to FollowStatus.Accepted,
                NetworkFollowStatus.Pending to FollowStatus.Pending,
                NetworkFollowStatus.Rejected to FollowStatus.Rejected,
                NetworkFollowStatus.Unknown("new") to FollowStatus.Unknown("new"),
            )

        expected.forEach { (network, model) -> assertEquals(model, network.toModel()) }
    }

    @Test
    fun `on EnrichedCollectionResponse toModel, map every status`() {
        val expected =
            mapOf(
                NetworkCollectionStatus.Notfound to CollectionStatus.NotFound,
                NetworkCollectionStatus.Ok to CollectionStatus.Ok,
                NetworkCollectionStatus.Unknown("new") to CollectionStatus.Unknown("new"),
            )

        expected.forEach { (network, model) ->
            val response =
                EnrichedCollectionResponse(
                    id = "collection-1",
                    name = "movies",
                    status = network,
                    createdAt = Date(1000),
                    updatedAt = Date(1000),
                    userId = "user-1",
                    custom = emptyMap(),
                )

            assertEquals(model, response.toModel().status)
        }
    }
}
