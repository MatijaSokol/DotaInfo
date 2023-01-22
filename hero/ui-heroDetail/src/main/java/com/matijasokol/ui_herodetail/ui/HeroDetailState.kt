package com.matijasokol.ui_herodetail.ui

import com.matijasokol.core.domain.ProgressBarState
import com.matijasokol.core.domain.Queue
import com.matijasokol.core.domain.UIComponent
import com.matijasokol.hero_domain.Hero

data class HeroDetailState(
    val progressBarState: ProgressBarState = ProgressBarState.Idle,
    val hero: Hero? = null,
    var errorQueue: Queue<UIComponent> = Queue(mutableListOf())
)
