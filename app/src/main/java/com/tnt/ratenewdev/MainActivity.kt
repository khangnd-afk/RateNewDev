package com.tnt.ratenewdev

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.tnt.rate.core.RateManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        RateUtils.init(application)
        findViewById<TextView>(R.id.tvTest).setOnClickListener {
            RateUtils.showRate(this, false)
        }
        findViewById<TextView>(R.id.tvReset).setOnClickListener {
            RateManager.dismissNext()
        }
    }
}