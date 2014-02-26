package com.android.settings.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothProfile.ServiceListener;
import android.bluetooth.BluetoothUuid;
import android.content.Context;
import android.os.ParcelUuid;
import android.util.Log;
import java.util.Iterator;
import java.util.List;

final class HeadsetProfile
  implements LocalBluetoothProfile
{
  static final ParcelUuid[] UUIDS = arrayOfParcelUuid;
  private static boolean V = true;
  private final CachedBluetoothDeviceManager mDeviceManager;
  private boolean mIsProfileReady;
  private final LocalBluetoothAdapter mLocalAdapter;
  private final LocalBluetoothProfileManager mProfileManager;
  private BluetoothHeadset mService;

  static
  {
    ParcelUuid[] arrayOfParcelUuid = new ParcelUuid[2];
    arrayOfParcelUuid[0] = BluetoothUuid.HSP;
    arrayOfParcelUuid[1] = BluetoothUuid.Handsfree;
  }

  HeadsetProfile(Context paramContext, LocalBluetoothAdapter paramLocalBluetoothAdapter, CachedBluetoothDeviceManager paramCachedBluetoothDeviceManager, LocalBluetoothProfileManager paramLocalBluetoothProfileManager)
  {
    this.mLocalAdapter = paramLocalBluetoothAdapter;
    this.mDeviceManager = paramCachedBluetoothDeviceManager;
    this.mProfileManager = paramLocalBluetoothProfileManager;
    this.mLocalAdapter.getProfileProxy(paramContext, new HeadsetServiceListener(null), 1);
  }

  public boolean connect(BluetoothDevice paramBluetoothDevice)
  {
    if (this.mService == null)
      return false;
    List localList = this.mService.getConnectedDevices();
    if (localList != null)
    {
      Iterator localIterator = localList.iterator();
      while (localIterator.hasNext())
      {
        BluetoothDevice localBluetoothDevice = (BluetoothDevice)localIterator.next();
        this.mService.disconnect(localBluetoothDevice);
      }
    }
    return this.mService.connect(paramBluetoothDevice);
  }

  public boolean disconnect(BluetoothDevice paramBluetoothDevice)
  {
    if (this.mService == null)
      return false;
    List localList = this.mService.getConnectedDevices();
    if ((!localList.isEmpty()) && (((BluetoothDevice)localList.get(0)).equals(paramBluetoothDevice)))
    {
      if (this.mService.getPriority(paramBluetoothDevice) > 100)
        this.mService.setPriority(paramBluetoothDevice, 100);
      return this.mService.disconnect(paramBluetoothDevice);
    }
    return false;
  }

  protected void finalize()
  {
    if (V)
      Log.d("HeadsetProfile", "finalize()");
    if (this.mService != null);
    try
    {
      BluetoothAdapter.getDefaultAdapter().closeProfileProxy(1, this.mService);
      this.mService = null;
      return;
    }
    catch (Throwable localThrowable)
    {
      Log.w("HeadsetProfile", "Error cleaning up HID proxy", localThrowable);
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
    return 2130837574;
  }

  public int getNameResource(BluetoothDevice paramBluetoothDevice)
  {
    return 2131427741;
  }

  public int getOrdinal()
  {
    return 0;
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
      return 2131427767;
    case 2:
    }
    return 2131427759;
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
    return "HEADSET";
  }

  private final class HeadsetServiceListener
    implements BluetoothProfile.ServiceListener
  {
    private HeadsetServiceListener()
    {
    }

    public void onServiceConnected(int paramInt, BluetoothProfile paramBluetoothProfile)
    {
      if (HeadsetProfile.V)
        Log.d("HeadsetProfile", "Bluetooth service connected");
      HeadsetProfile.access$102(HeadsetProfile.this, (BluetoothHeadset)paramBluetoothProfile);
      List localList = HeadsetProfile.this.mService.getConnectedDevices();
      while (!localList.isEmpty())
      {
        BluetoothDevice localBluetoothDevice = (BluetoothDevice)localList.remove(0);
        CachedBluetoothDevice localCachedBluetoothDevice = HeadsetProfile.this.mDeviceManager.findDevice(localBluetoothDevice);
        if (localCachedBluetoothDevice == null)
        {
          Log.w("HeadsetProfile", "HeadsetProfile found new device: " + localBluetoothDevice);
          localCachedBluetoothDevice = HeadsetProfile.this.mDeviceManager.addDevice(HeadsetProfile.this.mLocalAdapter, HeadsetProfile.this.mProfileManager, localBluetoothDevice);
        }
        localCachedBluetoothDevice.onProfileStateChanged(HeadsetProfile.this, 2);
        localCachedBluetoothDevice.refresh();
      }
      HeadsetProfile.this.mProfileManager.callServiceConnectedListeners();
      HeadsetProfile.access$502(HeadsetProfile.this, true);
    }

    public void onServiceDisconnected(int paramInt)
    {
      if (HeadsetProfile.V)
        Log.d("HeadsetProfile", "Bluetooth service disconnected");
      HeadsetProfile.this.mProfileManager.callServiceDisconnectedListeners();
      HeadsetProfile.access$502(HeadsetProfile.this, false);
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.bluetooth.HeadsetProfile
 * JD-Core Version:    0.6.2
 */