package com.matijasokol.ui_herodetail.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matijasokol.core.domain.DataState
import com.matijasokol.core.domain.Queue
import com.matijasokol.core.domain.UIComponent
import com.matijasokol.core.util.Logger
import com.matijasokol.hero_interactors.GetHeroFromCache
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class HeroDetailViewModel @Inject constructor(
    private val getHeroFromCache: GetHeroFromCache,
    private val savedStateHandle: SavedStateHandle,
    private val logger: Logger
) : ViewModel() {

    val state = mutableStateOf(HeroDetailState())

    init {
        savedStateHandle.get<Int>("heroId")?.let { heroId ->
            onTriggerEvent(HeroDetailEvent.GetHeroFromCache(heroId))
        }
    }

    fun onTriggerEvent(event: HeroDetailEvent) {
        when (event) {
            is HeroDetailEvent.GetHeroFromCache -> getHeroFromCache(event.id)
            HeroDetailEvent.OnRemoveHeadFromQueue -> removeHeadMessage()
        }
    }

    private fun getHeroFromCache(id: Int) {
        getHeroFromCache.execute(id).onEach { dataState ->
            when (dataState) {
                is DataState.Loading -> {
                    state.value = state.value.copy(progressBarState = dataState.progressBarState)
                }
                is DataState.Data -> {
                    state.value = state.value.copy(hero = dataState.data)
                }
                is DataState.Response -> when (dataState.uiComponent) {
                    is UIComponent.Dialog -> appendToMessageQueue(dataState.uiComponent)
                    is UIComponent.None -> logger.log((dataState.uiComponent as UIComponent.None).message)
                }
            }
        }.launchIn(viewModelScope)
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

    private fun appendToMessageQueue(uiComponent: UIComponent) {
        val queue = state.value.errorQueue
        queue.add(uiComponent)
        state.value = state.value.copy(errorQueue = Queue(mutableListOf())) // force recompose
        state.value = state.value.copy(errorQueue = queue)
    }
}