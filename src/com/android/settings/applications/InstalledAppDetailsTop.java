package com.android.settings.applications;

import android.app.Activity;
import android.content.Intent;
import android.preference.PreferenceActivity;

public class InstalledAppDetailsTop extends PreferenceActivity
{
  public Intent getIntent()
  {
    Intent localIntent = new Intent(super.getIntent());
    localIntent.putExtra(":android:show_fragment", InstalledAppDetails.class.getName());
    localIntent.putExtra(":android:no_headers", true);
    return localIntent;
  }

  protected boolean isValidFragment(String paramString)
  {
    return InstalledAppDetails.class.getName().equals(paramString);
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.applications.InstalledAppDetailsTop
 * JD-Core Version:    0.6.2
 */