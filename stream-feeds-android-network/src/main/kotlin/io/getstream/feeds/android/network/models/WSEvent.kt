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
@file:Suppress("ArrayInDataClass", "EnumEntryName", "RemoveRedundantQualifierName", "UnusedImport")

package io.getstream.feeds.android.network.models

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson

public interface WSEvent {
    public fun getWSEventType(): kotlin.String
}

public class WSEventAdapter : JsonAdapter<WSEvent>() {

    @FromJson
    override fun fromJson(reader: JsonReader): WSEvent? {
        val peek = reader.peekJson()
        var eventType: String? = null
        reader.beginObject()
        while (reader.hasNext()) {
            if (reader.nextName() == "type") {
                eventType = reader.nextString()
            } else {
                reader.skipValue()
            }
        }
        reader.endObject()

        return eventType?.let {
            peek.use { peekedReader ->
                io.getstream.feeds.android.network.infrastructure.Serializer.moshi
                    .adapter(getSubclass(eventType))
                    .fromJson(peekedReader)
            }
        }
    }

    @ToJson
    override fun toJson(writer: JsonWriter, value: WSEvent?) {
        throw UnsupportedOperationException("toJson not implemented")
    }

    private fun getSubclass(type: String): Class<out WSEvent> {
        return when (type) {
            "app.updated" -> io.getstream.feeds.android.network.models.AppUpdatedEvent::class.java
            "feeds.activity.added" ->
                io.getstream.feeds.android.network.models.ActivityAddedEvent::class.java
            "feeds.activity.deleted" ->
                io.getstream.feeds.android.network.models.ActivityDeletedEvent::class.java
            "feeds.activity.feedback" ->
                io.getstream.feeds.android.network.models.ActivityFeedbackEvent::class.java
            "feeds.activity.marked" ->
                io.getstream.feeds.android.network.models.ActivityMarkEvent::class.java
            "feeds.activity.pinned" ->
                io.getstream.feeds.android.network.models.ActivityPinnedEvent::class.java
            "feeds.activity.reaction.added" ->
                io.getstream.feeds.android.network.models.ActivityReactionAddedEvent::class.java
            "feeds.activity.reaction.deleted" ->
                io.getstream.feeds.android.network.models.ActivityReactionDeletedEvent::class.java
            "feeds.activity.reaction.updated" ->
                io.getstream.feeds.android.network.models.ActivityReactionUpdatedEvent::class.java
            "feeds.activity.removed_from_feed" ->
                io.getstream.feeds.android.network.models.ActivityRemovedFromFeedEvent::class.java
            "feeds.activity.unpinned" ->
                io.getstream.feeds.android.network.models.ActivityUnpinnedEvent::class.java
            "feeds.activity.updated" ->
                io.getstream.feeds.android.network.models.ActivityUpdatedEvent::class.java
            "feeds.bookmark.added" ->
                io.getstream.feeds.android.network.models.BookmarkAddedEvent::class.java
            "feeds.bookmark.deleted" ->
                io.getstream.feeds.android.network.models.BookmarkDeletedEvent::class.java
            "feeds.bookmark.updated" ->
                io.getstream.feeds.android.network.models.BookmarkUpdatedEvent::class.java
            "feeds.bookmark_folder.deleted" ->
                io.getstream.feeds.android.network.models.BookmarkFolderDeletedEvent::class.java
            "feeds.bookmark_folder.updated" ->
                io.getstream.feeds.android.network.models.BookmarkFolderUpdatedEvent::class.java
            "feeds.comment.added" ->
                io.getstream.feeds.android.network.models.CommentAddedEvent::class.java
            "feeds.comment.deleted" ->
                io.getstream.feeds.android.network.models.CommentDeletedEvent::class.java
            "feeds.comment.reaction.added" ->
                io.getstream.feeds.android.network.models.CommentReactionAddedEvent::class.java
            "feeds.comment.reaction.deleted" ->
                io.getstream.feeds.android.network.models.CommentReactionDeletedEvent::class.java
            "feeds.comment.reaction.updated" ->
                io.getstream.feeds.android.network.models.CommentReactionUpdatedEvent::class.java
            "feeds.comment.updated" ->
                io.getstream.feeds.android.network.models.CommentUpdatedEvent::class.java
            "feeds.feed.created" ->
                io.getstream.feeds.android.network.models.FeedCreatedEvent::class.java
            "feeds.feed.deleted" ->
                io.getstream.feeds.android.network.models.FeedDeletedEvent::class.java
            "feeds.feed.updated" ->
                io.getstream.feeds.android.network.models.FeedUpdatedEvent::class.java
            "feeds.feed_group.changed" ->
                io.getstream.feeds.android.network.models.FeedGroupChangedEvent::class.java
            "feeds.feed_group.deleted" ->
                io.getstream.feeds.android.network.models.FeedGroupDeletedEvent::class.java
            "feeds.feed_member.added" ->
                io.getstream.feeds.android.network.models.FeedMemberAddedEvent::class.java
            "feeds.feed_member.removed" ->
                io.getstream.feeds.android.network.models.FeedMemberRemovedEvent::class.java
            "feeds.feed_member.updated" ->
                io.getstream.feeds.android.network.models.FeedMemberUpdatedEvent::class.java
            "feeds.follow.created" ->
                io.getstream.feeds.android.network.models.FollowCreatedEvent::class.java
            "feeds.follow.deleted" ->
                io.getstream.feeds.android.network.models.FollowDeletedEvent::class.java
            "feeds.follow.updated" ->
                io.getstream.feeds.android.network.models.FollowUpdatedEvent::class.java
            "feeds.notification_feed.updated" ->
                io.getstream.feeds.android.network.models.NotificationFeedUpdatedEvent::class.java
            "feeds.poll.closed" ->
                io.getstream.feeds.android.network.models.PollClosedFeedEvent::class.java
            "feeds.poll.deleted" ->
                io.getstream.feeds.android.network.models.PollDeletedFeedEvent::class.java
            "feeds.poll.updated" ->
                io.getstream.feeds.android.network.models.PollUpdatedFeedEvent::class.java
            "feeds.poll.vote_casted" ->
                io.getstream.feeds.android.network.models.PollVoteCastedFeedEvent::class.java
            "feeds.poll.vote_changed" ->
                io.getstream.feeds.android.network.models.PollVoteChangedFeedEvent::class.java
            "feeds.poll.vote_removed" ->
                io.getstream.feeds.android.network.models.PollVoteRemovedFeedEvent::class.java
            "feeds.stories_feed.updated" ->
                io.getstream.feeds.android.network.models.StoriesFeedUpdatedEvent::class.java
            "health.check" -> io.getstream.feeds.android.network.models.HealthCheckEvent::class.java
            "moderation.custom_action" ->
                io.getstream.feeds.android.network.models.ModerationCustomActionEvent::class.java
            "moderation.flagged" ->
                io.getstream.feeds.android.network.models.ModerationFlaggedEvent::class.java
            "moderation.mark_reviewed" ->
                io.getstream.feeds.android.network.models.ModerationMarkReviewedEvent::class.java
            "user.banned" -> io.getstream.feeds.android.network.models.UserBannedEvent::class.java
            "user.deactivated" ->
                io.getstream.feeds.android.network.models.UserDeactivatedEvent::class.java
            "user.muted" -> io.getstream.feeds.android.network.models.UserMutedEvent::class.java
            "user.reactivated" ->
                io.getstream.feeds.android.network.models.UserReactivatedEvent::class.java
            "user.updated" -> io.getstream.feeds.android.network.models.UserUpdatedEvent::class.java
            else -> UnsupportedWSEvent::class.java
        }
    }
}

public class UnsupportedWSEvent(public val type: String) : WSEvent {
    override fun getWSEventType(): kotlin.String {
        return type
    }
}

public class UnsupportedWSEventException(public val type: String) : Exception()
