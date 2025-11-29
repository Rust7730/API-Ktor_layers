package com.example.plugins

import com.example.repository.Artists
import com.example.repository.Users
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction


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