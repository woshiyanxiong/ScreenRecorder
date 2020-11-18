package com.android.screenrecorder.helper;

import java.io.Serializable;

/**
 * @author: yanx
 * @time: 2020/11/16
 * @describe: com.demo.screenrecorder.model
 */
public class MediaBean implements Serializable {
    private String mediaName;
    private String path;
    private String length;
    private String videoTime;
    private String createData;

    public String getCreateData() {
        return createData;
    }

    public void setCreateData(String createData) {
        this.createData = createData;
    }

    public String getVideoTime() {
        return videoTime;
    }

    public void setVideoTime(String videoTime) {
        this.videoTime = videoTime;
    }

    public String getMediaName() {
        return mediaName;
    }

    public void setMediaName(String mediaName) {
        this.mediaName = mediaName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }
}
