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
java init.TestSocket

pause

