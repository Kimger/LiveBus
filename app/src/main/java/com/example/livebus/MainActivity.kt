package com.example.livebus

import LiveBus
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.livebus.event.EventMsg
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var int = 0
        btn_send.setOnClickListener { LiveBus.default.postSticky("test", EventMsg("测试消息发送成功" + int++)) }
        btn_observe.setOnClickListener {
            LiveBus.default.subscribe<EventMsg>(this, "test")
                .observe(this, Observer { t -> Toast.makeText(this, t.msg, Toast.LENGTH_SHORT).show() })
        }
        btn_next.setOnClickListener { startActivity(Intent(this, TestActivity::class.java)) }
    }
}
