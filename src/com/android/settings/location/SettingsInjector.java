package com.android.settings.location;

import android.R.styleable;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.Process;
import android.os.SystemClock;
import android.preference.Preference;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.xmlpull.v1.XmlPullParserException;

class SettingsInjector
{
  private final Context mContext;
  private final Handler mHandler;
  private final Set<Setting> mSettings;

  public SettingsInjector(Context paramContext)
  {
    this.mContext = paramContext;
    this.mSettings = new HashSet();
    this.mHandler = new StatusLoadingHandler(null);
  }

  private Preference addServiceSetting(List<Preference> paramList, InjectedSetting paramInjectedSetting)
  {
    Preference localPreference = new Preference(this.mContext);
    localPreference.setTitle(paramInjectedSetting.title);
    localPreference.setSummary(2131428282);
    localPreference.setIcon(this.mContext.getPackageManager().getDrawable(paramInjectedSetting.packageName, paramInjectedSetting.iconId, null));
    Intent localIntent = new Intent();
    localIntent.setClassName(paramInjectedSetting.packageName, paramInjectedSetting.settingsActivity);
    localPreference.setIntent(localIntent);
    paramList.add(localPreference);
    return localPreference;
  }

  private List<InjectedSetting> getSettings()
  {
    PackageManager localPackageManager = this.mContext.getPackageManager();
    List localList = localPackageManager.queryIntentServices(new Intent("android.location.SettingInjectorService"), 128);
    if (Log.isLoggable("SettingsInjector", 3))
      Log.d("SettingsInjector", "Found services: " + localList);
    ArrayList localArrayList = new ArrayList(localList.size());
    Iterator localIterator = localList.iterator();
    while (localIterator.hasNext())
    {
      ResolveInfo localResolveInfo = (ResolveInfo)localIterator.next();
      try
      {
        localInjectedSetting = parseServiceInfo(localResolveInfo, localPackageManager);
        if (localInjectedSetting == null)
          Log.w("SettingsInjector", "Unable to load service info " + localResolveInfo);
      }
      catch (XmlPullParserException localXmlPullParserException)
      {
        InjectedSetting localInjectedSetting;
        Log.w("SettingsInjector", "Unable to load service info " + localResolveInfo, localXmlPullParserException);
        continue;
        localArrayList.add(localInjectedSetting);
      }
      catch (IOException localIOException)
      {
        Log.w("SettingsInjector", "Unable to load service info " + localResolveInfo, localIOException);
      }
    }
    if (Log.isLoggable("SettingsInjector", 3))
      Log.d("SettingsInjector", "Loaded settings: " + localArrayList);
    return localArrayList;
  }

  private static InjectedSetting parseAttributes(String paramString1, String paramString2, Resources paramResources, AttributeSet paramAttributeSet)
  {
    TypedArray localTypedArray = paramResources.obtainAttributes(paramAttributeSet, R.styleable.SettingInjectorService);
    try
    {
      String str1 = localTypedArray.getString(1);
      int i = localTypedArray.getResourceId(0, 0);
      String str2 = localTypedArray.getString(2);
      if (Log.isLoggable("SettingsInjector", 3))
        Log.d("SettingsInjector", "parsed title: " + str1 + ", iconId: " + i + ", settingsActivity: " + str2);
      InjectedSetting localInjectedSetting = InjectedSetting.newInstance(paramString1, paramString2, str1, i, str2);
      return localInjectedSetting;
    }
    finally
    {
      localTypedArray.recycle();
    }
  }

