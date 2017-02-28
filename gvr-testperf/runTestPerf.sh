adb uninstall org.gearvrf.perftests.test 
adb uninstall org.gearvrf.perftests
./gradlew assembleDebug assembleAndroidTest 
adb install ./app/build/outputs/apk/app-debug-androidTest.apk 
adb install ./app/build/outputs/apk/gvr-testperf.apk 
adb logcat -c
adb shell am instrument -w -r   -e debug false -e class org.gearvrf.tester.GVRComplexSceneTests#complexScene org.gearvrf.perftests.test/android.support.test.runner.AndroidJUnitRunner
adb logcat -d | grep -i fps > complex_scene.log
