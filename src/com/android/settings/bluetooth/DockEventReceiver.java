package com.android.settings.bluetooth;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

public final class DockEventReceiver extends BroadcastReceiver
{
  private static PowerManager.WakeLock sStartingService;
  private static final Object sStartingServiceSync = new Object();

  private static void beginStartingService(Context paramContext, Intent paramIntent)
  {
    synchronized (sStartingServiceSync)
    {
      if (sStartingService == null)
        sStartingService = ((PowerManager)paramContext.getSystemService("power")).newWakeLock(1, "StartingDockService");
      sStartingService.acquire();
      if (paramContext.startService(paramIntent) == null)
        Log.e("DockEventReceiver", "Can't start DockService");
      return;
    }
  }

  public static void finishStartingService(Service paramService, int paramInt)
  {
    synchronized (sStartingServiceSync)
    {
      if ((sStartingService != null) && (paramService.stopSelfResult(paramInt)))
      {
        Log.d("DockEventReceiver", "finishStartingService: stopping service");
        sStartingService.release();
      }
      return;
    }
  }

  public void onReceive(Context paramContext, Intent paramIntent)
  {
    if (paramIntent == null);
    do
    {
      BluetoothDevice localBluetoothDevice;
      int j;
      int k;
      do
      {
        int i;
        do
        {
          return;
          i = paramIntent.getIntExtra("android.intent.extra.DOCK_STATE", paramIntent.getIntExtra("android.bluetooth.adapter.extra.STATE", -1234));
          localBluetoothDevice = (BluetoothDevice)paramIntent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
          if ((!"android.intent.action.DOCK_EVENT".equals(paramIntent.getAction())) && (!"com.android.settings.bluetooth.action.DOCK_SHOW_UI".endsWith(paramIntent.getAction())))
            break;
        }
        while ((localBluetoothDevice == null) && (("com.android.settings.bluetooth.action.DOCK_SHOW_UI".endsWith(paramIntent.getAction())) || ((i != 0) && (i != 3))));
        switch (i)
        {
        default:
          Log.e("DockEventReceiver", "Unknown state: " + i);
          return;
        case 0:
        case 1:
        case 2:
        case 3:
        case 4:
        }
        Intent localIntent1 = new Intent(paramIntent);
        localIntent1.setClass(paramContext, DockService.class);
        beginStartingService(paramContext, localIntent1);
        return;
        if ((!"android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED".equals(paramIntent.getAction())) && (!"android.bluetooth.a2dp.profile.action.CONNECTION_STATE_CHANGED".equals(paramIntent.getAction())))
          break;
        j = paramIntent.getIntExtra("android.bluetooth.profile.extra.STATE", 2);
        k = paramIntent.getIntExtra("android.bluetooth.profile.extra.PREVIOUS_STATE", 0);
      }
      while ((localBluetoothDevice == null) || (j != 0) || (k == 3));
      Intent localIntent2 = new Intent(paramIntent);
      localIntent2.setClass(paramContext, DockService.class);
      beginStartingService(paramContext, localIntent2);
      return;
    }
    while ((!"android.bluetooth.adapter.action.STATE_CHANGED".equals(paramIntent.getAction())) || (paramIntent.getIntExtra("android.bluetooth.adapter.extra.STATE", -2147483648) == 11));
    Intent localIntent3 = new Intent(paramIntent);
    localIntent3.setClass(paramContext, DockService.class);
    beginStartingService(paramContext, localIntent3);
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.bluetooth.DockEventReceiver
 * JD-Core Version:    0.6.2
 */