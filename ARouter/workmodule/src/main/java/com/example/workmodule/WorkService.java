package com.example.workmodule;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.launcher.ARouter;
import com.example.basemodule.service.HelloService;


public class WorkService extends Service {

    @Autowired(name = "/server/hello")
    HelloService helloService;

    public WorkService() {

        ARouter.getInstance().inject(this);

        if(helloService != null){
            String result = helloService.sayHello("yanghui");
            Log.d(getClass().getName(),"result : "+result);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
