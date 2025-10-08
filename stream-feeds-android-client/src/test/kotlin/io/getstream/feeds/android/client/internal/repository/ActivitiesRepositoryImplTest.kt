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
package io.getstream.feeds.android.client.internal.repository

import io.getstream.feeds.android.client.api.file.FeedUploadPayload
import io.getstream.feeds.android.client.api.file.FeedUploader
import io.getstream.feeds.android.client.api.file.FileType
import io.getstream.feeds.android.client.api.file.UploadedFile
import io.getstream.feeds.android.client.api.model.ActivityData
import io.getstream.feeds.android.client.api.model.FeedAddActivityRequest
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.model.PaginationResult
import io.getstream.feeds.android.client.api.model.toModel
import io.getstream.feeds.android.client.api.state.query.ActivitiesQuery
import io.getstream.feeds.android.client.api.state.query.toRequest
import io.getstream.feeds.android.client.internal.repository.RepositoryTestUtils.testDelegation
import io.getstream.feeds.android.client.internal.test.TestData.activityResponse
import io.getstream.feeds.android.client.internal.test.TestData.feedsReactionResponse
import io.getstream.feeds.android.client.internal.test.TestData.pinActivityResponse
import io.getstream.feeds.android.client.internal.test.TestData.unpinActivityResponse
import io.getstream.feeds.android.network.apis.FeedsApi
import io.getstream.feeds.android.network.models.AddActivityRequest
import io.getstream.feeds.android.network.models.AddActivityResponse
import io.getstream.feeds.android.network.models.AddReactionRequest
import io.getstream.feeds.android.network.models.AddReactionResponse
import io.getstream.feeds.android.network.models.Attachment
import io.getstream.feeds.android.network.models.DeleteActivitiesRequest
import io.getstream.feeds.android.network.models.DeleteActivitiesResponse
import io.getstream.feeds.android.network.models.DeleteActivityReactionResponse
import io.getstream.feeds.android.network.models.DeleteActivityResponse
import io.getstream.feeds.android.network.models.GetActivityResponse
import io.getstream.feeds.android.network.models.MarkActivityRequest
import io.getstream.feeds.android.network.models.QueryActivitiesResponse
import io.getstream.feeds.android.network.models.QueryActivityReactionsRequest
import io.getstream.feeds.android.network.models.QueryActivityReactionsResponse
import io.getstream.feeds.android.network.models.UpdateActivityRequest
import io.getstream.feeds.android.network.models.UpdateActivityResponse
import io.getstream.feeds.android.network.models.UpsertActivitiesRequest
import io.getstream.feeds.android.network.models.UpsertActivitiesResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.io.File
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Test

internal class ActivitiesRepositoryImplTest {
    private val feedsApi: FeedsApi = mockk()
    private val uploader: FeedUploader = mockk()

    private val repository = ActivitiesRepositoryImpl(api = feedsApi, uploader = uploader)

    @Test
    fun `on addActivity, upload attachments and send api request`() = runTest {
        val attachmentUploads =
            listOf(
                FeedUploadPayload(File("1"), FileType.Image("jpg")),
                FeedUploadPayload(File("2"), FileType.Image("png")),
            )
        val request =
            FeedAddActivityRequest(
                request =
                    AddActivityRequest(
                        type = "post",
                        text = "Nice post",
                        attachments =
                            listOf(Attachment(imageUrl = "alreadyUploaded", type = "image")),
                    ),
                attachmentUploads = attachmentUploads,
            )
        val expectedAddActivityRequest =
            AddActivityRequest(
                type = "post",
                text = "Nice post",
                attachments =
                    listOf(
                        Attachment(imageUrl = "alreadyUploaded", type = "image"),
                        Attachment(assetUrl = "file/1", thumbUrl = "thumb/1"),
                        Attachment(assetUrl = "file/2", thumbUrl = "thumb/2"),
                    ),
            )

        coEvery { uploader.upload(any()) } answers
            {
                val name = firstArg<FeedUploadPayload>().file.name
                Result.success(UploadedFile(fileUrl = "file/$name", thumbnailUrl = "thumb/$name"))
            }

        repository.addActivity(request)

        attachmentUploads.forEach { localFile -> coVerify { uploader.upload(localFile) } }
        coVerify { feedsApi.addActivity(expectedAddActivityRequest) }
    }

    @Test
    fun `addActivity on upload error return failure`() = runTest {
        val attachmentUploads = listOf(FeedUploadPayload(File("some file"), FileType.Image("jpg")))
        val request =
            FeedAddActivityRequest(
                request = AddActivityRequest(type = "post", text = "Nice post"),
                attachmentUploads = attachmentUploads,
            )

        coEvery { uploader.upload(any()) } returns Result.failure(Exception("Upload failed"))

        val result = repository.addActivity(request)

        assertEquals("Upload failed", result.exceptionOrNull()?.message)
        coVerify(exactly = 0) { feedsApi.addActivity(any()) }
    }

    @Test
    fun `on deleteActivity, delegate to api`() {
        testDelegation(
            apiFunction = { feedsApi.deleteActivity("id", true) },
            repositoryCall = { repository.deleteActivity("id", true) },
            apiResult = DeleteActivityResponse("duration"),
            repositoryResult = Unit,
        )
    }

