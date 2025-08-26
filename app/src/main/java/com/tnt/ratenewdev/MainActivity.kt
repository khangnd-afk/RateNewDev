package com.tnt.ratenewdev

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        RateUtils.init(this)
        findViewById<TextView>(R.id.tvTest).setOnClickListener {
            RateUtils.showRate(this, false)
        }
        findViewById<TextView>(R.id.tvReset).setOnClickListener {
            RateUtils.reset(this)
        }
    }
}