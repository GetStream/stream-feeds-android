package io.getstream.feeds.android.client.internal.state

import io.getstream.feeds.android.client.api.file.FeedUploadPayload
import io.getstream.feeds.android.client.api.model.ActivityData
import io.getstream.feeds.android.client.api.model.FeedAddActivityRequest
import io.getstream.feeds.android.client.api.state.query.FeedQuery
import io.getstream.feeds.android.client.internal.repository.ActivitiesRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

internal class FeedImplTest {
    private val activitiesRepository: ActivitiesRepository = mockk(relaxed = true)

    private val feed = FeedImpl(
        query = FeedQuery(group = "group", id = "id"),
        currentUserId = "user",
        activitiesRepository = activitiesRepository,
        bookmarksRepository = mockk(),
        commentsRepository = mockk(),
        feedsRepository = mockk(),
        pollsRepository = mockk(),
        subscriptionManager = mockk(relaxed = true)
    )

    @Test
    fun `on addActivity, delegate to repository and notify state on success`() = runTest {
        val request = FeedAddActivityRequest(type = "post", text = "Nice post")
        val attachmentUploadProgress: (FeedUploadPayload, Double) -> Unit = { _, _ -> }
        val activityData = mockk<ActivityData>(relaxed = true)
        coEvery { activitiesRepository.addActivity(any(), any()) } returns Result.success(activityData)

        feed.addActivity(request, attachmentUploadProgress)

        coVerify { activitiesRepository.addActivity(request, attachmentUploadProgress) }
        assertEquals(listOf(activityData), feed.state.activities.value)
    }
}
