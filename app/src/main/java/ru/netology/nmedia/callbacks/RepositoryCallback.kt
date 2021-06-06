package ru.netology.nmedia.callbacks

import java.lang.Exception

interface RepositoryCallback<T> {
    fun onSuccess(result: T)
    fun onError(e: Exception)
}