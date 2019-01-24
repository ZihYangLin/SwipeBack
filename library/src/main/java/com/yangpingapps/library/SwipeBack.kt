package com.yangpingapps.library

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout

class SwipeBack private constructor(activity: Activity, direction: SwipeListener.Direction) {
    data class Params(
        val activity: Activity,
        val direction: SwipeListener.Direction,
        var layoutResID: Int = -1,
        var listener: OnSwipeListener? = null,
        var shadow: Int = -1,
        var isAnimation: Boolean = true
    )

    companion object {
        fun init(activity: Activity, direction: SwipeListener.Direction): SwipeBack {
            return SwipeBack(activity, direction)
        }
    }

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

    fun setLaunchAnimation(switch: Boolean): SwipeBack {
        mParams.isAnimation = switch
        return this
    }

    fun attach() {
        val rootView = getContentLayout(mParams)
        mParams.activity.setContentView(rootView)
        if (mParams.isAnimation) {
            when (mParams.direction) {
                SwipeListener.Direction.RIGHT -> mParams.activity.overridePendingTransition(
                    R.anim.slide_in_left,
                    android.R.anim.fade_out
                )
                SwipeListener.Direction.LEFT -> mParams.activity.overridePendingTransition(
                    R.anim.slide_in_right,
                    android.R.anim.fade_out
                )
                SwipeListener.Direction.UP -> mParams.activity.overridePendingTransition(
                    R.anim.slide_in_top,
                    android.R.anim.fade_out
                )
                SwipeListener.Direction.DOWN -> mParams.activity.overridePendingTransition(
                    R.anim.slide_in_bottom,
                    android.R.anim.fade_out
                )
            }
        }
    }

    private fun getContentLayout(params: Params): View {
        if (params.layoutResID == -1) {
            throw NullPointerException("The ContentView is Null.")
        }
        val viewShadow = View(params.activity)
        val view = LayoutInflater.from(params.activity).inflate(params.layoutResID, null)
        setTouchListener(view, SwipeListener(view, params.activity, object : OnSwipeListener {
            override fun onSwiped(persent: Float, position: Float) {
                viewShadow.alpha = 1 - persent
                mParams.listener?.onSwiped(persent, position)
            }
        }, params.direction))

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


    private fun setTouchListener2(activity: Activity,direction: SwipeListener.Direction, view: View, shadow: View, listener: OnSwipeListener?) {
        Log.i("#ocean#3", "setTouchListener=>${view.toString()}")
        if (view !is EditText) {
            view.setOnTouchListener(SwipeListener(view, activity, object : OnSwipeListener {
                override fun onSwiped(persent: Float, position: Float) {
                    shadow.alpha = 1 - persent
                    mParams.listener?.onSwiped(persent, position)
                }
            },direction))
        }
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                setTouchListener2(activity, direction, innerView, shadow, listener)
            }
        }
    }


    private fun setTouchListener(view: View, listener: SwipeListener) {
        Log.i("#ocean#3", "setTouchListener=>${view.toString()}")
        view.setOnTouchListener(listener)
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                setTouchListener(innerView, listener)
            }
        }
    }
}