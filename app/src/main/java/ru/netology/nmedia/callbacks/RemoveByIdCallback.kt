package ru.netology.nmedia.callbacks

interface RemoveByIdCallback {
    fun onSuccess()
    fun onError(e: Exception)
}