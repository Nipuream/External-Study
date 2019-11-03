package com.example.workmodule;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.example.basemodule.service.HelloService;

@Route(path = "/work/main")
public class MainActivity extends AppCompatActivity {

    @Autowired(name = "/server/hello")
    HelloService helloService;

    private static final String TAG = "workMain";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.work_activity_main);
        ARouter.getInstance().inject(this);

        if(helloService != null){
            String result = helloService.sayHello("yanghui");
            Log.d(TAG,"result : "+result);
        }
    }
}
