package com.android.settings.wifi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.BitSet;

public class WifiApDialog extends AlertDialog
  implements TextWatcher, View.OnClickListener, AdapterView.OnItemSelectedListener
{
  private final DialogInterface.OnClickListener mListener;
  private EditText mPassword;
  private int mSecurityTypeIndex = 0;
  private TextView mSsid;
  private View mView;
  WifiConfiguration mWifiConfig;

  public WifiApDialog(Context paramContext, DialogInterface.OnClickListener paramOnClickListener, WifiConfiguration paramWifiConfiguration)
  {
    super(paramContext);
    this.mListener = paramOnClickListener;
    this.mWifiConfig = paramWifiConfiguration;
    if (paramWifiConfiguration != null)
      this.mSecurityTypeIndex = getSecurityTypeIndex(paramWifiConfiguration);
  }

  public static int getSecurityTypeIndex(WifiConfiguration paramWifiConfiguration)
  {
    if (paramWifiConfiguration.allowedKeyManagement.get(1))
      return 1;
    if (paramWifiConfiguration.allowedKeyManagement.get(4))
      return 2;
    return 0;
  }

  private void showSecurityFields()
  {
    if (this.mSecurityTypeIndex == 0)
    {
      this.mView.findViewById(2131231144).setVisibility(8);
      return;
    }
    this.mView.findViewById(2131231144).setVisibility(0);
  }

  private void validate()
  {
    if (((this.mSsid != null) && (this.mSsid.length() == 0)) || (((this.mSecurityTypeIndex == 1) || (this.mSecurityTypeIndex == 2)) && (this.mPassword.length() < 8)))
    {
      getButton(-1).setEnabled(false);
      return;
    }
    getButton(-1).setEnabled(true);
  }

  public void afterTextChanged(Editable paramEditable)
  {
    validate();
  }

  public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
  {
  }

  public WifiConfiguration getConfig()
  {
    WifiConfiguration localWifiConfiguration = new WifiConfiguration();
    localWifiConfiguration.SSID = this.mSsid.getText().toString();
    switch (this.mSecurityTypeIndex)
    {
    default:
      localWifiConfiguration = null;
    case 0:
    case 1:
    case 2:
    }
    do
    {
      do
      {
        return localWifiConfiguration;
        localWifiConfiguration.allowedKeyManagement.set(0);
        return localWifiConfiguration;
        localWifiConfiguration.allowedKeyManagement.set(1);
        localWifiConfiguration.allowedAuthAlgorithms.set(0);
      }
      while (this.mPassword.length() == 0);
      localWifiConfiguration.preSharedKey = this.mPassword.getText().toString();
      return localWifiConfiguration;
      localWifiConfiguration.allowedKeyManagement.set(4);
      localWifiConfiguration.allowedAuthAlgorithms.set(0);
    }
    while (this.mPassword.length() == 0);
    localWifiConfiguration.preSharedKey = this.mPassword.getText().toString();
    return localWifiConfiguration;
  }

  public void onClick(View paramView)
  {
    EditText localEditText = this.mPassword;
    if (((CheckBox)paramView).isChecked());
    for (int i = 144; ; i = 128)
    {
      localEditText.setInputType(i | 0x1);
      return;
    }
  }

  protected void onCreate(Bundle paramBundle)
  {
    this.mView = getLayoutInflater().inflate(2130968732, null);
    Spinner localSpinner = (Spinner)this.mView.findViewById(2131231143);
    setView(this.mView);
    setInverseBackgroundForced(true);
    Context localContext = getContext();
    setTitle(2131427963);
    this.mView.findViewById(2131231103).setVisibility(0);
    this.mSsid = ((TextView)this.mView.findViewById(2131231142));
    this.mPassword = ((EditText)this.mView.findViewById(2131231123));
    setButton(-1, localContext.getString(2131427913), this.mListener);
    setButton(-2, localContext.getString(2131427915), this.mListener);
    if (this.mWifiConfig != null)
    {
      this.mSsid.setText(this.mWifiConfig.SSID);
      localSpinner.setSelection(this.mSecurityTypeIndex);
      if ((this.mSecurityTypeIndex == 1) || (this.mSecurityTypeIndex == 2))
        this.mPassword.setText(this.mWifiConfig.preSharedKey);
    }
    this.mSsid.addTextChangedListener(this);
    this.mPassword.addTextChangedListener(this);
    ((CheckBox)this.mView.findViewById(2131231145)).setOnClickListener(this);
    localSpinner.setOnItemSelectedListener(this);
    super.onCreate(paramBundle);
    showSecurityFields();
    validate();
  }

  public void onItemSelected(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
  {
    this.mSecurityTypeIndex = paramInt;
    showSecurityFields();
    validate();
  }

  public void onNothingSelected(AdapterView<?> paramAdapterView)
  {
  }

  public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
  {
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.wifi.WifiApDialog
 * JD-Core Version:    0.6.2
 */