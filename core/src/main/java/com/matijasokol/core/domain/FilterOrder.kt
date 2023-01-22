package com.matijasokol.core.domain

sealed interface FilterOrder {

    object Ascending : FilterOrder

    object Descending : FilterOrder
}
