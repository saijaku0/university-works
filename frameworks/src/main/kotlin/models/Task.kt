package com.example.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

object Users : Table() {
    val id = integer("id").autoIncrement()
    val username = varchar("username", 50).uniqueIndex()
    val passwordHash = varchar("password_hash", 60)
    override val primaryKey = PrimaryKey(id)
}

object Tasks : Table() {
    val id = integer("id").autoIncrement()
    val title = varchar("title", 255)
    val description = text("description")
    val done = bool("done").default(false)
    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class Task(val id: Int? = null, val title: String, val description: String, val done: Boolean = false)

@Serializable
data class UserRegistration(val username: String, val password: String)

@Serializable
data class UserLogin(val username: String, val password: String)