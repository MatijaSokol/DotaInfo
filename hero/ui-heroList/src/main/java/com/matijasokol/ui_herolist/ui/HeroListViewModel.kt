package com.matijasokol.ui_herolist.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matijasokol.core.domain.DataState
import com.matijasokol.core.domain.Queue
import com.matijasokol.core.domain.UIComponent
import com.matijasokol.core.domain.UIComponentState
import com.matijasokol.core.util.Logger
import com.matijasokol.hero_domain.HeroAttribute
import com.matijasokol.hero_domain.HeroFilter
import com.matijasokol.hero_interactors.FilterHeros
import com.matijasokol.hero_interactors.GetHeros
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class HeroListViewModel @Inject constructor(
    private val getHeros: GetHeros,
    private val filterHeros: FilterHeros,
    private val logger: Logger
) : ViewModel() {

    val state = mutableStateOf(HeroListState())

    init {
        onTriggerEvent(HeroListEvents.GetHeros)
    }

    fun onTriggerEvent(event: HeroListEvents) {
        when (event) {
            HeroListEvents.GetHeros -> getHeros()
            HeroListEvents.FilterHeros -> filterHeros()
            is HeroListEvents.UpdateHeroName -> updateHeroName(event.heroName)
            is HeroListEvents.UpdateHeroFilter -> updateHeroFilter(event.heroFilter)
            is HeroListEvents.UpdateFilterDialogState -> updateFilterDialogState(event.uiComponentState)
            is HeroListEvents.UpdateAttributeFilter -> updateAttributeFilter(event.attribute)
            HeroListEvents.OnRemoveHeadFromQueue -> removeHeadMessage()
        }
    }

    private fun removeHeadMessage() {
        try {
            val queue = state.value.errorQueue
            queue.remove()
            state.value = state.value.copy(errorQueue = Queue(mutableListOf())) // force recompose
            state.value = state.value.copy(errorQueue = queue)
        } catch (e: Exception) {
            logger.log("Nothing to remove from DialogQueue")
        }
    }

    private fun updateAttributeFilter(attribute: HeroAttribute) {
        state.value = state.value.copy(
            primaryAttribute = attribute
        )
        filterHeros()
    }

    private fun updateFilterDialogState(uiComponentState: UIComponentState) {
        state.value = state.value.copy(
            filterDialogState = uiComponentState
        )
    }

    private fun updateHeroFilter(heroFilter: HeroFilter) {
        state.value = state.value.copy(
            heroFilter = heroFilter
        )
        filterHeros()
    }

    private fun updateHeroName(heroName: String) {
        state.value = state.value.copy(
            heroName = heroName
        )
    }

    private fun filterHeros() {
        val filteredList = filterHeros.execute(
            current = state.value.heros,
            heroName = state.value.heroName,
            heroFilter = state.value.heroFilter,
            attributeFilter = state.value.primaryAttribute
        )
        state.value = state.value.copy(
            filteredHeros = filteredList
        )
    }

    private fun getHeros() {
        getHeros.execute().onEach { dataState ->
            when (dataState) {
                is DataState.Data -> {
                    state.value = state.value.copy(heros = dataState.data ?: emptyList())
                    filterHeros()
                }
                is DataState.Loading ->
                    state.value = state.value.copy(progressBarState = dataState.progressBarState)
                is DataState.Response -> when (dataState.uiComponent) {
                    is UIComponent.Dialog -> appendToMessageQueue(dataState.uiComponent)
                    is UIComponent.None -> logger.log((dataState.uiComponent as UIComponent.None).message)
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun appendToMessageQueue(uiComponent: UIComponent) {
        val queue = state.value.errorQueue
        queue.add(uiComponent)
        state.value = state.value.copy(errorQueue = Queue(mutableListOf())) // force recompose
        state.value = state.value.copy(errorQueue = queue)
    }
}