package com.demo.screenrecorder

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.demo.screenrecorder.model.MessageBus
import com.demo.screenrecorder.utils.Utils
import org.greenrobot.eventbus.EventBus
import java.util.*

/**
 *@author: yanx
 *@time: 2020/11/13
 *@describe: com.demo.screenrecorder 悬浮窗
 */
class ScreenService : Service() {
    private var windowManager: WindowManager? = null
    private var layoutParams: WindowManager.LayoutParams? = null
    private var textView:TextView?=null
    private val displayMetrics by lazy { DisplayMetrics() }
    override fun onCreate() {
        super.onCreate()

        initWindows()
    }
    private var isStart=false

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    //初始化窗口
    private fun initWindows() {
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        layoutParams = WindowManager.LayoutParams()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams?.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            layoutParams?.type = WindowManager.LayoutParams.TYPE_PHONE
        }
        layoutParams?.format = PixelFormat.RGBA_8888
        layoutParams?.gravity = Gravity.LEFT or Gravity.TOP
        layoutParams?.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        layoutParams?.x = displayMetrics.widthPixels
        layoutParams?.y = displayMetrics.heightPixels-200
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        showFloatingWindow()
        return super.onStartCommand(intent, flags, startId)

    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("ClickableViewAccessibility")
    private fun showFloatingWindow() {
        if (Settings.canDrawOverlays(this)) {
            val view = LayoutInflater.from(this).inflate(R.layout.layout_windows, null)
            val imageView = view.findViewById<ImageView>(R.id.image)
            textView= view.findViewById(R.id.start)
            val closeImg = view.findViewById<ImageView>(R.id.close)
            var viewParams=view.layoutParams
            closeImg.setOnClickListener {
                windowManager?.removeView(view)
            }
            textView?.setOnClickListener {
                if (!isStart) {
                    EventBus.getDefault().post(MessageBus(MessageBus.START))
                    textView?.text = "00:00"
                    isStart=true
                    imageView.setImageResource(R.mipmap.ic_stop)
                    closeImg.visibility=View.GONE
                    startTimer()
                }else{
                    EventBus.getDefault().post(MessageBus(MessageBus.STOP))
                    textView?.text = "开始"
                    imageView.setImageResource(R.mipmap.ic_start)
                    isStart=false
                    closeImg.visibility=View.VISIBLE
                    stopTimer()
                    windowManager?.removeView(view)
                }
            }
            layoutParams?.width = ViewGroup.LayoutParams.WRAP_CONTENT
            layoutParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT
            windowManager!!.addView(view, layoutParams)
            Log.i("dddddd","true")

        }else{
            Log.i("dddddd","false")
        }
    }
    private var time = 0
    private var mTimer: Timer? = null
    private var mTimerTask: TimerTask? = null
    private val handler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == 1) {
                time++
                if (time >= 10 * 60) {
                    EventBus.getDefault().post(MessageBus(MessageBus.STOP))
                    return
                }
                textView?.text=secToTime(time)
                Log.i("jishiqi", "" + time)

            }
        }
    }

    private fun startTimer() {
        if (mTimer == null) {
            mTimer = Timer()
        }
        if (mTimerTask == null) {
            mTimerTask = object : TimerTask() {
                override fun run() {
                    handler.sendEmptyMessage(1)
                }
            }
        }
        if (mTimer != null && mTimerTask != null) mTimer?.schedule(mTimerTask, 0, 1000L)
    }

    private fun stopTimer() {
        handler.removeMessages(1)
        time=0
        if (mTimer != null) {
            mTimer?.cancel()
            mTimer = null
        }
        if (mTimerTask != null) {
            mTimerTask?.cancel()
            mTimerTask = null
        }
        textView?.setText("开始")

    }

    private fun startA(){
        val intent = Intent(this, Activity1::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    fun secToTime(time: Int): String? {
        val stringBuilder = StringBuilder()
        val hour = time / 3600
        val minute = time / 60 % 60
        val second = time % 60
        //        if(hour<10){
//            stringBuilder.append("0");
//
//        }
//        stringBuilder.append(hour);
        if (minute < 10) {
            stringBuilder.append("0")
        }
        stringBuilder.append(minute)
        stringBuilder.append(":")
        if (second < 10) {
            stringBuilder.append("0")
        }
        stringBuilder.append(second)
        return stringBuilder.toString()

    }

}