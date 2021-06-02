package ru.netology.nmedia.callbacks

import ru.netology.nmedia.dto.Post

interface RemoveByIdCallback {
    fun onSuccess()
    fun onError(e: Exception)
}