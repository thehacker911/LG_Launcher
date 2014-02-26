package com.android.settings.wifi;

import android.content.Context;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.Preference;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import java.util.BitSet;

class AccessPoint extends Preference
{
  private static final int[] STATE_NONE = new int[0];
  private static final int[] STATE_SECURED = { 2130771968 };
  String bssid;
  private WifiConfiguration mConfig;
  private WifiInfo mInfo;
  private int mRssi;
  ScanResult mScanResult;
  private NetworkInfo.DetailedState mState;
  int networkId;
  PskType pskType = PskType.UNKNOWN;
  int security;
  String ssid;
  boolean wpsAvailable = false;

  AccessPoint(Context paramContext, ScanResult paramScanResult)
  {
    super(paramContext);
    setWidgetLayoutResource(2130968690);
    loadResult(paramScanResult);
    refresh();
  }

  AccessPoint(Context paramContext, WifiConfiguration paramWifiConfiguration)
  {
    super(paramContext);
    setWidgetLayoutResource(2130968690);
    loadConfig(paramWifiConfiguration);
    refresh();
  }

  AccessPoint(Context paramContext, Bundle paramBundle)
  {
    super(paramContext);
    setWidgetLayoutResource(2130968690);
    this.mConfig = ((WifiConfiguration)paramBundle.getParcelable("key_config"));
    if (this.mConfig != null)
      loadConfig(this.mConfig);
    this.mScanResult = ((ScanResult)paramBundle.getParcelable("key_scanresult"));
    if (this.mScanResult != null)
      loadResult(this.mScanResult);
    this.mInfo = ((WifiInfo)paramBundle.getParcelable("key_wifiinfo"));
    if (paramBundle.containsKey("key_detailedstate"))
      this.mState = NetworkInfo.DetailedState.valueOf(paramBundle.getString("key_detailedstate"));
    update(this.mInfo, this.mState);
  }

  static String convertToQuotedString(String paramString)
  {
    return "\"" + paramString + "\"";
  }

  private static PskType getPskType(ScanResult paramScanResult)
  {
    boolean bool1 = paramScanResult.capabilities.contains("WPA-PSK");
    boolean bool2 = paramScanResult.capabilities.contains("WPA2-PSK");
    if ((bool2) && (bool1))
      return PskType.WPA_WPA2;
    if (bool2)
      return PskType.WPA2;
    if (bool1)
      return PskType.WPA;
    Log.w("Settings.AccessPoint", "Received abnormal flag string: " + paramScanResult.capabilities);
    return PskType.UNKNOWN;
  }

  private static int getSecurity(ScanResult paramScanResult)
  {
    if (paramScanResult.capabilities.contains("WEP"))
      return 1;
    if (paramScanResult.capabilities.contains("PSK"))
      return 2;
    if (paramScanResult.capabilities.contains("EAP"))
      return 3;
    return 0;
  }

  static int getSecurity(WifiConfiguration paramWifiConfiguration)
  {
    int i = 1;
    if (paramWifiConfiguration.allowedKeyManagement.get(i))
      i = 2;
    do
    {
      return i;
      if ((paramWifiConfiguration.allowedKeyManagement.get(2)) || (paramWifiConfiguration.allowedKeyManagement.get(3)))
        return 3;
    }
    while (paramWifiConfiguration.wepKeys[0] != null);
    return 0;
  }

  private void loadConfig(WifiConfiguration paramWifiConfiguration)
  {
    if (paramWifiConfiguration.SSID == null);
    for (String str = ""; ; str = removeDoubleQuotes(paramWifiConfiguration.SSID))
    {
      this.ssid = str;
      this.bssid = paramWifiConfiguration.BSSID;
      this.security = getSecurity(paramWifiConfiguration);
      this.networkId = paramWifiConfiguration.networkId;
      this.mRssi = 2147483647;
      this.mConfig = paramWifiConfiguration;
      return;
    }
  }

