package com.android.settings.inputmethod;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.UserDictionary.Words;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import com.android.settings.UserDictionarySettings;
import com.android.settings.Utils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.TreeSet;

public class UserDictionaryAddWordContents
{
  private static final String[] HAS_WORD_PROJECTION = { "word" };
  private String mLocale;
  private final int mMode;
  private final String mOldShortcut;
  private final String mOldWord;
  private String mSavedShortcut;
  private String mSavedWord;
  private final EditText mShortcutEditText;
  private final EditText mWordEditText;

  UserDictionaryAddWordContents(View paramView, Bundle paramBundle)
  {
    this.mWordEditText = ((EditText)paramView.findViewById(2131231097));
    this.mShortcutEditText = ((EditText)paramView.findViewById(2131231100));
    String str1 = paramBundle.getString("word");
    if (str1 != null)
    {
      this.mWordEditText.setText(str1);
      this.mWordEditText.setSelection(this.mWordEditText.getText().length());
    }
    String str2 = paramBundle.getString("shortcut");
    if ((str2 != null) && (this.mShortcutEditText != null))
      this.mShortcutEditText.setText(str2);
    this.mMode = paramBundle.getInt("mode");
    this.mOldWord = paramBundle.getString("word");
    this.mOldShortcut = paramBundle.getString("shortcut");
    updateLocale(paramBundle.getString("locale"));
  }

  UserDictionaryAddWordContents(View paramView, UserDictionaryAddWordContents paramUserDictionaryAddWordContents)
  {
    this.mWordEditText = ((EditText)paramView.findViewById(2131231097));
    this.mShortcutEditText = ((EditText)paramView.findViewById(2131231100));
    this.mMode = 0;
    this.mOldWord = paramUserDictionaryAddWordContents.mSavedWord;
    this.mOldShortcut = paramUserDictionaryAddWordContents.mSavedShortcut;
    updateLocale(this.mLocale);
  }

  private static void addLocaleDisplayNameToList(Context paramContext, ArrayList<LocaleRenderer> paramArrayList, String paramString)
  {
    if (paramString != null)
      paramArrayList.add(new LocaleRenderer(paramContext, paramString));
  }

  private boolean hasWord(String paramString, Context paramContext)
  {
    if ("".equals(this.mLocale));
    ContentResolver localContentResolver;
    Uri localUri;
    String[] arrayOfString1;
    String[] arrayOfString2;
    for (Cursor localCursor = paramContext.getContentResolver().query(UserDictionary.Words.CONTENT_URI, HAS_WORD_PROJECTION, "word=? AND locale is null", new String[] { paramString }, null); localCursor == null; localCursor = localContentResolver.query(localUri, arrayOfString1, "word=? AND locale=?", arrayOfString2, null))
    {
      if (localCursor != null)
        localCursor.close();
      return false;
      localContentResolver = paramContext.getContentResolver();
      localUri = UserDictionary.Words.CONTENT_URI;
      arrayOfString1 = HAS_WORD_PROJECTION;
      arrayOfString2 = new String[2];
      arrayOfString2[0] = paramString;
      arrayOfString2[1] = this.mLocale;
    }
    try
    {
      int i = localCursor.getCount();
      if (i > 0)
      {
        bool = true;
        return bool;
      }
      boolean bool = false;
    }
    finally
    {
      if (localCursor != null)
        localCursor.close();
    }
  }

