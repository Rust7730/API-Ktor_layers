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
    // Eliminamos el campo 'image'
    override val primaryKey = PrimaryKey(id)
}

object Albums : Table("albums") {
    val id = uuid("id").autoGenerate()
    // Cambiamos 'name' por 'title' para coincidir con Postman
    val title = varchar("title", 255)
    // Cambiamos 'year' por 'release_year'
    val releaseYear = integer("release_year")
    val artistId = uuid("artist_id").references(Artists.id)
    // Eliminamos el campo 'albumArt'
    override val primaryKey = PrimaryKey(id)
}

object Tracks : Table("tracks") {
    val id = uuid("id").autoGenerate()
    val title = varchar("title", 255)
    val duration = integer("duration") // Duración en segundos
    val albumId = uuid("album_id").references(Albums.id)
    override val primaryKey = PrimaryKey(id)
}