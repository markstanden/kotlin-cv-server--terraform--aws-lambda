package dev.markstanden.models

import kotlinx.serialization.Serializable

@Serializable
data class ErrorMessage(val error: String)