package game.model.player

import game.model.Board

class CpuAlphaBetaPlayer(playerID: Int, playerName: String, color: Int) : CpuPlayer(playerID, playerName, color) {

    constructor(other: CpuAlphaBetaPlayer) : this(other.playerID, other.playerName, other.color) {
        points = other.points
    }

    override fun makeAIMovement(board: Board, players: Array<Player>) {
        TODO("not implemented")
    }

    override fun clone() = CpuAlphaBetaPlayer(this)
}