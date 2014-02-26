package com.android.settings.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import com.android.internal.app.AlertActivity;
import com.android.internal.app.AlertController;
import com.android.internal.app.AlertController.AlertParams;
import java.util.Locale;

public final class BluetoothPairingDialog extends AlertActivity
  implements DialogInterface.OnClickListener, TextWatcher, CompoundButton.OnCheckedChangeListener
{
  private BluetoothDevice mDevice;
  private Button mOkButton;
  private String mPairingKey;
  private EditText mPairingView;
  private final BroadcastReceiver mReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      String str = paramAnonymousIntent.getAction();
      if ("android.bluetooth.device.action.BOND_STATE_CHANGED".equals(str))
      {
        int i = paramAnonymousIntent.getIntExtra("android.bluetooth.device.extra.BOND_STATE", -2147483648);
        if ((i == 12) || (i == 10))
          BluetoothPairingDialog.this.dismiss();
      }
      BluetoothDevice localBluetoothDevice;
      do
      {
        do
          return;
        while (!"android.bluetooth.device.action.PAIRING_CANCEL".equals(str));
        localBluetoothDevice = (BluetoothDevice)paramAnonymousIntent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
      }
      while ((localBluetoothDevice != null) && (!localBluetoothDevice.equals(BluetoothPairingDialog.this.mDevice)));
      BluetoothPairingDialog.this.dismiss();
    }
  };
  private int mType;

  private void createConfirmationDialog(CachedBluetoothDeviceManager paramCachedBluetoothDeviceManager)
  {
    AlertController.AlertParams localAlertParams = this.mAlertParams;
    localAlertParams.mIconId = 17301659;
    localAlertParams.mTitle = getString(2131427706);
    localAlertParams.mView = createView(paramCachedBluetoothDeviceManager);
    localAlertParams.mPositiveButtonText = getString(2131427716);
    localAlertParams.mPositiveButtonListener = this;
    localAlertParams.mNegativeButtonText = getString(2131427717);
    localAlertParams.mNegativeButtonListener = this;
    setupAlert();
  }

  private void createConsentDialog(CachedBluetoothDeviceManager paramCachedBluetoothDeviceManager)
  {
    AlertController.AlertParams localAlertParams = this.mAlertParams;
    localAlertParams.mIconId = 17301659;
    localAlertParams.mTitle = getString(2131427706);
    localAlertParams.mView = createView(paramCachedBluetoothDeviceManager);
    localAlertParams.mPositiveButtonText = getString(2131427716);
    localAlertParams.mPositiveButtonListener = this;
    localAlertParams.mNegativeButtonText = getString(2131427717);
    localAlertParams.mNegativeButtonListener = this;
    setupAlert();
  }

  private void createDisplayPasskeyOrPinDialog(CachedBluetoothDeviceManager paramCachedBluetoothDeviceManager)
  {
    AlertController.AlertParams localAlertParams = this.mAlertParams;
    localAlertParams.mIconId = 17301659;
    localAlertParams.mTitle = getString(2131427706);
    localAlertParams.mView = createView(paramCachedBluetoothDeviceManager);
    localAlertParams.mNegativeButtonText = getString(17039360);
    localAlertParams.mNegativeButtonListener = this;
    setupAlert();
    if (this.mType == 4)
      this.mDevice.setPairingConfirmation(true);
    while (this.mType != 5)
      return;
    byte[] arrayOfByte = BluetoothDevice.convertPinToBytes(this.mPairingKey);
    this.mDevice.setPin(arrayOfByte);
  }

  private View createPinEntryView(String paramString)
  {
    View localView = getLayoutInflater().inflate(2130968594, null);
    TextView localTextView1 = (TextView)localView.findViewById(2131230749);
    TextView localTextView2 = (TextView)localView.findViewById(2131230754);
    CheckBox localCheckBox = (CheckBox)localView.findViewById(2131230753);
    this.mPairingView = ((EditText)localView.findViewById(2131230751));
    this.mPairingView.addTextChangedListener(this);
    localCheckBox.setOnCheckedChangeListener(this);
    int i;
    int j;
    int k;
    switch (this.mType)
    {
    default:
      Log.e("BluetoothPairingDialog", "Incorrect pairing type for createPinEntryView: " + this.mType);
      return null;
    case 0:
      i = 2131427707;
      j = 2131427711;
      k = 16;
    case 1:
    }
    while (true)
    {
      localTextView1.setText(Html.fromHtml(getString(i, new Object[] { paramString })));
      localTextView2.setText(j);
      this.mPairingView.setInputType(2);
      EditText localEditText = this.mPairingView;
      InputFilter[] arrayOfInputFilter = new InputFilter[1];
      arrayOfInputFilter[0] = new InputFilter.LengthFilter(k);
      localEditText.setFilters(arrayOfInputFilter);
      return localView;
      i = 2131427708;
      j = 2131427712;
      k = 6;
      localCheckBox.setVisibility(8);
    }
  }

  private void createUserEntryDialog(CachedBluetoothDeviceManager paramCachedBluetoothDeviceManager)
  {
    AlertController.AlertParams localAlertParams = this.mAlertParams;
    localAlertParams.mIconId = 17301659;
    localAlertParams.mTitle = getString(2131427706);
    localAlertParams.mView = createPinEntryView(paramCachedBluetoothDeviceManager.getName(this.mDevice));
    localAlertParams.mPositiveButtonText = getString(17039370);
    localAlertParams.mPositiveButtonListener = this;
    localAlertParams.mNegativeButtonText = getString(17039360);
    localAlertParams.mNegativeButtonListener = this;
    setupAlert();
    this.mOkButton = this.mAlert.getButton(-1);
    this.mOkButton.setEnabled(false);
  }

  private View createView(CachedBluetoothDeviceManager paramCachedBluetoothDeviceManager)
  {
    View localView = getLayoutInflater().inflate(2130968593, null);
    String str1 = paramCachedBluetoothDeviceManager.getName(this.mDevice);
    TextView localTextView = (TextView)localView.findViewById(2131230749);
    String str2;
    switch (this.mType)
    {
    default:
      Log.e("BluetoothPairingDialog", "Incorrect pairing type received, not creating view");
      return null;
    case 2:
      Object[] arrayOfObject2 = new Object[2];
      arrayOfObject2[0] = str1;
      arrayOfObject2[1] = this.mPairingKey;
      str2 = getString(2131427713, arrayOfObject2);
    case 3:
    case 6:
    case 4:
    case 5:
    }
    while (true)
    {
      localTextView.setText(Html.fromHtml(str2));
      return localView;
      str2 = getString(2131427714, new Object[] { str1 });
      continue;
      Object[] arrayOfObject1 = new Object[2];
      arrayOfObject1[0] = str1;
      arrayOfObject1[1] = this.mPairingKey;
      str2 = getString(2131427715, arrayOfObject1);
    }
  }

  private void onCancel()
  {
    this.mDevice.cancelPairingUserInput();
  }

  private void onPair(String paramString)
  {
    switch (this.mType)
    {
    default:
      Log.e("BluetoothPairingDialog", "Incorrect pairing type received");
    case 4:
    case 5:
    case 0:
      byte[] arrayOfByte;
      do
      {
        return;
        arrayOfByte = BluetoothDevice.convertPinToBytes(paramString);
      }
      while (arrayOfByte == null);
      this.mDevice.setPin(arrayOfByte);
      return;
    case 1:
      int i = Integer.parseInt(paramString);
      this.mDevice.setPasskey(i);
      return;
    case 2:
    case 3:
      this.mDevice.setPairingConfirmation(true);
      return;
    case 6:
    }
    this.mDevice.setRemoteOutOfBandData();
  }

  public void afterTextChanged(Editable paramEditable)
  {
    Button localButton;
    if (this.mOkButton != null)
    {
      localButton = this.mOkButton;
      if (paramEditable.length() <= 0)
        break label29;
    }
    label29: for (boolean bool = true; ; bool = false)
    {
      localButton.setEnabled(bool);
      return;
    }
  }

  public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
  {
  }

  public void onCheckedChanged(CompoundButton paramCompoundButton, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.mPairingView.setInputType(1);
      return;
    }
    this.mPairingView.setInputType(2);
  }

  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    switch (paramInt)
    {
    default:
      onCancel();
      return;
    case -1:
    }
    if (this.mPairingView != null)
    {
      onPair(this.mPairingView.getText().toString());
      return;
    }
    onPair(null);
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    Intent localIntent = getIntent();
    if (!localIntent.getAction().equals("android.bluetooth.device.action.PAIRING_REQUEST"))
    {
      Log.e("BluetoothPairingDialog", "Error: this activity may be started only with intent android.bluetooth.device.action.PAIRING_REQUEST");
      finish();
      return;
    }
    LocalBluetoothManager localLocalBluetoothManager = LocalBluetoothManager.getInstance(this);
    if (localLocalBluetoothManager == null)
    {
      Log.e("BluetoothPairingDialog", "Error: BluetoothAdapter not supported by system");
      finish();
      return;
    }
    CachedBluetoothDeviceManager localCachedBluetoothDeviceManager = localLocalBluetoothManager.getCachedDeviceManager();
    this.mDevice = ((BluetoothDevice)localIntent.getParcelableExtra("android.bluetooth.device.extra.DEVICE"));
    this.mType = localIntent.getIntExtra("android.bluetooth.device.extra.PAIRING_VARIANT", -2147483648);
    switch (this.mType)
    {
    default:
      Log.e("BluetoothPairingDialog", "Incorrect pairing type received, not showing any dialog");
    case 0:
    case 1:
    case 2:
    case 3:
    case 6:
      while (true)
      {
        registerReceiver(this.mReceiver, new IntentFilter("android.bluetooth.device.action.PAIRING_CANCEL"));
        registerReceiver(this.mReceiver, new IntentFilter("android.bluetooth.device.action.BOND_STATE_CHANGED"));
        return;
        createUserEntryDialog(localCachedBluetoothDeviceManager);
        continue;
        int j = localIntent.getIntExtra("android.bluetooth.device.extra.PAIRING_KEY", -2147483648);
        if (j == -2147483648)
        {
          Log.e("BluetoothPairingDialog", "Invalid Confirmation Passkey received, not showing any dialog");
          return;
        }
        Locale localLocale = Locale.US;
        Object[] arrayOfObject3 = new Object[1];
        arrayOfObject3[0] = Integer.valueOf(j);
        this.mPairingKey = String.format(localLocale, "%06d", arrayOfObject3);
        createConfirmationDialog(localCachedBluetoothDeviceManager);
        continue;
        createConsentDialog(localCachedBluetoothDeviceManager);
      }
    case 4:
    case 5:
    }
    int i = localIntent.getIntExtra("android.bluetooth.device.extra.PAIRING_KEY", -2147483648);
    if (i == -2147483648)
    {
      Log.e("BluetoothPairingDialog", "Invalid Confirmation Passkey or PIN received, not showing any dialog");
      return;
    }
    Object[] arrayOfObject2;
    if (this.mType == 4)
    {
      arrayOfObject2 = new Object[1];
      arrayOfObject2[0] = Integer.valueOf(i);
    }
    Object[] arrayOfObject1;
    for (this.mPairingKey = String.format("%06d", arrayOfObject2); ; this.mPairingKey = String.format("%04d", arrayOfObject1))
    {
      createDisplayPasskeyOrPinDialog(localCachedBluetoothDeviceManager);
      break;
      arrayOfObject1 = new Object[1];
      arrayOfObject1[0] = Integer.valueOf(i);
    }
  }

  protected void onDestroy()
  {
    super.onDestroy();
    unregisterReceiver(this.mReceiver);
  }

  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    if (paramInt == 4)
      onCancel();
    return super.onKeyDown(paramInt, paramKeyEvent);
  }

  public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
  {
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.bluetooth.BluetoothPairingDialog
 * JD-Core Version:    0.6.2
 */