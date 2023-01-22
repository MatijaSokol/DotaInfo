package com.matijasokol.hero_datasource.network

import com.matijasokol.hero_domain.Hero
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpMethod

class HeroServiceImpl(
    private val httpClient: HttpClient
) : HeroService {

    override suspend fun getHeroStats(): List<Hero> {
        return httpClient.get(EndPoints.HERO_STATS) {
            method = HttpMethod.Get
        }.body<List<HeroDto>>().map { it.toHero() }
    }
}