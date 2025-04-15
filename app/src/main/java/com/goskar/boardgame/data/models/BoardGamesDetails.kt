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

    @field:Element(name = "age", required = false)
    var age: Int? = null,

    @field:Element(name = "thumbnail", required = false)
    var thumbnail: String? = null,

    @field:Element(name = "image", required = false)
    var image: String? = null,

    @field:Element(name = "description", required = false)
    var description: String? = null,

    @field:ElementList(inline = true, required = false)
    var expansions: List<BoardGameExpansionBGG>? = null
)

@Root(name = "boardgameexpansion", strict = false)
data class BoardGameExpansionBGG(
    @field:Attribute(name = "objectid")
    var id: String = "",

    @field:Text(required = false)
    var name: String? = null
)
