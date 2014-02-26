package com.android.settings.accessibility;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.app.ActivityManagerNative;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.Fragment;
import android.app.IActivityManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.TwoStatePreference;
import android.provider.Settings.Global;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.text.TextUtils.SimpleStringSplitter;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyCharacterMap;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.TextView;
import com.android.internal.content.PackageMonitor;
import com.android.internal.view.RotationPolicy;
import com.android.internal.view.RotationPolicy.RotationPolicyListener;
import com.android.settings.DialogCreatable;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AccessibilitySettings extends SettingsPreferenceFragment
  implements Preference.OnPreferenceChangeListener, DialogCreatable
{
  static final Set<ComponentName> sInstalledServices = new HashSet();
  static final TextUtils.SimpleStringSplitter sStringColonSplitter = new TextUtils.SimpleStringSplitter(':');
  private PreferenceScreen mCaptioningPreferenceScreen;
  private final Configuration mCurConfig = new Configuration();
  private PreferenceScreen mDisplayMagnificationPreferenceScreen;
  private PreferenceScreen mGlobalGesturePreferenceScreen;
  private final Handler mHandler = new Handler();
  private int mLongPressTimeoutDefault;
  private final Map<String, String> mLongPressTimeoutValuetoTitleMap = new HashMap();
  private Preference mNoServicesMessagePreference;
  private final RotationPolicy.RotationPolicyListener mRotationPolicyListener = new RotationPolicy.RotationPolicyListener()
  {
    public void onChange()
    {
      AccessibilitySettings.this.updateLockScreenRotationCheckbox();
    }
  };
  private ListPreference mSelectLongPressTimeoutPreference;
  private PreferenceCategory mServicesCategory;
  private final SettingsContentObserver mSettingsContentObserver = new SettingsContentObserver(this.mHandler)
  {
    public void onChange(boolean paramAnonymousBoolean, Uri paramAnonymousUri)
    {
      AccessibilitySettings.this.loadInstalledServices();
      AccessibilitySettings.this.updateServicesPreferences();
    }
  };
  private final PackageMonitor mSettingsPackageMonitor = new PackageMonitor()
  {
    private void sendUpdate()
    {
      AccessibilitySettings.this.mHandler.postDelayed(AccessibilitySettings.this.mUpdateRunnable, 1000L);
    }

    public void onPackageAdded(String paramAnonymousString, int paramAnonymousInt)
    {
      sendUpdate();
    }

    public void onPackageAppeared(String paramAnonymousString, int paramAnonymousInt)
    {
      sendUpdate();
    }

    public void onPackageDisappeared(String paramAnonymousString, int paramAnonymousInt)
    {
      sendUpdate();
    }

    public void onPackageRemoved(String paramAnonymousString, int paramAnonymousInt)
    {
      sendUpdate();
    }
  };
  private PreferenceCategory mSystemsCategory;
  private CheckBoxPreference mToggleLargeTextPreference;
  private CheckBoxPreference mToggleLockScreenRotationPreference;
  private CheckBoxPreference mTogglePowerButtonEndsCallPreference;
  private CheckBoxPreference mToggleSpeakPasswordPreference;
  private final Runnable mUpdateRunnable = new Runnable()
  {
    public void run()
    {
      AccessibilitySettings.this.loadInstalledServices();
      AccessibilitySettings.this.updateServicesPreferences();
    }
  };

  private void handleDisplayMagnificationPreferenceScreenClick()
  {
    int i = 1;
    Bundle localBundle = this.mDisplayMagnificationPreferenceScreen.getExtras();
    localBundle.putString("title", getString(2131428634));
    localBundle.putCharSequence("summary", getActivity().getResources().getText(2131428635));
    if (Settings.Secure.getInt(getContentResolver(), "accessibility_display_magnification_enabled", 0) == i);
    while (true)
    {
      localBundle.putBoolean("checked", i);
      super.onPreferenceTreeClick(this.mDisplayMagnificationPreferenceScreen, this.mDisplayMagnificationPreferenceScreen);
      return;
      int j = 0;
    }
  }

  private void handleLockScreenRotationPreferenceClick()
  {
    Activity localActivity = getActivity();
    if (!this.mToggleLockScreenRotationPreference.isChecked());
    for (boolean bool = true; ; bool = false)
    {
      RotationPolicy.setRotationLockForAccessibility(localActivity, bool);
      return;
    }
  }

  private void handleTogglEnableAccessibilityGesturePreferenceClick()
  {
    int i = 1;
    Bundle localBundle = this.mGlobalGesturePreferenceScreen.getExtras();
    localBundle.putString("title", getString(2131428636));
    localBundle.putString("summary", getString(2131428639));
    if (Settings.Global.getInt(getContentResolver(), "enable_accessibility_global_gesture_enabled", 0) == i);
    while (true)
    {
      localBundle.putBoolean("checked", i);
      super.onPreferenceTreeClick(this.mGlobalGesturePreferenceScreen, this.mGlobalGesturePreferenceScreen);
      return;
      int j = 0;
    }
  }

  private void handleToggleLargeTextPreferenceClick()
  {
    try
    {
      Configuration localConfiguration = this.mCurConfig;
      if (this.mToggleLargeTextPreference.isChecked());
      for (float f = 1.3F; ; f = 1.0F)
      {
        localConfiguration.fontScale = f;
        ActivityManagerNative.getDefault().updatePersistentConfiguration(this.mCurConfig);
        return;
      }
    }
    catch (RemoteException localRemoteException)
    {
    }
  }

  private void handleTogglePowerButtonEndsCallPreferenceClick()
  {
    ContentResolver localContentResolver = getContentResolver();
    if (this.mTogglePowerButtonEndsCallPreference.isChecked());
    for (int i = 2; ; i = 1)
    {
      Settings.Secure.putInt(localContentResolver, "incall_power_button_behavior", i);
      return;
    }
  }

  private void handleToggleSpeakPasswordPreferenceClick()
  {
    ContentResolver localContentResolver = getContentResolver();
    if (this.mToggleSpeakPasswordPreference.isChecked());
    for (int i = 1; ; i = 0)
    {
      Settings.Secure.putInt(localContentResolver, "speak_password", i);
      return;
    }
  }

  private void initializeAllPreferences()
  {
    this.mServicesCategory = ((PreferenceCategory)findPreference("services_category"));
    this.mSystemsCategory = ((PreferenceCategory)findPreference("system_category"));
    this.mToggleLargeTextPreference = ((CheckBoxPreference)findPreference("toggle_large_text_preference"));
    this.mTogglePowerButtonEndsCallPreference = ((CheckBoxPreference)findPreference("toggle_power_button_ends_call_preference"));
    if ((!KeyCharacterMap.deviceHasKey(26)) || (!Utils.isVoiceCapable(getActivity())))
      this.mSystemsCategory.removePreference(this.mTogglePowerButtonEndsCallPreference);
    this.mToggleLockScreenRotationPreference = ((CheckBoxPreference)findPreference("toggle_lock_screen_rotation_preference"));
    if (!RotationPolicy.isRotationSupported(getActivity()))
      this.mSystemsCategory.removePreference(this.mToggleLockScreenRotationPreference);
    this.mToggleSpeakPasswordPreference = ((CheckBoxPreference)findPreference("toggle_speak_password_preference"));
    this.mSelectLongPressTimeoutPreference = ((ListPreference)findPreference("select_long_press_timeout_preference"));
    this.mSelectLongPressTimeoutPreference.setOnPreferenceChangeListener(this);
    if (this.mLongPressTimeoutValuetoTitleMap.size() == 0)
    {
      String[] arrayOfString1 = getResources().getStringArray(2131165239);
      this.mLongPressTimeoutDefault = Integer.parseInt(arrayOfString1[0]);
      String[] arrayOfString2 = getResources().getStringArray(2131165238);
      int j = arrayOfString1.length;
      for (int k = 0; k < j; k++)
        this.mLongPressTimeoutValuetoTitleMap.put(arrayOfString1[k], arrayOfString2[k]);
    }
    this.mCaptioningPreferenceScreen = ((PreferenceScreen)findPreference("captioning_preference_screen"));
    this.mDisplayMagnificationPreferenceScreen = ((PreferenceScreen)findPreference("screen_magnification_preference_screen"));
    this.mGlobalGesturePreferenceScreen = ((PreferenceScreen)findPreference("enable_global_gesture_preference_screen"));
    int i = getActivity().getResources().getInteger(17694748);
    if ((!KeyCharacterMap.deviceHasKey(26)) || (i != 1))
      this.mSystemsCategory.removePreference(this.mGlobalGesturePreferenceScreen);
  }

  private void loadInstalledServices()
  {
    Set localSet = sInstalledServices;
    localSet.clear();
    List localList = AccessibilityManager.getInstance(getActivity()).getInstalledAccessibilityServiceList();
    if (localList == null);
    while (true)
    {
      return;
      int i = localList.size();
      for (int j = 0; j < i; j++)
      {
        ResolveInfo localResolveInfo = ((AccessibilityServiceInfo)localList.get(j)).getResolveInfo();
        localSet.add(new ComponentName(localResolveInfo.serviceInfo.packageName, localResolveInfo.serviceInfo.name));
      }
    }
  }

  private void offerInstallAccessibilitySerivceOnce()
  {
    if (this.mServicesCategory.getPreference(0) != this.mNoServicesMessagePreference);
    label109: 
    while (true)
    {
      return;
      SharedPreferences localSharedPreferences = getActivity().getPreferences(0);
      if (!localSharedPreferences.getBoolean("key_install_accessibility_service_offered_once", false));
      for (int i = 1; ; i = 0)
      {
        if (i == 0)
          break label109;
        Intent localIntent = new Intent("android.intent.action.VIEW", Uri.parse(SystemProperties.get("ro.screenreader.market", "market://search?q=pname:com.google.android.marvin.talkback")));
        if (getPackageManager().resolveActivity(localIntent, 0) == null)
          break;
        localSharedPreferences.edit().putBoolean("key_install_accessibility_service_offered_once", true).commit();
        showDialog(1);
        return;
      }
    }
  }

  private void updateAllPreferences()
  {
    updateServicesPreferences();
    updateSystemPreferences();
  }

  private void updateLockScreenRotationCheckbox()
  {
    Activity localActivity = getActivity();
    CheckBoxPreference localCheckBoxPreference;
    if (localActivity != null)
    {
      localCheckBoxPreference = this.mToggleLockScreenRotationPreference;
      if (RotationPolicy.isRotationLocked(localActivity))
        break label29;
    }
    label29: for (boolean bool = true; ; bool = false)
    {
      localCheckBoxPreference.setChecked(bool);
      return;
    }
  }

  private void updateServicesPreferences()
  {
    this.mServicesCategory.removeAll();
    List localList = AccessibilityManager.getInstance(getActivity()).getInstalledAccessibilityServiceList();
    Set localSet = AccessibilityUtils.getEnabledServicesFromSettings(getActivity());
    int i;
    int k;
    label54: AccessibilityServiceInfo localAccessibilityServiceInfo;
    PreferenceScreen localPreferenceScreen;
    String str1;
    ComponentName localComponentName;
    boolean bool;
    if (Settings.Secure.getInt(getContentResolver(), "accessibility_enabled", 0) == 1)
    {
      i = 1;
      int j = localList.size();
      k = 0;
      if (k >= j)
        break label397;
      localAccessibilityServiceInfo = (AccessibilityServiceInfo)localList.get(k);
      localPreferenceScreen = getPreferenceManager().createPreferenceScreen(getActivity());
      str1 = localAccessibilityServiceInfo.getResolveInfo().loadLabel(getPackageManager()).toString();
      ServiceInfo localServiceInfo = localAccessibilityServiceInfo.getResolveInfo().serviceInfo;
      localComponentName = new ComponentName(localServiceInfo.packageName, localServiceInfo.name);
      localPreferenceScreen.setKey(localComponentName.flattenToString());
      localPreferenceScreen.setTitle(str1);
      if ((i == 0) || (!localSet.contains(localComponentName)))
        break label376;
      bool = true;
      label168: if (!bool)
        break label382;
      localPreferenceScreen.setSummary(getString(2131428648));
    }
    while (true)
    {
      localPreferenceScreen.setOrder(k);
      localPreferenceScreen.setFragment(ToggleAccessibilityServicePreferenceFragment.class.getName());
      localPreferenceScreen.setPersistent(true);
      Bundle localBundle = localPreferenceScreen.getExtras();
      localBundle.putString("preference_key", localPreferenceScreen.getKey());
      localBundle.putBoolean("checked", bool);
      localBundle.putString("title", str1);
      String str2 = localAccessibilityServiceInfo.loadDescription(getPackageManager());
      if (TextUtils.isEmpty(str2))
        str2 = getString(2131428685);
      localBundle.putString("summary", str2);
      String str3 = localAccessibilityServiceInfo.getSettingsActivityName();
      if (!TextUtils.isEmpty(str3))
      {
        localBundle.putString("settings_title", getString(2131428647));
        localBundle.putString("settings_component_name", new ComponentName(localAccessibilityServiceInfo.getResolveInfo().serviceInfo.packageName, str3).flattenToString());
      }
      localBundle.putParcelable("component_name", localComponentName);
      this.mServicesCategory.addPreference(localPreferenceScreen);
      k++;
      break label54;
      i = 0;
      break;
      label376: bool = false;
      break label168;
      label382: localPreferenceScreen.setSummary(getString(2131428649));
    }
    label397: if (this.mServicesCategory.getPreferenceCount() == 0)
    {
      if (this.mNoServicesMessagePreference == null)
      {
        this.mNoServicesMessagePreference = new Preference(getActivity())
        {
          protected void onBindView(View paramAnonymousView)
          {
            super.onBindView(paramAnonymousView);
            ((TextView)paramAnonymousView.findViewById(2131230772)).setText(AccessibilitySettings.this.getString(2131428682));
          }
        };
        this.mNoServicesMessagePreference.setPersistent(false);
        this.mNoServicesMessagePreference.setLayoutResource(2130968713);
        this.mNoServicesMessagePreference.setSelectable(false);
      }
      this.mServicesCategory.addPreference(this.mNoServicesMessagePreference);
    }
  }

  private void updateSystemPreferences()
  {
    try
    {
      this.mCurConfig.updateFrom(ActivityManagerNative.getDefault().getConfiguration());
      label16: CheckBoxPreference localCheckBoxPreference = this.mToggleLargeTextPreference;
      boolean bool1;
      boolean bool3;
      label76: boolean bool2;
      label106: int i;
      label182: label197: int j;
      if (this.mCurConfig.fontScale == 1.3F)
      {
        bool1 = true;
        localCheckBoxPreference.setChecked(bool1);
        if ((KeyCharacterMap.deviceHasKey(26)) && (Utils.isVoiceCapable(getActivity())))
        {
          if (Settings.Secure.getInt(getContentResolver(), "incall_power_button_behavior", 1) != 2)
            break label267;
          bool3 = true;
          this.mTogglePowerButtonEndsCallPreference.setChecked(bool3);
        }
        updateLockScreenRotationCheckbox();
        if (Settings.Secure.getInt(getContentResolver(), "speak_password", 0) == 0)
          break label273;
        bool2 = true;
        this.mToggleSpeakPasswordPreference.setChecked(bool2);
        String str = String.valueOf(Settings.Secure.getInt(getContentResolver(), "long_press_timeout", this.mLongPressTimeoutDefault));
        this.mSelectLongPressTimeoutPreference.setValue(str);
        this.mSelectLongPressTimeoutPreference.setSummary((CharSequence)this.mLongPressTimeoutValuetoTitleMap.get(str));
        if (Settings.Secure.getInt(getContentResolver(), "accessibility_captioning_enabled", 0) != 1)
          break label279;
        i = 1;
        if (i == 0)
          break label285;
        this.mCaptioningPreferenceScreen.setSummary(2131428648);
        if (Settings.Secure.getInt(getContentResolver(), "accessibility_display_magnification_enabled", 0) != 1)
          break label298;
        j = 1;
        label214: if (j == 0)
          break label304;
        this.mDisplayMagnificationPreferenceScreen.setSummary(2131428648);
        label229: if (Settings.Global.getInt(getContentResolver(), "enable_accessibility_global_gesture_enabled", 0) != 1)
          break label317;
      }
      label267: label273: label279: label285: label298: label304: label317: for (int k = 1; ; k = 0)
      {
        if (k == 0)
          break label323;
        this.mGlobalGesturePreferenceScreen.setSummary(2131428637);
        return;
        bool1 = false;
        break;
        bool3 = false;
        break label76;
        bool2 = false;
        break label106;
        i = 0;
        break label182;
        this.mCaptioningPreferenceScreen.setSummary(2131428649);
        break label197;
        j = 0;
        break label214;
        this.mDisplayMagnificationPreferenceScreen.setSummary(2131428649);
        break label229;
      }
      label323: this.mGlobalGesturePreferenceScreen.setSummary(2131428638);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      break label16;
    }
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131034112);
    initializeAllPreferences();
  }

  public Dialog onCreateDialog(int paramInt)
  {
    switch (paramInt)
    {
    default:
      return null;
    case 1:
    }
    return new AlertDialog.Builder(getActivity()).setTitle(2131428683).setMessage(2131428684).setPositiveButton(17039370, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        AccessibilitySettings.this.removeDialog(1);
        Intent localIntent = new Intent("android.intent.action.VIEW", Uri.parse(SystemProperties.get("ro.screenreader.market", "market://search?q=pname:com.google.android.marvin.talkback")));
        try
        {
          AccessibilitySettings.this.startActivity(localIntent);
          return;
        }
        catch (ActivityNotFoundException localActivityNotFoundException)
        {
          Log.w("AccessibilitySettings", "Couldn't start play store activity", localActivityNotFoundException);
        }
      }
    }).setNegativeButton(17039360, null).create();
  }

  public void onPause()
  {
    this.mSettingsPackageMonitor.unregister();
    this.mSettingsContentObserver.unregister(getContentResolver());
    if (RotationPolicy.isRotationSupported(getActivity()))
      RotationPolicy.unregisterRotationPolicyListener(getActivity(), this.mRotationPolicyListener);
    super.onPause();
  }

  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    if (paramPreference == this.mSelectLongPressTimeoutPreference)
    {
      String str = (String)paramObject;
      Settings.Secure.putInt(getContentResolver(), "long_press_timeout", Integer.parseInt(str));
      this.mSelectLongPressTimeoutPreference.setSummary((CharSequence)this.mLongPressTimeoutValuetoTitleMap.get(str));
      return true;
    }
    return false;
  }

  public boolean onPreferenceTreeClick(PreferenceScreen paramPreferenceScreen, Preference paramPreference)
  {
    if (this.mToggleLargeTextPreference == paramPreference)
    {
      handleToggleLargeTextPreferenceClick();
      return true;
    }
    if (this.mTogglePowerButtonEndsCallPreference == paramPreference)
    {
      handleTogglePowerButtonEndsCallPreferenceClick();
      return true;
    }
    if (this.mToggleLockScreenRotationPreference == paramPreference)
    {
      handleLockScreenRotationPreferenceClick();
      return true;
    }
    if (this.mToggleSpeakPasswordPreference == paramPreference)
    {
      handleToggleSpeakPasswordPreferenceClick();
      return true;
    }
    if (this.mGlobalGesturePreferenceScreen == paramPreference)
    {
      handleTogglEnableAccessibilityGesturePreferenceClick();
      return true;
    }
    if (this.mDisplayMagnificationPreferenceScreen == paramPreference)
    {
      handleDisplayMagnificationPreferenceScreenClick();
      return true;
    }
    return super.onPreferenceTreeClick(paramPreferenceScreen, paramPreference);
  }

  public void onResume()
  {
    super.onResume();
    loadInstalledServices();
    updateAllPreferences();
    offerInstallAccessibilitySerivceOnce();
    this.mSettingsPackageMonitor.register(getActivity(), getActivity().getMainLooper(), false);
    this.mSettingsContentObserver.register(getContentResolver());
    if (RotationPolicy.isRotationSupported(getActivity()))
      RotationPolicy.registerRotationPolicyListener(getActivity(), this.mRotationPolicyListener);
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.accessibility.AccessibilitySettings
 * JD-Core Version:    0.6.2
 */