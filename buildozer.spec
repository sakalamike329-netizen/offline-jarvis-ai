[app]
title = JARVIS SERVER
package.name = jarvisserver
package.domain = com.mike.jarvisserver
source.dir =.
source.include_exts = py,png,jpg,kv,atlas,gguf
version = 1.0
requirements = python3,kivy,plyer
orientation = portrait
fullscreen = 0

[buildozer]
log_level = 2

[app:android]
android.permissions = READ_EXTERNAL_STORAGE,WRITE_EXTERNAL_STORAGE,INTERNET,READ_MEDIA_IMAGES
android.api = 33
android.minapi = 21
android.ndk = 25b
android.accept_sdk_license_agreement = True
