package com.android.settings.inputmethod;

import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.content.Context;
import android.content.pm.UserInfo;
import android.os.RemoteException;
import android.util.Log;
import android.util.Slog;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import com.android.internal.inputmethod.InputMethodUtils;
import com.android.internal.inputmethod.InputMethodUtils.InputMethodSettings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class InputMethodSettingValuesWrapper
{
  private static final Locale ENGLISH_LOCALE = new Locale("en");
  private static final String TAG = InputMethodSettingValuesWrapper.class.getSimpleName();
  private static volatile InputMethodSettingValuesWrapper sInstance;
  private final HashSet<InputMethodInfo> mAsciiCapableEnabledImis = new HashSet();
  private final InputMethodManager mImm;
  private final ArrayList<InputMethodInfo> mMethodList = new ArrayList();
  private final HashMap<String, InputMethodInfo> mMethodMap = new HashMap();
  private final InputMethodUtils.InputMethodSettings mSettings = new InputMethodUtils.InputMethodSettings(paramContext.getResources(), paramContext.getContentResolver(), this.mMethodMap, this.mMethodList, getDefaultCurrentUserId());

  private InputMethodSettingValuesWrapper(Context paramContext)
  {
    this.mImm = ((InputMethodManager)paramContext.getSystemService("input_method"));
    refreshAllInputMethodAndSubtypes();
  }

  private static int getDefaultCurrentUserId()
  {
    try
    {
      int i = ActivityManagerNative.getDefault().getCurrentUser().id;
      return i;
    }
    catch (RemoteException localRemoteException)
    {
      Slog.w(TAG, "Couldn't get current user ID; guessing it's 0", localRemoteException);
    }
    return 0;
  }

  private int getEnabledValidSystemNonAuxAsciiCapableImeCount(Context paramContext)
  {
    int i = 0;
    synchronized (this.mMethodMap)
    {
      List localList = this.mSettings.getEnabledInputMethodListLocked();
      Iterator localIterator = localList.iterator();
      while (localIterator.hasNext())
        if (isValidSystemNonAuxAsciiCapableIme((InputMethodInfo)localIterator.next(), paramContext))
          i++;
    }
    if (i == 0)
      Log.w(TAG, "No \"enabledValidSystemNonAuxAsciiCapableIme\"s found.");
    return i;
  }

  public static InputMethodSettingValuesWrapper getInstance(Context paramContext)
  {
    if (sInstance == null);
    synchronized (TAG)
    {
      if (sInstance == null)
        sInstance = new InputMethodSettingValuesWrapper(paramContext);
      return sInstance;
    }
  }

  private boolean isEnabledImi(InputMethodInfo paramInputMethodInfo)
  {
    synchronized (this.mMethodMap)
    {
      List localList = this.mSettings.getEnabledInputMethodListLocked();
      Iterator localIterator = localList.iterator();
      while (localIterator.hasNext())
        if (((InputMethodInfo)localIterator.next()).getId().equals(paramInputMethodInfo.getId()))
          return true;
    }
    return false;
  }

  private void updateAsciiCapableEnabledImis()
  {
    while (true)
    {
      int j;
      synchronized (this.mMethodMap)
      {
        this.mAsciiCapableEnabledImis.clear();
        Iterator localIterator = this.mSettings.getEnabledInputMethodListLocked().iterator();
        if (!localIterator.hasNext())
          break;
        InputMethodInfo localInputMethodInfo = (InputMethodInfo)localIterator.next();
        int i = localInputMethodInfo.getSubtypeCount();
        j = 0;
        if (j >= i)
          continue;
        InputMethodSubtype localInputMethodSubtype = localInputMethodInfo.getSubtypeAt(j);
        if (("keyboard".equalsIgnoreCase(localInputMethodSubtype.getMode())) && (localInputMethodSubtype.isAsciiCapable()))
          this.mAsciiCapableEnabledImis.add(localInputMethodInfo);
      }
      j++;
    }
  }

  public CharSequence getCurrentInputMethodName(Context paramContext)
  {
    synchronized (this.mMethodMap)
    {
      InputMethodInfo localInputMethodInfo = (InputMethodInfo)this.mMethodMap.get(this.mSettings.getSelectedInputMethod());
      if (localInputMethodInfo == null)
      {
        Log.w(TAG, "Invalid selected imi: " + this.mSettings.getSelectedInputMethod());
        return "";
      }
      CharSequence localCharSequence = InputMethodUtils.getImeAndSubtypeDisplayName(paramContext, localInputMethodInfo, this.mImm.getCurrentInputMethodSubtype());
      return localCharSequence;
    }
  }

  public List<InputMethodInfo> getInputMethodList()
  {
    synchronized (this.mMethodMap)
    {
      ArrayList localArrayList = this.mMethodList;
      return localArrayList;
    }
  }

  public boolean isAlwaysCheckedIme(InputMethodInfo paramInputMethodInfo, Context paramContext)
  {
    boolean bool = isEnabledImi(paramInputMethodInfo);
    int i;
    synchronized (this.mMethodMap)
    {
      if ((this.mSettings.getEnabledInputMethodListLocked().size() <= 1) && (bool))
        return true;
      i = getEnabledValidSystemNonAuxAsciiCapableImeCount(paramContext);
      if (i > 1)
        return false;
    }
    if ((i == 1) && (!bool))
      return false;
    if (!InputMethodUtils.isSystemIme(paramInputMethodInfo))
      return false;
    return isValidSystemNonAuxAsciiCapableIme(paramInputMethodInfo, paramContext);
  }

  public boolean isValidSystemNonAuxAsciiCapableIme(InputMethodInfo paramInputMethodInfo, Context paramContext)
  {
    boolean bool = true;
    if (paramInputMethodInfo.isAuxiliaryIme())
      bool = false;
    while (InputMethodUtils.isValidSystemDefaultIme(bool, paramInputMethodInfo, paramContext))
      return bool;
    if (this.mAsciiCapableEnabledImis.isEmpty())
    {
      Log.w(TAG, "ascii capable subtype enabled imi not found. Fall back to English Keyboard subtype.");
      return InputMethodUtils.containsSubtypeOf(paramInputMethodInfo, ENGLISH_LOCALE.getLanguage(), "keyboard");
    }
    return this.mAsciiCapableEnabledImis.contains(paramInputMethodInfo);
  }

  public void refreshAllInputMethodAndSubtypes()
  {
    synchronized (this.mMethodMap)
    {
      this.mMethodList.clear();
      this.mMethodMap.clear();
      List localList = this.mImm.getInputMethodList();
      this.mMethodList.addAll(localList);
      Iterator localIterator = localList.iterator();
      if (localIterator.hasNext())
      {
        InputMethodInfo localInputMethodInfo = (InputMethodInfo)localIterator.next();
        this.mMethodMap.put(localInputMethodInfo.getId(), localInputMethodInfo);
      }
    }
    updateAsciiCapableEnabledImis();
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.inputmethod.InputMethodSettingValuesWrapper
 * JD-Core Version:    0.6.2
 */