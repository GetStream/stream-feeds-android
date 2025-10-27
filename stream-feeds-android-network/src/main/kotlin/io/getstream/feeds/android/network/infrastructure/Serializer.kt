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

package io.getstream.feeds.android.network.infrastructure

import com.squareup.moshi.Moshi
import io.getstream.feeds.android.network.models.WSEventAdapter

public object Serializer {
    @JvmStatic
    public val moshiBuilder: Moshi.Builder =
        Moshi.Builder()
            .add(
                io.getstream.feeds.android.network.models.AWSRekognitionRule.Action.ActionAdapter()
            )
            .add(
                io.getstream.feeds.android.network.models.ActivityRequest.Visibility
                    .VisibilityAdapter()
            )
            .add(
                io.getstream.feeds.android.network.models.ActivityResponse.Visibility
                    .VisibilityAdapter()
            )
            .add(
                io.getstream.feeds.android.network.models.AddActivityRequest.Visibility
                    .VisibilityAdapter()
            )
            .add(io.getstream.feeds.android.network.models.AutomodRule.Action.ActionAdapter())
            .add(
                io.getstream.feeds.android.network.models.AutomodSemanticFiltersRule.Action
                    .ActionAdapter()
            )
            .add(
                io.getstream.feeds.android.network.models.BanActionRequest.DeleteMessages
                    .DeleteMessagesAdapter()
            )
            .add(
                io.getstream.feeds.android.network.models.BanOptions.DeleteMessages
                    .DeleteMessagesAdapter()
            )
            .add(
                io.getstream.feeds.android.network.models.BanRequest.DeleteMessages
                    .DeleteMessagesAdapter()
            )
            .add(
                io.getstream.feeds.android.network.models.BlockListOptions.Behavior
                    .BehaviorAdapter()
            )
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
            .add(io.getstream.feeds.android.network.models.ChannelConfig.Automod.AutomodAdapter())
            .add(
                io.getstream.feeds.android.network.models.ChannelConfig.AutomodBehavior
                    .AutomodBehaviorAdapter()
            )
            .add(
                io.getstream.feeds.android.network.models.ChannelConfig.BlocklistBehavior
                    .BlocklistBehaviorAdapter()
            )
            .add(
                io.getstream.feeds.android.network.models.ChannelConfigWithInfo.Automod
                    .AutomodAdapter()
            )
            .add(
                io.getstream.feeds.android.network.models.ChannelConfigWithInfo.AutomodBehavior
                    .AutomodBehaviorAdapter()
            )
            .add(
                io.getstream.feeds.android.network.models.ChannelConfigWithInfo.BlocklistBehavior
                    .BlocklistBehaviorAdapter()
            )
            .add(io.getstream.feeds.android.network.models.ChannelMember.Role.RoleAdapter())
            .add(
                io.getstream.feeds.android.network.models.ChannelOwnCapability
                    .ChannelOwnCapabilityAdapter()
            )
            .add(
                io.getstream.feeds.android.network.models.ConfigOverrides.BlocklistBehavior
                    .BlocklistBehaviorAdapter()
            )
            .add(
                io.getstream.feeds.android.network.models.CreateBlockListRequest.Type.TypeAdapter()
            )
            .add(
                io.getstream.feeds.android.network.models.CreateDeviceRequest.PushProvider
                    .PushProviderAdapter()
            )
            .add(
                io.getstream.feeds.android.network.models.CreatePollRequest.VotingVisibility
                    .VotingVisibilityAdapter()
            )
            .add(
                io.getstream.feeds.android.network.models.Device.PushProvider.PushProviderAdapter()
            )
            .add(io.getstream.feeds.android.network.models.FeedInput.Visibility.VisibilityAdapter())
            .add(
                io.getstream.feeds.android.network.models.FeedMemberResponse.Status.StatusAdapter()
            )
            .add(
                io.getstream.feeds.android.network.models.FeedOwnCapability
                    .FeedOwnCapabilityAdapter()
            )
            .add(
                io.getstream.feeds.android.network.models.FeedRequest.Visibility.VisibilityAdapter()
            )
            .add(
                io.getstream.feeds.android.network.models.FeedsPreferences.Comment.CommentAdapter()
            )
            .add(
                io.getstream.feeds.android.network.models.FeedsPreferences.CommentReaction
                    .CommentReactionAdapter()
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
                io.getstream.feeds.android.network.models.FollowRequest.PushPreference
                    .PushPreferenceAdapter()
            )
            .add(
                io.getstream.feeds.android.network.models.FollowResponse.PushPreference
                    .PushPreferenceAdapter()
            )
            .add(io.getstream.feeds.android.network.models.FollowResponse.Status.StatusAdapter())
            .add(io.getstream.feeds.android.network.models.ImageSize.Crop.CropAdapter())
            .add(io.getstream.feeds.android.network.models.ImageSize.Resize.ResizeAdapter())
            .add(io.getstream.feeds.android.network.models.LLMRule.Action.ActionAdapter())
            .add(io.getstream.feeds.android.network.models.MessageResponse.Type.TypeAdapter())
            .add(io.getstream.feeds.android.network.models.OCRRule.Action.ActionAdapter())
            .add(
                io.getstream.feeds.android.network.models.PushPreferenceInput.CallLevel
                    .CallLevelAdapter()
            )
            .add(
                io.getstream.feeds.android.network.models.PushPreferenceInput.ChatLevel
                    .ChatLevelAdapter()
            )
            .add(
                io.getstream.feeds.android.network.models.PushPreferenceInput.FeedsLevel
                    .FeedsLevelAdapter()
            )
            .add(io.getstream.feeds.android.network.models.QueryCommentsRequest.Sort.SortAdapter())
            .add(io.getstream.feeds.android.network.models.RuleBuilderAction.Type.TypeAdapter())
            .add(
                io.getstream.feeds.android.network.models.SubmitActionRequest.ActionType
                    .ActionTypeAdapter()
            )
            .add(
                io.getstream.feeds.android.network.models.UpdateFeedMembersRequest.Operation
                    .OperationAdapter()
            )
            .add(
                io.getstream.feeds.android.network.models.UpdateFollowRequest.PushPreference
                    .PushPreferenceAdapter()
            )
            .add(
                io.getstream.feeds.android.network.models.UpdatePollRequest.VotingVisibility
                    .VotingVisibilityAdapter()
            )
            .add(
                io.getstream.feeds.android.network.models.VelocityFilterConfigRule.Action
                    .ActionAdapter()
            )
            .add(
                io.getstream.feeds.android.network.models.VelocityFilterConfigRule.CascadingAction
                    .CascadingActionAdapter()
            )
            .add(io.getstream.feeds.android.network.infrastructure.BigDecimalAdapter())
            .add(io.getstream.feeds.android.network.infrastructure.BigIntegerAdapter())
            .add(io.getstream.feeds.android.network.infrastructure.ByteArrayAdapter())
            .add(io.getstream.feeds.android.network.infrastructure.URIAdapter())
            .add(io.getstream.feeds.android.network.infrastructure.UUIDAdapter())
            .add(io.getstream.feeds.android.network.infrastructure.JavaUtilDateTimeAdapter())
            .add(WSEventAdapter())
            .addLast(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())

    @JvmStatic public val moshi: Moshi by lazy { moshiBuilder.build() }
}
