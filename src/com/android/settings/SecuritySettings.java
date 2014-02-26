package com.android.settings;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog.Builder;
import android.app.Fragment;
import android.app.admin.DevicePolicyManager;
import android.content.ContentResolver;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
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
import android.security.KeyStore;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.android.internal.widget.LockPatternUtils;
import java.util.ArrayList;
import java.util.List;

public class SecuritySettings extends RestrictedSettingsFragment
  implements DialogInterface.OnClickListener, Preference.OnPreferenceChangeListener
{
  private CheckBoxPreference mBiometricWeakLiveliness;
  private ChooseLockSettingsHelper mChooseLockSettingsHelper;
  private DevicePolicyManager mDPM;
  private CheckBoxPreference mEnableKeyguardWidgets;
  private boolean mIsPrimary;
  private KeyStore mKeyStore;
  private ListPreference mLockAfter;
  private LockPatternUtils mLockPatternUtils;
  private Preference mNotificationAccess;
  private PackageManager mPM;
  private CheckBoxPreference mPowerButtonInstantlyLocks;
  private Preference mResetCredentials;
  private CheckBoxPreference mShowPassword;
  private CheckBoxPreference mToggleAppInstallation;
  private CheckBoxPreference mToggleVerifyApps;
  private CheckBoxPreference mVisiblePattern;
  private DialogInterface mWarnInstallApps;

  public SecuritySettings()
  {
    super(null);
  }

  private PreferenceScreen createPreferenceHierarchy()
  {
    PreferenceScreen localPreferenceScreen1 = getPreferenceScreen();
    if (localPreferenceScreen1 != null)
      localPreferenceScreen1.removeAll();
    addPreferencesFromResource(2131034145);
    PreferenceScreen localPreferenceScreen2 = getPreferenceScreen();
    int n;
    int i;
    label77: boolean bool1;
    label91: Preference localPreference3;
    label137: label176: label333: int k;
    label478: label485: PreferenceGroup localPreferenceGroup1;
    if (!this.mLockPatternUtils.isSecure())
      if (((UserManager)getSystemService("user")).getUsers(true).size() == 1)
      {
        n = 1;
        if ((n == 0) || (!this.mLockPatternUtils.isLockScreenDisabled()))
          break label671;
        i = 2131034149;
        addPreferencesFromResource(i);
        if (UserHandle.myUserId() != 0)
          break label786;
        bool1 = true;
        this.mIsPrimary = bool1;
        if (!this.mIsPrimary)
        {
          localPreference3 = findPreference("owner_info_settings");
          if (localPreference3 != null)
          {
            if (!UserManager.get(getActivity()).isLinkedUser())
              break label792;
            localPreference3.setTitle(2131427617);
          }
        }
        if (this.mIsPrimary);
        switch (this.mDPM.getStorageEncryptionStatus())
        {
        case 2:
        default:
          this.mLockAfter = ((ListPreference)localPreferenceScreen2.findPreference("lock_after_timeout"));
          if (this.mLockAfter != null)
          {
            setupLockAfterPreference();
            updateLockAfterPreferenceSummary();
          }
          this.mBiometricWeakLiveliness = ((CheckBoxPreference)localPreferenceScreen2.findPreference("biometric_weak_liveliness"));
          this.mVisiblePattern = ((CheckBoxPreference)localPreferenceScreen2.findPreference("visiblepattern"));
          this.mPowerButtonInstantlyLocks = ((CheckBoxPreference)localPreferenceScreen2.findPreference("power_button_instantly_locks"));
          if ((i == 2131034146) && (this.mLockPatternUtils.getKeyguardStoredPasswordQuality() != 65536))
          {
            PreferenceGroup localPreferenceGroup3 = (PreferenceGroup)localPreferenceScreen2.findPreference("security_category");
            if ((localPreferenceGroup3 != null) && (this.mVisiblePattern != null))
              localPreferenceGroup3.removePreference(localPreferenceScreen2.findPreference("visiblepattern"));
          }
          addPreferencesFromResource(2131034150);
          TelephonyManager localTelephonyManager = TelephonyManager.getDefault();
          if ((!this.mIsPrimary) || (!localTelephonyManager.hasIccCard()))
          {
            localPreferenceScreen2.removePreference(localPreferenceScreen2.findPreference("sim_lock"));
            this.mEnableKeyguardWidgets = ((CheckBoxPreference)localPreferenceScreen2.findPreference("keyguard_enable_widgets"));
            if (this.mEnableKeyguardWidgets != null)
            {
              if ((!ActivityManager.isLowRamDeviceStatic()) && (!this.mLockPatternUtils.isLockScreenDisabled()))
                break label855;
              PreferenceGroup localPreferenceGroup2 = (PreferenceGroup)localPreferenceScreen2.findPreference("security_category");
              if (localPreferenceGroup2 != null)
              {
                localPreferenceGroup2.removePreference(localPreferenceScreen2.findPreference("keyguard_enable_widgets"));
                this.mEnableKeyguardWidgets = null;
              }
            }
            this.mShowPassword = ((CheckBoxPreference)localPreferenceScreen2.findPreference("show_password"));
            this.mResetCredentials = localPreferenceScreen2.findPreference("reset_credentials");
            UserManager localUserManager = (UserManager)getActivity().getSystemService("user");
            this.mKeyStore = KeyStore.getInstance();
            if (localUserManager.hasUserRestriction("no_config_credentials"))
              break label943;
            Preference localPreference2 = localPreferenceScreen2.findPreference("credential_storage_type");
            if (!this.mKeyStore.isHardwareBacked())
              break label935;
            k = 2131428889;
            localPreference2.setSummary(k);
            localPreferenceGroup1 = (PreferenceGroup)localPreferenceScreen2.findPreference("device_admin_category");
            this.mToggleAppInstallation = ((CheckBoxPreference)findPreference("toggle_install_applications"));
            this.mToggleAppInstallation.setChecked(isNonMarketAppsAllowed());
            this.mToggleAppInstallation.setEnabled(this.mIsPrimary);
            this.mToggleVerifyApps = ((CheckBoxPreference)findPreference("toggle_verify_applications"));
            if ((!this.mIsPrimary) || (!showVerifierSetting()))
              break label972;
            if (!isVerifierInstalled())
              break label953;
            this.mToggleVerifyApps.setChecked(isVerifyAppsEnabled());
            label576: this.mNotificationAccess = findPreference("manage_notification_access");
            if (this.mNotificationAccess != null)
            {
              if (NotificationAccessSettings.getListenersCount(this.mPM) != 0)
                break label1001;
              if (localPreferenceGroup1 != null)
                localPreferenceGroup1.removePreference(this.mNotificationAccess);
            }
          }
          break;
        case 3:
        case 1:
        }
      }
    while (true)
    {
      if (shouldBePinProtected("restrictions_pin_set"))
      {
        protectByRestrictions(this.mToggleAppInstallation);
        protectByRestrictions(this.mToggleVerifyApps);
        protectByRestrictions(this.mResetCredentials);
        protectByRestrictions(localPreferenceScreen2.findPreference("credentials_install"));
      }
      return localPreferenceScreen2;
      n = 0;
      break;
      label671: i = 2131034147;
      break label77;
      if ((this.mLockPatternUtils.usingBiometricWeak()) && (this.mLockPatternUtils.isBiometricWeakInstalled()))
      {
        i = 2131034146;
        break label77;
      }
      switch (this.mLockPatternUtils.getKeyguardStoredPasswordQuality())
      {
      default:
        i = 0;
        break;
      case 65536:
        i = 2131034152;
        break;
      case 131072:
        i = 2131034154;
        break;
      case 262144:
      case 327680:
      case 393216:
        i = 2131034151;
        break;
        label786: bool1 = false;
        break label91;
        label792: localPreference3.setTitle(2131427615);
        break label137;
        addPreferencesFromResource(2131034148);
        break label176;
        addPreferencesFromResource(2131034155);
        break label176;
        if ((TelephonyManager.getDefault().getSimState() != 1) && (TelephonyManager.getDefault().getSimState() != 0))
          break label333;
        localPreferenceScreen2.findPreference("sim_lock").setEnabled(false);
        break label333;
        label855: int m;
        label871: label886: CheckBoxPreference localCheckBoxPreference;
        if ((0x1 & this.mDPM.getKeyguardDisabledFeatures(null)) != 0)
        {
          m = 1;
          if (m == 0)
            break label916;
          this.mEnableKeyguardWidgets.setSummary(2131427611);
          localCheckBoxPreference = this.mEnableKeyguardWidgets;
          if (m != 0)
            break label929;
        }
        label916: label929: for (boolean bool2 = true; ; bool2 = false)
        {
          localCheckBoxPreference.setEnabled(bool2);
          break;
          m = 0;
          break label871;
          this.mEnableKeyguardWidgets.setSummary("");
          break label886;
        }
        label935: k = 2131428890;
        break label478;
        label943: removePreference("credentials_management");
        break label485;
        label953: this.mToggleVerifyApps.setChecked(false);
        this.mToggleVerifyApps.setEnabled(false);
        break label576;
        label972: if (localPreferenceGroup1 != null)
        {
          localPreferenceGroup1.removePreference(this.mToggleVerifyApps);
          break label576;
        }
        this.mToggleVerifyApps.setEnabled(false);
        break label576;
        label1001: int j = getNumEnabledNotificationListeners();
        if (j == 0)
        {
          this.mNotificationAccess.setSummary(getResources().getString(2131427697));
        }
        else
        {
          Preference localPreference1 = this.mNotificationAccess;
          Resources localResources = getResources();
          Object[] arrayOfObject = new Object[1];
          arrayOfObject[0] = Integer.valueOf(j);
          localPreference1.setSummary(String.format(localResources.getQuantityString(2131623943, j, arrayOfObject), new Object[0]));
        }
        break;
      }
    }
  }

  private void disableUnusableTimeouts(long paramLong)
  {
    CharSequence[] arrayOfCharSequence1 = this.mLockAfter.getEntries();
    CharSequence[] arrayOfCharSequence2 = this.mLockAfter.getEntryValues();
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    for (int i = 0; i < arrayOfCharSequence2.length; i++)
      if (Long.valueOf(arrayOfCharSequence2[i].toString()).longValue() <= paramLong)
      {
        localArrayList1.add(arrayOfCharSequence1[i]);
        localArrayList2.add(arrayOfCharSequence2[i]);
      }
    if ((localArrayList1.size() != arrayOfCharSequence1.length) || (localArrayList2.size() != arrayOfCharSequence2.length))
    {
      this.mLockAfter.setEntries((CharSequence[])localArrayList1.toArray(new CharSequence[localArrayList1.size()]));
      this.mLockAfter.setEntryValues((CharSequence[])localArrayList2.toArray(new CharSequence[localArrayList2.size()]));
      int j = Integer.valueOf(this.mLockAfter.getValue()).intValue();
      if (j <= paramLong)
        this.mLockAfter.setValue(String.valueOf(j));
    }
    ListPreference localListPreference = this.mLockAfter;
    if (localArrayList1.size() > 0);
    for (boolean bool = true; ; bool = false)
    {
      localListPreference.setEnabled(bool);
      return;
    }
  }

  private int getNumEnabledNotificationListeners()
  {
    String str = Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners");
    if ((str == null) || ("".equals(str)))
      return 0;
    return str.split(":").length;
  }

  private boolean isNonMarketAppsAllowed()
  {
    int i = Settings.Global.getInt(getContentResolver(), "install_non_market_apps", 0);
    boolean bool = false;
    if (i > 0)
      bool = true;
    return bool;
  }

  private boolean isToggled(Preference paramPreference)
  {
    return ((CheckBoxPreference)paramPreference).isChecked();
  }

  private boolean isVerifierInstalled()
  {
    PackageManager localPackageManager = getPackageManager();
    Intent localIntent = new Intent("android.intent.action.PACKAGE_NEEDS_VERIFICATION");
    localIntent.setType("application/vnd.android.package-archive");
    localIntent.addFlags(1);
    return localPackageManager.queryBroadcastReceivers(localIntent, 0).size() > 0;
  }

  private boolean isVerifyAppsEnabled()
  {
    return Settings.Global.getInt(getContentResolver(), "package_verifier_enable", 1) > 0;
  }

  private void setNonMarketAppsAllowed(boolean paramBoolean)
  {
    if (((UserManager)getActivity().getSystemService("user")).hasUserRestriction("no_install_unknown_sources"))
      return;
    ContentResolver localContentResolver = getContentResolver();
    if (paramBoolean);
    for (int i = 1; ; i = 0)
    {
      Settings.Global.putInt(localContentResolver, "install_non_market_apps", i);
      return;
    }
  }

  private void setupLockAfterPreference()
  {
    long l1 = Settings.Secure.getLong(getContentResolver(), "lock_screen_lock_after_timeout", 5000L);
    this.mLockAfter.setValue(String.valueOf(l1));
    this.mLockAfter.setOnPreferenceChangeListener(this);
    if (this.mDPM != null);
    for (long l2 = this.mDPM.getMaximumTimeToLock(null); ; l2 = 0L)
    {
      long l3 = Math.max(0, Settings.System.getInt(getContentResolver(), "screen_off_timeout", 0));
      if (l2 > 0L)
        disableUnusableTimeouts(Math.max(0L, l2 - l3));
      return;
    }
  }

  private boolean showVerifierSetting()
  {
    return Settings.Global.getInt(getContentResolver(), "verifier_setting_visible", 1) > 0;
  }

  private void updateLockAfterPreferenceSummary()
  {
    long l = Settings.Secure.getLong(getContentResolver(), "lock_screen_lock_after_timeout", 5000L);
    CharSequence[] arrayOfCharSequence1 = this.mLockAfter.getEntries();
    CharSequence[] arrayOfCharSequence2 = this.mLockAfter.getEntryValues();
    int i = 0;
    for (int j = 0; j < arrayOfCharSequence2.length; j++)
      if (l >= Long.valueOf(arrayOfCharSequence2[j].toString()).longValue())
        i = j;
    ListPreference localListPreference = this.mLockAfter;
    Object[] arrayOfObject = new Object[1];
    arrayOfObject[0] = arrayOfCharSequence1[i];
    localListPreference.setSummary(getString(2131427607, arrayOfObject));
  }

  private void warnAppInstallation()
  {
    this.mWarnInstallApps = new AlertDialog.Builder(getActivity()).setTitle(getResources().getString(2131428203)).setIcon(17301543).setMessage(getResources().getString(2131428360)).setPositiveButton(17039379, this).setNegativeButton(17039369, null).show();
  }

  protected int getHelpResource()
  {
    return 2131429268;
  }

  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
    if ((paramInt1 == 124) && (paramInt2 == -1))
    {
      startBiometricWeakImprove();
      return;
    }
    if ((paramInt1 == 125) && (paramInt2 == -1))
    {
      this.mChooseLockSettingsHelper.utils().setBiometricWeakLivelinessEnabled(false);
      return;
    }
    createPreferenceHierarchy();
  }

  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    if ((paramDialogInterface == this.mWarnInstallApps) && (paramInt == -1))
    {
      setNonMarketAppsAllowed(true);
      if (this.mToggleAppInstallation != null)
        this.mToggleAppInstallation.setChecked(true);
    }
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mLockPatternUtils = new LockPatternUtils(getActivity());
    this.mPM = getActivity().getPackageManager();
    this.mDPM = ((DevicePolicyManager)getSystemService("device_policy"));
    this.mChooseLockSettingsHelper = new ChooseLockSettingsHelper(getActivity());
  }

  public void onDestroy()
  {
    super.onDestroy();
    if (this.mWarnInstallApps != null)
      this.mWarnInstallApps.dismiss();
  }

  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    int i;
    if (paramPreference == this.mLockAfter)
      i = Integer.parseInt((String)paramObject);
    try
    {
      Settings.Secure.putInt(getContentResolver(), "lock_screen_lock_after_timeout", i);
      updateLockAfterPreferenceSummary();
      return true;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      while (true)
        Log.e("SecuritySettings", "could not persist lockAfter timeout setting", localNumberFormatException);
    }
  }

  public boolean onPreferenceTreeClick(PreferenceScreen paramPreferenceScreen, Preference paramPreference)
  {
    if (ensurePinRestrictedPreference(paramPreference));
    String str;
    LockPatternUtils localLockPatternUtils;
    do
    {
      do
      {
        return true;
        str = paramPreference.getKey();
        localLockPatternUtils = this.mChooseLockSettingsHelper.utils();
        if ("unlock_set_or_change".equals(str))
        {
          startFragment(this, "com.android.settings.ChooseLockGeneric$ChooseLockGenericFragment", 123, null);
          return true;
        }
        if (!"biometric_weak_improve_matching".equals(str))
          break;
      }
      while (new ChooseLockSettingsHelper(getActivity(), this).launchConfirmationActivity(124, null, null));
      startBiometricWeakImprove();
      return true;
      if (!"biometric_weak_liveliness".equals(str))
        break;
      if (isToggled(paramPreference))
      {
        localLockPatternUtils.setBiometricWeakLivelinessEnabled(true);
        return true;
      }
      this.mBiometricWeakLiveliness.setChecked(true);
    }
    while (new ChooseLockSettingsHelper(getActivity(), this).launchConfirmationActivity(125, null, null));
    localLockPatternUtils.setBiometricWeakLivelinessEnabled(false);
    this.mBiometricWeakLiveliness.setChecked(false);
    return true;
    if ("lockenabled".equals(str))
    {
      localLockPatternUtils.setLockPatternEnabled(isToggled(paramPreference));
      return true;
    }
    if ("visiblepattern".equals(str))
    {
      localLockPatternUtils.setVisiblePatternEnabled(isToggled(paramPreference));
      return true;
    }
    if ("power_button_instantly_locks".equals(str))
    {
      localLockPatternUtils.setPowerButtonInstantlyLocks(isToggled(paramPreference));
      return true;
    }
    if ("keyguard_enable_widgets".equals(str))
    {
      localLockPatternUtils.setWidgetsEnabled(this.mEnableKeyguardWidgets.isChecked());
      return true;
    }
    if (paramPreference == this.mShowPassword)
    {
      ContentResolver localContentResolver2 = getContentResolver();
      boolean bool2 = this.mShowPassword.isChecked();
      int j = 0;
      if (bool2)
        j = 1;
      Settings.System.putInt(localContentResolver2, "show_password", j);
      return true;
    }
    if (paramPreference == this.mToggleAppInstallation)
    {
      if (this.mToggleAppInstallation.isChecked())
      {
        this.mToggleAppInstallation.setChecked(false);
        warnAppInstallation();
        return true;
      }
      setNonMarketAppsAllowed(false);
      return true;
    }
    if ("toggle_verify_applications".equals(str))
    {
      ContentResolver localContentResolver1 = getContentResolver();
      boolean bool1 = this.mToggleVerifyApps.isChecked();
      int i = 0;
      if (bool1)
        i = 1;
      Settings.Global.putInt(localContentResolver1, "package_verifier_enable", i);
      return true;
    }
    return super.onPreferenceTreeClick(paramPreferenceScreen, paramPreference);
  }

  public void onResume()
  {
    int i = 1;
    super.onResume();
    createPreferenceHierarchy();
    LockPatternUtils localLockPatternUtils = this.mChooseLockSettingsHelper.utils();
    if (this.mBiometricWeakLiveliness != null)
      this.mBiometricWeakLiveliness.setChecked(localLockPatternUtils.isBiometricWeakLivelinessEnabled());
    if (this.mVisiblePattern != null)
      this.mVisiblePattern.setChecked(localLockPatternUtils.isVisiblePatternEnabled());
    if (this.mPowerButtonInstantlyLocks != null)
      this.mPowerButtonInstantlyLocks.setChecked(localLockPatternUtils.getPowerButtonInstantlyLocks());
    Preference localPreference;
    if (this.mShowPassword != null)
    {
      CheckBoxPreference localCheckBoxPreference = this.mShowPassword;
      if (Settings.System.getInt(getContentResolver(), "show_password", i) != 0)
      {
        int k = i;
        localCheckBoxPreference.setChecked(k);
      }
    }
    else if (this.mResetCredentials != null)
    {
      localPreference = this.mResetCredentials;
      if (this.mKeyStore.isEmpty())
        break label163;
    }
    while (true)
    {
      localPreference.setEnabled(i);
      if (this.mEnableKeyguardWidgets != null)
        this.mEnableKeyguardWidgets.setChecked(localLockPatternUtils.getWidgetsEnabled());
      return;
      int m = 0;
      break;
      label163: int j = 0;
    }
  }

  public void startBiometricWeakImprove()
  {
    Intent localIntent = new Intent();
    localIntent.setClassName("com.android.facelock", "com.android.facelock.AddToSetup");
    startActivity(localIntent);
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.SecuritySettings
 * JD-Core Version:    0.6.2
 */