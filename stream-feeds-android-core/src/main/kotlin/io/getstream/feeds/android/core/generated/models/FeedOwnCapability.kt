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

package io.getstream.feeds.android.core.generated.models

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson
import kotlin.collections.*
import kotlin.io.*

/** [All possibility of string to use] */
/** FeedOwnCapability Enum */
sealed class FeedOwnCapability(val value: kotlin.String) {
    override fun toString(): String = value

    companion object {
        fun fromString(s: kotlin.String): FeedOwnCapability =
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

    object AddActivity : FeedOwnCapability("add-activity")

    object AddActivityReaction : FeedOwnCapability("add-activity-reaction")

    object AddComment : FeedOwnCapability("add-comment")

    object AddCommentReaction : FeedOwnCapability("add-comment-reaction")

    object BookmarkActivity : FeedOwnCapability("bookmark-activity")

    object CreateFeed : FeedOwnCapability("create-feed")

    object DeleteBookmark : FeedOwnCapability("delete-bookmark")

    object DeleteComment : FeedOwnCapability("delete-comment")

    object DeleteFeed : FeedOwnCapability("delete-feed")

    object EditBookmark : FeedOwnCapability("edit-bookmark")

    object Follow : FeedOwnCapability("follow")

    object InviteFeed : FeedOwnCapability("invite-feed")

    object JoinFeed : FeedOwnCapability("join-feed")

    object LeaveFeed : FeedOwnCapability("leave-feed")

    object ManageFeedGroup : FeedOwnCapability("manage-feed-group")

    object MarkActivity : FeedOwnCapability("mark-activity")

    object PinActivity : FeedOwnCapability("pin-activity")

    object QueryFeedMembers : FeedOwnCapability("query-feed-members")

    object QueryFollows : FeedOwnCapability("query-follows")

    object ReadActivities : FeedOwnCapability("read-activities")

    object ReadFeed : FeedOwnCapability("read-feed")

    object RemoveActivity : FeedOwnCapability("remove-activity")

    object RemoveActivityReaction : FeedOwnCapability("remove-activity-reaction")

    object RemoveCommentReaction : FeedOwnCapability("remove-comment-reaction")

    object Unfollow : FeedOwnCapability("unfollow")

    object UpdateActivity : FeedOwnCapability("update-activity")

    object UpdateComment : FeedOwnCapability("update-comment")

    object UpdateFeed : FeedOwnCapability("update-feed")

    object UpdateFeedFollowers : FeedOwnCapability("update-feed-followers")

    object UpdateFeedMembers : FeedOwnCapability("update-feed-members")

    data class Unknown(val unknownValue: kotlin.String) : FeedOwnCapability(unknownValue)

    class FeedOwnCapabilityAdapter : JsonAdapter<FeedOwnCapability>() {
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
