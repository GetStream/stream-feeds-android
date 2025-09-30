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
 * @property state The instance that manages updates to the feed state.
 */
internal class FeedEventHandler(
    private val fid: FeedId,
    private val filter: ActivitiesFilter?,
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
                if (event.fid == fid.rawValue && event.activity matches filter) {
                    state.onActivityAdded(event.activity)
                }
            }

            is StateUpdateEvent.ActivityDeleted -> {
                if (event.fid == fid.rawValue) {
                    state.onActivityRemoved(event.activityId)
                }
            }

            is StateUpdateEvent.ActivityUpdated -> {
                if (event.fid == fid.rawValue && event.activity matches filter) {
                    state.onActivityUpdated(event.activity)
                }
            }

            is StateUpdateEvent.ActivityRemovedFromFeed -> {
                if (event.fid == fid.rawValue) {
                    state.onActivityRemoved(event.activityId)
                }
            }

            is StateUpdateEvent.ActivityReactionAdded -> {
                if (event.fid == fid.rawValue) {
                    state.onReactionUpserted(event.reaction, event.activity)
                }
            }

            is StateUpdateEvent.ActivityReactionDeleted -> {
                if (event.fid == fid.rawValue) {
                    state.onReactionRemoved(event.reaction, event.activity)
                }
            }

            is StateUpdateEvent.ActivityReactionUpdated -> {
                if (event.fid == fid.rawValue) {
                    state.onReactionUpserted(event.reaction, event.activity)
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

            is StateUpdateEvent.BookmarkAdded -> {
                if (event.bookmark.activity.feeds.contains(fid.rawValue)) {
                    state.onBookmarkUpserted(event.bookmark)
                }
            }

            is StateUpdateEvent.BookmarkDeleted -> {
                if (event.bookmark.activity.feeds.contains(fid.rawValue)) {
                    state.onBookmarkRemoved(event.bookmark)
                }
            }

            is StateUpdateEvent.BookmarkUpdated -> {
                if (event.bookmark.activity.feeds.contains(fid.rawValue)) {
                    state.onBookmarkUpserted(event.bookmark)
                }
            }

            is StateUpdateEvent.CommentAdded -> {
                if (event.fid == fid.rawValue) {
                    state.onCommentAdded(event.comment)
                }
            }

            is StateUpdateEvent.CommentDeleted -> {
                if (event.fid == fid.rawValue) {
                    state.onCommentRemoved(event.comment)
                }
            }

            is StateUpdateEvent.CommentReactionAdded -> {
                if (event.fid == fid.rawValue) {
                    state.onCommentReactionUpserted(event.comment, event.reaction)
                }
            }

            is StateUpdateEvent.CommentReactionDeleted -> {
                if (event.fid == fid.rawValue) {
                    state.onCommentReactionRemoved(event.comment, event.reaction)
                }
            }

            is StateUpdateEvent.CommentReactionUpdated -> {
                if (event.fid == fid.rawValue) {
                    state.onCommentReactionUpserted(event.comment, event.reaction)
                }
            }

            is StateUpdateEvent.FeedAdded -> {
                if (event.feed.fid == fid) {
                    state.onFeedUpdated(event.feed)
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
                    state.onStoriesFeedUpdated(event.aggregatedActivities)
                }
            }

            is StateUpdateEvent.PollDeleted -> {
                if (event.fid == fid.rawValue) {
                    state.onPollDeleted(event.pollId)
                }
            }

            is StateUpdateEvent.PollUpdated -> {
                if (event.fid == fid.rawValue) {
                    state.onPollUpdated(event.poll)
                }
            }

            is StateUpdateEvent.PollVoteCasted -> {
                if (event.fid == fid.rawValue) {
                    state.onPollVoteUpserted(event.vote, event.pollId)
                }
            }

            is StateUpdateEvent.PollVoteChanged -> {
                if (event.fid == fid.rawValue) {
                    state.onPollVoteUpserted(event.vote, event.pollId)
                }
            }

            is StateUpdateEvent.PollVoteRemoved -> {
                if (event.fid == fid.rawValue) {
                    state.onPollVoteRemoved(event.vote, event.pollId)
                }
            }

            else -> {
                // Handle other events if necessary
            }
        }
    }

    private fun FollowData.matchesFeed() = sourceFeed.fid == fid || targetFeed.fid == fid
}
