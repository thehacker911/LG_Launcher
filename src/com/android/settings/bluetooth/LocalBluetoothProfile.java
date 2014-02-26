package com.android.settings.bluetooth;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;

abstract interface LocalBluetoothProfile
{
  public abstract boolean connect(BluetoothDevice paramBluetoothDevice);

  public abstract boolean disconnect(BluetoothDevice paramBluetoothDevice);

  public abstract int getConnectionStatus(BluetoothDevice paramBluetoothDevice);

  public abstract int getDrawableResource(BluetoothClass paramBluetoothClass);

  public abstract int getNameResource(BluetoothDevice paramBluetoothDevice);

  public abstract int getOrdinal();

  public abstract int getPreferred(BluetoothDevice paramBluetoothDevice);

  public abstract int getSummaryResourceForDevice(BluetoothDevice paramBluetoothDevice);

  public abstract boolean isAutoConnectable();

  public abstract boolean isConnectable();

  public abstract boolean isPreferred(BluetoothDevice paramBluetoothDevice);

  public abstract boolean isProfileReady();

  public abstract void setPreferred(BluetoothDevice paramBluetoothDevice, boolean paramBoolean);
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.bluetooth.LocalBluetoothProfile
 * JD-Core Version:    0.6.2
 */