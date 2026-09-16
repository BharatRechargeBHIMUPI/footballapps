package com.aceapps.main.adapter

data class Language(
    val name: String,
    val flag: String,
    val selected: Boolean = false,
    val code: String  // BCP-47 locale tag, e.g. "fr-ML", "en-US"

)