package com.android.settings.net;

import android.app.Activity;
import android.content.Context;
import android.net.NetworkPolicy;
import android.net.NetworkPolicyManager;
import android.net.NetworkTemplate;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import com.android.settings.DataUsageSummary;
import com.android.settings.SettingsPreferenceFragment;
import java.util.Iterator;
import java.util.List;

public class DataUsageMeteredSettings extends SettingsPreferenceFragment
{
  private PreferenceCategory mMobileCategory;
  private NetworkPolicyEditor mPolicyEditor;
  private NetworkPolicyManager mPolicyManager;
  private PreferenceCategory mWifiCategory;
  private Preference mWifiDisabled;
  private WifiManager mWifiManager;

  private Preference buildWifiPref(Context paramContext, WifiConfiguration paramWifiConfiguration)
  {
    String str = paramWifiConfiguration.SSID;
    MeteredPreference localMeteredPreference = new MeteredPreference(paramContext, NetworkTemplate.buildTemplateWifi(str));
    localMeteredPreference.setTitle(WifiInfo.removeDoubleQuotes(str));
    return localMeteredPreference;
  }

  private void updateNetworks(Context paramContext)
  {
    getPreferenceScreen().removePreference(this.mMobileCategory);
    this.mWifiCategory.removeAll();
    Iterator localIterator;
    if ((DataUsageSummary.hasWifiRadio(paramContext)) && (this.mWifiManager.isWifiEnabled()))
      localIterator = this.mWifiManager.getConfiguredNetworks().iterator();
    while (localIterator.hasNext())
    {
      WifiConfiguration localWifiConfiguration = (WifiConfiguration)localIterator.next();
      if (localWifiConfiguration.SSID != null)
      {
        this.mWifiCategory.addPreference(buildWifiPref(paramContext, localWifiConfiguration));
        continue;
        this.mWifiCategory.addPreference(this.mWifiDisabled);
      }
    }
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    Activity localActivity = getActivity();
    this.mPolicyManager = NetworkPolicyManager.from(localActivity);
    this.mWifiManager = ((WifiManager)localActivity.getSystemService("wifi"));
    this.mPolicyEditor = new NetworkPolicyEditor(this.mPolicyManager);
    this.mPolicyEditor.read();
    addPreferencesFromResource(2131034123);
    this.mMobileCategory = ((PreferenceCategory)findPreference("mobile"));
    this.mWifiCategory = ((PreferenceCategory)findPreference("wifi"));
    this.mWifiDisabled = findPreference("wifi_disabled");
    updateNetworks(localActivity);
  }

  private class MeteredPreference extends CheckBoxPreference
  {
    private boolean mBinding;
    private final NetworkTemplate mTemplate;

    public MeteredPreference(Context paramNetworkTemplate, NetworkTemplate arg3)
    {
      super();
      NetworkTemplate localNetworkTemplate;
      this.mTemplate = localNetworkTemplate;
      setPersistent(false);
      this.mBinding = true;
      NetworkPolicy localNetworkPolicy = DataUsageMeteredSettings.this.mPolicyEditor.getPolicyMaybeUnquoted(localNetworkTemplate);
      if (localNetworkPolicy != null)
        if (localNetworkPolicy.limitBytes != -1L)
        {
          setChecked(true);
          setEnabled(false);
        }
      while (true)
      {
        this.mBinding = false;
        return;
        setChecked(localNetworkPolicy.metered);
        continue;
        setChecked(false);
      }
    }

    protected void notifyChanged()
    {
      super.notifyChanged();
      if (!this.mBinding)
        DataUsageMeteredSettings.this.mPolicyEditor.setPolicyMetered(this.mTemplate, isChecked());
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.net.DataUsageMeteredSettings
 * JD-Core Version:    0.6.2
 */