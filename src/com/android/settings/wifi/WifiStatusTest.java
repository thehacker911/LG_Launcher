package com.android.settings.wifi;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;

public class WifiStatusTest extends Activity
{
  private TextView mBSSID;
  private TextView mHiddenSSID;
  private TextView mHttpClientTest;
  private String mHttpClientTestResult;
  private TextView mIPAddr;
  private TextView mLinkSpeed;
  private TextView mMACAddr;
  private TextView mNetworkId;
  private TextView mNetworkState;
  View.OnClickListener mPingButtonHandler = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      WifiStatusTest.this.updatePingState();
    }
  };
  private TextView mPingHostname;
  private String mPingHostnameResult;
  private TextView mPingIpAddr;
  private String mPingIpAddrResult;
  private TextView mRSSI;
  private TextView mSSID;
  private TextView mScanList;
  private TextView mSupplicantState;
  private WifiManager mWifiManager;
  private TextView mWifiState;
  private IntentFilter mWifiStateFilter;
  private final BroadcastReceiver mWifiStateReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if (paramAnonymousIntent.getAction().equals("android.net.wifi.WIFI_STATE_CHANGED"))
        WifiStatusTest.this.handleWifiStateChanged(paramAnonymousIntent.getIntExtra("wifi_state", 4));
      do
      {
        do
        {
          return;
          if (paramAnonymousIntent.getAction().equals("android.net.wifi.STATE_CHANGE"))
          {
            WifiStatusTest.this.handleNetworkStateChanged((NetworkInfo)paramAnonymousIntent.getParcelableExtra("networkInfo"));
            return;
          }
          if (paramAnonymousIntent.getAction().equals("android.net.wifi.SCAN_RESULTS"))
          {
            WifiStatusTest.this.handleScanResultsAvailable();
            return;
          }
        }
        while (paramAnonymousIntent.getAction().equals("android.net.wifi.supplicant.CONNECTION_CHANGE"));
        if (paramAnonymousIntent.getAction().equals("android.net.wifi.supplicant.STATE_CHANGE"))
        {
          WifiStatusTest.this.handleSupplicantStateChanged((SupplicantState)paramAnonymousIntent.getParcelableExtra("newState"), paramAnonymousIntent.hasExtra("supplicantError"), paramAnonymousIntent.getIntExtra("supplicantError", 0));
          return;
        }
        if (paramAnonymousIntent.getAction().equals("android.net.wifi.RSSI_CHANGED"))
        {
          WifiStatusTest.this.handleSignalChanged(paramAnonymousIntent.getIntExtra("newRssi", 0));
          return;
        }
      }
      while (paramAnonymousIntent.getAction().equals("android.net.wifi.NETWORK_IDS_CHANGED"));
      Log.e("WifiStatusTest", "Received an unknown Wifi Intent");
    }
  };
  private Button pingTestButton;
  private Button updateButton;
  View.OnClickListener updateButtonHandler = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      WifiInfo localWifiInfo = WifiStatusTest.this.mWifiManager.getConnectionInfo();
      WifiStatusTest.this.setWifiStateText(WifiStatusTest.this.mWifiManager.getWifiState());
      WifiStatusTest.this.mBSSID.setText(localWifiInfo.getBSSID());
      WifiStatusTest.this.mHiddenSSID.setText(String.valueOf(localWifiInfo.getHiddenSSID()));
      int i = localWifiInfo.getIpAddress();
      StringBuffer localStringBuffer1 = new StringBuffer();
      StringBuffer localStringBuffer2 = localStringBuffer1.append(i & 0xFF).append('.');
      int j = i >>> 8;
      StringBuffer localStringBuffer3 = localStringBuffer2.append(j & 0xFF).append('.');
      int k = j >>> 8;
      localStringBuffer3.append(k & 0xFF).append('.').append(0xFF & k >>> 8);
      WifiStatusTest.this.mIPAddr.setText(localStringBuffer1);
      WifiStatusTest.this.mLinkSpeed.setText(String.valueOf(localWifiInfo.getLinkSpeed()) + " Mbps");
      WifiStatusTest.this.mMACAddr.setText(localWifiInfo.getMacAddress());
      WifiStatusTest.this.mNetworkId.setText(String.valueOf(localWifiInfo.getNetworkId()));
      WifiStatusTest.this.mRSSI.setText(String.valueOf(localWifiInfo.getRssi()));
      WifiStatusTest.this.mSSID.setText(localWifiInfo.getSSID());
      SupplicantState localSupplicantState = localWifiInfo.getSupplicantState();
      WifiStatusTest.this.setSupplicantStateText(localSupplicantState);
    }
  };

  private void handleNetworkStateChanged(NetworkInfo paramNetworkInfo)
  {
    if (this.mWifiManager.isWifiEnabled())
    {
      String str = Summary.get(this, this.mWifiManager.getConnectionInfo().getSSID(), paramNetworkInfo.getDetailedState());
      this.mNetworkState.setText(str);
    }
  }

  private void handleScanResultsAvailable()
  {
    List localList = this.mWifiManager.getScanResults();
    StringBuffer localStringBuffer = new StringBuffer();
    if (localList != null)
    {
      int i = -1 + localList.size();
      if (i >= 0)
      {
        ScanResult localScanResult = (ScanResult)localList.get(i);
        if (localScanResult == null);
        while (true)
        {
          i--;
          break;
          if (!TextUtils.isEmpty(localScanResult.SSID))
            localStringBuffer.append(localScanResult.SSID + " ");
        }
      }
    }
    this.mScanList.setText(localStringBuffer);
  }

  private void handleSignalChanged(int paramInt)
  {
    this.mRSSI.setText(String.valueOf(paramInt));
  }

  private void handleSupplicantStateChanged(SupplicantState paramSupplicantState, boolean paramBoolean, int paramInt)
  {
    if (paramBoolean)
    {
      this.mSupplicantState.setText("ERROR AUTHENTICATING");
      return;
    }
    setSupplicantStateText(paramSupplicantState);
  }

  private void handleWifiStateChanged(int paramInt)
  {
    setWifiStateText(paramInt);
  }

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

  private final void pingHostname()
  {
    try
    {
      if (Runtime.getRuntime().exec("ping -c 1 -w 100 www.google.com").waitFor() == 0)
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
      if (Runtime.getRuntime().exec("ping -c 1 -w 100 " + "74.125.47.104").waitFor() == 0)
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

  private void setSupplicantStateText(SupplicantState paramSupplicantState)
  {
    if (SupplicantState.FOUR_WAY_HANDSHAKE.equals(paramSupplicantState))
    {
      this.mSupplicantState.setText("FOUR WAY HANDSHAKE");
      return;
    }
    if (SupplicantState.ASSOCIATED.equals(paramSupplicantState))
    {
      this.mSupplicantState.setText("ASSOCIATED");
      return;
    }
    if (SupplicantState.ASSOCIATING.equals(paramSupplicantState))
    {
      this.mSupplicantState.setText("ASSOCIATING");
      return;
    }
    if (SupplicantState.COMPLETED.equals(paramSupplicantState))
    {
      this.mSupplicantState.setText("COMPLETED");
      return;
    }
    if (SupplicantState.DISCONNECTED.equals(paramSupplicantState))
    {
      this.mSupplicantState.setText("DISCONNECTED");
      return;
    }
    if (SupplicantState.DORMANT.equals(paramSupplicantState))
    {
      this.mSupplicantState.setText("DORMANT");
      return;
    }
    if (SupplicantState.GROUP_HANDSHAKE.equals(paramSupplicantState))
    {
      this.mSupplicantState.setText("GROUP HANDSHAKE");
      return;
    }
    if (SupplicantState.INACTIVE.equals(paramSupplicantState))
    {
      this.mSupplicantState.setText("INACTIVE");
      return;
    }
    if (SupplicantState.INVALID.equals(paramSupplicantState))
    {
      this.mSupplicantState.setText("INVALID");
      return;
    }
    if (SupplicantState.SCANNING.equals(paramSupplicantState))
    {
      this.mSupplicantState.setText("SCANNING");
      return;
    }
    if (SupplicantState.UNINITIALIZED.equals(paramSupplicantState))
    {
      this.mSupplicantState.setText("UNINITIALIZED");
      return;
    }
    this.mSupplicantState.setText("BAD");
    Log.e("WifiStatusTest", "supplicant state is bad");
  }

  private void setWifiStateText(int paramInt)
  {
    String str;
    switch (paramInt)
    {
    default:
      str = "BAD";
      Log.e("WifiStatusTest", "wifi state is bad");
    case 0:
    case 1:
    case 2:
    case 3:
    case 4:
    }
    while (true)
    {
      this.mWifiState.setText(str);
      return;
      str = getString(2131427987);
      continue;
      str = getString(2131427988);
      continue;
      str = getString(2131427989);
      continue;
      str = getString(2131427990);
      continue;
      str = getString(2131427991);
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
    final Runnable local4 = new Runnable()
    {
      public void run()
      {
        WifiStatusTest.this.mPingIpAddr.setText(WifiStatusTest.this.mPingIpAddrResult);
        WifiStatusTest.this.mPingHostname.setText(WifiStatusTest.this.mPingHostnameResult);
        WifiStatusTest.this.mHttpClientTest.setText(WifiStatusTest.this.mHttpClientTestResult);
      }
    };
    new Thread()
    {
      public void run()
      {
        WifiStatusTest.this.pingIpAddr();
        localHandler.post(local4);
      }
    }
    .start();
    new Thread()
    {
      public void run()
      {
        WifiStatusTest.this.pingHostname();
        localHandler.post(local4);
      }
    }
    .start();
    new Thread()
    {
      public void run()
      {
        WifiStatusTest.this.httpClientTest();
        localHandler.post(local4);
      }
    }
    .start();
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mWifiManager = ((WifiManager)getSystemService("wifi"));
    this.mWifiStateFilter = new IntentFilter("android.net.wifi.WIFI_STATE_CHANGED");
    this.mWifiStateFilter.addAction("android.net.wifi.STATE_CHANGE");
    this.mWifiStateFilter.addAction("android.net.wifi.SCAN_RESULTS");
    this.mWifiStateFilter.addAction("android.net.wifi.supplicant.STATE_CHANGE");
    this.mWifiStateFilter.addAction("android.net.wifi.RSSI_CHANGED");
    this.mWifiStateFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
    registerReceiver(this.mWifiStateReceiver, this.mWifiStateFilter);
    setContentView(2130968743);
    this.updateButton = ((Button)findViewById(2131231212));
    this.updateButton.setOnClickListener(this.updateButtonHandler);
    this.mWifiState = ((TextView)findViewById(2131231213));
    this.mNetworkState = ((TextView)findViewById(2131231214));
    this.mSupplicantState = ((TextView)findViewById(2131231215));
    this.mRSSI = ((TextView)findViewById(2131231216));
    this.mBSSID = ((TextView)findViewById(2131231217));
    this.mSSID = ((TextView)findViewById(2131231142));
    this.mHiddenSSID = ((TextView)findViewById(2131231218));
    this.mIPAddr = ((TextView)findViewById(2131231219));
    this.mMACAddr = ((TextView)findViewById(2131231220));
    this.mNetworkId = ((TextView)findViewById(2131231221));
    this.mLinkSpeed = ((TextView)findViewById(2131231222));
    this.mScanList = ((TextView)findViewById(2131231223));
    this.mPingIpAddr = ((TextView)findViewById(2131230995));
    this.mPingHostname = ((TextView)findViewById(2131230996));
    this.mHttpClientTest = ((TextView)findViewById(2131230997));
    this.pingTestButton = ((Button)findViewById(2131230994));
    this.pingTestButton.setOnClickListener(this.mPingButtonHandler);
  }

  protected void onPause()
  {
    super.onPause();
    unregisterReceiver(this.mWifiStateReceiver);
  }

  protected void onResume()
  {
    super.onResume();
    registerReceiver(this.mWifiStateReceiver, this.mWifiStateFilter);
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.wifi.WifiStatusTest
 * JD-Core Version:    0.6.2
 */