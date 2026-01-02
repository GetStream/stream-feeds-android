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

package io.getstream.feeds.android.client.internal.state

import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.model.ThreadedCommentData
import io.getstream.feeds.android.client.api.state.query.ActivityCommentsQuery
import io.getstream.feeds.android.client.api.state.query.CommentsSort
import io.getstream.feeds.android.client.internal.model.PaginationResult
import io.getstream.feeds.android.client.internal.test.TestData.commentData
import io.getstream.feeds.android.client.internal.test.TestData.defaultPaginationResult
import io.getstream.feeds.android.client.internal.test.TestData.feedsReactionData
import io.getstream.feeds.android.client.internal.test.TestData.reactionGroupData
import io.getstream.feeds.android.client.internal.test.TestData.threadedCommentData
import java.util.Date
import org.junit.Assert.assertEquals
import org.junit.Test

internal class ActivityCommentListStateImplTest {

    private val query =
        ActivityCommentsQuery(
            objectId = "activity_1",
            objectType = "activity",
            sort = CommentsSort.First,
        )
    private val state = ActivityCommentListStateImpl(query, currentUserId = "user-1")

    @Test
    fun `onQueryMoreComments when called, then merge comments and update pagination`() {
        val batch1 =
            listOf(
                threadedCommentData(id = "c1", createdAt = Date(1)),
                threadedCommentData(id = "c3", createdAt = Date(3)),
            )
        val batch2 =
            listOf(
                threadedCommentData(id = "c2", createdAt = Date(2)),
                threadedCommentData(id = "c4", createdAt = Date(4)),
            )
        val expected =
            listOf(
                threadedCommentData(id = "c1", createdAt = Date(1)),
                threadedCommentData(id = "c2", createdAt = Date(2)),
                threadedCommentData(id = "c3", createdAt = Date(3)),
                threadedCommentData(id = "c4", createdAt = Date(4)),
            )
        val result1 = PaginationResult(models = batch1, pagination = PaginationData("next1"))
        val result2 = PaginationResult(models = batch2, pagination = PaginationData("next2"))

        state.onQueryMoreComments(result1)
        state.onQueryMoreComments(result2)

        assertEquals(result2.pagination, state.pagination)
        assertEquals(expected, state.comments.value)
    }

    @Test
    fun `onCommentAdded when has no parent, then add it at the top-level in the correct position`() {
        val comment1 = commentData(id = "c1", createdAt = Date(1))
        val comment2 = commentData(id = "c2", createdAt = Date(2))
        val comment3 = commentData(id = "c3", createdAt = Date(3))
        val expected = listOf(comment1, comment2, comment3).map(::ThreadedCommentData)

        state.onCommentUpserted(comment1)
        state.onCommentUpserted(comment3)
        state.onCommentUpserted(comment2)

        assertEquals(expected, state.comments.value)
    }

    @Test
    fun `onCommentAdded when has a parent, then add it as a child in the correct position`() {
        val initialReplies =
            listOf(
                threadedCommentData("c2", parentId = "c1", createdAt = Date(2)),
                threadedCommentData("c4", parentId = "c1", createdAt = Date(4)),
            )
        val parent = threadedCommentData(id = "c1", replies = initialReplies, replyCount = 2)
        val reply = commentData(id = "c3", parentId = "c1", createdAt = Date(3))
        val expectedReply =
            threadedCommentData(id = "c3", parentId = "c1", createdAt = Date(3), replies = null)

        setupInitialState(parent)
        state.onCommentUpserted(reply)

        val expected =
            parent.copy(
                replies = listOf(initialReplies[0], expectedReply, initialReplies[1]),
                replyCount = 3,
            )
        assertEquals(listOf(expected), state.comments.value)
    }

