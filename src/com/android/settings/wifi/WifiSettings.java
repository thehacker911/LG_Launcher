package com.android.settings.wifi;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.ActionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.android.settings.RestrictedSettingsFragment;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.wifi.p2p.WifiP2pSettings;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class WifiSettings extends RestrictedSettingsFragment
  implements DialogInterface.OnClickListener
{
  private Bundle mAccessPointSavedState;
  private boolean mAutoFinishOnConnection;
  private WifiManager.ActionListener mConnectListener;
  private final AtomicBoolean mConnected = new AtomicBoolean(false);
  private WifiDialog mDialog;
  private AccessPoint mDlgAccessPoint;
  private boolean mDlgEdit;
  private TextView mEmptyView;
  private boolean mEnableNextOnConnection;
  private final IntentFilter mFilter = new IntentFilter();
  private WifiManager.ActionListener mForgetListener;
  private WifiInfo mLastInfo;
  private NetworkInfo.DetailedState mLastState;
  private boolean mP2pSupported;
  private final BroadcastReceiver mReceiver;
  private WifiManager.ActionListener mSaveListener;
  private final Scanner mScanner;
  private AccessPoint mSelectedAccessPoint;
  private boolean mSetupWizardMode;
  private WifiEnabler mWifiEnabler;
  private WifiManager mWifiManager;

  public WifiSettings()
  {
    super("no_config_wifi");
    this.mFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
    this.mFilter.addAction("android.net.wifi.SCAN_RESULTS");
    this.mFilter.addAction("android.net.wifi.NETWORK_IDS_CHANGED");
    this.mFilter.addAction("android.net.wifi.supplicant.STATE_CHANGE");
    this.mFilter.addAction("android.net.wifi.CONFIGURED_NETWORKS_CHANGE");
    this.mFilter.addAction("android.net.wifi.LINK_CONFIGURATION_CHANGED");
    this.mFilter.addAction("android.net.wifi.STATE_CHANGE");
    this.mFilter.addAction("android.net.wifi.RSSI_CHANGED");
    this.mReceiver = new BroadcastReceiver()
    {
      public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
      {
        WifiSettings.this.handleEvent(paramAnonymousContext, paramAnonymousIntent);
      }
    };
    this.mScanner = new Scanner(null);
  }

  private void addMessagePreference(int paramInt)
  {
    if (this.mEmptyView != null)
      this.mEmptyView.setText(paramInt);
    getPreferenceScreen().removeAll();
  }

  private void changeNextButtonState(boolean paramBoolean)
  {
    if ((this.mEnableNextOnConnection) && (hasNextButton()))
      getNextButton().setEnabled(paramBoolean);
  }

  private List<AccessPoint> constructAccessPoints()
  {
    ArrayList localArrayList = new ArrayList();
    Multimap localMultimap = new Multimap(null);
    List localList1 = this.mWifiManager.getConfiguredNetworks();
    if (localList1 != null)
    {
      Iterator localIterator3 = localList1.iterator();
      while (localIterator3.hasNext())
      {
        WifiConfiguration localWifiConfiguration = (WifiConfiguration)localIterator3.next();
        AccessPoint localAccessPoint2 = new AccessPoint(getActivity(), localWifiConfiguration);
        localAccessPoint2.update(this.mLastInfo, this.mLastState);
        localArrayList.add(localAccessPoint2);
        localMultimap.put(localAccessPoint2.ssid, localAccessPoint2);
      }
    }
    List localList2 = this.mWifiManager.getScanResults();
    ScanResult localScanResult;
    int i;
    if (localList2 != null)
    {
      Iterator localIterator1 = localList2.iterator();
      while (true)
        if (localIterator1.hasNext())
        {
          localScanResult = (ScanResult)localIterator1.next();
          if ((localScanResult.SSID != null) && (localScanResult.SSID.length() != 0) && (!localScanResult.capabilities.contains("[IBSS]")))
          {
            Iterator localIterator2 = localMultimap.getAll(localScanResult.SSID).iterator();
            i = 0;
            label205: if (localIterator2.hasNext())
              if (!((AccessPoint)localIterator2.next()).update(localScanResult))
                break label290;
          }
        }
    }
    label290: for (int j = 1; ; j = i)
    {
      i = j;
      break label205;
      if (i != 0)
        break;
      AccessPoint localAccessPoint1 = new AccessPoint(getActivity(), localScanResult);
      localArrayList.add(localAccessPoint1);
      localMultimap.put(localAccessPoint1.ssid, localAccessPoint1);
      break;
      Collections.sort(localArrayList);
      return localArrayList;
    }
  }

  private void handleEvent(Context paramContext, Intent paramIntent)
  {
    String str = paramIntent.getAction();
    if ("android.net.wifi.WIFI_STATE_CHANGED".equals(str))
      updateWifiState(paramIntent.getIntExtra("wifi_state", 4));
    do
    {
      Activity localActivity;
      do
      {
        NetworkInfo localNetworkInfo;
        do
        {
          return;
          if (("android.net.wifi.SCAN_RESULTS".equals(str)) || ("android.net.wifi.CONFIGURED_NETWORKS_CHANGE".equals(str)) || ("android.net.wifi.LINK_CONFIGURATION_CHANGED".equals(str)))
          {
            updateAccessPoints();
            return;
          }
          if ("android.net.wifi.supplicant.STATE_CHANGE".equals(str))
          {
            SupplicantState localSupplicantState = (SupplicantState)paramIntent.getParcelableExtra("newState");
            if ((!this.mConnected.get()) && (SupplicantState.isHandshakeState(localSupplicantState)))
            {
              updateConnectionState(WifiInfo.getDetailedStateOf(localSupplicantState));
              return;
            }
            updateConnectionState(null);
            return;
          }
          if (!"android.net.wifi.STATE_CHANGE".equals(str))
            break;
          localNetworkInfo = (NetworkInfo)paramIntent.getParcelableExtra("networkInfo");
          this.mConnected.set(localNetworkInfo.isConnected());
          changeNextButtonState(localNetworkInfo.isConnected());
          updateAccessPoints();
          updateConnectionState(localNetworkInfo.getDetailedState());
        }
        while ((!this.mAutoFinishOnConnection) || (!localNetworkInfo.isConnected()));
        localActivity = getActivity();
      }
      while (localActivity == null);
      localActivity.setResult(-1);
      localActivity.finish();
      return;
    }
    while (!"android.net.wifi.RSSI_CHANGED".equals(str));
    updateConnectionState(null);
  }

  private void setOffMessage()
  {
    if (this.mEmptyView != null)
    {
      this.mEmptyView.setText(2131427845);
      if (Settings.Global.getInt(getActivity().getContentResolver(), "wifi_scan_always_enabled", 0) == 1)
      {
        this.mEmptyView.append("\n\n");
        if (!Settings.Secure.isLocationProviderEnabled(getActivity().getContentResolver(), "network"))
          break label87;
      }
    }
    label87: for (int i = 2131427827; ; i = 2131427828)
    {
      CharSequence localCharSequence = getText(i);
      this.mEmptyView.append(localCharSequence);
      getPreferenceScreen().removeAll();
      return;
    }
  }

  private void showDialog(AccessPoint paramAccessPoint, boolean paramBoolean)
  {
    if (this.mDialog != null)
    {
      removeDialog(1);
      this.mDialog = null;
    }
    this.mDlgAccessPoint = paramAccessPoint;
    this.mDlgEdit = paramBoolean;
    showDialog(1);
  }

  private void updateAccessPoints()
  {
    if (getActivity() == null)
      return;
    if (isRestrictedAndNotPinProtected())
    {
      addMessagePreference(2131427847);
      return;
    }
    switch (this.mWifiManager.getWifiState())
    {
    default:
      return;
    case 0:
      addMessagePreference(2131427816);
      return;
    case 3:
      List localList = constructAccessPoints();
      getPreferenceScreen().removeAll();
      if (localList.size() == 0)
        addMessagePreference(2131427846);
      Iterator localIterator = localList.iterator();
      while (localIterator.hasNext())
      {
        AccessPoint localAccessPoint = (AccessPoint)localIterator.next();
        getPreferenceScreen().addPreference(localAccessPoint);
      }
    case 2:
      getPreferenceScreen().removeAll();
      return;
    case 1:
    }
    setOffMessage();
  }

  private void updateConnectionState(NetworkInfo.DetailedState paramDetailedState)
  {
    if (!this.mWifiManager.isWifiEnabled())
    {
      this.mScanner.pause();
      return;
    }
    if (paramDetailedState == NetworkInfo.DetailedState.OBTAINING_IPADDR)
      this.mScanner.pause();
    while (true)
    {
      this.mLastInfo = this.mWifiManager.getConnectionInfo();
      if (paramDetailedState != null)
        this.mLastState = paramDetailedState;
      for (int i = -1 + getPreferenceScreen().getPreferenceCount(); i >= 0; i--)
      {
        Preference localPreference = getPreferenceScreen().getPreference(i);
        if ((localPreference instanceof AccessPoint))
          ((AccessPoint)localPreference).update(this.mLastInfo, this.mLastState);
      }
      break;
      this.mScanner.resume();
    }
  }

  private void updateWifiState(int paramInt)
  {
    Activity localActivity = getActivity();
    if (localActivity != null)
      localActivity.invalidateOptionsMenu();
    switch (paramInt)
    {
    default:
    case 3:
    case 2:
    case 1:
    }
    while (true)
    {
      this.mLastInfo = null;
      this.mLastState = null;
      this.mScanner.pause();
      return;
      this.mScanner.resume();
      return;
      addMessagePreference(2131427815);
      continue;
      setOffMessage();
    }
  }

  void forget()
  {
    if (this.mSelectedAccessPoint.networkId == -1)
    {
      Log.e("WifiSettings", "Failed to forget invalid network " + this.mSelectedAccessPoint.getConfig());
      return;
    }
    this.mWifiManager.forget(this.mSelectedAccessPoint.networkId, this.mForgetListener);
    if (this.mWifiManager.isWifiEnabled())
      this.mScanner.resume();
    updateAccessPoints();
    changeNextButtonState(false);
  }

  protected int getHelpResource()
  {
    if (this.mSetupWizardMode)
      return 0;
    return 2131429254;
  }

  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    this.mP2pSupported = getPackageManager().hasSystemFeature("android.hardware.wifi.direct");
    this.mWifiManager = ((WifiManager)getSystemService("wifi"));
    this.mConnectListener = new WifiManager.ActionListener()
    {
      public void onFailure(int paramAnonymousInt)
      {
        Activity localActivity = WifiSettings.this.getActivity();
        if (localActivity != null)
          Toast.makeText(localActivity, 2131427910, 0).show();
      }

      public void onSuccess()
      {
      }
    };
    this.mSaveListener = new WifiManager.ActionListener()
    {
      public void onFailure(int paramAnonymousInt)
      {
        Activity localActivity = WifiSettings.this.getActivity();
        if (localActivity != null)
          Toast.makeText(localActivity, 2131427914, 0).show();
      }

      public void onSuccess()
      {
      }
    };
    this.mForgetListener = new WifiManager.ActionListener()
    {
      public void onFailure(int paramAnonymousInt)
      {
        Activity localActivity = WifiSettings.this.getActivity();
        if (localActivity != null)
          Toast.makeText(localActivity, 2131427912, 0).show();
      }

      public void onSuccess()
      {
      }
    };
    if ((paramBundle != null) && (paramBundle.containsKey("wifi_ap_state")))
    {
      this.mDlgEdit = paramBundle.getBoolean("edit_mode");
      this.mAccessPointSavedState = paramBundle.getBundle("wifi_ap_state");
    }
    Activity localActivity = getActivity();
    Intent localIntent = localActivity.getIntent();
    this.mAutoFinishOnConnection = localIntent.getBooleanExtra("wifi_auto_finish_on_connect", false);
    if (this.mAutoFinishOnConnection)
    {
      if (hasNextButton())
        getNextButton().setVisibility(8);
      ConnectivityManager localConnectivityManager2 = (ConnectivityManager)localActivity.getSystemService("connectivity");
      if ((localConnectivityManager2 != null) && (localConnectivityManager2.getNetworkInfo(1).isConnected()))
      {
        localActivity.setResult(-1);
        localActivity.finish();
        return;
      }
    }
    this.mEnableNextOnConnection = localIntent.getBooleanExtra("wifi_enable_next_on_connect", false);
    if ((this.mEnableNextOnConnection) && (hasNextButton()))
    {
      ConnectivityManager localConnectivityManager1 = (ConnectivityManager)localActivity.getSystemService("connectivity");
      if (localConnectivityManager1 != null)
        changeNextButtonState(localConnectivityManager1.getNetworkInfo(1).isConnected());
    }
    addPreferencesFromResource(2131034176);
    if (this.mSetupWizardMode)
      getView().setSystemUiVisibility(27525120);
    if (!this.mSetupWizardMode)
    {
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
      this.mWifiEnabler = new WifiEnabler(localActivity, localSwitch);
    }
    this.mEmptyView = ((TextView)getView().findViewById(16908292));
    getListView().setEmptyView(this.mEmptyView);
    if (!this.mSetupWizardMode)
      registerForContextMenu(getListView());
    setHasOptionsMenu(true);
  }

  void onAddNetworkPressed()
  {
    this.mSelectedAccessPoint = null;
    showDialog(null, true);
  }

  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    if ((paramInt == -3) && (this.mSelectedAccessPoint != null))
      forget();
    while ((paramInt != -1) || (this.mDialog == null))
      return;
    submit(this.mDialog.getController());
  }

  public boolean onContextItemSelected(MenuItem paramMenuItem)
  {
    if (this.mSelectedAccessPoint == null)
      return super.onContextItemSelected(paramMenuItem);
    switch (paramMenuItem.getItemId())
    {
    default:
      return super.onContextItemSelected(paramMenuItem);
    case 7:
      if (this.mSelectedAccessPoint.networkId != -1)
      {
        this.mWifiManager.connect(this.mSelectedAccessPoint.networkId, this.mConnectListener);
        return true;
      }
      if (this.mSelectedAccessPoint.security == 0)
      {
        this.mSelectedAccessPoint.generateOpenNetworkConfig();
        this.mWifiManager.connect(this.mSelectedAccessPoint.getConfig(), this.mConnectListener);
        return true;
      }
      showDialog(this.mSelectedAccessPoint, true);
      return true;
    case 8:
      this.mWifiManager.forget(this.mSelectedAccessPoint.networkId, this.mForgetListener);
      return true;
    case 9:
    }
    showDialog(this.mSelectedAccessPoint, true);
    return true;
  }

  public void onCreate(Bundle paramBundle)
  {
    this.mSetupWizardMode = getActivity().getIntent().getBooleanExtra("firstRun", false);
    super.onCreate(paramBundle);
  }

  public void onCreateContextMenu(ContextMenu paramContextMenu, View paramView, ContextMenu.ContextMenuInfo paramContextMenuInfo)
  {
    if ((paramContextMenuInfo instanceof AdapterView.AdapterContextMenuInfo))
    {
      Preference localPreference = (Preference)getListView().getItemAtPosition(((AdapterView.AdapterContextMenuInfo)paramContextMenuInfo).position);
      if ((localPreference instanceof AccessPoint))
      {
        this.mSelectedAccessPoint = ((AccessPoint)localPreference);
        paramContextMenu.setHeaderTitle(this.mSelectedAccessPoint.ssid);
        if ((this.mSelectedAccessPoint.getLevel() != -1) && (this.mSelectedAccessPoint.getState() == null))
          paramContextMenu.add(0, 7, 0, 2131427842);
        if (this.mSelectedAccessPoint.networkId != -1)
        {
          paramContextMenu.add(0, 8, 0, 2131427843);
          paramContextMenu.add(0, 9, 0, 2131427844);
        }
      }
    }
  }

  public Dialog onCreateDialog(int paramInt)
  {
    switch (paramInt)
    {
    default:
      return super.onCreateDialog(paramInt);
    case 1:
      AccessPoint localAccessPoint = this.mDlgAccessPoint;
      if ((localAccessPoint == null) && (this.mAccessPointSavedState != null))
      {
        localAccessPoint = new AccessPoint(getActivity(), this.mAccessPointSavedState);
        this.mDlgAccessPoint = localAccessPoint;
        this.mAccessPointSavedState = null;
      }
      this.mSelectedAccessPoint = localAccessPoint;
      this.mDialog = new WifiDialog(getActivity(), this, localAccessPoint, this.mDlgEdit);
      return this.mDialog;
    case 2:
      return new WpsDialog(getActivity(), 0);
    case 3:
      return new WpsDialog(getActivity(), 1);
    case 4:
      return new AlertDialog.Builder(getActivity()).setMessage(2131427918).setCancelable(false).setNegativeButton(2131427916, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          WifiSettings.this.getActivity().setResult(1);
          WifiSettings.this.getActivity().finish();
        }
      }).setPositiveButton(2131427917, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
        }
      }).create();
    case 5:
    }
    return new AlertDialog.Builder(getActivity()).setMessage(2131427919).setCancelable(false).setNegativeButton(2131427916, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        WifiSettings.this.getActivity().setResult(1);
        WifiSettings.this.getActivity().finish();
      }
    }).setPositiveButton(2131427917, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
      }
    }).create();
  }

  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    if (isRestrictedAndNotPinProtected())
      return;
    boolean bool = this.mWifiManager.isWifiEnabled();
    TypedArray localTypedArray = getActivity().getTheme().obtainStyledAttributes(new int[] { 2130771987, 2130771989 });
    if (this.mSetupWizardMode)
    {
      paramMenu.add(0, 1, 0, 2131427836).setIcon(localTypedArray.getDrawable(1)).setEnabled(bool).setShowAsAction(2);
      paramMenu.add(0, 4, 0, 2131427834).setEnabled(bool).setShowAsAction(2);
    }
    while (true)
    {
      localTypedArray.recycle();
      super.onCreateOptionsMenu(paramMenu, paramMenuInflater);
      return;
      paramMenu.add(0, 1, 0, 2131427836).setIcon(localTypedArray.getDrawable(1)).setEnabled(bool).setShowAsAction(1);
      paramMenu.add(0, 4, 0, 2131427834).setIcon(localTypedArray.getDrawable(0)).setEnabled(bool).setShowAsAction(1);
      paramMenu.add(0, 6, 0, 2131427840).setEnabled(bool).setShowAsAction(0);
      paramMenu.add(0, 2, 0, 2131427838).setEnabled(bool).setShowAsAction(0);
      if (this.mP2pSupported)
        paramMenu.add(0, 3, 0, 2131427839).setEnabled(bool).setShowAsAction(0);
      paramMenu.add(0, 5, 0, 2131427841).setShowAsAction(0);
    }
  }

  public View onCreateView(final LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    if (this.mSetupWizardMode)
    {
      View localView = paramLayoutInflater.inflate(2130968710, paramViewGroup, false);
      localView.findViewById(2131231055).setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if (WifiSettings.this.mWifiManager.isWifiEnabled())
            WifiSettings.this.onAddNetworkPressed();
        }
      });
      final ImageButton localImageButton = (ImageButton)localView.findViewById(2131231053);
      if (localImageButton != null)
        localImageButton.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            if (WifiSettings.this.mWifiManager.isWifiEnabled())
            {
              PopupMenu localPopupMenu = new PopupMenu(paramLayoutInflater.getContext(), localImageButton);
              localPopupMenu.inflate(2131755013);
              localPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
              {
                public boolean onMenuItemClick(MenuItem paramAnonymous2MenuItem)
                {
                  if (2131231273 == paramAnonymous2MenuItem.getItemId())
                  {
                    WifiSettings.this.showDialog(2);
                    return true;
                  }
                  return false;
                }
              });
              localPopupMenu.show();
            }
          }
        });
      Intent localIntent = getActivity().getIntent();
      if (localIntent.getBooleanExtra("wifi_show_custom_button", false))
      {
        localView.findViewById(2131231058).setVisibility(0);
        localView.findViewById(2131231059).setVisibility(4);
        localView.findViewById(2131231061).setVisibility(4);
        localView.findViewById(2131230763).setVisibility(4);
        Button localButton = (Button)localView.findViewById(2131231060);
        localButton.setVisibility(0);
        localButton.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            ConnectivityManager localConnectivityManager = (ConnectivityManager)WifiSettings.this.getActivity().getSystemService("connectivity");
            int i = 0;
            if (localConnectivityManager != null)
            {
              NetworkInfo localNetworkInfo = localConnectivityManager.getActiveNetworkInfo();
              if ((localNetworkInfo == null) || (!localNetworkInfo.isConnected()))
                break label56;
            }
            label56: for (i = 1; i != 0; i = 0)
            {
              WifiSettings.this.showDialog(4);
              return;
            }
            WifiSettings.this.showDialog(5);
          }
        });
      }
      if (localIntent.getBooleanExtra("wifi_show_wifi_required_info", false))
        localView.findViewById(2131231054).setVisibility(0);
      return localView;
    }
    return super.onCreateView(paramLayoutInflater, paramViewGroup, paramBundle);
  }

  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    if (isRestrictedAndNotPinProtected())
      return false;
    switch (paramMenuItem.getItemId())
    {
    default:
      return super.onOptionsItemSelected(paramMenuItem);
    case 1:
      showDialog(2);
      return true;
    case 3:
      if ((getActivity() instanceof PreferenceActivity))
        ((PreferenceActivity)getActivity()).startPreferencePanel(WifiP2pSettings.class.getCanonicalName(), null, 2131427942, null, this, 0);
      while (true)
      {
        return true;
        startFragment(this, WifiP2pSettings.class.getCanonicalName(), -1, null);
      }
    case 2:
      showDialog(3);
      return true;
    case 6:
      if (this.mWifiManager.isWifiEnabled())
        this.mScanner.forceScan();
      return true;
    case 4:
      if (this.mWifiManager.isWifiEnabled())
        onAddNetworkPressed();
      return true;
    case 5:
    }
    if ((getActivity() instanceof PreferenceActivity))
      ((PreferenceActivity)getActivity()).startPreferencePanel(AdvancedWifiSettings.class.getCanonicalName(), null, 2131427921, null, this, 0);
    while (true)
    {
      return true;
      startFragment(this, AdvancedWifiSettings.class.getCanonicalName(), -1, null);
    }
  }

  public void onPause()
  {
    super.onPause();
    if (this.mWifiEnabler != null)
      this.mWifiEnabler.pause();
    getActivity().unregisterReceiver(this.mReceiver);
    this.mScanner.pause();
  }

  public boolean onPreferenceTreeClick(PreferenceScreen paramPreferenceScreen, Preference paramPreference)
  {
    if ((paramPreference instanceof AccessPoint))
    {
      this.mSelectedAccessPoint = ((AccessPoint)paramPreference);
      if ((this.mSelectedAccessPoint.security == 0) && (this.mSelectedAccessPoint.networkId == -1))
      {
        this.mSelectedAccessPoint.generateOpenNetworkConfig();
        this.mWifiManager.connect(this.mSelectedAccessPoint.getConfig(), this.mConnectListener);
      }
      while (true)
      {
        return true;
        showDialog(this.mSelectedAccessPoint, false);
      }
    }
    return super.onPreferenceTreeClick(paramPreferenceScreen, paramPreference);
  }

  public void onResume()
  {
    super.onResume();
    if (this.mWifiEnabler != null)
      this.mWifiEnabler.resume();
    getActivity().registerReceiver(this.mReceiver, this.mFilter);
    updateAccessPoints();
  }

  public void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    if ((this.mDialog != null) && (this.mDialog.isShowing()))
    {
      paramBundle.putBoolean("edit_mode", this.mDlgEdit);
      if (this.mDlgAccessPoint != null)
      {
        this.mAccessPointSavedState = new Bundle();
        this.mDlgAccessPoint.saveWifiState(this.mAccessPointSavedState);
        paramBundle.putBundle("wifi_ap_state", this.mAccessPointSavedState);
      }
    }
  }

  void refreshAccessPoints()
  {
    if (this.mWifiManager.isWifiEnabled())
      this.mScanner.resume();
    getPreferenceScreen().removeAll();
  }

  void resumeWifiScan()
  {
    if (this.mWifiManager.isWifiEnabled())
      this.mScanner.resume();
  }

  void submit(WifiConfigController paramWifiConfigController)
  {
    WifiConfiguration localWifiConfiguration = paramWifiConfigController.getConfig();
    if (localWifiConfiguration == null)
      if ((this.mSelectedAccessPoint != null) && (this.mSelectedAccessPoint.networkId != -1))
        this.mWifiManager.connect(this.mSelectedAccessPoint.networkId, this.mConnectListener);
    while (true)
    {
      if (this.mWifiManager.isWifiEnabled())
        this.mScanner.resume();
      updateAccessPoints();
      return;
      if (localWifiConfiguration.networkId != -1)
      {
        if (this.mSelectedAccessPoint != null)
          this.mWifiManager.save(localWifiConfiguration, this.mSaveListener);
      }
      else if (paramWifiConfigController.isEdit())
        this.mWifiManager.save(localWifiConfiguration, this.mSaveListener);
      else
        this.mWifiManager.connect(localWifiConfiguration, this.mConnectListener);
    }
  }

  private class Multimap<K, V>
  {
    private final HashMap<K, List<V>> store = new HashMap();

    private Multimap()
    {
    }

    List<V> getAll(K paramK)
    {
      List localList = (List)this.store.get(paramK);
      if (localList != null)
        return localList;
      return Collections.emptyList();
    }

    void put(K paramK, V paramV)
    {
      Object localObject = (List)this.store.get(paramK);
      if (localObject == null)
      {
        localObject = new ArrayList(3);
        this.store.put(paramK, localObject);
      }
      ((List)localObject).add(paramV);
    }
  }

  public static class ProportionalOuterFrame extends RelativeLayout
  {
    public ProportionalOuterFrame(Context paramContext)
    {
      super();
    }

    public ProportionalOuterFrame(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
    }

    public ProportionalOuterFrame(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
    {
      super(paramAttributeSet, paramInt);
    }

    protected void onMeasure(int paramInt1, int paramInt2)
    {
      int i = View.MeasureSpec.getSize(paramInt1);
      int j = View.MeasureSpec.getSize(paramInt2);
      Resources localResources = getContext().getResources();
      float f1 = localResources.getFraction(2131558416, 1, 1);
      float f2 = localResources.getFraction(2131558417, 1, 1);
      int k = localResources.getDimensionPixelSize(2131558418);
      setPaddingRelative((int)(f2 * i), 0, (int)(f2 * i), k);
      View localView = findViewById(2131231051);
      if (localView != null)
        localView.setMinimumHeight((int)(f1 * j));
      super.onMeasure(paramInt1, paramInt2);
    }
  }

  private class Scanner extends Handler
  {
    private int mRetry = 0;

    private Scanner()
    {
    }

    void forceScan()
    {
      removeMessages(0);
      sendEmptyMessage(0);
    }

    public void handleMessage(Message paramMessage)
    {
      if (WifiSettings.this.mWifiManager.startScan())
      {
        this.mRetry = 0;
        sendEmptyMessageDelayed(0, 10000L);
      }
      Activity localActivity;
      do
      {
        return;
        int i = 1 + this.mRetry;
        this.mRetry = i;
        if (i < 3)
          break;
        this.mRetry = 0;
        localActivity = WifiSettings.this.getActivity();
      }
      while (localActivity == null);
      Toast.makeText(localActivity, 2131427819, 1).show();
    }

    void pause()
    {
      this.mRetry = 0;
      removeMessages(0);
    }

    void resume()
    {
      if (!hasMessages(0))
        sendEmptyMessage(0);
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.wifi.WifiSettings
 * JD-Core Version:    0.6.2
 */