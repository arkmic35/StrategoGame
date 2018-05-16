package game.model.player

import game.model.Board
import io.reactivex.Emitter
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class CpuMinMaxPlayer(playerID: Int, playerName: String, color: Int) : CpuPlayer(playerID, playerName, color) {
    var aiDepth = 4
    var greedyTreshold = 16

    constructor(other: CpuMinMaxPlayer) : this(other.playerID, other.playerName, other.color) {
        points = other.points
    }

    override fun makeAIMovement(board: Board, players: Array<Player>): Observable<Void> {
        val processor = MinMaxProcessor(players, players.indexOf(this), board)

        return Observable.create({ emitterOutside: Emitter<Void> ->
            val observable = Observable.create(ObservableOnSubscribe<Pair<Int, Int>> { emitter ->
                emitter.onNext(processor.calculate(aiDepth, greedyTreshold))
                emitter.onComplete()
            }).subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())

            observable.subscribe(
                    { result ->
                        board.markField(this, result.first, result.second)
                        emitterOutside.onComplete()
                    },
                    { throwable ->
                        throwable.printStackTrace()
                    })
        })
    }


    override fun clone() = CpuMinMaxPlayer(this)
}
