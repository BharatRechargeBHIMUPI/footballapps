package com.aceapps.main.model


data class MatchModel(
    val time: String = "",
    val country: String = "",
    val team1: String = "",
    val team2: String = "",
    val pick: String = "",
    val odds: String = "",
    val status: String = "",
    val date: String = "",
    val score: String = "",
    val team1Emoji: String = "",
    val team2Emoji: String = "",

    val createdAt: Long = System.currentTimeMillis()
)

enum class MatchStatus {
    UPCOMING, WIN, LOSS
}