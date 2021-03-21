# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)


#设置路径
set(OpenSL ${CMAKE_SOURCE_DIR}/opensl)
set(OpenGL ${CMAKE_SOURCE_DIR}/gles)

#批量添加 cpp 文件
file(GLOB ALL_CPP ${OpenSL}/*.cpp ${OpenGL}/*.cpp )
file(GLOB ALL_AUDIO_ENCODER_CPP ${CMAKE_SOURCE_DIR}/process/*.cpp)
file(GLOB ALL_LIB_COMMON_CPP ${CMAKE_SOURCE_DIR}/libcommon/*.cpp ${CMAKE_SOURCE_DIR}/libcommon/egl_core/*.cpp ${CMAKE_SOURCE_DIR}/libcommon/message_queue/*.cpp ${CMAKE_SOURCE_DIR}/libcommon/opengl_media/*.cpp ${CMAKE_SOURCE_DIR}/libcommon/opensl_media/*.cpp)

#将cpp 生成动态库
#add_library(audiovideo SHARED ${ALL_CPP})
message("cmake path : ${CMAKE_SOURCE_DIR}")


# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

add_library( # Sets the name of the library.
             native-lib

             # Sets the library as a shared library.
             SHARED

             ${ALL_CPP}
             ${ALL_AUDIO_ENCODER_CPP}
             ${ALL_LIB_COMMON_CPP}

             # Provides a relative path to your source file(s).
             native-lib.cpp )

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

##系统添加 ndk log 库
#find_library( # Sets the name of the path variable.
#              log-lib
#
#              # Specifies the name of the NDK library that
#              # you want CMake to locate.
#              log )



# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.


#添加头文件
#include_directories(${CMAKE_SOURCE_DIR}/include)


# =========================================  libyuv =====================================

include_directories(${CMAKE_SOURCE_DIR}/libyuv/include)

set(LIBYUV ${CMAKE_SOURCE_DIR}/libyuv/src)
file(GLOB ALL_CPP_YUV ${LIBYUV}/*.cc)
add_library(
        yuv-lib
        SHARED
        ${ALL_CPP_YUV}
)

# ======================================== ffmpeg ========================================

include_directories(${CMAKE_SOURCE_DIR}/ffmpeg/include/
                    ${CMAKE_SOURCE_DIR}/ffmpeg/include/libavcodec
                    ${CMAKE_SOURCE_DIR}/ffmpeg/include/libavfilter
                    ${CMAKE_SOURCE_DIR}/ffmpeg/include/libavformat
                    ${CMAKE_SOURCE_DIR}/ffmpeg/include/libavutil
                    ${CMAKE_SOURCE_DIR}/ffmpeg/include/libswresample
                    ${CMAKE_SOURCE_DIR}/ffmpeg/include/libswscale
                    ${CMAKE_SOURCE_DIR}/ffmpeg/include/libpostproc
)

set(PATH_TO_PRE_BUILT ${CMAKE_SOURCE_DIR}/ffmpeg/prebuilt)

message("PREBUILT ${PATH_TO_PRE_BUILT}")



#链接库
target_link_libraries( # Specifies the target library.
                       native-lib

                       yuv-lib
                       OpenSLES
                       EGL
                       GLESv2
                       log
                       android
                       jnigraphics
                       z
                       m
                       dl


        #引入静态库 顺序不能弄错，否则会链接不上
        ${PATH_TO_PRE_BUILT}/libavformat.a
        ${PATH_TO_PRE_BUILT}/libavcodec.a
        ${PATH_TO_PRE_BUILT}/libavfilter.a
        ${PATH_TO_PRE_BUILT}/libavutil.a
        ${PATH_TO_PRE_BUILT}/libswscale.a
        ${PATH_TO_PRE_BUILT}/libpostproc.a
        ${PATH_TO_PRE_BUILT}/libvo-aacenc.a
        ${PATH_TO_PRE_BUILT}/libx264.a
        ${PATH_TO_PRE_BUILT}/libfdk-aac.a
        ${PATH_TO_PRE_BUILT}/libswresample.a

                       # Links the target library to the log library
                       # included in the NDK.
#                       ${log-lib}
)