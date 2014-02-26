package com.android.settings.nfc;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.android.internal.app.AlertActivity;
import com.android.internal.app.AlertController.AlertParams;
import java.util.Iterator;
import java.util.List;

public final class PaymentDefaultDialog extends AlertActivity
  implements DialogInterface.OnClickListener
{
  private PaymentBackend mBackend;
  private ComponentName mNewDefault;

  private boolean buildDialog(ComponentName paramComponentName, String paramString)
  {
    if ((paramComponentName == null) || (paramString == null))
    {
      Log.e("PaymentDefaultDialog", "Component or category are null");
      return false;
    }
    if (!"payment".equals(paramString))
    {
      Log.e("PaymentDefaultDialog", "Don't support defaults for category " + paramString);
      return false;
    }
    Object localObject1 = null;
    Object localObject2 = null;
    Iterator localIterator = this.mBackend.getPaymentAppInfos().iterator();
    while (localIterator.hasNext())
    {
      PaymentBackend.PaymentAppInfo localPaymentAppInfo = (PaymentBackend.PaymentAppInfo)localIterator.next();
      if (paramComponentName.equals(localPaymentAppInfo.componentName))
        localObject1 = localPaymentAppInfo;
      if (localPaymentAppInfo.isDefault)
        localObject2 = localPaymentAppInfo;
    }
    if (localObject1 == null)
    {
      Log.e("PaymentDefaultDialog", "Component " + paramComponentName + " is not a registered payment service.");
      return false;
    }
    ComponentName localComponentName = this.mBackend.getDefaultPaymentApp();
    if ((localComponentName != null) && (localComponentName.equals(paramComponentName)))
    {
      Log.e("PaymentDefaultDialog", "Component " + paramComponentName + " is already default.");
      return false;
    }
    this.mNewDefault = paramComponentName;
    AlertController.AlertParams localAlertParams = this.mAlertParams;
    localAlertParams.mTitle = getString(2131429246);
    String str2;
    Object[] arrayOfObject2;
    if (localObject2 == null)
    {
      str2 = getString(2131429247);
      arrayOfObject2 = new Object[1];
      arrayOfObject2[0] = localObject1.caption;
    }
    String str1;
    Object[] arrayOfObject1;
    for (localAlertParams.mMessage = String.format(str2, arrayOfObject2); ; localAlertParams.mMessage = String.format(str1, arrayOfObject1))
    {
      localAlertParams.mPositiveButtonText = getString(2131427334);
      localAlertParams.mNegativeButtonText = getString(2131427335);
      localAlertParams.mPositiveButtonListener = this;
      localAlertParams.mNegativeButtonListener = this;
      setupAlert();
      return true;
      str1 = getString(2131429248);
      arrayOfObject1 = new Object[2];
      arrayOfObject1[0] = localObject1.caption;
      arrayOfObject1[1] = localObject2.caption;
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
    this.mBackend.setDefaultPaymentApp(this.mNewDefault);
    setResult(-1);
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mBackend = new PaymentBackend(this);
    Intent localIntent = getIntent();
    ComponentName localComponentName = (ComponentName)localIntent.getParcelableExtra("component");
    String str = localIntent.getStringExtra("category");
    setResult(0);
    if (!buildDialog(localComponentName, str))
      finish();
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.nfc.PaymentDefaultDialog
 * JD-Core Version:    0.6.2
 */