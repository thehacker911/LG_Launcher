package com.android.settings.bluetooth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public final class BluetoothDiscoveryReceiver extends BroadcastReceiver
{
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    String str = paramIntent.getAction();
    Log.v("BluetoothDiscoveryReceiver", "Received: " + str);
    if ((str.equals("android.bluetooth.adapter.action.DISCOVERY_STARTED")) || (str.equals("android.bluetooth.adapter.action.DISCOVERY_FINISHED")))
      LocalBluetoothPreferences.persistDiscoveringTimestamp(paramContext);
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.bluetooth.BluetoothDiscoveryReceiver
 * JD-Core Version:    0.6.2
 */