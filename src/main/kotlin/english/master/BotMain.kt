package english.master

import english.master.db.Cards
import english.master.db.Users
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession

fun main() {
    establishDbConnection()
    val botsApi = TelegramBotsApi(DefaultBotSession::class.java)
    botsApi.registerBot(Bot())
}

fun establishDbConnection() {
    Database.connect(
        url = "jdbc:postgresql://${System.getenv("PGHOST") ?: "localhost"}:${System.getenv("PGPORT")?: "5431"}/${System.getenv("PGDATABASE")?: "englishbot"}",
        driver = "org.postgresql.Driver",
        user = System.getenv("PGUSER")?: "postgres",
        password = System.getenv("PGPASSWORD")?: "postgres"
    )
    transaction {
        SchemaUtils.createMissingTablesAndColumns(Users, Cards)
    }
}