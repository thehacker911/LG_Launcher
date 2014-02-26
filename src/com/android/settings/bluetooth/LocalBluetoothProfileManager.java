package com.android.settings.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothUuid;
import android.content.Context;
import android.content.Intent;
import android.os.ParcelUuid;
import android.util.Log;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

final class LocalBluetoothProfileManager
{
  private A2dpProfile mA2dpProfile;
  private final Context mContext;
  private final CachedBluetoothDeviceManager mDeviceManager;
  private final BluetoothEventManager mEventManager;
  private HeadsetProfile mHeadsetProfile;
  private final HidProfile mHidProfile;
  private final LocalBluetoothAdapter mLocalAdapter;
  private MapProfile mMapProfile;
  private OppProfile mOppProfile;
  private final PanProfile mPanProfile;
  private final PbapServerProfile mPbapProfile;
  private final Map<String, LocalBluetoothProfile> mProfileNameMap = new HashMap();
  private final Collection<ServiceListener> mServiceListeners = new ArrayList();

  LocalBluetoothProfileManager(Context paramContext, LocalBluetoothAdapter paramLocalBluetoothAdapter, CachedBluetoothDeviceManager paramCachedBluetoothDeviceManager, BluetoothEventManager paramBluetoothEventManager)
  {
    this.mContext = paramContext;
    this.mLocalAdapter = paramLocalBluetoothAdapter;
    this.mDeviceManager = paramCachedBluetoothDeviceManager;
    this.mEventManager = paramBluetoothEventManager;
    this.mLocalAdapter.setProfileManager(this);
    this.mEventManager.setProfileManager(this);
    ParcelUuid[] arrayOfParcelUuid = paramLocalBluetoothAdapter.getUuids();
    if (arrayOfParcelUuid != null)
      updateLocalProfiles(arrayOfParcelUuid);
    this.mHidProfile = new HidProfile(paramContext, this.mLocalAdapter, this.mDeviceManager, this);
    addProfile(this.mHidProfile, "HID", "android.bluetooth.input.profile.action.CONNECTION_STATE_CHANGED");
    this.mPanProfile = new PanProfile(paramContext);
    addPanProfile(this.mPanProfile, "PAN", "android.bluetooth.pan.profile.action.CONNECTION_STATE_CHANGED");
    Log.d("LocalBluetoothProfileManager", "Adding local MAP profile");
    this.mMapProfile = new MapProfile(this.mContext, this.mLocalAdapter, this.mDeviceManager, this);
    addProfile(this.mMapProfile, "MAP", "android.bluetooth.map.profile.action.CONNECTION_STATE_CHANGED");
    this.mPbapProfile = new PbapServerProfile(paramContext);
    Log.d("LocalBluetoothProfileManager", "LocalBluetoothProfileManager construction complete");
  }

  private void addPanProfile(LocalBluetoothProfile paramLocalBluetoothProfile, String paramString1, String paramString2)
  {
    this.mEventManager.addProfileHandler(paramString2, new PanStateChangedHandler(paramLocalBluetoothProfile));
    this.mProfileNameMap.put(paramString1, paramLocalBluetoothProfile);
  }

  private void addProfile(LocalBluetoothProfile paramLocalBluetoothProfile, String paramString1, String paramString2)
  {
    this.mEventManager.addProfileHandler(paramString2, new StateChangedHandler(paramLocalBluetoothProfile));
    this.mProfileNameMap.put(paramString1, paramLocalBluetoothProfile);
  }

  void addServiceListener(ServiceListener paramServiceListener)
  {
    this.mServiceListeners.add(paramServiceListener);
  }

  void callServiceConnectedListeners()
  {
    Iterator localIterator = this.mServiceListeners.iterator();
    while (localIterator.hasNext())
      ((ServiceListener)localIterator.next()).onServiceConnected();
  }

  void callServiceDisconnectedListeners()
  {
    Iterator localIterator = this.mServiceListeners.iterator();
    while (localIterator.hasNext())
      ((ServiceListener)localIterator.next()).onServiceDisconnected();
  }

  A2dpProfile getA2dpProfile()
  {
    return this.mA2dpProfile;
  }

  HeadsetProfile getHeadsetProfile()
  {
    return this.mHeadsetProfile;
  }

  PbapServerProfile getPbapProfile()
  {
    return this.mPbapProfile;
  }

  LocalBluetoothProfile getProfileByName(String paramString)
  {
    return (LocalBluetoothProfile)this.mProfileNameMap.get(paramString);
  }

