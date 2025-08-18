package io.getstream.feeds.android.client.internal.test

import io.getstream.feeds.android.client.api.model.CommentData
import io.getstream.feeds.android.client.api.model.ThreadedCommentData
import io.getstream.feeds.android.client.api.model.UserData
import java.util.Date

internal object TestData {
    fun commentData(
        id: String = "comment-id",
        parentId: String? = null,
        text: String = "Test comment",
        createdAt: Date = Date(1),
    ) = CommentData(
        id = id,
        parentId = parentId,
        attachments = null,
        confidenceScore = 0f,
        controversyScore = null,
        createdAt = createdAt,
        custom = null,
        deletedAt = null,
        downvoteCount = 0,
        latestReactions = emptyList(),
        mentionedUsers = emptyList(),
        meta = null,
        moderation = null,
        objectId = id,
        objectType = "comment",
        ownReactions = emptyList(),
        reactionCount = 0,
        reactionGroups = emptyMap(),
        replies = emptyList(),
        replyCount = 0,
        score = 0,
        status = "",
        text = text,
        updatedAt = Date(1),
        upvoteCount = 0,
        user = userData(id),
    )

    fun threadedCommentData(
        id: String,
        parentId: String? = null,
        text: String = "Test comment",
        replies: List<ThreadedCommentData> = emptyList(),
        createdAt: Date = Date(1),
    ): ThreadedCommentData {
        return ThreadedCommentData(
            id = id,
            parentId = parentId,
            attachments = null,
            confidenceScore = 0f,
            controversyScore = null,
            createdAt = createdAt,
            custom = null,
            deletedAt = null,
            downvoteCount = 0,
            latestReactions = emptyList(),
            mentionedUsers = emptyList(),
            meta = null,
            moderation = null,
            objectId = id,
            objectType = "comment",
            ownReactions = emptyList(),
            reactionCount = 0,
            reactionGroups = emptyMap(),
            replies = replies,
            replyCount = replies.size,
            score = 0,
            status = "",
            text = text,
            updatedAt = Date(1),
            upvoteCount = 0,
            user = userData(id),
        )
    }

    private fun userData(id: String): UserData = UserData(
        banned = false,
        blockedUserIds = emptyList(),
        createdAt = Date(1),
        custom = emptyMap(),
        deactivatedAt = null,
        deletedAt = null,
        id = id,
        image = null,
        language = null,
        lastActive = null,
        name = null,
        online = false,
        revokeTokensIssuedBefore = null,
        role = "",
        teams = emptyList(),
        updatedAt = Date(1)
    )
}
