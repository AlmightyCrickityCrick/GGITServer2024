package com.example.model

import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction




@Serializable
data class Feedback(var id: Int?= null, val name: String, val rating: Int, val experience: String)

@Serializable
data class FeedbackList(val feedbackList: List<Feedback>)

@Serializable
data class Program(var id: Int?= null, val name: String, val surname: String,
                   val email: String, val message: String)

@Serializable
data class ProgramList(val programList: List<Program>)




@Serializable
data class Pet(var id: Int?=null, val type: Int, val name: String, val surname: String, val image_link: String, val age: Int, val vaccine: Boolean, val bio: String)


@Serializable
data class PetList(val pets: List<Pet>)

@Serializable
data class Adoption(var id: Int?=null, val name: String, val surname: String, val phone: String, val house_type: String, val pets: String, val cost_agreement: Boolean, val free_hours: String)

@Serializable
data class AdoptionList(val adoptions: List<Adoption>)

class PetService(private val database: Database) {
    object Pets : Table() {
        val id = integer("id").autoIncrement()
        val type = integer("type")
        val name = varchar("name", length = 50)
        val surname = varchar("surname", 50)
        val image_link = varchar("image_link", 1000)
        val age = integer("age")
        val vaccine = bool("vaccine")
        val bio = varchar("bio", 1000)


        override val primaryKey = PrimaryKey(id)
    }

    object Adoptions: Table(){
        val id = integer("id").autoIncrement()
        val name = varchar("name", length = 50)
        val surname = varchar("surname", 50)
        val phone = varchar("phone", length = 50)
        val house_type = varchar("house_type", 50)
        val pets = varchar("pets", length = 50)
        val free_hours = varchar("free_hours", 50)
        val cost_agreement = bool("cost_agreement")

        override val primaryKey = PrimaryKey(id)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Pets)
            SchemaUtils.create(Adoptions)
            SchemaUtils.create(Feedbacks)
            SchemaUtils.create(Programs)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    suspend fun createPet(pet: Pet): Int = dbQuery {
        Pets.insert {
            it[name] = pet.name
            it[age] = pet.age
            it[type] = pet.type
            it[surname] = pet.surname
            it[image_link] = pet.image_link
            it[vaccine] = pet.vaccine
            it[bio] = pet.bio
        }[Pets.id]
    }

    suspend fun readPet(id: Int): Pet? {
        return dbQuery {
            Pets.select { Pets.id eq id }
                .map { Pet(it[Pets.id], it[Pets.type], it[Pets.name], it[Pets.surname], it[Pets.image_link], it[Pets.age], it[Pets.vaccine], it[Pets.bio]) }
                .singleOrNull()
        }
    }

    suspend fun deletePet(id:Int) {
        return dbQuery {
            Pets.deleteWhere{ Pets.id.eq(id)}
        }
    }

    suspend fun readAllPets(): List<Pet> {
        return dbQuery {
            Pets.selectAll().map { Pet(it[Pets.id], it[Pets.type], it[Pets.name], it[Pets.surname], it[Pets.image_link], it[Pets.age], it[Pets.vaccine], it[Pets.bio])}
        }
    }

    suspend fun createAdoption(ad: Adoption): Int = dbQuery {
        Adoptions.insert {
            it[name] = ad.name
            it[surname] = ad.surname
            it[phone] = ad.phone
            it[house_type] = ad.house_type
            it[pets] = ad.pets
            it[free_hours] = ad.free_hours
            it[cost_agreement] = ad.cost_agreement

        }[Adoptions.id]
    }

    suspend fun getAllAdoptions() : List<Adoption>{
        return dbQuery {
            Adoptions.selectAll().map {
                Adoption(it[Adoptions.id], it[Adoptions.name],
                    it[Adoptions.surname], it[Adoptions.phone], it[Adoptions.house_type],
                    it[Adoptions.pets], it[Adoptions.cost_agreement], it[Adoptions.free_hours])
            }
        }
    }


    object Feedbacks : Table() {
        val id = integer("id").autoIncrement()
        val name = varchar("name", length = 50)
        val rating = integer("rating")
        val experience = varchar("experience", 200)

        override val primaryKey = PrimaryKey(id)
    }

    object Programs : Table() {
        val id = integer("id").autoIncrement()
        val name = varchar("name", length = 50)
        val surname = varchar("surname", length = 50)
        val email = varchar("email", 50)
        val message = varchar("message", length = 50)

        override val primaryKey = PrimaryKey(id)
    }


    suspend fun createFeedback(user: Feedback): Int = dbQuery {
        Feedbacks.insert {
            it[name] = user.name
            it[rating] = user.rating
            it[experience] = user.experience
        }[Feedbacks.id]
    }

    suspend fun readFeedback(): List<Feedback> {
        return dbQuery {
            Feedbacks.selectAll()
                .map { Feedback(it[Feedbacks.id], it[Feedbacks.name], it[Feedbacks.rating], it[Feedbacks.experience]) }
        }
    }

    suspend fun createProgram(user: Program): Int = dbQuery {
        Programs.insert {
            it[name] = user.name
            it[surname] = user.surname
            it[email] = user.email
            it[message] = user.message
        }[Programs.id]
    }

    suspend fun readProgram(): List<Program> {
        return dbQuery {
            Programs.selectAll()
                .map { Program(it[Programs.id], it[Programs.name], it[Programs.surname], it[Programs.email], it[Programs.message]) }
        }
    }



}
