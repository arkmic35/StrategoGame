package game

import java.security.InvalidAlgorithmParameterException

class Board(val size: Int) {
    private val lastIndex = size - 1
    private val fields = Array(size, { Array<Player?>(size, { null }) })

    fun markField(player: Player, rowIndex: Int, columnIndex: Int) {
        var pointsToAdd = 0

        if (fields[rowIndex][columnIndex] != null) {
            throw InvalidAlgorithmParameterException()
        }

        fields[rowIndex][columnIndex] = player

        if (isSidePosition(rowIndex, columnIndex)) {
            if (isColumnFull(columnIndex)) {
                pointsToAdd += size
            }

            if (isRowFull(rowIndex)) {
                pointsToAdd += size
            }

            pointsToAdd += getPointsFromDiagonals(rowIndex, columnIndex)
            player.addPoints(pointsToAdd)
        }
    }

    private fun isFieldPositionCorrect(rowIndex: Int, columnIndex: Int): Boolean {
        return rowIndex in 0..lastIndex && columnIndex in 0..lastIndex
    }

    private fun isSidePosition(rowIndex: Int, columnIndex: Int): Boolean {
        return rowIndex == 0 || columnIndex == 0 || rowIndex == lastIndex || columnIndex == 0
    }

    private fun isColumnFull(columnIndex: Int): Boolean {
        for (rowIndex in 0..lastIndex) {
            if (fields[rowIndex][columnIndex] == null) {
                return false
            }
        }
        return true
    }

    private fun isRowFull(rowIndex: Int): Boolean {
        for (columnIndex in 0..lastIndex) {
            if (fields[rowIndex][columnIndex] == null) {
                return false
            }
        }
        return true
    }

    private fun getPointsFromDiagonals(rowIndex: Int, columnIndex: Int): Int {
        var pointsToAdd = 0
        var checkRow = rowIndex + 1
        var checkColumn = columnIndex + 1

        while (isFieldPositionCorrect(++checkRow, ++checkColumn) && fields[checkRow][checkColumn] != null) {
            if (isSidePosition(checkRow, checkColumn)) {
                pointsToAdd += Math.abs(checkRow - rowIndex)
            }
        }

        while (isFieldPositionCorrect(++checkRow, --checkColumn) && fields[checkRow][checkColumn] != null) {
            if (isSidePosition(checkRow, checkColumn)) {
                pointsToAdd += Math.abs(checkRow - rowIndex)
            }
        }

        while (isFieldPositionCorrect(--checkRow, ++checkColumn) && fields[checkRow][checkColumn] != null) {
            if (isSidePosition(checkRow, checkColumn)) {
                pointsToAdd += Math.abs(checkRow - rowIndex)
            }
        }

        while (isFieldPositionCorrect(--checkRow, --checkColumn) && fields[checkRow][checkColumn] != null) {
            if (isSidePosition(checkRow, checkColumn)) {
                pointsToAdd += Math.abs(checkRow - rowIndex)
            }
        }

        if (pointsToAdd != 0) {
            pointsToAdd++
        }

        return pointsToAdd
    }
}