package com.yangpingapps.swipeback

import android.graphics.Color
import android.os.Bundle
import com.yangpingapps.library.SwipeBaseActivity

class Main2Activity : SwipeBaseActivity() {
    override fun getDirection(): Companion.Direction = mDirection

    private lateinit var mDirection: Companion.Direction
    private var mAnimation: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = intent.extras
        mDirection = if (bundle != null) {
            mAnimation = bundle.getBoolean("ANIMTION")
            when (bundle.getString("DIRECTION", "RIGHT")) {
                "LEFT" -> Companion.Direction.LEFT
                "REIGT" -> Companion.Direction.RIGHT
                "DOWN" -> Companion.Direction.DOWN
                "UP" -> Companion.Direction.UP
                else -> Companion.Direction.RIGHT
            }
        } else {
            Companion.Direction.RIGHT
        }
        setContentView(R.layout.activity_main2)
    }

    override fun getShadowColor(): Int {
        return Color.parseColor("#7fff0000")
    }

    override fun getLaunchAnimation(): Boolean {
        return mAnimation
    }
}
