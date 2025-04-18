package com.goskar.boardgame.data.models

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "boardgames", strict = false)
data class SearchList(
    @field:ElementList(inline = true, required = false)
    var boardGames: List<searchListElements>? = null
)

@Root(name = "boardgame", strict = false)
data class searchListElements(
    @field:Attribute(name = "objectid")
    var id: String = "",

    @field:Element(name = "name", required = false)
    var name: String? = null,

    @field:Element(name = "yearpublished", required = false)
    var yearPublished: Int? = null,
)
