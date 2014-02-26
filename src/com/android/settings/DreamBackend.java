package com.android.settings;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings.Secure;
import android.service.dreams.IDreamManager;
import android.service.dreams.IDreamManager.Stub;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import com.android.internal.R.styleable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;

public class DreamBackend
{
  private static final String TAG = DreamSettings.class.getSimpleName() + ".Backend";
  private final DreamInfoComparator mComparator;
  private final Context mContext;
  private final IDreamManager mDreamManager;
  private final boolean mDreamsActivatedOnDockByDefault;
  private final boolean mDreamsActivatedOnSleepByDefault;
  private final boolean mDreamsEnabledByDefault;

  public DreamBackend(Context paramContext)
  {
    this.mContext = paramContext;
    this.mDreamManager = IDreamManager.Stub.asInterface(ServiceManager.getService("dreams"));
    this.mComparator = new DreamInfoComparator(getDefaultDream());
    this.mDreamsEnabledByDefault = paramContext.getResources().getBoolean(17891402);
    this.mDreamsActivatedOnSleepByDefault = paramContext.getResources().getBoolean(17891404);
    this.mDreamsActivatedOnDockByDefault = paramContext.getResources().getBoolean(17891403);
  }

  private boolean getBoolean(String paramString, boolean paramBoolean)
  {
    ContentResolver localContentResolver = this.mContext.getContentResolver();
    if (paramBoolean);
    for (int i = 1; Settings.Secure.getInt(localContentResolver, paramString, i) == 1; i = 0)
      return true;
    return false;
  }

  private static ComponentName getDreamComponentName(ResolveInfo paramResolveInfo)
  {
    if ((paramResolveInfo == null) || (paramResolveInfo.serviceInfo == null))
      return null;
    return new ComponentName(paramResolveInfo.serviceInfo.packageName, paramResolveInfo.serviceInfo.name);
  }

  private static ComponentName getSettingsComponentName(PackageManager paramPackageManager, ResolveInfo paramResolveInfo)
  {
    if ((paramResolveInfo == null) || (paramResolveInfo.serviceInfo == null) || (paramResolveInfo.serviceInfo.metaData == null));
    String str;
    do
    {
      while (true)
      {
        return null;
        str = null;
        XmlResourceParser localXmlResourceParser = null;
        try
        {
          localXmlResourceParser = paramResolveInfo.serviceInfo.loadXmlMetaData(paramPackageManager, "android.service.dream");
          str = null;
          if (localXmlResourceParser == null)
          {
            Log.w(TAG, "No android.service.dream meta-data");
            return null;
          }
          Resources localResources = paramPackageManager.getResourcesForApplication(paramResolveInfo.serviceInfo.applicationInfo);
          AttributeSet localAttributeSet = Xml.asAttributeSet(localXmlResourceParser);
          int i;
          do
            i = localXmlResourceParser.next();
          while ((i != 1) && (i != 2));
          boolean bool = "dream".equals(localXmlResourceParser.getName());
          str = null;
          if (!bool)
          {
            Log.w(TAG, "Meta-data does not start with dream tag");
            return null;
          }
          TypedArray localTypedArray = localResources.obtainAttributes(localAttributeSet, R.styleable.Dream);
          str = localTypedArray.getString(0);
          localTypedArray.recycle();
          localObject2 = null;
          if (localXmlResourceParser != null)
            localXmlResourceParser.close();
          if (localObject2 != null)
          {
            Log.w(TAG, "Error parsing : " + paramResolveInfo.serviceInfo.packageName, (Throwable)localObject2);
            return null;
          }
        }
        catch (PackageManager.NameNotFoundException localNameNotFoundException)
        {
          while (true)
          {
            localObject2 = localNameNotFoundException;
            if (localXmlResourceParser != null)
              localXmlResourceParser.close();
          }
        }
        catch (IOException localIOException)
        {
          while (true)
          {
            localObject2 = localIOException;
            if (localXmlResourceParser != null)
              localXmlResourceParser.close();
          }
        }
        catch (XmlPullParserException localXmlPullParserException)
        {
          while (true)
          {
            Object localObject2 = localXmlPullParserException;
            if (localXmlResourceParser != null)
              localXmlResourceParser.close();
          }
        }
        finally
        {
          if (localXmlResourceParser != null)
            localXmlResourceParser.close();
        }
      }
      if ((str != null) && (str.indexOf('/') < 0))
        str = paramResolveInfo.serviceInfo.packageName + "/" + str;
    }
    while (str == null);
    return ComponentName.unflattenFromString(str);
  }

