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
import helper.CyclingArrayIterator
import java.util.*


class GameActivity : AppCompatActivity(), GameOverDialog.GameOverDialogListener {
    private var boardSize = 0
    private var board: Board? = null
    private var binding: ActivityGameBinding? = null

    private lateinit var tiles: Array<Array<TileView>>
    private var playerTypes: Array<Player.PlayerType>? = null
    private var tableFrame: GridLayout? = null
    private var players: Array<Player>? = null

    private var playersIterator: CyclingArrayIterator<Player>? = null
    private var currentPlayer: Player? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_game)
        tableFrame = findViewById(R.id.tableFrame)

        val extras = intent.extras
        boardSize = extras.getInt("BOARD_SIZE")
        playerTypes = Array(2, { playerNumber ->
            Player.PlayerType.values()[extras.getInt("PLAYER${playerNumber + 1}_TYPE")]
        })

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
        val colors = intArrayOf(ContextCompat.getColor(this, R.color.colorTileBlue), ContextCompat.getColor(this, R.color.colorTileOrange))
        players = Array(2, { playerID ->
            val player =
                    if (playerTypes!![playerID] == Player.PlayerType.HUMAN) {
                        Player(playerID, "Gracz ${playerID + 1}", Player.PlayerType.HUMAN, colors[playerID])
                    } else {
                        Player(playerID, playerTypes!![playerID].name, playerTypes!![playerID], colors[playerID])
                    }

            player.messageLiveData.observe(this, android.arch.lifecycle.Observer { message ->
                if (message != null) {
                    Snackbar.make(findViewById(R.id.mainLayout), message, Snackbar.LENGTH_INDEFINITE).show()
                }
            })

            player
        })

        playersIterator = CyclingArrayIterator(players!!)
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
            currentPlayer = playersIterator!!.next()
            findViewById<TextView>(R.id.gameStatus).text = String.format(Locale.getDefault(), getString(R.string.game_currently), currentPlayer)

            if (currentPlayer!!.playerType != Player.PlayerType.HUMAN) {
                currentPlayer!!.makeAIMovement(board!!)
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
