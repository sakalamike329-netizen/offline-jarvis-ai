[[app]
title = Offline Jarvis AI
package.name = offlinejarvis
package.domain = com.sakala.jarvis
source.dir =.
version = 1.0
requirements = python3,kivy,flask,vosk,pyjnius,plyer,android
orientation = portrait
android.permissions = INTERNET,RECORD_AUDIO,READ_EXTERNAL_STORAGE,WRITE_EXTERNAL_STORAGE,WAKE_LOCK
android.api = 33
android.minapi = 21
p4a.arch = armeabi-v7a
android.accept_sdk_license_agreement = True

[buildozer]
log_level = 2
