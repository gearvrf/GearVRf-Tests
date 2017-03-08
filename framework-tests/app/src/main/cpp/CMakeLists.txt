cmake_minimum_required(VERSION 3.4.1)

project (framework-tests C CXX)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11 -fexceptions")
add_library(framework-tests SHARED helpers.cpp)

include_directories(${INCLUDE_DIR}/framework/src/main/jni)
include_directories(${INCLUDE_DIR}/framework/src/main/jni/contrib)

add_library(gvrf SHARED IMPORTED)
set_property(TARGET gvrf PROPERTY IMPORTED_LOCATION
             ${PATH_TO_LIBGVRF}/armeabi-v7a/libgvrf.so)
target_link_libraries(framework-tests gvrf log GLESv3)