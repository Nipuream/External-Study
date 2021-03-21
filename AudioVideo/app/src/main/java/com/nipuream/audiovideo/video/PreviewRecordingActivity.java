package com.nipuream.audiovideo.video;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import com.nipuream.audiovideo.R;


/**
 * 将摄像头数据封装成 h.264 格式码流
 * 使用native代码 显存纹理拷贝 编码生成.
 */
public class PreviewRecordingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_recording);
    }

    //开始编码
    public void startEncode(View view) {
    }

    //停止编码
    public void stopEncode(View view) {
    }

    //切换摄像头
    public void switchFace(View view) {
    }

}