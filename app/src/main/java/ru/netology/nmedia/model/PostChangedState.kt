package ru.netology.nmedia.model

import ru.netology.nmedia.enums.ActionType

data class PostChangedState (
    val id: Long = 0L,
    val actionType: ActionType,
    val failed: Boolean = false
)