package game

import android.databinding.BaseObservable
import android.databinding.Bindable
import com.arkmic35.stratego.BR
import java.util.*

class Player(val playerName: String, val playerType: PlayerType, val color: Int) : BaseObservable() {
    enum class PlayerType {
        PLAYER_HUMAN,
        PLAYER_CPU
    }

    @Suppress("MemberVisibilityCanBePrivate")
    @Bindable
    var points: Int = 0

    @Bindable
    fun getPointsString(): String {
        return "$points pkt."
    }

    fun addPoints(pointsToAdd: Int) {
        points += pointsToAdd
        notifyPropertyChanged(BR.points)
        notifyPropertyChanged(BR.pointsString)
    }

    fun makeRandomMovement(board: Board) {
        var rowIndex: Int
        var columnIndex: Int
        val random = Random()

        do {
            rowIndex = random.nextInt(board.size)
            columnIndex = random.nextInt(board.size)
        } while (!board.isFieldFree(rowIndex, columnIndex))

        board.markField(this, rowIndex, columnIndex)
    }

    override fun toString(): String {
        return playerName
    }
}