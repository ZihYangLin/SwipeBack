package com.yangpingapps.library

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout

open class SwipeBaseActivity : Activity(), SwipeListener {
    companion object {
        enum class Direction {
            RIGHT, LEFT, UP, DOWN
        }

        val DEFAULT_SWIPE_DIRECTION = Direction.RIGHT
    }

    private lateinit var viewShadow: View

    private var mSwipeDirection: Direction = DEFAULT_SWIPE_DIRECTION


    override fun setContentView(layoutResID: Int) {
        super.setContentView(getContentLayout(layoutResID))
        when (mSwipeDirection) {
            Direction.RIGHT -> overridePendingTransition(R.anim.slide_in_left, 0)
            Direction.LEFT -> overridePendingTransition(R.anim.slide_in_right, 0)
            Direction.UP -> overridePendingTransition(R.anim.slide_in_top, 0)
            Direction.DOWN -> overridePendingTransition(R.anim.slide_in_bottom, 0)
        }
    }

    private fun getContentLayout(layoutResID: Int): View {
        val view = LayoutInflater.from(this).inflate(layoutResID, null)
        view.setOnTouchListener(SwipeListener(this, mSwipeDirection))
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

    override fun onSwiped(persent: Float) {
        Log.i("Ocean#", "p:$persent")
        viewShadow.alpha = 1 - persent
    }

    protected open fun getShadowColor(): Int {
        return ContextCompat.getColor(this, R.color.shadow)
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, android.R.anim.fade_out)
    }

    class SwipeListener(private val activity: SwipeBaseActivity, private val direction: Direction) :
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
            Direction.RIGHT, Direction.LEFT -> screenWidth * 2 / 5
            Direction.UP, Direction.DOWN -> screenHeight * 2 / 5
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
                            Direction.RIGHT -> event.rawX - mTarget.downX > SWIPE_MIN_DISTANCE
                            Direction.LEFT -> mTarget.downX - event.rawX > SWIPE_MIN_DISTANCE
                            Direction.DOWN -> event.rawY - mTarget.downY > SWIPE_MIN_DISTANCE
                            Direction.UP -> mTarget.downY - event.rawY > SWIPE_MIN_DISTANCE
                        }
                        mTarget.positionX = when (direction) {
                            Direction.RIGHT -> if (event.rawX - mTarget.downX > 0) event.rawX - mTarget.downX else 0f
                            Direction.LEFT -> if (mTarget.downX - event.rawX > 0) mTarget.downX - event.rawX else 0f
                            else -> 0f
                        }
                        mTarget.positionY = when (direction) {
                            Direction.DOWN -> if (event.rawY - mTarget.downY > 0) event.rawY - mTarget.downY else 0f
                            Direction.UP -> if (mTarget.downY - event.rawY > 0) mTarget.downY - event.rawY else 0f
                            else -> 0f
                        }
                        Log.i(
                            "##OCEAN##",
                            "Finished:${mTarget.isFinished}, x:${mTarget.positionX}, y:${mTarget.positionY}"
                        )
                        when (direction) {
                            Direction.RIGHT -> {
                                activity.onSwiped(Math.min(mTarget.positionX / screenWidth, 1f))
                                v?.translationX = mTarget.positionX
                            }
                            Direction.LEFT -> {
                                activity.onSwiped(Math.min(mTarget.positionX / screenWidth, 1f))
                                v?.translationX = -mTarget.positionX
                            }
                            Direction.DOWN -> {
                                activity.onSwiped(Math.min(mTarget.positionY / screenHeight, 1f))
                                v?.translationY = mTarget.positionY
                            }
                            Direction.UP -> {
                                activity.onSwiped(Math.min(mTarget.positionY / screenHeight, 1f))
                                v?.translationY = -mTarget.positionY
                            }
                        }
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        if (System.currentTimeMillis() - mTarget.targetTime < 300) {
                            when (direction) {
                                Direction.LEFT, Direction.RIGHT -> {
                                    if (mTarget.positionX > 200) {
                                        val anim = if (direction == Direction.RIGHT) {
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
                                Direction.DOWN, Direction.UP -> {
                                    if (mTarget.positionY > 200) {
                                        val anim = if (direction == Direction.DOWN) {
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
                                Direction.RIGHT, Direction.LEFT -> {
                                    val anim = if (direction == Direction.RIGHT) {
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
                                Direction.UP, Direction.DOWN -> {
                                    val anim = if (direction == Direction.DOWN) {
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
                                Direction.RIGHT -> mTarget.positionX
                                Direction.LEFT -> -mTarget.positionX
                                Direction.UP -> -mTarget.positionY
                                Direction.DOWN -> mTarget.positionY
                            }
                            val anim = ValueAnimator.ofFloat(startValue, 0f)
                            anim.duration = 200
                            anim.addUpdateListener { animation ->
                                if (direction == Direction.LEFT || direction == Direction.RIGHT) {
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
}