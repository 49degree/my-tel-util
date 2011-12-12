package eu.chainfire.gingerbreak;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.util.Log;

public class MainActivity extends PreferenceActivity
{
  private String APP_COPYRIGHT_1 = "APK + Exploit Mods: Chainfire";
  private String APP_COPYRIGHT_2 = "Original Exploit: The Android Exploid Crew";
  private String APP_TITLE = "GingerBreak v1.2";
  private final String LOG_TAG = "GingerBreak";
  private Context _this;
  private Handler handler = new Handler();
  private boolean haveSU = false;

  private PreferenceScreen createPreferenceHierarchy()
  {
    PreferenceScreen localPreferenceScreen = getPreferenceManager().createPreferenceScreen(this);
    Preference localPreference = new Preference(this);
    localPreference.setTitle(APP_TITLE);
    localPreference.setSummary(APP_COPYRIGHT_1 + '\n' + APP_COPYRIGHT_2);
    localPreference.setKey("copyright");
    localPreference.setEnabled(true);
    localPreferenceScreen.addPreference(localPreference);
    PreferenceCategory localPreferenceCategory = Pref.Category(this, localPreferenceScreen, "Options");
    Pref.Preference(this, localPreferenceCategory, "Root device", "Runs the exploit and installs SuperUser", true, new Preference.OnPreferenceClickListener()
    {
      public boolean onPreferenceClick(Preference paramPreference)
      {
        new MainActivity.Exploit(MainActivity.this).go(handler);
        return false;
      }
    });
    Pref.Preference(this, localPreferenceCategory, "UnRoot device", "Attempts to unroot the device", haveSU, new Preference.OnPreferenceClickListener()
    {
      public boolean onPreferenceClick(Preference paramPreference)
      {
        new MainActivity.UnExploit(MainActivity.this).go(handler);
        return false;
      }
    });
    Pref.Preference(this, Pref.Category(this, localPreferenceScreen, "Donate"), "Donate with PayPal", "Chainfire: chainfire@chainfire.eu\nThe Android Exploid Crew: 7-4-3-C@web.de", true, null);
    return localPreferenceScreen;
  }

