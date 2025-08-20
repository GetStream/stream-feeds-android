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
package io.getstream.feeds.android.sample.feed

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.qualifiers.ApplicationContext
import io.getstream.feeds.android.client.api.file.FeedUploadPayload
import io.getstream.feeds.android.client.api.file.FileType
import io.getstream.feeds.android.client.api.model.ThreadedCommentData
import io.getstream.feeds.android.client.api.model.request.ActivityAddCommentRequest
import io.getstream.feeds.android.client.api.state.Activity
import io.getstream.feeds.android.client.api.state.ActivityState
import io.getstream.feeds.android.core.generated.models.AddCommentReactionRequest
import io.getstream.feeds.android.sample.util.copyToCache
import io.getstream.feeds.android.sample.util.deleteFiles
import io.getstream.feeds.android.sample.utils.logResult
import kotlinx.coroutines.launch

class CommentsSheetViewModel(
    private val activity: Activity,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private var canLoadMoreComments = true

    val state: ActivityState
        get() = activity.state

    init {
        viewModelScope.launch {
            activity.get().logResult(TAG, "Loading activity: ${activity.activityId}")
        }
    }

    fun onLoadMore() {
        if (!canLoadMoreComments) return
        viewModelScope.launch {
            activity
                .queryMoreComments()
                .onSuccess { comments -> canLoadMoreComments = comments.isNotEmpty() }
                .logResult(TAG, "Loading more comments for activity: ${activity.activityId}")
        }
    }

    fun onLikeClick(comment: ThreadedCommentData) {
        viewModelScope.launch {
            if (comment.ownReactions.any { it.type == "heart" }) {
                    activity.deleteCommentReaction(comment.id, "heart")
                } else {
                    val request = AddCommentReactionRequest("heart", createNotificationActivity = true)
                activity.addCommentReaction(comment.id, request)
                }
                .logResult(TAG, "Toggling heart reaction for comment: ${comment.id}")
        }
    }

    fun onPostComment(text: String, replyParentId: String?, attachments: List<Uri>) {
        viewModelScope.launch {
            val attachmentFiles =
                context.copyToCache(attachments).getOrElse { error ->
                    Log.e(TAG, "Failed to copy attachments", error)
                    return@launch
                }

            activity
                .addComment(
                    ActivityAddCommentRequest(
                        comment = text,
                        activityId = activity.activityId,
                        parentId = replyParentId,
                        createNotificationActivity = true,
                        attachmentUploads =
                            attachmentFiles.map {
                                FeedUploadPayload(file = it, type = FileType.Image("jpeg"))
                            },
                    ),
                    attachmentUploadProgress = { file, progress ->
                        Log.d(TAG, "Uploading attachment: ${file.type}, progress: $progress")
                    },
                )
                .logResult(TAG, "Adding comment to activity: ${activity.activityId}")

            deleteFiles(attachmentFiles)
        }
    }

    class Factory(private val activity: Activity, private val context: Context) :
        ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            CommentsSheetViewModel(activity, context) as T
    }

    companion object {
        private const val TAG = "CommentsSheetViewModel"
    }
}
