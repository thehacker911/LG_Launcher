package com.android.settings;

import android.app.Activity;
import android.app.ActivityManagerNative;
import android.app.Dialog;
import android.app.Fragment;
import android.app.IActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.RemoteException;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.preference.TwoStatePreference;
import android.provider.Settings.SettingNotFoundException;
import android.provider.Settings.System;
import android.util.Log;
import com.android.internal.view.RotationPolicy;
import com.android.internal.view.RotationPolicy.RotationPolicyListener;
import java.util.ArrayList;

public class DisplaySettings extends SettingsPreferenceFragment
  implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener
{
  private CheckBoxPreference mAccelerometer;
  private final Configuration mCurConfig = new Configuration();
  private WarnedListPreference mFontSizePref;
  private CheckBoxPreference mNotificationPulse;
  private final RotationPolicy.RotationPolicyListener mRotationPolicyListener = new RotationPolicy.RotationPolicyListener()
  {
    public void onChange()
    {
      DisplaySettings.this.updateAccelerometerRotationCheckbox();
    }
  };
  private Preference mScreenSaverPreference;
  private ListPreference mScreenTimeoutPreference;

  private void disableUnusableTimeouts(ListPreference paramListPreference)
  {
    DevicePolicyManager localDevicePolicyManager = (DevicePolicyManager)getActivity().getSystemService("device_policy");
    if (localDevicePolicyManager != null);
    for (long l = localDevicePolicyManager.getMaximumTimeToLock(null); l == 0L; l = 0L)
      return;
    CharSequence[] arrayOfCharSequence1 = paramListPreference.getEntries();
    CharSequence[] arrayOfCharSequence2 = paramListPreference.getEntryValues();
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    for (int i = 0; i < arrayOfCharSequence2.length; i++)
      if (Long.parseLong(arrayOfCharSequence2[i].toString()) <= l)
      {
        localArrayList1.add(arrayOfCharSequence1[i]);
        localArrayList2.add(arrayOfCharSequence2[i]);
      }
    if ((localArrayList1.size() != arrayOfCharSequence1.length) || (localArrayList2.size() != arrayOfCharSequence2.length))
    {
      int j = Integer.parseInt(paramListPreference.getValue());
      paramListPreference.setEntries((CharSequence[])localArrayList1.toArray(new CharSequence[localArrayList1.size()]));
      paramListPreference.setEntryValues((CharSequence[])localArrayList2.toArray(new CharSequence[localArrayList2.size()]));
      if (j <= l)
        paramListPreference.setValue(String.valueOf(j));
    }
    else
    {
      if (localArrayList1.size() <= 0)
        break label271;
    }
    label271: for (boolean bool = true; ; bool = false)
    {
      paramListPreference.setEnabled(bool);
      return;
      if ((localArrayList2.size() <= 0) || (Long.parseLong(((CharSequence)localArrayList2.get(-1 + localArrayList2.size())).toString()) != l))
        break;
      paramListPreference.setValue(String.valueOf(l));
      break;
    }
  }

  private void updateAccelerometerRotationCheckbox()
  {
    if (getActivity() == null)
      return;
    CheckBoxPreference localCheckBoxPreference = this.mAccelerometer;
    if (!RotationPolicy.isRotationLocked(getActivity()));
    for (boolean bool = true; ; bool = false)
    {
      localCheckBoxPreference.setChecked(bool);
      return;
    }
  }

  private void updateScreenSaverSummary()
  {
    if (this.mScreenSaverPreference != null)
      this.mScreenSaverPreference.setSummary(DreamSettings.getSummaryTextWithDreamName(getActivity()));
  }

  private void updateState()
  {
    updateAccelerometerRotationCheckbox();
    readFontSizePreference(this.mFontSizePref);
    updateScreenSaverSummary();
  }

  private void updateTimeoutPreferenceDescription(long paramLong)
  {
    ListPreference localListPreference = this.mScreenTimeoutPreference;
    String str;
    if (paramLong < 0L)
      str = "";
    while (true)
    {
      localListPreference.setSummary(str);
      return;
      CharSequence[] arrayOfCharSequence1 = localListPreference.getEntries();
      CharSequence[] arrayOfCharSequence2 = localListPreference.getEntryValues();
      if ((arrayOfCharSequence1 == null) || (arrayOfCharSequence1.length == 0))
      {
        str = "";
      }
      else
      {
        int i = 0;
        for (int j = 0; j < arrayOfCharSequence2.length; j++)
          if (paramLong >= Long.parseLong(arrayOfCharSequence2[j].toString()))
            i = j;
        Context localContext = localListPreference.getContext();
        Object[] arrayOfObject = new Object[1];
        arrayOfObject[0] = arrayOfCharSequence1[i];
        str = localContext.getString(2131428049, arrayOfObject);
      }
    }
  }

