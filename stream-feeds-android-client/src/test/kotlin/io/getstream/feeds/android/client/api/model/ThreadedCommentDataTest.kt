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

import io.getstream.feeds.android.client.internal.test.TestData.feedsReactionData
import io.getstream.feeds.android.client.internal.test.TestData.reactionGroupData
import io.getstream.feeds.android.client.internal.test.TestData.threadedCommentData
import java.util.Date
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

internal class ThreadedCommentDataTest {

    private val currentUserId = "current-user"
    private val otherUserId = "other-user"
    private val activityId = "activity-1"

    // MARK: - addReaction Tests

    @Test
    fun `addReaction from another user - adds reaction and updates counts`() {
        // Given
        val comment = threadedCommentData(id = "comment-1", text = "Test comment")
        val reaction =
            feedsReactionData(
                activityId = activityId,
                type = "like",
                userId = otherUserId,
                createdAt = Date(2000),
                updatedAt = Date(2000),
            )

        // When
        val result = comment.addReaction(reaction, currentUserId)

        // Then
        assertEquals(1, result.latestReactions.size)
        assertEquals(reaction, result.latestReactions.first())
        assertEquals(1, result.reactionCount)
        assertEquals(1, result.reactionGroups["like"]?.count)
        assertEquals(Date(2000), result.reactionGroups["like"]?.firstReactionAt)
        assertEquals(Date(2000), result.reactionGroups["like"]?.lastReactionAt)
        assertTrue("Own reactions should remain empty", result.ownReactions.isEmpty())
    }

    @Test
    fun `addReaction from current user - inserts new reaction`() {
        // Given
        val comment = threadedCommentData(id = "comment-1", text = "Test comment")
        val reaction =
            feedsReactionData(
                activityId = activityId,
                type = "like",
                userId = currentUserId,
                createdAt = Date(2000),
                updatedAt = Date(2000),
            )

        // When
        val result = comment.addReaction(reaction, currentUserId)

        // Then
        assertEquals(1, result.latestReactions.size)
        assertEquals(reaction, result.latestReactions.first())
        assertEquals(1, result.reactionCount)
        assertEquals(1, result.reactionGroups["like"]?.count)
        assertEquals(1, result.ownReactions.size)
        assertEquals(reaction, result.ownReactions.first())
    }

    @Test
    fun `addReaction from current user - updates existing reaction without incrementing count`() {
        // Given
        val existingReaction =
            feedsReactionData(
                activityId = activityId,
                type = "like",
                userId = currentUserId,
                createdAt = Date(1000),
                updatedAt = Date(1000),
            )
        val comment =
            threadedCommentData(id = "comment-1", text = "Test comment")
                .copy(
                    latestReactions = listOf(existingReaction),
                    ownReactions = listOf(existingReaction),
                    reactionCount = 1,
                    reactionGroups =
                        mapOf(
                            "like" to
                                reactionGroupData(
                                    count = 1,
                                    firstReactionAt = Date(1000),
                                    lastReactionAt = Date(1000),
                                )
                        ),
                )

        val updatedReaction =
            feedsReactionData(
                activityId = activityId,
                type = "like",
                userId = currentUserId,
                createdAt = Date(2000),
                updatedAt = Date(2000),
            )

        // When
        val result = comment.addReaction(updatedReaction, currentUserId)

        // Then
        assertEquals(1, result.latestReactions.size)
        assertEquals(updatedReaction, result.latestReactions.first())
        assertEquals(1, result.reactionCount) // Count should not increase for update
        assertEquals(1, result.reactionGroups["like"]?.count) // Count should not increase
        assertEquals(1, result.ownReactions.size)
        assertEquals(updatedReaction, result.ownReactions.first())
    }

    @Test
    fun `addReaction creates new reaction group when none exists`() {
        // Given
        val comment = threadedCommentData(id = "comment-1", text = "Test comment")
        val reaction =
            feedsReactionData(
                activityId = activityId,
                type = "love",
                userId = otherUserId,
                createdAt = Date(3000),
                updatedAt = Date(3000),
            )

        // When
        val result = comment.addReaction(reaction, currentUserId)

        // Then
        assertEquals(1, result.reactionGroups.size)
        assertTrue(
            "Should contain 'love' reaction group",
            result.reactionGroups.containsKey("love"),
        )
        assertEquals(1, result.reactionGroups["love"]?.count)
        assertEquals(Date(3000), result.reactionGroups["love"]?.firstReactionAt)
        assertEquals(Date(3000), result.reactionGroups["love"]?.lastReactionAt)
    }

