package dev.markstanden.datastore

import dev.markstanden.http.StatusCode
import dev.markstanden.models.CV

interface DataStore {
	suspend fun getCV(id: String): Pair<CV?, StatusCode>

	val baseBranch: String
}