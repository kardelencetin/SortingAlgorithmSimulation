package com.kardelencetin.sortingalgorithm.presentation.ui.activity

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.kardelencetin.sortingalgorithm.R
import com.kardelencetin.sortingalgorithm.domain.model.SortStep

class CustomSortView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    var step: SortStep? = null
        set(value) {
            field = value
            invalidate()
        }

    private val barPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        textSize = 42f
        isFakeBoldText = true
    }

    private val colorNormal = ContextCompat.getColor(context, R.color.gray)
    private val colorHighlighted = ContextCompat.getColor(context, R.color.red)
    private val colorMerged = ContextCompat.getColor(context, R.color.green)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val currentStep = step ?: return
        val list = currentStep.list
        if (list.isEmpty()) return

        val highlighted = currentStep.highlighted
        val merged = currentStep.merged

        val padding = 24f
        val barSpace = (width - padding * 2) / list.size
        val barWidth = barSpace * 0.8f
        val barMargin = barSpace * 0.2f
        val barHeight = height * 0.5f
        val top = (height - barHeight) / 2

        list.forEachIndexed { i, value ->
            barPaint.color = when {
                merged != null && i in merged -> colorMerged
                highlighted?.first == i || highlighted?.second == i -> colorHighlighted
                else -> colorNormal
            }

            val left = padding + i * barSpace + barMargin / 2
            val right = left + barWidth
            val rect = RectF(left, top, right, top + barHeight)

            canvas.drawRoundRect(rect, 18f, 18f, barPaint)

            val textY = rect.centerY() - (textPaint.descent() + textPaint.ascent()) / 2
            canvas.drawText(value.toString(), rect.centerX(), textY, textPaint)
        }
    }
}
