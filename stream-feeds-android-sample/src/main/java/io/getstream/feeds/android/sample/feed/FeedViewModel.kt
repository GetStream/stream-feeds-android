package io.getstream.feeds.android.sample.feed

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import io.getstream.feeds.android.client.api.FeedsClient
import io.getstream.feeds.android.client.api.model.ActivityData
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.model.FeedInputData
import io.getstream.feeds.android.client.api.model.FeedMemberRequestData
import io.getstream.feeds.android.client.api.model.FeedVisibility
import io.getstream.feeds.android.client.api.state.FeedState
import io.getstream.feeds.android.client.api.state.query.FeedQuery
import io.getstream.feeds.android.core.generated.models.AddActivityRequest
import io.getstream.feeds.android.core.generated.models.AddReactionRequest
import io.getstream.feeds.android.core.generated.models.UpdateActivityRequest
import kotlinx.coroutines.launch

class FeedViewModel(
    private val currentUserId: String,
    private val fid: FeedId,
    private val feedsClient: FeedsClient,
) : ViewModel() {

    private val query = FeedQuery(
        fid = fid,
        data = FeedInputData(
            members = listOf(FeedMemberRequestData(currentUserId)),
            visibility = FeedVisibility.Public,
        )
    )
    private val feed = feedsClient.feed(query)

    val state: FeedState
        get() = feed.state

    init {
        viewModelScope.launch {
            feed.getOrCreate()
                .onSuccess {
                    Log.d(TAG, "Feed created or retrieved successfully: $fid")
                }
                .onFailure {
                    // Handle error
                    Log.e(TAG, "Failed to get or create feed: $fid, $it")
                }
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
                feed.addReaction(activity.id, AddReactionRequest("heart"))
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

    fun onCreatePost(text: String) {
        viewModelScope.launch {
            feed.addActivity(
                AddActivityRequest(
                    type = "activity",
                    text = text,
                    fids = listOf(fid.rawValue)
                )
            )
                .onSuccess {
                    Log.d(TAG, "Activity created successfully: ${it.id}")
                }
                .onFailure {
                    Log.e(TAG, "Failed to create activity: $it")
                }
        }
    }

    companion object {

        private const val TAG = "FeedViewModel"
    }
}

class FeedViewModelFactory(
    private val currentUserId: String,
    private val fid: FeedId,
    private val feedsClient: FeedsClient
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FeedViewModel(currentUserId, fid, feedsClient) as T
    }
}