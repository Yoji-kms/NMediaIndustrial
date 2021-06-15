package ru.netology.nmedia.api

import retrofit2.Call
import retrofit2.http.*
import ru.netology.nmedia.dto.Post

interface PostsApiService {
    @GET("posts")
    fun getAll() : Call<List<Post>>

    @GET("posts/{id}")
    fun getById(@Path("id") id: Long) : Call<Post>

    @POST("posts")
    fun save (@Body post:Post) : Call<Post>

    @DELETE("posts/{id}")
    fun removeById(@Path("id") id: Long) : Call<Unit>

    @POST("posts/{id}/likes")
    fun likeById(@Path("id") id: Long) : Call<Post>

    @DELETE("posts/{id}/likes")
    fun dislikeById(@Path("id") id: Long) : Call<Post>
}
