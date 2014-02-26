package com.android.settings.location;

import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.preference.TwoStatePreference;

public class LocationMode extends LocationSettingsBase
  implements RadioButtonPreference.OnClickListener
{
  private RadioButtonPreference mBatterySaving;
  private RadioButtonPreference mHighAccuracy;
  private RadioButtonPreference mSensorsOnly;

  private PreferenceScreen createPreferenceHierarchy()
  {
    PreferenceScreen localPreferenceScreen1 = getPreferenceScreen();
    if (localPreferenceScreen1 != null)
      localPreferenceScreen1.removeAll();
    addPreferencesFromResource(2131034135);
    PreferenceScreen localPreferenceScreen2 = getPreferenceScreen();
    this.mHighAccuracy = ((RadioButtonPreference)localPreferenceScreen2.findPreference("high_accuracy"));
    this.mBatterySaving = ((RadioButtonPreference)localPreferenceScreen2.findPreference("battery_saving"));
    this.mSensorsOnly = ((RadioButtonPreference)localPreferenceScreen2.findPreference("sensors_only"));
    this.mHighAccuracy.setOnClickListener(this);
    this.mBatterySaving.setOnClickListener(this);
    this.mSensorsOnly.setOnClickListener(this);
    refreshLocationMode();
    return localPreferenceScreen2;
  }

  private void updateRadioButtons(RadioButtonPreference paramRadioButtonPreference)
  {
    if (paramRadioButtonPreference == null)
    {
      this.mHighAccuracy.setChecked(false);
      this.mBatterySaving.setChecked(false);
      this.mSensorsOnly.setChecked(false);
    }
    do
    {
      return;
      if (paramRadioButtonPreference == this.mHighAccuracy)
      {
        this.mHighAccuracy.setChecked(true);
        this.mBatterySaving.setChecked(false);
        this.mSensorsOnly.setChecked(false);
        return;
      }
      if (paramRadioButtonPreference == this.mBatterySaving)
      {
        this.mHighAccuracy.setChecked(false);
        this.mBatterySaving.setChecked(true);
        this.mSensorsOnly.setChecked(false);
        return;
      }
    }
    while (paramRadioButtonPreference != this.mSensorsOnly);
    this.mHighAccuracy.setChecked(false);
    this.mBatterySaving.setChecked(false);
    this.mSensorsOnly.setChecked(true);
  }

  public int getHelpResource()
  {
    return 2131429267;
  }

  public void onModeChanged(int paramInt, boolean paramBoolean)
  {
    switch (paramInt)
    {
    default:
      if ((paramInt == 0) || (paramBoolean))
        break;
    case 0:
    case 1:
    case 2:
    case 3:
    }
    for (boolean bool = true; ; bool = false)
    {
      this.mHighAccuracy.setEnabled(bool);
      this.mBatterySaving.setEnabled(bool);
      this.mSensorsOnly.setEnabled(bool);
      return;
      updateRadioButtons(null);
      break;
      updateRadioButtons(this.mSensorsOnly);
      break;
      updateRadioButtons(this.mBatterySaving);
      break;
      updateRadioButtons(this.mHighAccuracy);
      break;
    }
  }

  public void onPause()
  {
    super.onPause();
  }

  public void onRadioButtonClicked(RadioButtonPreference paramRadioButtonPreference)
  {
    int i;
    if (paramRadioButtonPreference == this.mHighAccuracy)
      i = 3;
    while (true)
    {
      setLocationMode(i);
      return;
      if (paramRadioButtonPreference == this.mBatterySaving)
      {
        i = 2;
      }
      else
      {
        RadioButtonPreference localRadioButtonPreference = this.mSensorsOnly;
        i = 0;
        if (paramRadioButtonPreference == localRadioButtonPreference)
          i = 1;
      }
    }
  }

  public void onResume()
  {
    super.onResume();
    createPreferenceHierarchy();
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.location.LocationMode
 * JD-Core Version:    0.6.2
 */