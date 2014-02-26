package com.android.settings.wifi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

class WifiDialog extends AlertDialog
  implements WifiConfigUiBase
{
  private final AccessPoint mAccessPoint;
  private WifiConfigController mController;
  private final boolean mEdit;
  private final DialogInterface.OnClickListener mListener;
  private View mView;

  public WifiDialog(Context paramContext, DialogInterface.OnClickListener paramOnClickListener, AccessPoint paramAccessPoint, boolean paramBoolean)
  {
    super(paramContext);
    this.mEdit = paramBoolean;
    this.mListener = paramOnClickListener;
    this.mAccessPoint = paramAccessPoint;
  }

  public WifiConfigController getController()
  {
    return this.mController;
  }

  public Button getSubmitButton()
  {
    return getButton(-1);
  }

  protected void onCreate(Bundle paramBundle)
  {
    this.mView = getLayoutInflater().inflate(2130968736, null);
    setView(this.mView);
    setInverseBackgroundForced(true);
    this.mController = new WifiConfigController(this, this.mView, this.mAccessPoint, this.mEdit);
    super.onCreate(paramBundle);
    this.mController.enableSubmitIfAppropriate();
  }

  public void setCancelButton(CharSequence paramCharSequence)
  {
    setButton(-2, paramCharSequence, this.mListener);
  }

  public void setForgetButton(CharSequence paramCharSequence)
  {
    setButton(-3, paramCharSequence, this.mListener);
  }

  public void setSubmitButton(CharSequence paramCharSequence)
  {
    setButton(-1, paramCharSequence, this.mListener);
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.wifi.WifiDialog
 * JD-Core Version:    0.6.2
 */