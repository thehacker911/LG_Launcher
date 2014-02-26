package com.android.settings;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.SELinux;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.widget.Toast;
import com.android.internal.app.PlatLogoActivity;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeviceInfoSettings extends RestrictedSettingsFragment
{
  int mDevHitCountdown;
  Toast mDevHitToast;
  long[] mHits = new long[3];

  public DeviceInfoSettings()
  {
    super(null);
  }

  public static String formatKernelVersion(String paramString)
  {
    Matcher localMatcher = Pattern.compile("Linux version (\\S+) \\((\\S+?)\\) (?:\\(gcc.+? \\)) (#\\d+) (?:.*?)?((Sun|Mon|Tue|Wed|Thu|Fri|Sat).+)").matcher(paramString);
    if (!localMatcher.matches())
    {
      Log.e("DeviceInfoSettings", "Regex did not match on /proc/version: " + paramString);
      return "Unavailable";
    }
    if (localMatcher.groupCount() < 4)
    {
      Log.e("DeviceInfoSettings", "Regex match on /proc/version only returned " + localMatcher.groupCount() + " groups");
      return "Unavailable";
    }
    return localMatcher.group(1) + "\n" + localMatcher.group(2) + " " + localMatcher.group(3) + "\n" + localMatcher.group(4);
  }

  public static String getFormattedKernelVersion()
  {
    try
    {
      String str = formatKernelVersion(readLine("/proc/version"));
      return str;
    }
    catch (IOException localIOException)
    {
      Log.e("DeviceInfoSettings", "IO Exception when getting kernel version for Device Info screen", localIOException);
    }
    return "Unavailable";
  }

  private String getMsvSuffix()
  {
    try
    {
      if (Long.parseLong(readLine("/sys/board_properties/soc/msv"), 16) == 0L)
        return " (ENGINEERING)";
    }
    catch (NumberFormatException localNumberFormatException)
    {
      return "";
    }
    catch (IOException localIOException)
    {
      label19: break label19;
    }
  }

  private static String readLine(String paramString)
    throws IOException
  {
    BufferedReader localBufferedReader = new BufferedReader(new FileReader(paramString), 256);
    try
    {
      String str = localBufferedReader.readLine();
      return str;
    }
    finally
    {
      localBufferedReader.close();
    }
  }

  private void removePreferenceIfBoolFalse(String paramString, int paramInt)
  {
    if (!getResources().getBoolean(paramInt))
    {
      Preference localPreference = findPreference(paramString);
      if (localPreference != null)
        getPreferenceScreen().removePreference(localPreference);
    }
  }

  private void removePreferenceIfPropertyMissing(PreferenceGroup paramPreferenceGroup, String paramString1, String paramString2)
  {
    if (SystemProperties.get(paramString2).equals(""));
    try
    {
      paramPreferenceGroup.removePreference(findPreference(paramString1));
      return;
    }
    catch (RuntimeException localRuntimeException)
    {
      Log.d("DeviceInfoSettings", "Property '" + paramString2 + "' missing and no '" + paramString1 + "' preference");
    }
  }

  private void setStringSummary(String paramString1, String paramString2)
  {
    try
    {
      findPreference(paramString1).setSummary(paramString2);
      return;
    }
    catch (RuntimeException localRuntimeException)
    {
      findPreference(paramString1).setSummary(getResources().getString(2131427339));
    }
  }

  private void setValueSummary(String paramString1, String paramString2)
  {
    try
    {
      findPreference(paramString1).setSummary(SystemProperties.get(paramString2, getResources().getString(2131427339)));
      return;
    }
    catch (RuntimeException localRuntimeException)
    {
    }
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131034127);
    protectByRestrictions("build_number");
    setStringSummary("firmware_version", Build.VERSION.RELEASE);
    findPreference("firmware_version").setEnabled(true);
    setValueSummary("baseband_version", "gsm.version.baseband");
    setStringSummary("device_model", Build.MODEL + getMsvSuffix());
    setValueSummary("fcc_equipment_id", "ro.ril.fccid");
    setStringSummary("device_model", Build.MODEL);
    setStringSummary("build_number", Build.DISPLAY);
    findPreference("build_number").setEnabled(true);
    findPreference("kernel_version").setSummary(getFormattedKernelVersion());
    Activity localActivity;
    PreferenceScreen localPreferenceScreen;
    if (!SELinux.isSELinuxEnabled())
    {
      setStringSummary("selinux_status", getResources().getString(2131429187));
      removePreferenceIfPropertyMissing(getPreferenceScreen(), "selinux_status", "ro.build.selinux");
      removePreferenceIfPropertyMissing(getPreferenceScreen(), "safetylegal", "ro.url.safetylegal");
      removePreferenceIfPropertyMissing(getPreferenceScreen(), "fcc_equipment_id", "ro.ril.fccid");
      if (Utils.isWifiOnly(getActivity()))
        getPreferenceScreen().removePreference(findPreference("baseband_version"));
      localActivity = getActivity();
      PreferenceGroup localPreferenceGroup = (PreferenceGroup)findPreference("container");
      Utils.updatePreferenceToSpecificActivityOrRemove(localActivity, localPreferenceGroup, "terms", 1);
      Utils.updatePreferenceToSpecificActivityOrRemove(localActivity, localPreferenceGroup, "license", 1);
      Utils.updatePreferenceToSpecificActivityOrRemove(localActivity, localPreferenceGroup, "copyright", 1);
      Utils.updatePreferenceToSpecificActivityOrRemove(localActivity, localPreferenceGroup, "team", 1);
      localPreferenceScreen = getPreferenceScreen();
      if (UserHandle.myUserId() != 0)
        break label341;
      Utils.updatePreferenceToSpecificActivityOrRemove(localActivity, localPreferenceScreen, "system_update_settings", 1);
    }
    while (true)
    {
      Utils.updatePreferenceToSpecificActivityOrRemove(localActivity, localPreferenceScreen, "contributors", 1);
      removePreferenceIfBoolFalse("additional_system_update_settings", 2131296259);
      removePreferenceIfBoolFalse("regulatory_info", 2131296261);
      return;
      if (SELinux.isSELinuxEnforced())
        break;
      setStringSummary("selinux_status", getResources().getString(2131429188));
      break;
      label341: removePreference("system_update_settings");
    }
  }

  public boolean onPreferenceTreeClick(PreferenceScreen paramPreferenceScreen, Preference paramPreference)
  {
    int i = 1;
    Intent localIntent;
    if (paramPreference.getKey().equals("firmware_version"))
    {
      System.arraycopy(this.mHits, i, this.mHits, 0, -1 + this.mHits.length);
      this.mHits[(-1 + this.mHits.length)] = SystemClock.uptimeMillis();
      if (this.mHits[0] >= SystemClock.uptimeMillis() - 500L)
      {
        localIntent = new Intent("android.intent.action.MAIN");
        localIntent.setClassName("android", PlatLogoActivity.class.getName());
      }
    }
    while (true)
    {
      int j;
      try
      {
        startActivity(localIntent);
        j = super.onPreferenceTreeClick(paramPreferenceScreen, paramPreference);
        return j;
      }
      catch (Exception localException)
      {
        Log.e("DeviceInfoSettings", "Unable to start activity " + localIntent.toString());
        continue;
      }
      if (paramPreference.getKey().equals("build_number"))
        if (UserHandle.myUserId() == 0)
          if (this.mDevHitCountdown > 0)
          {
            if ((this.mDevHitCountdown != j) || (!super.ensurePinRestrictedPreference(paramPreference)))
            {
              this.mDevHitCountdown = (-1 + this.mDevHitCountdown);
              if (this.mDevHitCountdown == 0)
              {
                getActivity().getSharedPreferences("development", 0).edit().putBoolean("show", j).apply();
                if (this.mDevHitToast != null)
                  this.mDevHitToast.cancel();
                this.mDevHitToast = Toast.makeText(getActivity(), 2131427340, j);
                this.mDevHitToast.show();
              }
              else if ((this.mDevHitCountdown > 0) && (this.mDevHitCountdown < 5))
              {
                if (this.mDevHitToast != null)
                  this.mDevHitToast.cancel();
                Activity localActivity = getActivity();
                Resources localResources = getResources();
                int k = this.mDevHitCountdown;
                Object[] arrayOfObject = new Object[j];
                arrayOfObject[0] = Integer.valueOf(this.mDevHitCountdown);
                this.mDevHitToast = Toast.makeText(localActivity, localResources.getQuantityString(2131623936, k, arrayOfObject), 0);
                this.mDevHitToast.show();
              }
            }
          }
          else if (this.mDevHitCountdown < 0)
          {
            if (this.mDevHitToast != null)
              this.mDevHitToast.cancel();
            this.mDevHitToast = Toast.makeText(getActivity(), 2131427341, j);
            this.mDevHitToast.show();
          }
    }
  }

  public void onResume()
  {
    super.onResume();
    if (getActivity().getSharedPreferences("development", 0).getBoolean("show", Build.TYPE.equals("eng")));
    for (int i = -1; ; i = 7)
    {
      this.mDevHitCountdown = i;
      this.mDevHitToast = null;
      return;
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.DeviceInfoSettings
 * JD-Core Version:    0.6.2
 */