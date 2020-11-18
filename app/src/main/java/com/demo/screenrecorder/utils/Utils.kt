package com.demo.screenrecorder.utils

import android.app.Activity
import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.text.TextUtils

/**
 *@author: yanx
 *@time: 2020/11/17
 *@describe: com.demo.screenrecorder.utils
 */
object Utils {
    /**
     * 设置mbps
     * @return Int
     */
    fun getBitRate():Int{
        val bit=SharedPreferencesUtils.getInstance().getString("mbps")
        if (bit=="1Mbps"){
            return 1*1024*1024
        }
        if (bit=="2Mbps"){
            return 2*1024*1024
        }
        if (bit=="5Mbps"){
            return 5*1024*1024
        }
        if (bit=="8Mbps"){
            return 8*1024*1024
        }
        if (bit=="10Mbps"){
            return 10*1024*1024
        }

        return 5*1024*1024
    }

    /**
     * 获取fps设置
     * @return Int
     */
    fun getFps():Int{
        val fps=SharedPreferencesUtils.getInstance().getString("fps")
        if (fps=="24fps"){
            return 24
        }
        if (fps=="15fps"){
            return 15
        }
        if (fps=="30fps"){
            return 30
        }
        if (fps=="60fps"){
            return 60
        }
        return 24
    }

    /**
     * 获取声音状态
     * @return Boolean true 是麦克风 false 是无声
     */
    fun getMicrophone():Boolean{
        val m=SharedPreferencesUtils.getInstance().getString("microphone")
        return m!="无声"
    }

    /**
     * 判断某个activity是否在前台显示
     */
    fun isForeground(activity: Activity): Boolean {
        return isForeground(activity, activity.javaClass.name)
    }

    /**
     * 判断某个界面是否在前台,返回true，为显示,否则不是
     */
    fun isForeground(context: Activity?, className: String): Boolean {
        if (context == null || TextUtils.isEmpty(className)) return false
        val am: ActivityManager =
            context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val list: List<ActivityManager.RunningTaskInfo> = am.getRunningTasks(1)
        if (list != null && list.size > 0) {
            val cpn:
                    ComponentName = list[0].topActivity
            if (className == cpn.getClassName()) return true
        }
        return false
    }
}