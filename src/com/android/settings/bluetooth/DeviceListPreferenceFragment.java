package com.android.settings.bluetooth;

import android.app.Fragment;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.util.Log;
import com.android.settings.ProgressCategory;
import com.android.settings.RestrictedSettingsFragment;
import java.util.Collection;
import java.util.Iterator;
import java.util.WeakHashMap;

public abstract class DeviceListPreferenceFragment extends RestrictedSettingsFragment
  implements BluetoothCallback
{
  private PreferenceGroup mDeviceListGroup;
  final WeakHashMap<CachedBluetoothDevice, BluetoothDevicePreference> mDevicePreferenceMap = new WeakHashMap();
  private BluetoothDeviceFilter.Filter mFilter = BluetoothDeviceFilter.ALL_FILTER;
  LocalBluetoothAdapter mLocalAdapter;
  LocalBluetoothManager mLocalManager;
  BluetoothDevice mSelectedDevice;

  DeviceListPreferenceFragment(String paramString)
  {
    super(paramString);
  }

  private void updateProgressUi(boolean paramBoolean)
  {
    if ((this.mDeviceListGroup instanceof BluetoothProgressCategory))
      ((BluetoothProgressCategory)this.mDeviceListGroup).setProgress(paramBoolean);
  }

  void addCachedDevices()
  {
    Iterator localIterator = this.mLocalManager.getCachedDeviceManager().getCachedDevicesCopy().iterator();
    while (localIterator.hasNext())
      onDeviceAdded((CachedBluetoothDevice)localIterator.next());
  }

  abstract void addPreferencesForActivity();

  void createDevicePreference(CachedBluetoothDevice paramCachedBluetoothDevice)
  {
    BluetoothDevicePreference localBluetoothDevicePreference = new BluetoothDevicePreference(getActivity(), paramCachedBluetoothDevice);
    initDevicePreference(localBluetoothDevicePreference);
    this.mDeviceListGroup.addPreference(localBluetoothDevicePreference);
    this.mDevicePreferenceMap.put(paramCachedBluetoothDevice, localBluetoothDevicePreference);
  }

  void initDevicePreference(BluetoothDevicePreference paramBluetoothDevicePreference)
  {
  }

  public void onBluetoothStateChanged(int paramInt)
  {
    if (paramInt == 10)
      updateProgressUi(false);
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mLocalManager = LocalBluetoothManager.getInstance(getActivity());
    if (this.mLocalManager == null)
    {
      Log.e("DeviceListPreferenceFragment", "Bluetooth is not supported on this device");
      return;
    }
    this.mLocalAdapter = this.mLocalManager.getBluetoothAdapter();
    addPreferencesForActivity();
    this.mDeviceListGroup = ((PreferenceCategory)findPreference("bt_device_list"));
  }

  public void onDeviceAdded(CachedBluetoothDevice paramCachedBluetoothDevice)
  {
    if (this.mDevicePreferenceMap.get(paramCachedBluetoothDevice) != null);
    while ((this.mLocalAdapter.getBluetoothState() != 12) || (!this.mFilter.matches(paramCachedBluetoothDevice.getDevice())))
      return;
    createDevicePreference(paramCachedBluetoothDevice);
  }

  public void onDeviceDeleted(CachedBluetoothDevice paramCachedBluetoothDevice)
  {
    BluetoothDevicePreference localBluetoothDevicePreference = (BluetoothDevicePreference)this.mDevicePreferenceMap.remove(paramCachedBluetoothDevice);
    if (localBluetoothDevicePreference != null)
      this.mDeviceListGroup.removePreference(localBluetoothDevicePreference);
  }

  void onDevicePreferenceClick(BluetoothDevicePreference paramBluetoothDevicePreference)
  {
    paramBluetoothDevicePreference.onClicked();
  }

  public void onPause()
  {
    super.onPause();
    if (this.mLocalManager == null)
      return;
    removeAllDevices();
    this.mLocalManager.setForegroundActivity(null);
    this.mLocalManager.getEventManager().unregisterCallback(this);
  }

  public boolean onPreferenceTreeClick(PreferenceScreen paramPreferenceScreen, Preference paramPreference)
  {
    if ("bt_scan".equals(paramPreference.getKey()))
    {
      this.mLocalAdapter.startScanning(true);
      return true;
    }
    if ((paramPreference instanceof BluetoothDevicePreference))
    {
      BluetoothDevicePreference localBluetoothDevicePreference = (BluetoothDevicePreference)paramPreference;
      this.mSelectedDevice = localBluetoothDevicePreference.getCachedDevice().getDevice();
      onDevicePreferenceClick(localBluetoothDevicePreference);
      return true;
    }
    return super.onPreferenceTreeClick(paramPreferenceScreen, paramPreference);
  }

  public void onResume()
  {
    super.onResume();
    if (this.mLocalManager == null)
      return;
    this.mLocalManager.setForegroundActivity(getActivity());
    this.mLocalManager.getEventManager().registerCallback(this);
    updateProgressUi(this.mLocalAdapter.isDiscovering());
  }

  public void onScanningStateChanged(boolean paramBoolean)
  {
    updateProgressUi(paramBoolean);
  }

  void removeAllDevices()
  {
    this.mLocalAdapter.stopScanning();
    this.mDevicePreferenceMap.clear();
    this.mDeviceListGroup.removeAll();
  }

  void setDeviceListGroup(PreferenceGroup paramPreferenceGroup)
  {
    this.mDeviceListGroup = paramPreferenceGroup;
  }

  final void setFilter(int paramInt)
  {
    this.mFilter = BluetoothDeviceFilter.getFilter(paramInt);
  }

  final void setFilter(BluetoothDeviceFilter.Filter paramFilter)
  {
    this.mFilter = paramFilter;
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.bluetooth.DeviceListPreferenceFragment
 * JD-Core Version:    0.6.2
 */