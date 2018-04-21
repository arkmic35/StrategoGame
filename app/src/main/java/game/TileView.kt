package game

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayout
import android.util.AttributeSet
import android.view.View
import com.arkmic35.stratego.R
import java.util.*

class TileView : View, Observer {
    var field: Field? = null
    private var tileWidth: Int = 0
    private var tileHeight: Int = 0
    private var margin: Int = 0
    var rowIndex = -1
    var columnIndex = -1

    constructor(context: Context, width: Int, height: Int, margin: Int, rowIndex: Int, columnIndex: Int) : super(context) {
        this.tileWidth = width
        this.tileHeight = height
        this.margin = margin
        this.rowIndex = rowIndex
        this.columnIndex = columnIndex
        setBackgroundColor(ContextCompat.getColor(context, R.color.colorTileGrey))
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        val params = this.layoutParams
        params as GridLayout.LayoutParams
        params.width = tileWidth
        params.height = tileHeight
        params.marginEnd = margin
        params.bottomMargin = margin
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    @Suppress("unused")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun update(o: Observable?, arg: Any?) {
        setBackgroundColor(field!!.player!!.color)
    }
}
