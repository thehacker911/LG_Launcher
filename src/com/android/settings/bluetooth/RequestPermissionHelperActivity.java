package com.android.settings.bluetooth;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import com.android.internal.app.AlertActivity;
import com.android.internal.app.AlertController.AlertParams;

public class RequestPermissionHelperActivity extends AlertActivity
  implements DialogInterface.OnClickListener
{
  private boolean mEnableOnly;
  private LocalBluetoothAdapter mLocalAdapter;
  private int mTimeout;

  private boolean parseIntent()
  {
    Intent localIntent = getIntent();
    if ((localIntent != null) && (localIntent.getAction().equals("com.android.settings.bluetooth.ACTION_INTERNAL_REQUEST_BT_ON")))
      this.mEnableOnly = true;
    LocalBluetoothManager localLocalBluetoothManager;
    while (true)
    {
      localLocalBluetoothManager = LocalBluetoothManager.getInstance(this);
      if (localLocalBluetoothManager != null)
        break label93;
      Log.e("RequestPermissionHelperActivity", "Error: there's a problem starting Bluetooth");
      setResult(0);
      return true;
      if ((localIntent == null) || (!localIntent.getAction().equals("com.android.settings.bluetooth.ACTION_INTERNAL_REQUEST_BT_ON_AND_DISCOVERABLE")))
        break;
      this.mEnableOnly = false;
      this.mTimeout = localIntent.getIntExtra("android.bluetooth.adapter.extra.DISCOVERABLE_DURATION", 120);
    }
    setResult(0);
    return true;
    label93: this.mLocalAdapter = localLocalBluetoothManager.getBluetoothAdapter();
    return false;
  }

  void createDialog()
  {
    AlertController.AlertParams localAlertParams = this.mAlertParams;
    if (this.mEnableOnly)
      localAlertParams.mMessage = getString(2131427460);
    while (true)
    {
      localAlertParams.mPositiveButtonText = getString(2131427337);
      localAlertParams.mPositiveButtonListener = this;
      localAlertParams.mNegativeButtonText = getString(2131427338);
      localAlertParams.mNegativeButtonListener = this;
      setupAlert();
      return;
      if (this.mTimeout == 0)
      {
        localAlertParams.mMessage = getString(2131427464);
      }
      else
      {
        Object[] arrayOfObject = new Object[1];
        arrayOfObject[0] = Integer.valueOf(this.mTimeout);
        localAlertParams.mMessage = getString(2131427463, arrayOfObject);
      }
    }
  }

  public void onBackPressed()
  {
    setResult(0);
    super.onBackPressed();
  }

  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    int j;
    int k;
    switch (paramInt)
    {
    default:
      return;
    case -1:
      j = 0;
      k = 30;
    case -2:
    }
    try
    {
      do
      {
        j = this.mLocalAdapter.getBluetoothState();
        Thread.sleep(100L);
        if (j != 13)
          break;
        k--;
      }
      while (k > 0);
      label62: int i;
      if ((j == 11) || (j == 12) || (this.mLocalAdapter.enable()))
        i = -1000;
      while (true)
      {
        setResult(i);
        return;
        i = 0;
        continue;
        i = 0;
      }
    }
    catch (InterruptedException localInterruptedException)
    {
      break label62;
    }
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    if (parseIntent())
      finish();
    do
    {
      return;
      createDialog();
    }
    while (getResources().getBoolean(2131296260) != true);
    onClick(null, -1);
    dismiss();
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.bluetooth.RequestPermissionHelperActivity
 * JD-Core Version:    0.6.2
 */