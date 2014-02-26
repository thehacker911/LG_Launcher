package com.android.settings;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import com.android.internal.app.AlertActivity;
import com.android.internal.app.AlertController.AlertParams;

public class AllowBindAppWidgetActivity extends AlertActivity
  implements DialogInterface.OnClickListener
{
  private CheckBox mAlwaysUse;
  private int mAppWidgetId;
  private AppWidgetManager mAppWidgetManager;
  private String mCallingPackage;
  private boolean mClicked;
  private ComponentName mComponentName;

  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    if (paramInt == -1)
    {
      setResult(0);
      if ((this.mAppWidgetId == -1) || (this.mComponentName == null) || (this.mCallingPackage == null));
    }
    try
    {
      this.mAppWidgetManager.bindAppWidgetId(this.mAppWidgetId, this.mComponentName);
      Intent localIntent = new Intent();
      localIntent.putExtra("appWidgetId", this.mAppWidgetId);
      setResult(-1, localIntent);
      boolean bool = this.mAlwaysUse.isChecked();
      if (bool != this.mAppWidgetManager.hasBindAppWidgetPermission(this.mCallingPackage))
        this.mAppWidgetManager.setBindAppWidgetPermission(this.mCallingPackage, bool);
      finish();
      return;
    }
    catch (Exception localException)
    {
      while (true)
        Log.v("BIND_APPWIDGET", "Error binding widget with id " + this.mAppWidgetId + " and component " + this.mComponentName);
    }
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    Intent localIntent = getIntent();
    Object localObject = "";
    if (localIntent != null);
    try
    {
      this.mAppWidgetId = localIntent.getIntExtra("appWidgetId", -1);
      this.mComponentName = ((ComponentName)localIntent.getParcelableExtra("appWidgetProvider"));
      this.mCallingPackage = getCallingPackage();
      PackageManager localPackageManager = getPackageManager();
      CharSequence localCharSequence = localPackageManager.getApplicationLabel(localPackageManager.getApplicationInfo(this.mCallingPackage, 0));
      localObject = localCharSequence;
      AlertController.AlertParams localAlertParams = this.mAlertParams;
      localAlertParams.mTitle = getString(2131428613);
      localAlertParams.mMessage = getString(2131428614, new Object[] { localObject });
      localAlertParams.mPositiveButtonText = getString(2131427336);
      localAlertParams.mNegativeButtonText = getString(17039360);
      localAlertParams.mPositiveButtonListener = this;
      localAlertParams.mNegativeButtonListener = this;
      localAlertParams.mView = ((LayoutInflater)getSystemService("layout_inflater")).inflate(17367080, null);
      this.mAlwaysUse = ((CheckBox)localAlertParams.mView.findViewById(16908923));
      this.mAlwaysUse.setText(getString(2131428615, new Object[] { localObject }));
      this.mAlwaysUse.setPadding(this.mAlwaysUse.getPaddingLeft(), this.mAlwaysUse.getPaddingTop(), this.mAlwaysUse.getPaddingRight(), (int)(this.mAlwaysUse.getPaddingBottom() + getResources().getDimension(2131558410)));
      this.mAppWidgetManager = AppWidgetManager.getInstance(this);
      this.mAlwaysUse.setChecked(this.mAppWidgetManager.hasBindAppWidgetPermission(this.mCallingPackage));
      setupAlert();
      return;
    }
    catch (Exception localException)
    {
      this.mAppWidgetId = -1;
      this.mComponentName = null;
      this.mCallingPackage = null;
      Log.v("BIND_APPWIDGET", "Error getting parameters");
      setResult(0);
      finish();
    }
  }

  protected void onDestroy()
  {
    if (!this.mClicked)
    {
      setResult(0);
      finish();
    }
    super.onDestroy();
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.AllowBindAppWidgetActivity
 * JD-Core Version:    0.6.2
 */