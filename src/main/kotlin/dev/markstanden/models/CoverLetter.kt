package dev.markstanden.models

import kotlinx.serialization.Serializable

@Serializable
data class CoverLetter(val greeting: String, val text: List<String>, val signOff: String)