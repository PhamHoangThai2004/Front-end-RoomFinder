package com.pht.roomfinder.helper

data class UIState(
    val isLoading: Boolean = false,
    val isOpen: Boolean = false,
    val isUpdate: Boolean = false,
    val isShowBottomNavigation: Boolean = true,
    val isNotificationVisible: Boolean = false,
    val isUserPosted: Boolean = false
)