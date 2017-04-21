cmake_minimum_required(VERSION 3.4.1)

project (framework-tests C CXX)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11 -fexceptions")
add_library(framework-tests SHARED helpers.cpp)

include_directories(${INCLUDE_DIR}/framework/src/main/jni)
include_directories(${INCLUDE_DIR}/framework/src/main/jni/contrib)
include_directories(${INCLUDE_DIR}/../../GearVRf/GVRf/Framework/framework/src/main/jni)
include_directories(${INCLUDE_DIR}/../../GearVRf/GVRf/Framework/framework/src/main/jni/contrib)

string(TOLOWER ${CMAKE_BUILD_TYPE} buildType)

add_library(gvrf SHARED IMPORTED)
set_property(TARGET gvrf PROPERTY IMPORTED_LOCATION
             ${CMAKE_CURRENT_SOURCE_DIR}/../../../../../../GearVRf/GVRf/Framework/framework/build/intermediates/ndkBuild/${buildType}/obj/local/${ANDROID_ABI}/libgvrf.so)

target_link_libraries(framework-tests gvrf log GLESv3)