  private static InjectedSetting parseServiceInfo(ResolveInfo paramResolveInfo, PackageManager paramPackageManager)
    throws XmlPullParserException, IOException
  {
    ServiceInfo localServiceInfo = paramResolveInfo.serviceInfo;
    ApplicationInfo localApplicationInfo = localServiceInfo.applicationInfo;
    Object localObject2;
    if (((0x1 & localApplicationInfo.flags) == 0) && (Log.isLoggable("SettingsInjector", 5)))
    {
      Log.w("SettingsInjector", "Ignoring attempt to inject setting from app not in system image: " + paramResolveInfo);
      localObject2 = null;
    }
    XmlResourceParser localXmlResourceParser;
    do
    {
      return localObject2;
      localXmlResourceParser = null;
      try
      {
        localXmlResourceParser = localServiceInfo.loadXmlMetaData(paramPackageManager, "android.location.SettingInjectorService");
        if (localXmlResourceParser == null)
          throw new XmlPullParserException("No android.location.SettingInjectorService meta-data for " + paramResolveInfo + ": " + localServiceInfo);
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException)
      {
        throw new XmlPullParserException("Unable to load resources for package " + localServiceInfo.packageName);
      }
      finally
      {
        if (localXmlResourceParser != null)
          localXmlResourceParser.close();
      }
      AttributeSet localAttributeSet = Xml.asAttributeSet(localXmlResourceParser);
      int i;
      do
        i = localXmlResourceParser.next();
      while ((i != 1) && (i != 2));
      if (!"injected-location-setting".equals(localXmlResourceParser.getName()))
        throw new XmlPullParserException("Meta-data does not start with injected-location-setting tag");
      Resources localResources = paramPackageManager.getResourcesForApplication(localApplicationInfo);
      InjectedSetting localInjectedSetting = parseAttributes(localServiceInfo.packageName, localServiceInfo.name, localResources, localAttributeSet);
      localObject2 = localInjectedSetting;
    }
    while (localXmlResourceParser == null);
    localXmlResourceParser.close();
    return localObject2;
  }

  public List<Preference> getInjectedSettings()
  {
    List localList = getSettings();
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = localList.iterator();
    while (localIterator.hasNext())
    {
      InjectedSetting localInjectedSetting = (InjectedSetting)localIterator.next();
      Preference localPreference = addServiceSetting(localArrayList, localInjectedSetting);
      this.mSettings.add(new Setting(localInjectedSetting, localPreference, null));
    }
    reloadStatusMessages();
    return localArrayList;
  }

  public void reloadStatusMessages()
  {
    if (Log.isLoggable("SettingsInjector", 3))
      Log.d("SettingsInjector", "reloadingStatusMessages: " + this.mSettings);
    this.mHandler.sendMessage(this.mHandler.obtainMessage(1));
  }

  private final class Setting
  {
    public final Preference preference;
    public final InjectedSetting setting;
    public long startMillis;

    private Setting(InjectedSetting paramPreference, Preference arg3)
    {
      this.setting = paramPreference;
      Object localObject;
      this.preference = localObject;
    }

    public boolean equals(Object paramObject)
    {
      return (this == paramObject) || (((paramObject instanceof Setting)) && (this.setting.equals(((Setting)paramObject).setting)));
    }

    public long getElapsedTime()
    {
      return SystemClock.elapsedRealtime() - this.startMillis;
    }

    public int hashCode()
    {
      return this.setting.hashCode();
    }

    public void maybeLogElapsedTime()
    {
      if ((Log.isLoggable("SettingsInjector", 3)) && (this.startMillis != 0L))
      {
        long l = getElapsedTime();
        Log.d("SettingsInjector", this + " update took " + l + " millis");
      }
    }

    public void startService()
    {
      Handler local1 = new Handler()
      {
        public void handleMessage(Message paramAnonymousMessage)
        {
          Bundle localBundle = paramAnonymousMessage.getData();
          String str = localBundle.getString("summary");
          boolean bool = localBundle.getBoolean("enabled", true);
          if (Log.isLoggable("SettingsInjector", 3))
            Log.d("SettingsInjector", SettingsInjector.Setting.this.setting + ": received " + paramAnonymousMessage + ", bundle: " + localBundle);
          SettingsInjector.Setting.this.preference.setSummary(str);
          SettingsInjector.Setting.this.preference.setEnabled(bool);
          SettingsInjector.this.mHandler.sendMessage(SettingsInjector.this.mHandler.obtainMessage(2, SettingsInjector.Setting.this));
        }
      };
      Messenger localMessenger = new Messenger(local1);
      Intent localIntent = this.setting.getServiceIntent();
      localIntent.putExtra("messenger", localMessenger);
      if (Log.isLoggable("SettingsInjector", 3))
        Log.d("SettingsInjector", this.setting + ": sending update intent: " + localIntent + ", handler: " + local1);
      for (this.startMillis = SystemClock.elapsedRealtime(); ; this.startMillis = 0L)
      {
        SettingsInjector.this.mContext.startServiceAsUser(localIntent, Process.myUserHandle());
        return;
      }
    }

