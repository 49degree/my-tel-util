package eu.chainfire.gingerbreak;

import android.util.Log;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SuperUser
{
  public static final String LOG_TAG = "GingerBreak";
  public static final boolean debug = true;

  public static List<String> executeSU(String paramString1, String paramString2)
  {
    String[] arrayOfString = new String[1];
    arrayOfString[0] = paramString2;
    return executeSU(paramString1, arrayOfString);
  }

  public static List<String> executeSU(String paramString, String[] paramArrayOfString)
  {
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = null;
    try
    {
      Process localProcess = Runtime.getRuntime().exec(paramString);
      DataOutputStream localDataOutputStream = new DataOutputStream(localProcess.getOutputStream());
      DataInputStream localDataInputStream1 = new DataInputStream(localProcess.getInputStream());
      DataInputStream localDataInputStream2 = new DataInputStream(localProcess.getErrorStream());
      int i = paramArrayOfString.length;
      int j = 0;
      if (j >= i)
      {
        localDataOutputStream.writeBytes("exit\n");
        localDataOutputStream.flush();
      }
      
      
      
      while (true)
      {
    	  Log.e("GingerBreak", "[isRunning(localProcess)] " + isRunning(localProcess));
    	  
        if (!isRunning(localProcess))
        {
//          localProcess.waitFor();
//          if (localProcess.exitValue() != 255)
//            break;
          String str1 = paramArrayOfString[j];
          Log.e("GingerBreak", "[GingerBreak][SU+] " + str1);
          localDataOutputStream.writeBytes(str1 + "\n");
          localDataOutputStream.flush();
        }
        if (localDataInputStream2.available() > 0)
        {
          String str3 = localDataInputStream2.readLine();
          Log.e("GingerBreak", "[GingerBreak][SU-] " + str3);
        }
        if (localDataInputStream1.available() > 0)
        {
          String str2 = localDataInputStream1.readLine();
          Log.e("GingerBreak", "[GingerBreak][SU*] " + str2);
          localArrayList1.add(str2);
        }
        if(j++>=i)
      	  break;
        Thread.sleep(20L);
      }
      
     
      while (localDataInputStream2.available() > 0){
          String str5 = localDataInputStream2.readLine();
          Log.e("GingerBreak", "[GingerBreak][SU-] " + str5);
      }
      
      while (true)
      {
        if (localDataInputStream1.available() <= 0)
        {
          localArrayList2 = localArrayList1;
          break;
        }
        String str4 = localDataInputStream1.readLine();
        Log.e("GingerBreak", "[GingerBreak][SU*] " + str4);
        localArrayList1.add(str4);
      }
    }
    catch (InterruptedException localInterruptedException)
    {
      localArrayList2 = null;
    }
    catch (IOException localIOException)
    {
        localArrayList2 = null;
    }
    return localArrayList2;
  }

  public static void executeSUreboot()
  {
    String[] arrayOfString = new String[7];
    arrayOfString[0] = "reboot -f";
    arrayOfString[1] = "reboot";
    arrayOfString[2] = "reboot normal";
    arrayOfString[3] = "toolbox reboot";
    arrayOfString[4] = "busybox reboot -f";
    arrayOfString[5] = "busybox reboot";
    arrayOfString[6] = "busybox reboot normal";
    executeSU("su", arrayOfString);
  }

  private static boolean isRunning(Process paramProcess)
  {
    try
    {
      int j = paramProcess.exitValue();
      if (j == 0)
    	  return true;
      else
    	  return false;
    }
    catch (Exception localException)
    {
      return false;
    }
  }
}

/* Location:           E:\寮�彂宸ュ叿\android寮�彂宸ュ叿\鍙嶇紪璇戝伐鍏穃apktool2.2\gingerbreak\gingerbreak_dex2jar.jar
 * Qualified Name:     eu.chainfire.gingerbreak.SuperUser
 * JD-Core Version:    0.6.0
 */