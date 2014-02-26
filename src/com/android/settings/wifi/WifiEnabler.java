package com.android.settings.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.Toast;
import com.android.settings.WirelessSettings;
import java.util.concurrent.atomic.AtomicBoolean;

public class WifiEnabler
  implements CompoundButton.OnCheckedChangeListener
{
  private AtomicBoolean mConnected = new AtomicBoolean(false);
  private final Context mContext;
  private final IntentFilter mIntentFilter;
  private final BroadcastReceiver mReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      String str = paramAnonymousIntent.getAction();
      if ("android.net.wifi.WIFI_STATE_CHANGED".equals(str))
        WifiEnabler.this.handleWifiStateChanged(paramAnonymousIntent.getIntExtra("wifi_state", 4));
      do
      {
        do
        {
          return;
          if (!"android.net.wifi.supplicant.STATE_CHANGE".equals(str))
            break;
        }
        while (WifiEnabler.this.mConnected.get());
        WifiEnabler.this.handleStateChanged(WifiInfo.getDetailedStateOf((SupplicantState)paramAnonymousIntent.getParcelableExtra("newState")));
        return;
      }
      while (!"android.net.wifi.STATE_CHANGE".equals(str));
      NetworkInfo localNetworkInfo = (NetworkInfo)paramAnonymousIntent.getParcelableExtra("networkInfo");
      WifiEnabler.this.mConnected.set(localNetworkInfo.isConnected());
      WifiEnabler.this.handleStateChanged(localNetworkInfo.getDetailedState());
    }
  };
  private boolean mStateMachineEvent;
  private Switch mSwitch;
  private final WifiManager mWifiManager;

  public WifiEnabler(Context paramContext, Switch paramSwitch)
  {
    this.mContext = paramContext;
    this.mSwitch = paramSwitch;
    this.mWifiManager = ((WifiManager)paramContext.getSystemService("wifi"));
    this.mIntentFilter = new IntentFilter("android.net.wifi.WIFI_STATE_CHANGED");
    this.mIntentFilter.addAction("android.net.wifi.supplicant.STATE_CHANGE");
    this.mIntentFilter.addAction("android.net.wifi.STATE_CHANGE");
  }

  private void handleStateChanged(NetworkInfo.DetailedState paramDetailedState)
  {
  }

  private void handleWifiStateChanged(int paramInt)
  {
    switch (paramInt)
    {
    default:
      setSwitchChecked(false);
      this.mSwitch.setEnabled(true);
      return;
    case 2:
      this.mSwitch.setEnabled(false);
      return;
    case 3:
      setSwitchChecked(true);
      this.mSwitch.setEnabled(true);
      return;
    case 0:
      this.mSwitch.setEnabled(false);
      return;
    case 1:
    }
    setSwitchChecked(false);
    this.mSwitch.setEnabled(true);
  }

  private void setSwitchChecked(boolean paramBoolean)
  {
    if (paramBoolean != this.mSwitch.isChecked())
    {
      this.mStateMachineEvent = true;
      this.mSwitch.setChecked(paramBoolean);
      this.mStateMachineEvent = false;
    }
  }

  public void onCheckedChanged(CompoundButton paramCompoundButton, boolean paramBoolean)
  {
    if (this.mStateMachineEvent);
    do
    {
      return;
      if ((paramBoolean) && (!WirelessSettings.isRadioAllowed(this.mContext, "wifi")))
      {
        Toast.makeText(this.mContext, 2131427818, 0).show();
        paramCompoundButton.setChecked(false);
        return;
      }
      int i = this.mWifiManager.getWifiApState();
      if ((paramBoolean) && ((i == 12) || (i == 13)))
        this.mWifiManager.setWifiApEnabled(null, false);
      this.mSwitch.setEnabled(false);
    }
    while (this.mWifiManager.setWifiEnabled(paramBoolean));
    this.mSwitch.setEnabled(true);
    Toast.makeText(this.mContext, 2131427817, 0).show();
  }

  public void pause()
  {
    this.mContext.unregisterReceiver(this.mReceiver);
    this.mSwitch.setOnCheckedChangeListener(null);
  }

  public void resume()
  {
    this.mContext.registerReceiver(this.mReceiver, this.mIntentFilter);
    this.mSwitch.setOnCheckedChangeListener(this);
  }

  public void setSwitch(Switch paramSwitch)
  {
    if (this.mSwitch == paramSwitch)
      return;
    this.mSwitch.setOnCheckedChangeListener(null);
    this.mSwitch = paramSwitch;
    this.mSwitch.setOnCheckedChangeListener(this);
    int i = this.mWifiManager.getWifiState();
    boolean bool1;
    if (i == 3)
    {
      bool1 = true;
      if (i != 1)
        break label95;
    }
    label95: for (int j = 1; ; j = 0)
    {
      this.mSwitch.setChecked(bool1);
      Switch localSwitch = this.mSwitch;
      boolean bool2;
      if (!bool1)
      {
        bool2 = false;
        if (j == 0);
      }
      else
      {
        bool2 = true;
      }
      localSwitch.setEnabled(bool2);
      return;
      bool1 = false;
      break;
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.wifi.WifiEnabler
 * JD-Core Version:    0.6.2
 */