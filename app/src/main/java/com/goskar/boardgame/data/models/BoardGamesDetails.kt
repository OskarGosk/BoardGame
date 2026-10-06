package com.goskar.boardgame.data.models

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root
import org.simpleframework.xml.Text

@Root(name = "boardgames", strict = false)
data class BoardGamesDetails(
    @field:ElementList(inline = true, required = false)
    var boardGamesBGG: List<BoardGameBGG>? = null
)

@Root(name = "boardgame", strict = false)
data class BoardGameBGG(
    @field:Attribute(name = "objectid")
    var id: String = "",

    @field:Element(name = "yearpublished", required = false)
    var yearPublished: Int? = null,

    @field:Element(name = "minplayers", required = false)
    var minPlayers: Int? = null,

    @field:Element(name = "maxplayers", required = false)
    var maxPlayers: Int? = null,

    @field:Element(name = "playingtime", required = false)
    var playingTime: Int? = null,

    @field:Element(name = "minplaytime", required = false)
    var minPlayTime: Int? = null,

    @field:Element(name = "maxplaytime", required = false)
    var maxPlayTime: Int? = null,

    @field:Element(name = "age", required = false)
    var age: Int? = null,

    @field:Element(name = "thumbnail", required = false)
    var thumbnail: String? = null,

    @field:Element(name = "image", required = false)
    var image: String? = null,

    @field:Element(name = "description", required = false)
    var description: String? = null,

    @field:ElementList(inline = true, required = false)
    var categories: List<BggCategory>? = null,

    @field:ElementList(inline = true, required = false)
    var mechanics: List<BggMechanic>? = null,

    @field:ElementList(inline = true, required = false)
    var expansions: List<BggExpansion>? = null,

    @field:ElementList(inline = true, required = false)
    var designers: List<BggDesigner>? = null,

    @field:ElementList(inline = true, required = false)
    var publishers: List<BggPublisher>? = null,

    @field:ElementList(inline = true, required = false)
    var artists: List<BggArtist>? = null,

    @field:ElementList(inline = true, required = false)
    var pollSummaries: List<BggPollSummary>? = null,

    @field:Element(name = "statistics", required = false)
    var statistics: BggStatistics? = null,
) {
    /** True when BGG lists the "Cooperative Game" mechanic. */
    fun isCooperative(): Boolean =
        mechanics?.any { it.value?.trim().equals("Cooperative Game", ignoreCase = true) } == true

    /** Average rating (0–10) when the detail was fetched with stats=1, else null. */
    fun averageRating(): Double? = statistics?.ratings?.average

    /** e.g. "Best with 3 players" from the suggested-player-count poll, or null. */
    fun bestPlayers(): String? =
        pollSummaries?.firstOrNull { it.name == "suggested_numplayers" }
            ?.results?.firstOrNull { it.name == "bestwith" }?.value
}

@Root(name = "boardgamecategory", strict = false)
data class BggCategory(
    @field:Attribute(name = "objectid", required = false)
    var id: String = "",

    @field:Text(required = false)
    var value: String? = null,
)

@Root(name = "boardgamemechanic", strict = false)
data class BggMechanic(
    @field:Attribute(name = "objectid", required = false)
    var id: String = "",

    @field:Text(required = false)
    var value: String? = null,
)

@Root(name = "boardgameexpansion", strict = false)
data class BggExpansion(
    @field:Attribute(name = "objectid", required = false)
    var id: String = "",

    @field:Text(required = false)
    var name: String? = null,
)

@Root(name = "boardgamedesigner", strict = false)
data class BggDesigner(
    @field:Attribute(name = "objectid", required = false)
    var id: String = "",

    @field:Text(required = false)
    var value: String? = null,
)

@Root(name = "boardgamepublisher", strict = false)
data class BggPublisher(
    @field:Attribute(name = "objectid", required = false)
    var id: String = "",

    @field:Text(required = false)
    var value: String? = null,
)

@Root(name = "boardgameartist", strict = false)
data class BggArtist(
    @field:Attribute(name = "objectid", required = false)
    var id: String = "",

    @field:Text(required = false)
    var value: String? = null,
)

@Root(name = "poll-summary", strict = false)
data class BggPollSummary(
    @field:Attribute(name = "name", required = false)
    var name: String = "",

    @field:ElementList(inline = true, required = false)
    var results: List<BggPollResult>? = null,
)

@Root(name = "result", strict = false)
data class BggPollResult(
    @field:Attribute(name = "name", required = false)
    var name: String = "",

    @field:Attribute(name = "value", required = false)
    var value: String = "",
)

@Root(name = "statistics", strict = false)
data class BggStatistics(
    @field:Element(name = "ratings", required = false)
    var ratings: BggRatings? = null,
)

@Root(name = "ratings", strict = false)
data class BggRatings(
    @field:Element(name = "average", required = false)
    var average: Double? = null,

    @field:Element(name = "bayesaverage", required = false)
    var bayesAverage: Double? = null,

    @field:Element(name = "averageweight", required = false)
    var averageWeight: Double? = null,
)