  private void loadResult(ScanResult paramScanResult)
  {
    this.ssid = paramScanResult.SSID;
    this.bssid = paramScanResult.BSSID;
    this.security = getSecurity(paramScanResult);
    if ((this.security != 3) && (paramScanResult.capabilities.contains("WPS")));
    for (boolean bool = true; ; bool = false)
    {
      this.wpsAvailable = bool;
      if (this.security == 2)
        this.pskType = getPskType(paramScanResult);
      this.networkId = -1;
      this.mRssi = paramScanResult.level;
      this.mScanResult = paramScanResult;
      return;
    }
  }

  private void refresh()
  {
    setTitle(this.ssid);
    Context localContext = getContext();
    if ((this.mConfig != null) && (this.mConfig.status == 1))
    {
      switch (this.mConfig.disableReason)
      {
      default:
        return;
      case 3:
        setSummary(localContext.getString(2131427887));
        return;
      case 1:
      case 2:
        setSummary(localContext.getString(2131427886));
        return;
      case 0:
      }
      setSummary(localContext.getString(2131427885));
      return;
    }
    if (this.mRssi == 2147483647)
    {
      setSummary(localContext.getString(2131427888));
      return;
    }
    if (this.mState != null)
    {
      setSummary(Summary.get(localContext, this.mState));
      return;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    if (this.mConfig != null)
      localStringBuilder.append(localContext.getString(2131427884));
    String str;
    if (this.security != 0)
    {
      if (localStringBuilder.length() == 0)
      {
        str = localContext.getString(2131427891);
        Object[] arrayOfObject = new Object[1];
        arrayOfObject[0] = getSecurityString(true);
        localStringBuilder.append(String.format(str, arrayOfObject));
      }
    }
    else if ((this.mConfig == null) && (this.wpsAvailable))
    {
      if (localStringBuilder.length() != 0)
        break label272;
      localStringBuilder.append(localContext.getString(2131427889));
    }
    while (true)
    {
      setSummary(localStringBuilder.toString());
      return;
      str = localContext.getString(2131427892);
      break;
      label272: localStringBuilder.append(localContext.getString(2131427890));
    }
  }

  static String removeDoubleQuotes(String paramString)
  {
    int i = paramString.length();
    if ((i > 1) && (paramString.charAt(0) == '"') && (paramString.charAt(i - 1) == '"'))
      paramString = paramString.substring(1, i - 1);
    return paramString;
  }

  public int compareTo(Preference paramPreference)
  {
    int i = 1;
    if (!(paramPreference instanceof AccessPoint));
    AccessPoint localAccessPoint;
    do
    {
      do
      {
        do
        {
          do
          {
            return i;
            localAccessPoint = (AccessPoint)paramPreference;
            if ((this.mInfo != null) && (localAccessPoint.mInfo == null))
              return -1;
          }
          while ((this.mInfo == null) && (localAccessPoint.mInfo != null));
          if ((this.mRssi != 2147483647) && (localAccessPoint.mRssi == 2147483647))
            return -1;
        }
        while ((this.mRssi == 2147483647) && (localAccessPoint.mRssi != 2147483647));
        if ((this.networkId != -1) && (localAccessPoint.networkId == -1))
          return -1;
      }
      while ((this.networkId == -1) && (localAccessPoint.networkId != -1));
      i = WifiManager.compareSignalLevel(localAccessPoint.mRssi, this.mRssi);
    }
    while (i != 0);
    return this.ssid.compareToIgnoreCase(localAccessPoint.ssid);
  }

  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof AccessPoint));
    while (compareTo((AccessPoint)paramObject) != 0)
      return false;
    return true;
  }

  protected void generateOpenNetworkConfig()
  {
    if (this.security != 0)
      throw new IllegalStateException();
    if (this.mConfig != null)
      return;
    this.mConfig = new WifiConfiguration();
    this.mConfig.SSID = convertToQuotedString(this.ssid);
    this.mConfig.allowedKeyManagement.set(0);
  }

  WifiConfiguration getConfig()
  {
    return this.mConfig;
  }

  WifiInfo getInfo()
  {
    return this.mInfo;
  }

  int getLevel()
  {
    if (this.mRssi == 2147483647)
      return -1;
    return WifiManager.calculateSignalLevel(this.mRssi, 4);
  }

  public String getSecurityString(boolean paramBoolean)
  {
    Context localContext = getContext();
    switch (this.security)
    {
    default:
      if (paramBoolean)
        return "";
      break;
    case 3:
      if (paramBoolean)
        return localContext.getString(2131427898);
      return localContext.getString(2131427905);
    case 2:
      switch (1.$SwitchMap$com$android$settings$wifi$AccessPoint$PskType[this.pskType.ordinal()])
      {
      default:
        if (paramBoolean)
          return localContext.getString(2131427897);
        break;
      case 1:
        if (paramBoolean)
          return localContext.getString(2131427894);
        return localContext.getString(2131427901);
      case 2:
        if (paramBoolean)
          return localContext.getString(2131427895);
        return localContext.getString(2131427902);
      case 3:
        if (paramBoolean)
          return localContext.getString(2131427896);
        return localContext.getString(2131427903);
      }
      return localContext.getString(2131427904);
    case 1:
      if (paramBoolean)
        return localContext.getString(2131427893);
      return localContext.getString(2131427900);
    }
    return localContext.getString(2131427899);
  }

  NetworkInfo.DetailedState getState()
  {
    return this.mState;
  }

  public int hashCode()
  {
    WifiInfo localWifiInfo = this.mInfo;
    int i = 0;
    if (localWifiInfo != null)
      i = 0 + 13 * this.mInfo.hashCode();
    return i + 19 * this.mRssi + 23 * this.networkId + 29 * this.ssid.hashCode();
  }

  protected void onBindView(View paramView)
  {
    super.onBindView(paramView);
    ImageView localImageView = (ImageView)paramView.findViewById(2131230982);
    if (this.mRssi == 2147483647)
    {
      localImageView.setImageDrawable(null);
      return;
    }
    localImageView.setImageLevel(getLevel());
    localImageView.setImageDrawable(getContext().getTheme().obtainStyledAttributes(new int[] { 2130771991 }).getDrawable(0));
    if (this.security != 0);
    for (int[] arrayOfInt = STATE_SECURED; ; arrayOfInt = STATE_NONE)
    {
      localImageView.setImageState(arrayOfInt, true);
      return;
    }
  }

  public void saveWifiState(Bundle paramBundle)
  {
    paramBundle.putParcelable("key_config", this.mConfig);
    paramBundle.putParcelable("key_scanresult", this.mScanResult);
    paramBundle.putParcelable("key_wifiinfo", this.mInfo);
    if (this.mState != null)
      paramBundle.putString("key_detailedstate", this.mState.toString());
  }

  void update(WifiInfo paramWifiInfo, NetworkInfo.DetailedState paramDetailedState)
  {
    int i;
    if ((paramWifiInfo != null) && (this.networkId != -1) && (this.networkId == paramWifiInfo.getNetworkId()))
      if (this.mInfo == null)
      {
        i = 1;
        this.mRssi = paramWifiInfo.getRssi();
        this.mInfo = paramWifiInfo;
        this.mState = paramDetailedState;
        refresh();
      }
    while (true)
    {
      if (i != 0)
        notifyHierarchyChanged();
      return;
      i = 0;
      break;
      WifiInfo localWifiInfo = this.mInfo;
      i = 0;
      if (localWifiInfo != null)
      {
        i = 1;
        this.mInfo = null;
        this.mState = null;
        refresh();
      }
    }
  }

  boolean update(ScanResult paramScanResult)
  {
    if ((this.ssid.equals(paramScanResult.SSID)) && (this.security == getSecurity(paramScanResult)))
    {
      if (WifiManager.compareSignalLevel(paramScanResult.level, this.mRssi) > 0)
      {
        int i = getLevel();
        this.mRssi = paramScanResult.level;
        if (getLevel() != i)
          notifyChanged();
      }
      if (this.security == 2)
        this.pskType = getPskType(paramScanResult);
      refresh();
      return true;
    }
    return false;
  }

  static enum PskType
  {
    static
    {
      PskType[] arrayOfPskType = new PskType[4];
      arrayOfPskType[0] = UNKNOWN;
      arrayOfPskType[1] = WPA;
      arrayOfPskType[2] = WPA2;
      arrayOfPskType[3] = WPA_WPA2;
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.wifi.AccessPoint
 * JD-Core Version:    0.6.2
 */