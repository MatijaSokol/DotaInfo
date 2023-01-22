package com.matijasokol.ui_heroDetail.ui

import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import coil.ImageLoader
import com.matijasokol.hero_datasource_test.network.data.HeroDataValid
import com.matijasokol.hero_datasource_test.network.serializeHeroData
import com.matijasokol.ui_heroDetail.coil.FakeImageLoader
import com.matijasokol.ui_herodetail.ui.HeroDetail
import com.matijasokol.ui_herodetail.ui.HeroDetailState
import org.junit.Rule
import org.junit.Test

class HeroDetailTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val imageLoader: ImageLoader = FakeImageLoader.build()
    private val heroData = serializeHeroData(HeroDataValid.data)

    @Test
    fun isHeroDataShown() {
        //choose a random hero
        val hero = heroData.random()
        composeTestRule.setContent {
            val state = remember {
                HeroDetailState(
                    hero = hero
                )
            }
            HeroDetail(
                state = state,
                events = {},
                imageLoader = imageLoader
            )
        }

        composeTestRule.onNodeWithText(text = hero.localizedName).assertIsDisplayed()
        composeTestRule.onNodeWithText(text = hero.primaryAttribute.uiValue).assertIsDisplayed()
        composeTestRule.onNodeWithText(text = hero.attackType.uiValue).assertIsDisplayed()

        val proWinPercentage = (hero.proWins.toDouble() / hero.proPick.toDouble() * 100).toInt()
        composeTestRule.onNodeWithText("$proWinPercentage %")

        val turboWinPercentage = (hero.turboWins.toDouble() / hero.turboPicks.toDouble() * 100).toInt()
        composeTestRule.onNodeWithText("$turboWinPercentage %")
    }
}