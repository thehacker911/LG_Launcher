package com.android.settings.accounts;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserManager;
import android.util.Log;
import android.widget.Toast;
import com.android.settings.Utils;
import java.io.IOException;

public class AddAccountSettings extends Activity
{
  private boolean mAddAccountCalled = false;
  private final AccountManagerCallback<Bundle> mCallback = new AccountManagerCallback()
  {
    public void run(AccountManagerFuture<Bundle> paramAnonymousAccountManagerFuture)
    {
      int i = 1;
      try
      {
        Bundle localBundle1 = (Bundle)paramAnonymousAccountManagerFuture.getResult();
        Intent localIntent = (Intent)localBundle1.get("intent");
        if (localIntent != null)
        {
          i = 0;
          Bundle localBundle2 = new Bundle();
          localBundle2.putParcelable("pendingIntent", AddAccountSettings.this.mPendingIntent);
          localBundle2.putBoolean("hasMultipleUsers", Utils.hasMultipleUsers(AddAccountSettings.this));
          localIntent.putExtras(localBundle2);
          AddAccountSettings.this.startActivityForResult(localIntent, 2);
        }
        while (true)
        {
          if (Log.isLoggable("AccountSettings", 2))
            Log.v("AccountSettings", "account added: " + localBundle1);
          return;
          AddAccountSettings.this.setResult(-1);
          if (AddAccountSettings.this.mPendingIntent != null)
          {
            AddAccountSettings.this.mPendingIntent.cancel();
            AddAccountSettings.access$002(AddAccountSettings.this, null);
          }
        }
      }
      catch (OperationCanceledException localOperationCanceledException)
      {
        if (Log.isLoggable("AccountSettings", 2))
          Log.v("AccountSettings", "addAccount was canceled");
        return;
      }
      catch (IOException localIOException)
      {
        if (Log.isLoggable("AccountSettings", 2))
          Log.v("AccountSettings", "addAccount failed: " + localIOException);
        return;
      }
      catch (AuthenticatorException localAuthenticatorException)
      {
        if (Log.isLoggable("AccountSettings", 2))
          Log.v("AccountSettings", "addAccount failed: " + localAuthenticatorException);
        return;
      }
      finally
      {
        if (i != 0)
          AddAccountSettings.this.finish();
      }
    }
  };
  private PendingIntent mPendingIntent;

  private void addAccount(String paramString)
  {
    Bundle localBundle = new Bundle();
    this.mPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(), 0);
    localBundle.putParcelable("pendingIntent", this.mPendingIntent);
    localBundle.putBoolean("hasMultipleUsers", Utils.hasMultipleUsers(this));
    AccountManager.get(this).addAccount(paramString, null, null, localBundle, null, this.mCallback, null);
    this.mAddAccountCalled = true;
  }

  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    switch (paramInt1)
    {
    default:
      return;
    case 1:
      if (paramInt2 == 0)
      {
        setResult(paramInt2);
        finish();
        return;
      }
      addAccount(paramIntent.getStringExtra("selected_account"));
      return;
    case 2:
    }
    setResult(paramInt2);
    if (this.mPendingIntent != null)
    {
      this.mPendingIntent.cancel();
      this.mPendingIntent = null;
    }
    finish();
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    if (paramBundle != null)
    {
      this.mAddAccountCalled = paramBundle.getBoolean("AddAccountCalled");
      if (Log.isLoggable("AccountSettings", 2))
        Log.v("AccountSettings", "restored");
    }
    if (((UserManager)getSystemService("user")).hasUserRestriction("no_modify_accounts"))
    {
      Toast.makeText(this, 2131429221, 1).show();
      finish();
      return;
    }
    if (this.mAddAccountCalled)
    {
      finish();
      return;
    }
    String[] arrayOfString1 = getIntent().getStringArrayExtra("authorities");
    String[] arrayOfString2 = getIntent().getStringArrayExtra("account_types");
    Intent localIntent = new Intent(this, ChooseAccountActivity.class);
    if (arrayOfString1 != null)
      localIntent.putExtra("authorities", arrayOfString1);
    if (arrayOfString2 != null)
      localIntent.putExtra("account_types", arrayOfString2);
    startActivityForResult(localIntent, 1);
  }

  protected void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    paramBundle.putBoolean("AddAccountCalled", this.mAddAccountCalled);
    if (Log.isLoggable("AccountSettings", 2))
      Log.v("AccountSettings", "saved");
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.accounts.AddAccountSettings
 * JD-Core Version:    0.6.2
 */