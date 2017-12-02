package com.jwx.animationandcustomview.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.jwx.animationandcustomview.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView();
    }

    private fun initView() {
        findViewById<Button>(R.id.main_btn_vp_tl).setOnClickListener(View.OnClickListener {
            val intent = Intent()
            intent.setClass(this, TabSelectedAnimationActivity::class.java)
            startActivity(intent)
        })
        findViewById<Button>(R.id.main_btn_pull_back).setOnClickListener(View.OnClickListener {
            val intent = Intent()
            intent.setClass(this, PullBackActivity::class.java)
            startActivity(intent)
        })
    }
}
