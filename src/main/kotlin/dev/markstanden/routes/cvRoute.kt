//package dev.markstanden.routes
//
//import dev.markstanden.datastore.DataStore
//import io.ktor.http.*
//import io.ktor.server.application.*
//import io.ktor.server.freemarker.*
//import io.ktor.server.response.*
//import io.ktor.server.routing.*
//
//// TODO: 02/07/2022 refactor this route with github specific code extracted.
//fun Route.cvRoute(baseRoute: String, store: DataStore) {
//	route("/$baseRoute/{folder}") {
//		get {
//			val folder = call.parameters["folder"]!!
//			// TODO: 06/07/2022 clean/validate user input
//
//			val (cv, status) = store.getCV(id = folder)
//
//			if (status != HttpStatusCode.OK || cv == null) {
//				call.response.status(status)
//				call.respond("Something went wrong")
//				return@get
//			}
//
//			// TODO: 02/07/2022 refactor with CV as a single variable
//			call.respond(
//				FreeMarkerContent(
//					template = "cvTemplate.ftl", model = mapOf(
//					"coverLetter" to cv.coverLetter,
//					"user" to cv.user,
//					"experience" to cv.experienceSection,
//					"sections" to cv.sections
//				)
//				)
//			)
//		}
//
//	}
//}