package com.example.plugins

import com.example.model.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*

val database = Database.connect(
    url = "jdbc:h2:file:./data/pawsmatch2db;DB_CLOSE_ON_EXIT=FALSE",
    user = "root",
    driver = "org.h2.Driver",
    password = ""
)

val userService = PetService(database)

fun Application.configureDatabases() {



    routing {
        // Create user
        post("/pet") {
            val pet = call.receive<Pet>()
            val id = userService.createPet(pet)
            call.respond(HttpStatusCode.Created, id)
        }

        get("/pet"){
        val pets = userService.readAllPets()
            call.respond(Json.encodeToJsonElement(PetList.serializer(), PetList(pets)))
        }

        // Read user
        get("/pet/{id}") {
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            val pet = userService.readPet(id)
            if (pet != null) {
                call.respond(HttpStatusCode.OK, pet)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        delete("/pet/{id}"){
            val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
            val pet = userService.deletePet(id)
            call.respond(HttpStatusCode.OK, pet)
        }

        post("/pet/{id}/adopt"){
            val ad = call.receive<Adoption>()
            val id = userService.createAdoption(ad)
            call.respond(HttpStatusCode.Created, id)
        }

       get("/pet/adopt"){
            val adoption = userService.getAllAdoptions()
           call.respond(Json.encodeToJsonElement(AdoptionList.serializer(), AdoptionList(adoption)))
       }
    }
}
