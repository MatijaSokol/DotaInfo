package com.matijasokol.dotainfo.ui

import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.ImageLoader
import com.matijasokol.dotainfo.MainActivity
import com.matijasokol.dotainfo.coil.FakeImageLoader
import com.matijasokol.dotainfo.di.HeroInteractorsModule
import com.matijasokol.dotainfo.ui.navigation.Screen
import com.matijasokol.dotainfo.ui.theme.DotaInfoTheme
import com.matijasokol.hero_datasource.cache.HeroCache
import com.matijasokol.hero_datasource.network.HeroService
import com.matijasokol.hero_datasource_test.cache.HeroCacheFake
import com.matijasokol.hero_datasource_test.cache.HeroDatabaseFake
import com.matijasokol.hero_datasource_test.network.HeroServiceFake
import com.matijasokol.hero_datasource_test.network.HeroServiceResponseType
import com.matijasokol.hero_domain.HeroAttribute
import com.matijasokol.hero_interactors.FilterHeros
import com.matijasokol.hero_interactors.GetHeroFromCache
import com.matijasokol.hero_interactors.GetHeros
import com.matijasokol.hero_interactors.HeroInteractors
import com.matijasokol.ui_herodetail.ui.HeroDetail
import com.matijasokol.ui_herodetail.ui.HeroDetailViewModel
import com.matijasokol.ui_herolist.ui.HeroList
import com.matijasokol.ui_herolist.ui.HeroListViewModel
import com.matijasokol.ui_herolist.ui.test.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Singleton

/**
 * NOTE: These tests will fail with Accompanist Animations for navigation transitions.
 * To get them to pass you can't use 'import com.google.accompanist.navigation.animation.composable'
 *
 * End to end tests for the HeroList Screen.
 * 1. Searching for a hero by name
 * 2. Ordering the data by hero name (desc and asc)
 * 3. Ordering the data by pro win % (desc and asc)
 * 4. Filtering the data by hero primary attribute (Strength / Agility / Intelligence)
 */
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@UninstallModules(HeroInteractorsModule::class)
@HiltAndroidTest
class HeroListEndToEnd {

    @Module
    @InstallIn(SingletonComponent::class)
    object TestHeroInteractorsModule {

        @Provides
        @Singleton
        fun provideHeroCache(): HeroCache {
            return HeroCacheFake(HeroDatabaseFake())
        }

        @Provides
        @Singleton
        fun provideHeroService(): HeroService {
            return HeroServiceFake.build(
                type = HeroServiceResponseType.GoodData
            )
        }

        @Provides
        @Singleton
        fun provideHeroInteractors(
            cache: HeroCache,
            service: HeroService
        ): HeroInteractors {
            return HeroInteractors(
                getHeros = GetHeros(
                    cache = cache,
                    service = service
                ),
                filterHeros = FilterHeros(),
                getHeroFromCache = GetHeroFromCache(
                    cache = cache
                )
            )
        }
    }

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val imageLoader: ImageLoader = FakeImageLoader.build()

