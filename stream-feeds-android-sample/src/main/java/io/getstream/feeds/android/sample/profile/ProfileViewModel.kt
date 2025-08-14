package io.getstream.feeds.android.sample.profile

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ramcosta.composedestinations.generated.destinations.ProfileScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import io.getstream.feeds.android.client.api.model.FeedData
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.state.Feed
import io.getstream.feeds.android.sample.login.LoginManager
import io.getstream.feeds.android.sample.util.AsyncResource
import io.getstream.feeds.android.sample.util.map
import io.getstream.feeds.android.sample.util.notNull
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    loginManager: LoginManager,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val fid = ProfileScreenDestination.argsFrom(savedStateHandle).fid

    private val feed = flow {
        emit(AsyncResource.notNull(loginManager.currentState()?.client?.feed(fid)))
    }.stateIn(viewModelScope, SharingStarted.Eagerly, AsyncResource.Loading)

    val state = feed
        .map { loadingState -> loadingState.map(Feed::state) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, AsyncResource.Loading)

    private val _followSuggestions: MutableStateFlow<List<FeedData>> = MutableStateFlow(emptyList())
    val followSuggestions: StateFlow<List<FeedData>> = _followSuggestions.asStateFlow()

    init {
        onFeedLoaded {
            getOrCreate()
                .onSuccess {
                    queryFollowSuggestions(limit = 10)
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
        onFeedLoaded {
            follow(feedId)
                .onSuccess {
                    Log.d(TAG, "Successfully followed feed: $it")
                }
                .onFailure {
                    Log.e(TAG, "Failed to follow feed: $feedId", it)
                }
        }
    }

    private fun onFeedLoaded(block: suspend Feed.() -> Unit) {
        viewModelScope.launch {
            feed.filterIsInstance<AsyncResource.Content<Feed>>()
                .first()
                .data
                .block()
        }
    }

    companion object {
        private const val TAG = "ProfileViewModel"
    }
}
