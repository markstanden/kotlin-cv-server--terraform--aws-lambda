package dev.markstanden.datastore

import dev.markstanden.models.CV
import io.ktor.http.*

interface DataStore {
	suspend fun getCV(id: String): Pair<CV?, HttpStatusCode>

	val baseBranch: String
}