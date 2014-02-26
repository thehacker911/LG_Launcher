package com.android.settings;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import java.util.ArrayList;

public class HomeSettings extends SettingsPreferenceFragment
{
  HomeAppPreference mCurrentHome = null;
  View.OnClickListener mDeleteClickListener = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      int i = ((Integer)paramAnonymousView.getTag()).intValue();
      HomeSettings.this.uninstallApp((HomeSettings.HomeAppPreference)HomeSettings.this.mPrefs.get(i));
    }
  };
  View.OnClickListener mHomeClickListener = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      int i = ((Integer)paramAnonymousView.getTag()).intValue();
      HomeSettings.HomeAppPreference localHomeAppPreference = (HomeSettings.HomeAppPreference)HomeSettings.this.mPrefs.get(i);
      if (!localHomeAppPreference.isChecked)
        HomeSettings.this.makeCurrentHome(localHomeAppPreference);
    }
  };
  ComponentName[] mHomeComponentSet;
  final IntentFilter mHomeFilter = new IntentFilter("android.intent.action.MAIN");
  PackageManager mPm;
  PreferenceGroup mPrefGroup;
  ArrayList<HomeAppPreference> mPrefs;
  boolean mShowNotice;

  public HomeSettings()
  {
    this.mHomeFilter.addCategory("android.intent.category.HOME");
    this.mHomeFilter.addCategory("android.intent.category.DEFAULT");
  }

  void buildHomeActivitiesList()
  {
    ArrayList localArrayList = new ArrayList();
    ComponentName localComponentName1 = this.mPm.getHomeActivities(localArrayList);
    Activity localActivity = getActivity();
    this.mCurrentHome = null;
    this.mPrefGroup.removeAll();
    this.mPrefs = new ArrayList();
    this.mHomeComponentSet = new ComponentName[localArrayList.size()];
    int i = 0;
    int j = 0;
    while (true)
      if (j < localArrayList.size())
      {
        ActivityInfo localActivityInfo = ((ResolveInfo)localArrayList.get(j)).activityInfo;
        ComponentName localComponentName2 = new ComponentName(localActivityInfo.packageName, localActivityInfo.name);
        this.mHomeComponentSet[j] = localComponentName2;
        try
        {
          HomeAppPreference localHomeAppPreference = new HomeAppPreference(localActivity, localComponentName2, i, localActivityInfo.loadIcon(this.mPm), localActivityInfo.loadLabel(this.mPm), this, localActivityInfo);
          this.mPrefs.add(localHomeAppPreference);
          this.mPrefGroup.addPreference(localHomeAppPreference);
          localHomeAppPreference.setEnabled(true);
          if (localComponentName2.equals(localComponentName1))
            this.mCurrentHome = localHomeAppPreference;
          i++;
          j++;
        }
        catch (Exception localException)
        {
          while (true)
            Log.v("HomeSettings", "Problem dealing with activity " + localComponentName2, localException);
        }
      }
    if (this.mCurrentHome != null)
      new Handler().post(new Runnable()
      {
        public void run()
        {
          HomeSettings.this.mCurrentHome.setChecked(true);
        }
      });
  }

  void makeCurrentHome(HomeAppPreference paramHomeAppPreference)
  {
    if (this.mCurrentHome != null)
      this.mCurrentHome.setChecked(false);
    paramHomeAppPreference.setChecked(true);
    this.mCurrentHome = paramHomeAppPreference;
    this.mPm.replacePreferredActivity(this.mHomeFilter, 1048576, this.mHomeComponentSet, paramHomeAppPreference.activityName);
  }

  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
    buildHomeActivitiesList();
    if ((paramInt1 > 10) && (this.mCurrentHome == null));
    for (int i = 0; ; i++)
      if (i < this.mPrefs.size())
      {
        HomeAppPreference localHomeAppPreference = (HomeAppPreference)this.mPrefs.get(i);
        if (localHomeAppPreference.isSystem)
          makeCurrentHome(localHomeAppPreference);
      }
      else
      {
        if (this.mPrefs.size() < 2)
        {
          if (this.mShowNotice)
          {
            this.mShowNotice = false;
            Settings.requestHomeNotice();
          }
          finishFragment();
        }
        return;
      }
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131034132);
    this.mPm = getPackageManager();
    this.mPrefGroup = ((PreferenceGroup)findPreference("home"));
    Bundle localBundle = getArguments();
    if ((localBundle != null) && (localBundle.getBoolean("show", false)));
    for (boolean bool = true; ; bool = false)
    {
      this.mShowNotice = bool;
      return;
    }
  }

  public void onResume()
  {
    super.onResume();
    buildHomeActivitiesList();
  }

  void uninstallApp(HomeAppPreference paramHomeAppPreference)
  {
    Intent localIntent = new Intent("android.intent.action.UNINSTALL_PACKAGE", Uri.parse("package:" + paramHomeAppPreference.uninstallTarget));
    localIntent.putExtra("android.intent.extra.UNINSTALL_ALL_USERS", false);
    boolean bool = paramHomeAppPreference.isChecked;
    int i = 0;
    if (bool)
      i = 1;
    startActivityForResult(localIntent, i + 10);
  }

  class HomeAppPreference extends Preference
  {
    ComponentName activityName;
    HomeSettings fragment;
    final ColorFilter grayscaleFilter;
    int index;
    boolean isChecked;
    boolean isSystem;
    String uninstallTarget;

    public HomeAppPreference(Context paramComponentName, ComponentName paramInt, int paramDrawable, Drawable paramCharSequence, CharSequence paramHomeSettings, HomeSettings paramActivityInfo, ActivityInfo arg8)
    {
      super();
      setLayoutResource(2130968675);
      setIcon(paramCharSequence);
      setTitle(paramHomeSettings);
      this.activityName = paramInt;
      this.fragment = paramActivityInfo;
      this.index = paramDrawable;
      ColorMatrix localColorMatrix = new ColorMatrix();
      localColorMatrix.setSaturation(0.0F);
      localColorMatrix.getArray()[18] = 0.5F;
      this.grayscaleFilter = new ColorMatrixColorFilter(localColorMatrix);
      ActivityInfo localActivityInfo;
      determineTargets(localActivityInfo);
    }

    private void determineTargets(ActivityInfo paramActivityInfo)
    {
      boolean bool1 = true;
      Bundle localBundle = paramActivityInfo.metaData;
      if (localBundle != null)
      {
        String str = localBundle.getString("android.app.home.alternate");
        if (str != null)
          try
          {
            if (HomeSettings.this.mPm.checkSignatures(paramActivityInfo.packageName, str) >= 0)
            {
              PackageInfo localPackageInfo = HomeSettings.this.mPm.getPackageInfo(str, 0);
              if ((0x1 & localPackageInfo.applicationInfo.flags) != 0);
              for (boolean bool2 = bool1; ; bool2 = false)
              {
                this.isSystem = bool2;
                this.uninstallTarget = localPackageInfo.packageName;
                return;
              }
            }
          }
          catch (Exception localException)
          {
            Log.w("HomeSettings", "Unable to compare/resolve alternate", localException);
          }
      }
      if ((0x1 & paramActivityInfo.applicationInfo.flags) != 0);
      while (true)
      {
        this.isSystem = bool1;
        this.uninstallTarget = paramActivityInfo.packageName;
        return;
        bool1 = false;
      }
    }

    protected void onBindView(View paramView)
    {
      super.onBindView(paramView);
      ((RadioButton)paramView.findViewById(2131230957)).setChecked(this.isChecked);
      Integer localInteger = new Integer(this.index);
      ImageView localImageView = (ImageView)paramView.findViewById(2131230959);
      if (this.isSystem)
      {
        localImageView.setEnabled(false);
        localImageView.setColorFilter(this.grayscaleFilter);
      }
      while (true)
      {
        View localView = paramView.findViewById(2131230956);
        localView.setOnClickListener(HomeSettings.this.mHomeClickListener);
        localView.setTag(localInteger);
        return;
        localImageView.setOnClickListener(HomeSettings.this.mDeleteClickListener);
        localImageView.setTag(localInteger);
      }
    }

    void setChecked(boolean paramBoolean)
    {
      if (paramBoolean != this.isChecked)
      {
        this.isChecked = paramBoolean;
        notifyChanged();
      }
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.HomeSettings
 * JD-Core Version:    0.6.2
 */