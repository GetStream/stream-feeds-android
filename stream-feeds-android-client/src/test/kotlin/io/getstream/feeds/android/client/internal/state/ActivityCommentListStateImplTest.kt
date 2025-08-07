import io.getstream.feeds.android.client.api.state.query.ActivityCommentsQuery
import io.getstream.feeds.android.client.api.state.query.CommentsSort
import io.getstream.feeds.android.client.internal.state.ActivityCommentListStateImpl
import io.getstream.feeds.android.client.internal.test.TestData.threadedCommentData
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Date

internal class ActivityCommentListStateImplTest {

    private val query = ActivityCommentsQuery(
        objectId = "activity_1",
        objectType = "activity",
        sort = CommentsSort.First,
    )
    private val state = ActivityCommentListStateImpl(query, currentUserId = "user_1")

    @Test
    fun `onCommentAdded when has no parent, then add it at the top-level in the correct position`() {
        val comment1 = threadedCommentData(id = "c1", createdAt = Date(1))
        val comment2 = threadedCommentData(id = "c2", createdAt = Date(2))
        val comment3 = threadedCommentData(id = "c3", createdAt = Date(3))
        val expected = listOf(comment1, comment2, comment3)

        state.onCommentAdded(comment1)
        state.onCommentAdded(comment3)
        state.onCommentAdded(comment2)

        assertEquals(expected, state.comments.value)
    }

    @Test
    fun `onCommentAdded when has a parent, then add it as a child in the correct position`() {
        val parent = threadedCommentData(
            id = "c1",
            replies = listOf(
                threadedCommentData("c2", parentId = "c1", createdAt = Date(2)),
                threadedCommentData("c4", parentId = "c1", createdAt = Date(4))
            )
        )
        val reply = threadedCommentData("c3", parentId = "c1", createdAt = Date(3))
        val expected = threadedCommentData(
            id = "c1",
            replies = listOf(
                threadedCommentData("c2", parentId = "c1", createdAt = Date(2)),
                threadedCommentData("c3", parentId = "c1", createdAt = Date(3)),
                threadedCommentData("c4", parentId = "c1", createdAt = Date(4))
            )
        )

        state.onCommentAdded(parent)
        state.onCommentAdded(reply)

        assertEquals(listOf(expected), state.comments.value)
    }
}
