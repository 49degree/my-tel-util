@echo off
rem ��ȡ��ǰ�����豸
adb devices >takeSnapshot/devices.txt
rem ��ȡAPK�ļ�
dir takeSnapshot\apk /B >takeSnapshot/apk.txt
rem ����monkeyrunner �ű�
monkeyrunner monkeyrunner/takeSnapshot/work.py
