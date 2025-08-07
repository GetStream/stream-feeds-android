package io.getstream.feeds.android.sample.feed

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import io.getstream.feeds.android.client.api.model.ThreadedCommentData
import io.getstream.feeds.android.client.api.model.request.ActivityAddCommentRequest
import io.getstream.feeds.android.client.api.state.Activity
import io.getstream.feeds.android.client.api.state.ActivityState
import io.getstream.feeds.android.core.generated.models.AddCommentReactionRequest
import kotlinx.coroutines.launch

class CommentsSheetViewModel(
    private val activity: Activity
) : ViewModel() {

    private var canLoadMoreComments = true

    val state: ActivityState
        get() = activity.state

    init {
        viewModelScope.launch {
            activity.get()
                .logResult("Loading activity: ${activity.activityId}")
        }
    }

    fun onLoadMore() {
        if (!canLoadMoreComments) return
        viewModelScope.launch {
            activity.queryMoreComments()
                .onSuccess { comments -> canLoadMoreComments = comments.isNotEmpty() }
                .logResult("Loading more comments for activity: ${activity.activityId}")
        }
    }

    fun onLikeClick(comment: ThreadedCommentData) {
        viewModelScope.launch {
            if (comment.ownReactions.any { it.type == "heart" }) {
                activity.deleteCommentReaction(comment.id, "heart")
            } else {
                activity.addCommentReaction(comment.id, AddCommentReactionRequest("heart"))
            }.logResult("Toggling heart reaction for comment: ${comment.id}")
        }
    }

    fun onPostComment(text: String, replyParentId: String?) {
        viewModelScope.launch {
            activity.addComment(ActivityAddCommentRequest(comment = text, parentId = replyParentId))
                .logResult("Adding comment to activity: ${activity.activityId}")
        }
    }

    private fun <T> Result<T>.logResult(operation: String) {
        fold(
            onSuccess = { Log.d(TAG, "[Success] $operation") },
            onFailure = { Log.e(TAG, "[Failure] $operation", it) }
        )
    }

    class Factory(private val activity: Activity) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            CommentsSheetViewModel(activity) as T
    }

    companion object {
        private const val TAG = "CommentsSheetViewModel"
    }
}
