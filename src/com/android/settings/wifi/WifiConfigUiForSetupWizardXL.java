package com.android.settings.wifi;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

public class WifiConfigUiForSetupWizardXL
  implements View.OnFocusChangeListener, WifiConfigUiBase
{
  private final WifiSettingsForSetupWizardXL mActivity;
  private Button mCancelButton;
  private Button mConnectButton;
  private WifiConfigController mController;
  private Handler mHandler;
  private LayoutInflater mInflater;
  private final InputMethodManager mInputMethodManager;
  private View mView;

  public Context getContext()
  {
    return this.mActivity;
  }

  public WifiConfigController getController()
  {
    return this.mController;
  }

  public LayoutInflater getLayoutInflater()
  {
    return this.mInflater;
  }

  public Button getSubmitButton()
  {
    return this.mConnectButton;
  }

  public void onFocusChange(View paramView, boolean paramBoolean)
  {
    paramView.setOnFocusChangeListener(null);
    if (paramBoolean)
      this.mHandler.post(new FocusRunnable(paramView));
  }

  public void requestFocusAndShowKeyboard(int paramInt)
  {
    View localView = this.mView.findViewById(paramInt);
    if (localView == null)
      Log.w("SetupWizard", "password field to be focused not found.");
    do
    {
      return;
      if (!(localView instanceof EditText))
      {
        Log.w("SetupWizard", "password field is not EditText");
        return;
      }
      if (!localView.isFocused())
        break;
      Log.i("SetupWizard", "Already focused");
    }
    while (this.mInputMethodManager.showSoftInput(localView, 0));
    Log.w("SetupWizard", "Failed to show SoftInput");
    return;
    localView.setOnFocusChangeListener(this);
    boolean bool = localView.requestFocus();
    Object[] arrayOfObject = new Object[1];
    if (bool);
    for (String str = "successful"; ; str = "failed")
    {
      arrayOfObject[0] = str;
      Log.i("SetupWizard", String.format("Focus request: %s", arrayOfObject));
      if (bool)
        break;
      localView.setOnFocusChangeListener(null);
      return;
    }
  }

  public void setCancelButton(CharSequence paramCharSequence)
  {
    this.mCancelButton.setVisibility(0);
  }

  public void setForgetButton(CharSequence paramCharSequence)
  {
  }

  public void setSubmitButton(CharSequence paramCharSequence)
  {
    this.mConnectButton.setVisibility(0);
    this.mConnectButton.setText(paramCharSequence);
  }

  public void setTitle(int paramInt)
  {
    Log.d("SetupWizard", "Ignoring setTitle");
  }

  public void setTitle(CharSequence paramCharSequence)
  {
    Log.d("SetupWizard", "Ignoring setTitle");
  }

  private class FocusRunnable
    implements Runnable
  {
    final View mViewToBeFocused;

    public FocusRunnable(View arg2)
    {
      Object localObject;
      this.mViewToBeFocused = localObject;
    }

    public void run()
    {
      if (WifiConfigUiForSetupWizardXL.this.mInputMethodManager.showSoftInput(this.mViewToBeFocused, 0))
      {
        WifiConfigUiForSetupWizardXL.this.mActivity.setPaddingVisibility(8);
        return;
      }
      Log.w("SetupWizard", "Failed to show software keyboard ");
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.wifi.WifiConfigUiForSetupWizardXL
 * JD-Core Version:    0.6.2
 */