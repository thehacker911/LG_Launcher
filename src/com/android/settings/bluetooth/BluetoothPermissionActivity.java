package com.android.settings.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.android.internal.app.AlertActivity;
import com.android.internal.app.AlertController;
import com.android.internal.app.AlertController.AlertParams;

public class BluetoothPermissionActivity extends AlertActivity
  implements DialogInterface.OnClickListener, Preference.OnPreferenceChangeListener
{
  private BluetoothDevice mDevice;
  private Button mOkButton;
  private BroadcastReceiver mReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if ((!paramAnonymousIntent.getAction().equals("android.bluetooth.device.action.CONNECTION_ACCESS_CANCEL")) || (paramAnonymousIntent.getIntExtra("android.bluetooth.device.extra.ACCESS_REQUEST_TYPE", 2) != BluetoothPermissionActivity.this.mRequestType));
      BluetoothDevice localBluetoothDevice;
      do
      {
        return;
        localBluetoothDevice = (BluetoothDevice)paramAnonymousIntent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
      }
      while (!BluetoothPermissionActivity.this.mDevice.equals(localBluetoothDevice));
      BluetoothPermissionActivity.this.dismissDialog();
    }
  };
  private boolean mReceiverRegistered = false;
  private int mRequestType = 0;
  private String mReturnClass = null;
  private String mReturnPackage = null;
  private View mView;
  private TextView messageView;

  private View createConnectionDialogView()
  {
    String str = createRemoteName();
    this.mView = getLayoutInflater().inflate(2130968591, null);
    this.messageView = ((TextView)this.mView.findViewById(2131230749));
    this.messageView.setText(getString(2131427470, new Object[] { str }));
    return this.mView;
  }

  private View createMapDialogView()
  {
    String str = createRemoteName();
    this.mView = getLayoutInflater().inflate(2130968591, null);
    this.messageView = ((TextView)this.mView.findViewById(2131230749));
    this.messageView.setText(getString(2131427476, new Object[] { str, str }));
    return this.mView;
  }

  private View createPhonebookDialogView()
  {
    String str = createRemoteName();
    this.mView = getLayoutInflater().inflate(2130968591, null);
    this.messageView = ((TextView)this.mView.findViewById(2131230749));
    this.messageView.setText(getString(2131427472, new Object[] { str, str }));
    return this.mView;
  }

  private String createRemoteName()
  {
    if (this.mDevice != null);
    for (String str = this.mDevice.getAliasName(); ; str = null)
    {
      if (str == null)
        str = getString(2131428395);
      return str;
    }
  }

  private void dismissDialog()
  {
    dismiss();
  }

  private void onNegative()
  {
    Log.d("BluetoothPermissionActivity", "onNegative");
    savePermissionChoice(this.mRequestType, 2);
    sendIntentToReceiver("android.bluetooth.device.action.CONNECTION_ACCESS_REPLY", false, null, false);
    finish();
  }

  private void onPositive()
  {
    Log.d("BluetoothPermissionActivity", "onPositive");
    savePermissionChoice(this.mRequestType, 1);
    sendIntentToReceiver("android.bluetooth.device.action.CONNECTION_ACCESS_REPLY", true, "android.bluetooth.device.extra.ALWAYS_ALLOWED", true);
    finish();
  }

  private void savePermissionChoice(int paramInt1, int paramInt2)
  {
    LocalBluetoothManager localLocalBluetoothManager = LocalBluetoothManager.getInstance(this);
    CachedBluetoothDeviceManager localCachedBluetoothDeviceManager = localLocalBluetoothManager.getCachedDeviceManager();
    CachedBluetoothDevice localCachedBluetoothDevice = localCachedBluetoothDeviceManager.findDevice(this.mDevice);
    Log.d("BluetoothPermissionActivity", "savePermissionChoice permissionType: " + paramInt1);
    if (localCachedBluetoothDevice == null)
      localCachedBluetoothDevice = localCachedBluetoothDeviceManager.addDevice(localLocalBluetoothManager.getBluetoothAdapter(), localLocalBluetoothManager.getProfileManager(), this.mDevice);
    if (paramInt1 == 2)
      localCachedBluetoothDevice.setPhonebookPermissionChoice(paramInt2);
    while (paramInt1 != 3)
      return;
    localCachedBluetoothDevice.setMessagePermissionChoice(paramInt2);
  }

  private void sendIntentToReceiver(String paramString1, boolean paramBoolean1, String paramString2, boolean paramBoolean2)
  {
    Intent localIntent = new Intent(paramString1);
    if ((this.mReturnPackage != null) && (this.mReturnClass != null))
      localIntent.setClassName(this.mReturnPackage, this.mReturnClass);
    Log.i("BluetoothPermissionActivity", "sendIntentToReceiver() Request type: " + this.mRequestType + " mReturnPackage" + this.mReturnPackage + " mReturnClass" + this.mReturnClass);
    if (paramBoolean1);
    for (int i = 1; ; i = 2)
    {
      localIntent.putExtra("android.bluetooth.device.extra.CONNECTION_ACCESS_RESULT", i);
      if (paramString2 != null)
        localIntent.putExtra(paramString2, paramBoolean2);
      localIntent.putExtra("android.bluetooth.device.extra.DEVICE", this.mDevice);
      localIntent.putExtra("android.bluetooth.device.extra.ACCESS_REQUEST_TYPE", this.mRequestType);
      sendBroadcast(localIntent, "android.permission.BLUETOOTH_ADMIN");
      return;
    }
  }

  private void showDialog(String paramString, int paramInt)
  {
    AlertController.AlertParams localAlertParams = this.mAlertParams;
    localAlertParams.mIconId = 17301659;
    localAlertParams.mTitle = paramString;
    Log.i("BluetoothPermissionActivity", "showDialog() Request type: " + this.mRequestType + " this: " + this);
    switch (paramInt)
    {
    default:
    case 1:
    case 2:
    case 3:
    }
    while (true)
    {
      localAlertParams.mPositiveButtonText = getString(2131427334);
      localAlertParams.mPositiveButtonListener = this;
      localAlertParams.mNegativeButtonText = getString(2131427335);
      localAlertParams.mNegativeButtonListener = this;
      this.mOkButton = this.mAlert.getButton(-1);
      setupAlert();
      return;
      localAlertParams.mView = createConnectionDialogView();
      continue;
      localAlertParams.mView = createPhonebookDialogView();
      continue;
      localAlertParams.mView = createMapDialogView();
    }
  }

  public void onBackPressed()
  {
    Log.i("BluetoothPermissionActivity", "Back button pressed! ignoring");
  }

  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    switch (paramInt)
    {
    default:
      return;
    case -1:
      onPositive();
      return;
    case -2:
    }
    onNegative();
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    Intent localIntent = getIntent();
    if (!localIntent.getAction().equals("android.bluetooth.device.action.CONNECTION_ACCESS_REQUEST"))
    {
      Log.e("BluetoothPermissionActivity", "Error: this activity may be started only with intent ACTION_CONNECTION_ACCESS_REQUEST");
      finish();
      return;
    }
    this.mDevice = ((BluetoothDevice)localIntent.getParcelableExtra("android.bluetooth.device.extra.DEVICE"));
    this.mReturnPackage = localIntent.getStringExtra("android.bluetooth.device.extra.PACKAGE_NAME");
    this.mReturnClass = localIntent.getStringExtra("android.bluetooth.device.extra.CLASS_NAME");
    this.mRequestType = localIntent.getIntExtra("android.bluetooth.device.extra.ACCESS_REQUEST_TYPE", 2);
    Log.i("BluetoothPermissionActivity", "onCreate() Request type: " + this.mRequestType);
    if (this.mRequestType == 1)
      showDialog(getString(2131427468), this.mRequestType);
    while (true)
    {
      registerReceiver(this.mReceiver, new IntentFilter("android.bluetooth.device.action.CONNECTION_ACCESS_CANCEL"));
      this.mReceiverRegistered = true;
      return;
      if (this.mRequestType == 2)
      {
        showDialog(getString(2131427471), this.mRequestType);
      }
      else
      {
        if (this.mRequestType != 3)
          break;
        showDialog(getString(2131427475), this.mRequestType);
      }
    }
    Log.e("BluetoothPermissionActivity", "Error: bad request type: " + this.mRequestType);
    finish();
  }

  protected void onDestroy()
  {
    super.onDestroy();
    if (this.mReceiverRegistered)
    {
      unregisterReceiver(this.mReceiver);
      this.mReceiverRegistered = false;
    }
  }

  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    return true;
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.bluetooth.BluetoothPermissionActivity
 * JD-Core Version:    0.6.2
 */