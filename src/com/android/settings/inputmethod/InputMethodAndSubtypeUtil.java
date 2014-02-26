package com.android.settings.inputmethod;

import android.content.ContentResolver;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.Settings.Secure;
import android.provider.Settings.SettingNotFoundException;
import android.text.TextUtils;
import android.text.TextUtils.SimpleStringSplitter;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodSubtype;
import com.android.internal.inputmethod.InputMethodUtils;
import com.android.settings.SettingsPreferenceFragment;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class InputMethodAndSubtypeUtil
{
  private static final TextUtils.SimpleStringSplitter sStringInputMethodSplitter = new TextUtils.SimpleStringSplitter(':');
  private static final TextUtils.SimpleStringSplitter sStringInputMethodSubtypeSplitter = new TextUtils.SimpleStringSplitter(';');

  public static void buildDisabledSystemInputMethods(StringBuilder paramStringBuilder, HashSet<String> paramHashSet)
  {
    int i = 0;
    Iterator localIterator = paramHashSet.iterator();
    if (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      if (i != 0)
        paramStringBuilder.append(':');
      while (true)
      {
        paramStringBuilder.append(str);
        break;
        i = 1;
      }
    }
  }

  private static void buildEnabledInputMethodsString(StringBuilder paramStringBuilder, String paramString, HashSet<String> paramHashSet)
  {
    paramStringBuilder.append(paramString);
    Iterator localIterator = paramHashSet.iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      paramStringBuilder.append(';').append(str);
    }
  }

  public static void buildInputMethodsAndSubtypesString(StringBuilder paramStringBuilder, HashMap<String, HashSet<String>> paramHashMap)
  {
    int i = 0;
    Iterator localIterator = paramHashMap.keySet().iterator();
    if (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      if (i != 0)
        paramStringBuilder.append(':');
      while (true)
      {
        buildEnabledInputMethodsString(paramStringBuilder, str, (HashSet)paramHashMap.get(str));
        break;
        i = 1;
      }
    }
  }

  private static HashSet<String> getDisabledSystemIMEs(ContentResolver paramContentResolver)
  {
    HashSet localHashSet = new HashSet();
    String str = Settings.Secure.getString(paramContentResolver, "disabled_system_input_methods");
    if (TextUtils.isEmpty(str));
    while (true)
    {
      return localHashSet;
      sStringInputMethodSplitter.setString(str);
      while (sStringInputMethodSplitter.hasNext())
        localHashSet.add(sStringInputMethodSplitter.next());
    }
  }

  private static HashMap<String, HashSet<String>> getEnabledInputMethodsAndSubtypeList(ContentResolver paramContentResolver)
  {
    String str1 = Settings.Secure.getString(paramContentResolver, "enabled_input_methods");
    HashMap localHashMap = new HashMap();
    if (TextUtils.isEmpty(str1));
    while (true)
    {
      return localHashMap;
      sStringInputMethodSplitter.setString(str1);
      while (sStringInputMethodSplitter.hasNext())
      {
        String str2 = sStringInputMethodSplitter.next();
        sStringInputMethodSubtypeSplitter.setString(str2);
        if (sStringInputMethodSubtypeSplitter.hasNext())
        {
          HashSet localHashSet = new HashSet();
          String str3 = sStringInputMethodSubtypeSplitter.next();
          while (sStringInputMethodSubtypeSplitter.hasNext())
            localHashSet.add(sStringInputMethodSubtypeSplitter.next());
          localHashMap.put(str3, localHashSet);
        }
      }
    }
  }

  private static int getInputMethodSubtypeSelected(ContentResolver paramContentResolver)
  {
    try
    {
      int i = Settings.Secure.getInt(paramContentResolver, "selected_input_method_subtype");
      return i;
    }
    catch (Settings.SettingNotFoundException localSettingNotFoundException)
    {
    }
    return -1;
  }

  private static boolean isInputMethodSubtypeSelected(ContentResolver paramContentResolver)
  {
    return getInputMethodSubtypeSelected(paramContentResolver) != -1;
  }

  public static void loadInputMethodSubtypeList(SettingsPreferenceFragment paramSettingsPreferenceFragment, ContentResolver paramContentResolver, List<InputMethodInfo> paramList, Map<String, List<Preference>> paramMap)
  {
    HashMap localHashMap = getEnabledInputMethodsAndSubtypeList(paramContentResolver);
    Iterator localIterator1 = paramList.iterator();
    while (localIterator1.hasNext())
    {
      String str = ((InputMethodInfo)localIterator1.next()).getId();
      Preference localPreference = paramSettingsPreferenceFragment.findPreference(str);
      if ((localPreference != null) && ((localPreference instanceof CheckBoxPreference)))
      {
        CheckBoxPreference localCheckBoxPreference = (CheckBoxPreference)localPreference;
        boolean bool = localHashMap.containsKey(str);
        localCheckBoxPreference.setChecked(bool);
        if (paramMap != null)
        {
          Iterator localIterator2 = ((List)paramMap.get(str)).iterator();
          while (localIterator2.hasNext())
            ((Preference)localIterator2.next()).setEnabled(bool);
        }
        setSubtypesPreferenceEnabled(paramSettingsPreferenceFragment, paramList, str, bool);
      }
    }
    updateSubtypesPreferenceChecked(paramSettingsPreferenceFragment, paramList, localHashMap);
  }

  private static void putSelectedInputMethodSubtype(ContentResolver paramContentResolver, int paramInt)
  {
    Settings.Secure.putInt(paramContentResolver, "selected_input_method_subtype", paramInt);
  }

  public static void saveInputMethodSubtypeList(SettingsPreferenceFragment paramSettingsPreferenceFragment, ContentResolver paramContentResolver, List<InputMethodInfo> paramList, boolean paramBoolean)
  {
    String str1 = Settings.Secure.getString(paramContentResolver, "default_input_method");
    int i = getInputMethodSubtypeSelected(paramContentResolver);
    HashMap localHashMap = getEnabledInputMethodsAndSubtypeList(paramContentResolver);
    HashSet localHashSet1 = getDisabledSystemIMEs(paramContentResolver);
    paramList.size();
    int j = 0;
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      InputMethodInfo localInputMethodInfo = (InputMethodInfo)localIterator.next();
      String str2 = localInputMethodInfo.getId();
      Preference localPreference = paramSettingsPreferenceFragment.findPreference(str2);
      if (localPreference != null)
      {
        boolean bool1;
        boolean bool2;
        boolean bool3;
        HashSet localHashSet2;
        int k;
        int n;
        label198: InputMethodSubtype localInputMethodSubtype;
        String str3;
        CheckBoxPreference localCheckBoxPreference;
        if ((localPreference instanceof CheckBoxPreference))
        {
          bool1 = ((CheckBoxPreference)localPreference).isChecked();
          bool2 = str2.equals(str1);
          bool3 = InputMethodUtils.isSystemIme(localInputMethodInfo);
          if (((paramBoolean) || (!InputMethodSettingValuesWrapper.getInstance(paramSettingsPreferenceFragment.getActivity()).isAlwaysCheckedIme(localInputMethodInfo, paramSettingsPreferenceFragment.getActivity()))) && (!bool1))
            break label340;
          if (!localHashMap.containsKey(str2))
            localHashMap.put(str2, new HashSet());
          localHashSet2 = (HashSet)localHashMap.get(str2);
          k = 0;
          int m = localInputMethodInfo.getSubtypeCount();
          n = 0;
          if (n >= m)
            break label356;
          localInputMethodSubtype = localInputMethodInfo.getSubtypeAt(n);
          str3 = String.valueOf(localInputMethodSubtype.hashCode());
          localCheckBoxPreference = (CheckBoxPreference)paramSettingsPreferenceFragment.findPreference(str2 + str3);
          if (localCheckBoxPreference != null)
            break label276;
        }
        while (true)
        {
          n++;
          break label198;
          bool1 = localHashMap.containsKey(str2);
          break;
          label276: if (k == 0)
          {
            localHashSet2.clear();
            j = 1;
            k = 1;
          }
          if (localCheckBoxPreference.isChecked())
          {
            localHashSet2.add(str3);
            if ((bool2) && (i == localInputMethodSubtype.hashCode()))
              j = 0;
          }
          else
          {
            localHashSet2.remove(str3);
          }
        }
        label340: localHashMap.remove(str2);
        if (bool2)
          str1 = null;
        label356: if ((bool3) && (paramBoolean))
          if (localHashSet1.contains(str2))
          {
            if (bool1)
              localHashSet1.remove(str2);
          }
          else if (!bool1)
            localHashSet1.add(str2);
      }
    }
    StringBuilder localStringBuilder1 = new StringBuilder();
    buildInputMethodsAndSubtypesString(localStringBuilder1, localHashMap);
    StringBuilder localStringBuilder2 = new StringBuilder();
    buildDisabledSystemInputMethods(localStringBuilder2, localHashSet1);
    if ((j != 0) || (!isInputMethodSubtypeSelected(paramContentResolver)))
      putSelectedInputMethodSubtype(paramContentResolver, -1);
    Settings.Secure.putString(paramContentResolver, "enabled_input_methods", localStringBuilder1.toString());
    if (localStringBuilder2.length() > 0)
      Settings.Secure.putString(paramContentResolver, "disabled_system_input_methods", localStringBuilder2.toString());
    if (str1 != null);
    while (true)
    {
      Settings.Secure.putString(paramContentResolver, "default_input_method", str1);
      return;
      str1 = "";
    }
  }

  public static void setSubtypesPreferenceEnabled(SettingsPreferenceFragment paramSettingsPreferenceFragment, List<InputMethodInfo> paramList, String paramString, boolean paramBoolean)
  {
    PreferenceScreen localPreferenceScreen = paramSettingsPreferenceFragment.getPreferenceScreen();
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      InputMethodInfo localInputMethodInfo = (InputMethodInfo)localIterator.next();
      if (paramString.equals(localInputMethodInfo.getId()))
      {
        int i = localInputMethodInfo.getSubtypeCount();
        for (int j = 0; j < i; j++)
        {
          InputMethodSubtype localInputMethodSubtype = localInputMethodInfo.getSubtypeAt(j);
          CheckBoxPreference localCheckBoxPreference = (CheckBoxPreference)localPreferenceScreen.findPreference(paramString + localInputMethodSubtype.hashCode());
          if (localCheckBoxPreference != null)
            localCheckBoxPreference.setEnabled(paramBoolean);
        }
      }
    }
  }

  public static void updateSubtypesPreferenceChecked(SettingsPreferenceFragment paramSettingsPreferenceFragment, List<InputMethodInfo> paramList, HashMap<String, HashSet<String>> paramHashMap)
  {
    PreferenceScreen localPreferenceScreen = paramSettingsPreferenceFragment.getPreferenceScreen();
    Iterator localIterator = paramList.iterator();
    while (true)
    {
      InputMethodInfo localInputMethodInfo;
      String str1;
      if (localIterator.hasNext())
      {
        localInputMethodInfo = (InputMethodInfo)localIterator.next();
        str1 = localInputMethodInfo.getId();
        if (paramHashMap.containsKey(str1));
      }
      else
      {
        return;
      }
      HashSet localHashSet = (HashSet)paramHashMap.get(str1);
      int i = localInputMethodInfo.getSubtypeCount();
      for (int j = 0; j < i; j++)
      {
        String str2 = String.valueOf(localInputMethodInfo.getSubtypeAt(j).hashCode());
        CheckBoxPreference localCheckBoxPreference = (CheckBoxPreference)localPreferenceScreen.findPreference(str1 + str2);
        if (localCheckBoxPreference != null)
          localCheckBoxPreference.setChecked(localHashSet.contains(str2));
      }
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.inputmethod.InputMethodAndSubtypeUtil
 * JD-Core Version:    0.6.2
 */