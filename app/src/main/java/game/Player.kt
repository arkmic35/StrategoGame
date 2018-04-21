package game

import java.util.*

class Player(private val playerName: String, val playerType: PlayerType, val color: Int) {
    enum class PlayerType {
        PLAYER_HUMAN,
        PLAYER_CPU
    }

    private var points: Int = 0

    fun addPoints(pointsToAdd: Int) {
        points += pointsToAdd
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