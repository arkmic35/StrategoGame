package game

import android.arch.lifecycle.MutableLiveData
import android.databinding.BaseObservable
import android.databinding.Bindable
import com.arkmic35.stratego.BR
import java.util.*

class Player(val playerName: String, val playerType: PlayerType, val color: Int) : BaseObservable() {
    enum class PlayerType {
        HUMAN,
        CPU_RANDOM,
        CPU_MINMAX,
        CPU_ALPHABETA
    }

    val messageLiveData = MutableLiveData<String>()

    @Suppress("MemberVisibilityCanBePrivate")
    @Bindable
    var points: Int = 0

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

    fun makeAIMovement(board: Board) {
        var rowIndex: Int
        var columnIndex: Int
        val random = Random()

        when (playerType) {
            Player.PlayerType.HUMAN -> {
                return
            }

            Player.PlayerType.CPU_RANDOM -> {
                val randomPair = board.freeFields[random.nextInt(board.freeFields.size)]
                board.markField(this, randomPair.first, randomPair.second)
            }

            Player.PlayerType.CPU_MINMAX -> {
                TODO()
            }

            Player.PlayerType.CPU_ALPHABETA -> {
                TODO()
            }
        }
    }

    override fun toString(): String {
        return playerName
    }
}