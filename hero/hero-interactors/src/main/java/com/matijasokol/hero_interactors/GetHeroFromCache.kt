package com.matijasokol.hero_interactors

import com.matijasokol.core.domain.DataState
import com.matijasokol.core.domain.ProgressBarState
import com.matijasokol.core.domain.UIComponent
import com.matijasokol.hero_datasource.cache.HeroCache
import com.matijasokol.hero_domain.Hero
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetHeroFromCache(
    private val cache: HeroCache
) {

    fun execute(id: Int): Flow<DataState<Hero>> = flow {
        try {
            emit(
                DataState.Loading(
                    progressBarState = ProgressBarState.Loading
                )
            )

            val cachedHero = cache.getHero(id) ?: throw Exception("That hero does not exist in the cache")

            emit(DataState.Data(cachedHero))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(
                DataState.Response(
                    uiComponent = UIComponent.Dialog(
                        title = "Error",
                        description = e.message ?: "Unknown Error"
                    )
                )
            )
        } finally {
            emit(
                DataState.Loading(
                    progressBarState = ProgressBarState.Idle
                )
            )
        }
    }
}