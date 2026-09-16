[app]
title = Jarvis AI
package.name = jarvisai
package.domain = com.mike.jarvisai
source.dir =.
source.include_exts = py,png,jpg,kv,atlas
version = 1.0
requirements = python3,kivy
orientation = portrait
fullscreen = 0

[buildozer]
log_level = 2

[app:android]
android.permissions = INTERNET,RECORD_AUDIO
android.api = 33
android.minapi = 21
android.ndk = 25b
android.accept_sdk_license_agreement = True
