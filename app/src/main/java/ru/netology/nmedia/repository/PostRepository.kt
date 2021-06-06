package ru.netology.nmedia.repository

import ru.netology.nmedia.callbacks.RepositoryCallback
import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getAllAsync(callback: RepositoryCallback<List<Post>>)
    fun likeByIdAsync(callback: RepositoryCallback<Post>, id: Long, likedByMe: Boolean)
    fun saveAsync(callback: RepositoryCallback<Post>, post: Post)
    fun removeByIdAsync(callback: RepositoryCallback<Unit>, id: Long)
}
