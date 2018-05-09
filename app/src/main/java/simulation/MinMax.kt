package simulation

import game.Player
import game.model.Board
import helper.CyclingArrayIterator

class MinMax(allPlayers: Array<Player>, private val mainPlayerIndex: Int, board: Board) {
    private val allPlayers = Array(allPlayers.size, { arrayIndex ->
        Player(allPlayers[arrayIndex])
    })
    private val amountOfPlayers = allPlayers.size
    private val board = Board(board)

    fun runSimulation(): Pair<Int, Int> {
        val iterator = CyclingArrayIterator(allPlayers)
        iterator.currentIndex = mainPlayerIndex
        iterator.next()

        val possibleMovements = board.freeFields
        val scoreboardsForMovements = Array(possibleMovements.size, { IntArray(allPlayers.size) })

        possibleMovements.forEachIndexed { index, movement ->
            val newBoard = Board(board)
            newBoard.markField(allPlayers[mainPlayerIndex], movement.first, movement.second)
            scoreboardsForMovements[index] = simulateTree(CyclingArrayIterator(iterator), newBoard, IntArray(amountOfPlayers), 1)
        }

        val bestMovementIndex = scoreboardsForMovements.indices.maxBy { index -> scoreboardsForMovements[index][mainPlayerIndex] }
        return possibleMovements[bestMovementIndex!!]
    }

    private fun simulateTree(iterator: CyclingArrayIterator<Player>, currentBoard: Board, currentScoreboard: IntArray, stepNumber: Int): IntArray {
        if (stepNumber >= 5 || currentBoard.freeFields.isEmpty()) {
            return currentScoreboard
        }

        val currentPlayer = iterator.current()
        val possibleMovements = currentBoard.freeFields
        val movementPoints = IntArray(possibleMovements.size)
        val selectedIndex: Int?
        val selectedMove: Pair<Int, Int>?

        possibleMovements.forEachIndexed { index, pair ->
            movementPoints[index] = currentBoard.getPointsForMarkingField(pair.first, pair.second)
        }

        selectedIndex = if (currentPlayer.playerID == allPlayers[mainPlayerIndex].playerID) { //szukanie max
            movementPoints.indices.maxBy { index -> movementPoints[index] }
        } else { //szukanie min
            movementPoints.indices.minBy { index -> movementPoints[index] }
        }

        iterator.next()
        selectedMove = possibleMovements[selectedIndex!!]
        currentBoard.markField(currentPlayer, selectedMove.first, selectedMove.second)
        currentScoreboard[iterator.currentIndex] += movementPoints[selectedIndex]
        return simulateTree(iterator, currentBoard, currentScoreboard, stepNumber + 1)
    }

}