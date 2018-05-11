package game

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayout
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.arkmic35.stratego.R
import com.arkmic35.stratego.databinding.ActivityGameBinding
import game.model.Board
import game.model.player.*
import helper.CyclingArrayIterator
import io.reactivex.disposables.Disposable
import java.security.InvalidParameterException
import java.util.*


class GameActivity : AppCompatActivity(), GameOverDialog.GameOverDialogListener {
    private var boardSize = 0
    private var board: Board? = null
    private var binding: ActivityGameBinding? = null

    private lateinit var tiles: Array<Array<TileView>>
    private var playerTypes: Array<Int>? = null
    private var tableFrame: GridLayout? = null
    private var players: Array<Player>? = null

    private var playersIterator: CyclingArrayIterator<Player>? = null
    private var currentPlayer: Player? = null
    private var ongoingAISubscription: Disposable? = null

    private var gameStatusText: TextView? = null
    private var progressBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_game)
        tableFrame = findViewById(R.id.tableFrame)
        gameStatusText = findViewById(R.id.gameStatus)
        progressBar = findViewById(R.id.progressBar)

        val extras = intent.extras
        boardSize = extras.getInt("BOARD_SIZE")
        playerTypes = Array(2, { playerNumber ->
            extras.getInt("PLAYER${playerNumber + 1}_TYPE")
        })

        prepareBoard()
    }

    override fun onDestroy() {
        if (ongoingAISubscription != null) {
            ongoingAISubscription!!.dispose()
        }

        super.onDestroy()
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

        val playerSuffixes = Array(2, { index ->
            if (playerTypes!![0] == playerTypes!![1]) {
                String.format(" %d", index + 1)
            } else {
                ""
            }
        })

        players = Array(2, { playerId ->
            val player =
                    when (playerTypes!![playerId]) {
                        0 -> HumanPlayer(playerId, "Gracz${playerSuffixes[playerId]}", colors[playerId])
                        1 -> CpuRandomPlayer(playerId, "Random${playerSuffixes[playerId]}", colors[playerId])
                        2 -> CpuGreedyPlayer(playerId, "Zachłanny${playerSuffixes[playerId]}", colors[playerId])
                        3 -> CpuMinMaxPlayer(playerId, "MinMax${playerSuffixes[playerId]}", colors[playerId])
                        4 -> CpuAlphaBetaPlayer(playerId, "AlfaBeta${playerSuffixes[playerId]}", colors[playerId])
                        else -> throw InvalidParameterException("Unknown type of player")
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
        if (ongoingAISubscription == null && rowIndex != null && columnIndex != null && board!!.isFieldFree(rowIndex, columnIndex)) {
            board!!.markField(currentPlayer!!, rowIndex, columnIndex)
            nextPlayer()
        }
    }

    private fun nextPlayer() {
        if (!board!!.freeFields.isEmpty()) {
            currentPlayer = playersIterator!!.next()

            if (currentPlayer!! is CpuPlayer) {
                val player = currentPlayer!! as CpuPlayer

                gameStatusText!!.visibility = View.GONE
                progressBar!!.visibility = View.VISIBLE

                ongoingAISubscription = player.makeAIMovement(board!!, players!!).subscribe({}, {}, {
                    progressBar!!.visibility = View.GONE
                    nextPlayer()
                    ongoingAISubscription = null
                })

            } else {
                gameStatusText!!.text = String.format(Locale.getDefault(), getString(R.string.game_currently), currentPlayer)

                gameStatusText!!.visibility = View.VISIBLE
                progressBar!!.visibility = View.GONE
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
