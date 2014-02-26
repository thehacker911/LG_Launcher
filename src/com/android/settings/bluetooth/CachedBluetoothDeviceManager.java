package com.android.settings.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

final class CachedBluetoothDeviceManager
{
  private final List<CachedBluetoothDevice> mCachedDevices = new ArrayList();
  private Context mContext;

  CachedBluetoothDeviceManager(Context paramContext)
  {
    this.mContext = paramContext;
  }

  public static boolean onDeviceDisappeared(CachedBluetoothDevice paramCachedBluetoothDevice)
  {
    paramCachedBluetoothDevice.setVisible(false);
    int i = paramCachedBluetoothDevice.getBondState();
    boolean bool = false;
    if (i == 10)
      bool = true;
    return bool;
  }

  CachedBluetoothDevice addDevice(LocalBluetoothAdapter paramLocalBluetoothAdapter, LocalBluetoothProfileManager paramLocalBluetoothProfileManager, BluetoothDevice paramBluetoothDevice)
  {
    CachedBluetoothDevice localCachedBluetoothDevice = new CachedBluetoothDevice(this.mContext, paramLocalBluetoothAdapter, paramLocalBluetoothProfileManager, paramBluetoothDevice);
    this.mCachedDevices.add(localCachedBluetoothDevice);
    return localCachedBluetoothDevice;
  }

  CachedBluetoothDevice findDevice(BluetoothDevice paramBluetoothDevice)
  {
    Iterator localIterator = this.mCachedDevices.iterator();
    while (localIterator.hasNext())
    {
      CachedBluetoothDevice localCachedBluetoothDevice = (CachedBluetoothDevice)localIterator.next();
      if (localCachedBluetoothDevice.getDevice().equals(paramBluetoothDevice))
        return localCachedBluetoothDevice;
    }
    return null;
  }

  public Collection<CachedBluetoothDevice> getCachedDevicesCopy()
  {
    try
    {
      ArrayList localArrayList = new ArrayList(this.mCachedDevices);
      return localArrayList;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }

  public String getName(BluetoothDevice paramBluetoothDevice)
  {
    CachedBluetoothDevice localCachedBluetoothDevice = findDevice(paramBluetoothDevice);
    String str;
    if (localCachedBluetoothDevice != null)
      str = localCachedBluetoothDevice.getName();
    do
    {
      return str;
      str = paramBluetoothDevice.getAliasName();
    }
    while (str != null);
    return paramBluetoothDevice.getAddress();
  }

  public void onBluetoothStateChanged(int paramInt)
  {
    if (paramInt == 13);
    while (true)
    {
      int i;
      try
      {
        i = -1 + this.mCachedDevices.size();
        if (i >= 0)
        {
          CachedBluetoothDevice localCachedBluetoothDevice = (CachedBluetoothDevice)this.mCachedDevices.get(i);
          if (localCachedBluetoothDevice.getBondState() != 12)
          {
            localCachedBluetoothDevice.setVisible(false);
            this.mCachedDevices.remove(i);
          }
          else
          {
            localCachedBluetoothDevice.clearProfileConnectionState();
          }
        }
      }
      finally
      {
      }
      return;
      i--;
    }
  }

  public void onBtClassChanged(BluetoothDevice paramBluetoothDevice)
  {
    try
    {
      CachedBluetoothDevice localCachedBluetoothDevice = findDevice(paramBluetoothDevice);
      if (localCachedBluetoothDevice != null)
        localCachedBluetoothDevice.refreshBtClass();
      return;
    }
    finally
    {
    }
  }

  public void onDeviceNameUpdated(BluetoothDevice paramBluetoothDevice)
  {
    CachedBluetoothDevice localCachedBluetoothDevice = findDevice(paramBluetoothDevice);
    if (localCachedBluetoothDevice != null)
      localCachedBluetoothDevice.refreshName();
  }

  public void onScanningStateChanged(boolean paramBoolean)
  {
    if (!paramBoolean);
    while (true)
    {
      return;
      try
      {
        for (int i = -1 + this.mCachedDevices.size(); i >= 0; i--)
          ((CachedBluetoothDevice)this.mCachedDevices.get(i)).setVisible(false);
      }
      finally
      {
      }
    }
  }

  public void onUuidChanged(BluetoothDevice paramBluetoothDevice)
  {
    try
    {
      CachedBluetoothDevice localCachedBluetoothDevice = findDevice(paramBluetoothDevice);
      if (localCachedBluetoothDevice != null)
        localCachedBluetoothDevice.onUuidChanged();
      return;
    }
    finally
    {
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.bluetooth.CachedBluetoothDeviceManager
 * JD-Core Version:    0.6.2
 */