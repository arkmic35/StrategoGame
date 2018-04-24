package helper

class CyclingArrayIterator<T>(private val collection: Array<T>) {
    private val collectionSize = collection.size
    private val lastIndex = collectionSize - 1
    var currentIndex = lastIndex

    constructor(other: CyclingArrayIterator<T>) : this(other.collection) {
        currentIndex = other.currentIndex
    }

    fun current(): T {
        return collection[currentIndex]
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