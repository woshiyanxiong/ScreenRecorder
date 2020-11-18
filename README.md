## [apk体验](https://githubapk.oss-cn-beijing.aliyuncs.com/app-debug.apk)
## Android录屏基于5.0之上
### 1.如何使用
  直接复制recording库下面的代码，或者引入recording库。
  然后在所承载的录制activity中进行设置
  ```kotlin

    /**
     * 开始录制 注意之前要申请权限
     *Manifest.permission.MODIFY_AUDIO_SETTINGS
     *Manifest.permission.MICROPHONE
     *Manifest.permission.STORAGE
     *Manifest.permission.RECORD_AUDIO
     */
ScreenRecorderBuild.Builder()
                .setActivity(this) 
                .setFps(Utils.getFps())//设置fps 默认24fps
                .setBitRate(Utils.getBitRate())//设置视频编码比特率1Mbps、5Mbps。//默认5Mbps
                .setStateListener(this) //设置录制监听 
                .setWidth(1080)//宽 默认系统
                .setHeight(1980)//高 默认系统
                .setIsAudioVoice(true)//true有声 false无声
                .build()
```
  在onActivityResult中
  
```kotlin
    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        data?.let {
            screenRecorderBuild?.onActivityResult(requestCode, resultCode, data)
        }
    }
```
然后调用以下方法进行录制
```kotlin
startRecord() //开始录制
stopRecord()//停止录制
resume()//继续录制
pause()//暂停录制
```
如要监听录制状态可实现`ScreenStateListener`更多状态可以自行添加
```kotlin
public interface ScreenStateListener {
    /**
     * 录制中
     */
    void recording();

    /**
     * 暂停中
     */
    void pause();

    /**
     * 录制结束
     * @param path 文件地址
     */
    void stop(String path);

    /**
     * 异常
     * @param msg 异常提示
     */
    void error(String msg);

}
```
在`onDestroy()`中 释放
```kotlin
screenRecorderBuild?.cancelAll()
```
### 代码基于[ScreenRecordHelper](https://github.com/nanchen2251/ScreenRecordHelper)封装
### [更多介绍](https://blog.csdn.net/qq_30710615/article/details/109766970)



