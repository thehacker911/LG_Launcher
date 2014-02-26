package com.android.settings;

import android.accounts.Account;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.preference.Preference;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import java.util.ArrayList;

public class AccountPreference extends Preference
{
  private Account mAccount;
  private ArrayList<String> mAuthorities;
  private boolean mShowTypeIcon;
  private int mStatus;
  private ImageView mSyncStatusIcon;

  public AccountPreference(Context paramContext, Account paramAccount, Drawable paramDrawable, ArrayList<String> paramArrayList, boolean paramBoolean)
  {
    super(paramContext);
    this.mAccount = paramAccount;
    this.mAuthorities = paramArrayList;
    this.mShowTypeIcon = paramBoolean;
    if (paramBoolean)
      setIcon(paramDrawable);
    while (true)
    {
      setTitle(this.mAccount.name);
      setSummary("");
      setPersistent(false);
      setSyncStatus(1, false);
      return;
      setIcon(getSyncStatusIcon(1));
    }
  }

  private String getSyncContentDescription(int paramInt)
  {
    switch (paramInt)
    {
    default:
      Log.e("AccountPreference", "Unknown sync status: " + paramInt);
      return getContext().getString(2131428971);
    case 0:
      return getContext().getString(2131428969);
    case 1:
      return getContext().getString(2131428970);
    case 2:
    }
    return getContext().getString(2131428971);
  }

  private int getSyncStatusIcon(int paramInt)
  {
    switch (paramInt)
    {
    default:
      Log.e("AccountPreference", "Unknown sync status: " + paramInt);
      return 2130837634;
    case 0:
      return 2130837630;
    case 1:
      return 2130837632;
    case 2:
      return 2130837634;
    case 3:
    }
    return 2130837630;
  }

  private int getSyncStatusMessage(int paramInt)
  {
    switch (paramInt)
    {
    default:
      Log.e("AccountPreference", "Unknown sync status: " + paramInt);
      return 2131428984;
    case 0:
      return 2131428982;
    case 1:
      return 2131428983;
    case 2:
      return 2131428984;
    case 3:
    }
    return 2131428986;
  }

  public Account getAccount()
  {
    return this.mAccount;
  }

  public ArrayList<String> getAuthorities()
  {
    return this.mAuthorities;
  }

  protected void onBindView(View paramView)
  {
    super.onBindView(paramView);
    if (!this.mShowTypeIcon)
    {
      this.mSyncStatusIcon = ((ImageView)paramView.findViewById(16908294));
      this.mSyncStatusIcon.setImageResource(getSyncStatusIcon(this.mStatus));
      this.mSyncStatusIcon.setContentDescription(getSyncContentDescription(this.mStatus));
    }
  }

  public void setSyncStatus(int paramInt, boolean paramBoolean)
  {
    this.mStatus = paramInt;
    if ((!this.mShowTypeIcon) && (this.mSyncStatusIcon != null))
    {
      this.mSyncStatusIcon.setImageResource(getSyncStatusIcon(paramInt));
      this.mSyncStatusIcon.setContentDescription(getSyncContentDescription(this.mStatus));
    }
    if (paramBoolean)
      setSummary(getSyncStatusMessage(paramInt));
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.AccountPreference
 * JD-Core Version:    0.6.2
 */