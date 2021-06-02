package ru.netology.nmedia.callbacks

import ru.netology.nmedia.dto.Post

interface GetAllCallback {
    fun onSuccess(posts: List<Post>)
    fun onError(e: Exception)
}