package com.android.settings;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import java.util.Iterator;
import java.util.List;

public class WallpaperTypeSettings extends SettingsPreferenceFragment
{
  private void populateWallpaperTypes()
  {
    Intent localIntent1 = new Intent("android.intent.action.SET_WALLPAPER");
    PackageManager localPackageManager = getPackageManager();
    List localList = localPackageManager.queryIntentActivities(localIntent1, 65536);
    PreferenceScreen localPreferenceScreen = getPreferenceScreen();
    localPreferenceScreen.setOrderingAsAdded(false);
    Iterator localIterator = localList.iterator();
    while (localIterator.hasNext())
    {
      ResolveInfo localResolveInfo = (ResolveInfo)localIterator.next();
      Preference localPreference = new Preference(getActivity());
      Intent localIntent2 = new Intent(localIntent1);
      localIntent2.setComponent(new ComponentName(localResolveInfo.activityInfo.packageName, localResolveInfo.activityInfo.name));
      localPreference.setIntent(localIntent2);
      Object localObject = localResolveInfo.loadLabel(localPackageManager);
      if (localObject == null)
        localObject = localResolveInfo.activityInfo.packageName;
      localPreference.setTitle((CharSequence)localObject);
      localPreferenceScreen.addPreference(localPreference);
    }
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131034170);
    populateWallpaperTypes();
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.WallpaperTypeSettings
 * JD-Core Version:    0.6.2
 */