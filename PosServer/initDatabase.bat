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
@ECHO 运行应用程序
@ECHO 参数以;隔开，第一个参数为数据库配置文件，默认以bin/server.properties中配置的数据库文件进行连接(如下)
@ECHO 从第二个参数开始为要初始化的数据的sql语句文件也以;隔开
java init.Init ;init_datas.txt;

pause

