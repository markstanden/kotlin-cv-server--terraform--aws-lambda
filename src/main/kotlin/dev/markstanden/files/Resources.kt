package dev.markstanden.files

fun asResource(path: String): String? =
	{}::class.java.getResource(path)?.readText()