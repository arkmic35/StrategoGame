package game.model

import game.Player
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidParameterException

class Board(val size: Int) {
    private var lastIndex = size - 1
    var fields = Array(size, { Array(size, { Field() }) })
    var freeFields = generateAllFields(size)

    companion object {
        fun generateAllFields(size: Int): ArrayList<Pair<Int, Int>> {
            val result = ArrayList<Pair<Int, Int>>(size * size)
            val list = (0 until size).toList()

            for (num1 in list) {
                for (num2 in list) {
                    result.add(Pair(num1, num2))
                }
            }

            return result
        }
    }

    constructor(other: Board) : this(other.size) {
        for (rowIndex in other.fields.indices) {
            for (columnIndex in other.fields.indices) {
                fields[rowIndex][columnIndex] = Field(other.fields[rowIndex][columnIndex])
            }
        }

        freeFields = ArrayList(other.freeFields)
    }

    fun getPointsForMarkingField(rowIndex: Int, columnIndex: Int): Int {
        if (!isFieldPositionCorrect(rowIndex, columnIndex) || !isFieldFree(rowIndex, columnIndex)) {
            throw InvalidAlgorithmParameterException()
        }

        fields[rowIndex][columnIndex].player = Player(999, "Simulator", Player.PlayerType.SIMULATOR, 0)
        val (result, _) = calculatePointsForField(rowIndex, columnIndex, false)
        unmarkField(rowIndex, columnIndex)
        return result
    }

    fun markField(player: Player, rowIndex: Int, columnIndex: Int) {
        if (!isFieldPositionCorrect(rowIndex, columnIndex) || !isFieldFree(rowIndex, columnIndex)) {
            throw InvalidAlgorithmParameterException()
        }

        fields[rowIndex][columnIndex].player = player
        freeFields.remove(Pair(rowIndex, columnIndex))

        val (pointsToAdd, message) = calculatePointsForField(rowIndex, columnIndex, true)
        message.insert(0, player.playerName + " otrzymuje:")

        if (pointsToAdd != 0) {
            player.addPoints(pointsToAdd, message.toString())
        }
    }

    private fun unmarkField(rowIndex: Int, columnIndex: Int) {
        if (!isFieldPositionCorrect(rowIndex, columnIndex) || isFieldFree(rowIndex, columnIndex)) {
            throw InvalidAlgorithmParameterException()
        }

        fields[rowIndex][columnIndex].player = null

        if (!freeFields.contains(Pair(rowIndex, columnIndex))) {
            freeFields.add(Pair(rowIndex, columnIndex))
        }
    }

    private fun calculatePointsForField(rowIndex: Int, columnIndex: Int, composeMessage: Boolean): Pair<Int, StringBuilder> {
        if (!isFieldPositionCorrect(rowIndex, columnIndex)) {
            throw InvalidParameterException()
        }

        var pointsToAdd = 0
        val message = StringBuilder()

        if (isSidePosition(rowIndex, columnIndex)) {
            if (isColumnFull(columnIndex)) {
                pointsToAdd += size

                if (composeMessage) {
                    message.append("\n+$size pkt za kolumnę ${columnIndex + 1}")
                }
            }

            if (isRowFull(rowIndex)) {
                pointsToAdd += size

                if (composeMessage) {
                    message.append("\n+$size pkt za wiersz ${'A' + rowIndex}")
                }
            }

            val topLeftDiagonalStartPoint = findTopLeftDiagonalStart(rowIndex, columnIndex)
            val (topLeftDiagonalPoints, leftDiagonalEndPoint) = checkTopLeftDiagonal(topLeftDiagonalStartPoint.first, topLeftDiagonalStartPoint.second)

            if (topLeftDiagonalPoints > 1) {
                pointsToAdd += topLeftDiagonalPoints

                if (composeMessage) {
                    message.append(
                            String.format("\n+%d pkt za linię \u2198 od %c%d do %c%d",
                                    topLeftDiagonalPoints,
                                    'A' + topLeftDiagonalStartPoint.first,
                                    topLeftDiagonalStartPoint.second + 1,
                                    'A' + leftDiagonalEndPoint.first,
                                    leftDiagonalEndPoint.second + 1
                            )
                    )
                }
            }

            val bottomLeftDiagonalStartPoint = findBottomLeftDiagonalStart(rowIndex, columnIndex)
            val (bottomLeftDiagonalPoints, bottomLeftDiagonalEndPoint) = checkBottomLeftDiagonal(bottomLeftDiagonalStartPoint.first, bottomLeftDiagonalStartPoint.second)

            if (bottomLeftDiagonalPoints > 1) {
                pointsToAdd += bottomLeftDiagonalPoints

                if (composeMessage) {
                    message.append(
                            String.format("\n+%d pkt za linię \u2197 od %c%d do %c%d",
                                    bottomLeftDiagonalPoints,
                                    'A' + bottomLeftDiagonalStartPoint.first,
                                    bottomLeftDiagonalStartPoint.second + 1,
                                    'A' + bottomLeftDiagonalEndPoint.first,
                                    bottomLeftDiagonalEndPoint.second + 1
                            )
                    )
                }
            }
        }

        return Pair(pointsToAdd, message)
    }

