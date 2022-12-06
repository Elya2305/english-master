package english.master

import english.master.db.Cards
import english.master.db.Users
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession
import java.lang.System.getenv

fun main() {
    establishDbConnection()
    val botsApi = TelegramBotsApi(DefaultBotSession::class.java)
    botsApi.registerBot(Bot())
}

fun establishDbConnection() {
    Database.connect(
        url = "jdbc:postgresql://${getenv("PROD_DB_HOST")}:${getenv("PROD_DB_PORT")}/${getenv("PROD_DB_NAME")}",
        driver = "org.postgresql.Driver",
        user = "postgres",
        password = "postgres"
    )
    transaction {
        SchemaUtils.createMissingTablesAndColumns(Users, Cards)
    }
}