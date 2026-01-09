/*
 * Copyright (c) 2014-2026 Stream.io Inc. All rights reserved.
 *
 * Licensed under the Stream License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://github.com/GetStream/stream-feeds-android/blob/main/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.getstream.feeds.android.client.internal.model

import io.getstream.feeds.android.client.api.model.PollData
import io.getstream.feeds.android.client.api.model.PollOptionData
import io.getstream.feeds.android.client.api.model.PollVoteData
import io.getstream.feeds.android.client.internal.utils.upsert
import io.getstream.feeds.android.network.models.PollResponseData

/** Extension function to convert [PollResponseData] to [PollData]. */
internal fun PollResponseData.toModel(): PollData =
    PollData(
        allowAnswers = allowAnswers,
        allowUserSuggestedOptions = allowUserSuggestedOptions,
        answersCount = answersCount,
        createdAt = createdAt,
        createdBy = createdBy?.toModel(),
        createdById = createdById,
        custom = custom,
        description = description,
        enforceUniqueVote = enforceUniqueVote,
        id = id,
        isClosed = isClosed ?: false,
        latestAnswers = latestAnswers.map { it.toModel() },
        latestVotesByOption =
            latestVotesByOption.mapValues { it.value.map { vote -> vote.toModel() } },
        maxVotesAllowed = maxVotesAllowed,
        name = name,
        options = options.map { it.toModel() },
        ownVotes = ownVotes.map { it.toModel() },
        updatedAt = updatedAt,
        voteCount = voteCount,
        voteCountsByOption = voteCountsByOption,
        votingVisibility = votingVisibility,
    )

/**
 * Extension function to update a poll from another poll while preserving own votes because "own"
 * data coming from WS event is not reliable. Optionally, a different ownVotes instance can be
 * provided to be used instead of the current one.
 *
 * @param updatedPoll The poll to update from.
 * @param ownVotes The own votes to preserve. If not provided, the current own votes are preserved.
 * @return A new [PollData] instance with updated information.
 */
internal fun PollData.update(
    updatedPoll: PollData,
    ownVotes: List<PollVoteData> = this.ownVotes,
): PollData = updatedPoll.copy(ownVotes = ownVotes)

/**
 * Extension function to update a poll from another poll while preserving own votes because "own"
 * data coming from WS event is not reliable.
 */
internal fun PollData.update(updated: PollData): PollData = updated.copy(ownVotes = ownVotes)

/**
 * Extension function to add a new option to the poll. This function creates a new [PollData]
 * instance with the new option added to the existing options.
 *
 * @param option The [PollOptionData] to be added to the poll.
 * @return A new [PollData] instance with the added option.
 */
internal fun PollData.addOption(option: PollOptionData): PollData {
    return this.copy(options = this.options.upsert(option, PollOptionData::id))
}

/**
 * Extension function to remove an option from the poll. This function creates a new [PollData]
 * instance with the specified option removed from the existing options.
 *
 * @param optionId The ID of the option to remove.
 * @return A new [PollData] instance with the specified option removed.
 */
internal fun PollData.removeOption(optionId: String): PollData {
    return this.copy(options = this.options.filter { it.id != optionId })
}

/**
 * Extension function to update an existing option in the poll. This function creates a new
 * [PollData] instance with the specified option updated in the existing options.
 *
 * @param option The [PollOptionData] to be updated in the poll.
 * @return A new [PollData] instance with the updated option.
 */
internal fun PollData.updateOption(option: PollOptionData): PollData {
    return this.copy(
        options =
            this.options.map {
                if (it.id == option.id) {
                    option
                } else {
                    it
                }
            }
    )
}

/**
 * Calls [changeVotes] with an [upsert] operation.
 *
 * @see changeVotes
 */
internal fun PollData.upsertVote(updated: PollData, vote: PollVoteData, currentUserId: String) =
    changeVotes(updated, vote, currentUserId) {
        if (updated.enforceUniqueVote) {
            listOf(vote)
        } else {
            upsert(vote, PollVoteData::id)
        }
    }

/**
 * Calls [changeVotes] with a [filter] operation to remove the vote.
 *
 * @see changeVotes
 */
internal fun PollData.removeVote(updated: PollData, vote: PollVoteData, currentUserId: String) =
    changeVotes(updated, vote, currentUserId) { filter { it.id != vote.id } }

/**
 * Merges the receiver poll with [updated] and updates own votes using the provided [updateOwnVotes]
 * function if the vote belongs to the current user.
 *
 * @param updated The updated poll data to merge with the current poll.
 * @param vote The vote that was added or removed.
 * @param currentUserId The ID of the current user, used to determine if the reaction belongs to
 *   them.
 * @param updateOwnVotes A function that takes the current list of own votes and returns the updated
 *   list of own votes.
 * @return The updated [PollData] instance.
 */
internal inline fun PollData.changeVotes(
    updated: PollData,
    vote: PollVoteData,
    currentUserId: String,
    updateOwnVotes: List<PollVoteData>.() -> List<PollVoteData>,
): PollData {
    val updatedOwnVotes =
        if (vote.user?.id == currentUserId) {
            this.ownVotes.updateOwnVotes()
        } else {
            this.ownVotes
        }

    return update(updated, ownVotes = updatedOwnVotes)
}
