package com.android.settings.inputmethod;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.hardware.input.InputManager;
import android.hardware.input.InputManager.InputDeviceListener;
import android.hardware.input.KeyboardLayout;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.preference.TwoStatePreference;
import android.provider.Settings.Secure;
import android.provider.Settings.System;
import android.text.TextUtils;
import android.view.InputDevice;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import com.android.settings.Settings.KeyboardLayoutPickerActivity;
import com.android.settings.Settings.SpellCheckersSettingsActivity;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.UserDictionarySettings;
import com.android.settings.Utils;
import com.android.settings.VoiceInputOutputSettings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;

public class InputMethodAndLanguageSettings extends SettingsPreferenceFragment
  implements InputManager.InputDeviceListener, Preference.OnPreferenceChangeListener, KeyboardLayoutDialogFragment.OnSetupKeyboardLayoutsListener
{
  private static final String[] sHardKeyboardKeys = { "auto_replace", "auto_caps", "auto_punctuate" };
  private static final String[] sSystemSettingNames = { "auto_replace", "auto_caps", "auto_punctuate" };
  private int mDefaultInputMethodSelectorVisibility = 0;
  private PreferenceCategory mGameControllerCategory;
  private Handler mHandler;
  private PreferenceCategory mHardKeyboardCategory;
  private final ArrayList<PreferenceScreen> mHardKeyboardPreferenceList = new ArrayList();
  private InputManager mIm;
  private InputMethodManager mImm;
  private final ArrayList<InputMethodPreference> mInputMethodPreferenceList = new ArrayList();
  private InputMethodSettingValuesWrapper mInputMethodSettingValues;
  private Intent mIntentWaitingForResult;
  private boolean mIsOnlyImeSettings;
  private PreferenceCategory mKeyboardSettingsCategory;
  private Preference mLanguagePref;
  private final Preference.OnPreferenceChangeListener mOnImePreferenceChangedListener = new Preference.OnPreferenceChangeListener()
  {
    public boolean onPreferenceChange(Preference paramAnonymousPreference, Object paramAnonymousObject)
    {
      InputMethodSettingValuesWrapper.getInstance(paramAnonymousPreference.getContext()).refreshAllInputMethodAndSubtypes();
      ((BaseAdapter)InputMethodAndLanguageSettings.this.getPreferenceScreen().getRootAdapter()).notifyDataSetChanged();
      InputMethodAndLanguageSettings.this.updateInputMethodPreferenceViews();
      return true;
    }
  };
  private SettingsObserver mSettingsObserver;

  private InputMethodPreference getInputMethodPreference(InputMethodInfo paramInputMethodInfo)
  {
    CharSequence localCharSequence = paramInputMethodInfo.loadLabel(getPackageManager());
    String str = paramInputMethodInfo.getSettingsActivity();
    Intent localIntent;
    if (!TextUtils.isEmpty(str))
    {
      localIntent = new Intent("android.intent.action.MAIN");
      localIntent.setClassName(paramInputMethodInfo.getPackageName(), str);
    }
    while (true)
    {
      InputMethodPreference localInputMethodPreference = new InputMethodPreference(this, localIntent, this.mImm, paramInputMethodInfo);
      localInputMethodPreference.setKey(paramInputMethodInfo.getId());
      localInputMethodPreference.setTitle(localCharSequence);
      return localInputMethodPreference;
      localIntent = null;
    }
  }

  private boolean hasOnlyOneLanguageInstance(String paramString, String[] paramArrayOfString)
  {
    boolean bool1 = true;
    boolean bool2 = false;
    int i = paramArrayOfString.length;
    for (int j = 0; j < i; j++)
    {
      String str = paramArrayOfString[j];
      if ((str.length() > 2) && (str.startsWith(paramString)))
      {
        bool2++;
        if (bool2 > bool1)
          return false;
      }
    }
    if (bool2 == bool1);
    while (true)
    {
      return bool1;
      bool1 = false;
    }
  }

  private boolean haveInputDeviceWithVibrator()
  {
    int[] arrayOfInt = InputDevice.getDeviceIds();
    for (int i = 0; i < arrayOfInt.length; i++)
    {
      InputDevice localInputDevice = InputDevice.getDevice(arrayOfInt[i]);
      if ((localInputDevice != null) && (!localInputDevice.isVirtual()) && (localInputDevice.getVibrator().hasVibrator()))
        return true;
    }
    return false;
  }

  private void showKeyboardLayoutDialog(String paramString)
  {
    KeyboardLayoutDialogFragment localKeyboardLayoutDialogFragment = new KeyboardLayoutDialogFragment(paramString);
    localKeyboardLayoutDialogFragment.setTargetFragment(this, 0);
    localKeyboardLayoutDialogFragment.show(getActivity().getFragmentManager(), "keyboardLayout");
  }

  private void updateCurrentImeName()
  {
    Activity localActivity = getActivity();
    if ((localActivity == null) || (this.mImm == null));
    Preference localPreference;
    CharSequence localCharSequence;
    do
    {
      do
      {
        return;
        localPreference = getPreferenceScreen().findPreference("current_input_method");
      }
      while (localPreference == null);
      localCharSequence = this.mInputMethodSettingValues.getCurrentInputMethodName(localActivity);
    }
    while (TextUtils.isEmpty(localCharSequence));
    try
    {
      localPreference.setSummary(localCharSequence);
      return;
    }
    finally
    {
    }
  }

  private void updateGameControllers()
  {
    int i = 1;
    if (haveInputDeviceWithVibrator())
    {
      getPreferenceScreen().addPreference(this.mGameControllerCategory);
      CheckBoxPreference localCheckBoxPreference = (CheckBoxPreference)this.mGameControllerCategory.findPreference("vibrate_input_devices");
      if (Settings.System.getInt(getContentResolver(), "vibrate_input_devices", i) > 0);
      while (true)
      {
        localCheckBoxPreference.setChecked(i);
        return;
        int j = 0;
      }
    }
    getPreferenceScreen().removePreference(this.mGameControllerCategory);
  }

  private void updateHardKeyboards()
  {
    this.mHardKeyboardPreferenceList.clear();
    if (getResources().getConfiguration().keyboard == 2)
    {
      int[] arrayOfInt = InputDevice.getDeviceIds();
      int n = 0;
      if (n < arrayOfInt.length)
      {
        InputDevice localInputDevice = InputDevice.getDevice(arrayOfInt[n]);
        final String str1;
        KeyboardLayout localKeyboardLayout;
        label102: PreferenceScreen localPreferenceScreen;
        if ((localInputDevice != null) && (!localInputDevice.isVirtual()) && (localInputDevice.isFullKeyboard()))
        {
          str1 = localInputDevice.getDescriptor();
          String str2 = this.mIm.getCurrentKeyboardLayoutForInputDevice(str1);
          if (str2 == null)
            break label172;
          localKeyboardLayout = this.mIm.getKeyboardLayout(str2);
          localPreferenceScreen = new PreferenceScreen(getActivity(), null);
          localPreferenceScreen.setTitle(localInputDevice.getName());
          if (localKeyboardLayout == null)
            break label178;
          localPreferenceScreen.setSummary(localKeyboardLayout.toString());
        }
        while (true)
        {
          localPreferenceScreen.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
          {
            public boolean onPreferenceClick(Preference paramAnonymousPreference)
            {
              InputMethodAndLanguageSettings.this.showKeyboardLayoutDialog(str1);
              return true;
            }
          });
          this.mHardKeyboardPreferenceList.add(localPreferenceScreen);
          n++;
          break;
          label172: localKeyboardLayout = null;
          break label102;
          label178: localPreferenceScreen.setSummary(2131428526);
        }
      }
    }
    if (!this.mHardKeyboardPreferenceList.isEmpty())
    {
      int j;
      for (int i = this.mHardKeyboardCategory.getPreferenceCount(); ; i = j)
      {
        j = i - 1;
        if (i <= 0)
          break;
        Preference localPreference2 = this.mHardKeyboardCategory.getPreference(j);
        if (localPreference2.getOrder() < 1000)
          this.mHardKeyboardCategory.removePreference(localPreference2);
      }
      Collections.sort(this.mHardKeyboardPreferenceList);
      int k = this.mHardKeyboardPreferenceList.size();
      for (int m = 0; m < k; m++)
      {
        Preference localPreference1 = (Preference)this.mHardKeyboardPreferenceList.get(m);
        localPreference1.setOrder(m);
        this.mHardKeyboardCategory.addPreference(localPreference1);
      }
      getPreferenceScreen().addPreference(this.mHardKeyboardCategory);
      return;
    }
    getPreferenceScreen().removePreference(this.mHardKeyboardCategory);
  }

  private void updateInputDevices()
  {
    updateHardKeyboards();
    updateGameControllers();
  }

  private void updateInputMethodPreferenceViews()
  {
    synchronized (this.mInputMethodPreferenceList)
    {
      Iterator localIterator1 = this.mInputMethodPreferenceList.iterator();
      if (localIterator1.hasNext())
      {
        InputMethodPreference localInputMethodPreference3 = (InputMethodPreference)localIterator1.next();
        this.mKeyboardSettingsCategory.removePreference(localInputMethodPreference3);
      }
    }
    this.mInputMethodPreferenceList.clear();
    List localList = this.mInputMethodSettingValues.getInputMethodList();
    int i;
    if (localList == null)
      i = 0;
    while (true)
    {
      int j;
      if (j < i)
      {
        InputMethodPreference localInputMethodPreference1 = getInputMethodPreference((InputMethodInfo)localList.get(j));
        localInputMethodPreference1.setOnImePreferenceChangeListener(this.mOnImePreferenceChangedListener);
        this.mInputMethodPreferenceList.add(localInputMethodPreference1);
        j++;
        continue;
        i = localList.size();
      }
      else
      {
        if (!this.mInputMethodPreferenceList.isEmpty())
        {
          Collections.sort(this.mInputMethodPreferenceList);
          for (int k = 0; k < i; k++)
            this.mKeyboardSettingsCategory.addPreference((Preference)this.mInputMethodPreferenceList.get(k));
        }
        Iterator localIterator2 = this.mInputMethodPreferenceList.iterator();
        while (localIterator2.hasNext())
        {
          InputMethodPreference localInputMethodPreference2 = (InputMethodPreference)localIterator2.next();
          if ((localInputMethodPreference2 instanceof InputMethodPreference))
            ((InputMethodPreference)localInputMethodPreference2).updatePreferenceViews();
        }
        updateCurrentImeName();
        InputMethodAndSubtypeUtil.loadInputMethodSubtypeList(this, getContentResolver(), this.mInputMethodSettingValues.getInputMethodList(), null);
        return;
        j = 0;
      }
    }
  }

  private void updateUserDictionaryPreference(Preference paramPreference)
  {
    final TreeSet localTreeSet = UserDictionaryList.getUserDictionaryLocalesSet(getActivity());
    if (localTreeSet == null)
    {
      getPreferenceScreen().removePreference(paramPreference);
      return;
    }
    paramPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
    {
      public boolean onPreferenceClick(Preference paramAnonymousPreference)
      {
        Bundle localBundle = new Bundle();
        if (localTreeSet.size() <= 1)
          if (!localTreeSet.isEmpty())
            localBundle.putString("locale", (String)localTreeSet.first());
        for (Object localObject = UserDictionarySettings.class; ; localObject = UserDictionaryList.class)
        {
          InputMethodAndLanguageSettings.this.startFragment(InputMethodAndLanguageSettings.this, ((Class)localObject).getCanonicalName(), -1, localBundle);
          return true;
        }
      }
    });
  }

  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
    if (this.mIntentWaitingForResult != null)
    {
      String str = this.mIntentWaitingForResult.getStringExtra("input_device_descriptor");
      this.mIntentWaitingForResult = null;
      showKeyboardLayoutDialog(str);
    }
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131034134);
    try
    {
      this.mDefaultInputMethodSelectorVisibility = Integer.valueOf(getString(2131427333)).intValue();
      label29: if (getActivity().getAssets().getLocales().length == 1)
        getPreferenceScreen().removePreference(findPreference("phone_language"));
      while (true)
      {
        new VoiceInputOutputSettings(this).onCreate();
        this.mHardKeyboardCategory = ((PreferenceCategory)findPreference("hard_keyboard"));
        this.mKeyboardSettingsCategory = ((PreferenceCategory)findPreference("keyboard_settings_category"));
        this.mGameControllerCategory = ((PreferenceCategory)findPreference("game_controller_settings_category"));
        this.mIsOnlyImeSettings = "android.settings.INPUT_METHOD_SETTINGS".equals(getActivity().getIntent().getAction());
        getActivity().getIntent().setAction(null);
        if (this.mIsOnlyImeSettings)
        {
          getPreferenceScreen().removeAll();
          getPreferenceScreen().addPreference(this.mHardKeyboardCategory);
          getPreferenceScreen().addPreference(this.mKeyboardSettingsCategory);
        }
        this.mImm = ((InputMethodManager)getSystemService("input_method"));
        this.mInputMethodSettingValues = InputMethodSettingValuesWrapper.getInstance(getActivity());
        this.mKeyboardSettingsCategory.removeAll();
        if (!this.mIsOnlyImeSettings)
        {
          PreferenceScreen localPreferenceScreen = new PreferenceScreen(getActivity(), null);
          localPreferenceScreen.setKey("current_input_method");
          localPreferenceScreen.setTitle(getResources().getString(2131428564));
          this.mKeyboardSettingsCategory.addPreference(localPreferenceScreen);
        }
        this.mIm = ((InputManager)getActivity().getSystemService("input"));
        updateInputDevices();
        Intent localIntent = new Intent("android.intent.action.MAIN");
        localIntent.setClass(getActivity(), Settings.SpellCheckersSettingsActivity.class);
        SpellCheckersPreference localSpellCheckersPreference = (SpellCheckersPreference)findPreference("spellcheckers_settings");
        if (localSpellCheckersPreference != null)
          localSpellCheckersPreference.setFragmentIntent(this, localIntent);
        this.mHandler = new Handler();
        this.mSettingsObserver = new SettingsObserver(this.mHandler, getActivity());
        return;
        this.mLanguagePref = findPreference("phone_language");
      }
    }
    catch (NumberFormatException localNumberFormatException)
    {
      break label29;
    }
  }

  public void onInputDeviceAdded(int paramInt)
  {
    updateInputDevices();
  }

  public void onInputDeviceChanged(int paramInt)
  {
    updateInputDevices();
  }

  public void onInputDeviceRemoved(int paramInt)
  {
    updateInputDevices();
  }

  public void onPause()
  {
    super.onPause();
    this.mIm.unregisterInputDeviceListener(this);
    this.mSettingsObserver.pause();
    ContentResolver localContentResolver = getContentResolver();
    List localList = this.mInputMethodSettingValues.getInputMethodList();
    if (!this.mHardKeyboardPreferenceList.isEmpty());
    for (boolean bool = true; ; bool = false)
    {
      InputMethodAndSubtypeUtil.saveInputMethodSubtypeList(this, localContentResolver, localList, bool);
      return;
    }
  }

  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    return false;
  }

  public boolean onPreferenceTreeClick(PreferenceScreen paramPreferenceScreen, Preference paramPreference)
  {
    if (Utils.isMonkeyRunning())
      return false;
    if ((paramPreference instanceof PreferenceScreen))
      if (paramPreference.getFragment() == null);
    CheckBoxPreference localCheckBoxPreference;
    do
    {
      do
        while (true)
        {
          return super.onPreferenceTreeClick(paramPreferenceScreen, paramPreference);
          if ("current_input_method".equals(paramPreference.getKey()))
            ((InputMethodManager)getSystemService("input_method")).showInputMethodPicker();
        }
      while (!(paramPreference instanceof CheckBoxPreference));
      localCheckBoxPreference = (CheckBoxPreference)paramPreference;
      if (!this.mHardKeyboardPreferenceList.isEmpty())
        for (int j = 0; j < sHardKeyboardKeys.length; j++)
          if (localCheckBoxPreference == this.mHardKeyboardCategory.findPreference(sHardKeyboardKeys[j]))
          {
            ContentResolver localContentResolver2 = getContentResolver();
            String str = sSystemSettingNames[j];
            boolean bool2 = localCheckBoxPreference.isChecked();
            int k = 0;
            if (bool2)
              k = 1;
            Settings.System.putInt(localContentResolver2, str, k);
            return true;
          }
    }
    while (localCheckBoxPreference != this.mGameControllerCategory.findPreference("vibrate_input_devices"));
    ContentResolver localContentResolver1 = getContentResolver();
    boolean bool1 = localCheckBoxPreference.isChecked();
    int i = 0;
    if (bool1)
      i = 1;
    Settings.System.putInt(localContentResolver1, "vibrate_input_devices", i);
    return true;
  }

  public void onResume()
  {
    super.onResume();
    this.mSettingsObserver.resume();
    this.mIm.registerInputDeviceListener(this, null);
    Configuration localConfiguration;
    String str1;
    String str4;
    String str2;
    int i;
    label164: CheckBoxPreference localCheckBoxPreference;
    if (!this.mIsOnlyImeSettings)
    {
      if (this.mLanguagePref != null)
      {
        localConfiguration = getResources().getConfiguration();
        str1 = localConfiguration.locale.getLanguage();
        if (!str1.equals("zz"))
          break label244;
        str4 = localConfiguration.locale.getCountry();
        if (!str4.equals("ZZ"))
          break label217;
        str2 = "[Developer] Accented English (zz_ZZ)";
        if (str2.length() > 1)
        {
          String str3 = Character.toUpperCase(str2.charAt(0)) + str2.substring(1);
          this.mLanguagePref.setSummary(str3);
        }
      }
      updateUserDictionaryPreference(findPreference("key_user_dictionary_settings"));
    }
    else
    {
      if (this.mHardKeyboardPreferenceList.isEmpty())
        break label303;
      i = 0;
      if (i >= sHardKeyboardKeys.length)
        break label303;
      localCheckBoxPreference = (CheckBoxPreference)this.mHardKeyboardCategory.findPreference(sHardKeyboardKeys[i]);
      if (Settings.System.getInt(getContentResolver(), sSystemSettingNames[i], 1) <= 0)
        break label298;
    }
    label298: for (boolean bool = true; ; bool = false)
    {
      localCheckBoxPreference.setChecked(bool);
      i++;
      break label164;
      label217: if (str4.equals("ZY"))
      {
        str2 = "[Developer] Fake Bi-Directional (zz_ZY)";
        break;
      }
      str2 = "";
      break;
      label244: if (hasOnlyOneLanguageInstance(str1, Resources.getSystem().getAssets().getLocales()))
      {
        str2 = localConfiguration.locale.getDisplayLanguage(localConfiguration.locale);
        break;
      }
      str2 = localConfiguration.locale.getDisplayName(localConfiguration.locale);
      break;
    }
    label303: updateInputDevices();
    this.mInputMethodSettingValues.refreshAllInputMethodAndSubtypes();
    updateInputMethodPreferenceViews();
  }

  public void onSetupKeyboardLayouts(String paramString)
  {
    Intent localIntent = new Intent("android.intent.action.MAIN");
    localIntent.setClass(getActivity(), Settings.KeyboardLayoutPickerActivity.class);
    localIntent.putExtra("input_device_descriptor", paramString);
    this.mIntentWaitingForResult = localIntent;
    startActivityForResult(localIntent, 0);
  }

  private class SettingsObserver extends ContentObserver
  {
    private Context mContext;

    public SettingsObserver(Handler paramContext, Context arg3)
    {
      super();
      Object localObject;
      this.mContext = localObject;
    }

    public void onChange(boolean paramBoolean)
    {
      InputMethodAndLanguageSettings.this.updateCurrentImeName();
    }

    public void pause()
    {
      this.mContext.getContentResolver().unregisterContentObserver(this);
    }

    public void resume()
    {
      ContentResolver localContentResolver = this.mContext.getContentResolver();
      localContentResolver.registerContentObserver(Settings.Secure.getUriFor("default_input_method"), false, this);
      localContentResolver.registerContentObserver(Settings.Secure.getUriFor("selected_input_method_subtype"), false, this);
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.inputmethod.InputMethodAndLanguageSettings
 * JD-Core Version:    0.6.2
 */