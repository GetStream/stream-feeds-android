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
import kotlin.collections.*
import kotlin.io.*

/** [All possibility of string to use] */
/** FeedOwnCapability Enum */
public sealed class FeedOwnCapability(public val value: kotlin.String) {
    override fun toString(): String = value

    public companion object {
        public fun fromString(s: kotlin.String): FeedOwnCapability =
            when (s) {
                "add-activity" -> AddActivity
                "add-activity-reaction" -> AddActivityReaction
                "add-comment" -> AddComment
                "add-comment-reaction" -> AddCommentReaction
                "bookmark-activity" -> BookmarkActivity
                "create-feed" -> CreateFeed
                "delete-bookmark" -> DeleteBookmark
                "delete-comment" -> DeleteComment
                "delete-feed" -> DeleteFeed
                "edit-bookmark" -> EditBookmark
                "follow" -> Follow
                "invite-feed" -> InviteFeed
                "join-feed" -> JoinFeed
                "leave-feed" -> LeaveFeed
                "manage-feed-group" -> ManageFeedGroup
                "mark-activity" -> MarkActivity
                "pin-activity" -> PinActivity
                "query-feed-members" -> QueryFeedMembers
                "query-follows" -> QueryFollows
                "read-activities" -> ReadActivities
                "read-feed" -> ReadFeed
                "remove-activity" -> RemoveActivity
                "remove-activity-reaction" -> RemoveActivityReaction
                "remove-comment-reaction" -> RemoveCommentReaction
                "unfollow" -> Unfollow
                "update-activity" -> UpdateActivity
                "update-comment" -> UpdateComment
                "update-feed" -> UpdateFeed
                "update-feed-followers" -> UpdateFeedFollowers
                "update-feed-members" -> UpdateFeedMembers
                else -> Unknown(s)
            }
    }

    public object AddActivity : FeedOwnCapability("add-activity")

    public object AddActivityReaction : FeedOwnCapability("add-activity-reaction")

    public object AddComment : FeedOwnCapability("add-comment")

    public object AddCommentReaction : FeedOwnCapability("add-comment-reaction")

    public object BookmarkActivity : FeedOwnCapability("bookmark-activity")

    public object CreateFeed : FeedOwnCapability("create-feed")

    public object DeleteBookmark : FeedOwnCapability("delete-bookmark")

    public object DeleteComment : FeedOwnCapability("delete-comment")

    public object DeleteFeed : FeedOwnCapability("delete-feed")

    public object EditBookmark : FeedOwnCapability("edit-bookmark")

    public object Follow : FeedOwnCapability("follow")

    public object InviteFeed : FeedOwnCapability("invite-feed")

    public object JoinFeed : FeedOwnCapability("join-feed")

    public object LeaveFeed : FeedOwnCapability("leave-feed")

    public object ManageFeedGroup : FeedOwnCapability("manage-feed-group")

    public object MarkActivity : FeedOwnCapability("mark-activity")

    public object PinActivity : FeedOwnCapability("pin-activity")

    public object QueryFeedMembers : FeedOwnCapability("query-feed-members")

    public object QueryFollows : FeedOwnCapability("query-follows")

    public object ReadActivities : FeedOwnCapability("read-activities")

    public object ReadFeed : FeedOwnCapability("read-feed")

    public object RemoveActivity : FeedOwnCapability("remove-activity")

    public object RemoveActivityReaction : FeedOwnCapability("remove-activity-reaction")

    public object RemoveCommentReaction : FeedOwnCapability("remove-comment-reaction")

    public object Unfollow : FeedOwnCapability("unfollow")

    public object UpdateActivity : FeedOwnCapability("update-activity")

    public object UpdateComment : FeedOwnCapability("update-comment")

    public object UpdateFeed : FeedOwnCapability("update-feed")

    public object UpdateFeedFollowers : FeedOwnCapability("update-feed-followers")

    public object UpdateFeedMembers : FeedOwnCapability("update-feed-members")

    public data class Unknown(val unknownValue: kotlin.String) : FeedOwnCapability(unknownValue)

    public class FeedOwnCapabilityAdapter : JsonAdapter<FeedOwnCapability>() {
        @FromJson
        override fun fromJson(reader: JsonReader): FeedOwnCapability? {
            val s = reader.nextString() ?: return null
            return FeedOwnCapability.fromString(s)
        }

        @ToJson
        override fun toJson(writer: JsonWriter, value: FeedOwnCapability?) {
            writer.value(value?.value)
        }
    }
}
