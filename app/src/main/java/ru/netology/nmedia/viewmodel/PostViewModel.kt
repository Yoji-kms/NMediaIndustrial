package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.enums.ActionType
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.model.PostChangedState
import ru.netology.nmedia.repository.*
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.util.SingleLiveEvent

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    authorAvatar = "",
    likedByMe = false,
    likes = 0,
    published = 0L,
    synced = false
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryImpl(
        AppDb.getInstance(context = application).postDao()
    )

    val data: LiveData<FeedModel> = repository.data.map(::FeedModel)
    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    private val edited = MutableLiveData(empty)

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    private val _networkError = MutableLiveData<Boolean>()
    val networkError: LiveData<Boolean>
        get() = _networkError

    private val postChangedStateList = mutableListOf<PostChangedState>()

    init {
        loadPosts()
    }

    fun loadPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true, error = false)
            if (_networkError.value == true) {
                    postChangedStateList.map {
                        async { repository.retry(it.actionType, it.id, it.newContent) }
                    }.awaitAll()
                    postChangedStateList.clear()
            }
            repository.getAll()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun refreshPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(refreshing = true, error = false)
            if (_networkError.value == true) {
                    postChangedStateList.map {
                        async { repository.retry(it.actionType, it.id, it.newContent) }
                    }.awaitAll()
                    postChangedStateList.clear()
                }
            repository.getAll()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun save() {
        edited.value?.let {
            _postCreated.value = Unit
            viewModelScope.launch {
                try {
                    _networkError.value = false
                    val id = repository.save(it)
                    edited.value = it.copy(id = id)
                    _dataState.value = FeedModelState()
                } catch (e: Exception) {
                    _networkError.value = true
                    postChangedStateList.add(
                        PostChangedState(
                            id = it.id, actionType = ActionType.SAVE, newContent = it.content
                        )
                    )
                }
            }
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

    fun likeById(id: Long) = viewModelScope.launch {
        try {
            _networkError.value = false
            repository.likeById(id)
        } catch (e: Exception) {
            _networkError.value = true
            postChangedStateList.add(
                PostChangedState(id = id, actionType = ActionType.LIKE)
            )
        }
    }

    fun removeById(id: Long) = viewModelScope.launch {
        try {
            _networkError.value = false
            repository.removeById(id)
        } catch (e: Exception) {
            _networkError.value = true
            postChangedStateList.add(
                PostChangedState(id = id, actionType = ActionType.REMOVE)
            )
        }
    }
}
