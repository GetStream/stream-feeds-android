package io.getstream.feeds.android.client.internal.utils

import org.junit.Assert.assertEquals
import org.junit.Test

internal class ListTreeUpdateTest {

    private val comparator = compareBy(TestNode::sortField)

    @Test
    fun `when id is not found, then return the original list`() {
        val initial = listOf(
            TestNode("2", 25),
            TestNode("1", 20),
            TestNode("3", 30)
        )

        val updated = initial.treeUpdateFirst(
            matcher = { it.id == "4" },
            updateElement = { it.copy(sortField = 35) },
            childrenSelector = TestNode::nodes,
            updateChildren = { node, children -> node.copy(nodes = children) },
            comparator = comparator
        )

        assertEquals(initial, updated)
    }

    @Test
    fun `when id is found and comparator is not provided, then update element`() {
        val initial = listOf(
            TestNode("2", 25),
            TestNode("1", 20),
            TestNode("3", 30)
        )
        val expected = listOf(
            TestNode("2", 35),
            TestNode("1", 20),
            TestNode("3", 30),
        )

        val updated = initial.treeUpdateFirst(
            matcher = { it.id == "2" },
            updateElement = { it.copy(sortField = 35) },
            childrenSelector = TestNode::nodes,
            updateChildren = { node, children -> node.copy(nodes = children) },
        )

        assertEquals(expected, updated)
    }

    @Test
    fun `when id is found and comparator is provided, then update element and sort the result`() {
        val initial = listOf(
            TestNode("2", 25),
            TestNode("1", 20),
            TestNode("3", 30)
        )
        val expected = listOf(
            TestNode("1", 20),
            TestNode("3", 30),
            TestNode("2", 35),
        )

        val updated = initial.treeUpdateFirst(
            matcher = { it.id == "2" },
            updateElement = { it.copy(sortField = 35) },
            childrenSelector = TestNode::nodes,
            updateChildren = { node, children -> node.copy(nodes = children) },
            comparator = comparator
        )

        assertEquals(expected, updated)
    }

    @Test
    fun `when id is found in a child, then update the children list`() {
        val initial = listOf(
            TestNode("1", 20),
            TestNode("2", 25, listOf(TestNode("5", 50), TestNode("3", 30))),
            TestNode("4", 40)
        )
        val expected = listOf(
            TestNode("1", 20),
            TestNode("2", 25, listOf(TestNode("3", 35), TestNode("5", 50))),
            TestNode("4", 40)
        )

        val updated = initial.treeUpdateFirst(
            matcher = { it.id == "3" },
            updateElement = { it.copy(sortField = 35) },
            childrenSelector = TestNode::nodes,
            updateChildren = { node, children -> node.copy(nodes = children) },
            comparator = comparator
        )

        assertEquals(expected, updated)
    }

    @Test
    fun `when id is found in multiple nodes, then update only the first match`() {
        val initial = listOf(
            TestNode("A", 1, listOf(TestNode("target", 100))),
            TestNode("B", 2, listOf(TestNode("target", 200))),
        )
        val expected = listOf(
            TestNode("A", 1, listOf(TestNode("target", 101))),
            TestNode("B", 2, listOf(TestNode("target", 200))),
        )

        val updated = initial.treeUpdateFirst(
            matcher = { it.id == "target" },
            updateElement = { it.copy(sortField = it.sortField + 1) },
            childrenSelector = TestNode::nodes,
            updateChildren = { node, children -> node.copy(nodes = children) }
        )

        assertEquals(expected, updated)
    }

    @Test
    fun `when updating a child changes parent's sort key, then parent level must be resorted`() {
        val initial = listOf(
            TestNode("A", 10, listOf(TestNode("x", 10))),
            TestNode("B", 15, listOf(TestNode("y", 15)))
        )
        val expected = listOf(
            TestNode("B", 15, listOf(TestNode("y", 15))),
            TestNode("A", 25, listOf(TestNode("x", 25)))
        )

        val updated = initial.treeUpdateFirst(
            matcher = { it.id == "x" },
            updateElement = { it.copy(sortField = 25) },
            childrenSelector = TestNode::nodes,
            updateChildren = { node, children ->
                node.copy(
                    nodes = children,
                    sortField = children.maxOfOrNull { it.sortField } ?: node.sortField
                )
            },
            comparator = comparator
        )


        assertEquals(expected, updated)
    }

    private data class TestNode(val id: String, val sortField: Int, val nodes: List<TestNode> = emptyList())
}
