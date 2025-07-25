package io.getstream.feeds.android.client.api.model.request

import io.getstream.feeds.android.core.generated.models.AddCommentRequest
import io.getstream.feeds.android.core.generated.models.Attachment
import io.getstream.feeds.android.client.api.state.Activity

/**
 * A request for adding comment when interacting with [Activity].
 */
public data class ActivityAddCommentRequest(
    private val request: AddCommentRequest,
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
        attachments: List<Attachment>? = null,
        createNotificationActivity: Boolean? = null,
        custom: Map<String, Any?>? = null,
        mentionedUserIds: List<String>? = null,
        parentId: String? = null,
    ): this (
        AddCommentRequest(
            comment = comment,
            attachments = attachments,
//            createNotificationActivity = createNotificationActivity,
            custom = custom,
            mentionedUserIds = mentionedUserIds,
            objectId = "",
            objectType = "activity",
            parentId = parentId,
        )
    )

    /**
     * Sets the ID of the activity to which this comment will be added.
     */
    internal fun withActivityId(activityId: String): AddCommentRequest {
        return request.copy(objectId = activityId)
    }
}
