package de.gematik.security.plugins

import de.gematik.security.Customer
import de.gematik.security.QrCodeInvitation
import de.gematik.security.customers
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import java.text.SimpleDateFormat
import java.util.*


fun Application.configureRouting() {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    routing {
        staticResources("/static","files")
        get("/") {
            call.respond(FreeMarkerContent("showInvitation.ftl", mapOf("invitation" to QrCodeInvitation)))
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
                val birthDate = formParameters.getOrFail("birthdate")
                val email = formParameters.getOrFail("email")
                val newEntry = Customer(name, givenName, sdf.parse(birthDate), email)
                customers.add(newEntry)
                call.respondRedirect("/admin/${newEntry.id}")
            }
            get("{id}") {
                val id = call.parameters.getOrFail<Int>("id").toInt()
                call.respond(FreeMarkerContent("show.ftl", mapOf("customer" to customers.find { it.id == id })))
            }
            get("{id}/edit") {
                val id = call.parameters.getOrFail<Int>("id").toInt()
                call.respond(FreeMarkerContent("edit.ftl", mapOf("customer" to customers.find { it.id == id })))
            }
            post("{id}") {
                val id = call.parameters.getOrFail<Int>("id").toInt()
                val formParameters = call.receiveParameters()
                when (formParameters.getOrFail("_action")) {
                    "update" -> {
                        val index = customers.indexOf(customers.find { it.id == id })
                        val name = formParameters.getOrFail("name")
                        val givenName = formParameters.getOrFail("givenname")
                        val birthDate = formParameters.getOrFail("birthdate")
                        val email = formParameters.get("email")
                        customers[index].name = name
                        customers[index].givenName = givenName
                        customers[index].birthDate = sdf.parse(birthDate)
                        customers[index].email = email
                        call.respondRedirect("/admin/$id")
                    }

                    "delete" -> {
                        customers.removeIf { it.id == id }
                        call.respondRedirect("/admin")
                    }
                }
            }
        }
    }
}
