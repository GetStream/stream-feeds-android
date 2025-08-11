package io.getstream.feeds.android.client.api.model.request

import io.getstream.feeds.android.client.api.file.FeedUploadPayload
import io.getstream.feeds.android.client.api.state.Activity
import io.getstream.feeds.android.core.generated.models.AddCommentRequest
import io.getstream.feeds.android.core.generated.models.Attachment

/**
 * A request for adding comment when interacting with [Activity].
 */
public data class ActivityAddCommentRequest(
    val request: AddCommentRequest,
    val attachmentUploads: List<FeedUploadPayload> = emptyList(),
) {

    /**
     * Creates a request to add a comment to an activity.
     *
     * @param comment The content of the comment to be added.
     * @param attachments Optional list of attachments to include with the comment.
     * @param createNotificationActivity Optional flag to create a notification activity.
     * @param custom Optional custom data to include with the comment.
     * @param mentionedUserIds Optional list of user IDs to mention in the comment.
     * @param parentId Optional ID of the parent comment if this is a reply.
     */
    public constructor(
        comment: String,
        activityId: String,
        attachments: List<Attachment>? = null,
        createNotificationActivity: Boolean? = null,
        custom: Map<String, Any?>? = null,
        mentionedUserIds: List<String>? = null,
        parentId: String? = null,
        attachmentUploads: List<FeedUploadPayload> = emptyList(),
    ) : this(
        AddCommentRequest(
            comment = comment,
            attachments = attachments,
            createNotificationActivity = createNotificationActivity,
            custom = custom,
            mentionedUserIds = mentionedUserIds,
            objectId = activityId,
            objectType = "activity",
            parentId = parentId,
        ),
        attachmentUploads = attachmentUploads,
    )
}
