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

package io.getstream.feeds.android.core.generated.infrastructure

import com.squareup.moshi.Moshi

object Serializer {
    @JvmStatic
    val moshiBuilder: Moshi.Builder = Moshi.Builder()
        .add(io.getstream.feeds.android.core.generated.models.AWSRekognitionRule.Action.ActionAdapter())
        .add(io.getstream.feeds.android.core.generated.models.ActivityRequest.Visibility.VisibilityAdapter())
        .add(io.getstream.feeds.android.core.generated.models.ActivityResponse.Visibility.VisibilityAdapter())
        .add(io.getstream.feeds.android.core.generated.models.AddActivityRequest.Visibility.VisibilityAdapter())
        .add(io.getstream.feeds.android.core.generated.models.AudioSettings.DefaultDevice.DefaultDeviceAdapter())
        .add(io.getstream.feeds.android.core.generated.models.AudioSettingsResponse.DefaultDevice.DefaultDeviceAdapter())
        .add(io.getstream.feeds.android.core.generated.models.AutomodRule.Action.ActionAdapter())
        .add(io.getstream.feeds.android.core.generated.models.AutomodSemanticFiltersRule.Action.ActionAdapter())
        .add(io.getstream.feeds.android.core.generated.models.BanActionRequest.DeleteMessages.DeleteMessagesAdapter())
        .add(io.getstream.feeds.android.core.generated.models.BanRequest.DeleteMessages.DeleteMessagesAdapter())
        .add(io.getstream.feeds.android.core.generated.models.BlockListOptions.Behavior.BehaviorAdapter())
        .add(io.getstream.feeds.android.core.generated.models.BlockListRule.Action.ActionAdapter())
        .add(io.getstream.feeds.android.core.generated.models.BodyguardRule.Action.ActionAdapter())
        .add(io.getstream.feeds.android.core.generated.models.BodyguardSeverityRule.Action.ActionAdapter())
        .add(io.getstream.feeds.android.core.generated.models.BodyguardSeverityRule.Severity.SeverityAdapter())
        .add(io.getstream.feeds.android.core.generated.models.ChannelConfig.Automod.AutomodAdapter())
        .add(io.getstream.feeds.android.core.generated.models.ChannelConfig.AutomodBehavior.AutomodBehaviorAdapter())
        .add(io.getstream.feeds.android.core.generated.models.ChannelConfig.BlocklistBehavior.BlocklistBehaviorAdapter())
        .add(io.getstream.feeds.android.core.generated.models.ChannelConfigWithInfo.Automod.AutomodAdapter())
        .add(io.getstream.feeds.android.core.generated.models.ChannelConfigWithInfo.AutomodBehavior.AutomodBehaviorAdapter())
        .add(io.getstream.feeds.android.core.generated.models.ChannelConfigWithInfo.BlocklistBehavior.BlocklistBehaviorAdapter())
        .add(io.getstream.feeds.android.core.generated.models.ChannelMember.Role.RoleAdapter())
        .add(io.getstream.feeds.android.core.generated.models.ChannelOwnCapability.ChannelOwnCapabilityAdapter())
        .add(io.getstream.feeds.android.core.generated.models.ConfigOverrides.BlocklistBehavior.BlocklistBehaviorAdapter())
        .add(io.getstream.feeds.android.core.generated.models.CreateBlockListRequest.Type.TypeAdapter())
        .add(io.getstream.feeds.android.core.generated.models.CreateDeviceRequest.PushProvider.PushProviderAdapter())
        .add(io.getstream.feeds.android.core.generated.models.CreatePollRequest.VotingVisibility.VotingVisibilityAdapter())
        .add(io.getstream.feeds.android.core.generated.models.Device.PushProvider.PushProviderAdapter())
        .add(io.getstream.feeds.android.core.generated.models.FeedInput.Visibility.VisibilityAdapter())
        .add(io.getstream.feeds.android.core.generated.models.FeedMemberResponse.Status.StatusAdapter())
        .add(io.getstream.feeds.android.core.generated.models.FeedOwnCapability.FeedOwnCapabilityAdapter())
        .add(io.getstream.feeds.android.core.generated.models.FeedRequest.Visibility.VisibilityAdapter())
        .add(io.getstream.feeds.android.core.generated.models.FollowRequest.PushPreference.PushPreferenceAdapter())
        .add(io.getstream.feeds.android.core.generated.models.FollowResponse.PushPreference.PushPreferenceAdapter())
        .add(io.getstream.feeds.android.core.generated.models.FollowResponse.Status.StatusAdapter())
        .add(io.getstream.feeds.android.core.generated.models.FrameRecordSettings.Mode.ModeAdapter())
        .add(io.getstream.feeds.android.core.generated.models.FrameRecordingSettingsResponse.Mode.ModeAdapter())
        .add(io.getstream.feeds.android.core.generated.models.ImageSize.Crop.CropAdapter())
        .add(io.getstream.feeds.android.core.generated.models.ImageSize.Resize.ResizeAdapter())
        .add(io.getstream.feeds.android.core.generated.models.IngressAudioEncodingOptions.Channels.ChannelsAdapter())
        .add(io.getstream.feeds.android.core.generated.models.IngressVideoLayer.Codec.CodecAdapter())
        .add(io.getstream.feeds.android.core.generated.models.LayoutSettings.Name.NameAdapter())
        .add(io.getstream.feeds.android.core.generated.models.MessageResponse.Type.TypeAdapter())
        .add(io.getstream.feeds.android.core.generated.models.NoiseCancellationSettings.Mode.ModeAdapter())
        .add(io.getstream.feeds.android.core.generated.models.OCRRule.Action.ActionAdapter())
        .add(io.getstream.feeds.android.core.generated.models.QueryCommentsRequest.Sort.SortAdapter())
        .add(io.getstream.feeds.android.core.generated.models.SingleFollowRequest.PushPreference.PushPreferenceAdapter())
        .add(io.getstream.feeds.android.core.generated.models.StoriesConfig.ExpirationBehaviour.ExpirationBehaviourAdapter())
        .add(io.getstream.feeds.android.core.generated.models.SubmitActionRequest.ActionType.ActionTypeAdapter())
        .add(io.getstream.feeds.android.core.generated.models.TranscriptionSettings.ClosedCaptionMode.ClosedCaptionModeAdapter())
        .add(io.getstream.feeds.android.core.generated.models.TranscriptionSettings.Language.LanguageAdapter())
        .add(io.getstream.feeds.android.core.generated.models.TranscriptionSettings.Mode.ModeAdapter())
        .add(io.getstream.feeds.android.core.generated.models.TranscriptionSettingsResponse.ClosedCaptionMode.ClosedCaptionModeAdapter())
        .add(io.getstream.feeds.android.core.generated.models.TranscriptionSettingsResponse.Language.LanguageAdapter())
        .add(io.getstream.feeds.android.core.generated.models.TranscriptionSettingsResponse.Mode.ModeAdapter())
        .add(io.getstream.feeds.android.core.generated.models.UpdateFeedMembersRequest.Operation.OperationAdapter())
        .add(io.getstream.feeds.android.core.generated.models.UpdateFollowRequest.PushPreference.PushPreferenceAdapter())
        .add(io.getstream.feeds.android.core.generated.models.UpdatePollRequest.VotingVisibility.VotingVisibilityAdapter())
        .add(io.getstream.feeds.android.core.generated.models.VelocityFilterConfigRule.Action.ActionAdapter())
        .add(io.getstream.feeds.android.core.generated.models.VelocityFilterConfigRule.CascadingAction.CascadingActionAdapter())
        .add(io.getstream.feeds.android.core.generated.models.VideoSettings.CameraFacing.CameraFacingAdapter())
        .add(io.getstream.feeds.android.core.generated.models.VideoSettingsResponse.CameraFacing.CameraFacingAdapter())
        .add(io.getstream.feeds.android.core.generated.infrastructure.BigDecimalAdapter())
        .add(io.getstream.feeds.android.core.generated.infrastructure.BigIntegerAdapter())
        .add(io.getstream.feeds.android.core.generated.infrastructure.ByteArrayAdapter())
        .add(io.getstream.feeds.android.core.generated.infrastructure.LocalDateAdapter())
        .add(io.getstream.feeds.android.core.generated.infrastructure.LocalDateTimeAdapter())
        .add(io.getstream.feeds.android.core.generated.infrastructure.OffsetDateTimeAdapter())
        .add(io.getstream.feeds.android.core.generated.infrastructure.URIAdapter())
        .add(io.getstream.feeds.android.core.generated.infrastructure.UUIDAdapter())
        .addLast(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())
    
    @JvmStatic
    val moshi: Moshi by lazy {
        moshiBuilder.build()
    }
}