package com.android.settings.wifi;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.ActionListener;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;

public class WifiSettingsForSetupWizardXL extends Activity
  implements View.OnClickListener
{
  private static final EnumMap<NetworkInfo.DetailedState, NetworkInfo.DetailedState> sNetworkStateMap = new EnumMap(NetworkInfo.DetailedState.class);
  private Button mAddNetworkButton;
  private Button mBackButton;
  private View mBottomPadding;
  private Button mConnectButton;
  private View mConnectingStatusLayout;
  private TextView mConnectingStatusView;
  private View mContentPadding;
  private CharSequence mEditingTitle;
  private InputMethodManager mInputMethodManager;
  private CharSequence mNetworkName = "";
  private NetworkInfo.DetailedState mPreviousNetworkState = NetworkInfo.DetailedState.DISCONNECTED;
  private ProgressBar mProgressBar;
  private Button mRefreshButton;
  private int mScreenState = 0;
  private Button mSkipOrNextButton;
  private TextView mTitleView;
  private View mTopDividerNoProgress;
  private View mTopPadding;
  private WifiConfigUiForSetupWizardXL mWifiConfig;
  private WifiManager mWifiManager;
  private WifiSettings mWifiSettings;
  private View mWifiSettingsFragmentLayout;

  static
  {
    sNetworkStateMap.put(NetworkInfo.DetailedState.IDLE, NetworkInfo.DetailedState.DISCONNECTED);
    sNetworkStateMap.put(NetworkInfo.DetailedState.SCANNING, NetworkInfo.DetailedState.SCANNING);
    sNetworkStateMap.put(NetworkInfo.DetailedState.CONNECTING, NetworkInfo.DetailedState.CONNECTING);
    sNetworkStateMap.put(NetworkInfo.DetailedState.AUTHENTICATING, NetworkInfo.DetailedState.CONNECTING);
    sNetworkStateMap.put(NetworkInfo.DetailedState.OBTAINING_IPADDR, NetworkInfo.DetailedState.CONNECTING);
    sNetworkStateMap.put(NetworkInfo.DetailedState.CONNECTED, NetworkInfo.DetailedState.CONNECTED);
    sNetworkStateMap.put(NetworkInfo.DetailedState.SUSPENDED, NetworkInfo.DetailedState.SUSPENDED);
    sNetworkStateMap.put(NetworkInfo.DetailedState.DISCONNECTING, NetworkInfo.DetailedState.DISCONNECTED);
    sNetworkStateMap.put(NetworkInfo.DetailedState.DISCONNECTED, NetworkInfo.DetailedState.DISCONNECTED);
    sNetworkStateMap.put(NetworkInfo.DetailedState.FAILED, NetworkInfo.DetailedState.FAILED);
  }

  private void hideSoftwareKeyboard()
  {
    Log.i("SetupWizard", "Hiding software keyboard.");
    View localView = getCurrentFocus();
    if (localView != null)
      this.mInputMethodManager.hideSoftInputFromWindow(localView.getWindowToken(), 0);
  }

  private void initViews()
  {
    Intent localIntent = getIntent();
    if (localIntent.getBooleanExtra("firstRun", false))
      findViewById(2131230813).setSystemUiVisibility(4194304);
    if (localIntent.getBooleanExtra("extra_prefs_landscape_lock", false))
      setRequestedOrientation(6);
    if (localIntent.getBooleanExtra("extra_prefs_portrait_lock", false))
      setRequestedOrientation(7);
    this.mTitleView = ((TextView)findViewById(2131231193));
    this.mProgressBar = ((ProgressBar)findViewById(2131231194));
    this.mProgressBar.setMax(2);
    this.mTopDividerNoProgress = findViewById(2131231195);
    this.mBottomPadding = findViewById(2131231205);
    this.mProgressBar.setVisibility(0);
    this.mProgressBar.setIndeterminate(true);
    this.mTopDividerNoProgress.setVisibility(8);
    this.mAddNetworkButton = ((Button)findViewById(2131231207));
    this.mAddNetworkButton.setOnClickListener(this);
    this.mRefreshButton = ((Button)findViewById(2131231211));
    this.mRefreshButton.setOnClickListener(this);
    this.mSkipOrNextButton = ((Button)findViewById(2131231210));
    this.mSkipOrNextButton.setOnClickListener(this);
    this.mConnectButton = ((Button)findViewById(2131231209));
    this.mConnectButton.setOnClickListener(this);
    this.mBackButton = ((Button)findViewById(2131231208));
    this.mBackButton.setOnClickListener(this);
    this.mTopPadding = findViewById(2131231192);
    this.mContentPadding = findViewById(2131231198);
    this.mWifiSettingsFragmentLayout = findViewById(2131231199);
    this.mConnectingStatusLayout = findViewById(2131231202);
    this.mConnectingStatusView = ((TextView)findViewById(2131231203));
  }

  private void onAddNetworkButtonPressed()
  {
    this.mWifiSettings.onAddNetworkPressed();
  }

  private void onBackButtonPressed()
  {
    if ((this.mScreenState == 2) || (this.mScreenState == 3))
    {
      Log.d("SetupWizard", "Back button pressed after connect action.");
      this.mScreenState = 0;
      restoreFirstVisibilityState();
      this.mSkipOrNextButton.setEnabled(true);
      changeNextButtonState(false);
      showScanningState();
      Iterator localIterator = this.mWifiManager.getConfiguredNetworks().iterator();
      while (localIterator.hasNext())
      {
        WifiConfiguration localWifiConfiguration = (WifiConfiguration)localIterator.next();
        Object[] arrayOfObject = new Object[2];
        arrayOfObject[0] = localWifiConfiguration.SSID;
        arrayOfObject[1] = Integer.valueOf(localWifiConfiguration.networkId);
        Log.d("SetupWizard", String.format("forgeting Wi-Fi network \"%s\" (id: %d)", arrayOfObject));
        this.mWifiManager.forget(localWifiConfiguration.networkId, new WifiManager.ActionListener()
        {
          public void onFailure(int paramAnonymousInt)
          {
          }

          public void onSuccess()
          {
          }
        });
      }
      this.mWifiSettingsFragmentLayout.setVisibility(8);
      refreshAccessPoints(true);
    }
    while (true)
    {
      setPaddingVisibility(0);
      this.mConnectingStatusLayout.setVisibility(8);
      ViewGroup localViewGroup = (ViewGroup)findViewById(2131231201);
      localViewGroup.removeAllViews();
      localViewGroup.setVisibility(8);
      this.mWifiConfig = null;
      return;
      this.mScreenState = 0;
      this.mWifiSettings.resumeWifiScan();
      restoreFirstVisibilityState();
      this.mAddNetworkButton.setEnabled(true);
      this.mRefreshButton.setEnabled(true);
      this.mSkipOrNextButton.setEnabled(true);
      showDisconnectedProgressBar();
      this.mWifiSettingsFragmentLayout.setVisibility(0);
      this.mBottomPadding.setVisibility(8);
    }
  }

  private void onEapNetworkSelected()
  {
    this.mConnectButton.setVisibility(8);
    this.mBackButton.setText(2131428950);
  }

  private void refreshAccessPoints(boolean paramBoolean)
  {
    showScanningState();
    if (paramBoolean)
      this.mWifiManager.disconnect();
    this.mWifiSettings.refreshAccessPoints();
  }

  private void restoreFirstVisibilityState()
  {
    showDefaultTitle();
    this.mAddNetworkButton.setVisibility(0);
    this.mRefreshButton.setVisibility(0);
    this.mSkipOrNextButton.setVisibility(0);
    this.mConnectButton.setVisibility(8);
    this.mBackButton.setVisibility(8);
    setPaddingVisibility(0);
  }

  private void showConnectingProgressBar()
  {
    showTopDividerWithProgressBar();
    this.mProgressBar.setIndeterminate(false);
    this.mProgressBar.setProgress(1);
  }

  private void showConnectingState()
  {
    this.mScreenState = 2;
    this.mBackButton.setVisibility(0);
    this.mEditingTitle = this.mTitleView.getText();
    showConnectingTitle();
    showConnectingProgressBar();
    setPaddingVisibility(0);
  }

  private void showConnectingTitle()
  {
    if ((TextUtils.isEmpty(this.mNetworkName)) && (this.mWifiConfig != null))
    {
      if ((this.mWifiConfig.getController() == null) || (this.mWifiConfig.getController().getConfig() == null))
        break label87;
      this.mNetworkName = this.mWifiConfig.getController().getConfig().SSID;
    }
    while (true)
    {
      TextView localTextView = this.mTitleView;
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = this.mNetworkName;
      localTextView.setText(getString(2131428942, arrayOfObject));
      return;
      label87: Log.w("SetupWizard", "Unexpected null found (WifiController or WifiConfig is null). Ignore them.");
    }
  }

  private void showDefaultTitle()
  {
    this.mTitleView.setText(getString(2131428940));
  }

  private void showDisconnectedProgressBar()
  {
    if (this.mScreenState == 0)
    {
      this.mProgressBar.setVisibility(8);
      this.mProgressBar.setIndeterminate(false);
      this.mTopDividerNoProgress.setVisibility(0);
      return;
    }
    this.mProgressBar.setVisibility(0);
    this.mProgressBar.setIndeterminate(false);
    this.mProgressBar.setProgress(0);
    this.mTopDividerNoProgress.setVisibility(8);
  }

  private void showScanningProgressBar()
  {
    showTopDividerWithProgressBar();
    this.mProgressBar.setIndeterminate(true);
  }

  private void showScanningState()
  {
    setPaddingVisibility(0);
    this.mWifiSettingsFragmentLayout.setVisibility(8);
    showScanningProgressBar();
  }

  private void showTopDividerWithProgressBar()
  {
    this.mProgressBar.setVisibility(0);
    this.mTopDividerNoProgress.setVisibility(8);
    this.mBottomPadding.setVisibility(8);
  }

  void changeNextButtonState(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.mSkipOrNextButton.setText(2131428949);
      return;
    }
    this.mSkipOrNextButton.setText(2131428948);
  }

  boolean initSecurityFields(View paramView, int paramInt)
  {
    paramView.findViewById(2131231147).setVisibility(8);
    paramView.findViewById(2131231148).setVisibility(8);
    paramView.findViewById(2131231150).setVisibility(0);
    paramView.findViewById(2131231151).setVisibility(0);
    if (paramInt == 3)
    {
      setPaddingVisibility(0);
      hideSoftwareKeyboard();
      if (paramView.findViewById(2131231149).getVisibility() == 0)
        paramView.findViewById(2131231148).setVisibility(0);
      while (true)
      {
        paramView.findViewById(2131231152).setVisibility(8);
        paramView.findViewById(2131231150).setVisibility(8);
        paramView.findViewById(2131231151).setVisibility(8);
        onEapNetworkSelected();
        return false;
        paramView.findViewById(2131231147).setVisibility(0);
      }
    }
    this.mConnectButton.setVisibility(0);
    setPaddingVisibility(8);
    if (this.mWifiConfig != null)
    {
      if ((paramInt != 2) && (paramInt != 1))
        break label183;
      this.mWifiConfig.requestFocusAndShowKeyboard(2131231123);
    }
    while (true)
    {
      return true;
      label183: this.mWifiConfig.requestFocusAndShowKeyboard(2131231142);
    }
  }

  public void onClick(View paramView)
  {
    hideSoftwareKeyboard();
    if (paramView == this.mAddNetworkButton)
    {
      Log.d("SetupWizard", "AddNetwork button pressed");
      onAddNetworkButtonPressed();
    }
    do
    {
      return;
      if (paramView == this.mRefreshButton)
      {
        Log.d("SetupWizard", "Refresh button pressed");
        refreshAccessPoints(true);
        return;
      }
      if (paramView == this.mSkipOrNextButton)
      {
        Log.d("SetupWizard", "Skip/Next button pressed");
        if (TextUtils.equals(getString(2131428948), ((Button)paramView).getText()))
        {
          this.mWifiManager.setWifiEnabled(false);
          setResult(1);
        }
        while (true)
        {
          finish();
          return;
          setResult(-1);
        }
      }
      if (paramView == this.mConnectButton)
      {
        Log.d("SetupWizard", "Connect button pressed");
        onConnectButtonPressed();
        return;
      }
    }
    while (paramView != this.mBackButton);
    Log.d("SetupWizard", "Back button pressed");
    onBackButtonPressed();
  }

  void onConnectButtonPressed()
  {
    this.mScreenState = 2;
    this.mWifiSettings.submit(this.mWifiConfig.getController());
    showConnectingState();
    this.mBackButton.setVisibility(0);
    this.mBackButton.setText(2131428950);
    ((ViewGroup)findViewById(2131231201)).setVisibility(8);
    this.mConnectingStatusLayout.setVisibility(0);
    this.mConnectingStatusView.setText(2131428966);
    this.mSkipOrNextButton.setVisibility(0);
    this.mSkipOrNextButton.setEnabled(false);
    this.mConnectButton.setVisibility(8);
    this.mAddNetworkButton.setVisibility(8);
    this.mRefreshButton.setVisibility(8);
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    requestWindowFeature(1);
    setContentView(2130968741);
    this.mWifiManager = ((WifiManager)getSystemService("wifi"));
    this.mWifiManager.setWifiEnabled(true);
    this.mWifiSettings = ((WifiSettings)getFragmentManager().findFragmentById(2131231200));
    this.mInputMethodManager = ((InputMethodManager)getSystemService("input_method"));
    initViews();
    showScanningState();
  }

  void setPaddingVisibility(int paramInt)
  {
    this.mTopPadding.setVisibility(paramInt);
    this.mContentPadding.setVisibility(paramInt);
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.wifi.WifiSettingsForSetupWizardXL
 * JD-Core Version:    0.6.2
 */