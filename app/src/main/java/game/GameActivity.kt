package game

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayout
import android.widget.TextView
import com.arkmic35.stratego.R
import java.util.*


class GameActivity : AppCompatActivity() {
    private var boardSize = 0
    private var board: Board? = null

    private lateinit var tiles: Array<Array<TileView>>
    private var gameMode: GameMode? = null
    private var tableFrame: GridLayout? = null
    private var players: Array<Player>? = null

    private var playersIterator: Iterator<Player>? = null
    private var currentPlayer: Player? = null

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
        board = Board(boardSize)
        drawTiles()

        when (extras.getInt("GAME_MODE")) {
            0 -> gameMode = GameMode.PLAYER_VS_PHONE
            1 -> gameMode = GameMode.PLAYER_VS_PLAYER
            2 -> gameMode = GameMode.PHONE_VS_PHONE
        }

        createPlayers()
    }

    private fun drawTiles() {
        val gridLayout = tableFrame!!
        gridLayout.columnCount = boardSize
        gridLayout.rowCount = boardSize

        val tileWidth = resources.getDimension(R.dimen.tiles_tile_width)
        val tileHeight = resources.getDimension(R.dimen.tiles_tile_height)
        val spaceBetweenTiles = resources.getDimension(R.dimen.tiles_space_between)

        tiles = Array(boardSize, { rowIndex ->
            Array(boardSize, { columnIndex ->
                val tileView = TileView(
                        this,
                        tileWidth.toInt(),
                        tileHeight.toInt(),
                        spaceBetweenTiles.toInt(),
                        rowIndex,
                        columnIndex)

                val field = board!!.fields[rowIndex][columnIndex]

                tileView.field = field
                field.addObserver(tileView)

                tileView.setOnClickListener({ view ->
                    (view as TileView)
                    humanPlayerAction(view.rowIndex, view.columnIndex)
                })

                gridLayout.addView(tileView)
                tileView
            })
        })
    }

    private fun createPlayers() {
        when (gameMode) {
            GameMode.PLAYER_VS_PHONE -> {
                players = arrayOf(Player("Gracz", Player.PlayerType.PLAYER_HUMAN, Color.RED), Player("Telefon", Player.PlayerType.PLAYER_CPU, Color.BLACK))
            }

            GameMode.PLAYER_VS_PLAYER -> {
                players = arrayOf(Player("Gracz 1", Player.PlayerType.PLAYER_HUMAN, Color.RED), Player("Gracz 2", Player.PlayerType.PLAYER_HUMAN, Color.BLACK))
            }

            GameMode.PHONE_VS_PHONE -> {
                players = arrayOf(Player("Telefon 1", Player.PlayerType.PLAYER_CPU, Color.RED), Player("Telefon 2", Player.PlayerType.PLAYER_CPU, Color.BLACK))
            }
        }

        nextPlayer()
    }

    private fun humanPlayerAction(rowIndex: Int?, columnIndex: Int?) {
        if (rowIndex != null && columnIndex != null && board!!.isFieldFree(rowIndex, columnIndex)) {
            board!!.markField(currentPlayer!!, rowIndex, columnIndex)
            nextPlayer()
        }
    }

    private fun nextPlayer() {
        if (board!!.freeSpots > 0) {
            if (playersIterator == null || !playersIterator!!.hasNext()) {
                playersIterator = players!!.iterator()
            }

            currentPlayer = playersIterator!!.next()
            findViewById<TextView>(R.id.gameStatus).text = String.format(Locale.getDefault(), getString(R.string.game_currently), currentPlayer)

            if (currentPlayer!!.playerType == Player.PlayerType.PLAYER_CPU) {
                currentPlayer!!.makeRandomMovement(board!!)
                nextPlayer()
            }

        } else {
            TODO("Koniec gry")
        }
    }
}
