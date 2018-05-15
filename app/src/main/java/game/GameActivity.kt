package game

import android.databinding.DataBindingUtil
import android.graphics.Typeface
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayout
import android.view.Gravity
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

    private var playerTypes: Array<Int>? = null
    private var aiDepth = 0
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
        aiDepth = extras.getInt("AIDEPTH")

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
        gridLayout.columnCount = boardSize + 1
        gridLayout.rowCount = boardSize + 1
        gridLayout.removeAllViews()

        val tileWidth = resources.getDimension(R.dimen.tiles_tile_width).toInt()
        val tileHeight = resources.getDimension(R.dimen.tiles_tile_height).toInt()
        val spaceBetweenTiles = resources.getDimension(R.dimen.tiles_space_between).toInt()

        for (rowIndex in 0..boardSize) {
            for (columnIndex in 0..boardSize) {
                val firstRow = rowIndex == 0
                val firstColumn = columnIndex == 0
                val params = GridLayout.LayoutParams()
                params.setGravity(Gravity.CENTER)

                if (firstRow || firstColumn) {
                    val textView = TextView(this)
                    textView.typeface = Typeface.DEFAULT_BOLD
                    textView.layoutParams = params

                    if (firstRow && !firstColumn) { //etykiety kolumn 1,2,3,4,...
                        textView.text = columnIndex.toString()
                    } else if (!firstRow && firstColumn) { //etykiety wierszy A,B,C,D,...
                        textView.text = ('A' + rowIndex - 1).toString()
                        params.marginEnd = 16
                    } else { //punkt (0,0)
                    }

                    gridLayout.addView(textView)
                } else {
                    val tileView = TileView(
                            this,
                            tileWidth,
                            tileHeight,
                            spaceBetweenTiles,
                            rowIndex - 1,
                            columnIndex - 1)

                    val field = board!!.fields[rowIndex - 1][columnIndex - 1]

                    tileView.field = field
                    field.addObserver(tileView)

                    tileView.setOnClickListener({ view ->
                        (view as TileView)
                        humanPlayerAction(view.rowIndex, view.columnIndex)
                    })

                    gridLayout.addView(tileView)
                }
            }
        }
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

            if (player is CpuMinMaxPlayer) {
                player.aiDepth = aiDepth
            }

            if (player is CpuAlphaBetaPlayer) {
                player.aiDepth = aiDepth
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
