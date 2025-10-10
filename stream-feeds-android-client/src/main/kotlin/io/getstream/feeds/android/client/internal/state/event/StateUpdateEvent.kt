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

import io.getstream.feeds.android.client.api.model.ActivityData
import io.getstream.feeds.android.client.api.model.ActivityPinData
import io.getstream.feeds.android.client.api.model.AggregatedActivityData
import io.getstream.feeds.android.client.api.model.BookmarkData
import io.getstream.feeds.android.client.api.model.BookmarkFolderData
import io.getstream.feeds.android.client.api.model.CommentData
import io.getstream.feeds.android.client.api.model.FeedData
import io.getstream.feeds.android.client.api.model.FeedMemberData
import io.getstream.feeds.android.client.api.model.FeedsReactionData
import io.getstream.feeds.android.client.api.model.FollowData
import io.getstream.feeds.android.client.api.model.PollData
import io.getstream.feeds.android.client.api.model.PollVoteData
import io.getstream.feeds.android.client.api.model.toModel
import io.getstream.feeds.android.network.models.ActivityAddedEvent
import io.getstream.feeds.android.network.models.ActivityDeletedEvent
import io.getstream.feeds.android.network.models.ActivityPinnedEvent
import io.getstream.feeds.android.network.models.ActivityReactionAddedEvent
import io.getstream.feeds.android.network.models.ActivityReactionDeletedEvent
import io.getstream.feeds.android.network.models.ActivityReactionUpdatedEvent
import io.getstream.feeds.android.network.models.ActivityRemovedFromFeedEvent
import io.getstream.feeds.android.network.models.ActivityUnpinnedEvent
import io.getstream.feeds.android.network.models.ActivityUpdatedEvent
import io.getstream.feeds.android.network.models.AggregatedActivityResponse
import io.getstream.feeds.android.network.models.BookmarkAddedEvent
import io.getstream.feeds.android.network.models.BookmarkDeletedEvent
import io.getstream.feeds.android.network.models.BookmarkFolderDeletedEvent
import io.getstream.feeds.android.network.models.BookmarkFolderUpdatedEvent
import io.getstream.feeds.android.network.models.BookmarkUpdatedEvent
import io.getstream.feeds.android.network.models.CommentAddedEvent
import io.getstream.feeds.android.network.models.CommentDeletedEvent
import io.getstream.feeds.android.network.models.CommentReactionAddedEvent
import io.getstream.feeds.android.network.models.CommentReactionDeletedEvent
import io.getstream.feeds.android.network.models.CommentReactionUpdatedEvent
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
import io.getstream.feeds.android.network.models.WSEvent

/**
 * Represents an event that may trigger a state update. These events are typically the result of
 * receiving a WebSocket event or having executed a successful API call that can modify the state.
 */
internal sealed interface StateUpdateEvent {

    data class ActivityAdded(val fid: String, val activity: ActivityData) : StateUpdateEvent

    data class ActivityDeleted(val fid: String, val activityId: String) : StateUpdateEvent

    data class ActivityRemovedFromFeed(val fid: String, val activityId: String) : StateUpdateEvent

    data class ActivityUpdated(val fid: String, val activity: ActivityData) : StateUpdateEvent

    data class ActivityPinned(val fid: String, val pinnedActivity: ActivityPinData) :
        StateUpdateEvent

    data class ActivityUnpinned(val fid: String, val activityId: String) : StateUpdateEvent

    data class ActivityReactionAdded(
        val fid: String,
        val activity: ActivityData,
        val reaction: FeedsReactionData,
    ) : StateUpdateEvent

    data class ActivityReactionDeleted(
        val fid: String,
        val activity: ActivityData,
        val reaction: FeedsReactionData,
    ) : StateUpdateEvent

    data class ActivityReactionUpdated(
        val fid: String,
        val activity: ActivityData,
        val reaction: FeedsReactionData,
    ) : StateUpdateEvent

    data class BookmarkAdded(val bookmark: BookmarkData) : StateUpdateEvent

    data class BookmarkDeleted(val bookmark: BookmarkData) : StateUpdateEvent

    data class BookmarkUpdated(val bookmark: BookmarkData) : StateUpdateEvent

    data class BookmarkFolderDeleted(val folderId: String) : StateUpdateEvent

    data class BookmarkFolderUpdated(val folder: BookmarkFolderData) : StateUpdateEvent

    data class CommentAdded(val fid: String, val comment: CommentData) : StateUpdateEvent

    data class CommentDeleted(val fid: String, val comment: CommentData) : StateUpdateEvent

    data class CommentUpdated(val comment: CommentData) : StateUpdateEvent

    data class CommentReactionAdded(
        val fid: String,
        val comment: CommentData,
        val reaction: FeedsReactionData,
    ) : StateUpdateEvent

    data class CommentReactionDeleted(
        val fid: String,
        val comment: CommentData,
        val reaction: FeedsReactionData,
    ) : StateUpdateEvent

    data class CommentReactionUpdated(
        val fid: String,
        val comment: CommentData,
        val reaction: FeedsReactionData,
    ) : StateUpdateEvent

    data class FeedUpdated(val feed: FeedData) : StateUpdateEvent

    data class FeedDeleted(val fid: String) : StateUpdateEvent

    data class FeedMemberAdded(val fid: String, val member: FeedMemberData) : StateUpdateEvent

    data class FeedMemberRemoved(val fid: String, val memberId: String) : StateUpdateEvent

    data class FeedMemberUpdated(val fid: String, val member: FeedMemberData) : StateUpdateEvent

    data class FollowAdded(val follow: FollowData) : StateUpdateEvent

