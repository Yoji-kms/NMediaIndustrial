package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.enums.ActionType

interface PostRepository {
    val data: LiveData<List<Post>>
    suspend fun getAll()
    suspend fun getById(id: Long): Post
    suspend fun likeById(id: Long)
    suspend fun save(post: Post): Long
    suspend fun removeById(id: Long)
    suspend fun retry(action: ActionType, id: Long, newContent: String)
}
