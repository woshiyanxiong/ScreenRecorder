package com.demo.screenrecorder

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cn.jzvd.Jzvd
import com.android.screenrecorder.helper.MediaBean
import kotlinx.android.synthetic.main.activity_play_video.*

/**
 *@author: yanx
 *@time: 2020/11/17
 *@describe: com.demo.screenrecorder
 */
class PlayVideoActivity:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_video)
        val bean=this.intent.getSerializableExtra("data") as MediaBean
        jz_video.setUp(bean.path
            , bean.mediaName)
        black.setOnClickListener { finish() }
    }

    override fun onPause() {
        super.onPause()
        Jzvd.releaseAllVideos();
    }

}