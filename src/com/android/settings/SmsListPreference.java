package com.android.settings;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.preference.ListPreference;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

public class SmsListPreference extends ListPreference
{
  private Drawable[] mEntryDrawables;

  public SmsListPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }

  protected void onPrepareDialogBuilder(AlertDialog.Builder paramBuilder)
  {
    int i = findIndexOfValue(getValue());
    paramBuilder.setAdapter(new SmsArrayAdapter(getContext(), 2130968711, getEntries(), this.mEntryDrawables, i), this);
    super.onPrepareDialogBuilder(paramBuilder);
  }

  public void setEntryDrawables(Drawable[] paramArrayOfDrawable)
  {
    this.mEntryDrawables = paramArrayOfDrawable;
  }

  public class SmsArrayAdapter extends ArrayAdapter<CharSequence>
  {
    private Drawable[] mImageDrawables = null;
    private int mSelectedIndex = 0;

    public SmsArrayAdapter(Context paramInt1, int paramArrayOfCharSequence, CharSequence[] paramArrayOfDrawable, Drawable[] paramInt2, int arg6)
    {
      super(paramArrayOfCharSequence, paramArrayOfDrawable);
      int i;
      this.mSelectedIndex = i;
      this.mImageDrawables = paramInt2;
    }

    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      View localView = ((Activity)getContext()).getLayoutInflater().inflate(2130968711, paramViewGroup, false);
      CheckedTextView localCheckedTextView = (CheckedTextView)localView.findViewById(2131231063);
      localCheckedTextView.setText((CharSequence)getItem(paramInt));
      if (paramInt == this.mSelectedIndex)
        localCheckedTextView.setChecked(true);
      ((ImageView)localView.findViewById(2131231062)).setImageDrawable(this.mImageDrawables[paramInt]);
      return localView;
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.SmsListPreference
 * JD-Core Version:    0.6.2
 */