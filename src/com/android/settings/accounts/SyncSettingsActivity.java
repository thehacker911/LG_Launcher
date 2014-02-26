package com.android.settings.accounts;

import android.content.Intent;
import android.preference.PreferenceActivity;

public class SyncSettingsActivity extends PreferenceActivity
{
  public Intent getIntent()
  {
    Intent localIntent = new Intent(super.getIntent());
    localIntent.putExtra(":android:show_fragment", SyncSettings.class.getName());
    localIntent.putExtra(":android:no_headers", true);
    return localIntent;
  }

  protected boolean isValidFragment(String paramString)
  {
    return SyncSettings.class.getName().equals(paramString);
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.accounts.SyncSettingsActivity
 * JD-Core Version:    0.6.2
 */