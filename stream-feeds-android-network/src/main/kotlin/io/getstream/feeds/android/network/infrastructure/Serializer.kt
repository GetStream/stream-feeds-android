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

package io.getstream.feeds.android.network.infrastructure

import com.squareup.moshi.Moshi

public object Serializer {
    @JvmStatic
    public val moshiBuilder: Moshi.Builder =
        Moshi.Builder()
            .add(
                io.getstream.feeds.android.network.models.ActivityFeedbackEventPayload.Action
                    .ActionAdapter()
            )
            .add(
                io.getstream.feeds.android.network.models.ActivitySelectorType
                    .ActivitySelectorTypeAdapter()
            )
            .add(
                io.getstream.feeds.android.network.models.ActivityVisibility
                    .ActivityVisibilityAdapter()
            )
            .add(io.getstream.feeds.android.network.models.AutomodMode.AutomodModeAdapter())
            .add(io.getstream.feeds.android.network.models.BlockListRule.Action.ActionAdapter())
            .add(io.getstream.feeds.android.network.models.BodyguardRule.Action.ActionAdapter())
            .add(
                io.getstream.feeds.android.network.models.BodyguardSeverityRule.Action
                    .ActionAdapter()
            )
            .add(
                io.getstream.feeds.android.network.models.BodyguardSeverityRule.Severity
                    .SeverityAdapter()
            )
            .add(
                io.getstream.feeds.android.network.models.BulkActionAppealsRequest.ActionType
                    .ActionTypeAdapter()
            )
            .add(
                io.getstream.feeds.android.network.models.ChangeFeedVisibilityRequest
                    .PendingFollowsAction
                    .PendingFollowsActionAdapter()
            )
            .add(
                io.getstream.feeds.android.network.models.ChannelModerationBehavior
                    .ChannelModerationBehaviorAdapter()
            )
            .add(
                io.getstream.feeds.android.network.models.ChannelOwnCapability
                    .ChannelOwnCapabilityAdapter()
            )
            .add(
                io.getstream.feeds.android.network.models.ChatPreferencesInput.ChannelMentions
                    .ChannelMentionsAdapter()
            )
            .add(
                io.getstream.feeds.android.network.models.ChatPreferencesInput.DefaultPreference
                    .DefaultPreferenceAdapter()
            )
            .add(
                io.getstream.feeds.android.network.models.ChatPreferencesInput.DirectMentions
                    .DirectMentionsAdapter()
            )
            .add(
                io.getstream.feeds.android.network.models.ChatPreferencesInput.GroupMentions
                    .GroupMentionsAdapter()
            )
            .add(
                io.getstream.feeds.android.network.models.ChatPreferencesInput.HereMentions
                    .HereMentionsAdapter()
            )
            .add(
                io.getstream.feeds.android.network.models.ChatPreferencesInput.RoleMentions
                    .RoleMentionsAdapter()
            )
            .add(
                io.getstream.feeds.android.network.models.ChatPreferencesInput.ThreadReplies
                    .ThreadRepliesAdapter()
            )
            .add(
                io.getstream.feeds.android.network.models.CollectionStatus.CollectionStatusAdapter()
            )
            .add(io.getstream.feeds.android.network.models.CommentStatus.CommentStatusAdapter())
            .add(
                io.getstream.feeds.android.network.models.CreateBlockListRequest.Type.TypeAdapter()
            )
            .add(io.getstream.feeds.android.network.models.CreateQueueRequest.Type.TypeAdapter())
            .add(io.getstream.feeds.android.network.models.DeleteMessages.DeleteMessagesAdapter())
            .add(
                io.getstream.feeds.android.network.models.FeedMemberStatus.FeedMemberStatusAdapter()
            )
            .add(
                io.getstream.feeds.android.network.models.FeedOwnCapability
                    .FeedOwnCapabilityAdapter()
            )
            .add(io.getstream.feeds.android.network.models.FeedVisibility.FeedVisibilityAdapter())
            .add(
                io.getstream.feeds.android.network.models.FeedsPreferences.Comment.CommentAdapter()
            )
            .add(
                io.getstream.feeds.android.network.models.FeedsPreferences.CommentMention
                    .CommentMentionAdapter()
            )
            .add(
                io.getstream.feeds.android.network.models.FeedsPreferences.CommentReaction
                    .CommentReactionAdapter()
            )
            .add(
                io.getstream.feeds.android.network.models.FeedsPreferences.CommentReply
                    .CommentReplyAdapter()
            )
            .add(io.getstream.feeds.android.network.models.FeedsPreferences.Follow.FollowAdapter())
            .add(
                io.getstream.feeds.android.network.models.FeedsPreferences.Mention.MentionAdapter()
            )
            .add(
                io.getstream.feeds.android.network.models.FeedsPreferences.Reaction
                    .ReactionAdapter()
            )
            .add(
                io.getstream.feeds.android.network.models.FollowPushPreference
                    .FollowPushPreferenceAdapter()
            )
            .add(io.getstream.feeds.android.network.models.FollowStatus.FollowStatusAdapter())
            .add(
                io.getstream.feeds.android.network.models.FriendReactionType
                    .FriendReactionTypeAdapter()
            )
            .add(io.getstream.feeds.android.network.models.LLMRule.Action.ActionAdapter())
            .add(io.getstream.feeds.android.network.models.MediaDisplayResponse.Mode.ModeAdapter())
            .add(
                io.getstream.feeds.android.network.models.ModerationAction.ModerationActionAdapter()
            )
            .add(
                io.getstream.feeds.android.network.models.ModerationEngineConfigRequest.Model
                    .ModelAdapter()
            )
            .add(
                io.getstream.feeds.android.network.models.ModerationEngineConfigRequest.Provider
                    .ProviderAdapter()
            )
            .add(
                io.getstream.feeds.android.network.models.ModerationEngineConfigResponse.Provider
                    .ProviderAdapter()
            )
            .add(io.getstream.feeds.android.network.models.PushLevel.PushLevelAdapter())
            .add(
                io.getstream.feeds.android.network.models.PushPreferenceInput.ChatLevel
                    .ChatLevelAdapter()
            )
            .add(
                io.getstream.feeds.android.network.models.PushPreferenceInput.FeedsLevel
                    .FeedsLevelAdapter()
            )
            .add(
                io.getstream.feeds.android.network.models.PushProviderName.PushProviderNameAdapter()
            )
            .add(io.getstream.feeds.android.network.models.QueryCommentsRequest.Sort.SortAdapter())
            .add(io.getstream.feeds.android.network.models.RestrictReplies.RestrictRepliesAdapter())
            .add(io.getstream.feeds.android.network.models.RuleBuilderAction.Type.TypeAdapter())
            .add(io.getstream.feeds.android.network.models.SortParamType.SortParamTypeAdapter())
            .add(
                io.getstream.feeds.android.network.models.SubmitActionRequest.ActionType
                    .ActionTypeAdapter()
            )
            .add(
                io.getstream.feeds.android.network.models.UpdateFeedMembersRequest.Operation
                    .OperationAdapter()
            )
            .add(
                io.getstream.feeds.android.network.models.VelocityFilterConfigRule.Action
                    .ActionAdapter()
            )
            .add(
                io.getstream.feeds.android.network.models.VelocityFilterConfigRule.CascadingAction
                    .CascadingActionAdapter()
            )
            .add(
                io.getstream.feeds.android.network.models.VotingVisibility.VotingVisibilityAdapter()
            )
            .add(io.getstream.feeds.android.network.infrastructure.BigDecimalAdapter())
            .add(io.getstream.feeds.android.network.infrastructure.BigIntegerAdapter())
            .add(io.getstream.feeds.android.network.infrastructure.ByteArrayAdapter())
            .add(io.getstream.feeds.android.network.infrastructure.URIAdapter())
            .add(io.getstream.feeds.android.network.infrastructure.UUIDAdapter())
            .add(io.getstream.feeds.android.network.infrastructure.JavaUtilDateTimeAdapter())
            .add(io.getstream.feeds.android.network.models.WSEventAdapter())
            .addLast(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())

    @JvmStatic public val moshi: Moshi by lazy { moshiBuilder.build() }
}
