package com.demo.screenrecorder.model;

/**
 * @author: yanx
 * @time: 2020/10/29
 * @describe: com.nanchen.screenrecorder
 */
public class MessageBus {
    public static String START="start";
    public static String STOP="stop";
    public static String PAUSE="pause";
    public static String CONTINUES="continues";
    public static String INIT="init";
    public static String BACK="back";
    private String msg;
    private ActivityBean bean;

    public MessageBus(String msg, ActivityBean bean) {
        this.msg = msg;
        this.bean = bean;
    }

    public ActivityBean getBean() {
        return bean;
    }

    public void setBean(ActivityBean bean) {
        this.bean = bean;
    }

    public MessageBus(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
