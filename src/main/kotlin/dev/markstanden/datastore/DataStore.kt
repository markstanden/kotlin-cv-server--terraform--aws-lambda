package dev.markstanden.datastore

import dev.markstanden.models.CV
import io.ktor.http.*

interface DataStore {
	suspend fun getCV(id: String): Pair<CV?, HttpStatusCode>
	suspend fun getCover(id: String): Pair<String, HttpStatusCode>

	val baseBranch: String
}