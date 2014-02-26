package com.android.settings.accounts;

import android.accounts.Account;
import android.app.ActivityManager;
import android.content.Context;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.TwoStatePreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.settings.widget.AnimatedImageView;

public class SyncStateCheckBoxPreference extends CheckBoxPreference
{
  private Account mAccount;
  private String mAuthority;
  private boolean mFailed = false;
  private boolean mIsActive = false;
  private boolean mIsPending = false;
  private boolean mOneTimeSyncMode = false;

  public SyncStateCheckBoxPreference(Context paramContext, Account paramAccount, String paramString)
  {
    super(paramContext, null);
    this.mAccount = paramAccount;
    this.mAuthority = paramString;
    setWidgetLayoutResource(2130968689);
  }

  public SyncStateCheckBoxPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    setWidgetLayoutResource(2130968689);
    this.mAccount = null;
    this.mAuthority = null;
  }

  public Account getAccount()
  {
    return this.mAccount;
  }

  public String getAuthority()
  {
    return this.mAuthority;
  }

  public boolean isOneTimeSyncMode()
  {
    return this.mOneTimeSyncMode;
  }

  public void onBindView(View paramView)
  {
    super.onBindView(paramView);
    AnimatedImageView localAnimatedImageView = (AnimatedImageView)paramView.findViewById(2131230981);
    View localView1 = paramView.findViewById(2131230980);
    int i;
    int j;
    label47: int k;
    if ((this.mIsActive) || (this.mIsPending))
    {
      i = 1;
      if (i == 0)
        break label164;
      j = 0;
      localAnimatedImageView.setVisibility(j);
      localAnimatedImageView.setAnimating(this.mIsActive);
      if ((!this.mFailed) || (i != 0))
        break label171;
      k = 1;
      label76: if (k == 0)
        break label177;
    }
    View localView2;
    label164: label171: label177: for (int m = 0; ; m = 8)
    {
      localView1.setVisibility(m);
      localView2 = paramView.findViewById(16908289);
      if (!this.mOneTimeSyncMode)
        break label184;
      localView2.setVisibility(8);
      TextView localTextView = (TextView)paramView.findViewById(16908304);
      Context localContext = getContext();
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = getSummary();
      localTextView.setText(localContext.getString(2131428991, arrayOfObject));
      return;
      i = 0;
      break;
      j = 8;
      break label47;
      k = 0;
      break label76;
    }
    label184: localView2.setVisibility(0);
  }

  protected void onClick()
  {
    if (!this.mOneTimeSyncMode)
    {
      if (ActivityManager.isUserAMonkey())
        Log.d("SyncState", "ignoring monkey's attempt to flip sync state");
    }
    else
      return;
    super.onClick();
  }

  public void setActive(boolean paramBoolean)
  {
    this.mIsActive = paramBoolean;
    notifyChanged();
  }

  public void setFailed(boolean paramBoolean)
  {
    this.mFailed = paramBoolean;
    notifyChanged();
  }

  public void setOneTimeSyncMode(boolean paramBoolean)
  {
    this.mOneTimeSyncMode = paramBoolean;
    notifyChanged();
  }

  public void setPending(boolean paramBoolean)
  {
    this.mIsPending = paramBoolean;
    notifyChanged();
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.accounts.SyncStateCheckBoxPreference
 * JD-Core Version:    0.6.2
 */