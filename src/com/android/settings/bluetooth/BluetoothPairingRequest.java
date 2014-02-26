package com.android.settings.bluetooth;

import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.PowerManager;
import android.text.TextUtils;

public final class BluetoothPairingRequest extends BroadcastReceiver
{
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    String str1 = paramIntent.getAction();
    Resources localResources;
    Notification.Builder localBuilder;
    PendingIntent localPendingIntent;
    String str3;
    String str4;
    if (str1.equals("android.bluetooth.device.action.PAIRING_REQUEST"))
    {
      BluetoothDevice localBluetoothDevice = (BluetoothDevice)paramIntent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
      int i = paramIntent.getIntExtra("android.bluetooth.device.extra.PAIRING_VARIANT", -2147483648);
      Intent localIntent = new Intent();
      localIntent.setClass(paramContext, BluetoothPairingDialog.class);
      localIntent.putExtra("android.bluetooth.device.extra.DEVICE", localBluetoothDevice);
      localIntent.putExtra("android.bluetooth.device.extra.PAIRING_VARIANT", i);
      if ((i == 2) || (i == 4) || (i == 5))
        localIntent.putExtra("android.bluetooth.device.extra.PAIRING_KEY", paramIntent.getIntExtra("android.bluetooth.device.extra.PAIRING_KEY", -2147483648));
      localIntent.setAction("android.bluetooth.device.action.PAIRING_REQUEST");
      localIntent.setFlags(268435456);
      PowerManager localPowerManager = (PowerManager)paramContext.getSystemService("power");
      if (localBluetoothDevice != null);
      for (String str2 = localBluetoothDevice.getAddress(); (localPowerManager.isScreenOn()) && (LocalBluetoothPreferences.shouldShowDialogInForeground(paramContext, str2)); str2 = null)
      {
        paramContext.startActivity(localIntent);
        return;
      }
      localResources = paramContext.getResources();
      localBuilder = new Notification.Builder(paramContext).setSmallIcon(17301632).setTicker(localResources.getString(2131427454));
      localPendingIntent = PendingIntent.getActivity(paramContext, 0, localIntent, 1073741824);
      str3 = paramIntent.getStringExtra("android.bluetooth.device.extra.NAME");
      if (!TextUtils.isEmpty(str3))
        break label346;
      if (localBluetoothDevice != null)
        str4 = localBluetoothDevice.getAliasName();
    }
    while (true)
    {
      localBuilder.setContentTitle(localResources.getString(2131427455)).setContentText(localResources.getString(2131427456, new Object[] { str4 })).setContentIntent(localPendingIntent).setAutoCancel(true).setDefaults(1);
      ((NotificationManager)paramContext.getSystemService("notification")).notify(17301632, localBuilder.getNotification());
      return;
      str4 = paramContext.getString(17039374);
      continue;
      if (!str1.equals("android.bluetooth.device.action.PAIRING_CANCEL"))
        break;
      ((NotificationManager)paramContext.getSystemService("notification")).cancel(17301632);
      return;
      label346: str4 = str3;
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.bluetooth.BluetoothPairingRequest
 * JD-Core Version:    0.6.2
 */