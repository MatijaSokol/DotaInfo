package com.matijasokol.ui_herolist.ui

import com.matijasokol.core.domain.ProgressBarState
import com.matijasokol.core.domain.Queue
import com.matijasokol.core.domain.UIComponent
import com.matijasokol.core.domain.UIComponentState
import com.matijasokol.hero_domain.Hero
import com.matijasokol.hero_domain.HeroAttribute
import com.matijasokol.hero_domain.HeroFilter

data class HeroListState(
    val progressBarState: ProgressBarState = ProgressBarState.Idle,
    val heros: List<Hero> = emptyList(),
    val filteredHeros: List<Hero> = emptyList(),
    val heroName: String = "",
    val heroFilter: HeroFilter = HeroFilter.Hero(),
    val primaryAttribute: HeroAttribute = HeroAttribute.Unknown,
    val filterDialogState: UIComponentState = UIComponentState.Hide,
    var errorQueue: Queue<UIComponent> = Queue(mutableListOf())
)
