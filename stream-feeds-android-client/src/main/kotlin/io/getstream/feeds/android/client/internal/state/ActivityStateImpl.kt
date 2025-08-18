package io.getstream.feeds.android.client.internal.state

import io.getstream.feeds.android.client.api.model.ActivityData
import io.getstream.feeds.android.client.api.model.PollData
import io.getstream.feeds.android.client.api.model.PollOptionData
import io.getstream.feeds.android.client.api.model.PollVoteData
import io.getstream.feeds.android.client.api.model.ThreadedCommentData
import io.getstream.feeds.android.client.api.model.addOption
import io.getstream.feeds.android.client.api.model.castVote
import io.getstream.feeds.android.client.api.model.removeOption
import io.getstream.feeds.android.client.api.model.removeVote
import io.getstream.feeds.android.client.api.model.updateOption
import io.getstream.feeds.android.client.api.state.ActivityCommentListState
import io.getstream.feeds.android.client.api.state.ActivityState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * An observable object representing the current state of an activity.
 *
 * This class manages the state of a single activity including its comments, poll data, and
 * real-time updates.
 * It automatically updates when WebSocket events are received and provides change handlers for
 * state modifications.
 *
 * @property activityCommentListState The mutable state for the list of comments associated with the
 * activity.
 */
internal class ActivityStateImpl(
    private val currentUserId: String,
    private val activityCommentListState: ActivityCommentListState,
) : ActivityMutableState {

    private val _activity: MutableStateFlow<ActivityData?> = MutableStateFlow(null)
    private val _poll: MutableStateFlow<PollData?> = MutableStateFlow(null)

    override val activity: StateFlow<ActivityData?>
        get() = _activity.asStateFlow()

    override val comments: StateFlow<List<ThreadedCommentData>>
        get() = activityCommentListState.comments

    override val poll: StateFlow<PollData?>
        get() = _poll.asStateFlow()

    override fun onActivityUpdated(activity: ActivityData) {
        _activity.value = activity
        _poll.value = activity.poll
    }

    override fun onPollClosed(poll: PollData) {
        if (_poll.value?.id != poll.id) return
        _poll.value = poll
    }

    override fun onPollDeleted(pollId: String) {
        if (_poll.value?.id != pollId) return
        _poll.value = null
    }

    override fun onPollUpdated(poll: PollData) {
        if (_poll.value?.id != poll.id) return
        _poll.value = poll
    }

    override fun onOptionCreated(option: PollOptionData) {
        _poll.value = _poll.value?.addOption(option)
    }

    override fun onOptionDeleted(optionId: String) {
        _poll.value = _poll.value?.removeOption(optionId)
    }

    override fun onOptionUpdated(option: PollOptionData) {
        _poll.value = _poll.value?.updateOption(option)
    }

    override fun onPollVoteCasted(
        vote: PollVoteData,
        poll: PollData
    ) {
        if (_poll.value?.id != poll.id) return
        _poll.value = poll
    }

    override fun onPollVoteCasted(vote: PollVoteData?) {
        if (vote == null) return
        _poll.value = _poll.value?.castVote(vote, currentUserId)
    }

    override fun onPollVoteChanged(
        vote: PollVoteData,
        poll: PollData
    ) {
        if (_poll.value?.id != poll.id) return
        _poll.value = poll
    }

    override fun onPollVoteRemoved(
        vote: PollVoteData,
        poll: PollData
    ) {
        if (_poll.value?.id != poll.id) return
        _poll.value = poll
    }

    override fun onPollVoteRemoved(vote: PollVoteData?) {
        if (vote == null) return
        _poll.value = _poll.value?.removeVote(vote, currentUserId)
    }
}

/**
 * Mutable state interface for activity state management.
 *
 * This interface extends the [ActivityState] and [ActivityStateUpdates] interfaces to provide
 * a mutable state representation of an activity, allowing updates to the activity and its
 * associated poll data.
 */
internal interface ActivityMutableState : ActivityState, ActivityStateUpdates

/**
 * Interface for handling updates to the activity state.
 *
 * This interface defines methods that are called when the activity or its associated poll is
 * updated, closed, deleted, or when votes are casted, changed, or removed.
 */
internal interface ActivityStateUpdates {

    /**
     * Called when the activity is updated.
     *
     * @param activity The updated activity data.
     */
    fun onActivityUpdated(activity: ActivityData)

    /**
     * Called when the associated poll is closed.
     *
     * @param poll The updated poll data.
     */
    fun onPollClosed(poll: PollData)

    /**
     * Called when the associated poll is deleted.
     *
     * @param pollId The ID of the deleted poll.
     */
    fun onPollDeleted(pollId: String)

    /**
     * Called when the associated poll is updated.
     *
     * @param poll The updated poll data.
     */
    fun onPollUpdated(poll: PollData)

    /**
     * Called when a new poll option is created.
     *
     * @param option The newly created poll option data.
     */
    fun onOptionCreated(option: PollOptionData)

    /**
     * Called when a poll option is deleted.
     *
     * @param optionId The poll option ID that was deleted.
     */
    fun onOptionDeleted(optionId: String)

    /**
     * Called when a poll option is updated.
     *
     * @param option The updated poll option data.
     */
    fun onOptionUpdated(option: PollOptionData)

    /**
     * Called when a vote is casted on the poll.
     *
     * @param vote The vote that was casted.
     * @param poll The poll associated with the vote.
     */
    fun onPollVoteCasted(vote: PollVoteData, poll: PollData)

    /**
     * Called when a vote is casted on the poll.
     *
     * @param vote The vote that was casted, or null if the vote was not successful.
     */
    fun onPollVoteCasted(vote: PollVoteData?)

    /**
     * Called when a vote is changed on the poll.
     *
     * @param vote The updated vote data.
     * @param poll The poll associated with the vote.
     */
    fun onPollVoteChanged(vote: PollVoteData, poll: PollData)

    /**
     * Called when a vote is removed from the poll.
     *
     * @param vote The vote that was removed.
     * @param poll The poll associated with the vote.
     */
    fun onPollVoteRemoved(vote: PollVoteData, poll: PollData)

    /**
     * Called when a vote is removed from the poll.
     *
     * @param vote The vote that was removed, or null if the vote was not found.
     */
    fun onPollVoteRemoved(vote: PollVoteData?)
}
