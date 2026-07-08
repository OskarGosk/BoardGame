package com.goskar.boardgame.data.models

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.simpleframework.xml.core.Persister

class BoardGameDetailsParsingTest {

    // Trimmed but representative /boardgame/298047?stats=1 response (Marvel United),
    // keeping the fields we now parse plus some we intentionally ignore (publisher, family, poll).
    private val marvelUnitedXml = """
        <boardgames termsofuse="https://boardgamegeek.com/xmlapi/termsofuse">
            <boardgame objectid="298047">
                <yearpublished>2020</yearpublished>
                <minplayers>1</minplayers>
                <maxplayers>4</maxplayers>
                <playingtime>40</playingtime>
                <minplaytime>40</minplaytime>
                <maxplaytime>40</maxplaytime>
                <age>14</age>
                <name primary="true" sortindex="1">Marvel United</name>
                <thumbnail>thumb.jpg</thumbnail>
                <image>image.jpg</image>
                <description>Cooperative superhero game.</description>
                <boardgamecategory objectid="1002">Card Game</boardgamecategory>
                <boardgamecategory objectid="1116">Comic Book / Strip</boardgamecategory>
                <boardgamemechanic objectid="2023">Cooperative Game</boardgamemechanic>
                <boardgamemechanic objectid="2040">Hand Management</boardgamemechanic>
                <boardgamedesigner objectid="6838">Andrea Chiarvesio</boardgamedesigner>
                <boardgamedesigner objectid="1533">Eric M. Lang</boardgamedesigner>
                <boardgamepublisher objectid="21608">CMON Global Limited</boardgamepublisher>
                <boardgameartist objectid="14551">Edouard Guiton</boardgameartist>
                <boardgamefamily objectid="17935">Theme: Superheroes</boardgamefamily>
                <boardgameexpansion objectid="321772">Marvel United: Adam Warlock</boardgameexpansion>
                <boardgameexpansion objectid="379766">Marvel United: Annihilation</boardgameexpansion>
                <poll name="suggested_numplayers" title="User Suggested Number of Players" totalvotes="221">
                    <results numplayers="3">
                        <result value="Best" numvotes="132" />
                        <result value="Recommended" numvotes="50" />
                    </results>
                </poll>
                <poll-summary name="suggested_numplayers" title="User Suggested Number of Players">
                    <result name="bestwith" value="Best with 3 players" />
                    <result name="recommmendedwith" value="Recommended with 1-4 players" />
                </poll-summary>
                <statistics page="1">
                    <ratings>
                        <average>7.5</average>
                        <bayesaverage>7.1</bayesaverage>
                        <averageweight>1.8</averageweight>
                    </ratings>
                </statistics>
            </boardgame>
        </boardgames>
    """.trimIndent()

    private fun parse(): BoardGameBGG =
        Persister().read(BoardGamesDetails::class.java, marvelUnitedXml)
            .boardGamesBGG!!.first()

    @Test
    fun parsesCoreAndPlaytimeRange() {
        val bg = parse()
        assertEquals("298047", bg.id)
        assertEquals(1, bg.minPlayers)
        assertEquals(4, bg.maxPlayers)
        assertEquals(40, bg.playingTime)
        assertEquals(40, bg.minPlayTime)
        assertEquals(40, bg.maxPlayTime)
        assertEquals(14, bg.age)
    }

    @Test
    fun parsesCategoriesAndMechanics() {
        val bg = parse()
        assertEquals(listOf("Card Game", "Comic Book / Strip"), bg.categories?.map { it.value })
        assertEquals(listOf("Cooperative Game", "Hand Management"), bg.mechanics?.map { it.value })
    }

    @Test
    fun detectsCooperativeMechanic() {
        assertTrue(parse().isCooperative())
    }

    @Test
    fun parsesExpansionsWithIds() {
        val expansions = parse().expansions.orEmpty()
        assertEquals(2, expansions.size)
        assertEquals("321772", expansions[0].id)
        assertEquals("Marvel United: Adam Warlock", expansions[0].name)
    }

    @Test
    fun parsesStatisticsRating() {
        val bg = parse()
        assertEquals(7.5, bg.averageRating()!!, 0.0001)
        assertEquals(1.8, bg.statistics?.ratings?.averageWeight!!, 0.0001)
    }

    @Test
    fun parsesCreditsAndBestPlayers() {
        val bg = parse()
        assertEquals(listOf("Andrea Chiarvesio", "Eric M. Lang"), bg.designers?.map { it.value })
        assertEquals(listOf("CMON Global Limited"), bg.publishers?.map { it.value })
        assertEquals(listOf("Edouard Guiton"), bg.artists?.map { it.value })
        assertEquals("Best with 3 players", bg.bestPlayers())
    }
}
