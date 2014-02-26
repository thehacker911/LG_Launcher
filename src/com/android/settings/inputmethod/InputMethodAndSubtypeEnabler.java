package com.android.settings.inputmethod;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import com.android.internal.inputmethod.InputMethodUtils;
import com.android.settings.SettingsPreferenceFragment;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class InputMethodAndSubtypeEnabler extends SettingsPreferenceFragment
{
  private static final String TAG = InputMethodAndSubtypeEnabler.class.getSimpleName();
  private Collator mCollator = Collator.getInstance();
  private AlertDialog mDialog = null;
  private boolean mHaveHardKeyboard;
  private InputMethodManager mImm;
  private final HashMap<String, List<Preference>> mInputMethodAndSubtypePrefsMap = new HashMap();
  private String mInputMethodId;
  private List<InputMethodInfo> mInputMethodProperties;
  private final HashMap<String, CheckBoxPreference> mSubtypeAutoSelectionCBMap = new HashMap();
  private String mSystemLocale = "";
  private String mTitle;

  private void clearImplicitlyEnabledSubtypes(String paramString)
  {
    updateImplicitlyEnabledSubtypes(paramString, false);
  }

  private PreferenceScreen createPreferenceHierarchy()
  {
    PreferenceScreen localPreferenceScreen = getPreferenceManager().createPreferenceScreen(getActivity());
    Activity localActivity = getActivity();
    int i;
    int j;
    label29: InputMethodInfo localInputMethodInfo;
    int k;
    if (this.mInputMethodProperties == null)
    {
      i = 0;
      j = 0;
      if (j >= i)
        break label463;
      localInputMethodInfo = (InputMethodInfo)this.mInputMethodProperties.get(j);
      k = localInputMethodInfo.getSubtypeCount();
      if (k > 1)
        break label83;
    }
    while (true)
    {
      j++;
      break label29;
      i = this.mInputMethodProperties.size();
      break;
      label83: String str = localInputMethodInfo.getId();
      if ((TextUtils.isEmpty(this.mInputMethodId)) || (this.mInputMethodId.equals(str)))
      {
        PreferenceCategory localPreferenceCategory1 = new PreferenceCategory(localActivity);
        localPreferenceScreen.addPreference(localPreferenceCategory1);
        localPreferenceCategory1.setTitle(localInputMethodInfo.loadLabel(getPackageManager()));
        localPreferenceCategory1.setKey(str);
        CheckBoxPreference localCheckBoxPreference = new CheckBoxPreference(localActivity);
        this.mSubtypeAutoSelectionCBMap.put(str, localCheckBoxPreference);
        localPreferenceCategory1.addPreference(localCheckBoxPreference);
        PreferenceCategory localPreferenceCategory2 = new PreferenceCategory(localActivity);
        localPreferenceCategory2.setTitle(2131428572);
        localPreferenceScreen.addPreference(localPreferenceCategory2);
        ArrayList localArrayList = new ArrayList();
        Object localObject = null;
        int m = 0;
        if (k > 0)
        {
          int n = 0;
          if (n < k)
          {
            InputMethodSubtype localInputMethodSubtype = localInputMethodInfo.getSubtypeAt(n);
            CharSequence localCharSequence = localInputMethodSubtype.getDisplayName(localActivity, localInputMethodInfo.getPackageName(), localInputMethodInfo.getServiceInfo().applicationInfo);
            if (localInputMethodSubtype.overridesImplicitlyEnabledSubtype())
              if (m == 0)
              {
                m = 1;
                localObject = localCharSequence;
              }
            while (true)
            {
              n++;
              break;
              SubtypeCheckBoxPreference localSubtypeCheckBoxPreference = new SubtypeCheckBoxPreference(localActivity, localInputMethodSubtype.getLocale(), this.mSystemLocale, this.mCollator);
              localSubtypeCheckBoxPreference.setKey(str + localInputMethodSubtype.hashCode());
              localSubtypeCheckBoxPreference.setTitle(localCharSequence);
              localArrayList.add(localSubtypeCheckBoxPreference);
            }
          }
          Collections.sort(localArrayList);
          for (int i1 = 0; i1 < localArrayList.size(); i1++)
            localPreferenceCategory2.addPreference((Preference)localArrayList.get(i1));
          this.mInputMethodAndSubtypePrefsMap.put(str, localArrayList);
        }
        if (m != 0)
        {
          if (TextUtils.isEmpty(localObject))
          {
            Log.w(TAG, "Title for auto subtype is empty.");
            localCheckBoxPreference.setTitle("---");
          }
          else
          {
            localCheckBoxPreference.setTitle(localObject);
          }
        }
        else
          localCheckBoxPreference.setTitle(2131428573);
      }
    }
    label463: return localPreferenceScreen;
  }

  private boolean isNoSubtypesExplicitlySelected(String paramString)
  {
    boolean bool = true;
    Iterator localIterator = ((List)this.mInputMethodAndSubtypePrefsMap.get(paramString)).iterator();
    while (localIterator.hasNext())
    {
      Preference localPreference = (Preference)localIterator.next();
      if (((localPreference instanceof CheckBoxPreference)) && (((CheckBoxPreference)localPreference).isChecked()))
        bool = false;
    }
    return bool;
  }

  private void onCreateIMM()
  {
    this.mInputMethodProperties = ((InputMethodManager)getSystemService("input_method")).getInputMethodList();
  }

  private void setCheckedImplicitlyEnabledSubtypes(String paramString)
  {
    updateImplicitlyEnabledSubtypes(paramString, true);
  }

  private void setSubtypeAutoSelectionEnabled(String paramString, boolean paramBoolean)
  {
    CheckBoxPreference localCheckBoxPreference = (CheckBoxPreference)this.mSubtypeAutoSelectionCBMap.get(paramString);
    if (localCheckBoxPreference == null);
    do
    {
      return;
      localCheckBoxPreference.setChecked(paramBoolean);
      Iterator localIterator = ((List)this.mInputMethodAndSubtypePrefsMap.get(paramString)).iterator();
      while (localIterator.hasNext())
      {
        Preference localPreference = (Preference)localIterator.next();
        if ((localPreference instanceof CheckBoxPreference))
        {
          if (!paramBoolean);
          for (boolean bool = true; ; bool = false)
          {
            localPreference.setEnabled(bool);
            if (!paramBoolean)
              break;
            ((CheckBoxPreference)localPreference).setChecked(false);
            break;
          }
        }
      }
    }
    while (!paramBoolean);
    InputMethodAndSubtypeUtil.saveInputMethodSubtypeList(this, getContentResolver(), this.mInputMethodProperties, this.mHaveHardKeyboard);
    setCheckedImplicitlyEnabledSubtypes(paramString);
  }

  private void updateAutoSelectionCB()
  {
    Iterator localIterator = this.mInputMethodAndSubtypePrefsMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      setSubtypeAutoSelectionEnabled(str, isNoSubtypesExplicitlySelected(str));
    }
    setCheckedImplicitlyEnabledSubtypes(null);
  }

  private void updateImplicitlyEnabledSubtypes(String paramString, boolean paramBoolean)
  {
    Iterator localIterator1 = this.mInputMethodProperties.iterator();
    label245: 
    while (localIterator1.hasNext())
    {
      InputMethodInfo localInputMethodInfo = (InputMethodInfo)localIterator1.next();
      String str1 = localInputMethodInfo.getId();
      if ((paramString == null) || (paramString.equals(str1)))
      {
        CheckBoxPreference localCheckBoxPreference1 = (CheckBoxPreference)this.mSubtypeAutoSelectionCBMap.get(str1);
        if ((localCheckBoxPreference1 != null) && (localCheckBoxPreference1.isChecked()))
        {
          List localList1 = (List)this.mInputMethodAndSubtypePrefsMap.get(str1);
          List localList2 = this.mImm.getEnabledInputMethodSubtypeList(localInputMethodInfo, true);
          if ((localList1 != null) && (localList2 != null))
          {
            Iterator localIterator2 = localList1.iterator();
            while (true)
            {
              if (!localIterator2.hasNext())
                break label245;
              Preference localPreference = (Preference)localIterator2.next();
              if ((localPreference instanceof CheckBoxPreference))
              {
                CheckBoxPreference localCheckBoxPreference2 = (CheckBoxPreference)localPreference;
                localCheckBoxPreference2.setChecked(false);
                if (paramBoolean)
                {
                  Iterator localIterator3 = localList2.iterator();
                  if (localIterator3.hasNext())
                  {
                    InputMethodSubtype localInputMethodSubtype = (InputMethodSubtype)localIterator3.next();
                    String str2 = str1 + localInputMethodSubtype.hashCode();
                    if (!localCheckBoxPreference2.getKey().equals(str2))
                      break;
                    localCheckBoxPreference2.setChecked(true);
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    if (!TextUtils.isEmpty(this.mTitle))
      getActivity().setTitle(this.mTitle);
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mImm = ((InputMethodManager)getSystemService("input_method"));
    Configuration localConfiguration = getResources().getConfiguration();
    if (localConfiguration.keyboard == 2);
    for (boolean bool = true; ; bool = false)
    {
      this.mHaveHardKeyboard = bool;
      Bundle localBundle = getArguments();
      this.mInputMethodId = getActivity().getIntent().getStringExtra("input_method_id");
      if ((this.mInputMethodId == null) && (localBundle != null))
      {
        String str2 = localBundle.getString("input_method_id");
        if (str2 != null)
          this.mInputMethodId = str2;
      }
      this.mTitle = getActivity().getIntent().getStringExtra("android.intent.extra.TITLE");
      if ((this.mTitle == null) && (localBundle != null))
      {
        String str1 = localBundle.getString("android.intent.extra.TITLE");
        if (str1 != null)
          this.mTitle = str1;
      }
      Locale localLocale = localConfiguration.locale;
      this.mSystemLocale = localLocale.toString();
      this.mCollator = Collator.getInstance(localLocale);
      onCreateIMM();
      setPreferenceScreen(createPreferenceHierarchy());
      return;
    }
  }

  public void onDestroy()
  {
    super.onDestroy();
    if (this.mDialog != null)
    {
      this.mDialog.dismiss();
      this.mDialog = null;
    }
  }

  public void onPause()
  {
    super.onPause();
    clearImplicitlyEnabledSubtypes(null);
    InputMethodAndSubtypeUtil.saveInputMethodSubtypeList(this, getContentResolver(), this.mInputMethodProperties, this.mHaveHardKeyboard);
  }

  public boolean onPreferenceTreeClick(PreferenceScreen paramPreferenceScreen, Preference paramPreference)
  {
    final String str1;
    if ((paramPreference instanceof CheckBoxPreference))
    {
      final CheckBoxPreference localCheckBoxPreference = (CheckBoxPreference)paramPreference;
      Iterator localIterator = this.mSubtypeAutoSelectionCBMap.keySet().iterator();
      while (localIterator.hasNext())
      {
        String str2 = (String)localIterator.next();
        if (this.mSubtypeAutoSelectionCBMap.get(str2) == localCheckBoxPreference)
        {
          setSubtypeAutoSelectionEnabled(str2, localCheckBoxPreference.isChecked());
          return super.onPreferenceTreeClick(paramPreferenceScreen, paramPreference);
        }
      }
      str1 = localCheckBoxPreference.getKey();
      if (!localCheckBoxPreference.isChecked())
        break label355;
      int i = this.mInputMethodProperties.size();
      Object localObject;
      for (int j = 0; ; j++)
      {
        localObject = null;
        if (j >= i)
          break;
        InputMethodInfo localInputMethodInfo = (InputMethodInfo)this.mInputMethodProperties.get(j);
        if (str1.equals(localInputMethodInfo.getId()))
        {
          localObject = localInputMethodInfo;
          if (!InputMethodUtils.isSystemIme(localInputMethodInfo))
            break;
          InputMethodAndSubtypeUtil.setSubtypesPreferenceEnabled(this, this.mInputMethodProperties, str1, true);
          return super.onPreferenceTreeClick(paramPreferenceScreen, paramPreference);
        }
      }
      if (localObject == null)
        return super.onPreferenceTreeClick(paramPreferenceScreen, paramPreference);
      localCheckBoxPreference.setChecked(false);
      if (this.mDialog != null)
        break label335;
      this.mDialog = new AlertDialog.Builder(getActivity()).setTitle(17039380).setIconAttribute(16843605).setCancelable(true).setPositiveButton(17039370, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          localCheckBoxPreference.setChecked(true);
          InputMethodAndSubtypeUtil.setSubtypesPreferenceEnabled(InputMethodAndSubtypeEnabler.this, InputMethodAndSubtypeEnabler.this.mInputMethodProperties, str1, true);
        }
      }).setNegativeButton(17039360, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
        }
      }).create();
      AlertDialog localAlertDialog = this.mDialog;
      Resources localResources = getResources();
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = localObject.getServiceInfo().applicationInfo.loadLabel(getPackageManager());
      localAlertDialog.setMessage(localResources.getString(2131428513, arrayOfObject));
      this.mDialog.show();
    }
    while (true)
    {
      return super.onPreferenceTreeClick(paramPreferenceScreen, paramPreference);
      label335: if (!this.mDialog.isShowing())
        break;
      this.mDialog.dismiss();
      break;
      label355: InputMethodAndSubtypeUtil.setSubtypesPreferenceEnabled(this, this.mInputMethodProperties, str1, false);
      updateAutoSelectionCB();
    }
  }

  public void onResume()
  {
    super.onResume();
    InputMethodSettingValuesWrapper.getInstance(getActivity()).refreshAllInputMethodAndSubtypes();
    InputMethodAndSubtypeUtil.loadInputMethodSubtypeList(this, getContentResolver(), this.mInputMethodProperties, this.mInputMethodAndSubtypePrefsMap);
    updateAutoSelectionCB();
  }

  private static class SubtypeCheckBoxPreference extends CheckBoxPreference
  {
    private final Collator mCollator;
    private final boolean mIsSystemLanguage;
    private final boolean mIsSystemLocale;

    public SubtypeCheckBoxPreference(Context paramContext, String paramString1, String paramString2, Collator paramCollator)
    {
      super();
      if (TextUtils.isEmpty(paramString1))
        this.mIsSystemLocale = false;
      boolean bool1;
      for (this.mIsSystemLanguage = false; ; this.mIsSystemLanguage = bool1)
      {
        this.mCollator = paramCollator;
        return;
        this.mIsSystemLocale = paramString1.equals(paramString2);
        if (!this.mIsSystemLocale)
        {
          boolean bool2 = paramString1.startsWith(paramString2.substring(0, 2));
          bool1 = false;
          if (!bool2);
        }
        else
        {
          bool1 = true;
        }
      }
    }

    public int compareTo(Preference paramPreference)
    {
      int i = -1;
      if ((paramPreference instanceof SubtypeCheckBoxPreference))
      {
        SubtypeCheckBoxPreference localSubtypeCheckBoxPreference = (SubtypeCheckBoxPreference)paramPreference;
        CharSequence localCharSequence1 = getTitle();
        CharSequence localCharSequence2 = localSubtypeCheckBoxPreference.getTitle();
        if (TextUtils.equals(localCharSequence1, localCharSequence2))
          i = 0;
        do
        {
          do
          {
            do
              return i;
            while (this.mIsSystemLocale);
            if (localSubtypeCheckBoxPreference.mIsSystemLocale)
              return 1;
          }
          while (this.mIsSystemLanguage);
          if (localSubtypeCheckBoxPreference.mIsSystemLanguage)
            return 1;
          if (TextUtils.isEmpty(localCharSequence1))
            return 1;
        }
        while (TextUtils.isEmpty(localCharSequence2));
        return this.mCollator.compare(localCharSequence1.toString(), localCharSequence2.toString());
      }
      Log.w(InputMethodAndSubtypeEnabler.TAG, "Illegal preference type.");
      return super.compareTo(paramPreference);
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.inputmethod.InputMethodAndSubtypeEnabler
 * JD-Core Version:    0.6.2
 */