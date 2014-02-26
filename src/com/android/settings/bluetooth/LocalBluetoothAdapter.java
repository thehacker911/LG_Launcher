package com.android.settings.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile.ServiceListener;
import android.content.Context;
import android.os.ParcelUuid;
import java.util.Set;

public final class LocalBluetoothAdapter
{
  private static LocalBluetoothAdapter sInstance;
  private final BluetoothAdapter mAdapter;
  private long mLastScan;
  private LocalBluetoothProfileManager mProfileManager;
  private int mState = -2147483648;

  private LocalBluetoothAdapter(BluetoothAdapter paramBluetoothAdapter)
  {
    this.mAdapter = paramBluetoothAdapter;
  }

  static LocalBluetoothAdapter getInstance()
  {
    try
    {
      if (sInstance == null)
      {
        BluetoothAdapter localBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (localBluetoothAdapter != null)
          sInstance = new LocalBluetoothAdapter(localBluetoothAdapter);
      }
      LocalBluetoothAdapter localLocalBluetoothAdapter = sInstance;
      return localLocalBluetoothAdapter;
    }
    finally
    {
    }
  }

  void cancelDiscovery()
  {
    this.mAdapter.cancelDiscovery();
  }

  boolean disable()
  {
    return this.mAdapter.disable();
  }

  boolean enable()
  {
    return this.mAdapter.enable();
  }

  public int getBluetoothState()
  {
    try
    {
      syncBluetoothState();
      int i = this.mState;
      return i;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }

  Set<BluetoothDevice> getBondedDevices()
  {
    return this.mAdapter.getBondedDevices();
  }

  String getName()
  {
    return this.mAdapter.getName();
  }

  void getProfileProxy(Context paramContext, BluetoothProfile.ServiceListener paramServiceListener, int paramInt)
  {
    this.mAdapter.getProfileProxy(paramContext, paramServiceListener, paramInt);
  }

  int getScanMode()
  {
    return this.mAdapter.getScanMode();
  }

  int getState()
  {
    return this.mAdapter.getState();
  }

  ParcelUuid[] getUuids()
  {
    return this.mAdapter.getUuids();
  }

  boolean isDiscovering()
  {
    return this.mAdapter.isDiscovering();
  }

  boolean isEnabled()
  {
    return this.mAdapter.isEnabled();
  }

  public void setBluetoothEnabled(boolean paramBoolean)
  {
    boolean bool;
    if (paramBoolean)
    {
      bool = this.mAdapter.enable();
      if (!bool)
        break label49;
      if (!paramBoolean)
        break label42;
    }
    label42: for (int i = 11; ; i = 13)
    {
      setBluetoothStateInt(i);
      return;
      bool = this.mAdapter.disable();
      break;
    }
    label49: syncBluetoothState();
  }

  void setBluetoothStateInt(int paramInt)
  {
    try
    {
      this.mState = paramInt;
      if ((paramInt == 12) && (this.mProfileManager != null))
        this.mProfileManager.setBluetoothStateOn();
      return;
    }
    finally
    {
    }
  }

  void setName(String paramString)
  {
    this.mAdapter.setName(paramString);
  }

  void setProfileManager(LocalBluetoothProfileManager paramLocalBluetoothProfileManager)
  {
    this.mProfileManager = paramLocalBluetoothProfileManager;
  }

  void setScanMode(int paramInt)
  {
    this.mAdapter.setScanMode(paramInt);
  }

  boolean setScanMode(int paramInt1, int paramInt2)
  {
    return this.mAdapter.setScanMode(paramInt1, paramInt2);
  }

  void startScanning(boolean paramBoolean)
  {
    if (!this.mAdapter.isDiscovering())
    {
      if (paramBoolean)
        break label49;
      if (300000L + this.mLastScan <= System.currentTimeMillis())
        break label30;
    }
    label30: label49: 
    while (!this.mAdapter.startDiscovery())
    {
      A2dpProfile localA2dpProfile;
      do
      {
        return;
        localA2dpProfile = this.mProfileManager.getA2dpProfile();
      }
      while ((localA2dpProfile != null) && (localA2dpProfile.isA2dpPlaying()));
    }
    this.mLastScan = System.currentTimeMillis();
  }

  void stopScanning()
  {
    if (this.mAdapter.isDiscovering())
      this.mAdapter.cancelDiscovery();
  }

  boolean syncBluetoothState()
  {
    if (this.mAdapter.getState() != this.mState)
    {
      setBluetoothStateInt(this.mAdapter.getState());
      return true;
    }
    return false;
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.bluetooth.LocalBluetoothAdapter
 * JD-Core Version:    0.6.2
 */