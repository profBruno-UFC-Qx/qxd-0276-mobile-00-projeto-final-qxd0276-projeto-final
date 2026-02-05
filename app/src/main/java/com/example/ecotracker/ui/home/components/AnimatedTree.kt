package com.example.ecotracker.ui.home.components

import androidx.annotation.Size
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.fadeIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.ecotracker.ui.home.viewmodel.TreeStage
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ecotracker.utils.treeStageToDrawable

@Composable
fun AnimatedTree(
    treeStage: TreeStage,
    modifier: Modifier,
    imageSize: Dp = 180.dp
){
    val infiniteTransition = rememberInfiniteTransition(label = "treeBorder")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing)
        ),
        label = "angle"
    )
    // Gradiente
    val brush = Brush.sweepGradient(
        colors = listOf(
            Color(0xFF1976D2),
            Color(0xFF16A69A),
            Color(0xFF66BB6A),
            Color(0xFF2E7D32),
            Color(0xFF36A69A),
            Color(0xFF1976D2)
        )
    )
    AnimatedContent(
        targetState = treeStage,
        transitionSpec = {
            fadeIn(animationSpec = tween(600)) +
                    scaleIn(initialScale = 0.85f) togetherWith
                    fadeOut(animationSpec = tween(300))
        }
    ) { stage ->
        Image(
            painter = painterResource(treeStageToDrawable(stage)),
            contentDescription = "Árvore Animada",
            contentScale = ContentScale.Crop,
            modifier = modifier
                .size(imageSize)
                .clip(CircleShape)
                .drawBehind{
                    rotate(angle){
                        drawCircle(
                            brush = brush,
                            radius = size.minDimension / 2 -3.dp.toPx(),
                            style = Stroke(width = 6.dp.toPx())
                        )
                    }
                }

        )
    }
}