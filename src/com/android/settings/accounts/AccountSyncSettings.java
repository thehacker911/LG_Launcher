package com.android.settings.accounts;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SyncAdapterType;
import android.content.SyncInfo;
import android.content.SyncStatusInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.UserManager;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.android.settings.Utils;
import com.google.android.collect.Lists;
import com.google.android.collect.Maps;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class AccountSyncSettings extends AccountPreferenceBase
{
  private Account mAccount;
  private Account[] mAccounts;
  private ArrayList<SyncStateCheckBoxPreference> mCheckBoxes = new ArrayList();
  private TextView mErrorInfoView;
  private ArrayList<SyncAdapterType> mInvisibleAdapters = Lists.newArrayList();
  private ImageView mProviderIcon;
  private TextView mProviderId;
  private TextView mUserId;

  private void addSyncStateCheckBox(Account paramAccount, String paramString)
  {
    SyncStateCheckBoxPreference localSyncStateCheckBoxPreference = new SyncStateCheckBoxPreference(getActivity(), paramAccount, paramString);
    localSyncStateCheckBoxPreference.setPersistent(false);
    ProviderInfo localProviderInfo = getPackageManager().resolveContentProvider(paramString, 0);
    if (localProviderInfo == null)
      return;
    CharSequence localCharSequence = localProviderInfo.loadLabel(getPackageManager());
    if (TextUtils.isEmpty(localCharSequence))
    {
      Log.e("AccountSettings", "Provider needs a label for authority '" + paramString + "'");
      return;
    }
    localSyncStateCheckBoxPreference.setTitle(getString(2131429007, new Object[] { localCharSequence }));
    localSyncStateCheckBoxPreference.setKey(paramString);
    this.mCheckBoxes.add(localSyncStateCheckBoxPreference);
  }

  private void cancelSyncForEnabledProviders()
  {
    requestOrCancelSyncForEnabledProviders(false);
    getActivity().invalidateOptionsMenu();
  }

  private boolean isSyncing(List<SyncInfo> paramList, Account paramAccount, String paramString)
  {
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      SyncInfo localSyncInfo = (SyncInfo)localIterator.next();
      if ((localSyncInfo.account.equals(paramAccount)) && (localSyncInfo.authority.equals(paramString)))
        return true;
    }
    return false;
  }

  private void requestOrCancelSync(Account paramAccount, String paramString, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      Bundle localBundle = new Bundle();
      localBundle.putBoolean("force", true);
      ContentResolver.requestSync(paramAccount, paramString, localBundle);
      return;
    }
    ContentResolver.cancelSync(paramAccount, paramString);
  }

  private void requestOrCancelSyncForEnabledProviders(boolean paramBoolean)
  {
    int i = getPreferenceScreen().getPreferenceCount();
    int j = 0;
    if (j < i)
    {
      Preference localPreference = getPreferenceScreen().getPreference(j);
      if (!(localPreference instanceof SyncStateCheckBoxPreference));
      while (true)
      {
        j++;
        break;
        SyncStateCheckBoxPreference localSyncStateCheckBoxPreference = (SyncStateCheckBoxPreference)localPreference;
        if (localSyncStateCheckBoxPreference.isChecked())
          requestOrCancelSync(localSyncStateCheckBoxPreference.getAccount(), localSyncStateCheckBoxPreference.getAuthority(), paramBoolean);
      }
    }
    if (this.mAccount != null)
    {
      Iterator localIterator = this.mInvisibleAdapters.iterator();
      while (localIterator.hasNext())
      {
        SyncAdapterType localSyncAdapterType = (SyncAdapterType)localIterator.next();
        if (localSyncAdapterType.accountType.equals(this.mAccount.type))
          requestOrCancelSync(this.mAccount, localSyncAdapterType.authority, paramBoolean);
      }
    }
  }

  private void setFeedsState()
  {
    Date localDate = new Date();
    List localList = ContentResolver.getCurrentSyncs();
    int i = 0;
    updateAccountCheckboxes(this.mAccounts);
    int j = 0;
    int k = getPreferenceScreen().getPreferenceCount();
    while (j < k)
    {
      Preference localPreference = getPreferenceScreen().getPreference(j);
      if (!(localPreference instanceof SyncStateCheckBoxPreference))
      {
        j++;
      }
      else
      {
        SyncStateCheckBoxPreference localSyncStateCheckBoxPreference = (SyncStateCheckBoxPreference)localPreference;
        String str1 = localSyncStateCheckBoxPreference.getAuthority();
        Account localAccount = localSyncStateCheckBoxPreference.getAccount();
        SyncStatusInfo localSyncStatusInfo = ContentResolver.getSyncStatus(localAccount, str1);
        boolean bool1 = ContentResolver.getSyncAutomatically(localAccount, str1);
        boolean bool2;
        label113: boolean bool3;
        label121: boolean bool4;
        boolean bool5;
        label160: long l;
        label262: label275: boolean bool6;
        label302: boolean bool7;
        label327: boolean bool10;
        if (localSyncStatusInfo == null)
        {
          bool2 = false;
          if (localSyncStatusInfo != null)
            break label418;
          bool3 = false;
          bool4 = isSyncing(localList, localAccount, str1);
          if ((localSyncStatusInfo == null) || (localSyncStatusInfo.lastFailureTime == 0L) || (localSyncStatusInfo.getLastFailureMesgAsInt(0) == 1))
            break label428;
          bool5 = true;
          if (!bool1)
            bool5 = false;
          if ((bool5) && (!bool4) && (!bool2))
            i = 1;
          if (Log.isLoggable("AccountSettings", 2))
            Log.d("AccountSettings", "Update sync status: " + localAccount + " " + str1 + " active = " + bool4 + " pend =" + bool2);
          if (localSyncStatusInfo != null)
            break label434;
          l = 0L;
          if (bool1)
            break label444;
          localSyncStateCheckBoxPreference.setSummary(2131428983);
          int n = ContentResolver.getIsSyncable(localAccount, str1);
          if ((!bool4) || (n < 0) || (bool3))
            break label518;
          bool6 = true;
          localSyncStateCheckBoxPreference.setActive(bool6);
          if ((!bool2) || (n < 0) || (bool3))
            break label524;
          bool7 = true;
          localSyncStateCheckBoxPreference.setPending(bool7);
          localSyncStateCheckBoxPreference.setFailed(bool5);
          ConnectivityManager localConnectivityManager = (ConnectivityManager)getSystemService("connectivity");
          boolean bool8 = ContentResolver.getMasterSyncAutomatically();
          boolean bool9 = localConnectivityManager.getBackgroundDataSetting();
          if ((bool8) && (bool9))
            break label530;
          bool10 = true;
          label378: localSyncStateCheckBoxPreference.setOneTimeSyncMode(bool10);
          if ((!bool10) && (!bool1))
            break label536;
        }
        label518: label524: label530: label536: for (boolean bool11 = true; ; bool11 = false)
        {
          localSyncStateCheckBoxPreference.setChecked(bool11);
          break;
          bool2 = localSyncStatusInfo.pending;
          break label113;
          label418: bool3 = localSyncStatusInfo.initialize;
          break label121;
          label428: bool5 = false;
          break label160;
          label434: l = localSyncStatusInfo.lastSuccessTime;
          break label262;
          label444: if (bool4)
          {
            localSyncStateCheckBoxPreference.setSummary(2131428986);
            break label275;
          }
          if (l != 0L)
          {
            localDate.setTime(l);
            String str2 = formatSyncDate(localDate);
            localSyncStateCheckBoxPreference.setSummary(getResources().getString(2131428985, new Object[] { str2 }));
            break label275;
          }
          localSyncStateCheckBoxPreference.setSummary("");
          break label275;
          bool6 = false;
          break label302;
          bool7 = false;
          break label327;
          bool10 = false;
          break label378;
        }
      }
    }
    TextView localTextView = this.mErrorInfoView;
    if (i != 0);
    for (int m = 0; ; m = 8)
    {
      localTextView.setVisibility(m);
      getActivity().invalidateOptionsMenu();
      return;
    }
  }

  private void startSyncForEnabledProviders()
  {
    requestOrCancelSyncForEnabledProviders(true);
    getActivity().invalidateOptionsMenu();
  }

  private void updateAccountCheckboxes(Account[] paramArrayOfAccount)
  {
    this.mInvisibleAdapters.clear();
    SyncAdapterType[] arrayOfSyncAdapterType = ContentResolver.getSyncAdapterTypes();
    HashMap localHashMap = Maps.newHashMap();
    int i = 0;
    int j = arrayOfSyncAdapterType.length;
    if (i < j)
    {
      SyncAdapterType localSyncAdapterType = arrayOfSyncAdapterType[i];
      if (localSyncAdapterType.isUserVisible())
      {
        ArrayList localArrayList2 = (ArrayList)localHashMap.get(localSyncAdapterType.accountType);
        if (localArrayList2 == null)
        {
          localArrayList2 = new ArrayList();
          localHashMap.put(localSyncAdapterType.accountType, localArrayList2);
        }
        if (Log.isLoggable("AccountSettings", 2))
          Log.d("AccountSettings", "onAccountUpdated: added authority " + localSyncAdapterType.authority + " to accountType " + localSyncAdapterType.accountType);
        localArrayList2.add(localSyncAdapterType.authority);
      }
      while (true)
      {
        i++;
        break;
        this.mInvisibleAdapters.add(localSyncAdapterType);
      }
    }
    int k = 0;
    int m = this.mCheckBoxes.size();
    while (k < m)
    {
      getPreferenceScreen().removePreference((Preference)this.mCheckBoxes.get(k));
      k++;
    }
    this.mCheckBoxes.clear();
    int n = 0;
    int i1 = paramArrayOfAccount.length;
    while (n < i1)
    {
      Account localAccount = paramArrayOfAccount[n];
      if (Log.isLoggable("AccountSettings", 2))
        Log.d("AccountSettings", "looking for sync adapters that match account " + localAccount);
      ArrayList localArrayList1 = (ArrayList)localHashMap.get(localAccount.type);
      if ((localArrayList1 != null) && ((this.mAccount == null) || (this.mAccount.equals(localAccount))))
      {
        int i4 = 0;
        int i5 = localArrayList1.size();
        while (i4 < i5)
        {
          String str = (String)localArrayList1.get(i4);
          int i6 = ContentResolver.getIsSyncable(localAccount, str);
          if (Log.isLoggable("AccountSettings", 2))
            Log.d("AccountSettings", "  found authority " + str + " " + i6);
          if (i6 > 0)
            addSyncStateCheckBox(localAccount, str);
          i4++;
        }
      }
      n++;
    }
    Collections.sort(this.mCheckBoxes);
    int i2 = 0;
    int i3 = this.mCheckBoxes.size();
    while (i2 < i3)
    {
      getPreferenceScreen().addPreference((Preference)this.mCheckBoxes.get(i2));
      i2++;
    }
  }

  protected int getHelpResource()
  {
    return 2131429261;
  }

  protected void initializeUi(View paramView)
  {
    addPreferencesFromResource(2131034113);
    this.mErrorInfoView = ((TextView)paramView.findViewById(2131230723));
    this.mErrorInfoView.setVisibility(8);
    this.mUserId = ((TextView)paramView.findViewById(2131231065));
    this.mProviderId = ((TextView)paramView.findViewById(2131231066));
    this.mProviderIcon = ((ImageView)paramView.findViewById(2131231064));
  }

  public void onAccountsUpdated(Account[] paramArrayOfAccount)
  {
    super.onAccountsUpdated(paramArrayOfAccount);
    this.mAccounts = paramArrayOfAccount;
    updateAccountCheckboxes(paramArrayOfAccount);
    onSyncStateUpdated();
  }

  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    Bundle localBundle = getArguments();
    if (localBundle == null)
      Log.e("AccountSettings", "No arguments provided when starting intent. ACCOUNT_KEY needed.");
    do
    {
      return;
      this.mAccount = ((Account)localBundle.getParcelable("account"));
    }
    while (this.mAccount == null);
    if (Log.isLoggable("AccountSettings", 2))
      Log.v("AccountSettings", "Got account: " + this.mAccount);
    this.mUserId.setText(this.mAccount.name);
    this.mProviderId.setText(this.mAccount.type);
  }

  protected void onAuthDescriptionsUpdated()
  {
    super.onAuthDescriptionsUpdated();
    getPreferenceScreen().removeAll();
    if (this.mAccount != null)
    {
      this.mProviderIcon.setImageDrawable(getDrawableForType(this.mAccount.type));
      this.mProviderId.setText(getLabelForType(this.mAccount.type));
    }
    addPreferencesFromResource(2131034113);
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setHasOptionsMenu(true);
  }

  public Dialog onCreateDialog(int paramInt)
  {
    AlertDialog localAlertDialog;
    if (paramInt == 100)
      localAlertDialog = new AlertDialog.Builder(getActivity()).setTitle(2131429003).setMessage(2131429004).setNegativeButton(17039360, null).setPositiveButton(2131429000, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          AccountManager.get(AccountSyncSettings.this.getActivity()).removeAccount(AccountSyncSettings.this.mAccount, new AccountManagerCallback()
          {
            public void run(AccountManagerFuture<Boolean> paramAnonymous2AccountManagerFuture)
            {
              if (!AccountSyncSettings.this.isResumed())
                return;
              int i = 1;
              try
              {
                boolean bool = ((Boolean)paramAnonymous2AccountManagerFuture.getResult()).booleanValue();
                if (bool == true)
                  i = 0;
                label38: if ((i != 0) && (AccountSyncSettings.this.getActivity() != null) && (!AccountSyncSettings.this.getActivity().isFinishing()))
                {
                  AccountSyncSettings.this.showDialog(101);
                  return;
                }
                AccountSyncSettings.this.finish();
                return;
              }
              catch (AuthenticatorException localAuthenticatorException)
              {
                break label38;
              }
              catch (IOException localIOException)
              {
                break label38;
              }
              catch (OperationCanceledException localOperationCanceledException)
              {
                break label38;
              }
            }
          }
          , null);
        }
      }).create();
    do
    {
      return localAlertDialog;
      if (paramInt == 101)
        return new AlertDialog.Builder(getActivity()).setTitle(2131429003).setPositiveButton(17039370, null).setMessage(2131429005).create();
      localAlertDialog = null;
    }
    while (paramInt != 102);
    return new AlertDialog.Builder(getActivity()).setTitle(2131429008).setMessage(2131429009).setPositiveButton(17039370, null).create();
  }

  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    MenuItem localMenuItem1 = paramMenu.add(0, 1, 0, getString(2131428989)).setIcon(2130837592);
    MenuItem localMenuItem2 = paramMenu.add(0, 2, 0, getString(2131428990)).setIcon(17301560);
    if (!((UserManager)getSystemService("user")).hasUserRestriction("no_modify_accounts"))
      paramMenu.add(0, 3, 0, getString(2131429000)).setIcon(2130837591).setShowAsAction(4);
    localMenuItem1.setShowAsAction(4);
    localMenuItem2.setShowAsAction(4);
    super.onCreateOptionsMenu(paramMenu, paramMenuInflater);
  }

  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    View localView = paramLayoutInflater.inflate(2130968578, paramViewGroup, false);
    Utils.prepareCustomPreferencesList(paramViewGroup, localView, (ListView)localView.findViewById(16908298), false);
    initializeUi(localView);
    return localView;
  }

  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    switch (paramMenuItem.getItemId())
    {
    default:
      return super.onOptionsItemSelected(paramMenuItem);
    case 1:
      startSyncForEnabledProviders();
      return true;
    case 2:
      cancelSyncForEnabledProviders();
      return true;
    case 3:
    }
    showDialog(100);
    return true;
  }

  public void onPause()
  {
    super.onPause();
    AccountManager.get(getActivity()).removeOnAccountsUpdatedListener(this);
  }

  public boolean onPreferenceTreeClick(PreferenceScreen paramPreferenceScreen, Preference paramPreference)
  {
    if ((paramPreference instanceof SyncStateCheckBoxPreference))
    {
      SyncStateCheckBoxPreference localSyncStateCheckBoxPreference = (SyncStateCheckBoxPreference)paramPreference;
      String str = localSyncStateCheckBoxPreference.getAuthority();
      Account localAccount = localSyncStateCheckBoxPreference.getAccount();
      boolean bool1 = ContentResolver.getSyncAutomatically(localAccount, str);
      if (localSyncStateCheckBoxPreference.isOneTimeSyncMode())
        requestOrCancelSync(localAccount, str, true);
      boolean bool2;
      do
      {
        do
        {
          return true;
          bool2 = localSyncStateCheckBoxPreference.isChecked();
        }
        while (bool2 == bool1);
        ContentResolver.setSyncAutomatically(localAccount, str, bool2);
      }
      while ((ContentResolver.getMasterSyncAutomatically()) && (bool2));
      requestOrCancelSync(localAccount, str, bool2);
      return true;
    }
    return super.onPreferenceTreeClick(paramPreferenceScreen, paramPreference);
  }

  public void onPrepareOptionsMenu(Menu paramMenu)
  {
    int i = 1;
    super.onPrepareOptionsMenu(paramMenu);
    int k;
    MenuItem localMenuItem;
    if (ContentResolver.getCurrentSync() != null)
    {
      k = i;
      localMenuItem = paramMenu.findItem(i);
      if (k != 0)
        break label57;
    }
    while (true)
    {
      localMenuItem.setVisible(i);
      paramMenu.findItem(2).setVisible(k);
      return;
      int m = 0;
      break;
      label57: int j = 0;
    }
  }

  public void onResume()
  {
    Activity localActivity = getActivity();
    AccountManager.get(localActivity).addOnAccountsUpdatedListener(this, null, false);
    updateAuthDescriptions();
    onAccountsUpdated(AccountManager.get(localActivity).getAccounts());
    super.onResume();
  }

  protected void onSyncStateUpdated()
  {
    if (!isResumed())
      return;
    setFeedsState();
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.accounts.AccountSyncSettings
 * JD-Core Version:    0.6.2
 */