@echo off
rem 获取当前运行设备
adb devices >takeSnapshot/devices.txt
rem 获取APK文件
dir takeSnapshot\apk /B >takeSnapshot/apk.txt
rem 运行monkeyrunner 脚本
monkeyrunner monkeyrunner/takeSnapshot/work.py
