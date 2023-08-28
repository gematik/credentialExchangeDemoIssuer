package de.gematik.security.insurance

import de.gematik.security.credentialExchangeLib.credentialSubjects.Gender
import de.gematik.security.credentialExchangeLib.extensions.toIsoInstantString
import de.gematik.security.hostName
import de.gematik.security.qrCode
import de.gematik.security.url
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter


fun Application.configureRouting() {

    routing {
        staticResources("/static", "files")
        get("/") {
            call.respond(FreeMarkerContent("index.ftl", mapOf("url" to object {
                val address = hostName
                val lastCallingRemoteAddress = (Controller.lastCallingRemoteAddress ?: de.gematik.security.medicaloffice.Controller.lastCallingRemoteAddress)?.let{
                    if(it.contains(":")) "[$it]" else it
                }
            })))
        }
        route("insurance") {
            get {
                call.respond(FreeMarkerContent("index_insurance.ftl", mapOf("customers" to customers)))
            }
            get("new") {
                call.respond(FreeMarkerContent("new_customer.ftl", model = null))
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
                    LocalDate.parse(birthDate, DateTimeFormatter.ISO_DATE)
                        .atTime(12,0)
                        .atZone(ZoneId.of("UTC"))
                        .toIsoInstantString(),
                    if (gender.isBlank()) Gender.Undefined else Gender.valueOf(gender),
                    email
                )
                customers.add(newEntry)
                call.respondRedirect("/insurance/${newEntry.id}")
            }
            get("{id}") {
                val id = call.parameters.getOrFail<Int>("id").toInt()
                call.respond(
                    FreeMarkerContent(
                        "show_customer.ftl",
                        mapOf("customer" to customers.find { it.id == id })
                    )
                )
            }
            get("{id}/invitation") {
                val invationId = call.parameters.getOrFail<String>("id")
                customers.find { it.invitation.id == invationId }?.let { customer ->
                    call.respond(FreeMarkerContent("showInvitationInsurance.ftl", mapOf("invitation" to object {
                        val givenName = customer.givenName
                        val name = customer.name
                        val insurantId = customer.insurance?.insurant?.insurantId
                        val url = customer.invitation.url
                        val qrCode = customer.invitation.qrCode
                    })))
                }
            }
            get("{id}/edit") {
                val id = call.parameters.getOrFail<Int>("id").toInt()
                call.respond(
                    FreeMarkerContent(
                        "edit_customer.ftl",
                        mapOf("customer" to customers.find { it.id == id })
                    )
                )
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
                        customers[index].birthDate = LocalDate.parse(birthDate, DateTimeFormatter.ISO_DATE)
                            .atTime(12,0)
                            .atZone(ZoneId.of("UTC"))
                            .toIsoInstantString()
                        customers[index].gender = if (gender.isBlank()) Gender.Undefined else Gender.valueOf(gender)
                        customers[index].email = email
                        call.respondRedirect("/insurance/$id")
                    }

                    "delete" -> {
                        customers.removeIf { it.id == id }
                        call.respondRedirect("/insurance")
                    }
                }
            }
        }
    }
}
