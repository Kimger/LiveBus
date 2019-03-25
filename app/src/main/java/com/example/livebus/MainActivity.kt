package com.example.livebus

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import com.example.livebus.event.EventMsg
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_send.setOnClickListener { LiveBus.default.postSticky(EventMsg("要发送的消息")) }
        btn_observe.setOnClickListener { LiveBus.default.post("eventKey", "阿U而后覅偶安慰害怕") }
        btn_next.setOnClickListener { startActivity(Intent(this, TestActivity::class.java)) }
//        LiveBus.getDefault().subscribe<EventMsg>(EventMsg::class.java)
//            .observe(this, Observer { t -> Toast.makeText(this, t.msg, Toast.LENGTH_SHORT).show() })
//        btn_next.setOnClickListener { LiveBus.getDefault().post(EventMsg("33333333333333")) }

        LiveBus.default.subscribe(EventMsg::class.java)
            .observe(this, Observer { t -> Toast.makeText(this, t.msg, Toast.LENGTH_SHORT).show() })


        LiveBus.default.subscribe<String>("eventKey")
            .observe(this, Observer { t -> Toast.makeText(this, t, Toast.LENGTH_SHORT).show() })


        LiveBus.default.subscribe(DataBean::class.java)
            .observe(this, Observer { bean -> })

    }

}
