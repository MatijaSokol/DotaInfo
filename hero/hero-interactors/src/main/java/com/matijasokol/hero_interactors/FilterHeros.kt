package com.matijasokol.hero_interactors

import com.matijasokol.core.domain.FilterOrder
import com.matijasokol.hero_domain.Hero
import com.matijasokol.hero_domain.HeroAttribute
import com.matijasokol.hero_domain.HeroFilter
import kotlin.math.round

class FilterHeros {

    fun execute(
        current: List<Hero>,
        heroName: String,
        heroFilter: HeroFilter,
        attributeFilter: HeroAttribute
    ): List<Hero> {
        var filteredList = current.filter {
            it.localizedName.lowercase().contains(heroName.lowercase())
        }.toMutableList()

        filteredList = when(attributeFilter) {
            HeroAttribute.Agility -> filteredList.filter { it.primaryAttribute is HeroAttribute.Agility }.toMutableList()
            HeroAttribute.Intelligence -> filteredList.filter { it.primaryAttribute is HeroAttribute.Intelligence }.toMutableList()
            HeroAttribute.Strength -> filteredList.filter { it.primaryAttribute is HeroAttribute.Strength }.toMutableList()
            HeroAttribute.Unknown -> filteredList
        }

        when (heroFilter) {
            is HeroFilter.Hero -> {
                when (heroFilter.order) {
                    FilterOrder.Ascending -> filteredList.sortBy { it.localizedName }
                    FilterOrder.Descending -> filteredList.sortByDescending { it.localizedName }
                }
            }
            is HeroFilter.ProWins -> {
                when (heroFilter.order) {
                    FilterOrder.Ascending -> filteredList.sortBy {
                        getWinRate(
                            proPick = it.proPick.toDouble(),
                            proWins = it.proWins.toDouble()
                        )
                    }
                    FilterOrder.Descending -> filteredList.sortByDescending {
                        getWinRate(
                            proPick = it.proPick.toDouble(),
                            proWins = it.proWins.toDouble()
                        )
                    }
                }
            }
        }

        return filteredList
    }

    private fun getWinRate(proPick: Double, proWins: Double): Int {
        return if (proPick <= 0) 0
        else round(proWins / proPick * 100).toInt()
    }
}