package com.android.settings.bluetooth;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

final class BluetoothEventManager
{
  private final IntentFilter mAdapterIntentFilter;
  private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      String str = paramAnonymousIntent.getAction();
      BluetoothDevice localBluetoothDevice = (BluetoothDevice)paramAnonymousIntent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
      BluetoothEventManager.Handler localHandler = (BluetoothEventManager.Handler)BluetoothEventManager.this.mHandlerMap.get(str);
      if (localHandler != null)
        localHandler.onReceive(paramAnonymousContext, paramAnonymousIntent, localBluetoothDevice);
    }
  };
  private final Collection<BluetoothCallback> mCallbacks = new ArrayList();
  private Context mContext;
  private final CachedBluetoothDeviceManager mDeviceManager;
  private final Map<String, Handler> mHandlerMap;
  private final LocalBluetoothAdapter mLocalAdapter;
  private final IntentFilter mProfileIntentFilter;
  private LocalBluetoothProfileManager mProfileManager;

  BluetoothEventManager(LocalBluetoothAdapter paramLocalBluetoothAdapter, CachedBluetoothDeviceManager paramCachedBluetoothDeviceManager, Context paramContext)
  {
    this.mLocalAdapter = paramLocalBluetoothAdapter;
    this.mDeviceManager = paramCachedBluetoothDeviceManager;
    this.mAdapterIntentFilter = new IntentFilter();
    this.mProfileIntentFilter = new IntentFilter();
    this.mHandlerMap = new HashMap();
    this.mContext = paramContext;
    addHandler("android.bluetooth.adapter.action.STATE_CHANGED", new AdapterStateChangedHandler(null));
    addHandler("android.bluetooth.adapter.action.DISCOVERY_STARTED", new ScanningStateChangedHandler(true));
    addHandler("android.bluetooth.adapter.action.DISCOVERY_FINISHED", new ScanningStateChangedHandler(false));
    addHandler("android.bluetooth.device.action.FOUND", new DeviceFoundHandler(null));
    addHandler("android.bluetooth.device.action.DISAPPEARED", new DeviceDisappearedHandler(null));
    addHandler("android.bluetooth.device.action.NAME_CHANGED", new NameChangedHandler(null));
    addHandler("android.bluetooth.device.action.BOND_STATE_CHANGED", new BondStateChangedHandler(null));
    addHandler("android.bluetooth.device.action.PAIRING_CANCEL", new PairingCancelHandler(null));
    addHandler("android.bluetooth.device.action.CLASS_CHANGED", new ClassChangedHandler(null));
    addHandler("android.bluetooth.device.action.UUID", new UuidChangedHandler(null));
    addHandler("android.intent.action.DOCK_EVENT", new DockEventHandler(null));
    this.mContext.registerReceiver(this.mBroadcastReceiver, this.mAdapterIntentFilter);
  }

  private void dispatchDeviceAdded(CachedBluetoothDevice paramCachedBluetoothDevice)
  {
    synchronized (this.mCallbacks)
    {
      Iterator localIterator = this.mCallbacks.iterator();
      if (localIterator.hasNext())
        ((BluetoothCallback)localIterator.next()).onDeviceAdded(paramCachedBluetoothDevice);
    }
  }

  private static String getDockedDeviceAddress(Context paramContext)
  {
    Intent localIntent = paramContext.registerReceiver(null, new IntentFilter("android.intent.action.DOCK_EVENT"));
    String str = null;
    if (localIntent != null)
    {
      int i = localIntent.getIntExtra("android.intent.extra.DOCK_STATE", 0);
      str = null;
      if (i != 0)
      {
        BluetoothDevice localBluetoothDevice = (BluetoothDevice)localIntent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
        str = null;
        if (localBluetoothDevice != null)
          str = localBluetoothDevice.getAddress();
      }
    }
    return str;
  }

  void addHandler(String paramString, Handler paramHandler)
  {
    this.mHandlerMap.put(paramString, paramHandler);
    this.mAdapterIntentFilter.addAction(paramString);
  }

  void addProfileHandler(String paramString, Handler paramHandler)
  {
    this.mHandlerMap.put(paramString, paramHandler);
    this.mProfileIntentFilter.addAction(paramString);
  }

  boolean readPairedDevices()
  {
    Set localSet = this.mLocalAdapter.getBondedDevices();
    boolean bool;
    if (localSet == null)
      bool = false;
    while (true)
    {
      return bool;
      bool = false;
      Iterator localIterator = localSet.iterator();
      while (localIterator.hasNext())
      {
        BluetoothDevice localBluetoothDevice = (BluetoothDevice)localIterator.next();
        if (this.mDeviceManager.findDevice(localBluetoothDevice) == null)
        {
          dispatchDeviceAdded(this.mDeviceManager.addDevice(this.mLocalAdapter, this.mProfileManager, localBluetoothDevice));
          bool = true;
        }
      }
    }
  }

  void registerCallback(BluetoothCallback paramBluetoothCallback)
  {
    synchronized (this.mCallbacks)
    {
      this.mCallbacks.add(paramBluetoothCallback);
      return;
    }
  }

  void registerProfileIntentReceiver()
  {
    this.mContext.registerReceiver(this.mBroadcastReceiver, this.mProfileIntentFilter);
  }

  void setProfileManager(LocalBluetoothProfileManager paramLocalBluetoothProfileManager)
  {
    this.mProfileManager = paramLocalBluetoothProfileManager;
  }

  void unregisterCallback(BluetoothCallback paramBluetoothCallback)
  {
    synchronized (this.mCallbacks)
    {
      this.mCallbacks.remove(paramBluetoothCallback);
      return;
    }
  }

  private class AdapterStateChangedHandler
    implements BluetoothEventManager.Handler
  {
    private AdapterStateChangedHandler()
    {
    }

    public void onReceive(Context paramContext, Intent paramIntent, BluetoothDevice paramBluetoothDevice)
    {
      int i = paramIntent.getIntExtra("android.bluetooth.adapter.extra.STATE", -2147483648);
      BluetoothEventManager.this.mLocalAdapter.setBluetoothStateInt(i);
      synchronized (BluetoothEventManager.this.mCallbacks)
      {
        Iterator localIterator = BluetoothEventManager.this.mCallbacks.iterator();
        if (localIterator.hasNext())
          ((BluetoothCallback)localIterator.next()).onBluetoothStateChanged(i);
      }
      BluetoothEventManager.this.mDeviceManager.onBluetoothStateChanged(i);
    }
  }

  private class BondStateChangedHandler
    implements BluetoothEventManager.Handler
  {
    private BondStateChangedHandler()
    {
    }

    private void showUnbondMessage(Context paramContext, String paramString, int paramInt)
    {
      int i;
      switch (paramInt)
      {
      case 3:
      default:
        Log.w("BluetoothEventManager", "showUnbondMessage: Not displaying any message for reason: " + paramInt);
        return;
      case 1:
        i = 2131427720;
      case 2:
      case 4:
      case 5:
      case 6:
      case 7:
      case 8:
      }
      while (true)
      {
        Utils.showError(paramContext, paramString, i);
        return;
        i = 2131427722;
        continue;
        i = 2131427721;
        continue;
        i = 2131427719;
      }
    }

    public void onReceive(Context paramContext, Intent paramIntent, BluetoothDevice paramBluetoothDevice)
    {
      if (paramBluetoothDevice == null)
        Log.e("BluetoothEventManager", "ACTION_BOND_STATE_CHANGED with no EXTRA_DEVICE");
      int i;
      CachedBluetoothDevice localCachedBluetoothDevice;
      do
      {
        return;
        i = paramIntent.getIntExtra("android.bluetooth.device.extra.BOND_STATE", -2147483648);
        localCachedBluetoothDevice = BluetoothEventManager.this.mDeviceManager.findDevice(paramBluetoothDevice);
        if (localCachedBluetoothDevice == null)
        {
          Log.w("BluetoothEventManager", "CachedBluetoothDevice for device " + paramBluetoothDevice + " not found, calling readPairedDevices().");
          if (!BluetoothEventManager.this.readPairedDevices())
          {
            Log.e("BluetoothEventManager", "Got bonding state changed for " + paramBluetoothDevice + ", but we have no record of that device.");
            return;
          }
          localCachedBluetoothDevice = BluetoothEventManager.this.mDeviceManager.findDevice(paramBluetoothDevice);
          if (localCachedBluetoothDevice == null)
          {
            Log.e("BluetoothEventManager", "Got bonding state changed for " + paramBluetoothDevice + ", but device not added in cache.");
            return;
          }
        }
        synchronized (BluetoothEventManager.this.mCallbacks)
        {
          Iterator localIterator = BluetoothEventManager.this.mCallbacks.iterator();
          if (localIterator.hasNext())
            ((BluetoothCallback)localIterator.next()).onDeviceBondStateChanged(localCachedBluetoothDevice, i);
        }
        localCachedBluetoothDevice.onBondingStateChanged(i);
      }
      while (i != 10);
      if (paramBluetoothDevice.isBluetoothDock())
      {
        LocalBluetoothPreferences.removeDockAutoConnectSetting(paramContext, paramBluetoothDevice.getAddress());
        if (!paramBluetoothDevice.getAddress().equals(BluetoothEventManager.getDockedDeviceAddress(paramContext)))
          localCachedBluetoothDevice.setVisible(false);
      }
      int j = paramIntent.getIntExtra("android.bluetooth.device.extra.REASON", -2147483648);
      showUnbondMessage(paramContext, localCachedBluetoothDevice.getName(), j);
    }
  }

  private class ClassChangedHandler
    implements BluetoothEventManager.Handler
  {
    private ClassChangedHandler()
    {
    }

    public void onReceive(Context paramContext, Intent paramIntent, BluetoothDevice paramBluetoothDevice)
    {
      BluetoothEventManager.this.mDeviceManager.onBtClassChanged(paramBluetoothDevice);
    }
  }

  private class DeviceDisappearedHandler
    implements BluetoothEventManager.Handler
  {
    private DeviceDisappearedHandler()
    {
    }

    public void onReceive(Context paramContext, Intent paramIntent, BluetoothDevice paramBluetoothDevice)
    {
      CachedBluetoothDevice localCachedBluetoothDevice = BluetoothEventManager.this.mDeviceManager.findDevice(paramBluetoothDevice);
      if (localCachedBluetoothDevice == null)
        Log.w("BluetoothEventManager", "received ACTION_DISAPPEARED for an unknown device: " + paramBluetoothDevice);
      while (!CachedBluetoothDeviceManager.onDeviceDisappeared(localCachedBluetoothDevice))
        return;
      synchronized (BluetoothEventManager.this.mCallbacks)
      {
        Iterator localIterator = BluetoothEventManager.this.mCallbacks.iterator();
        if (localIterator.hasNext())
          ((BluetoothCallback)localIterator.next()).onDeviceDeleted(localCachedBluetoothDevice);
      }
    }
  }

  private class DeviceFoundHandler
    implements BluetoothEventManager.Handler
  {
    private DeviceFoundHandler()
    {
    }

    public void onReceive(Context paramContext, Intent paramIntent, BluetoothDevice paramBluetoothDevice)
    {
      short s = paramIntent.getShortExtra("android.bluetooth.device.extra.RSSI", (short)-32768);
      BluetoothClass localBluetoothClass = (BluetoothClass)paramIntent.getParcelableExtra("android.bluetooth.device.extra.CLASS");
      String str = paramIntent.getStringExtra("android.bluetooth.device.extra.NAME");
      CachedBluetoothDevice localCachedBluetoothDevice = BluetoothEventManager.this.mDeviceManager.findDevice(paramBluetoothDevice);
      if (localCachedBluetoothDevice == null)
      {
        localCachedBluetoothDevice = BluetoothEventManager.this.mDeviceManager.addDevice(BluetoothEventManager.this.mLocalAdapter, BluetoothEventManager.this.mProfileManager, paramBluetoothDevice);
        Log.d("BluetoothEventManager", "DeviceFoundHandler created new CachedBluetoothDevice: " + localCachedBluetoothDevice);
        BluetoothEventManager.this.dispatchDeviceAdded(localCachedBluetoothDevice);
      }
      localCachedBluetoothDevice.setRssi(s);
      localCachedBluetoothDevice.setBtClass(localBluetoothClass);
      localCachedBluetoothDevice.setName(str);
      localCachedBluetoothDevice.setVisible(true);
    }
  }

  private class DockEventHandler
    implements BluetoothEventManager.Handler
  {
    private DockEventHandler()
    {
    }

    public void onReceive(Context paramContext, Intent paramIntent, BluetoothDevice paramBluetoothDevice)
    {
      if ((paramIntent.getIntExtra("android.intent.extra.DOCK_STATE", 1) == 0) && (paramBluetoothDevice != null) && (paramBluetoothDevice.getBondState() == 10))
      {
        CachedBluetoothDevice localCachedBluetoothDevice = BluetoothEventManager.this.mDeviceManager.findDevice(paramBluetoothDevice);
        if (localCachedBluetoothDevice != null)
          localCachedBluetoothDevice.setVisible(false);
      }
    }
  }

  static abstract interface Handler
  {
    public abstract void onReceive(Context paramContext, Intent paramIntent, BluetoothDevice paramBluetoothDevice);
  }

  private class NameChangedHandler
    implements BluetoothEventManager.Handler
  {
    private NameChangedHandler()
    {
    }

    public void onReceive(Context paramContext, Intent paramIntent, BluetoothDevice paramBluetoothDevice)
    {
      BluetoothEventManager.this.mDeviceManager.onDeviceNameUpdated(paramBluetoothDevice);
    }
  }

  private class PairingCancelHandler
    implements BluetoothEventManager.Handler
  {
    private PairingCancelHandler()
    {
    }

    public void onReceive(Context paramContext, Intent paramIntent, BluetoothDevice paramBluetoothDevice)
    {
      if (paramBluetoothDevice == null)
      {
        Log.e("BluetoothEventManager", "ACTION_PAIRING_CANCEL with no EXTRA_DEVICE");
        return;
      }
      Utils.showError(paramContext, BluetoothEventManager.this.mDeviceManager.findDevice(paramBluetoothDevice).getName(), 2131427719);
    }
  }

  private class ScanningStateChangedHandler
    implements BluetoothEventManager.Handler
  {
    private final boolean mStarted;

    ScanningStateChangedHandler(boolean arg2)
    {
      boolean bool;
      this.mStarted = bool;
    }

    public void onReceive(Context paramContext, Intent paramIntent, BluetoothDevice paramBluetoothDevice)
    {
      synchronized (BluetoothEventManager.this.mCallbacks)
      {
        Iterator localIterator = BluetoothEventManager.this.mCallbacks.iterator();
        if (localIterator.hasNext())
          ((BluetoothCallback)localIterator.next()).onScanningStateChanged(this.mStarted);
      }
      BluetoothEventManager.this.mDeviceManager.onScanningStateChanged(this.mStarted);
      LocalBluetoothPreferences.persistDiscoveringTimestamp(paramContext);
    }
  }

  private class UuidChangedHandler
    implements BluetoothEventManager.Handler
  {
    private UuidChangedHandler()
    {
    }

    public void onReceive(Context paramContext, Intent paramIntent, BluetoothDevice paramBluetoothDevice)
    {
      BluetoothEventManager.this.mDeviceManager.onUuidChanged(paramBluetoothDevice);
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.bluetooth.BluetoothEventManager
 * JD-Core Version:    0.6.2
 */