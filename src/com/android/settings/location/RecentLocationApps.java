package com.android.settings.location;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.AppOpsManager.OpEntry;
import android.app.AppOpsManager.PackageOps;
import android.content.ContextWrapper;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.UserHandle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;
import com.android.settings.applications.InstalledAppDetails;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RecentLocationApps
{
  private static final String TAG = RecentLocationApps.class.getSimpleName();
  private final PreferenceActivity mActivity;
  private final PackageManager mPackageManager;

  public RecentLocationApps(PreferenceActivity paramPreferenceActivity)
  {
    this.mActivity = paramPreferenceActivity;
    this.mPackageManager = paramPreferenceActivity.getPackageManager();
  }

  private Preference createRecentLocationEntry(Drawable paramDrawable, CharSequence paramCharSequence, boolean paramBoolean, Preference.OnPreferenceClickListener paramOnPreferenceClickListener)
  {
    Preference localPreference = new Preference(this.mActivity);
    localPreference.setIcon(paramDrawable);
    localPreference.setTitle(paramCharSequence);
    if (paramBoolean)
      localPreference.setSummary(2131428276);
    while (true)
    {
      localPreference.setOnPreferenceClickListener(paramOnPreferenceClickListener);
      return localPreference;
      localPreference.setSummary(2131428277);
    }
  }

  private Preference getPreferenceFromOps(long paramLong, AppOpsManager.PackageOps paramPackageOps)
  {
    String str = paramPackageOps.getPackageName();
    List localList = paramPackageOps.getOps();
    boolean bool = false;
    int i = 0;
    long l = paramLong - 900000L;
    Iterator localIterator = localList.iterator();
    while (localIterator.hasNext())
    {
      AppOpsManager.OpEntry localOpEntry = (AppOpsManager.OpEntry)localIterator.next();
      if ((localOpEntry.isRunning()) || (localOpEntry.getTime() >= l))
        switch (localOpEntry.getOp())
        {
        default:
          break;
        case 41:
          i = 1;
          break;
        case 42:
          bool = true;
        }
    }
    if ((!bool) && (i == 0))
      if (Log.isLoggable(TAG, 2))
        Log.v(TAG, str + " hadn't used location within the time interval.");
    while (true)
    {
      return null;
      try
      {
        ApplicationInfo localApplicationInfo = this.mPackageManager.getApplicationInfo(str, 128);
        if (localApplicationInfo.uid == paramPackageOps.getUid())
          return createRecentLocationEntry(this.mPackageManager.getApplicationIcon(localApplicationInfo), this.mPackageManager.getApplicationLabel(localApplicationInfo), bool, new PackageEntryClickedListener(str));
        if (Log.isLoggable(TAG, 2))
        {
          Log.v(TAG, "package " + str + " with Uid " + paramPackageOps.getUid() + " belongs to another inactive account, ignored.");
          return null;
        }
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException)
      {
        Log.wtf(TAG, "Package not found: " + str, localNameNotFoundException);
      }
    }
    return null;
  }

  public List<Preference> getAppList()
  {
    List localList = ((AppOpsManager)this.mActivity.getSystemService("appops")).getPackagesForOps(new int[] { 41, 42 });
    ArrayList localArrayList = new ArrayList();
    long l = System.currentTimeMillis();
    Iterator localIterator = localList.iterator();
    label146: 
    while (localIterator.hasNext())
    {
      AppOpsManager.PackageOps localPackageOps = (AppOpsManager.PackageOps)localIterator.next();
      int i = localPackageOps.getUid();
      if ((i == 1000) && ("android".equals(localPackageOps.getPackageName())));
      for (int j = 1; ; j = 0)
      {
        if ((j != 0) || (ActivityManager.getCurrentUser() != UserHandle.getUserId(i)))
          break label146;
        Preference localPreference = getPreferenceFromOps(l, localPackageOps);
        if (localPreference == null)
          break;
        localArrayList.add(localPreference);
        break;
      }
    }
    return localArrayList;
  }

  private class PackageEntryClickedListener
    implements Preference.OnPreferenceClickListener
  {
    private String mPackage;

    public PackageEntryClickedListener(String arg2)
    {
      Object localObject;
      this.mPackage = localObject;
    }

    public boolean onPreferenceClick(Preference paramPreference)
    {
      Bundle localBundle = new Bundle();
      localBundle.putString("package", this.mPackage);
      RecentLocationApps.this.mActivity.startPreferencePanel(InstalledAppDetails.class.getName(), localBundle, 2131428365, null, null, 0);
      return true;
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.location.RecentLocationApps
 * JD-Core Version:    0.6.2
 */