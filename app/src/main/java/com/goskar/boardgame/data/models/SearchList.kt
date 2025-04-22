package com.goskar.boardgame.data.models

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "boardgames", strict = false)
data class SearchBGGList(
    @field:ElementList(inline = true, required = false)
    var boardGames: List<SearchBGGListElements>? = null
)

@Root(name = "boardgame", strict = false)
data class SearchBGGListElements(
    @field:Attribute(name = "objectid")
    var id: String = "",

    @field:Element(name = "name", required = false)
    var name: String? = null,

    @field:Element(name = "yearpublished", required = false)
    var yearPublished: Int? = null,
)
