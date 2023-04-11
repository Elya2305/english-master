package english.master

import english.master.db.Cards
import english.master.db.Users
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession

@SpringBootApplication
open class BotMain

fun main(args: Array<String>) {
//    establishDbConnection()
    val botsApi = TelegramBotsApi(DefaultBotSession::class.java)
    botsApi.registerBot(Bot())
    runApplication<BotMain>(*args)
}

fun establishDbConnection() {
    Database.connect(
        url = "jdbc:postgresql://localhost:5431/englishbot",
        driver = "org.postgresql.Driver",
        user = "postgres",
        password = "postgres"
    )
    transaction {
        SchemaUtils.createMissingTablesAndColumns(Users, Cards)
    }
}