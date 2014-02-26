package com.android.settings.inputmethod;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.provider.UserDictionary.Words;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.UserDictionarySettings;
import com.android.settings.Utils;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;

public class UserDictionaryList extends SettingsPreferenceFragment
{
  private String mLocale;

  public static TreeSet<String> getUserDictionaryLocalesSet(Activity paramActivity)
  {
    Cursor localCursor = paramActivity.managedQuery(UserDictionary.Words.CONTENT_URI, new String[] { "locale" }, null, null, null);
    TreeSet localTreeSet = new TreeSet();
    if (localCursor == null)
      localTreeSet = null;
    label182: 
    do
    {
      return localTreeSet;
      String str2;
      if (localCursor.moveToFirst())
      {
        int i = localCursor.getColumnIndex("locale");
        str2 = localCursor.getString(i);
        if (str2 == null)
          break label182;
      }
      while (true)
      {
        localTreeSet.add(str2);
        if (localCursor.moveToNext())
          break;
        InputMethodManager localInputMethodManager = (InputMethodManager)paramActivity.getSystemService("input_method");
        Iterator localIterator1 = localInputMethodManager.getEnabledInputMethodList().iterator();
        while (localIterator1.hasNext())
        {
          Iterator localIterator2 = localInputMethodManager.getEnabledInputMethodSubtypeList((InputMethodInfo)localIterator1.next(), true).iterator();
          while (localIterator2.hasNext())
          {
            String str1 = ((InputMethodSubtype)localIterator2.next()).getLocale();
            if (!TextUtils.isEmpty(str1))
              localTreeSet.add(str1);
          }
        }
        str2 = "";
      }
    }
    while (localTreeSet.contains(Locale.getDefault().getLanguage().toString()));
    localTreeSet.add(Locale.getDefault().toString());
    return localTreeSet;
  }

  protected void createUserDictSettings(PreferenceGroup paramPreferenceGroup)
  {
    Activity localActivity = getActivity();
    paramPreferenceGroup.removeAll();
    TreeSet localTreeSet = getUserDictionaryLocalesSet(localActivity);
    if (this.mLocale != null)
      localTreeSet.add(this.mLocale);
    if (localTreeSet.size() > 1)
      localTreeSet.add("");
    if (localTreeSet.isEmpty())
      paramPreferenceGroup.addPreference(createUserDictionaryPreference(null, localActivity));
    while (true)
    {
      return;
      Iterator localIterator = localTreeSet.iterator();
      while (localIterator.hasNext())
        paramPreferenceGroup.addPreference(createUserDictionaryPreference((String)localIterator.next(), localActivity));
    }
  }

  protected Preference createUserDictionaryPreference(String paramString, Activity paramActivity)
  {
    Preference localPreference = new Preference(getActivity());
    Intent localIntent = new Intent("android.settings.USER_DICTIONARY_SETTINGS");
    if (paramString == null)
    {
      localPreference.setTitle(Locale.getDefault().getDisplayName());
      localPreference.setIntent(localIntent);
      localPreference.setFragment(UserDictionarySettings.class.getName());
      return localPreference;
    }
    if ("".equals(paramString))
      localPreference.setTitle(getString(2131428545));
    while (true)
    {
      localIntent.putExtra("locale", paramString);
      localPreference.getExtras().putString("locale", paramString);
      break;
      localPreference.setTitle(Utils.createLocaleFromString(paramString).getDisplayName());
    }
  }

  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    Intent localIntent = getActivity().getIntent();
    String str1;
    Bundle localBundle;
    String str2;
    label33: String str3;
    if (localIntent == null)
    {
      str1 = null;
      localBundle = getArguments();
      if (localBundle != null)
        break label59;
      str2 = null;
      if (str2 == null)
        break label71;
      str3 = str2;
    }
    while (true)
    {
      this.mLocale = str3;
      return;
      str1 = localIntent.getStringExtra("locale");
      break;
      label59: str2 = localBundle.getString("locale");
      break label33;
      label71: if (str1 != null)
        str3 = str1;
      else
        str3 = null;
    }
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setPreferenceScreen(getPreferenceManager().createPreferenceScreen(getActivity()));
    getActivity().getActionBar().setTitle(2131428528);
  }

  public void onResume()
  {
    super.onResume();
    createUserDictSettings(getPreferenceScreen());
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.inputmethod.UserDictionaryList
 * JD-Core Version:    0.6.2
 */