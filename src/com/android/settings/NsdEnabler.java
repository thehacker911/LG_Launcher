package com.android.settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.nsd.NsdManager;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;

public class NsdEnabler
  implements Preference.OnPreferenceChangeListener
{
  private final CheckBoxPreference mCheckbox;
  private final Context mContext;
  private final IntentFilter mIntentFilter;
  private NsdManager mNsdManager;
  private final BroadcastReceiver mReceiver;

  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    boolean bool = ((Boolean)paramObject).booleanValue();
    this.mCheckbox.setEnabled(false);
    this.mNsdManager.setEnabled(bool);
    return false;
  }

  public void pause()
  {
    this.mContext.unregisterReceiver(this.mReceiver);
    this.mCheckbox.setOnPreferenceChangeListener(null);
  }

  public void resume()
  {
    this.mContext.registerReceiver(this.mReceiver, this.mIntentFilter);
    this.mCheckbox.setOnPreferenceChangeListener(this);
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.NsdEnabler
 * JD-Core Version:    0.6.2
 */