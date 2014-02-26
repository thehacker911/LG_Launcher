package com.android.settings.users;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.RestrictionEntry;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.IPackageManager.Stub;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ParceledListSlice;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.content.pm.UserInfo;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceGroup;
import android.preference.SwitchPreference;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import com.android.settings.SettingsPreferenceFragment;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

public class AppRestrictionsFragment extends SettingsPreferenceFragment
  implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener, View.OnClickListener
{
  private static final String TAG = AppRestrictionsFragment.class.getSimpleName();
  private PreferenceGroup mAppList;
  private boolean mAppListChanged;
  private AsyncTask mAppLoadingTask;
  private int mCustomRequestCode = 1000;
  private HashMap<Integer, AppRestrictionsPreference> mCustomRequestMap = new HashMap();
  private boolean mFirstTime = true;
  protected IPackageManager mIPm;
  private boolean mNewUser;
  protected PackageManager mPackageManager;
  private BroadcastReceiver mPackageObserver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      AppRestrictionsFragment.this.onPackageChanged(paramAnonymousIntent);
    }
  };
  protected boolean mRestrictedProfile;
  HashMap<String, Boolean> mSelectedPackages = new HashMap();
  private PackageInfo mSysPackageInfo;
  protected UserHandle mUser;
  private List<ApplicationInfo> mUserApps;
  private BroadcastReceiver mUserBackgrounding = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if (AppRestrictionsFragment.this.mAppListChanged)
        AppRestrictionsFragment.this.applyUserAppsStates();
    }
  };
  protected UserManager mUserManager;
  private List<SelectableAppInfo> mVisibleApps;

  private void addSystemApps(List<SelectableAppInfo> paramList, Intent paramIntent, Set<String> paramSet)
  {
    if (getActivity() == null);
    while (true)
    {
      return;
      PackageManager localPackageManager = this.mPackageManager;
      Iterator localIterator = localPackageManager.queryIntentActivities(paramIntent, 8704).iterator();
      while (localIterator.hasNext())
      {
        ResolveInfo localResolveInfo = (ResolveInfo)localIterator.next();
        if ((localResolveInfo.activityInfo != null) && (localResolveInfo.activityInfo.applicationInfo != null))
        {
          String str = localResolveInfo.activityInfo.packageName;
          int i = localResolveInfo.activityInfo.applicationInfo.flags;
          if ((((i & 0x1) != 0) || ((i & 0x80) != 0)) && (!paramSet.contains(str)))
          {
            int j = localPackageManager.getApplicationEnabledSetting(str);
            if ((j == 4) || (j == 2))
            {
              ApplicationInfo localApplicationInfo = getAppInfoForUser(str, 0, this.mUser);
              if ((localApplicationInfo == null) || ((0x800000 & localApplicationInfo.flags) == 0));
            }
            else
            {
              SelectableAppInfo localSelectableAppInfo = new SelectableAppInfo();
              localSelectableAppInfo.packageName = localResolveInfo.activityInfo.packageName;
              localSelectableAppInfo.appName = localResolveInfo.activityInfo.applicationInfo.loadLabel(localPackageManager);
              localSelectableAppInfo.icon = localResolveInfo.activityInfo.loadIcon(localPackageManager);
              localSelectableAppInfo.activityName = localResolveInfo.activityInfo.loadLabel(localPackageManager);
              if (localSelectableAppInfo.activityName == null)
                localSelectableAppInfo.activityName = localSelectableAppInfo.appName;
              paramList.add(localSelectableAppInfo);
            }
          }
        }
      }
    }
  }

  private void addSystemImes(Set<String> paramSet)
  {
    Activity localActivity = getActivity();
    if (localActivity == null);
    while (true)
    {
      return;
      Iterator localIterator = ((InputMethodManager)localActivity.getSystemService("input_method")).getInputMethodList().iterator();
      while (localIterator.hasNext())
      {
        InputMethodInfo localInputMethodInfo = (InputMethodInfo)localIterator.next();
        try
        {
          if ((localInputMethodInfo.isDefault(localActivity)) && (isSystemPackage(localInputMethodInfo.getPackageName())))
            paramSet.add(localInputMethodInfo.getPackageName());
        }
        catch (Resources.NotFoundException localNotFoundException)
        {
        }
      }
    }
  }

  // ERROR //
  private void applyUserAppState(String paramString, boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 178	com/android/settings/users/AppRestrictionsFragment:mUser	Landroid/os/UserHandle;
    //   4: invokevirtual 252	android/os/UserHandle:getIdentifier	()I
    //   7: istore_3
    //   8: iload_2
    //   9: ifeq +108 -> 117
    //   12: aload_0
    //   13: getfield 254	com/android/settings/users/AppRestrictionsFragment:mIPm	Landroid/content/pm/IPackageManager;
    //   16: aload_1
    //   17: sipush 8192
    //   20: iload_3
    //   21: invokeinterface 260 4 0
    //   26: astore 7
    //   28: aload 7
    //   30: ifnull +22 -> 52
    //   33: aload 7
    //   35: getfield 263	android/content/pm/ApplicationInfo:enabled	Z
    //   38: ifeq +14 -> 52
    //   41: ldc 183
    //   43: aload 7
    //   45: getfield 166	android/content/pm/ApplicationInfo:flags	I
    //   48: iand
    //   49: ifne +21 -> 70
    //   52: aload_0
    //   53: getfield 254	com/android/settings/users/AppRestrictionsFragment:mIPm	Landroid/content/pm/IPackageManager;
    //   56: aload_1
    //   57: aload_0
    //   58: getfield 178	com/android/settings/users/AppRestrictionsFragment:mUser	Landroid/os/UserHandle;
    //   61: invokevirtual 252	android/os/UserHandle:getIdentifier	()I
    //   64: invokeinterface 267 3 0
    //   69: pop
    //   70: aload 7
    //   72: ifnull +44 -> 116
    //   75: ldc_w 268
    //   78: aload 7
    //   80: getfield 166	android/content/pm/ApplicationInfo:flags	I
    //   83: iand
    //   84: ifeq +32 -> 116
    //   87: ldc 183
    //   89: aload 7
    //   91: getfield 166	android/content/pm/ApplicationInfo:flags	I
    //   94: iand
    //   95: ifeq +21 -> 116
    //   98: aload_0
    //   99: aload_1
    //   100: invokespecial 272	com/android/settings/users/AppRestrictionsFragment:disableUiForPackage	(Ljava/lang/String;)V
    //   103: aload_0
    //   104: getfield 254	com/android/settings/users/AppRestrictionsFragment:mIPm	Landroid/content/pm/IPackageManager;
    //   107: aload_1
    //   108: iconst_0
    //   109: iload_3
    //   110: invokeinterface 276 4 0
    //   115: pop
    //   116: return
    //   117: aload_0
    //   118: getfield 254	com/android/settings/users/AppRestrictionsFragment:mIPm	Landroid/content/pm/IPackageManager;
    //   121: aload_1
    //   122: iconst_0
    //   123: iload_3
    //   124: invokeinterface 260 4 0
    //   129: ifnull -13 -> 116
    //   132: aload_0
    //   133: getfield 278	com/android/settings/users/AppRestrictionsFragment:mRestrictedProfile	Z
    //   136: ifeq +23 -> 159
    //   139: aload_0
    //   140: getfield 254	com/android/settings/users/AppRestrictionsFragment:mIPm	Landroid/content/pm/IPackageManager;
    //   143: aload_1
    //   144: aconst_null
    //   145: aload_0
    //   146: getfield 178	com/android/settings/users/AppRestrictionsFragment:mUser	Landroid/os/UserHandle;
    //   149: invokevirtual 252	android/os/UserHandle:getIdentifier	()I
    //   152: iconst_4
    //   153: invokeinterface 282 5 0
    //   158: return
    //   159: aload_0
    //   160: aload_1
    //   161: invokespecial 272	com/android/settings/users/AppRestrictionsFragment:disableUiForPackage	(Ljava/lang/String;)V
    //   164: aload_0
    //   165: getfield 254	com/android/settings/users/AppRestrictionsFragment:mIPm	Landroid/content/pm/IPackageManager;
    //   168: aload_1
    //   169: iconst_1
    //   170: iload_3
    //   171: invokeinterface 276 4 0
    //   176: pop
    //   177: return
    //   178: astore 6
    //   180: return
    //   181: astore 4
    //   183: return
    //
    // Exception table:
    //   from	to	target	type
    //   12	28	178	android/os/RemoteException
    //   33	52	178	android/os/RemoteException
    //   52	70	178	android/os/RemoteException
    //   75	116	178	android/os/RemoteException
    //   117	158	181	android/os/RemoteException
    //   159	177	181	android/os/RemoteException
  }

  private void applyUserAppsStates()
  {
    int i = this.mUser.getIdentifier();
    if ((!this.mUserManager.getUserInfo(i).isRestricted()) && (i != UserHandle.myUserId()))
      Log.e(TAG, "Cannot apply application restrictions on another user!");
    while (true)
    {
      return;
      Iterator localIterator = this.mSelectedPackages.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        applyUserAppState((String)localEntry.getKey(), ((Boolean)localEntry.getValue()).booleanValue());
      }
    }
  }

  private void disableUiForPackage(String paramString)
  {
    AppRestrictionsPreference localAppRestrictionsPreference = (AppRestrictionsPreference)findPreference(getKeyForPackage(paramString));
    if (localAppRestrictionsPreference != null)
      localAppRestrictionsPreference.setEnabled(false);
  }

  private void fetchAndMergeApps()
  {
    this.mAppList.setOrderingAsAdded(false);
    this.mVisibleApps = new ArrayList();
    if (getActivity() == null);
    while (true)
    {
      return;
      PackageManager localPackageManager = this.mPackageManager;
      IPackageManager localIPackageManager = this.mIPm;
      HashSet localHashSet1 = new HashSet();
      addSystemImes(localHashSet1);
      Intent localIntent1 = new Intent("android.intent.action.MAIN");
      localIntent1.addCategory("android.intent.category.LAUNCHER");
      addSystemApps(this.mVisibleApps, localIntent1, localHashSet1);
      Intent localIntent2 = new Intent("android.appwidget.action.APPWIDGET_UPDATE");
      addSystemApps(this.mVisibleApps, localIntent2, localHashSet1);
      Iterator localIterator1 = localPackageManager.getInstalledApplications(8192).iterator();
      while (localIterator1.hasNext())
      {
        ApplicationInfo localApplicationInfo2 = (ApplicationInfo)localIterator1.next();
        if ((0x800000 & localApplicationInfo2.flags) != 0)
          if (((0x1 & localApplicationInfo2.flags) == 0) && ((0x80 & localApplicationInfo2.flags) == 0))
          {
            SelectableAppInfo localSelectableAppInfo4 = new SelectableAppInfo();
            localSelectableAppInfo4.packageName = localApplicationInfo2.packageName;
            localSelectableAppInfo4.appName = localApplicationInfo2.loadLabel(localPackageManager);
            localSelectableAppInfo4.activityName = localSelectableAppInfo4.appName;
            localSelectableAppInfo4.icon = localApplicationInfo2.loadIcon(localPackageManager);
            this.mVisibleApps.add(localSelectableAppInfo4);
          }
          else
          {
            try
            {
              PackageInfo localPackageInfo = localPackageManager.getPackageInfo(localApplicationInfo2.packageName, 0);
              if ((this.mRestrictedProfile) && (localPackageInfo.requiredAccountType != null) && (localPackageInfo.restrictedAccountType == null))
                this.mSelectedPackages.put(localApplicationInfo2.packageName, Boolean.valueOf(false));
            }
            catch (PackageManager.NameNotFoundException localNameNotFoundException)
            {
            }
          }
      }
      this.mUserApps = null;
      try
      {
        this.mUserApps = localIPackageManager.getInstalledApplications(8192, this.mUser.getIdentifier()).getList();
        label328: if (this.mUserApps != null)
        {
          Iterator localIterator3 = this.mUserApps.iterator();
          while (localIterator3.hasNext())
          {
            ApplicationInfo localApplicationInfo1 = (ApplicationInfo)localIterator3.next();
            if (((0x800000 & localApplicationInfo1.flags) != 0) && ((0x1 & localApplicationInfo1.flags) == 0) && ((0x80 & localApplicationInfo1.flags) == 0))
            {
              SelectableAppInfo localSelectableAppInfo3 = new SelectableAppInfo();
              localSelectableAppInfo3.packageName = localApplicationInfo1.packageName;
              localSelectableAppInfo3.appName = localApplicationInfo1.loadLabel(localPackageManager);
              localSelectableAppInfo3.activityName = localSelectableAppInfo3.appName;
              localSelectableAppInfo3.icon = localApplicationInfo1.loadIcon(localPackageManager);
              this.mVisibleApps.add(localSelectableAppInfo3);
            }
          }
        }
        List localList = this.mVisibleApps;
        AppLabelComparator localAppLabelComparator = new AppLabelComparator(null);
        Collections.sort(localList, localAppLabelComparator);
        HashSet localHashSet2 = new HashSet();
        int i = -1 + this.mVisibleApps.size();
        if (i >= 0)
        {
          SelectableAppInfo localSelectableAppInfo2 = (SelectableAppInfo)this.mVisibleApps.get(i);
          String str = localSelectableAppInfo2.packageName + "+" + localSelectableAppInfo2.activityName;
          if ((!TextUtils.isEmpty(localSelectableAppInfo2.packageName)) && (!TextUtils.isEmpty(localSelectableAppInfo2.activityName)) && (localHashSet2.contains(str)))
            this.mVisibleApps.remove(i);
          while (true)
          {
            i--;
            break;
            localHashSet2.add(str);
          }
        }
        HashMap localHashMap = new HashMap();
        Iterator localIterator2 = this.mVisibleApps.iterator();
        while (localIterator2.hasNext())
        {
          SelectableAppInfo localSelectableAppInfo1 = (SelectableAppInfo)localIterator2.next();
          if (localHashMap.containsKey(localSelectableAppInfo1.packageName))
            localSelectableAppInfo1.masterEntry = ((SelectableAppInfo)localHashMap.get(localSelectableAppInfo1.packageName));
          else
            localHashMap.put(localSelectableAppInfo1.packageName, localSelectableAppInfo1);
        }
      }
      catch (RemoteException localRemoteException)
      {
        break label328;
      }
    }
  }

  private String findInArray(String[] paramArrayOfString1, String[] paramArrayOfString2, String paramString)
  {
    for (int i = 0; ; i++)
      if (i < paramArrayOfString2.length)
      {
        if (paramArrayOfString2[i].equals(paramString))
          paramString = paramArrayOfString1[i];
      }
      else
        return paramString;
  }

  private int generateCustomActivityRequestCode(AppRestrictionsPreference paramAppRestrictionsPreference)
  {
    this.mCustomRequestCode = (1 + this.mCustomRequestCode);
    this.mCustomRequestMap.put(Integer.valueOf(this.mCustomRequestCode), paramAppRestrictionsPreference);
    return this.mCustomRequestCode;
  }

  private ApplicationInfo getAppInfoForUser(String paramString, int paramInt, UserHandle paramUserHandle)
  {
    try
    {
      ApplicationInfo localApplicationInfo = this.mIPm.getApplicationInfo(paramString, paramInt, paramUserHandle.getIdentifier());
      return localApplicationInfo;
    }
    catch (RemoteException localRemoteException)
    {
    }
    return null;
  }

  private String getKeyForPackage(String paramString)
  {
    return "pkg_" + paramString;
  }

  private boolean isAppEnabledForUser(PackageInfo paramPackageInfo)
  {
    if (paramPackageInfo == null);
    int i;
    do
    {
      return false;
      i = paramPackageInfo.applicationInfo.flags;
    }
    while (((0x800000 & i) == 0) || ((0x8000000 & i) != 0));
    return true;
  }

  private boolean isPlatformSigned(PackageInfo paramPackageInfo)
  {
    boolean bool1 = false;
    if (paramPackageInfo != null)
    {
      Signature[] arrayOfSignature = paramPackageInfo.signatures;
      bool1 = false;
      if (arrayOfSignature != null)
      {
        boolean bool2 = this.mSysPackageInfo.signatures[0].equals(paramPackageInfo.signatures[0]);
        bool1 = false;
        if (bool2)
          bool1 = true;
      }
    }
    return bool1;
  }

  private boolean isSystemPackage(String paramString)
  {
    try
    {
      PackageInfo localPackageInfo = this.mPackageManager.getPackageInfo(paramString, 0);
      if (localPackageInfo.applicationInfo == null)
        return false;
      int i = localPackageInfo.applicationInfo.flags;
      if (((i & 0x1) != 0) || ((i & 0x80) != 0))
        return true;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
    }
    return false;
  }

  private void onAppSettingsIconClicked(AppRestrictionsPreference paramAppRestrictionsPreference)
  {
    boolean bool = true;
    if (paramAppRestrictionsPreference.getKey().startsWith("pkg_"))
    {
      if (!paramAppRestrictionsPreference.isPanelOpen())
        break label40;
      removeRestrictionsForApp(paramAppRestrictionsPreference);
      if (paramAppRestrictionsPreference.isPanelOpen())
        break label98;
    }
    while (true)
    {
      paramAppRestrictionsPreference.setPanelOpen(bool);
      return;
      label40: String str = paramAppRestrictionsPreference.getKey().substring("pkg_".length());
      if (str.equals(getActivity().getPackageName()))
      {
        onRestrictionsReceived(paramAppRestrictionsPreference, str, RestrictionUtils.getRestrictions(getActivity(), this.mUser));
        break;
      }
      requestRestrictionsForApp(str, paramAppRestrictionsPreference, bool);
      break;
      label98: bool = false;
    }
  }

  private void onPackageChanged(Intent paramIntent)
  {
    String str = paramIntent.getAction();
    AppRestrictionsPreference localAppRestrictionsPreference = (AppRestrictionsPreference)findPreference(getKeyForPackage(paramIntent.getData().getSchemeSpecificPart()));
    if (localAppRestrictionsPreference == null);
    while (((!"android.intent.action.PACKAGE_ADDED".equals(str)) || (!localAppRestrictionsPreference.isChecked())) && ((!"android.intent.action.PACKAGE_REMOVED".equals(str)) || (localAppRestrictionsPreference.isChecked())))
      return;
    localAppRestrictionsPreference.setEnabled(true);
  }

  private void onRestrictionsReceived(AppRestrictionsPreference paramAppRestrictionsPreference, String paramString, ArrayList<RestrictionEntry> paramArrayList)
  {
    removeRestrictionsForApp(paramAppRestrictionsPreference);
    Context localContext = paramAppRestrictionsPreference.getContext();
    int i = 1;
    Iterator localIterator = paramArrayList.iterator();
    label465: 
    while (localIterator.hasNext())
    {
      RestrictionEntry localRestrictionEntry = (RestrictionEntry)localIterator.next();
      int j = localRestrictionEntry.getType();
      Object localObject = null;
      switch (j)
      {
      default:
      case 1:
      case 2:
      case 3:
      case 4:
      }
      while (true)
      {
        if (localObject == null)
          break label465;
        ((Preference)localObject).setPersistent(false);
        ((Preference)localObject).setOrder(i + paramAppRestrictionsPreference.getOrder());
        ((Preference)localObject).setKey(paramAppRestrictionsPreference.getKey().substring("pkg_".length()) + ";" + localRestrictionEntry.getKey());
        this.mAppList.addPreference((Preference)localObject);
        ((Preference)localObject).setOnPreferenceChangeListener(this);
        paramAppRestrictionsPreference.mChildren.add(localObject);
        i++;
        break;
        localObject = new CheckBoxPreference(localContext);
        ((Preference)localObject).setTitle(localRestrictionEntry.getTitle());
        ((Preference)localObject).setSummary(localRestrictionEntry.getDescription());
        ((CheckBoxPreference)localObject).setChecked(localRestrictionEntry.getSelectedState());
        continue;
        localObject = new ListPreference(localContext);
        ((Preference)localObject).setTitle(localRestrictionEntry.getTitle());
        String str = localRestrictionEntry.getSelectedString();
        if (str == null)
          str = localRestrictionEntry.getDescription();
        ((Preference)localObject).setSummary(findInArray(localRestrictionEntry.getChoiceEntries(), localRestrictionEntry.getChoiceValues(), str));
        ((ListPreference)localObject).setEntryValues(localRestrictionEntry.getChoiceValues());
        ((ListPreference)localObject).setEntries(localRestrictionEntry.getChoiceEntries());
        ((ListPreference)localObject).setValue(str);
        ((ListPreference)localObject).setDialogTitle(localRestrictionEntry.getTitle());
        continue;
        localObject = new MultiSelectListPreference(localContext);
        ((Preference)localObject).setTitle(localRestrictionEntry.getTitle());
        ((MultiSelectListPreference)localObject).setEntryValues(localRestrictionEntry.getChoiceValues());
        ((MultiSelectListPreference)localObject).setEntries(localRestrictionEntry.getChoiceEntries());
        HashSet localHashSet = new HashSet();
        String[] arrayOfString = localRestrictionEntry.getAllSelectedStrings();
        int k = arrayOfString.length;
        for (int m = 0; m < k; m++)
          localHashSet.add(arrayOfString[m]);
        ((MultiSelectListPreference)localObject).setValues(localHashSet);
        ((MultiSelectListPreference)localObject).setDialogTitle(localRestrictionEntry.getTitle());
      }
    }
    paramAppRestrictionsPreference.setRestrictions(paramArrayList);
    if ((i == 1) && (paramAppRestrictionsPreference.isImmutable()) && (paramAppRestrictionsPreference.isChecked()))
      this.mAppList.removePreference(paramAppRestrictionsPreference);
  }

  private void populateApps()
  {
    Activity localActivity = getActivity();
    if (localActivity == null);
    while (true)
    {
      return;
      PackageManager localPackageManager = this.mPackageManager;
      IPackageManager localIPackageManager = this.mIPm;
      this.mAppList.removeAll();
      List localList = localPackageManager.queryBroadcastReceivers(new Intent("android.intent.action.GET_RESTRICTION_ENTRIES"), 0);
      int i = 0;
      label70: SelectableAppInfo localSelectableAppInfo;
      String str;
      boolean bool1;
      AppRestrictionsPreference localAppRestrictionsPreference;
      boolean bool2;
      Drawable localDrawable;
      label154: boolean bool3;
      if (this.mVisibleApps.size() > 0)
      {
        Iterator localIterator = this.mVisibleApps.iterator();
        while (true)
          if (localIterator.hasNext())
          {
            localSelectableAppInfo = (SelectableAppInfo)localIterator.next();
            str = localSelectableAppInfo.packageName;
            if (str != null)
            {
              bool1 = str.equals(localActivity.getPackageName());
              localAppRestrictionsPreference = new AppRestrictionsPreference(localActivity, this);
              bool2 = resolveInfoListHasPackage(localList, str);
              if (localSelectableAppInfo.icon != null)
              {
                localDrawable = localSelectableAppInfo.icon.mutate();
                localAppRestrictionsPreference.setIcon(localDrawable);
                localAppRestrictionsPreference.setChecked(false);
                localAppRestrictionsPreference.setTitle(localSelectableAppInfo.activityName);
                if (localSelectableAppInfo.masterEntry != null)
                {
                  Object[] arrayOfObject = new Object[1];
                  arrayOfObject[0] = localSelectableAppInfo.masterEntry.activityName;
                  localAppRestrictionsPreference.setSummary(localActivity.getString(2131429277, arrayOfObject));
                }
                localAppRestrictionsPreference.setKey(getKeyForPackage(str));
                if ((!bool2) && (!bool1))
                  break label505;
                bool3 = true;
                label241: localAppRestrictionsPreference.setSettingsEnabled(bool3);
                localAppRestrictionsPreference.setPersistent(false);
                localAppRestrictionsPreference.setOnPreferenceChangeListener(this);
                localAppRestrictionsPreference.setOnPreferenceClickListener(this);
              }
            }
          }
      }
      try
      {
        PackageInfo localPackageInfo2 = localIPackageManager.getPackageInfo(str, 8256, this.mUser.getIdentifier());
        localPackageInfo1 = localPackageInfo2;
        if ((localPackageInfo1 != null) && ((localPackageInfo1.requiredForAllUsers) || (isPlatformSigned(localPackageInfo1))))
        {
          localAppRestrictionsPreference.setChecked(true);
          localAppRestrictionsPreference.setImmutable(true);
          if ((!bool2) && (!bool1))
            break label70;
          if (bool2)
            requestRestrictionsForApp(str, localAppRestrictionsPreference, false);
          label348: if ((this.mRestrictedProfile) && (localPackageInfo1.requiredAccountType != null) && (localPackageInfo1.restrictedAccountType == null))
          {
            localAppRestrictionsPreference.setChecked(false);
            localAppRestrictionsPreference.setImmutable(true);
            localAppRestrictionsPreference.setSummary(2131429278);
          }
          if ((this.mRestrictedProfile) && (localPackageInfo1.restrictedAccountType != null))
            localAppRestrictionsPreference.setSummary(2131429279);
          if (localSelectableAppInfo.masterEntry != null)
          {
            localAppRestrictionsPreference.setImmutable(true);
            localAppRestrictionsPreference.setChecked(((Boolean)this.mSelectedPackages.get(str)).booleanValue());
          }
          this.mAppList.addPreference(localAppRestrictionsPreference);
          if (!bool1)
            break label536;
          localAppRestrictionsPreference.setOrder(100);
        }
        while (true)
        {
          this.mSelectedPackages.put(str, Boolean.valueOf(localAppRestrictionsPreference.isChecked()));
          this.mAppListChanged = true;
          i++;
          break;
          localDrawable = null;
          break label154;
          label505: bool3 = false;
          break label241;
          if ((this.mNewUser) || (!isAppEnabledForUser(localPackageInfo1)))
            break label348;
          localAppRestrictionsPreference.setChecked(true);
          break label348;
          label536: localAppRestrictionsPreference.setOrder(100 * (i + 2));
        }
        if ((!this.mNewUser) || (!this.mFirstTime))
          continue;
        this.mFirstTime = false;
        applyUserAppsStates();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        while (true)
          PackageInfo localPackageInfo1 = null;
      }
    }
  }

  private void removeRestrictionsForApp(AppRestrictionsPreference paramAppRestrictionsPreference)
  {
    Iterator localIterator = paramAppRestrictionsPreference.mChildren.iterator();
    while (localIterator.hasNext())
    {
      Preference localPreference = (Preference)localIterator.next();
      this.mAppList.removePreference(localPreference);
    }
    paramAppRestrictionsPreference.mChildren.clear();
  }

  private void requestRestrictionsForApp(String paramString, AppRestrictionsPreference paramAppRestrictionsPreference, boolean paramBoolean)
  {
    Bundle localBundle = this.mUserManager.getApplicationRestrictions(paramString, this.mUser);
    Intent localIntent = new Intent("android.intent.action.GET_RESTRICTION_ENTRIES");
    localIntent.setPackage(paramString);
    localIntent.putExtra("android.intent.extra.restrictions_bundle", localBundle);
    localIntent.addFlags(32);
    getActivity().sendOrderedBroadcast(localIntent, null, new RestrictionsResultReceiver(paramString, paramAppRestrictionsPreference, paramBoolean), null, -1, null, null);
  }

  private boolean resolveInfoListHasPackage(List<ResolveInfo> paramList, String paramString)
  {
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
      if (((ResolveInfo)localIterator.next()).activityInfo.packageName.equals(paramString))
        return true;
    return false;
  }

  private void updateAllEntries(String paramString, boolean paramBoolean)
  {
    for (int i = 0; i < this.mAppList.getPreferenceCount(); i++)
    {
      Preference localPreference = this.mAppList.getPreference(i);
      if (((localPreference instanceof AppRestrictionsPreference)) && (paramString.equals(localPreference.getKey())))
        ((AppRestrictionsPreference)localPreference).setChecked(paramBoolean);
    }
  }

  protected PreferenceGroup getAppPreferenceGroup()
  {
    return getPreferenceScreen();
  }

  protected Drawable getCircularUserIcon()
  {
    Bitmap localBitmap = this.mUserManager.getUserIcon(this.mUser.getIdentifier());
    if (localBitmap == null)
      return null;
    return CircleFramedDrawable.getInstance(getActivity(), localBitmap);
  }

  protected void init(Bundle paramBundle)
  {
    if (paramBundle != null)
      this.mUser = new UserHandle(paramBundle.getInt("user_id"));
    while (true)
    {
      if (this.mUser == null)
        this.mUser = Process.myUserHandle();
      this.mPackageManager = getActivity().getPackageManager();
      this.mIPm = IPackageManager.Stub.asInterface(ServiceManager.getService("package"));
      this.mUserManager = ((UserManager)getActivity().getSystemService("user"));
      this.mRestrictedProfile = this.mUserManager.getUserInfo(this.mUser.getIdentifier()).isRestricted();
      try
      {
        this.mSysPackageInfo = this.mPackageManager.getPackageInfo("android", 64);
        label114: addPreferencesFromResource(2131034117);
        this.mAppList = getAppPreferenceGroup();
        return;
        Bundle localBundle = getArguments();
        if (localBundle == null)
          continue;
        if (localBundle.containsKey("user_id"))
          this.mUser = new UserHandle(localBundle.getInt("user_id"));
        this.mNewUser = localBundle.getBoolean("new_user", false);
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException)
      {
        break label114;
      }
    }
  }

  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
    AppRestrictionsPreference localAppRestrictionsPreference = (AppRestrictionsPreference)this.mCustomRequestMap.get(Integer.valueOf(paramInt1));
    if (localAppRestrictionsPreference == null)
    {
      Log.w(TAG, "Unknown requestCode " + paramInt1);
      return;
    }
    String str;
    Bundle localBundle;
    if (paramInt2 == -1)
    {
      str = localAppRestrictionsPreference.getKey().substring("pkg_".length());
      ArrayList localArrayList = paramIntent.getParcelableArrayListExtra("android.intent.extra.restrictions_list");
      localBundle = paramIntent.getBundleExtra("android.intent.extra.restrictions_bundle");
      if (localArrayList == null)
        break label138;
      localAppRestrictionsPreference.setRestrictions(localArrayList);
      this.mUserManager.setApplicationRestrictions(str, RestrictionUtils.restrictionsToBundle(localArrayList), this.mUser);
    }
    while (true)
    {
      this.mCustomRequestMap.remove(Integer.valueOf(paramInt1));
      return;
      label138: if (localBundle != null)
        this.mUserManager.setApplicationRestrictions(str, localBundle, this.mUser);
    }
  }

  public void onClick(View paramView)
  {
    AppRestrictionsPreference localAppRestrictionsPreference;
    if ((paramView.getTag() instanceof AppRestrictionsPreference))
    {
      localAppRestrictionsPreference = (AppRestrictionsPreference)paramView.getTag();
      if (paramView.getId() != 2131230936)
        break label34;
      onAppSettingsIconClicked(localAppRestrictionsPreference);
    }
    label34: 
    while (localAppRestrictionsPreference.isImmutable())
      return;
    if (!localAppRestrictionsPreference.isChecked());
    for (boolean bool = true; ; bool = false)
    {
      localAppRestrictionsPreference.setChecked(bool);
      String str = localAppRestrictionsPreference.getKey().substring("pkg_".length());
      this.mSelectedPackages.put(str, Boolean.valueOf(localAppRestrictionsPreference.isChecked()));
      if ((localAppRestrictionsPreference.isChecked()) && (localAppRestrictionsPreference.hasSettings) && (localAppRestrictionsPreference.restrictions == null))
        requestRestrictionsForApp(str, localAppRestrictionsPreference, false);
      this.mAppListChanged = true;
      if (!this.mRestrictedProfile)
        applyUserAppState(str, localAppRestrictionsPreference.isChecked());
      updateAllEntries(localAppRestrictionsPreference.getKey(), localAppRestrictionsPreference.isChecked());
      return;
    }
  }

  public void onPause()
  {
    super.onPause();
    this.mNewUser = false;
    getActivity().unregisterReceiver(this.mUserBackgrounding);
    getActivity().unregisterReceiver(this.mPackageObserver);
    if (this.mAppListChanged)
      new Thread()
      {
        public void run()
        {
          AppRestrictionsFragment.this.applyUserAppsStates();
        }
      }
      .start();
  }

  public boolean onPreferenceChange(Preference paramPreference, Object paramObject)
  {
    String str1 = paramPreference.getKey();
    String str2;
    ArrayList localArrayList;
    RestrictionEntry localRestrictionEntry;
    if ((str1 != null) && (str1.contains(";")))
    {
      StringTokenizer localStringTokenizer = new StringTokenizer(str1, ";");
      str2 = localStringTokenizer.nextToken();
      String str3 = localStringTokenizer.nextToken();
      localArrayList = ((AppRestrictionsPreference)this.mAppList.findPreference("pkg_" + str2)).getRestrictions();
      if (localArrayList != null)
      {
        Iterator localIterator = localArrayList.iterator();
        while (localIterator.hasNext())
        {
          localRestrictionEntry = (RestrictionEntry)localIterator.next();
          if (localRestrictionEntry.getKey().equals(str3))
            switch (localRestrictionEntry.getType())
            {
            default:
              break;
            case 1:
              localRestrictionEntry.setSelectedState(((Boolean)paramObject).booleanValue());
              if (!str2.equals(getActivity().getPackageName()))
                break label288;
              RestrictionUtils.setRestrictions(getActivity(), localArrayList, this.mUser);
            case 2:
            case 3:
            case 4:
            }
        }
      }
    }
    while (true)
    {
      return true;
      ListPreference localListPreference = (ListPreference)paramPreference;
      localRestrictionEntry.setSelectedString((String)paramObject);
      localListPreference.setSummary(findInArray(localRestrictionEntry.getChoiceEntries(), localRestrictionEntry.getChoiceValues(), (String)paramObject));
      break;
      Set localSet = (Set)paramObject;
      String[] arrayOfString = new String[localSet.size()];
      localSet.toArray(arrayOfString);
      localRestrictionEntry.setAllSelectedStrings(arrayOfString);
      break;
      label288: this.mUserManager.setApplicationRestrictions(str2, RestrictionUtils.restrictionsToBundle(localArrayList), this.mUser);
    }
  }

  public boolean onPreferenceClick(Preference paramPreference)
  {
    if (paramPreference.getKey().startsWith("pkg_"))
    {
      AppRestrictionsPreference localAppRestrictionsPreference = (AppRestrictionsPreference)paramPreference;
      if (!localAppRestrictionsPreference.isImmutable())
      {
        String str = localAppRestrictionsPreference.getKey().substring("pkg_".length());
        boolean bool1 = localAppRestrictionsPreference.isChecked();
        boolean bool2 = false;
        if (!bool1)
          bool2 = true;
        localAppRestrictionsPreference.setChecked(bool2);
        this.mSelectedPackages.put(str, Boolean.valueOf(bool2));
        updateAllEntries(localAppRestrictionsPreference.getKey(), bool2);
        this.mAppListChanged = true;
        applyUserAppState(str, bool2);
      }
      return true;
    }
    return false;
  }

  public void onResume()
  {
    super.onResume();
    getActivity().registerReceiver(this.mUserBackgrounding, new IntentFilter("android.intent.action.USER_BACKGROUND"));
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.intent.action.PACKAGE_ADDED");
    localIntentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
    localIntentFilter.addDataScheme("package");
    getActivity().registerReceiver(this.mPackageObserver, localIntentFilter);
    this.mAppListChanged = false;
    if ((this.mAppLoadingTask == null) || (this.mAppLoadingTask.getStatus() == AsyncTask.Status.FINISHED))
      this.mAppLoadingTask = new AppLoadingTask(null).execute((Void[])null);
  }

  public void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    paramBundle.putInt("user_id", this.mUser.getIdentifier());
  }

  private class AppLabelComparator
    implements Comparator<AppRestrictionsFragment.SelectableAppInfo>
  {
    private AppLabelComparator()
    {
    }

    public int compare(AppRestrictionsFragment.SelectableAppInfo paramSelectableAppInfo1, AppRestrictionsFragment.SelectableAppInfo paramSelectableAppInfo2)
    {
      String str1 = paramSelectableAppInfo1.activityName.toString();
      String str2 = paramSelectableAppInfo2.activityName.toString();
      return str1.toLowerCase().compareTo(str2.toLowerCase());
    }
  }

  private class AppLoadingTask extends AsyncTask<Void, Void, Void>
  {
    private AppLoadingTask()
    {
    }

    protected Void doInBackground(Void[] paramArrayOfVoid)
    {
      AppRestrictionsFragment.this.fetchAndMergeApps();
      return null;
    }

    protected void onPostExecute(Void paramVoid)
    {
      AppRestrictionsFragment.this.populateApps();
    }

    protected void onPreExecute()
    {
    }
  }

  static class AppRestrictionsPreference extends SwitchPreference
  {
    private final ColorFilter grayscaleFilter;
    private boolean hasSettings;
    private boolean immutable;
    private View.OnClickListener listener;
    private List<Preference> mChildren = new ArrayList();
    private boolean panelOpen;
    private ArrayList<RestrictionEntry> restrictions;

    AppRestrictionsPreference(Context paramContext, View.OnClickListener paramOnClickListener)
    {
      super();
      setLayoutResource(2130968665);
      this.listener = paramOnClickListener;
      ColorMatrix localColorMatrix = new ColorMatrix();
      localColorMatrix.setSaturation(0.0F);
      localColorMatrix.getArray()[18] = 0.5F;
      this.grayscaleFilter = new ColorMatrixColorFilter(localColorMatrix);
    }

    private void setSettingsEnabled(boolean paramBoolean)
    {
      this.hasSettings = paramBoolean;
    }

    ArrayList<RestrictionEntry> getRestrictions()
    {
      return this.restrictions;
    }

    boolean isImmutable()
    {
      return this.immutable;
    }

    boolean isPanelOpen()
    {
      return this.panelOpen;
    }

    protected void onBindView(View paramView)
    {
      int i = 8;
      boolean bool1 = true;
      super.onBindView(paramView);
      View localView1 = paramView.findViewById(2131230936);
      int j;
      boolean bool2;
      label117: final Switch localSwitch;
      if (this.hasSettings)
      {
        j = 0;
        localView1.setVisibility(j);
        View localView2 = paramView.findViewById(2131230937);
        if (this.hasSettings)
          i = 0;
        localView2.setVisibility(i);
        localView1.setOnClickListener(this.listener);
        localView1.setTag(this);
        View localView3 = paramView.findViewById(2131230935);
        localView3.setOnClickListener(this.listener);
        localView3.setTag(this);
        ViewGroup localViewGroup = (ViewGroup)paramView.findViewById(16908312);
        if (isImmutable())
          break label184;
        bool2 = bool1;
        localViewGroup.setEnabled(bool2);
        if (localViewGroup.getChildCount() > 0)
        {
          localSwitch = (Switch)localViewGroup.getChildAt(0);
          if (isImmutable())
            break label190;
        }
      }
      while (true)
      {
        localSwitch.setEnabled(bool1);
        localSwitch.setTag(this);
        localSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
          public void onCheckedChanged(CompoundButton paramAnonymousCompoundButton, boolean paramAnonymousBoolean)
          {
            AppRestrictionsFragment.AppRestrictionsPreference.this.listener.onClick(localSwitch);
          }
        });
        return;
        j = i;
        break;
        label184: bool2 = false;
        break label117;
        label190: bool1 = false;
      }
    }

    public void setChecked(boolean paramBoolean)
    {
      if (paramBoolean)
        getIcon().setColorFilter(null);
      while (true)
      {
        super.setChecked(paramBoolean);
        return;
        getIcon().setColorFilter(this.grayscaleFilter);
      }
    }

    void setImmutable(boolean paramBoolean)
    {
      this.immutable = paramBoolean;
    }

    void setPanelOpen(boolean paramBoolean)
    {
      this.panelOpen = paramBoolean;
    }

    void setRestrictions(ArrayList<RestrictionEntry> paramArrayList)
    {
      this.restrictions = paramArrayList;
    }
  }

  class RestrictionsResultReceiver extends BroadcastReceiver
  {
    boolean invokeIfCustom;
    String packageName;
    AppRestrictionsFragment.AppRestrictionsPreference preference;

    RestrictionsResultReceiver(String paramAppRestrictionsPreference, AppRestrictionsFragment.AppRestrictionsPreference paramBoolean, boolean arg4)
    {
      this.packageName = paramAppRestrictionsPreference;
      this.preference = paramBoolean;
      boolean bool;
      this.invokeIfCustom = bool;
    }

    public void onReceive(Context paramContext, Intent paramIntent)
    {
      Bundle localBundle = getResultExtras(true);
      ArrayList localArrayList = localBundle.getParcelableArrayList("android.intent.extra.restrictions_list");
      Intent localIntent = (Intent)localBundle.getParcelable("android.intent.extra.restrictions_intent");
      if ((localArrayList != null) && (localIntent == null))
      {
        AppRestrictionsFragment.this.onRestrictionsReceived(this.preference, this.packageName, localArrayList);
        if (AppRestrictionsFragment.this.mRestrictedProfile)
          AppRestrictionsFragment.this.mUserManager.setApplicationRestrictions(this.packageName, RestrictionUtils.restrictionsToBundle(localArrayList), AppRestrictionsFragment.this.mUser);
      }
      do
      {
        do
          return;
        while (localIntent == null);
        this.preference.setRestrictions(localArrayList);
      }
      while ((!this.invokeIfCustom) || (!AppRestrictionsFragment.this.isResumed()));
      int i = AppRestrictionsFragment.this.generateCustomActivityRequestCode(this.preference);
      AppRestrictionsFragment.this.startActivityForResult(localIntent, i);
    }
  }

  static class SelectableAppInfo
  {
    CharSequence activityName;
    CharSequence appName;
    Drawable icon;
    SelectableAppInfo masterEntry;
    String packageName;

    public String toString()
    {
      return this.packageName + ": appName=" + this.appName + "; activityName=" + this.activityName + "; icon=" + this.icon + "; masterEntry=" + this.masterEntry;
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.users.AppRestrictionsFragment
 * JD-Core Version:    0.6.2
 */