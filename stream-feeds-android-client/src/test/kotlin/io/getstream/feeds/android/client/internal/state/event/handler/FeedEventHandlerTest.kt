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
import io.getstream.feeds.android.client.api.model.toModel
import io.getstream.feeds.android.client.internal.state.FeedStateUpdates
import io.getstream.feeds.android.client.internal.test.TestData.activityResponse
import io.getstream.feeds.android.client.internal.test.TestData.bookmarkResponse
import io.getstream.feeds.android.client.internal.test.TestData.commentResponse
import io.getstream.feeds.android.client.internal.test.TestData.feedResponse
import io.getstream.feeds.android.client.internal.test.TestData.feedsReactionResponse
import io.getstream.feeds.android.client.internal.test.TestData.followResponse
import io.getstream.feeds.android.client.internal.test.TestData.pinActivityResponse
import io.getstream.feeds.android.client.internal.test.TestData.pollResponseData
import io.getstream.feeds.android.client.internal.test.TestData.pollVoteResponseData
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
import io.getstream.feeds.android.network.models.CommentAddedEvent
import io.getstream.feeds.android.network.models.CommentDeletedEvent
import io.getstream.feeds.android.network.models.FeedDeletedEvent
import io.getstream.feeds.android.network.models.FeedUpdatedEvent
import io.getstream.feeds.android.network.models.FollowCreatedEvent
import io.getstream.feeds.android.network.models.FollowDeletedEvent
import io.getstream.feeds.android.network.models.FollowUpdatedEvent
import io.getstream.feeds.android.network.models.NotificationFeedUpdatedEvent
import io.getstream.feeds.android.network.models.PollClosedFeedEvent
import io.getstream.feeds.android.network.models.PollDeletedFeedEvent
import io.getstream.feeds.android.network.models.PollUpdatedFeedEvent
import io.getstream.feeds.android.network.models.PollVoteCastedFeedEvent
import io.getstream.feeds.android.network.models.PollVoteChangedFeedEvent
import io.getstream.feeds.android.network.models.PollVoteRemovedFeedEvent
import io.getstream.feeds.android.network.models.WSEvent
import io.mockk.called
import io.mockk.mockk
import io.mockk.verify
import java.util.Date
import org.junit.Test

internal class FeedEventHandlerTest {
    private val fid = FeedId("user", "feed-1")
    private val state: FeedStateUpdates = mockk(relaxed = true)

    private val handler = FeedEventHandler(fid, state)

    @Test
    fun `on ActivityAddedEvent for matching feed, then call onActivityAdded`() {
        val activity = activityResponse()
        val event =
            ActivityAddedEvent(
                createdAt = Date(),
                fid = fid.rawValue,
                activity = activity,
                type = "feeds.activity.added",
            )

        handler.onEvent(event)

        verify { state.onActivityAdded(activity.toModel()) }
    }

    @Test
    fun `on ActivityAddedEvent for different feed, then do not call onActivityAdded`() {
        val activity = activityResponse()
        val event =
            ActivityAddedEvent(
                createdAt = Date(),
                fid = "user:different-feed",
                activity = activity,
                type = "feeds.activity.added",
            )

        handler.onEvent(event)

        verify(exactly = 0) { state.onActivityAdded(any()) }
    }

    @Test
    fun `on ActivityRemovedFromFeedEvent for matching feed, then call onActivityRemoved`() {
        val activity = activityResponse()
        val event =
            ActivityRemovedFromFeedEvent(
                createdAt = Date(),
                fid = fid.rawValue,
                activity = activity,
                type = "feeds.activity.removed_from_feed",
            )

        handler.onEvent(event)

        verify { state.onActivityRemoved(activity.id) }
    }

    @Test
    fun `on ActivityDeletedEvent for matching feed, then call onActivityRemoved`() {
        val activity = activityResponse()
        val event =
            ActivityDeletedEvent(
                createdAt = Date(),
                fid = fid.rawValue,
                activity = activity,
                type = "feeds.activity.deleted",
            )

        handler.onEvent(event)

        verify { state.onActivityRemoved(activity.id) }
    }

    @Test
    fun `on ActivityUpdatedEvent for matching feed, then call onActivityUpdated`() {
        val activity = activityResponse()
        val event =
            ActivityUpdatedEvent(
                createdAt = Date(),
                fid = fid.rawValue,
                activity = activity,
                type = "feeds.activity.updated",
            )

        handler.onEvent(event)

        verify { state.onActivityUpdated(activity.toModel()) }
    }

    @Test
    fun `on ActivityReactionAddedEvent for matching feed, then call onReactionAdded`() {
        val activity = activityResponse()
        val reaction = feedsReactionResponse()
        val event =
            ActivityReactionAddedEvent(
                createdAt = Date(),
                fid = fid.rawValue,
                activity = activity,
                reaction = reaction,
                type = "feeds.activity.reaction.added",
            )

        handler.onEvent(event)

        verify { state.onReactionAdded(reaction.toModel()) }
    }

