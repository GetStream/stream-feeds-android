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

import io.getstream.feeds.android.client.api.state.query.CommentsSortDataFields
import io.getstream.feeds.android.client.internal.model.addReply
import io.getstream.feeds.android.client.internal.model.removeReaction
import io.getstream.feeds.android.client.internal.model.upsertReaction
import io.getstream.feeds.android.client.internal.test.TestData.commentData
import io.getstream.feeds.android.client.internal.test.TestData.feedsReactionData
import io.getstream.feeds.android.client.internal.test.TestData.threadedCommentData
import org.junit.Assert.assertEquals
import org.junit.Test

internal class ThreadedCommentDataTest {

    private val currentUserId = "current-user"
    private val otherUserId = "other-user"
    private val activityId = "activity-1"

    @Test
    fun `upsertReaction from another user, merge updated comment and keep own reactions`() {
        // Given
        val existingOwnReaction =
            feedsReactionData(activityId = activityId, type = "heart", userId = currentUserId)
        val originalComment =
            threadedCommentData(
                id = "comment-1",
                text = "Original text",
                ownReactions = listOf(existingOwnReaction),
            )
        val newReaction =
            feedsReactionData(activityId = activityId, type = "like", userId = otherUserId)
        val update = commentData(id = "comment-1", text = "Updated text")
        val expected = originalComment.copy(text = "Updated text")

        // When
        val result = originalComment.upsertReaction(update, newReaction, currentUserId)

        // Then
        assertEquals(expected, result)
    }

    @Test
    fun `upsertReaction from current user, merge updated comment and add to own reactions`() {
        // Given
        val existingOwnReaction =
            feedsReactionData(activityId = activityId, type = "heart", userId = currentUserId)
        val originalComment =
            threadedCommentData(
                id = "comment-1",
                text = "Original text",
                ownReactions = listOf(existingOwnReaction),
            )
        val newReaction =
            feedsReactionData(activityId = activityId, type = "like", userId = currentUserId)
        val update = commentData(id = "comment-1", text = "Updated text")
        val expected =
            originalComment.copy(
                text = "Updated text",
                ownReactions = listOf(existingOwnReaction, newReaction),
            )

        // When
        val result = originalComment.upsertReaction(update, newReaction, currentUserId)

        // Then
        assertEquals(expected, result)
    }

    // MARK: - removeReaction Tests

    @Test
    fun `removeReaction from another user, merge update comment and keep own reactions`() {
        // Given
        val existingOwnReaction =
            feedsReactionData(activityId = activityId, type = "heart", userId = currentUserId)
        val originalComment =
            threadedCommentData(
                id = "comment-1",
                text = "Original text",
                ownReactions = listOf(existingOwnReaction),
            )
        val reaction =
            feedsReactionData(activityId = activityId, type = "like", userId = otherUserId)
        val update = commentData(id = "comment-1", text = "Updated text")
        val expected = originalComment.copy(text = "Updated text")

        // When
        val result = originalComment.removeReaction(update, reaction, currentUserId)

        // Then
        assertEquals(expected, result)
    }

    @Test
    fun `removeReaction from current user, merge updated comment and remove from own reactions`() {
        // Given
        val existingOwnReaction =
            feedsReactionData(activityId = activityId, type = "heart", userId = currentUserId)
        val originalComment =
            threadedCommentData(
                id = "comment-1",
                text = "Original text",
                ownReactions = listOf(existingOwnReaction),
            )
        val update = commentData(id = "comment-1", text = "Updated text")
        val expected = originalComment.copy(text = "Updated text", ownReactions = emptyList())

        // When
        val result = originalComment.removeReaction(update, existingOwnReaction, currentUserId)

        // Then
        assertEquals(expected, result)
    }

    @Test
    fun `addReply should append reply and increment replyCount`() {
        // Given
        val comparator = Comparator<CommentsSortDataFields> { _, _ -> 0 } // simple comparator
        val parent = threadedCommentData(
            id = "comment-1",
            text = "Original text",
            replies = emptyList(),
            replyCount = 0
        )
        val newComment = threadedCommentData(
            id = "reply-comment-1",
            parentId = "comment-1",
            text = "Updated text"
        )

        // When
        val updated = parent.addReply(newComment, comparator)

        // Then
        assertEquals(1, updated.replyCount)
        assertEquals(listOf(newComment), updated.replies)
    }

    @Test
    fun `addReply should keep replies sorted based on comparator`() {
        // Given
        val comparator = Comparator<CommentsSortDataFields> { a, b ->
            // Reverse sort  for demonstration
            b.createdAt.compareTo(a.createdAt)
        }

        val originalComment = threadedCommentData(id = "comment-1", parentId = "parent-1")
        val newComment = threadedCommentData(id = "comment-2", parentId = "parent-1")
        val parent = threadedCommentData(
            id = "parent-1",
            replies = listOf(originalComment),
            replyCount = 1
        )
        val updated = parent.addReply(newComment, comparator)

        // Then: since comparator sorts descending, originalComment should come first
        assertEquals(listOf(originalComment, newComment), updated.replies)
    }

}
