package com.android.settings.bluetooth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.Toast;
import com.android.settings.WirelessSettings;

public final class BluetoothEnabler
  implements CompoundButton.OnCheckedChangeListener
{
  private final Context mContext;
  private final IntentFilter mIntentFilter;
  private final LocalBluetoothAdapter mLocalAdapter;
  private final BroadcastReceiver mReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      int i = paramAnonymousIntent.getIntExtra("android.bluetooth.adapter.extra.STATE", -2147483648);
      BluetoothEnabler.this.handleStateChanged(i);
    }
  };
  private Switch mSwitch;
  private boolean mValidListener;

  public BluetoothEnabler(Context paramContext, Switch paramSwitch)
  {
    this.mContext = paramContext;
    this.mSwitch = paramSwitch;
    this.mValidListener = false;
    LocalBluetoothManager localLocalBluetoothManager = LocalBluetoothManager.getInstance(paramContext);
    if (localLocalBluetoothManager == null)
    {
      this.mLocalAdapter = null;
      this.mSwitch.setEnabled(false);
    }
    while (true)
    {
      this.mIntentFilter = new IntentFilter("android.bluetooth.adapter.action.STATE_CHANGED");
      return;
      this.mLocalAdapter = localLocalBluetoothManager.getBluetoothAdapter();
    }
  }

  private void setChecked(boolean paramBoolean)
  {
    if (paramBoolean != this.mSwitch.isChecked())
    {
      if (this.mValidListener)
        this.mSwitch.setOnCheckedChangeListener(null);
      this.mSwitch.setChecked(paramBoolean);
      if (this.mValidListener)
        this.mSwitch.setOnCheckedChangeListener(this);
    }
  }

  void handleStateChanged(int paramInt)
  {
    switch (paramInt)
    {
    default:
      setChecked(false);
      this.mSwitch.setEnabled(true);
      return;
    case 11:
      this.mSwitch.setEnabled(false);
      return;
    case 12:
      setChecked(true);
      this.mSwitch.setEnabled(true);
      return;
    case 13:
      this.mSwitch.setEnabled(false);
      return;
    case 10:
    }
    setChecked(false);
    this.mSwitch.setEnabled(true);
  }

  public void onCheckedChanged(CompoundButton paramCompoundButton, boolean paramBoolean)
  {
    if ((paramBoolean) && (!WirelessSettings.isRadioAllowed(this.mContext, "bluetooth")))
    {
      Toast.makeText(this.mContext, 2131427818, 0).show();
      paramCompoundButton.setChecked(false);
    }
    if (this.mLocalAdapter != null)
      this.mLocalAdapter.setBluetoothEnabled(paramBoolean);
    this.mSwitch.setEnabled(false);
  }

  public void pause()
  {
    if (this.mLocalAdapter == null)
      return;
    this.mContext.unregisterReceiver(this.mReceiver);
    this.mSwitch.setOnCheckedChangeListener(null);
    this.mValidListener = false;
  }

  public void resume()
  {
    if (this.mLocalAdapter == null)
    {
      this.mSwitch.setEnabled(false);
      return;
    }
    handleStateChanged(this.mLocalAdapter.getBluetoothState());
    this.mContext.registerReceiver(this.mReceiver, this.mIntentFilter);
    this.mSwitch.setOnCheckedChangeListener(this);
    this.mValidListener = true;
  }

  public void setSwitch(Switch paramSwitch)
  {
    if (this.mSwitch == paramSwitch)
      return;
    this.mSwitch.setOnCheckedChangeListener(null);
    this.mSwitch = paramSwitch;
    Switch localSwitch1 = this.mSwitch;
    boolean bool1 = this.mValidListener;
    BluetoothEnabler localBluetoothEnabler = null;
    if (bool1)
      localBluetoothEnabler = this;
    localSwitch1.setOnCheckedChangeListener(localBluetoothEnabler);
    int i = 10;
    if (this.mLocalAdapter != null)
      i = this.mLocalAdapter.getBluetoothState();
    boolean bool2;
    if (i == 12)
    {
      bool2 = true;
      if (i != 10)
        break label130;
    }
    label130: for (int j = 1; ; j = 0)
    {
      setChecked(bool2);
      Switch localSwitch2 = this.mSwitch;
      boolean bool3;
      if (!bool2)
      {
        bool3 = false;
        if (j == 0);
      }
      else
      {
        bool3 = true;
      }
      localSwitch2.setEnabled(bool3);
      return;
      bool2 = false;
      break;
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.bluetooth.BluetoothEnabler
 * JD-Core Version:    0.6.2
 */