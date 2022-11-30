package english.master.db

import org.jetbrains.exposed.sql.ResultRow

data class UserRecord(
    val id: Long,
    val username: String,
    val firstName: String,
)

data class CardRecord(
    val id: Int? = null,
    val userId: Long,
    val word: String,
    val description: String,
    val backImg: ByteArray,
    val frontImg: ByteArray,
)

fun Cards.rowToCardRecord(row: ResultRow): CardRecord =
    CardRecord(
        id = row[id],
        userId = row[user],
        word = row[word],
        description = row[description],
        backImg = row[backImage].bytes,
        frontImg = row[frontImage].bytes,
    )

fun Users.rowToUserRecord(row: ResultRow): UserRecord =
    UserRecord(
        id = row[id],
        username = row[username],
        firstName = row[firstName]
    )
