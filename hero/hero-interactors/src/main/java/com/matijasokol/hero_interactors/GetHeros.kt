package com.matijasokol.hero_interactors

import com.matijasokol.core.domain.DataState
import com.matijasokol.core.domain.ProgressBarState
import com.matijasokol.core.domain.UIComponent
import com.matijasokol.hero_datasource.cache.HeroCache
import com.matijasokol.hero_datasource.network.HeroService
import com.matijasokol.hero_domain.Hero
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetHeros(
    private val service: HeroService,
    private val cache: HeroCache
) {

    fun execute(): Flow<DataState<List<Hero>>> = flow {
        try {
            emit(
                DataState.Loading(
                    progressBarState = ProgressBarState.Loading
                )
            )

            val heros = try {
                service.getHeroStats()
            } catch (e: Exception) {
                e.printStackTrace()
                emit(
                    DataState.Response(
                        uiComponent = UIComponent.Dialog(
                            title = "Network Data Error",
                            description = e.message ?: "Unknown Error"
                        )
                    )
                )
                emptyList()
            }

            cache.insert(heros)

            val cachedHeros = cache.selectAll()

            emit(DataState.Data(data = cachedHeros))
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