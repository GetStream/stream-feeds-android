package io.getstream.feeds.android.sample.notification

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ramcosta.composedestinations.generated.destinations.NotificationsScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import io.getstream.feeds.android.client.api.model.AggregatedActivityData
import io.getstream.feeds.android.client.api.state.Feed
import io.getstream.feeds.android.core.generated.models.MarkActivityRequest
import io.getstream.feeds.android.sample.login.LoginManager
import io.getstream.feeds.android.sample.util.AsyncResource
import io.getstream.feeds.android.sample.util.map
import io.getstream.feeds.android.sample.util.notNull
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    loginManager: LoginManager,
    savedStateHandle: SavedStateHandle,
): ViewModel() {

    private val fid = NotificationsScreenDestination.argsFrom(savedStateHandle).fid

    private val feed = flow {
        emit(AsyncResource.notNull(loginManager.currentState()?.client?.feed(fid)))
    }.stateIn(viewModelScope, SharingStarted.Eagerly, AsyncResource.Loading)

    val state = feed
        .map { state -> state.map(Feed::state) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, AsyncResource.Loading)

    init {
        onFeedLoaded { getOrCreate() }
    }

    fun onMarkAggregatedActivityRead(activity: AggregatedActivityData) {
        onFeedLoaded {
            // Check that the activity is not already read
            val notificationStatus = state.notificationStatus.value
            if (notificationStatus?.readActivities?.contains(activity.group) == true) {
                return@onFeedLoaded
            }
            // Collect all activity ids to mark them as read
            val request = MarkActivityRequest(markRead = listOf(activity.group))
            markActivity(request)
        }
    }

    fun onMarkAllRead() {
        onFeedLoaded {
            val request = MarkActivityRequest(markAllRead = true)
            markActivity(request)
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
}