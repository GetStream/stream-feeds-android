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
package io.getstream.feeds.android.client.api.model

/**
 * A data model representing a request to add or update a member in a feed.
 *
 * @property userId The unique identifier of the user to add or update as a member. This property is
 *   required and identifies which user the member request applies to.
 * @property invite Whether to send an invitation to the user. If `true`, an invitation will be sent
 *   to the user. If `false` or `null`, the user will be added directly without an invitation (if
 *   permissions allow).
 * @property role The role to assign to the member in the feed. This property controls the
 *   permissions and capabilities the member will have within the feed. It may be `null` to use the
 *   default role for the feed.
 * @property custom Custom data associated with the member. This property allows for storing
 *   additional metadata or custom fields specific to your application's member management needs. An
 *   empty map means no custom data is associated with the member.
 */
public data class FeedMemberRequestData(
    public val userId: String,
    public val invite: Boolean? = null,
    public val role: String? = null,
    public val custom: Map<String, Any?> = emptyMap(),
)
