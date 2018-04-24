package game

import android.content.Context
import android.view.View
import android.view.ViewGroup

class FixedLayout(context: Context) : ViewGroup(context) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        measureChildren(widthMeasureSpec, heightMeasureSpec)

        var width = 0
        var height = 0

        val count = childCount
        for (i in 0 until count) {
            val child = getChildAt(i)
            if (child.visibility != View.GONE) {
                val lp = child.layoutParams as FixedLayout.LayoutParams
                val right = lp.x + child.measuredWidth
                val bottom = lp.y + child.measuredHeight
                width = Math.max(width, right)
                height = Math.max(height, bottom)
            }
        }

        height = Math.max(height, suggestedMinimumHeight)
        width = Math.max(width, suggestedMinimumWidth)
        width = View.resolveSize(width, widthMeasureSpec)
        height = View.resolveSize(height, heightMeasureSpec)
        setMeasuredDimension(width, height)

    }

    override fun generateDefaultLayoutParams(): ViewGroup.LayoutParams {
        return LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0, 0)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val count = childCount
        for (i in 0 until count) {
            val child = getChildAt(i)
            if (child.visibility != View.GONE) {
                val lp = child.layoutParams as FixedLayout.LayoutParams
                child.layout(lp.x, lp.y, lp.x + child.measuredWidth, lp.y + child.measuredHeight)
            }
        }
    }

    override fun checkLayoutParams(p: ViewGroup.LayoutParams): Boolean {
        return p is FixedLayout.LayoutParams
    }

    override fun generateLayoutParams(p: ViewGroup.LayoutParams): ViewGroup.LayoutParams {
        return LayoutParams(p)
    }

    class LayoutParams : ViewGroup.LayoutParams {

        var x: Int = 0
        var y: Int = 0

        constructor(width: Int, height: Int, x: Int, y: Int) : super(width, height) {
            this.x = x
            this.y = y
        }

        constructor(source: ViewGroup.LayoutParams) : super(source) {}

    }
}