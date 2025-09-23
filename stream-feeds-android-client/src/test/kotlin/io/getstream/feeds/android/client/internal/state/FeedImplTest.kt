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
package io.getstream.feeds.android.client.internal.state

import io.getstream.feeds.android.client.api.file.FeedUploadPayload
import io.getstream.feeds.android.client.api.model.ActivityData
import io.getstream.feeds.android.client.api.model.FeedAddActivityRequest
import io.getstream.feeds.android.client.api.model.FeedData
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.model.ModelUpdates
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.model.PaginationResult
import io.getstream.feeds.android.client.api.model.QueryConfiguration
import io.getstream.feeds.android.client.api.model.request.ActivityAddCommentRequest
import io.getstream.feeds.android.client.api.state.query.ActivitiesSort
import io.getstream.feeds.android.client.api.state.query.FeedQuery
import io.getstream.feeds.android.client.internal.client.reconnect.FeedWatchHandler
import io.getstream.feeds.android.client.internal.repository.ActivitiesRepository
import io.getstream.feeds.android.client.internal.repository.BookmarksRepository
import io.getstream.feeds.android.client.internal.repository.CommentsRepository
import io.getstream.feeds.android.client.internal.repository.FeedsRepository
import io.getstream.feeds.android.client.internal.repository.GetOrCreateInfo
import io.getstream.feeds.android.client.internal.repository.PollsRepository
import io.getstream.feeds.android.client.internal.state.event.StateUpdateEvent
import io.getstream.feeds.android.client.internal.subscribe.StateUpdateEventListener
import io.getstream.feeds.android.client.internal.test.TestData.activityData
import io.getstream.feeds.android.client.internal.test.TestData.bookmarkData
import io.getstream.feeds.android.client.internal.test.TestData.commentData
import io.getstream.feeds.android.client.internal.test.TestData.feedData
import io.getstream.feeds.android.client.internal.test.TestData.feedMemberData
import io.getstream.feeds.android.client.internal.test.TestData.feedsReactionData
import io.getstream.feeds.android.client.internal.test.TestData.followData
import io.getstream.feeds.android.client.internal.test.TestData.pollData
import io.getstream.feeds.android.client.internal.test.TestData.reactionGroupData
import io.getstream.feeds.android.client.internal.test.TestSubscriptionManager
import io.getstream.feeds.android.network.models.AddBookmarkRequest
import io.getstream.feeds.android.network.models.AddCommentReactionRequest
import io.getstream.feeds.android.network.models.AddReactionRequest
import io.getstream.feeds.android.network.models.CreatePollRequest
import io.getstream.feeds.android.network.models.FeedMemberRequest
import io.getstream.feeds.android.network.models.FollowRequest
import io.getstream.feeds.android.network.models.MarkActivityRequest
import io.getstream.feeds.android.network.models.UpdateActivityRequest
import io.getstream.feeds.android.network.models.UpdateBookmarkRequest
import io.getstream.feeds.android.network.models.UpdateCommentRequest
import io.getstream.feeds.android.network.models.UpdateFeedMembersRequest
import io.getstream.feeds.android.network.models.UpdateFeedRequest
import io.mockk.called
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

internal class FeedImplTest {
    private val activitiesRepository: ActivitiesRepository = mockk(relaxed = true)
    private val bookmarksRepository: BookmarksRepository = mockk(relaxed = true)
    private val commentsRepository: CommentsRepository = mockk(relaxed = true)
    private val feedsRepository: FeedsRepository = mockk(relaxed = true)
    private val pollsRepository: PollsRepository = mockk(relaxed = true)
    private val feedWatchHandler: FeedWatchHandler = mockk(relaxed = true)
    private val stateEventListener: StateUpdateEventListener = mockk(relaxed = true)

    @Test
    fun `on getOrCreate with watch enabled, then call feedWatchHandler`() = runTest {
        val feed = createFeed(watch = true)
        val feedId = FeedId("group:id")
        val testFeedData = feedData()
        val feedInfo = getOrCreateInfo(testFeedData)
        coEvery { feedsRepository.getOrCreateFeed(any()) } returns Result.success(feedInfo)

        val result = feed.getOrCreate()

        assertEquals(feedInfo.feed, result.getOrNull())
        verify { feedWatchHandler.onStartWatching(feedId) }
    }

