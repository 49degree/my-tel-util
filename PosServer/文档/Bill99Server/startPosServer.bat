@ECHO OFF
@ECHO STARTUP App
@ECHO ���û�������,ѭ����ǰĿ¼�µ�libĿ¼������jar�ļ�,������CLASSPATH
FOR %%F IN (libs\*.jar) DO call :addcp %%F
goto extlibe
:addcp
SET CLASSPATH=%CLASSPATH%;%1
goto :eof
:extlibe
@ECHO ��ǰĿ¼�µ�binĿ¼Ϊclass�ļ����Ŀ¼,����binĿ¼��CLASSPATH��
SET CLASSPATH=%CLASSPATH%;bin\
@ECHO ��ʾCLASSPATH
SET CLASSPATH
@ECHO ����Ӧ�ó���
@ECHO com.a3650.posserver.core.init.socket.SocketInitContext��ʼ�������
@ECHO ��ɴ������������ļ���Ĭ���ļ�Ϊbin/server.properties
@ECHO ���ʹ��WEBLOGIC����Դ������VM��������-Dsun.lang.ClassLoader.allowArraySyntax=true 
.\jre6\bin\java -Xms12m -Xmx512m com.a3650.posserver.core.init.socket.SocketInitContext
pause

