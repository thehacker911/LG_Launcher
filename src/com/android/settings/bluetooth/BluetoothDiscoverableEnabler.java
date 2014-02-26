package com.android.settings.bluetooth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.SystemProperties;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.Log;

final class BluetoothDiscoverableEnabler
  implements Preference.OnPreferenceClickListener
{
  private final Context mContext;
  private boolean mDiscoverable;
  private final Preference mDiscoveryPreference;
  private final LocalBluetoothAdapter mLocalAdapter;
  private int mNumberOfPairedDevices;
  private final BroadcastReceiver mReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if ("android.bluetooth.adapter.action.SCAN_MODE_CHANGED".equals(paramAnonymousIntent.getAction()))
      {
        int i = paramAnonymousIntent.getIntExtra("android.bluetooth.adapter.extra.SCAN_MODE", -2147483648);
        if (i != -2147483648)
          BluetoothDiscoverableEnabler.this.handleModeChanged(i);
      }
    }
  };
  private final SharedPreferences mSharedPreferences;
  private int mTimeoutSecs = -1;
  private final Handler mUiHandler;
  private final Runnable mUpdateCountdownSummaryRunnable = new Runnable()
  {
    public void run()
    {
      BluetoothDiscoverableEnabler.this.updateCountdownSummary();
    }
  };

  BluetoothDiscoverableEnabler(Context paramContext, LocalBluetoothAdapter paramLocalBluetoothAdapter, Preference paramPreference)
  {
    this.mContext = paramContext;
    this.mUiHandler = new Handler();
    this.mLocalAdapter = paramLocalBluetoothAdapter;
    this.mDiscoveryPreference = paramPreference;
    this.mSharedPreferences = paramPreference.getSharedPreferences();
    paramPreference.setPersistent(false);
  }

  private static String formatTimeRemaining(int paramInt)
  {
    StringBuilder localStringBuilder = new StringBuilder(6);
    int i = paramInt / 60;
    localStringBuilder.append(i).append(':');
    int j = paramInt - i * 60;
    if (j < 10)
      localStringBuilder.append('0');
    localStringBuilder.append(j);
    return localStringBuilder.toString();
  }

  private int getDiscoverableTimeout()
  {
    if (this.mTimeoutSecs != -1)
      return this.mTimeoutSecs;
    int i = SystemProperties.getInt("debug.bt.discoverable_time", -1);
    String str;
    if (i < 0)
    {
      str = this.mSharedPreferences.getString("bt_discoverable_timeout", "twomin");
      if (!str.equals("never"))
        break label56;
      i = 0;
    }
    while (true)
    {
      this.mTimeoutSecs = i;
      return i;
      label56: if (str.equals("onehour"))
        i = 3600;
      else if (str.equals("fivemin"))
        i = 300;
      else
        i = 120;
    }
  }

  private void setEnabled(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      int i = getDiscoverableTimeout();
      long l = System.currentTimeMillis() + 1000L * i;
      LocalBluetoothPreferences.persistDiscoverableEndTimestamp(this.mContext, l);
      this.mLocalAdapter.setScanMode(23, i);
      updateCountdownSummary();
      Log.d("BluetoothDiscoverableEnabler", "setEnabled(): enabled = " + paramBoolean + "timeout = " + i);
      if (i > 0)
        BluetoothDiscoverableTimeoutReceiver.setDiscoverableAlarm(this.mContext, l);
      return;
    }
    this.mLocalAdapter.setScanMode(21);
    BluetoothDiscoverableTimeoutReceiver.cancelDiscoverableAlarm(this.mContext);
  }

  private void setSummaryNotDiscoverable()
  {
    if (this.mNumberOfPairedDevices != 0)
    {
      this.mDiscoveryPreference.setSummary(2131427425);
      return;
    }
    this.mDiscoveryPreference.setSummary(2131427424);
  }

  private void updateCountdownSummary()
  {
    if (this.mLocalAdapter.getScanMode() != 23)
      return;
    long l1 = System.currentTimeMillis();
    long l2 = LocalBluetoothPreferences.getDiscoverableEndTimestamp(this.mContext);
    if (l1 > l2)
    {
      updateTimerDisplay(0);
      return;
    }
    updateTimerDisplay((int)((l2 - l1) / 1000L));
    try
    {
      this.mUiHandler.removeCallbacks(this.mUpdateCountdownSummaryRunnable);
      this.mUiHandler.postDelayed(this.mUpdateCountdownSummaryRunnable, 1000L);
      return;
    }
    finally
    {
    }
  }

  private void updateTimerDisplay(int paramInt)
  {
    if (getDiscoverableTimeout() == 0)
    {
      this.mDiscoveryPreference.setSummary(2131427423);
      return;
    }
    String str = formatTimeRemaining(paramInt);
    this.mDiscoveryPreference.setSummary(this.mContext.getString(2131427422, new Object[] { str }));
  }

  int getDiscoverableTimeoutIndex()
  {
    switch (getDiscoverableTimeout())
    {
    default:
      return 0;
    case 300:
      return 1;
    case 3600:
      return 2;
    case 0:
    }
    return 3;
  }

  void handleModeChanged(int paramInt)
  {
    Log.d("BluetoothDiscoverableEnabler", "handleModeChanged(): mode = " + paramInt);
    if (paramInt == 23)
    {
      this.mDiscoverable = true;
      updateCountdownSummary();
      return;
    }
    this.mDiscoverable = false;
    setSummaryNotDiscoverable();
  }

  public boolean onPreferenceClick(Preference paramPreference)
  {
    if (!this.mDiscoverable);
    for (boolean bool = true; ; bool = false)
    {
      this.mDiscoverable = bool;
      setEnabled(this.mDiscoverable);
      return true;
    }
  }

  public void pause()
  {
    if (this.mLocalAdapter == null)
      return;
    this.mUiHandler.removeCallbacks(this.mUpdateCountdownSummaryRunnable);
    this.mContext.unregisterReceiver(this.mReceiver);
    this.mDiscoveryPreference.setOnPreferenceClickListener(null);
  }

  public void resume()
  {
    if (this.mLocalAdapter == null)
      return;
    IntentFilter localIntentFilter = new IntentFilter("android.bluetooth.adapter.action.SCAN_MODE_CHANGED");
    this.mContext.registerReceiver(this.mReceiver, localIntentFilter);
    this.mDiscoveryPreference.setOnPreferenceClickListener(this);
    handleModeChanged(this.mLocalAdapter.getScanMode());
  }

  void setDiscoverableTimeout(int paramInt)
  {
    String str;
    switch (paramInt)
    {
    default:
      this.mTimeoutSecs = 120;
      str = "twomin";
    case 1:
    case 2:
    case 3:
    }
    while (true)
    {
      this.mSharedPreferences.edit().putString("bt_discoverable_timeout", str).apply();
      setEnabled(true);
      return;
      this.mTimeoutSecs = 300;
      str = "fivemin";
      continue;
      this.mTimeoutSecs = 3600;
      str = "onehour";
      continue;
      this.mTimeoutSecs = 0;
      str = "never";
    }
  }

  void setNumberOfPairedDevices(int paramInt)
  {
    this.mNumberOfPairedDevices = paramInt;
    handleModeChanged(this.mLocalAdapter.getScanMode());
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.bluetooth.BluetoothDiscoverableEnabler
 * JD-Core Version:    0.6.2
 */