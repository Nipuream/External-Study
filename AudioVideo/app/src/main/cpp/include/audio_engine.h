//
// Created by Administrator on 2021/1/30 0030.
//

#ifndef AUDIOVIDEO_AUDIO_ENGINE_H
#define AUDIOVIDEO_AUDIO_ENGINE_H

#include <SLES/OpenSLES.h>
#include <stdio.h>
#include <SLES/OpenSLES_Android.h>
#include <assert.h>
#include <android/log.h>
#include "base.h"


class AudioEngine {

public:
    SLObjectItf engineObj;
    SLEngineItf  engine;

    SLObjectItf outputMixObj;

private:
    void createEngine(){

        //1.1 创建引擎并获取引擎接口
        SLresult  result = slCreateEngine(&engineObj, 0,NULL, 0, NULL, NULL);
        if(SL_RESULT_SUCCESS != result){
            return ;
        }

        //1.2 初始化引擎
        result = (*engineObj)-> Realize(engineObj, SL_BOOLEAN_FALSE);
        if(SL_BOOLEAN_FALSE != result){
            return ;
        }

        //1.3 获取引擎接口 SLEngineItf engineInterface
        result = (*engineObj)->GetInterface(engineObj, SL_IID_ENGINE, &engine);
        if(SL_RESULT_SUCCESS != result){
            return ;
        }

        //第二步 设置混音器
        //2.1 创建混音器
        result = (*engine)->CreateOutputMix(engine, &outputMixObj, 0,0,0);

        if(SL_RESULT_SUCCESS != result){
            return ;
        }

        //2.2 初始化 混音器
        result = (*outputMixObj)->Realize(outputMixObj, SL_BOOLEAN_FALSE);
        if(SL_BOOLEAN_FALSE != result){
            return ;
        }
    }

    virtual void release(){
        if(outputMixObj){
            (*outputMixObj)->Destroy(outputMixObj);
            outputMixObj = NULL;
        }

        if(engineObj){
            (*engineObj)->Destroy(engineObj);
            engineObj = NULL;
            engine = NULL;
        }
    }

public:
    AudioEngine() {
        createEngine();
    }

    virtual ~AudioEngine(){
        release();
    }
};


#endif //AUDIOVIDEO_AUDIO_ENGINE_H