    @Test
    fun `onCommentUpdated when it's at the top level, then update it in the list`() {
        val initialComment = threadedCommentData(id = "c1", createdAt = Date(1))
        val update = commentData(id = "c1", text = "Updated comment")
        val expected = threadedCommentData(id = "c1", text = "Updated comment", createdAt = Date(1))

        setupInitialState(initialComment)
        state.onCommentUpserted(update)

        assertEquals(listOf(expected), state.comments.value)
    }

    @Test
    fun `onCommentUpdated when it's a reply, then update it in the parent's replies`() {
        val initialReplies =
            listOf(
                threadedCommentData("c2", parentId = "c1", createdAt = Date(2)),
                threadedCommentData("c3", parentId = "c1", createdAt = Date(3)),
            )
        val parent = threadedCommentData(id = "c1", replies = initialReplies)
        val update =
            commentData(id = "c2", parentId = "c1", text = "Updated reply", createdAt = Date(4))
        val updatedReply =
            threadedCommentData("c2", parentId = "c1", text = "Updated reply", createdAt = Date(4))
        val expected = parent.copy(replies = listOf(initialReplies[1], updatedReply))

        setupInitialState(parent)
        state.onCommentUpserted(update)

        assertEquals(listOf(expected), state.comments.value)
    }

    @Test
    fun `on onCommentUpdated, then preserve ownReactions when updating threaded comment`() {
        val ownReaction = feedsReactionData("c1", "like", "user-1")
        val originalComment =
            threadedCommentData(
                id = "c1",
                text = "Original comment",
                ownReactions = listOf(ownReaction),
            )
        val update = commentData(id = "c1", text = "Updated comment", ownReactions = emptyList())

        setupInitialState(originalComment)
        state.onCommentUpserted(update)

        val expectedComment = originalComment.copy(text = "Updated comment")
        assertEquals(listOf(expectedComment), state.comments.value)
    }

    @Test
    fun `onCommentRemoved when it's a top-level comment, then remove it from the list`() {
        val comment1 = threadedCommentData(id = "c1", createdAt = Date(1))
        val comment2 = threadedCommentData(id = "c2", createdAt = Date(2))
        val comment3 = threadedCommentData(id = "c3", createdAt = Date(3))
        val initialComments = listOf(comment1, comment2, comment3)

        setupInitialState(*initialComments.toTypedArray())
        state.onCommentRemoved("c2")

        val expected = listOf(comment1, comment3)
        assertEquals(expected, state.comments.value)
    }

    @Test
    fun `onCommentRemoved when it's a nested reply, then remove it from parent's replies`() {
        val child2 = threadedCommentData("c2", parentId = "c1", createdAt = Date(2))
        val child3 = threadedCommentData("c3", parentId = "c1", createdAt = Date(3))
        val child4 = threadedCommentData("c4", parentId = "c1", createdAt = Date(4))
        val initialReplies = listOf(child2, child3, child4)
        val parent = threadedCommentData(id = "c1", replies = initialReplies)

        setupInitialState(parent)
        state.onCommentRemoved("c3")

        val expected = parent.copy(replies = listOf(child2, child4), replyCount = 2)
        assertEquals(listOf(expected), state.comments.value)
    }

    @Test
    fun `onCommentReactionUpserted when it's a top-level comment, then add reaction to it`() {
        val comment = threadedCommentData(id = "c1", createdAt = Date(1))
        val reaction =
            feedsReactionData(
                activityId = "c1",
                type = "like",
                userId = "user-2",
                createdAt = Date(2),
            )
        val update = commentData(id = "c1", text = "Updated comment")

        setupInitialState(comment)
        state.onCommentReactionUpserted(update, reaction, enforceUnique = false)

        val expected = comment.copy(text = "Updated comment")
        assertEquals(listOf(expected), state.comments.value)
    }

