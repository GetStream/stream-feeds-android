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
/** ChannelOwnCapability Enum */
public sealed class ChannelOwnCapability(public val value: kotlin.String) {
    override fun toString(): String = value

    public companion object {
        public fun fromString(s: kotlin.String): ChannelOwnCapability =
            when (s) {
                "ban-channel-members" -> BanChannelMembers
                "cast-poll-vote" -> CastPollVote
                "connect-events" -> ConnectEvents
                "create-attachment" -> CreateAttachment
                "delete-any-message" -> DeleteAnyMessage
                "delete-channel" -> DeleteChannel
                "delete-own-message" -> DeleteOwnMessage
                "flag-message" -> FlagMessage
                "freeze-channel" -> FreezeChannel
                "join-channel" -> JoinChannel
                "leave-channel" -> LeaveChannel
                "mute-channel" -> MuteChannel
                "pin-message" -> PinMessage
                "query-poll-votes" -> QueryPollVotes
                "quote-message" -> QuoteMessage
                "read-events" -> ReadEvents
                "search-messages" -> SearchMessages
                "send-custom-events" -> SendCustomEvents
                "send-links" -> SendLinks
                "send-message" -> SendMessage
                "send-poll" -> SendPoll
                "send-reaction" -> SendReaction
                "send-reply" -> SendReply
                "send-restricted-visibility-message" -> SendRestrictedVisibilityMessage
                "send-typing-events" -> SendTypingEvents
                "set-channel-cooldown" -> SetChannelCooldown
                "share-location" -> ShareLocation
                "skip-slow-mode" -> SkipSlowMode
                "slow-mode" -> SlowMode
                "typing-events" -> TypingEvents
                "update-any-message" -> UpdateAnyMessage
                "update-channel" -> UpdateChannel
                "update-channel-members" -> UpdateChannelMembers
                "update-own-message" -> UpdateOwnMessage
                "update-thread" -> UpdateThread
                "upload-file" -> UploadFile
                else -> Unknown(s)
            }
    }

    public object BanChannelMembers : ChannelOwnCapability("ban-channel-members")

    public object CastPollVote : ChannelOwnCapability("cast-poll-vote")

    public object ConnectEvents : ChannelOwnCapability("connect-events")

    public object CreateAttachment : ChannelOwnCapability("create-attachment")

    public object DeleteAnyMessage : ChannelOwnCapability("delete-any-message")

    public object DeleteChannel : ChannelOwnCapability("delete-channel")

    public object DeleteOwnMessage : ChannelOwnCapability("delete-own-message")

    public object FlagMessage : ChannelOwnCapability("flag-message")

    public object FreezeChannel : ChannelOwnCapability("freeze-channel")

    public object JoinChannel : ChannelOwnCapability("join-channel")

    public object LeaveChannel : ChannelOwnCapability("leave-channel")

    public object MuteChannel : ChannelOwnCapability("mute-channel")

    public object PinMessage : ChannelOwnCapability("pin-message")

    public object QueryPollVotes : ChannelOwnCapability("query-poll-votes")

    public object QuoteMessage : ChannelOwnCapability("quote-message")

    public object ReadEvents : ChannelOwnCapability("read-events")

    public object SearchMessages : ChannelOwnCapability("search-messages")

    public object SendCustomEvents : ChannelOwnCapability("send-custom-events")

    public object SendLinks : ChannelOwnCapability("send-links")

    public object SendMessage : ChannelOwnCapability("send-message")

    public object SendPoll : ChannelOwnCapability("send-poll")

    public object SendReaction : ChannelOwnCapability("send-reaction")

    public object SendReply : ChannelOwnCapability("send-reply")

    public object SendRestrictedVisibilityMessage :
        ChannelOwnCapability("send-restricted-visibility-message")

    public object SendTypingEvents : ChannelOwnCapability("send-typing-events")

    public object SetChannelCooldown : ChannelOwnCapability("set-channel-cooldown")

    public object ShareLocation : ChannelOwnCapability("share-location")

    public object SkipSlowMode : ChannelOwnCapability("skip-slow-mode")

    public object SlowMode : ChannelOwnCapability("slow-mode")

    public object TypingEvents : ChannelOwnCapability("typing-events")

    public object UpdateAnyMessage : ChannelOwnCapability("update-any-message")

    public object UpdateChannel : ChannelOwnCapability("update-channel")

    public object UpdateChannelMembers : ChannelOwnCapability("update-channel-members")

    public object UpdateOwnMessage : ChannelOwnCapability("update-own-message")

    public object UpdateThread : ChannelOwnCapability("update-thread")

    public object UploadFile : ChannelOwnCapability("upload-file")

    public data class Unknown(val unknownValue: kotlin.String) : ChannelOwnCapability(unknownValue)

    public class ChannelOwnCapabilityAdapter : JsonAdapter<ChannelOwnCapability>() {
        @FromJson
        override fun fromJson(reader: JsonReader): ChannelOwnCapability? {
            val s = reader.nextString() ?: return null
            return ChannelOwnCapability.fromString(s)
        }

        @ToJson
        override fun toJson(writer: JsonWriter, value: ChannelOwnCapability?) {
            writer.value(value?.value)
        }
    }
}
