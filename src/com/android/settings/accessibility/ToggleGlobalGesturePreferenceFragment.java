package com.android.settings.accessibility;

import android.content.ContentResolver;
import android.os.Bundle;
import android.provider.Settings.Global;

public class ToggleGlobalGesturePreferenceFragment extends ToggleFeaturePreferenceFragment
{
  protected void onInstallActionBarToggleSwitch()
  {
    super.onInstallActionBarToggleSwitch();
    this.mToggleSwitch.setOnBeforeCheckedChangeListener(new ToggleSwitch.OnBeforeCheckedChangeListener()
    {
      public boolean onBeforeCheckedChanged(ToggleSwitch paramAnonymousToggleSwitch, boolean paramAnonymousBoolean)
      {
        paramAnonymousToggleSwitch.setCheckedInternal(paramAnonymousBoolean);
        ToggleGlobalGesturePreferenceFragment.this.getArguments().putBoolean("checked", paramAnonymousBoolean);
        ToggleGlobalGesturePreferenceFragment.this.onPreferenceToggled(ToggleGlobalGesturePreferenceFragment.this.mPreferenceKey, paramAnonymousBoolean);
        return false;
      }
    });
  }

  protected void onPreferenceToggled(String paramString, boolean paramBoolean)
  {
    ContentResolver localContentResolver = getContentResolver();
    if (paramBoolean);
    for (int i = 1; ; i = 0)
    {
      Settings.Global.putInt(localContentResolver, "enable_accessibility_global_gesture_enabled", i);
      return;
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.accessibility.ToggleGlobalGesturePreferenceFragment
 * JD-Core Version:    0.6.2
 */