    data class FollowUpdated(val follow: FollowData) : StateUpdateEvent

    data class FollowDeleted(val follow: FollowData) : StateUpdateEvent

    data class NotificationFeedUpdated(
        val fid: String,
        val aggregatedActivities: List<AggregatedActivityData>,
        val notificationStatus: NotificationStatusResponse?,
    ) : StateUpdateEvent

    data class PollClosed(val fid: String, val poll: PollData) : StateUpdateEvent

    data class PollDeleted(val fid: String, val pollId: String) : StateUpdateEvent

    data class PollUpdated(val fid: String, val poll: PollData) : StateUpdateEvent

    data class PollVoteCasted(val fid: String, val pollId: String, val vote: PollVoteData) :
        StateUpdateEvent

    data class PollVoteChanged(val fid: String, val pollId: String, val vote: PollVoteData) :
        StateUpdateEvent

    data class PollVoteRemoved(val fid: String, val pollId: String, val vote: PollVoteData) :
        StateUpdateEvent
}

internal fun WSEvent.toModel(): StateUpdateEvent? =
    when (this) {
        is ActivityAddedEvent -> StateUpdateEvent.ActivityAdded(fid, activity.toModel())

        is ActivityDeletedEvent -> StateUpdateEvent.ActivityDeleted(fid, activity.id)

        is ActivityRemovedFromFeedEvent ->
            StateUpdateEvent.ActivityRemovedFromFeed(fid, activity.id)

        is ActivityUpdatedEvent -> StateUpdateEvent.ActivityUpdated(fid, activity.toModel())

        is ActivityPinnedEvent -> StateUpdateEvent.ActivityPinned(fid, pinnedActivity.toModel())

        is ActivityUnpinnedEvent ->
            StateUpdateEvent.ActivityUnpinned(fid, pinnedActivity.activity.id)

        is ActivityReactionAddedEvent ->
            StateUpdateEvent.ActivityReactionAdded(fid, activity.toModel(), reaction.toModel())

        is ActivityReactionDeletedEvent ->
            StateUpdateEvent.ActivityReactionDeleted(fid, activity.toModel(), reaction.toModel())

        is ActivityReactionUpdatedEvent ->
            StateUpdateEvent.ActivityReactionUpdated(fid, activity.toModel(), reaction.toModel())

        is BookmarkAddedEvent -> StateUpdateEvent.BookmarkAdded(bookmark.toModel())

        is BookmarkDeletedEvent -> StateUpdateEvent.BookmarkDeleted(bookmark.toModel())

        is BookmarkUpdatedEvent -> StateUpdateEvent.BookmarkUpdated(bookmark.toModel())

        is BookmarkFolderDeletedEvent -> StateUpdateEvent.BookmarkFolderDeleted(bookmarkFolder.id)

        is BookmarkFolderUpdatedEvent ->
            StateUpdateEvent.BookmarkFolderUpdated(bookmarkFolder.toModel())

        is CommentAddedEvent -> StateUpdateEvent.CommentAdded(fid, comment.toModel())

        is CommentUpdatedEvent -> StateUpdateEvent.CommentUpdated(comment.toModel())

        is CommentDeletedEvent -> StateUpdateEvent.CommentDeleted(fid, comment.toModel())

        is CommentReactionAddedEvent ->
            StateUpdateEvent.CommentReactionAdded(fid, comment.toModel(), reaction.toModel())

        is CommentReactionDeletedEvent ->
            StateUpdateEvent.CommentReactionDeleted(fid, comment.toModel(), reaction.toModel())

        is CommentReactionUpdatedEvent ->
            StateUpdateEvent.CommentReactionUpdated(fid, comment.toModel(), reaction.toModel())

        is FeedUpdatedEvent -> StateUpdateEvent.FeedUpdated(feed.toModel())

        is FeedDeletedEvent -> StateUpdateEvent.FeedDeleted(fid)

        is FollowCreatedEvent -> StateUpdateEvent.FollowAdded(follow.toModel())

        is FollowUpdatedEvent -> StateUpdateEvent.FollowUpdated(follow.toModel())

        is FollowDeletedEvent -> StateUpdateEvent.FollowDeleted(follow.toModel())

        is NotificationFeedUpdatedEvent ->
            StateUpdateEvent.NotificationFeedUpdated(
                fid = fid,
                aggregatedActivities =
                    aggregatedActivities?.map(AggregatedActivityResponse::toModel).orEmpty(),
                notificationStatus = notificationStatus,
            )

        is FeedMemberAddedEvent -> StateUpdateEvent.FeedMemberAdded(fid, member.toModel())

        is FeedMemberRemovedEvent -> StateUpdateEvent.FeedMemberRemoved(fid, memberId)

        is FeedMemberUpdatedEvent -> StateUpdateEvent.FeedMemberUpdated(fid, member.toModel())

        is PollClosedFeedEvent -> StateUpdateEvent.PollClosed(fid, poll.toModel())

        is PollDeletedFeedEvent -> StateUpdateEvent.PollDeleted(fid, poll.id)

        is PollUpdatedFeedEvent -> StateUpdateEvent.PollUpdated(fid, poll.toModel())

        is PollVoteCastedFeedEvent ->
            StateUpdateEvent.PollVoteCasted(fid, poll.id, pollVote.toModel())

        is PollVoteChangedFeedEvent ->
            StateUpdateEvent.PollVoteChanged(fid, poll.id, pollVote.toModel())

        is PollVoteRemovedFeedEvent ->
            StateUpdateEvent.PollVoteRemoved(fid, poll.id, pollVote.toModel())

        else -> null
    }
