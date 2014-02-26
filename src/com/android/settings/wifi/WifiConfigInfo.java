package com.android.settings.wifi;

import android.app.Activity;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.TextView;
import java.util.List;

public class WifiConfigInfo extends Activity
{
  private TextView mConfigList;
  private WifiManager mWifiManager;

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mWifiManager = ((WifiManager)getSystemService("wifi"));
    setContentView(2130968734);
    this.mConfigList = ((TextView)findViewById(2131231146));
  }

  protected void onResume()
  {
    super.onResume();
    if (this.mWifiManager.isWifiEnabled())
    {
      List localList = this.mWifiManager.getConfiguredNetworks();
      StringBuffer localStringBuffer = new StringBuffer();
      for (int i = -1 + localList.size(); i >= 0; i--)
        localStringBuffer.append(localList.get(i));
      this.mConfigList.setText(localStringBuffer);
      return;
    }
    this.mConfigList.setText(2131427988);
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.wifi.WifiConfigInfo
 * JD-Core Version:    0.6.2
 */