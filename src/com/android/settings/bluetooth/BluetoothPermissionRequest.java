package com.android.settings.bluetooth;

import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

public final class BluetoothPermissionRequest extends BroadcastReceiver
{
  Context mContext;
  BluetoothDevice mDevice;
  int mRequestType;
  String mReturnClass = null;
  String mReturnPackage = null;

  private boolean checkUserChoice()
  {
    if ((this.mRequestType != 2) && (this.mRequestType != 3))
      return false;
    LocalBluetoothManager localLocalBluetoothManager = LocalBluetoothManager.getInstance(this.mContext);
    CachedBluetoothDeviceManager localCachedBluetoothDeviceManager = localLocalBluetoothManager.getCachedDeviceManager();
    CachedBluetoothDevice localCachedBluetoothDevice = localCachedBluetoothDeviceManager.findDevice(this.mDevice);
    if (localCachedBluetoothDevice == null)
      localCachedBluetoothDevice = localCachedBluetoothDeviceManager.addDevice(localLocalBluetoothManager.getBluetoothAdapter(), localLocalBluetoothManager.getProfileManager(), this.mDevice);
    int k;
    boolean bool;
    if (this.mRequestType == 2)
    {
      k = localCachedBluetoothDevice.getPhonebookPermissionChoice();
      if (k == 0)
        return false;
      if (k == 1)
      {
        sendIntentToReceiver("android.bluetooth.device.action.CONNECTION_ACCESS_REPLY", true, "android.bluetooth.device.extra.ALWAYS_ALLOWED", true);
        bool = true;
      }
    }
    while (true)
    {
      return bool;
      if (k == 2)
      {
        sendIntentToReceiver("android.bluetooth.device.action.CONNECTION_ACCESS_REPLY", false, null, false);
        bool = true;
      }
      else
      {
        Log.e("BluetoothPermissionRequest", "Bad phonebookPermission: " + k);
        bool = false;
        continue;
        int i = this.mRequestType;
        bool = false;
        if (i == 3)
        {
          int j = localCachedBluetoothDevice.getMessagePermissionChoice();
          if (j == 0)
            return false;
          if (j == 1)
          {
            sendIntentToReceiver("android.bluetooth.device.action.CONNECTION_ACCESS_REPLY", true, "android.bluetooth.device.extra.ALWAYS_ALLOWED", true);
            bool = true;
          }
          else if (j == 2)
          {
            sendIntentToReceiver("android.bluetooth.device.action.CONNECTION_ACCESS_REPLY", false, null, false);
            bool = true;
          }
          else
          {
            Log.e("BluetoothPermissionRequest", "Bad messagePermission: " + j);
            bool = false;
          }
        }
      }
    }
  }

  private String getNotificationTag(int paramInt)
  {
    if (paramInt == 2)
      return "Phonebook Access";
    if (this.mRequestType == 3)
      return "Message Access";
    return null;
  }

  private void sendIntentToReceiver(String paramString1, boolean paramBoolean1, String paramString2, boolean paramBoolean2)
  {
    Intent localIntent = new Intent(paramString1);
    if ((this.mReturnPackage != null) && (this.mReturnClass != null))
      localIntent.setClassName(this.mReturnPackage, this.mReturnClass);
    if (paramBoolean1);
    for (int i = 1; ; i = 2)
    {
      localIntent.putExtra("android.bluetooth.device.extra.CONNECTION_ACCESS_RESULT", i);
      if (paramString2 != null)
        localIntent.putExtra(paramString2, paramBoolean2);
      localIntent.putExtra("android.bluetooth.device.extra.DEVICE", this.mDevice);
      localIntent.putExtra("android.bluetooth.device.extra.ACCESS_REQUEST_TYPE", this.mRequestType);
      this.mContext.sendBroadcast(localIntent, "android.permission.BLUETOOTH_ADMIN");
      return;
    }
  }