    fun isFieldFree(rowIndex: Int, columnIndex: Int): Boolean {
        if (!isFieldPositionCorrect(rowIndex, columnIndex)) {
            throw InvalidParameterException()
        }

        return fields[rowIndex][columnIndex].player == null
    }

    private fun isFieldPositionCorrect(rowIndex: Int, columnIndex: Int): Boolean {
        return rowIndex in 0..lastIndex && columnIndex in 0..lastIndex
    }

    private fun isSidePosition(rowIndex: Int, columnIndex: Int): Boolean {
        return rowIndex == 0 || columnIndex == lastIndex || rowIndex == lastIndex || columnIndex == 0
    }

    private fun isColumnFull(columnIndex: Int): Boolean {
        for (rowIndex in 0..lastIndex) {
            if (isFieldFree(rowIndex, columnIndex)) {
                return false
            }
        }
        return true
    }

    private fun isRowFull(rowIndex: Int): Boolean {
        for (columnIndex in 0..lastIndex) {
            if (isFieldFree(rowIndex, columnIndex)) {
                return false
            }
        }
        return true
    }

    private fun findTopLeftDiagonalStart(rowIndex: Int, columnIndex: Int): Pair<Int, Int> {
        var checkRow = rowIndex
        var checkColumn = columnIndex

        while (checkRow != 0 && checkColumn != 0) {
            checkRow--
            checkColumn--
        }

        return Pair(checkRow, checkColumn)
    }

    private fun checkTopLeftDiagonal(rowIndex: Int, columnIndex: Int): Pair<Int, Pair<Int, Int>> {
        assert(isSidePosition(rowIndex, columnIndex))
        var checkRow = rowIndex
        var checkColumn = columnIndex
        var length = 0

        while (isFieldPositionCorrect(checkRow, checkColumn)) {
            if (isFieldFree(checkRow, checkColumn)) {
                break
            }

            length++

            if (checkRow == lastIndex || checkColumn == lastIndex) {
                return Pair(length, Pair(checkRow, checkColumn))
            }

            checkRow++
            checkColumn++
        }

        return Pair(0, Pair(0, 0))
    }

    private fun findBottomLeftDiagonalStart(rowIndex: Int, columnIndex: Int): Pair<Int, Int> {
        var checkRow = rowIndex
        var checkColumn = columnIndex

        while (checkRow != lastIndex && checkColumn != 0) {
            checkRow++
            checkColumn--
        }

        return Pair(checkRow, checkColumn)
    }

    private fun checkBottomLeftDiagonal(rowIndex: Int, columnIndex: Int): Pair<Int, Pair<Int, Int>> {
        assert(isSidePosition(rowIndex, columnIndex))
        var checkRow = rowIndex
        var checkColumn = columnIndex
        var length = 0

        while (isFieldPositionCorrect(checkRow, checkColumn)) {
            if (isFieldFree(checkRow, checkColumn)) {
                break
            }

            length++

            if (checkRow == 0 || checkColumn == lastIndex) {
                return Pair(length, Pair(checkRow, checkColumn))
            }

            checkRow--
            checkColumn++
        }

        return Pair(0, Pair(0, 0))
    }
}
