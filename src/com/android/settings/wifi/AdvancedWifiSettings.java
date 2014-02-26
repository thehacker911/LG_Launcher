package com.android.settings.wifi;

import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.preference.TwoStatePreference;
import android.provider.Settings.Global;
import android.text.TextUtils;
import android.util.Log;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

public class AdvancedWifiSettings extends SettingsPreferenceFragment
  implements Preference.OnPreferenceChangeListener
{
  private WifiManager mWifiManager;

  private void initPreferences()
  {
    int i = 1;
    CheckBoxPreference localCheckBoxPreference1 = (CheckBoxPreference)findPreference("notify_open_networks");
    CheckBoxPreference localCheckBoxPreference2;
    label108: ListPreference localListPreference1;
    if (Settings.Global.getInt(getContentResolver(), "wifi_networks_available_notification_on", 0) == i)
    {
      int k = i;
      localCheckBoxPreference1.setChecked(k);
      localCheckBoxPreference1.setEnabled(this.mWifiManager.isWifiEnabled());
      localCheckBoxPreference2 = (CheckBoxPreference)findPreference("wifi_poor_network_detection");
      if (localCheckBoxPreference2 != null)
      {
        if (!Utils.isWifiOnly(getActivity()))
          break label324;
        getPreferenceScreen().removePreference(localCheckBoxPreference2);
      }
      CheckBoxPreference localCheckBoxPreference3 = (CheckBoxPreference)findPreference("wifi_scan_always_available");
      if (Settings.Global.getInt(getContentResolver(), "wifi_scan_always_enabled", 0) != i)
        break label357;
      int n = i;
      localCheckBoxPreference3.setChecked(n);
      Intent localIntent = new Intent("android.credentials.INSTALL_AS_USER");
      localIntent.setClassName("com.android.certinstaller", "com.android.certinstaller.CertInstallerMain");
      localIntent.putExtra("install_as_uid", 1010);
      findPreference("install_credentials").setIntent(localIntent);
      CheckBoxPreference localCheckBoxPreference4 = (CheckBoxPreference)findPreference("suspend_optimizations");
      if (Settings.Global.getInt(getContentResolver(), "wifi_suspend_optimizations_enabled", i) != i)
        break label363;
      label183: localCheckBoxPreference4.setChecked(i);
      localListPreference1 = (ListPreference)findPreference("frequency_band");
      if (!this.mWifiManager.isDualBandSupported())
        break label379;
      localListPreference1.setOnPreferenceChangeListener(this);
      int i2 = this.mWifiManager.getFrequencyBand();
      if (i2 == -1)
        break label368;
      localListPreference1.setValue(String.valueOf(i2));
      updateFrequencyBandSummary(localListPreference1, i2);
    }
    while (true)
    {
      ListPreference localListPreference2 = (ListPreference)findPreference("sleep_policy");
      if (localListPreference2 != null)
      {
        if (Utils.isWifiOnly(getActivity()))
          localListPreference2.setEntries(2131165211);
        localListPreference2.setOnPreferenceChangeListener(this);
        String str = String.valueOf(Settings.Global.getInt(getContentResolver(), "wifi_sleep_policy", 2));
        localListPreference2.setValue(str);
        updateSleepPolicySummary(localListPreference2, str);
      }
      return;
      int m = 0;
      break;
      label324: if (Settings.Global.getInt(getContentResolver(), "wifi_watchdog_poor_network_test_enabled", 0) == i);
      int i4;
      for (int i3 = i; ; i4 = 0)
      {
        localCheckBoxPreference2.setChecked(i3);
        break;
      }
      label357: int i1 = 0;
      break label108;
      label363: int j = 0;
      break label183;
      label368: Log.e("AdvancedWifiSettings", "Failed to fetch frequency band");
      continue;
      label379: if (localListPreference1 != null)
        getPreferenceScreen().removePreference(localListPreference1);
    }
  }

  private void refreshWifiInfo()
  {
    WifiInfo localWifiInfo = this.mWifiManager.getConnectionInfo();
    Preference localPreference1 = findPreference("mac_address");
    Object localObject;
    if (localWifiInfo == null)
    {
      localObject = null;
      if (TextUtils.isEmpty((CharSequence)localObject))
        break label82;
    }
    while (true)
    {
      localPreference1.setSummary((CharSequence)localObject);
      Preference localPreference2 = findPreference("current_ip_address");
      String str = Utils.getWifiIpAddresses(getActivity());
      if (str == null)
        str = getActivity().getString(2131428124);
      localPreference2.setSummary(str);
      return;
      localObject = localWifiInfo.getMacAddress();
      break;
      label82: localObject = getActivity().getString(2131428124);
    }
  }

  private void updateFrequencyBandSummary(Preference paramPreference, int paramInt)
  {
    paramPreference.setSummary(getResources().getStringArray(2131165213)[paramInt]);
  }

  private void updateSleepPolicySummary(Preference paramPreference, String paramString)
  {
    if (paramString != null)
    {
      String[] arrayOfString1 = getResources().getStringArray(2131165212);
      int i;
      String[] arrayOfString2;
      if (Utils.isWifiOnly(getActivity()))
      {
        i = 2131165211;
        arrayOfString2 = getResources().getStringArray(i);
      }
      for (int j = 0; ; j++)
      {
        if (j >= arrayOfString1.length)
          break label94;
        if ((paramString.equals(arrayOfString1[j])) && (j < arrayOfString2.length))
        {
          paramPreference.setSummary(arrayOfString2[j]);
          return;
          i = 2131165210;
          break;
        }
      }
    }
    label94: paramPreference.setSummary("");
    Log.e("AdvancedWifiSettings", "Invalid sleep policy value: " + paramString);
  }

  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    this.mWifiManager = ((WifiManager)getSystemService("wifi"));
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131034173);
  }

  // ERROR //
  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokevirtual 256	android/preference/Preference:getKey	()Ljava/lang/String;
    //   4: astore_3
    //   5: ldc 115
    //   7: aload_3
    //   8: invokevirtual 217	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   11: ifeq +29 -> 40
    //   14: aload_2
    //   15: checkcast 130	java/lang/String
    //   18: invokestatic 262	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   21: istore 8
    //   23: aload_0
    //   24: getfield 43	com/android/settings/wifi/AdvancedWifiSettings:mWifiManager	Landroid/net/wifi/WifiManager;
    //   27: iload 8
    //   29: iconst_1
    //   30: invokevirtual 266	android/net/wifi/WifiManager:setFrequencyBand	(IZ)V
    //   33: aload_0
    //   34: aload_1
    //   35: iload 8
    //   37: invokespecial 141	com/android/settings/wifi/AdvancedWifiSettings:updateFrequencyBandSummary	(Landroid/preference/Preference;I)V
    //   40: ldc 143
    //   42: aload_3
    //   43: invokevirtual 217	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   46: ifeq +31 -> 77
    //   49: aload_2
    //   50: checkcast 130	java/lang/String
    //   53: astore 5
    //   55: aload_0
    //   56: invokevirtual 27	com/android/settings/SettingsPreferenceFragment:getContentResolver	()Landroid/content/ContentResolver;
    //   59: ldc 150
    //   61: aload 5
    //   63: invokestatic 262	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   66: invokestatic 270	android/provider/Settings$Global:putInt	(Landroid/content/ContentResolver;Ljava/lang/String;I)Z
    //   69: pop
    //   70: aload_0
    //   71: aload_1
    //   72: aload 5
    //   74: invokespecial 154	com/android/settings/wifi/AdvancedWifiSettings:updateSleepPolicySummary	(Landroid/preference/Preference;Ljava/lang/String;)V
    //   77: iconst_1
    //   78: ireturn
    //   79: astore 7
    //   81: aload_0
    //   82: invokevirtual 62	android/app/Fragment:getActivity	()Landroid/app/Activity;
    //   85: ldc_w 271
    //   88: iconst_0
    //   89: invokestatic 277	android/widget/Toast:makeText	(Landroid/content/Context;II)Landroid/widget/Toast;
    //   92: invokevirtual 280	android/widget/Toast:show	()V
    //   95: iconst_0
    //   96: ireturn
    //   97: astore 4
    //   99: aload_0
    //   100: invokevirtual 62	android/app/Fragment:getActivity	()Landroid/app/Activity;
    //   103: ldc_w 281
    //   106: iconst_0
    //   107: invokestatic 277	android/widget/Toast:makeText	(Landroid/content/Context;II)Landroid/widget/Toast;
    //   110: invokevirtual 280	android/widget/Toast:show	()V
    //   113: iconst_0
    //   114: ireturn
    //
    // Exception table:
    //   from	to	target	type
    //   14	40	79	java/lang/NumberFormatException
    //   49	77	97	java/lang/NumberFormatException
  }

  public boolean onPreferenceTreeClick(PreferenceScreen paramPreferenceScreen, Preference paramPreference)
  {
    String str = paramPreference.getKey();
    if ("notify_open_networks".equals(str))
    {
      ContentResolver localContentResolver4 = getContentResolver();
      boolean bool4 = ((CheckBoxPreference)paramPreference).isChecked();
      int m = 0;
      if (bool4)
        m = 1;
      Settings.Global.putInt(localContentResolver4, "wifi_networks_available_notification_on", m);
      return true;
    }
    if ("wifi_poor_network_detection".equals(str))
    {
      ContentResolver localContentResolver3 = getContentResolver();
      boolean bool3 = ((CheckBoxPreference)paramPreference).isChecked();
      int k = 0;
      if (bool3)
        k = 1;
      Settings.Global.putInt(localContentResolver3, "wifi_watchdog_poor_network_test_enabled", k);
      return true;
    }
    if ("suspend_optimizations".equals(str))
    {
      ContentResolver localContentResolver2 = getContentResolver();
      boolean bool2 = ((CheckBoxPreference)paramPreference).isChecked();
      int j = 0;
      if (bool2)
        j = 1;
      Settings.Global.putInt(localContentResolver2, "wifi_suspend_optimizations_enabled", j);
      return true;
    }
    if ("wifi_scan_always_available".equals(str))
    {
      ContentResolver localContentResolver1 = getContentResolver();
      boolean bool1 = ((CheckBoxPreference)paramPreference).isChecked();
      int i = 0;
      if (bool1)
        i = 1;
      Settings.Global.putInt(localContentResolver1, "wifi_scan_always_enabled", i);
      return true;
    }
    return super.onPreferenceTreeClick(paramPreferenceScreen, paramPreference);
  }

  public void onResume()
  {
    super.onResume();
    initPreferences();
    refreshWifiInfo();
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.wifi.AdvancedWifiSettings
 * JD-Core Version:    0.6.2
 */