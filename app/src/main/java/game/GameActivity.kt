package game

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.FrameLayout
import com.arkmic35.stratego.R

class GameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val tile = TileView(this)
        tile.startPosition = Pair(100F, 100F)
        tile.endPosition = Pair(200F, 200F)

        val tableFrame = findViewById<FrameLayout>(R.id.tableFrame)
        tableFrame.addView(tile)
    }
}
