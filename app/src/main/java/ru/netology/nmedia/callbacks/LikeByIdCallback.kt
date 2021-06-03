package ru.netology.nmedia.callbacks

import ru.netology.nmedia.dto.Post

interface LikeByIdCallback {
    fun onSuccess(post: Post)
    fun onError(e: Exception)
}