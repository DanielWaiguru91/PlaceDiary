package tech.danielwaiguru.placediary

import android.app.Application
import timber.log.Timber

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        initTimber()
    }
    private fun initTimber(){
        Timber.plant(Timber.DebugTree())
    }
}