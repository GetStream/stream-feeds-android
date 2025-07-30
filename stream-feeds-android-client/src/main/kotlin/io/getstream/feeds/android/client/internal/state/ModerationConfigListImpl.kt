package io.getstream.feeds.android.client.internal.state

import io.getstream.feeds.android.client.api.model.ModerationConfigData
import io.getstream.feeds.android.client.api.model.QueryConfiguration
import io.getstream.feeds.android.client.api.state.ModerationConfigList
import io.getstream.feeds.android.client.api.state.ModerationConfigListState
import io.getstream.feeds.android.client.api.state.query.ModerationConfigsQuery
import io.getstream.feeds.android.client.internal.repository.ModerationRepository

/**
 * An implementation of [ModerationConfigList] that provides methods to query and manage a list of
 * moderation configurations.
 *
 * @property query The query used to fetch moderation configurations.
 * @property moderationRepository The repository used to interact with the moderation service.
 * @property _state The internal state of the moderation config list.
 */
internal class ModerationConfigListImpl(
    override val query: ModerationConfigsQuery,
    private val moderationRepository: ModerationRepository,
    private val _state: ModerationConfigListStateImpl = ModerationConfigListStateImpl(query),
) : ModerationConfigList {

    override val state: ModerationConfigListState
        get() = _state

    override suspend fun get(): Result<List<ModerationConfigData>> {
        return queryConfigs(query)
    }

    override suspend fun queryMoreConfigs(limit: Int?): Result<List<ModerationConfigData>> {
        val next = _state.pagination?.next
        if (next == null) {
            // No more pages to load
            return Result.success(emptyList())
        }
        val nextQuery = ModerationConfigsQuery(
            filter = _state.queryConfig?.filter,
            sort = _state.queryConfig?.sort,
            limit = limit,
            next = next,
            previous = null,
        )
        return queryConfigs(nextQuery)
    }

    private suspend fun queryConfigs(query: ModerationConfigsQuery): Result<List<ModerationConfigData>> {
        return moderationRepository.queryModerationConfigs(query)
            .onSuccess {
                _state.onLoadMoreConfigs(it, QueryConfiguration(query.filter, query.sort))
            }
            .map {
                it.models
            }
    }
}