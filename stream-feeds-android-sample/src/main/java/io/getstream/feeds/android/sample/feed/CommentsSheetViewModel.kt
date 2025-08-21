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
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ramcosta.composedestinations.generated.destinations.CommentsBottomSheetDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.getstream.feeds.android.client.api.file.FeedUploadPayload
import io.getstream.feeds.android.client.api.file.FileType
import io.getstream.feeds.android.client.api.model.ThreadedCommentData
import io.getstream.feeds.android.client.api.model.request.ActivityAddCommentRequest
import io.getstream.feeds.android.client.api.state.Activity
import io.getstream.feeds.android.core.generated.models.AddCommentReactionRequest
import io.getstream.feeds.android.sample.login.LoginManager
import io.getstream.feeds.android.sample.util.AsyncResource
import io.getstream.feeds.android.sample.util.copyToCache
import io.getstream.feeds.android.sample.util.deleteFiles
import io.getstream.feeds.android.sample.util.map
import io.getstream.feeds.android.sample.util.notNull
import io.getstream.feeds.android.sample.util.withFirstContent
import io.getstream.feeds.android.sample.utils.logResult
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CommentsSheetViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    loginManager: LoginManager,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val args = CommentsBottomSheetDestination.argsFrom(savedStateHandle)
    private var canLoadMoreComments = true

    private val activity = flow {
        val activity = loginManager.currentState()
            ?.client
            ?.activity(activityId = args.activityId, fid = args.fid)
        emit(AsyncResource.notNull(activity))
    }.stateIn(viewModelScope, SharingStarted.Eagerly, AsyncResource.Loading)

    val state = activity
        .map { loadingState -> loadingState.map(Activity::state) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, AsyncResource.Loading)

    init {
        activity.withFirstContent(viewModelScope) {
            get().logResult(TAG, "Loading activity: $activityId")
        }
    }

    fun onLoadMore() {
        if (!canLoadMoreComments) return
        activity.withFirstContent(viewModelScope) {
            queryMoreComments()
                .onSuccess { comments -> canLoadMoreComments = comments.isNotEmpty() }
                .logResult(TAG, "Loading more comments for activity: $activityId")
        }
    }

    fun onLikeClick(comment: ThreadedCommentData) {
        activity.withFirstContent(viewModelScope) {
            if (comment.ownReactions.any { it.type == "heart" }) {
                    deleteCommentReaction(comment.id, "heart")
                } else {
                    val request =
                        AddCommentReactionRequest("heart", createNotificationActivity = true)
                    activity.addCommentReaction(comment.id, request)
                }
                .logResult(TAG, "Toggling heart reaction for comment: ${comment.id}")
        }
    }

    fun onPostComment(text: String, replyParentId: String?, attachments: List<Uri>) {
        activity.withFirstContent(viewModelScope) {
            val attachmentFiles =
                context.copyToCache(attachments).getOrElse { error ->
                    Log.e(TAG, "Failed to copy attachments", error)
                    return@withFirstContent
                }

            addComment(
                ActivityAddCommentRequest(
                    comment = text,
                    activityId = activityId,
                    parentId = replyParentId,
                    createNotificationActivity = true,
                        attachmentUploads =
                            attachmentFiles.map {
                                FeedUploadPayload(file = it, type = FileType.Image("jpeg")
                        )
                    },
                ),
                attachmentUploadProgress = { file, progress ->
                    Log.d(TAG, "Uploading attachment: ${file.type}, progress: $progress")
                },
            )
                .logResult(TAG, "Adding comment to activity: $activityId")

            deleteFiles(attachmentFiles)
        }
    }

    companion object {
        private const val TAG = "CommentsSheetViewModel"
    }
}
