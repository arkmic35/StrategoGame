package game

import android.arch.lifecycle.MutableLiveData
import android.databinding.BaseObservable
import android.databinding.Bindable
import com.arkmic35.stratego.BR
import simulation.MinMax
import java.util.*

class Player(val playerID: Int, val playerName: String, val playerType: PlayerType, val color: Int) : BaseObservable() {
    enum class PlayerType {
        HUMAN,
        CPU_RANDOM,
        CPU_GREEDY,
        CPU_MIN_MAX,
        CPU_ALPHA_BETA,
        SIMULATOR
    }

    val messageLiveData = MutableLiveData<String>()

    @Suppress("MemberVisibilityCanBePrivate")
    @Bindable
    var points: Int = 0

    constructor(other: Player) : this(other.playerID, other.playerName, other.playerType, other.color) {
        points = other.points
    }

    @Bindable
    fun getPointsString(): String {
        return "$points pkt."
    }

    private fun addPoints(pointsToAdd: Int) {
        points += pointsToAdd
        notifyPropertyChanged(BR.points)
        notifyPropertyChanged(BR.pointsString)
    }

    fun addPoints(pointsToAdd: Int, message: String) {
        messageLiveData.postValue(message)
        addPoints(pointsToAdd)
    }

    fun makeAIMovement(board: Board, players: Array<Player>) {
        val random = Random()

        when (playerType) {
            Player.PlayerType.HUMAN -> {
                return
            }

            Player.PlayerType.CPU_RANDOM -> {
                val randomPair = board.freeFields[random.nextInt(board.freeFields.size)]
                board.markField(this, randomPair.first, randomPair.second)
            }

            Player.PlayerType.CPU_GREEDY -> {
                val boardCopy = Board(board)
                val possibleMovements = board.freeFields
                val pointsForMovements = IntArray(possibleMovements.size, { index ->
                    boardCopy.getPointsForMarkingField(possibleMovements[index].first, possibleMovements[index].second)
                })

                val bestMovementIndex = (pointsForMovements.indices.maxBy { it -> pointsForMovements[it] })!!
                board.markField(this, possibleMovements[bestMovementIndex].first, possibleMovements[bestMovementIndex].second)
            }

            Player.PlayerType.CPU_MIN_MAX -> {
                val simulator = MinMax(players, players.indexOf(this), board)
                val result = simulator.runSimulation()
                board.markField(this, result.first, result.second)
            }

            Player.PlayerType.CPU_ALPHA_BETA -> {
                TODO()
            }

            Player.PlayerType.SIMULATOR -> {
            }
        }
    }

    override fun toString(): String {
        return playerName
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Player

        if (playerID != other.playerID) return false
        return true
    }

    override fun hashCode(): Int {
        return playerID
    }
}