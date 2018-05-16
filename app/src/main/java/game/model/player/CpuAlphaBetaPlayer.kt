package game.model.player

import game.model.Board
import io.reactivex.Emitter
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class CpuAlphaBetaPlayer(playerID: Int, playerName: String, color: Int) : CpuPlayer(playerID, playerName, color) {
    var aiDepth = 4
    var greedyTreshold = 16
    var shuffleFreeFieldsArray = true

    constructor(other: CpuAlphaBetaPlayer) : this(other.playerID, other.playerName, other.color) {
        points = other.points
    }

    override fun makeAIMovement(board: Board, players: Array<Player>): Observable<Void> {
        val processor = AlphaBetaProcessor(players, players.indexOf(this), board)

        return Observable.create({ emitterOutside: Emitter<Void> ->
            val observable = Observable.create(ObservableOnSubscribe<Pair<Int, Int>> { emitter ->
                emitter.onNext(processor.calculate(aiDepth, greedyTreshold, shuffleFreeFieldsArray))
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

    override fun clone() = CpuAlphaBetaPlayer(this)
}