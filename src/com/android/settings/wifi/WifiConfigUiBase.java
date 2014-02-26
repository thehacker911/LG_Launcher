package com.android.settings.wifi;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Button;

public abstract interface WifiConfigUiBase
{
  public abstract Context getContext();

  public abstract LayoutInflater getLayoutInflater();

  public abstract Button getSubmitButton();

  public abstract void setCancelButton(CharSequence paramCharSequence);

  public abstract void setForgetButton(CharSequence paramCharSequence);

  public abstract void setSubmitButton(CharSequence paramCharSequence);

  public abstract void setTitle(int paramInt);

  public abstract void setTitle(CharSequence paramCharSequence);
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.wifi.WifiConfigUiBase
 * JD-Core Version:    0.6.2
 */