package english.master.db.repo

import english.master.db.CardRecord
import english.master.db.Cards
import english.master.db.Cards.toCardRecord
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.time.LocalDateTime

class CardRepo {
    private val table = Cards

    fun insert(record: CardRecord): CardRecord {
        return transaction {
            table.insert {
                it[user] = record.userId
                it[description] = record.description
                it[word] = record.word
                it[backImage] = ExposedBlob(record.backImg)
                it[frontImage] = ExposedBlob(record.frontImg)
                it[createdAt] = LocalDateTime.now()
            }.resultedValues!!.first().toCardRecord()
        }
    }

    fun update(record: CardRecord) {
        transaction {
            table.update({ table.id eq record.id!! }) {
                it[user] = record.userId
                it[description] = record.description
                it[word] = record.word
                it[backImage] = ExposedBlob(record.backImg)
                it[frontImage] = ExposedBlob(record.frontImg)
            }
        }
    }

    fun delete(id: Int) {
        transaction {
            table.deleteWhere { table.id eq id }
        }
    }

    fun count(userId: Long): Long {
        return transaction {
            return@transaction table.select { table.user eq userId }.count()
        }
    }

    fun findRandomNByUser(userId: Long, number: Int): List<CardRecord> {
        return transaction {
            val ids = table.select { table.user eq userId }.map { it[Cards.id] }.shuffled().take(number)
            return@transaction table.select { table.id inList ids }
                .map { it.toCardRecord() }
        }
    }

    fun allByCreatedAt(userId: Long): List<CardRecord> {
        return transaction {
            table.select { table.user eq userId }.orderBy(table.createdAt).map { it.toCardRecord() }
        }
    }

    fun findIndexedByCreatedAt(index: Long, userId: Long): CardRecord {
        return transaction {
            table.select { table.user eq userId }.orderBy(table.createdAt)
                .limit(1, offset = index - 1)
                .map { it.toCardRecord() }.first()
        }
    }
}
