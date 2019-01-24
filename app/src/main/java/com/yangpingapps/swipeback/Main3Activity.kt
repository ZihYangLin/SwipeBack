package com.yangpingapps.swipeback

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.yangpingapps.library.SwipeBack
import com.yangpingapps.library.SwipeListener


class Main3Activity : AppCompatActivity() {
    private lateinit var mDirection: SwipeListener.Direction
    private var mAnimation: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = intent.extras
        mDirection = if (bundle != null) {
            mAnimation = bundle.getBoolean("ANIMTION")
            when (bundle.getString("DIRECTION", "RIGHT")) {
                "LEFT" -> SwipeListener.Direction.LEFT
                "REIGT" -> SwipeListener.Direction.RIGHT
                "DOWN" -> SwipeListener.Direction.DOWN
                "UP" -> SwipeListener.Direction.UP
                else -> SwipeListener.Direction.RIGHT
            }
        } else {
            SwipeListener.Direction.RIGHT
        }

        SwipeBack.init(this, mDirection)
            .setLaunchAnimation(mAnimation)
            .setContentView(R.layout.activity_main3)
            .setShadowColor(Color.parseColor("#7400ff00"))
            .attach()
    }
}
