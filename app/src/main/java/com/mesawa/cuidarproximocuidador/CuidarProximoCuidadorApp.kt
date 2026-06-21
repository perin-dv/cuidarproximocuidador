package com.mesawa.cuidarproximocuidador

import android.app.Application

class CuidarProximoCuidadorApp : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: CuidarProximoCuidadorApp
            private set
    }
}
