package english.master.util

class ExtendedListIterator<T>(private val items: List<T>) : ListIterator<T> {
    private var index: Int = 0
    private var current: T = items[index]

    override fun hasNext() = index < items.size

    override fun hasPrevious() = index > 0

    override fun next(): T {
        if (!hasNext()) throw NoSuchElementException()
        return items[index++]
    }

    override fun nextIndex() = index

    override fun previous(): T {
        if (!hasPrevious()) throw NoSuchElementException()
        return items[--index]
    }

    override fun previousIndex() = index - 1

    fun nextSilent(): T {
        if (!hasNext()) throw NoSuchElementException()
        return items[index + 1]
    }

    fun previousSilent(): T {
        if (!hasPrevious()) throw NoSuchElementException()
        return items[index - 1]
    }

    fun items() = items
}