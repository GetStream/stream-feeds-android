/*
 * Copyright (c) 2014-2026 Stream.io Inc. All rights reserved.
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
import io.getstream.feeds.android.client.api.FeedsClient
import io.getstream.feeds.android.client.api.file.FeedUploadPayload
import io.getstream.feeds.android.client.api.file.FileType
import io.getstream.feeds.android.client.api.model.ThreadedCommentData
import io.getstream.feeds.android.client.api.model.request.ActivityAddCommentRequest
import io.getstream.feeds.android.client.api.state.Activity
import io.getstream.feeds.android.network.models.AddCommentReactionRequest
import io.getstream.feeds.android.network.models.UpdateCommentRequest
import io.getstream.feeds.android.sample.login.LoginManager
import io.getstream.feeds.android.sample.util.AsyncResource
import io.getstream.feeds.android.sample.util.copyToCache
import io.getstream.feeds.android.sample.util.deleteFiles
import io.getstream.feeds.android.sample.util.notNull
import io.getstream.feeds.android.sample.util.withFirstContent
import io.getstream.feeds.android.sample.utils.logResult
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class CommentsSheetViewModel
@Inject
constructor(
    @param:ApplicationContext private val context: Context,
    loginManager: LoginManager,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val args = CommentsBottomSheetDestination.argsFrom(savedStateHandle)
    private var canLoadMoreComments = true

    val state =
        flow { emit(AsyncResource.notNull(loginManager.currentClient()?.let(::getState))) }
            .stateIn(viewModelScope, SharingStarted.Eagerly, AsyncResource.Loading)

    private var replyParentId: String? = null
    private val _createContentState = MutableStateFlow(CreateContentState.Hidden)
    val createContentState: StateFlow<CreateContentState> = _createContentState.asStateFlow()

    init {
        state.withFirstContent(viewModelScope) {
            activity.get().logResult(TAG, "Loading activity: ${activity.activityId}")
        }
    }

    fun onEvent(event: Event) {
        when (event) {
            Event.OnScrollToBottom -> loadMore()
            is Event.OnEdit -> edit(id = event.commentId, text = event.text)
            is Event.OnPost -> post(event.text, replyParentId, event.attachments)
            is Event.OnLike -> toggleLike(event.comment)
            is Event.OnDelete -> delete(event.commentId)
            Event.OnAddContent -> showAddContent(true)
            Event.OnContentCreateDismiss -> showAddContent(false)
            is Event.OnReply -> showAddContent(true, event.parentId)
        }
    }

    private fun loadMore() {
        if (!canLoadMoreComments) return
        state.withFirstContent(viewModelScope) {
            activity
                .queryMoreComments()
                .onSuccess { comments -> canLoadMoreComments = comments.isNotEmpty() }
                .logResult(TAG, "Loading more comments for activity: ${activity.activityId}")
        }
    }

    private fun toggleLike(comment: ThreadedCommentData) {
        state.withFirstContent(viewModelScope) {
            if (comment.ownReactions.any { it.type == "heart" }) {
                    activity.deleteCommentReaction(comment.id, "heart")
                } else {
                    val request =
                        AddCommentReactionRequest("heart", createNotificationActivity = true)
                    activity.addCommentReaction(comment.id, request)
                }
                .logResult(TAG, "Toggling heart reaction for comment: ${comment.id}")
        }
    }

    private fun delete(commentId: String) {
        state.withFirstContent(viewModelScope) {
            activity.deleteComment(commentId).logResult(TAG, "Deleting comment: $commentId")
        }
    }

    private fun edit(id: String, text: String) {
        state.withFirstContent(viewModelScope) {
            activity
                .updateComment(id, UpdateCommentRequest(text))
                .logResult(TAG, "Editing comment $id with text: $text")
        }
    }

    private fun showAddContent(show: Boolean, replyParentId: String? = null) {
        _createContentState.value =
            if (show) CreateContentState.Composing else CreateContentState.Hidden
        this.replyParentId = replyParentId
    }

    private fun post(text: String, replyParentId: String?, attachments: List<Uri>) {
        _createContentState.value = CreateContentState.Posting

        state.withFirstContent(viewModelScope) {
            val attachmentFiles =
                context.copyToCache(attachments).getOrElse { error ->
                    Log.e(TAG, "Failed to copy attachments", error)
                    _createContentState.value = CreateContentState.Composing
                    return@withFirstContent
                }

            val result =
                activity
                    .addComment(
                        ActivityAddCommentRequest(
                            comment = text,
                            activityId = activity.activityId,
                            parentId = replyParentId,
                            createNotificationActivity = true,
                            attachmentUploads =
                                attachmentFiles.map {
                                    FeedUploadPayload(file = it, type = FileType.Image)
                                },
                        ),
                        attachmentUploadProgress = { file, progress ->
                            Log.d(TAG, "Uploading attachment: ${file.type}, progress: $progress")
                        },
                    )
                    .logResult(TAG, "Adding comment to activity: ${activity.activityId}")

            deleteFiles(attachmentFiles)

            _createContentState.value =
                result.fold(
                    onSuccess = { CreateContentState.Hidden },
                    onFailure = { CreateContentState.Composing },
                )
        }
    }

    private fun getState(client: FeedsClient): ViewState =
        ViewState(
            userId = client.user.id,
            activity = client.activity(activityId = args.activityId, fid = args.fid),
        )

    data class ViewState(val userId: String, val activity: Activity)

    sealed interface Event {
        data object OnScrollToBottom : Event

        data object OnAddContent : Event

        data class OnLike(val comment: ThreadedCommentData) : Event

        data class OnDelete(val commentId: String) : Event

        data class OnEdit(val commentId: String, val text: String) : Event

        data class OnPost(val text: String, val attachments: List<Uri>) : Event

        data object OnContentCreateDismiss : Event

        data class OnReply(val parentId: String) : Event
    }

    companion object {
        private const val TAG = "CommentsSheetViewModel"
    }
}
