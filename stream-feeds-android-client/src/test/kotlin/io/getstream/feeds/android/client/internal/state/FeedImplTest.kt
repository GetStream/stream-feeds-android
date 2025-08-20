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
import io.getstream.feeds.android.client.api.model.request.ActivityAddCommentRequest
import io.getstream.feeds.android.client.api.state.query.FeedQuery
import io.getstream.feeds.android.client.internal.repository.ActivitiesRepository
import io.getstream.feeds.android.client.internal.repository.CommentsRepository
import io.getstream.feeds.android.client.internal.test.TestData.commentData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

internal class FeedImplTest {
    private val activitiesRepository: ActivitiesRepository = mockk(relaxed = true)
    private val commentsRepository: CommentsRepository = mockk(relaxed = true)

    private val feed =
        FeedImpl(
            query = FeedQuery(group = "group", id = "id"),
            currentUserId = "user",
            activitiesRepository = activitiesRepository,
            bookmarksRepository = mockk(),
            commentsRepository = commentsRepository,
            feedsRepository = mockk(),
            pollsRepository = mockk(),
            subscriptionManager = mockk(relaxed = true),
        )

    @Test
    fun `on addActivity, delegate to repository and notify state on success`() = runTest {
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
        val request = ActivityAddCommentRequest(activityId = "activityId", comment = "Comment")
        val progress = { _: FeedUploadPayload, _: Double -> }
        val commentData = commentData("id")
        coEvery { commentsRepository.addComment(any(), any()) } returns Result.success(commentData)

        feed.addComment(request, progress)

        coVerify { commentsRepository.addComment(request, progress) }
    }
}
