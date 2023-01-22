package com.matijasokol.core.domain

sealed interface UIComponentState {

    object Show : UIComponentState

    object Hide : UIComponentState
}