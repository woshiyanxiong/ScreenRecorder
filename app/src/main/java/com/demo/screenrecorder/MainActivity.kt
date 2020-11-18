package com.demo.screenrecorder

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.screenrecorder.helper.MediaBean
import com.android.screenrecorder.helper.ScreenFileUtils
import com.android.screenrecorder.helper.ScreenRecorderBuild
import com.android.screenrecorder.helper.ScreenStateListener
import com.demo.screenrecorder.model.MessageBus
import com.demo.screenrecorder.utils.SharedPreferencesUtils
import com.demo.screenrecorder.utils.Utils
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MainActivity : AppCompatActivity(), ScreenStateListener {
    private var screenRecorderBuild: ScreenRecorderBuild? = null


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        initScreenRecorderBuild()
        findViewById<RelativeLayout>(R.id.start_recorder).setOnClickListener {
            if (!Settings.canDrawOverlays(this@MainActivity)) {
                Toast.makeText(this@MainActivity, "当前无权限，请授权", Toast.LENGTH_SHORT)
                startActivityForResult(
                        Intent(
                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:$packageName")
                        ), 0
                )
            } else {
                startService(Intent(this@MainActivity, ScreenService::class.java))
//                moveTaskToBack(true)
            }
        }
        setting.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }

    }

    override fun onResume() {
        super.onResume()
        initVideo()
    }


    private fun initScreenRecorderBuild() {

        val rxPermissions = RxPermissions(this)
        val disposble = rxPermissions.request(
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.SYSTEM_ALERT_WINDOW,
                Manifest.permission.ACCESS_NOTIFICATION_POLICY
        ).subscribe {

            screenRecorderBuild = ScreenRecorderBuild.Builder()
                    .setActivity(this)
                    .setFps(Utils.getFps())
                    .setBitRate(Utils.getBitRate())
                    .setStateListener(this)
                    .build()
            Log.i("ddddd", "dddddd")
        }
//        disposble.dispose()

    }

    private var disposeOn: Disposable? = null
    private fun initVideo() {
        disposeOn = Observable.create(ObservableOnSubscribe<List<MediaBean>> { emitter ->
            emitter.onNext(ScreenFileUtils.getVideoFile(ScreenFileUtils.VIDEO_URL)!!)
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val recyclerView = findViewById<RecyclerView>(R.id.recycle_view)
                    recyclerView.layoutManager = GridLayoutManager(this, 2)
                    val adapter = ListAdapter(it)
                    recyclerView.adapter = adapter
                }, {

                })
    }

    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        data?.let {
            Log.e("MainActivity", "onActivityResult")
            screenRecorderBuild?.onActivityResult(requestCode, resultCode, data)
        }
        if (requestCode == 0) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show()
                startService(Intent(this@MainActivity, ScreenService::class.java))
//                moveTaskToBack(true)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onGetMessages(messageBus: MessageBus) {
        if (messageBus.msg == MessageBus.START) {
            screenRecorderBuild?.startRecord()
            if (!Utils.isForeground(this)) {
                startActivity(Intent(this, Activity1::class.java))
            }

        }
        if (messageBus.msg == MessageBus.STOP) {
            screenRecorderBuild?.stopRecord()
        }
        if (messageBus.msg == MessageBus.PAUSE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                screenRecorderBuild?.pause()
            }
        }
        if (messageBus.msg == MessageBus.CONTINUES) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                screenRecorderBuild?.resume()
            }
        }
        if (messageBus.msg == MessageBus.INIT) {
            screenRecorderBuild?.setBitRate(Utils.getBitRate())
            screenRecorderBuild?.setFps(Utils.getFps())
            screenRecorderBuild?.setAudioVoice(Utils.getMicrophone())
            Log.e("MainActivity", "init")
        }
    }


    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onDestroy() {
        super.onDestroy()
        screenRecorderBuild?.cancelAll()
        EventBus.getDefault().unregister(this)
        disposeOn?.dispose()
    }

    override fun stop(path: String?) {
        Log.e("录制结束....", "地址=${path}")
        Toast.makeText(this, "文件以保存到${path}", Toast.LENGTH_SHORT).show()
    }

    override fun pause() {
        Log.e("录制暂停....", "录制暂停....")
    }

    override fun recording() {
        Log.e("录制中....", "录制中....")
    }

    override fun error(msg: String?) {
        Log.e("录制出错....", msg)
    }
}