  private void errorMessage(String paramString)
  {
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(_this);
    localBuilder.setTitle("GingerBreak - ERROR").setMessage(paramString).setNeutralButton("OK", new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramDialogInterface, int paramInt)
      {
        finish();
      }
    });
    try
    {
      localBuilder.show();
      return;
    }
    catch (Exception localException)
    {
      
    }
  }

  private void log(String paramString)
  {
    Log.i("GingerBreak", "[GingerBreak] " + paramString);
  }

  private void okMessage(String paramString)
  {
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(_this);
    localBuilder.setTitle("GingerBreak").setMessage(paramString).setNeutralButton("OK", null);
    try
    {
      localBuilder.show();
      return;
    }
    catch (Exception localException)
    {
    }
  }

  private void rebootMessage(String paramString)
  {
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(_this);
    localBuilder.setTitle("GingerBreak").setMessage(paramString).setNeutralButton("OK", new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramDialogInterface, int paramInt)
      {
      }
    });
    try
    {
      localBuilder.show();
      return;
    }
    catch (Exception localException)
    {
    }
  }

  public boolean deleteFile(String paramString)
  {
    return new File(paramString).delete();
  }

  public boolean fileExists(String paramString1, String paramString2)
  {
    boolean i = false;
    List localList = SuperUser.executeSU(paramString1, "ls \"" + paramString2 + "\"");
    Iterator localIterator = null;
    if ((localList != null) && (localList.size() > 0))
      localIterator = localList.iterator();
    while (true)
    {
      if (localIterator!=null&&!localIterator.hasNext())
        return i;
      if (((String)localIterator.next()).indexOf(paramString2) == -1)
        continue;
      i = true;
    }
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    _this = this;
    new Startup(null).go(handler);
  }

  protected void onStop()
  {
    super.onStop();
    finish();
  }

  public boolean systemRemountWritable()
  {
    boolean bool = false;
    List localList = SuperUser.executeSU("su", "mount");
    String str1 = null;
    Iterator localIterator = null;
    if ((localList != null) && (localList.size() > 0))
      localIterator = localList.iterator();
    while (true)
    {
      if (localIterator!=null&&!localIterator.hasNext())
      {
        if ((!bool) && (str1 != null))
        {
          SuperUser.executeSU("su", "mount -o rw -o remount " + str1 + " /system");
          bool = systemWritable();
        }
        return bool;
      }
      String str2 = ((String)localIterator.next()).trim().replace("(", ",").replace(")", ",").replace(" ", ",") + ",";
      if (str2.indexOf(",/system,") == -1)
        continue;
      if (str2.indexOf(",rw,") != -1)
      {
        bool = true;
        continue;
      }
      if (str2.indexOf(",") == -1)
        continue;
      str1 = str2.substring(0, str2.indexOf(","));
    }
  }

  public boolean systemWritable()
  {
    boolean i = false;
    List localList = SuperUser.executeSU("su", "mount");
    Iterator localIterator = null;
    if ((localList != null) && (localList.size() > 0))
      localIterator = localList.iterator();
    while (true)
    {
      if (localIterator!=null&&!localIterator.hasNext())
        return i;
      String str = ((String)localIterator.next()).trim().replace("(", ",").replace(")", ",").replace(" ", ",") + ",";
      if ((str.indexOf(",/system,") == -1) || (str.indexOf(",rw,") == -1))
        continue;
      i = true;
    }
  }

  private class Exploit extends AsyncTask<Integer, Integer, Integer>
  {
    private ProgressDialog dialog = null;
    private Handler handler = null;

    private Exploit(Context context)
    {
    }

    protected Integer doInBackground(Integer[] paramArrayOfInteger)
    {
      updateMessage("Rooting ...\nRunning exploit ...\nThis may take a few minutes !\nDevice will reboot on success !");
      SuperUser.executeSU("sh", "/data/data/eu.chainfire.gingerbreak/files/gingerbreak");
      try
      {
        Thread.sleep(30000L);
        return Integer.valueOf(0);
      }
      catch (Exception localException)
      {
    	  return Integer.valueOf(0);
      }
    }

    public void go(Handler paramHandler)
    {
      handler = paramHandler;
      execute(new Integer[0]);
    }

    protected void onPostExecute(Integer paramInteger)
    {
      dialog.dismiss();
      MainActivity.this.rebootMessage("Not sure what happened here. Either the exploit failed or the reboot failed. Please reboot manually and see if you have SuperUser !");
    }

    protected void onPreExecute()
    {
      dialog = new ProgressDialog(_this);
      dialog.setTitle("GingerBreak");
      dialog.setMessage("Rooting ...");
      dialog.setIndeterminate(true);
      dialog.setCancelable(false);
      dialog.show();
    }

    protected void updateMessage(String paramString)
    {
      MainActivity.Exploit.UpdateRunnable local1UpdateRunnable = new MainActivity.Exploit.UpdateRunnable(this);
      local1UpdateRunnable.message = paramString;
      handler.post(local1UpdateRunnable);
    }
    private class UpdateRunnable implements Runnable {
    	public String message = "";

		public void run() {
			if (MainActivity.UnExploit.access$2(this$1) != null)
				MainActivity.UnExploit.access$2(this$1).setMessage(message);
			}
		}
  }

  private class Startup extends AsyncTask<Integer, Integer, Integer>
  {
    private ProgressDialog dialog = null;
    private Handler handler = null;

    private Startup(Context context)
    {
    }

    protected Integer doInBackground(Integer[] paramArrayOfInteger)
    {
      updateMessage("Loading ...\nChecking folder ...");
      String str1 = Asset.assetFolder(_this);
      Integer localInteger;
      if (str1 == null)
        localInteger = Integer.valueOf(2);
      while (true)
      {
    	  
        if (!str1.equals("/data/data/eu.chainfire.gingerbreak/files/"))
        {
          localInteger = Integer.valueOf(2);
          continue;
        }
        updateMessage("Loading ...\nChecking for SuperUser ...");
        MainActivity localMainActivity = MainActivity.this;
        if (SuperUser.executeSU("su", "ls /") != null);
        for (boolean bool = true; ; bool = false)
        {
          localMainActivity.haveSU = bool;
          updateMessage("Loading ...\nCleaning files ...");
          deleteFile("/data/data/eu.chainfire.gingerbreak/files/gingerbreak");
          deleteFile("/data/data/eu.chainfire.gingerbreak/files/su");
          deleteFile("/data/data/eu.chainfire.gingerbreak/files/superuser.apk");
          deleteFile("/data/data/eu.chainfire.gingerbreak/files/install.sh");
          deleteFile("/data/data/eu.chainfire.gingerbreak/files/sh");
          deleteFile("/data/data/eu.chainfire.gingerbreak/files/bsh");
          deleteFile("/data/data/eu.chainfire.gingerbreak/files/crashlog");
          deleteFile("/data/data/eu.chainfire.gingerbreak/files/rooted");
          updateMessage("Loading ...\nExtracting assets ...\nGingerBreak");
          String str2 = Asset.extractAssetToFile(_this, "gingerbreak.png");
          String[] arrayOfString1 = new String[3];
          arrayOfString1[0] = "rm /data/data/eu.chainfire.gingerbreak/files/gingerbreak";
          arrayOfString1[1] = ("cat \"" + str2 + "\" > /data/data/eu.chainfire.gingerbreak/files/gingerbreak");
          arrayOfString1[2] = "chmod 0755 /data/data/eu.chainfire.gingerbreak/files/gingerbreak";
          SuperUser.executeSU("sh", arrayOfString1);
          updateMessage("Loading ...\nExtracting assets ...\nSuperUser (BIN)");
          String str3 = Asset.extractAssetToFile(_this, "su.png");
          String[] arrayOfString2 = new String[3];
          arrayOfString2[0] = "rm /data/data/eu.chainfire.gingerbreak/files/su";
          arrayOfString2[1] = ("cat \"" + str3 + "\" > /data/data/eu.chainfire.gingerbreak/files/su");
          arrayOfString2[2] = "chmod 0755 /data/data/eu.chainfire.gingerbreak/files/su";
          SuperUser.executeSU("sh", arrayOfString2);
          updateMessage("Loading ...\nExtracting assets ...\nSuperUser (APK)");
          String str4 = Asset.extractAssetToFile(_this, "superuser.png");
          String[] arrayOfString3 = new String[3];
          arrayOfString3[0] = "rm /data/data/eu.chainfire.gingerbreak/files/superuser.apk";
          arrayOfString3[1] = ("cat \"" + str4 + "\" > /data/data/eu.chainfire.gingerbreak/files/superuser.apk");
          arrayOfString3[2] = "chmod 0755 /data/data/eu.chainfire.gingerbreak/files/superuser.apk";
          SuperUser.executeSU("sh", arrayOfString3);
          updateMessage("Loading ...\nExtracting assets ...\nInstall script");
          String str5 = Asset.extractAssetToFile(_this, "install.png");
          String[] arrayOfString4 = new String[3];
          arrayOfString4[0] = "rm /data/data/eu.chainfire.gingerbreak/files/install.sh";
          arrayOfString4[1] = ("cat \"" + str5 + "\" > /data/data/eu.chainfire.gingerbreak/files/install.sh");
          arrayOfString4[2] = "chmod 0755 /data/data/eu.chainfire.gingerbreak/files/install.sh";
          SuperUser.executeSU("sh", arrayOfString4);
          if ((fileExists("sh", "/data/data/eu.chainfire.gingerbreak/files/gingerbreak")) && (fileExists("sh", "/data/data/eu.chainfire.gingerbreak/files/su")) && (fileExists("sh", "/data/data/eu.chainfire.gingerbreak/files/superuser.apk")) && (fileExists("sh", "/data/data/eu.chainfire.gingerbreak/files/install.sh")))
            break ;
          localInteger = Integer.valueOf(1);
          break;
        }
        updateMessage("Loading ...\nUser interface ...");
        localInteger = Integer.valueOf(0);
        return localInteger;
      }
     
    }

    public void go(Handler paramHandler)
    {
      handler = paramHandler;
      execute(new Integer[0]);
    }

    protected void onPostExecute(Integer paramInteger)
    {
      dialog.dismiss();
      if (paramInteger.intValue() == 1)
        MainActivity.this.errorMessage("Could not extract assets !");
      if (paramInteger.intValue() == 2)
        MainActivity.this.errorMessage("Data is in an unexpected location!\n\nPlease report this in the XDA thread!\n\n" + Asset.assetFolder(_this));
      while (true)
      {
        
        if (paramInteger.intValue() == 0)
        {
          setPreferenceScreen(MainActivity.this.createPreferenceHierarchy());
          MainActivity.this.okMessage("Please make sure of the following before rooting:\n\n- You have an SD card inserted and mounted\n- USB debugging is enabled");
          continue;
        }else{
        	return;
        }
      }
    }

    protected void onPreExecute()
    {
      dialog = new ProgressDialog(_this);
      dialog.setTitle("GingerBreak");
      dialog.setMessage("Loading ...");
      dialog.setIndeterminate(true);
      dialog.setCancelable(false);
      dialog.show();
    }

    protected void updateMessage(String paramString)
    {
      MainActivity.Startup.UpdateRunnable local1UpdateRunnable = new MainActivity.Startup.UpdateRunnable(this);
      //UpdateRunnable local1UpdateRunnable = new UpdateRunnable(this);
      local1UpdateRunnable.message = paramString;
      handler.post(local1UpdateRunnable);
    }
    private class UpdateRunnable implements Runnable {
    	public String message = "";
    	public void run(){
    		if (MainActivity.Startup.access$2(this$1) != null)
    			MainActivity.Startup.access$2(this$1).setMessage(message);
    		}
    }
  }

  private class UnExploit extends AsyncTask<Integer, Integer, Integer>
  {
    private ProgressDialog dialog = null;
    private Handler handler = null;

    private UnExploit(Context context)
    {
    }

    protected Integer doInBackground(Integer[] paramArrayOfInteger)
    {
      updateMessage("UnRooting ...\nRemoving files ...\nDevice will reboot on success !");
      Integer localInteger = 0;
      if (!systemRemountWritable())
        localInteger = Integer.valueOf(1);
      while (true){
        String[] arrayOfString = new String[11];
        arrayOfString[0] = "rm /system/app/Superuser.apk";
        arrayOfString[1] = "rm /system/bin/su";
        arrayOfString[2] = "rm /system/xbin/su";
        arrayOfString[3] = "rm /data/dalvik-cache/*";
        arrayOfString[4] = "reboot -f";
        arrayOfString[5] = "reboot";
        arrayOfString[6] = "reboot normal";
        arrayOfString[7] = "toolbox reboot";
        arrayOfString[8] = "busybox reboot -f";
        arrayOfString[9] = "busybox reboot";
        arrayOfString[10] = "busybox reboot normal";
        SuperUser.executeSU("su", arrayOfString);
        try
        {
          Thread.sleep(30000L);
          localInteger = Integer.valueOf(0);
        }catch (Exception localException){
        	localInteger = Integer.valueOf(0);
        	break;
        }
      }
      return localInteger;
    }

    public void go(Handler paramHandler)
    {
      handler = paramHandler;
      execute(new Integer[0]);
    }

    protected void onPostExecute(Integer paramInteger)
    {
      dialog.dismiss();
      if (paramInteger.intValue() == 1)
        MainActivity.this.okMessage("Failed to remount /system as read/write !");
      while (true)
      {
        
        MainActivity.this.okMessage("Not sure what happened here. Either the UnRoot failed or the reboot failed. Please reboot manually and see if you still have SuperUser !");
        return;
      }
    }

    protected void onPreExecute()
    {
      dialog = new ProgressDialog(_this);
      dialog.setTitle("GingerBreak");
      dialog.setMessage("UnRooting ...");
      dialog.setIndeterminate(true);
      dialog.setCancelable(false);
      dialog.show();
    }

    protected void updateMessage(String paramString)
    {
      MainActivity.UnExploit.UpdateRunnable local1UpdateRunnable = new MainActivity.UnExploit.UpdateRunnable(this);
      local1UpdateRunnable.message = paramString;
      handler.post(local1UpdateRunnable);
    }
    private class UpdateRunnable implements Runnable {
    	public String message = "";
    	public void run() {
    		if (MainActivity.UnExploit.access$2(this$1) != null)
    			MainActivity.UnExploit.access$2(this$1).setMessage(message);
		}
	}
  }
}