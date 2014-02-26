package com.android.settings.bluetooth;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BluetoothDiscoverableTimeoutReceiver extends BroadcastReceiver
{
  static void cancelDiscoverableAlarm(Context paramContext)
  {
    Log.d("BluetoothDiscoverableTimeoutReceiver", "cancelDiscoverableAlarm(): Enter");
    Intent localIntent = new Intent("android.bluetooth.intent.DISCOVERABLE_TIMEOUT");
    localIntent.setClass(paramContext, BluetoothDiscoverableTimeoutReceiver.class);
    PendingIntent localPendingIntent = PendingIntent.getBroadcast(paramContext, 0, localIntent, 536870912);
    if (localPendingIntent != null)
      ((AlarmManager)paramContext.getSystemService("alarm")).cancel(localPendingIntent);
  }

  static void setDiscoverableAlarm(Context paramContext, long paramLong)
  {
    Log.d("BluetoothDiscoverableTimeoutReceiver", "setDiscoverableAlarm(): alarmTime = " + paramLong);
    Intent localIntent = new Intent("android.bluetooth.intent.DISCOVERABLE_TIMEOUT");
    localIntent.setClass(paramContext, BluetoothDiscoverableTimeoutReceiver.class);
    PendingIntent localPendingIntent = PendingIntent.getBroadcast(paramContext, 0, localIntent, 0);
    AlarmManager localAlarmManager = (AlarmManager)paramContext.getSystemService("alarm");
    if (localPendingIntent != null)
    {
      localAlarmManager.cancel(localPendingIntent);
      Log.d("BluetoothDiscoverableTimeoutReceiver", "setDiscoverableAlarm(): cancel prev alarm");
    }
    localAlarmManager.set(0, paramLong, PendingIntent.getBroadcast(paramContext, 0, localIntent, 0));
  }

  public void onReceive(Context paramContext, Intent paramIntent)
  {
    LocalBluetoothAdapter localLocalBluetoothAdapter = LocalBluetoothAdapter.getInstance();
    if ((localLocalBluetoothAdapter != null) && (localLocalBluetoothAdapter.getState() == 12))
    {
      Log.d("BluetoothDiscoverableTimeoutReceiver", "Disable discoverable...");
      localLocalBluetoothAdapter.setScanMode(21);
      return;
    }
    Log.e("BluetoothDiscoverableTimeoutReceiver", "localBluetoothAdapter is NULL!!");
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.bluetooth.BluetoothDiscoverableTimeoutReceiver
 * JD-Core Version:    0.6.2
 */