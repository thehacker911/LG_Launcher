package com.android.settings.accounts;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.OnAccountsUpdateListener;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SyncAdapterType;
import android.content.SyncInfo;
import android.content.SyncStatusInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import com.android.settings.AccountPreference;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settings.location.LocationSettings;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;

public class ManageAccountsSettings extends AccountPreferenceBase
  implements OnAccountsUpdateListener
{
  private String mAccountType;
  private String[] mAuthorities;
  private TextView mErrorInfoView;
  private Account mFirstAccount;

  private void addAuthenticatorSettings()
  {
    PreferenceScreen localPreferenceScreen = addPreferencesForType(this.mAccountType, getPreferenceScreen());
    if (localPreferenceScreen != null)
      updatePreferenceIntents(localPreferenceScreen);
  }

  private void requestOrCancelSyncForAccounts(boolean paramBoolean)
  {
    SyncAdapterType[] arrayOfSyncAdapterType = ContentResolver.getSyncAdapterTypes();
    Bundle localBundle = new Bundle();
    localBundle.putBoolean("force", true);
    int i = getPreferenceScreen().getPreferenceCount();
    for (int j = 0; j < i; j++)
    {
      Preference localPreference = getPreferenceScreen().getPreference(j);
      if ((localPreference instanceof AccountPreference))
      {
        Account localAccount = ((AccountPreference)localPreference).getAccount();
        int k = 0;
        if (k < arrayOfSyncAdapterType.length)
        {
          SyncAdapterType localSyncAdapterType = arrayOfSyncAdapterType[k];
          if ((arrayOfSyncAdapterType[k].accountType.equals(this.mAccountType)) && (ContentResolver.getSyncAutomatically(localAccount, localSyncAdapterType.authority)))
          {
            if (!paramBoolean)
              break label134;
            ContentResolver.requestSync(localAccount, localSyncAdapterType.authority, localBundle);
          }
          while (true)
          {
            k++;
            break;
            label134: ContentResolver.cancelSync(localAccount, localSyncAdapterType.authority);
          }
        }
      }
    }
  }

  private void startAccountSettings(AccountPreference paramAccountPreference)
  {
    Bundle localBundle = new Bundle();
    localBundle.putParcelable("account", paramAccountPreference.getAccount());
    ((PreferenceActivity)getActivity()).startPreferencePanel(AccountSyncSettings.class.getCanonicalName(), localBundle, 2131428974, paramAccountPreference.getAccount().name, this, 1);
  }

  private void updatePreferenceIntents(PreferenceScreen paramPreferenceScreen)
  {
    PackageManager localPackageManager = getActivity().getPackageManager();
    int i = 0;
    if (i < paramPreferenceScreen.getPreferenceCount())
    {
      Preference localPreference = paramPreferenceScreen.getPreference(i);
      Intent localIntent = localPreference.getIntent();
      if (localIntent != null)
      {
        if (!localIntent.getAction().equals("android.settings.LOCATION_SOURCE_SETTINGS"))
          break label76;
        localPreference.setOnPreferenceClickListener(new FragmentStarter(LocationSettings.class.getName(), 2131427618));
      }
      while (true)
      {
        i++;
        break;
        label76: if (localPackageManager.resolveActivity(localIntent, 65536) == null)
        {
          paramPreferenceScreen.removePreference(localPreference);
          break;
        }
        localIntent.putExtra("account", this.mFirstAccount);
        localIntent.setFlags(0x10000000 | localIntent.getFlags());
      }
    }
  }

  public void onAccountsUpdated(Account[] paramArrayOfAccount)
  {
    if (getActivity() == null)
      return;
    getPreferenceScreen().removeAll();
    this.mFirstAccount = null;
    addPreferencesFromResource(2131034137);
    int i = 0;
    int j = paramArrayOfAccount.length;
    while (i < j)
    {
      Account localAccount = paramArrayOfAccount[i];
      if ((this.mAccountType != null) && (!localAccount.type.equals(this.mAccountType)))
      {
        i++;
      }
      else
      {
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
            if (k == 0)
              break;
            Drawable localDrawable = getDrawableForType(localAccount.type);
            AccountPreference localAccountPreference = new AccountPreference(getActivity(), localAccount, localDrawable, localArrayList, false);
            getPreferenceScreen().addPreference(localAccountPreference);
            if (this.mFirstAccount != null)
              break;
            this.mFirstAccount = localAccount;
            getActivity().invalidateOptionsMenu();
            break;
          }
        }
      }
    }
    if ((this.mAccountType != null) && (this.mFirstAccount != null))
      addAuthenticatorSettings();
    while (true)
    {
      onSyncStateUpdated();
      return;
      Intent localIntent = new Intent("android.settings.SETTINGS");
      localIntent.setFlags(67108864);
      getActivity().startActivity(localIntent);
    }
  }

  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    Activity localActivity = getActivity();
    this.mErrorInfoView = ((TextView)getView().findViewById(2131230723));
    this.mErrorInfoView.setVisibility(8);
    this.mAuthorities = localActivity.getIntent().getStringArrayExtra("authorities");
    Bundle localBundle = getArguments();
    if ((localBundle != null) && (localBundle.containsKey("account_label")))
      getActivity().setTitle(localBundle.getString("account_label"));
    updateAuthDescriptions();
  }

  protected void onAuthDescriptionsUpdated()
  {
    for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++)
    {
      Preference localPreference = getPreferenceScreen().getPreference(i);
      if ((localPreference instanceof AccountPreference))
      {
        AccountPreference localAccountPreference = (AccountPreference)localPreference;
        localAccountPreference.setSummary(getLabelForType(localAccountPreference.getAccount().type));
      }
    }
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    Bundle localBundle = getArguments();
    if ((localBundle != null) && (localBundle.containsKey("account_type")))
      this.mAccountType = localBundle.getString("account_type");
    addPreferencesFromResource(2131034137);
    setHasOptionsMenu(true);
  }

  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    paramMenu.add(0, 1, 0, getString(2131428989)).setIcon(2130837592);
    paramMenu.add(0, 2, 0, getString(2131428990)).setIcon(17301560);
    super.onCreateOptionsMenu(paramMenu, paramMenuInflater);
  }

  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    View localView = paramLayoutInflater.inflate(2130968642, paramViewGroup, false);
    Utils.prepareCustomPreferencesList(paramViewGroup, localView, (ListView)localView.findViewById(16908298), false);
    return localView;
  }

  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    switch (paramMenuItem.getItemId())
    {
    default:
      return super.onOptionsItemSelected(paramMenuItem);
    case 1:
      requestOrCancelSyncForAccounts(true);
      return true;
    case 2:
    }
    requestOrCancelSyncForAccounts(false);
    return true;
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

  public void onPrepareOptionsMenu(Menu paramMenu)
  {
    int i = 1;
    super.onPrepareOptionsMenu(paramMenu);
    label38: MenuItem localMenuItem2;
    if (ContentResolver.getCurrentSync() != null)
    {
      int k = i;
      MenuItem localMenuItem1 = paramMenu.findItem(i);
      if ((k != 0) || (this.mFirstAccount == null))
        break label83;
      int n = i;
      localMenuItem1.setVisible(n);
      localMenuItem2 = paramMenu.findItem(2);
      if ((k == 0) || (this.mFirstAccount == null))
        break label89;
    }
    while (true)
    {
      localMenuItem2.setVisible(i);
      return;
      int m = 0;
      break;
      label83: int i1 = 0;
      break label38;
      label89: int j = 0;
    }
  }

  public void onStart()
  {
    super.onStart();
    AccountManager.get(getActivity()).addOnAccountsUpdatedListener(this, null, true);
  }

  public void onStop()
  {
    super.onStop();
    Activity localActivity = getActivity();
    AccountManager.get(localActivity).removeOnAccountsUpdatedListener(this);
    localActivity.getActionBar().setDisplayOptions(0, 16);
    localActivity.getActionBar().setCustomView(null);
  }

  protected void onSyncStateUpdated()
  {
    if (getActivity() == null)
      return;
    SyncInfo localSyncInfo = ContentResolver.getCurrentSync();
    int i = 0;
    Date localDate = new Date();
    SyncAdapterType[] arrayOfSyncAdapterType = ContentResolver.getSyncAdapterTypes();
    HashSet localHashSet = new HashSet();
    int j = 0;
    int k = arrayOfSyncAdapterType.length;
    while (j < k)
    {
      SyncAdapterType localSyncAdapterType = arrayOfSyncAdapterType[j];
      if (localSyncAdapterType.isUserVisible())
        localHashSet.add(localSyncAdapterType.authority);
      j++;
    }
    int m = 0;
    int n = getPreferenceScreen().getPreferenceCount();
    if (m < n)
    {
      Preference localPreference = getPreferenceScreen().getPreference(m);
      if (!(localPreference instanceof AccountPreference));
      while (true)
      {
        m++;
        break;
        AccountPreference localAccountPreference = (AccountPreference)localPreference;
        Account localAccount = localAccountPreference.getAccount();
        int i2 = 0;
        long l = 0L;
        int i3 = 0;
        ArrayList localArrayList = localAccountPreference.getAuthorities();
        int i4 = 0;
        if (localArrayList != null)
        {
          Iterator localIterator = localArrayList.iterator();
          if (localIterator.hasNext())
          {
            String str2 = (String)localIterator.next();
            SyncStatusInfo localSyncStatusInfo = ContentResolver.getSyncStatus(localAccount, str2);
            int i5;
            label232: int i6;
            label289: int i7;
            if ((ContentResolver.getSyncAutomatically(localAccount, str2)) && (ContentResolver.getMasterSyncAutomatically()) && (ContentResolver.getIsSyncable(localAccount, str2) > 0))
            {
              i5 = 1;
              boolean bool2 = ContentResolver.isSyncPending(localAccount, str2);
              if ((localSyncInfo == null) || (!localSyncInfo.authority.equals(str2)) || (!new Account(localSyncInfo.account.name, localSyncInfo.account.type).equals(localAccount)))
                break label406;
              i6 = 1;
              if ((localSyncStatusInfo == null) || (i5 == 0) || (localSyncStatusInfo.lastFailureTime == 0L) || (localSyncStatusInfo.getLastFailureMesgAsInt(0) == 1))
                break label412;
              i7 = 1;
              label322: if ((i7 != 0) && (i6 == 0) && (!bool2))
              {
                i3 = 1;
                i = 1;
              }
              i4 |= i6;
              if ((localSyncStatusInfo != null) && (l < localSyncStatusInfo.lastSuccessTime))
                l = localSyncStatusInfo.lastSuccessTime;
              if ((i5 == 0) || (!localHashSet.contains(str2)))
                break label418;
            }
            label406: label412: label418: for (int i8 = 1; ; i8 = 0)
            {
              i2 += i8;
              break;
              i5 = 0;
              break label232;
              i6 = 0;
              break label289;
              i7 = 0;
              break label322;
            }
          }
        }
        else
        {
          boolean bool1 = Log.isLoggable("AccountSettings", 2);
          i2 = 0;
          i3 = 0;
          i4 = 0;
          if (bool1)
            Log.v("AccountSettings", "no syncadapters found for " + localAccount);
        }
        if (i3 != 0)
          localAccountPreference.setSyncStatus(2, true);
        else if (i2 == 0)
          localAccountPreference.setSyncStatus(1, true);
        else if (i2 > 0)
        {
          if (i4 != 0)
          {
            localAccountPreference.setSyncStatus(3, true);
          }
          else
          {
            localAccountPreference.setSyncStatus(0, true);
            if (l > 0L)
            {
              localAccountPreference.setSyncStatus(0, false);
              localDate.setTime(l);
              String str1 = formatSyncDate(localDate);
              localAccountPreference.setSummary(getResources().getString(2131428985, new Object[] { str1 }));
            }
          }
        }
        else
          localAccountPreference.setSyncStatus(1, true);
      }
    }
    TextView localTextView = this.mErrorInfoView;
    if (i != 0);
    for (int i1 = 0; ; i1 = 8)
    {
      localTextView.setVisibility(i1);
      return;
    }
  }

  private class FragmentStarter
    implements Preference.OnPreferenceClickListener
  {
    private final String mClass;
    private final int mTitleRes;

    public FragmentStarter(String paramInt, int arg3)
    {
      this.mClass = paramInt;
      int i;
      this.mTitleRes = i;
    }

    public boolean onPreferenceClick(Preference paramPreference)
    {
      ((PreferenceActivity)ManageAccountsSettings.this.getActivity()).startPreferencePanel(this.mClass, null, this.mTitleRes, null, null, 0);
      if (this.mClass.equals(LocationSettings.class.getName()))
      {
        Intent localIntent = new Intent("com.android.settings.accounts.LAUNCHING_LOCATION_SETTINGS");
        ManageAccountsSettings.this.getActivity().sendBroadcast(localIntent, "android.permission.WRITE_SECURE_SETTINGS");
      }
      return true;
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.accounts.ManageAccountsSettings
 * JD-Core Version:    0.6.2
 */