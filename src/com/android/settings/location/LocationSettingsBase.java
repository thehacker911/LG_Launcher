package com.android.settings.location;

import android.app.Activity;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.UserManager;
import android.provider.Settings.Secure;
import android.util.Log;
import com.android.settings.SettingsPreferenceFragment;

public abstract class LocationSettingsBase extends SettingsPreferenceFragment
  implements LoaderManager.LoaderCallbacks<Cursor>
{
  private boolean mActive = false;
  private int mCurrentMode;

  private boolean isRestricted()
  {
    return ((UserManager)getActivity().getSystemService("user")).hasUserRestriction("no_share_location");
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    getLoaderManager().initLoader(1, null, this);
  }

  public Loader<Cursor> onCreateLoader(int paramInt, Bundle paramBundle)
  {
    switch (paramInt)
    {
    default:
      return null;
    case 1:
    }
    return new CursorLoader(getActivity(), Settings.Secure.CONTENT_URI, null, "(name=?)", new String[] { "location_mode" }, null);
  }

  public void onLoadFinished(Loader<Cursor> paramLoader, Cursor paramCursor)
  {
    refreshLocationMode();
  }

  public void onLoaderReset(Loader<Cursor> paramLoader)
  {
  }

  public abstract void onModeChanged(int paramInt, boolean paramBoolean);

  public void onPause()
  {
    super.onPause();
    this.mActive = false;
  }

  public void onResume()
  {
    super.onResume();
    this.mActive = true;
  }

  public void refreshLocationMode()
  {
    if (this.mActive)
    {
      int i = Settings.Secure.getInt(getContentResolver(), "location_mode", 0);
      this.mCurrentMode = i;
      onModeChanged(i, isRestricted());
    }
  }

  public void setLocationMode(int paramInt)
  {
    if (isRestricted())
    {
      if (Log.isLoggable("LocationSettingsBase", 4))
        Log.i("LocationSettingsBase", "Restricted user, not setting location mode");
      int i = Settings.Secure.getInt(getContentResolver(), "location_mode", 0);
      if (this.mActive)
        onModeChanged(i, true);
      return;
    }
    Intent localIntent = new Intent("com.android.settings.location.MODE_CHANGING");
    localIntent.putExtra("CURRENT_MODE", this.mCurrentMode);
    localIntent.putExtra("NEW_MODE", paramInt);
    getActivity().sendBroadcast(localIntent, "android.permission.WRITE_SECURE_SETTINGS");
    Settings.Secure.putInt(getContentResolver(), "location_mode", paramInt);
    refreshLocationMode();
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.location.LocationSettingsBase
 * JD-Core Version:    0.6.2
 */