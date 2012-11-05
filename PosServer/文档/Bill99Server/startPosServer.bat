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
@ECHO com.a3650.posserver.core.init.socket.SocketInitContext开始服务程序
@ECHO 后可带服务器配置文件，默认文件为bin/server.properties
@ECHO 如果使用WEBLOGIC数据源，启动VM参数加上-Dsun.lang.ClassLoader.allowArraySyntax=true 
.\jre6\bin\java -Xms12m -Xmx512m com.a3650.posserver.core.init.socket.SocketInitContext
pause

