package game

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class TileView : View {
    private val paint: Paint = Paint()
    var startPosition: Pair<Float, Float>? = null
    var endPosition: Pair<Float, Float>? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    @Suppress("unused")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun onDraw(canvas: Canvas) {
        if (startPosition != null && endPosition != null) {
            paint.color = Color.GRAY
            canvas.drawRect(startPosition!!.first, startPosition!!.second, endPosition!!.first, endPosition!!.second, paint)
        }
    }
}
