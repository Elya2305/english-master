package english.master.db.repo

import english.master.db.UserRecord
import english.master.db.Users
import english.master.db.Users.toUserRecord
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class UserRepo {
    private val table = Users

    fun insertOrUpdate(record: UserRecord) {
        transaction {
            table.select { Users.id eq record.id }
                .firstOrNull()
                ?.let { update(record) }
                ?: run { insert(record) }
        }
    }

    fun insert(record: UserRecord) {
        table.insert {
            it[id] = record.id
            it[username] = record.username
            it[firstName] = record.firstName
        }
    }

    fun update(record: UserRecord) {
        table.update({ Users.id eq record.id }) {
            it[username] = record.username
            it[firstName] = record.firstName
        }
    }

    fun findAll(): List<UserRecord> {
        return table.selectAll()
            .map { it.toUserRecord() }
    }
}