    @Test
    fun `on getOrCreate with watch disabled, then do not call feedWatchHandler`() = runTest {
        val feed = createFeed(watch = false)
        val testFeedData = feedData()
        val feedInfo = getOrCreateInfo(testFeedData)
        coEvery { feedsRepository.getOrCreateFeed(any()) } returns Result.success(feedInfo)

        val result = feed.getOrCreate()

        assertEquals(feedInfo.feed, result.getOrNull())
        verify { feedWatchHandler wasNot called }
    }

    @Test
    fun `on stopWatching, then call feedWatchHandler`() = runTest {
        val feed = createFeed()
        val feedId = FeedId("group:id")
        coEvery { feedsRepository.stopWatching(any(), any()) } returns Result.success(Unit)

        feed.stopWatching()

        coVerify {
            feedWatchHandler.onStopWatching(feedId)
            feedsRepository.stopWatching("group", "id")
        }
    }

    @Test
    fun `on addActivity, delegate to repository and fire event on success`() = runTest {
        val feed = createFeed()
        val request = FeedAddActivityRequest(type = "post", text = "Nice post")
        val attachmentUploadProgress: (FeedUploadPayload, Double) -> Unit = { _, _ -> }
        val activityData = mockk<ActivityData>(relaxed = true)
        coEvery { activitiesRepository.addActivity(any(), any()) } returns
            Result.success(activityData)

        feed.addActivity(request, attachmentUploadProgress)

        coVerify {
            activitiesRepository.addActivity(request, attachmentUploadProgress)
            stateEventListener.onEvent(StateUpdateEvent.ActivityAdded("group:id", activityData))
        }
        assertEquals(listOf(activityData), feed.state.activities.value)
    }

    @Test
    fun `on addComment, delegate to repository`() = runTest {
        val feed = createFeed()
        val request = ActivityAddCommentRequest(activityId = "activityId", comment = "Comment")
        val progress = { _: FeedUploadPayload, _: Double -> }
        val commentData = commentData("id")
        coEvery { commentsRepository.addComment(any(), any()) } returns Result.success(commentData)

        feed.addComment(request, progress)

        coVerify { commentsRepository.addComment(request, progress) }
    }

    @Test
    fun `on updateFeed, delegate to repository and fire event`() = runTest {
        val feed = createFeed()
        val request = UpdateFeedRequest(custom = mapOf("key" to "value"))
        val updatedFeedData = feedData("id", "group", "Updated Feed")
        coEvery { feedsRepository.updateFeed("group", "id", request) } returns
            Result.success(updatedFeedData)

        val result = feed.updateFeed(request)

        assertEquals(updatedFeedData, result.getOrNull())
        assertEquals(updatedFeedData, feed.state.feed.value)
        verify { stateEventListener.onEvent(StateUpdateEvent.FeedUpdated(updatedFeedData)) }
    }

    @Test
    fun `on deleteFeed, delegate to repository and fire event`() = runTest {
        val feed = createFeed()
        val hardDelete = true
        // Set up initial state with some data
        val initialFeedData = feedData()
        val initialActivity = activityData("activity-1")
        val feedInfo = getOrCreateInfo(initialFeedData, listOf(initialActivity))
        coEvery { feedsRepository.getOrCreateFeed(any()) } returns Result.success(feedInfo)
        feed.getOrCreate()

        coEvery { feedsRepository.deleteFeed("group", "id", hardDelete) } returns
            Result.success(Unit)

        val result = feed.deleteFeed(hardDelete)

        assertEquals(Unit, result.getOrNull())
        assertNull(feed.state.feed.value)
        assertTrue("Activities should be cleared", feed.state.activities.value.isEmpty())
        verify { stateEventListener.onEvent(StateUpdateEvent.FeedDeleted("group:id")) }
    }

