package game.model.player

import game.model.Board

class MinMaxProcessor(players: Array<Player>, private val myPlayerIndex: Int, board: Board) {
    private val players = Array(players.size, { arrayIndex ->
        players[arrayIndex].clone()
    })
    private val amountOfPlayers = players.size
    private val board = Board(board, true)

    private var selectedField: Pair<Int, Int>? = null
    private var aiDepth = 4

    fun calculate(aiDepth: Int): Pair<Int, Int> {
        this.aiDepth = aiDepth
        recursive(board, myPlayerIndex, 0, 0)
        return selectedField!!
    }

    private fun recursive(board: Board, currentPlayerIndex: Int, pointsDifference: Int, depth: Int): Int {
        if (board.freeFields.isEmpty() || depth > aiDepth) {
            return pointsDifference
        }

        val nextPlayer =
                if (currentPlayerIndex + 1 == amountOfPlayers)
                    0
                else
                    currentPlayerIndex + 1

        val freeFieldsCopy = ArrayList(board.freeFields)

        if (currentPlayerIndex == myPlayerIndex) {
            val pointsForFields = IntArray(freeFieldsCopy.size, { index ->
                val position = freeFieldsCopy[index]
                val points = board.getPointsForMarkingField(position.first, position.second)

                board.markField(players[currentPlayerIndex], position.first, position.second)
                val result = recursive(board, nextPlayer, pointsDifference + points, depth + 1)
                board.unmarkField(position.first, position.second)

                result
            })

            val bestIndex = pointsForFields.indices.maxBy { it -> pointsForFields[it] }!!

            if (depth == 0) {
                selectedField = freeFieldsCopy[bestIndex]
            }

            return pointsForFields[bestIndex]
        } else {
            val pointsForFields = IntArray(freeFieldsCopy.size, { index ->
                val position = freeFieldsCopy[index]
                val points = board.getPointsForMarkingField(position.first, position.second)

                board.markField(players[currentPlayerIndex], position.first, position.second)
                val result = recursive(board, nextPlayer, pointsDifference - points, depth + 1)
                board.unmarkField(position.first, position.second)

                result
            })

            val worstIndex = pointsForFields.indices.minBy { it -> pointsForFields[it] }!!
            return pointsForFields[worstIndex]
        }
    }
}