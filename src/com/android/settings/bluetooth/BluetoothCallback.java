package com.android.settings.bluetooth;

abstract interface BluetoothCallback
{
  public abstract void onBluetoothStateChanged(int paramInt);

  public abstract void onDeviceAdded(CachedBluetoothDevice paramCachedBluetoothDevice);

  public abstract void onDeviceBondStateChanged(CachedBluetoothDevice paramCachedBluetoothDevice, int paramInt);

  public abstract void onDeviceDeleted(CachedBluetoothDevice paramCachedBluetoothDevice);

  public abstract void onScanningStateChanged(boolean paramBoolean);
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.bluetooth.BluetoothCallback
 * JD-Core Version:    0.6.2
 */