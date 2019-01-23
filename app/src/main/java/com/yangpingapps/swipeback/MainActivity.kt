package com.yangpingapps.swipeback

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_left.setOnClickListener {
            launchActivity("LEFT")
        }
        btn_right.setOnClickListener {
            launchActivity("RIGHT")
        }
        btn_down.setOnClickListener {
            launchActivity("DOWN")
        }
        btn_up.setOnClickListener {
            launchActivity("UP")
        }
    }

    private fun launchActivity(direction: String) {
        val bundle = Bundle()
        bundle.putString("DIRECTION", direction)
        bundle.putBoolean("ANIMTION", rb_on.isChecked)
        val intent = Intent(this, Main2Activity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
    }

}
