package com.android.settings.wifi.p2p;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.preference.Preference;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

public class WifiP2pPeer extends Preference
{
  private static final int[] STATE_SECURED = { 2130771968 };
  public WifiP2pDevice device;
  private final int mRssi;
  private ImageView mSignal;

  public WifiP2pPeer(Context paramContext, WifiP2pDevice paramWifiP2pDevice)
  {
    super(paramContext);
    this.device = paramWifiP2pDevice;
    setWidgetLayoutResource(2130968690);
    this.mRssi = 60;
  }

  private void refresh()
  {
    if (this.mSignal == null)
      return;
    Context localContext = getContext();
    this.mSignal.setImageLevel(getLevel());
    setSummary(localContext.getResources().getStringArray(2131165207)[this.device.status]);
  }

  public int compareTo(Preference paramPreference)
  {
    if (!(paramPreference instanceof WifiP2pPeer));
    WifiP2pPeer localWifiP2pPeer;
    do
    {
      return 1;
      localWifiP2pPeer = (WifiP2pPeer)paramPreference;
      if (this.device.status == localWifiP2pPeer.device.status)
        break;
    }
    while (this.device.status >= localWifiP2pPeer.device.status);
    return -1;
    if (this.device.deviceName != null)
      return this.device.deviceName.compareToIgnoreCase(localWifiP2pPeer.device.deviceName);
    return this.device.deviceAddress.compareToIgnoreCase(localWifiP2pPeer.device.deviceAddress);
  }

  int getLevel()
  {
    if (this.mRssi == 2147483647)
      return -1;
    return WifiManager.calculateSignalLevel(this.mRssi, 4);
  }

  protected void onBindView(View paramView)
  {
    if (TextUtils.isEmpty(this.device.deviceName))
    {
      setTitle(this.device.deviceAddress);
      this.mSignal = ((ImageView)paramView.findViewById(2131230982));
      if (this.mRssi != 2147483647)
        break label78;
      this.mSignal.setImageDrawable(null);
    }
    while (true)
    {
      refresh();
      super.onBindView(paramView);
      return;
      setTitle(this.device.deviceName);
      break;
      label78: this.mSignal.setImageResource(2130837693);
      this.mSignal.setImageState(STATE_SECURED, true);
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.wifi.p2p.WifiP2pPeer
 * JD-Core Version:    0.6.2
 */