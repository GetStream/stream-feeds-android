package io.getstream.feeds.android.sample.feed

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import io.getstream.android.core.result.runSafely
import io.getstream.feeds.android.client.api.FeedsClient
import io.getstream.feeds.android.client.api.file.FeedUploadContext
import io.getstream.feeds.android.client.api.file.FeedUploadPayload
import io.getstream.feeds.android.client.api.file.FileType
import io.getstream.feeds.android.client.api.model.ActivityData
import io.getstream.feeds.android.client.api.model.FeedAddActivityRequest
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.model.FeedInputData
import io.getstream.feeds.android.client.api.model.FeedMemberRequestData
import io.getstream.feeds.android.client.api.model.FeedVisibility
import io.getstream.feeds.android.client.api.state.FeedState
import io.getstream.feeds.android.client.api.state.query.FeedQuery
import io.getstream.feeds.android.core.generated.models.AddActivityRequest
import io.getstream.feeds.android.core.generated.models.AddReactionRequest
import io.getstream.feeds.android.core.generated.models.UpdateActivityRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class FeedViewModel(
    private val currentUserId: String,
    private val fid: FeedId,
    private val feedsClient: FeedsClient,
    private val application: Application
) : ViewModel() {

    // User feed
    private val query = FeedQuery(
        fid = fid,
        data = FeedInputData(
            members = listOf(FeedMemberRequestData(currentUserId)),
            visibility = FeedVisibility.Followers,
        )
    )
    private val feed = feedsClient.feed(query)

    // Notification feed
    private val notificationFid = FeedId("notification", currentUserId)
    private val notificationFeed = feedsClient.feed(notificationFid)

    val state: FeedState
        get() = feed.state

    val notificationState: FeedState
        get() = notificationFeed.state

    init {
        viewModelScope.launch {
            feed.getOrCreate()
        }
        viewModelScope.launch {
            notificationFeed.getOrCreate()
        }
    }

    fun onLoadMore() {
        if (!state.canLoadMoreActivities) return
        viewModelScope.launch {
            feed.queryMoreActivities()
                .onSuccess {
                    Log.d(TAG, "Loaded more activities for feed: $fid")
                }
                .onFailure {
                    Log.e(TAG, "Failed to load more activities for feed: $fid, $it")
                }
        }
    }

    fun onHeartClick(activity: ActivityData) {
        if (activity.ownReactions.isEmpty()) {
            // Add 'heart' reaction
            viewModelScope.launch {
                val request = AddReactionRequest("heart", createNotificationActivity = true)
                feed.addReaction(activity.id, request)
            }
        } else {
            // Remove 'heart' reaction
            viewModelScope.launch {
                feed.deleteReaction(activity.id, "heart")
            }
        }
    }

    fun onRepostClick(activity: ActivityData, text: String?) {
        viewModelScope.launch {
            feed.repost(activity.id, text = text)
        }
    }

    fun onBookmarkClick(activity: ActivityData) {
        if (activity.ownBookmarks.isEmpty()) {
            // Add bookmark
            viewModelScope.launch {
                feed.addBookmark(activity.id)
            }
        } else {
            // Remove bookmark
            viewModelScope.launch {
                feed.deleteBookmark(activity.id)
            }
        }
    }

    fun onDeleteClick(activityId: String) {
        viewModelScope.launch {
            feed.deleteActivity(activityId)
                .onSuccess {
                    Log.d(TAG, "Activity deleted successfully: $activityId")
                }
                .onFailure {
                    Log.e(TAG, "Failed to delete activity: $activityId, $it")
                }
        }
    }

    fun onEditActivity(activityId: String, text: String) {
        viewModelScope.launch {
            feed.updateActivity(activityId, UpdateActivityRequest(text = text))
                .onSuccess {
                    Log.d(TAG, "Activity updated successfully: $activityId")
                }
                .onFailure {
                    Log.e(TAG, "Failed to update activity: $activityId, $it")
                }
        }
    }

    fun onCreatePost(text: String, attachments: List<Uri>) {
        viewModelScope.launch {
            val attachmentFiles = copyToFiles(attachments).getOrElse { error ->
                Log.e(TAG, "Failed to copy attachments", error)
                return@launch
            }

            feed.addActivity(
                FeedAddActivityRequest(
                    AddActivityRequest(
                        type = "activity",
                        text = text,
                        fids = listOf(fid.rawValue)
                    ),
                    attachmentUploads = attachmentFiles.map {
                        FeedUploadPayload(it, FileType.Image("jpeg"), FeedUploadContext(fid))
                    }
                ),
                attachmentUploadProgress = { file, progress ->
                    Log.d(TAG, "Uploading attachment: ${file.type}, progress: $progress")
                }
            )
                .onSuccess {
                    Log.d(TAG, "Activity created successfully: ${it.id}")
                }
                .onFailure {
                    Log.e(TAG, "Failed to create activity: $it")
                }

            deleteFiles(attachmentFiles)
        }
    }

    private suspend fun copyToFiles(uris: List<Uri>): Result<List<File>> {
        val files = mutableListOf<File>()
        for (uri in uris) {
            try {
                val file = copyToFile(uri)
                files.add(file)
            } catch (e: Exception) {
                Log.e(TAG, "Error copying file from URI: $uri", e)
                deleteFiles(files)
                return Result.failure(e)
            }
        }

        return Result.success(files)
    }

    private suspend fun copyToFile(uri: Uri) = withContext(Dispatchers.IO) {
        val outputFile = File(application.cacheDir, "attachment_${System.currentTimeMillis()}.tmp")

        application.contentResolver.openInputStream(uri).use { inputStream ->
            checkNotNull(inputStream) { "Error opening input stream for URI: $uri" }

            FileOutputStream(outputFile).use(inputStream::copyTo)
        }
        outputFile
    }


    private suspend fun deleteFiles(files: List<File>) = runSafely {
        withContext(Dispatchers.IO) {
            files.forEach(File::delete)
        }
    }

    companion object {

        private const val TAG = "FeedViewModel"
    }
}

fun feedViewModelFactory(
    currentUserId: String,
    fid: FeedId,
    feedsClient: FeedsClient
) = viewModelFactory {
    initializer {
        FeedViewModel(
            currentUserId = currentUserId,
            fid = fid,
            feedsClient = feedsClient,
            application = checkNotNull(this[APPLICATION_KEY])
        )
    }
}
