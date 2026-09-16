[app]
title = JARVIS SERVER
package.name = jarvisserver
package.domain = com.mike.jarvisserver
source.dir =.
source.include_exts = py,png,jpg,kv,atlas
version = 1.0
requirements = python3,kivy==2.3.0,plyer
orientation = portrait

[buildozer]
log_level = 2

[app:android]
android.permissions = INTERNET,READ_EXTERNAL_STORAGE,WRITE_EXTERNAL_STORAGE,RECORD_AUDIO
android.api = 33
android.minapi = 21
android.build_tools_version = 33.0.2
android.ndk = 25b
android.accept_sdk_license_agreement = True
android.archs = armeabi-v7a, arm64-v8a
