package com.btryx.yourmoodcalendar


import android.app.Application
import com.btryx.yourmoodcalendar.database.AppDatabase
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate() }
}