    @Test
    fun `on deleteActivities, delegate to api`() {
        val request = DeleteActivitiesRequest(listOf("id"))

        testDelegation(
            apiFunction = { feedsApi.deleteActivities(request) },
            repositoryCall = { repository.deleteActivities(request) },
            apiResult = DeleteActivitiesResponse("duration"),
        )
    }

    @Test
    fun `on getActivity, delegate to api`() {
        val apiResult = GetActivityResponse("duration", activityResponse())

        testDelegation(
            apiFunction = { feedsApi.getActivity("id") },
            repositoryCall = { repository.getActivity("id") },
            apiResult = apiResult,
            repositoryResult = apiResult.activity.toModel(),
        )
    }

    @Test
    fun `on updateActivity, delegate to api`() {
        val request = UpdateActivityRequest()
        val apiResult = UpdateActivityResponse("duration", activityResponse())

        testDelegation(
            apiFunction = { feedsApi.updateActivity("id", request) },
            repositoryCall = { repository.updateActivity("id", request) },
            apiResult = apiResult,
            repositoryResult = apiResult.activity.toModel(),
        )
    }

    @Test
    fun `on upsertActivities, delegate to api`() {
        testDelegation(
            apiFunction = { feedsApi.upsertActivities(UpsertActivitiesRequest()) },
            repositoryCall = { repository.upsertActivities(emptyList()) },
            apiResult = UpsertActivitiesResponse("duration", emptyList()),
            repositoryResult = emptyList<ActivityData>(),
        )
    }

    @Test
    fun `on pin, delegate to api`() {
        val apiResult = pinActivityResponse()

        testDelegation(
            apiFunction = {
                feedsApi.pinActivity(feedGroupId = "group", feedId = "feed", activityId = "id")
            },
            repositoryCall = { repository.pin("id", FeedId("group", "feed")) },
            apiResult = apiResult,
            repositoryResult = apiResult.activity.toModel(),
        )
    }

    @Test
    fun `on unpin, delegate to api`() {
        val apiResult = unpinActivityResponse()

        testDelegation(
            apiFunction = {
                feedsApi.unpinActivity(feedGroupId = "group", feedId = "feed", activityId = "id")
            },
            repositoryCall = { repository.unpin("id", FeedId("group", "feed")) },
            apiResult = apiResult,
            repositoryResult = apiResult.activity.toModel(),
        )
    }

    @Test
    fun `on markActivity, delegate to api`() {
        val request = MarkActivityRequest()
        testDelegation(
            apiFunction = {
                feedsApi.markActivity(
                    feedGroupId = "group",
                    feedId = "id",
                    markActivityRequest = request,
                )
            },
            repositoryCall = { repository.markActivity("group", "id", request) },
            apiResult = Unit,
            repositoryResult = Unit,
        )
    }

    @Test
    fun `on addActivityReaction, delegate to api`() {
        val request = AddReactionRequest("type")
        val apiResult = AddReactionResponse("duration", activityResponse(), feedsReactionResponse())

        testDelegation(
            apiFunction = { feedsApi.addActivityReaction("activityId", request) },
            repositoryCall = { repository.addActivityReaction("activityId", request) },
            apiResult = apiResult,
            repositoryResult = apiResult.reaction.toModel() to apiResult.activity.toModel(),
        )
    }

    @Test
    fun `on deleteActivityReaction, delegate to api`() {
        val apiResult =
            DeleteActivityReactionResponse("duration", activityResponse(), feedsReactionResponse())

        testDelegation(
            apiFunction = { feedsApi.deleteActivityReaction("activityId", "type") },
            repositoryCall = { repository.deleteActivityReaction("activityId", "type") },
            apiResult = apiResult,
            repositoryResult = apiResult.reaction.toModel() to apiResult.activity.toModel(),
        )
    }

    @Test
    fun `on queryActivityReactions, delegate to api`() {
        val request = QueryActivityReactionsRequest()

        testDelegation(
            apiFunction = { feedsApi.queryActivityReactions("activityId", request) },
            repositoryCall = { repository.queryActivityReactions("activityId", request) },
            apiResult = QueryActivityReactionsResponse(duration = "duration"),
            repositoryResult = PaginationResult(models = emptyList(), pagination = PaginationData()),
        )
    }

    @Test
    fun `on addActivity, delegate to api`() {
        val request = AddActivityRequest(type = "post", text = "Simple activity")
        val apiResult = AddActivityResponse("duration", activityResponse())

        testDelegation(
            apiFunction = { feedsApi.addActivity(request) },
            repositoryCall = { repository.addActivity(request) },
            apiResult = apiResult,
            repositoryResult = apiResult.activity.toModel(),
        )
    }

    @Test
    fun `on queryActivities, delegate to api and transform result`() {
        val query = ActivitiesQuery(limit = 10)
        val apiResult =
            QueryActivitiesResponse(
                duration = "duration",
                activities = listOf(activityResponse()),
                next = "next-cursor",
                prev = "prev-cursor",
            )

        testDelegation(
            apiFunction = { feedsApi.queryActivities(query.toRequest()) },
            repositoryCall = { repository.queryActivities(query) },
            apiResult = apiResult,
            repositoryResult =
                PaginationResult(
                    models = apiResult.activities.map { it.toModel() },
                    pagination = PaginationData(next = apiResult.next, previous = apiResult.prev),
                ),
        )
    }
}
