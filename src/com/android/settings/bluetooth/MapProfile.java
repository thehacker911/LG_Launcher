package com.android.settings.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothMap;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothProfile.ServiceListener;
import android.bluetooth.BluetoothUuid;
import android.content.Context;
import android.os.ParcelUuid;
import android.util.Log;
import java.util.List;

final class MapProfile
  implements LocalBluetoothProfile
{
  static final ParcelUuid[] UUIDS = arrayOfParcelUuid;
  private static boolean V = true;
  private final CachedBluetoothDeviceManager mDeviceManager;
  private boolean mIsProfileReady;
  private final LocalBluetoothAdapter mLocalAdapter;
  private final LocalBluetoothProfileManager mProfileManager;
  private BluetoothMap mService;

  static
  {
    ParcelUuid[] arrayOfParcelUuid = new ParcelUuid[3];
    arrayOfParcelUuid[0] = BluetoothUuid.MAP;
    arrayOfParcelUuid[1] = BluetoothUuid.MNS;
    arrayOfParcelUuid[2] = BluetoothUuid.MAS;
  }

  MapProfile(Context paramContext, LocalBluetoothAdapter paramLocalBluetoothAdapter, CachedBluetoothDeviceManager paramCachedBluetoothDeviceManager, LocalBluetoothProfileManager paramLocalBluetoothProfileManager)
  {
    this.mLocalAdapter = paramLocalBluetoothAdapter;
    this.mDeviceManager = paramCachedBluetoothDeviceManager;
    this.mProfileManager = paramLocalBluetoothProfileManager;
    this.mLocalAdapter.getProfileProxy(paramContext, new MapServiceListener(null), 9);
  }

  public boolean connect(BluetoothDevice paramBluetoothDevice)
  {
    if (V)
      Log.d("MapProfile", "connect() - should not get called");
    return true;
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
      Log.d("MapProfile", "finalize()");
    if (this.mService != null);
    try
    {
      BluetoothAdapter.getDefaultAdapter().closeProfileProxy(9, this.mService);
      this.mService = null;
      return;
    }
    catch (Throwable localThrowable)
    {
      Log.w("MapProfile", "Error cleaning up MAP proxy", localThrowable);
    }
  }

  public int getConnectionStatus(BluetoothDevice paramBluetoothDevice)
  {
    if (this.mService == null)
      return 0;
    List localList = this.mService.getConnectedDevices();
    if (V)
      Log.d("MapProfile", "getConnectionStatus: status is: " + this.mService.getConnectionState(paramBluetoothDevice));
    if ((!localList.isEmpty()) && (((BluetoothDevice)localList.get(0)).equals(paramBluetoothDevice)));
    for (int i = this.mService.getConnectionState(paramBluetoothDevice); ; i = 0)
      return i;
  }

  public int getDrawableResource(BluetoothClass paramBluetoothClass)
  {
    return 2130837571;
  }

  public int getNameResource(BluetoothDevice paramBluetoothDevice)
  {
    return 2131427746;
  }

  public int getOrdinal()
  {
    return 9;
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
      return 2131427771;
    case 2:
    }
    return 2131427761;
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
    if (V)
      Log.d("MapProfile", "isProfileReady(): " + this.mIsProfileReady);
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
    return "MAP";
  }

  private final class MapServiceListener
    implements BluetoothProfile.ServiceListener
  {
    private MapServiceListener()
    {
    }

    public void onServiceConnected(int paramInt, BluetoothProfile paramBluetoothProfile)
    {
      if (MapProfile.V)
        Log.d("MapProfile", "Bluetooth service connected");
      MapProfile.access$102(MapProfile.this, (BluetoothMap)paramBluetoothProfile);
      List localList = MapProfile.this.mService.getConnectedDevices();
      while (!localList.isEmpty())
      {
        BluetoothDevice localBluetoothDevice = (BluetoothDevice)localList.remove(0);
        CachedBluetoothDevice localCachedBluetoothDevice = MapProfile.this.mDeviceManager.findDevice(localBluetoothDevice);
        if (localCachedBluetoothDevice == null)
        {
          Log.w("MapProfile", "MapProfile found new device: " + localBluetoothDevice);
          localCachedBluetoothDevice = MapProfile.this.mDeviceManager.addDevice(MapProfile.this.mLocalAdapter, MapProfile.this.mProfileManager, localBluetoothDevice);
        }
        localCachedBluetoothDevice.onProfileStateChanged(MapProfile.this, 2);
        localCachedBluetoothDevice.refresh();
      }
      MapProfile.this.mProfileManager.callServiceConnectedListeners();
      MapProfile.access$502(MapProfile.this, true);
    }

    public void onServiceDisconnected(int paramInt)
    {
      if (MapProfile.V)
        Log.d("MapProfile", "Bluetooth service disconnected");
      MapProfile.this.mProfileManager.callServiceDisconnectedListeners();
      MapProfile.access$502(MapProfile.this, false);
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.bluetooth.MapProfile
 * JD-Core Version:    0.6.2
 */