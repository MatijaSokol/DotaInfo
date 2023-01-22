package com.matijasokol.ui_herodetail.ui

sealed interface HeroDetailEvent {

    data class GetHeroFromCache(
        val id: Int
    ) : HeroDetailEvent

    object OnRemoveHeadFromQueue : HeroDetailEvent
}