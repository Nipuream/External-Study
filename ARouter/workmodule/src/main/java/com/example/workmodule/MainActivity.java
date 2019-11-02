package com.example.workmodule;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.example.basemodule.service.HelloService;
import com.example.myapplication.R;

@Route(path = "/work/main")
public class MainActivity extends AppCompatActivity {

    @Autowired(name = "hello")
    HelloService helloService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ARouter.getInstance().inject(this);

        if(helloService != null){
            helloService.sayHello("yanghui");
        }
    }
}