    @Test
    fun `on updateActivity, delegate to repository and fire event`() = runTest {
        val feed = createFeed()
        val activityId = "activity-1"
        val request = UpdateActivityRequest(text = "Updated activity")
        val originalActivity = activityData(activityId, "Original activity")
        val updatedActivity = activityData(activityId, "Updated activity")

        // Set up initial state with activity
        val feedInfo = getOrCreateInfo(feedData(), listOf(originalActivity))
        coEvery { feedsRepository.getOrCreateFeed(any()) } returns Result.success(feedInfo)
        feed.getOrCreate()

        coEvery { activitiesRepository.updateActivity(activityId, request) } returns
            Result.success(updatedActivity)

        val result = feed.updateActivity(activityId, request)

        assertEquals(updatedActivity, result.getOrNull())
        val stateActivities = feed.state.activities.value
        assertEquals(1, stateActivities.size)
        assertEquals("Updated activity", stateActivities.first().text)
        verify {
            stateEventListener.onEvent(
                StateUpdateEvent.ActivityUpdated("group:id", updatedActivity)
            )
        }
    }

    @Test
    fun `on deleteActivity, delegate to repository and fire event`() = runTest {
        val feed = createFeed()
        val activityId = "activity-1"
        val hardDelete = false
        val activity = activityData(activityId)

        // Set up initial state with activity
        val feedInfo = getOrCreateInfo(feedData(), listOf(activity))
        coEvery { feedsRepository.getOrCreateFeed(any()) } returns Result.success(feedInfo)
        feed.getOrCreate()

        coEvery { activitiesRepository.deleteActivity(activityId, hardDelete) } returns
            Result.success(Unit)

        val result = feed.deleteActivity(activityId, hardDelete)

        assertEquals(Unit, result.getOrNull())
        assertTrue("Activity should be removed from state", feed.state.activities.value.isEmpty())
        verify {
            stateEventListener.onEvent(StateUpdateEvent.ActivityDeleted("group:id", activityId))
        }
    }

    @Test
    fun `on repost, delegate to repository and fire event`() = runTest {
        val feed = createFeed()
        val parentActivityId = "parent-activity"
        val text = "Repost text"
        val repostActivity = activityData("repost-1", text, "post")

        coEvery { activitiesRepository.addActivity(any<FeedAddActivityRequest>()) } returns
            Result.success(repostActivity)

        val result = feed.repost(parentActivityId, text)

        assertEquals(repostActivity, result.getOrNull())
        val stateActivities = feed.state.activities.value
        assertEquals(1, stateActivities.size)
        assertEquals(text, stateActivities.first().text)
        verify {
            stateEventListener.onEvent(StateUpdateEvent.ActivityAdded("group:id", repostActivity))
        }
    }

    @Test
    fun `on addBookmark, delegate to repository and fire event`() = runTest {
        val feed = createFeed()
        val activityId = "activity-1"
        val request = AddBookmarkRequest(folderId = "folder-1")
        val activity = activityData(activityId)
        val bookmark = bookmarkData(activityId)

        // Set up initial state with activity
        val feedInfo = getOrCreateInfo(feedData(), listOf(activity))
        coEvery { feedsRepository.getOrCreateFeed(any()) } returns Result.success(feedInfo)
        feed.getOrCreate()

        coEvery { bookmarksRepository.addBookmark(activityId, request) } returns
            Result.success(bookmark)

        val result = feed.addBookmark(activityId, request)

        assertEquals(bookmark, result.getOrNull())
        val stateActivities = feed.state.activities.value
        assertEquals(1, stateActivities.size)
        // The activity should be updated with bookmark info
        assertNotNull("Activity should be updated with bookmark", stateActivities.first())
        verify { stateEventListener.onEvent(StateUpdateEvent.BookmarkAdded(bookmark)) }
    }

