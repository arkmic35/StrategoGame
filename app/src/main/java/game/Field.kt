package game

import java.util.*

class Field() : Observable() {
    constructor(other: Field) : this() {
        this.player = other.player
    }

    var player: Player? = null
        set(value) {
            field = value
            setChanged()
            notifyObservers()
        }
}
