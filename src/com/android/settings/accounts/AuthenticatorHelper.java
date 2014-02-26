package com.android.settings.accounts;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorDescription;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AuthenticatorHelper
{
  private Map<String, Drawable> mAccTypeIconCache = new HashMap();
  private AuthenticatorDescription[] mAuthDescs;
  private ArrayList<String> mEnabledAccountTypes = new ArrayList();
  private Map<String, AuthenticatorDescription> mTypeToAuthDescription = new HashMap();

  public boolean containsAccountType(String paramString)
  {
    return this.mTypeToAuthDescription.containsKey(paramString);
  }

  public AuthenticatorDescription getAccountTypeDescription(String paramString)
  {
    return (AuthenticatorDescription)this.mTypeToAuthDescription.get(paramString);
  }

  public Drawable getDrawableForType(Context paramContext, String paramString)
  {
    Drawable localDrawable1;
    synchronized (this.mAccTypeIconCache)
    {
      if (this.mAccTypeIconCache.containsKey(paramString))
      {
        Drawable localDrawable2 = (Drawable)this.mAccTypeIconCache.get(paramString);
        return localDrawable2;
      }
      boolean bool = this.mTypeToAuthDescription.containsKey(paramString);
      localDrawable1 = null;
      if (!bool);
    }
    try
    {
      AuthenticatorDescription localAuthenticatorDescription = (AuthenticatorDescription)this.mTypeToAuthDescription.get(paramString);
      localDrawable1 = paramContext.createPackageContext(localAuthenticatorDescription.packageName, 0).getResources().getDrawable(localAuthenticatorDescription.iconId);
      synchronized (this.mAccTypeIconCache)
      {
        this.mAccTypeIconCache.put(paramString, localDrawable1);
        label125: if (localDrawable1 == null)
          localDrawable1 = paramContext.getPackageManager().getDefaultActivityIcon();
        return localDrawable1;
        localObject1 = finally;
        throw localObject1;
      }
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      break label125;
    }
    catch (Resources.NotFoundException localNotFoundException)
    {
      break label125;
    }
  }

  public String[] getEnabledAccountTypes()
  {
    return (String[])this.mEnabledAccountTypes.toArray(new String[this.mEnabledAccountTypes.size()]);
  }

  public CharSequence getLabelForType(Context paramContext, String paramString)
  {
    boolean bool = this.mTypeToAuthDescription.containsKey(paramString);
    Object localObject = null;
    if (bool);
    try
    {
      AuthenticatorDescription localAuthenticatorDescription = (AuthenticatorDescription)this.mTypeToAuthDescription.get(paramString);
      CharSequence localCharSequence = paramContext.createPackageContext(localAuthenticatorDescription.packageName, 0).getResources().getText(localAuthenticatorDescription.labelId);
      localObject = localCharSequence;
      return localObject;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      Log.w("AuthenticatorHelper", "No label name for account type " + paramString);
      return null;
    }
    catch (Resources.NotFoundException localNotFoundException)
    {
      Log.w("AuthenticatorHelper", "No label icon for account type " + paramString);
    }
    return null;
  }

  public boolean hasAccountPreferences(String paramString)
  {
    if (containsAccountType(paramString))
    {
      AuthenticatorDescription localAuthenticatorDescription = getAccountTypeDescription(paramString);
      if ((localAuthenticatorDescription != null) && (localAuthenticatorDescription.accountPreferencesId != 0))
        return true;
    }
    return false;
  }

  public void onAccountsUpdated(Context paramContext, Account[] paramArrayOfAccount)
  {
    if (paramArrayOfAccount == null)
      paramArrayOfAccount = AccountManager.get(paramContext).getAccounts();
    this.mEnabledAccountTypes.clear();
    this.mAccTypeIconCache.clear();
    for (Account localAccount : paramArrayOfAccount)
      if (!this.mEnabledAccountTypes.contains(localAccount.type))
        this.mEnabledAccountTypes.add(localAccount.type);
  }

  public void preloadDrawableForType(final Context paramContext, final String paramString)
  {
    new AsyncTask()
    {
      protected Void doInBackground(Void[] paramAnonymousArrayOfVoid)
      {
        AuthenticatorHelper.this.getDrawableForType(paramContext, paramString);
        return null;
      }
    }
    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
  }

  public void updateAuthDescriptions(Context paramContext)
  {
    this.mAuthDescs = AccountManager.get(paramContext).getAuthenticatorTypes();
    for (int i = 0; i < this.mAuthDescs.length; i++)
      this.mTypeToAuthDescription.put(this.mAuthDescs[i].type, this.mAuthDescs[i]);
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.accounts.AuthenticatorHelper
 * JD-Core Version:    0.6.2
 */