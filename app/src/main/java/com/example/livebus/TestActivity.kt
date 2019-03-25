package com.example.livebus

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.livebus.event.EventMsg

/**
 * @author Kimger
 * @email kimgerxue@gmail.com
 * @date 2019/3/25 10:42
 * @description
 */
class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        LiveBus.default.subscribeSticky<EventMsg>(this, "test")
            .observe(this, Observer { t -> Toast.makeText(this, t.msg, Toast.LENGTH_SHORT).show() })
    }
}