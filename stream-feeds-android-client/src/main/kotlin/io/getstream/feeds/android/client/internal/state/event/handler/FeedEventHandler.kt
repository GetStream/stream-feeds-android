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
import io.getstream.feeds.android.client.api.model.FollowData
import io.getstream.feeds.android.client.api.state.query.ActivitiesFilter
import io.getstream.feeds.android.client.internal.state.FeedStateUpdates
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent
import io.getstream.feeds.android.client.internal.state.query.matches
import io.getstream.feeds.android.client.internal.subscribe.StateUpdateEventListener

/**
 * This class handles feed events and updates the feed state accordingly. It is responsible for
 * processing incoming events related to feeds, such as activity updates, reactions, comments, and
 * other feed-related actions.
 *
 * @param fid The unique identifier for the feed this handler is associated with.
 * @param activityFilter An optional filter to determine which activities should be processed.
 * @param currentUserId The ID of the current user, used to filter user-specific events.
 * @property state The instance that manages updates to the feed state.
 */
internal class FeedEventHandler(
    private val fid: FeedId,
    private val activityFilter: ActivitiesFilter?,
    private val currentUserId: String,
    private val state: FeedStateUpdates,
) : StateUpdateEventListener {

    /**
     * Processes a state update event and updates the feed state.
     *
     * @param event The state update event to process.
     */
    override fun onEvent(event: StateUpdateEvent) {
        when (event) {
            is StateUpdateEvent.ActivityAdded -> {
                if (event.fid == fid.rawValue && event.activity matches activityFilter) {
                    state.onActivityUpserted(event.activity)
                }
            }

            is StateUpdateEvent.ActivityDeleted -> {
                if (event.fid == fid.rawValue) {
                    state.onActivityRemoved(event.activityId)
                }
            }

            is StateUpdateEvent.ActivityUpdated -> {
                if (event.fid == fid.rawValue) {
                    if (event.activity matches activityFilter) {
                        state.onActivityUpserted(event.activity)
                    } else {
                        // We remove elements that used to match the filter but no longer do
                        state.onActivityRemoved(event.activity.id)
                    }
                }
            }

            is StateUpdateEvent.ActivityRemovedFromFeed -> {
                if (event.fid == fid.rawValue) {
                    state.onActivityRemoved(event.activityId)
                }
            }

            is StateUpdateEvent.ActivityReactionDeleted -> {
                if (event.fid == fid.rawValue) {
                    state.onReactionRemoved(event.reaction, event.activity)
                }
            }

            is StateUpdateEvent.ActivityReactionUpserted -> {
                if (event.fid == fid.rawValue) {
                    state.onReactionUpserted(event.reaction, event.activity, event.enforceUnique)
                }
            }

            is StateUpdateEvent.ActivityPinned -> {
                if (event.fid == fid.rawValue) {
                    state.onActivityPinned(event.pinnedActivity)
                }
            }

            is StateUpdateEvent.ActivityUnpinned -> {
                if (event.fid == fid.rawValue) {
                    state.onActivityUnpinned(event.activityId)
                }
            }

            is StateUpdateEvent.ActivityHidden -> {
                if (event.userId == currentUserId) {
                    state.onActivityHidden(event.activityId, event.hidden)
                }
            }

            is StateUpdateEvent.BookmarkAdded -> {
                state.onBookmarkUpserted(event.bookmark)
            }

            is StateUpdateEvent.BookmarkDeleted -> {
                state.onBookmarkRemoved(event.bookmark)
            }

            is StateUpdateEvent.BookmarkUpdated -> {
                state.onBookmarkUpserted(event.bookmark)
            }

            is StateUpdateEvent.CommentAdded -> {
                if (event.fid == fid.rawValue) {
                    state.onCommentUpserted(event.comment)
                }
            }

            is StateUpdateEvent.CommentDeleted -> {
                if (event.fid == fid.rawValue) {
                    state.onCommentRemoved(event.comment)
                }
            }

            is StateUpdateEvent.CommentUpdated -> {
                if (event.fid == fid.rawValue) {
                    state.onCommentUpserted(event.comment)
                }
            }

            is StateUpdateEvent.CommentReactionDeleted -> {
                if (event.fid == fid.rawValue) {
                    state.onCommentReactionRemoved(event.comment, event.reaction)
                }
            }

            is StateUpdateEvent.CommentReactionUpserted -> {
                if (event.fid == fid.rawValue) {
                    state.onCommentReactionUpserted(
                        event.comment,
                        event.reaction,
                        event.enforceUnique,
                    )
                }
            }

            is StateUpdateEvent.FeedDeleted -> {
                if (event.fid == fid.rawValue) {
                    state.onFeedDeleted()
                }
            }

            is StateUpdateEvent.FeedUpdated -> {
                if (event.feed.fid == fid) {
                    state.onFeedUpdated(event.feed)
                }
            }

            is StateUpdateEvent.FollowAdded -> {
                if (event.follow.matchesFeed()) {
                    state.onFollowAdded(event.follow)
                }
            }

            is StateUpdateEvent.FollowDeleted -> {
                if (event.follow.matchesFeed()) {
                    state.onFollowRemoved(event.follow)
                }
            }

            is StateUpdateEvent.FollowUpdated -> {
                if (event.follow.matchesFeed()) {
                    state.onFollowUpdated(event.follow)
                }
            }

            is StateUpdateEvent.NotificationFeedUpdated -> {
                if (event.fid == fid.rawValue) {
                    state.onNotificationFeedUpdated(
                        aggregatedActivities = event.aggregatedActivities,
                        notificationStatus = event.notificationStatus,
                    )
                }
            }

            is StateUpdateEvent.StoriesFeedUpdated -> {
                if (event.fid == fid.rawValue) {
                    state.onStoriesFeedUpdated(event.activities, event.aggregatedActivities)
                }
            }

            // The fid in poll events doesn't necessarily match all the feeds that contain the poll
            // so we can't early return here based on that.
            is StateUpdateEvent.PollDeleted -> state.onPollDeleted(event.pollId)

            is StateUpdateEvent.PollUpdated -> state.onPollUpdated(event.poll)

            is StateUpdateEvent.PollVoteCasted -> state.onPollVoteUpserted(event.vote, event.pollId)

            is StateUpdateEvent.PollVoteChanged ->
                state.onPollVoteUpserted(event.vote, event.pollId)

            is StateUpdateEvent.PollVoteRemoved -> state.onPollVoteRemoved(event.vote, event.pollId)

            else -> {
                // Handle other events if necessary
            }
        }
    }

    private fun FollowData.matchesFeed() = sourceFeed.fid == fid || targetFeed.fid == fid
}
