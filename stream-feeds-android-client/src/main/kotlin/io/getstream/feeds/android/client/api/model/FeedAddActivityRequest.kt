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

package io.getstream.feeds.android.client.api.model

import io.getstream.feeds.android.client.api.file.FeedUploadPayload
import io.getstream.feeds.android.network.models.ActivityLocation
import io.getstream.feeds.android.network.models.AddActivityRequest
import io.getstream.feeds.android.network.models.Attachment

public data class FeedAddActivityRequest
internal constructor(
    val request: AddActivityRequest,
    val attachmentUploads: List<FeedUploadPayload> = emptyList(),
) {
    public constructor(
        type: String,
        feeds: List<String> = emptyList(),
        attachments: List<Attachment>? = null,
        attachmentUploads: List<FeedUploadPayload>? = null,
        collectionRefs: List<String>? = null,
        custom: Map<String, Any>? = null,
        expiresAt: String? = null,
        filterTags: List<String>? = null,
        id: String? = null,
        interestTags: List<String>? = null,
        location: ActivityLocation? = null,
        mentionedUserIds: List<String>? = null,
        parentId: String? = null,
        pollId: String? = null,
        restrictReplies: AddActivityRequest.RestrictReplies? = null,
        searchData: Map<String, Any>? = null,
        skipEnrichUrl: Boolean? = null,
        text: String? = null,
        visibility: AddActivityRequest.Visibility? = null,
        visibilityTag: String? = null,
    ) : this(
        request =
            AddActivityRequest(
                attachments = attachments,
                collectionRefs = collectionRefs,
                custom = custom,
                expiresAt = expiresAt,
                filterTags = filterTags,
                id = id,
                interestTags = interestTags,
                location = location,
                mentionedUserIds = mentionedUserIds,
                parentId = parentId,
                pollId = pollId,
                restrictReplies = restrictReplies,
                searchData = searchData,
                skipEnrichUrl = skipEnrichUrl,
                text = text,
                type = type,
                visibility = visibility,
                visibilityTag = visibilityTag,
                feeds = feeds,
            ),
        attachmentUploads = attachmentUploads.orEmpty(),
    )
}
