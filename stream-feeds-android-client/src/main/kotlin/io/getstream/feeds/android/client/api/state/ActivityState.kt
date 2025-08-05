package io.getstream.feeds.android.client.api.state

import io.getstream.feeds.android.client.api.model.ActivityData
import io.getstream.feeds.android.client.api.model.PollData
import io.getstream.feeds.android.client.api.model.ThreadedCommentData
import kotlinx.coroutines.flow.StateFlow

/**
 * An observable object representing the current state of an activity.
 *
 * This class manages the state of a single activity including its comments, poll data, and
 * real-time updates. It automatically updates when WebSocket events are received and provides
 * change handlers for state modifications.
 */
public interface ActivityState {

    /**
     * The current activity data.
     */
    public val activity: StateFlow<ActivityData?>

    /**
     * The list of comments for this activity, sorted by default sorting criteria.
     */
    public val comments: StateFlow<List<ThreadedCommentData>>

    /**
     * The poll data associated with this activity, if any.
     */
    public val poll: StateFlow<PollData?>
}
