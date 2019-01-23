package com.yangpingapps.library

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager


class SwipeListener(
    private val activity: SwipeBaseActivity,
    private val direction: SwipeBaseActivity.Companion.Direction
) :
    View.OnTouchListener {
    data class ActionTarget(
        var downX: Float = 0f, var positionX: Float = 0f
        , var downY: Float = 0f, var positionY: Float = 0f
        , var targetTime: Long = 0L, var isFinished: Boolean = false
    )

    companion object {
        data class Screen(val widthPixels: Int, val heightPixels: Int)

        fun getScreenSize(context: Context): Screen {
            val dm = DisplayMetrics()
            val windowManager = context
                .getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay.getMetrics(dm)
            return Screen(dm.widthPixels, dm.heightPixels)
        }
    }

    private val screenWidth: Float = getScreenSize(activity).widthPixels.toFloat()
    private val screenHeight: Float = getScreenSize(activity).heightPixels.toFloat()
    private val SWIPE_MIN_DISTANCE = when (direction) {
        SwipeBaseActivity.Companion.Direction.RIGHT, SwipeBaseActivity.Companion.Direction.LEFT -> screenWidth * 2 / 5
        SwipeBaseActivity.Companion.Direction.UP, SwipeBaseActivity.Companion.Direction.DOWN -> screenHeight * 2 / 5
    }
    private val mTarget = ActionTarget()

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (event != null) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    mTarget.targetTime = System.currentTimeMillis()
                    mTarget.downX = event.rawX
                    mTarget.downY = event.rawY
                    return true
                }
                MotionEvent.ACTION_MOVE -> {
                    mTarget.isFinished = when (direction) {
                        SwipeBaseActivity.Companion.Direction.RIGHT -> event.rawX - mTarget.downX > SWIPE_MIN_DISTANCE
                        SwipeBaseActivity.Companion.Direction.LEFT -> mTarget.downX - event.rawX > SWIPE_MIN_DISTANCE
                        SwipeBaseActivity.Companion.Direction.DOWN -> event.rawY - mTarget.downY > SWIPE_MIN_DISTANCE
                        SwipeBaseActivity.Companion.Direction.UP -> mTarget.downY - event.rawY > SWIPE_MIN_DISTANCE
                    }
                    mTarget.positionX = when (direction) {
                        SwipeBaseActivity.Companion.Direction.RIGHT -> if (event.rawX - mTarget.downX > 0) event.rawX - mTarget.downX else 0f
                        SwipeBaseActivity.Companion.Direction.LEFT -> if (mTarget.downX - event.rawX > 0) mTarget.downX - event.rawX else 0f
                        else -> 0f
                    }
                    mTarget.positionY = when (direction) {
                        SwipeBaseActivity.Companion.Direction.DOWN -> if (event.rawY - mTarget.downY > 0) event.rawY - mTarget.downY else 0f
                        SwipeBaseActivity.Companion.Direction.UP -> if (mTarget.downY - event.rawY > 0) mTarget.downY - event.rawY else 0f
                        else -> 0f
                    }
                    when (direction) {
                        SwipeBaseActivity.Companion.Direction.RIGHT -> {
                            activity.onSwiped(Math.min(mTarget.positionX / screenWidth, 1f), mTarget.positionX)
                            v?.translationX = mTarget.positionX
                        }
                        SwipeBaseActivity.Companion.Direction.LEFT -> {
                            activity.onSwiped(Math.min(mTarget.positionX / screenWidth, 1f), -mTarget.positionX)
                            v?.translationX = -mTarget.positionX
                        }
                        SwipeBaseActivity.Companion.Direction.DOWN -> {
                            activity.onSwiped(Math.min(mTarget.positionY / screenHeight, 1f), mTarget.positionY)
                            v?.translationY = mTarget.positionY
                        }
                        SwipeBaseActivity.Companion.Direction.UP -> {
                            activity.onSwiped(Math.min(mTarget.positionY / screenHeight, 1f), -mTarget.positionY)
                            v?.translationY = -mTarget.positionY
                        }
                    }
                    return true
                }
                MotionEvent.ACTION_UP -> {
                    if (System.currentTimeMillis() - mTarget.targetTime < 300) {
                        when (direction) {
                            SwipeBaseActivity.Companion.Direction.LEFT, SwipeBaseActivity.Companion.Direction.RIGHT -> {
                                if (mTarget.positionX > 200) {
                                    val anim = if (direction == SwipeBaseActivity.Companion.Direction.RIGHT) {
                                        ValueAnimator.ofFloat(mTarget.positionX, screenWidth)
                                    } else {
                                        ValueAnimator.ofFloat(-mTarget.positionX, -screenWidth)
                                    }
                                    anim.duration = 200
                                    anim.addUpdateListener { animation ->
                                        activity.onSwiped(
                                            Math.abs(
                                                Math.min(
                                                    animation.animatedValue as Float / screenWidth,
                                                    1f
                                                )
                                            )
                                            , animation.animatedValue as Float
                                        )
                                        v?.translationX = (animation.animatedValue as Float)
                                    }
                                    anim.addListener(object : AnimatorListenerAdapter() {
                                        override fun onAnimationEnd(animation: Animator?) {
                                            activity.finish()
                                        }
                                    })
                                    anim.start()
                                    return true
                                }
                            }
                            SwipeBaseActivity.Companion.Direction.DOWN, SwipeBaseActivity.Companion.Direction.UP -> {
                                if (mTarget.positionY > 200) {
                                    val anim = if (direction == SwipeBaseActivity.Companion.Direction.DOWN) {
                                        ValueAnimator.ofFloat(mTarget.positionY, screenHeight)
                                    } else {
                                        ValueAnimator.ofFloat(-mTarget.positionY, -screenHeight)
                                    }
                                    anim.duration = 200
                                    anim.addUpdateListener { animation ->
                                        activity.onSwiped(
                                            Math.abs(
                                                Math.min(
                                                    animation.animatedValue as Float / screenHeight,
                                                    1f
                                                )
                                            )
                                            , animation.animatedValue as Float
                                        )
                                        v?.translationY = (animation.animatedValue as Float)
                                    }
                                    anim.addListener(object : AnimatorListenerAdapter() {
                                        override fun onAnimationEnd(animation: Animator?) {
                                            activity.finish()
                                        }
                                    })
                                    anim.start()
                                    return true
                                }
                            }
                        }
                    }

                    if (mTarget.isFinished) {
                        when (direction) {
                            SwipeBaseActivity.Companion.Direction.RIGHT, SwipeBaseActivity.Companion.Direction.LEFT -> {
                                val anim = if (direction == SwipeBaseActivity.Companion.Direction.RIGHT) {
                                    ValueAnimator.ofFloat(mTarget.positionX, screenWidth)
                                } else {
                                    ValueAnimator.ofFloat(-mTarget.positionX, -screenWidth)
                                }
                                anim.duration = 200
                                anim.addUpdateListener { animation ->
                                    activity.onSwiped(
                                        Math.abs(
                                            Math.min(
                                                animation.animatedValue as Float / screenWidth,
                                                1f
                                            )
                                        )
                                        , animation.animatedValue as Float
                                    )
                                    v?.translationX = (animation.animatedValue as Float)
                                }
                                anim.addListener(object : AnimatorListenerAdapter() {
                                    override fun onAnimationEnd(animation: Animator?) {
                                        activity.finish()
                                    }
                                })
                                anim.start()
                            }
                            SwipeBaseActivity.Companion.Direction.UP, SwipeBaseActivity.Companion.Direction.DOWN -> {
                                val anim = if (direction == SwipeBaseActivity.Companion.Direction.DOWN) {
                                    ValueAnimator.ofFloat(mTarget.positionY, screenHeight)
                                } else {
                                    ValueAnimator.ofFloat(-mTarget.positionY, -screenHeight)
                                }
                                anim.duration = 200
                                anim.addUpdateListener { animation ->
                                    activity.onSwiped(
                                        Math.abs(
                                            Math.min(
                                                animation.animatedValue as Float / screenHeight,
                                                1f
                                            )
                                        )
                                        , animation.animatedValue as Float
                                    )
                                    v?.translationY = (animation.animatedValue as Float)
                                }
                                anim.addListener(object : AnimatorListenerAdapter() {
                                    override fun onAnimationEnd(animation: Animator?) {
                                        activity.finish()
                                    }
                                })
                                anim.start()
                            }
                        }

                    } else {
                        val startValue = when (direction) {
                            SwipeBaseActivity.Companion.Direction.RIGHT -> mTarget.positionX
                            SwipeBaseActivity.Companion.Direction.LEFT -> -mTarget.positionX
                            SwipeBaseActivity.Companion.Direction.UP -> -mTarget.positionY
                            SwipeBaseActivity.Companion.Direction.DOWN -> mTarget.positionY
                        }
                        val anim = ValueAnimator.ofFloat(startValue, 0f)
                        anim.duration = 200
                        anim.addUpdateListener { animation ->
                            if (direction == SwipeBaseActivity.Companion.Direction.LEFT || direction == SwipeBaseActivity.Companion.Direction.RIGHT) {
                                v?.translationX = (animation.animatedValue as Float)
                            } else {
                                v?.translationY = (animation.animatedValue as Float)
                            }
                        }
                        anim.start()
                    }
                    return true
                }
            }
        }
        return false
    }
}