package com.android.settings.bluetooth;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothDevice;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserManager;
import android.preference.PreferenceFragment;
import com.android.settings.SettingsPreferenceFragment;

public final class DevicePickerFragment extends DeviceListPreferenceFragment
{
  private String mLaunchClass;
  private String mLaunchPackage;
  private boolean mNeedAuth;
  private boolean mStartScanOnResume;

  public DevicePickerFragment()
  {
    super(null);
  }

  private void sendDevicePickedIntent(BluetoothDevice paramBluetoothDevice)
  {
    Intent localIntent = new Intent("android.bluetooth.devicepicker.action.DEVICE_SELECTED");
    localIntent.putExtra("android.bluetooth.device.extra.DEVICE", paramBluetoothDevice);
    if ((this.mLaunchPackage != null) && (this.mLaunchClass != null))
      localIntent.setClassName(this.mLaunchPackage, this.mLaunchClass);
    getActivity().sendBroadcast(localIntent);
  }

  void addPreferencesForActivity()
  {
    addPreferencesFromResource(2131034129);
    Intent localIntent = getActivity().getIntent();
    this.mNeedAuth = localIntent.getBooleanExtra("android.bluetooth.devicepicker.extra.NEED_AUTH", false);
    setFilter(localIntent.getIntExtra("android.bluetooth.devicepicker.extra.FILTER_TYPE", 0));
    this.mLaunchPackage = localIntent.getStringExtra("android.bluetooth.devicepicker.extra.LAUNCH_PACKAGE");
    this.mLaunchClass = localIntent.getStringExtra("android.bluetooth.devicepicker.extra.DEVICE_PICKER_LAUNCH_CLASS");
  }

  public void onBluetoothStateChanged(int paramInt)
  {
    super.onBluetoothStateChanged(paramInt);
    if (paramInt == 12)
      this.mLocalAdapter.startScanning(false);
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    getActivity().setTitle(getString(2131427458));
    if ((!((UserManager)getSystemService("user")).hasUserRestriction("no_config_bluetooth")) && (paramBundle == null));
    for (boolean bool = true; ; bool = false)
    {
      this.mStartScanOnResume = bool;
      return;
    }
  }

  public void onDeviceBondStateChanged(CachedBluetoothDevice paramCachedBluetoothDevice, int paramInt)
  {
    if (paramInt == 12)
    {
      BluetoothDevice localBluetoothDevice = paramCachedBluetoothDevice.getDevice();
      if (localBluetoothDevice.equals(this.mSelectedDevice))
      {
        sendDevicePickedIntent(localBluetoothDevice);
        finish();
      }
    }
  }

  void onDevicePreferenceClick(BluetoothDevicePreference paramBluetoothDevicePreference)
  {
    this.mLocalAdapter.stopScanning();
    LocalBluetoothPreferences.persistSelectedDeviceInPicker(getActivity(), this.mSelectedDevice.getAddress());
    if ((paramBluetoothDevicePreference.getCachedDevice().getBondState() == 12) || (!this.mNeedAuth))
    {
      sendDevicePickedIntent(this.mSelectedDevice);
      finish();
      return;
    }
    super.onDevicePreferenceClick(paramBluetoothDevicePreference);
  }

  public void onResume()
  {
    super.onResume();
    addCachedDevices();
    if (this.mStartScanOnResume)
    {
      this.mLocalAdapter.startScanning(true);
      this.mStartScanOnResume = false;
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.bluetooth.DevicePickerFragment
 * JD-Core Version:    0.6.2
 */