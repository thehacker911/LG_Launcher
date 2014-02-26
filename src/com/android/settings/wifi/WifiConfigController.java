package com.android.settings.wifi;

import android.content.Context;
import android.content.res.Resources;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.NetworkInfo.DetailedState;
import android.net.ProxyProperties;
import android.net.RouteInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.IpAssignment;
import android.net.wifi.WifiConfiguration.ProxySettings;
import android.net.wifi.WifiEnterpriseConfig;
import android.net.wifi.WifiInfo;
import android.os.Handler;
import android.security.KeyStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsSpinner;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import com.android.settings.ProxySelector;
import java.net.InetAddress;
import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;

public class WifiConfigController
  implements TextWatcher, AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener
{
  private final ArrayAdapter<String> PHASE2_FULL_ADAPTER;
  private final ArrayAdapter<String> PHASE2_PEAP_ADAPTER;
  private final AccessPoint mAccessPoint;
  private int mAccessPointSecurity;
  private final WifiConfigUiBase mConfigUi;
  private TextView mDns1View;
  private TextView mDns2View;
  private TextView mEapAnonymousView;
  private Spinner mEapCaCertSpinner;
  private TextView mEapIdentityView;
  private Spinner mEapMethodSpinner;
  private Spinner mEapUserCertSpinner;
  private boolean mEdit;
  private TextView mGatewayView;
  private final boolean mInXlSetupWizard;
  private TextView mIpAddressView;
  private WifiConfiguration.IpAssignment mIpAssignment = WifiConfiguration.IpAssignment.UNASSIGNED;
  private Spinner mIpSettingsSpinner;
  private LinkProperties mLinkProperties = new LinkProperties();
  private TextView mNetworkPrefixLengthView;
  private TextView mPasswordView;
  private ArrayAdapter<String> mPhase2Adapter;
  private Spinner mPhase2Spinner;
  private TextView mProxyExclusionListView;
  private TextView mProxyHostView;
  private TextView mProxyPortView;
  private WifiConfiguration.ProxySettings mProxySettings = WifiConfiguration.ProxySettings.UNASSIGNED;
  private Spinner mProxySettingsSpinner;
  private Spinner mSecuritySpinner;
  private TextView mSsidView;
  private final Handler mTextViewChangedHandler;
  private final View mView;
  private String unspecifiedCert = "unspecified";

  public WifiConfigController(WifiConfigUiBase paramWifiConfigUiBase, View paramView, AccessPoint paramAccessPoint, boolean paramBoolean)
  {
    this.mConfigUi = paramWifiConfigUiBase;
    this.mInXlSetupWizard = (paramWifiConfigUiBase instanceof WifiConfigUiForSetupWizardXL);
    this.mView = paramView;
    this.mAccessPoint = paramAccessPoint;
    int i;
    Context localContext;
    Resources localResources;
    if (paramAccessPoint == null)
    {
      i = 0;
      this.mAccessPointSecurity = i;
      this.mEdit = paramBoolean;
      this.mTextViewChangedHandler = new Handler();
      localContext = this.mConfigUi.getContext();
      localResources = localContext.getResources();
      this.PHASE2_PEAP_ADAPTER = new ArrayAdapter(localContext, 17367048, localContext.getResources().getStringArray(2131165217));
      this.PHASE2_PEAP_ADAPTER.setDropDownViewResource(17367049);
      this.PHASE2_FULL_ADAPTER = new ArrayAdapter(localContext, 17367048, localContext.getResources().getStringArray(2131165218));
      this.PHASE2_FULL_ADAPTER.setDropDownViewResource(17367049);
      this.unspecifiedCert = localContext.getString(2131427883);
      this.mIpSettingsSpinner = ((Spinner)this.mView.findViewById(2131231173));
      this.mIpSettingsSpinner.setOnItemSelectedListener(this);
      this.mProxySettingsSpinner = ((Spinner)this.mView.findViewById(2131231166));
      this.mProxySettingsSpinner.setOnItemSelectedListener(this);
      if (this.mAccessPoint != null)
        break label477;
      this.mConfigUi.setTitle(2131427834);
      this.mSsidView = ((TextView)this.mView.findViewById(2131231142));
      this.mSsidView.addTextChangedListener(this);
      this.mSecuritySpinner = ((Spinner)this.mView.findViewById(2131231143));
      this.mSecuritySpinner.setOnItemSelectedListener(this);
      if (!this.mInXlSetupWizard)
        break label461;
      this.mView.findViewById(2131231149).setVisibility(0);
      this.mView.findViewById(2131231155).setVisibility(0);
      ArrayAdapter localArrayAdapter = new ArrayAdapter(localContext, 2130968742, 16908308, localContext.getResources().getStringArray(2131165203));
      this.mSecuritySpinner.setAdapter(localArrayAdapter);
      label366: showIpConfigFields();
      showProxyFields();
      this.mView.findViewById(2131231187).setVisibility(0);
      ((CheckBox)this.mView.findViewById(2131231188)).setOnCheckedChangeListener(this);
      this.mConfigUi.setSubmitButton(localContext.getString(2131427913));
    }
    label906: label1079: 
    while (true)
    {
      this.mConfigUi.setCancelButton(localContext.getString(2131427915));
      if (this.mConfigUi.getSubmitButton() != null)
        enableSubmitIfAppropriate();
      return;
      i = paramAccessPoint.security;
      break;
      label461: this.mView.findViewById(2131231103).setVisibility(0);
      break label366;
      label477: this.mConfigUi.setTitle(this.mAccessPoint.ssid);
      ViewGroup localViewGroup = (ViewGroup)this.mView.findViewById(2131231141);
      NetworkInfo.DetailedState localDetailedState = this.mAccessPoint.getState();
      if (localDetailedState != null)
        addRow(localViewGroup, 2131427869, Summary.get(this.mConfigUi.getContext(), localDetailedState));
      int j = this.mAccessPoint.getLevel();
      if (j != -1)
        addRow(localViewGroup, 2131427868, localResources.getStringArray(2131165209)[j]);
      WifiInfo localWifiInfo = this.mAccessPoint.getInfo();
      if ((localWifiInfo != null) && (localWifiInfo.getLinkSpeed() != -1))
        addRow(localViewGroup, 2131427870, localWifiInfo.getLinkSpeed() + "Mbps");
      addRow(localViewGroup, 2131427867, this.mAccessPoint.getSecurityString(false));
      int k = this.mAccessPoint.networkId;
      int m = 0;
      WifiConfiguration localWifiConfiguration;
      if (k != -1)
      {
        localWifiConfiguration = this.mAccessPoint.getConfig();
        if (localWifiConfiguration.ipAssignment == WifiConfiguration.IpAssignment.STATIC)
          this.mIpSettingsSpinner.setSelection(1);
        for (m = 1; ; m = 0)
        {
          Iterator localIterator = localWifiConfiguration.linkProperties.getAddresses().iterator();
          while (localIterator.hasNext())
            addRow(localViewGroup, 2131427871, ((InetAddress)localIterator.next()).getHostAddress());
          this.mIpSettingsSpinner.setSelection(0);
        }
        if (localWifiConfiguration.proxySettings != WifiConfiguration.ProxySettings.STATIC)
          break label906;
        this.mProxySettingsSpinner.setSelection(1);
        m = 1;
      }
      while (true)
      {
        if ((this.mAccessPoint.networkId == -1) || (this.mEdit))
        {
          showSecurityFields();
          showIpConfigFields();
          showProxyFields();
          this.mView.findViewById(2131231187).setVisibility(0);
          ((CheckBox)this.mView.findViewById(2131231188)).setOnCheckedChangeListener(this);
          if (m != 0)
          {
            ((CheckBox)this.mView.findViewById(2131231188)).setChecked(true);
            this.mView.findViewById(2131231189).setVisibility(0);
          }
        }
        if (!this.mEdit)
          break label1004;
        this.mConfigUi.setSubmitButton(localContext.getString(2131427913));
        break;
        if (localWifiConfiguration.proxySettings == WifiConfiguration.ProxySettings.PAC)
        {
          this.mProxySettingsSpinner.setVisibility(8);
          TextView localTextView = (TextView)this.mView.findViewById(2131231190);
          localTextView.setVisibility(0);
          localTextView.setText(localContext.getString(2131427507) + localWifiConfiguration.linkProperties.getHttpProxy().getPacFileUrl());
          m = 1;
        }
        else
        {
          this.mProxySettingsSpinner.setSelection(0);
        }
      }
      label1004: if ((localDetailedState == null) && (j != -1))
        this.mConfigUi.setSubmitButton(localContext.getString(2131427909));
      while (true)
      {
        if (this.mAccessPoint.networkId == -1)
          break label1079;
        this.mConfigUi.setForgetButton(localContext.getString(2131427911));
        break;
        this.mView.findViewById(2131231172).setVisibility(8);
      }
    }
  }

  private void addRow(ViewGroup paramViewGroup, int paramInt, String paramString)
  {
    View localView = this.mConfigUi.getLayoutInflater().inflate(2130968737, paramViewGroup, false);
    ((TextView)localView.findViewById(2131230837)).setText(paramInt);
    ((TextView)localView.findViewById(2131230928)).setText(paramString);
    paramViewGroup.addView(localView);
  }

  private boolean ipAndProxyFieldsAreValid()
  {
    this.mLinkProperties.clear();
    if ((this.mIpSettingsSpinner != null) && (this.mIpSettingsSpinner.getSelectedItemPosition() == 1));
    for (WifiConfiguration.IpAssignment localIpAssignment = WifiConfiguration.IpAssignment.STATIC; ; localIpAssignment = WifiConfiguration.IpAssignment.DHCP)
    {
      this.mIpAssignment = localIpAssignment;
      if ((this.mIpAssignment != WifiConfiguration.IpAssignment.STATIC) || (validateIpConfigFields(this.mLinkProperties) == 0))
        break;
      return false;
    }
    WifiConfiguration.ProxySettings localProxySettings;
    if ((this.mProxySettingsSpinner != null) && (this.mProxySettingsSpinner.getSelectedItemPosition() == 1))
      localProxySettings = WifiConfiguration.ProxySettings.STATIC;
    while (true)
    {
      this.mProxySettings = localProxySettings;
      String str1;
      String str2;
      String str3;
      int i;
      if ((this.mProxySettings == WifiConfiguration.ProxySettings.STATIC) && (this.mProxyHostView != null))
      {
        str1 = this.mProxyHostView.getText().toString();
        str2 = this.mProxyPortView.getText().toString();
        str3 = this.mProxyExclusionListView.getText().toString();
        i = 0;
      }
      try
      {
        i = Integer.parseInt(str2);
        int k = ProxySelector.validate(str1, str2, str3);
        j = k;
        if (j == 0)
        {
          ProxyProperties localProxyProperties = new ProxyProperties(str1, i, str3);
          this.mLinkProperties.setHttpProxy(localProxyProperties);
          return true;
          localProxySettings = WifiConfiguration.ProxySettings.NONE;
        }
      }
      catch (NumberFormatException localNumberFormatException)
      {
        while (true)
          int j = 2131427505;
      }
    }
    return false;
  }

  private void loadCertificates(Spinner paramSpinner, String paramString)
  {
    Context localContext = this.mConfigUi.getContext();
    String[] arrayOfString1 = KeyStore.getInstance().saw(paramString, 1010);
    Object localObject;
    if ((arrayOfString1 == null) || (arrayOfString1.length == 0))
    {
      localObject = new String[1];
      localObject[0] = this.unspecifiedCert;
    }
    while (true)
    {
      ArrayAdapter localArrayAdapter = new ArrayAdapter(localContext, 17367048, (Object[])localObject);
      localArrayAdapter.setDropDownViewResource(17367049);
      paramSpinner.setAdapter(localArrayAdapter);
      return;
      String[] arrayOfString2 = new String[1 + arrayOfString1.length];
      arrayOfString2[0] = this.unspecifiedCert;
      System.arraycopy(arrayOfString1, 0, arrayOfString2, 1, arrayOfString1.length);
      localObject = arrayOfString2;
    }
  }

  private void setAnonymousIdentInvisible()
  {
    this.mView.findViewById(2131231185).setVisibility(8);
    this.mEapAnonymousView.setText("");
  }

  private void setCaCertInvisible()
  {
    this.mView.findViewById(2131231182).setVisibility(8);
    this.mEapCaCertSpinner.setSelection(0);
  }

  private void setPasswordInvisible()
  {
    this.mPasswordView.setText("");
    this.mView.findViewById(2131231154).setVisibility(8);
    this.mView.findViewById(2131231186).setVisibility(8);
  }

  private void setPhase2Invisible()
  {
    this.mView.findViewById(2131231181).setVisibility(8);
    this.mPhase2Spinner.setSelection(0);
  }

  private void setSelection(Spinner paramSpinner, String paramString)
  {
    ArrayAdapter localArrayAdapter;
    if (paramString != null)
      localArrayAdapter = (ArrayAdapter)paramSpinner.getAdapter();
    for (int i = -1 + localArrayAdapter.getCount(); ; i--)
      if (i >= 0)
      {
        if (paramString.equals(localArrayAdapter.getItem(i)))
          paramSpinner.setSelection(i);
      }
      else
        return;
  }

  private void setUserCertInvisible()
  {
    this.mView.findViewById(2131231183).setVisibility(8);
    this.mEapUserCertSpinner.setSelection(0);
  }

  private void showEapFieldsByMethod(int paramInt)
  {
    this.mView.findViewById(2131231180).setVisibility(0);
    this.mView.findViewById(2131231184).setVisibility(0);
    this.mView.findViewById(2131231182).setVisibility(0);
    this.mView.findViewById(2131231154).setVisibility(0);
    this.mView.findViewById(2131231186).setVisibility(0);
    this.mConfigUi.getContext();
    switch (paramInt)
    {
    default:
      return;
    case 3:
      setPhase2Invisible();
      setCaCertInvisible();
      setAnonymousIdentInvisible();
      setUserCertInvisible();
      return;
    case 1:
      this.mView.findViewById(2131231183).setVisibility(0);
      setPhase2Invisible();
      setAnonymousIdentInvisible();
      setPasswordInvisible();
      return;
    case 0:
      if (this.mPhase2Adapter != this.PHASE2_PEAP_ADAPTER)
      {
        this.mPhase2Adapter = this.PHASE2_PEAP_ADAPTER;
        this.mPhase2Spinner.setAdapter(this.mPhase2Adapter);
      }
      this.mView.findViewById(2131231181).setVisibility(0);
      this.mView.findViewById(2131231185).setVisibility(0);
      setUserCertInvisible();
      return;
    case 2:
    }
    if (this.mPhase2Adapter != this.PHASE2_FULL_ADAPTER)
    {
      this.mPhase2Adapter = this.PHASE2_FULL_ADAPTER;
      this.mPhase2Spinner.setAdapter(this.mPhase2Adapter);
    }
    this.mView.findViewById(2131231181).setVisibility(0);
    this.mView.findViewById(2131231185).setVisibility(0);
    setUserCertInvisible();
  }

  private void showIpConfigFields()
  {
    this.mView.findViewById(2131231172).setVisibility(0);
    if ((this.mAccessPoint != null) && (this.mAccessPoint.networkId != -1));
    for (WifiConfiguration localWifiConfiguration = this.mAccessPoint.getConfig(); ; localWifiConfiguration = null)
    {
      if (this.mIpSettingsSpinner.getSelectedItemPosition() == 1)
      {
        this.mView.findViewById(2131231174).setVisibility(0);
        if (this.mIpAddressView == null)
        {
          this.mIpAddressView = ((TextView)this.mView.findViewById(2131231175));
          this.mIpAddressView.addTextChangedListener(this);
          this.mGatewayView = ((TextView)this.mView.findViewById(2131231176));
          this.mGatewayView.addTextChangedListener(this);
          this.mNetworkPrefixLengthView = ((TextView)this.mView.findViewById(2131231177));
          this.mNetworkPrefixLengthView.addTextChangedListener(this);
          this.mDns1View = ((TextView)this.mView.findViewById(2131231178));
          this.mDns1View.addTextChangedListener(this);
          this.mDns2View = ((TextView)this.mView.findViewById(2131231179));
          this.mDns2View.addTextChangedListener(this);
        }
        if (localWifiConfiguration != null)
        {
          LinkProperties localLinkProperties = localWifiConfiguration.linkProperties;
          Iterator localIterator1 = localLinkProperties.getLinkAddresses().iterator();
          if (localIterator1.hasNext())
          {
            LinkAddress localLinkAddress = (LinkAddress)localIterator1.next();
            this.mIpAddressView.setText(localLinkAddress.getAddress().getHostAddress());
            this.mNetworkPrefixLengthView.setText(Integer.toString(localLinkAddress.getNetworkPrefixLength()));
          }
          Iterator localIterator2 = localLinkProperties.getRoutes().iterator();
          while (localIterator2.hasNext())
          {
            RouteInfo localRouteInfo = (RouteInfo)localIterator2.next();
            if (localRouteInfo.isDefaultRoute())
              this.mGatewayView.setText(localRouteInfo.getGateway().getHostAddress());
          }
          Iterator localIterator3 = localLinkProperties.getDnses().iterator();
          if (localIterator3.hasNext())
            this.mDns1View.setText(((InetAddress)localIterator3.next()).getHostAddress());
          if (localIterator3.hasNext())
            this.mDns2View.setText(((InetAddress)localIterator3.next()).getHostAddress());
        }
        return;
      }
      this.mView.findViewById(2131231174).setVisibility(8);
      return;
    }
  }

  private void showProxyFields()
  {
    this.mView.findViewById(2131231164).setVisibility(0);
    if ((this.mAccessPoint != null) && (this.mAccessPoint.networkId != -1));
    for (WifiConfiguration localWifiConfiguration = this.mAccessPoint.getConfig(); ; localWifiConfiguration = null)
    {
      if (this.mProxySettingsSpinner.getSelectedItemPosition() == 1)
      {
        this.mView.findViewById(2131231167).setVisibility(0);
        this.mView.findViewById(2131231168).setVisibility(0);
        if (this.mProxyHostView == null)
        {
          this.mProxyHostView = ((TextView)this.mView.findViewById(2131231169));
          this.mProxyHostView.addTextChangedListener(this);
          this.mProxyPortView = ((TextView)this.mView.findViewById(2131231170));
          this.mProxyPortView.addTextChangedListener(this);
          this.mProxyExclusionListView = ((TextView)this.mView.findViewById(2131231171));
          this.mProxyExclusionListView.addTextChangedListener(this);
        }
        if (localWifiConfiguration != null)
        {
          ProxyProperties localProxyProperties = localWifiConfiguration.linkProperties.getHttpProxy();
          if (localProxyProperties != null)
          {
            this.mProxyHostView.setText(localProxyProperties.getHost());
            this.mProxyPortView.setText(Integer.toString(localProxyProperties.getPort()));
            this.mProxyExclusionListView.setText(localProxyProperties.getExclusionList());
          }
        }
        return;
      }
      this.mView.findViewById(2131231167).setVisibility(8);
      this.mView.findViewById(2131231168).setVisibility(8);
      return;
    }
  }

  private void showSecurityFields()
  {
    if ((this.mInXlSetupWizard) && (!((WifiSettingsForSetupWizardXL)this.mConfigUi.getContext()).initSecurityFields(this.mView, this.mAccessPointSecurity)))
      return;
    if (this.mAccessPointSecurity == 0)
    {
      this.mView.findViewById(2131231152).setVisibility(8);
      return;
    }
    this.mView.findViewById(2131231152).setVisibility(0);
    if (this.mPasswordView == null)
    {
      this.mPasswordView = ((TextView)this.mView.findViewById(2131231123));
      this.mPasswordView.addTextChangedListener(this);
      ((CheckBox)this.mView.findViewById(2131231145)).setOnCheckedChangeListener(this);
      if ((this.mAccessPoint != null) && (this.mAccessPoint.networkId != -1))
        this.mPasswordView.setHint(2131427882);
    }
    if (this.mAccessPointSecurity != 3)
    {
      this.mView.findViewById(2131231157).setVisibility(8);
      return;
    }
    this.mView.findViewById(2131231157).setVisibility(0);
    if (this.mEapMethodSpinner == null)
    {
      this.mEapMethodSpinner = ((Spinner)this.mView.findViewById(2131231158));
      this.mEapMethodSpinner.setOnItemSelectedListener(this);
      this.mPhase2Spinner = ((Spinner)this.mView.findViewById(2131231159));
      this.mEapCaCertSpinner = ((Spinner)this.mView.findViewById(2131231160));
      this.mEapUserCertSpinner = ((Spinner)this.mView.findViewById(2131231161));
      this.mEapIdentityView = ((TextView)this.mView.findViewById(2131231162));
      this.mEapAnonymousView = ((TextView)this.mView.findViewById(2131231163));
      loadCertificates(this.mEapCaCertSpinner, "CACERT_");
      loadCertificates(this.mEapUserCertSpinner, "USRPKEY_");
      if ((this.mAccessPoint != null) && (this.mAccessPoint.networkId != -1))
      {
        WifiEnterpriseConfig localWifiEnterpriseConfig = this.mAccessPoint.getConfig().enterpriseConfig;
        int i = localWifiEnterpriseConfig.getEapMethod();
        int j = localWifiEnterpriseConfig.getPhase2Method();
        this.mEapMethodSpinner.setSelection(i);
        showEapFieldsByMethod(i);
        switch (i)
        {
        default:
          this.mPhase2Spinner.setSelection(j);
        case 0:
        }
        while (true)
        {
          setSelection(this.mEapCaCertSpinner, localWifiEnterpriseConfig.getCaCertificateAlias());
          setSelection(this.mEapUserCertSpinner, localWifiEnterpriseConfig.getClientCertificateAlias());
          this.mEapIdentityView.setText(localWifiEnterpriseConfig.getIdentity());
          this.mEapAnonymousView.setText(localWifiEnterpriseConfig.getAnonymousIdentity());
          return;
          switch (j)
          {
          case 1:
          case 2:
          default:
            Log.e("WifiConfigController", "Invalid phase 2 method " + j);
            break;
          case 0:
            this.mPhase2Spinner.setSelection(0);
            break;
          case 3:
            this.mPhase2Spinner.setSelection(1);
            break;
          case 4:
            this.mPhase2Spinner.setSelection(2);
          }
        }
      }
      this.mEapMethodSpinner.setSelection(0);
      showEapFieldsByMethod(0);
      return;
    }
    showEapFieldsByMethod(this.mEapMethodSpinner.getSelectedItemPosition());
  }

  // ERROR //
  private int validateIpConfigFields(LinkProperties paramLinkProperties)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 522	com/android/settings/wifi/WifiConfigController:mIpAddressView	Landroid/widget/TextView;
    //   4: ifnonnull +5 -> 9
    //   7: iconst_0
    //   8: ireturn
    //   9: aload_0
    //   10: getfield 522	com/android/settings/wifi/WifiConfigController:mIpAddressView	Landroid/widget/TextView;
    //   13: invokevirtual 418	android/widget/TextView:getText	()Ljava/lang/CharSequence;
    //   16: invokevirtual 419	java/lang/Object:toString	()Ljava/lang/String;
    //   19: astore_2
    //   20: aload_2
    //   21: invokestatic 658	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   24: ifeq +7 -> 31
    //   27: ldc_w 659
    //   30: ireturn
    //   31: aload_2
    //   32: invokestatic 665	android/net/NetworkUtils:numericToInetAddress	(Ljava/lang/String;)Ljava/net/InetAddress;
    //   35: astore 4
    //   37: iconst_m1
    //   38: istore 5
    //   40: aload_0
    //   41: getfield 529	com/android/settings/wifi/WifiConfigController:mNetworkPrefixLengthView	Landroid/widget/TextView;
    //   44: invokevirtual 418	android/widget/TextView:getText	()Ljava/lang/CharSequence;
    //   47: invokevirtual 419	java/lang/Object:toString	()Ljava/lang/String;
    //   50: invokestatic 429	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   53: istore 5
    //   55: iload 5
    //   57: iflt +258 -> 315
    //   60: iload 5
    //   62: bipush 32
    //   64: if_icmple +6 -> 70
    //   67: goto +248 -> 315
    //   70: aload_1
    //   71: new 540	android/net/LinkAddress
    //   74: dup
    //   75: aload 4
    //   77: iload 5
    //   79: invokespecial 668	android/net/LinkAddress:<init>	(Ljava/net/InetAddress;I)V
    //   82: invokevirtual 672	android/net/LinkProperties:addLinkAddress	(Landroid/net/LinkAddress;)Z
    //   85: pop
    //   86: aload_0
    //   87: getfield 526	com/android/settings/wifi/WifiConfigController:mGatewayView	Landroid/widget/TextView;
    //   90: invokevirtual 418	android/widget/TextView:getText	()Ljava/lang/CharSequence;
    //   93: invokevirtual 419	java/lang/Object:toString	()Ljava/lang/String;
    //   96: astore 7
    //   98: aload 7
    //   100: invokestatic 658	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   103: ifeq +145 -> 248
    //   106: aload 4
    //   108: iload 5
    //   110: invokestatic 676	android/net/NetworkUtils:getNetworkPart	(Ljava/net/InetAddress;I)Ljava/net/InetAddress;
    //   113: invokevirtual 679	java/net/InetAddress:getAddress	()[B
    //   116: astore 18
    //   118: aload 18
    //   120: iconst_m1
    //   121: aload 18
    //   123: arraylength
    //   124: iadd
    //   125: iconst_1
    //   126: bastore
    //   127: aload_0
    //   128: getfield 526	com/android/settings/wifi/WifiConfigController:mGatewayView	Landroid/widget/TextView;
    //   131: aload 18
    //   133: invokestatic 683	java/net/InetAddress:getByAddress	([B)Ljava/net/InetAddress;
    //   136: invokevirtual 341	java/net/InetAddress:getHostAddress	()Ljava/lang/String;
    //   139: invokevirtual 371	android/widget/TextView:setText	(Ljava/lang/CharSequence;)V
    //   142: aload_0
    //   143: getfield 532	com/android/settings/wifi/WifiConfigController:mDns1View	Landroid/widget/TextView;
    //   146: invokevirtual 418	android/widget/TextView:getText	()Ljava/lang/CharSequence;
    //   149: invokevirtual 419	java/lang/Object:toString	()Ljava/lang/String;
    //   152: astore 10
    //   154: aload 10
    //   156: invokestatic 658	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   159: ifeq +118 -> 277
    //   162: aload_0
    //   163: getfield 532	com/android/settings/wifi/WifiConfigController:mDns1View	Landroid/widget/TextView;
    //   166: aload_0
    //   167: getfield 86	com/android/settings/wifi/WifiConfigController:mConfigUi	Lcom/android/settings/wifi/WifiConfigUiBase;
    //   170: invokeinterface 109 1 0
    //   175: ldc_w 684
    //   178: invokevirtual 143	android/content/Context:getString	(I)Ljava/lang/String;
    //   181: invokevirtual 371	android/widget/TextView:setText	(Ljava/lang/CharSequence;)V
    //   184: aload_0
    //   185: getfield 535	com/android/settings/wifi/WifiConfigController:mDns2View	Landroid/widget/TextView;
    //   188: invokevirtual 687	android/widget/TextView:length	()I
    //   191: ifle +28 -> 219
    //   194: aload_0
    //   195: getfield 535	com/android/settings/wifi/WifiConfigController:mDns2View	Landroid/widget/TextView;
    //   198: invokevirtual 418	android/widget/TextView:getText	()Ljava/lang/CharSequence;
    //   201: invokevirtual 419	java/lang/Object:toString	()Ljava/lang/String;
    //   204: astore 13
    //   206: aload 13
    //   208: invokestatic 665	android/net/NetworkUtils:numericToInetAddress	(Ljava/lang/String;)Ljava/net/InetAddress;
    //   211: astore 15
    //   213: aload_1
    //   214: aload 15
    //   216: invokevirtual 691	android/net/LinkProperties:addDns	(Ljava/net/InetAddress;)V
    //   219: iconst_0
    //   220: ireturn
    //   221: astore 6
    //   223: aload_0
    //   224: getfield 529	com/android/settings/wifi/WifiConfigController:mNetworkPrefixLengthView	Landroid/widget/TextView;
    //   227: aload_0
    //   228: getfield 86	com/android/settings/wifi/WifiConfigController:mConfigUi	Lcom/android/settings/wifi/WifiConfigUiBase;
    //   231: invokeinterface 109 1 0
    //   236: ldc_w 692
    //   239: invokevirtual 143	android/content/Context:getString	(I)Ljava/lang/String;
    //   242: invokevirtual 371	android/widget/TextView:setText	(Ljava/lang/CharSequence;)V
    //   245: goto -159 -> 86
    //   248: aload 7
    //   250: invokestatic 665	android/net/NetworkUtils:numericToInetAddress	(Ljava/lang/String;)Ljava/net/InetAddress;
    //   253: astore 9
    //   255: aload_1
    //   256: new 554	android/net/RouteInfo
    //   259: dup
    //   260: aload 9
    //   262: invokespecial 694	android/net/RouteInfo:<init>	(Ljava/net/InetAddress;)V
    //   265: invokevirtual 698	android/net/LinkProperties:addRoute	(Landroid/net/RouteInfo;)V
    //   268: goto -126 -> 142
    //   271: astore 8
    //   273: ldc_w 699
    //   276: ireturn
    //   277: aload 10
    //   279: invokestatic 665	android/net/NetworkUtils:numericToInetAddress	(Ljava/lang/String;)Ljava/net/InetAddress;
    //   282: astore 12
    //   284: aload_1
    //   285: aload 12
    //   287: invokevirtual 691	android/net/LinkProperties:addDns	(Ljava/net/InetAddress;)V
    //   290: goto -106 -> 184
    //   293: astore 11
    //   295: ldc_w 700
    //   298: ireturn
    //   299: astore 14
    //   301: ldc_w 700
    //   304: ireturn
    //   305: astore 17
    //   307: goto -165 -> 142
    //   310: astore 16
    //   312: goto -170 -> 142
    //   315: ldc_w 701
    //   318: ireturn
    //   319: astore_3
    //   320: ldc_w 659
    //   323: ireturn
    //
    // Exception table:
    //   from	to	target	type
    //   40	55	221	java/lang/NumberFormatException
    //   70	86	221	java/lang/NumberFormatException
    //   248	255	271	java/lang/IllegalArgumentException
    //   277	284	293	java/lang/IllegalArgumentException
    //   206	213	299	java/lang/IllegalArgumentException
    //   106	142	305	java/net/UnknownHostException
    //   106	142	310	java/lang/RuntimeException
    //   31	37	319	java/lang/IllegalArgumentException
  }

  public void afterTextChanged(Editable paramEditable)
  {
    this.mTextViewChangedHandler.post(new Runnable()
    {
      public void run()
      {
        WifiConfigController.this.enableSubmitIfAppropriate();
      }
    });
  }

  public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
  {
  }

  void enableSubmitIfAppropriate()
  {
    Button localButton = this.mConfigUi.getSubmitButton();
    if (localButton == null)
      return;
    TextView localTextView = this.mPasswordView;
    int i = 0;
    if (localTextView != null)
      if ((this.mAccessPointSecurity != 1) || (this.mPasswordView.length() != 0))
      {
        int j = this.mAccessPointSecurity;
        i = 0;
        if (j == 2)
        {
          int k = this.mPasswordView.length();
          i = 0;
          if (k >= 8);
        }
      }
      else
      {
        i = 1;
      }
    boolean bool;
    if (((this.mSsidView != null) && (this.mSsidView.length() == 0)) || (((this.mAccessPoint == null) || (this.mAccessPoint.networkId == -1)) && (i != 0)))
      bool = false;
    while (true)
    {
      localButton.setEnabled(bool);
      return;
      if (ipAndProxyFieldsAreValid())
        bool = true;
      else
        bool = false;
    }
  }

  WifiConfiguration getConfig()
  {
    if ((this.mAccessPoint != null) && (this.mAccessPoint.networkId != -1) && (!this.mEdit))
      return null;
    WifiConfiguration localWifiConfiguration = new WifiConfiguration();
    if (this.mAccessPoint == null)
    {
      localWifiConfiguration.SSID = AccessPoint.convertToQuotedString(this.mSsidView.getText().toString());
      localWifiConfiguration.hiddenSSID = true;
    }
    while (true)
      switch (this.mAccessPointSecurity)
      {
      default:
        return null;
        if (this.mAccessPoint.networkId == -1)
          localWifiConfiguration.SSID = AccessPoint.convertToQuotedString(this.mAccessPoint.ssid);
        else
          localWifiConfiguration.networkId = this.mAccessPoint.networkId;
        break;
      case 0:
      case 1:
      case 2:
      case 3:
      }
    localWifiConfiguration.allowedKeyManagement.set(0);
    while (true)
    {
      localWifiConfiguration.proxySettings = this.mProxySettings;
      localWifiConfiguration.ipAssignment = this.mIpAssignment;
      localWifiConfiguration.linkProperties = new LinkProperties(this.mLinkProperties);
      return localWifiConfiguration;
      localWifiConfiguration.allowedKeyManagement.set(0);
      localWifiConfiguration.allowedAuthAlgorithms.set(0);
      localWifiConfiguration.allowedAuthAlgorithms.set(1);
      if (this.mPasswordView.length() != 0)
      {
        int k = this.mPasswordView.length();
        String str4 = this.mPasswordView.getText().toString();
        if (((k == 10) || (k == 26) || (k == 58)) && (str4.matches("[0-9A-Fa-f]*")))
        {
          localWifiConfiguration.wepKeys[0] = str4;
        }
        else
        {
          localWifiConfiguration.wepKeys[0] = ('"' + str4 + '"');
          continue;
          localWifiConfiguration.allowedKeyManagement.set(1);
          if (this.mPasswordView.length() != 0)
          {
            String str3 = this.mPasswordView.getText().toString();
            if (str3.matches("[0-9A-Fa-f]{64}"))
            {
              localWifiConfiguration.preSharedKey = str3;
            }
            else
            {
              localWifiConfiguration.preSharedKey = ('"' + str3 + '"');
              continue;
              localWifiConfiguration.allowedKeyManagement.set(2);
              localWifiConfiguration.allowedKeyManagement.set(3);
              localWifiConfiguration.enterpriseConfig = new WifiEnterpriseConfig();
              int i = this.mEapMethodSpinner.getSelectedItemPosition();
              int j = this.mPhase2Spinner.getSelectedItemPosition();
              localWifiConfiguration.enterpriseConfig.setEapMethod(i);
              switch (i)
              {
              default:
                localWifiConfiguration.enterpriseConfig.setPhase2Method(j);
              case 0:
              }
              while (true)
              {
                String str1 = (String)this.mEapCaCertSpinner.getSelectedItem();
                if (str1.equals(this.unspecifiedCert))
                  str1 = "";
                localWifiConfiguration.enterpriseConfig.setCaCertificateAlias(str1);
                String str2 = (String)this.mEapUserCertSpinner.getSelectedItem();
                if (str2.equals(this.unspecifiedCert))
                  str2 = "";
                localWifiConfiguration.enterpriseConfig.setClientCertificateAlias(str2);
                localWifiConfiguration.enterpriseConfig.setIdentity(this.mEapIdentityView.getText().toString());
                localWifiConfiguration.enterpriseConfig.setAnonymousIdentity(this.mEapAnonymousView.getText().toString());
                if (!this.mPasswordView.isShown())
                  break label715;
                if (this.mPasswordView.length() <= 0)
                  break;
                localWifiConfiguration.enterpriseConfig.setPassword(this.mPasswordView.getText().toString());
                break;
                switch (j)
                {
                default:
                  Log.e("WifiConfigController", "Unknown phase2 method" + j);
                  break;
                case 0:
                  localWifiConfiguration.enterpriseConfig.setPhase2Method(0);
                  break;
                case 1:
                  localWifiConfiguration.enterpriseConfig.setPhase2Method(3);
                  break;
                case 2:
                  localWifiConfiguration.enterpriseConfig.setPhase2Method(4);
                }
              }
              label715: localWifiConfiguration.enterpriseConfig.setPassword(this.mPasswordView.getText().toString());
            }
          }
        }
      }
    }
  }

  public boolean isEdit()
  {
    return this.mEdit;
  }

  public void onCheckedChanged(CompoundButton paramCompoundButton, boolean paramBoolean)
  {
    if (paramCompoundButton.getId() == 2131231145)
    {
      i = this.mPasswordView.getSelectionEnd();
      localTextView = this.mPasswordView;
      if (paramBoolean)
      {
        j = 144;
        localTextView.setInputType(j | 0x1);
        if (i >= 0)
          ((EditText)this.mPasswordView).setSelection(i);
      }
    }
    while (paramCompoundButton.getId() != 2131231188)
      while (true)
      {
        int i;
        TextView localTextView;
        return;
        int j = 128;
      }
    if (paramBoolean)
    {
      this.mView.findViewById(2131231189).setVisibility(0);
      return;
    }
    this.mView.findViewById(2131231189).setVisibility(8);
  }

  public void onItemSelected(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
  {
    if (paramAdapterView == this.mSecuritySpinner)
    {
      this.mAccessPointSecurity = paramInt;
      showSecurityFields();
    }
    while (true)
    {
      enableSubmitIfAppropriate();
      return;
      if (paramAdapterView == this.mEapMethodSpinner)
        showSecurityFields();
      else if (paramAdapterView == this.mProxySettingsSpinner)
        showProxyFields();
      else
        showIpConfigFields();
    }
  }

  public void onNothingSelected(AdapterView<?> paramAdapterView)
  {
  }

  public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
  {
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.wifi.WifiConfigController
 * JD-Core Version:    0.6.2
 */