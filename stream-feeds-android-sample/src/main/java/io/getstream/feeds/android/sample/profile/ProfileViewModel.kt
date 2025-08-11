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
import io.getstream.feeds.android.client.api.state.FeedState
import io.getstream.feeds.android.sample.login.LoginManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    loginManager: LoginManager,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val fid = ProfileScreenDestination.argsFrom(savedStateHandle).fid
    private val feed: Feed? = loginManager.state?.client?.feed(fid)
    public val state: FeedState? = feed?.state

    private val _followSuggestions: MutableStateFlow<List<FeedData>> = MutableStateFlow(emptyList())
    public val followSuggestions: StateFlow<List<FeedData>> = _followSuggestions.asStateFlow()

    init {
        executeFeedOp {
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
        executeFeedOp {
            follow(feedId)
                .onSuccess {
                    Log.d(TAG, "Successfully followed feed: $it")
                }
                .onFailure {
                    Log.e(TAG, "Failed to follow feed: $feedId", it)
                }
        }
    }

    private fun executeFeedOp(block: suspend Feed.() -> Unit) {
        if (feed != null) {
            viewModelScope.launch { block(feed) }
        }
    }

    companion object {
        private const val TAG = "ProfileViewModel"
    }
}
