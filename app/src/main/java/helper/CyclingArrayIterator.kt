package helper

class CyclingArrayIterator<T>(private val collection: Array<T>) {
    private val collectionSize = collection.size
    private val lastIndex = collectionSize - 1
    private var currentIndex = lastIndex

    fun next(): T {
        if (currentIndex == lastIndex) {
            currentIndex = 0
        } else {
            currentIndex++
        }

        return collection[currentIndex]
    }
}