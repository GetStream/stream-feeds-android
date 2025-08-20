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
    ) =
        CommentData(
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

    private fun userData(id: String): UserData =
        UserData(
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
            updatedAt = Date(1),
        )
}
