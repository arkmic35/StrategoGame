package game

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class TileView : View {
    private val paint: Paint = Paint()
    var rectangle: RectF? = null

    constructor(context: Context?, x: Float, y: Float, width: Float, height: Float) : super(context) {
        this.rectangle = RectF(x, y, x + width, y + height)
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    @Suppress("unused")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun onDraw(canvas: Canvas) {
        if (rectangle != null) {
            paint.color = Color.GRAY
            canvas.drawRect(rectangle, paint)
        }
    }
}
