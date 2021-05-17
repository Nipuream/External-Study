package com.hikvision.auto.router.base.business.recorder.model;

import com.hikvision.auto.router.base.comm.BaseModelB;
import com.hikvision.auto.router.base.sdk.recorder.model.FaceRegisterS;

import java.util.List;

public class FaceRegisterModelB extends BaseModelB {

    private List<FaceRegisterS> registerS;

    public FaceRegisterModelB(List<FaceRegisterS> registerS) {
        this.registerS = registerS;
    }

    public List<FaceRegisterS> getRegisterS() {
        return registerS;
    }

    public void setRegisterS(List<FaceRegisterS> registerS) {
        this.registerS = registerS;
    }

    @Override
    public String toString() {
        return "FaceRegisterModelB{" +
                "registerS=" + registerS +
                '}';
    }
}
