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
import io.getstream.feeds.android.client.api.model.ModelUpdates
import io.getstream.feeds.android.client.api.model.PollData
import io.getstream.feeds.android.client.api.model.PollVoteData
import io.getstream.feeds.android.client.internal.model.toModel
import io.getstream.feeds.android.network.models.ActivityAddedEvent
import io.getstream.feeds.android.network.models.ActivityDeletedEvent
import io.getstream.feeds.android.network.models.ActivityFeedbackEvent
import io.getstream.feeds.android.network.models.ActivityFeedbackEventPayload
import io.getstream.feeds.android.network.models.ActivityPinnedEvent
import io.getstream.feeds.android.network.models.ActivityReactionAddedEvent
import io.getstream.feeds.android.network.models.ActivityReactionDeletedEvent
import io.getstream.feeds.android.network.models.ActivityReactionUpdatedEvent
import io.getstream.feeds.android.network.models.ActivityRemovedFromFeedEvent
import io.getstream.feeds.android.network.models.ActivityResponse
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
import io.getstream.feeds.android.network.models.FeedCreatedEvent
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

/**
 * Represents an event that may trigger a state update. These events are typically the result of
 * receiving a WebSocket event or having executed a successful API call that can modify the state.
 */
internal sealed interface StateUpdateEvent {

    data class ActivityAdded(val scope: FidScope, val activity: ActivityData) : StateUpdateEvent

    data class ActivityDeleted(val scope: FidScope, val activityId: String) : StateUpdateEvent

    data class ActivityRemovedFromFeed(val scope: FidScope, val activityId: String) :
        StateUpdateEvent

    data class ActivityUpdated(val scope: FidScope, val activity: ActivityData) : StateUpdateEvent

    data class ActivityPinned(val scope: FidScope, val pinnedActivity: ActivityPinData) :
        StateUpdateEvent

    data class ActivityUnpinned(val scope: FidScope, val activityId: String) : StateUpdateEvent

    data class ActivityHidden(val activityId: String, val userId: String, val hidden: Boolean) :
        StateUpdateEvent

    data class ActivityReactionDeleted(
        val scope: FidScope,
        val activity: ActivityData,
        val reaction: FeedsReactionData,
    ) : StateUpdateEvent

    data class ActivityReactionUpserted(
        val scope: FidScope,
        val activity: ActivityData,
        val reaction: FeedsReactionData,
        val enforceUnique: Boolean,
    ) : StateUpdateEvent

    data class BookmarkAdded(val bookmark: BookmarkData) : StateUpdateEvent

    data class BookmarkDeleted(val bookmark: BookmarkData) : StateUpdateEvent

    data class BookmarkUpdated(val bookmark: BookmarkData) : StateUpdateEvent

    data class BookmarkFolderDeleted(val folderId: String) : StateUpdateEvent

    data class BookmarkFolderUpdated(val folder: BookmarkFolderData) : StateUpdateEvent

    data class CommentAdded(val scope: FidScope, val comment: CommentData) : StateUpdateEvent

    data class CommentDeleted(val scope: FidScope, val comment: CommentData) : StateUpdateEvent

    data class CommentUpdated(val scope: FidScope, val comment: CommentData) : StateUpdateEvent

    data class CommentReactionDeleted(
        val scope: FidScope,
        val comment: CommentData,
        val reaction: FeedsReactionData,
    ) : StateUpdateEvent

    data class CommentReactionUpserted(
        val scope: FidScope,
        val comment: CommentData,
        val reaction: FeedsReactionData,
        val enforceUnique: Boolean,
    ) : StateUpdateEvent

    data class FeedAdded(val feed: FeedData) : StateUpdateEvent

    data class FeedDeleted(val fid: String) : StateUpdateEvent

    data class FeedUpdated(val feed: FeedData) : StateUpdateEvent

    data class FeedMemberAdded(val fid: String, val member: FeedMemberData) : StateUpdateEvent

    data class FeedMemberRemoved(val fid: String, val memberId: String) : StateUpdateEvent

    data class FeedMemberUpdated(val fid: String, val member: FeedMemberData) : StateUpdateEvent

    data class FeedMemberBatchUpdate(val fid: String, val updates: ModelUpdates<FeedMemberData>) :
        StateUpdateEvent

    data class FollowAdded(val follow: FollowData) : StateUpdateEvent

    data class FollowDeleted(val follow: FollowData) : StateUpdateEvent

    data class FollowUpdated(val follow: FollowData) : StateUpdateEvent

    data class NotificationFeedUpdated(
        val fid: String,
        val aggregatedActivities: List<AggregatedActivityData>,
        val notificationStatus: NotificationStatusResponse?,
    ) : StateUpdateEvent

    data class StoriesFeedUpdated(
        val fid: String,
        val activities: List<ActivityData>,
        val aggregatedActivities: List<AggregatedActivityData>,
    ) : StateUpdateEvent

    data class PollDeleted(val pollId: String) : StateUpdateEvent

    data class PollUpdated(val poll: PollData) : StateUpdateEvent

    data class PollVoteRemoved(val pollId: String, val vote: PollVoteData) : StateUpdateEvent

    data class PollVoteCasted(val pollId: String, val vote: PollVoteData) : StateUpdateEvent

