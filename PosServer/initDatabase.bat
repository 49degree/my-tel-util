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
@ECHO ������;��������һ������Ϊ���ݿ������ļ���Ĭ����bin/server.properties�����õ����ݿ��ļ���������(����)
@ECHO �ӵڶ���������ʼΪҪ��ʼ�������ݵ�sql����ļ�Ҳ��;����
java init.Init ;init_datas.txt;

pause

