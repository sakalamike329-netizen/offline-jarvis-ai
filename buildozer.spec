[app]
title = JARVIS Server
package.name = jarvisserver
package.domain = com.mikesakala.jarvisserver
source.dir =.
source.include_exts = py,png,jpg,kv,atlas
version = 0.1
requirements = python3,kivy==2.3.0
orientation = portrait
fullscreen = 0
android.permissions = INTERNET

[buildozer]
log_level = 2
warn_on_root = 1

[app:android]
android.api = 33
android.minapi = 21
android.ndk = 25b
android.sdk = 33
android.archs = armeabi-v7a
android.accept_sdk_license_agreements = True
android.allow_backup = False
p4a.bootstrap = sdl2
p4a.arch = armeabi-v7a
