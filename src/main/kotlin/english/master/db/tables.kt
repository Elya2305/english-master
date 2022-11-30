package english.master.db

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object Users : Table() {
    val id = long("id")
    val username = varchar("name", 50)
    val firstName = varchar("first_name", 50)
    val status = varchar("status", 20).default(Status.ACTIVE.name)

    override val primaryKey = PrimaryKey(id, name = "PK_User_Id")

    fun ResultRow.toUserRecord() = Users.rowToUserRecord(this)
}

object Cards : Table() {
    val id = integer("id").autoIncrement()
    val word = varchar("word", 50)
    val backImage = blob("back_img")
    val frontImage = blob("front_img")
    val description = text("description")
    val user = long("user_id").references(Users.id)
    val createdAt = datetime("created_at")

    override val primaryKey = PrimaryKey(id, name = "PK_Cards_Id")

    fun ResultRow.toCardRecord() = Cards.rowToCardRecord(this)
}

enum class Status {
    ACTIVE, DELETED
}