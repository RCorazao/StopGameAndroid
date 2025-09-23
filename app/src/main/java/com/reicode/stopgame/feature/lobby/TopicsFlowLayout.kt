package com.reicode.stopgame.feature.lobby

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reicode.stopgame.R
import com.reicode.stopgame.realtime.dto.TopicDto
import kotlin.math.max

/**
 * A reusable composable for displaying topics in a flow layout. Topics automatically wrap to new
 * rows and size to fit their content.
 *
 * @param topics List of topics to display
 * @param isEditMode Whether the layout is in edit mode (for future use)
 * @param onRemoveTopic Callback for removing a topic (for future use)
 */
@Composable
fun TopicsFlowLayout(
        topics: List<TopicDto>,
        isEditMode: Boolean = false,
        onRemoveTopic: (TopicDto) -> Unit = {}
) {
    Layout(
            content = {
                topics.forEach { topic ->
                    AssistChip(
                            onClick = {
                                if (isEditMode) {
                                    onRemoveTopic(topic)
                                }
                            },
                            label = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                            text = topic.name,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                    )
                                    if (isEditMode) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = stringResource(R.string.remove_topic_description, topic.name),
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            },
                            colors =
                                    AssistChipDefaults.assistChipColors(
                                            containerColor = if (isEditMode) Color(0xFFFEE2E2) else Color(0xFFF3F4F6),
                                            labelColor = if (isEditMode) Color(0xFFDC2626) else Color(0xFF374151)
                                    )
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
    ) { measurables, constraints ->
        val spacing = 8.dp.roundToPx()
        val placeables = measurables.map { it.measure(Constraints()) }

        var currentRowWidth = 0
        var currentRowHeight = 0
        var totalHeight = 0
        val rows = mutableListOf<List<Int>>()
        var currentRow = mutableListOf<Int>()

        placeables.forEachIndexed { index, placeable ->
            val itemWidth = placeable.width + spacing

            if (currentRowWidth + itemWidth <= constraints.maxWidth || currentRow.isEmpty()) {
                currentRow.add(index)
                currentRowWidth += itemWidth
                currentRowHeight = max(currentRowHeight, placeable.height)
            } else {
                rows.add(currentRow.toList())
                totalHeight += currentRowHeight + spacing
                currentRow = mutableListOf(index)
                currentRowWidth = itemWidth
                currentRowHeight = placeable.height
            }
        }

        if (currentRow.isNotEmpty()) {
            rows.add(currentRow)
            totalHeight += currentRowHeight
        }

        layout(constraints.maxWidth, totalHeight) {
            var yPosition = 0

            rows.forEach { row ->
                var xPosition = 0
                val rowHeight = row.maxOfOrNull { placeables[it].height } ?: 0

                row.forEach { index ->
                    placeables[index].placeRelative(xPosition, yPosition)
                    xPosition += placeables[index].width + spacing
                }

                yPosition += rowHeight + spacing
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TopicsFlowLayoutPreview() {
    val sampleTopics =
            listOf(
                    TopicDto(
                            id = "1",
                            name = "Animals",
                            isDefault = true,
                            createdByUserId = "host1",
                            createdAt = "2024-01-01T10:00:00Z"
                    ),
                    TopicDto(
                            id = "2",
                            name = "Countries",
                            isDefault = true,
                            createdByUserId = "host1",
                            createdAt = "2024-01-01T10:00:00Z"
                    ),
                    TopicDto(
                            id = "3",
                            name = "Movies and TV Shows",
                            isDefault = false,
                            createdByUserId = "host1",
                            createdAt = "2024-01-01T10:00:00Z"
                    ),
                    TopicDto(
                            id = "4",
                            name = "Food",
                            isDefault = true,
                            createdByUserId = "host1",
                            createdAt = "2024-01-01T10:00:00Z"
                    ),
                    TopicDto(
                            id = "5",
                            name = "Sports",
                            isDefault = false,
                            createdByUserId = "host1",
                            createdAt = "2024-01-01T10:00:00Z"
                    ),
                    TopicDto(
                            id = "6",
                            name = "Technology and Gadgets",
                            isDefault = false,
                            createdByUserId = "host1",
                            createdAt = "2024-01-01T10:00:00Z"
                    )
            )

    TopicsFlowLayout(topics = sampleTopics)
}
