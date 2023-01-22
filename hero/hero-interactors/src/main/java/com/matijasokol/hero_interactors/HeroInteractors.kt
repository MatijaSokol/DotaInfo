package com.matijasokol.hero_interactors

import com.matijasokol.hero_datasource.cache.HeroCache
import com.matijasokol.hero_datasource.network.HeroService
import com.squareup.sqldelight.db.SqlDriver

data class HeroInteractors(
    val getHeros: GetHeros,
    val getHeroFromCache: GetHeroFromCache,
    val filterHeros: FilterHeros
) {

    companion object Factory {
        val schema: SqlDriver.Schema = HeroCache.schema
        val dbName: String = HeroCache.dbName

        fun build(sqlDriver: SqlDriver): HeroInteractors {
            val service = HeroService.build()
            val cache = HeroCache.build(sqlDriver)

            return HeroInteractors(
                getHeros = GetHeros(
                    service = service,
                    cache = cache
                ),
                getHeroFromCache = GetHeroFromCache(
                    cache = cache
                ),
                filterHeros = FilterHeros()
            )
        }
    }
}