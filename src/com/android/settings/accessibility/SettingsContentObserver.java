package com.android.settings.accessibility;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings.Secure;

abstract class SettingsContentObserver extends ContentObserver
{
  public SettingsContentObserver(Handler paramHandler)
  {
    super(paramHandler);
  }

  public void register(ContentResolver paramContentResolver)
  {
    paramContentResolver.registerContentObserver(Settings.Secure.getUriFor("accessibility_enabled"), false, this);
    paramContentResolver.registerContentObserver(Settings.Secure.getUriFor("enabled_accessibility_services"), false, this);
  }

  public void unregister(ContentResolver paramContentResolver)
  {
    paramContentResolver.unregisterContentObserver(this);
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.accessibility.SettingsContentObserver
 * JD-Core Version:    0.6.2
 */