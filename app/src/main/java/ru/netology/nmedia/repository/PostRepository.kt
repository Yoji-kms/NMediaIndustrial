package ru.netology.nmedia.repository

import ru.netology.nmedia.callbacks.GetAllCallback
import ru.netology.nmedia.callbacks.LikeByIdCallback
import ru.netology.nmedia.callbacks.RemoveByIdCallback
import ru.netology.nmedia.callbacks.SaveCallback
import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getAll(): List<Post>
    fun likeById(id: Long)
    fun save(post: Post)
    fun removeById(id: Long)

    fun getAllAsync(callback: GetAllCallback)
    fun likeByIdAsync(callback: LikeByIdCallback, id: Long)
    fun saveAsync(callback: SaveCallback, post: Post)
    fun removeByIdAsync(callback: RemoveByIdCallback, id: Long)
}