  int floatToIndex(float paramFloat)
  {
    String[] arrayOfString = getResources().getStringArray(2131165193);
    float f1 = Float.parseFloat(arrayOfString[0]);
    for (int i = 1; i < arrayOfString.length; i++)
    {
      float f2 = Float.parseFloat(arrayOfString[i]);
      if (paramFloat < f1 + 0.5F * (f2 - f1))
        return i - 1;
      f1 = f2;
    }
    return -1 + arrayOfString.length;
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    ContentResolver localContentResolver = getActivity().getContentResolver();
    addPreferencesFromResource(2131034130);
    this.mAccelerometer = ((CheckBoxPreference)findPreference("accelerometer"));
    this.mAccelerometer.setPersistent(false);
    if ((!RotationPolicy.isRotationSupported(getActivity())) || (RotationPolicy.isRotationLockToggleSupported(getActivity())))
      getPreferenceScreen().removePreference(this.mAccelerometer);
    this.mScreenSaverPreference = findPreference("screensaver");
    if ((this.mScreenSaverPreference != null) && (!getResources().getBoolean(17891401)))
      getPreferenceScreen().removePreference(this.mScreenSaverPreference);
    this.mScreenTimeoutPreference = ((ListPreference)findPreference("screen_timeout"));
    long l = Settings.System.getLong(localContentResolver, "screen_off_timeout", 30000L);
    this.mScreenTimeoutPreference.setValue(String.valueOf(l));
    this.mScreenTimeoutPreference.setOnPreferenceChangeListener(this);
    disableUnusableTimeouts(this.mScreenTimeoutPreference);
    updateTimeoutPreferenceDescription(l);
    this.mFontSizePref = ((WarnedListPreference)findPreference("font_size"));
    this.mFontSizePref.setOnPreferenceChangeListener(this);
    this.mFontSizePref.setOnPreferenceClickListener(this);
    this.mNotificationPulse = ((CheckBoxPreference)findPreference("notification_pulse"));
    if ((this.mNotificationPulse != null) && (!getResources().getBoolean(17891367)))
    {
      getPreferenceScreen().removePreference(this.mNotificationPulse);
      return;
    }
    while (true)
    {
      try
      {
        CheckBoxPreference localCheckBoxPreference = this.mNotificationPulse;
        if (Settings.System.getInt(localContentResolver, "notification_light_pulse") == 1)
        {
          bool = true;
          localCheckBoxPreference.setChecked(bool);
          this.mNotificationPulse.setOnPreferenceChangeListener(this);
          return;
        }
      }
      catch (Settings.SettingNotFoundException localSettingNotFoundException)
      {
        Log.e("DisplaySettings", "notification_light_pulse not found");
        return;
      }
      boolean bool = false;
    }
  }

  public Dialog onCreateDialog(int paramInt)
  {
    if (paramInt == 1)
      return Utils.buildGlobalChangeWarningDialog(getActivity(), 2131429241, new Runnable()
      {
        public void run()
        {
          DisplaySettings.this.mFontSizePref.click();
        }
      });
    return null;
  }

  public void onPause()
  {
    super.onPause();
    RotationPolicy.unregisterRotationPolicyListener(getActivity(), this.mRotationPolicyListener);
  }

  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    String str = paramPreference.getKey();
    int i;
    if ("screen_timeout".equals(str))
      i = Integer.parseInt((String)paramObject);
    try
    {
      Settings.System.putInt(getContentResolver(), "screen_off_timeout", i);
      updateTimeoutPreferenceDescription(i);
      if ("font_size".equals(str))
        writeFontSizePreference(paramObject);
      return true;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      while (true)
        Log.e("DisplaySettings", "could not persist screen timeout setting", localNumberFormatException);
    }
  }

  public boolean onPreferenceClick(Preference paramPreference)
  {
    if (paramPreference == this.mFontSizePref)
    {
      if (Utils.hasMultipleUsers(getActivity()))
      {
        showDialog(1);
        return true;
      }
      this.mFontSizePref.click();
    }
    return false;
  }

  public boolean onPreferenceTreeClick(PreferenceScreen paramPreferenceScreen, Preference paramPreference)
  {
    int i = 1;
    if (paramPreference == this.mAccelerometer)
    {
      localActivity = getActivity();
      if (!this.mAccelerometer.isChecked())
        RotationPolicy.setRotationLockForAccessibility(localActivity, i);
    }
    while (paramPreference != this.mNotificationPulse)
      while (true)
      {
        Activity localActivity;
        return super.onPreferenceTreeClick(paramPreferenceScreen, paramPreference);
        i = 0;
      }
    boolean bool = this.mNotificationPulse.isChecked();
    ContentResolver localContentResolver = getContentResolver();
    int j = 0;
    if (bool)
      j = i;
    Settings.System.putInt(localContentResolver, "notification_light_pulse", j);
    return i;
  }

  public void onResume()
  {
    super.onResume();
    RotationPolicy.registerRotationPolicyListener(getActivity(), this.mRotationPolicyListener);
    updateState();
  }

  public void readFontSizePreference(ListPreference paramListPreference)
  {
    try
    {
      this.mCurConfig.updateFrom(ActivityManagerNative.getDefault().getConfiguration());
      int i = floatToIndex(this.mCurConfig.fontScale);
      paramListPreference.setValueIndex(i);
      Resources localResources = getResources();
      String[] arrayOfString = localResources.getStringArray(2131165192);
      String str = localResources.getString(2131428064);
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = arrayOfString[i];
      paramListPreference.setSummary(String.format(str, arrayOfObject));
      return;
    }
    catch (RemoteException localRemoteException)
    {
      while (true)
        Log.w("DisplaySettings", "Unable to retrieve font size");
    }
  }

  public void writeFontSizePreference(Object paramObject)
  {
    try
    {
      this.mCurConfig.fontScale = Float.parseFloat(paramObject.toString());
      ActivityManagerNative.getDefault().updatePersistentConfiguration(this.mCurConfig);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.w("DisplaySettings", "Unable to save font size");
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.DisplaySettings
 * JD-Core Version:    0.6.2
 */