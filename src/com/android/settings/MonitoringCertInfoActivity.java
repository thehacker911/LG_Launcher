package com.android.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.IWindowManager;
import android.view.Window;
import android.view.WindowManagerGlobal;

public class MonitoringCertInfoActivity extends Activity
  implements DialogInterface.OnClickListener
{
  private boolean hasDeviceOwner = false;

  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    if (this.hasDeviceOwner)
    {
      finish();
      return;
    }
    Intent localIntent = new Intent("com.android.settings.TRUSTED_CREDENTIALS_USER");
    localIntent.setFlags(335544320);
    startActivity(localIntent);
    finish();
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    DevicePolicyManager localDevicePolicyManager = (DevicePolicyManager)getSystemService("device_policy");
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);
    localBuilder.setTitle(2131429192);
    localBuilder.setCancelable(true);
    boolean bool;
    if (localDevicePolicyManager.getDeviceOwner() != null)
      bool = true;
    while (true)
    {
      this.hasDeviceOwner = bool;
      int i;
      AlertDialog localAlertDialog;
      if (this.hasDeviceOwner)
      {
        Resources localResources = getResources();
        Object[] arrayOfObject = new Object[1];
        arrayOfObject[0] = localDevicePolicyManager.getDeviceOwnerName();
        localBuilder.setMessage(localResources.getString(2131429193, arrayOfObject));
        i = 2131429191;
        localBuilder.setPositiveButton(i, this);
        localAlertDialog = localBuilder.create();
        localAlertDialog.getWindow().setType(2003);
      }
      try
      {
        WindowManagerGlobal.getWindowManagerService().dismissKeyguard();
        label131: localAlertDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
          public void onCancel(DialogInterface paramAnonymousDialogInterface)
          {
            MonitoringCertInfoActivity.this.finish();
          }
        });
        localAlertDialog.show();
        return;
        bool = false;
        continue;
        localBuilder.setIcon(17301624);
        localBuilder.setMessage(2131429194);
        i = 2131429195;
      }
      catch (RemoteException localRemoteException)
      {
        break label131;
      }
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.MonitoringCertInfoActivity
 * JD-Core Version:    0.6.2
 */