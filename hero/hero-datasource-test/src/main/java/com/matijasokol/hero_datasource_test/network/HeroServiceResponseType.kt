package com.matijasokol.hero_datasource_test.network

sealed interface HeroServiceResponseType {

    object EmptyList: HeroServiceResponseType

    object MalformedData: HeroServiceResponseType

    object GoodData: HeroServiceResponseType

    object Http404: HeroServiceResponseType
}