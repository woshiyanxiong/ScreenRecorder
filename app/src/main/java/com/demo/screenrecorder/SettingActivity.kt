package com.demo.screenrecorder

import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.demo.screenrecorder.model.MessageBus
import com.demo.screenrecorder.utils.SharedPreferencesUtils
import com.demo.screenrecorder.utils.Utils
import kotlinx.android.synthetic.main.activity_setting.*
import org.greenrobot.eventbus.EventBus

/**
 *@author: yanx
 *@time: 2020/11/17
 *@describe: com.demo.screenrecorder
 */
class SettingActivity : AppCompatActivity() {
    private var isSetting = false //是否设置相关参数
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        black.setOnClickListener { finish() }
        fenbian_layout.setOnClickListener {
//            showFps()
        }
        fps_layout.setOnClickListener {
            showFps()
        }
        hz_layout.setOnClickListener {
            showBitRate()
        }
        sy_layout.setOnClickListener {
            setMicrophone()
        }
        fpsText.text = SharedPreferencesUtils.getInstance().getString("fps", "24fps")
        mbpText.text = SharedPreferencesUtils.getInstance().getString("mbps", "5Mbps")
        micText.text = SharedPreferencesUtils.getInstance().getString("microphone", "麦克风")
    }

    private fun showFps() {
        val list = arrayOf("15fps", "24fps", "30fps")
        AlertDialog.Builder(this).setTitle("帧数")
            .setItems(list) { dialog, position ->
                dialog.dismiss()
                SharedPreferencesUtils.getInstance().putString("fps", list[position])
                fpsText.text = list[position]
                isSetting = true
            }
            .setNegativeButton("确定", null)
            .create().show()
    }


    private fun showBitRate() {
        val list = arrayOf("1Mbps", "2Mbps", "5Mbps", "8Mbps", "10Mbps")
        AlertDialog.Builder(this).setTitle("视频画质")
            .setItems(list) { dialog, position ->
                dialog.dismiss()
                mbpText.text = list[position]
                SharedPreferencesUtils.getInstance().putString("mbps", list[position])
                isSetting = true
            }
            .setNegativeButton("确定", null)
            .create().show()
    }


    private fun setMicrophone() {
        val list = arrayOf("麦克风", "无声")
        AlertDialog.Builder(this).setTitle("分辨率")
            .setItems(list) { dialog, position ->
                dialog.dismiss()
                fbText.text = list[position]
                SharedPreferencesUtils.getInstance().putString("microphone", list[position])
                isSetting = true
            }
            .setNegativeButton("确定", null)
            .create().show()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isSetting) {
            EventBus.getDefault().post(MessageBus(MessageBus.INIT))
        }
    }


}