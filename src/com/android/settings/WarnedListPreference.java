package com.android.settings;

import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;

public class WarnedListPreference extends ListPreference
{
  public WarnedListPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }

  public void click()
  {
    super.onClick();
  }

  protected void onClick()
  {
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.WarnedListPreference
 * JD-Core Version:    0.6.2
 */