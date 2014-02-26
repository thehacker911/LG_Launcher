package com.android.settings.wifi.p2p;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pGroup;
import android.preference.Preference;
import android.view.View;

public class WifiP2pPersistentGroup extends Preference
{
  public WifiP2pGroup mGroup;

  public WifiP2pPersistentGroup(Context paramContext, WifiP2pGroup paramWifiP2pGroup)
  {
    super(paramContext);
    this.mGroup = paramWifiP2pGroup;
  }

  String getGroupName()
  {
    return this.mGroup.getNetworkName();
  }

  int getNetworkId()
  {
    return this.mGroup.getNetworkId();
  }

  protected void onBindView(View paramView)
  {
    setTitle(this.mGroup.getNetworkName());
    super.onBindView(paramView);
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.wifi.p2p.WifiP2pPersistentGroup
 * JD-Core Version:    0.6.2
 */