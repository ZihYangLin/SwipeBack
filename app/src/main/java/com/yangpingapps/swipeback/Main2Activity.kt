package com.yangpingapps.swipeback

import android.graphics.Color
import android.os.Bundle
import com.yangpingapps.library.SwipeBaseActivity

class Main2Activity : SwipeBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
    }

    override fun getShadowColor(): Int {
        return Color.parseColor("#7fff0000")
    }
}