  private static void logd(String paramString, Object[] paramArrayOfObject)
  {
  }

  private void setBoolean(String paramString, boolean paramBoolean)
  {
    ContentResolver localContentResolver = this.mContext.getContentResolver();
    if (paramBoolean);
    for (int i = 1; ; i = 0)
    {
      Settings.Secure.putInt(localContentResolver, paramString, i);
      return;
    }
  }

  public ComponentName getActiveDream()
  {
    if (this.mDreamManager == null);
    while (true)
    {
      return null;
      try
      {
        ComponentName[] arrayOfComponentName = this.mDreamManager.getDreamComponents();
        if ((arrayOfComponentName != null) && (arrayOfComponentName.length > 0))
        {
          ComponentName localComponentName = arrayOfComponentName[0];
          return localComponentName;
        }
      }
      catch (RemoteException localRemoteException)
      {
        Log.w(TAG, "Failed to get active dream", localRemoteException);
      }
    }
    return null;
  }

  public CharSequence getActiveDreamName()
  {
    ComponentName localComponentName = getActiveDream();
    Object localObject = null;
    PackageManager localPackageManager;
    if (localComponentName != null)
      localPackageManager = this.mContext.getPackageManager();
    try
    {
      ServiceInfo localServiceInfo = localPackageManager.getServiceInfo(localComponentName, 0);
      localObject = null;
      if (localServiceInfo != null)
      {
        CharSequence localCharSequence = localServiceInfo.loadLabel(localPackageManager);
        localObject = localCharSequence;
      }
      return localObject;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
    }
    return null;
  }

  public ComponentName getDefaultDream()
  {
    if (this.mDreamManager == null)
      return null;
    try
    {
      ComponentName localComponentName = this.mDreamManager.getDefaultDreamComponent();
      return localComponentName;
    }
    catch (RemoteException localRemoteException)
    {
      Log.w(TAG, "Failed to get default dream", localRemoteException);
    }
    return null;
  }

  public List<DreamInfo> getDreamInfos()
  {
    logd("getDreamInfos()", new Object[0]);
    ComponentName localComponentName = getActiveDream();
    PackageManager localPackageManager = this.mContext.getPackageManager();
    List localList = localPackageManager.queryIntentServices(new Intent("android.service.dreams.DreamService"), 128);
    ArrayList localArrayList = new ArrayList(localList.size());
    Iterator localIterator = localList.iterator();
    while (localIterator.hasNext())
    {
      ResolveInfo localResolveInfo = (ResolveInfo)localIterator.next();
      if (localResolveInfo.serviceInfo != null)
      {
        DreamInfo localDreamInfo = new DreamInfo();
        localDreamInfo.caption = localResolveInfo.loadLabel(localPackageManager);
        localDreamInfo.icon = localResolveInfo.loadIcon(localPackageManager);
        localDreamInfo.componentName = getDreamComponentName(localResolveInfo);
        localDreamInfo.isActive = localDreamInfo.componentName.equals(localComponentName);
        localDreamInfo.settingsComponentName = getSettingsComponentName(localPackageManager, localResolveInfo);
        localArrayList.add(localDreamInfo);
      }
    }
    Collections.sort(localArrayList, this.mComparator);
    return localArrayList;
  }

  public boolean isActivatedOnDock()
  {
    return getBoolean("screensaver_activate_on_dock", this.mDreamsActivatedOnDockByDefault);
  }

