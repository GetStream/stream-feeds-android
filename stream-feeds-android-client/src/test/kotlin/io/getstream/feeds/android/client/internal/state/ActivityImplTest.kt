package io.getstream.feeds.android.client.internal.state

import io.getstream.feeds.android.client.api.file.FeedUploadPayload
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.model.ThreadedCommentData
import io.getstream.feeds.android.client.api.model.request.ActivityAddCommentRequest
import io.getstream.feeds.android.client.internal.common.StreamSubscriptionManager
import io.getstream.feeds.android.client.internal.repository.ActivitiesRepository
import io.getstream.feeds.android.client.internal.repository.CommentsRepository
import io.getstream.feeds.android.client.internal.repository.PollsRepository
import io.getstream.feeds.android.client.internal.socket.FeedsSocketListener
import io.getstream.feeds.android.client.internal.test.TestData.commentData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

internal class ActivityImplTest {
    private val activitiesRepository: ActivitiesRepository = mockk()
    private val commentsRepository: CommentsRepository = mockk()
    private val pollsRepository: PollsRepository = mockk()
    private val commentListState = mockk<ActivityCommentListMutableState>(relaxed = true)
    private val activityCommentListImpl: ActivityCommentListImpl = mockk {
        every { state } returns commentListState
        every { mutableState } returns commentListState
    }
    private val subscriptionManager: StreamSubscriptionManager<FeedsSocketListener> = mockk(relaxed = true)

    private val activity = ActivityImpl(
        activityId = "activityId",
        fid = FeedId("feedId"),
        currentUserId = "currentUserId",
        activitiesRepository = activitiesRepository,
        commentsRepository = commentsRepository,
        pollsRepository = pollsRepository,
        commentList = activityCommentListImpl,
        subscriptionManager = subscriptionManager
    )

    @Test
    fun `on addComment, delegate to repository and notify state`() = runTest {
        val request = ActivityAddCommentRequest(activityId = "activityId", comment = "Comment")
        val progress = { _: FeedUploadPayload, _: Double -> }
        val commentData = commentData("id")
        coEvery { commentsRepository.addComment(any(), any()) } returns Result.success(commentData)

        activity.addComment(request, progress)

        coVerify {
            commentsRepository.addComment(request, progress)
            commentListState.onCommentAdded(ThreadedCommentData(commentData))
        }
    }

    @Test
    fun `on addCommentsBatch, delegate to repository and notify state`() = runTest {
        val requests = listOf(
            ActivityAddCommentRequest(activityId = "activityId", comment = "Comment 1"),
            ActivityAddCommentRequest(activityId = "activityId", comment = "Comment 2"),
        )
        val commentData = listOf(commentData("id1"), commentData("id2"))
        coEvery { commentsRepository.addCommentsBatch(requests) } returns Result.success(commentData)

        activity.addCommentsBatch(requests)

        coVerify {
            commentsRepository.addCommentsBatch(requests)
            commentData.forEach { data -> commentListState.onCommentAdded(ThreadedCommentData(data)) }
        }
    }
}
