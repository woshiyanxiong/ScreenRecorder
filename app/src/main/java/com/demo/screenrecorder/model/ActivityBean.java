package com.demo.screenrecorder.model;

import android.content.Intent;

/**
 * @author: yanx
 * @time: 2020/11/17
 * @describe: com.demo.screenrecorder.model
 */
public class ActivityBean {
    private int requestCode;
    private int resultCode;
    private Intent data;

    public ActivityBean(int requestCode, int resultCode, Intent data) {
        this.requestCode = requestCode;
        this.resultCode = resultCode;
        this.data = data;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public Intent getData() {
        return data;
    }

    public void setData(Intent data) {
        this.data = data;
    }
}