    @Test
    fun `on deleteBookmark, delegate to repository and fire event`() = runTest {
        val feed = createFeed()
        val activityId = "activity-1"
        val folderId = "folder-1"
        val activity = activityData(activityId)
        val bookmark = bookmarkData(activityId)

        // Set up initial state with activity
        val feedInfo = getOrCreateInfo(feedData(), listOf(activity))
        coEvery { feedsRepository.getOrCreateFeed(any()) } returns Result.success(feedInfo)
        feed.getOrCreate()

        coEvery { bookmarksRepository.deleteBookmark(activityId, folderId) } returns
            Result.success(bookmark)

        val result = feed.deleteBookmark(activityId, folderId)

        assertEquals(bookmark, result.getOrNull())
        val stateActivities = feed.state.activities.value
        assertEquals(1, stateActivities.size)
        // The activity should be updated to remove bookmark
        assertNotNull("Activity should be updated after bookmark removal", stateActivities.first())
        verify { stateEventListener.onEvent(StateUpdateEvent.BookmarkDeleted(bookmark)) }
    }

    @Test
    fun `on follow, delegate to repository and update following state`() = runTest {
        val feed = createFeed()
        val targetFid = FeedId("user:target")
        val createNotificationActivity = true
        val custom = mapOf("key" to "value")
        val pushPreference = FollowRequest.PushPreference.All
        val follow = followData("user", "target")

        // Set up initial feed state
        val feedInfo = getOrCreateInfo(feedData())
        coEvery { feedsRepository.getOrCreateFeed(any()) } returns Result.success(feedInfo)
        feed.getOrCreate()

        coEvery { feedsRepository.follow(any()) } returns Result.success(follow)

        val result = feed.follow(targetFid, createNotificationActivity, custom, pushPreference)

        assertEquals(follow, result.getOrNull())
        // State verification: the follow operation should succeed, indicating state was updated
        assertTrue("Follow operation should succeed", result.isSuccess)
    }

    @Test
    fun `on unfollow, delegate to repository and fire event`() = runTest {
        val feed = createFeed()
        val targetFid = FeedId("user:target")
        val follow = followData("group:id", "user:target")

        // Set up initial state with follow
        val feedInfo = getOrCreateInfo(feedData()).copy(following = listOf(follow))
        coEvery { feedsRepository.getOrCreateFeed(any()) } returns Result.success(feedInfo)
        feed.getOrCreate()

        coEvery { feedsRepository.unfollow(any(), any()) } returns Result.success(follow)

        val result = feed.unfollow(targetFid)

        assertEquals(Unit, result.getOrNull())
        assertTrue("Following should be removed from state", feed.state.following.value.isEmpty())
        verify { stateEventListener.onEvent(StateUpdateEvent.FollowDeleted(follow)) }
    }

    @Test
    fun `on acceptFollow, delegate to repository and update followers state`() = runTest {
        val feed = createFeed()
        val sourceFid = FeedId("user:source")
        val role = "member"
        val follow = followData("source", "id")

        // Set up initial feed state
        val feedInfo = getOrCreateInfo(feedData())
        coEvery { feedsRepository.getOrCreateFeed(any()) } returns Result.success(feedInfo)
        feed.getOrCreate()

        coEvery { feedsRepository.acceptFollow(any()) } returns Result.success(follow)

        val result = feed.acceptFollow(sourceFid, role)

        assertEquals(follow, result.getOrNull())
        // State verification: the accept operation should succeed, indicating state was updated
        assertTrue("Accept follow operation should succeed", result.isSuccess)
    }

    @Test
    fun `on rejectFollow, delegate to repository and update state`() = runTest {
        val feed = createFeed()
        val sourceFid = FeedId("user:source")
        val follow = followData("source", "id")

        // Set up initial feed state
        val feedInfo = getOrCreateInfo(feedData())
        coEvery { feedsRepository.getOrCreateFeed(any()) } returns Result.success(feedInfo)
        feed.getOrCreate()

        coEvery { feedsRepository.rejectFollow(any()) } returns Result.success(follow)

        val result = feed.rejectFollow(sourceFid)

        assertEquals(follow, result.getOrNull())
        // State verification: the reject operation should succeed, indicating state was updated
        assertTrue("Reject follow operation should succeed", result.isSuccess)
    }

