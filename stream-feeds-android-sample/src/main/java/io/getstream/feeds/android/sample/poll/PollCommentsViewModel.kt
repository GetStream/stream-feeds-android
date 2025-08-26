package io.getstream.feeds.android.sample.poll

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ramcosta.composedestinations.generated.destinations.PollCommentsScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import io.getstream.feeds.android.client.api.query.Filters
import io.getstream.feeds.android.client.api.state.PollVoteList
import io.getstream.feeds.android.client.api.state.query.PollVotesQuery
import io.getstream.feeds.android.sample.login.LoginManager
import io.getstream.feeds.android.sample.util.AsyncResource
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
class PollCommentsViewModel @Inject constructor(
    loginManager: LoginManager,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val pollId = PollCommentsScreenDestination.argsFrom(savedStateHandle).pollId

    private val pollVoteList = flow {
        AsyncResource.notNull(loginManager.currentState())
            .map(::getPollVoteList)
            .let { emit(it) }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, AsyncResource.Loading)

    val state = pollVoteList.map { asyncResource -> asyncResource.map(PollVoteList::state) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, AsyncResource.Loading)

    init {
        pollVoteList.withFirstContent(viewModelScope) {
            get().logResult(TAG, "Loading votes for poll: $pollId")
        }
    }

    fun onLoadMore() {
        pollVoteList.withFirstContent(viewModelScope) {
            if (!state.canLoadMore) return@withFirstContent
            queryMorePollVotes()
                .logResult(TAG, "Loading more votes for poll: $pollId")
        }
    }

    private fun getPollVoteList(userState: LoginManager.UserState): PollVoteList {
        val query = PollVotesQuery(
            pollId = pollId,
            userId = userState.user.id,
            filter = Filters.equal("is_answer", true)
        )
        return userState.client.pollVoteList(query)
    }

    companion object {
        private const val TAG = "PollCommentsViewModel"
    }
}
