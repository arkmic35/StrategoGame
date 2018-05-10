package game.model.player

import game.model.Board
import io.reactivex.Observable

abstract class CpuPlayer(playerID: Int, playerName: String, color: Int) : Player(playerID, playerName, color) {
    abstract fun makeAIMovement(board: Board, players: Array<Player>): Observable<Void>
}