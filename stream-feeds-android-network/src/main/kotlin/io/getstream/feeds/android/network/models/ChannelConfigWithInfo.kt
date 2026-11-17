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

import com.squareup.moshi.Json

/**  */
@com.squareup.moshi.JsonClass(generateAdapter = true)
public data class ChannelConfigWithInfo(
    @Json(name = "automod")
    public val automod: io.getstream.feeds.android.network.models.AutomodMode,
    @Json(name = "automod_behavior")
    public val automodBehavior: io.getstream.feeds.android.network.models.ChannelModerationBehavior,
    @Json(name = "connect_events") public val connectEvents: Boolean,
    @Json(name = "count_messages") public val countMessages: Boolean,
    @Json(name = "created_at") public val createdAt: java.util.Date,
    @Json(name = "custom_events") public val customEvents: Boolean,
    @Json(name = "delivery_events") public val deliveryEvents: Boolean,
    @Json(name = "mark_messages_pending") public val markMessagesPending: Boolean,
    @Json(name = "max_message_length") public val maxMessageLength: Int,
    @Json(name = "message_retention") public val messageRetention: String,
    @Json(name = "mutes") public val mutes: Boolean,
    @Json(name = "name") public val name: String,
    @Json(name = "polls") public val polls: Boolean,
    @Json(name = "push_notifications") public val pushNotifications: Boolean,
    @Json(name = "quotes") public val quotes: Boolean,
    @Json(name = "reactions") public val reactions: Boolean,
    @Json(name = "read_events") public val readEvents: Boolean,
    @Json(name = "reminders") public val reminders: Boolean,
    @Json(name = "replies") public val replies: Boolean,
    @Json(name = "search") public val search: Boolean,
    @Json(name = "shared_locations") public val sharedLocations: Boolean,
    @Json(name = "skip_last_msg_update_for_system_msgs")
    public val skipLastMsgUpdateForSystemMsgs: Boolean,
    @Json(name = "typing_events") public val typingEvents: Boolean,
    @Json(name = "updated_at") public val updatedAt: java.util.Date,
    @Json(name = "uploads") public val uploads: Boolean,
    @Json(name = "url_enrichment") public val urlEnrichment: Boolean,
    @Json(name = "user_message_reminders") public val userMessageReminders: Boolean,
    @Json(name = "commands")
    public val commands: List<io.getstream.feeds.android.network.models.Command> = emptyList(),
    @Json(name = "blocklist") public val blocklist: String? = null,
    @Json(name = "blocklist_behavior")
    public val blocklistBehavior:
        io.getstream.feeds.android.network.models.ChannelModerationBehavior? =
        null,
    @Json(name = "partition_size") public val partitionSize: Int? = null,
    @Json(name = "partition_ttl") public val partitionTtl: String? = null,
    @Json(name = "push_level")
    public val pushLevel: io.getstream.feeds.android.network.models.PushLevel? = null,
    @Json(name = "scheduled_messages_max_lead_time_seconds")
    public val scheduledMessagesMaxLeadTimeSeconds: Int? = null,
    @Json(name = "scheduled_messages_max_per_channel")
    public val scheduledMessagesMaxPerChannel: Int? = null,
    @Json(name = "scheduled_messages_max_per_user")
    public val scheduledMessagesMaxPerUser: Int? = null,
    @Json(name = "scheduled_messages_min_lead_time_seconds")
    public val scheduledMessagesMinLeadTimeSeconds: Int? = null,
    @Json(name = "allowed_flag_reasons") public val allowedFlagReasons: List<String>? = null,
    @Json(name = "blocklists")
    public val blocklists: List<io.getstream.feeds.android.network.models.BlockListOptions>? = null,
    @Json(name = "automod_thresholds")
    public val automodThresholds: io.getstream.feeds.android.network.models.Thresholds? = null,
    @Json(name = "chat_preferences")
    public val chatPreferences: io.getstream.feeds.android.network.models.ChatPreferences? = null,
    @Json(name = "grants") public val grants: Map<String, List<String>>? = null,
)