  int apply(Context paramContext, Bundle paramBundle)
  {
    if (paramBundle != null)
      saveStateIntoBundle(paramBundle);
    ContentResolver localContentResolver = paramContext.getContentResolver();
    if ((this.mMode == 0) && (!TextUtils.isEmpty(this.mOldWord)))
      UserDictionarySettings.deleteWord(this.mOldWord, this.mOldShortcut, localContentResolver);
    String str1 = this.mWordEditText.getText().toString();
    Object localObject;
    if (this.mShortcutEditText == null)
      localObject = null;
    while (TextUtils.isEmpty(str1))
    {
      return 1;
      String str2 = this.mShortcutEditText.getText().toString();
      if (TextUtils.isEmpty(str2))
        localObject = null;
      else
        localObject = str2;
    }
    this.mSavedWord = str1;
    this.mSavedShortcut = localObject;
    if (hasWord(str1, paramContext))
      return 2;
    UserDictionarySettings.deleteWord(str1, null, localContentResolver);
    if (!TextUtils.isEmpty(localObject))
      UserDictionarySettings.deleteWord(str1, localObject, localContentResolver);
    String str3 = str1.toString();
    boolean bool = TextUtils.isEmpty(this.mLocale);
    Locale localLocale = null;
    if (bool);
    while (true)
    {
      UserDictionary.Words.addWord(paramContext, str3, 250, localObject, localLocale);
      return 0;
      localLocale = Utils.createLocaleFromString(this.mLocale);
    }
  }

  void delete(Context paramContext)
  {
    if ((this.mMode == 0) && (!TextUtils.isEmpty(this.mOldWord)))
    {
      ContentResolver localContentResolver = paramContext.getContentResolver();
      UserDictionarySettings.deleteWord(this.mOldWord, this.mOldShortcut, localContentResolver);
    }
  }

  public String getCurrentUserDictionaryLocale()
  {
    return this.mLocale;
  }

  public ArrayList<LocaleRenderer> getLocalesList(Activity paramActivity)
  {
    TreeSet localTreeSet = UserDictionaryList.getUserDictionaryLocalesSet(paramActivity);
    localTreeSet.remove(this.mLocale);
    String str = Locale.getDefault().toString();
    localTreeSet.remove(str);
    localTreeSet.remove("");
    ArrayList localArrayList = new ArrayList();
    addLocaleDisplayNameToList(paramActivity, localArrayList, this.mLocale);
    if (!str.equals(this.mLocale))
      addLocaleDisplayNameToList(paramActivity, localArrayList, str);
    Iterator localIterator = localTreeSet.iterator();
    while (localIterator.hasNext())
      addLocaleDisplayNameToList(paramActivity, localArrayList, (String)localIterator.next());
    if (!"".equals(this.mLocale))
      addLocaleDisplayNameToList(paramActivity, localArrayList, "");
    localArrayList.add(new LocaleRenderer(paramActivity, null));
    return localArrayList;
  }

  void saveStateIntoBundle(Bundle paramBundle)
  {
    paramBundle.putString("word", this.mWordEditText.getText().toString());
    paramBundle.putString("originalWord", this.mOldWord);
    if (this.mShortcutEditText != null)
      paramBundle.putString("shortcut", this.mShortcutEditText.getText().toString());
    if (this.mOldShortcut != null)
      paramBundle.putString("originalShortcut", this.mOldShortcut);
    paramBundle.putString("locale", this.mLocale);
  }

  void updateLocale(String paramString)
  {
    if (paramString == null)
      paramString = Locale.getDefault().toString();
    this.mLocale = paramString;
  }

  public static class LocaleRenderer
  {
    private final String mDescription;
    private final String mLocaleString;

    public LocaleRenderer(Context paramContext, String paramString)
    {
      this.mLocaleString = paramString;
      if (paramString == null)
      {
        this.mDescription = paramContext.getString(2131428546);
        return;
      }
      if ("".equals(paramString))
      {
        this.mDescription = paramContext.getString(2131428545);
        return;
      }
      this.mDescription = Utils.createLocaleFromString(paramString).getDisplayName();
    }

    public String getLocaleString()
    {
      return this.mLocaleString;
    }

    public boolean isMoreLanguages()
    {
      return this.mLocaleString == null;
    }

    public String toString()
    {
      return this.mDescription;
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.inputmethod.UserDictionaryAddWordContents
 * JD-Core Version:    0.6.2
 */