package com.android.settings;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.ActivityManagerNative;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.Fragment;
import android.app.IActivityManager;
import android.app.admin.DevicePolicyManager;
import android.app.backup.IBackupManager;
import android.app.backup.IBackupManager.Stub;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.hardware.usb.IUsbManager;
import android.hardware.usb.IUsbManager.Stub;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.PowerManager;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.preference.TwoStatePreference;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;
import android.provider.Settings.System;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.IWindowManager;
import android.view.IWindowManager.Stub;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import dalvik.system.VMRuntime;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class DevelopmentSettings extends RestrictedSettingsFragment
  implements DialogInterface.OnClickListener, DialogInterface.OnDismissListener, Preference.OnPreferenceChangeListener, CompoundButton.OnCheckedChangeListener
{
  private Dialog mAdbDialog;
  private Dialog mAdbKeysDialog;
  private final ArrayList<Preference> mAllPrefs = new ArrayList();
  private CheckBoxPreference mAllowMockLocation;
  private ListPreference mAnimatorDurationScale;
  private ListPreference mAppProcessLimit;
  private IBackupManager mBackupManager;
  private CheckBoxPreference mBtHciSnoopLog;
  private Preference mBugreport;
  private CheckBoxPreference mBugreportInPower;
  private Preference mClearAdbKeys;
  private String mDebugApp;
  private Preference mDebugAppPref;
  private ListPreference mDebugHwOverdraw;
  private CheckBoxPreference mDebugLayout;
  private boolean mDialogClicked;
  private CheckBoxPreference mDisableOverlays;
  private final HashSet<Preference> mDisabledPrefs = new HashSet();
  private boolean mDontPokeProperties;
  private DevicePolicyManager mDpm;
  private CheckBoxPreference mEnableAdb;
  private Dialog mEnableDialog;
  private CheckBoxPreference mEnableTerminal;
  private Switch mEnabledSwitch;
  private CheckBoxPreference mForceHardwareUi;
  private CheckBoxPreference mForceMsaa;
  private CheckBoxPreference mForceRtlLayout;
  private boolean mHaveDebugSettings;
  private CheckBoxPreference mImmediatelyDestroyActivities;
  private CheckBoxPreference mKeepScreenOn;
  private boolean mLastEnabledState;
  private ListPreference mOpenGLTraces;
  private ListPreference mOverlayDisplayDevices;
  private PreferenceScreen mPassword;
  private CheckBoxPreference mPointerLocation;
  private final ArrayList<CheckBoxPreference> mResetCbPrefs = new ArrayList();
  private CheckBoxPreference mShowAllANRs;
  private CheckBoxPreference mShowCpuUsage;
  private CheckBoxPreference mShowHwLayersUpdates;
  private CheckBoxPreference mShowHwScreenUpdates;
  private ListPreference mShowNonRectClip;
  private CheckBoxPreference mShowScreenUpdates;
  private CheckBoxPreference mShowTouches;
  private CheckBoxPreference mStrictMode;
  private ListPreference mTrackFrameTime;
  private ListPreference mTransitionAnimationScale;
  private boolean mUnavailable;
  private CheckBoxPreference mVerifyAppsOverUsb;
  private CheckBoxPreference mWaitForDebugger;
  private CheckBoxPreference mWifiDisplayCertification;
  private ListPreference mWindowAnimationScale;
  private IWindowManager mWindowManager;

  public DevelopmentSettings()
  {
    super("restrictions_pin_set");
  }

  private ListPreference addListPreference(String paramString)
  {
    ListPreference localListPreference = (ListPreference)findPreference(paramString);
    this.mAllPrefs.add(localListPreference);
    localListPreference.setOnPreferenceChangeListener(this);
    return localListPreference;
  }

  private String currentRuntimeValue()
  {
    return SystemProperties.get("persist.sys.dalvik.vm.lib", VMRuntime.getRuntime().vmLibrary());
  }

  private static int currentStrictModeActiveIndex()
  {
    if (TextUtils.isEmpty(SystemProperties.get("persist.sys.strictmode.visual")))
      return 0;
    if (SystemProperties.getBoolean("persist.sys.strictmode.visual", false))
      return 1;
    return 2;
  }

  private void disableForUser(Preference paramPreference)
  {
    if (paramPreference != null)
    {
      paramPreference.setEnabled(false);
      this.mDisabledPrefs.add(paramPreference);
    }
  }

  private void dismissDialogs()
  {
    if (this.mAdbDialog != null)
    {
      this.mAdbDialog.dismiss();
      this.mAdbDialog = null;
    }
    if (this.mAdbKeysDialog != null)
    {
      this.mAdbKeysDialog.dismiss();
      this.mAdbKeysDialog = null;
    }
    if (this.mEnableDialog != null)
    {
      this.mEnableDialog.dismiss();
      this.mEnableDialog = null;
    }
  }

  private boolean enableVerifierSetting()
  {
    ContentResolver localContentResolver = getActivity().getContentResolver();
    if (Settings.Global.getInt(localContentResolver, "adb_enabled", 0) == 0);
    PackageManager localPackageManager;
    Intent localIntent;
    do
    {
      do
        return false;
      while (Settings.Global.getInt(localContentResolver, "package_verifier_enable", 1) == 0);
      localPackageManager = getActivity().getPackageManager();
      localIntent = new Intent("android.intent.action.PACKAGE_NEEDS_VERIFICATION");
      localIntent.setType("application/vnd.android.package-archive");
      localIntent.addFlags(1);
    }
    while (localPackageManager.queryBroadcastReceivers(localIntent, 0).size() == 0);
    return true;
  }

  private CheckBoxPreference findAndInitCheckboxPref(String paramString)
  {
    CheckBoxPreference localCheckBoxPreference = (CheckBoxPreference)findPreference(paramString);
    if (localCheckBoxPreference == null)
      throw new IllegalArgumentException("Cannot find preference with key = " + paramString);
    this.mAllPrefs.add(localCheckBoxPreference);
    this.mResetCbPrefs.add(localCheckBoxPreference);
    return localCheckBoxPreference;
  }

  private static boolean isPackageInstalled(Context paramContext, String paramString)
  {
    try
    {
      PackageInfo localPackageInfo = paramContext.getPackageManager().getPackageInfo(paramString, 0);
      boolean bool = false;
      if (localPackageInfo != null)
        bool = true;
      return bool;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
    }
    return false;
  }

  private void removePreference(Preference paramPreference)
  {
    getPreferenceScreen().removePreference(paramPreference);
    this.mAllPrefs.remove(paramPreference);
  }

  private boolean removePreferenceForProduction(Preference paramPreference)
  {
    if ("user".equals(Build.TYPE))
    {
      removePreference(paramPreference);
      return true;
    }
    return false;
  }

  private void resetDangerousOptions()
  {
    this.mDontPokeProperties = true;
    for (int i = 0; i < this.mResetCbPrefs.size(); i++)
    {
      CheckBoxPreference localCheckBoxPreference = (CheckBoxPreference)this.mResetCbPrefs.get(i);
      if (localCheckBoxPreference.isChecked())
      {
        localCheckBoxPreference.setChecked(false);
        onPreferenceTreeClick(null, localCheckBoxPreference);
      }
    }
    resetDebuggerOptions();
    writeAnimationScaleOption(0, this.mWindowAnimationScale, null);
    writeAnimationScaleOption(1, this.mTransitionAnimationScale, null);
    writeAnimationScaleOption(2, this.mAnimatorDurationScale, null);
    writeOverlayDisplayDevicesOptions(null);
    writeAppProcessLimitOptions(null);
    this.mHaveDebugSettings = false;
    updateAllOptions();
    this.mDontPokeProperties = false;
    pokeSystemProperties();
  }

  private static void resetDebuggerOptions()
  {
    try
    {
      ActivityManagerNative.getDefault().setDebugApp(null, false, true);
      return;
    }
    catch (RemoteException localRemoteException)
    {
    }
  }

  private void setPrefsEnabledState(boolean paramBoolean)
  {
    int i = 0;
    if (i < this.mAllPrefs.size())
    {
      Preference localPreference = (Preference)this.mAllPrefs.get(i);
      if ((paramBoolean) && (!this.mDisabledPrefs.contains(localPreference)));
      for (boolean bool = true; ; bool = false)
      {
        localPreference.setEnabled(bool);
        i++;
        break;
      }
    }
    updateAllOptions();
  }

  private boolean showVerifierSetting()
  {
    return Settings.Global.getInt(getActivity().getContentResolver(), "verifier_setting_visible", 1) > 0;
  }

  private void updateAllOptions()
  {
    int i = 1;
    Activity localActivity = getActivity();
    ContentResolver localContentResolver = localActivity.getContentResolver();
    this.mHaveDebugSettings = false;
    CheckBoxPreference localCheckBoxPreference1 = this.mEnableAdb;
    label74: label102: CheckBoxPreference localCheckBoxPreference5;
    if (Settings.Global.getInt(localContentResolver, "adb_enabled", 0) != 0)
    {
      int k = i;
      updateCheckBox(localCheckBoxPreference1, k);
      if (this.mEnableTerminal != null)
      {
        CheckBoxPreference localCheckBoxPreference6 = this.mEnableTerminal;
        if (localActivity.getPackageManager().getApplicationEnabledSetting("com.android.terminal") != i)
          break label305;
        int i6 = i;
        updateCheckBox(localCheckBoxPreference6, i6);
      }
      CheckBoxPreference localCheckBoxPreference2 = this.mBugreportInPower;
      if (Settings.Secure.getInt(localContentResolver, "bugreport_in_power_menu", 0) == 0)
        break label311;
      int n = i;
      updateCheckBox(localCheckBoxPreference2, n);
      CheckBoxPreference localCheckBoxPreference3 = this.mKeepScreenOn;
      if (Settings.Global.getInt(localContentResolver, "stay_on_while_plugged_in", 0) == 0)
        break label317;
      int i2 = i;
      label130: updateCheckBox(localCheckBoxPreference3, i2);
      CheckBoxPreference localCheckBoxPreference4 = this.mBtHciSnoopLog;
      if (Settings.Secure.getInt(localContentResolver, "bluetooth_hci_log", 0) == 0)
        break label323;
      int i4 = i;
      label158: updateCheckBox(localCheckBoxPreference4, i4);
      localCheckBoxPreference5 = this.mAllowMockLocation;
      if (Settings.Secure.getInt(localContentResolver, "mock_location", 0) == 0)
        break label329;
    }
    while (true)
    {
      updateCheckBox(localCheckBoxPreference5, i);
      updateRuntimeValue();
      updateHdcpValues();
      updatePasswordSummary();
      updateDebuggerOptions();
      updateStrictModeVisualOptions();
      updatePointerLocationOptions();
      updateShowTouchesOptions();
      updateFlingerOptions();
      updateCpuUsageOptions();
      updateHardwareUiOptions();
      updateMsaaOptions();
      updateTrackFrameTimeOptions();
      updateShowNonRectClipOptions();
      updateShowHwScreenUpdatesOptions();
      updateShowHwLayersUpdatesOptions();
      updateDebugHwOverdrawOptions();
      updateDebugLayoutOptions();
      updateAnimationScaleOptions();
      updateOverlayDisplayDevicesOptions();
      updateOpenGLTracesOptions();
      updateImmediatelyDestroyActivitiesOptions();
      updateAppProcessLimitOptions();
      updateShowAllANRsOptions();
      updateVerifyAppsOverUsbOptions();
      updateBugreportOptions();
      updateForceRtlOptions();
      updateWifiDisplayCertificationOptions();
      return;
      int m = 0;
      break;
      label305: int i7 = 0;
      break label74;
      label311: int i1 = 0;
      break label102;
      label317: int i3 = 0;
      break label130;
      label323: int i5 = 0;
      break label158;
      label329: int j = 0;
    }
  }

  private void updateAnimationScaleOptions()
  {
    updateAnimationScaleValue(0, this.mWindowAnimationScale);
    updateAnimationScaleValue(1, this.mTransitionAnimationScale);
    updateAnimationScaleValue(2, this.mAnimatorDurationScale);
  }

  private void updateAnimationScaleValue(int paramInt, ListPreference paramListPreference)
  {
    while (true)
    {
      int i;
      try
      {
        float f = this.mWindowManager.getAnimationScale(paramInt);
        if (f != 1.0F)
          this.mHaveDebugSettings = true;
        CharSequence[] arrayOfCharSequence = paramListPreference.getEntryValues();
        i = 0;
        if (i < arrayOfCharSequence.length)
        {
          if (f <= Float.parseFloat(arrayOfCharSequence[i].toString()))
          {
            paramListPreference.setValueIndex(i);
            paramListPreference.setSummary(paramListPreference.getEntries()[i]);
          }
        }
        else
        {
          paramListPreference.setValueIndex(-1 + arrayOfCharSequence.length);
          paramListPreference.setSummary(paramListPreference.getEntries()[0]);
          return;
        }
      }
      catch (RemoteException localRemoteException)
      {
        return;
      }
      i++;
    }
  }

  private void updateAppProcessLimitOptions()
  {
    while (true)
    {
      int j;
      try
      {
        int i = ActivityManagerNative.getDefault().getProcessLimit();
        CharSequence[] arrayOfCharSequence = this.mAppProcessLimit.getEntryValues();
        j = 0;
        if (j < arrayOfCharSequence.length)
        {
          if (Integer.parseInt(arrayOfCharSequence[j].toString()) >= i)
          {
            if (j != 0)
              this.mHaveDebugSettings = true;
            this.mAppProcessLimit.setValueIndex(j);
            this.mAppProcessLimit.setSummary(this.mAppProcessLimit.getEntries()[j]);
          }
        }
        else
        {
          this.mAppProcessLimit.setValueIndex(0);
          this.mAppProcessLimit.setSummary(this.mAppProcessLimit.getEntries()[0]);
          return;
        }
      }
      catch (RemoteException localRemoteException)
      {
        return;
      }
      j++;
    }
  }

  private void updateBugreportOptions()
  {
    if ("user".equals(Build.TYPE))
    {
      ContentResolver localContentResolver = getActivity().getContentResolver();
      if (Settings.Global.getInt(localContentResolver, "adb_enabled", 0) != 0);
      for (int i = 1; i != 0; i = 0)
      {
        this.mBugreport.setEnabled(true);
        this.mBugreportInPower.setEnabled(true);
        return;
      }
      this.mBugreport.setEnabled(false);
      this.mBugreportInPower.setEnabled(false);
      this.mBugreportInPower.setChecked(false);
      Settings.Secure.putInt(localContentResolver, "bugreport_in_power_menu", 0);
      return;
    }
    this.mBugreportInPower.setEnabled(true);
  }

  private void updateCpuUsageOptions()
  {
    CheckBoxPreference localCheckBoxPreference = this.mShowCpuUsage;
    int i = Settings.Global.getInt(getActivity().getContentResolver(), "show_processes", 0);
    boolean bool = false;
    if (i != 0)
      bool = true;
    updateCheckBox(localCheckBoxPreference, bool);
  }

  private void updateDebugHwOverdrawOptions()
  {
    String str = SystemProperties.get("debug.hwui.overdraw");
    if (str == null)
      str = "";
    CharSequence[] arrayOfCharSequence = this.mDebugHwOverdraw.getEntryValues();
    for (int i = 0; i < arrayOfCharSequence.length; i++)
      if (str.contentEquals(arrayOfCharSequence[i]))
      {
        this.mDebugHwOverdraw.setValueIndex(i);
        this.mDebugHwOverdraw.setSummary(this.mDebugHwOverdraw.getEntries()[i]);
        return;
      }
    this.mDebugHwOverdraw.setValueIndex(0);
    this.mDebugHwOverdraw.setSummary(this.mDebugHwOverdraw.getEntries()[0]);
  }

  private void updateDebugLayoutOptions()
  {
    updateCheckBox(this.mDebugLayout, SystemProperties.getBoolean("debug.layout", false));
  }

  private void updateDebuggerOptions()
  {
    this.mDebugApp = Settings.Global.getString(getActivity().getContentResolver(), "debug_app");
    CheckBoxPreference localCheckBoxPreference = this.mWaitForDebugger;
    boolean bool;
    if (Settings.Global.getInt(getActivity().getContentResolver(), "wait_for_debugger", 0) != 0)
      bool = true;
    while (true)
    {
      updateCheckBox(localCheckBoxPreference, bool);
      if ((this.mDebugApp != null) && (this.mDebugApp.length() > 0))
        try
        {
          ApplicationInfo localApplicationInfo = getActivity().getPackageManager().getApplicationInfo(this.mDebugApp, 512);
          CharSequence localCharSequence = getActivity().getPackageManager().getApplicationLabel(localApplicationInfo);
          String str2;
          if (localCharSequence != null)
            str2 = localCharSequence.toString();
          for (str1 = str2; ; str1 = this.mDebugApp)
          {
            this.mDebugAppPref.setSummary(getResources().getString(2131429024, new Object[] { str1 }));
            this.mWaitForDebugger.setEnabled(true);
            this.mHaveDebugSettings = true;
            return;
            bool = false;
            break;
          }
        }
        catch (PackageManager.NameNotFoundException localNameNotFoundException)
        {
          while (true)
            String str1 = this.mDebugApp;
        }
    }
    this.mDebugAppPref.setSummary(getResources().getString(2131429023));
    this.mWaitForDebugger.setEnabled(false);
  }

  private void updateFlingerOptions()
  {
    boolean bool1 = true;
    try
    {
      IBinder localIBinder = ServiceManager.getService("SurfaceFlinger");
      Parcel localParcel1;
      Parcel localParcel2;
      boolean bool2;
      CheckBoxPreference localCheckBoxPreference2;
      if (localIBinder != null)
      {
        localParcel1 = Parcel.obtain();
        localParcel2 = Parcel.obtain();
        localParcel1.writeInterfaceToken("android.ui.ISurfaceComposer");
        localIBinder.transact(1010, localParcel1, localParcel2, 0);
        localParcel2.readInt();
        localParcel2.readInt();
        int i = localParcel2.readInt();
        CheckBoxPreference localCheckBoxPreference1 = this.mShowScreenUpdates;
        if (i == 0)
          break label129;
        bool2 = bool1;
        updateCheckBox(localCheckBoxPreference1, bool2);
        localParcel2.readInt();
        int j = localParcel2.readInt();
        localCheckBoxPreference2 = this.mDisableOverlays;
        if (j == 0)
          break label135;
      }
      while (true)
      {
        updateCheckBox(localCheckBoxPreference2, bool1);
        localParcel2.recycle();
        localParcel1.recycle();
        return;
        label129: bool2 = false;
        break;
        label135: bool1 = false;
      }
    }
    catch (RemoteException localRemoteException)
    {
    }
  }

  private void updateForceRtlOptions()
  {
    CheckBoxPreference localCheckBoxPreference = this.mForceRtlLayout;
    int i = Settings.Global.getInt(getActivity().getContentResolver(), "debug.force_rtl", 0);
    boolean bool = false;
    if (i != 0)
      bool = true;
    updateCheckBox(localCheckBoxPreference, bool);
  }

  private void updateHardwareUiOptions()
  {
    updateCheckBox(this.mForceHardwareUi, SystemProperties.getBoolean("persist.sys.ui.hw", false));
  }

  private void updateHdcpValues()
  {
    ListPreference localListPreference = (ListPreference)findPreference("hdcp_checking");
    String str;
    String[] arrayOfString1;
    String[] arrayOfString2;
    int i;
    if (localListPreference != null)
    {
      str = SystemProperties.get("persist.sys.hdcp_checking");
      arrayOfString1 = getResources().getStringArray(2131165256);
      arrayOfString2 = getResources().getStringArray(2131165257);
      i = 1;
    }
    for (int j = 0; ; j++)
      if (j < arrayOfString1.length)
      {
        if (str.equals(arrayOfString1[j]))
          i = j;
      }
      else
      {
        localListPreference.setValue(arrayOfString1[i]);
        localListPreference.setSummary(arrayOfString2[i]);
        localListPreference.setOnPreferenceChangeListener(this);
        return;
      }
  }

  private void updateImmediatelyDestroyActivitiesOptions()
  {
    CheckBoxPreference localCheckBoxPreference = this.mImmediatelyDestroyActivities;
    int i = Settings.Global.getInt(getActivity().getContentResolver(), "always_finish_activities", 0);
    boolean bool = false;
    if (i != 0)
      bool = true;
    updateCheckBox(localCheckBoxPreference, bool);
  }

  private void updateMsaaOptions()
  {
    updateCheckBox(this.mForceMsaa, SystemProperties.getBoolean("debug.egl.force_msaa", false));
  }

  private void updateOpenGLTracesOptions()
  {
    String str = SystemProperties.get("debug.egl.trace");
    if (str == null)
      str = "";
    CharSequence[] arrayOfCharSequence = this.mOpenGLTraces.getEntryValues();
    for (int i = 0; i < arrayOfCharSequence.length; i++)
      if (str.contentEquals(arrayOfCharSequence[i]))
      {
        this.mOpenGLTraces.setValueIndex(i);
        this.mOpenGLTraces.setSummary(this.mOpenGLTraces.getEntries()[i]);
        return;
      }
    this.mOpenGLTraces.setValueIndex(0);
    this.mOpenGLTraces.setSummary(this.mOpenGLTraces.getEntries()[0]);
  }

  private void updateOverlayDisplayDevicesOptions()
  {
    String str = Settings.Global.getString(getActivity().getContentResolver(), "overlay_display_devices");
    if (str == null)
      str = "";
    CharSequence[] arrayOfCharSequence = this.mOverlayDisplayDevices.getEntryValues();
    for (int i = 0; i < arrayOfCharSequence.length; i++)
      if (str.contentEquals(arrayOfCharSequence[i]))
      {
        this.mOverlayDisplayDevices.setValueIndex(i);
        this.mOverlayDisplayDevices.setSummary(this.mOverlayDisplayDevices.getEntries()[i]);
        return;
      }
    this.mOverlayDisplayDevices.setValueIndex(0);
    this.mOverlayDisplayDevices.setSummary(this.mOverlayDisplayDevices.getEntries()[0]);
  }

  private void updatePasswordSummary()
  {
    try
    {
      if (this.mBackupManager.hasBackupPassword())
      {
        this.mPassword.setSummary(2131428919);
        return;
      }
      this.mPassword.setSummary(2131428918);
      return;
    }
    catch (RemoteException localRemoteException)
    {
    }
  }

  private void updatePointerLocationOptions()
  {
    CheckBoxPreference localCheckBoxPreference = this.mPointerLocation;
    int i = Settings.System.getInt(getActivity().getContentResolver(), "pointer_location", 0);
    boolean bool = false;
    if (i != 0)
      bool = true;
    updateCheckBox(localCheckBoxPreference, bool);
  }

  private void updateRuntimeValue()
  {
    ListPreference localListPreference = (ListPreference)findPreference("select_runtime");
    String str;
    String[] arrayOfString1;
    String[] arrayOfString2;
    if (localListPreference != null)
    {
      str = currentRuntimeValue();
      arrayOfString1 = getResources().getStringArray(2131165253);
      arrayOfString2 = getResources().getStringArray(2131165254);
    }
    for (int i = 0; ; i++)
    {
      int j = arrayOfString1.length;
      int k = 0;
      if (i < j)
      {
        if (str.equals(arrayOfString1[i]))
          k = i;
      }
      else
      {
        localListPreference.setValue(arrayOfString1[k]);
        localListPreference.setSummary(arrayOfString2[k]);
        localListPreference.setOnPreferenceChangeListener(this);
        return;
      }
    }
  }

  private void updateShowAllANRsOptions()
  {
    CheckBoxPreference localCheckBoxPreference = this.mShowAllANRs;
    int i = Settings.Secure.getInt(getActivity().getContentResolver(), "anr_show_background", 0);
    boolean bool = false;
    if (i != 0)
      bool = true;
    updateCheckBox(localCheckBoxPreference, bool);
  }

  private void updateShowHwLayersUpdatesOptions()
  {
    updateCheckBox(this.mShowHwLayersUpdates, SystemProperties.getBoolean("debug.hwui.show_layers_updates", false));
  }

  private void updateShowHwScreenUpdatesOptions()
  {
    updateCheckBox(this.mShowHwScreenUpdates, SystemProperties.getBoolean("debug.hwui.show_dirty_regions", false));
  }

  private void updateShowNonRectClipOptions()
  {
    String str = SystemProperties.get("debug.hwui.show_non_rect_clip");
    if (str == null)
      str = "hide";
    CharSequence[] arrayOfCharSequence = this.mShowNonRectClip.getEntryValues();
    for (int i = 0; i < arrayOfCharSequence.length; i++)
      if (str.contentEquals(arrayOfCharSequence[i]))
      {
        this.mShowNonRectClip.setValueIndex(i);
        this.mShowNonRectClip.setSummary(this.mShowNonRectClip.getEntries()[i]);
        return;
      }
    this.mShowNonRectClip.setValueIndex(0);
    this.mShowNonRectClip.setSummary(this.mShowNonRectClip.getEntries()[0]);
  }

  private void updateShowTouchesOptions()
  {
    CheckBoxPreference localCheckBoxPreference = this.mShowTouches;
    int i = Settings.System.getInt(getActivity().getContentResolver(), "show_touches", 0);
    boolean bool = false;
    if (i != 0)
      bool = true;
    updateCheckBox(localCheckBoxPreference, bool);
  }

  private void updateStrictModeVisualOptions()
  {
    int i = 1;
    CheckBoxPreference localCheckBoxPreference = this.mStrictMode;
    if (currentStrictModeActiveIndex() == i);
    while (true)
    {
      updateCheckBox(localCheckBoxPreference, i);
      return;
      int j = 0;
    }
  }

  private void updateTrackFrameTimeOptions()
  {
    String str = SystemProperties.get("debug.hwui.profile");
    if (str == null)
      str = "";
    CharSequence[] arrayOfCharSequence = this.mTrackFrameTime.getEntryValues();
    for (int i = 0; i < arrayOfCharSequence.length; i++)
      if (str.contentEquals(arrayOfCharSequence[i]))
      {
        this.mTrackFrameTime.setValueIndex(i);
        this.mTrackFrameTime.setSummary(this.mTrackFrameTime.getEntries()[i]);
        return;
      }
    this.mTrackFrameTime.setValueIndex(0);
    this.mTrackFrameTime.setSummary(this.mTrackFrameTime.getEntries()[0]);
  }

  private void updateVerifyAppsOverUsbOptions()
  {
    int i = 1;
    CheckBoxPreference localCheckBoxPreference = this.mVerifyAppsOverUsb;
    if (Settings.Global.getInt(getActivity().getContentResolver(), "verifier_verify_adb_installs", i) != 0);
    while (true)
    {
      updateCheckBox(localCheckBoxPreference, i);
      this.mVerifyAppsOverUsb.setEnabled(enableVerifierSetting());
      return;
      int j = 0;
    }
  }

  private void updateWifiDisplayCertificationOptions()
  {
    CheckBoxPreference localCheckBoxPreference = this.mWifiDisplayCertification;
    int i = Settings.Global.getInt(getActivity().getContentResolver(), "wifi_display_certification_on", 0);
    boolean bool = false;
    if (i != 0)
      bool = true;
    updateCheckBox(localCheckBoxPreference, bool);
  }

  private void writeAnimationScaleOption(int paramInt, ListPreference paramListPreference, Object paramObject)
  {
    if (paramObject != null);
    try
    {
      for (float f = Float.parseFloat(paramObject.toString()); ; f = 1.0F)
      {
        this.mWindowManager.setAnimationScale(paramInt, f);
        updateAnimationScaleValue(paramInt, paramListPreference);
        return;
      }
    }
    catch (RemoteException localRemoteException)
    {
    }
  }

  private void writeAppProcessLimitOptions(Object paramObject)
  {
    if (paramObject != null);
    try
    {
      for (int i = Integer.parseInt(paramObject.toString()); ; i = -1)
      {
        ActivityManagerNative.getDefault().setProcessLimit(i);
        updateAppProcessLimitOptions();
        return;
      }
    }
    catch (RemoteException localRemoteException)
    {
    }
  }

  private void writeBtHciSnoopLogOptions()
  {
    BluetoothAdapter.getDefaultAdapter().configHciSnoopLog(this.mBtHciSnoopLog.isChecked());
    ContentResolver localContentResolver = getActivity().getContentResolver();
    if (this.mBtHciSnoopLog.isChecked());
    for (int i = 1; ; i = 0)
    {
      Settings.Secure.putInt(localContentResolver, "bluetooth_hci_log", i);
      return;
    }
  }

  private void writeCpuUsageOptions()
  {
    boolean bool = this.mShowCpuUsage.isChecked();
    ContentResolver localContentResolver = getActivity().getContentResolver();
    if (bool);
    Intent localIntent;
    for (int i = 1; ; i = 0)
    {
      Settings.Global.putInt(localContentResolver, "show_processes", i);
      localIntent = new Intent().setClassName("com.android.systemui", "com.android.systemui.LoadAverageService");
      if (!bool)
        break;
      getActivity().startService(localIntent);
      return;
    }
    getActivity().stopService(localIntent);
  }

  private void writeDebugHwOverdrawOptions(Object paramObject)
  {
    if (paramObject == null);
    for (String str = ""; ; str = paramObject.toString())
    {
      SystemProperties.set("debug.hwui.overdraw", str);
      pokeSystemProperties();
      updateDebugHwOverdrawOptions();
      return;
    }
  }

  private void writeDebugLayoutOptions()
  {
    if (this.mDebugLayout.isChecked());
    for (String str = "true"; ; str = "false")
    {
      SystemProperties.set("debug.layout", str);
      pokeSystemProperties();
      return;
    }
  }

  private void writeDebuggerOptions()
  {
    try
    {
      ActivityManagerNative.getDefault().setDebugApp(this.mDebugApp, this.mWaitForDebugger.isChecked(), true);
      return;
    }
    catch (RemoteException localRemoteException)
    {
    }
  }

  private void writeDisableOverlaysOption()
  {
    try
    {
      IBinder localIBinder = ServiceManager.getService("SurfaceFlinger");
      if (localIBinder != null)
      {
        Parcel localParcel = Parcel.obtain();
        localParcel.writeInterfaceToken("android.ui.ISurfaceComposer");
        boolean bool = this.mDisableOverlays.isChecked();
        int i = 0;
        if (bool)
          i = 1;
        localParcel.writeInt(i);
        localIBinder.transact(1008, localParcel, null, 0);
        localParcel.recycle();
        updateFlingerOptions();
      }
      return;
    }
    catch (RemoteException localRemoteException)
    {
    }
  }

  private void writeForceRtlOptions()
  {
    boolean bool = this.mForceRtlLayout.isChecked();
    ContentResolver localContentResolver = getActivity().getContentResolver();
    int i;
    if (bool)
    {
      i = 1;
      Settings.Global.putInt(localContentResolver, "debug.force_rtl", i);
      if (!bool)
        break label70;
    }
    label70: for (String str = "1"; ; str = "0")
    {
      SystemProperties.set("debug.force_rtl", str);
      LocalePicker.updateLocale(getActivity().getResources().getConfiguration().locale);
      return;
      i = 0;
      break;
    }
  }

  private void writeHardwareUiOptions()
  {
    if (this.mForceHardwareUi.isChecked());
    for (String str = "true"; ; str = "false")
    {
      SystemProperties.set("persist.sys.ui.hw", str);
      pokeSystemProperties();
      return;
    }
  }

  private void writeImmediatelyDestroyActivitiesOptions()
  {
    try
    {
      ActivityManagerNative.getDefault().setAlwaysFinish(this.mImmediatelyDestroyActivities.isChecked());
      return;
    }
    catch (RemoteException localRemoteException)
    {
    }
  }

  private void writeMsaaOptions()
  {
    if (this.mForceMsaa.isChecked());
    for (String str = "true"; ; str = "false")
    {
      SystemProperties.set("debug.egl.force_msaa", str);
      pokeSystemProperties();
      return;
    }
  }

  private void writeOpenGLTracesOptions(Object paramObject)
  {
    if (paramObject == null);
    for (String str = ""; ; str = paramObject.toString())
    {
      SystemProperties.set("debug.egl.trace", str);
      pokeSystemProperties();
      updateOpenGLTracesOptions();
      return;
    }
  }

  private void writeOverlayDisplayDevicesOptions(Object paramObject)
  {
    Settings.Global.putString(getActivity().getContentResolver(), "overlay_display_devices", (String)paramObject);
    updateOverlayDisplayDevicesOptions();
  }

  private void writePointerLocationOptions()
  {
    ContentResolver localContentResolver = getActivity().getContentResolver();
    if (this.mPointerLocation.isChecked());
    for (int i = 1; ; i = 0)
    {
      Settings.System.putInt(localContentResolver, "pointer_location", i);
      return;
    }
  }

  private void writeShowAllANRsOptions()
  {
    ContentResolver localContentResolver = getActivity().getContentResolver();
    if (this.mShowAllANRs.isChecked());
    for (int i = 1; ; i = 0)
    {
      Settings.Secure.putInt(localContentResolver, "anr_show_background", i);
      return;
    }
  }

  private void writeShowHwLayersUpdatesOptions()
  {
    if (this.mShowHwLayersUpdates.isChecked());
    for (String str = "true"; ; str = null)
    {
      SystemProperties.set("debug.hwui.show_layers_updates", str);
      pokeSystemProperties();
      return;
    }
  }

  private void writeShowHwScreenUpdatesOptions()
  {
    if (this.mShowHwScreenUpdates.isChecked());
    for (String str = "true"; ; str = null)
    {
      SystemProperties.set("debug.hwui.show_dirty_regions", str);
      pokeSystemProperties();
      return;
    }
  }

  private void writeShowNonRectClipOptions(Object paramObject)
  {
    if (paramObject == null);
    for (String str = ""; ; str = paramObject.toString())
    {
      SystemProperties.set("debug.hwui.show_non_rect_clip", str);
      pokeSystemProperties();
      updateShowNonRectClipOptions();
      return;
    }
  }

  private void writeShowTouchesOptions()
  {
    ContentResolver localContentResolver = getActivity().getContentResolver();
    if (this.mShowTouches.isChecked());
    for (int i = 1; ; i = 0)
    {
      Settings.System.putInt(localContentResolver, "show_touches", i);
      return;
    }
  }

  private void writeShowUpdatesOption()
  {
    try
    {
      IBinder localIBinder = ServiceManager.getService("SurfaceFlinger");
      if (localIBinder != null)
      {
        Parcel localParcel = Parcel.obtain();
        localParcel.writeInterfaceToken("android.ui.ISurfaceComposer");
        boolean bool = this.mShowScreenUpdates.isChecked();
        int i = 0;
        if (bool)
          i = 1;
        localParcel.writeInt(i);
        localIBinder.transact(1002, localParcel, null, 0);
        localParcel.recycle();
        updateFlingerOptions();
      }
      return;
    }
    catch (RemoteException localRemoteException)
    {
    }
  }

  private void writeStrictModeVisualOptions()
  {
    try
    {
      IWindowManager localIWindowManager = this.mWindowManager;
      if (this.mStrictMode.isChecked());
      for (String str = "1"; ; str = "")
      {
        localIWindowManager.setStrictModeVisualIndicatorPreference(str);
        return;
      }
    }
    catch (RemoteException localRemoteException)
    {
    }
  }

  private void writeTrackFrameTimeOptions(Object paramObject)
  {
    if (paramObject == null);
    for (String str = ""; ; str = paramObject.toString())
    {
      SystemProperties.set("debug.hwui.profile", str);
      pokeSystemProperties();
      updateTrackFrameTimeOptions();
      return;
    }
  }

  private void writeVerifyAppsOverUsbOptions()
  {
    ContentResolver localContentResolver = getActivity().getContentResolver();
    if (this.mVerifyAppsOverUsb.isChecked());
    for (int i = 1; ; i = 0)
    {
      Settings.Global.putInt(localContentResolver, "verifier_verify_adb_installs", i);
      return;
    }
  }

  private void writeWifiDisplayCertificationOptions()
  {
    ContentResolver localContentResolver = getActivity().getContentResolver();
    if (this.mWifiDisplayCertification.isChecked());
    for (int i = 1; ; i = 0)
    {
      Settings.Global.putInt(localContentResolver, "wifi_display_certification_on", i);
      return;
    }
  }

  void filterRuntimeOptions(Preference paramPreference)
  {
    ListPreference localListPreference = (ListPreference)paramPreference;
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    String[] arrayOfString1 = getResources().getStringArray(2131165253);
    String[] arrayOfString2 = getResources().getStringArray(2131165254);
    for (int i = 0; i < arrayOfString1.length; i++)
    {
      String str1 = arrayOfString1[i];
      String str2 = arrayOfString2[i];
      if (new File("/system/lib/" + str1).exists())
      {
        localArrayList1.add(str1);
        localArrayList2.add(str2);
      }
    }
    int j = localArrayList1.size();
    if (j <= 1)
    {
      removePreference(paramPreference);
      return;
    }
    localListPreference.setEntryValues((CharSequence[])localArrayList1.toArray(new String[j]));
    localListPreference.setEntries((CharSequence[])localArrayList2.toArray(new String[j]));
  }

  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    Activity localActivity = getActivity();
    this.mEnabledSwitch = new Switch(localActivity);
    int i = localActivity.getResources().getDimensionPixelSize(2131558402);
    this.mEnabledSwitch.setPaddingRelative(0, 0, i, 0);
    if (this.mUnavailable)
    {
      this.mEnabledSwitch.setEnabled(false);
      return;
    }
    this.mEnabledSwitch.setOnCheckedChangeListener(this);
  }

  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    if (paramInt1 == 1000)
    {
      if (paramInt2 == -1)
      {
        this.mDebugApp = paramIntent.getAction();
        writeDebuggerOptions();
        updateDebuggerOptions();
      }
      return;
    }
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
  }

  public void onCheckedChanged(CompoundButton paramCompoundButton, boolean paramBoolean)
  {
    if ((paramCompoundButton == this.mEnabledSwitch) && (paramBoolean != this.mLastEnabledState))
    {
      if (paramBoolean)
      {
        this.mDialogClicked = false;
        if (this.mEnableDialog != null)
          dismissDialogs();
        this.mEnableDialog = new AlertDialog.Builder(getActivity()).setMessage(getActivity().getResources().getString(2131428602)).setTitle(2131428601).setIconAttribute(16843605).setPositiveButton(17039379, this).setNegativeButton(17039369, this).show();
        this.mEnableDialog.setOnDismissListener(this);
      }
    }
    else
      return;
    resetDangerousOptions();
    Settings.Global.putInt(getActivity().getContentResolver(), "development_settings_enabled", 0);
    this.mLastEnabledState = paramBoolean;
    setPrefsEnabledState(this.mLastEnabledState);
  }

  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    if (paramDialogInterface == this.mAdbDialog)
      if (paramInt == -1)
      {
        this.mDialogClicked = true;
        Settings.Global.putInt(getActivity().getContentResolver(), "adb_enabled", 1);
        this.mVerifyAppsOverUsb.setEnabled(true);
        updateVerifyAppsOverUsbOptions();
        updateBugreportOptions();
      }
    do
    {
      do
      {
        return;
        this.mEnableAdb.setChecked(false);
        return;
        if (paramDialogInterface != this.mAdbKeysDialog)
          break;
      }
      while (paramInt != -1);
      try
      {
        IUsbManager.Stub.asInterface(ServiceManager.getService("usb")).clearUsbDebuggingKeys();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("DevelopmentSettings", "Unable to clear adb keys", localRemoteException);
        return;
      }
    }
    while (paramDialogInterface != this.mEnableDialog);
    if (paramInt == -1)
    {
      this.mDialogClicked = true;
      Settings.Global.putInt(getActivity().getContentResolver(), "development_settings_enabled", 1);
      this.mLastEnabledState = true;
      setPrefsEnabledState(this.mLastEnabledState);
      return;
    }
    this.mEnabledSwitch.setChecked(false);
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mWindowManager = IWindowManager.Stub.asInterface(ServiceManager.getService("window"));
    this.mBackupManager = IBackupManager.Stub.asInterface(ServiceManager.getService("backup"));
    this.mDpm = ((DevicePolicyManager)getActivity().getSystemService("device_policy"));
    if (Process.myUserHandle().getIdentifier() != 0)
    {
      this.mUnavailable = true;
      setPreferenceScreen(new PreferenceScreen(getActivity(), null));
      return;
    }
    addPreferencesFromResource(2131034125);
    PreferenceGroup localPreferenceGroup = (PreferenceGroup)findPreference("debug_debugging_category");
    this.mEnableAdb = findAndInitCheckboxPref("enable_adb");
    this.mClearAdbKeys = findPreference("clear_adb_keys");
    if ((!SystemProperties.getBoolean("ro.adb.secure", false)) && (localPreferenceGroup != null))
      localPreferenceGroup.removePreference(this.mClearAdbKeys);
    this.mEnableTerminal = findAndInitCheckboxPref("enable_terminal");
    if (!isPackageInstalled(getActivity(), "com.android.terminal"))
    {
      localPreferenceGroup.removePreference(this.mEnableTerminal);
      this.mEnableTerminal = null;
    }
    this.mBugreport = findPreference("bugreport");
    this.mBugreportInPower = findAndInitCheckboxPref("bugreport_in_power");
    this.mKeepScreenOn = findAndInitCheckboxPref("keep_screen_on");
    this.mBtHciSnoopLog = findAndInitCheckboxPref("bt_hci_snoop_log");
    this.mAllowMockLocation = findAndInitCheckboxPref("allow_mock_location");
    this.mPassword = ((PreferenceScreen)findPreference("local_backup_password"));
    this.mAllPrefs.add(this.mPassword);
    if (!Process.myUserHandle().equals(UserHandle.OWNER))
    {
      disableForUser(this.mEnableAdb);
      disableForUser(this.mClearAdbKeys);
      disableForUser(this.mEnableTerminal);
      disableForUser(this.mPassword);
    }
    this.mDebugAppPref = findPreference("debug_app");
    this.mAllPrefs.add(this.mDebugAppPref);
    this.mWaitForDebugger = findAndInitCheckboxPref("wait_for_debugger");
    this.mVerifyAppsOverUsb = findAndInitCheckboxPref("verify_apps_over_usb");
    if (!showVerifierSetting())
    {
      if (localPreferenceGroup == null)
        break label750;
      localPreferenceGroup.removePreference(this.mVerifyAppsOverUsb);
    }
    while (true)
    {
      this.mStrictMode = findAndInitCheckboxPref("strict_mode");
      this.mPointerLocation = findAndInitCheckboxPref("pointer_location");
      this.mShowTouches = findAndInitCheckboxPref("show_touches");
      this.mShowScreenUpdates = findAndInitCheckboxPref("show_screen_updates");
      this.mDisableOverlays = findAndInitCheckboxPref("disable_overlays");
      this.mShowCpuUsage = findAndInitCheckboxPref("show_cpu_usage");
      this.mForceHardwareUi = findAndInitCheckboxPref("force_hw_ui");
      this.mForceMsaa = findAndInitCheckboxPref("force_msaa");
      this.mTrackFrameTime = addListPreference("track_frame_time");
      this.mShowNonRectClip = addListPreference("show_non_rect_clip");
      this.mShowHwScreenUpdates = findAndInitCheckboxPref("show_hw_screen_udpates");
      this.mShowHwLayersUpdates = findAndInitCheckboxPref("show_hw_layers_udpates");
      this.mDebugLayout = findAndInitCheckboxPref("debug_layout");
      this.mForceRtlLayout = findAndInitCheckboxPref("force_rtl_layout_all_locales");
      this.mDebugHwOverdraw = addListPreference("debug_hw_overdraw");
      this.mWifiDisplayCertification = findAndInitCheckboxPref("wifi_display_certification");
      this.mWindowAnimationScale = addListPreference("window_animation_scale");
      this.mTransitionAnimationScale = addListPreference("transition_animation_scale");
      this.mAnimatorDurationScale = addListPreference("animator_duration_scale");
      this.mOverlayDisplayDevices = addListPreference("overlay_display_devices");
      this.mOpenGLTraces = addListPreference("enable_opengl_traces");
      this.mImmediatelyDestroyActivities = ((CheckBoxPreference)findPreference("immediately_destroy_activities"));
      this.mAllPrefs.add(this.mImmediatelyDestroyActivities);
      this.mResetCbPrefs.add(this.mImmediatelyDestroyActivities);
      this.mAppProcessLimit = addListPreference("app_process_limit");
      this.mShowAllANRs = ((CheckBoxPreference)findPreference("show_all_anrs"));
      this.mAllPrefs.add(this.mShowAllANRs);
      this.mResetCbPrefs.add(this.mShowAllANRs);
      Preference localPreference1 = findPreference("select_runtime");
      if (localPreference1 != null)
      {
        this.mAllPrefs.add(localPreference1);
        filterRuntimeOptions(localPreference1);
      }
      Preference localPreference2 = findPreference("hdcp_checking");
      if (localPreference2 == null)
        break;
      this.mAllPrefs.add(localPreference2);
      removePreferenceForProduction(localPreference2);
      return;
      label750: this.mVerifyAppsOverUsb.setEnabled(false);
    }
  }

  public void onDestroy()
  {
    dismissDialogs();
    super.onDestroy();
  }

  public void onDismiss(DialogInterface paramDialogInterface)
  {
    if (paramDialogInterface == this.mAdbDialog)
    {
      if (!this.mDialogClicked)
        this.mEnableAdb.setChecked(false);
      this.mAdbDialog = null;
    }
    while (paramDialogInterface != this.mEnableDialog)
      return;
    if (!this.mDialogClicked)
      this.mEnabledSwitch.setChecked(false);
    this.mEnableDialog = null;
  }

  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    if ("select_runtime".equals(paramPreference.getKey()))
    {
      String str1 = VMRuntime.getRuntime().vmLibrary();
      final String str2 = paramObject.toString();
      if (!str2.equals(str1))
      {
        final Activity localActivity = getActivity();
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(getActivity());
        localBuilder.setMessage(localActivity.getResources().getString(2131428593, new Object[] { str1, str2 }));
        localBuilder.setPositiveButton(17039370, new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            SystemProperties.set("persist.sys.dalvik.vm.lib", str2);
            DevelopmentSettings.this.pokeSystemProperties();
            ((PowerManager)localActivity.getSystemService("power")).reboot(null);
          }
        });
        localBuilder.setNegativeButton(17039360, new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            DevelopmentSettings.this.updateRuntimeValue();
          }
        });
        localBuilder.show();
      }
      return true;
    }
    if ("hdcp_checking".equals(paramPreference.getKey()))
    {
      SystemProperties.set("persist.sys.hdcp_checking", paramObject.toString());
      updateHdcpValues();
      pokeSystemProperties();
      return true;
    }
    if (paramPreference == this.mWindowAnimationScale)
    {
      writeAnimationScaleOption(0, this.mWindowAnimationScale, paramObject);
      return true;
    }
    if (paramPreference == this.mTransitionAnimationScale)
    {
      writeAnimationScaleOption(1, this.mTransitionAnimationScale, paramObject);
      return true;
    }
    if (paramPreference == this.mAnimatorDurationScale)
    {
      writeAnimationScaleOption(2, this.mAnimatorDurationScale, paramObject);
      return true;
    }
    if (paramPreference == this.mOverlayDisplayDevices)
    {
      writeOverlayDisplayDevicesOptions(paramObject);
      return true;
    }
    if (paramPreference == this.mOpenGLTraces)
    {
      writeOpenGLTracesOptions(paramObject);
      return true;
    }
    if (paramPreference == this.mTrackFrameTime)
    {
      writeTrackFrameTimeOptions(paramObject);
      return true;
    }
    if (paramPreference == this.mDebugHwOverdraw)
    {
      writeDebugHwOverdrawOptions(paramObject);
      return true;
    }
    if (paramPreference == this.mShowNonRectClip)
    {
      writeShowNonRectClipOptions(paramObject);
      return true;
    }
    if (paramPreference == this.mAppProcessLimit)
    {
      writeAppProcessLimitOptions(paramObject);
      return true;
    }
    return false;
  }

  public boolean onPreferenceTreeClick(PreferenceScreen paramPreferenceScreen, Preference paramPreference)
  {
    int i = 1;
    if (Utils.isMonkeyRunning())
      return false;
    if (paramPreference == this.mEnableAdb)
    {
      if (this.mEnableAdb.isChecked())
      {
        this.mDialogClicked = false;
        if (this.mAdbDialog != null)
          dismissDialogs();
        this.mAdbDialog = new AlertDialog.Builder(getActivity()).setMessage(getActivity().getResources().getString(2131428599)).setTitle(2131428598).setIconAttribute(16843605).setPositiveButton(17039379, this).setNegativeButton(17039369, this).show();
        this.mAdbDialog.setOnDismissListener(this);
        return false;
      }
      Settings.Global.putInt(getActivity().getContentResolver(), "adb_enabled", 0);
      this.mVerifyAppsOverUsb.setEnabled(false);
      this.mVerifyAppsOverUsb.setChecked(false);
      updateBugreportOptions();
      return false;
    }
    if (paramPreference == this.mClearAdbKeys)
    {
      if (this.mAdbKeysDialog != null)
        dismissDialogs();
      this.mAdbKeysDialog = new AlertDialog.Builder(getActivity()).setMessage(2131428600).setPositiveButton(17039370, this).setNegativeButton(17039360, null).show();
      return false;
    }
    if (paramPreference == this.mEnableTerminal)
    {
      PackageManager localPackageManager = getActivity().getPackageManager();
      if (this.mEnableTerminal.isChecked());
      while (true)
      {
        localPackageManager.setApplicationEnabledSetting("com.android.terminal", i, 0);
        return false;
        i = 0;
      }
    }
    if (paramPreference == this.mBugreportInPower)
    {
      ContentResolver localContentResolver3 = getActivity().getContentResolver();
      if (this.mBugreportInPower.isChecked());
      while (true)
      {
        Settings.Secure.putInt(localContentResolver3, "bugreport_in_power_menu", i);
        return false;
        i = 0;
      }
    }
    if (paramPreference == this.mKeepScreenOn)
    {
      ContentResolver localContentResolver2 = getActivity().getContentResolver();
      if (this.mKeepScreenOn.isChecked());
      for (int j = 3; ; j = 0)
      {
        Settings.Global.putInt(localContentResolver2, "stay_on_while_plugged_in", j);
        return false;
      }
    }
    if (paramPreference == this.mBtHciSnoopLog)
    {
      writeBtHciSnoopLogOptions();
      return false;
    }
    if (paramPreference == this.mAllowMockLocation)
    {
      ContentResolver localContentResolver1 = getActivity().getContentResolver();
      if (this.mAllowMockLocation.isChecked());
      while (true)
      {
        Settings.Secure.putInt(localContentResolver1, "mock_location", i);
        return false;
        i = 0;
      }
    }
    if (paramPreference == this.mDebugAppPref)
    {
      startActivityForResult(new Intent(getActivity(), AppPicker.class), 1000);
      return false;
    }
    if (paramPreference == this.mWaitForDebugger)
    {
      writeDebuggerOptions();
      return false;
    }
    if (paramPreference == this.mVerifyAppsOverUsb)
    {
      writeVerifyAppsOverUsbOptions();
      return false;
    }
    if (paramPreference == this.mStrictMode)
    {
      writeStrictModeVisualOptions();
      return false;
    }
    if (paramPreference == this.mPointerLocation)
    {
      writePointerLocationOptions();
      return false;
    }
    if (paramPreference == this.mShowTouches)
    {
      writeShowTouchesOptions();
      return false;
    }
    if (paramPreference == this.mShowScreenUpdates)
    {
      writeShowUpdatesOption();
      return false;
    }
    if (paramPreference == this.mDisableOverlays)
    {
      writeDisableOverlaysOption();
      return false;
    }
    if (paramPreference == this.mShowCpuUsage)
    {
      writeCpuUsageOptions();
      return false;
    }
    if (paramPreference == this.mImmediatelyDestroyActivities)
    {
      writeImmediatelyDestroyActivitiesOptions();
      return false;
    }
    if (paramPreference == this.mShowAllANRs)
    {
      writeShowAllANRsOptions();
      return false;
    }
    if (paramPreference == this.mForceHardwareUi)
    {
      writeHardwareUiOptions();
      return false;
    }
    if (paramPreference == this.mForceMsaa)
    {
      writeMsaaOptions();
      return false;
    }
    if (paramPreference == this.mShowHwScreenUpdates)
    {
      writeShowHwScreenUpdatesOptions();
      return false;
    }
    if (paramPreference == this.mShowHwLayersUpdates)
    {
      writeShowHwLayersUpdatesOptions();
      return false;
    }
    if (paramPreference == this.mDebugLayout)
    {
      writeDebugLayoutOptions();
      return false;
    }
    if (paramPreference == this.mForceRtlLayout)
    {
      writeForceRtlOptions();
      return false;
    }
    if (paramPreference == this.mWifiDisplayCertification)
    {
      writeWifiDisplayCertificationOptions();
      return false;
    }
    return super.onPreferenceTreeClick(paramPreferenceScreen, paramPreference);
  }

  public void onResume()
  {
    super.onResume();
    if (this.mUnavailable)
    {
      TextView localTextView = (TextView)getView().findViewById(16908292);
      getListView().setEmptyView(localTextView);
      if (localTextView != null)
        localTextView.setText(2131428581);
      return;
    }
    if (this.mDpm.getMaximumTimeToLock(null) > 0L)
      this.mDisabledPrefs.add(this.mKeepScreenOn);
    while (true)
    {
      int i = Settings.Global.getInt(getActivity().getContentResolver(), "development_settings_enabled", 0);
      boolean bool = false;
      if (i != 0)
        bool = true;
      this.mLastEnabledState = bool;
      this.mEnabledSwitch.setChecked(this.mLastEnabledState);
      setPrefsEnabledState(this.mLastEnabledState);
      if ((!this.mHaveDebugSettings) || (this.mLastEnabledState))
        break;
      Settings.Global.putInt(getActivity().getContentResolver(), "development_settings_enabled", 1);
      this.mLastEnabledState = true;
      this.mEnabledSwitch.setChecked(this.mLastEnabledState);
      setPrefsEnabledState(this.mLastEnabledState);
      return;
      this.mDisabledPrefs.remove(this.mKeepScreenOn);
    }
  }

  public void onStart()
  {
    super.onStart();
    Activity localActivity = getActivity();
    localActivity.getActionBar().setDisplayOptions(16, 16);
    localActivity.getActionBar().setCustomView(this.mEnabledSwitch, new ActionBar.LayoutParams(-2, -2, 8388629));
  }

  public void onStop()
  {
    super.onStop();
    Activity localActivity = getActivity();
    localActivity.getActionBar().setDisplayOptions(0, 16);
    localActivity.getActionBar().setCustomView(null);
  }

  void pokeSystemProperties()
  {
    if (!this.mDontPokeProperties)
      new SystemPropPoker().execute(new Void[0]);
  }

  void updateCheckBox(CheckBoxPreference paramCheckBoxPreference, boolean paramBoolean)
  {
    paramCheckBoxPreference.setChecked(paramBoolean);
    this.mHaveDebugSettings = (paramBoolean | this.mHaveDebugSettings);
  }

  static class SystemPropPoker extends AsyncTask<Void, Void, Void>
  {
    // ERROR //
    protected Void doInBackground(Void[] paramArrayOfVoid)
    {
      // Byte code:
      //   0: invokestatic 26	android/os/ServiceManager:listServices	()[Ljava/lang/String;
      //   3: astore_3
      //   4: aload_3
      //   5: arraylength
      //   6: istore 4
      //   8: iconst_0
      //   9: istore 5
      //   11: iload 5
      //   13: iload 4
      //   15: if_icmpge +52 -> 67
      //   18: aload_3
      //   19: iload 5
      //   21: aaload
      //   22: astore 6
      //   24: aload 6
      //   26: invokestatic 30	android/os/ServiceManager:checkService	(Ljava/lang/String;)Landroid/os/IBinder;
      //   29: astore 7
      //   31: aload 7
      //   33: ifnull +27 -> 60
      //   36: invokestatic 36	android/os/Parcel:obtain	()Landroid/os/Parcel;
      //   39: astore 8
      //   41: aload 7
      //   43: ldc 37
      //   45: aload 8
      //   47: aconst_null
      //   48: iconst_0
      //   49: invokeinterface 43 5 0
      //   54: pop
      //   55: aload 8
      //   57: invokevirtual 46	android/os/Parcel:recycle	()V
      //   60: iinc 5 1
      //   63: goto -52 -> 11
      //   66: astore_2
      //   67: aconst_null
      //   68: areturn
      //   69: astore 10
      //   71: ldc 48
      //   73: new 50	java/lang/StringBuilder
      //   76: dup
      //   77: invokespecial 51	java/lang/StringBuilder:<init>	()V
      //   80: ldc 53
      //   82: invokevirtual 57	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   85: aload 6
      //   87: invokevirtual 57	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   90: ldc 59
      //   92: invokevirtual 57	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   95: aload 10
      //   97: invokevirtual 62	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   100: invokevirtual 66	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   103: invokestatic 72	android/util/Log:i	(Ljava/lang/String;Ljava/lang/String;)I
      //   106: pop
      //   107: goto -52 -> 55
      //   110: astore 9
      //   112: goto -57 -> 55
      //
      // Exception table:
      //   from	to	target	type
      //   0	4	66	android/os/RemoteException
      //   41	55	69	java/lang/Exception
      //   41	55	110	android/os/RemoteException
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.DevelopmentSettings
 * JD-Core Version:    0.6.2
 */