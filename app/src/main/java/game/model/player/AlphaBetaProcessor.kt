package game.model.player

import game.model.Board
import kotlin.math.max
import kotlin.math.min

class AlphaBetaProcessor(players: Array<Player>, private val myPlayerIndex: Int, board: Board) {
    private val players = Array(players.size, { arrayIndex ->
        players[arrayIndex].clone()
    })
    private val amountOfPlayers = players.size
    private val board = Board(board, true)

    private var selectedField: Pair<Int, Int>? = null
    private var aiDepth = 4

    fun calculate(aiDepth: Int): Pair<Int, Int> {
        this.aiDepth = aiDepth
        recursive(board, myPlayerIndex, 0, Int.MIN_VALUE, Int.MAX_VALUE, 0)
        return selectedField!!
    }

    private fun recursive(board: Board, currentPlayerIndex: Int, pointsDifference: Int, alpha: Int, beta: Int, depth: Int): Int {
        if (board.freeFields.isEmpty() || depth > aiDepth) {
            return pointsDifference
        }

        val nextPlayer =
                if (currentPlayerIndex + 1 == amountOfPlayers)
                    0
                else
                    currentPlayerIndex + 1

        val freeFieldsCopy = ArrayList(board.freeFields)
        var varAlpha = alpha
        var varBeta = beta

        if (currentPlayerIndex == myPlayerIndex) {
            val pointsForFields = IntArray(freeFieldsCopy.size, { index ->
                if (varBeta <= varAlpha) {
                    Int.MIN_VALUE
                } else {
                    val position = freeFieldsCopy[index]
                    val points = board.getPointsForMarkingField(position.first, position.second)

                    board.markField(players[currentPlayerIndex], position.first, position.second)
                    val result = recursive(board, nextPlayer, pointsDifference + points, varAlpha, varBeta, depth + 1)
                    board.unmarkField(position.first, position.second)

                    varAlpha = max(varAlpha, result)
                    result
                }
            })

            val bestIndex = pointsForFields.indices.maxBy { it -> pointsForFields[it] }!!

            if (depth == 0) {
                selectedField = freeFieldsCopy[bestIndex]
            }

            return pointsForFields[bestIndex]
        } else {
            val pointsForFields = IntArray(freeFieldsCopy.size, { index ->
                if (varBeta <= varAlpha) {
                    Int.MAX_VALUE
                } else {
                    val position = freeFieldsCopy[index]
                    val points = board.getPointsForMarkingField(position.first, position.second)

                    board.markField(players[currentPlayerIndex], position.first, position.second)
                    val result = recursive(board, nextPlayer, pointsDifference - points, varAlpha, varBeta, depth + 1)
                    board.unmarkField(position.first, position.second)

                    varBeta = min(varBeta, result)
                    result
                }
            })

            val worstIndex = pointsForFields.indices.minBy { it -> pointsForFields[it] }!!
            return pointsForFields[worstIndex]
        }
    }
}