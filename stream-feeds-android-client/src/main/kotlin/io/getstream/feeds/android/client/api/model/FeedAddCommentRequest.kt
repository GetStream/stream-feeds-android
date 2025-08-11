package io.getstream.feeds.android.client.api.model

import io.getstream.feeds.android.client.api.file.FeedUploadPayload
import io.getstream.feeds.android.core.generated.models.AddCommentRequest

public data class FeedAddCommentRequest(
    val request: AddCommentRequest,
    val attachmentUploads: List<FeedUploadPayload> = emptyList(),
)
