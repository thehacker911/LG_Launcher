package com.android.settings;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.preference.TwoStatePreference;
import android.provider.Settings.Global;
import android.provider.Settings.System;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.android.settings.bluetooth.DockEventReceiver;
import java.util.List;

public class SoundSettings extends SettingsPreferenceFragment
  implements Preference.OnPreferenceChangeListener
{
  private static final String[] NEED_VOICE_CAPABILITY = { "ringtone", "dtmf_tone", "category_calls_and_notification", "emergency_tone", "vibrate_when_ringing" };
  private AudioManager mAudioManager;
  private CheckBoxPreference mDockAudioMediaEnabled;
  private Preference mDockAudioSettings;
  private Intent mDockIntent;
  private CheckBoxPreference mDockSounds;
  private CheckBoxPreference mDtmfTone;
  private Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      switch (paramAnonymousMessage.what)
      {
      default:
        return;
      case 1:
        SoundSettings.this.mRingtonePreference.setSummary((CharSequence)paramAnonymousMessage.obj);
        return;
      case 2:
      }
      SoundSettings.this.mNotificationPreference.setSummary((CharSequence)paramAnonymousMessage.obj);
    }
  };
  private CheckBoxPreference mHapticFeedback;
  private CheckBoxPreference mLockSounds;
  private Preference mMusicFx;
  private Preference mNotificationPreference;
  private final BroadcastReceiver mReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if (paramAnonymousIntent.getAction().equals("android.intent.action.DOCK_EVENT"))
        SoundSettings.this.handleDockChange(paramAnonymousIntent);
    }
  };
  private Runnable mRingtoneLookupRunnable;
  private Preference mRingtonePreference;
  private CheckBoxPreference mSoundEffects;
  private PreferenceGroup mSoundSettings;
  private CheckBoxPreference mVibrateWhenRinging;

  private Dialog createUndockedMessage()
  {
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(getActivity());
    localBuilder.setTitle(2131428034);
    localBuilder.setMessage(2131428035);
    localBuilder.setPositiveButton(17039370, null);
    return localBuilder.create();
  }

  private void handleDockChange(Intent paramIntent)
  {
    boolean bool1 = true;
    int i;
    boolean bool2;
    if (this.mDockAudioSettings != null)
    {
      i = paramIntent.getIntExtra("android.intent.extra.DOCK_STATE", 0);
      if (paramIntent.getParcelableExtra("android.bluetooth.device.extra.DEVICE") == null)
        break label57;
      bool2 = bool1;
    }
    while (true)
    {
      this.mDockIntent = paramIntent;
      if (i != 0);
      try
      {
        removeDialog(1);
        label43: if (bool2)
        {
          this.mDockAudioSettings.setEnabled(bool1);
          return;
          label57: bool2 = false;
          continue;
        }
        if (i == 3)
        {
          ContentResolver localContentResolver = getContentResolver();
          this.mDockAudioSettings.setEnabled(bool1);
          if (Settings.Global.getInt(localContentResolver, "dock_audio_media_enabled", -1) == -1)
            Settings.Global.putInt(localContentResolver, "dock_audio_media_enabled", 0);
          this.mDockAudioMediaEnabled = ((CheckBoxPreference)findPreference("dock_audio_media_enabled"));
          this.mDockAudioMediaEnabled.setPersistent(false);
          CheckBoxPreference localCheckBoxPreference = this.mDockAudioMediaEnabled;
          if (Settings.Global.getInt(localContentResolver, "dock_audio_media_enabled", 0) != 0);
          while (true)
          {
            localCheckBoxPreference.setChecked(bool1);
            return;
            bool1 = false;
          }
        }
        this.mDockAudioSettings.setEnabled(false);
        return;
        this.mDockAudioSettings.setEnabled(false);
        return;
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        break label43;
      }
    }
  }

  private void initDockSettings()
  {
    ContentResolver localContentResolver = getContentResolver();
    if (needsDockSettings())
    {
      this.mDockSounds = ((CheckBoxPreference)findPreference("dock_sounds"));
      this.mDockSounds.setPersistent(false);
      CheckBoxPreference localCheckBoxPreference = this.mDockSounds;
      if (Settings.Global.getInt(localContentResolver, "dock_sounds_enabled", 0) != 0);
      for (boolean bool = true; ; bool = false)
      {
        localCheckBoxPreference.setChecked(bool);
        this.mDockAudioSettings = findPreference("dock_audio");
        this.mDockAudioSettings.setEnabled(false);
        return;
      }
    }
    getPreferenceScreen().removePreference(findPreference("dock_category"));
    getPreferenceScreen().removePreference(findPreference("dock_audio"));
    getPreferenceScreen().removePreference(findPreference("dock_sounds"));
    Settings.Global.putInt(localContentResolver, "dock_audio_media_enabled", 1);
  }

  private void lookupRingtoneNames()
  {
    new Thread(this.mRingtoneLookupRunnable).start();
  }

  private boolean needsDockSettings()
  {
    return getResources().getBoolean(2131296256);
  }

  private void updateRingtoneName(int paramInt1, Preference paramPreference, int paramInt2)
  {
    if (paramPreference == null);
    Activity localActivity;
    do
    {
      return;
      localActivity = getActivity();
    }
    while (localActivity == null);
    Uri localUri = RingtoneManager.getActualDefaultRingtoneUri(localActivity, paramInt1);
    String str = localActivity.getString(17040431);
    if (localUri == null)
      str = localActivity.getString(17040429);
    while (true)
    {
      this.mHandler.sendMessage(this.mHandler.obtainMessage(paramInt2, str));
      return;
      try
      {
        Cursor localCursor = localActivity.getContentResolver().query(localUri, new String[] { "title" }, null, null, null);
        if (localCursor != null)
        {
          if (localCursor.moveToFirst())
            str = localCursor.getString(0);
          localCursor.close();
        }
      }
      catch (SQLiteException localSQLiteException)
      {
      }
    }
  }

  protected int getHelpResource()
  {
    return 2131429259;
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    ContentResolver localContentResolver = getContentResolver();
    int i = TelephonyManager.getDefault().getCurrentPhoneType();
    this.mAudioManager = ((AudioManager)getSystemService("audio"));
    addPreferencesFromResource(2131034158);
    if (2 != i)
      getPreferenceScreen().removePreference(findPreference("emergency_tone"));
    if (!getResources().getBoolean(2131296257))
      findPreference("ring_volume").setDependency(null);
    if (getResources().getBoolean(17891415))
      getPreferenceScreen().removePreference(findPreference("ring_volume"));
    this.mVibrateWhenRinging = ((CheckBoxPreference)findPreference("vibrate_when_ringing"));
    this.mVibrateWhenRinging.setPersistent(false);
    CheckBoxPreference localCheckBoxPreference1 = this.mVibrateWhenRinging;
    boolean bool1;
    boolean bool2;
    label196: boolean bool3;
    label245: boolean bool4;
    label294: CheckBoxPreference localCheckBoxPreference5;
    if (Settings.System.getInt(localContentResolver, "vibrate_when_ringing", 0) != 0)
    {
      bool1 = true;
      localCheckBoxPreference1.setChecked(bool1);
      this.mDtmfTone = ((CheckBoxPreference)findPreference("dtmf_tone"));
      this.mDtmfTone.setPersistent(false);
      CheckBoxPreference localCheckBoxPreference2 = this.mDtmfTone;
      if (Settings.System.getInt(localContentResolver, "dtmf_tone", 1) == 0)
        break label587;
      bool2 = true;
      localCheckBoxPreference2.setChecked(bool2);
      this.mSoundEffects = ((CheckBoxPreference)findPreference("sound_effects"));
      this.mSoundEffects.setPersistent(false);
      CheckBoxPreference localCheckBoxPreference3 = this.mSoundEffects;
      if (Settings.System.getInt(localContentResolver, "sound_effects_enabled", 1) == 0)
        break label593;
      bool3 = true;
      localCheckBoxPreference3.setChecked(bool3);
      this.mHapticFeedback = ((CheckBoxPreference)findPreference("haptic_feedback"));
      this.mHapticFeedback.setPersistent(false);
      CheckBoxPreference localCheckBoxPreference4 = this.mHapticFeedback;
      if (Settings.System.getInt(localContentResolver, "haptic_feedback_enabled", 1) == 0)
        break label599;
      bool4 = true;
      localCheckBoxPreference4.setChecked(bool4);
      this.mLockSounds = ((CheckBoxPreference)findPreference("lock_sounds"));
      this.mLockSounds.setPersistent(false);
      localCheckBoxPreference5 = this.mLockSounds;
      if (Settings.System.getInt(localContentResolver, "lockscreen_sounds_enabled", 1) == 0)
        break label605;
    }
    label587: label593: label599: label605: for (boolean bool5 = true; ; bool5 = false)
    {
      localCheckBoxPreference5.setChecked(bool5);
      this.mRingtonePreference = findPreference("ringtone");
      this.mNotificationPreference = findPreference("notification_sound");
      Vibrator localVibrator = (Vibrator)getSystemService("vibrator");
      if ((localVibrator == null) || (!localVibrator.hasVibrator()))
      {
        removePreference("vibrate_when_ringing");
        removePreference("haptic_feedback");
      }
      if (2 == i)
      {
        ListPreference localListPreference = (ListPreference)findPreference("emergency_tone");
        localListPreference.setValue(String.valueOf(Settings.Global.getInt(localContentResolver, "emergency_tone", 0)));
        localListPreference.setOnPreferenceChangeListener(this);
      }
      this.mSoundSettings = ((PreferenceGroup)findPreference("sound_settings"));
      this.mMusicFx = this.mSoundSettings.findPreference("musicfx");
      Intent localIntent = new Intent("android.media.action.DISPLAY_AUDIO_EFFECT_CONTROL_PANEL");
      if (getPackageManager().queryIntentActivities(localIntent, 512).size() <= 2)
        this.mSoundSettings.removePreference(this.mMusicFx);
      if (Utils.isVoiceCapable(getActivity()))
        break label611;
      String[] arrayOfString = NEED_VOICE_CAPABILITY;
      int j = arrayOfString.length;
      for (int k = 0; k < j; k++)
      {
        Preference localPreference = findPreference(arrayOfString[k]);
        if (localPreference != null)
          getPreferenceScreen().removePreference(localPreference);
      }
      bool1 = false;
      break;
      bool2 = false;
      break label196;
      bool3 = false;
      break label245;
      bool4 = false;
      break label294;
    }
    label611: this.mRingtoneLookupRunnable = new Runnable()
    {
      public void run()
      {
        if (SoundSettings.this.mRingtonePreference != null)
          SoundSettings.this.updateRingtoneName(1, SoundSettings.this.mRingtonePreference, 1);
        if (SoundSettings.this.mNotificationPreference != null)
          SoundSettings.this.updateRingtoneName(2, SoundSettings.this.mNotificationPreference, 2);
      }
    };
    initDockSettings();
  }

  public Dialog onCreateDialog(int paramInt)
  {
    if (paramInt == 1)
      return createUndockedMessage();
    return null;
  }

  public void onPause()
  {
    super.onPause();
    getActivity().unregisterReceiver(this.mReceiver);
  }

  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    if ("emergency_tone".equals(paramPreference.getKey()));
    try
    {
      int i = Integer.parseInt((String)paramObject);
      Settings.Global.putInt(getContentResolver(), "emergency_tone", i);
      return true;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      while (true)
        Log.e("SoundSettings", "could not persist emergency tone setting", localNumberFormatException);
    }
  }

  public boolean onPreferenceTreeClick(PreferenceScreen paramPreferenceScreen, Preference paramPreference)
  {
    int i5;
    if (paramPreference == this.mVibrateWhenRinging)
    {
      ContentResolver localContentResolver7 = getContentResolver();
      if (this.mVibrateWhenRinging.isChecked())
      {
        i5 = 1;
        Settings.System.putInt(localContentResolver7, "vibrate_when_ringing", i5);
      }
    }
    while (true)
    {
      label37: boolean bool1 = true;
      Preference localPreference;
      do
      {
        return bool1;
        i5 = 0;
        break;
        if (paramPreference == this.mDtmfTone)
        {
          ContentResolver localContentResolver6 = getContentResolver();
          boolean bool8 = this.mDtmfTone.isChecked();
          int i4 = 0;
          if (bool8)
            i4 = 1;
          Settings.System.putInt(localContentResolver6, "dtmf_tone", i4);
          break label37;
        }
        if (paramPreference == this.mSoundEffects)
        {
          if (this.mSoundEffects.isChecked())
            this.mAudioManager.loadSoundEffects();
          while (true)
          {
            ContentResolver localContentResolver5 = getContentResolver();
            boolean bool7 = this.mSoundEffects.isChecked();
            int i3 = 0;
            if (bool7)
              i3 = 1;
            Settings.System.putInt(localContentResolver5, "sound_effects_enabled", i3);
            break;
            this.mAudioManager.unloadSoundEffects();
          }
        }
        if (paramPreference == this.mHapticFeedback)
        {
          ContentResolver localContentResolver4 = getContentResolver();
          boolean bool6 = this.mHapticFeedback.isChecked();
          int i2 = 0;
          if (bool6)
            i2 = 1;
          Settings.System.putInt(localContentResolver4, "haptic_feedback_enabled", i2);
          break label37;
        }
        if (paramPreference == this.mLockSounds)
        {
          ContentResolver localContentResolver3 = getContentResolver();
          boolean bool5 = this.mLockSounds.isChecked();
          int i1 = 0;
          if (bool5)
            i1 = 1;
          Settings.System.putInt(localContentResolver3, "lockscreen_sounds_enabled", i1);
          break label37;
        }
        localPreference = this.mMusicFx;
        bool1 = false;
      }
      while (paramPreference == localPreference);
      if (paramPreference == this.mDockAudioSettings)
      {
        if (this.mDockIntent != null);
        for (int k = this.mDockIntent.getIntExtra("android.intent.extra.DOCK_STATE", 0); ; k = 0)
        {
          if (k != 0)
            break label326;
          showDialog(1);
          break;
        }
        label326: if (this.mDockIntent.getParcelableExtra("android.bluetooth.device.extra.DEVICE") != null);
        for (int m = 1; ; m = 0)
        {
          if (m == 0)
            break label399;
          Intent localIntent = new Intent(this.mDockIntent);
          localIntent.setAction("com.android.settings.bluetooth.action.DOCK_SHOW_UI");
          localIntent.setClass(getActivity(), DockEventReceiver.class);
          getActivity().sendBroadcast(localIntent);
          break;
        }
        label399: PreferenceScreen localPreferenceScreen = (PreferenceScreen)this.mDockAudioSettings;
        Bundle localBundle = localPreferenceScreen.getExtras();
        int n = Settings.Global.getInt(getContentResolver(), "dock_audio_media_enabled", 0);
        boolean bool4 = false;
        if (n == 1)
          bool4 = true;
        localBundle.putBoolean("checked", bool4);
        super.onPreferenceTreeClick(localPreferenceScreen, localPreferenceScreen);
      }
      else if (paramPreference == this.mDockSounds)
      {
        ContentResolver localContentResolver2 = getContentResolver();
        boolean bool3 = this.mDockSounds.isChecked();
        int j = 0;
        if (bool3)
          j = 1;
        Settings.Global.putInt(localContentResolver2, "dock_sounds_enabled", j);
      }
      else if (paramPreference == this.mDockAudioMediaEnabled)
      {
        ContentResolver localContentResolver1 = getContentResolver();
        boolean bool2 = this.mDockAudioMediaEnabled.isChecked();
        int i = 0;
        if (bool2)
          i = 1;
        Settings.Global.putInt(localContentResolver1, "dock_audio_media_enabled", i);
      }
    }
  }

  public void onResume()
  {
    super.onResume();
    lookupRingtoneNames();
    IntentFilter localIntentFilter = new IntentFilter("android.intent.action.DOCK_EVENT");
    getActivity().registerReceiver(this.mReceiver, localIntentFilter);
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.SoundSettings
 * JD-Core Version:    0.6.2
 */