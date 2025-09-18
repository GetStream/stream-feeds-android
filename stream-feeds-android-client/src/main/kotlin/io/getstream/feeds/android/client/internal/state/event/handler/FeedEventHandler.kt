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
import io.getstream.feeds.android.client.internal.subscribe.FeedsEventListener
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

/**
 * This class handles feed events and updates the feed state accordingly. It is responsible for
 * processing incoming events related to feeds, such as activity updates, reactions, comments, and
 * other feed-related actions.
 *
 * @param fid The unique identifier for the feed this handler is associated with.
 * @property state The instance that manages updates to the feed state.
 */
internal class FeedEventHandler(private val fid: FeedId, private val state: FeedStateUpdates) :
    FeedsEventListener {

    /**
     * Processes a WebSocket event and updates the feed state.
     *
     * @param event The WebSocket event to process.
     */
    override fun onEvent(event: WSEvent) {
        when (event) {
            is ActivityAddedEvent -> {
                if (event.fid == fid.rawValue) {
                    state.onActivityAdded(event.activity.toModel())
                }
            }

            is ActivityDeletedEvent -> {
                if (event.fid == fid.rawValue) {
                    state.onActivityRemoved(event.activity.id)
                }
            }

            is ActivityRemovedFromFeedEvent -> {
                if (event.fid == fid.rawValue) {
                    state.onActivityRemoved(event.activity.id)
                }
            }

            is ActivityReactionAddedEvent -> {
                if (event.fid == fid.rawValue) {
                    state.onReactionAdded(event.reaction.toModel())
                }
            }

            is ActivityReactionDeletedEvent -> {
                if (event.fid == fid.rawValue) {
                    state.onReactionRemoved(event.reaction.toModel())
                }
            }

            is ActivityUpdatedEvent -> {
                if (event.fid == fid.rawValue) {
                    state.onActivityUpdated(event.activity.toModel())
                }
            }

            is ActivityPinnedEvent -> {
                if (event.fid == fid.rawValue) {
                    state.onActivityPinned(event.pinnedActivity.toModel())
                }
            }

            is ActivityUnpinnedEvent -> {
                if (event.fid == fid.rawValue) {
                    state.onActivityUnpinned(event.pinnedActivity.activity.id)
                }
            }

            is BookmarkAddedEvent -> {
                if (event.bookmark.activity.feeds.contains(fid.rawValue)) {
                    state.onBookmarkAdded(event.bookmark.toModel())
                }
            }

            is BookmarkDeletedEvent -> {
                if (event.bookmark.activity.feeds.contains(fid.rawValue)) {
                    state.onBookmarkRemoved(event.bookmark.toModel())
                }
            }

            is CommentAddedEvent -> {
                if (event.fid == fid.rawValue) {
                    state.onCommentAdded(event.comment.toModel())
                }
            }

            is CommentDeletedEvent -> {
                if (event.fid == fid.rawValue) {
                    state.onCommentRemoved(event.comment.toModel())
                }
            }

            is FeedDeletedEvent -> {
                if (event.fid == fid.rawValue) {
                    state.onFeedDeleted()
                }
            }

            is FeedUpdatedEvent -> {
                if (event.fid == fid.rawValue) {
                    state.onFeedUpdated(event.feed.toModel())
                }
            }

            is FollowCreatedEvent -> {
                if (event.fid == fid.rawValue) {
                    state.onFollowAdded(event.follow.toModel())
                }
            }

            is FollowDeletedEvent -> {
                if (event.fid == fid.rawValue) {
                    state.onFollowRemoved(event.follow.toModel())
                }
            }

            is FollowUpdatedEvent -> {
                if (event.fid == fid.rawValue) {
                    state.onFollowUpdated(event.follow.toModel())
                }
            }

            is NotificationFeedUpdatedEvent -> {
                if (event.fid == fid.rawValue) {
                    state.onNotificationFeedUpdated(
                        aggregatedActivities =
                            event.aggregatedActivities?.map { it.toModel() }.orEmpty(),
                        notificationStatus = event.notificationStatus,
                    )
                }
            }

            is PollClosedFeedEvent -> {
                if (event.fid == fid.rawValue) {
                    state.onPollChanged(event.poll.id, event.poll.toModel())
                }
            }

            is PollDeletedFeedEvent -> {
                if (event.fid == fid.rawValue) {
                    state.onPollChanged(event.poll.id, null)
                }
            }

            is PollUpdatedFeedEvent -> {
                if (event.fid == fid.rawValue) {
                    state.onPollChanged(event.poll.id, event.poll.toModel())
                }
            }

            is PollVoteCastedFeedEvent -> {
                if (event.fid == fid.rawValue) {
                    state.onPollChanged(event.poll.id, event.poll.toModel())
                }
            }

            is PollVoteChangedFeedEvent -> {
                if (event.fid == fid.rawValue) {
                    state.onPollChanged(event.poll.id, event.poll.toModel())
                }
            }

            is PollVoteRemovedFeedEvent -> {
                if (event.fid == fid.rawValue) {
                    state.onPollChanged(event.poll.id, event.poll.toModel())
                }
            }

            else -> {
                // Handle other events if necessary
            }
        }
    }
}
