package com.yangpingapps.library

import android.app.Activity
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout

open abstract class SwipeBaseActivity : Activity(), OnSwipeListener {
    abstract fun getDirection(): Direction

    enum class Direction {
        RIGHT, LEFT, UP, DOWN
    }

    private lateinit var viewShadow: View


    override fun setContentView(layoutResID: Int) {
        super.setContentView(getContentLayout(layoutResID))
        if (getLaunchAnimation()) {
            when (getDirection()) {
                Direction.RIGHT -> overridePendingTransition(R.anim.slide_in_left, 0)
                Direction.LEFT -> overridePendingTransition(R.anim.slide_in_right, 0)
                Direction.UP -> overridePendingTransition(R.anim.slide_in_top, 0)
                Direction.DOWN -> overridePendingTransition(R.anim.slide_in_bottom, 0)
            }
        }
    }

    private fun getContentLayout(layoutResID: Int): View {
        val view = LayoutInflater.from(this).inflate(layoutResID, null)
        view.setOnTouchListener(SwipeListener(this, getDirection()))
        val root = FrameLayout(this)
        viewShadow = View(this)
        viewShadow.setBackgroundColor(getShadowColor())
        val params = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT
            , FrameLayout.LayoutParams.MATCH_PARENT
        )
        root.addView(viewShadow, params)
        root.addView(view)
        return root
    }

    override fun onSwiped(persent: Float, position: Float) {
        viewShadow.alpha = 1 - persent
    }

    protected open fun getShadowColor(): Int {
        return ContextCompat.getColor(this, R.color.shadow)
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, android.R.anim.fade_out)
    }

    open fun getLaunchAnimation(): Boolean = true

}