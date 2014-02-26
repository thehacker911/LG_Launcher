package com.android.settings;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.AsyncResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.telephony.CellInfo;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsSpinner;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneConstants.State;
import com.android.internal.telephony.PhoneFactory;
import com.android.internal.telephony.PhoneStateIntentReceiver;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;

public class RadioInfo extends Activity
{
  private final String TAG = "phone";
  private TextView attempts;
  private TextView callState;
  private Button cellInfoListRateButton;
  private TextView dBm;
  private TextView disconnects;
  private TextView dnsCheckState;
  private Button dnsCheckToggleButton;
  private TextView gprsState;
  private TextView gsmState;
  private Button imsRegRequiredButton;
  private Button lteRamDumpButton;
  private TextView mCellInfo;
  CellInfoListRateHandler mCellInfoListRateHandler = new CellInfoListRateHandler();
  private List<CellInfo> mCellInfoValue;
  private TextView mCfi;
  private boolean mCfiValue = false;
  private TextView mDeviceId;
  View.OnClickListener mDnsCheckButtonHandler = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      Phone localPhone = RadioInfo.this.phone;
      if (!RadioInfo.this.phone.isDnsCheckDisabled());
      for (boolean bool = true; ; bool = false)
      {
        localPhone.disableDnsCheck(bool);
        RadioInfo.this.updateDnsCheckState();
        return;
      }
    }
  };
  private MenuItem.OnMenuItemClickListener mGetPdpList = new MenuItem.OnMenuItemClickListener()
  {
    public boolean onMenuItemClick(MenuItem paramAnonymousMenuItem)
    {
      RadioInfo.this.phone.getDataCallList(null);
      return true;
    }
  };
  private Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      switch (paramAnonymousMessage.what)
      {
      default:
      case 100:
      case 200:
      case 300:
      case 1000:
      case 1001:
      case 1002:
      case 1005:
      case 1006:
      }
      do
      {
        do
        {
          return;
          RadioInfo.this.updatePhoneState();
          return;
          RadioInfo.this.updateSignalStrength();
          return;
          RadioInfo.this.updateServiceState();
          RadioInfo.this.updatePowerState();
          return;
          AsyncResult localAsyncResult3 = (AsyncResult)paramAnonymousMessage.obj;
          if (localAsyncResult3.exception == null)
          {
            int i = ((int[])(int[])localAsyncResult3.result)[0];
            if (i >= RadioInfo.this.mPreferredNetworkLabels.length)
            {
              RadioInfo.this.log("EVENT_QUERY_PREFERRED_TYPE_DONE: unknown type=" + i);
              i = -1 + RadioInfo.this.mPreferredNetworkLabels.length;
            }
            RadioInfo.this.preferredNetworkType.setSelection(i, true);
            return;
          }
          RadioInfo.this.preferredNetworkType.setSelection(-1 + RadioInfo.this.mPreferredNetworkLabels.length, true);
          return;
        }
        while (((AsyncResult)paramAnonymousMessage.obj).exception == null);
        RadioInfo.this.phone.getPreferredNetworkType(obtainMessage(1000));
        return;
        AsyncResult localAsyncResult2 = (AsyncResult)paramAnonymousMessage.obj;
        if (localAsyncResult2.exception == null)
        {
          RadioInfo.this.updateNeighboringCids((ArrayList)localAsyncResult2.result);
          return;
        }
        RadioInfo.this.mNeighboringCids.setText("unknown");
        return;
        AsyncResult localAsyncResult1 = (AsyncResult)paramAnonymousMessage.obj;
        if (localAsyncResult1.exception != null)
        {
          RadioInfo.this.smsc.setText("refresh error");
          return;
        }
        RadioInfo.this.smsc.setText((String)localAsyncResult1.result);
        return;
        RadioInfo.this.updateSmscButton.setEnabled(true);
      }
      while (((AsyncResult)paramAnonymousMessage.obj).exception == null);
      RadioInfo.this.smsc.setText("update error");
    }
  };
  private TextView mHttpClientTest;
  private String mHttpClientTestResult;
  View.OnClickListener mImsRegRequiredHandler = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      int i = 1;
      RadioInfo localRadioInfo = RadioInfo.this;
      Object[] arrayOfObject = new Object[2];
      arrayOfObject[0] = "persist.radio.imsregrequired";
      String str1;
      if (RadioInfo.this.isImsRegRequired())
      {
        str1 = "on";
        arrayOfObject[i] = str1;
        localRadioInfo.log(String.format("toggle %s: currently %s", arrayOfObject));
        if (RadioInfo.this.isImsRegRequired())
          break label90;
        label60: if (i == 0)
          break label95;
      }
      label90: label95: for (String str2 = "1"; ; str2 = "0")
      {
        SystemProperties.set("persist.radio.imsregrequired", str2);
        RadioInfo.this.updateImsRegRequiredState();
        return;
        str1 = "off";
        break;
        i = 0;
        break label60;
      }
    }
  };
  private TextView mLocation;
  View.OnClickListener mLteRamDumpHandler = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      int i = 1;
      RadioInfo localRadioInfo = RadioInfo.this;
      Object[] arrayOfObject = new Object[2];
      arrayOfObject[0] = "persist.radio.ramdump";
      String str1;
      if (RadioInfo.this.isSmsOverImsEnabled())
      {
        str1 = "on";
        arrayOfObject[i] = str1;
        localRadioInfo.log(String.format("toggle %s: currently %s", arrayOfObject));
        if (RadioInfo.this.isLteRamDumpEnabled())
          break label90;
        label60: if (i == 0)
          break label95;
      }
      label90: label95: for (String str2 = "1"; ; str2 = "0")
      {
        SystemProperties.set("persist.radio.ramdump", str2);
        RadioInfo.this.updateLteRamDumpState();
        return;
        str1 = "off";
        break;
        i = 0;
        break label60;
      }
    }
  };
  private TextView mMwi;
  private boolean mMwiValue = false;
  private TextView mNeighboringCids;
  View.OnClickListener mOemInfoButtonHandler = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      Intent localIntent = new Intent("com.android.settings.OEM_RADIO_INFO");
      try
      {
        RadioInfo.this.startActivity(localIntent);
        return;
      }
      catch (ActivityNotFoundException localActivityNotFoundException)
      {
        RadioInfo.this.log("OEM-specific Info/Settings Activity Not Found : " + localActivityNotFoundException);
      }
    }
  };
  private PhoneStateListener mPhoneStateListener = new PhoneStateListener()
  {
    public void onCallForwardingIndicatorChanged(boolean paramAnonymousBoolean)
    {
      RadioInfo.access$802(RadioInfo.this, paramAnonymousBoolean);
      RadioInfo.this.updateCallRedirect();
    }

    public void onCellInfoChanged(List<CellInfo> paramAnonymousList)
    {
      RadioInfo.this.log("onCellInfoChanged: arrayCi=" + paramAnonymousList);
      RadioInfo.this.updateCellInfoTv(paramAnonymousList);
    }

    public void onCellLocationChanged(CellLocation paramAnonymousCellLocation)
    {
      RadioInfo.this.updateLocation(paramAnonymousCellLocation);
    }

    public void onDataActivity(int paramAnonymousInt)
    {
      RadioInfo.this.updateDataStats2();
    }

    public void onDataConnectionStateChanged(int paramAnonymousInt)
    {
      RadioInfo.this.updateDataState();
      RadioInfo.this.updateDataStats();
      RadioInfo.this.updatePdpList();
      RadioInfo.this.updateNetworkType();
    }

    public void onMessageWaitingIndicatorChanged(boolean paramAnonymousBoolean)
    {
      RadioInfo.access$602(RadioInfo.this, paramAnonymousBoolean);
      RadioInfo.this.updateMessageWaiting();
    }
  };
  private PhoneStateIntentReceiver mPhoneStateReceiver;
  View.OnClickListener mPingButtonHandler = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      RadioInfo.this.updatePingState();
    }
  };
  private TextView mPingHostname;
  private String mPingHostnameResult;
  private TextView mPingIpAddr;
  private String mPingIpAddrResult;
  View.OnClickListener mPowerButtonHandler = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      Phone localPhone = RadioInfo.this.phone;
      if (!RadioInfo.this.isRadioOn());
      for (boolean bool = true; ; bool = false)
      {
        localPhone.setRadioPower(bool);
        return;
      }
    }
  };
  AdapterView.OnItemSelectedListener mPreferredNetworkHandler = new AdapterView.OnItemSelectedListener()
  {
    public void onItemSelected(AdapterView paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
    {
      Message localMessage = RadioInfo.this.mHandler.obtainMessage(1001);
      if ((paramAnonymousInt >= 0) && (paramAnonymousInt <= -2 + RadioInfo.this.mPreferredNetworkLabels.length))
        RadioInfo.this.phone.setPreferredNetworkType(paramAnonymousInt, localMessage);
    }

    public void onNothingSelected(AdapterView paramAnonymousAdapterView)
    {
    }
  };
  private String[] mPreferredNetworkLabels = { "WCDMA preferred", "GSM only", "WCDMA only", "GSM auto (PRL)", "CDMA auto (PRL)", "CDMA only", "EvDo only", "GSM/CDMA auto (PRL)", "LTE/CDMA auto (PRL)", "LTE/GSM auto (PRL)", "LTE/GSM/CDMA auto (PRL)", "LTE only", "Unknown" };
  View.OnClickListener mRefreshSmscButtonHandler = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      RadioInfo.this.refreshSmsc();
    }
  };
  private MenuItem.OnMenuItemClickListener mSelectBandCallback = new MenuItem.OnMenuItemClickListener()
  {
    public boolean onMenuItemClick(MenuItem paramAnonymousMenuItem)
    {
      Intent localIntent = new Intent();
      localIntent.setClass(RadioInfo.this, BandMode.class);
      RadioInfo.this.startActivity(localIntent);
      return true;
    }
  };
  View.OnClickListener mSmsOverImsHandler = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      int i = 1;
      RadioInfo localRadioInfo = RadioInfo.this;
      Object[] arrayOfObject = new Object[2];
      arrayOfObject[0] = "persist.radio.imsallowmtsms";
      String str1;
      if (RadioInfo.this.isSmsOverImsEnabled())
      {
        str1 = "on";
        arrayOfObject[i] = str1;
        localRadioInfo.log(String.format("toggle %s: currently %s", arrayOfObject));
        if (RadioInfo.this.isSmsOverImsEnabled())
          break label90;
        label60: if (i == 0)
          break label95;
      }
      label90: label95: for (String str2 = "1"; ; str2 = "0")
      {
        SystemProperties.set("persist.radio.imsallowmtsms", str2);
        RadioInfo.this.updateSmsOverImsState();
        return;
        str1 = "off";
        break;
        i = 0;
        break label60;
      }
    }
  };
  private TelephonyManager mTelephonyManager;
  private MenuItem.OnMenuItemClickListener mToggleData = new MenuItem.OnMenuItemClickListener()
  {
    public boolean onMenuItemClick(MenuItem paramAnonymousMenuItem)
    {
      ConnectivityManager localConnectivityManager = (ConnectivityManager)RadioInfo.this.getSystemService("connectivity");
      switch (RadioInfo.this.mTelephonyManager.getDataState())
      {
      case 1:
      default:
        return true;
      case 2:
        localConnectivityManager.setMobileDataEnabled(false);
        return true;
      case 0:
      }
      localConnectivityManager.setMobileDataEnabled(true);
      return true;
    }
  };
  View.OnClickListener mUpdateSmscButtonHandler = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      RadioInfo.this.updateSmscButton.setEnabled(false);
      RadioInfo.this.phone.setSmscAddress(RadioInfo.this.smsc.getText().toString(), RadioInfo.this.mHandler.obtainMessage(1006));
    }
  };
  private MenuItem.OnMenuItemClickListener mViewADNCallback = new MenuItem.OnMenuItemClickListener()
  {
    public boolean onMenuItemClick(MenuItem paramAnonymousMenuItem)
    {
      Intent localIntent = new Intent("android.intent.action.VIEW");
      localIntent.setClassName("com.android.phone", "com.android.phone.SimContacts");
      RadioInfo.this.startActivity(localIntent);
      return true;
    }
  };
  private MenuItem.OnMenuItemClickListener mViewFDNCallback = new MenuItem.OnMenuItemClickListener()
  {
    public boolean onMenuItemClick(MenuItem paramAnonymousMenuItem)
    {
      Intent localIntent = new Intent("android.intent.action.VIEW");
      localIntent.setClassName("com.android.phone", "com.android.phone.FdnList");
      RadioInfo.this.startActivity(localIntent);
      return true;
    }
  };
  private MenuItem.OnMenuItemClickListener mViewSDNCallback = new MenuItem.OnMenuItemClickListener()
  {
    public boolean onMenuItemClick(MenuItem paramAnonymousMenuItem)
    {
      Intent localIntent = new Intent("android.intent.action.VIEW", Uri.parse("content://icc/sdn"));
      localIntent.setClassName("com.android.phone", "com.android.phone.ADNList");
      RadioInfo.this.startActivity(localIntent);
      return true;
    }
  };
  private TextView network;
  private TextView number;
  private Button oemInfoButton;
  private TextView operatorName;
  private Phone phone = null;
  private Button pingTestButton;
  private Spinner preferredNetworkType;
  private Button radioPowerButton;
  private TextView received;
  private Button refreshSmscButton;
  private TextView resets;
  private TextView roamingState;
  private TextView sent;
  private TextView sentSinceReceived;
  private Button smsOverImsButton;
  private EditText smsc;
  private TextView successes;
  private Button updateSmscButton;

  private void httpClientTest()
  {
    DefaultHttpClient localDefaultHttpClient = new DefaultHttpClient();
    try
    {
      HttpGet localHttpGet = new HttpGet("http://www.google.com");
      HttpResponse localHttpResponse = localDefaultHttpClient.execute(localHttpGet);
      if (localHttpResponse.getStatusLine().getStatusCode() == 200);
      for (this.mHttpClientTestResult = "Pass"; ; this.mHttpClientTestResult = ("Fail: Code: " + String.valueOf(localHttpResponse)))
      {
        localHttpGet.abort();
        return;
      }
    }
    catch (IOException localIOException)
    {
      this.mHttpClientTestResult = "Fail: IOException";
    }
  }

  private boolean isImsRegRequired()
  {
    return SystemProperties.getBoolean("persist.radio.imsregrequired", false);
  }

  private boolean isLteRamDumpEnabled()
  {
    return SystemProperties.getBoolean("persist.radio.ramdump", false);
  }

  private boolean isRadioOn()
  {
    return this.phone.getServiceState().getState() != 3;
  }

  private boolean isSmsOverImsEnabled()
  {
    return SystemProperties.getBoolean("persist.radio.imsallowmtsms", false);
  }

  private void log(String paramString)
  {
    Log.d("phone", "[RadioInfo] " + paramString);
  }

  private final void pingHostname()
  {
    try
    {
      if (Runtime.getRuntime().exec("ping -c 1 www.google.com").waitFor() == 0)
      {
        this.mPingHostnameResult = "Pass";
        return;
      }
      this.mPingHostnameResult = "Fail: Host unreachable";
      return;
    }
    catch (UnknownHostException localUnknownHostException)
    {
      this.mPingHostnameResult = "Fail: Unknown Host";
      return;
    }
    catch (IOException localIOException)
    {
      this.mPingHostnameResult = "Fail: IOException";
      return;
    }
    catch (InterruptedException localInterruptedException)
    {
      this.mPingHostnameResult = "Fail: InterruptedException";
    }
  }

  private final void pingIpAddr()
  {
    try
    {
      if (Runtime.getRuntime().exec("ping -c 1 " + "74.125.47.104").waitFor() == 0)
      {
        this.mPingIpAddrResult = "Pass";
        return;
      }
      this.mPingIpAddrResult = "Fail: IP addr not reachable";
      return;
    }
    catch (IOException localIOException)
    {
      this.mPingIpAddrResult = "Fail: IOException";
      return;
    }
    catch (InterruptedException localInterruptedException)
    {
      this.mPingIpAddrResult = "Fail: InterruptedException";
    }
  }

  private void refreshSmsc()
  {
    this.phone.getSmscAddress(this.mHandler.obtainMessage(1005));
  }

  private final void updateCallRedirect()
  {
    this.mCfi.setText(String.valueOf(this.mCfiValue));
  }

  private void updateCellInfoListRate()
  {
    this.cellInfoListRateButton.setText("CellInfoListRate " + this.mCellInfoListRateHandler.getRate());
    updateCellInfoTv(this.mTelephonyManager.getAllCellInfo());
  }

  private final void updateCellInfoTv(List<CellInfo> paramList)
  {
    this.mCellInfoValue = paramList;
    StringBuilder localStringBuilder = new StringBuilder();
    if (this.mCellInfoValue != null)
    {
      Iterator localIterator = this.mCellInfoValue.iterator();
      int j;
      for (int i = 0; localIterator.hasNext(); i = j)
      {
        CellInfo localCellInfo = (CellInfo)localIterator.next();
        localStringBuilder.append('[');
        localStringBuilder.append(i);
        localStringBuilder.append("]=");
        localStringBuilder.append(localCellInfo.toString());
        j = i + 1;
        if (j < this.mCellInfoValue.size())
          localStringBuilder.append("\n");
      }
    }
    this.mCellInfo.setText(localStringBuilder.toString());
  }

  private final void updateDataState()
  {
    int i = this.mTelephonyManager.getDataState();
    Resources localResources = getResources();
    String str = localResources.getString(2131427371);
    switch (i)
    {
    default:
    case 2:
    case 1:
    case 0:
    case 3:
    }
    while (true)
    {
      this.gprsState.setText(str);
      return;
      str = localResources.getString(2131427369);
      continue;
      str = localResources.getString(2131427368);
      continue;
      str = localResources.getString(2131427367);
      continue;
      str = localResources.getString(2131427370);
    }
  }

  private final void updateDataStats()
  {
    String str1 = SystemProperties.get("net.gsm.radio-reset", "0");
    this.resets.setText(str1);
    String str2 = SystemProperties.get("net.gsm.attempt-gprs", "0");
    this.attempts.setText(str2);
    String str3 = SystemProperties.get("net.gsm.succeed-gprs", "0");
    this.successes.setText(str3);
    String str4 = SystemProperties.get("net.ppp.reset-by-timeout", "0");
    this.sentSinceReceived.setText(str4);
  }

  private final void updateDataStats2()
  {
    Resources localResources = getResources();
    long l1 = TrafficStats.getMobileTxPackets();
    long l2 = TrafficStats.getMobileRxPackets();
    long l3 = TrafficStats.getMobileTxBytes();
    long l4 = TrafficStats.getMobileRxBytes();
    String str1 = localResources.getString(2131427372);
    String str2 = localResources.getString(2131427373);
    this.sent.setText(l1 + " " + str1 + ", " + l3 + " " + str2);
    this.received.setText(l2 + " " + str1 + ", " + l4 + " " + str2);
  }

  private void updateDnsCheckState()
  {
    TextView localTextView = this.dnsCheckState;
    if (this.phone.isDnsCheckDisabled());
    for (String str = "0.0.0.0 allowed"; ; str = "0.0.0.0 not allowed")
    {
      localTextView.setText(str);
      return;
    }
  }

  private void updateImsRegRequiredState()
  {
    log("updateImsRegRequiredState isImsRegRequired()=" + isImsRegRequired());
    if (isImsRegRequired());
    for (String str = getString(2131427351); ; str = getString(2131427350))
    {
      this.imsRegRequiredButton.setText(str);
      return;
    }
  }

  private final void updateLocation(CellLocation paramCellLocation)
  {
    Resources localResources = getResources();
    if ((paramCellLocation instanceof GsmCellLocation))
    {
      GsmCellLocation localGsmCellLocation = (GsmCellLocation)paramCellLocation;
      int i1 = localGsmCellLocation.getLac();
      int i2 = localGsmCellLocation.getCid();
      TextView localTextView2 = this.mLocation;
      StringBuilder localStringBuilder6 = new StringBuilder().append(localResources.getString(2131427376)).append(" = ");
      String str6;
      StringBuilder localStringBuilder7;
      if (i1 == -1)
      {
        str6 = "unknown";
        localStringBuilder7 = localStringBuilder6.append(str6).append("   ").append(localResources.getString(2131427377)).append(" = ");
        if (i2 != -1)
          break label142;
      }
      label142: for (String str7 = "unknown"; ; str7 = Integer.toHexString(i2))
      {
        localTextView2.setText(str7);
        return;
        str6 = Integer.toHexString(i1);
        break;
      }
    }
    if ((paramCellLocation instanceof CdmaCellLocation))
    {
      CdmaCellLocation localCdmaCellLocation = (CdmaCellLocation)paramCellLocation;
      int i = localCdmaCellLocation.getBaseStationId();
      int j = localCdmaCellLocation.getSystemId();
      int k = localCdmaCellLocation.getNetworkId();
      int m = localCdmaCellLocation.getBaseStationLatitude();
      int n = localCdmaCellLocation.getBaseStationLongitude();
      TextView localTextView1 = this.mLocation;
      StringBuilder localStringBuilder1 = new StringBuilder().append("BID = ");
      String str1;
      String str2;
      label258: String str3;
      label290: String str4;
      label322: StringBuilder localStringBuilder5;
      if (i == -1)
      {
        str1 = "unknown";
        StringBuilder localStringBuilder2 = localStringBuilder1.append(str1).append("   ").append("SID = ");
        if (j != -1)
          break label380;
        str2 = "unknown";
        StringBuilder localStringBuilder3 = localStringBuilder2.append(str2).append("   ").append("NID = ");
        if (k != -1)
          break label390;
        str3 = "unknown";
        StringBuilder localStringBuilder4 = localStringBuilder3.append(str3).append("\n").append("LAT = ");
        if (m != -1)
          break label400;
        str4 = "unknown";
        localStringBuilder5 = localStringBuilder4.append(str4).append("   ").append("LONG = ");
        if (n != -1)
          break label410;
      }
      label390: label400: label410: for (String str5 = "unknown"; ; str5 = Integer.toHexString(n))
      {
        localTextView1.setText(str5);
        return;
        str1 = Integer.toHexString(i);
        break;
        label380: str2 = Integer.toHexString(j);
        break label258;
        str3 = Integer.toHexString(k);
        break label290;
        str4 = Integer.toHexString(m);
        break label322;
      }
    }
    this.mLocation.setText("unknown");
  }

  private void updateLteRamDumpState()
  {
    log("updateLteRamDumpState isLteRamDumpEnabled()=" + isLteRamDumpEnabled());
    if (isLteRamDumpEnabled());
    for (String str = getString(2131427353); ; str = getString(2131427352))
    {
      this.lteRamDumpButton.setText(str);
      return;
    }
  }

  private final void updateMessageWaiting()
  {
    this.mMwi.setText(String.valueOf(this.mMwiValue));
  }

  private final void updateNeighboringCids(ArrayList<NeighboringCellInfo> paramArrayList)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    if (paramArrayList != null)
      if (paramArrayList.isEmpty())
        localStringBuilder.append("no neighboring cells");
    while (true)
    {
      this.mNeighboringCids.setText(localStringBuilder.toString());
      return;
      Iterator localIterator = paramArrayList.iterator();
      while (localIterator.hasNext())
        localStringBuilder.append(((NeighboringCellInfo)localIterator.next()).toString()).append(" ");
      continue;
      localStringBuilder.append("unknown");
    }
  }

  private final void updateNetworkType()
  {
    String str = SystemProperties.get("gsm.network.type", getResources().getString(2131427371));
    this.network.setText(str);
  }

  private final void updatePdpList()
  {
    StringBuilder localStringBuilder = new StringBuilder("========DATA=======\n");
    this.disconnects.setText(localStringBuilder.toString());
  }

  private final void updatePhoneState()
  {
    PhoneConstants.State localState = this.mPhoneStateReceiver.getPhoneState();
    Resources localResources = getResources();
    String str = localResources.getString(2131427371);
    switch (23.$SwitchMap$com$android$internal$telephony$PhoneConstants$State[localState.ordinal()])
    {
    default:
    case 1:
    case 2:
    case 3:
    }
    while (true)
    {
      this.callState.setText(str);
      return;
      str = localResources.getString(2131427364);
      continue;
      str = localResources.getString(2131427365);
      continue;
      str = localResources.getString(2131427366);
    }
  }

  private final void updatePingState()
  {
    final Handler localHandler = new Handler();
    this.mPingIpAddrResult = getResources().getString(2131427371);
    this.mPingHostnameResult = getResources().getString(2131427371);
    this.mHttpClientTestResult = getResources().getString(2131427371);
    this.mPingIpAddr.setText(this.mPingIpAddrResult);
    this.mPingHostname.setText(this.mPingHostnameResult);
    this.mHttpClientTest.setText(this.mHttpClientTestResult);
    final Runnable local3 = new Runnable()
    {
      public void run()
      {
        RadioInfo.this.mPingIpAddr.setText(RadioInfo.this.mPingIpAddrResult);
        RadioInfo.this.mPingHostname.setText(RadioInfo.this.mPingHostnameResult);
        RadioInfo.this.mHttpClientTest.setText(RadioInfo.this.mHttpClientTestResult);
      }
    };
    new Thread()
    {
      public void run()
      {
        RadioInfo.this.pingIpAddr();
        localHandler.post(local3);
      }
    }
    .start();
    new Thread()
    {
      public void run()
      {
        RadioInfo.this.pingHostname();
        localHandler.post(local3);
      }
    }
    .start();
    new Thread()
    {
      public void run()
      {
        RadioInfo.this.httpClientTest();
        localHandler.post(local3);
      }
    }
    .start();
  }

  private void updatePowerState()
  {
    if (isRadioOn());
    for (String str = getString(2131427347); ; str = getString(2131427346))
    {
      this.radioPowerButton.setText(str);
      return;
    }
  }

  private final void updateProperties()
  {
    Resources localResources = getResources();
    String str1 = this.phone.getDeviceId();
    if (str1 == null)
      str1 = localResources.getString(2131427371);
    this.mDeviceId.setText(str1);
    String str2 = this.phone.getLine1Number();
    if (str2 == null)
      str2 = localResources.getString(2131427371);
    this.number.setText(str2);
  }

  private final void updateServiceState()
  {
    ServiceState localServiceState = this.mPhoneStateReceiver.getServiceState();
    int i = localServiceState.getState();
    Resources localResources = getResources();
    String str = localResources.getString(2131427371);
    switch (i)
    {
    default:
      this.gsmState.setText(str);
      if (localServiceState.getRoaming())
        this.roamingState.setText(2131427362);
      break;
    case 0:
    case 1:
    case 2:
    case 3:
    }
    while (true)
    {
      this.operatorName.setText(localServiceState.getOperatorAlphaLong());
      return;
      str = localResources.getString(2131427358);
      break;
      str = localResources.getString(2131427360);
      break;
      str = localResources.getString(2131427361);
      break;
      this.roamingState.setText(2131427363);
    }
  }

  private final void updateSignalStrength()
  {
    int i = this.mPhoneStateReceiver.getServiceState().getState();
    Resources localResources = getResources();
    if ((1 == i) || (3 == i))
      this.dBm.setText("0");
    int j = this.mPhoneStateReceiver.getSignalStrengthDbm();
    if (-1 == j)
      j = 0;
    int k = this.mPhoneStateReceiver.getSignalStrengthLevelAsu();
    int m = 0;
    if (-1 == k);
    while (true)
    {
      this.dBm.setText(String.valueOf(j) + " " + localResources.getString(2131427374) + "   " + String.valueOf(m) + " " + localResources.getString(2131427375));
      return;
      m = k;
    }
  }

  private void updateSmsOverImsState()
  {
    log("updateSmsOverImsState isSmsOverImsEnabled()=" + isSmsOverImsEnabled());
    if (isSmsOverImsEnabled());
    for (String str = getString(2131427349); ; str = getString(2131427348))
    {
      this.smsOverImsButton.setText(str);
      return;
    }
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2130968697);
    this.mTelephonyManager = ((TelephonyManager)getSystemService("phone"));
    this.phone = PhoneFactory.getDefaultPhone();
    this.mDeviceId = ((TextView)findViewById(2131230991));
    this.number = ((TextView)findViewById(2131230992));
    this.callState = ((TextView)findViewById(2131231008));
    this.operatorName = ((TextView)findViewById(2131230993));
    this.roamingState = ((TextView)findViewById(2131231002));
    this.gsmState = ((TextView)findViewById(2131231003));
    this.gprsState = ((TextView)findViewById(2131231004));
    this.network = ((TextView)findViewById(2131231005));
    this.dBm = ((TextView)findViewById(2131230998));
    this.mMwi = ((TextView)findViewById(2131231006));
    this.mCfi = ((TextView)findViewById(2131231007));
    this.mLocation = ((TextView)findViewById(2131230999));
    this.mNeighboringCids = ((TextView)findViewById(2131231000));
    this.mCellInfo = ((TextView)findViewById(2131231001));
    this.resets = ((TextView)findViewById(2131231009));
    this.attempts = ((TextView)findViewById(2131231010));
    this.successes = ((TextView)findViewById(2131231011));
    this.disconnects = ((TextView)findViewById(2131231012));
    this.sentSinceReceived = ((TextView)findViewById(2131231015));
    this.sent = ((TextView)findViewById(2131231013));
    this.received = ((TextView)findViewById(2131231014));
    this.smsc = ((EditText)findViewById(2131231025));
    this.dnsCheckState = ((TextView)findViewById(2131231027));
    this.mPingIpAddr = ((TextView)findViewById(2131230995));
    this.mPingHostname = ((TextView)findViewById(2131230996));
    this.mHttpClientTest = ((TextView)findViewById(2131230997));
    this.preferredNetworkType = ((Spinner)findViewById(2131231016));
    ArrayAdapter localArrayAdapter = new ArrayAdapter(this, 17367048, this.mPreferredNetworkLabels);
    localArrayAdapter.setDropDownViewResource(17367049);
    this.preferredNetworkType.setAdapter(localArrayAdapter);
    this.preferredNetworkType.setOnItemSelectedListener(this.mPreferredNetworkHandler);
    this.radioPowerButton = ((Button)findViewById(2131231017));
    this.radioPowerButton.setOnClickListener(this.mPowerButtonHandler);
    this.cellInfoListRateButton = ((Button)findViewById(2131231018));
    this.cellInfoListRateButton.setOnClickListener(this.mCellInfoListRateHandler);
    this.imsRegRequiredButton = ((Button)findViewById(2131231019));
    this.imsRegRequiredButton.setOnClickListener(this.mImsRegRequiredHandler);
    this.smsOverImsButton = ((Button)findViewById(2131231020));
    this.smsOverImsButton.setOnClickListener(this.mSmsOverImsHandler);
    this.lteRamDumpButton = ((Button)findViewById(2131231021));
    this.lteRamDumpButton.setOnClickListener(this.mLteRamDumpHandler);
    this.pingTestButton = ((Button)findViewById(2131230994));
    this.pingTestButton.setOnClickListener(this.mPingButtonHandler);
    this.updateSmscButton = ((Button)findViewById(2131231023));
    this.updateSmscButton.setOnClickListener(this.mUpdateSmscButtonHandler);
    this.refreshSmscButton = ((Button)findViewById(2131231024));
    this.refreshSmscButton.setOnClickListener(this.mRefreshSmscButtonHandler);
    this.dnsCheckToggleButton = ((Button)findViewById(2131231026));
    this.dnsCheckToggleButton.setOnClickListener(this.mDnsCheckButtonHandler);
    this.oemInfoButton = ((Button)findViewById(2131231028));
    this.oemInfoButton.setOnClickListener(this.mOemInfoButtonHandler);
    if (getPackageManager().queryIntentActivities(new Intent("com.android.settings.OEM_RADIO_INFO"), 0).size() == 0)
      this.oemInfoButton.setEnabled(false);
    this.mPhoneStateReceiver = new PhoneStateIntentReceiver(this, this.mHandler);
    this.mPhoneStateReceiver.notifySignalStrength(200);
    this.mPhoneStateReceiver.notifyServiceState(300);
    this.mPhoneStateReceiver.notifyPhoneCallState(100);
    this.phone.getPreferredNetworkType(this.mHandler.obtainMessage(1000));
    this.phone.getNeighboringCids(this.mHandler.obtainMessage(1002));
    CellLocation.requestLocationUpdate();
    this.mCellInfoValue = this.mTelephonyManager.getAllCellInfo();
    log("onCreate: mCellInfoValue=" + this.mCellInfoValue);
  }

  public boolean onCreateOptionsMenu(Menu paramMenu)
  {
    paramMenu.add(0, 0, 0, 2131427528).setOnMenuItemClickListener(this.mSelectBandCallback).setAlphabeticShortcut('b');
    paramMenu.add(1, 1, 0, 2131427354).setOnMenuItemClickListener(this.mViewADNCallback);
    paramMenu.add(1, 2, 0, 2131427355).setOnMenuItemClickListener(this.mViewFDNCallback);
    paramMenu.add(1, 3, 0, 2131427356).setOnMenuItemClickListener(this.mViewSDNCallback);
    paramMenu.add(1, 4, 0, 2131427357).setOnMenuItemClickListener(this.mGetPdpList);
    paramMenu.add(1, 5, 0, "Disable data connection").setOnMenuItemClickListener(this.mToggleData);
    return true;
  }

  public void onPause()
  {
    super.onPause();
    log("onPause: unregister phone & data intents");
    this.mPhoneStateReceiver.unregisterIntent();
    this.mTelephonyManager.listen(this.mPhoneStateListener, 0);
  }

  public boolean onPrepareOptionsMenu(Menu paramMenu)
  {
    MenuItem localMenuItem = paramMenu.findItem(5);
    int i = this.mTelephonyManager.getDataState();
    boolean bool = true;
    switch (i)
    {
    case 1:
    default:
      bool = false;
    case 2:
    case 3:
    case 0:
    }
    while (true)
    {
      localMenuItem.setVisible(bool);
      return true;
      localMenuItem.setTitle("Disable data connection");
      continue;
      localMenuItem.setTitle("Enable data connection");
    }
  }

  protected void onResume()
  {
    super.onResume();
    updatePhoneState();
    updateSignalStrength();
    updateMessageWaiting();
    updateCallRedirect();
    updateServiceState();
    updateLocation(this.mTelephonyManager.getCellLocation());
    updateDataState();
    updateDataStats();
    updateDataStats2();
    updatePowerState();
    updateCellInfoListRate();
    updateImsRegRequiredState();
    updateSmsOverImsState();
    updateLteRamDumpState();
    updateProperties();
    updateDnsCheckState();
    log("onResume: register phone & data intents");
    this.mPhoneStateReceiver.registerIntent();
    this.mTelephonyManager.listen(this.mPhoneStateListener, 1244);
  }

  class CellInfoListRateHandler
    implements View.OnClickListener
  {
    int index = 0;
    int[] rates = { 2147483647, 0, 1000 };

    CellInfoListRateHandler()
    {
    }

    public int getRate()
    {
      return this.rates[this.index];
    }

    public void onClick(View paramView)
    {
      this.index = (1 + this.index);
      if (this.index >= this.rates.length)
        this.index = 0;
      RadioInfo.this.phone.setCellInfoListRate(this.rates[this.index]);
      RadioInfo.this.updateCellInfoListRate();
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.RadioInfo
 * JD-Core Version:    0.6.2
 */