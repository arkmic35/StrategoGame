package game.model.player

import game.model.Board

class CpuGreedyPlayer(playerID: Int, playerName: String, color: Int) : CpuPlayer(playerID, playerName, color) {

    constructor(other: CpuGreedyPlayer) : this(other.playerID, other.playerName, other.color) {
        points = other.points
    }

    override fun makeAIMovement(board: Board, players: Array<Player>) {
        val boardCopy = Board(board)

        val possibleMovements = boardCopy.freeFields
        val pointsForMovements = IntArray(possibleMovements.size, { index ->
            boardCopy.getPointsForMarkingField(possibleMovements[index].first, possibleMovements[index].second)
        })

        val bestMovementIndex = (pointsForMovements.indices.maxBy { it -> pointsForMovements[it] })!!
        board.markField(this, possibleMovements[bestMovementIndex].first, possibleMovements[bestMovementIndex].second)
    }

    override fun clone() = CpuGreedyPlayer(this)
}