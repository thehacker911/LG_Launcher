package com.android.settings.bluetooth;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothPbap;
import android.bluetooth.BluetoothPbap.ServiceListener;
import android.content.Context;
import android.util.Log;

final class PbapServerProfile
  implements LocalBluetoothProfile
{
  private static boolean V = true;
  private boolean mIsProfileReady;
  private BluetoothPbap mService;

  PbapServerProfile(Context paramContext)
  {
    new BluetoothPbap(paramContext, new PbapServiceListener(null));
  }

  public boolean connect(BluetoothDevice paramBluetoothDevice)
  {
    return false;
  }

  public boolean disconnect(BluetoothDevice paramBluetoothDevice)
  {
    if (this.mService == null)
      return false;
    return this.mService.disconnect();
  }

  protected void finalize()
  {
    if (V)
      Log.d("PbapServerProfile", "finalize()");
    if (this.mService != null);
    try
    {
      this.mService.close();
      this.mService = null;
      return;
    }
    catch (Throwable localThrowable)
    {
      Log.w("PbapServerProfile", "Error cleaning up PBAP proxy", localThrowable);
    }
  }

  public int getConnectionStatus(BluetoothDevice paramBluetoothDevice)
  {
    if (this.mService == null);
    while (!this.mService.isConnected(paramBluetoothDevice))
      return 0;
    return 2;
  }

  public int getDrawableResource(BluetoothClass paramBluetoothClass)
  {
    return 0;
  }

  public int getNameResource(BluetoothDevice paramBluetoothDevice)
  {
    return 0;
  }

  public int getOrdinal()
  {
    return 6;
  }

  public int getPreferred(BluetoothDevice paramBluetoothDevice)
  {
    return -1;
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
    return true;
  }

  public boolean isPreferred(BluetoothDevice paramBluetoothDevice)
  {
    return false;
  }

  public boolean isProfileReady()
  {
    return this.mIsProfileReady;
  }

  public void setPreferred(BluetoothDevice paramBluetoothDevice, boolean paramBoolean)
  {
  }

  public String toString()
  {
    return "PBAP Server";
  }

  private final class PbapServiceListener
    implements BluetoothPbap.ServiceListener
  {
    private PbapServiceListener()
    {
    }

    public void onServiceConnected(BluetoothPbap paramBluetoothPbap)
    {
      if (PbapServerProfile.V)
        Log.d("PbapServerProfile", "Bluetooth service connected");
      PbapServerProfile.access$102(PbapServerProfile.this, paramBluetoothPbap);
      PbapServerProfile.access$202(PbapServerProfile.this, true);
    }

    public void onServiceDisconnected()
    {
      if (PbapServerProfile.V)
        Log.d("PbapServerProfile", "Bluetooth service disconnected");
      PbapServerProfile.access$202(PbapServerProfile.this, false);
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.bluetooth.PbapServerProfile
 * JD-Core Version:    0.6.2
 */