package com.example

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction

object Users : Table("users") {
    val id = uuid("id").autoGenerate()
    val username = varchar("username", 255).uniqueIndex()
    val password = varchar("password", 255)
    val role = varchar("role", 50).default("USER")

    override val primaryKey = PrimaryKey(id)
}
object Artists : Table("artists") {
    val id = uuid("id").autoGenerate()
    val name = varchar("name", 255)
    val genre = varchar("genre", 100).nullable()
    val image = text("image").nullable()

    override val primaryKey = PrimaryKey(id)
}
fun Application.configureDatabase() {
    val dbUrl = "jdbc:postgresql://localhost:5432/API-Clon"
    val dbDriver = "org.postgresql.Driver"
    val dbUser = "postgres"
    val dbPassword = "rudy1254"

    val hikariConfig = HikariConfig().apply {
        jdbcUrl = dbUrl
        driverClassName = dbDriver
        username = dbUser
        password = dbPassword
        maximumPoolSize = 10
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    }

    val dataSource = HikariDataSource(hikariConfig)
    Database.connect(dataSource)

    transaction {
        SchemaUtils.create(Users, Artists)
    }
}