package com.android.settings;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothPan;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothProfile.ServiceListener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.webkit.WebView;
import com.android.settings.wifi.WifiApDialog;
import com.android.settings.wifi.WifiApEnabler;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class TetherSettings extends SettingsPreferenceFragment
  implements DialogInterface.OnClickListener, Preference.OnPreferenceChangeListener
{
  private boolean mBluetoothEnableForTether;
  private AtomicReference<BluetoothPan> mBluetoothPan = new AtomicReference();
  private String[] mBluetoothRegexs;
  private CheckBoxPreference mBluetoothTether;
  private Preference mCreateNetwork;
  private WifiApDialog mDialog;
  private CheckBoxPreference mEnableWifiAp;
  private boolean mMassStorageActive;
  private BluetoothProfile.ServiceListener mProfileServiceListener = new BluetoothProfile.ServiceListener()
  {
    public void onServiceConnected(int paramAnonymousInt, BluetoothProfile paramAnonymousBluetoothProfile)
    {
      TetherSettings.this.mBluetoothPan.set((BluetoothPan)paramAnonymousBluetoothProfile);
    }

    public void onServiceDisconnected(int paramAnonymousInt)
    {
      TetherSettings.this.mBluetoothPan.set(null);
    }
  };
  private String[] mProvisionApp;
  private String[] mSecurityType;
  private BroadcastReceiver mTetherChangeReceiver;
  private int mTetherChoice = -1;
  private boolean mUsbConnected;
  private String[] mUsbRegexs;
  private CheckBoxPreference mUsbTether;
  private WebView mView;
  private WifiApEnabler mWifiApEnabler;
  private WifiConfiguration mWifiConfig = null;
  private WifiManager mWifiManager;
  private String[] mWifiRegexs;

  private static String findIface(String[] paramArrayOfString1, String[] paramArrayOfString2)
  {
    int i = paramArrayOfString1.length;
    for (int j = 0; j < i; j++)
    {
      String str = paramArrayOfString1[j];
      int k = paramArrayOfString2.length;
      for (int m = 0; m < k; m++)
        if (str.matches(paramArrayOfString2[m]))
          return str;
    }
    return null;
  }

  private void initWifiTethering()
  {
    Activity localActivity = getActivity();
    this.mWifiManager = ((WifiManager)getSystemService("wifi"));
    this.mWifiConfig = this.mWifiManager.getWifiApConfiguration();
    this.mSecurityType = getResources().getStringArray(2131165204);
    this.mCreateNetwork = findPreference("wifi_ap_ssid_and_security");
    if (this.mWifiConfig == null)
    {
      String str2 = localActivity.getString(17040437);
      Preference localPreference2 = this.mCreateNetwork;
      String str3 = localActivity.getString(2131427964);
      Object[] arrayOfObject2 = new Object[2];
      arrayOfObject2[0] = str2;
      arrayOfObject2[1] = this.mSecurityType[0];
      localPreference2.setSummary(String.format(str3, arrayOfObject2));
      return;
    }
    int i = WifiApDialog.getSecurityTypeIndex(this.mWifiConfig);
    Preference localPreference1 = this.mCreateNetwork;
    String str1 = localActivity.getString(2131427964);
    Object[] arrayOfObject1 = new Object[2];
    arrayOfObject1[0] = this.mWifiConfig.SSID;
    arrayOfObject1[1] = this.mSecurityType[i];
    localPreference1.setSummary(String.format(str1, arrayOfObject1));
  }

  private void setUsbTethering(boolean paramBoolean)
  {
    ConnectivityManager localConnectivityManager = (ConnectivityManager)getSystemService("connectivity");
    this.mUsbTether.setChecked(false);
    if (localConnectivityManager.setUsbTethering(paramBoolean) != 0)
    {
      this.mUsbTether.setSummary(2131428247);
      return;
    }
    this.mUsbTether.setSummary("");
  }

  public static boolean showInShortcuts(Context paramContext)
  {
    ConnectivityManager localConnectivityManager = (ConnectivityManager)paramContext.getSystemService("connectivity");
    if (UserHandle.myUserId() != 0);
    for (int i = 1; (i == 0) && (localConnectivityManager.isTetheringSupported()); i = 0)
      return true;
    return false;
  }

  private void startProvisioningIfNecessary(int paramInt)
  {
    this.mTetherChoice = paramInt;
    if (isProvisioningNeeded())
    {
      Intent localIntent = new Intent("android.intent.action.MAIN");
      localIntent.setClassName(this.mProvisionApp[0], this.mProvisionApp[1]);
      startActivityForResult(localIntent, 0);
      return;
    }
    startTethering();
  }

  private void startTethering()
  {
    switch (this.mTetherChoice)
    {
    default:
      return;
    case 0:
      this.mWifiApEnabler.setSoftapEnabled(true);
      return;
    case 2:
      BluetoothAdapter localBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
      if (localBluetoothAdapter.getState() == 10)
      {
        this.mBluetoothEnableForTether = true;
        localBluetoothAdapter.enable();
        this.mBluetoothTether.setSummary(2131427465);
        this.mBluetoothTether.setEnabled(false);
        return;
      }
      BluetoothPan localBluetoothPan = (BluetoothPan)this.mBluetoothPan.get();
      if (localBluetoothPan != null)
        localBluetoothPan.setBluetoothTethering(true);
      this.mBluetoothTether.setSummary(2131428249);
      return;
    case 1:
    }
    setUsbTethering(true);
  }

  private void updateBluetoothState(String[] paramArrayOfString1, String[] paramArrayOfString2, String[] paramArrayOfString3)
  {
    int i = 0;
    int j = paramArrayOfString3.length;
    for (int k = 0; k < j; k++)
    {
      String str2 = paramArrayOfString3[k];
      String[] arrayOfString = this.mBluetoothRegexs;
      int i1 = arrayOfString.length;
      for (int i2 = 0; i2 < i1; i2++)
        if (str2.matches(arrayOfString[i2]))
          i = 1;
    }
    int m = BluetoothAdapter.getDefaultAdapter().getState();
    if (m == 13)
    {
      this.mBluetoothTether.setEnabled(false);
      this.mBluetoothTether.setSummary(2131427466);
      return;
    }
    if (m == 11)
    {
      this.mBluetoothTether.setEnabled(false);
      this.mBluetoothTether.setSummary(2131427465);
      return;
    }
    BluetoothPan localBluetoothPan = (BluetoothPan)this.mBluetoothPan.get();
    if ((m == 12) && (localBluetoothPan != null) && (localBluetoothPan.isTetheringOn()))
    {
      this.mBluetoothTether.setChecked(true);
      this.mBluetoothTether.setEnabled(true);
      int n = localBluetoothPan.getConnectedDevices().size();
      if (n > 1)
      {
        Object[] arrayOfObject = new Object[1];
        arrayOfObject[0] = Integer.valueOf(n);
        String str1 = getString(2131428251, arrayOfObject);
        this.mBluetoothTether.setSummary(str1);
        return;
      }
      if (n == 1)
      {
        this.mBluetoothTether.setSummary(2131428250);
        return;
      }
      if (i != 0)
      {
        this.mBluetoothTether.setSummary(2131428253);
        return;
      }
      this.mBluetoothTether.setSummary(2131428249);
      return;
    }
    this.mBluetoothTether.setEnabled(true);
    this.mBluetoothTether.setChecked(false);
    this.mBluetoothTether.setSummary(2131428252);
  }

  private void updateState()
  {
    ConnectivityManager localConnectivityManager = (ConnectivityManager)getSystemService("connectivity");
    updateState(localConnectivityManager.getTetherableIfaces(), localConnectivityManager.getTetheredIfaces(), localConnectivityManager.getTetheringErroredIfaces());
  }

  private void updateState(String[] paramArrayOfString1, String[] paramArrayOfString2, String[] paramArrayOfString3)
  {
    updateUsbState(paramArrayOfString1, paramArrayOfString2, paramArrayOfString3);
    updateBluetoothState(paramArrayOfString1, paramArrayOfString2, paramArrayOfString3);
  }

  private void updateUsbState(String[] paramArrayOfString1, String[] paramArrayOfString2, String[] paramArrayOfString3)
  {
    ConnectivityManager localConnectivityManager = (ConnectivityManager)getSystemService("connectivity");
    int i;
    int j;
    int k;
    if ((this.mUsbConnected) && (!this.mMassStorageActive))
    {
      i = 1;
      j = 0;
      k = paramArrayOfString1.length;
    }
    for (int m = 0; ; m++)
    {
      if (m >= k)
        break label117;
      String str3 = paramArrayOfString1[m];
      String[] arrayOfString3 = this.mUsbRegexs;
      int i10 = arrayOfString3.length;
      int i11 = 0;
      while (true)
        if (i11 < i10)
        {
          if ((str3.matches(arrayOfString3[i11])) && (j == 0))
            j = localConnectivityManager.getLastTetherError(str3);
          i11++;
          continue;
          i = 0;
          break;
        }
    }
    label117: int n = 0;
    int i1 = paramArrayOfString2.length;
    for (int i2 = 0; i2 < i1; i2++)
    {
      String str2 = paramArrayOfString2[i2];
      String[] arrayOfString2 = this.mUsbRegexs;
      int i8 = arrayOfString2.length;
      for (int i9 = 0; i9 < i8; i9++)
        if (str2.matches(arrayOfString2[i9]))
          n = 1;
    }
    int i3 = 0;
    int i4 = paramArrayOfString3.length;
    for (int i5 = 0; i5 < i4; i5++)
    {
      String str1 = paramArrayOfString3[i5];
      String[] arrayOfString1 = this.mUsbRegexs;
      int i6 = arrayOfString1.length;
      for (int i7 = 0; i7 < i6; i7++)
        if (str1.matches(arrayOfString1[i7]))
          i3 = 1;
    }
    if (n != 0)
    {
      this.mUsbTether.setSummary(2131428244);
      this.mUsbTether.setEnabled(true);
      this.mUsbTether.setChecked(true);
      return;
    }
    if (i != 0)
    {
      if (j == 0)
        this.mUsbTether.setSummary(2131428243);
      while (true)
      {
        this.mUsbTether.setEnabled(true);
        this.mUsbTether.setChecked(false);
        return;
        this.mUsbTether.setSummary(2131428247);
      }
    }
    if (i3 != 0)
    {
      this.mUsbTether.setSummary(2131428247);
      this.mUsbTether.setEnabled(false);
      this.mUsbTether.setChecked(false);
      return;
    }
    if (this.mMassStorageActive)
    {
      this.mUsbTether.setSummary(2131428245);
      this.mUsbTether.setEnabled(false);
      this.mUsbTether.setChecked(false);
      return;
    }
    this.mUsbTether.setSummary(2131428246);
    this.mUsbTether.setEnabled(false);
    this.mUsbTether.setChecked(false);
  }

  public int getHelpResource()
  {
    return 2131429264;
  }

  boolean isProvisioningNeeded()
  {
    if (SystemProperties.getBoolean("net.tethering.noprovisioning", false));
    while (this.mProvisionApp.length != 2)
      return false;
    return true;
  }

  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
    if (paramInt1 == 0)
    {
      if (paramInt2 == -1)
        startTethering();
    }
    else
      return;
    switch (this.mTetherChoice)
    {
    default:
    case 2:
    case 1:
    }
    while (true)
    {
      this.mTetherChoice = -1;
      return;
      this.mBluetoothTether.setChecked(false);
      continue;
      this.mUsbTether.setChecked(false);
    }
  }

  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    if (paramInt == -1)
    {
      this.mWifiConfig = this.mDialog.getConfig();
      if (this.mWifiConfig != null)
      {
        if (this.mWifiManager.getWifiApState() != 13)
          break label125;
        this.mWifiManager.setWifiApEnabled(null, false);
        this.mWifiManager.setWifiApEnabled(this.mWifiConfig, true);
      }
    }
    while (true)
    {
      int i = WifiApDialog.getSecurityTypeIndex(this.mWifiConfig);
      Preference localPreference = this.mCreateNetwork;
      String str = getActivity().getString(2131427964);
      Object[] arrayOfObject = new Object[2];
      arrayOfObject[0] = this.mWifiConfig.SSID;
      arrayOfObject[1] = this.mSecurityType[i];
      localPreference.setSummary(String.format(str, arrayOfObject));
      return;
      label125: this.mWifiManager.setWifiApConfiguration(this.mWifiConfig);
    }
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131034163);
    Activity localActivity = getActivity();
    BluetoothAdapter localBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    if (localBluetoothAdapter != null)
      localBluetoothAdapter.getProfileProxy(localActivity.getApplicationContext(), this.mProfileServiceListener, 5);
    this.mEnableWifiAp = ((CheckBoxPreference)findPreference("enable_wifi_ap"));
    Preference localPreference = findPreference("wifi_ap_ssid_and_security");
    this.mUsbTether = ((CheckBoxPreference)findPreference("usb_tether_settings"));
    this.mBluetoothTether = ((CheckBoxPreference)findPreference("enable_bluetooth_tethering"));
    ConnectivityManager localConnectivityManager = (ConnectivityManager)getSystemService("connectivity");
    this.mUsbRegexs = localConnectivityManager.getTetherableUsbRegexs();
    this.mWifiRegexs = localConnectivityManager.getTetherableWifiRegexs();
    this.mBluetoothRegexs = localConnectivityManager.getTetherableBluetoothRegexs();
    int i;
    int j;
    label149: int k;
    if (this.mUsbRegexs.length != 0)
    {
      i = 1;
      if (this.mWifiRegexs.length == 0)
        break label264;
      j = 1;
      if (this.mBluetoothRegexs.length == 0)
        break label270;
      k = 1;
      label160: if ((i == 0) || (Utils.isMonkeyRunning()))
        getPreferenceScreen().removePreference(this.mUsbTether);
      if ((j == 0) || (Utils.isMonkeyRunning()))
        break label276;
      this.mWifiApEnabler = new WifiApEnabler(localActivity, this.mEnableWifiAp);
      initWifiTethering();
      label214: if (k != 0)
        break label301;
      getPreferenceScreen().removePreference(this.mBluetoothTether);
    }
    while (true)
    {
      this.mProvisionApp = getResources().getStringArray(17235988);
      this.mView = new WebView(localActivity);
      return;
      i = 0;
      break;
      label264: j = 0;
      break label149;
      label270: k = 0;
      break label160;
      label276: getPreferenceScreen().removePreference(this.mEnableWifiAp);
      getPreferenceScreen().removePreference(localPreference);
      break label214;
      label301: BluetoothPan localBluetoothPan = (BluetoothPan)this.mBluetoothPan.get();
      if ((localBluetoothPan != null) && (localBluetoothPan.isTetheringOn()))
        this.mBluetoothTether.setChecked(true);
      else
        this.mBluetoothTether.setChecked(false);
    }
  }

  public Dialog onCreateDialog(int paramInt)
  {
    if (paramInt == 1)
    {
      this.mDialog = new WifiApDialog(getActivity(), this, this.mWifiConfig);
      return this.mDialog;
    }
    return null;
  }

  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    if (((Boolean)paramObject).booleanValue())
    {
      startProvisioningIfNecessary(0);
      return false;
    }
    this.mWifiApEnabler.setSoftapEnabled(false);
    return false;
  }

  public boolean onPreferenceTreeClick(PreferenceScreen paramPreferenceScreen, Preference paramPreference)
  {
    ConnectivityManager localConnectivityManager = (ConnectivityManager)getSystemService("connectivity");
    boolean bool;
    if (paramPreference == this.mUsbTether)
    {
      bool = this.mUsbTether.isChecked();
      if (bool)
        startProvisioningIfNecessary(1);
    }
    while (true)
    {
      return super.onPreferenceTreeClick(paramPreferenceScreen, paramPreference);
      setUsbTethering(bool);
      continue;
      if (paramPreference == this.mBluetoothTether)
      {
        if (this.mBluetoothTether.isChecked())
        {
          startProvisioningIfNecessary(2);
        }
        else
        {
          String str = findIface(localConnectivityManager.getTetheredIfaces(), this.mBluetoothRegexs);
          int i = 0;
          if (str != null)
          {
            int j = localConnectivityManager.untether(str);
            i = 0;
            if (j != 0)
              i = 1;
          }
          BluetoothPan localBluetoothPan = (BluetoothPan)this.mBluetoothPan.get();
          if (localBluetoothPan != null)
            localBluetoothPan.setBluetoothTethering(false);
          if (i != 0)
            this.mBluetoothTether.setSummary(2131428253);
          else
            this.mBluetoothTether.setSummary(2131428252);
        }
      }
      else if (paramPreference == this.mCreateNetwork)
        showDialog(1);
    }
  }

  public void onStart()
  {
    super.onStart();
    Activity localActivity = getActivity();
    this.mMassStorageActive = "shared".equals(Environment.getExternalStorageState());
    this.mTetherChangeReceiver = new TetherChangeReceiver(null);
    IntentFilter localIntentFilter1 = new IntentFilter("android.net.conn.TETHER_STATE_CHANGED");
    Intent localIntent = localActivity.registerReceiver(this.mTetherChangeReceiver, localIntentFilter1);
    IntentFilter localIntentFilter2 = new IntentFilter();
    localIntentFilter2.addAction("android.hardware.usb.action.USB_STATE");
    localActivity.registerReceiver(this.mTetherChangeReceiver, localIntentFilter2);
    IntentFilter localIntentFilter3 = new IntentFilter();
    localIntentFilter3.addAction("android.intent.action.MEDIA_SHARED");
    localIntentFilter3.addAction("android.intent.action.MEDIA_UNSHARED");
    localIntentFilter3.addDataScheme("file");
    localActivity.registerReceiver(this.mTetherChangeReceiver, localIntentFilter3);
    IntentFilter localIntentFilter4 = new IntentFilter();
    localIntentFilter4.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
    localActivity.registerReceiver(this.mTetherChangeReceiver, localIntentFilter4);
    if (localIntent != null)
      this.mTetherChangeReceiver.onReceive(localActivity, localIntent);
    if (this.mWifiApEnabler != null)
    {
      this.mEnableWifiAp.setOnPreferenceChangeListener(this);
      this.mWifiApEnabler.resume();
    }
    updateState();
  }

  public void onStop()
  {
    super.onStop();
    getActivity().unregisterReceiver(this.mTetherChangeReceiver);
    this.mTetherChangeReceiver = null;
    if (this.mWifiApEnabler != null)
    {
      this.mEnableWifiAp.setOnPreferenceChangeListener(null);
      this.mWifiApEnabler.pause();
    }
  }

  private class TetherChangeReceiver extends BroadcastReceiver
  {
    private TetherChangeReceiver()
    {
    }

    public void onReceive(Context paramContext, Intent paramIntent)
    {
      String str = paramIntent.getAction();
      if (str.equals("android.net.conn.TETHER_STATE_CHANGED"))
      {
        ArrayList localArrayList1 = paramIntent.getStringArrayListExtra("availableArray");
        ArrayList localArrayList2 = paramIntent.getStringArrayListExtra("activeArray");
        ArrayList localArrayList3 = paramIntent.getStringArrayListExtra("erroredArray");
        TetherSettings.this.updateState((String[])localArrayList1.toArray(new String[localArrayList1.size()]), (String[])localArrayList2.toArray(new String[localArrayList2.size()]), (String[])localArrayList3.toArray(new String[localArrayList3.size()]));
      }
      do
      {
        return;
        if (str.equals("android.intent.action.MEDIA_SHARED"))
        {
          TetherSettings.access$202(TetherSettings.this, true);
          TetherSettings.this.updateState();
          return;
        }
        if (str.equals("android.intent.action.MEDIA_UNSHARED"))
        {
          TetherSettings.access$202(TetherSettings.this, false);
          TetherSettings.this.updateState();
          return;
        }
        if (str.equals("android.hardware.usb.action.USB_STATE"))
        {
          TetherSettings.access$402(TetherSettings.this, paramIntent.getBooleanExtra("connected", false));
          TetherSettings.this.updateState();
          return;
        }
      }
      while (!str.equals("android.bluetooth.adapter.action.STATE_CHANGED"));
      if (TetherSettings.this.mBluetoothEnableForTether)
        switch (paramIntent.getIntExtra("android.bluetooth.adapter.extra.STATE", -2147483648))
        {
        default:
        case 12:
        case 10:
        case -2147483648:
        }
      while (true)
      {
        TetherSettings.this.updateState();
        return;
        BluetoothPan localBluetoothPan = (BluetoothPan)TetherSettings.this.mBluetoothPan.get();
        if (localBluetoothPan != null)
        {
          localBluetoothPan.setBluetoothTethering(true);
          TetherSettings.access$502(TetherSettings.this, false);
          continue;
          TetherSettings.access$502(TetherSettings.this, false);
        }
      }
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.TetherSettings
 * JD-Core Version:    0.6.2
 */