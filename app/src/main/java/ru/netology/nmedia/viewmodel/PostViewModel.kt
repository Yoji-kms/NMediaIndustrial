package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.*
import ru.netology.nmedia.callbacks.RepositoryCallback
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.*
import ru.netology.nmedia.util.SingleLiveEvent

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    authorAvatar = "",
    likedByMe = false,
    likes = 0,
    published = 0L
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryImpl()
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data
    private val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }

    fun loadPosts() {
        _data.postValue(FeedModel(loading = true))
        repository.getAllAsync(object : RepositoryCallback<List<Post>> {
            override fun onSuccess(result: List<Post>) {
                _data.postValue(FeedModel(posts = result, empty = result.isEmpty()))
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
            }
        })
    }

    fun save() {
        edited.value?.let {
            repository.saveAsync(object : RepositoryCallback<Post> {
                override fun onSuccess(result: Post) {
                    _postCreated.postValue(Unit)
                }

                override fun onError(e: Exception) {
                    print("Error message: $e")
                }
            }, it)
        }
        edited.value = empty
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun likeById(id: Long, likedByMe: Boolean) {
        val old = _data.value?.posts.orEmpty()

        _data.postValue(
            FeedModel(posts = _data.value?.posts.orEmpty().map {
                if (it.id == id) {
                    if (it.likedByMe) it.copy(likedByMe = !likedByMe,likes = it.likes-1)
                    else it.copy(likedByMe = !likedByMe, likes = it.likes+1)
                }
                else it
            })
        )

        repository.likeByIdAsync(object : RepositoryCallback<Post> {
            override fun onSuccess(result: Post) {}

            override fun onError(e: Exception) {
                _data.postValue(_data.value?.copy(posts = old))
            }
        }, id, likedByMe)
    }

    fun removeById(id: Long) {
        val old = _data.value?.posts.orEmpty()
        _data.postValue(
            _data.value?.copy(posts = _data.value?.posts.orEmpty()
                .filter { it.id != id })
        )

        repository.removeByIdAsync(object : RepositoryCallback<Unit> {
            override fun onError(e: Exception) {
                _data.postValue(_data.value?.copy(posts = old))
            }

            override fun onSuccess(result: Unit) {            }
        }, id)
    }
}
