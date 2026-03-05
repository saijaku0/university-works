package com.example

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*

class ApplicationTest {

    @Test
    fun testRoot() = testApplication {
        application {
            configureRouting()
        }
        val indexResponse = client.get("/")
        assertEquals(HttpStatusCode.OK, indexResponse.status)

        val tasksResponse = client.get("/tasks")
    }

}
