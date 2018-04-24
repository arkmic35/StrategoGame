package helper

class CyclingArrayIterator<T>(private val collection: Array<T>) {
    var currentIndex = 0
    val lastIndex = collection.size - 1

    constructor(other: CyclingArrayIterator<T>) : this(other.collection) {
        currentIndex = other.currentIndex
    }

    fun next(): T {
        if (currentIndex == lastIndex) {
            currentIndex = 0
        } else {
            currentIndex++
        }

        return collection[currentIndex]
    }
}