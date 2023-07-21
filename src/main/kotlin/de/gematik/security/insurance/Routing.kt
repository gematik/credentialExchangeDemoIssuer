package de.gematik.security.insurance

import de.gematik.security.credentialExchangeLib.credentialSubjects.Gender
import de.gematik.security.insurance.Insurant
import de.gematik.security.insurance.insurants
import de.gematik.security.medicaloffice.AuthorizedVaccine
import de.gematik.security.medicaloffice.Patient
import de.gematik.security.medicaloffice.Vaccination
import de.gematik.security.medicaloffice.patients
import de.gematik.security.qrCode
import de.gematik.security.toDate
import de.gematik.security.url
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*


fun Application.configureRouting() {

    routing {
        staticResources("/static", "files")
        get("/") {
            call.respondRedirect("/static")
        }
        route("insurance") {
            get {
                call.respond(FreeMarkerContent("index_insurance.ftl", mapOf("customers" to insurants)))
            }
            get("new") {
                call.respond(FreeMarkerContent("new_insurant.ftl", model = null))
            }
            post {
                val formParameters = call.receiveParameters()
                val name = formParameters.getOrFail("name")
                val givenName = formParameters.getOrFail("givenname")
                val gender = formParameters.getOrFail("gender")
                val birthDate = formParameters.getOrFail("birthdate")
                val email = formParameters.getOrFail("email")
                val newEntry = Insurant(
                    name,
                    givenName,
                    birthDate.toDate(),
                    if (gender.isBlank()) Gender.Undefined else Gender.valueOf(gender),
                    email
                )
                insurants.add(newEntry)
                call.respondRedirect("/insurance/${newEntry.id}")
            }
            get("{id}") {
                val id = call.parameters.getOrFail<Int>("id").toInt()
                call.respond(
                    FreeMarkerContent(
                        "show_insurant.ftl",
                        mapOf("customer" to insurants.find { it.id == id })
                    )
                )
            }
            get("{id}/invitation") {
                val invationId = call.parameters.getOrFail<String>("id")
                insurants.find { it.insurance?.invitation?.id == invationId }?.let { customer ->
                    call.respond(FreeMarkerContent("showInvitationInsurance.ftl", mapOf("invitation" to object {
                        val givenName = customer.givenName
                        val name = customer.name
                        val insurantId = customer.insurance?.insurantId
                        val url = customer.insurance!!.invitation.url
                        val qrCode = customer.insurance!!.invitation.qrCode
                    })))
                }
            }
            get("{id}/edit") {
                val id = call.parameters.getOrFail<Int>("id").toInt()
                call.respond(
                    FreeMarkerContent(
                        "edit_insurant.ftl",
                        mapOf("customer" to insurants.find { it.id == id })
                    )
                )
            }
            post("{id}/edit") {
                val id = call.parameters.getOrFail<Int>("id").toInt()
                val formParameters = call.receiveParameters()
                when (formParameters.getOrFail("_action")) {
                    "update" -> {
                        val index = insurants.indexOf(insurants.find { it.id == id })
                        val name = formParameters.getOrFail("name")
                        val givenName = formParameters.getOrFail("givenname")
                        val gender = formParameters.getOrFail("gender")
                        val birthDate = formParameters.getOrFail("birthdate")
                        val email = formParameters.get("email")
                        insurants[index].name = name
                        insurants[index].givenName = givenName
                        insurants[index].birthDate = birthDate.toDate()
                        insurants[index].gender = if (gender.isBlank()) Gender.Undefined else Gender.valueOf(gender)
                        insurants[index].email = email
                        call.respondRedirect("/insurance/$id")
                    }

                    "delete" -> {
                        insurants.removeIf { it.id == id }
                        call.respondRedirect("/insurance")
                    }
                }
            }
        }
    }
}