    @Test
    fun `onCommentReactionUpserted when it's a nested reply, then add reaction to the reply`() {
        val child = threadedCommentData("c2", parentId = "c1", text = "Original text")
        val parent = threadedCommentData(id = "c1", replies = listOf(child))
        val reaction =
            feedsReactionData(
                activityId = "c2",
                type = "like",
                userId = "user-2",
                createdAt = Date(2),
            )
        val update = commentData(id = "c2", parentId = "c1", text = "Updated text")

        setupInitialState(parent)
        state.onCommentReactionUpserted(update, reaction, enforceUnique = false)

        val expected = parent.copy(replies = listOf(child.copy(text = "Updated text")))
        assertEquals(listOf(expected), state.comments.value)
    }

    @Test
    fun `onCommentReactionUpserted with enforceUnique true, then replace all existing user reactions with single new one`() {
        val existingReactions =
            listOf(
                feedsReactionData(
                    activityId = "c1",
                    type = "like",
                    userId = "user-1",
                    createdAt = Date(1000),
                ),
                feedsReactionData(
                    activityId = "c1",
                    type = "heart",
                    userId = "user-1",
                    createdAt = Date(1500),
                ),
            )
        val comment = threadedCommentData(id = "c1", ownReactions = existingReactions)

        // New reaction from same user
        val newReaction =
            feedsReactionData(
                activityId = "c1",
                type = "smile",
                userId = "user-1",
                createdAt = Date(2000),
            )
        val update = commentData(id = "c1")

        setupInitialState(comment)
        state.onCommentReactionUpserted(update, newReaction, enforceUnique = true)

        val expected = comment.copy(ownReactions = listOf(newReaction))
        assertEquals(listOf(expected), state.comments.value)
    }

    @Test
    fun `onCommentReactionRemoved when it's a top-level comment, then remove reaction from it`() {
        val reaction = feedsReactionData(activityId = "c1", type = "like", userId = "user-2")
        val comment =
            threadedCommentData(
                id = "c1",
                createdAt = Date(1),
                latestReactions = listOf(reaction),
                reactionCount = 1,
                reactionGroups = mapOf("like" to reactionGroupData()),
            )
        val update = commentData(id = "c1", text = "Updated comment")

        setupInitialState(comment)
        state.onCommentReactionRemoved(update, reaction)

        val expected =
            comment.copy(
                text = "Updated comment",
                latestReactions = emptyList(),
                reactionCount = 0,
                reactionGroups = emptyMap(),
            )
        assertEquals(listOf(expected), state.comments.value)
    }

    @Test
    fun `onCommentReactionRemoved when it's a nested reply, then remove reaction from the reply`() {
        val reaction = feedsReactionData(activityId = "c2", type = "like", userId = "user-2")
        val reply =
            threadedCommentData(
                "c2",
                parentId = "c1",
                createdAt = Date(2),
                latestReactions = listOf(reaction),
                reactionCount = 1,
                reactionGroups = mapOf("like" to reactionGroupData()),
            )
        val parent = threadedCommentData(id = "c1", replies = listOf(reply))
        val update =
            commentData(id = "c2", parentId = "c1", text = "Updated reply", createdAt = Date(2))
        val expected =
            parent.copy(
                replies =
                    listOf(
                        reply.copy(
                            text = "Updated reply",
                            latestReactions = emptyList(),
                            reactionCount = 0,
                            reactionGroups = emptyMap(),
                        )
                    )
            )

        setupInitialState(parent)
        state.onCommentReactionRemoved(update, reaction)

        assertEquals(listOf(expected), state.comments.value)
    }

    @Test
    fun `on onActivityRemoved, clear all comments`() {
        val comment1 = threadedCommentData(id = "c1", createdAt = Date(1))
        val comment2 = threadedCommentData(id = "c2", createdAt = Date(2))
        val comment3 = threadedCommentData(id = "c3", createdAt = Date(3))

        setupInitialState(comment1, comment2, comment3)
        state.onActivityRemoved()

        assertEquals(emptyList<Any>(), state.comments.value)
    }

    private fun setupInitialState(vararg initialComments: ThreadedCommentData) {
        state.onQueryMoreComments(defaultPaginationResult(initialComments.toList()))
    }
}
