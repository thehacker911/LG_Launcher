package com.android.settings;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.preference.Preference;
import android.provider.Telephony.Carriers;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

public class ApnPreference extends Preference
  implements View.OnClickListener, CompoundButton.OnCheckedChangeListener
{
  private static CompoundButton mCurrentChecked = null;
  private static String mSelectedKey = null;
  private boolean mProtectFromCheckedChange = false;
  private boolean mSelectable = true;

  public ApnPreference(Context paramContext)
  {
    this(paramContext, null);
  }

  public ApnPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 2130771986);
  }

  public ApnPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }

  public View getView(View paramView, ViewGroup paramViewGroup)
  {
    View localView1 = super.getView(paramView, paramViewGroup);
    View localView2 = localView1.findViewById(16842753);
    RadioButton localRadioButton;
    if ((localView2 != null) && ((localView2 instanceof RadioButton)))
    {
      localRadioButton = (RadioButton)localView2;
      if (!this.mSelectable)
        break label123;
      localRadioButton.setOnCheckedChangeListener(this);
      boolean bool = getKey().equals(mSelectedKey);
      if (bool)
      {
        mCurrentChecked = localRadioButton;
        mSelectedKey = getKey();
      }
      this.mProtectFromCheckedChange = true;
      localRadioButton.setChecked(bool);
      this.mProtectFromCheckedChange = false;
    }
    while (true)
    {
      View localView3 = localView1.findViewById(16842752);
      if ((localView3 != null) && ((localView3 instanceof RelativeLayout)))
        localView3.setOnClickListener(this);
      return localView1;
      label123: localRadioButton.setVisibility(8);
    }
  }

  public void onCheckedChanged(CompoundButton paramCompoundButton, boolean paramBoolean)
  {
    Log.i("ApnPreference", "ID: " + getKey() + " :" + paramBoolean);
    if (this.mProtectFromCheckedChange)
      return;
    if (paramBoolean)
    {
      if (mCurrentChecked != null)
        mCurrentChecked.setChecked(false);
      mCurrentChecked = paramCompoundButton;
      mSelectedKey = getKey();
      callChangeListener(mSelectedKey);
      return;
    }
    mCurrentChecked = null;
    mSelectedKey = null;
  }

  public void onClick(View paramView)
  {
    if ((paramView != null) && (16842752 == paramView.getId()))
    {
      Context localContext = getContext();
      if (localContext != null)
      {
        int i = Integer.parseInt(getKey());
        localContext.startActivity(new Intent("android.intent.action.EDIT", ContentUris.withAppendedId(Telephony.Carriers.CONTENT_URI, i)));
      }
    }
  }

  public void setChecked()
  {
    mSelectedKey = getKey();
  }

  public void setSelectable(boolean paramBoolean)
  {
    this.mSelectable = paramBoolean;
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.ApnPreference
 * JD-Core Version:    0.6.2
 */