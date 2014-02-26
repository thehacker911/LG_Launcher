package com.android.settings.accessibility;

import android.app.Fragment;
import android.content.ContentResolver;
import android.os.Bundle;
import android.provider.Settings.Secure;
import com.android.settings.SettingsPreferenceFragment;

public class ToggleScreenMagnificationPreferenceFragment extends ToggleFeaturePreferenceFragment
{
  protected void onInstallActionBarToggleSwitch()
  {
    super.onInstallActionBarToggleSwitch();
    this.mToggleSwitch.setOnBeforeCheckedChangeListener(new ToggleSwitch.OnBeforeCheckedChangeListener()
    {
      public boolean onBeforeCheckedChanged(ToggleSwitch paramAnonymousToggleSwitch, boolean paramAnonymousBoolean)
      {
        paramAnonymousToggleSwitch.setCheckedInternal(paramAnonymousBoolean);
        ToggleScreenMagnificationPreferenceFragment.this.getArguments().putBoolean("checked", paramAnonymousBoolean);
        ToggleScreenMagnificationPreferenceFragment.this.onPreferenceToggled(ToggleScreenMagnificationPreferenceFragment.this.mPreferenceKey, paramAnonymousBoolean);
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
      Settings.Secure.putInt(localContentResolver, "accessibility_display_magnification_enabled", i);
      return;
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.accessibility.ToggleScreenMagnificationPreferenceFragment
 * JD-Core Version:    0.6.2
 */