    data class PollVoteChanged(val pollId: String, val vote: PollVoteData) : StateUpdateEvent
}

internal fun WSEvent.toModel(): StateUpdateEvent? =
    when (this) {
        is ActivityAddedEvent ->
            StateUpdateEvent.ActivityAdded(FidScope.of(fid), activity.toModel())

        is ActivityDeletedEvent -> StateUpdateEvent.ActivityDeleted(FidScope.of(fid), activity.id)

        is ActivityRemovedFromFeedEvent ->
            StateUpdateEvent.ActivityRemovedFromFeed(FidScope.of(fid), activity.id)

        is ActivityUpdatedEvent ->
            StateUpdateEvent.ActivityUpdated(FidScope.of(fid), activity.toModel())

        is ActivityPinnedEvent ->
            StateUpdateEvent.ActivityPinned(FidScope.of(fid), pinnedActivity.toModel())

        is ActivityUnpinnedEvent ->
            StateUpdateEvent.ActivityUnpinned(FidScope.of(fid), pinnedActivity.activity.id)

        is ActivityFeedbackEvent ->
            when (activityFeedback.action) {
                ActivityFeedbackEventPayload.Action.Hide ->
                    StateUpdateEvent.ActivityHidden(
                        activityId = activityFeedback.activityId,
                        userId = activityFeedback.user.id,
                        hidden = activityFeedback.value == "true",
                    )
                else -> null
            }

        is ActivityReactionAddedEvent ->
            StateUpdateEvent.ActivityReactionUpserted(
                scope = FidScope.of(fid),
                activity = activity.toModel(),
                reaction = reaction.toModel(),
                enforceUnique = false,
            )

        is ActivityReactionDeletedEvent ->
            StateUpdateEvent.ActivityReactionDeleted(
                FidScope.of(fid),
                activity.toModel(),
                reaction.toModel(),
            )

        is ActivityReactionUpdatedEvent ->
            StateUpdateEvent.ActivityReactionUpserted(
                scope = FidScope.of(fid),
                activity = activity.toModel(),
                reaction = reaction.toModel(),
                enforceUnique = true,
            )

        is BookmarkAddedEvent -> StateUpdateEvent.BookmarkAdded(bookmark.toModel())

        is BookmarkDeletedEvent -> StateUpdateEvent.BookmarkDeleted(bookmark.toModel())

        is BookmarkUpdatedEvent -> StateUpdateEvent.BookmarkUpdated(bookmark.toModel())

        is BookmarkFolderDeletedEvent -> StateUpdateEvent.BookmarkFolderDeleted(bookmarkFolder.id)

        is BookmarkFolderUpdatedEvent ->
            StateUpdateEvent.BookmarkFolderUpdated(bookmarkFolder.toModel())

        is CommentAddedEvent -> StateUpdateEvent.CommentAdded(FidScope.of(fid), comment.toModel())

        is CommentUpdatedEvent ->
            StateUpdateEvent.CommentUpdated(FidScope.of(fid), comment.toModel())

        is CommentDeletedEvent ->
            StateUpdateEvent.CommentDeleted(FidScope.of(fid), comment.toModel())

        is CommentReactionAddedEvent ->
            StateUpdateEvent.CommentReactionUpserted(
                scope = FidScope.of(fid),
                comment = comment.toModel(),
                reaction = reaction.toModel(),
                enforceUnique = false,
            )

        is CommentReactionDeletedEvent ->
            StateUpdateEvent.CommentReactionDeleted(
                scope = FidScope.of(fid),
                comment = comment.toModel(),
                reaction = reaction.toModel(),
            )

        is CommentReactionUpdatedEvent ->
            StateUpdateEvent.CommentReactionUpserted(
                scope = FidScope.of(fid),
                comment = comment.toModel(),
                reaction = reaction.toModel(),
                enforceUnique = true,
            )

        is FeedCreatedEvent -> StateUpdateEvent.FeedAdded(feed.toModel())

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

        is StoriesFeedUpdatedEvent ->
            StateUpdateEvent.StoriesFeedUpdated(
                fid = fid,
                activities = activities?.map(ActivityResponse::toModel).orEmpty(),
                aggregatedActivities =
                    aggregatedActivities?.map(AggregatedActivityResponse::toModel).orEmpty(),
            )

        is FeedMemberAddedEvent -> StateUpdateEvent.FeedMemberAdded(fid, member.toModel())

        is FeedMemberRemovedEvent -> StateUpdateEvent.FeedMemberRemoved(fid, memberId)

        is FeedMemberUpdatedEvent -> StateUpdateEvent.FeedMemberUpdated(fid, member.toModel())

        is PollClosedFeedEvent -> StateUpdateEvent.PollUpdated(poll.toModel())

        is PollDeletedFeedEvent -> StateUpdateEvent.PollDeleted(poll.id)

        is PollUpdatedFeedEvent -> StateUpdateEvent.PollUpdated(poll.toModel())

        is PollVoteCastedFeedEvent -> StateUpdateEvent.PollVoteCasted(poll.id, pollVote.toModel())

        is PollVoteChangedFeedEvent -> StateUpdateEvent.PollVoteChanged(poll.id, pollVote.toModel())

        is PollVoteRemovedFeedEvent -> StateUpdateEvent.PollVoteRemoved(poll.id, pollVote.toModel())

        else -> null
    }
