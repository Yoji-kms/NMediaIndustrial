package ru.netology.nmedia.repository

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.application.App
import ru.netology.nmedia.callbacks.*
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toDto
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import java.io.IOException
import kotlin.Exception


class PostRepositoryImpl(private val dao: PostDao) : PostRepository {

    companion object {
        @SuppressLint("StaticFieldLeak")
        val context: Context = App.appContext()
    }

    override val data: LiveData<List<Post>> = dao.getAll().map(List<PostEntity>::toDto)

    override suspend fun getAll() {
        try {
            val response = PostsApi.retrofitService.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(body.toEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun likeById(id: Long) {
        try {
            dao.likeById(id)
            val likedByMe = getById(id).likedByMe
            val response = if (!likedByMe) PostsApi.retrofitService.dislikeById(id)
            else PostsApi.retrofitService.likeById(id)

            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun save(post: Post): Long {
        try {
            val id = dao.save(PostEntity.fromDto(post))
            val response = PostsApi.retrofitService.save(post.copy(synced = true))
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
//            val body = response.body() ?: throw ApiError(response.code(), response.message())
            return id
//            dao.save(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun getById(id: Long): Post =
        try {
            dao.getById(id).toDto()
        } catch (e: Exception) {
            throw UnknownError
        }


    override suspend fun removeById(id: Long) {
        try {
            dao.removeById(id)
            val response = PostsApi.retrofitService.removeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }
}
