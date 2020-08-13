package com.nipuream.plugintest;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import java.lang.reflect.Method;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Resources pluginResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void loadPlugin(View view){

        Log.i(TAG,"loadPlugin -> apk/dex.");
        try {
            PluginHelper.loadPluginClass(this, this.getClassLoader());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.i(TAG,"loadPlugin -> load resource.");
        try {
            pluginResource = PluginHelper.initPluginResource(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void test(View view){

        try {
            Class<?> cls = Class.forName("com.example.test.Sort");
            Object object = cls.newInstance();

            int[] array = {23, 67, 98, 57, 14, 23};
            Method method = cls.getMethod("popPopule",array.getClass());
            method.invoke(object, array);

            Log.i(TAG, "array :  " + Arrays.toString(array));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(pluginResource != null){
            int id = pluginResource.getIdentifier("test","color","com.example.test");
            int colorId = pluginResource.getColor(id);
            view.setBackgroundColor(colorId);
        }
    }

    public void startActivity(View view){


        Class pluginActivityClass = null;

        try {
            pluginActivityClass = Class.forName("com.example.test.MainActivity");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if(pluginActivityClass == null){
            Toast.makeText(this, "找不到PluginActivity", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, pluginActivityClass);
        startActivity(intent);
    }



}
