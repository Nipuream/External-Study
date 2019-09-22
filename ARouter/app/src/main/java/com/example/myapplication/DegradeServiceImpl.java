package com.example.myapplication;

import android.content.Context;
import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.service.DegradeService;

/**
 * 定义全局的 降级策略处理
 */
@Route(path = "test/degrade")
public class DegradeServiceImpl implements DegradeService {

    @Override
    public void onLost(Context context, Postcard postcard) {

    }

    @Override
    public void init(Context context) {

    }
}
