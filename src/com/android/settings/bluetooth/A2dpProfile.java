package com.android.settings.bluetooth;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothProfile.ServiceListener;
import android.bluetooth.BluetoothUuid;
import android.content.Context;
import android.os.ParcelUuid;
import android.util.Log;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

final class A2dpProfile
  implements LocalBluetoothProfile
{
  static final ParcelUuid[] SINK_UUIDS = arrayOfParcelUuid;
  private static boolean V = true;
  private final CachedBluetoothDeviceManager mDeviceManager;
  private boolean mIsProfileReady;
  private final LocalBluetoothAdapter mLocalAdapter;
  private final LocalBluetoothProfileManager mProfileManager;
  private BluetoothA2dp mService;

  static
  {
    ParcelUuid[] arrayOfParcelUuid = new ParcelUuid[2];
    arrayOfParcelUuid[0] = BluetoothUuid.AudioSink;
    arrayOfParcelUuid[1] = BluetoothUuid.AdvAudioDist;
  }

  A2dpProfile(Context paramContext, LocalBluetoothAdapter paramLocalBluetoothAdapter, CachedBluetoothDeviceManager paramCachedBluetoothDeviceManager, LocalBluetoothProfileManager paramLocalBluetoothProfileManager)
  {
    this.mLocalAdapter = paramLocalBluetoothAdapter;
    this.mDeviceManager = paramCachedBluetoothDeviceManager;
    this.mProfileManager = paramLocalBluetoothProfileManager;
    this.mLocalAdapter.getProfileProxy(paramContext, new A2dpServiceListener(null), 2);
  }

  public boolean connect(BluetoothDevice paramBluetoothDevice)
  {
    if (this.mService == null)
      return false;
    List localList = getConnectedDevices();
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
    if (this.mService.getPriority(paramBluetoothDevice) > 100)
      this.mService.setPriority(paramBluetoothDevice, 100);
    return this.mService.disconnect(paramBluetoothDevice);
  }

  protected void finalize()
  {
    if (V)
      Log.d("A2dpProfile", "finalize()");
    if (this.mService != null);
    try
    {
      BluetoothAdapter.getDefaultAdapter().closeProfileProxy(2, this.mService);
      this.mService = null;
      return;
    }
    catch (Throwable localThrowable)
    {
      Log.w("A2dpProfile", "Error cleaning up A2DP proxy", localThrowable);
    }
  }

  public List<BluetoothDevice> getConnectedDevices()
  {
    if (this.mService == null)
      return new ArrayList(0);
    return this.mService.getDevicesMatchingConnectionStates(new int[] { 2, 1, 3 });
  }

  public int getConnectionStatus(BluetoothDevice paramBluetoothDevice)
  {
    if (this.mService == null)
      return 0;
    return this.mService.getConnectionState(paramBluetoothDevice);
  }

  public int getDrawableResource(BluetoothClass paramBluetoothClass)
  {
    return 2130837573;
  }

  public int getNameResource(BluetoothDevice paramBluetoothDevice)
  {
    return 2131427740;
  }

  public int getOrdinal()
  {
    return 1;
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
      return 2131427766;
    case 2:
    }
    return 2131427758;
  }

  boolean isA2dpPlaying()
  {
    if (this.mService == null)
      return false;
    List localList = this.mService.getConnectedDevices();
    return (!localList.isEmpty()) && (this.mService.isA2dpPlaying((BluetoothDevice)localList.get(0)));
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
    return "A2DP";
  }

  private final class A2dpServiceListener
    implements BluetoothProfile.ServiceListener
  {
    private A2dpServiceListener()
    {
    }

    public void onServiceConnected(int paramInt, BluetoothProfile paramBluetoothProfile)
    {
      if (A2dpProfile.V)
        Log.d("A2dpProfile", "Bluetooth service connected");
      A2dpProfile.access$102(A2dpProfile.this, (BluetoothA2dp)paramBluetoothProfile);
      List localList = A2dpProfile.this.mService.getConnectedDevices();
      while (!localList.isEmpty())
      {
        BluetoothDevice localBluetoothDevice = (BluetoothDevice)localList.remove(0);
        CachedBluetoothDevice localCachedBluetoothDevice = A2dpProfile.this.mDeviceManager.findDevice(localBluetoothDevice);
        if (localCachedBluetoothDevice == null)
        {
          Log.w("A2dpProfile", "A2dpProfile found new device: " + localBluetoothDevice);
          localCachedBluetoothDevice = A2dpProfile.this.mDeviceManager.addDevice(A2dpProfile.this.mLocalAdapter, A2dpProfile.this.mProfileManager, localBluetoothDevice);
        }
        localCachedBluetoothDevice.onProfileStateChanged(A2dpProfile.this, 2);
        localCachedBluetoothDevice.refresh();
      }
      A2dpProfile.access$502(A2dpProfile.this, true);
    }

    public void onServiceDisconnected(int paramInt)
    {
      if (A2dpProfile.V)
        Log.d("A2dpProfile", "Bluetooth service disconnected");
      A2dpProfile.access$502(A2dpProfile.this, false);
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.bluetooth.A2dpProfile
 * JD-Core Version:    0.6.2
 */