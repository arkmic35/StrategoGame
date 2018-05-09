package game.model.player

import android.arch.lifecycle.MutableLiveData
import android.databinding.BaseObservable
import android.databinding.Bindable
import com.arkmic35.stratego.BR

abstract class Player(val playerID: Int, val playerName: String, val color: Int) : BaseObservable(), Cloneable {
    @Bindable
    var points: Int = 0

    val messageLiveData = MutableLiveData<String>()

    constructor(other: Player) : this(other.playerID, other.playerName, other.color) {
        points = other.points
    }

    @Bindable
    fun getPointsString(): String {
        return "$points pkt."
    }

    fun addPoints(pointsToAdd: Int, message: String) {
        messageLiveData.postValue(message)
        addPoints(pointsToAdd)
    }

    private fun addPoints(pointsToAdd: Int) {
        points += pointsToAdd
        notifyPropertyChanged(BR.points)
        notifyPropertyChanged(BR.pointsString)
    }

    override fun toString(): String {
        return playerName
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Player) return false

        if (playerID != other.playerID) return false
        return true
    }

    override fun hashCode(): Int {
        return playerID
    }

    public abstract override fun clone(): Player
}