    @Test
    fun `addReaction increments existing reaction group`() {
        // Given
        val existingReaction =
            feedsReactionData(
                activityId = activityId,
                type = "like",
                userId = otherUserId,
                createdAt = Date(1000),
                updatedAt = Date(1000),
            )
        val comment =
            threadedCommentData(id = "comment-1", text = "Test comment")
                .copy(
                    latestReactions = listOf(existingReaction),
                    reactionCount = 1,
                    reactionGroups =
                        mapOf(
                            "like" to
                                reactionGroupData(
                                    count = 1,
                                    firstReactionAt = Date(1000),
                                    lastReactionAt = Date(1000),
                                )
                        ),
                )

        val newReaction =
            feedsReactionData(
                activityId = activityId,
                type = "like",
                userId = "another-user",
                createdAt = Date(2000),
                updatedAt = Date(2000),
            )

        // When
        val result = comment.addReaction(newReaction, currentUserId)

        // Then
        assertEquals(2, result.reactionCount)
        assertEquals(2, result.reactionGroups["like"]?.count)
        assertEquals(Date(1000), result.reactionGroups["like"]?.firstReactionAt)
        assertEquals(Date(2000), result.reactionGroups["like"]?.lastReactionAt)
    }

    // MARK: - removeReaction Tests

    @Test
    fun `removeReaction from another user - removes reaction and updates counts`() {
        // Given
        val reaction =
            feedsReactionData(
                activityId = activityId,
                type = "like",
                userId = otherUserId,
                createdAt = Date(1000),
                updatedAt = Date(1000),
            )
        val comment =
            threadedCommentData(id = "comment-1", text = "Test comment")
                .copy(
                    latestReactions = listOf(reaction),
                    reactionCount = 1,
                    reactionGroups =
                        mapOf(
                            "like" to
                                reactionGroupData(
                                    count = 1,
                                    firstReactionAt = Date(1000),
                                    lastReactionAt = Date(1000),
                                )
                        ),
                )

        // When
        val result = comment.removeReaction(reaction, currentUserId)

        // Then
        assertTrue("Latest reactions should be empty", result.latestReactions.isEmpty())
        assertEquals(0, result.reactionCount)
        assertFalse(
            "Reaction groups should not contain 'like'",
            result.reactionGroups.containsKey("like"),
        )
        assertTrue("Own reactions should remain empty", result.ownReactions.isEmpty())
    }

    @Test
    fun `removeReaction from current user - removes existing reaction`() {
        // Given
        val reaction =
            feedsReactionData(
                activityId = activityId,
                type = "like",
                userId = currentUserId,
                createdAt = Date(1000),
                updatedAt = Date(1000),
            )
        val comment =
            threadedCommentData(id = "comment-1", text = "Test comment")
                .copy(
                    latestReactions = listOf(reaction),
                    ownReactions = listOf(reaction),
                    reactionCount = 1,
                    reactionGroups =
                        mapOf(
                            "like" to
                                reactionGroupData(
                                    count = 1,
                                    firstReactionAt = Date(1000),
                                    lastReactionAt = Date(1000),
                                )
                        ),
                )

        // When
        val result = comment.removeReaction(reaction, currentUserId)

        // Then
        assertTrue("Latest reactions should be empty", result.latestReactions.isEmpty())
        assertEquals(0, result.reactionCount)
        assertFalse(
            "Reaction groups should not contain 'like'",
            result.reactionGroups.containsKey("like"),
        )
        assertTrue("Own reactions should be empty", result.ownReactions.isEmpty())
    }

    @Test
    fun `removeReaction from current user - non-existing reaction does not change state`() {
        // Given
        val existingReaction =
            feedsReactionData(
                activityId = activityId,
                type = "like",
                userId = currentUserId,
                createdAt = Date(1000),
                updatedAt = Date(1000),
            )
        val comment =
            threadedCommentData(id = "comment-1", text = "Test comment")
                .copy(
                    latestReactions = listOf(existingReaction),
                    ownReactions = listOf(existingReaction),
                    reactionCount = 1,
                    reactionGroups =
                        mapOf(
                            "like" to
                                reactionGroupData(
                                    count = 1,
                                    firstReactionAt = Date(1000),
                                    lastReactionAt = Date(1000),
                                )
                        ),
                )

        val nonExistingReaction =
            feedsReactionData(
                activityId = "different-activity",
                type = "love",
                userId = currentUserId,
                createdAt = Date(2000),
                updatedAt = Date(2000),
            )

        // When
        val result = comment.removeReaction(nonExistingReaction, currentUserId)

        // Then
        assertEquals(1, result.latestReactions.size)
        assertEquals(existingReaction, result.latestReactions.first())
        assertEquals(1, result.reactionCount)
        assertTrue(
            "Should still contain 'like' reaction group",
            result.reactionGroups.containsKey("like"),
        )
        assertEquals(1, result.reactionGroups["like"]?.count)
        assertEquals(1, result.ownReactions.size)
        assertEquals(existingReaction, result.ownReactions.first())
    }

