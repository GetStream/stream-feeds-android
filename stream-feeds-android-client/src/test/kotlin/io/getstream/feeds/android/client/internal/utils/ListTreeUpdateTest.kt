/*
 * Copyright (c) 2014-2025 Stream.io Inc. All rights reserved.
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
package io.getstream.feeds.android.client.internal.utils

import org.junit.Assert.assertEquals
import org.junit.Test

internal class ListTreeUpdateTest {

    private val comparator = compareBy(TestNode::sortField)

    @Test
    fun `when id is not found, then return the original list`() {
        val initial = listOf(TestNode("2", 25), TestNode("1", 20), TestNode("3", 30))

        val updated =
            initial.treeUpdateFirst(
                matcher = { it.id == "4" },
                updateElement = { it.copy(sortField = 35) },
                childrenSelector = TestNode::nodes,
                updateChildren = { node, children -> node.copy(nodes = children) },
                comparator = comparator,
            )

        assertEquals(initial, updated)
    }

    @Test
    fun `when id is found and comparator is not provided, then update element`() {
        val initial = listOf(TestNode("2", 25), TestNode("1", 20), TestNode("3", 30))
        val expected = listOf(TestNode("2", 35), TestNode("1", 20), TestNode("3", 30))

        val updated =
            initial.treeUpdateFirst(
                matcher = { it.id == "2" },
                updateElement = { it.copy(sortField = 35) },
                childrenSelector = TestNode::nodes,
                updateChildren = { node, children -> node.copy(nodes = children) },
            )

        assertEquals(expected, updated)
    }

    @Test
    fun `when id is found and comparator is provided, then update element and sort the result`() {
        val initial = listOf(TestNode("2", 25), TestNode("1", 20), TestNode("3", 30))
        val expected = listOf(TestNode("1", 20), TestNode("3", 30), TestNode("2", 35))

        val updated =
            initial.treeUpdateFirst(
                matcher = { it.id == "2" },
                updateElement = { it.copy(sortField = 35) },
                childrenSelector = TestNode::nodes,
                updateChildren = { node, children -> node.copy(nodes = children) },
                comparator = comparator,
            )

        assertEquals(expected, updated)
    }

    @Test
    fun `when id is found in a child, then update the children list`() {
        val initial =
            listOf(
                TestNode("1", 20),
                TestNode("2", 25, listOf(TestNode("5", 50), TestNode("3", 30))),
                TestNode("4", 40),
            )
        val expected =
            listOf(
                TestNode("1", 20),
                TestNode("2", 25, listOf(TestNode("3", 35), TestNode("5", 50))),
                TestNode("4", 40),
            )

        val updated =
            initial.treeUpdateFirst(
                matcher = { it.id == "3" },
                updateElement = { it.copy(sortField = 35) },
                childrenSelector = TestNode::nodes,
                updateChildren = { node, children -> node.copy(nodes = children) },
                comparator = comparator,
            )

        assertEquals(expected, updated)
    }

    @Test
    fun `when id is found in multiple nodes, then update only the first match`() {
        val initial =
            listOf(
                TestNode("A", 1, listOf(TestNode("target", 100))),
                TestNode("B", 2, listOf(TestNode("target", 200))),
            )
        val expected =
            listOf(
                TestNode("A", 1, listOf(TestNode("target", 101))),
                TestNode("B", 2, listOf(TestNode("target", 200))),
            )

        val updated =
            initial.treeUpdateFirst(
                matcher = { it.id == "target" },
                updateElement = { it.copy(sortField = it.sortField + 1) },
                childrenSelector = TestNode::nodes,
                updateChildren = { node, children -> node.copy(nodes = children) },
            )

        assertEquals(expected, updated)
    }

    @Test
    fun `when updating a child changes parent's sort key, then parent level must be resorted`() {
        val initial =
            listOf(
                TestNode("A", 10, listOf(TestNode("x", 10))),
                TestNode("B", 15, listOf(TestNode("y", 15))),
            )
        val expected =
            listOf(
                TestNode("B", 15, listOf(TestNode("y", 15))),
                TestNode("A", 25, listOf(TestNode("x", 25))),
            )

        val updated =
            initial.treeUpdateFirst(
                matcher = { it.id == "x" },
                updateElement = { it.copy(sortField = 25) },
                childrenSelector = TestNode::nodes,
                updateChildren = { node, children ->
                    node.copy(
                        nodes = children,
                        sortField = children.maxOfOrNull { it.sortField } ?: node.sortField,
                    )
                },
                comparator = comparator,
            )

        assertEquals(expected, updated)
    }

    @Test
    fun `when updating a grandchild changes multiple ancestors' sort keys, all ancestor levels are resorted`() {
        // comparator: ascending by sortField
        // Top level initially sorted: A(10), B(15), C(20)
        val initial =
            listOf(
                TestNode(
                    id = "A",
                    sortField = 10,
                    nodes =
                        listOf(
                            TestNode(
                                id = "M",
                                sortField = 12,
                                nodes =
                                    listOf(
                                        TestNode(id = "x", sortField = 10) // target (grandchild)
                                    ),
                            )
                        ),
                ),
                TestNode("B", 15, listOf(TestNode("b1", 15))),
                TestNode("C", 20, listOf(TestNode("c1", 20))),
            )

        // Update grandchild "x" to 35; updateChildren bubbles max(child) up to parent and
        // grandparent.
        val updated =
            initial.treeUpdateFirst(
                matcher = { it.id == "x" },
                updateElement = { it.copy(sortField = 35) },
                childrenSelector = TestNode::nodes,
                updateChildren = { node, children ->
                    node.copy(
                        nodes = children,
                        sortField = (children.maxOfOrNull { it.sortField } ?: node.sortField),
                    )
                },
                comparator = comparator, // compareBy(TestNode::sortField) ascending
            )

        // Expected top-level order after bubbling: B(15), C(20), A(35)
        val expected =
            listOf(
                TestNode("B", 15, listOf(TestNode("b1", 15))),
                TestNode("C", 20, listOf(TestNode("c1", 20))),
                TestNode(
                    "A",
                    35,
                    nodes = listOf(TestNode("M", 35, nodes = listOf(TestNode("x", 35)))),
                ),
            )

        assertEquals(expected, updated)
    }

    @Test
    fun `on treeRemoveFirst when element is not found, then return original list`() {
        val initial = listOf(TestNode("1", 20), TestNode("2", 25), TestNode("3", 30))

        val result =
            initial.treeRemoveFirst(
                matcher = { it.id == "4" },
                childrenSelector = TestNode::nodes,
                updateChildren = { node, children -> node.copy(nodes = children) },
            )

        assertEquals(initial, result)
    }

    @Test
    fun `on treeRemoveFirst when element is found at top level, then remove element`() {
        val initial = listOf(TestNode("1", 20), TestNode("2", 25), TestNode("3", 30))
        val expected = listOf(TestNode("1", 20), TestNode("3", 30))

        val result =
            initial.treeRemoveFirst(
                matcher = { it.id == "2" },
                childrenSelector = TestNode::nodes,
                updateChildren = { node, children -> node.copy(nodes = children) },
            )

        assertEquals(expected, result)
    }

    @Test
    fun `on treeRemoveFirst when element is nested, then remove from children`() {
        val initial =
            listOf(
                TestNode("1", 20),
                TestNode(
                    "2",
                    25,
                    listOf(TestNode("5", 50, listOf(TestNode("3", 30))), TestNode("4", 40)),
                ),
                TestNode("6", 60),
            )
        val expected =
            listOf(
                TestNode("1", 20),
                TestNode("2", 25, listOf(TestNode("5", 50), TestNode("4", 40))),
                TestNode("6", 60),
            )

        val result =
            initial.treeRemoveFirst(
                matcher = { it.id == "3" },
                childrenSelector = TestNode::nodes,
                updateChildren = { node, children -> node.copy(nodes = children) },
            )

        assertEquals(expected, result)
    }

    @Test
    fun `on treeRemoveFirst when multiple elements match, then remove only first match`() {
        val initial =
            listOf(
                TestNode("A", 1, listOf(TestNode("target", 100))),
                TestNode("B", 2, listOf(TestNode("target", 200))),
            )
        val expected =
            listOf(TestNode("A", 1, listOf()), TestNode("B", 2, listOf(TestNode("target", 200))))

        val result =
            initial.treeRemoveFirst(
                matcher = { it.id == "target" },
                childrenSelector = TestNode::nodes,
                updateChildren = { node, children -> node.copy(nodes = children) },
            )

        assertEquals(expected, result)
    }

    private data class TestNode(
        val id: String,
        val sortField: Int,
        val nodes: List<TestNode> = emptyList(),
    )
}
