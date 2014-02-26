package com.android.settings.deviceinfo;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.UserManager;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.preference.TwoStatePreference;
import android.util.Log;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

public class UsbSettings extends SettingsPreferenceFragment
{
  private CheckBoxPreference mMtp;
  private CheckBoxPreference mPtp;
  private final BroadcastReceiver mStateReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if (paramAnonymousIntent.getAction().equals("android.hardware.usb.action.USB_STATE"))
      {
        UsbSettings.access$002(UsbSettings.this, paramAnonymousIntent.getBooleanExtra("accessory", false));
        Log.e("UsbSettings", "UsbAccessoryMode " + UsbSettings.this.mUsbAccessoryMode);
      }
      UsbSettings.this.updateToggles(UsbSettings.this.mUsbManager.getDefaultFunction());
    }
  };
  private boolean mUsbAccessoryMode;
  private UsbManager mUsbManager;

  private PreferenceScreen createPreferenceHierarchy()
  {
    PreferenceScreen localPreferenceScreen1 = getPreferenceScreen();
    if (localPreferenceScreen1 != null)
      localPreferenceScreen1.removeAll();
    addPreferencesFromResource(2131034167);
    PreferenceScreen localPreferenceScreen2 = getPreferenceScreen();
    this.mMtp = ((CheckBoxPreference)localPreferenceScreen2.findPreference("usb_mtp"));
    this.mPtp = ((CheckBoxPreference)localPreferenceScreen2.findPreference("usb_ptp"));
    if (((UserManager)getActivity().getSystemService("user")).hasUserRestriction("no_usb_file_transfer"))
    {
      this.mMtp.setEnabled(false);
      this.mPtp.setEnabled(false);
    }
    return localPreferenceScreen2;
  }

  private void updateToggles(String paramString)
  {
    if ("mtp".equals(paramString))
    {
      this.mMtp.setChecked(true);
      this.mPtp.setChecked(false);
    }
    while (((UserManager)getActivity().getSystemService("user")).hasUserRestriction("no_usb_file_transfer"))
    {
      Log.e("UsbSettings", "USB is locked down");
      this.mMtp.setEnabled(false);
      this.mPtp.setEnabled(false);
      return;
      if ("ptp".equals(paramString))
      {
        this.mMtp.setChecked(false);
        this.mPtp.setChecked(true);
      }
      else
      {
        this.mMtp.setChecked(false);
        this.mPtp.setChecked(false);
      }
    }
    if (!this.mUsbAccessoryMode)
    {
      Log.e("UsbSettings", "USB Normal Mode");
      this.mMtp.setEnabled(true);
      this.mPtp.setEnabled(true);
      return;
    }
    Log.e("UsbSettings", "USB Accessory Mode");
    this.mMtp.setEnabled(false);
    this.mPtp.setEnabled(false);
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mUsbManager = ((UsbManager)getSystemService("usb"));
  }

  public void onPause()
  {
    super.onPause();
    getActivity().unregisterReceiver(this.mStateReceiver);
  }

  public boolean onPreferenceTreeClick(PreferenceScreen paramPreferenceScreen, Preference paramPreference)
  {
    if (Utils.isMonkeyRunning());
    while (((UserManager)getActivity().getSystemService("user")).hasUserRestriction("no_usb_file_transfer"))
      return true;
    String str = "none";
    if ((paramPreference == this.mMtp) && (this.mMtp.isChecked()));
    for (str = "mtp"; ; str = "ptp")
      do
      {
        this.mUsbManager.setCurrentFunction(str, true);
        updateToggles(str);
        return true;
      }
      while ((paramPreference != this.mPtp) || (!this.mPtp.isChecked()));
  }

  public void onResume()
  {
    super.onResume();
    createPreferenceHierarchy();
    getActivity().registerReceiver(this.mStateReceiver, new IntentFilter("android.hardware.usb.action.USB_STATE"));
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.deviceinfo.UsbSettings
 * JD-Core Version:    0.6.2
 */