  public boolean isActivatedOnSleep()
  {
    return getBoolean("screensaver_activate_on_sleep", this.mDreamsActivatedOnSleepByDefault);
  }

  public boolean isEnabled()
  {
    return getBoolean("screensaver_enabled", this.mDreamsEnabledByDefault);
  }

  public void launchSettings(DreamInfo paramDreamInfo)
  {
    logd("launchSettings(%s)", new Object[] { paramDreamInfo });
    if ((paramDreamInfo == null) || (paramDreamInfo.settingsComponentName == null))
      return;
    this.mContext.startActivity(new Intent().setComponent(paramDreamInfo.settingsComponentName));
  }

  public void setActivatedOnDock(boolean paramBoolean)
  {
    Object[] arrayOfObject = new Object[1];
    arrayOfObject[0] = Boolean.valueOf(paramBoolean);
    logd("setActivatedOnDock(%s)", arrayOfObject);
    setBoolean("screensaver_activate_on_dock", paramBoolean);
  }

  public void setActivatedOnSleep(boolean paramBoolean)
  {
    Object[] arrayOfObject = new Object[1];
    arrayOfObject[0] = Boolean.valueOf(paramBoolean);
    logd("setActivatedOnSleep(%s)", arrayOfObject);
    setBoolean("screensaver_activate_on_sleep", paramBoolean);
  }

  public void setActiveDream(ComponentName paramComponentName)
  {
    logd("setActiveDream(%s)", new Object[] { paramComponentName });
    if (this.mDreamManager == null)
      return;
    try
    {
      ComponentName[] arrayOfComponentName = { paramComponentName };
      IDreamManager localIDreamManager = this.mDreamManager;
      if (paramComponentName == null)
        arrayOfComponentName = null;
      localIDreamManager.setDreamComponents(arrayOfComponentName);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.w(TAG, "Failed to set active dream to " + paramComponentName, localRemoteException);
    }
  }

  public void setEnabled(boolean paramBoolean)
  {
    Object[] arrayOfObject = new Object[1];
    arrayOfObject[0] = Boolean.valueOf(paramBoolean);
    logd("setEnabled(%s)", arrayOfObject);
    setBoolean("screensaver_enabled", paramBoolean);
  }

  public void startDreaming()
  {
    logd("startDreaming()", new Object[0]);
    if (this.mDreamManager == null)
      return;
    try
    {
      this.mDreamManager.dream();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.w(TAG, "Failed to dream", localRemoteException);
    }
  }

  public static class DreamInfo
  {
    CharSequence caption;
    public ComponentName componentName;
    Drawable icon;
    boolean isActive;
    public ComponentName settingsComponentName;

    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder(DreamInfo.class.getSimpleName());
      localStringBuilder.append('[').append(this.caption);
      if (this.isActive)
        localStringBuilder.append(",active");
      localStringBuilder.append(',').append(this.componentName);
      if (this.settingsComponentName != null)
        localStringBuilder.append("settings=").append(this.settingsComponentName);
      return ']';
    }
  }

  private static class DreamInfoComparator
    implements Comparator<DreamBackend.DreamInfo>
  {
    private final ComponentName mDefaultDream;

    public DreamInfoComparator(ComponentName paramComponentName)
    {
      this.mDefaultDream = paramComponentName;
    }

    private String sortKey(DreamBackend.DreamInfo paramDreamInfo)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      if (paramDreamInfo.componentName.equals(this.mDefaultDream));
      for (char c = '0'; ; c = '1')
      {
        localStringBuilder.append(c);
        localStringBuilder.append(paramDreamInfo.caption);
        return localStringBuilder.toString();
      }
    }

    public int compare(DreamBackend.DreamInfo paramDreamInfo1, DreamBackend.DreamInfo paramDreamInfo2)
    {
      return sortKey(paramDreamInfo1).compareTo(sortKey(paramDreamInfo2));
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.DreamBackend
 * JD-Core Version:    0.6.2
 */