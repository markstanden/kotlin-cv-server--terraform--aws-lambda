package dev.markstanden.Files

fun asResource(path: String): String? =
	{}::class.java.getResource(path)?.readText()