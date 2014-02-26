package com.android.settings;

import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;
import android.preference.Preference;
import android.util.AttributeSet;

public class BrightnessPreference extends Preference
{
  public BrightnessPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }

  protected void onClick()
  {
    Intent localIntent = new Intent("android.intent.action.SHOW_BRIGHTNESS_DIALOG");
    getContext().sendBroadcastAsUser(localIntent, UserHandle.CURRENT_OR_SELF);
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.BrightnessPreference
 * JD-Core Version:    0.6.2
 */