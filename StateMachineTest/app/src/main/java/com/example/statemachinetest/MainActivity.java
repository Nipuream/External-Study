package com.example.statemachinetest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;

import com.example.statemachinetest.impl.Hsm1;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Hsm1 hsm1 = Hsm1.makeHsm1();
        hsm1.sendMessage(hsm1.obtainMessage(Hsm1.CMD_1));
        hsm1.sendMessage(hsm1.obtainMessage(Hsm1.CMD_2));
    }
}
