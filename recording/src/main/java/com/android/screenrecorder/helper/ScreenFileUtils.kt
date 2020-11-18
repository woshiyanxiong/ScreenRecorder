package com.android.screenrecorder.helper

import android.media.MediaMetadataRetriever
import android.os.Environment
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author: yanx
 * @time: 2020/11/13
 * @describe: com.nanchen.screenrecorder.utils
 */
object ScreenFileUtils {
    /**
     * 文件保存的地址
     */
    val VIDEO_URL = (Environment.getExternalStorageDirectory()
        .absolutePath + File.separator
            + "Screen" + File.separator + "Camera")

    /**
     * 获取视频文件
     *
     * @return
     */
    fun getVideoFile(filepath: String?): List<MediaBean>? {
        val file = File(filepath)
        if (!file.exists()) {
            return null
        }
        val list: MutableList<MediaBean> = ArrayList()
        file.listFiles { file1: File ->
            var name = file1.name
            val i = name.indexOf('.')
            if (i != -1) {
                name = name.substring(i)
                if (name.equals(".mp4", ignoreCase = true) || name.equals(
                        ".3gp",
                        ignoreCase = true
                    ) || name.equals(".wmv", ignoreCase = true)
                    || name.equals(".ts", ignoreCase = true) || name.equals(
                        ".rmvb",
                        ignoreCase = true
                    )
                    || name.equals(".mov", ignoreCase = true) || name.equals(
                        ".m4v",
                        ignoreCase = true
                    )
                    || name.equals(".avi", ignoreCase = true) || name.equals(
                        ".m3u8",
                        ignoreCase = true
                    )
                    || name.equals(".3gpp", ignoreCase = true) || name.equals(
                        ".3gpp2",
                        ignoreCase = true
                    )
                    || name.equals(".mkv", ignoreCase = true) || name.equals(
                        ".flv",
                        ignoreCase = true
                    )
                    || name.equals(".divx", ignoreCase = true) || name.equals(
                        ".f4v",
                        ignoreCase = true
                    )
                    || name.equals(".rm", ignoreCase = true) || name.equals(
                        ".asf",
                        ignoreCase = true
                    )
                    || name.equals(".ram", ignoreCase = true) || name.equals(
                        ".mpg",
                        ignoreCase = true
                    )
                    || name.equals(".v8", ignoreCase = true) || name.equals(
                        ".swf",
                        ignoreCase = true
                    )
                    || name.equals(".m2v", ignoreCase = true) || name.equals(
                        ".asx",
                        ignoreCase = true
                    )
                    || name.equals(".ra", ignoreCase = true) || name.equals(
                        ".ndivx",
                        ignoreCase = true
                    )
                    || name.equals(".xvid", ignoreCase = true)
                ) {
                    val video = MediaBean()
                    file1.usableSpace
                    video.mediaName = file1.name
                    video.path = file1.absolutePath
                    video.length = byteToMB(file1.length())
                    video.videoTime = getLocalVideoDuration(file1.absolutePath)
                    video.createData=getFileLastModifiedTime(file1)
                    list.add(video)
                    return@listFiles true
                }
                // 判断是不是目录
            } else if (file1.isDirectory) {
                getVideoFile(filepath)
            }
            false
        }
        return list
    }

    /**
     * get Local video duration
     *
     * @return
     */
    fun getLocalVideoDuration(videoPath: String?): String {
//除以 1000 返回是秒
        val duration: Int
        try {
            val mmr = MediaMetadataRetriever()
            mmr.setDataSource(videoPath)
            duration =
                mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)!!.toInt() / 1000

//宽
            val width =
                mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
            //高
            val height =
                mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
        } catch (e: Exception) {
            e.printStackTrace()
            return "0"
        }
        return secToTime(duration).toString()
    }

    //将字节数转化为MB
    private fun byteToMB(size: Long): String {
        val kb: Long = 1024
        val mb = kb * 1024
        val gb = mb * 1024
        return if (size >= gb) {
            String.format("%.1f GB", size.toFloat() / gb)
        } else if (size >= mb) {
            val f = size.toFloat() / mb
            String.format(if (f > 100) "%.0f MB" else "%.1f MB", f)
        } else if (size > kb) {
            val f = size.toFloat() / kb
            String.format(if (f > 100) "%.0f KB" else "%.1f KB", f)
        } else {
            String.format("%d B", size)
        }
    }

    private fun secToTime(time: Int): String? {
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


    private fun getFileLastModifiedTime(file: File): String? {
         val mformatType = "yyyy/MM/dd HH:mm:ss"
        val cal = Calendar.getInstance()
        val time = file.lastModified()
        val formatter = SimpleDateFormat(mformatType)
        cal.timeInMillis = time

        // 输出：修改时间[2] 2009-08-17 10:32:38
        return formatter.format(cal.time)
    }

}