package com.android.settings;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceFragment.OnPreferenceStartFragmentCallback;
import android.provider.Settings.Global;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.ListPopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import java.util.Calendar;
import java.util.TimeZone;

public class DateTimeSettingsSetupWizard extends Activity
  implements PreferenceFragment.OnPreferenceStartFragmentCallback, View.OnClickListener, AdapterView.OnItemClickListener, CompoundButton.OnCheckedChangeListener
{
  private static final String TAG = DateTimeSettingsSetupWizard.class.getSimpleName();
  private CompoundButton mAutoDateTimeButton;
  private DatePicker mDatePicker;
  private InputMethodManager mInputMethodManager;
  private BroadcastReceiver mIntentReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      DateTimeSettingsSetupWizard.this.updateTimeAndDateDisplay();
    }
  };
  private TimeZone mSelectedTimeZone;
  private TimePicker mTimePicker;
  private SimpleAdapter mTimeZoneAdapter;
  private Button mTimeZoneButton;
  private ListPopupWindow mTimeZonePopup;
  private boolean mUsingXLargeLayout;

  private boolean isAutoDateTimeEnabled()
  {
    try
    {
      int i = Settings.Global.getInt(getContentResolver(), "auto_time");
      return i > 0;
    }
    catch (Settings.SettingNotFoundException localSettingNotFoundException)
    {
    }
    return true;
  }

  private void showTimezonePicker(int paramInt)
  {
    View localView = findViewById(paramInt);
    if (localView == null)
    {
      Log.e(TAG, "Unable to find zone picker anchor view " + paramInt);
      return;
    }
    this.mTimeZonePopup = new ListPopupWindow(this, null);
    this.mTimeZonePopup.setWidth(localView.getWidth());
    this.mTimeZonePopup.setAnchorView(localView);
    this.mTimeZonePopup.setAdapter(this.mTimeZoneAdapter);
    this.mTimeZonePopup.setOnItemClickListener(this);
    this.mTimeZonePopup.setModal(true);
    this.mTimeZonePopup.show();
  }

  private void updateTimeAndDateDisplay()
  {
    if (!this.mUsingXLargeLayout)
      return;
    Calendar localCalendar = Calendar.getInstance();
    this.mTimeZoneButton.setText(localCalendar.getTimeZone().getDisplayName());
    this.mDatePicker.updateDate(localCalendar.get(1), localCalendar.get(2), localCalendar.get(5));
    this.mTimePicker.setCurrentHour(Integer.valueOf(localCalendar.get(11)));
    this.mTimePicker.setCurrentMinute(Integer.valueOf(localCalendar.get(12)));
  }

  public void initUiForXl()
  {
    boolean bool1 = true;
    TimeZone localTimeZone = TimeZone.getDefault();
    this.mSelectedTimeZone = localTimeZone;
    this.mTimeZoneButton = ((Button)findViewById(2131230817));
    this.mTimeZoneButton.setText(localTimeZone.getDisplayName());
    this.mTimeZoneButton.setOnClickListener(this);
    Intent localIntent = getIntent();
    boolean bool2;
    boolean bool3;
    label123: DatePicker localDatePicker;
    if (localIntent.hasExtra("extra_initial_auto_datetime_value"))
    {
      bool2 = localIntent.getBooleanExtra("extra_initial_auto_datetime_value", false);
      this.mAutoDateTimeButton = ((CompoundButton)findViewById(2131230819));
      this.mAutoDateTimeButton.setChecked(bool2);
      this.mAutoDateTimeButton.setOnCheckedChangeListener(this);
      this.mTimePicker = ((TimePicker)findViewById(2131230824));
      TimePicker localTimePicker = this.mTimePicker;
      if (bool2)
        break label235;
      bool3 = bool1;
      localTimePicker.setEnabled(bool3);
      this.mDatePicker = ((DatePicker)findViewById(2131230821));
      localDatePicker = this.mDatePicker;
      if (bool2)
        break label241;
    }
    while (true)
    {
      localDatePicker.setEnabled(bool1);
      this.mDatePicker.setCalendarViewShown(false);
      DateTimeSettings.configureDatePicker(this.mDatePicker);
      this.mInputMethodManager = ((InputMethodManager)getSystemService("input_method"));
      ((Button)findViewById(2131230763)).setOnClickListener(this);
      Button localButton = (Button)findViewById(2131231061);
      if (localButton != null)
        localButton.setOnClickListener(this);
      return;
      bool2 = isAutoDateTimeEnabled();
      break;
      label235: bool3 = false;
      break label123;
      label241: bool1 = false;
    }
  }

  public void onCheckedChanged(CompoundButton paramCompoundButton, boolean paramBoolean)
  {
    int i = 1;
    boolean bool;
    label46: DatePicker localDatePicker;
    if (paramCompoundButton == this.mAutoDateTimeButton)
    {
      ContentResolver localContentResolver = getContentResolver();
      if (!paramBoolean)
        break label104;
      int j = i;
      Settings.Global.putInt(localContentResolver, "auto_time", j);
      TimePicker localTimePicker = this.mTimePicker;
      if (paramBoolean)
        break label110;
      bool = i;
      localTimePicker.setEnabled(bool);
      localDatePicker = this.mDatePicker;
      if (paramBoolean)
        break label116;
    }
    while (true)
    {
      localDatePicker.setEnabled(i);
      if (paramBoolean)
      {
        View localView = getCurrentFocus();
        if (localView != null)
        {
          this.mInputMethodManager.hideSoftInputFromWindow(localView.getWindowToken(), 0);
          localView.clearFocus();
        }
      }
      return;
      label104: int k = 0;
      break;
      label110: bool = false;
      break label46;
      label116: i = 0;
    }
  }

  public void onClick(View paramView)
  {
    ContentResolver localContentResolver;
    switch (paramView.getId())
    {
    default:
      return;
    case 2131230817:
      showTimezonePicker(2131230817);
      return;
    case 2131230763:
      if ((this.mSelectedTimeZone != null) && (!TimeZone.getDefault().equals(this.mSelectedTimeZone)))
      {
        Log.i(TAG, "Another TimeZone is selected by a user. Changing system TimeZone.");
        ((AlarmManager)getSystemService("alarm")).setTimeZone(this.mSelectedTimeZone.getID());
      }
      if (this.mAutoDateTimeButton != null)
      {
        localContentResolver = getContentResolver();
        if (!this.mAutoDateTimeButton.isChecked())
          break label199;
      }
      break;
    case 2131231061:
    }
    label199: for (int i = 1; ; i = 0)
    {
      Settings.Global.putInt(localContentResolver, "auto_time", i);
      if (!this.mAutoDateTimeButton.isChecked())
      {
        DateTimeSettings.setDate(this, this.mDatePicker.getYear(), this.mDatePicker.getMonth(), this.mDatePicker.getDayOfMonth());
        DateTimeSettings.setTime(this, this.mTimePicker.getCurrentHour().intValue(), this.mTimePicker.getCurrentMinute().intValue());
      }
      setResult(-1);
      finish();
      return;
    }
  }

  protected void onCreate(Bundle paramBundle)
  {
    int i = 1;
    requestWindowFeature(i);
    super.onCreate(paramBundle);
    setContentView(2130968620);
    if (findViewById(2131230817) != null)
    {
      this.mUsingXLargeLayout = i;
      if (!this.mUsingXLargeLayout)
        break label83;
      initUiForXl();
    }
    while (true)
    {
      this.mTimeZoneAdapter = ZonePicker.constructTimezoneAdapter(this, false, 2130968621);
      if (!this.mUsingXLargeLayout)
        findViewById(2131230813).setSystemUiVisibility(4194304);
      return;
      i = 0;
      break;
      label83: findViewById(2131230763).setOnClickListener(this);
    }
  }

  public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
  {
    TimeZone localTimeZone = ZonePicker.obtainTimeZoneFromItem(paramAdapterView.getItemAtPosition(paramInt));
    if (this.mUsingXLargeLayout)
    {
      this.mSelectedTimeZone = localTimeZone;
      Calendar localCalendar = Calendar.getInstance(localTimeZone);
      if (this.mTimeZoneButton != null)
        this.mTimeZoneButton.setText(localTimeZone.getDisplayName());
      this.mDatePicker.updateDate(localCalendar.get(1), localCalendar.get(2), localCalendar.get(5));
      this.mTimePicker.setCurrentHour(Integer.valueOf(localCalendar.get(11)));
      this.mTimePicker.setCurrentMinute(Integer.valueOf(localCalendar.get(12)));
    }
    while (true)
    {
      this.mTimeZonePopup.dismiss();
      return;
      ((AlarmManager)getSystemService("alarm")).setTimeZone(localTimeZone.getID());
      ((DateTimeSettings)getFragmentManager().findFragmentById(2131230815)).updateTimeAndDateDisplay(this);
    }
  }

  public void onPause()
  {
    super.onPause();
    unregisterReceiver(this.mIntentReceiver);
  }

  public boolean onPreferenceStartFragment(PreferenceFragment paramPreferenceFragment, Preference paramPreference)
  {
    showTimezonePicker(2131230814);
    return true;
  }

  public void onResume()
  {
    super.onResume();
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.intent.action.TIME_TICK");
    localIntentFilter.addAction("android.intent.action.TIME_SET");
    localIntentFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
    registerReceiver(this.mIntentReceiver, localIntentFilter, null, null);
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.DateTimeSettingsSetupWizard
 * JD-Core Version:    0.6.2
 */