    @Test
    fun `removeReaction removes empty reaction groups`() {
        // Given
        val reaction =
            feedsReactionData(
                activityId = activityId,
                type = "like",
                userId = otherUserId,
                createdAt = Date(1000),
                updatedAt = Date(1000),
            )
        val comment =
            threadedCommentData(id = "comment-1", text = "Test comment")
                .copy(
                    latestReactions = listOf(reaction),
                    reactionCount = 1,
                    reactionGroups =
                        mapOf(
                            "like" to
                                reactionGroupData(
                                    count = 1,
                                    firstReactionAt = Date(1000),
                                    lastReactionAt = Date(1000),
                                )
                        ),
                )

        // When
        val result = comment.removeReaction(reaction, currentUserId)

        // Then
        assertFalse(
            "Empty reaction group should be removed",
            result.reactionGroups.containsKey("like"),
        )
        assertEquals(0, result.reactionCount)
    }

    @Test
    fun `removeReaction decrements but preserves non-empty reaction groups`() {
        // Given
        val reaction1 =
            feedsReactionData(
                activityId = activityId,
                type = "like",
                userId = otherUserId,
                createdAt = Date(1000),
                updatedAt = Date(1000),
            )
        val reaction2 =
            feedsReactionData(
                activityId = activityId,
                type = "like",
                userId = "another-user",
                createdAt = Date(2000),
                updatedAt = Date(2000),
            )
        val comment =
            threadedCommentData(id = "comment-1", text = "Test comment")
                .copy(
                    latestReactions = listOf(reaction1, reaction2),
                    reactionCount = 2,
                    reactionGroups =
                        mapOf(
                            "like" to
                                reactionGroupData(
                                    count = 2,
                                    firstReactionAt = Date(1000),
                                    lastReactionAt = Date(2000),
                                )
                        ),
                )

        // When
        val result = comment.removeReaction(reaction1, currentUserId)

        // Then
        assertTrue(
            "Should still contain 'like' reaction group",
            result.reactionGroups.containsKey("like"),
        )
        assertEquals(1, result.reactionGroups["like"]?.count)
        assertEquals(1, result.reactionCount)
        assertEquals(1, result.latestReactions.size)
        assertEquals(reaction2, result.latestReactions.first())
    }

    @Test
    fun `removeReaction handles missing reaction group gracefully`() {
        // Given
        val reaction =
            feedsReactionData(
                activityId = activityId,
                type = "like",
                userId = otherUserId,
                createdAt = Date(1000),
                updatedAt = Date(1000),
            )
        val comment =
            threadedCommentData(id = "comment-1", text = "Test comment")
                .copy(
                    latestReactions = listOf(reaction),
                    reactionCount = 1,
                    // Note: no reaction groups map
                    reactionGroups = emptyMap(),
                )

        // When
        val result = comment.removeReaction(reaction, currentUserId)

        // Then
        assertTrue("Latest reactions should be empty", result.latestReactions.isEmpty())
        assertTrue("Reaction groups should remain empty", result.reactionGroups.isEmpty())
        assertTrue("Own reactions should remain empty", result.ownReactions.isEmpty())
    }

    @Test
    fun `removeReaction from current user does not affect count when reaction was not present`() {
        // Given
        val otherUserReaction =
            feedsReactionData(
                activityId = activityId,
                type = "like",
                userId = otherUserId,
                createdAt = Date(1000),
                updatedAt = Date(1000),
            )
        val comment =
            threadedCommentData(id = "comment-1", text = "Test comment")
                .copy(
                    latestReactions = listOf(otherUserReaction),
                    ownReactions = emptyList(), // Current user has no reactions
                    reactionCount = 1,
                    reactionGroups =
                        mapOf(
                            "like" to
                                reactionGroupData(
                                    count = 1,
                                    firstReactionAt = Date(1000),
                                    lastReactionAt = Date(1000),
                                )
                        ),
                )

        val nonExistingCurrentUserReaction =
            feedsReactionData(
                activityId = activityId,
                type = "like",
                userId = currentUserId,
                createdAt = Date(2000),
                updatedAt = Date(2000),
            )

        // When
        val result = comment.removeReaction(nonExistingCurrentUserReaction, currentUserId)

        // Then
        assertEquals(1, result.latestReactions.size)
        assertEquals(1, result.reactionCount)
        assertEquals(1, result.reactionGroups["like"]?.count)
        assertTrue("Own reactions should remain empty", result.ownReactions.isEmpty())
    }
}