  public void onReceive(Context paramContext, Intent paramIntent)
  {
    this.mContext = paramContext;
    String str1 = paramIntent.getAction();
    if (str1.equals("android.bluetooth.device.action.CONNECTION_ACCESS_REQUEST"))
    {
      this.mDevice = ((BluetoothDevice)paramIntent.getParcelableExtra("android.bluetooth.device.extra.DEVICE"));
      this.mRequestType = paramIntent.getIntExtra("android.bluetooth.device.extra.ACCESS_REQUEST_TYPE", 1);
      this.mReturnPackage = paramIntent.getStringExtra("android.bluetooth.device.extra.PACKAGE_NAME");
      this.mReturnClass = paramIntent.getStringExtra("android.bluetooth.device.extra.CLASS_NAME");
      if (!checkUserChoice());
    }
    while (!str1.equals("android.bluetooth.device.action.CONNECTION_ACCESS_CANCEL"))
    {
      return;
      Intent localIntent1 = new Intent(str1);
      localIntent1.setClass(paramContext, BluetoothPermissionActivity.class);
      localIntent1.setFlags(402653184);
      localIntent1.setType(Integer.toString(this.mRequestType));
      localIntent1.putExtra("android.bluetooth.device.extra.ACCESS_REQUEST_TYPE", this.mRequestType);
      localIntent1.putExtra("android.bluetooth.device.extra.DEVICE", this.mDevice);
      localIntent1.putExtra("android.bluetooth.device.extra.PACKAGE_NAME", this.mReturnPackage);
      localIntent1.putExtra("android.bluetooth.device.extra.CLASS_NAME", this.mReturnClass);
      if (this.mDevice != null);
      for (String str2 = this.mDevice.getAddress(); (((PowerManager)paramContext.getSystemService("power")).isScreenOn()) && (LocalBluetoothPreferences.shouldShowDialogInForeground(paramContext, str2)); str2 = null)
      {
        paramContext.startActivity(localIntent1);
        return;
      }
      Intent localIntent2 = new Intent("android.bluetooth.device.action.CONNECTION_ACCESS_REPLY");
      localIntent2.putExtra("android.bluetooth.device.extra.DEVICE", this.mDevice);
      localIntent2.putExtra("android.bluetooth.device.extra.CONNECTION_ACCESS_RESULT", 2);
      localIntent2.putExtra("android.bluetooth.device.extra.ACCESS_REQUEST_TYPE", this.mRequestType);
      BluetoothDevice localBluetoothDevice = this.mDevice;
      String str3 = null;
      if (localBluetoothDevice != null)
        str3 = this.mDevice.getAliasName();
      String str4;
      String str5;
      switch (this.mRequestType)
      {
      default:
        str4 = paramContext.getString(2131427468);
        str5 = paramContext.getString(2131427470, new Object[] { str3, str3 });
      case 2:
      case 3:
      }
      while (true)
      {
        Notification localNotification = new Notification.Builder(paramContext).setContentTitle(str4).setTicker(str5).setContentText(str5).setSmallIcon(17301632).setAutoCancel(true).setPriority(2).setOnlyAlertOnce(false).setDefaults(-1).setContentIntent(PendingIntent.getActivity(paramContext, 0, localIntent1, 0)).setDeleteIntent(PendingIntent.getBroadcast(paramContext, 0, localIntent2, 0)).build();
        localNotification.flags = (0x20 | localNotification.flags);
        ((NotificationManager)paramContext.getSystemService("notification")).notify(getNotificationTag(this.mRequestType), 17301632, localNotification);
        return;
        str4 = paramContext.getString(2131427471);
        str5 = paramContext.getString(2131427472, new Object[] { str3, str3 });
        continue;
        str4 = paramContext.getString(2131427475);
        str5 = paramContext.getString(2131427476, new Object[] { str3, str3 });
      }
    }
    NotificationManager localNotificationManager = (NotificationManager)paramContext.getSystemService("notification");
    this.mRequestType = paramIntent.getIntExtra("android.bluetooth.device.extra.ACCESS_REQUEST_TYPE", 2);
    localNotificationManager.cancel(getNotificationTag(this.mRequestType), 17301632);
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.bluetooth.BluetoothPermissionRequest
 * JD-Core Version:    0.6.2
 */