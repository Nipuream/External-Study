//
// Created by Administrator on 2020/9/12 0012.
//
//使用EGL需要添加的头文件
#include <EGL/egl.h>
#include <EGL/eglext.h>

//使用OpenGL ES 2.0 也需要在 CMakeLists.txt 中添加 GLESv2库，并指定头文件
#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>
#include "base.h"

extern ANativeWindow* nativeWindow;

EGLDisplay display;
EGLSurface winSurface;
EGLContext context;
pthread_mutex_t  mutex = PTHREAD_MUTEX_INITIALIZER; // 互斥锁
bool isPlay = false;

//const int width = 480;
//const int height = 640;

const int width = 640;
const int height = 480;

//顶点着色器，每个顶点执行一次，可以并行执行
#define GET_STR(x) #x
static const char* vertexShader = GET_STR(

        attribute
        vec4 aPosition; //输入的顶点坐标，会在程序指定将数据输入到该字段
        attribute
        vec2 aTextCoord; //输入的纹理坐标，会在程序指定将数据输入到该字段
        varying
        vec2 vTextCoord; //输出的纹理坐标

        void main(){
            //这里其实是将上下翻转过来 (因为安卓图片会自动上下翻转，所以转回来)
            vTextCoord = vec2(aTextCoord.x, 1.0 - aTextCoord.y);
            //直接把传入的坐标作为渲染管线，gl_position 是Opengl内置的
            gl_Position = aPosition;
        }
);

//图元被光栅化为多少片段，就被调用多少次
static const char* fragYUV420P = GET_STR(

        precision
        mediump float;
        varying
        vec2 vTextCoord;
        //输入的yuv三个纹理
        uniform
        sampler2D yTexture; //采样器
        uniform
        sampler2D uTexture; //采样器
        uniform
        sampler2D vTexture; //采样器

        void main(){
            vec3 yuv;
            vec3 rgb;
            //分别取yuv各个分量的采样纹理
            yuv.x = texture2D(yTexture, vTextCoord).g;
            yuv.y = texture2D(uTexture, vTextCoord).g - 0.5;
            yuv.z = texture2D(vTexture, vTextCoord).g - 0.5;
            rgb = mat3(
                    1.0,1.0,1.0,
                    0.0,-0.39465,2.03211,
                    1.13983,-0.5806,0.0
            ) * yuv;
            //gl_FragColor 是Opengl内置的
            gl_FragColor = vec4(rgb, 1.0);
        }

);


void release();
GLint initShader(const char*source, GLint type);

void loadImage(void* addr, AndroidBitmapInfo& info){
    int h = info.height;
    int w = info.width;

    LOGI("look bitmap information h : %d , w : %d", h, w);
    LOGI("bitmap info.stride:%d, info.width:%d, info.height : %d", info.stride, info.width, info.height);
    //process bitmap.

    ANativeWindow_setBuffersGeometry(nativeWindow, 0, 0, WINDOW_FORMAT_RGBA_8888);

    ANativeWindow_Buffer buffer;
    if(ANativeWindow_lock(nativeWindow, &buffer, 0)){
        ANativeWindow_release(nativeWindow);
        nativeWindow = 0;
        return ;
    }

    LOGI("bufferwidth : %d, bufferStride : %d", buffer.width, buffer.stride);
    auto dst_bits = static_cast<uint8_t *>(buffer.bits);
    auto source_bits = static_cast<uint8_t *>(addr);

    for(int i = 0; i < h; i++){
        memcpy(dst_bits + buffer.stride * (i+150) * 4, source_bits + w * i * 4, w * 4);
    }

    ANativeWindow_unlockAndPost(nativeWindow);
}


