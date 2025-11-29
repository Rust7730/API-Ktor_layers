package com.example.repository

import org.jetbrains.exposed.sql.Table

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
object Albums : Table("albums") {
    val id = uuid("id").autoGenerate()
    val name = varchar("name", 255)
    val year = integer("year")
    val albumArt = text("album_art").nullable()
    val artistId = uuid("artist_id").references(Artists.id)

    override val primaryKey = PrimaryKey(id)
}