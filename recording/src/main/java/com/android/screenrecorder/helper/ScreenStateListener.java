package com.android.screenrecorder.helper;

/**
 * @author: yanx
 * @time: 2020/11/17
 * @describe: com.android.screenrecorder.helper
 */
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
