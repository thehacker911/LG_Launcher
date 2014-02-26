package com.android.settings.bluetooth;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;

final class OppProfile
  implements LocalBluetoothProfile
{
  public boolean connect(BluetoothDevice paramBluetoothDevice)
  {
    return false;
  }

  public boolean disconnect(BluetoothDevice paramBluetoothDevice)
  {
    return false;
  }

  public int getConnectionStatus(BluetoothDevice paramBluetoothDevice)
  {
    return 0;
  }

  public int getDrawableResource(BluetoothClass paramBluetoothClass)
  {
    return 0;
  }

  public int getNameResource(BluetoothDevice paramBluetoothDevice)
  {
    return 2131427742;
  }

  public int getOrdinal()
  {
    return 2;
  }

  public int getPreferred(BluetoothDevice paramBluetoothDevice)
  {
    return 0;
  }

  public int getSummaryResourceForDevice(BluetoothDevice paramBluetoothDevice)
  {
    return 0;
  }

  public boolean isAutoConnectable()
  {
    return false;
  }

  public boolean isConnectable()
  {
    return false;
  }

  public boolean isPreferred(BluetoothDevice paramBluetoothDevice)
  {
    return false;
  }

  public boolean isProfileReady()
  {
    return true;
  }

  public void setPreferred(BluetoothDevice paramBluetoothDevice, boolean paramBoolean)
  {
  }

  public String toString()
  {
    return "OPP";
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.bluetooth.OppProfile
 * JD-Core Version:    0.6.2
 */