package io.getstream.feeds.android.sample.feed

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import io.getstream.feeds.android.client.api.model.request.ActivityAddCommentRequest
import io.getstream.feeds.android.client.api.state.Activity
import io.getstream.feeds.android.client.api.state.ActivityState
import kotlinx.coroutines.launch

class CommentsSheetViewModel(
    private val activity: Activity
) : ViewModel() {

    private var canLoadMoreComments = true

    val state: ActivityState
        get() = activity.state

    init {
        viewModelScope.launch {
            activity.get().fold(
                onSuccess = {
                    Log.d(TAG, "Activity loaded successfully ${activity.activityId}")
                },
                onFailure = {
                    Log.e(TAG, "Failed to load activity ${activity.activityId}", it)
                }
            )
        }
    }

    fun onLoadMore() {
        if (!canLoadMoreComments) return
        viewModelScope.launch {
            activity.queryMoreComments().fold(
                onSuccess = { comments ->
                    canLoadMoreComments = comments.isNotEmpty()
                    Log.d(TAG, "Loaded more comments for activity ${activity.activityId}: ${comments.size}")
                },
                onFailure = {
                    Log.e(TAG, "Failed to load more comments for activity ${activity.activityId}", it)
                }
            )
        }
    }

    fun onPostComment(text: String) {
        viewModelScope.launch {
            activity.addComment(ActivityAddCommentRequest(comment = text))
                .fold(
                    onSuccess = {
                        Log.d(TAG, "Comment added successfully to activity: ${activity.activityId}")
                    },
                    onFailure = {
                        Log.e(TAG, "Failed to add comment to activity: ${activity.activityId}", it)
                    }
                )
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
