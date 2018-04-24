package game

import java.security.InvalidAlgorithmParameterException

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
        if (!isFieldFree(rowIndex, columnIndex)) {
            throw InvalidAlgorithmParameterException()
        }

        fields[rowIndex][columnIndex].player = Player(999, "Simulator", Player.PlayerType.SIMULATOR, 0)
        val (result, _) = calculatePointsForField(rowIndex, columnIndex, false)
        unmarkField(rowIndex, columnIndex)
        return result
    }

    fun markField(player: Player, rowIndex: Int, columnIndex: Int) {
        if (!isFieldFree(rowIndex, columnIndex)) {
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
        if (isFieldFree(rowIndex, columnIndex)) {
            throw InvalidAlgorithmParameterException()
        }

        fields[rowIndex][columnIndex].player = null

        if (!freeFields.contains(Pair(rowIndex, columnIndex))) {
            freeFields.add(Pair(rowIndex, columnIndex))
        }
    }

    private fun calculatePointsForField(rowIndex: Int, columnIndex: Int, composeMessage: Boolean): Pair<Int, StringBuilder> {
        var pointsToAdd = 0
        val message = StringBuilder()

        if (isSidePosition(rowIndex, columnIndex)) {
            if (isColumnFull(columnIndex)) {
                pointsToAdd += size

                if (composeMessage) {
                    message.append(String.format("\n+%d pkt za wypełnienie kolumny #%d", size, columnIndex + 1))
                }
            }

            if (isRowFull(rowIndex)) {
                pointsToAdd += size

                if (composeMessage) {
                    message.append("\n+$size pkt za wypełnienie wiersza #" + (rowIndex + 1))
                }
            }

            val farLeftDiagonal = findFarLeftDiagonal(rowIndex, columnIndex)
            val leftDiagonalPoints = checkFarLeftDiagonal(farLeftDiagonal[0], farLeftDiagonal[1])

            if (leftDiagonalPoints > 1) {
                pointsToAdd += leftDiagonalPoints

                if (composeMessage) {
                    message.append(String.format("\n+%d pkt za wypełnienie linii ukośnej od (%d, %d)", leftDiagonalPoints, farLeftDiagonal[0] + 1, farLeftDiagonal[1] + 1))
                }
            }

            val farRightDiagonal = findFarRightDiagonal(rowIndex, columnIndex)
            val rightDiagonalPoints = checkFarRightDiagonal(farRightDiagonal[0], farRightDiagonal[1])

            if (rightDiagonalPoints > 1) {
                pointsToAdd += rightDiagonalPoints

                if (composeMessage) {
                    message.append(String.format("\n+%d pkt za wypełnienie linii ukośnej od (%d, %d)", rightDiagonalPoints, farRightDiagonal[0] + 1, farRightDiagonal[1] + 1))
                }
            }
        }

        return Pair(pointsToAdd, message)
    }

    fun isFieldFree(rowIndex: Int, columnIndex: Int): Boolean {
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

    private fun findFarLeftDiagonal(rowIndex: Int, columnIndex: Int): IntArray {
        var checkRow = rowIndex
        var checkColumn = columnIndex

        while (checkRow != 0 && checkColumn != 0) {
            checkRow--
            checkColumn--
        }

        return intArrayOf(checkRow, checkColumn)
    }

    private fun checkFarLeftDiagonal(rowIndex: Int, columnIndex: Int): Int {
        assert(isSidePosition(rowIndex, columnIndex))
        var checkRow = rowIndex
        var checkColumn = columnIndex
        var length = 0

        while (isFieldPositionCorrect(checkRow, checkColumn)) {
            if (isFieldFree(checkRow, checkColumn)) {
                return 0
            }

            length++

            if (checkRow == lastIndex || checkColumn == lastIndex) {
                break
            }

            checkRow++
            checkColumn++
        }

        return length
    }

    private fun checkFarRightDiagonal(rowIndex: Int, columnIndex: Int): Int {
        assert(isSidePosition(rowIndex, columnIndex))
        var checkRow = rowIndex
        var checkColumn = columnIndex
        var length = 0

        while (isFieldPositionCorrect(checkRow, checkColumn)) {
            if (isFieldFree(checkRow, checkColumn)) {
                return 0
            }

            length++

            if (checkRow == lastIndex || checkColumn == 0) {
                break
            }

            checkRow++
            checkColumn--
        }

        return length
    }

    private fun findFarRightDiagonal(rowIndex: Int, columnIndex: Int): IntArray {
        var checkRow = rowIndex
        var checkColumn = columnIndex

        while (checkRow != 0 && checkColumn != lastIndex) {
            checkRow--
            checkColumn++
        }

        return intArrayOf(checkRow, checkColumn)
    }
}
