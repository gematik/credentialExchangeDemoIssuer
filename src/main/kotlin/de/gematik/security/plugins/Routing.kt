package de.gematik.security.plugins

import de.gematik.security.*
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import kotlinx.coroutines.processNextEventInCurrentThread


fun Application.configureRouting() {

    routing {
        staticResources("/static", "files")
        get("/") {
            call.respondRedirect("admin")
        }
        route("admin") {
            get {
                call.respond(FreeMarkerContent("index.ftl", mapOf("customers" to customers)))
            }
            get("new") {
                call.respond(FreeMarkerContent("new.ftl", model = null))
            }
            post {
                val formParameters = call.receiveParameters()
                val name = formParameters.getOrFail("name")
                val givenName = formParameters.getOrFail("givenname")
                val gender = formParameters.getOrFail("gender")
                val birthDate = formParameters.getOrFail("birthdate")
                val email = formParameters.getOrFail("email")
                val newEntry = Customer(
                    name,
                    givenName,
                    birthDate.toDate(),
                    if (gender.isBlank()) Gender.Uni else Gender.valueOf(gender),
                    email
                )
                customers.add(newEntry)
                call.respondRedirect("/admin/${newEntry.id}")
            }
            get("{id}") {
                val id = call.parameters.getOrFail<Int>("id").toInt()
                call.respond(FreeMarkerContent("show.ftl", mapOf("customer" to customers.find { it.id == id })))
            }
            get("{id}/invitation") {
                val id = call.parameters.getOrFail<String>("id")
                call.respond(FreeMarkerContent("showInvitation.ftl", mapOf("invitation" to object {
                    private val customer =
                        customers.find { it.vaccinations.firstOrNull() { it.invitation.id == id } != null }
                    private val vaccination = customer?.vaccinations?.firstOrNull { it.invitation.id == id }
                    val givenName = customer?.givenName
                    val name = customer?.name
                    val dateOfVaccination = vaccination?.dateOfVaccination
                    val url = vaccination?.invitation?.url
                    val qrCode = vaccination?.invitation?.qrCode
                }
                )))
            }
            get("{id}/edit") {
                val id = call.parameters.getOrFail<Int>("id").toInt()
                call.respond(FreeMarkerContent("edit.ftl", mapOf("customer" to customers.find { it.id == id })))
            }
            post("{id}/edit") {
                val id = call.parameters.getOrFail<Int>("id").toInt()
                val formParameters = call.receiveParameters()
                when (formParameters.getOrFail("_action")) {
                    "update" -> {
                        val index = customers.indexOf(customers.find { it.id == id })
                        val name = formParameters.getOrFail("name")
                        val givenName = formParameters.getOrFail("givenname")
                        val gender = formParameters.getOrFail("gender")
                        val birthDate = formParameters.getOrFail("birthdate")
                        val email = formParameters.get("email")
                        customers[index].name = name
                        customers[index].givenName = givenName
                        customers[index].birthDate = birthDate.toDate()
                        customers[index].gender = if (gender.isBlank()) Gender.Uni else Gender.valueOf(gender)
                        customers[index].email = email
                        call.respondRedirect("/admin/$id")
                    }

                    "delete" -> {
                        customers.removeIf { it.id == id }
                        call.respondRedirect("/admin")
                    }
                }
            }
            get("{id}/addVaccination") {
                val id = call.parameters.getOrFail<Int>("id").toInt()
                call.respond(FreeMarkerContent("addVaccination.ftl", mapOf("data" to object {
                    val customer = customers.find { it.id == id }
                    val vaccines = AuthorizedVaccine.values().toList()
                })))
            }
            post("{id}/addVaccination") {
                val id = call.parameters.getOrFail<Int>("id").toInt()
                val formParameters = call.receiveParameters()
                val index = customers.indexOf(customers.find { it.id == id })
                val vaccination = Vaccination(
                    dateOfVaccination = formParameters.getOrFail("dateOfVaccination").toDate(),
                    atcCode = formParameters.getOrFail("atcCode"),
                    vaccine = AuthorizedVaccine.valueOf(formParameters.getOrFail("vaccine")),
                    batchNumber = formParameters.getOrFail("batchNumber"),
                    order = formParameters.get("order")!!.toInt()
                )
                customers[index].vaccinations.add(vaccination)
                call.respondRedirect("/admin/$id")
            }
        }
    }
}
