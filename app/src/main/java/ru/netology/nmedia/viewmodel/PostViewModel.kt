package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.*
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
    published = 0L
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

    private val _postChangedState = MutableLiveData<PostChangedState>()
    val postChangedState: LiveData<PostChangedState>
        get() = _postChangedState

    init {
        loadPosts()
    }

    fun loadPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true, error = false)
            with(_postChangedState.value) {
                if (this?.failed == true) {
                    when (this.actionType) {
                        ActionType.LIKE -> likeById(this.id)
                        ActionType.REMOVE -> removeById(this.id)
                        ActionType.SAVE -> save()
                    }
                }
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
            with(_postChangedState.value) {
                if (this?.failed == true) {
                    when (this.actionType) {
                        ActionType.LIKE -> likeById(this.id)
                        ActionType.REMOVE -> removeById(this.id)
                        ActionType.SAVE -> save()
                    }
                }
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
                    val post = try {
                        repository.getById(_postChangedState.value?.id ?: 0L)
                    } catch (e: Exception) {
                        it
                    }
                    _postChangedState.value =
                        PostChangedState(actionType = ActionType.SAVE, failed = false)
                    val id = repository.save(post)
                    edited.value = post.copy(id = id)
                    _dataState.value = FeedModelState()
                } catch (e: Exception) {
                    _postChangedState.value =
                        PostChangedState(id = it.id, actionType = ActionType.SAVE, failed = true)
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
            _postChangedState.value =
                PostChangedState(actionType = ActionType.LIKE, failed = false)
            repository.likeById(id)
        } catch (e: Exception) {
            _postChangedState.value =
                PostChangedState(id = id, actionType = ActionType.LIKE, failed = true)
        }
    }

    fun removeById(id: Long) = viewModelScope.launch {
        try {
            _postChangedState.value =
                PostChangedState(actionType = ActionType.REMOVE, failed = false)
            repository.removeById(id)
        } catch (e: Exception) {
            _postChangedState.value =
                PostChangedState(id = id, actionType = ActionType.REMOVE, failed = true)
        }
    }
}
