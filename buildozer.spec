[app]

# (str) Title of your application
title = VoxCPM Voice Cloning AI

# (str) Package name
package.name = voxcpmvoiceclone

# (str) Package domain (needed for android/ios packaging)
package.domain = com.ai.voxcpm

# (str) Source code where the main.py lives
source.dir = .

# (list) Source files to include (let empty to include all the files)
source.include_exts = py,png,jpg,kv,atlas,json,wav,mp3

# (list) Application requirements
# Note: Heavy C++ / PyTorch / PyAV dependencies like torch, torchaudio, librosa, and whisperx 
# are best processed via a backend API (e.g., Gradio / FastAPI server on Google Colab or Cloud GPU)
# because PyTorch with CUDA cannot be bundled natively inside a standalone Android APK.
requirements = python3,kivy,requests,urllib3,certifi

# (str) Custom source folders for requirements
# Sets custom source for any requirement with recipes

# (str) Main entry point of the python application
# Rename your python script to main.py or specify it here
source.main = main.py

# (str) Application versioning
version = 1.0.0

# (list) Permissions
android.permissions = INTERNET, RECORD_AUDIO, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE

# (int) Target Android API
android.api = 33

# (int) Minimum API required
android.minapi = 21

# (str) Android NDK version
android.ndk = 25b

# (bool) If True, then skip building NDK code
android.skip_update = False

# (bool) Accept NDK license automatically
android.accept_sdk_license = True

# (str) Android architecture to build for (e.g. armeabi-v7a, arm64-v8a)
android.archs = arm64-v8a, armeabi-v7a

# (bool) Enable AndroidX
android.enable_androidx = True

# (list) List of Java .jar files to add
# android.add_jars = foo.jar,bar.jar

# (str) Orientation
orientation = portrait

# (bool) Fullscreen
fullscreen = 0

[buildozer]

# (int) Log level (0 = error only, 1 = info, 2 = debug (with command output))
log_level = 2

# (int) Display warning if buildozer is run as root (0 = disable, 1 = enable)
warn_on_root = 1