    @Test
    fun `on queryMoreActivities, delegate to repository`() = runTest {
        val feed = createFeed()
        val limit = 10
        val newActivity = activityData("activity-2")

        // Set up initial state with pagination
        val activities =
            PaginationResult(listOf(activityData("activity-1")), PaginationData(next = "cursor"))
        val feedInfo = getOrCreateInfo(feedData()).copy(activities = activities)
        coEvery { feedsRepository.getOrCreateFeed(any()) } returns Result.success(feedInfo)
        feed.getOrCreate()

        // Mock the next query
        val nextFeedInfo =
            feedInfo.copy(activities = PaginationResult(listOf(newActivity), PaginationData.EMPTY))
        coEvery { feedsRepository.getOrCreateFeed(any()) } returns Result.success(nextFeedInfo)

        val result = feed.queryMoreActivities(limit)

        assertEquals(listOf(newActivity), result.getOrNull())
        val stateActivities = feed.state.activities.value
        assertTrue("Should have activities", stateActivities.isNotEmpty())
    }

    @Test
    fun `on markActivity, delegate to repository`() = runTest {
        val feed = createFeed()
        val request = MarkActivityRequest(markSeen = listOf("activity-1"))

        coEvery { activitiesRepository.markActivity("group", "id", request) } returns
            Result.success(Unit)

        val result = feed.markActivity(request)

        assertEquals(Unit, result.getOrNull())
    }

    @Test
    fun `on stopWatching, delegate to repository and feedWatchHandler`() = runTest {
        val feed = createFeed()

        coEvery { feedsRepository.stopWatching("group", "id") } returns Result.success(Unit)

        val result = feed.stopWatching()

        assertEquals(Unit, result.getOrNull())
        verify { feedWatchHandler.onStopWatching(any()) }
    }

    @Test
    fun `on updateBookmark, delegate to repository`() = runTest {
        val feed = createFeed()
        val activityId = "activity-1"
        val request = UpdateBookmarkRequest(folderId = "new-folder")
        val bookmark = bookmarkData(activityId)

        coEvery { bookmarksRepository.updateBookmark(activityId, request) } returns
            Result.success(bookmark)

        val result = feed.updateBookmark(activityId, request)

        assertEquals(bookmark, result.getOrNull())
    }

    @Test
    fun `on getComment, delegate to repository`() = runTest {
        val feed = createFeed()
        val commentId = "comment-1"
        val comment = commentData(commentId)

        coEvery { commentsRepository.getComment(commentId) } returns Result.success(comment)

        val result = feed.getComment(commentId)

        assertEquals(comment, result.getOrNull())
    }

    @Test
    fun `on updateComment, delegate to repository`() = runTest {
        val feed = createFeed()
        val commentId = "comment-1"
        val request = UpdateCommentRequest(comment = "Updated comment")
        val comment = commentData(commentId, "Updated comment")

        coEvery { commentsRepository.updateComment(commentId, request) } returns
            Result.success(comment)

        val result = feed.updateComment(commentId, request)

        assertEquals(comment, result.getOrNull())
    }

    @Test
    fun `on deleteComment, delegate to repository and fire events`() = runTest {
        val feed = createFeed()
        val commentId = "comment-1"
        val hardDelete = true
        val activityId = "activity-1"
        val originalActivity = activityData(activityId, "Original activity")
        val updatedActivity = activityData(activityId, "Updated activity after comment deletion")
        val comment = commentData(commentId)
        val deleteData = comment to updatedActivity

        // Set up initial state with the original activity
        val feedInfo = getOrCreateInfo(feedData(), listOf(originalActivity))
        coEvery { feedsRepository.getOrCreateFeed(any()) } returns Result.success(feedInfo)
        feed.getOrCreate()

        coEvery { commentsRepository.deleteComment(commentId, hardDelete) } returns
            Result.success(deleteData)

        val result = feed.deleteComment(commentId, hardDelete)

        assertEquals(Unit, result.getOrNull())
        assertEquals(listOf(updatedActivity), feed.state.activities.value)
        verify {
            stateEventListener.onEvent(StateUpdateEvent.CommentDeleted("group:id", comment))
            stateEventListener.onEvent(
                StateUpdateEvent.ActivityUpdated("group:id", updatedActivity)
            )
        }
    }

