package com.android.settings.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothInputDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothProfile.ServiceListener;
import android.content.Context;
import android.util.Log;
import java.util.List;

final class HidProfile
  implements LocalBluetoothProfile
{
  private static boolean V = true;
  private final CachedBluetoothDeviceManager mDeviceManager;
  private boolean mIsProfileReady;
  private final LocalBluetoothAdapter mLocalAdapter;
  private final LocalBluetoothProfileManager mProfileManager;
  private BluetoothInputDevice mService;

  HidProfile(Context paramContext, LocalBluetoothAdapter paramLocalBluetoothAdapter, CachedBluetoothDeviceManager paramCachedBluetoothDeviceManager, LocalBluetoothProfileManager paramLocalBluetoothProfileManager)
  {
    this.mLocalAdapter = paramLocalBluetoothAdapter;
    this.mDeviceManager = paramCachedBluetoothDeviceManager;
    this.mProfileManager = paramLocalBluetoothProfileManager;
    paramLocalBluetoothAdapter.getProfileProxy(paramContext, new InputDeviceServiceListener(null), 4);
  }

  static int getHidClassDrawable(BluetoothClass paramBluetoothClass)
  {
    switch (paramBluetoothClass.getDeviceClass())
    {
    default:
      return 2130837578;
    case 1344:
    case 1472:
      return 2130837576;
    case 1408:
    }
    return 2130837580;
  }

  public boolean connect(BluetoothDevice paramBluetoothDevice)
  {
    if (this.mService == null)
      return false;
    return this.mService.connect(paramBluetoothDevice);
  }

  public boolean disconnect(BluetoothDevice paramBluetoothDevice)
  {
    if (this.mService == null)
      return false;
    return this.mService.disconnect(paramBluetoothDevice);
  }

  protected void finalize()
  {
    if (V)
      Log.d("HidProfile", "finalize()");
    if (this.mService != null);
    try
    {
      BluetoothAdapter.getDefaultAdapter().closeProfileProxy(4, this.mService);
      this.mService = null;
      return;
    }
    catch (Throwable localThrowable)
    {
      Log.w("HidProfile", "Error cleaning up HID proxy", localThrowable);
    }
  }

  public int getConnectionStatus(BluetoothDevice paramBluetoothDevice)
  {
    if (this.mService == null)
      return 0;
    List localList = this.mService.getConnectedDevices();
    if ((!localList.isEmpty()) && (((BluetoothDevice)localList.get(0)).equals(paramBluetoothDevice)));
    for (int i = this.mService.getConnectionState(paramBluetoothDevice); ; i = 0)
      return i;
  }

  public int getDrawableResource(BluetoothClass paramBluetoothClass)
  {
    if (paramBluetoothClass == null)
      return 2130837576;
    return getHidClassDrawable(paramBluetoothClass);
  }

  public int getNameResource(BluetoothDevice paramBluetoothDevice)
  {
    return 2131427743;
  }

  public int getOrdinal()
  {
    return 3;
  }

  public int getPreferred(BluetoothDevice paramBluetoothDevice)
  {
    if (this.mService == null)
      return 0;
    return this.mService.getPriority(paramBluetoothDevice);
  }

  public int getSummaryResourceForDevice(BluetoothDevice paramBluetoothDevice)
  {
    int i = getConnectionStatus(paramBluetoothDevice);
    switch (i)
    {
    case 1:
    default:
      return Utils.getConnectionStateSummary(i);
    case 0:
      return 2131427769;
    case 2:
    }
    return 2131427763;
  }

  public boolean isAutoConnectable()
  {
    return true;
  }

  public boolean isConnectable()
  {
    return true;
  }

  public boolean isPreferred(BluetoothDevice paramBluetoothDevice)
  {
    if (this.mService == null);
    while (this.mService.getPriority(paramBluetoothDevice) <= 0)
      return false;
    return true;
  }

  public boolean isProfileReady()
  {
    return this.mIsProfileReady;
  }

  public void setPreferred(BluetoothDevice paramBluetoothDevice, boolean paramBoolean)
  {
    if (this.mService == null);
    do
    {
      return;
      if (!paramBoolean)
        break;
    }
    while (this.mService.getPriority(paramBluetoothDevice) >= 100);
    this.mService.setPriority(paramBluetoothDevice, 100);
    return;
    this.mService.setPriority(paramBluetoothDevice, 0);
  }

  public String toString()
  {
    return "HID";
  }

  private final class InputDeviceServiceListener
    implements BluetoothProfile.ServiceListener
  {
    private InputDeviceServiceListener()
    {
    }

    public void onServiceConnected(int paramInt, BluetoothProfile paramBluetoothProfile)
    {
      if (HidProfile.V)
        Log.d("HidProfile", "Bluetooth service connected");
      HidProfile.access$102(HidProfile.this, (BluetoothInputDevice)paramBluetoothProfile);
      List localList = HidProfile.this.mService.getConnectedDevices();
      while (!localList.isEmpty())
      {
        BluetoothDevice localBluetoothDevice = (BluetoothDevice)localList.remove(0);
        CachedBluetoothDevice localCachedBluetoothDevice = HidProfile.this.mDeviceManager.findDevice(localBluetoothDevice);
        if (localCachedBluetoothDevice == null)
        {
          Log.w("HidProfile", "HidProfile found new device: " + localBluetoothDevice);
          localCachedBluetoothDevice = HidProfile.this.mDeviceManager.addDevice(HidProfile.this.mLocalAdapter, HidProfile.this.mProfileManager, localBluetoothDevice);
        }
        localCachedBluetoothDevice.onProfileStateChanged(HidProfile.this, 2);
        localCachedBluetoothDevice.refresh();
      }
      HidProfile.access$502(HidProfile.this, true);
    }

    public void onServiceDisconnected(int paramInt)
    {
      if (HidProfile.V)
        Log.d("HidProfile", "Bluetooth service disconnected");
      HidProfile.access$502(HidProfile.this, false);
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.bluetooth.HidProfile
 * JD-Core Version:    0.6.2
 */