package com.android.settings.location;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class LocationSettings extends LocationSettingsBase
  implements CompoundButton.OnCheckedChangeListener
{
  private PreferenceCategory mCategoryRecentLocationRequests;
  private Preference mLocationMode;
  private BroadcastReceiver mReceiver;
  private Switch mSwitch;
  private boolean mValidListener = false;

  private void addLocationServices(Context paramContext, PreferenceScreen paramPreferenceScreen)
  {
    PreferenceCategory localPreferenceCategory = (PreferenceCategory)paramPreferenceScreen.findPreference("location_services");
    final SettingsInjector localSettingsInjector = new SettingsInjector(paramContext);
    List localList = localSettingsInjector.getInjectedSettings();
    this.mReceiver = new BroadcastReceiver()
    {
      public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
      {
        if (Log.isLoggable("LocationSettings", 3))
          Log.d("LocationSettings", "Received settings change intent: " + paramAnonymousIntent);
        localSettingsInjector.reloadStatusMessages();
      }
    };
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.location.InjectedSettingChanged");
    localIntentFilter.addAction("android.location.MODE_CHANGED");
    paramContext.registerReceiver(this.mReceiver, localIntentFilter);
    if (localList.size() > 0)
    {
      addPreferencesSorted(localList, localPreferenceCategory);
      return;
    }
    paramPreferenceScreen.removePreference(localPreferenceCategory);
  }

  private void addPreferencesSorted(List<Preference> paramList, PreferenceGroup paramPreferenceGroup)
  {
    Collections.sort(paramList, new Comparator()
    {
      public int compare(Preference paramAnonymousPreference1, Preference paramAnonymousPreference2)
      {
        return paramAnonymousPreference1.getTitle().toString().compareTo(paramAnonymousPreference2.getTitle().toString());
      }
    });
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
      paramPreferenceGroup.addPreference((Preference)localIterator.next());
  }

  private PreferenceScreen createPreferenceHierarchy()
  {
    final PreferenceActivity localPreferenceActivity = (PreferenceActivity)getActivity();
    PreferenceScreen localPreferenceScreen1 = getPreferenceScreen();
    if (localPreferenceScreen1 != null)
      localPreferenceScreen1.removeAll();
    addPreferencesFromResource(2131034136);
    PreferenceScreen localPreferenceScreen2 = getPreferenceScreen();
    this.mLocationMode = localPreferenceScreen2.findPreference("location_mode");
    this.mLocationMode.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
    {
      public boolean onPreferenceClick(Preference paramAnonymousPreference)
      {
        localPreferenceActivity.startPreferencePanel(LocationMode.class.getName(), null, 2131428278, null, LocationSettings.this, 0);
        return true;
      }
    });
    this.mCategoryRecentLocationRequests = ((PreferenceCategory)localPreferenceScreen2.findPreference("recent_location_requests"));
    List localList = new RecentLocationApps(localPreferenceActivity).getAppList();
    if (localList.size() > 0)
      addPreferencesSorted(localList, this.mCategoryRecentLocationRequests);
    while (true)
    {
      addLocationServices(localPreferenceActivity, localPreferenceScreen2);
      if ((localPreferenceActivity.onIsHidingHeaders()) || (!localPreferenceActivity.onIsMultiPane()))
      {
        int i = localPreferenceActivity.getResources().getDimensionPixelSize(2131558402);
        this.mSwitch.setPaddingRelative(0, 0, i, 0);
        localPreferenceActivity.getActionBar().setDisplayOptions(16, 16);
        localPreferenceActivity.getActionBar().setCustomView(this.mSwitch, new ActionBar.LayoutParams(-2, -2, 8388629));
      }
      setHasOptionsMenu(true);
      refreshLocationMode();
      return localPreferenceScreen2;
      Preference localPreference = new Preference(localPreferenceActivity);
      localPreference.setLayoutResource(2130968641);
      localPreference.setTitle(2131428274);
      localPreference.setSelectable(false);
      this.mCategoryRecentLocationRequests.addPreference(localPreference);
    }
  }

  public int getHelpResource()
  {
    return 2131429267;
  }

  public void onCheckedChanged(CompoundButton paramCompoundButton, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      setLocationMode(3);
      return;
    }
    setLocationMode(0);
  }

  public void onModeChanged(int paramInt, boolean paramBoolean)
  {
    boolean bool1 = true;
    boolean bool2;
    label39: boolean bool3;
    label52: Preference localPreference;
    switch (paramInt)
    {
    default:
      if (paramInt != 0)
      {
        bool2 = bool1;
        Switch localSwitch = this.mSwitch;
        if (paramBoolean)
          break label199;
        bool3 = bool1;
        localSwitch.setEnabled(bool3);
        localPreference = this.mLocationMode;
        if ((!bool2) || (paramBoolean))
          break label205;
      }
      break;
    case 0:
    case 1:
    case 2:
    case 3:
    }
    while (true)
    {
      localPreference.setEnabled(bool1);
      this.mCategoryRecentLocationRequests.setEnabled(bool2);
      if (bool2 != this.mSwitch.isChecked())
      {
        if (this.mValidListener)
          this.mSwitch.setOnCheckedChangeListener(null);
        this.mSwitch.setChecked(bool2);
        if (this.mValidListener)
          this.mSwitch.setOnCheckedChangeListener(this);
      }
      return;
      this.mLocationMode.setSummary(2131428272);
      break;
      this.mLocationMode.setSummary(2131428271);
      break;
      this.mLocationMode.setSummary(2131428270);
      break;
      this.mLocationMode.setSummary(2131428269);
      break;
      bool2 = false;
      break label39;
      label199: bool3 = false;
      break label52;
      label205: bool1 = false;
    }
  }

  public void onPause()
  {
    try
    {
      getActivity().unregisterReceiver(this.mReceiver);
      label11: super.onPause();
      this.mValidListener = false;
      this.mSwitch.setOnCheckedChangeListener(null);
      return;
    }
    catch (RuntimeException localRuntimeException)
    {
      break label11;
    }
  }

  public void onResume()
  {
    super.onResume();
    this.mSwitch = new Switch(getActivity());
    this.mSwitch.setOnCheckedChangeListener(this);
    this.mValidListener = true;
    createPreferenceHierarchy();
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.location.LocationSettings
 * JD-Core Version:    0.6.2
 */