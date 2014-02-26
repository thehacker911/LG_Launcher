package com.android.settings.bluetooth;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import java.util.WeakHashMap;

public final class BluetoothSettings extends DeviceListPreferenceFragment
{
  private boolean mActivityStarted;
  private PreferenceGroup mAvailableDevicesCategory;
  private boolean mAvailableDevicesCategoryIsPresent;
  private BluetoothEnabler mBluetoothEnabler;
  private final View.OnClickListener mDeviceProfilesListener = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      if ((paramAnonymousView.getTag() instanceof CachedBluetoothDevice))
      {
        if (BluetoothSettings.this.isRestrictedAndNotPinProtected())
          return;
        CachedBluetoothDevice localCachedBluetoothDevice = (CachedBluetoothDevice)paramAnonymousView.getTag();
        Bundle localBundle = new Bundle(1);
        localBundle.putParcelable("device", localCachedBluetoothDevice.getDevice());
        ((PreferenceActivity)BluetoothSettings.this.getActivity()).startPreferencePanel(DeviceProfilesSettings.class.getName(), localBundle, 2131427752, null, null, 0);
        return;
      }
      Log.w("BluetoothSettings", "onClick() called for other View: " + paramAnonymousView);
    }
  };
  private BluetoothDiscoverableEnabler mDiscoverableEnabler;
  private TextView mEmptyView;
  private final IntentFilter mIntentFilter = new IntentFilter("android.bluetooth.adapter.action.LOCAL_NAME_CHANGED");
  Preference mMyDevicePreference;
  private PreferenceGroup mPairedDevicesCategory;
  private final BroadcastReceiver mReceiver = new BroadcastReceiver()
  {
    private void updateDeviceName()
    {
      if ((BluetoothSettings.this.mLocalAdapter.isEnabled()) && (BluetoothSettings.this.mMyDevicePreference != null))
        BluetoothSettings.this.mMyDevicePreference.setTitle(BluetoothSettings.this.mLocalAdapter.getName());
    }

    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if (paramAnonymousIntent.getAction().equals("android.bluetooth.adapter.action.LOCAL_NAME_CHANGED"))
        updateDeviceName();
    }
  };

  public BluetoothSettings()
  {
    super("no_config_bluetooth");
  }

  private void addDeviceCategory(PreferenceGroup paramPreferenceGroup, int paramInt, BluetoothDeviceFilter.Filter paramFilter)
  {
    paramPreferenceGroup.setTitle(paramInt);
    getPreferenceScreen().addPreference(paramPreferenceGroup);
    setFilter(paramFilter);
    setDeviceListGroup(paramPreferenceGroup);
    addCachedDevices();
    paramPreferenceGroup.setEnabled(true);
  }

  private void startScanning()
  {
    if (isRestrictedAndNotPinProtected())
      return;
    if (!this.mAvailableDevicesCategoryIsPresent)
      getPreferenceScreen().addPreference(this.mAvailableDevicesCategory);
    this.mLocalAdapter.startScanning(true);
  }

  private void updateContent(int paramInt, boolean paramBoolean)
  {
    PreferenceScreen localPreferenceScreen = getPreferenceScreen();
    int i = 0;
    switch (paramInt)
    {
    default:
    case 12:
    case 13:
    case 10:
    case 11:
    }
    while (true)
    {
      setDeviceListGroup(localPreferenceScreen);
      removeAllDevices();
      this.mEmptyView.setText(i);
      getActivity().invalidateOptionsMenu();
      return;
      localPreferenceScreen.removeAll();
      localPreferenceScreen.setOrderingAsAdded(true);
      this.mDevicePreferenceMap.clear();
      if (this.mMyDevicePreference == null)
        this.mMyDevicePreference = new Preference(getActivity());
      this.mMyDevicePreference.setTitle(this.mLocalAdapter.getName());
      if (getResources().getBoolean(17891385))
      {
        this.mMyDevicePreference.setIcon(2130837571);
        this.mMyDevicePreference.setPersistent(false);
        this.mMyDevicePreference.setEnabled(true);
        localPreferenceScreen.addPreference(this.mMyDevicePreference);
        if ((!isRestrictedAndNotPinProtected()) && (this.mDiscoverableEnabler == null))
        {
          this.mDiscoverableEnabler = new BluetoothDiscoverableEnabler(getActivity(), this.mLocalAdapter, this.mMyDevicePreference);
          this.mDiscoverableEnabler.resume();
          LocalBluetoothManager.getInstance(getActivity()).setDiscoverableEnabler(this.mDiscoverableEnabler);
        }
        if (this.mPairedDevicesCategory != null)
          break label406;
        this.mPairedDevicesCategory = new PreferenceCategory(getActivity());
        label244: addDeviceCategory(this.mPairedDevicesCategory, 2131427728, BluetoothDeviceFilter.BONDED_DEVICE_FILTER);
        int j = this.mPairedDevicesCategory.getPreferenceCount();
        if (this.mDiscoverableEnabler != null)
          this.mDiscoverableEnabler.setNumberOfPairedDevices(j);
        if (this.mAvailableDevicesCategory != null)
          break label416;
        this.mAvailableDevicesCategory = new BluetoothProgressCategory(getActivity(), null);
        label305: if (!isRestrictedAndNotPinProtected())
          addDeviceCategory(this.mAvailableDevicesCategory, 2131427729, BluetoothDeviceFilter.UNBONDED_DEVICE_FILTER);
        int k = this.mAvailableDevicesCategory.getPreferenceCount();
        this.mAvailableDevicesCategoryIsPresent = true;
        if (k == 0)
        {
          localPreferenceScreen.removePreference(this.mAvailableDevicesCategory);
          this.mAvailableDevicesCategoryIsPresent = false;
        }
        if (j == 0)
        {
          localPreferenceScreen.removePreference(this.mPairedDevicesCategory);
          if (paramBoolean != true)
            break label426;
          this.mActivityStarted = false;
          startScanning();
        }
      }
      while (true)
      {
        getActivity().invalidateOptionsMenu();
        return;
        this.mMyDevicePreference.setIcon(2130837577);
        break;
        label406: this.mPairedDevicesCategory.removeAll();
        break label244;
        label416: this.mAvailableDevicesCategory.removeAll();
        break label305;
        label426: if (!this.mAvailableDevicesCategoryIsPresent)
          getPreferenceScreen().addPreference(this.mAvailableDevicesCategory);
      }
      i = 2131427466;
      continue;
      i = 2131427738;
      continue;
      i = 2131427465;
    }
  }

  void addPreferencesForActivity()
  {
    addPreferencesFromResource(2131034121);
    Activity localActivity = getActivity();
    Switch localSwitch = new Switch(localActivity);
    if ((localActivity instanceof PreferenceActivity))
    {
      PreferenceActivity localPreferenceActivity = (PreferenceActivity)localActivity;
      if ((localPreferenceActivity.onIsHidingHeaders()) || (!localPreferenceActivity.onIsMultiPane()))
      {
        localSwitch.setPaddingRelative(0, 0, localActivity.getResources().getDimensionPixelSize(2131558402), 0);
        localActivity.getActionBar().setDisplayOptions(16, 16);
        localActivity.getActionBar().setCustomView(localSwitch, new ActionBar.LayoutParams(-2, -2, 8388629));
      }
    }
    this.mBluetoothEnabler = new BluetoothEnabler(localActivity, localSwitch);
    setHasOptionsMenu(true);
  }

  protected int getHelpResource()
  {
    return 2131429255;
  }

  void initDevicePreference(BluetoothDevicePreference paramBluetoothDevicePreference)
  {
    if (paramBluetoothDevicePreference.getCachedDevice().getBondState() == 12)
      paramBluetoothDevicePreference.setOnSettingsClickListener(this.mDeviceProfilesListener);
  }

  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    if (paramBundle == null);
    for (boolean bool = true; ; bool = false)
    {
      this.mActivityStarted = bool;
      this.mEmptyView = ((TextView)getView().findViewById(16908292));
      getListView().setEmptyView(this.mEmptyView);
      return;
    }
  }

  public void onBluetoothStateChanged(int paramInt)
  {
    super.onBluetoothStateChanged(paramInt);
    updateContent(paramInt, true);
  }

  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    if (this.mLocalAdapter == null);
    while (isRestrictedAndNotPinProtected())
      return;
    boolean bool1;
    int i;
    label48: MenuItem localMenuItem;
    if (this.mLocalAdapter.getBluetoothState() == 12)
    {
      bool1 = true;
      boolean bool2 = this.mLocalAdapter.isDiscovering();
      if (!bool2)
        break label166;
      i = 2131427726;
      localMenuItem = paramMenu.add(0, 1, 0, i);
      if ((!bool1) || (bool2))
        break label174;
    }
    label166: label174: for (boolean bool3 = true; ; bool3 = false)
    {
      localMenuItem.setEnabled(bool3).setShowAsAction(1);
      paramMenu.add(0, 2, 0, 2131427435).setEnabled(bool1).setShowAsAction(0);
      paramMenu.add(0, 3, 0, 2131427426).setEnabled(bool1).setShowAsAction(0);
      paramMenu.add(0, 4, 0, 2131427457).setShowAsAction(0);
      super.onCreateOptionsMenu(paramMenu, paramMenuInflater);
      return;
      bool1 = false;
      break;
      i = 2131427725;
      break label48;
    }
  }

  public void onDeviceBondStateChanged(CachedBluetoothDevice paramCachedBluetoothDevice, int paramInt)
  {
    setDeviceListGroup(getPreferenceScreen());
    removeAllDevices();
    updateContent(this.mLocalAdapter.getBluetoothState(), false);
  }

  void onDevicePreferenceClick(BluetoothDevicePreference paramBluetoothDevicePreference)
  {
    this.mLocalAdapter.stopScanning();
    super.onDevicePreferenceClick(paramBluetoothDevicePreference);
  }

  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    boolean bool = true;
    switch (paramMenuItem.getItemId())
    {
    default:
      bool = super.onOptionsItemSelected(paramMenuItem);
    case 1:
      do
        return bool;
      while (this.mLocalAdapter.getBluetoothState() != 12);
      startScanning();
      return bool;
    case 2:
      new BluetoothNameDialogFragment().show(getFragmentManager(), "rename device");
      return bool;
    case 3:
      new BluetoothVisibilityTimeoutFragment().show(getFragmentManager(), "visibility timeout");
      return bool;
    case 4:
    }
    Intent localIntent = new Intent("android.btopp.intent.action.OPEN_RECEIVED_FILES");
    getActivity().sendBroadcast(localIntent);
    return bool;
  }

  public void onPause()
  {
    super.onPause();
    if (this.mBluetoothEnabler != null)
      this.mBluetoothEnabler.pause();
    getActivity().unregisterReceiver(this.mReceiver);
    if (this.mDiscoverableEnabler != null)
      this.mDiscoverableEnabler.pause();
  }

  public void onResume()
  {
    if (this.mBluetoothEnabler != null)
      this.mBluetoothEnabler.resume();
    super.onResume();
    if (this.mDiscoverableEnabler != null)
      this.mDiscoverableEnabler.resume();
    getActivity().registerReceiver(this.mReceiver, this.mIntentFilter);
    if (this.mLocalAdapter != null)
      updateContent(this.mLocalAdapter.getBluetoothState(), this.mActivityStarted);
  }

  public void onScanningStateChanged(boolean paramBoolean)
  {
    super.onScanningStateChanged(paramBoolean);
    getActivity().invalidateOptionsMenu();
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.bluetooth.BluetoothSettings
 * JD-Core Version:    0.6.2
 */