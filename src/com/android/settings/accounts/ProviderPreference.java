package com.android.settings.accounts;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.preference.Preference;

public class ProviderPreference extends Preference
{
  private String mAccountType;

  public ProviderPreference(Context paramContext, String paramString, Drawable paramDrawable, CharSequence paramCharSequence)
  {
    super(paramContext);
    this.mAccountType = paramString;
    setIcon(paramDrawable);
    setPersistent(false);
    setTitle(paramCharSequence);
  }

  public String getAccountType()
  {
    return this.mAccountType;
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.accounts.ProviderPreference
 * JD-Core Version:    0.6.2
 */