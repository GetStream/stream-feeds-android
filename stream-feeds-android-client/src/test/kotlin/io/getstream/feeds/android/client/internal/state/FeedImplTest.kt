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
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.model.PaginationResult
import io.getstream.feeds.android.client.api.model.QueryConfiguration
import io.getstream.feeds.android.client.api.model.request.ActivityAddCommentRequest
import io.getstream.feeds.android.client.api.state.query.ActivitiesSort
import io.getstream.feeds.android.client.api.state.query.FeedQuery
import io.getstream.feeds.android.client.internal.client.reconnect.FeedWatchHandler
import io.getstream.feeds.android.client.internal.repository.ActivitiesRepository
import io.getstream.feeds.android.client.internal.repository.CommentsRepository
import io.getstream.feeds.android.client.internal.repository.FeedsRepository
import io.getstream.feeds.android.client.internal.repository.GetOrCreateInfo
import io.getstream.feeds.android.client.internal.test.TestData.commentData
import io.getstream.feeds.android.client.internal.test.TestData.feedData
import io.mockk.called
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

internal class FeedImplTest {
    private val activitiesRepository: ActivitiesRepository = mockk(relaxed = true)
    private val commentsRepository: CommentsRepository = mockk(relaxed = true)
    private val feedsRepository: FeedsRepository = mockk(relaxed = true)
    private val feedWatchHandler: FeedWatchHandler = mockk(relaxed = true)

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
    fun `on addActivity, delegate to repository and notify state on success`() = runTest {
        val feed = createFeed()
        val request = FeedAddActivityRequest(type = "post", text = "Nice post")
        val attachmentUploadProgress: (FeedUploadPayload, Double) -> Unit = { _, _ -> }
        val activityData = mockk<ActivityData>(relaxed = true)
        coEvery { activitiesRepository.addActivity(any(), any()) } returns
            Result.success(activityData)

        feed.addActivity(request, attachmentUploadProgress)

        coVerify { activitiesRepository.addActivity(request, attachmentUploadProgress) }
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

    private fun createFeed(watch: Boolean = false) =
        FeedImpl(
            query = FeedQuery(group = "group", id = "id", watch = watch),
            currentUserId = "user",
            activitiesRepository = activitiesRepository,
            bookmarksRepository = mockk(),
            commentsRepository = commentsRepository,
            feedsRepository = feedsRepository,
            pollsRepository = mockk(),
            subscriptionManager = mockk(relaxed = true),
            feedWatchHandler = feedWatchHandler,
        )

    private fun getOrCreateInfo(testFeedData: FeedData): GetOrCreateInfo =
        GetOrCreateInfo(
            activities = PaginationResult(models = emptyList(), pagination = PaginationData.EMPTY),
            activitiesQueryConfig =
                QueryConfiguration(filter = null, sort = ActivitiesSort.Default),
            feed = testFeedData,
            followers = emptyList(),
            following = emptyList(),
            followRequests = emptyList(),
            members = PaginationResult(models = emptyList(), pagination = PaginationData.EMPTY),
            ownCapabilities = emptyList(),
            pinnedActivities = emptyList(),
            aggregatedActivities = emptyList(),
            notificationStatus = null,
        )
}
