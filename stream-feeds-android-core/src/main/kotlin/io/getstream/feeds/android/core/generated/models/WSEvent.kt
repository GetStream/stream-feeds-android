/*
 * Copyright (c) 2014-2024 Stream.io Inc. All rights reserved.
 *
 * Licensed under the Stream License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://github.com/GetStream/stream-video-android/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress(
    "ArrayInDataClass",
    "EnumEntryName",
    "RemoveRedundantQualifierName",
    "UnusedImport"
)

package io.getstream.feeds.android.core.generated.models

import com.squareup.moshi.FromJson
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson

public interface WSEvent {
    fun getWSEventType(): kotlin.String 
    
}


class WSEventAdapter : JsonAdapter<WSEvent>() {

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
                io.getstream.feeds.android.core.generated.infrastructure.Serializer.moshi.adapter(getSubclass(eventType)).fromJson(peekedReader)
            }
        }
    }

    @ToJson
    override fun toJson(writer: JsonWriter, value: WSEvent?) {
        throw UnsupportedOperationException("toJson not implemented")
    }

    private fun getSubclass(type: String): Class<out WSEvent> {
        return when (type) {
            "app.updated" -> io.getstream.feeds.android.core.generated.models.AppUpdatedEvent::class.java
            "feeds.activity.added" -> io.getstream.feeds.android.core.generated.models.ActivityAddedEvent::class.java
            "feeds.activity.deleted" -> io.getstream.feeds.android.core.generated.models.ActivityDeletedEvent::class.java
            "feeds.activity.marked" -> io.getstream.feeds.android.core.generated.models.ActivityMarkEvent::class.java
            "feeds.activity.pinned" -> io.getstream.feeds.android.core.generated.models.ActivityPinnedEvent::class.java
            "feeds.activity.reaction.added" -> io.getstream.feeds.android.core.generated.models.ActivityReactionAddedEvent::class.java
            "feeds.activity.reaction.deleted" -> io.getstream.feeds.android.core.generated.models.ActivityReactionDeletedEvent::class.java
            "feeds.activity.reaction.updated" -> io.getstream.feeds.android.core.generated.models.ActivityReactionUpdatedEvent::class.java
            "feeds.activity.removed_from_feed" -> io.getstream.feeds.android.core.generated.models.ActivityRemovedFromFeedEvent::class.java
            "feeds.activity.unpinned" -> io.getstream.feeds.android.core.generated.models.ActivityUnpinnedEvent::class.java
            "feeds.activity.updated" -> io.getstream.feeds.android.core.generated.models.ActivityUpdatedEvent::class.java
            "feeds.bookmark.added" -> io.getstream.feeds.android.core.generated.models.BookmarkAddedEvent::class.java
            "feeds.bookmark.deleted" -> io.getstream.feeds.android.core.generated.models.BookmarkDeletedEvent::class.java
            "feeds.bookmark.updated" -> io.getstream.feeds.android.core.generated.models.BookmarkUpdatedEvent::class.java
            "feeds.bookmark_folder.deleted" -> io.getstream.feeds.android.core.generated.models.BookmarkFolderDeletedEvent::class.java
            "feeds.bookmark_folder.updated" -> io.getstream.feeds.android.core.generated.models.BookmarkFolderUpdatedEvent::class.java
            "feeds.comment.added" -> io.getstream.feeds.android.core.generated.models.CommentAddedEvent::class.java
            "feeds.comment.deleted" -> io.getstream.feeds.android.core.generated.models.CommentDeletedEvent::class.java
            "feeds.comment.reaction.added" -> io.getstream.feeds.android.core.generated.models.CommentReactionAddedEvent::class.java
            "feeds.comment.reaction.deleted" -> io.getstream.feeds.android.core.generated.models.CommentReactionDeletedEvent::class.java
            "feeds.comment.reaction.updated" -> io.getstream.feeds.android.core.generated.models.CommentReactionUpdatedEvent::class.java
            "feeds.comment.updated" -> io.getstream.feeds.android.core.generated.models.CommentUpdatedEvent::class.java
            "feeds.feed.created" -> io.getstream.feeds.android.core.generated.models.FeedCreatedEvent::class.java
            "feeds.feed.deleted" -> io.getstream.feeds.android.core.generated.models.FeedDeletedEvent::class.java
            "feeds.feed.updated" -> io.getstream.feeds.android.core.generated.models.FeedUpdatedEvent::class.java
            "feeds.feed_group.changed" -> io.getstream.feeds.android.core.generated.models.FeedGroupChangedEvent::class.java
            "feeds.feed_group.deleted" -> io.getstream.feeds.android.core.generated.models.FeedGroupDeletedEvent::class.java
            "feeds.feed_member.added" -> io.getstream.feeds.android.core.generated.models.FeedMemberAddedEvent::class.java
            "feeds.feed_member.removed" -> io.getstream.feeds.android.core.generated.models.FeedMemberRemovedEvent::class.java
            "feeds.feed_member.updated" -> io.getstream.feeds.android.core.generated.models.FeedMemberUpdatedEvent::class.java
            "feeds.follow.created" -> io.getstream.feeds.android.core.generated.models.FollowCreatedEvent::class.java
            "feeds.follow.deleted" -> io.getstream.feeds.android.core.generated.models.FollowDeletedEvent::class.java
            "feeds.follow.updated" -> io.getstream.feeds.android.core.generated.models.FollowUpdatedEvent::class.java
            "feeds.notification_feed.updated" -> io.getstream.feeds.android.core.generated.models.NotificationFeedUpdatedEvent::class.java
            "feeds.poll.closed" -> io.getstream.feeds.android.core.generated.models.PollClosedFeedEvent::class.java
            "feeds.poll.deleted" -> io.getstream.feeds.android.core.generated.models.PollDeletedFeedEvent::class.java
            "feeds.poll.updated" -> io.getstream.feeds.android.core.generated.models.PollUpdatedFeedEvent::class.java
            "feeds.poll.vote_casted" -> io.getstream.feeds.android.core.generated.models.PollVoteCastedFeedEvent::class.java
            "feeds.poll.vote_changed" -> io.getstream.feeds.android.core.generated.models.PollVoteChangedFeedEvent::class.java
            "feeds.poll.vote_removed" -> io.getstream.feeds.android.core.generated.models.PollVoteRemovedFeedEvent::class.java
            "health.check" -> io.getstream.feeds.android.core.generated.models.HealthCheckEvent::class.java
            "moderation.custom_action" -> io.getstream.feeds.android.core.generated.models.ModerationCustomActionEvent::class.java
            "moderation.flagged" -> io.getstream.feeds.android.core.generated.models.ModerationFlaggedEvent::class.java
            "moderation.mark_reviewed" -> io.getstream.feeds.android.core.generated.models.ModerationMarkReviewedEvent::class.java
            "user.banned" -> io.getstream.feeds.android.core.generated.models.UserBannedEvent::class.java
            "user.deactivated" -> io.getstream.feeds.android.core.generated.models.UserDeactivatedEvent::class.java
            "user.muted" -> io.getstream.feeds.android.core.generated.models.UserMutedEvent::class.java
            "user.reactivated" -> io.getstream.feeds.android.core.generated.models.UserReactivatedEvent::class.java
            "user.updated" -> io.getstream.feeds.android.core.generated.models.UserUpdatedEvent::class.java
            else -> UnsupportedWSEvent::class.java       
        }
    }
}

class UnsupportedWSEvent(val type: String) : WSEvent {
    override fun getWSEventType(): kotlin.String {
        return type
    }        
    
}

class UnsupportedWSEventException(val type: String) : Exception()
