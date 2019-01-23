package com.yangpingapps.swipeback

import android.app.Application

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        setTheme(R.style.SwipeAppTheme)
    }
}