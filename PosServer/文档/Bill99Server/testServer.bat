@ECHO OFF
@ECHO STARTUP App
@ECHO 设置环境变量,循环当前目录下的lib目录下所有jar文件,并设置CLASSPATH
FOR %%F IN (libs\*.jar) DO call :addcp %%F
goto extlibe
:addcp
SET CLASSPATH=%CLASSPATH%;%1
goto :eof
:extlibe
@ECHO 当前目录下的bin目录为class文件存放目录,设置bin目录到CLASSPATH中
SET CLASSPATH=%CLASSPATH%;bin\
@ECHO 显示CLASSPATH
SET CLASSPATH
.\jre6\bin\java init.TestSocket

pause

