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
        .add(io.getstream.feeds.android.core.generated.models.ActivityRequest.Visibility.VisibilityAdapter())
        .add(io.getstream.feeds.android.core.generated.models.ActivityResponse.Visibility.VisibilityAdapter())
        .add(io.getstream.feeds.android.core.generated.models.AddActivityRequest.Visibility.VisibilityAdapter())
        .add(io.getstream.feeds.android.core.generated.models.BlockListOptions.Behavior.BehaviorAdapter())
        .add(io.getstream.feeds.android.core.generated.models.ChannelConfigWithInfo.Automod.AutomodAdapter())
        .add(io.getstream.feeds.android.core.generated.models.ChannelConfigWithInfo.AutomodBehavior.AutomodBehaviorAdapter())
        .add(io.getstream.feeds.android.core.generated.models.ChannelConfigWithInfo.BlocklistBehavior.BlocklistBehaviorAdapter())
        .add(io.getstream.feeds.android.core.generated.models.ChannelMember.Role.RoleAdapter())
        .add(io.getstream.feeds.android.core.generated.models.ChannelOwnCapability.ChannelOwnCapabilityAdapter())
        .add(io.getstream.feeds.android.core.generated.models.FeedInput.Visibility.VisibilityAdapter())
        .add(io.getstream.feeds.android.core.generated.models.FeedMemberResponse.Status.StatusAdapter())
        .add(io.getstream.feeds.android.core.generated.models.FeedOwnCapability.FeedOwnCapabilityAdapter())
        .add(io.getstream.feeds.android.core.generated.models.FeedRequest.Visibility.VisibilityAdapter())
        .add(io.getstream.feeds.android.core.generated.models.FollowRequest.PushPreference.PushPreferenceAdapter())
        .add(io.getstream.feeds.android.core.generated.models.FollowResponse.PushPreference.PushPreferenceAdapter())
        .add(io.getstream.feeds.android.core.generated.models.FollowResponse.Status.StatusAdapter())
        .add(io.getstream.feeds.android.core.generated.models.QueryCommentsRequest.Sort.SortAdapter())
        .add(io.getstream.feeds.android.core.generated.models.SingleFollowRequest.PushPreference.PushPreferenceAdapter())
        .add(io.getstream.feeds.android.core.generated.models.StoriesConfig.ExpirationBehaviour.ExpirationBehaviourAdapter())
        .add(io.getstream.feeds.android.core.generated.models.UpdateFeedMembersRequest.Operation.OperationAdapter())
        .add(io.getstream.feeds.android.core.generated.models.UpdateFollowRequest.PushPreference.PushPreferenceAdapter())
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