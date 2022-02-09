package com.dragonsko.treasurehunt

import java.util.*

object NameGenerator {

    private val SIZE_SMALL_BEFORE =
        listOf(
            Pair("Maigre", "Maigre"),
            Pair("Petit", "Petite"),
            Pair("Modeste", "Modeste"),
            Pair("Médiocre", "Médiocre"),
            Pair("Humble", "Humble"),
        )

    private val SIZE_BIG_BEFORE =
        listOf(
            Pair("Gigantesque", "Gigantesque"),
            Pair("Immense", "Immense"),
            Pair("Gros", "Grosse"),
            Pair("Abondant", "Abondante"),
            Pair("Grand", "Grande"),
            Pair("Massif", "Massive"),
        )

    /**
     * structure:
     * Pair (nom, isMasculine)
     */
    private val MAIN_NAME =
        listOf(
            Pair("trésor", true),
            Pair("héritage", true),
            Pair("magot", true),
            Pair("butin", true),
            Pair("fortune", false),
            Pair("richesse", false),
            Pair("réserve", false),
        )

    val SIZE_SMALL_AFTER =
        listOf(
            Pair("anodin", "anodine"),
            Pair("ordinaire", "ordinaire"),
            Pair("minime", "minime"),
            Pair("oublié", "oubliée"),
        )

    val SIZE_MEDIUM_AFTER =
        listOf(
            Pair("maudit", "maudite"),
            Pair("prisé", "prisée"),
            Pair("scintillant", "scintillante"),
            Pair("ensanglanté", "ensanglantée"),
            Pair("royal", "royale"),
        )

    val SIZE_BIG_AFTER =
        listOf(
            Pair("fantastique", "fantastique"),
            Pair("mythique", "mythique"),
            Pair("légendaire", "légendaire"),
            Pair("épique", "épique"),
            Pair("glorieux", "glorieuse"),
            Pair("inimaginable", "inimaginable"),
        )

    fun generateQuestName(ratio: Double): String {
        var treasureName = ""
        val name_i = Random().nextInt(MAIN_NAME.size)
        val name = MAIN_NAME[name_i]
        // First adjective
        if (ratio < 0.7) {
            val i = Random().nextInt(SIZE_SMALL_BEFORE.size)
            val adj = SIZE_SMALL_BEFORE[i]
            treasureName += if (name.second) adj.first else adj.second
        } else if (ratio > 1.6) {
            val i = Random().nextInt(SIZE_BIG_BEFORE.size)
            val adj = SIZE_BIG_BEFORE[i]
            treasureName += if (name.second) adj.first else adj.second
        }

        if (treasureName.isEmpty()) {
            treasureName += name.first.replaceFirstChar { it.uppercase() }
        } else {
            treasureName += " ${name.first}"
        }

        if (ratio < 1) {
            val i = Random().nextInt(SIZE_SMALL_AFTER.size)
            val adj = SIZE_SMALL_AFTER[i]
            treasureName += " ${if (name.second) adj.first else adj.second}"
        } else if (ratio > 1.3) {
            val i = Random().nextInt(SIZE_BIG_AFTER.size)
            val adj = SIZE_BIG_AFTER[i]
            treasureName += " ${if (name.second) adj.first else adj.second}"
        } else {
            val i = Random().nextInt(SIZE_MEDIUM_AFTER.size)
            val adj = SIZE_MEDIUM_AFTER[i]
            treasureName += " ${if (name.second) adj.first else adj.second}"
        }

        return treasureName
    }
}