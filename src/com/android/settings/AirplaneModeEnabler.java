package com.android.settings;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.TwoStatePreference;
import android.provider.Settings.Global;
import com.android.internal.telephony.PhoneStateIntentReceiver;

public class AirplaneModeEnabler
  implements Preference.OnPreferenceChangeListener
{
  private ContentObserver mAirplaneModeObserver = new ContentObserver(new Handler())
  {
    public void onChange(boolean paramAnonymousBoolean)
    {
      AirplaneModeEnabler.this.onAirplaneModeChanged();
    }
  };
  private final CheckBoxPreference mCheckBoxPref;
  private final Context mContext;
  private Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      switch (paramAnonymousMessage.what)
      {
      default:
        return;
      case 3:
      }
      AirplaneModeEnabler.this.onAirplaneModeChanged();
    }
  };
  private PhoneStateIntentReceiver mPhoneStateReceiver;

  public AirplaneModeEnabler(Context paramContext, CheckBoxPreference paramCheckBoxPreference)
  {
    this.mContext = paramContext;
    this.mCheckBoxPref = paramCheckBoxPreference;
    paramCheckBoxPreference.setPersistent(false);
    this.mPhoneStateReceiver = new PhoneStateIntentReceiver(this.mContext, this.mHandler);
    this.mPhoneStateReceiver.notifyServiceState(3);
  }

  public static boolean isAirplaneModeOn(Context paramContext)
  {
    int i = Settings.Global.getInt(paramContext.getContentResolver(), "airplane_mode_on", 0);
    boolean bool = false;
    if (i != 0)
      bool = true;
    return bool;
  }

  private void onAirplaneModeChanged()
  {
    this.mCheckBoxPref.setChecked(isAirplaneModeOn(this.mContext));
  }

  private void setAirplaneModeOn(boolean paramBoolean)
  {
    ContentResolver localContentResolver = this.mContext.getContentResolver();
    if (paramBoolean);
    for (int i = 1; ; i = 0)
    {
      Settings.Global.putInt(localContentResolver, "airplane_mode_on", i);
      this.mCheckBoxPref.setChecked(paramBoolean);
      Intent localIntent = new Intent("android.intent.action.AIRPLANE_MODE");
      localIntent.putExtra("state", paramBoolean);
      this.mContext.sendBroadcastAsUser(localIntent, UserHandle.ALL);
      return;
    }
  }

  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    if (Boolean.parseBoolean(SystemProperties.get("ril.cdma.inecmmode")));
    while (true)
    {
      return true;
      setAirplaneModeOn(((Boolean)paramObject).booleanValue());
    }
  }

  public void pause()
  {
    this.mPhoneStateReceiver.unregisterIntent();
    this.mCheckBoxPref.setOnPreferenceChangeListener(null);
    this.mContext.getContentResolver().unregisterContentObserver(this.mAirplaneModeObserver);
  }

  public void resume()
  {
    this.mCheckBoxPref.setChecked(isAirplaneModeOn(this.mContext));
    this.mPhoneStateReceiver.registerIntent();
    this.mCheckBoxPref.setOnPreferenceChangeListener(this);
    this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("airplane_mode_on"), true, this.mAirplaneModeObserver);
  }

  public void setAirplaneModeInECM(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean1)
    {
      setAirplaneModeOn(paramBoolean2);
      return;
    }
    onAirplaneModeChanged();
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.AirplaneModeEnabler
 * JD-Core Version:    0.6.2
 */