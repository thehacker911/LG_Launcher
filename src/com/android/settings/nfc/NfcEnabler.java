package com.android.settings.nfc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.preference.TwoStatePreference;

public class NfcEnabler
  implements Preference.OnPreferenceChangeListener
{
  private final PreferenceScreen mAndroidBeam;
  private final CheckBoxPreference mCheckbox;
  private final Context mContext;
  private final IntentFilter mIntentFilter;
  private final NfcAdapter mNfcAdapter;
  private final BroadcastReceiver mReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if ("android.nfc.action.ADAPTER_STATE_CHANGED".equals(paramAnonymousIntent.getAction()))
        NfcEnabler.this.handleNfcStateChanged(paramAnonymousIntent.getIntExtra("android.nfc.extra.ADAPTER_STATE", 1));
    }
  };

  public NfcEnabler(Context paramContext, CheckBoxPreference paramCheckBoxPreference, PreferenceScreen paramPreferenceScreen)
  {
    this.mContext = paramContext;
    this.mCheckbox = paramCheckBoxPreference;
    this.mAndroidBeam = paramPreferenceScreen;
    this.mNfcAdapter = NfcAdapter.getDefaultAdapter(paramContext);
    if (this.mNfcAdapter == null)
    {
      this.mCheckbox.setEnabled(false);
      this.mAndroidBeam.setEnabled(false);
      this.mIntentFilter = null;
      return;
    }
    this.mIntentFilter = new IntentFilter("android.nfc.action.ADAPTER_STATE_CHANGED");
  }

  private void handleNfcStateChanged(int paramInt)
  {
    switch (paramInt)
    {
    default:
      return;
    case 1:
      this.mCheckbox.setChecked(false);
      this.mCheckbox.setEnabled(true);
      this.mAndroidBeam.setEnabled(false);
      this.mAndroidBeam.setSummary(2131427803);
      return;
    case 3:
      this.mCheckbox.setChecked(true);
      this.mCheckbox.setEnabled(true);
      this.mAndroidBeam.setEnabled(true);
      if (this.mNfcAdapter.isNdefPushEnabled())
      {
        this.mAndroidBeam.setSummary(2131427801);
        return;
      }
      this.mAndroidBeam.setSummary(2131427802);
      return;
    case 2:
      this.mCheckbox.setChecked(true);
      this.mCheckbox.setEnabled(false);
      this.mAndroidBeam.setEnabled(false);
      return;
    case 4:
    }
    this.mCheckbox.setChecked(false);
    this.mCheckbox.setEnabled(false);
    this.mAndroidBeam.setEnabled(false);
  }

  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    boolean bool = ((Boolean)paramObject).booleanValue();
    this.mCheckbox.setEnabled(false);
    if (bool)
    {
      this.mNfcAdapter.enable();
      return false;
    }
    this.mNfcAdapter.disable();
    return false;
  }

  public void pause()
  {
    if (this.mNfcAdapter == null)
      return;
    this.mContext.unregisterReceiver(this.mReceiver);
    this.mCheckbox.setOnPreferenceChangeListener(null);
  }

  public void resume()
  {
    if (this.mNfcAdapter == null)
      return;
    handleNfcStateChanged(this.mNfcAdapter.getAdapterState());
    this.mContext.registerReceiver(this.mReceiver, this.mIntentFilter);
    this.mCheckbox.setOnPreferenceChangeListener(this);
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.nfc.NfcEnabler
 * JD-Core Version:    0.6.2
 */