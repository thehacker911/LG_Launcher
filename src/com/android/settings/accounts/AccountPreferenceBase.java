package com.android.settings.accounts;

import android.accounts.Account;
import android.accounts.AuthenticatorDescription;
import android.accounts.OnAccountsUpdateListener;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncAdapterType;
import android.content.SyncStatusObserver;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import com.android.settings.SettingsPreferenceFragment;
import com.google.android.collect.Maps;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

class AccountPreferenceBase extends SettingsPreferenceFragment
  implements OnAccountsUpdateListener
{
  private HashMap<String, ArrayList<String>> mAccountTypeToAuthorities = null;
  private AuthenticatorHelper mAuthenticatorHelper = new AuthenticatorHelper();
  private java.text.DateFormat mDateFormat;
  private final Handler mHandler = new Handler();
  private Object mStatusChangeListenerHandle;
  private SyncStatusObserver mSyncStatusObserver = new SyncStatusObserver()
  {
    public void onStatusChanged(int paramAnonymousInt)
    {
      AccountPreferenceBase.this.mHandler.post(new Runnable()
      {
        public void run()
        {
          AccountPreferenceBase.this.onSyncStateUpdated();
        }
      });
    }
  };
  private java.text.DateFormat mTimeFormat;

  public PreferenceScreen addPreferencesForType(String paramString, PreferenceScreen paramPreferenceScreen)
  {
    boolean bool = this.mAuthenticatorHelper.containsAccountType(paramString);
    Object localObject = null;
    AuthenticatorDescription localAuthenticatorDescription;
    if (bool)
      localAuthenticatorDescription = null;
    try
    {
      localAuthenticatorDescription = this.mAuthenticatorHelper.getAccountTypeDescription(paramString);
      localObject = null;
      if (localAuthenticatorDescription != null)
      {
        int i = localAuthenticatorDescription.accountPreferencesId;
        localObject = null;
        if (i != 0)
        {
          Context localContext = getActivity().createPackageContext(localAuthenticatorDescription.packageName, 0);
          PreferenceScreen localPreferenceScreen = getPreferenceManager().inflateFromResource(localContext, localAuthenticatorDescription.accountPreferencesId, paramPreferenceScreen);
          localObject = localPreferenceScreen;
        }
      }
      return localObject;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      Log.w("AccountSettings", "Couldn't load preferences.xml file from " + localAuthenticatorDescription.packageName);
      return null;
    }
    catch (Resources.NotFoundException localNotFoundException)
    {
      Log.w("AccountSettings", "Couldn't load preferences.xml file from " + localAuthenticatorDescription.packageName);
    }
    return null;
  }

  protected String formatSyncDate(Date paramDate)
  {
    return this.mDateFormat.format(paramDate) + " " + this.mTimeFormat.format(paramDate);
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
        if (Log.isLoggable("AccountSettings", 2))
          Log.d("AccountSettings", "added authority " + localSyncAdapterType.authority + " to accountType " + localSyncAdapterType.accountType);
        localArrayList.add(localSyncAdapterType.authority);
        i++;
      }
    }
    return (ArrayList)this.mAccountTypeToAuthorities.get(paramString);
  }

  protected Drawable getDrawableForType(String paramString)
  {
    return this.mAuthenticatorHelper.getDrawableForType(getActivity(), paramString);
  }

  protected CharSequence getLabelForType(String paramString)
  {
    return this.mAuthenticatorHelper.getLabelForType(getActivity(), paramString);
  }

  public void onAccountsUpdated(Account[] paramArrayOfAccount)
  {
  }

  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    Activity localActivity = getActivity();
    this.mDateFormat = android.text.format.DateFormat.getDateFormat(localActivity);
    this.mTimeFormat = android.text.format.DateFormat.getTimeFormat(localActivity);
  }

  protected void onAuthDescriptionsUpdated()
  {
  }

  public void onPause()
  {
    super.onPause();
    ContentResolver.removeStatusChangeListener(this.mStatusChangeListenerHandle);
  }

  public void onResume()
  {
    super.onResume();
    this.mStatusChangeListenerHandle = ContentResolver.addStatusChangeListener(13, this.mSyncStatusObserver);
    onSyncStateUpdated();
  }

  protected void onSyncStateUpdated()
  {
  }

  public void updateAuthDescriptions()
  {
    this.mAuthenticatorHelper.updateAuthDescriptions(getActivity());
    onAuthDescriptionsUpdated();
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.accounts.AccountPreferenceBase
 * JD-Core Version:    0.6.2
 */