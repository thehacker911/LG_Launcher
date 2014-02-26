package com.android.settings.bluetooth;

import android.app.QueuedWork;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.Log;
import java.util.concurrent.ExecutorService;

final class LocalBluetoothPreferences
{
  static long getDiscoverableEndTimestamp(Context paramContext)
  {
    return getSharedPreferences(paramContext).getLong("discoverable_end_timestamp", 0L);
  }

  static boolean getDockAutoConnectSetting(Context paramContext, String paramString)
  {
    return getSharedPreferences(paramContext).getBoolean("auto_connect_to_dock" + paramString, false);
  }

  private static SharedPreferences getSharedPreferences(Context paramContext)
  {
    return paramContext.getSharedPreferences("bluetooth_settings", 0);
  }

  static boolean hasDockAutoConnectSetting(Context paramContext, String paramString)
  {
    return getSharedPreferences(paramContext).contains("auto_connect_to_dock" + paramString);
  }

  static void persistDiscoverableEndTimestamp(Context paramContext, long paramLong)
  {
    SharedPreferences.Editor localEditor = getSharedPreferences(paramContext).edit();
    localEditor.putLong("discoverable_end_timestamp", paramLong);
    localEditor.apply();
  }

  static void persistDiscoveringTimestamp(Context paramContext)
  {
    QueuedWork.singleThreadExecutor().submit(new Runnable()
    {
      public void run()
      {
        SharedPreferences.Editor localEditor = LocalBluetoothPreferences.getSharedPreferences(this.val$context).edit();
        localEditor.putLong("last_discovering_time", System.currentTimeMillis());
        localEditor.apply();
      }
    });
  }

  static void persistSelectedDeviceInPicker(Context paramContext, String paramString)
  {
    SharedPreferences.Editor localEditor = getSharedPreferences(paramContext).edit();
    localEditor.putString("last_selected_device", paramString);
    localEditor.putLong("last_selected_device_time", System.currentTimeMillis());
    localEditor.apply();
  }

  static void removeDockAutoConnectSetting(Context paramContext, String paramString)
  {
    SharedPreferences.Editor localEditor = getSharedPreferences(paramContext).edit();
    localEditor.remove("auto_connect_to_dock" + paramString);
    localEditor.apply();
  }

  static void saveDockAutoConnectSetting(Context paramContext, String paramString, boolean paramBoolean)
  {
    SharedPreferences.Editor localEditor = getSharedPreferences(paramContext).edit();
    localEditor.putBoolean("auto_connect_to_dock" + paramString, paramBoolean);
    localEditor.apply();
  }

  static boolean shouldShowDialogInForeground(Context paramContext, String paramString)
  {
    LocalBluetoothManager localLocalBluetoothManager = LocalBluetoothManager.getInstance(paramContext);
    if (localLocalBluetoothManager == null)
    {
      Log.v("LocalBluetoothPreferences", "manager == null - do not show dialog.");
      return false;
    }
    if (localLocalBluetoothManager.isForegroundActivity())
      return true;
    if ((0x5 & paramContext.getResources().getConfiguration().uiMode) == 5)
    {
      Log.v("LocalBluetoothPreferences", "in appliance mode - do not show dialog.");
      return false;
    }
    long l = System.currentTimeMillis();
    SharedPreferences localSharedPreferences = getSharedPreferences(paramContext);
    if (60000L + localSharedPreferences.getLong("discoverable_end_timestamp", 0L) > l)
      return true;
    LocalBluetoothAdapter localLocalBluetoothAdapter = localLocalBluetoothManager.getBluetoothAdapter();
    if ((localLocalBluetoothAdapter != null) && (localLocalBluetoothAdapter.isDiscovering()))
      return true;
    if (60000L + localSharedPreferences.getLong("last_discovering_time", 0L) > l)
      return true;
    if ((paramString != null) && (paramString.equals(localSharedPreferences.getString("last_selected_device", null))) && (60000L + localSharedPreferences.getLong("last_selected_device_time", 0L) > l))
      return true;
    Log.v("LocalBluetoothPreferences", "Found no reason to show the dialog - do not show dialog.");
    return false;
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.bluetooth.LocalBluetoothPreferences
 * JD-Core Version:    0.6.2
 */