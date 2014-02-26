package com.android.settings.bluetooth;

import android.content.Context;
import android.util.Log;

public final class LocalBluetoothManager
{
  private static LocalBluetoothManager sInstance;
  private final CachedBluetoothDeviceManager mCachedDeviceManager;
  private final Context mContext;
  private BluetoothDiscoverableEnabler mDiscoverableEnabler;
  private final BluetoothEventManager mEventManager;
  private Context mForegroundActivity;
  private final LocalBluetoothAdapter mLocalAdapter;
  private final LocalBluetoothProfileManager mProfileManager;

  private LocalBluetoothManager(LocalBluetoothAdapter paramLocalBluetoothAdapter, Context paramContext)
  {
    this.mContext = paramContext;
    this.mLocalAdapter = paramLocalBluetoothAdapter;
    this.mCachedDeviceManager = new CachedBluetoothDeviceManager(paramContext);
    this.mEventManager = new BluetoothEventManager(this.mLocalAdapter, this.mCachedDeviceManager, paramContext);
    this.mProfileManager = new LocalBluetoothProfileManager(paramContext, this.mLocalAdapter, this.mCachedDeviceManager, this.mEventManager);
  }

  public static LocalBluetoothManager getInstance(Context paramContext)
  {
    try
    {
      LocalBluetoothAdapter localLocalBluetoothAdapter;
      if (sInstance == null)
      {
        localLocalBluetoothAdapter = LocalBluetoothAdapter.getInstance();
        if (localLocalBluetoothAdapter != null);
      }
      for (LocalBluetoothManager localLocalBluetoothManager = null; ; localLocalBluetoothManager = sInstance)
      {
        return localLocalBluetoothManager;
        sInstance = new LocalBluetoothManager(localLocalBluetoothAdapter, paramContext.getApplicationContext());
      }
    }
    finally
    {
    }
  }

  public LocalBluetoothAdapter getBluetoothAdapter()
  {
    return this.mLocalAdapter;
  }

  CachedBluetoothDeviceManager getCachedDeviceManager()
  {
    return this.mCachedDeviceManager;
  }

  public BluetoothDiscoverableEnabler getDiscoverableEnabler()
  {
    return this.mDiscoverableEnabler;
  }

  BluetoothEventManager getEventManager()
  {
    return this.mEventManager;
  }

  public Context getForegroundActivity()
  {
    return this.mForegroundActivity;
  }

  LocalBluetoothProfileManager getProfileManager()
  {
    return this.mProfileManager;
  }

  boolean isForegroundActivity()
  {
    return this.mForegroundActivity != null;
  }

  public void setDiscoverableEnabler(BluetoothDiscoverableEnabler paramBluetoothDiscoverableEnabler)
  {
    this.mDiscoverableEnabler = paramBluetoothDiscoverableEnabler;
  }

  void setForegroundActivity(Context paramContext)
  {
    if (paramContext != null);
    try
    {
      Log.d("LocalBluetoothManager", "setting foreground activity to non-null context");
      this.mForegroundActivity = paramContext;
      while (true)
      {
        return;
        if (this.mForegroundActivity != null)
        {
          Log.d("LocalBluetoothManager", "setting foreground activity to null");
          this.mForegroundActivity = null;
        }
      }
    }
    finally
    {
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.bluetooth.LocalBluetoothManager
 * JD-Core Version:    0.6.2
 */