    @Test
    fun `on ActivityReactionDeletedEvent for matching feed, then call onReactionRemoved`() {
        val activity = activityResponse()
        val reaction = feedsReactionResponse()
        val event =
            ActivityReactionDeletedEvent(
                createdAt = Date(),
                fid = fid.rawValue,
                activity = activity,
                reaction = reaction,
                type = "feeds.activity.reaction.deleted",
            )

        handler.onEvent(event)

        verify { state.onReactionRemoved(reaction.toModel()) }
    }

    @Test
    fun `on ActivityPinnedEvent for matching feed, then call onActivityPinned`() {
        val pinnedActivity = pinActivityResponse()
        val event =
            ActivityPinnedEvent(
                createdAt = Date(),
                fid = fid.rawValue,
                pinnedActivity = pinnedActivity,
                type = "feeds.activity.pinned",
            )

        handler.onEvent(event)

        verify { state.onActivityPinned(pinnedActivity.toModel()) }
    }

    @Test
    fun `on ActivityUnpinnedEvent for matching feed, then call onActivityUnpinned`() {
        val pinnedActivity = pinActivityResponse()
        val event =
            ActivityUnpinnedEvent(
                createdAt = Date(),
                fid = fid.rawValue,
                pinnedActivity = pinnedActivity,
                type = "feeds.activity.unpinned",
            )

        handler.onEvent(event)

        verify { state.onActivityUnpinned(pinnedActivity.activity.id) }
    }

    @Test
    fun `on BookmarkAddedEvent for activity in feed, then call onBookmarkAdded`() {
        val bookmark =
            bookmarkResponse()
                .copy(
                    activity = activityResponse().copy(feeds = listOf(fid.rawValue, "other:feed"))
                )
        val event =
            BookmarkAddedEvent(
                createdAt = Date(),
                bookmark = bookmark,
                type = "feeds.bookmark.added",
            )

        handler.onEvent(event)

        verify { state.onBookmarkAdded(bookmark.toModel()) }
    }

    @Test
    fun `on BookmarkAddedEvent for activity not in feed, then do not call onBookmarkAdded`() {
        val bookmark =
            bookmarkResponse()
                .copy(
                    activity = activityResponse().copy(feeds = listOf("other:feed", "another:feed"))
                )
        val event =
            BookmarkAddedEvent(
                createdAt = Date(),
                bookmark = bookmark,
                type = "feeds.bookmark.added",
            )

        handler.onEvent(event)

        verify(exactly = 0) { state.onBookmarkAdded(any()) }
    }

    @Test
    fun `on BookmarkDeletedEvent for activity in feed, then call onBookmarkRemoved`() {
        val bookmark =
            bookmarkResponse()
                .copy(activity = activityResponse().copy(feeds = listOf(fid.rawValue)))
        val event =
            BookmarkDeletedEvent(
                createdAt = Date(),
                bookmark = bookmark,
                type = "feeds.bookmark.deleted",
            )

        handler.onEvent(event)

        verify { state.onBookmarkRemoved(bookmark.toModel()) }
    }

    @Test
    fun `on CommentAddedEvent for matching feed, then call onCommentAdded`() {
        val comment = commentResponse()
        val event =
            CommentAddedEvent(
                createdAt = Date(),
                fid = fid.rawValue,
                comment = comment,
                type = "feeds.comment.added",
                activity = activityResponse(),
            )

        handler.onEvent(event)

        verify { state.onCommentAdded(comment.toModel()) }
    }

    @Test
    fun `on CommentDeletedEvent for matching feed, then call onCommentRemoved`() {
        val comment = commentResponse()
        val event =
            CommentDeletedEvent(
                createdAt = Date(),
                fid = fid.rawValue,
                comment = comment,
                type = "feeds.comment.deleted",
            )

        handler.onEvent(event)

        verify { state.onCommentRemoved(comment.toModel()) }
    }

    @Test
    fun `on FeedDeletedEvent for matching feed, then call onFeedDeleted`() {
        val event =
            FeedDeletedEvent(createdAt = Date(), fid = fid.rawValue, type = "feeds.feed.deleted")

        handler.onEvent(event)

        verify { state.onFeedDeleted() }
    }

    @Test
    fun `on FeedUpdatedEvent for matching feed, then call onFeedUpdated`() {
        val feed = feedResponse()
        val event =
            FeedUpdatedEvent(
                createdAt = Date(),
                fid = fid.rawValue,
                feed = feed,
                type = "feeds.feed.updated",
            )

        handler.onEvent(event)

        verify { state.onFeedUpdated(feed.toModel()) }
    }

