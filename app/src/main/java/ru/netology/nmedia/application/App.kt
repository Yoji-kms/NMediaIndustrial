package ru.netology.nmedia.application

import android.app.Application
import android.content.Context
import android.os.StrictMode

class App : Application() {
    init {
        instance = this
    }

    companion object {
        private var instance: App? = null

        fun appContext(): Context = instance!!.applicationContext
    }

    override fun onCreate() {
        super.onCreate()

        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
    }
}