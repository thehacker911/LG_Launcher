package com.android.settings;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import com.android.internal.app.AlertActivity;
import com.android.internal.app.AlertController.AlertParams;
import com.android.internal.telephony.SmsApplication;
import com.android.internal.telephony.SmsApplication.SmsApplicationData;

public final class SmsDefaultDialog extends AlertActivity
  implements DialogInterface.OnClickListener
{
  private SmsApplication.SmsApplicationData mNewSmsApplicationData;

  private boolean buildDialog(String paramString)
  {
    if (((TelephonyManager)getSystemService("phone")).getPhoneType() == 0);
    SmsApplication.SmsApplicationData localSmsApplicationData;
    do
    {
      do
      {
        return false;
        this.mNewSmsApplicationData = SmsApplication.getSmsApplicationData(paramString, this);
      }
      while (this.mNewSmsApplicationData == null);
      ComponentName localComponentName = SmsApplication.getDefaultSmsApplication(this, true);
      localSmsApplicationData = null;
      if (localComponentName == null)
        break;
      localSmsApplicationData = SmsApplication.getSmsApplicationData(localComponentName.getPackageName(), this);
    }
    while (localSmsApplicationData.mPackageName.equals(this.mNewSmsApplicationData.mPackageName));
    AlertController.AlertParams localAlertParams = this.mAlertParams;
    localAlertParams.mTitle = getString(2131428260);
    Object[] arrayOfObject2;
    if (localSmsApplicationData != null)
    {
      arrayOfObject2 = new Object[2];
      arrayOfObject2[0] = this.mNewSmsApplicationData.mApplicationName;
      arrayOfObject2[1] = localSmsApplicationData.mApplicationName;
    }
    Object[] arrayOfObject1;
    for (localAlertParams.mMessage = getString(2131428261, arrayOfObject2); ; localAlertParams.mMessage = getString(2131428262, arrayOfObject1))
    {
      localAlertParams.mPositiveButtonText = getString(2131427334);
      localAlertParams.mNegativeButtonText = getString(2131427335);
      localAlertParams.mPositiveButtonListener = this;
      localAlertParams.mNegativeButtonListener = this;
      setupAlert();
      return true;
      arrayOfObject1 = new Object[1];
      arrayOfObject1[0] = this.mNewSmsApplicationData.mApplicationName;
    }
  }

  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    switch (paramInt)
    {
    default:
      return;
    case -1:
    }
    SmsApplication.setDefaultApplication(this.mNewSmsApplicationData.mPackageName, this);
    setResult(-1);
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    String str = getIntent().getStringExtra("package");
    setResult(0);
    if (!buildDialog(str))
      finish();
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.SmsDefaultDialog
 * JD-Core Version:    0.6.2
 */