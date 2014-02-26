package com.android.settings.wifi;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.provider.Settings.Global;

public class WifiScanModeActivity extends Activity
{
  private String mApp;
  private DialogFragment mDialog;

  private void createDialog()
  {
    if (this.mDialog == null)
    {
      this.mDialog = AlertDialogFragment.newInstance(this.mApp);
      this.mDialog.show(getFragmentManager(), "dialog");
    }
  }

  private void dismissDialog()
  {
    if (this.mDialog != null)
    {
      this.mDialog.dismiss();
      this.mDialog = null;
    }
  }

  private void doNegativeClick()
  {
    setResult(0);
    finish();
  }

  private void doPositiveClick()
  {
    Settings.Global.putInt(getContentResolver(), "wifi_scan_always_enabled", 1);
    setResult(-1);
    finish();
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    Intent localIntent = getIntent();
    if (paramBundle == null)
      if ((localIntent != null) && (localIntent.getAction().equals("android.net.wifi.action.REQUEST_SCAN_ALWAYS_AVAILABLE")))
        this.mApp = getCallingPackage();
    try
    {
      PackageManager localPackageManager = getPackageManager();
      for (this.mApp = ((String)localPackageManager.getApplicationLabel(localPackageManager.getApplicationInfo(this.mApp, 0))); ; this.mApp = paramBundle.getString("app"))
      {
        label66: createDialog();
        return;
        finish();
        return;
      }
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      break label66;
    }
  }

  public void onPause()
  {
    super.onPause();
    dismissDialog();
  }

  public void onResume()
  {
    super.onResume();
    createDialog();
  }

  public void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    paramBundle.putString("app", this.mApp);
  }

  public static class AlertDialogFragment extends DialogFragment
  {
    private final String mApp;

    public AlertDialogFragment()
    {
      this.mApp = null;
    }

    public AlertDialogFragment(String paramString)
    {
      this.mApp = paramString;
    }

    static AlertDialogFragment newInstance(String paramString)
    {
      return new AlertDialogFragment(paramString);
    }

    public void onCancel(DialogInterface paramDialogInterface)
    {
      ((WifiScanModeActivity)getActivity()).doNegativeClick();
    }

    public Dialog onCreateDialog(Bundle paramBundle)
    {
      AlertDialog.Builder localBuilder = new AlertDialog.Builder(getActivity());
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = this.mApp;
      return localBuilder.setMessage(getString(2131427906, arrayOfObject)).setPositiveButton(2131427907, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          ((WifiScanModeActivity)WifiScanModeActivity.AlertDialogFragment.this.getActivity()).doPositiveClick();
        }
      }).setNegativeButton(2131427908, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          ((WifiScanModeActivity)WifiScanModeActivity.AlertDialogFragment.this.getActivity()).doNegativeClick();
        }
      }).create();
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.wifi.WifiScanModeActivity
 * JD-Core Version:    0.6.2
 */