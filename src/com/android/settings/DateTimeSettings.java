package com.android.settings;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.Settings.Global;
import android.provider.Settings.SettingNotFoundException;
import android.provider.Settings.System;
import android.widget.DatePicker;
import android.widget.TimePicker;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateTimeSettings extends SettingsPreferenceFragment
  implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, SharedPreferences.OnSharedPreferenceChangeListener
{
  private CheckBoxPreference mAutoTimePref;
  private CheckBoxPreference mAutoTimeZonePref;
  private ListPreference mDateFormat;
  private Preference mDatePref;
  private Calendar mDummyDate;
  private BroadcastReceiver mIntentReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      Activity localActivity = DateTimeSettings.this.getActivity();
      if (localActivity != null)
        DateTimeSettings.this.updateTimeAndDateDisplay(localActivity);
    }
  };
  private Preference mTime24Pref;
  private Preference mTimePref;
  private Preference mTimeZone;

  static void configureDatePicker(DatePicker paramDatePicker)
  {
    Calendar localCalendar = Calendar.getInstance();
    localCalendar.clear();
    localCalendar.set(1970, 0, 1);
    paramDatePicker.setMinDate(localCalendar.getTimeInMillis());
    localCalendar.clear();
    localCalendar.set(2037, 11, 31);
    paramDatePicker.setMaxDate(localCalendar.getTimeInMillis());
  }

  private boolean getAutoState(String paramString)
  {
    try
    {
      int i = Settings.Global.getInt(getContentResolver(), paramString);
      boolean bool = false;
      if (i > 0)
        bool = true;
      return bool;
    }
    catch (Settings.SettingNotFoundException localSettingNotFoundException)
    {
    }
    return false;
  }

  private String getDateFormat()
  {
    return Settings.System.getString(getContentResolver(), "date_format");
  }

  private static String getTimeZoneText(TimeZone paramTimeZone)
  {
    SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("ZZZZ, zzzz");
    localSimpleDateFormat.setTimeZone(paramTimeZone);
    return localSimpleDateFormat.format(new Date());
  }

  private void initUI()
  {
    boolean bool1 = getAutoState("auto_time");
    boolean bool2 = getAutoState("auto_time_zone");
    boolean bool3 = getActivity().getIntent().getBooleanExtra("firstRun", false);
    this.mDummyDate = Calendar.getInstance();
    this.mAutoTimePref = ((CheckBoxPreference)findPreference("auto_time"));
    this.mAutoTimePref.setChecked(bool1);
    this.mAutoTimeZonePref = ((CheckBoxPreference)findPreference("auto_zone"));
    if ((Utils.isWifiOnly(getActivity())) || (bool3))
    {
      getPreferenceScreen().removePreference(this.mAutoTimeZonePref);
      bool2 = false;
    }
    this.mAutoTimeZonePref.setChecked(bool2);
    this.mTimePref = findPreference("time");
    this.mTime24Pref = findPreference("24 hour");
    this.mTimeZone = findPreference("timezone");
    this.mDatePref = findPreference("date");
    this.mDateFormat = ((ListPreference)findPreference("date_format"));
    if (bool3)
    {
      getPreferenceScreen().removePreference(this.mTime24Pref);
      getPreferenceScreen().removePreference(this.mDateFormat);
    }
    String[] arrayOfString1 = getResources().getStringArray(2131165185);
    String[] arrayOfString2 = new String[arrayOfString1.length];
    String str1 = getDateFormat();
    if (str1 == null)
      str1 = "";
    Calendar localCalendar = this.mDummyDate;
    int i = this.mDummyDate.get(1);
    localCalendar.set(i, 11, 31, 13, 0, 0);
    int j = 0;
    if (j < arrayOfString2.length)
    {
      String str2 = android.text.format.DateFormat.getDateFormatForSetting(getActivity(), arrayOfString1[j]).format(this.mDummyDate.getTime());
      if (arrayOfString1[j].length() == 0)
        arrayOfString2[j] = getResources().getString(2131427480, new Object[] { str2 });
      while (true)
      {
        j++;
        break;
        arrayOfString2[j] = str2;
      }
    }
    this.mDateFormat.setEntries(arrayOfString2);
    this.mDateFormat.setEntryValues(2131165185);
    this.mDateFormat.setValue(str1);
    Preference localPreference1 = this.mTimePref;
    boolean bool4;
    boolean bool5;
    label401: Preference localPreference3;
    if (!bool1)
    {
      bool4 = true;
      localPreference1.setEnabled(bool4);
      Preference localPreference2 = this.mDatePref;
      if (bool1)
        break label435;
      bool5 = true;
      localPreference2.setEnabled(bool5);
      localPreference3 = this.mTimeZone;
      if (bool2)
        break label441;
    }
    label435: label441: for (boolean bool6 = true; ; bool6 = false)
    {
      localPreference3.setEnabled(bool6);
      return;
      bool4 = false;
      break;
      bool5 = false;
      break label401;
    }
  }

  private boolean is24Hour()
  {
    return android.text.format.DateFormat.is24HourFormat(getActivity());
  }

  private void set24Hour(boolean paramBoolean)
  {
    ContentResolver localContentResolver = getContentResolver();
    if (paramBoolean);
    for (String str = "24"; ; str = "12")
    {
      Settings.System.putString(localContentResolver, "time_12_24", str);
      return;
    }
  }

  static void setDate(Context paramContext, int paramInt1, int paramInt2, int paramInt3)
  {
    Calendar localCalendar = Calendar.getInstance();
    localCalendar.set(1, paramInt1);
    localCalendar.set(2, paramInt2);
    localCalendar.set(5, paramInt3);
    long l = localCalendar.getTimeInMillis();
    if (l / 1000L < 2147483647L)
      ((AlarmManager)paramContext.getSystemService("alarm")).setTime(l);
  }

  static void setTime(Context paramContext, int paramInt1, int paramInt2)
  {
    Calendar localCalendar = Calendar.getInstance();
    localCalendar.set(11, paramInt1);
    localCalendar.set(12, paramInt2);
    localCalendar.set(13, 0);
    localCalendar.set(14, 0);
    long l = localCalendar.getTimeInMillis();
    if (l / 1000L < 2147483647L)
      ((AlarmManager)paramContext.getSystemService("alarm")).setTime(l);
  }

  private void timeUpdated()
  {
    Intent localIntent = new Intent("android.intent.action.TIME_SET");
    getActivity().sendBroadcast(localIntent);
  }

  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    updateTimeAndDateDisplay(getActivity());
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131034124);
    initUI();
  }

  public Dialog onCreateDialog(int paramInt)
  {
    Calendar localCalendar = Calendar.getInstance();
    switch (paramInt)
    {
    default:
      throw new IllegalArgumentException();
    case 0:
      DatePickerDialog localDatePickerDialog = new DatePickerDialog(getActivity(), this, localCalendar.get(1), localCalendar.get(2), localCalendar.get(5));
      configureDatePicker(localDatePickerDialog.getDatePicker());
      return localDatePickerDialog;
    case 1:
    }
    return new TimePickerDialog(getActivity(), this, localCalendar.get(11), localCalendar.get(12), android.text.format.DateFormat.is24HourFormat(getActivity()));
  }

  public void onDateSet(DatePicker paramDatePicker, int paramInt1, int paramInt2, int paramInt3)
  {
    Activity localActivity = getActivity();
    if (localActivity != null)
    {
      setDate(localActivity, paramInt1, paramInt2, paramInt3);
      updateTimeAndDateDisplay(localActivity);
    }
  }

  public void onPause()
  {
    super.onPause();
    getActivity().unregisterReceiver(this.mIntentReceiver);
    getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
  }

  public boolean onPreferenceTreeClick(PreferenceScreen paramPreferenceScreen, Preference paramPreference)
  {
    if (paramPreference == this.mDatePref)
      showDialog(0);
    while (true)
    {
      return super.onPreferenceTreeClick(paramPreferenceScreen, paramPreference);
      if (paramPreference == this.mTimePref)
      {
        removeDialog(1);
        showDialog(1);
      }
      else if (paramPreference == this.mTime24Pref)
      {
        set24Hour(((CheckBoxPreference)this.mTime24Pref).isChecked());
        updateTimeAndDateDisplay(getActivity());
        timeUpdated();
      }
    }
  }

  public void onResume()
  {
    super.onResume();
    getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    ((CheckBoxPreference)this.mTime24Pref).setChecked(is24Hour());
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.intent.action.TIME_TICK");
    localIntentFilter.addAction("android.intent.action.TIME_SET");
    localIntentFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
    getActivity().registerReceiver(this.mIntentReceiver, localIntentFilter, null, null);
    updateTimeAndDateDisplay(getActivity());
  }

  public void onSharedPreferenceChanged(SharedPreferences paramSharedPreferences, String paramString)
  {
    int i = 1;
    if (paramString.equals("date_format"))
    {
      String str = paramSharedPreferences.getString(paramString, getResources().getString(2131427479));
      Settings.System.putString(getContentResolver(), "date_format", str);
      updateTimeAndDateDisplay(getActivity());
    }
    label108: 
    do
    {
      return;
      if (paramString.equals("auto_time"))
      {
        boolean bool2 = paramSharedPreferences.getBoolean(paramString, i);
        ContentResolver localContentResolver2 = getContentResolver();
        boolean bool3;
        Preference localPreference3;
        if (bool2)
        {
          int m = i;
          Settings.Global.putInt(localContentResolver2, "auto_time", m);
          Preference localPreference2 = this.mTimePref;
          if (bool2)
            break label139;
          bool3 = i;
          localPreference2.setEnabled(bool3);
          localPreference3 = this.mDatePref;
          if (bool2)
            break label145;
        }
        while (true)
        {
          localPreference3.setEnabled(i);
          return;
          int n = 0;
          break;
          bool3 = false;
          break label108;
          i = 0;
        }
      }
    }
    while (!paramString.equals("auto_zone"));
    label139: label145: boolean bool1 = paramSharedPreferences.getBoolean(paramString, i);
    ContentResolver localContentResolver1 = getContentResolver();
    Preference localPreference1;
    if (bool1)
    {
      int j = i;
      Settings.Global.putInt(localContentResolver1, "auto_time_zone", j);
      localPreference1 = this.mTimeZone;
      if (bool1)
        break label217;
    }
    while (true)
    {
      localPreference1.setEnabled(i);
      return;
      int k = 0;
      break;
      label217: i = 0;
    }
  }

  public void onTimeSet(TimePicker paramTimePicker, int paramInt1, int paramInt2)
  {
    Activity localActivity = getActivity();
    if (localActivity != null)
    {
      setTime(localActivity, paramInt1, paramInt2);
      updateTimeAndDateDisplay(localActivity);
    }
  }

  public void updateTimeAndDateDisplay(Context paramContext)
  {
    java.text.DateFormat localDateFormat = android.text.format.DateFormat.getDateFormat(paramContext);
    Calendar localCalendar = Calendar.getInstance();
    this.mDummyDate.setTimeZone(localCalendar.getTimeZone());
    this.mDummyDate.set(localCalendar.get(1), 11, 31, 13, 0, 0);
    Date localDate = this.mDummyDate.getTime();
    this.mTimePref.setSummary(android.text.format.DateFormat.getTimeFormat(getActivity()).format(localCalendar.getTime()));
    this.mTimeZone.setSummary(getTimeZoneText(localCalendar.getTimeZone()));
    this.mDatePref.setSummary(localDateFormat.format(localCalendar.getTime()));
    this.mDateFormat.setSummary(localDateFormat.format(localDate));
    this.mTime24Pref.setSummary(android.text.format.DateFormat.getTimeFormat(getActivity()).format(localDate));
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.DateTimeSettings
 * JD-Core Version:    0.6.2
 */