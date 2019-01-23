package com.yangpingapps.library

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout

class SwipeBack(activity: Activity, direction: SwipeListener.Direction) {
    data class Params(
        val activity: Activity,
        val direction: SwipeListener.Direction,
        var layoutResID: Int = -1,
        var listener: OnSwipeListener? = null,
        var shadow: Int = -1,
        var isAnimation: Boolean = true
    )

    private val mParams = Params(activity, direction)

    fun setContentView(layoutResID: Int): SwipeBack {
        mParams.layoutResID = layoutResID
        return this
    }

    fun setListener(listener: OnSwipeListener): SwipeBack {
        mParams.listener = listener
        return this
    }

    fun setShadowColor(color: Int): SwipeBack {
        mParams.shadow = color
        return this
    }

    fun setLaunchAnimation(switch:Boolean):SwipeBack{
        mParams.isAnimation = switch
        return this
    }

    fun attach() {
        val rootView = getContentLayout(mParams)
        mParams.activity.setContentView(rootView)
        if (mParams.isAnimation) {
            when (mParams.direction) {
                SwipeListener.Direction.RIGHT -> mParams.activity.overridePendingTransition(R.anim.slide_in_left, android.R.anim.fade_out)
                SwipeListener.Direction.LEFT -> mParams.activity.overridePendingTransition(R.anim.slide_in_right, android.R.anim.fade_out)
                SwipeListener.Direction.UP -> mParams.activity.overridePendingTransition(R.anim.slide_in_top, android.R.anim.fade_out)
                SwipeListener.Direction.DOWN -> mParams.activity.overridePendingTransition(R.anim.slide_in_bottom, android.R.anim.fade_out)
            }
        }
    }
    private fun getContentLayout(params: Params): View {
        if (params.layoutResID == -1) {
            throw NullPointerException("The ContentView is Null.")
        }
        val viewShadow = View(params.activity)
        if (params.listener == null) {
            params.listener = object : OnSwipeListener {
                override fun onSwiped(persent: Float, position: Float) {
                    viewShadow.alpha = 1 - persent
                }
            }
        }

        val view = LayoutInflater.from(params.activity).inflate(params.layoutResID, null)
        view.setOnTouchListener(SwipeListener(params.activity, params.listener!!, params.direction))
        val root = FrameLayout(params.activity)
        viewShadow.setBackgroundColor(params.shadow)
        val layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT
            , FrameLayout.LayoutParams.MATCH_PARENT
        )
        root.addView(viewShadow, layoutParams)
        root.addView(view)
        return root
    }
}