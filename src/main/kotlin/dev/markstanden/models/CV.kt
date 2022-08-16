package dev.markstanden.models

import kotlinx.serialization.Serializable

@Serializable
data class CV(
	val user: User,
	val coverLetter: CoverLetter,
	val experienceSection: ExperienceSection,
	val sections: List<Section>,
) {
	@Serializable
	data class User(
		val name: String,
		val location: Location,
		val contact: Contact,
		val links: List<Link>,
	)

	@Serializable
	data class ExperienceSection(
		/** The title displayed at the section header
		 * of the work experience section */
		val title: String,

		/** Each of the work positions within the section,
		 * held within an immutable list */
		val items: List<Position>,
	)

	@Serializable
	data class Contact(
		val phone: String,
		val email: String,
	)

	@Serializable
	data class Link(
		val title: String,
		val url: String,
	)

	@Serializable
	class Position(
		val title: String,
		val content: List<String>,
		val dates: String,
		val business: Business,
	)

	@Serializable
	data class Section(
		val title: String,
		val items: List<Item>,
	)

	@Serializable
	data class Business(
		val title: String,
		val link: String,
		val location: Location,
		val department: String,
	)

	@Serializable
	data class Location(
		val city: String,
		val country: String,
	)

	@Serializable
	data class Item(
		val title: String,
		val content: List<String> = emptyList(),
		val dates: String = "",
		val link: String = "",
	)
}