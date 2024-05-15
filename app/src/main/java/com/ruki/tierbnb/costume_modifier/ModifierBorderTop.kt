package com.ruki.tierbnb.costume_modifier

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.topBorder(
    color: Color,
    width: Dp = 1.dp
) = this.then(
    Modifier.drawBehind {
        val strokeWidth = width.toPx()
        val y = strokeWidth / 2
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(0f, y),
            end = androidx.compose.ui.geometry.Offset(size.width, y),
            strokeWidth = strokeWidth
        )
    }
)
