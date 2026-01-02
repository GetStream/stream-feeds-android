/*
 * Copyright (c) 2014-2026 Stream.io Inc. All rights reserved.
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
                "add-activity-bookmark" -> AddActivityBookmark
                "add-activity-reaction" -> AddActivityReaction
                "add-comment" -> AddComment
                "add-comment-reaction" -> AddCommentReaction
                "create-feed" -> CreateFeed
                "delete-any-activity" -> DeleteAnyActivity
                "delete-any-comment" -> DeleteAnyComment
                "delete-feed" -> DeleteFeed
                "delete-own-activity" -> DeleteOwnActivity
                "delete-own-activity-bookmark" -> DeleteOwnActivityBookmark
                "delete-own-activity-reaction" -> DeleteOwnActivityReaction
                "delete-own-comment" -> DeleteOwnComment
                "delete-own-comment-reaction" -> DeleteOwnCommentReaction
                "follow" -> Follow
                "pin-activity" -> PinActivity
                "query-feed-members" -> QueryFeedMembers
                "query-follows" -> QueryFollows
                "read-activities" -> ReadActivities
                "read-feed" -> ReadFeed
                "unfollow" -> Unfollow
                "update-any-activity" -> UpdateAnyActivity
                "update-any-comment" -> UpdateAnyComment
                "update-feed" -> UpdateFeed
                "update-feed-followers" -> UpdateFeedFollowers
                "update-feed-members" -> UpdateFeedMembers
                "update-own-activity" -> UpdateOwnActivity
                "update-own-activity-bookmark" -> UpdateOwnActivityBookmark
                "update-own-comment" -> UpdateOwnComment
                else -> Unknown(s)
            }
    }

    public object AddActivity : FeedOwnCapability("add-activity")

    public object AddActivityBookmark : FeedOwnCapability("add-activity-bookmark")

    public object AddActivityReaction : FeedOwnCapability("add-activity-reaction")

    public object AddComment : FeedOwnCapability("add-comment")

    public object AddCommentReaction : FeedOwnCapability("add-comment-reaction")

    public object CreateFeed : FeedOwnCapability("create-feed")

    public object DeleteAnyActivity : FeedOwnCapability("delete-any-activity")

    public object DeleteAnyComment : FeedOwnCapability("delete-any-comment")

    public object DeleteFeed : FeedOwnCapability("delete-feed")

    public object DeleteOwnActivity : FeedOwnCapability("delete-own-activity")

    public object DeleteOwnActivityBookmark : FeedOwnCapability("delete-own-activity-bookmark")

    public object DeleteOwnActivityReaction : FeedOwnCapability("delete-own-activity-reaction")

    public object DeleteOwnComment : FeedOwnCapability("delete-own-comment")

    public object DeleteOwnCommentReaction : FeedOwnCapability("delete-own-comment-reaction")

    public object Follow : FeedOwnCapability("follow")

    public object PinActivity : FeedOwnCapability("pin-activity")

    public object QueryFeedMembers : FeedOwnCapability("query-feed-members")

    public object QueryFollows : FeedOwnCapability("query-follows")

    public object ReadActivities : FeedOwnCapability("read-activities")

    public object ReadFeed : FeedOwnCapability("read-feed")

    public object Unfollow : FeedOwnCapability("unfollow")

    public object UpdateAnyActivity : FeedOwnCapability("update-any-activity")

    public object UpdateAnyComment : FeedOwnCapability("update-any-comment")

    public object UpdateFeed : FeedOwnCapability("update-feed")

    public object UpdateFeedFollowers : FeedOwnCapability("update-feed-followers")

    public object UpdateFeedMembers : FeedOwnCapability("update-feed-members")

    public object UpdateOwnActivity : FeedOwnCapability("update-own-activity")

    public object UpdateOwnActivityBookmark : FeedOwnCapability("update-own-activity-bookmark")

    public object UpdateOwnComment : FeedOwnCapability("update-own-comment")

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
