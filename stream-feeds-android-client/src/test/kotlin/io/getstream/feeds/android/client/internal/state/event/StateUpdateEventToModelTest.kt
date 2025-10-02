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
package io.getstream.feeds.android.client.internal.state.event

import io.getstream.feeds.android.client.internal.test.TestData
import io.getstream.feeds.android.network.models.ActivityAddedEvent
import io.getstream.feeds.android.network.models.ActivityDeletedEvent
import io.getstream.feeds.android.network.models.ActivityPinnedEvent
import io.getstream.feeds.android.network.models.ActivityReactionAddedEvent
import io.getstream.feeds.android.network.models.ActivityReactionDeletedEvent
import io.getstream.feeds.android.network.models.ActivityRemovedFromFeedEvent
import io.getstream.feeds.android.network.models.ActivityUnpinnedEvent
import io.getstream.feeds.android.network.models.ActivityUpdatedEvent
import io.getstream.feeds.android.network.models.BookmarkAddedEvent
import io.getstream.feeds.android.network.models.BookmarkDeletedEvent
import io.getstream.feeds.android.network.models.BookmarkFolderDeletedEvent
import io.getstream.feeds.android.network.models.BookmarkFolderUpdatedEvent
import io.getstream.feeds.android.network.models.BookmarkUpdatedEvent
import io.getstream.feeds.android.network.models.CommentAddedEvent
import io.getstream.feeds.android.network.models.CommentDeletedEvent
import io.getstream.feeds.android.network.models.CommentReactionAddedEvent
import io.getstream.feeds.android.network.models.CommentReactionDeletedEvent
import io.getstream.feeds.android.network.models.CommentUpdatedEvent
import io.getstream.feeds.android.network.models.FeedDeletedEvent
import io.getstream.feeds.android.network.models.FeedMemberAddedEvent
import io.getstream.feeds.android.network.models.FeedMemberRemovedEvent
import io.getstream.feeds.android.network.models.FeedMemberUpdatedEvent
import io.getstream.feeds.android.network.models.FeedUpdatedEvent
import io.getstream.feeds.android.network.models.FollowCreatedEvent
import io.getstream.feeds.android.network.models.FollowDeletedEvent
import io.getstream.feeds.android.network.models.FollowUpdatedEvent
import io.getstream.feeds.android.network.models.NotificationFeedUpdatedEvent
import io.getstream.feeds.android.network.models.NotificationStatusResponse
import io.getstream.feeds.android.network.models.PollClosedFeedEvent
import io.getstream.feeds.android.network.models.PollDeletedFeedEvent
import io.getstream.feeds.android.network.models.PollUpdatedFeedEvent
import io.getstream.feeds.android.network.models.PollVoteCastedFeedEvent
import io.getstream.feeds.android.network.models.PollVoteChangedFeedEvent
import io.getstream.feeds.android.network.models.PollVoteRemovedFeedEvent
import io.getstream.feeds.android.network.models.StoriesFeedUpdatedEvent
import io.getstream.feeds.android.network.models.WSEvent
import java.util.Date
import kotlin.reflect.KClass
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
internal class StateUpdateEventToModelTest(
    private val testName: String,
    private val wsEvent: WSEvent,
    private val expectedType: KClass<StateUpdateEvent>,
) {

    @Test
    fun testToModel() {
        val result = wsEvent.toModel()

        assertNotNull("Should not return null for $testName", result)
        assertEquals("Wrong return type for $testName", expectedType, result!!::class)
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): Collection<Array<Any>> =
            listOf(
                activityAdded().shouldMapTo<StateUpdateEvent.ActivityAdded>(),
                activityDeleted().shouldMapTo<StateUpdateEvent.ActivityDeleted>(),
                activityRemovedFromFeed().shouldMapTo<StateUpdateEvent.ActivityRemovedFromFeed>(),
                activityUpdated().shouldMapTo<StateUpdateEvent.ActivityUpdated>(),
                activityPinned().shouldMapTo<StateUpdateEvent.ActivityPinned>(),
                activityUnpinned().shouldMapTo<StateUpdateEvent.ActivityUnpinned>(),
                activityReactionAdded().shouldMapTo<StateUpdateEvent.ActivityReactionUpserted>(),
                activityReactionDeleted().shouldMapTo<StateUpdateEvent.ActivityReactionDeleted>(),
                bookmarkAdded().shouldMapTo<StateUpdateEvent.BookmarkAdded>(),
                bookmarkDeleted().shouldMapTo<StateUpdateEvent.BookmarkDeleted>(),
                bookmarkUpdated().shouldMapTo<StateUpdateEvent.BookmarkUpdated>(),
                bookmarkFolderDeleted().shouldMapTo<StateUpdateEvent.BookmarkFolderDeleted>(),
                bookmarkFolderUpdated().shouldMapTo<StateUpdateEvent.BookmarkFolderUpdated>(),
                commentAdded().shouldMapTo<StateUpdateEvent.CommentAdded>(),
                commentUpdated().shouldMapTo<StateUpdateEvent.CommentUpdated>(),
                commentDeleted().shouldMapTo<StateUpdateEvent.CommentDeleted>(),
                commentReactionAdded().shouldMapTo<StateUpdateEvent.CommentReactionUpserted>(),
                commentReactionDeleted().shouldMapTo<StateUpdateEvent.CommentReactionDeleted>(),
                feedUpdated().shouldMapTo<StateUpdateEvent.FeedUpdated>(),
                feedDeleted().shouldMapTo<StateUpdateEvent.FeedDeleted>(),
                followCreated().shouldMapTo<StateUpdateEvent.FollowAdded>(),
                followUpdated().shouldMapTo<StateUpdateEvent.FollowUpdated>(),
                followDeleted().shouldMapTo<StateUpdateEvent.FollowDeleted>(),
                notificationFeedUpdated().shouldMapTo<StateUpdateEvent.NotificationFeedUpdated>(),
                storiesFeedUpdated().shouldMapTo<StateUpdateEvent.StoriesFeedUpdated>(),
                feedMemberAdded().shouldMapTo<StateUpdateEvent.FeedMemberAdded>(),
                feedMemberRemoved().shouldMapTo<StateUpdateEvent.FeedMemberRemoved>(),
                feedMemberUpdated().shouldMapTo<StateUpdateEvent.FeedMemberUpdated>(),
                pollClosedFeed().shouldMapTo<StateUpdateEvent.PollUpdated>(),
                pollDeletedFeed().shouldMapTo<StateUpdateEvent.PollDeleted>(),
                pollUpdatedFeed().shouldMapTo<StateUpdateEvent.PollUpdated>(),
                pollVoteCastedFeed().shouldMapTo<StateUpdateEvent.PollVoteCasted>(),
                pollVoteChangedFeed().shouldMapTo<StateUpdateEvent.PollVoteChanged>(),
                pollVoteRemovedFeed().shouldMapTo<StateUpdateEvent.PollVoteRemoved>(),
            )

        private inline fun <reified S : StateUpdateEvent> WSEvent.shouldMapTo() =
            arrayOf(this::class.simpleName.orEmpty(), this, S::class)

        private fun activityAdded() =
            ActivityAddedEvent(
                createdAt = Date(1000),
                fid = "group:feed",
                activity = TestData.activityResponse(),
                type = "activity.added",
            )

        private fun activityDeleted() =
            ActivityDeletedEvent(
                createdAt = Date(1000),
                fid = "group:feed",
                activity = TestData.activityResponse(),
                type = "activity.deleted",
            )

        private fun activityUpdated() =
            ActivityUpdatedEvent(
                createdAt = Date(1000),
                fid = "group:feed",
                activity = TestData.activityResponse(),
                type = "activity.updated",
            )

        private fun activityRemovedFromFeed() =
            ActivityRemovedFromFeedEvent(
                createdAt = Date(1000),
                fid = "group:feed",
                activity = TestData.activityResponse(),
                type = "activity.removed",
            )

        private fun activityPinned() =
            ActivityPinnedEvent(
                createdAt = Date(1000),
                fid = "group:feed",
                pinnedActivity = TestData.activityPinResponse(),
                type = "activity.pinned",
            )

        private fun activityUnpinned() =
            ActivityUnpinnedEvent(
                createdAt = Date(1000),
                fid = "group:feed",
                pinnedActivity = TestData.activityPinResponse(),
                type = "activity.unpinned",
            )

        private fun activityReactionAdded() =
            ActivityReactionAddedEvent(
                createdAt = Date(1000),
                fid = "group:feed",
                activity = TestData.activityResponse(),
                reaction = TestData.feedsReactionResponse(),
                type = "reaction.added",
            )

        private fun activityReactionDeleted() =
            ActivityReactionDeletedEvent(
                createdAt = Date(1000),
                fid = "group:feed",
                activity = TestData.activityResponse(),
                reaction = TestData.feedsReactionResponse(),
                type = "reaction.deleted",
            )

        private fun bookmarkAdded() =
            BookmarkAddedEvent(
                createdAt = Date(1000),
                bookmark = TestData.bookmarkResponse(),
                type = "bookmark.added",
            )

        private fun bookmarkDeleted() =
            BookmarkDeletedEvent(
                createdAt = Date(1000),
                bookmark = TestData.bookmarkResponse(),
                type = "bookmark.deleted",
            )

        private fun bookmarkUpdated() =
            BookmarkUpdatedEvent(
                createdAt = Date(1000),
                bookmark = TestData.bookmarkResponse(),
                type = "bookmark.updated",
            )

        private fun bookmarkFolderDeleted() =
            BookmarkFolderDeletedEvent(
                createdAt = Date(1000),
                bookmarkFolder = TestData.bookmarkFolderResponse(),
                type = "bookmark_folder.deleted",
            )

        private fun bookmarkFolderUpdated() =
            BookmarkFolderUpdatedEvent(
                createdAt = Date(1000),
                bookmarkFolder = TestData.bookmarkFolderResponse(),
                type = "bookmark_folder.updated",
            )

        private fun commentAdded() =
            CommentAddedEvent(
                createdAt = Date(1000),
                fid = "group:feed",
                activity = TestData.activityResponse(),
                comment = TestData.commentResponse(),
                type = "comment.added",
            )

        private fun commentUpdated() =
            CommentUpdatedEvent(
                createdAt = Date(1000),
                fid = "group:feed",
                comment = TestData.commentResponse(),
                type = "comment.updated",
            )

        private fun commentDeleted() =
            CommentDeletedEvent(
                createdAt = Date(1000),
                fid = "group:feed",
                comment = TestData.commentResponse(),
                type = "comment.deleted",
            )

        private fun commentReactionAdded() =
            CommentReactionAddedEvent(
                createdAt = Date(1000),
                fid = "group:feed",
                activity = TestData.activityResponse(),
                comment = TestData.commentResponse(),
                reaction = TestData.feedsReactionResponse(),
                type = "comment_reaction.added",
            )

        private fun commentReactionDeleted() =
            CommentReactionDeletedEvent(
                createdAt = Date(1000),
                fid = "group:feed",
                comment = TestData.commentResponse(),
                reaction = TestData.feedsReactionResponse(),
                type = "comment_reaction.deleted",
            )

        private fun feedUpdated() =
            FeedUpdatedEvent(
                createdAt = Date(1000),
                fid = "group:feed",
                feed = TestData.feedResponse(),
                type = "feed.updated",
            )

        private fun feedDeleted() =
            FeedDeletedEvent(createdAt = Date(1000), fid = "group:feed", type = "feed.deleted")

        private fun followCreated() =
            FollowCreatedEvent(
                createdAt = Date(1000),
                fid = "group:feed",
                follow = TestData.followResponse(),
                type = "follow.created",
            )

        private fun followUpdated() =
            FollowUpdatedEvent(
                createdAt = Date(1000),
                fid = "group:feed",
                follow = TestData.followResponse(),
                type = "follow.updated",
            )

        private fun followDeleted() =
            FollowDeletedEvent(
                createdAt = Date(1000),
                fid = "group:feed",
                follow = TestData.followResponse(),
                type = "follow.deleted",
            )

        private fun notificationFeedUpdated() =
            NotificationFeedUpdatedEvent(
                createdAt = Date(1000),
                fid = "group:feed",
                aggregatedActivities = emptyList(),
                notificationStatus = NotificationStatusResponse(unread = 5, unseen = 3),
                type = "notification_feed.updated",
            )

        private fun storiesFeedUpdated() =
            StoriesFeedUpdatedEvent(
                createdAt = Date(1000),
                fid = "group:feed",
                aggregatedActivities = emptyList(),
                type = "stories_feed.updated",
            )

        private fun feedMemberAdded() =
            FeedMemberAddedEvent(
                createdAt = Date(1000),
                fid = "group:feed",
                member = TestData.feedMemberResponse(),
                type = "feed_member.added",
            )

        private fun feedMemberRemoved() =
            FeedMemberRemovedEvent(
                createdAt = Date(1000),
                fid = "group:feed",
                memberId = "user-1",
                type = "feed_member.removed",
            )

        private fun feedMemberUpdated() =
            FeedMemberUpdatedEvent(
                createdAt = Date(1000),
                fid = "group:feed",
                member = TestData.feedMemberResponse(),
                type = "feed_member.updated",
            )

        private fun pollClosedFeed() =
            PollClosedFeedEvent(
                createdAt = Date(1000),
                fid = "group:feed",
                poll = TestData.pollResponseData(),
                type = "poll.closed",
            )

        private fun pollDeletedFeed() =
            PollDeletedFeedEvent(
                createdAt = Date(1000),
                fid = "group:feed",
                poll = TestData.pollResponseData(),
                type = "poll.deleted",
            )

        private fun pollUpdatedFeed() =
            PollUpdatedFeedEvent(
                createdAt = Date(1000),
                fid = "group:feed",
                poll = TestData.pollResponseData(),
                type = "poll.updated",
            )

        private fun pollVoteCastedFeed() =
            PollVoteCastedFeedEvent(
                createdAt = Date(1000),
                fid = "group:feed",
                poll = TestData.pollResponseData(),
                pollVote = TestData.pollVoteResponseData(),
                type = "poll_vote.casted",
            )

        private fun pollVoteChangedFeed() =
            PollVoteChangedFeedEvent(
                createdAt = Date(1000),
                fid = "group:feed",
                poll = TestData.pollResponseData(),
                pollVote = TestData.pollVoteResponseData(),
                type = "poll_vote.changed",
            )

        private fun pollVoteRemovedFeed() =
            PollVoteRemovedFeedEvent(
                createdAt = Date(1000),
                fid = "group:feed",
                poll = TestData.pollResponseData(),
                pollVote = TestData.pollVoteResponseData(),
                type = "poll_vote.removed",
            )
    }
}
