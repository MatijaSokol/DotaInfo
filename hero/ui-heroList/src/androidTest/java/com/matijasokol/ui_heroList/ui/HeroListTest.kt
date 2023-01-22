package com.matijasokol.ui_heroList.ui

import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import coil.ImageLoader
import com.matijasokol.hero_datasource_test.network.data.HeroDataValid
import com.matijasokol.hero_datasource_test.network.serializeHeroData
import com.matijasokol.ui_heroList.coil.FakeImageLoader
import com.matijasokol.ui_herolist.ui.HeroList
import com.matijasokol.ui_herolist.ui.HeroListState
import org.junit.Rule
import org.junit.Test

class HeroListTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val imageLoader: ImageLoader = FakeImageLoader.build()
    private val heroData = serializeHeroData(HeroDataValid.data)

    @Test
    fun areHerosShown() {
        composeTestRule.setContent {
            val state = remember {
                HeroListState(
                    heros = heroData,
                    filteredHeros = heroData
                )
            }
            HeroList(
                state = state,
                events = {},
                imageLoader = imageLoader,
                navigateToDetailScreen = {}
            )
        }

        composeTestRule.onNodeWithText(text = "Anti-Mage").assertIsDisplayed()
        composeTestRule.onNodeWithText(text = "Axe").assertIsDisplayed()
        composeTestRule.onNodeWithText(text = "Bane").assertIsDisplayed()
    }
}