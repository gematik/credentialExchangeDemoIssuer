package de.gematik.security.medicaloffice

import freemarker.cache.ClassTemplateLoader
import freemarker.core.HTMLOutputFormat
import io.ktor.server.application.*
import io.ktor.server.freemarker.*

fun Application.configureTemplating() {
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templatesmedicaloffice")
        outputFormat = HTMLOutputFormat.INSTANCE
    }
}
