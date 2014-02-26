package com.android.settings.bluetooth;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothUuid;
import android.os.ParcelUuid;
import android.util.Log;

final class BluetoothDeviceFilter
{
  static final Filter ALL_FILTER = new AllFilter(null);
  static final Filter BONDED_DEVICE_FILTER = new BondedDeviceFilter(null);
  private static final Filter[] FILTERS = arrayOfFilter;
  static final Filter UNBONDED_DEVICE_FILTER = new UnbondedDeviceFilter(null);

  static
  {
    Filter[] arrayOfFilter = new Filter[5];
    arrayOfFilter[0] = ALL_FILTER;
    arrayOfFilter[1] = new AudioFilter(null);
    arrayOfFilter[2] = new TransferFilter(null);
    arrayOfFilter[3] = new PanuFilter(null);
    arrayOfFilter[4] = new NapFilter(null);
  }

  static Filter getFilter(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < FILTERS.length))
      return FILTERS[paramInt];
    Log.w("BluetoothDeviceFilter", "Invalid filter type " + paramInt + " for device picker");
    return ALL_FILTER;
  }

  private static final class AllFilter
    implements BluetoothDeviceFilter.Filter
  {
    public boolean matches(BluetoothDevice paramBluetoothDevice)
    {
      return true;
    }
  }

  private static final class AudioFilter extends BluetoothDeviceFilter.ClassUuidFilter
  {
    private AudioFilter()
    {
      super();
    }

    boolean matches(ParcelUuid[] paramArrayOfParcelUuid, BluetoothClass paramBluetoothClass)
    {
      if (paramArrayOfParcelUuid != null)
      {
        if (BluetoothUuid.containsAnyUuid(paramArrayOfParcelUuid, A2dpProfile.SINK_UUIDS));
        while (BluetoothUuid.containsAnyUuid(paramArrayOfParcelUuid, HeadsetProfile.UUIDS))
          return true;
      }
      do
      {
        do
          return false;
        while (paramBluetoothClass == null);
        if (paramBluetoothClass.doesClassMatch(1))
          break;
      }
      while (!paramBluetoothClass.doesClassMatch(0));
      return true;
    }
  }

  private static final class BondedDeviceFilter
    implements BluetoothDeviceFilter.Filter
  {
    public boolean matches(BluetoothDevice paramBluetoothDevice)
    {
      return paramBluetoothDevice.getBondState() == 12;
    }
  }

  private static abstract class ClassUuidFilter
    implements BluetoothDeviceFilter.Filter
  {
    public boolean matches(BluetoothDevice paramBluetoothDevice)
    {
      return matches(paramBluetoothDevice.getUuids(), paramBluetoothDevice.getBluetoothClass());
    }

    abstract boolean matches(ParcelUuid[] paramArrayOfParcelUuid, BluetoothClass paramBluetoothClass);
  }

  static abstract interface Filter
  {
    public abstract boolean matches(BluetoothDevice paramBluetoothDevice);
  }

  private static final class NapFilter extends BluetoothDeviceFilter.ClassUuidFilter
  {
    private NapFilter()
    {
      super();
    }

    boolean matches(ParcelUuid[] paramArrayOfParcelUuid, BluetoothClass paramBluetoothClass)
    {
      if ((paramArrayOfParcelUuid != null) && (BluetoothUuid.isUuidPresent(paramArrayOfParcelUuid, BluetoothUuid.NAP)));
      while ((paramBluetoothClass != null) && (paramBluetoothClass.doesClassMatch(5)))
        return true;
      return false;
    }
  }

  private static final class PanuFilter extends BluetoothDeviceFilter.ClassUuidFilter
  {
    private PanuFilter()
    {
      super();
    }

    boolean matches(ParcelUuid[] paramArrayOfParcelUuid, BluetoothClass paramBluetoothClass)
    {
      if ((paramArrayOfParcelUuid != null) && (BluetoothUuid.isUuidPresent(paramArrayOfParcelUuid, BluetoothUuid.PANU)));
      while ((paramBluetoothClass != null) && (paramBluetoothClass.doesClassMatch(4)))
        return true;
      return false;
    }
  }

  private static final class TransferFilter extends BluetoothDeviceFilter.ClassUuidFilter
  {
    private TransferFilter()
    {
      super();
    }

    boolean matches(ParcelUuid[] paramArrayOfParcelUuid, BluetoothClass paramBluetoothClass)
    {
      if ((paramArrayOfParcelUuid != null) && (BluetoothUuid.isUuidPresent(paramArrayOfParcelUuid, BluetoothUuid.ObexObjectPush)));
      while ((paramBluetoothClass != null) && (paramBluetoothClass.doesClassMatch(2)))
        return true;
      return false;
    }
  }

  private static final class UnbondedDeviceFilter
    implements BluetoothDeviceFilter.Filter
  {
    public boolean matches(BluetoothDevice paramBluetoothDevice)
    {
      return paramBluetoothDevice.getBondState() != 12;
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.bluetooth.BluetoothDeviceFilter
 * JD-Core Version:    0.6.2
 */