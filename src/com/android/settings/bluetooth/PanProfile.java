package com.android.settings.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothPan;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothProfile.ServiceListener;
import android.content.Context;
import android.util.Log;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

final class PanProfile
  implements LocalBluetoothProfile
{
  private static boolean V = true;
  private final HashMap<BluetoothDevice, Integer> mDeviceRoleMap = new HashMap();
  private boolean mIsProfileReady;
  private BluetoothPan mService;

  PanProfile(Context paramContext)
  {
    BluetoothAdapter.getDefaultAdapter().getProfileProxy(paramContext, new PanServiceListener(null), 5);
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
    return this.mService.disconnect(paramBluetoothDevice);
  }

  protected void finalize()
  {
    if (V)
      Log.d("PanProfile", "finalize()");
    if (this.mService != null);
    try
    {
      BluetoothAdapter.getDefaultAdapter().closeProfileProxy(5, this.mService);
      this.mService = null;
      return;
    }
    catch (Throwable localThrowable)
    {
      Log.w("PanProfile", "Error cleaning up PAN proxy", localThrowable);
    }
  }

  public int getConnectionStatus(BluetoothDevice paramBluetoothDevice)
  {
    if (this.mService == null)
      return 0;
    return this.mService.getConnectionState(paramBluetoothDevice);
  }

  public int getDrawableResource(BluetoothClass paramBluetoothClass)
  {
    return 2130837579;
  }

  public int getNameResource(BluetoothDevice paramBluetoothDevice)
  {
    if (isLocalRoleNap(paramBluetoothDevice))
      return 2131427745;
    return 2131427744;
  }

  public int getOrdinal()
  {
    return 4;
  }

  public int getPreferred(BluetoothDevice paramBluetoothDevice)
  {
    return -1;
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
      return 2131427770;
    case 2:
    }
    if (isLocalRoleNap(paramBluetoothDevice))
      return 2131427765;
    return 2131427764;
  }

  public boolean isAutoConnectable()
  {
    return false;
  }

  public boolean isConnectable()
  {
    return true;
  }

  boolean isLocalRoleNap(BluetoothDevice paramBluetoothDevice)
  {
    if (this.mDeviceRoleMap.containsKey(paramBluetoothDevice))
      return ((Integer)this.mDeviceRoleMap.get(paramBluetoothDevice)).intValue() == 1;
    return false;
  }

  public boolean isPreferred(BluetoothDevice paramBluetoothDevice)
  {
    return getConnectionStatus(paramBluetoothDevice) == 2;
  }

  public boolean isProfileReady()
  {
    return this.mIsProfileReady;
  }

  void setLocalRole(BluetoothDevice paramBluetoothDevice, int paramInt)
  {
    this.mDeviceRoleMap.put(paramBluetoothDevice, Integer.valueOf(paramInt));
  }

  public void setPreferred(BluetoothDevice paramBluetoothDevice, boolean paramBoolean)
  {
  }

  public String toString()
  {
    return "PAN";
  }

  private final class PanServiceListener
    implements BluetoothProfile.ServiceListener
  {
    private PanServiceListener()
    {
    }

    public void onServiceConnected(int paramInt, BluetoothProfile paramBluetoothProfile)
    {
      if (PanProfile.V)
        Log.d("PanProfile", "Bluetooth service connected");
      PanProfile.access$102(PanProfile.this, (BluetoothPan)paramBluetoothProfile);
      PanProfile.access$202(PanProfile.this, true);
    }

    public void onServiceDisconnected(int paramInt)
    {
      if (PanProfile.V)
        Log.d("PanProfile", "Bluetooth service disconnected");
      PanProfile.access$202(PanProfile.this, false);
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.bluetooth.PanProfile
 * JD-Core Version:    0.6.2
 */