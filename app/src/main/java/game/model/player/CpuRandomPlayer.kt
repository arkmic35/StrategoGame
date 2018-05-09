package game.model.player

import game.model.Board
import java.util.*

class CpuRandomPlayer(playerID: Int, playerName: String, color: Int) : CpuPlayer(playerID, playerName, color) {
    private var random = Random()

    constructor(other: CpuRandomPlayer) : this(other.playerID, other.playerName, other.color) {
        points = other.points
    }

    override fun makeAIMovement(board: Board, players: Array<Player>) {
        val randomPair = board.freeFields[random.nextInt(board.freeFields.size)]

        board.markField(this, randomPair.first, randomPair.second)
    }

    override fun clone() = CpuRandomPlayer(this)
}