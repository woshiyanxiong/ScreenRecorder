package com.android.screenrecorder.helper

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.io.File
import java.lang.System.currentTimeMillis
import kotlin.math.min

/**
 *@author: yanx
 *@time: 2020/11/13
 *@describe: com.android.screenrecorder.helper
 */

class ScreenRecorderBuild private constructor(builder: Builder) {
    private var TAG = "ScreenRecorderBuild"
    private var activity: Activity? = null
    private val mediaProjectionManager by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity?.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as? MediaProjectionManager
        } else {
            TODO("VERSION.SDK_INT < LOLLIPOP")
        }
    }
    private var mediaRecorder: MediaRecorder? = null
    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private val displayMetrics by lazy { DisplayMetrics() }
    private var savePath = ScreenFileUtils.VIDEO_URL //地址保存
    private var saveName = "${currentTimeMillis()}"
    private var saveFile: File? = null
    private var bitRate = 0
    private var fps = 0
    private var isRecording = false
    private var listener: ScreenStateListener? = null
    private var width = 0
    private var height = 0
    private var isAudioVoice = true //是否是无声还是麦克风

    /**
     * 开始录制 注意之前要申请权限
     *Manifest.permission.MODIFY_AUDIO_SETTINGS
     *Manifest.permission.MICROPHONE
     *Manifest.permission.STORAGE
     *Manifest.permission.RECORD_AUDIO
     */
    @SuppressLint("NewApi")
    fun startRecord() {
        Log.d(TAG, "startRecord")
        if (mediaProjectionManager == null) {
            Log.d(TAG, "mediaProjectionManager == null，当前手机暂不支持录屏")
            showToast("当前手机暂不支持录屏")
            return
        }
        if (isRecording){
            Log.e(TAG, "已在录制中")
            return
        }
        mediaProjectionManager?.apply {
            val intent = this.createScreenCaptureIntent()
            if (activity?.packageManager?.resolveActivity(
                    intent,
                    PackageManager.MATCH_DEFAULT_ONLY
                ) != null
            ) {
                Log.e(TAG,"startActivityForResult")
                activity?.startActivityForResult(intent, REQUEST_CODE)
            } else {
                showToast("当前手机暂不支持录屏")
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                mediaProjection = mediaProjectionManager!!.getMediaProjection(resultCode, data)
                // 实测，部分手机上录制视频的时候会有弹窗的出现
                Handler().postDelayed({
//                    activity?.moveTaskToBack(true)
                    setMediaRecorder()
                }, 150)
            } else {
                showToast("当前手机暂不支持录屏")
            }
        }
    }

    /**
     * 设置MediaRecorder
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setMediaRecorder() {
        val f = File(savePath)
        if (!f.exists()) {
            f.mkdirs()
        }
        saveFile = File(savePath, "$saveName.tmp")
        saveFile?.apply {
            if (exists()) {
                delete()
            }
        }
        mediaRecorder = MediaRecorder()
        width = if (width == 0) displayMetrics.widthPixels.coerceAtMost(1080) else width
        height = if (height == 0) min(displayMetrics.heightPixels, 1920) else height
        //MediaRecorder 的视频输出尺寸是否是系统所支持的尺寸,反正不论设置多大 这里最大也就1080*1920
        //录制的屏幕大小最好不要超过1080*1920 即超出系统最大尺寸 否则出现 stop failed: -1007
        Log.i("当前手机屏幕高宽", "${width},${height}")
        mediaRecorder?.apply {
            if (isAudioVoice) {
                //设置音频源
                setAudioSource(MediaRecorder.AudioSource.MIC)
            }
            //设置视频的采集方式
            setVideoSource(MediaRecorder.VideoSource.SURFACE)
            //设置文件的输出格式
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            //设置video的编码格式
            setVideoEncoder(MediaRecorder.VideoEncoder.H264)
            if (isAudioVoice) {
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            }
            //设置输出文件的路径
            setOutputFile(saveFile!!.absolutePath)
            //设置要捕获的视频的宽度和高度
            setVideoSize(width, height)
            //设置录制的视频编码比特率
            setVideoEncodingBitRate(bitRate)
            //设置要捕获的视频帧速率 这个设置貌似无效
            setVideoFrameRate(fps) //这里可以对外选择设置15 30 60
            try {
                prepare()
                //name: 是生成的VirtualDisplay实例的名称；
                //width, height: 分别是生成实例的宽高，必须大于0；
                //dpi: 生成实例的像素密度，必须大于0，一般都取1；
                //surface: 这个比较重要，是你生成的VirtualDisplay的载体，
                //         我的理解是，VirtualDisplay的内容是一帧帧的屏幕截图（所以你看到是有宽高，像素密度等设置），
                //         所以MediaProjection获取到的其实是一帧帧的图，然后通过          surface（surface你可以理解成是android的一个画布，
                //         默认它会以每秒60帧来刷新，这里我们不再展开细说），来顺序播放这些图片，形成视频。
                virtualDisplay = mediaProjection?.createVirtualDisplay(
                    "ScreenRecorderBuild", width, height, displayMetrics.densityDpi,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, surface, null, null
                )
                mediaRecorder?.start()
                isRecording = true
                listener?.recording()
                Log.e("当前录制参数","fps=${fps},bit=${bitRate}")
            } catch (e: Exception) {
                Log.e(TAG, "IllegalStateException preparing MediaRecorder: ${e.message}")
                e.printStackTrace()
                listener?.error(e.printStackTrace().toString())
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun resume() {
        mediaRecorder?.resume()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun pause() {
        mediaRecorder?.pause()
        listener?.pause()
    }

    /**
     * 停止录制
     */
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun stopRecord() {
        if (isRecording) {
            isRecording = false
            mediaRecorder?.apply {
                setOnErrorListener(null)
                setOnInfoListener(null)
                setPreviewDisplay(null)
            }
            try {
                mediaRecorder?.stop()
                if (saveFile != null) {
                    val newFile = File(savePath, "$saveName.mp4")
                    saveFile!!.renameTo(newFile)
                    listener?.stop(newFile.absolutePath)
                }
                saveFile = null
            } catch (e: Exception) {
                Log.e(TAG, "stopErr${e.message}")
                listener?.error("stopErr${e.message}")
            } finally {
                mediaRecorder?.reset()
                virtualDisplay?.release()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mediaProjection?.stop()
                }
            }
        }
    }

    /**
     * 在onDestroy释放
     */
    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun cancelAll() {
        mediaRecorder?.release()
        mediaRecorder = null
        virtualDisplay?.release()
        virtualDisplay = null
        mediaProjection?.stop()
        mediaProjection = null
    }

    fun setFps(fps: Int) {
        this.fps = fps
    }

    fun setBitRate(bitRate: Int) {
        this.bitRate = bitRate
    }

    fun setWidth(width: Int) {
        this.width = width
    }

    fun setHeight(height: Int) {
        this.height = height
    }

    fun setAudioVoice(isAudioVoice: Boolean) {
        this.isAudioVoice = isAudioVoice
    }


    private fun showToast(msg: String) = Toast.makeText(activity!!, msg, Toast.LENGTH_SHORT).show()

    companion object {
        const val REQUEST_CODE = 100

    }

    class Builder {
        var activity: Activity? = null
        var fps: Int = 30//默认为30fps
        var bitRate: Int = 5 * 1024 * 1024//默认为5Mb/s
        var listener: ScreenStateListener? = null
        var savePath = ScreenFileUtils.VIDEO_URL //默认存放文件
        var width = 0
        var height = 0
        var isAudioVoice = true

        fun setActivity(activity: Activity): Builder {
            this.activity = activity
            return this
        }

        fun setFps(fps: Int): Builder {
            this.fps = fps
            return this
        }

        fun setBitRate(bitRate: Int): Builder {
            this.bitRate = bitRate
            return this
        }

        fun setStateListener(listener: ScreenStateListener): Builder {
            this.listener = listener
            return this
        }

        fun setSavePath(savePath: String): Builder {
            this.savePath = savePath
            return this
        }

        fun setWidth(width: Int): Builder {
            this.width = width
            return this
        }

        fun setHeight(height: Int): Builder {
            this.height = height
            return this
        }

        fun setIsAudioVoice(isAudioVoice: Boolean): Builder {
            this.isAudioVoice = isAudioVoice
            return this
        }

        fun build() = ScreenRecorderBuild(this)
    }

    init {
        activity = builder.activity
        fps = builder.fps
        bitRate = builder.bitRate
        activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        listener = builder.listener
    }


}
