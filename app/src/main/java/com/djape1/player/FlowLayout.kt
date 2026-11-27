package com.djape1.player

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup

/** Απλή διάταξη που αλλάζει γραμμή όταν τα παιδιά δεν χωράνε (για τα chips σε στενές οθόνες). */
class FlowLayout @JvmOverloads constructor(c: Context, a: AttributeSet? = null) : ViewGroup(c, a) {

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams = MarginLayoutParams(context, attrs)
    override fun generateDefaultLayoutParams(): LayoutParams = MarginLayoutParams(WRAP_CONTENT, WRAP_CONTENT)
    override fun generateLayoutParams(p: LayoutParams?): LayoutParams = MarginLayoutParams(p)
    override fun checkLayoutParams(p: LayoutParams?): Boolean = p is MarginLayoutParams

    override fun onMeasure(wSpec: Int, hSpec: Int) {
        val maxW = MeasureSpec.getSize(wSpec) - paddingLeft - paddingRight
        var x = 0; var y = 0; var rowH = 0; var usedW = 0
        for (i in 0 until childCount) {
            val v = getChildAt(i)
            if (v.visibility == View.GONE) continue
            measureChildWithMargins(v, wSpec, 0, hSpec, 0)
            val lp = v.layoutParams as MarginLayoutParams
            val w = v.measuredWidth + lp.leftMargin + lp.rightMargin
            val h = v.measuredHeight + lp.topMargin + lp.bottomMargin
            if (x > 0 && x + w > maxW) { y += rowH; x = 0; rowH = 0 }
            x += w; rowH = maxOf(rowH, h); usedW = maxOf(usedW, x)
        }
        val total = y + rowH + paddingTop + paddingBottom
        setMeasuredDimension(
            resolveSize(usedW + paddingLeft + paddingRight, wSpec),
            resolveSize(total, hSpec)
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val maxW = r - l - paddingLeft - paddingRight
        var x = 0; var y = 0; var rowH = 0
        for (i in 0 until childCount) {
            val v = getChildAt(i)
            if (v.visibility == View.GONE) continue
            val lp = v.layoutParams as MarginLayoutParams
            val w = v.measuredWidth + lp.leftMargin + lp.rightMargin
            val h = v.measuredHeight + lp.topMargin + lp.bottomMargin
            if (x > 0 && x + w > maxW) { y += rowH; x = 0; rowH = 0 }
            val left = paddingLeft + x + lp.leftMargin
            val top = paddingTop + y + lp.topMargin
            v.layout(left, top, left + v.measuredWidth, top + v.measuredHeight)
            x += w; rowH = maxOf(rowH, h)
        }
    }
}
