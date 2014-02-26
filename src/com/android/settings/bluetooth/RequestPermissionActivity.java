package com.android.settings.bluetooth;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;

public class RequestPermissionActivity extends Activity
  implements DialogInterface.OnClickListener
{
  private AlertDialog mDialog;
  private boolean mEnableOnly;
  private LocalBluetoothAdapter mLocalAdapter;
  private boolean mNeededToEnableBluetooth;
  private final BroadcastReceiver mReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if (paramAnonymousIntent == null);
      while ((!RequestPermissionActivity.this.mNeededToEnableBluetooth) || (!"android.bluetooth.adapter.action.STATE_CHANGED".equals(paramAnonymousIntent.getAction())) || (paramAnonymousIntent.getIntExtra("android.bluetooth.adapter.extra.STATE", -2147483648) != 12) || (!RequestPermissionActivity.this.mUserConfirmed))
        return;
      RequestPermissionActivity.this.proceedAndFinish();
    }
  };
  private int mTimeout = 120;
  private boolean mUserConfirmed;

  private void createDialog()
  {
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
    if (this.mNeededToEnableBluetooth)
    {
      localBuilder.setMessage(getString(2131427465));
      localBuilder.setCancelable(false);
      this.mDialog = localBuilder.create();
      this.mDialog.show();
      if (getResources().getBoolean(2131296260) == true)
        onClick(null, -1);
      return;
    }
    if (this.mTimeout == 0)
      localBuilder.setMessage(getString(2131427462));
    while (true)
    {
      localBuilder.setPositiveButton(getString(2131427337), this);
      localBuilder.setNegativeButton(getString(2131427338), this);
      break;
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = Integer.valueOf(this.mTimeout);
      localBuilder.setMessage(getString(2131427461, arrayOfObject));
    }
  }

  private boolean parseIntent()
  {
    Intent localIntent = getIntent();
    if ((localIntent != null) && (localIntent.getAction().equals("android.bluetooth.adapter.action.REQUEST_ENABLE")))
      this.mEnableOnly = true;
    LocalBluetoothManager localLocalBluetoothManager;
    while (true)
    {
      localLocalBluetoothManager = LocalBluetoothManager.getInstance(this);
      if (localLocalBluetoothManager != null)
        break label149;
      Log.e("RequestPermissionActivity", "Error: there's a problem starting Bluetooth");
      setResult(0);
      return true;
      if ((localIntent == null) || (!localIntent.getAction().equals("android.bluetooth.adapter.action.REQUEST_DISCOVERABLE")))
        break;
      this.mTimeout = localIntent.getIntExtra("android.bluetooth.adapter.extra.DISCOVERABLE_DURATION", 120);
      Log.d("RequestPermissionActivity", "Setting Bluetooth Discoverable Timeout = " + this.mTimeout);
      if ((this.mTimeout < 0) || (this.mTimeout > 3600))
        this.mTimeout = 120;
    }
    Log.e("RequestPermissionActivity", "Error: this activity may be started only with intent android.bluetooth.adapter.action.REQUEST_ENABLE or android.bluetooth.adapter.action.REQUEST_DISCOVERABLE");
    setResult(0);
    return true;
    label149: this.mLocalAdapter = localLocalBluetoothManager.getBluetoothAdapter();
    return false;
  }

  private void proceedAndFinish()
  {
    int i;
    if (this.mEnableOnly)
      i = -1;
    while (true)
    {
      if (this.mDialog != null)
        this.mDialog.dismiss();
      setResult(i);
      finish();
      return;
      if (this.mLocalAdapter.setScanMode(23, this.mTimeout))
      {
        long l = System.currentTimeMillis() + 1000L * this.mTimeout;
        LocalBluetoothPreferences.persistDiscoverableEndTimestamp(this, l);
        if (this.mTimeout > 0)
          BluetoothDiscoverableTimeoutReceiver.setDiscoverableAlarm(this, l);
        i = this.mTimeout;
        if (i < 1)
          i = 1;
      }
      else
      {
        i = 0;
      }
    }
  }

  protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    if (paramInt1 != 1)
    {
      Log.e("RequestPermissionActivity", "Unexpected onActivityResult " + paramInt1 + ' ' + paramInt2);
      setResult(0);
      finish();
      return;
    }
    if (paramInt2 != -1000)
    {
      setResult(paramInt2);
      finish();
      return;
    }
    this.mUserConfirmed = true;
    if (this.mLocalAdapter.getBluetoothState() == 12)
    {
      proceedAndFinish();
      return;
    }
    createDialog();
  }

  public void onBackPressed()
  {
    setResult(0);
    super.onBackPressed();
  }

  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    switch (paramInt)
    {
    default:
      return;
    case -1:
      proceedAndFinish();
      return;
    case -2:
    }
    setResult(0);
    finish();
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    if (parseIntent())
    {
      finish();
      return;
    }
    int i = this.mLocalAdapter.getState();
    switch (i)
    {
    default:
      Log.e("RequestPermissionActivity", "Unknown adapter state: " + i);
      return;
    case 10:
    case 11:
    case 13:
      registerReceiver(this.mReceiver, new IntentFilter("android.bluetooth.adapter.action.STATE_CHANGED"));
      Intent localIntent = new Intent();
      localIntent.setClass(this, RequestPermissionHelperActivity.class);
      if (this.mEnableOnly)
        localIntent.setAction("com.android.settings.bluetooth.ACTION_INTERNAL_REQUEST_BT_ON");
      while (true)
      {
        startActivityForResult(localIntent, 1);
        this.mNeededToEnableBluetooth = true;
        return;
        localIntent.setAction("com.android.settings.bluetooth.ACTION_INTERNAL_REQUEST_BT_ON_AND_DISCOVERABLE");
        localIntent.putExtra("android.bluetooth.adapter.extra.DISCOVERABLE_DURATION", this.mTimeout);
      }
    case 12:
    }
    if (this.mEnableOnly)
    {
      proceedAndFinish();
      return;
    }
    createDialog();
  }

  protected void onDestroy()
  {
    super.onDestroy();
    if (this.mNeededToEnableBluetooth)
      unregisterReceiver(this.mReceiver);
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.bluetooth.RequestPermissionActivity
 * JD-Core Version:    0.6.2
 */