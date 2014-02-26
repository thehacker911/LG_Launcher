package com.android.settings.deviceinfo;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.telephony.CellBroadcastMessage;
import android.telephony.PhoneNumberUtils;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneFactory;
import com.android.internal.telephony.PhoneStateIntentReceiver;
import com.android.settings.Utils;
import java.lang.ref.WeakReference;

public class Status extends PreferenceActivity
{
  private static final String[] PHONE_RELATED_ENTRIES = { "data_state", "service_state", "operator_name", "roaming_state", "network_type", "latest_area_info", "number", "imei", "imei_sv", "prl_version", "min_number", "meid_number", "signal_strength", "icc_id" };
  private BroadcastReceiver mAreaInfoReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      Bundle localBundle;
      if ("android.cellbroadcastreceiver.CB_AREA_INFO_RECEIVED".equals(paramAnonymousIntent.getAction()))
      {
        localBundle = paramAnonymousIntent.getExtras();
        if (localBundle != null)
          break label22;
      }
      label22: CellBroadcastMessage localCellBroadcastMessage;
      do
      {
        return;
        localCellBroadcastMessage = (CellBroadcastMessage)localBundle.get("message");
      }
      while ((localCellBroadcastMessage == null) || (localCellBroadcastMessage.getServiceCategory() != 50));
      String str = localCellBroadcastMessage.getMessageBody();
      Status.this.updateAreaInfo(str);
    }
  };
  private BroadcastReceiver mBatteryInfoReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if ("android.intent.action.BATTERY_CHANGED".equals(paramAnonymousIntent.getAction()))
      {
        Status.this.mBatteryLevel.setSummary(Utils.getBatteryPercentage(paramAnonymousIntent));
        Status.this.mBatteryStatus.setSummary(Utils.getBatteryStatus(Status.this.getResources(), paramAnonymousIntent));
      }
    }
  };
  private Preference mBatteryLevel;
  private Preference mBatteryStatus;
  private Handler mHandler;
  private Phone mPhone = null;
  private PhoneStateListener mPhoneStateListener = new PhoneStateListener()
  {
    public void onDataConnectionStateChanged(int paramAnonymousInt)
    {
      Status.this.updateDataState();
      Status.this.updateNetworkType();
    }
  };
  private PhoneStateIntentReceiver mPhoneStateReceiver;
  private Resources mRes;
  private boolean mShowLatestAreaInfo;
  private Preference mSignalStrength;
  private TelephonyManager mTelephonyManager;
  private Preference mUptime;
  private String sUnknown;

  private String convert(long paramLong)
  {
    int i = (int)(paramLong % 60L);
    int j = (int)(paramLong / 60L % 60L);
    int k = (int)(paramLong / 3600L);
    return k + ":" + pad(j) + ":" + pad(i);
  }

  private String pad(int paramInt)
  {
    if (paramInt >= 10)
      return String.valueOf(paramInt);
    return "0" + String.valueOf(paramInt);
  }

  private void removePreferenceFromScreen(String paramString)
  {
    Preference localPreference = findPreference(paramString);
    if (localPreference != null)
      getPreferenceScreen().removePreference(localPreference);
  }

  private void setBtStatus()
  {
    BluetoothAdapter localBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    Preference localPreference = findPreference("bt_address");
    if (localBluetoothAdapter == null)
    {
      getPreferenceScreen().removePreference(localPreference);
      return;
    }
    String str;
    if (localBluetoothAdapter.isEnabled())
    {
      str = localBluetoothAdapter.getAddress();
      if (TextUtils.isEmpty(str))
        break label55;
    }
    while (true)
    {
      localPreference.setSummary(str);
      return;
      str = null;
      break;
      label55: str = getString(2131428124);
    }
  }

  private void setIpAddressStatus()
  {
    Preference localPreference = findPreference("wifi_ip_address");
    String str = Utils.getDefaultIpAddresses(this);
    if (str != null)
    {
      localPreference.setSummary(str);
      return;
    }
    localPreference.setSummary(getString(2131428124));
  }

  private void setSummaryText(String paramString1, String paramString2)
  {
    if (TextUtils.isEmpty(paramString2))
      paramString2 = this.sUnknown;
    if (findPreference(paramString1) != null)
      findPreference(paramString1).setSummary(paramString2);
  }

  private void setWifiStatus()
  {
    WifiInfo localWifiInfo = ((WifiManager)getSystemService("wifi")).getConnectionInfo();
    Preference localPreference = findPreference("wifi_mac_address");
    Object localObject;
    if (localWifiInfo == null)
    {
      localObject = null;
      if (TextUtils.isEmpty((CharSequence)localObject))
        break label47;
    }
    while (true)
    {
      localPreference.setSummary((CharSequence)localObject);
      return;
      localObject = localWifiInfo.getMacAddress();
      break;
      label47: localObject = getString(2131428124);
    }
  }

  private void setWimaxStatus()
  {
    if (((ConnectivityManager)getSystemService("connectivity")).getNetworkInfo(6) == null)
    {
      PreferenceScreen localPreferenceScreen = getPreferenceScreen();
      Preference localPreference = findPreference("wimax_mac_address");
      if (localPreference != null)
        localPreferenceScreen.removePreference(localPreference);
      return;
    }
    findPreference("wimax_mac_address").setSummary(SystemProperties.get("net.wimax.mac.address", getString(2131428124)));
  }

  private void updateAreaInfo(String paramString)
  {
    if (paramString != null)
      setSummaryText("latest_area_info", paramString);
  }

  private void updateDataState()
  {
    int i = this.mTelephonyManager.getDataState();
    String str = this.mRes.getString(2131427371);
    switch (i)
    {
    default:
    case 2:
    case 3:
    case 1:
    case 0:
    }
    while (true)
    {
      setSummaryText("data_state", str);
      return;
      str = this.mRes.getString(2131427369);
      continue;
      str = this.mRes.getString(2131427370);
      continue;
      str = this.mRes.getString(2131427368);
      continue;
      str = this.mRes.getString(2131427367);
    }
  }

  private void updateNetworkType()
  {
    int i = this.mTelephonyManager.getNetworkType();
    String str = null;
    if (i != 0)
      str = this.mTelephonyManager.getNetworkTypeName();
    setSummaryText("network_type", str);
  }

  private void updateServiceState(ServiceState paramServiceState)
  {
    int i = paramServiceState.getState();
    String str = this.mRes.getString(2131427371);
    switch (i)
    {
    default:
      setSummaryText("service_state", str);
      if (paramServiceState.getRoaming())
        setSummaryText("roaming_state", this.mRes.getString(2131427362));
      break;
    case 0:
    case 1:
    case 2:
    case 3:
    }
    while (true)
    {
      setSummaryText("operator_name", paramServiceState.getOperatorAlphaLong());
      return;
      str = this.mRes.getString(2131427358);
      break;
      str = this.mRes.getString(2131427359);
      break;
      str = this.mRes.getString(2131427361);
      break;
      setSummaryText("roaming_state", this.mRes.getString(2131427363));
    }
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mHandler = new MyHandler(this);
    this.mTelephonyManager = ((TelephonyManager)getSystemService("phone"));
    addPreferencesFromResource(2131034128);
    this.mBatteryLevel = findPreference("battery_level");
    this.mBatteryStatus = findPreference("battery_status");
    this.mRes = getResources();
    this.sUnknown = this.mRes.getString(2131427339);
    if (UserHandle.myUserId() == 0)
      this.mPhone = PhoneFactory.getDefaultPhone();
    this.mSignalStrength = findPreference("signal_strength");
    this.mUptime = findPreference("up_time");
    String[] arrayOfString;
    int i;
    int j;
    if ((this.mPhone == null) || (Utils.isWifiOnly(getApplicationContext())))
    {
      arrayOfString = PHONE_RELATED_ENTRIES;
      i = arrayOfString.length;
      j = 0;
    }
    while (j < i)
    {
      removePreferenceFromScreen(arrayOfString[j]);
      j++;
      continue;
      if (!this.mPhone.getPhoneName().equals("CDMA"))
        break label452;
      setSummaryText("meid_number", this.mPhone.getMeid());
      setSummaryText("min_number", this.mPhone.getCdmaMin());
      if (getResources().getBoolean(2131296263))
        findPreference("min_number").setTitle(2131428110);
      setSummaryText("prl_version", this.mPhone.getCdmaPrlVersion());
      removePreferenceFromScreen("imei_sv");
      if (this.mPhone.getLteOnCdmaMode() != 1)
        break label437;
      setSummaryText("icc_id", this.mPhone.getIccSerialNumber());
      setSummaryText("imei", this.mPhone.getImei());
    }
    while (true)
    {
      String str2 = this.mPhone.getLine1Number();
      boolean bool = TextUtils.isEmpty(str2);
      String str3 = null;
      if (!bool)
        str3 = PhoneNumberUtils.formatNumber(str2);
      setSummaryText("number", str3);
      this.mPhoneStateReceiver = new PhoneStateIntentReceiver(this, this.mHandler);
      this.mPhoneStateReceiver.notifySignalStrength(200);
      this.mPhoneStateReceiver.notifyServiceState(300);
      if (!this.mShowLatestAreaInfo)
        removePreferenceFromScreen("latest_area_info");
      setWimaxStatus();
      setWifiStatus();
      setBtStatus();
      setIpAddressStatus();
      String str1 = Build.SERIAL;
      if ((str1 == null) || (str1.equals("")))
        break;
      setSummaryText("serial_number", str1);
      return;
      label437: removePreferenceFromScreen("imei");
      removePreferenceFromScreen("icc_id");
      continue;
      label452: setSummaryText("imei", this.mPhone.getDeviceId());
      setSummaryText("imei_sv", ((TelephonyManager)getSystemService("phone")).getDeviceSoftwareVersion());
      removePreferenceFromScreen("prl_version");
      removePreferenceFromScreen("meid_number");
      removePreferenceFromScreen("min_number");
      removePreferenceFromScreen("icc_id");
      if ("br".equals(this.mTelephonyManager.getSimCountryIso()))
        this.mShowLatestAreaInfo = true;
    }
    removePreferenceFromScreen("serial_number");
  }

  public void onPause()
  {
    super.onPause();
    if ((this.mPhone != null) && (!Utils.isWifiOnly(getApplicationContext())))
    {
      this.mPhoneStateReceiver.unregisterIntent();
      this.mTelephonyManager.listen(this.mPhoneStateListener, 0);
    }
    if (this.mShowLatestAreaInfo)
      unregisterReceiver(this.mAreaInfoReceiver);
    unregisterReceiver(this.mBatteryInfoReceiver);
    this.mHandler.removeMessages(500);
  }

  protected void onResume()
  {
    super.onResume();
    if ((this.mPhone != null) && (!Utils.isWifiOnly(getApplicationContext())))
    {
      this.mPhoneStateReceiver.registerIntent();
      updateSignalStrength();
      updateServiceState(this.mPhone.getServiceState());
      updateDataState();
      this.mTelephonyManager.listen(this.mPhoneStateListener, 64);
      if (this.mShowLatestAreaInfo)
      {
        registerReceiver(this.mAreaInfoReceiver, new IntentFilter("android.cellbroadcastreceiver.CB_AREA_INFO_RECEIVED"), "android.permission.RECEIVE_EMERGENCY_BROADCAST", null);
        sendBroadcastAsUser(new Intent("android.cellbroadcastreceiver.GET_LATEST_CB_AREA_INFO"), UserHandle.ALL, "android.permission.RECEIVE_EMERGENCY_BROADCAST");
      }
    }
    registerReceiver(this.mBatteryInfoReceiver, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
    this.mHandler.sendEmptyMessage(500);
  }

  void updateSignalStrength()
  {
    if (this.mSignalStrength != null)
    {
      int i = this.mPhoneStateReceiver.getServiceState().getState();
      Resources localResources = getResources();
      if ((1 == i) || (3 == i))
        this.mSignalStrength.setSummary("0");
      int j = this.mPhoneStateReceiver.getSignalStrengthDbm();
      if (-1 == j)
        j = 0;
      int k = this.mPhoneStateReceiver.getSignalStrengthLevelAsu();
      if (-1 == k)
        k = 0;
      this.mSignalStrength.setSummary(String.valueOf(j) + " " + localResources.getString(2131427374) + "   " + String.valueOf(k) + " " + localResources.getString(2131427375));
    }
  }

  void updateTimes()
  {
    (SystemClock.uptimeMillis() / 1000L);
    long l = SystemClock.elapsedRealtime() / 1000L;
    if (l == 0L)
      l = 1L;
    this.mUptime.setSummary(convert(l));
  }

  private static class MyHandler extends Handler
  {
    private WeakReference<Status> mStatus;

    public MyHandler(Status paramStatus)
    {
      this.mStatus = new WeakReference(paramStatus);
    }

    public void handleMessage(Message paramMessage)
    {
      Status localStatus = (Status)this.mStatus.get();
      if (localStatus == null)
        return;
      switch (paramMessage.what)
      {
      default:
        return;
      case 200:
        localStatus.updateSignalStrength();
        return;
      case 300:
        localStatus.updateServiceState(localStatus.mPhoneStateReceiver.getServiceState());
        return;
      case 500:
      }
      localStatus.updateTimes();
      sendEmptyMessageDelayed(500, 1000L);
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.deviceinfo.Status
 * JD-Core Version:    0.6.2
 */