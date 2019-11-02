package com.example.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.example.myapplication.info.Test;

@Route(path = "/app/test")
public class TestActivity extends AppCompatActivity {

    @Autowired(name = "key1")
    long length;

    @Autowired(name = "key2")
    String value;

    @Autowired(name = "key3")
    Test test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        ARouter.getInstance().inject(this);

        Log.i(getClass().getName(),"key1 = "+length + ", key2 = "+value + ", key3 = "+test);
    }

}
