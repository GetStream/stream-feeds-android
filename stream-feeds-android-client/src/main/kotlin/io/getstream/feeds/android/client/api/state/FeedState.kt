package io.getstream.feeds.android.client.api.state

import io.getstream.feeds.android.client.api.model.ActivityData
import io.getstream.feeds.android.client.api.model.ActivityPinData
import io.getstream.feeds.android.client.api.model.FeedData
import io.getstream.feeds.android.client.api.model.FeedId
import io.getstream.feeds.android.client.api.model.FeedMemberData
import io.getstream.feeds.android.client.api.model.FollowData
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.core.generated.models.FeedOwnCapability
import kotlinx.coroutines.flow.StateFlow

/**
 * An observable object representing the current state of a feed.
 *
 * This class manages the state of a feed including activities, followers, members, and
 * pagination information. It automatically updates when WebSocket events are received and provides
 * change handlers for state modifications.
 */
public interface FeedState {

    public val fid: FeedId

    public val feedQuery: FeedQuery

    public val activities: StateFlow<List<ActivityData>>

    public val feed: StateFlow<FeedData?>

    public val followers: StateFlow<List<FollowData>>

    public val following: StateFlow<List<FollowData>>

    public val followRequests: StateFlow<List<FollowData>>

    public val members: StateFlow<List<FeedMemberData>>

    public val ownCapabilities: StateFlow<List<FeedOwnCapability>>

    public val pinnedActivities: StateFlow<List<ActivityPinData>>

    public val activitiesPagination: PaginationData?

    public val canLoadMoreActivities: Boolean
        get() = activitiesPagination?.next != null
}