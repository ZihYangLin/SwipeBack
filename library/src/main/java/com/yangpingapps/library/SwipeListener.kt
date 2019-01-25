package com.yangpingapps.library

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager


class SwipeListener(
    private val rootView: View,
    private val activity: Activity,
    private val listener: OnSwipeListener,
    private val direction: Direction
) : View.OnTouchListener {
    public enum class Direction {
        RIGHT, LEFT, UP, DOWN
    }

    enum class Orientation {
        Horizontal, Vertical
    }

    data class ActionTarget(
        var downX: Float = 0f, var positionX: Float = 0f
        , var downY: Float = 0f, var positionY: Float = 0f
        , var targetTime: Long = 0L, var isFinished: Boolean = false
    )

    companion object {
        const val TAG = "SwipeListener"

        data class Screen(val widthPixels: Int, val heightPixels: Int)

        fun getScreenSize(context: Context): Screen {
            val dm = DisplayMetrics()
            val windowManager = context
                .getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay.getMetrics(dm)
            return Screen(dm.widthPixels, dm.heightPixels)
        }
    }

    private var isLocked = false
    private var orientation = Orientation.Horizontal
    private val screenWidth: Float = getScreenSize(activity).widthPixels.toFloat()
    private val screenHeight: Float = getScreenSize(activity).heightPixels.toFloat()
    private val SWIPE_BACK_DISTANCE = when (direction) {
        Direction.RIGHT, Direction.LEFT -> screenWidth * 2 / 5
        Direction.UP, Direction.DOWN -> screenHeight * 2 / 5
    }
    private val FLING_BACK_DISTANCE = 200
    private val FLING_BACK_DURATION = when (direction) {
        Direction.RIGHT, Direction.LEFT -> screenWidth / 3
        Direction.UP, Direction.DOWN -> screenHeight / 3
    }
    private val mTarget = ActionTarget()

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (event != null) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    isLocked = false
                    mTarget.isFinished = false
                    mTarget.targetTime = System.currentTimeMillis()
                    mTarget.downX = event.rawX
                    mTarget.downY = event.rawY
                    orientation = Orientation.Horizontal
                    return true
                }
                MotionEvent.ACTION_MOVE -> {

                    mTarget.positionX = if (Math.abs(event.rawX - mTarget.downX) > 0) event.rawX - mTarget.downX else 0f
                    mTarget.positionY = if (Math.abs(event.rawY - mTarget.downY) > 0) event.rawY - mTarget.downY else 0f

                    /*Define the scroll drection.*/
                    if (!isLocked) {
                        orientation = if (Math.abs(mTarget.positionX) > Math.abs(mTarget.positionY)) {
                            Orientation.Horizontal
                        } else {
                            Orientation.Vertical
                        }
                        if (Math.abs(mTarget.positionX) > 50 || Math.abs(mTarget.positionY) > 50) {
                            isLocked = true
                        }
                    }

                    Log.i("#ocean#", "SD: ${orientation}, move=> x:${mTarget.positionX}, y:${mTarget.positionY}")
                    when (direction) {
                        Direction.RIGHT, Direction.LEFT -> {
                            if (orientation == Orientation.Horizontal) {
                                mTarget.isFinished = Math.abs(mTarget.positionX) > SWIPE_BACK_DISTANCE
                                listener.onSwiped(
                                    Math.min(Math.abs(mTarget.positionX) / screenWidth, 1f),
                                    mTarget.positionX
                                )
                                rootView.translationX = if (direction == Direction.RIGHT) {
                                    Math.max(mTarget.positionX, 0f)
                                } else {
                                    Math.min(mTarget.positionX, 0f)
                                }
                            }

                            return orientation == Orientation.Horizontal
                        }
                        Direction.DOWN, Direction.UP -> {
                            if (orientation == Orientation.Vertical) {
                                mTarget.isFinished = Math.abs(mTarget.positionY) > SWIPE_BACK_DISTANCE
                                listener.onSwiped(
                                    Math.min(Math.abs(mTarget.positionY) / screenHeight, 1f),
                                    mTarget.positionY
                                )
                                rootView.translationY = if (direction == Direction.DOWN) {
                                    Math.max(mTarget.positionY, 0f)
                                } else {
                                    Math.min(mTarget.positionY, 0f)
                                }
                            }

                            return orientation == Orientation.Vertical
                        }
                    }

                }
                MotionEvent.ACTION_UP -> {
                    isLocked = false
                    when (direction) {
                        Direction.RIGHT -> {
                            if (orientation == Orientation.Horizontal) {
                                /*FlingBack*/
                                if (System.currentTimeMillis() - mTarget.targetTime < FLING_BACK_DURATION) {
                                    if (mTarget.positionX > FLING_BACK_DISTANCE) {
                                        finishByHorizontalAnimation(mTarget.positionX, screenWidth)
                                        return false
                                    }
                                }

                                /*SwipeBack*/
                                if (mTarget.positionX > 0) {
                                    if (mTarget.isFinished) {
                                        finishByHorizontalAnimation(mTarget.positionX, screenWidth)
                                        return false
                                    } else {
                                        returnBack()
                                    }
                                }
                            }
                        }
                        Direction.LEFT -> {
                            if (orientation == Orientation.Horizontal) {
                                /*FlingBack*/
                                if (System.currentTimeMillis() - mTarget.targetTime < FLING_BACK_DURATION) {
                                    if (-mTarget.positionX > FLING_BACK_DISTANCE) {
                                        finishByHorizontalAnimation(mTarget.positionX, -screenWidth)
                                        return false
                                    }
                                }

                                /*SwipeBack*/
                                if (mTarget.positionX < 0) {
                                    if (mTarget.isFinished) {
                                        finishByHorizontalAnimation(mTarget.positionX, -screenWidth)
                                        return false
                                    } else {
                                        returnBack()
                                    }
                                }
                            }
                        }
                        Direction.DOWN -> {
                            if (orientation == Orientation.Vertical) {
                                /*FlingBack*/
                                if (System.currentTimeMillis() - mTarget.targetTime < FLING_BACK_DURATION) {
                                    if (mTarget.positionY > FLING_BACK_DISTANCE) {
                                        finishByVerticalAnimation(mTarget.positionY, screenHeight)
                                        return false
                                    }
                                }

                                /*SwipeBack*/
                                if (mTarget.positionY > 0) {
                                    if (mTarget.isFinished) {
                                        finishByVerticalAnimation(mTarget.positionY, screenHeight)
                                        return false
                                    } else {
                                        returnBack()
                                    }
                                }
                            }
                        }
                        Direction.UP -> {
                            if (orientation == Orientation.Vertical) {
                                /*FlingBack*/
                                if (System.currentTimeMillis() - mTarget.targetTime < FLING_BACK_DURATION) {
                                    if (-mTarget.positionY > FLING_BACK_DISTANCE) {
                                        finishByVerticalAnimation(mTarget.positionY, -screenHeight)
                                        return false
                                    }
                                }

                                /*SwipeBack*/
                                if (mTarget.positionY < 0) {
                                    if (mTarget.isFinished) {
                                        finishByVerticalAnimation(mTarget.positionY, -screenHeight)
                                        return false
                                    } else {
                                        returnBack()
                                    }
                                }
                            }
                        }
                    }
                    return false
                }
            }
        }
        return false
    }

    private fun returnBack() {
        Log.i(TAG, "returnBack")
        val startValue = when (direction) {
            Direction.RIGHT -> mTarget.positionX
            Direction.LEFT -> mTarget.positionX
            Direction.UP -> mTarget.positionY
            Direction.DOWN -> mTarget.positionY
        }
        val anim = ValueAnimator.ofFloat(startValue, 0f)
        anim.duration = 200
        anim.addUpdateListener { animation ->
            if (direction == Direction.LEFT || direction == Direction.RIGHT) {
                rootView.translationX = (animation.animatedValue as Float)
            } else {
                rootView.translationY = (animation.animatedValue as Float)
            }
        }
        anim.start()
    }

    private fun finishByHorizontalAnimation(start: Float, end: Float) {
        Log.i(TAG, "finishByHorizontalAnimation=> start: ${start}, end: ${end}")
        val anim = ValueAnimator.ofFloat(start, end)
        anim.duration = 200
        anim.addUpdateListener { animation ->
            listener.onSwiped(
                Math.abs(Math.min(animation.animatedValue as Float / screenWidth, 1f))
                , animation.animatedValue as Float
            )
            rootView.translationX = (animation.animatedValue as Float)
        }
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                activity.finish()
            }
        })
        anim.start()
    }

    private fun finishByVerticalAnimation(start: Float, end: Float) {
        Log.i(TAG, "finishByVerticalAnimation=> start: ${start}, end: ${end}")
        val anim = ValueAnimator.ofFloat(start, end)
        anim.duration = 200
        anim.addUpdateListener { animation ->
            listener.onSwiped(
                Math.abs(Math.min(animation.animatedValue as Float / screenHeight, 1f))
                , animation.animatedValue as Float
            )
            rootView.translationY = (animation.animatedValue as Float)
        }
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                activity.finish()
            }
        })
        anim.start()
    }
}