package com.example.plugins

import com.example.model.Feedback
import com.example.model.FeedbackList
import com.example.model.Program
import com.example.model.ProgramList
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.Database


fun Application.configureRouting() {
    routing {
        // Create user
        post("/feedback") {
            val user = call.receive<Feedback>()
            val id = userService.createFeedback(user)
            val feedbacks = userService.readFeedback()
            call.respond(Json.encodeToJsonElement(FeedbackList.serializer(), FeedbackList(feedbacks)))
        }

        get("/feedback"){
            val feedbacks = userService.readFeedback()
            call.respond(Json.encodeToJsonElement(FeedbackList.serializer(), FeedbackList(feedbacks)))
        }

        post("/program") {
            try{
                val user = call.receive<Program>()
                val id = userService.createProgram(user)
                call.respond(HttpStatusCode.Created, "success")
            } catch(error: Error){
                call.respond(HttpStatusCode.BadRequest, "Object defined incorrectly")
            }

        }


        get("/program"){
            val feedbacks = userService.readProgram()
            call.respond(Json.encodeToJsonElement(ProgramList.serializer(), ProgramList(feedbacks)))

        }


    }

    }
