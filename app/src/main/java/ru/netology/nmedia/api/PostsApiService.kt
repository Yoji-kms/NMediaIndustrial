package ru.netology.nmedia.api

import retrofit2.Response
import retrofit2.http.*
import ru.netology.nmedia.dto.Post

interface PostsApiService {
    @GET("posts")
    suspend fun getAll() : Response<List<Post>>

    @GET("posts/{id}")
    suspend fun getById(@Path("id") id: Long) : Response<Post>

    @POST("posts")
    suspend fun save (@Body post:Post) : Response<Post>

    @DELETE("posts/{id}")
    suspend fun removeById(@Path("id") id: Long) : Response<Unit>

    @POST("posts/{id}/likes")
    suspend fun likeById(@Path("id") id: Long) : Response<Post>

    @DELETE("posts/{id}/likes")
    suspend fun dislikeById(@Path("id") id: Long) : Response<Post>
}
