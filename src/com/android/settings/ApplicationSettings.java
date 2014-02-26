package com.android.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;
import android.provider.Settings.System;

public class ApplicationSettings extends SettingsPreferenceFragment
{
  private ListPreference mInstallLocation;
  private CheckBoxPreference mToggleAdvancedSettings;

  private String getAppInstallLocation()
  {
    int i = Settings.Global.getInt(getContentResolver(), "default_install_location", 0);
    if (i == 1)
      return "device";
    if (i == 2)
      return "sdcard";
    if (i == 0)
      return "auto";
    return "auto";
  }

  private boolean isAdvancedSettingsEnabled()
  {
    int i = Settings.System.getInt(getContentResolver(), "advanced_settings", 0);
    boolean bool = false;
    if (i > 0)
      bool = true;
    return bool;
  }

  private void setAdvancedSettingsEnabled(boolean paramBoolean)
  {
    if (paramBoolean);
    for (int i = 1; ; i = 0)
    {
      Settings.Secure.putInt(getContentResolver(), "advanced_settings", i);
      Intent localIntent = new Intent("android.intent.action.ADVANCED_SETTINGS");
      localIntent.putExtra("state", i);
      getActivity().sendBroadcast(localIntent);
      return;
    }
  }

  protected void handleUpdateAppInstallLocation(String paramString)
  {
    if ("device".equals(paramString))
      Settings.Global.putInt(getContentResolver(), "default_install_location", 1);
    while (true)
    {
      this.mInstallLocation.setValue(paramString);
      return;
      if ("sdcard".equals(paramString))
        Settings.Global.putInt(getContentResolver(), "default_install_location", 2);
      else if ("auto".equals(paramString))
        Settings.Global.putInt(getContentResolver(), "default_install_location", 0);
      else
        Settings.Global.putInt(getContentResolver(), "default_install_location", 0);
    }
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131034118);
    this.mToggleAdvancedSettings = ((CheckBoxPreference)findPreference("toggle_advanced_settings"));
    this.mToggleAdvancedSettings.setChecked(isAdvancedSettingsEnabled());
    getPreferenceScreen().removePreference(this.mToggleAdvancedSettings);
    this.mInstallLocation = ((ListPreference)findPreference("app_install_location"));
    int i = Settings.Global.getInt(getContentResolver(), "set_install_location", 0);
    int j = 0;
    if (i != 0)
      j = 1;
    if (j == 0)
    {
      getPreferenceScreen().removePreference(this.mInstallLocation);
      return;
    }
    this.mInstallLocation.setValue(getAppInstallLocation());
    this.mInstallLocation.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
    {
      public boolean onPreferenceChange(Preference paramAnonymousPreference, Object paramAnonymousObject)
      {
        String str = (String)paramAnonymousObject;
        ApplicationSettings.this.handleUpdateAppInstallLocation(str);
        return false;
      }
    });
  }

  public boolean onPreferenceTreeClick(PreferenceScreen paramPreferenceScreen, Preference paramPreference)
  {
    if (paramPreference == this.mToggleAdvancedSettings)
      setAdvancedSettingsEnabled(this.mToggleAdvancedSettings.isChecked());
    return super.onPreferenceTreeClick(paramPreferenceScreen, paramPreference);
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.ApplicationSettings
 * JD-Core Version:    0.6.2
 */