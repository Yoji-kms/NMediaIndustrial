package ru.netology.nmedia.callbacks

interface LikeByIdCallback {
    fun onSuccess()
    fun onError(e: Exception)
}