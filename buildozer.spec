[app]
title = JARVIS SERVER
package.name = jarvisserver
package.domain = com.mikesakala.jarvisserver
source.dir =.
source.include_exts = py,png,jpg,kv,atlas
version = 0.1
requirements = python3,kivy,plyer
orientation = portrait
fullscreen = 0

[app:android]
android.permissions = INTERNET,READ_EXTERNAL_STORAGE,WRITE_EXTERNAL_STORAGE,RECORD_AUDIO
android.api = 33
android.minapi = 21
android.ndk = 25b
android.sdk = 33
android.build_tools_version = 33.0.2
android.accept_sdk_license_agreement = True
android.archs = arm64-v8a
# The build.yml will replace the line above with armeabi-v7a and arm64-v8a

[buildozer]
log_level = 2
warn_on_root = 1
