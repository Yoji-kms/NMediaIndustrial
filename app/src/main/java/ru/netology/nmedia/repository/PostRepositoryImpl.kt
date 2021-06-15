package ru.netology.nmedia.repository

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.application.App
import ru.netology.nmedia.callbacks.*
import ru.netology.nmedia.dto.Post


class PostRepositoryImpl : PostRepository {

    companion object {
        @SuppressLint("StaticFieldLeak")
        val context: Context = App.appContext()
    }


    override fun getAllAsync(callback: RepositoryCallback<List<Post>>) {
        PostsApi.retrofitService.getAll().enqueue(object : Callback<List<Post>> {
            override fun onResponse(
                call: Call<List<Post>>,
                response: Response<List<Post>>
            ) {

                if (!response.isSuccessful) {
                    Toast.makeText(context, response.message(), Toast.LENGTH_LONG).show()
                    callback.onError(java.lang.RuntimeException(response.message()))
                    return
                }

                callback.onSuccess(
                    response.body() ?: throw java.lang.RuntimeException("body is null")
                )
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                if (t.message != null) Log.e("Error", t.message!!)
            }

        })
    }

    override fun likeByIdAsync(callback: RepositoryCallback<Post>, id: Long, likedByMe: Boolean) {
        val retrofitCallback: Callback<Post> = object : Callback<Post> {
            override fun onResponse(
                call: Call<Post>,
                response: Response<Post>
            ) {
                if (!response.isSuccessful) {
                    Toast.makeText(context, response.message(), Toast.LENGTH_LONG).show()
                    callback.onError(java.lang.RuntimeException(response.message()))
                    return
                }

                callback.onSuccess(
                    response.body() ?: throw java.lang.RuntimeException("Body is null")
                )
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                if (t.message != null) Log.e("Error", t.message!!)
            }
        }

        if (likedByMe) PostsApi.retrofitService.dislikeById(id).enqueue(retrofitCallback)
        else PostsApi.retrofitService.likeById(id).enqueue(retrofitCallback)
    }

    override fun saveAsync(callback: RepositoryCallback<Post>, post: Post) {
        PostsApi.retrofitService.save(post).enqueue(object : Callback<Post> {
            override fun onResponse(
                call: Call<Post>,
                response: Response<Post>
            ) {
                if (!response.isSuccessful) {
                    Toast.makeText(context, response.message(), Toast.LENGTH_LONG).show()
                    callback.onError(java.lang.RuntimeException(response.message()))
                    return
                }

                callback.onSuccess(
                    response.body() ?: throw java.lang.RuntimeException("Body is null")
                )
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                if (t.message != null) Log.e("Error", t.message!!)
            }
        })
    }

    override fun removeByIdAsync(callback: RepositoryCallback<Unit>, id: Long) {
        PostsApi.retrofitService.removeById(id).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (!response.isSuccessful) {
                    Toast.makeText(context, response.message(), Toast.LENGTH_LONG).show()
                    callback.onError(java.lang.RuntimeException(response.message()))
                    return
                }

                callback.onSuccess(
                    response.body() ?: throw java.lang.RuntimeException("Body is null")
                )
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                if (t.message != null) Log.e("Error", t.message!!)
            }
        })
    }
}
