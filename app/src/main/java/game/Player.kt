package game

import android.graphics.Color

class Player(color: Color) {
    var points: Int = 0

    fun addPoints(pointsToAdd: Int) {
        points += pointsToAdd
    }
}