    public String toString()
    {
      return "Setting{setting=" + this.setting + ", preference=" + this.preference + '}';
    }
  }

  private final class StatusLoadingHandler extends Handler
  {
    private boolean mReloadRequested;
    private Set<SettingsInjector.Setting> mSettingsBeingLoaded = new HashSet();
    private Set<SettingsInjector.Setting> mSettingsToLoad = new HashSet();
    private Set<SettingsInjector.Setting> mTimedOutSettings = new HashSet();

    private StatusLoadingHandler()
    {
    }

    public void handleMessage(Message paramMessage)
    {
      if (Log.isLoggable("SettingsInjector", 3))
        Log.d("SettingsInjector", "handleMessage start: " + paramMessage + ", " + this);
      switch (paramMessage.what)
      {
      default:
        Log.wtf("SettingsInjector", "Unexpected what: " + paramMessage);
        if ((this.mSettingsBeingLoaded.size() > 0) || (this.mTimedOutSettings.size() > 1))
          if (Log.isLoggable("SettingsInjector", 2))
            Log.v("SettingsInjector", "too many services already live for " + paramMessage + ", " + this);
        break;
      case 1:
      case 2:
      case 3:
      }
      label474: SettingsInjector.Setting localSetting2;
      do
      {
        Iterator localIterator;
        do
        {
          return;
          this.mReloadRequested = true;
          break;
          SettingsInjector.Setting localSetting3 = (SettingsInjector.Setting)paramMessage.obj;
          localSetting3.maybeLogElapsedTime();
          this.mSettingsBeingLoaded.remove(localSetting3);
          this.mTimedOutSettings.remove(localSetting3);
          removeMessages(3, localSetting3);
          break;
          SettingsInjector.Setting localSetting1 = (SettingsInjector.Setting)paramMessage.obj;
          this.mSettingsBeingLoaded.remove(localSetting1);
          this.mTimedOutSettings.add(localSetting1);
          if (!Log.isLoggable("SettingsInjector", 5))
            break;
          Log.w("SettingsInjector", "Timed out after " + localSetting1.getElapsedTime() + " millis trying to get status for: " + localSetting1);
          break;
          if ((this.mReloadRequested) && (this.mSettingsToLoad.isEmpty()) && (this.mSettingsBeingLoaded.isEmpty()) && (this.mTimedOutSettings.isEmpty()))
          {
            if (Log.isLoggable("SettingsInjector", 2))
              Log.v("SettingsInjector", "reloading because idle and reload requesteed " + paramMessage + ", " + this);
            this.mSettingsToLoad.addAll(SettingsInjector.this.mSettings);
            this.mReloadRequested = false;
          }
          localIterator = this.mSettingsToLoad.iterator();
          if (localIterator.hasNext())
            break label474;
        }
        while (!Log.isLoggable("SettingsInjector", 2));
        Log.v("SettingsInjector", "nothing left to do for " + paramMessage + ", " + this);
        return;
        localSetting2 = (SettingsInjector.Setting)localIterator.next();
        localIterator.remove();
        localSetting2.startService();
        this.mSettingsBeingLoaded.add(localSetting2);
        sendMessageDelayed(obtainMessage(3, localSetting2), 1000L);
      }
      while (!Log.isLoggable("SettingsInjector", 3));
      Log.d("SettingsInjector", "handleMessage end " + paramMessage + ", " + this + ", started loading " + localSetting2);
    }

    public String toString()
    {
      return "StatusLoadingHandler{mSettingsToLoad=" + this.mSettingsToLoad + ", mSettingsBeingLoaded=" + this.mSettingsBeingLoaded + ", mTimedOutSettings=" + this.mTimedOutSettings + ", mReloadRequested=" + this.mReloadRequested + '}';
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.location.SettingsInjector
 * JD-Core Version:    0.6.2
 */