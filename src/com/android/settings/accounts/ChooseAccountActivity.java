package com.android.settings.accounts;

import android.accounts.AccountManager;
import android.accounts.AuthenticatorDescription;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncAdapterType;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.util.Log;
import com.android.internal.util.CharSequences;
import com.google.android.collect.Maps;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public class ChooseAccountActivity extends PreferenceActivity
{
  private HashMap<String, ArrayList<String>> mAccountTypeToAuthorities = null;
  public HashSet<String> mAccountTypesFilter;
  private PreferenceGroup mAddAccountGroup;
  private AuthenticatorDescription[] mAuthDescs;
  private String[] mAuthorities;
  private final ArrayList<ProviderEntry> mProviderList = new ArrayList();
  private Map<String, AuthenticatorDescription> mTypeToAuthDescription = new HashMap();

  private void finishWithAccountType(String paramString)
  {
    Intent localIntent = new Intent();
    localIntent.putExtra("selected_account", paramString);
    setResult(-1, localIntent);
    finish();
  }

  private void onAuthDescriptionsUpdated()
  {
    int i = 0;
    if (i < this.mAuthDescs.length)
    {
      String str = this.mAuthDescs[i].type;
      CharSequence localCharSequence = getLabelForType(str);
      ArrayList localArrayList1 = getAuthoritiesForAccountType(str);
      int m = 1;
      int n;
      if ((this.mAuthorities != null) && (this.mAuthorities.length > 0) && (localArrayList1 != null))
      {
        n = 0;
        label64: int i1 = this.mAuthorities.length;
        m = 0;
        if (n < i1)
        {
          if (!localArrayList1.contains(this.mAuthorities[n]))
            break label164;
          m = 1;
        }
      }
      if ((m != 0) && (this.mAccountTypesFilter != null) && (!this.mAccountTypesFilter.contains(str)))
        m = 0;
      if (m != 0)
      {
        ArrayList localArrayList2 = this.mProviderList;
        ProviderEntry localProviderEntry2 = new ProviderEntry(localCharSequence, str);
        localArrayList2.add(localProviderEntry2);
      }
      while (true)
      {
        i++;
        break;
        label164: n++;
        break label64;
        if (Log.isLoggable("ChooseAccountActivity", 2))
          Log.v("ChooseAccountActivity", "Skipped pref " + localCharSequence + ": has no authority we need");
      }
    }
    if (this.mProviderList.size() == 1)
      finishWithAccountType(((ProviderEntry)this.mProviderList.get(0)).type);
    while (true)
    {
      return;
      if (this.mProviderList.size() <= 0)
        break;
      Collections.sort(this.mProviderList);
      this.mAddAccountGroup.removeAll();
      Iterator localIterator = this.mProviderList.iterator();
      while (localIterator.hasNext())
      {
        ProviderEntry localProviderEntry1 = (ProviderEntry)localIterator.next();
        Drawable localDrawable = getDrawableForType(localProviderEntry1.type);
        ProviderPreference localProviderPreference = new ProviderPreference(this, localProviderEntry1.type, localDrawable, localProviderEntry1.name);
        this.mAddAccountGroup.addPreference(localProviderPreference);
      }
    }
    if (Log.isLoggable("ChooseAccountActivity", 2))
    {
      StringBuilder localStringBuilder = new StringBuilder();
      String[] arrayOfString = this.mAuthorities;
      int j = arrayOfString.length;
      for (int k = 0; k < j; k++)
      {
        localStringBuilder.append(arrayOfString[k]);
        localStringBuilder.append(' ');
      }
      Log.v("ChooseAccountActivity", "No providers found for authorities: " + localStringBuilder);
    }
    setResult(0);
    finish();
  }

  private void updateAuthDescriptions()
  {
    this.mAuthDescs = AccountManager.get(this).getAuthenticatorTypes();
    for (int i = 0; i < this.mAuthDescs.length; i++)
      this.mTypeToAuthDescription.put(this.mAuthDescs[i].type, this.mAuthDescs[i]);
    onAuthDescriptionsUpdated();
  }

  public ArrayList<String> getAuthoritiesForAccountType(String paramString)
  {
    if (this.mAccountTypeToAuthorities == null)
    {
      this.mAccountTypeToAuthorities = Maps.newHashMap();
      SyncAdapterType[] arrayOfSyncAdapterType = ContentResolver.getSyncAdapterTypes();
      int i = 0;
      int j = arrayOfSyncAdapterType.length;
      while (i < j)
      {
        SyncAdapterType localSyncAdapterType = arrayOfSyncAdapterType[i];
        ArrayList localArrayList = (ArrayList)this.mAccountTypeToAuthorities.get(localSyncAdapterType.accountType);
        if (localArrayList == null)
        {
          localArrayList = new ArrayList();
          this.mAccountTypeToAuthorities.put(localSyncAdapterType.accountType, localArrayList);
        }
        if (Log.isLoggable("ChooseAccountActivity", 2))
          Log.d("ChooseAccountActivity", "added authority " + localSyncAdapterType.authority + " to accountType " + localSyncAdapterType.accountType);
        localArrayList.add(localSyncAdapterType.authority);
        i++;
      }
    }
    return (ArrayList)this.mAccountTypeToAuthorities.get(paramString);
  }

  protected Drawable getDrawableForType(String paramString)
  {
    boolean bool = this.mTypeToAuthDescription.containsKey(paramString);
    Object localObject = null;
    if (bool);
    try
    {
      AuthenticatorDescription localAuthenticatorDescription = (AuthenticatorDescription)this.mTypeToAuthDescription.get(paramString);
      Drawable localDrawable = createPackageContext(localAuthenticatorDescription.packageName, 0).getResources().getDrawable(localAuthenticatorDescription.iconId);
      localObject = localDrawable;
      return localObject;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      Log.w("ChooseAccountActivity", "No icon name for account type " + paramString);
      return null;
    }
    catch (Resources.NotFoundException localNotFoundException)
    {
      Log.w("ChooseAccountActivity", "No icon resource for account type " + paramString);
    }
    return null;
  }

  protected CharSequence getLabelForType(String paramString)
  {
    boolean bool = this.mTypeToAuthDescription.containsKey(paramString);
    Object localObject = null;
    if (bool);
    try
    {
      AuthenticatorDescription localAuthenticatorDescription = (AuthenticatorDescription)this.mTypeToAuthDescription.get(paramString);
      CharSequence localCharSequence = createPackageContext(localAuthenticatorDescription.packageName, 0).getResources().getText(localAuthenticatorDescription.labelId);
      localObject = localCharSequence;
      return localObject;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      Log.w("ChooseAccountActivity", "No label name for account type " + paramString);
      return null;
    }
    catch (Resources.NotFoundException localNotFoundException)
    {
      Log.w("ChooseAccountActivity", "No label resource for account type " + paramString);
    }
    return null;
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setContentView(2130968579);
    addPreferencesFromResource(2131034114);
    this.mAuthorities = getIntent().getStringArrayExtra("authorities");
    String[] arrayOfString = getIntent().getStringArrayExtra("account_types");
    if (arrayOfString != null)
    {
      this.mAccountTypesFilter = new HashSet();
      int i = arrayOfString.length;
      for (int j = 0; j < i; j++)
      {
        String str = arrayOfString[j];
        this.mAccountTypesFilter.add(str);
      }
    }
    this.mAddAccountGroup = getPreferenceScreen();
    updateAuthDescriptions();
  }

  public boolean onPreferenceTreeClick(PreferenceScreen paramPreferenceScreen, Preference paramPreference)
  {
    if ((paramPreference instanceof ProviderPreference))
    {
      ProviderPreference localProviderPreference = (ProviderPreference)paramPreference;
      if (Log.isLoggable("ChooseAccountActivity", 2))
        Log.v("ChooseAccountActivity", "Attempting to add account of type " + localProviderPreference.getAccountType());
      finishWithAccountType(localProviderPreference.getAccountType());
    }
    return true;
  }

  private static class ProviderEntry
    implements Comparable<ProviderEntry>
  {
    private final CharSequence name;
    private final String type;

    ProviderEntry(CharSequence paramCharSequence, String paramString)
    {
      this.name = paramCharSequence;
      this.type = paramString;
    }

    public int compareTo(ProviderEntry paramProviderEntry)
    {
      if (this.name == null)
        return -1;
      if (paramProviderEntry.name == null)
        return 1;
      return CharSequences.compareToIgnoreCase(this.name, paramProviderEntry.name);
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.accounts.ChooseAccountActivity
 * JD-Core Version:    0.6.2
 */