void drawWithOpenGl(const char* path){

    pthread_mutex_lock(&mutex); //lock

    //先判断资源是否没有释放，避免播放异常
    release();

    isPlay = true;


    //1.获取display EGLDisplay是一个封装系统物理屏幕的数据类型(可以理解成为绘制目标的一个抽象)
    display = eglGetDisplay(EGL_DEFAULT_DISPLAY);
    if(display == EGL_NO_DISPLAY){
        LOGD("egl display failed.");
        return ;
    }

    //2.初始化egl，后面两个参数为主次版本号
    if(EGL_TRUE != eglInitialize(display, 0 , 0)){
        LOGD("eglInitialize failed");
        return;
    }

    //3.确定可用的渲染表面(surface)的配置
    //一旦EGL有了display之后，它就可以将OpenGl es的输出和设备桥接起来，但是需要指定一些配置项，类似于色彩格式、像素格式、RGBA的表示
    //以及SurfaceType等，不同的系统以及平台使用EGL的标准是不同的
    EGLConfig eglConfig;
    EGLint configNum;
    EGLint configSpec[] = {
            EGL_BUFFER_SIZE, 32,
            EGL_ALPHA_SIZE, 8,
            EGL_RED_SIZE, 8,
            EGL_GREEN_SIZE, 8,
            EGL_BLUE_SIZE, 8,
            EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT,
            EGL_SURFACE_TYPE, EGL_WINDOW_BIT,
            EGL_NONE
    };

    if(EGL_TRUE != eglChooseConfig(display, configSpec, &eglConfig, 1, &configNum)){
        LOGD("eglChooseConfig failed.");
        return;
    }



//    EGLint format;
//    LOGI("format : %d", format);
//    if(!eglGetConfigAttrib(display,eglConfig,EGL_NATIVE_VISUAL_ID, &format)){
//        LOGE("eglGetConfigAttrib() returned error %d ", eglGetError());
//        return ;
//    }
//
//    ANativeWindow_setBuffersGeometry(nativeWindow,width,height,WINDOW_FORMAT_RGBA_8888);

    //4.创建渲染表面 surface
    //创建surface (egl 和 NativeWindow 进行关联，最后一个参数为属性信息，0表示默认版本)
    //这个创建出来的Surface是个可以实际显示的Surface
    winSurface = eglCreateWindowSurface(display, eglConfig, nativeWindow, 0);
    if(winSurface == EGL_NO_SURFACE){
        LOGD("eglCreateWindowSurface failed");
        return ;
    }

    //5.创建渲染上下文Context
    const EGLint ctxAttr[] = {
            EGL_CONTEXT_CLIENT_VERSION, 2, EGL_NONE
    };

    //EGL_NO_CONTEXT 表示不需要多个设备共享上下文
    context = eglCreateContext(display, eglConfig, EGL_NO_CONTEXT, ctxAttr);
    if(context == EGL_NO_CONTEXT){
        LOGD("eglCreateContext failed");
        return ;
    }


    //6.指定某个EGLContext 为当前上下文，关联起来
    //egl 和 opengl 关联
    //两个surface一个读一个写，第二个一般用来离线渲染
    if(EGL_TRUE != eglMakeCurrent(display, winSurface, winSurface, context)){
        LOGD("eglMakeCurrent failed");
        return ;
    }

    //顶灯着色器
    GLint vsh = initShader(vertexShader, GL_VERTEX_SHADER);
    //片元着色器
    GLint fsh = initShader(fragYUV420P, GL_FRAGMENT_SHADER);

    //创建渲染程序
    GLint program = glCreateProgram();
    if(program == 0){
        LOGD("glCreateProgram failed");
        return ;
    }

    //向渲染程序添加着色器
    glAttachShader(program, vsh);
    glAttachShader(program, fsh);

    //链接程序
    glLinkProgram(program);
    GLint status = 0;
    glGetProgramiv(program, GL_LINK_STATUS, &status);
    if(status == 0){
        LOGD("glLinkProgram failed");
        return ;
    }

    LOGD("glLinkProgram success");
    //激活渲染程序
    glUseProgram(program);

    //加入三维顶点数据
    static float ver[] = {
            1.0f, -1.0f, 0.0f,
            -1.0f,-1.0f,0.0f,
            1.0f,1.0f,0.0f,
            -1.0f,1.0f,0.0f
    };

    GLuint apos = static_cast<GLuint>(glGetAttribLocation(program, "aPosition"));
    glEnableVertexAttribArray(apos);
    glVertexAttribPointer(apos, 3, GL_FLOAT, GL_FALSE, 0, ver);

    //加入纹理坐标
    static float fragment[] = {
            1.0f,0.0f,
            0.0f,0.0f,
            1.0f,1.0f,
            0.0f,1.0f
    };

    GLuint aTex = static_cast<GLuint>(glGetAttribLocation(program, "aTextCoord"));
    glEnableVertexAttribArray(aTex);
    glVertexAttribPointer(aTex, 2, GL_FLOAT, GL_FALSE, 0, fragment);

    //纹理初始化
    //对sampler变量，使用函数glUniformli和glUniformliv进行设置
    glUniform1i(glGetUniformLocation(program, "yTexture"), 0);
    glUniform1i(glGetUniformLocation(program,"uTexture"),1);
    glUniform1i(glGetUniformLocation(program,"vTexture"),2);

    //纹理ID
    GLuint texts[3] = {0};
    //创建若干个纹理对象，并且得到纹理ID
    glGenTextures(3, texts);

    //绑定纹理，后面的设置和加载全部作用于当前绑定的纹理对象
    //GL_TEXTURE0,GL_TEXTURE1,GL_TEXTURE2 的就是纹理单元，GL_TEXTURE_1D,GL_TEXTURE_2D,CUBE_WAP为纹理目标
    //通过 glBindTexture 函数将纹理目标和纹理绑定后，对纹理目标所进行的操作都反应到对纹理上
    glBindTexture(GL_TEXTURE_2D, texts[0]);
    //缩小的过滤器
    glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    //放大的过滤器
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    //设置纹理的格式和大小
    //加载纹理到Opengl，读入buffer 定义的位图数据，并吧它复制到当前绑定的纹理对象
    //当前绑定的纹理对象就会被附加上纹理图像
    //width,height 表示第几个像素共用一个yuv元素?
    glTexImage2D(GL_TEXTURE_2D,
                 0,
                 GL_LUMINANCE,//gpu内部格式 亮度，灰度图（这里就是只取一个亮度的颜色通道的意思)
                 width,
                 height,
                 0, //纹理边框
                 GL_LUMINANCE, //数据的像素格式 亮度 灰度图
                 GL_UNSIGNED_BYTE, //像素点存储的数据类型
                 NULL //纹理的数据（先不传)
    );

    //绑定纹理
    glBindTexture(GL_TEXTURE_2D, texts[1]);
    //缩小的过滤器
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    //设置纹理的格式和大小
    glTexImage2D(GL_TEXTURE_2D,
                 0,//细节基本 默认0
                 GL_LUMINANCE,//gpu内部格式 亮度，灰度图（这里就是只取一个颜色通道的意思）
                 width / 2,
                 height / 2,//u数据数量为屏幕的4分之1
                 0,//边框
                 GL_LUMINANCE,//数据的像素格式 亮度，灰度图
                 GL_UNSIGNED_BYTE,//像素点存储的数据类型
                 NULL //纹理的数据（先不传）
    );

    glBindTexture(GL_TEXTURE_2D, texts[2]);
    //缩小的过滤器
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    //设置纹理的格式和大小
    glTexImage2D(GL_TEXTURE_2D,
                 0,//细节基本 默认0
                 GL_LUMINANCE,//gpu内部格式 亮度，灰度图（这里就是只取一个颜色通道的意思）
                 width / 2,
                 height / 2,//v数据数量为屏幕的4分之1
                 0,//边框
                 GL_LUMINANCE,//数据的像素格式 亮度，灰度图
                 GL_UNSIGNED_BYTE,//像素点存储的数据类型
                 NULL //纹理的数据（先不传）
    );

    unsigned char* buf[3] = {0};
    buf[0] = new unsigned char[width * height]; //y
    buf[1] = new unsigned char[width * height / 4]; //u
    buf[2] = new unsigned char[width * height / 4]; //v


    FILE* fp = fopen(path, "rb");
    if(!fp){
        LOGD("open file %s fail", path);
        return ;
    }

    //读取视频yuv数据
    while (!feof(fp)){

        if(!isPlay){
            return ;
        }

        fread(buf[0], 1, width * height, fp);
        fread(buf[1], 1, width * height / 4, fp);
        fread(buf[2], 1, width * height /4, fp);

        //激活第一层纹理，绑定到创建的纹理
        glActiveTexture(GL_TEXTURE0);
        //绑定y对应的纹理
        glBindTexture(GL_TEXTURE_2D, texts[0]);
        //替换纹理，比重新使用glTexImage2D性能高多
        glTexSubImage2D(GL_TEXTURE_2D, 0,
                        0, 0,//相对原来的纹理的offset
                        width, height,//加载的纹理宽度、高度。最好为2的次幂
                        GL_LUMINANCE, GL_UNSIGNED_BYTE,
                        buf[0]);

        glActiveTexture(GL_TEXTURE1);
        //绑定y对应的纹理
        glBindTexture(GL_TEXTURE_2D, texts[1]);
        //替换纹理，比重新使用glTextImage2D性能高
        glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, width / 2, height / 2, GL_LUMINANCE,
                        GL_UNSIGNED_BYTE,
                        buf[1]);

        //激活第三层纹理，绑定到创建的纹理
        glActiveTexture(GL_TEXTURE2);
        //绑定v对应的纹理
        glBindTexture(GL_TEXTURE_2D, texts[2]);
        //替换纹理，比重新使用glTextImage2D性能高
        glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, width / 2, height / 2, GL_LUMINANCE,
                        GL_UNSIGNED_BYTE,
                        buf[2]);

        /**
         * 1. GL_POINT 以点的形式绘制，通常在在绘制粒子效果的场景中
         * 2. GL_LINES 以线的形式绘制，通常在绘制直线的场景中
         * 3. GL_TRIANGLE_STRIP 以三角形的形式进行绘制，所有的二维图像的渲染都会使用这种方式
         */
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
        //窗口显示，交换双缓存区
        eglSwapBuffers(display, winSurface);
    }

    release();
    isPlay = false;
    //释放锁
    pthread_mutex_unlock(&mutex);
}

GLint initShader(const char*source, GLint type){
    //创建shader
    GLint sh = glCreateShader(type);
    if(sh == 0){
        LOGD("glCreate shader %d failed", type);
        return 0;
    }

    //加载shader
    glShaderSource(sh, 1, &source, 0); //代码长度 读到0 就表示字符串结尾

    //编译shader
    glCompileShader(sh);

    GLint status;
    glGetShaderiv(sh, GL_COMPILE_STATUS, &status);
    if(status == 0){
        LOGD("glCompileShader %d failed", type);
        LOGD("source %s", source);
        return 0;
    }

    LOGD("glCompileShader %d success", type);
    return sh;
}

void release(){
    if(display || winSurface || context){
        //销毁显示设备
        eglDestroySurface(display, winSurface);
        //销毁上下文
        eglDestroyContext(display, context);
        //释放窗口
        ANativeWindow_release(nativeWindow);
        //释放线程
        eglReleaseThread();
        //停止
        eglTerminate(display);
        eglMakeCurrent(display, winSurface, EGL_NO_SURFACE, context);

        context = EGL_NO_CONTEXT;
        display = EGL_NO_SURFACE;
        winSurface = nullptr;
        winSurface = 0;
        nativeWindow = 0;
        isPlay = false;
    }
}