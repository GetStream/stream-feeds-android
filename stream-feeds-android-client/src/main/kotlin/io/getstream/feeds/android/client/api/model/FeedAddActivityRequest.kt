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
import io.getstream.feeds.android.network.models.AddActivityRequest
import io.getstream.feeds.android.network.models.Attachment
import io.getstream.feeds.android.network.models.Location

/**
 * Request payload for adding a new activity to one or more feeds.
 *
 * @property request The activity request sent to the server.
 * @property attachmentUploads Local files to upload and attach to the activity.
 */
public data class FeedAddActivityRequest
internal constructor(
    val request: AddActivityRequest,
    val attachmentUploads: List<FeedUploadPayload> = emptyList(),
) {
    /**
     * Builds a [FeedAddActivityRequest] from individual activity fields.
     *
     * @param type The activity type (for example `"post"`).
     * @param feeds The IDs of the feeds the activity should be added to.
     * @param attachments Already-uploaded attachments to attach to the activity.
     * @param attachmentUploads Local files to upload and attach to the activity.
     * @param collectionRefs References to collection entries associated with the activity.
     * @param createNotificationActivity Whether the server should create a notification activity.
     * @param custom Custom data to attach to the activity.
     * @param expiresAt Optional expiration timestamp for the activity.
     * @param filterTags Tags used for filtering the activity.
     * @param id Optional client-provided activity ID.
     * @param interestTags Tags used to drive interest-based ranking.
     * @param location Optional geographic location associated with the activity.
     * @param mentionedUserIds IDs of users mentioned in the activity.
     * @param parentId The ID of the parent activity, when this is a reply.
     * @param pollId The ID of an associated poll, when applicable.
     * @param restrictReplies The reply-restriction policy for the activity.
     * @param searchData Additional data indexed for search.
     * @param skipEnrichUrl Whether to skip URL enrichment when processing the activity text.
     * @param text The textual content of the activity.
     * @param visibility The visibility policy for the activity.
     * @param visibilityTag The visibility tag used when [visibility] is tag-based.
     */
    public constructor(
        type: String,
        feeds: List<String> = emptyList(),
        attachments: List<Attachment>? = null,
        attachmentUploads: List<FeedUploadPayload>? = null,
        collectionRefs: List<String>? = null,
        createNotificationActivity: Boolean? = null,
        custom: Map<String, Any>? = null,
        expiresAt: String? = null,
        filterTags: List<String>? = null,
        id: String? = null,
        interestTags: List<String>? = null,
        location: Location? = null,
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
                createNotificationActivity = createNotificationActivity,
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
