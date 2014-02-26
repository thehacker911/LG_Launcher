package com.android.settings.users;

import android.content.Context;
import android.os.UserHandle;
import android.os.UserManager;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;

public class UserPreference extends Preference
{
  private View.OnClickListener mDeleteClickListener;
  private int mSerialNumber = -1;
  private View.OnClickListener mSettingsClickListener;
  private int mUserId = -10;

  public UserPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, -10, null, null);
  }

  UserPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt, View.OnClickListener paramOnClickListener1, View.OnClickListener paramOnClickListener2)
  {
    super(paramContext, paramAttributeSet);
    if ((paramOnClickListener2 != null) || (paramOnClickListener1 != null))
      setWidgetLayoutResource(2130968686);
    this.mDeleteClickListener = paramOnClickListener2;
    this.mSettingsClickListener = paramOnClickListener1;
    this.mUserId = paramInt;
  }

  private int getSerialNumber()
  {
    if (this.mUserId == UserHandle.myUserId())
      return -2147483648;
    if (this.mSerialNumber < 0)
    {
      if (this.mUserId == -10)
        return 2147483647;
      this.mSerialNumber = ((UserManager)getContext().getSystemService("user")).getUserSerialNumber(this.mUserId);
      if (this.mSerialNumber < 0)
        return this.mUserId;
    }
    return this.mSerialNumber;
  }

  public int compareTo(Preference paramPreference)
  {
    if ((!(paramPreference instanceof UserPreference)) || (getSerialNumber() > ((UserPreference)paramPreference).getSerialNumber()))
      return 1;
    return -1;
  }

  public int getUserId()
  {
    return this.mUserId;
  }

  protected void onBindView(View paramView)
  {
    View localView1 = paramView.findViewById(2131230977);
    View localView2 = paramView.findViewById(2131230975);
    View localView3 = paramView.findViewById(2131230978);
    View localView4;
    if (localView3 != null)
    {
      if (this.mDeleteClickListener != null)
      {
        localView3.setOnClickListener(this.mDeleteClickListener);
        localView3.setTag(this);
      }
    }
    else
    {
      localView4 = paramView.findViewById(2131230976);
      if (localView4 != null)
      {
        if (this.mSettingsClickListener == null)
          break label119;
        localView4.setOnClickListener(this.mSettingsClickListener);
        localView4.setTag(this);
        if (this.mDeleteClickListener != null)
          localView2.setVisibility(8);
      }
    }
    while (true)
    {
      super.onBindView(paramView);
      return;
      localView3.setVisibility(8);
      localView1.setVisibility(8);
      break;
      label119: localView4.setVisibility(8);
      localView2.setVisibility(8);
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.users.UserPreference
 * JD-Core Version:    0.6.2
 */