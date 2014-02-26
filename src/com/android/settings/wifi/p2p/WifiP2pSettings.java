package com.android.settings.wifi.p2p;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pGroupList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.GroupInfoListener;
import android.net.wifi.p2p.WifiP2pManager.PersistentGroupInfoListener;
import android.os.Bundle;
import android.os.SystemProperties;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import com.android.settings.SettingsPreferenceFragment;
import java.util.Collection;
import java.util.Iterator;

public class WifiP2pSettings extends SettingsPreferenceFragment
  implements WifiP2pManager.GroupInfoListener, WifiP2pManager.PersistentGroupInfoListener
{
  private DialogInterface.OnClickListener mCancelConnectListener;
  private WifiP2pManager.Channel mChannel;
  private int mConnectedDevices;
  private WifiP2pGroup mConnectedGroup;
  private DialogInterface.OnClickListener mDeleteGroupListener;
  private EditText mDeviceNameText;
  private DialogInterface.OnClickListener mDisconnectListener;
  private final IntentFilter mIntentFilter = new IntentFilter();
  private boolean mLastGroupFormed = false;
  private WifiP2pDeviceList mPeers = new WifiP2pDeviceList();
  private PreferenceGroup mPeersGroup;
  private PreferenceGroup mPersistentGroup;
  private final BroadcastReceiver mReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      int i = 1;
      String str = paramAnonymousIntent.getAction();
      if ("android.net.wifi.p2p.STATE_CHANGED".equals(str))
      {
        WifiP2pSettings localWifiP2pSettings = WifiP2pSettings.this;
        if (paramAnonymousIntent.getIntExtra("wifi_p2p_state", i) == 2)
        {
          WifiP2pSettings.access$002(localWifiP2pSettings, i);
          WifiP2pSettings.this.handleP2pStateChanged();
        }
      }
      label206: 
      do
      {
        int j;
        do
        {
          return;
          j = 0;
          break;
          if ("android.net.wifi.p2p.PEERS_CHANGED".equals(str))
          {
            WifiP2pSettings.access$202(WifiP2pSettings.this, (WifiP2pDeviceList)paramAnonymousIntent.getParcelableExtra("wifiP2pDeviceList"));
            WifiP2pSettings.this.handlePeersChanged();
            return;
          }
          if (!"android.net.wifi.p2p.CONNECTION_STATE_CHANGE".equals(str))
            break label206;
        }
        while (WifiP2pSettings.this.mWifiP2pManager == null);
        NetworkInfo localNetworkInfo = (NetworkInfo)paramAnonymousIntent.getParcelableExtra("networkInfo");
        WifiP2pInfo localWifiP2pInfo = (WifiP2pInfo)paramAnonymousIntent.getParcelableExtra("wifiP2pInfo");
        if (WifiP2pSettings.this.mWifiP2pManager != null)
          WifiP2pSettings.this.mWifiP2pManager.requestGroupInfo(WifiP2pSettings.this.mChannel, WifiP2pSettings.this);
        if (localNetworkInfo.isConnected());
        while (true)
        {
          WifiP2pSettings.access$602(WifiP2pSettings.this, localWifiP2pInfo.groupFormed);
          return;
          if (WifiP2pSettings.this.mLastGroupFormed != j)
            WifiP2pSettings.this.startSearch();
        }
        if ("android.net.wifi.p2p.THIS_DEVICE_CHANGED".equals(str))
        {
          WifiP2pSettings.access$802(WifiP2pSettings.this, (WifiP2pDevice)paramAnonymousIntent.getParcelableExtra("wifiP2pDevice"));
          WifiP2pSettings.this.updateDevicePref();
          return;
        }
        if ("android.net.wifi.p2p.DISCOVERY_STATE_CHANGE".equals(str))
        {
          if (paramAnonymousIntent.getIntExtra("discoveryState", j) == 2)
          {
            WifiP2pSettings.this.updateSearchMenu(j);
            return;
          }
          WifiP2pSettings.this.updateSearchMenu(false);
          return;
        }
      }
      while ((!"android.net.wifi.p2p.PERSISTENT_GROUPS_CHANGED".equals(str)) || (WifiP2pSettings.this.mWifiP2pManager == null));
      WifiP2pSettings.this.mWifiP2pManager.requestPersistentGroupInfo(WifiP2pSettings.this.mChannel, WifiP2pSettings.this);
    }
  };
  private DialogInterface.OnClickListener mRenameListener;
  private String mSavedDeviceName;
  private WifiP2pPersistentGroup mSelectedGroup;
  private String mSelectedGroupName;
  private WifiP2pPeer mSelectedWifiPeer;
  private WifiP2pDevice mThisDevice;
  private Preference mThisDevicePref;
  private boolean mWifiP2pEnabled;
  private WifiP2pManager mWifiP2pManager;
  private boolean mWifiP2pSearching;

  private void handleP2pStateChanged()
  {
    updateSearchMenu(false);
    if (this.mWifiP2pEnabled)
    {
      PreferenceScreen localPreferenceScreen = getPreferenceScreen();
      localPreferenceScreen.removeAll();
      localPreferenceScreen.setOrderingAsAdded(true);
      localPreferenceScreen.addPreference(this.mThisDevicePref);
      this.mPeersGroup.setEnabled(true);
      localPreferenceScreen.addPreference(this.mPeersGroup);
      this.mPersistentGroup.setEnabled(true);
      localPreferenceScreen.addPreference(this.mPersistentGroup);
    }
  }

  private void handlePeersChanged()
  {
    this.mPeersGroup.removeAll();
    this.mConnectedDevices = 0;
    Iterator localIterator = this.mPeers.getDeviceList().iterator();
    while (localIterator.hasNext())
    {
      WifiP2pDevice localWifiP2pDevice = (WifiP2pDevice)localIterator.next();
      this.mPeersGroup.addPreference(new WifiP2pPeer(getActivity(), localWifiP2pDevice));
      if (localWifiP2pDevice.status == 0)
        this.mConnectedDevices = (1 + this.mConnectedDevices);
    }
  }

  private void startSearch()
  {
    if ((this.mWifiP2pManager != null) && (!this.mWifiP2pSearching))
      this.mWifiP2pManager.discoverPeers(this.mChannel, new WifiP2pManager.ActionListener()
      {
        public void onFailure(int paramAnonymousInt)
        {
        }

        public void onSuccess()
        {
        }
      });
  }

  private void updateDevicePref()
  {
    if (this.mThisDevice != null)
    {
      if (!TextUtils.isEmpty(this.mThisDevice.deviceName))
        break label59;
      this.mThisDevicePref.setTitle(this.mThisDevice.deviceAddress);
    }
    while (true)
    {
      this.mThisDevicePref.setPersistent(false);
      this.mThisDevicePref.setEnabled(true);
      this.mThisDevicePref.setSelectable(false);
      return;
      label59: this.mThisDevicePref.setTitle(this.mThisDevice.deviceName);
    }
  }

  private void updateSearchMenu(boolean paramBoolean)
  {
    this.mWifiP2pSearching = paramBoolean;
    Activity localActivity = getActivity();
    if (localActivity != null)
      localActivity.invalidateOptionsMenu();
  }

  public void onActivityCreated(Bundle paramBundle)
  {
    addPreferencesFromResource(2131034175);
    this.mIntentFilter.addAction("android.net.wifi.p2p.STATE_CHANGED");
    this.mIntentFilter.addAction("android.net.wifi.p2p.PEERS_CHANGED");
    this.mIntentFilter.addAction("android.net.wifi.p2p.CONNECTION_STATE_CHANGE");
    this.mIntentFilter.addAction("android.net.wifi.p2p.THIS_DEVICE_CHANGED");
    this.mIntentFilter.addAction("android.net.wifi.p2p.DISCOVERY_STATE_CHANGE");
    this.mIntentFilter.addAction("android.net.wifi.p2p.PERSISTENT_GROUPS_CHANGED");
    Activity localActivity = getActivity();
    this.mWifiP2pManager = ((WifiP2pManager)getSystemService("wifip2p"));
    if (this.mWifiP2pManager != null)
    {
      this.mChannel = this.mWifiP2pManager.initialize(localActivity, getActivity().getMainLooper(), null);
      if (this.mChannel == null)
      {
        Log.e("WifiP2pSettings", "Failed to set up connection with wifi p2p service");
        this.mWifiP2pManager = null;
      }
    }
    while (true)
    {
      if ((paramBundle != null) && (paramBundle.containsKey("PEER_STATE")))
      {
        WifiP2pDevice localWifiP2pDevice = (WifiP2pDevice)paramBundle.getParcelable("PEER_STATE");
        this.mSelectedWifiPeer = new WifiP2pPeer(getActivity(), localWifiP2pDevice);
      }
      if ((paramBundle != null) && (paramBundle.containsKey("DEV_NAME")))
        this.mSavedDeviceName = paramBundle.getString("DEV_NAME");
      if ((paramBundle != null) && (paramBundle.containsKey("GROUP_NAME")))
        this.mSelectedGroupName = paramBundle.getString("GROUP_NAME");
      this.mRenameListener = new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          if ((paramAnonymousInt == -1) && (WifiP2pSettings.this.mWifiP2pManager != null))
            WifiP2pSettings.this.mWifiP2pManager.setDeviceName(WifiP2pSettings.this.mChannel, WifiP2pSettings.this.mDeviceNameText.getText().toString(), new WifiP2pManager.ActionListener()
            {
              public void onFailure(int paramAnonymous2Int)
              {
                Toast.makeText(WifiP2pSettings.this.getActivity(), 2131427951, 1).show();
              }

              public void onSuccess()
              {
              }
            });
        }
      };
      this.mDisconnectListener = new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          if ((paramAnonymousInt == -1) && (WifiP2pSettings.this.mWifiP2pManager != null))
            WifiP2pSettings.this.mWifiP2pManager.removeGroup(WifiP2pSettings.this.mChannel, new WifiP2pManager.ActionListener()
            {
              public void onFailure(int paramAnonymous2Int)
              {
              }

              public void onSuccess()
              {
              }
            });
        }
      };
      this.mCancelConnectListener = new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          if ((paramAnonymousInt == -1) && (WifiP2pSettings.this.mWifiP2pManager != null))
            WifiP2pSettings.this.mWifiP2pManager.cancelConnect(WifiP2pSettings.this.mChannel, new WifiP2pManager.ActionListener()
            {
              public void onFailure(int paramAnonymous2Int)
              {
              }

              public void onSuccess()
              {
              }
            });
        }
      };
      this.mDeleteGroupListener = new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          if (paramAnonymousInt == -1)
            if ((WifiP2pSettings.this.mWifiP2pManager != null) && (WifiP2pSettings.this.mSelectedGroup != null))
            {
              WifiP2pSettings.this.mWifiP2pManager.deletePersistentGroup(WifiP2pSettings.this.mChannel, WifiP2pSettings.this.mSelectedGroup.getNetworkId(), new WifiP2pManager.ActionListener()
              {
                public void onFailure(int paramAnonymous2Int)
                {
                }

                public void onSuccess()
                {
                }
              });
              WifiP2pSettings.access$1202(WifiP2pSettings.this, null);
            }
          while (paramAnonymousInt != -2)
            return;
          WifiP2pSettings.access$1202(WifiP2pSettings.this, null);
        }
      };
      setHasOptionsMenu(true);
      PreferenceScreen localPreferenceScreen = getPreferenceScreen();
      localPreferenceScreen.removeAll();
      localPreferenceScreen.setOrderingAsAdded(true);
      this.mThisDevicePref = new Preference(getActivity());
      localPreferenceScreen.addPreference(this.mThisDevicePref);
      this.mPeersGroup = new PreferenceCategory(getActivity());
      this.mPeersGroup.setTitle(2131427948);
      this.mPersistentGroup = new PreferenceCategory(getActivity());
      this.mPersistentGroup.setTitle(2131427949);
      super.onActivityCreated(paramBundle);
      return;
      Log.e("WifiP2pSettings", "mWifiP2pManager is null !");
    }
  }

  public Dialog onCreateDialog(int paramInt)
  {
    String str2;
    String str3;
    label84: AlertDialog localAlertDialog;
    if (paramInt == 1)
      if (TextUtils.isEmpty(this.mSelectedWifiPeer.device.deviceName))
      {
        str2 = this.mSelectedWifiPeer.device.deviceAddress;
        if (this.mConnectedDevices <= 1)
          break label158;
        Activity localActivity = getActivity();
        Object[] arrayOfObject = new Object[2];
        arrayOfObject[0] = str2;
        arrayOfObject[1] = Integer.valueOf(-1 + this.mConnectedDevices);
        str3 = localActivity.getString(2131427954, arrayOfObject);
        localAlertDialog = new AlertDialog.Builder(getActivity()).setTitle(2131427952).setMessage(str3).setPositiveButton(getActivity().getString(2131428420), this.mDisconnectListener).setNegativeButton(getActivity().getString(2131428421), null).create();
      }
    label158: 
    do
    {
      return localAlertDialog;
      str2 = this.mSelectedWifiPeer.device.deviceName;
      break;
      str3 = getActivity().getString(2131427953, new Object[] { str2 });
      break label84;
      if (paramInt == 2)
      {
        if (TextUtils.isEmpty(this.mSelectedWifiPeer.device.deviceName));
        for (String str1 = this.mSelectedWifiPeer.device.deviceAddress; ; str1 = this.mSelectedWifiPeer.device.deviceName)
          return new AlertDialog.Builder(getActivity()).setTitle(2131427955).setMessage(getActivity().getString(2131427956, new Object[] { str1 })).setPositiveButton(getActivity().getString(2131428420), this.mCancelConnectListener).setNegativeButton(getActivity().getString(2131428421), null).create();
      }
      if (paramInt == 3)
      {
        this.mDeviceNameText = new EditText(getActivity());
        EditText localEditText = this.mDeviceNameText;
        InputFilter[] arrayOfInputFilter = new InputFilter[1];
        arrayOfInputFilter[0] = new InputFilter.LengthFilter(30);
        localEditText.setFilters(arrayOfInputFilter);
        if (this.mSavedDeviceName != null)
        {
          this.mDeviceNameText.setText(this.mSavedDeviceName);
          this.mDeviceNameText.setSelection(this.mSavedDeviceName.length());
        }
        while (true)
        {
          this.mSavedDeviceName = null;
          return new AlertDialog.Builder(getActivity()).setTitle(2131427947).setView(this.mDeviceNameText).setPositiveButton(getActivity().getString(2131428420), this.mRenameListener).setNegativeButton(getActivity().getString(2131428421), null).create();
          if ((this.mThisDevice != null) && (!TextUtils.isEmpty(this.mThisDevice.deviceName)))
          {
            this.mDeviceNameText.setText(this.mThisDevice.deviceName);
            this.mDeviceNameText.setSelection(0, this.mThisDevice.deviceName.length());
          }
        }
      }
      localAlertDialog = null;
    }
    while (paramInt != 4);
    return new AlertDialog.Builder(getActivity()).setMessage(getActivity().getString(2131427957)).setPositiveButton(getActivity().getString(2131428420), this.mDeleteGroupListener).setNegativeButton(getActivity().getString(2131428421), this.mDeleteGroupListener).create();
  }

  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    if (this.mWifiP2pSearching);
    for (int i = 2131427946; ; i = 2131427945)
    {
      paramMenu.add(0, 1, 0, i).setEnabled(this.mWifiP2pEnabled).setShowAsAction(1);
      paramMenu.add(0, 2, 0, 2131427947).setEnabled(this.mWifiP2pEnabled).setShowAsAction(1);
      super.onCreateOptionsMenu(paramMenu, paramMenuInflater);
      return;
    }
  }

  public void onGroupInfoAvailable(WifiP2pGroup paramWifiP2pGroup)
  {
    this.mConnectedGroup = paramWifiP2pGroup;
    updateDevicePref();
  }

  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    switch (paramMenuItem.getItemId())
    {
    default:
      return super.onOptionsItemSelected(paramMenuItem);
    case 1:
      startSearch();
      return true;
    case 2:
    }
    showDialog(3);
    return true;
  }

  public void onPause()
  {
    super.onPause();
    this.mWifiP2pManager.stopPeerDiscovery(this.mChannel, null);
    getActivity().unregisterReceiver(this.mReceiver);
  }

  public void onPersistentGroupInfoAvailable(WifiP2pGroupList paramWifiP2pGroupList)
  {
    this.mPersistentGroup.removeAll();
    Iterator localIterator = paramWifiP2pGroupList.getGroupList().iterator();
    while (localIterator.hasNext())
    {
      WifiP2pGroup localWifiP2pGroup = (WifiP2pGroup)localIterator.next();
      WifiP2pPersistentGroup localWifiP2pPersistentGroup = new WifiP2pPersistentGroup(getActivity(), localWifiP2pGroup);
      this.mPersistentGroup.addPreference(localWifiP2pPersistentGroup);
      if (localWifiP2pPersistentGroup.getGroupName().equals(this.mSelectedGroupName))
      {
        this.mSelectedGroup = localWifiP2pPersistentGroup;
        this.mSelectedGroupName = null;
      }
    }
    if (this.mSelectedGroupName != null)
      Log.w("WifiP2pSettings", " Selected group " + this.mSelectedGroupName + " disappered on next query ");
  }

  public boolean onPreferenceTreeClick(PreferenceScreen paramPreferenceScreen, Preference paramPreference)
  {
    if ((paramPreference instanceof WifiP2pPeer))
    {
      this.mSelectedWifiPeer = ((WifiP2pPeer)paramPreference);
      if (this.mSelectedWifiPeer.device.status == 0)
        showDialog(1);
    }
    while (true)
    {
      return super.onPreferenceTreeClick(paramPreferenceScreen, paramPreference);
      if (this.mSelectedWifiPeer.device.status == 1)
      {
        showDialog(2);
      }
      else
      {
        WifiP2pConfig localWifiP2pConfig = new WifiP2pConfig();
        localWifiP2pConfig.deviceAddress = this.mSelectedWifiPeer.device.deviceAddress;
        int i = SystemProperties.getInt("wifidirect.wps", -1);
        if (i != -1)
          localWifiP2pConfig.wps.setup = i;
        while (true)
        {
          this.mWifiP2pManager.connect(this.mChannel, localWifiP2pConfig, new WifiP2pManager.ActionListener()
          {
            public void onFailure(int paramAnonymousInt)
            {
              Log.e("WifiP2pSettings", " connect fail " + paramAnonymousInt);
              Toast.makeText(WifiP2pSettings.this.getActivity(), 2131427950, 0).show();
            }

            public void onSuccess()
            {
            }
          });
          break;
          if (this.mSelectedWifiPeer.device.wpsPbcSupported())
            localWifiP2pConfig.wps.setup = 0;
          else if (this.mSelectedWifiPeer.device.wpsKeypadSupported())
            localWifiP2pConfig.wps.setup = 2;
          else
            localWifiP2pConfig.wps.setup = 1;
        }
        if ((paramPreference instanceof WifiP2pPersistentGroup))
        {
          this.mSelectedGroup = ((WifiP2pPersistentGroup)paramPreference);
          showDialog(4);
        }
      }
    }
  }

  public void onPrepareOptionsMenu(Menu paramMenu)
  {
    MenuItem localMenuItem1 = paramMenu.findItem(1);
    MenuItem localMenuItem2 = paramMenu.findItem(2);
    if (this.mWifiP2pEnabled)
    {
      localMenuItem1.setEnabled(true);
      localMenuItem2.setEnabled(true);
    }
    while (this.mWifiP2pSearching)
    {
      localMenuItem1.setTitle(2131427946);
      return;
      localMenuItem1.setEnabled(false);
      localMenuItem2.setEnabled(false);
    }
    localMenuItem1.setTitle(2131427945);
  }

  public void onResume()
  {
    super.onResume();
    getActivity().registerReceiver(this.mReceiver, this.mIntentFilter);
  }

  public void onSaveInstanceState(Bundle paramBundle)
  {
    if (this.mSelectedWifiPeer != null)
      paramBundle.putParcelable("PEER_STATE", this.mSelectedWifiPeer.device);
    if (this.mDeviceNameText != null)
      paramBundle.putString("DEV_NAME", this.mDeviceNameText.getText().toString());
    if (this.mSelectedGroup != null)
      paramBundle.putString("GROUP_NAME", this.mSelectedGroup.getGroupName());
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.wifi.p2p.WifiP2pSettings
 * JD-Core Version:    0.6.2
 */