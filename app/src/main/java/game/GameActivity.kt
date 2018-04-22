package game

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayout
import android.widget.TextView
import com.arkmic35.stratego.R
import com.arkmic35.stratego.databinding.ActivityGameBinding
import java.util.*


class GameActivity : AppCompatActivity(), GameOverDialog.GameOverDialogListener {
    private var boardSize = 0
    private var board: Board? = null
    private var binding: ActivityGameBinding? = null

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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_game)
        tableFrame = findViewById(R.id.tableFrame)

        val extras = intent.extras
        boardSize = extras.getInt("BOARD_SIZE")

        when (extras.getInt("GAME_MODE")) {
            0 -> gameMode = GameMode.PLAYER_VS_PHONE
            1 -> gameMode = GameMode.PLAYER_VS_PLAYER
            2 -> gameMode = GameMode.PHONE_VS_PHONE
        }

        prepareBoard()
    }

    private fun prepareBoard() {
        board = Board(boardSize)
        drawTiles()

        createPlayers()
        binding!!.player1 = players!![0]
        binding!!.player2 = players!![1]
    }

    private fun drawTiles() {
        val gridLayout = tableFrame!!
        gridLayout.columnCount = boardSize
        gridLayout.rowCount = boardSize
        gridLayout.removeAllViews()

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
        val colors = intArrayOf(ContextCompat.getColor(this, R.color.colorTileRed), ContextCompat.getColor(this, R.color.colorTileBlack))

        when (gameMode) {
            GameMode.PLAYER_VS_PHONE -> {
                players = arrayOf(Player("Gracz", Player.PlayerType.PLAYER_HUMAN, colors[0]), Player("Telefon", Player.PlayerType.PLAYER_CPU, colors[1]))
            }

            GameMode.PLAYER_VS_PLAYER -> {
                players = arrayOf(Player("Gracz 1", Player.PlayerType.PLAYER_HUMAN, colors[0]), Player("Gracz 2", Player.PlayerType.PLAYER_HUMAN, colors[1]))
            }

            GameMode.PHONE_VS_PHONE -> {
                players = arrayOf(Player("Telefon 1", Player.PlayerType.PLAYER_CPU, colors[0]), Player("Telefon 2", Player.PlayerType.PLAYER_CPU, colors[1]))
            }
        }

        players!![0].messageLiveData.observe(this, android.arch.lifecycle.Observer { message ->
            if (message != null) {
                Snackbar.make(findViewById(R.id.mainLayout), message, Snackbar.LENGTH_INDEFINITE).show()
            }
        })

        players!![1].messageLiveData.observe(this, android.arch.lifecycle.Observer { message ->
            if (message != null) {
                Snackbar.make(findViewById(R.id.mainLayout), message, Snackbar.LENGTH_INDEFINITE).show()
            }
        })

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
            val dialog = GameOverDialog()
            val bundle = Bundle()
            val points1 = players!![0].points
            val points2 = players!![1].points

            bundle.putString("WINNER",
                    when {
                        points1 > points2 -> players!![0].playerName
                        points1 < points2 -> players!![1].playerName
                        else -> null
                    })

            dialog.arguments = bundle
            dialog.show(supportFragmentManager, "GAME_OVER")
        }
    }

    override fun playAgainClick() {
        prepareBoard()
    }

    override fun openSettingsClick() {
        onBackPressed()
    }
}