  public boolean isManagerReady()
  {
    try
    {
      HeadsetProfile localHeadsetProfile = this.mHeadsetProfile;
      boolean bool2;
      if (localHeadsetProfile != null)
      {
        boolean bool1 = localHeadsetProfile.isProfileReady();
        bool2 = bool1;
      }
      while (true)
      {
        return bool2;
        A2dpProfile localA2dpProfile = this.mA2dpProfile;
        if (localA2dpProfile != null)
        {
          boolean bool3 = localA2dpProfile.isProfileReady();
          bool2 = bool3;
        }
        else
        {
          bool2 = false;
        }
      }
    }
    finally
    {
    }
  }

  void removeServiceListener(ServiceListener paramServiceListener)
  {
    this.mServiceListeners.remove(paramServiceListener);
  }

  void setBluetoothStateOn()
  {
    ParcelUuid[] arrayOfParcelUuid = this.mLocalAdapter.getUuids();
    if (arrayOfParcelUuid != null)
      updateLocalProfiles(arrayOfParcelUuid);
    this.mEventManager.readPairedDevices();
  }

  void updateLocalProfiles(ParcelUuid[] paramArrayOfParcelUuid)
  {
    if (BluetoothUuid.isUuidPresent(paramArrayOfParcelUuid, BluetoothUuid.AudioSource))
    {
      if (this.mA2dpProfile == null)
      {
        Log.d("LocalBluetoothProfileManager", "Adding local A2DP profile");
        this.mA2dpProfile = new A2dpProfile(this.mContext, this.mLocalAdapter, this.mDeviceManager, this);
        addProfile(this.mA2dpProfile, "A2DP", "android.bluetooth.a2dp.profile.action.CONNECTION_STATE_CHANGED");
      }
      if ((!BluetoothUuid.isUuidPresent(paramArrayOfParcelUuid, BluetoothUuid.Handsfree_AG)) && (!BluetoothUuid.isUuidPresent(paramArrayOfParcelUuid, BluetoothUuid.HSP_AG)))
        break label212;
      if (this.mHeadsetProfile == null)
      {
        Log.d("LocalBluetoothProfileManager", "Adding local HEADSET profile");
        this.mHeadsetProfile = new HeadsetProfile(this.mContext, this.mLocalAdapter, this.mDeviceManager, this);
        addProfile(this.mHeadsetProfile, "HEADSET", "android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED");
      }
      label132: if (!BluetoothUuid.isUuidPresent(paramArrayOfParcelUuid, BluetoothUuid.ObexObjectPush))
        break label231;
      if (this.mOppProfile == null)
      {
        Log.d("LocalBluetoothProfileManager", "Adding local OPP profile");
        this.mOppProfile = new OppProfile();
        this.mProfileNameMap.put("OPP", this.mOppProfile);
      }
    }
    while (true)
    {
      this.mEventManager.registerProfileIntentReceiver();
      return;
      if (this.mA2dpProfile == null)
        break;
      Log.w("LocalBluetoothProfileManager", "Warning: A2DP profile was previously added but the UUID is now missing.");
      break;
      label212: if (this.mHeadsetProfile == null)
        break label132;
      Log.w("LocalBluetoothProfileManager", "Warning: HEADSET profile was previously added but the UUID is now missing.");
      break label132;
      label231: if (this.mOppProfile != null)
        Log.w("LocalBluetoothProfileManager", "Warning: OPP profile was previously added but the UUID is now missing.");
    }
  }

