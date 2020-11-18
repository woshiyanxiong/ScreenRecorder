package com.demo.screenrecorder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.demo.screenrecorder.model.ActivityBean;
import com.demo.screenrecorder.model.MessageBus;

import org.greenrobot.eventbus.EventBus;

/**
 * @author: yanx
 * @time: 2020/11/17
 * @describe: 1像素activity 用于应用处于后台时开始录制
 */
@Deprecated()
public class Activity1 extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setGravity(Gravity.LEFT | Gravity.TOP);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.x = 0;
        layoutParams.y = 0;
        layoutParams.width = 1;
        layoutParams.height = 1;
        layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        window.setAttributes(layoutParams);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("Activity1", "onActivityResult");
        EventBus.getDefault().post(new MessageBus(MessageBus.BACK, new ActivityBean(requestCode, resultCode, data)));
        finish();
    }
}
