package com.android.settings.wifi;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.TwoStatePreference;
import android.provider.Settings.Global;
import android.provider.Settings.SettingNotFoundException;
import java.util.ArrayList;

public class WifiApEnabler
{
  private final CheckBoxPreference mCheckBox;
  ConnectivityManager mCm;
  private final Context mContext;
  private final IntentFilter mIntentFilter;
  private final CharSequence mOriginalSummary;
  private final BroadcastReceiver mReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      String str = paramAnonymousIntent.getAction();
      if ("android.net.wifi.WIFI_AP_STATE_CHANGED".equals(str))
        WifiApEnabler.this.handleWifiApStateChanged(paramAnonymousIntent.getIntExtra("wifi_state", 14));
      do
      {
        return;
        if ("android.net.conn.TETHER_STATE_CHANGED".equals(str))
        {
          ArrayList localArrayList1 = paramAnonymousIntent.getStringArrayListExtra("availableArray");
          ArrayList localArrayList2 = paramAnonymousIntent.getStringArrayListExtra("activeArray");
          ArrayList localArrayList3 = paramAnonymousIntent.getStringArrayListExtra("erroredArray");
          WifiApEnabler.this.updateTetherState(localArrayList1.toArray(), localArrayList2.toArray(), localArrayList3.toArray());
          return;
        }
      }
      while (!"android.intent.action.AIRPLANE_MODE".equals(str));
      WifiApEnabler.this.enableWifiCheckBox();
    }
  };
  private WifiManager mWifiManager;
  private String[] mWifiRegexs;

  public WifiApEnabler(Context paramContext, CheckBoxPreference paramCheckBoxPreference)
  {
    this.mContext = paramContext;
    this.mCheckBox = paramCheckBoxPreference;
    this.mOriginalSummary = paramCheckBoxPreference.getSummary();
    paramCheckBoxPreference.setPersistent(false);
    this.mWifiManager = ((WifiManager)paramContext.getSystemService("wifi"));
    this.mCm = ((ConnectivityManager)this.mContext.getSystemService("connectivity"));
    this.mWifiRegexs = this.mCm.getTetherableWifiRegexs();
    this.mIntentFilter = new IntentFilter("android.net.wifi.WIFI_AP_STATE_CHANGED");
    this.mIntentFilter.addAction("android.net.conn.TETHER_STATE_CHANGED");
    this.mIntentFilter.addAction("android.intent.action.AIRPLANE_MODE");
  }

  private void enableWifiCheckBox()
  {
    if (Settings.Global.getInt(this.mContext.getContentResolver(), "airplane_mode_on", 0) != 0);
    for (int i = 1; i == 0; i = 0)
    {
      this.mCheckBox.setEnabled(true);
      return;
    }
    this.mCheckBox.setSummary(this.mOriginalSummary);
    this.mCheckBox.setEnabled(false);
  }

  private void handleWifiApStateChanged(int paramInt)
  {
    switch (paramInt)
    {
    default:
      this.mCheckBox.setChecked(false);
      this.mCheckBox.setSummary(2131427817);
      enableWifiCheckBox();
      return;
    case 12:
      this.mCheckBox.setSummary(2131427959);
      this.mCheckBox.setEnabled(false);
      return;
    case 13:
      this.mCheckBox.setChecked(true);
      this.mCheckBox.setEnabled(true);
      return;
    case 10:
      this.mCheckBox.setSummary(2131427960);
      this.mCheckBox.setEnabled(false);
      return;
    case 11:
    }
    this.mCheckBox.setChecked(false);
    this.mCheckBox.setSummary(this.mOriginalSummary);
    enableWifiCheckBox();
  }

  private void updateTetherState(Object[] paramArrayOfObject1, Object[] paramArrayOfObject2, Object[] paramArrayOfObject3)
  {
    int i = 0;
    int j = 0;
    int k = paramArrayOfObject2.length;
    for (int m = 0; m < k; m++)
    {
      String str2 = (String)paramArrayOfObject2[m];
      String[] arrayOfString2 = this.mWifiRegexs;
      int i4 = arrayOfString2.length;
      for (int i5 = 0; i5 < i4; i5++)
        if (str2.matches(arrayOfString2[i5]))
          i = 1;
    }
    int n = paramArrayOfObject3.length;
    for (int i1 = 0; i1 < n; i1++)
    {
      String str1 = (String)paramArrayOfObject3[i1];
      String[] arrayOfString1 = this.mWifiRegexs;
      int i2 = arrayOfString1.length;
      for (int i3 = 0; i3 < i2; i3++)
        if (str1.matches(arrayOfString1[i3]))
          j = 1;
    }
    if (i != 0)
      updateConfigSummary(this.mWifiManager.getWifiApConfiguration());
    while (j == 0)
      return;
    this.mCheckBox.setSummary(2131427817);
  }

  public void pause()
  {
    this.mContext.unregisterReceiver(this.mReceiver);
  }

  public void resume()
  {
    this.mContext.registerReceiver(this.mReceiver, this.mIntentFilter);
    enableWifiCheckBox();
  }

  public void setSoftapEnabled(boolean paramBoolean)
  {
    ContentResolver localContentResolver = this.mContext.getContentResolver();
    int i = this.mWifiManager.getWifiState();
    if ((paramBoolean) && ((i == 2) || (i == 3)))
    {
      this.mWifiManager.setWifiEnabled(false);
      Settings.Global.putInt(localContentResolver, "wifi_saved_state", 1);
    }
    if (this.mWifiManager.setWifiApEnabled(null, paramBoolean))
      this.mCheckBox.setEnabled(false);
    while (true)
    {
      if (!paramBoolean);
      try
      {
        int k = Settings.Global.getInt(localContentResolver, "wifi_saved_state");
        j = k;
        if (j == 1)
        {
          this.mWifiManager.setWifiEnabled(true);
          Settings.Global.putInt(localContentResolver, "wifi_saved_state", 0);
        }
        return;
        this.mCheckBox.setSummary(2131427817);
      }
      catch (Settings.SettingNotFoundException localSettingNotFoundException)
      {
        while (true)
          int j = 0;
      }
    }
  }

  public void updateConfigSummary(WifiConfiguration paramWifiConfiguration)
  {
    String str1 = this.mContext.getString(17040437);
    CheckBoxPreference localCheckBoxPreference = this.mCheckBox;
    String str2 = this.mContext.getString(2131427961);
    Object[] arrayOfObject = new Object[1];
    if (paramWifiConfiguration == null);
    while (true)
    {
      arrayOfObject[0] = str1;
      localCheckBoxPreference.setSummary(String.format(str2, arrayOfObject));
      return;
      str1 = paramWifiConfiguration.SSID;
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.wifi.WifiApEnabler
 * JD-Core Version:    0.6.2
 */