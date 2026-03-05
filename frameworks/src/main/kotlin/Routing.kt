package com.example

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.http.content.* 
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq 
import org.mindrot.jbcrypt.BCrypt
import java.util.*
import com.example.models.*

fun Application.configureRouting() {
    routing {
        staticResources("/", "static")
        
        route("/tasks") {
                
            get {
                val tasks = transaction {
                    Tasks.selectAll().map {
                        Task(
                            id = it[Tasks.id],
                            title = it[Tasks.title],
                            description = it[Tasks.description],
                            done = it[Tasks.done]
                        )
                    }
                }
                call.respond(tasks)
            }

            post {
                val task = call.receive<Task>()
                val id = transaction {
                    Tasks.insert {
                        it[title] = task.title
                        it[description] = task.description
                        it[done] = task.done
                    } get Tasks.id
                }
                call.respond(HttpStatusCode.Created, mapOf("id" to id))
            }

            get("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull() 
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid ID format")
                
                val task = transaction {
                    // Новый синтаксис вместо простого .select { ... }
                    Tasks.selectAll().where { Tasks.id eq id }.map {
                        Task(
                            id = it[Tasks.id],
                            title = it[Tasks.title],
                            description = it[Tasks.description],
                            done = it[Tasks.done]
                        )
                    }.singleOrNull()
                }
                    
                if (task != null) {
                    call.respond(HttpStatusCode.OK, task)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Task not found")
                }
            }

            delete("/{id}") {
                val taskId = call.parameters["id"]?.toIntOrNull() 
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid ID format")
                
                val deletedRows = transaction {
                    // 2. Пишем Tasks.id (колонка) eq taskId (твоя переменная)
                    // Никаких "it." тут не нужно!
                    Tasks.deleteWhere { Tasks.id eq taskId }
                }
                
                if (deletedRows > 0) {
                    call.respond(HttpStatusCode.OK, mapOf("result" to "Task deleted"))
                } else {
                    call.respond(HttpStatusCode.NotFound, "Task not found")
                }
            }
        }
    }
}
