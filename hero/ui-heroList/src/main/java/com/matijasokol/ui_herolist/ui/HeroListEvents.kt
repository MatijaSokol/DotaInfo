package com.matijasokol.ui_herolist.ui

import com.matijasokol.core.domain.UIComponentState
import com.matijasokol.hero_domain.HeroAttribute
import com.matijasokol.hero_domain.HeroFilter

sealed interface HeroListEvents {

    object GetHeros : HeroListEvents

    object FilterHeros : HeroListEvents

    data class UpdateHeroName(
        val heroName: String
    ) : HeroListEvents

    data class UpdateHeroFilter(
        val heroFilter: HeroFilter
    ) : HeroListEvents

    data class UpdateFilterDialogState(
        val uiComponentState: UIComponentState
    ) : HeroListEvents

    data class UpdateAttributeFilter(
        val attribute: HeroAttribute
    ) : HeroListEvents

    object OnRemoveHeadFromQueue : HeroListEvents
}
