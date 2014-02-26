package com.android.settings;

import android.util.Log;

public class SubSettings extends Settings
{
  protected boolean isValidFragment(String paramString)
  {
    Log.d("SubSettings", "Launching fragment " + paramString);
    return true;
  }

  public boolean onNavigateUp()
  {
    finish();
    return true;
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.SubSettings
 * JD-Core Version:    0.6.2
 */