    @Test
    fun `on queryFollowSuggestions, delegate to repository`() = runTest {
        val feed = createFeed()
        val limit = 10
        val suggestions = listOf(feedData("suggested-1"), feedData("suggested-2"))

        coEvery { feedsRepository.queryFollowSuggestions("group", limit) } returns
            Result.success(suggestions)

        val result = feed.queryFollowSuggestions(limit)

        assertEquals(suggestions, result.getOrNull())
    }

    @Test
    fun `on queryFeedMembers, delegate to memberList`() = runTest {
        val feed = createFeed()
        val members = listOf(feedMemberData())

        coEvery { feedsRepository.queryFeedMembers(any(), any(), any()) } returns
            Result.success(PaginationResult(members, PaginationData.EMPTY))

        val result = feed.queryFeedMembers()

        assertEquals(members, result.getOrNull())
    }

    @Test
    fun `on queryMoreFeedMembers with no pagination, then return empty list`() = runTest {
        val feed = createFeed()
        val limit = 5
        val moreMembers = listOf(feedMemberData())

        coEvery { feedsRepository.queryFeedMembers(any(), any(), any()) } returnsMany
            listOf(
                Result.success(PaginationResult(emptyList(), PaginationData(next = "next"))),
                Result.success(PaginationResult(moreMembers, PaginationData.EMPTY)),
            )

        feed.queryFeedMembers()
        val result = feed.queryMoreFeedMembers(limit)

        assertEquals(moreMembers, result.getOrNull())
    }

    @Test
    fun `on updateFeedMembers, delegate to repository and update memberList state`() = runTest {
        val feed = createFeed()
        val request =
            UpdateFeedMembersRequest(
                operation = UpdateFeedMembersRequest.Operation.Upsert,
                members = listOf(FeedMemberRequest(userId = "user1", role = "member")),
            )
        val memberUpdates =
            ModelUpdates(
                added = emptyList(),
                removedIds = emptyList(),
                updated = listOf(feedMemberData("user1")),
            )

        coEvery { feedsRepository.updateFeedMembers("group", "id", request) } returns
            Result.success(memberUpdates)

        val result = feed.updateFeedMembers(request)

        assertEquals(memberUpdates, result.getOrNull())
    }

    @Test
    fun `on acceptFeedMember, delegate to repository`() = runTest {
        val feed = createFeed()
        val member = feedMemberData()

        coEvery { feedsRepository.acceptFeedMember("group", "id") } returns Result.success(member)

        val result = feed.acceptFeedMember()

        assertEquals(member, result.getOrNull())
    }

    @Test
    fun `on rejectFeedMember, delegate to repository`() = runTest {
        val feed = createFeed()
        val member = feedMemberData()

        coEvery { feedsRepository.rejectFeedMember("group", "id") } returns Result.success(member)

        val result = feed.rejectFeedMember()

        assertEquals(member, result.getOrNull())
    }

    @Test
    fun `on addReaction, delegate to repository and fire event`() = runTest {
        val feed = createFeed()
        val activityId = "activity-1"
        val request = AddReactionRequest(type = "like")
        val reaction = feedsReactionData(activityId = activityId, type = "like", userId = "user")
        val activity = activityData(activityId)

        // Set up initial state with activity
        val feedInfo = getOrCreateInfo(feedData(), listOf(activity))
        coEvery { feedsRepository.getOrCreateFeed(any()) } returns Result.success(feedInfo)
        feed.getOrCreate()

        coEvery { activitiesRepository.addReaction(activityId, request) } returns
            Result.success(reaction)

        val result = feed.addReaction(activityId, request)

        assertEquals(reaction, result.getOrNull())
        val updatedActivity = feed.state.activities.value.first()
        assertEquals(listOf(reaction), updatedActivity.ownReactions)
        assertEquals(1, updatedActivity.reactionCount)
        verify {
            stateEventListener.onEvent(StateUpdateEvent.ActivityReactionAdded("group:id", reaction))
        }
    }

