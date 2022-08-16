//package dev.markstanden.routes
//
//import dev.markstanden.datastore.DataStore
//import io.ktor.server.application.*
//import io.ktor.server.response.*
//import io.ktor.server.routing.*
//
//fun Route.root(baseRoute: String, store: DataStore) {
//	route("/") {
//		get {
//			call.respondRedirect("/$baseRoute/${store.baseBranch}")
//		}
//	}
//}