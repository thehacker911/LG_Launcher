package com.android.settings.inputmethod;

import android.app.Activity;
import android.content.Intent;
import android.preference.PreferenceActivity;

public class InputMethodAndSubtypeEnablerActivity extends PreferenceActivity
{
  public Intent getIntent()
  {
    Intent localIntent = new Intent(super.getIntent());
    if (!localIntent.hasExtra(":android:show_fragment"))
    {
      localIntent.putExtra(":android:show_fragment", InputMethodAndSubtypeEnabler.class.getName());
      localIntent.putExtra(":android:no_headers", true);
    }
    return localIntent;
  }

  protected boolean isValidFragment(String paramString)
  {
    return InputMethodAndSubtypeEnabler.class.getName().equals(paramString);
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.inputmethod.InputMethodAndSubtypeEnablerActivity
 * JD-Core Version:    0.6.2
 */