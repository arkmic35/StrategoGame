package game.model.player

import game.model.Board
import io.reactivex.Observable

class CpuGreedyPlayer(playerID: Int, playerName: String, color: Int) : CpuPlayer(playerID, playerName, color) {

    constructor(other: CpuGreedyPlayer) : this(other.playerID, other.playerName, other.color) {
        points = other.points
    }

    override fun makeAIMovement(board: Board, players: Array<Player>): Observable<Void> {
        return Observable.create({ emitter ->
            //            val boardCopy = Board(board)
//
//            val possibleMovements = boardCopy.freeFields
//            val pointsForMovements = IntArray(possibleMovements.size, { index ->
//                boardCopy.getPointsForMarkingField(possibleMovements[index].first, possibleMovements[index].second)
//            })
//
//            val bestMovementIndex = (pointsForMovements.indices.maxBy { it -> pointsForMovements[it] })!!
            val bestGreedyMovement = board.findBestGreedyField()
            board.markField(this, bestGreedyMovement.first, bestGreedyMovement.second)
            emitter.onComplete()
        })
    }

    override fun clone() = CpuGreedyPlayer(this)
}