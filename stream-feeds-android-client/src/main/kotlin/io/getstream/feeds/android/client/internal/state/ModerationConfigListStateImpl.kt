package io.getstream.feeds.android.client.internal.state

import io.getstream.feeds.android.client.api.model.ModerationConfigData
import io.getstream.feeds.android.client.api.model.PaginationData
import io.getstream.feeds.android.client.api.model.PaginationResult
import io.getstream.feeds.android.client.api.model.QueryConfiguration
import io.getstream.feeds.android.client.api.state.ModerationConfigListState
import io.getstream.feeds.android.client.api.state.query.ModerationConfigSort
import io.getstream.feeds.android.client.api.state.query.ModerationConfigsQuery
import io.getstream.feeds.android.client.internal.utils.mergeSorted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * An observable state object that manages the current state of a moderation config list.
 *
 * This class maintains the current list of moderation configs and pagination information.
 */
internal class ModerationConfigListStateImpl(
    override val query: ModerationConfigsQuery,
) : ModerationConfigListMutableState {

    private val _configs: MutableStateFlow<List<ModerationConfigData>> =
        MutableStateFlow(emptyList())

    internal var queryConfig: QueryConfiguration<ModerationConfigSort>? = null
        private set

    private var _pagination: PaginationData? = null

    private val configsSorting: List<ModerationConfigSort>
        get() = queryConfig?.sort ?: ModerationConfigSort.Default


    override val configs: StateFlow<List<ModerationConfigData>>
        get() = _configs.asStateFlow()

    override val pagination: PaginationData?
        get() = _pagination

    override fun onLoadMoreConfigs(
        result: PaginationResult<ModerationConfigData>,
        queryConfig: QueryConfiguration<ModerationConfigSort>
    ) {
        _pagination = result.pagination
        this.queryConfig = queryConfig
        // Merge the new configs with the existing ones (keeping the sort order)
        _configs.value =
            _configs.value.mergeSorted(result.models, ModerationConfigData::id, configsSorting)
    }

}

internal interface ModerationConfigListMutableState : ModerationConfigListState,
    ModerationConfigListStateUpdates

internal interface ModerationConfigListStateUpdates {

    fun onLoadMoreConfigs(
        result: PaginationResult<ModerationConfigData>,
        queryConfig: QueryConfiguration<ModerationConfigSort>,
    )
}
