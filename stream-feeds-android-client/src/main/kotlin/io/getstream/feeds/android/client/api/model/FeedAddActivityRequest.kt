package io.getstream.feeds.android.client.api.model

import io.getstream.feeds.android.client.api.file.FeedUploadPayload
import io.getstream.feeds.android.core.generated.models.ActivityLocation
import io.getstream.feeds.android.core.generated.models.AddActivityRequest
import io.getstream.feeds.android.core.generated.models.Attachment

public data class FeedAddActivityRequest(
    val request: AddActivityRequest,
    val attachmentUploads: List<FeedUploadPayload> = emptyList(),
) {
    public constructor(
        attachments: List<Attachment>? = null,
        attachmentUploads: List<FeedUploadPayload>? = null,
        custom: Map<String, Any>? = null,
        expiresAt: String? = null,
        filterTags: List<String>? = null,
        id: String? = null,
        interestTags: List<String>? = null,
        location: ActivityLocation? = null,
        mentionedUserIds: List<String>? = null,
        parentId: String? = null,
        pollId: String? = null,
        searchData: Map<String, Any>? = null,
        text: String? = null,
        type: String,
        visibility: AddActivityRequest.Visibility? = null,
        visibilityTag: String? = null
    ) : this(
        request = AddActivityRequest(
            attachments = attachments,
            custom = custom,
            expiresAt = expiresAt,
            filterTags = filterTags,
            id = id,
            interestTags = interestTags,
            location = location,
            mentionedUserIds = mentionedUserIds,
            parentId = parentId,
            pollId = pollId,
            searchData = searchData,
            text = text,
            type = type,
            visibility = visibility,
            visibilityTag = visibilityTag
        ),
        attachmentUploads = attachmentUploads.orEmpty()
    )
}