    @Before
    fun before() {
        composeTestRule.activity.setContent {
            DotaInfoTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Screen.HeroList.route,
                    builder = {
                        composable(
                            route = Screen.HeroList.route
                        ) {
                            val viewModel: HeroListViewModel = hiltViewModel()
                            HeroList(
                                state = viewModel.state.value,
                                events = viewModel::onTriggerEvent,
                                navigateToDetailScreen = { heroId ->
                                    navController.navigate("${Screen.HeroDetail.route}/$heroId")
                                },
                                imageLoader = imageLoader
                            )
                        }
                        composable(
                            route = Screen.HeroDetail.route + "/{heroId}",
                            arguments = Screen.HeroDetail.arguments
                        ) {
                            val viewModel: HeroDetailViewModel = hiltViewModel()
                            HeroDetail(
                                state = viewModel.state.value,
                                events = viewModel::onTriggerEvent,
                                imageLoader = imageLoader
                            )
                        }
                    }
                )
            }
        }
    }

    @Test
    fun testSearchHeroByName() {
        composeTestRule.onRoot(useUnmergedTree = true).printToLog("TAG") // For learning the ui tree system

        composeTestRule.onNodeWithTag(TAG_HERO_SEARCH_BAR).performTextInput("Anti-Mage")
        composeTestRule.onNodeWithTagUnmerged(TAG_HERO_NAME).assertTextEquals("Anti-Mage")
        composeTestRule.onNodeWithTag(TAG_HERO_SEARCH_BAR).performTextClearance()

        composeTestRule.onNodeWithTag(TAG_HERO_SEARCH_BAR).performTextInput("Storm Spirit")
        composeTestRule.onNodeWithTagUnmerged(TAG_HERO_NAME).assertTextEquals("Storm Spirit")
        composeTestRule.onNodeWithTag(TAG_HERO_SEARCH_BAR).performTextClearance()

        composeTestRule.onNodeWithTag(TAG_HERO_SEARCH_BAR).performTextInput("Mirana")
        composeTestRule.onNodeWithTagUnmerged(TAG_HERO_NAME).assertTextEquals("Mirana")
    }

    @Test
    fun testFilterHeroAlphabetically() {
        // Show the dialog
        composeTestRule.onNodeWithTagUnmerged(TAG_HERO_FILTER_BTN).performClick()

        // Confirm the filter dialog is showing
        composeTestRule.onNodeWithTagUnmerged(TAG_HERO_FILTER_DIALOG).assertIsDisplayed()

        // Filter by "Hero" name
        composeTestRule.onNodeWithTagUnmerged(TAG_HERO_FILTER_HERO_CHECKBOX).performClick()

        // Order Descending (z-a)
        composeTestRule.onNodeWithTagUnmerged(TAG_HERO_FILTER_DESC).performClick()

        // Close the dialog
        composeTestRule.onNodeWithTagUnmerged(TAG_HERO_FILTER_DIALOG_DONE).performClick()

        // Confirm the order is correct
        composeTestRule.onAllNodesWithTagUnmerged(TAG_HERO_NAME).assertAny(hasText("Zeus"))

        // Show the dialog
        composeTestRule.onNodeWithTagUnmerged(TAG_HERO_FILTER_BTN).performClick()

        // Order Ascending (a-z)
        composeTestRule.onNodeWithTagUnmerged(TAG_HERO_FILTER_ASC).performClick()

        // Close the dialog
        composeTestRule.onNodeWithTagUnmerged(TAG_HERO_FILTER_DIALOG_DONE).performClick()

        // Confirm the order is correct
        composeTestRule.onAllNodesWithTagUnmerged(TAG_HERO_NAME).assertAny(hasText("Abaddon"))
    }

    @Test
    fun testFilterHeroByProWins() {
        // Show the dialog
        composeTestRule.onNodeWithTagUnmerged(TAG_HERO_FILTER_BTN).performClick()

        // Confirm the filter dialog is showing
        composeTestRule.onNodeWithTagUnmerged(TAG_HERO_FILTER_DIALOG).assertIsDisplayed()

        // Filter by ProWin %
        composeTestRule.onNodeWithTagUnmerged(TAG_HERO_FILTER_PROWINS_CHECKBOX).performClick()

        // Order Descending (100% - 0%)
        composeTestRule.onNodeWithTagUnmerged(TAG_HERO_FILTER_DESC).performClick()

        // Close the dialog
        composeTestRule.onNodeWithTagUnmerged(TAG_HERO_FILTER_DIALOG_DONE).performClick()

        // Confirm the order is correct
        composeTestRule.onAllNodesWithTagUnmerged(TAG_HERO_NAME).assertAny(hasText("Chen"))

        // Show the dialog
        composeTestRule.onNodeWithTagUnmerged(TAG_HERO_FILTER_BTN).performClick()

        // Order Ascending (0% - 100%)
        composeTestRule.onNodeWithTagUnmerged(TAG_HERO_FILTER_ASC).performClick()

        // Close the dialog
        composeTestRule.onNodeWithTagUnmerged(TAG_HERO_FILTER_DIALOG_DONE).performClick()

        // Confirm the order is correct
        composeTestRule.onAllNodesWithTagUnmerged(TAG_HERO_NAME).assertAny(hasText("Dawnbreaker"))
    }

    @Test
    fun testFilterHeroByStrength() {
        // Show the dialog
        composeTestRule.onNodeWithTagUnmerged(TAG_HERO_FILTER_BTN).performClick()

        // Confirm the filter dialog is showing
        composeTestRule.onNodeWithTagUnmerged(TAG_HERO_FILTER_DIALOG).assertIsDisplayed()

        composeTestRule.onNodeWithTagUnmerged(TAG_HERO_FILTER_STENGTH_CHECKBOX).performClick()

        // Close the dialog
        composeTestRule.onNodeWithTagUnmerged(TAG_HERO_FILTER_DIALOG_DONE).performClick()

        // Confirm that only STRENGTH heros are showing
        composeTestRule.onAllNodesWithTagUnmerged(TAG_HERO_PRIMARY_ATTRIBUTE).assertAll(hasText(HeroAttribute.Strength.uiValue))
    }

    @Test
    fun testFilterHeroByAgility() {
        // Show the dialog
        composeTestRule.onNodeWithTagUnmerged(TAG_HERO_FILTER_BTN).performClick()

        // Confirm the filter dialog is showing
        composeTestRule.onNodeWithTagUnmerged(TAG_HERO_FILTER_DIALOG).assertIsDisplayed()

        composeTestRule.onNodeWithTagUnmerged(TAG_HERO_FILTER_AGILITY_CHECKBOX).performClick()

        // Close the dialog
        composeTestRule.onNodeWithTagUnmerged(TAG_HERO_FILTER_DIALOG_DONE).performClick()

        // Confirm that only STRENGTH heros are showing
        composeTestRule.onAllNodesWithTagUnmerged(TAG_HERO_PRIMARY_ATTRIBUTE).assertAll(hasText(HeroAttribute.Agility.uiValue))
    }

    @Test
    fun testFilterHeroByIntelligence() {
        // Show the dialog
        composeTestRule.onNodeWithTagUnmerged(TAG_HERO_FILTER_BTN).performClick()

        // Confirm the filter dialog is showing
        composeTestRule.onNodeWithTagUnmerged(TAG_HERO_FILTER_DIALOG).assertIsDisplayed()

        composeTestRule.onNodeWithTagUnmerged(TAG_HERO_FILTER_INT_CHECKBOX).performClick()

        // Close the dialog
        composeTestRule.onNodeWithTagUnmerged(TAG_HERO_FILTER_DIALOG_DONE).performClick()

        // Confirm that only STRENGTH heros are showing
        composeTestRule.onAllNodesWithTagUnmerged(TAG_HERO_PRIMARY_ATTRIBUTE).assertAll(hasText(HeroAttribute.Intelligence.uiValue))
    }
}

fun SemanticsNodeInteractionsProvider.onNodeWithTagUnmerged(testTag: String): SemanticsNodeInteraction =
    onNodeWithTag(testTag, useUnmergedTree = true)

fun SemanticsNodeInteractionsProvider.onAllNodesWithTagUnmerged(testTag: String): SemanticsNodeInteractionCollection =
    onAllNodesWithTag(testTag, useUnmergedTree = true)