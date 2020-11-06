package sk.teamsoft.observableadapterdemo

import android.app.Application
import timber.log.Timber
import timber.log.Timber.DebugTree

/**
 * @author Dusan Bartos
 */
class DemoApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(DebugTree())
    }
}