    @Test
    fun `on FollowCreatedEvent for matching feed, then call onFollowAdded`() {
        val follow = followResponse()
        val event =
            FollowCreatedEvent(
                createdAt = Date(),
                fid = fid.rawValue,
                follow = follow,
                type = "feeds.follow.created",
            )

        handler.onEvent(event)

        verify { state.onFollowAdded(follow.toModel()) }
    }

    @Test
    fun `on FollowDeletedEvent for matching feed, then call onFollowRemoved`() {
        val follow = followResponse()
        val event =
            FollowDeletedEvent(
                createdAt = Date(),
                fid = fid.rawValue,
                follow = follow,
                type = "feeds.follow.deleted",
            )

        handler.onEvent(event)

        verify { state.onFollowRemoved(follow.toModel()) }
    }

    @Test
    fun `on FollowUpdatedEvent for matching feed, then call onFollowUpdated`() {
        val follow = followResponse()
        val event =
            FollowUpdatedEvent(
                createdAt = Date(),
                fid = fid.rawValue,
                follow = follow,
                type = "feeds.follow.updated",
            )

        handler.onEvent(event)

        verify { state.onFollowUpdated(follow.toModel()) }
    }

    @Test
    fun `on NotificationFeedUpdatedEvent, then call onNotificationFeedUpdated`() {
        val event =
            NotificationFeedUpdatedEvent(
                createdAt = Date(),
                fid = fid.rawValue,
                aggregatedActivities = emptyList(),
                notificationStatus = null,
                type = "feeds.notification.updated",
            )

        handler.onEvent(event)

        verify { state.onNotificationFeedUpdated(emptyList(), null) }
    }

    @Test
    fun `on PollClosedFeedEvent for matching feed, then call onPollChanged`() {
        val poll = pollResponseData()
        val event =
            PollClosedFeedEvent(
                createdAt = Date(),
                fid = fid.rawValue,
                poll = poll,
                type = "feeds.poll.closed",
            )

        handler.onEvent(event)

        verify { state.onPollChanged(poll.id, poll.toModel()) }
    }

    @Test
    fun `on PollDeletedFeedEvent for matching feed, then call onPollChanged with null`() {
        val poll = pollResponseData()
        val event =
            PollDeletedFeedEvent(
                createdAt = Date(),
                fid = fid.rawValue,
                poll = poll,
                type = "feeds.poll.deleted",
            )

        handler.onEvent(event)

        verify { state.onPollChanged(poll.id, null) }
    }

    @Test
    fun `on PollUpdatedFeedEvent for matching feed, then call onPollChanged`() {
        val poll = pollResponseData()
        val event =
            PollUpdatedFeedEvent(
                createdAt = Date(),
                fid = fid.rawValue,
                poll = poll,
                type = "feeds.poll.updated",
            )

        handler.onEvent(event)

        verify { state.onPollChanged(poll.id, poll.toModel()) }
    }

    @Test
    fun `on PollVoteCastedFeedEvent for matching feed, then call onPollChanged`() {
        val poll = pollResponseData()
        val pollVote = pollVoteResponseData()
        val event =
            PollVoteCastedFeedEvent(
                createdAt = Date(),
                fid = fid.rawValue,
                poll = poll,
                pollVote = pollVote,
                type = "feeds.poll.vote.casted",
            )

        handler.onEvent(event)

        verify { state.onPollChanged(poll.id, poll.toModel()) }
    }

    @Test
    fun `on PollVoteChangedFeedEvent for matching feed, then call onPollChanged`() {
        val poll = pollResponseData()
        val pollVote = pollVoteResponseData()
        val event =
            PollVoteChangedFeedEvent(
                createdAt = Date(),
                fid = fid.rawValue,
                poll = poll,
                pollVote = pollVote,
                type = "feeds.poll.vote.changed",
            )

        handler.onEvent(event)

        verify { state.onPollChanged(poll.id, poll.toModel()) }
    }

    @Test
    fun `on PollVoteRemovedFeedEvent for matching feed, then call onPollChanged`() {
        val poll = pollResponseData()
        val pollVote = pollVoteResponseData()
        val event =
            PollVoteRemovedFeedEvent(
                createdAt = Date(),
                fid = fid.rawValue,
                poll = poll,
                pollVote = pollVote,
                type = "feeds.poll.vote.removed",
            )

        handler.onEvent(event)

        verify { state.onPollChanged(poll.id, poll.toModel()) }
    }

    @Test
    fun `on unknown event, then do nothing`() {
        val unknownEvent =
            object : WSEvent {
                override fun getWSEventType(): String = "unknown.event"
            }

        handler.onEvent(unknownEvent)

        verify { state wasNot called }
    }
}
