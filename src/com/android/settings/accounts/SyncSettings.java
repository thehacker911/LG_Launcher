package com.android.settings.accounts;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.OnAccountsUpdateListener;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.util.Log;
import com.android.settings.AccountPreference;
import com.android.settings.DialogCreatable;
import com.android.settings.SettingsPreferenceFragment.SettingsDialogFragment;
import java.util.ArrayList;

public class SyncSettings extends AccountPreferenceBase
  implements OnAccountsUpdateListener, DialogCreatable
{
  private String[] mAuthorities;
  private CheckBoxPreference mAutoSyncPreference;
  private SettingsPreferenceFragment.SettingsDialogFragment mDialogFragment;

  private void removeAccountPreferences()
  {
    PreferenceScreen localPreferenceScreen = getPreferenceScreen();
    int i = 0;
    while (i < localPreferenceScreen.getPreferenceCount())
      if ((localPreferenceScreen.getPreference(i) instanceof AccountPreference))
        localPreferenceScreen.removePreference(localPreferenceScreen.getPreference(i));
      else
        i++;
  }

  private void startAccountSettings(AccountPreference paramAccountPreference)
  {
    Intent localIntent = new Intent("android.settings.ACCOUNT_SYNC_SETTINGS");
    localIntent.putExtra("account", paramAccountPreference.getAccount());
    localIntent.setFlags(268435456);
    startActivity(localIntent);
    finish();
  }

  public void onAccountsUpdated(Account[] paramArrayOfAccount)
  {
    if (getActivity() == null)
      return;
    removeAccountPreferences();
    int i = 0;
    int j = paramArrayOfAccount.length;
    if (i < j)
    {
      Account localAccount = paramArrayOfAccount[i];
      ArrayList localArrayList = getAuthoritiesForAccountType(localAccount.type);
      int k = 1;
      String[] arrayOfString;
      int m;
      if ((this.mAuthorities != null) && (localArrayList != null))
      {
        arrayOfString = this.mAuthorities;
        m = arrayOfString.length;
      }
      for (int n = 0; ; n++)
      {
        k = 0;
        if (n < m)
        {
          if (localArrayList.contains(arrayOfString[n]))
            k = 1;
        }
        else
        {
          if (k != 0)
          {
            Drawable localDrawable = getDrawableForType(localAccount.type);
            AccountPreference localAccountPreference = new AccountPreference(getActivity(), localAccount, localDrawable, localArrayList, true);
            getPreferenceScreen().addPreference(localAccountPreference);
            localAccountPreference.setSummary(getLabelForType(localAccount.type));
          }
          i++;
          break;
        }
      }
    }
    onSyncStateUpdated();
  }

  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    Activity localActivity = getActivity();
    this.mAutoSyncPreference.setChecked(ContentResolver.getMasterSyncAutomatically());
    this.mAuthorities = localActivity.getIntent().getStringArrayExtra("authorities");
    updateAuthDescriptions();
  }

  protected void onAuthDescriptionsUpdated()
  {
    for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++)
      if ((getPreferenceScreen().getPreference(i) instanceof AccountPreference))
      {
        AccountPreference localAccountPreference = (AccountPreference)getPreferenceScreen().getPreference(i);
        localAccountPreference.setIcon(getDrawableForType(localAccountPreference.getAccount().type));
        localAccountPreference.setSummary(getLabelForType(localAccountPreference.getAccount().type));
      }
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    addPreferencesFromResource(2131034160);
    this.mAutoSyncPreference = ((CheckBoxPreference)getPreferenceScreen().findPreference("sync_switch"));
    this.mAutoSyncPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
    {
      public boolean onPreferenceChange(Preference paramAnonymousPreference, Object paramAnonymousObject)
      {
        if (ActivityManager.isUserAMonkey())
          Log.d("SyncSettings", "ignoring monkey's attempt to flip sync state");
        while (true)
        {
          return true;
          ContentResolver.setMasterSyncAutomatically(((Boolean)paramAnonymousObject).booleanValue());
        }
      }
    });
    setHasOptionsMenu(true);
  }

  public boolean onPreferenceTreeClick(PreferenceScreen paramPreferenceScreen, Preference paramPreference)
  {
    if ((paramPreference instanceof AccountPreference))
    {
      startAccountSettings((AccountPreference)paramPreference);
      return true;
    }
    return false;
  }

  public void onStart()
  {
    super.onStart();
    AccountManager.get(getActivity()).addOnAccountsUpdatedListener(this, null, true);
  }

  public void onStop()
  {
    super.onStop();
    AccountManager.get(getActivity()).removeOnAccountsUpdatedListener(this);
  }

  public void showDialog(int paramInt)
  {
    if (this.mDialogFragment != null)
      Log.e("AccountSettings", "Old dialog fragment not null!");
    this.mDialogFragment = new SettingsPreferenceFragment.SettingsDialogFragment(this, paramInt);
    this.mDialogFragment.show(getActivity().getFragmentManager(), Integer.toString(paramInt));
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.accounts.SyncSettings
 * JD-Core Version:    0.6.2
 */