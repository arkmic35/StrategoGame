package game

import java.util.*

class Field : Observable() {
    var player: Player? = null
        set(value) {
            field = value
            setChanged()
            notifyObservers()
        }
}
