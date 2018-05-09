package game.model.player

class HumanPlayer(playerID: Int, playerName: String, color: Int) : Player(playerID, playerName, color) {
    constructor(other: HumanPlayer) : this(other.playerID, other.playerName, other.color) {
        points = other.points
    }

    override fun clone() = HumanPlayer(this)
}