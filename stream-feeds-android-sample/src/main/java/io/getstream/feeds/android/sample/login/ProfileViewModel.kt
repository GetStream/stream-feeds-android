package io.getstream.feeds.android.sample.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import io.getstream.feeds.android.client.api.FeedsClient
import io.getstream.feeds.android.client.api.model.FeedData
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.state.Feed
import io.getstream.feeds.android.client.api.state.FeedState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val fid: FeedId,
    private val feedsClient: FeedsClient,
    private val feed: Feed = feedsClient.feed(fid),
): ViewModel() {

    public val state: FeedState = feed.state

    private val _followSuggestions: MutableStateFlow<List<FeedData>> = MutableStateFlow(emptyList())
    public val followSuggestions: StateFlow<List<FeedData>> = _followSuggestions.asStateFlow()

    init {
        viewModelScope.launch {
            feed.getOrCreate()
                .onSuccess {
                    feed.queryFollowSuggestions(limit = 10)
                        .onSuccess {
                            _followSuggestions.value = it
                        }
                }
                .onFailure {
                    Log.e(TAG, "Failed to get or create feed: $fid", it)
                }
        }
    }

    fun follow(feedId: FeedId) {
        viewModelScope.launch {
            feed.follow(feedId)
                .onSuccess {
                    Log.d(TAG, "Successfully followed feed: $it")
                }
                .onFailure {
                    Log.e(TAG, "Failed to follow feed: $feedId", it)
                }
        }
    }

    companion object {
        private const val TAG = "ProfileViewModel"
    }
}

class ProfileViewModelFactory(
    private val fid: FeedId,
    private val feedsClient: FeedsClient
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ProfileViewModel(fid, feedsClient) as T
    }
}
