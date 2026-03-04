package com.example.models

import org.jetbrains.exposed.sql.Table

object Tasks : Table() {
    val id = integer("id").autoIncrement()
    val title = varchar("title", 255)
    val description = text("description")
    val done = bool("done").default(false)

    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class Task(
    val id: Int? = null,
    val title: String,
    val description: String,
    val done: Boolean = false
)