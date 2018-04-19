package game

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.FrameLayout
import com.arkmic35.stratego.R

class GameActivity : AppCompatActivity() {
    private var boardSize = 0
    private lateinit var tiles: Array<Array<TileView>>
    private var gameMode: GameMode? = null
    private var tableFrame: FrameLayout? = null

    enum class GameMode {
        PLAYER_VS_PHONE,
        PLAYER_VS_PLAYER,
        PHONE_VS_PHONE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        tableFrame = findViewById(R.id.tableFrame)

        val extras = intent.extras
        boardSize = extras.getInt("BOARD_SIZE")
        drawTiles()

        when (extras.getInt("GAME_MODE")) {
            0 -> gameMode = GameMode.PLAYER_VS_PHONE
            1 -> gameMode = GameMode.PLAYER_VS_PLAYER
            2 -> gameMode = GameMode.PHONE_VS_PHONE
        }
    }

    private fun drawTiles() {
        val marginLeft = resources.getDimension(R.dimen.tiles_margin_left)
        val marginTop = resources.getDimension(R.dimen.tiles_margin_top)
        val tileWidth = resources.getDimension(R.dimen.tiles_tile_width)
        val tileHeight = resources.getDimension(R.dimen.tiles_tile_height)
        val spaceBetweenTiles = resources.getDimension(R.dimen.tiles_space_between)

        tiles = Array(boardSize, { rowIndex ->
            Array(boardSize, { columnIndex ->
                val tileView = TileView(
                        this,
                        marginLeft + columnIndex * (tileWidth + spaceBetweenTiles),
                        marginTop + rowIndex * (tileHeight + spaceBetweenTiles),
                        tileWidth,
                        tileHeight
                )

                tableFrame!!.addView(tileView)
                tileView
            })
        })
    }
}