  void updateProfiles(ParcelUuid[] paramArrayOfParcelUuid1, ParcelUuid[] paramArrayOfParcelUuid2, Collection<LocalBluetoothProfile> paramCollection1, Collection<LocalBluetoothProfile> paramCollection2, boolean paramBoolean, BluetoothDevice paramBluetoothDevice)
  {
    while (true)
    {
      try
      {
        paramCollection2.clear();
        paramCollection2.addAll(paramCollection1);
        paramCollection1.clear();
        if (paramArrayOfParcelUuid1 == null)
          return;
        if ((this.mHeadsetProfile != null) && (((BluetoothUuid.isUuidPresent(paramArrayOfParcelUuid2, BluetoothUuid.HSP_AG)) && (BluetoothUuid.isUuidPresent(paramArrayOfParcelUuid1, BluetoothUuid.HSP))) || ((BluetoothUuid.isUuidPresent(paramArrayOfParcelUuid2, BluetoothUuid.Handsfree_AG)) && (BluetoothUuid.isUuidPresent(paramArrayOfParcelUuid1, BluetoothUuid.Handsfree)))))
        {
          paramCollection1.add(this.mHeadsetProfile);
          paramCollection2.remove(this.mHeadsetProfile);
        }
        if ((BluetoothUuid.containsAnyUuid(paramArrayOfParcelUuid1, A2dpProfile.SINK_UUIDS)) && (this.mA2dpProfile != null))
        {
          paramCollection1.add(this.mA2dpProfile);
          paramCollection2.remove(this.mA2dpProfile);
        }
        if ((BluetoothUuid.isUuidPresent(paramArrayOfParcelUuid1, BluetoothUuid.ObexObjectPush)) && (this.mOppProfile != null))
        {
          paramCollection1.add(this.mOppProfile);
          paramCollection2.remove(this.mOppProfile);
        }
        if (((BluetoothUuid.isUuidPresent(paramArrayOfParcelUuid1, BluetoothUuid.Hid)) || (BluetoothUuid.isUuidPresent(paramArrayOfParcelUuid1, BluetoothUuid.Hogp))) && (this.mHidProfile != null))
        {
          paramCollection1.add(this.mHidProfile);
          paramCollection2.remove(this.mHidProfile);
        }
        if (paramBoolean)
          Log.d("LocalBluetoothProfileManager", "Valid PAN-NAP connection exists.");
        if ((BluetoothUuid.isUuidPresent(paramArrayOfParcelUuid1, BluetoothUuid.NAP)) && (this.mPanProfile != null))
        {
          paramCollection1.add(this.mPanProfile);
          paramCollection2.remove(this.mPanProfile);
          if ((this.mMapProfile == null) || (this.mMapProfile.getConnectionStatus(paramBluetoothDevice) != 2))
            continue;
          paramCollection1.add(this.mMapProfile);
          paramCollection2.remove(this.mMapProfile);
          this.mMapProfile.setPreferred(paramBluetoothDevice, true);
          continue;
        }
      }
      finally
      {
      }
      if (!paramBoolean);
    }
  }

  private class PanStateChangedHandler extends LocalBluetoothProfileManager.StateChangedHandler
  {
    PanStateChangedHandler(LocalBluetoothProfile arg2)
    {
      super(localLocalBluetoothProfile);
    }

    public void onReceive(Context paramContext, Intent paramIntent, BluetoothDevice paramBluetoothDevice)
    {
      ((PanProfile)this.mProfile).setLocalRole(paramBluetoothDevice, paramIntent.getIntExtra("android.bluetooth.pan.extra.LOCAL_ROLE", 0));
      super.onReceive(paramContext, paramIntent, paramBluetoothDevice);
    }
  }

  public static abstract interface ServiceListener
  {
    public abstract void onServiceConnected();

    public abstract void onServiceDisconnected();
  }

  private class StateChangedHandler
    implements BluetoothEventManager.Handler
  {
    final LocalBluetoothProfile mProfile;

    StateChangedHandler(LocalBluetoothProfile arg2)
    {
      Object localObject;
      this.mProfile = localObject;
    }

    public void onReceive(Context paramContext, Intent paramIntent, BluetoothDevice paramBluetoothDevice)
    {
      CachedBluetoothDevice localCachedBluetoothDevice = LocalBluetoothProfileManager.this.mDeviceManager.findDevice(paramBluetoothDevice);
      if (localCachedBluetoothDevice == null)
      {
        Log.w("LocalBluetoothProfileManager", "StateChangedHandler found new device: " + paramBluetoothDevice);
        localCachedBluetoothDevice = LocalBluetoothProfileManager.this.mDeviceManager.addDevice(LocalBluetoothProfileManager.this.mLocalAdapter, LocalBluetoothProfileManager.this, paramBluetoothDevice);
      }
      int i = paramIntent.getIntExtra("android.bluetooth.profile.extra.STATE", 0);
      int j = paramIntent.getIntExtra("android.bluetooth.profile.extra.PREVIOUS_STATE", 0);
      if ((i == 0) && (j == 1))
        Log.i("LocalBluetoothProfileManager", "Failed to connect " + this.mProfile + " device");
      localCachedBluetoothDevice.onProfileStateChanged(this.mProfile, i);
      localCachedBluetoothDevice.refresh();
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.bluetooth.LocalBluetoothProfileManager
 * JD-Core Version:    0.6.2
 */