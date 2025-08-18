package io.getstream.feeds.android.sample.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import io.getstream.feeds.android.client.api.model.ThreadedCommentData
import io.getstream.feeds.android.client.api.model.request.ActivityAddCommentRequest
import io.getstream.feeds.android.client.api.state.Activity
import io.getstream.feeds.android.client.api.state.ActivityState
import io.getstream.feeds.android.core.generated.models.AddCommentReactionRequest
import io.getstream.feeds.android.sample.utils.logResult
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
                .logResult(TAG, "Loading activity: ${activity.activityId}")
        }
    }

    fun onLoadMore() {
        if (!canLoadMoreComments) return
        viewModelScope.launch {
            activity.queryMoreComments()
                .onSuccess { comments -> canLoadMoreComments = comments.isNotEmpty() }
                .logResult(TAG, "Loading more comments for activity: ${activity.activityId}")
        }
    }

    fun onLikeClick(comment: ThreadedCommentData) {
        viewModelScope.launch {
            if (comment.ownReactions.any { it.type == "heart" }) {
                activity.deleteCommentReaction(comment.id, "heart")
            } else {
                activity.addCommentReaction(comment.id, AddCommentReactionRequest("heart"))
            }.logResult(TAG, "Toggling heart reaction for comment: ${comment.id}")
        }
    }

    fun onPostComment(text: String, replyParentId: String?) {
        viewModelScope.launch {
            activity.addComment(ActivityAddCommentRequest(comment = text, parentId = replyParentId))
                .logResult(TAG, "Adding comment to activity: ${activity.activityId}")
        }
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
