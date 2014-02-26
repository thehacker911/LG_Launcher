package com.android.settings.vpn2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.security.KeyStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import com.android.internal.net.VpnProfile;
import java.net.InetAddress;

class VpnDialog extends AlertDialog
  implements TextWatcher, View.OnClickListener, AdapterView.OnItemSelectedListener
{
  private TextView mDnsServers;
  private boolean mEditing;
  private Spinner mIpsecCaCert;
  private TextView mIpsecIdentifier;
  private TextView mIpsecSecret;
  private Spinner mIpsecServerCert;
  private Spinner mIpsecUserCert;
  private final KeyStore mKeyStore = KeyStore.getInstance();
  private TextView mL2tpSecret;
  private final DialogInterface.OnClickListener mListener;
  private CheckBox mMppe;
  private TextView mName;
  private TextView mPassword;
  private final VpnProfile mProfile;
  private TextView mRoutes;
  private CheckBox mSaveLogin;
  private TextView mSearchDomains;
  private TextView mServer;
  private Spinner mType;
  private TextView mUsername;
  private View mView;

  VpnDialog(Context paramContext, DialogInterface.OnClickListener paramOnClickListener, VpnProfile paramVpnProfile, boolean paramBoolean)
  {
    super(paramContext);
    this.mListener = paramOnClickListener;
    this.mProfile = paramVpnProfile;
    this.mEditing = paramBoolean;
  }

  private void changeType(int paramInt)
  {
    this.mMppe.setVisibility(8);
    this.mView.findViewById(2131231106).setVisibility(8);
    this.mView.findViewById(2131231108).setVisibility(8);
    this.mView.findViewById(2131231111).setVisibility(8);
    this.mView.findViewById(2131231113).setVisibility(8);
    switch (paramInt)
    {
    default:
      return;
    case 0:
      this.mMppe.setVisibility(0);
      return;
    case 1:
      this.mView.findViewById(2131231106).setVisibility(0);
    case 3:
      this.mView.findViewById(2131231108).setVisibility(0);
      return;
    case 2:
      this.mView.findViewById(2131231106).setVisibility(0);
    case 4:
      this.mView.findViewById(2131231111).setVisibility(0);
    case 5:
    }
    this.mView.findViewById(2131231113).setVisibility(0);
  }

  private void loadCertificates(Spinner paramSpinner, String paramString1, int paramInt, String paramString2)
  {
    Context localContext = getContext();
    String str;
    String[] arrayOfString1;
    Object localObject;
    if (paramInt == 0)
    {
      str = "";
      arrayOfString1 = this.mKeyStore.saw(paramString1);
      if ((arrayOfString1 != null) && (arrayOfString1.length != 0))
        break label116;
      localObject = new String[] { str };
      label46: ArrayAdapter localArrayAdapter = new ArrayAdapter(localContext, 17367048, (Object[])localObject);
      localArrayAdapter.setDropDownViewResource(17367049);
      paramSpinner.setAdapter(localArrayAdapter);
    }
    for (int i = 1; ; i++)
      if (i < localObject.length)
      {
        if (localObject[i].equals(paramString2))
          paramSpinner.setSelection(i);
      }
      else
      {
        return;
        str = localContext.getString(paramInt);
        break;
        label116: String[] arrayOfString2 = new String[1 + arrayOfString1.length];
        arrayOfString2[0] = str;
        System.arraycopy(arrayOfString1, 0, arrayOfString2, 1, arrayOfString1.length);
        localObject = arrayOfString2;
        break label46;
      }
  }

  private boolean validate(boolean paramBoolean)
  {
    if (!paramBoolean)
      if ((this.mUsername.getText().length() == 0) || (this.mPassword.getText().length() == 0));
    do
    {
      do
      {
        return true;
        return false;
        if ((this.mName.getText().length() == 0) || (this.mServer.getText().length() == 0) || (!validateAddresses(this.mDnsServers.getText().toString(), false)) || (!validateAddresses(this.mRoutes.getText().toString(), true)))
          return false;
        switch (this.mType.getSelectedItemPosition())
        {
        case 0:
        case 5:
        default:
          return false;
        case 1:
        case 3:
        case 2:
        case 4:
        }
      }
      while (this.mIpsecSecret.getText().length() != 0);
      return false;
    }
    while (this.mIpsecUserCert.getSelectedItemPosition() != 0);
    return false;
  }

  private boolean validateAddresses(String paramString, boolean paramBoolean)
  {
    while (true)
    {
      int j;
      try
      {
        String[] arrayOfString1 = paramString.split(" ");
        int i = arrayOfString1.length;
        j = 0;
        if (j < i)
        {
          String str = arrayOfString1[j];
          if (str.isEmpty())
            break label175;
          int k = 32;
          if (paramBoolean)
          {
            String[] arrayOfString2 = str.split("/", 2);
            str = arrayOfString2[0];
            k = Integer.parseInt(arrayOfString2[1]);
          }
          byte[] arrayOfByte = InetAddress.parseNumericAddress(str).getAddress();
          int m = 0xFF & arrayOfByte[3] | (0xFF & arrayOfByte[2]) << 8 | (0xFF & arrayOfByte[1]) << 16 | (0xFF & arrayOfByte[0]) << 24;
          int n = arrayOfByte.length;
          if ((n == 4) && (k >= 0) && (k <= 32) && ((k >= 32) || (m << k == 0)))
            break label175;
          return false;
        }
      }
      catch (Exception localException)
      {
        return false;
      }
      return true;
      label175: j++;
    }
  }

  public void afterTextChanged(Editable paramEditable)
  {
    getButton(-1).setEnabled(validate(this.mEditing));
  }

  public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
  {
  }

  VpnProfile getProfile()
  {
    VpnProfile localVpnProfile = new VpnProfile(this.mProfile.key);
    localVpnProfile.name = this.mName.getText().toString();
    localVpnProfile.type = this.mType.getSelectedItemPosition();
    localVpnProfile.server = this.mServer.getText().toString().trim();
    localVpnProfile.username = this.mUsername.getText().toString();
    localVpnProfile.password = this.mPassword.getText().toString();
    localVpnProfile.searchDomains = this.mSearchDomains.getText().toString().trim();
    localVpnProfile.dnsServers = this.mDnsServers.getText().toString().trim();
    localVpnProfile.routes = this.mRoutes.getText().toString().trim();
    switch (localVpnProfile.type)
    {
    default:
    case 0:
    case 1:
    case 3:
    case 2:
    case 4:
    case 5:
    }
    while (true)
    {
      localVpnProfile.saveLogin = this.mSaveLogin.isChecked();
      return localVpnProfile;
      localVpnProfile.mppe = this.mMppe.isChecked();
      continue;
      localVpnProfile.l2tpSecret = this.mL2tpSecret.getText().toString();
      localVpnProfile.ipsecIdentifier = this.mIpsecIdentifier.getText().toString();
      localVpnProfile.ipsecSecret = this.mIpsecSecret.getText().toString();
      continue;
      localVpnProfile.l2tpSecret = this.mL2tpSecret.getText().toString();
      if (this.mIpsecUserCert.getSelectedItemPosition() != 0)
        localVpnProfile.ipsecUserCert = ((String)this.mIpsecUserCert.getSelectedItem());
      if (this.mIpsecCaCert.getSelectedItemPosition() != 0)
        localVpnProfile.ipsecCaCert = ((String)this.mIpsecCaCert.getSelectedItem());
      if (this.mIpsecServerCert.getSelectedItemPosition() != 0)
        localVpnProfile.ipsecServerCert = ((String)this.mIpsecServerCert.getSelectedItem());
    }
  }

  boolean isEditing()
  {
    return this.mEditing;
  }

  public void onClick(View paramView)
  {
    paramView.setVisibility(8);
    this.mView.findViewById(2131231117).setVisibility(0);
  }

  protected void onCreate(Bundle paramBundle)
  {
    this.mView = getLayoutInflater().inflate(2130968728, null);
    setView(this.mView);
    setInverseBackgroundForced(true);
    Context localContext = getContext();
    this.mName = ((TextView)this.mView.findViewById(2131230837));
    this.mType = ((Spinner)this.mView.findViewById(2131231103));
    this.mServer = ((TextView)this.mView.findViewById(2131231104));
    this.mUsername = ((TextView)this.mView.findViewById(2131231122));
    this.mPassword = ((TextView)this.mView.findViewById(2131231123));
    this.mSearchDomains = ((TextView)this.mView.findViewById(2131231118));
    this.mDnsServers = ((TextView)this.mView.findViewById(2131231119));
    this.mRoutes = ((TextView)this.mView.findViewById(2131231120));
    this.mMppe = ((CheckBox)this.mView.findViewById(2131231105));
    this.mL2tpSecret = ((TextView)this.mView.findViewById(2131231107));
    this.mIpsecIdentifier = ((TextView)this.mView.findViewById(2131231109));
    this.mIpsecSecret = ((TextView)this.mView.findViewById(2131231110));
    this.mIpsecUserCert = ((Spinner)this.mView.findViewById(2131231112));
    this.mIpsecCaCert = ((Spinner)this.mView.findViewById(2131231114));
    this.mIpsecServerCert = ((Spinner)this.mView.findViewById(2131231115));
    this.mSaveLogin = ((CheckBox)this.mView.findViewById(2131231124));
    this.mName.setText(this.mProfile.name);
    this.mType.setSelection(this.mProfile.type);
    this.mServer.setText(this.mProfile.server);
    if (this.mProfile.saveLogin)
    {
      this.mUsername.setText(this.mProfile.username);
      this.mPassword.setText(this.mProfile.password);
    }
    this.mSearchDomains.setText(this.mProfile.searchDomains);
    this.mDnsServers.setText(this.mProfile.dnsServers);
    this.mRoutes.setText(this.mProfile.routes);
    this.mMppe.setChecked(this.mProfile.mppe);
    this.mL2tpSecret.setText(this.mProfile.l2tpSecret);
    this.mIpsecIdentifier.setText(this.mProfile.ipsecIdentifier);
    this.mIpsecSecret.setText(this.mProfile.ipsecSecret);
    loadCertificates(this.mIpsecUserCert, "USRPKEY_", 0, this.mProfile.ipsecUserCert);
    loadCertificates(this.mIpsecCaCert, "CACERT_", 2131429154, this.mProfile.ipsecCaCert);
    loadCertificates(this.mIpsecServerCert, "USRCERT_", 2131429155, this.mProfile.ipsecServerCert);
    this.mSaveLogin.setChecked(this.mProfile.saveLogin);
    this.mName.addTextChangedListener(this);
    this.mType.setOnItemSelectedListener(this);
    this.mServer.addTextChangedListener(this);
    this.mUsername.addTextChangedListener(this);
    this.mPassword.addTextChangedListener(this);
    this.mDnsServers.addTextChangedListener(this);
    this.mRoutes.addTextChangedListener(this);
    this.mIpsecSecret.addTextChangedListener(this);
    this.mIpsecUserCert.setOnItemSelectedListener(this);
    boolean bool1 = validate(true);
    boolean bool2;
    View localView;
    label752: Button localButton;
    if ((this.mEditing) || (!bool1))
    {
      bool2 = true;
      this.mEditing = bool2;
      if (!this.mEditing)
        break label835;
      setTitle(2131429159);
      this.mView.findViewById(2131231102).setVisibility(0);
      changeType(this.mProfile.type);
      localView = this.mView.findViewById(2131231116);
      if ((!this.mProfile.searchDomains.isEmpty()) || (!this.mProfile.dnsServers.isEmpty()) || (!this.mProfile.routes.isEmpty()))
        break label826;
      localView.setOnClickListener(this);
      setButton(-1, localContext.getString(2131429157), this.mListener);
      label768: setButton(-2, localContext.getString(2131429156), this.mListener);
      super.onCreate(null);
      localButton = getButton(-1);
      if (!this.mEditing)
        break label898;
    }
    while (true)
    {
      localButton.setEnabled(bool1);
      getWindow().setSoftInputMode(20);
      return;
      bool2 = false;
      break;
      label826: onClick(localView);
      break label752;
      label835: Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = this.mProfile.name;
      setTitle(localContext.getString(2131429160, arrayOfObject));
      this.mView.findViewById(2131231121).setVisibility(0);
      setButton(-1, localContext.getString(2131429158), this.mListener);
      break label768;
      label898: bool1 = validate(false);
    }
  }

  public void onItemSelected(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
  {
    if (paramAdapterView == this.mType)
      changeType(paramInt);
    getButton(-1).setEnabled(validate(this.mEditing));
  }

  public void onNothingSelected(AdapterView<?> paramAdapterView)
  {
  }

  public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
  {
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.vpn2.VpnDialog
 * JD-Core Version:    0.6.2
 */