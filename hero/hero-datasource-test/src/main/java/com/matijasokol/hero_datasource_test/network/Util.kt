package com.matijasokol.hero_datasource_test.network

import com.matijasokol.hero_datasource.network.HeroDto
import com.matijasokol.hero_datasource.network.toHero
import com.matijasokol.hero_domain.Hero
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString

val json = Json {
    ignoreUnknownKeys = true
}

fun serializeHeroData(jsonData: String): List<Hero> {
    return json.decodeFromString<List<HeroDto>>(
        jsonData
    ).map { it.toHero() }
}