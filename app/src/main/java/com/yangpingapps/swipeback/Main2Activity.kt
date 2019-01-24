package com.yangpingapps.swipeback

import android.graphics.Color
import android.os.Bundle
import com.yangpingapps.library.SwipeActivity
import com.yangpingapps.library.SwipeListener.Direction

class Main2Activity : SwipeActivity() {
    override fun getDirection(): Direction = mDirection

    private lateinit var mDirection: Direction
    private var mAnimation: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = intent.extras
        mDirection = if (bundle != null) {
            mAnimation = bundle.getBoolean("ANIMTION")
            when (bundle.getString("DIRECTION", "RIGHT")) {
                "LEFT" -> Direction.LEFT
                "REIGT" -> Direction.RIGHT
                "DOWN" -> Direction.DOWN
                "UP" -> Direction.UP
                else -> Direction.RIGHT
            }
        } else {
            Direction.RIGHT
        }
        setContentView(R.layout.activity_main2)
    }

    override fun getShadowColor(): Int {
        return Color.parseColor("#7fff0000")
    }

    override fun getLaunchAnimation(): Boolean {
        return mAnimation
    }

    override fun onSwiped(persent: Float, position: Float) {
        super.onSwiped(persent, position)
    }
}
