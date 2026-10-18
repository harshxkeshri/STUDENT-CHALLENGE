package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// Colors from the official Mirai School of Technology logo
val MiraiNavy = Color(0xFF0C2340)
val MiraiPurple = Color(0xFF6D28D9)
val MiraiGreen = Color(0xFF16A34A)
val MiraiBlue = Color(0xFF2563EB)
val MiraiYellow = Color(0xFFEAB308)
val MiraiCyan = Color(0xFF0891B2)
val MiraiRed = Color(0xFFE11D48)
val MiraiOrange = Color(0xFFEA580C)

/**
 * Renders the circular Mirai School of Technology logo emblem:
 * 7 segmented colored petals surrounding a central bold Navy 'M'.
 */
@Composable
fun MiraiLogoBadge(
    modifier: Modifier = Modifier,
    sizeDp: Dp = 64.dp,
    showBackgroundCircle: Boolean = true,
    backgroundColor: Color = Color.White
) {
    Box(
        modifier = modifier
            .size(sizeDp)
            .testTag("mirai_logo_badge")
            .then(
                if (showBackgroundCircle) {
                    Modifier.clip(CircleShape).background(backgroundColor)
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(sizeDp * 0.08f)) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val outerRadius = size.minDimension / 2f
            val innerRadius = outerRadius * 0.65f
            val strokeWidth = outerRadius - innerRadius

            val sectorColors = listOf(
                MiraiPurple, // 0: Top
                MiraiGreen,  // 1: Top Right
                MiraiBlue,   // 2: Right
                MiraiYellow, // 3: Bottom Right
                MiraiCyan,   // 4: Bottom Left
                MiraiRed,    // 5: Left
                MiraiOrange  // 6: Top Left
            )

            val sweepAngle = 360f / 7f
            val gapAngle = 4f
            val netSweep = sweepAngle - gapAngle

            // Draw 7 colored arcs (petals)
            sectorColors.forEachIndexed { index, color ->
                // Start from top (-90 degrees) minus half segment to center top petal
                val startAngle = -90f - (sweepAngle / 2f) + (index * sweepAngle) + (gapAngle / 2f)
                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = netSweep,
                    useCenter = false,
                    topLeft = Offset(center.x - (innerRadius + outerRadius) / 2f, center.y - (innerRadius + outerRadius) / 2f),
                    size = Size(innerRadius + outerRadius, innerRadius + outerRadius),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                // Draw tiny symbol dot/accent inside each segment
                val midAngleRad = (startAngle + netSweep / 2f) * (PI / 180.0)
                val dotRadius = (innerRadius + outerRadius) / 2f
                val dotCenter = Offset(
                    (center.x + dotRadius * cos(midAngleRad)).toFloat(),
                    (center.y + dotRadius * sin(midAngleRad)).toFloat()
                )
                drawCircle(
                    color = Color.White,
                    radius = strokeWidth * 0.18f,
                    center = dotCenter
                )
            }

            // Draw stylized Center Navy M
            drawMiraiNavyM(center, innerRadius * 0.88f)
        }
    }
}

/**
 * Draws the geometric M with two central pillars
 */
private fun DrawScope.drawMiraiNavyM(center: Offset, scaleRadius: Float) {
    val mWidth = scaleRadius * 1.5f
    val mHeight = scaleRadius * 1.4f
    val left = center.x - mWidth / 2f
    val right = center.x + mWidth / 2f
    val top = center.y - mHeight / 2f
    val bottom = center.y + mHeight / 2f

    // Outer M silhouette
    val mPath = Path().apply {
        // Top-left
        moveTo(left, top)
        lineTo(left + mWidth * 0.22f, top)
        // Center valley
        lineTo(center.x, top + mHeight * 0.45f)
        // Top-right
        lineTo(right - mWidth * 0.22f, top)
        lineTo(right, top)
        // Down right wing
        lineTo(right, bottom)
        lineTo(right - mWidth * 0.22f, bottom)
        lineTo(right - mWidth * 0.22f, top + mHeight * 0.35f)
        lineTo(center.x, bottom - mHeight * 0.15f)
        lineTo(left + mWidth * 0.22f, top + mHeight * 0.35f)
        lineTo(left + mWidth * 0.22f, bottom)
        lineTo(left, bottom)
        close()
    }
    drawPath(mPath, color = MiraiNavy)

    // Two central pillars inside M
    val pillarWidth = mWidth * 0.12f
    val pillarHeight = mHeight * 0.45f
    val pillarY = bottom - pillarHeight

    // Left pillar
    drawRect(
        color = MiraiNavy,
        topLeft = Offset(center.x - pillarWidth * 1.4f, pillarY),
        size = Size(pillarWidth, pillarHeight)
    )

    // Right pillar
    drawRect(
        color = MiraiNavy,
        topLeft = Offset(center.x + pillarWidth * 0.4f, pillarY),
        size = Size(pillarWidth, pillarHeight)
    )
}

/**
 * Full Branding Header with Mirai Logo & Official Typography
 */
@Composable
fun MiraiLogoHeader(
    modifier: Modifier = Modifier,
    logoSize: Dp = 80.dp,
    textColor: Color = Color.White,
    subtextColor: Color = Color(0xFF94A3B8)
) {
    Column(
        modifier = modifier.testTag("mirai_logo_header"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MiraiLogoBadge(
            sizeDp = logoSize,
            showBackgroundCircle = true,
            backgroundColor = Color.White
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "MIRAI",
            color = textColor,
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 4.sp,
            fontFamily = FontFamily.SansSerif
        )

        Text(
            text = "SCHOOL OF TECHNOLOGY",
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .width(36.dp)
                    .height(1.dp)
                    .background(subtextColor.copy(alpha = 0.5f))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "CLUBS MANAGEMENT",
                color = subtextColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.5.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .width(36.dp)
                    .height(1.dp)
                    .background(subtextColor.copy(alpha = 0.5f))
            )
        }
    }
}
