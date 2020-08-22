package com.hikvision.auto.router.base.sdk;

import com.hikvision.auto.router.base.sdk.logger.Logger;
import com.hikvision.auto.router.base.sdk.peripheral.IPeripheral;
import com.hikvision.auto.router.base.sdk.recorder.IRecorder;
import com.hikvision.auto.router.base.sdk.system.ISystem;

public interface HIKSDK {

    String PATH = "sdk";

    ISystem system();

    IRecorder recorder();

    IPeripheral peripheral();

    Logger logger();
}
