package game.model.player

import game.model.Board
import simulation.MinMax

class CpuMinMaxPlayer(playerID: Int, playerName: String, color: Int) : CpuPlayer(playerID, playerName, color) {
    constructor(other: CpuMinMaxPlayer) : this(other.playerID, other.playerName, other.color) {
        points = other.points
    }

    override fun makeAIMovement(board: Board, players: Array<Player>) {
        val simulator = MinMax(players, players.indexOf(this), board)
        val result = simulator.runSimulation()
        board.markField(this, result.first, result.second)
    }

    override fun clone() = CpuMinMaxPlayer(this)
}
