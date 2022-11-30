package english.master.util

fun <T> List<T>.takeMax(num: Int): List<T> {
    if (num > this.size) {
        return this.take(this.size)
    }
    return this.take(num)
}

fun String.equalsIgnoreCase(str: String?): Boolean {
    return this.equals(str, ignoreCase = true)
}