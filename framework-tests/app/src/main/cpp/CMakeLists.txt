cmake_minimum_required(VERSION 3.4.1)

project (framework-tests C CXX)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11 -fexceptions")
add_library(framework-tests SHARED helpers.cpp)

include_directories(${INCLUDE_DIR}/framework/src/main/jni)
include_directories(${INCLUDE_DIR}/framework/src/main/jni/contrib)
include_directories(${INCLUDE_DIR}/../../GearVRf/GVRf/Framework/framework/src/main/jni)
include_directories(${INCLUDE_DIR}/../../GearVRf/GVRf/Framework/framework/src/main/jni/contrib)

string(TOLOWER ${CMAKE_BUILD_TYPE} buildType)

target_link_libraries(framework-tests log GLESv3)