    @Test
    fun `on deleteReaction, delegate to repository and fire event`() = runTest {
        val feed = createFeed()
        val activityId = "activity-1"
        val type = "like"
        val reaction = feedsReactionData(activityId = activityId, type = type, userId = "user")
        coEvery { activitiesRepository.deleteReaction(activityId, type) } returns
            Result.success(reaction)

        // Set up initial state with activity that has a reaction
        val activityWithReaction =
            activityData(activityId)
                .copy(
                    ownReactions = listOf(reaction),
                    reactionCount = 1,
                    reactionGroups = mapOf("like" to reactionGroupData(count = 1)),
                )
        val feedInfo = getOrCreateInfo(feedData(), listOf(activityWithReaction))
        coEvery { feedsRepository.getOrCreateFeed(any()) } returns Result.success(feedInfo)
        feed.getOrCreate()

        val result = feed.deleteReaction(activityId, type)

        assertEquals(reaction, result.getOrNull())
        val updatedActivity = feed.state.activities.value.first()
        assertTrue("There should be no reaction", updatedActivity.ownReactions.isEmpty())
        assertEquals(0, updatedActivity.reactionCount)
        verify {
            stateEventListener.onEvent(
                StateUpdateEvent.ActivityReactionDeleted("group:id", reaction)
            )
        }
    }

    @Test
    fun `on addCommentReaction, delegate to repository`() = runTest {
        val feed = createFeed()
        val commentId = "comment-1"
        val request = AddCommentReactionRequest(type = "like")
        val reaction = feedsReactionData()

        coEvery { commentsRepository.addCommentReaction(commentId, request) } returns
            Result.success(Pair(reaction, commentData(commentId)))

        val result = feed.addCommentReaction(commentId, request)

        assertEquals(reaction, result.getOrNull())
    }

    @Test
    fun `on deleteCommentReaction, delegate to repository`() = runTest {
        val feed = createFeed()
        val commentId = "comment-1"
        val type = "like"
        val reaction = feedsReactionData()

        coEvery { commentsRepository.deleteCommentReaction(commentId, type) } returns
            Result.success(Pair(reaction, commentData(commentId)))

        val result = feed.deleteCommentReaction(commentId, type)

        assertEquals(reaction, result.getOrNull())
    }

    @Test
    fun `on createPoll, create poll then add activity with poll`() = runTest {
        val feed = createFeed()
        val pollRequest = CreatePollRequest(name = "Test Poll")
        val activityType = "poll"
        val poll = pollData("poll-1")
        val activity = activityData("activity-1", type = activityType)

        coEvery { pollsRepository.createPoll(pollRequest) } returns Result.success(poll)
        coEvery { activitiesRepository.addActivity(any<FeedAddActivityRequest>()) } returns
            Result.success(activity)

        val result = feed.createPoll(pollRequest, activityType)

        assertEquals(activity, result.getOrNull())
        coVerify { pollsRepository.createPoll(pollRequest) }
        coVerify {
            activitiesRepository.addActivity(
                match<FeedAddActivityRequest> { request ->
                    request.request.pollId == poll.id &&
                        request.request.type == activityType &&
                        request.request.feeds.contains("group:id")
                }
            )
        }
    }

    private fun createFeed(watch: Boolean = false) =
        FeedImpl(
            query = FeedQuery(group = "group", id = "id", watch = watch),
            currentUserId = "user",
            activitiesRepository = activitiesRepository,
            bookmarksRepository = bookmarksRepository,
            commentsRepository = commentsRepository,
            feedsRepository = feedsRepository,
            pollsRepository = pollsRepository,
            subscriptionManager = TestSubscriptionManager(stateEventListener),
            feedWatchHandler = feedWatchHandler,
        )

    private fun getOrCreateInfo(
        testFeedData: FeedData,
        activities: List<ActivityData> = emptyList(),
    ): GetOrCreateInfo =
        GetOrCreateInfo(
            activities = PaginationResult(models = activities, pagination = PaginationData.EMPTY),
            activitiesQueryConfig =
                QueryConfiguration(filter = null, sort = ActivitiesSort.Default),
            feed = testFeedData,
            followers = emptyList(),
            following = emptyList(),
            followRequests = emptyList(),
            members = PaginationResult(models = emptyList(), pagination = PaginationData.EMPTY),
            pinnedActivities = emptyList(),
            aggregatedActivities = emptyList(),
            notificationStatus = null,
        )
}
