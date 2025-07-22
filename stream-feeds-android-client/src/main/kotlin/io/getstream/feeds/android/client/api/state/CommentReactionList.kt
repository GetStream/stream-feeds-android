package io.getstream.feeds.android.client.api.state

/**
 * A class representing a paginated list of reactions for a specific comment.
 *
 * This class provides methods to fetch and manage reactions for a comment, including
 * pagination support and real-time updates through WebSocket events. It maintains an
 * observable state that automatically updates when reaction-related events are received.
 *
 * TODO: Add Usage examples
 *
 * ## Features
 *
 * - **Pagination**: Supports loading reactions in pages with configurable limits
 * - **Real-time Updates**: Automatically receives WebSocket events for reaction changes
 * - **Filtering**: Supports filtering by reaction type, user ID, and creation date
 * - **Sorting**: Configurable sorting options for reaction ordering
 * - **Observable State**: Provides reactive state management for UI updates
 */
public interface CommentReactionList {
}