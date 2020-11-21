# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)


#设置路径
set(OpenSL ${CMAKE_SOURCE_DIR}/opensl)
set(OpenGL ${CMAKE_SOURCE_DIR}/gles)

#批量添加 cpp 文件
file(GLOB ALL_CPP ${OpenSL}/*.cpp ${OpenGL}/*.cpp)

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

             # Provides a relative path to your source file(s).
             native-lib.cpp )

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

#系统添加 ndk log 库
find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

message("log path : ${log-lib}")

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.


#添加头文件
include_directories(${CMAKE_SOURCE_DIR}/include)



# =========================================  libyuv =====================================

include_directories(${CMAKE_SOURCE_DIR}/libyuv/include)

set(LIBYUV ${CMAKE_SOURCE_DIR}/libyuv/src)
file(GLOB ALL_CPP_YUV ${LIBYUV}/*.cc)
add_library(
        yuv-lib
        SHARED
        ${ALL_CPP_YUV}
)


#链接库
target_link_libraries( # Specifies the target library.
                       native-lib
                       yuv-lib
                       OpenSLES
                       EGL
                       GLESv2
                       android
                       jnigraphics

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )