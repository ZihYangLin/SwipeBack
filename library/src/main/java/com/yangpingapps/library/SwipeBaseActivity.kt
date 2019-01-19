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

    private lateinit var viewShadow: View

    override fun setContentView(layoutResID: Int) {
        super.setContentView(getContentLayout(layoutResID))
        overridePendingTransition(R.anim.slide_in_left, 0)
    }

    private fun getContentLayout(layoutResID: Int): View {
        val view = LayoutInflater.from(this).inflate(layoutResID, null)
        view.setOnTouchListener(SwipeListener(this))
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

    class SwipeListener(val activity: SwipeBaseActivity) : View.OnTouchListener {
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
        private val SWIPE_MIN_DISTANCE = screenWidth * 2 / 5
        private var isFinished = false
        private var downRawX = 0f
        private var positionX = 0f
        private var timeTouch = 0L

        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            if (event != null) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        timeTouch = System.currentTimeMillis()
                        downRawX = event.rawX
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        isFinished = event.rawX - downRawX > SWIPE_MIN_DISTANCE
                        positionX = if (event.rawX - downRawX > 0) event.rawX - downRawX else 0f
                        activity.onSwiped(Math.min(positionX / screenWidth, 1f))
                        v?.translationX = positionX
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        if (System.currentTimeMillis() - timeTouch < 300) {
                            if (positionX > 200) {
                                val anim = ValueAnimator.ofFloat(positionX, screenWidth)
                                anim.duration = 200
                                anim.addUpdateListener { animation ->
                                    activity.onSwiped((Math.min(animation.animatedValue as Float / screenWidth, 1f)))
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

                        if (isFinished) {
                            val anim = ValueAnimator.ofFloat(positionX, screenWidth)
                            anim.duration = 200
                            anim.addUpdateListener { animation ->
                                activity.onSwiped((Math.min(animation.animatedValue as Float / screenWidth, 1f)))
                                v?.translationX = (animation.animatedValue as Float)
                            }
                            anim.addListener(object : AnimatorListenerAdapter() {
                                override fun onAnimationEnd(animation: Animator?) {
                                    activity.finish()
                                }
                            })
                            anim.start()
                        } else {
                            val anim = ValueAnimator.ofFloat(positionX, 0f)
                            anim.duration = 200
                            anim.addUpdateListener { animation ->
                                v?.translationX = (animation.animatedValue as Float)
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