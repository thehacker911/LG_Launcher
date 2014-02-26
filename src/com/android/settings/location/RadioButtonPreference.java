package com.android.settings.location;

import android.content.Context;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class RadioButtonPreference extends CheckBoxPreference
{
  private OnClickListener mListener = null;

  public RadioButtonPreference(Context paramContext)
  {
    this(paramContext, null);
  }

  public RadioButtonPreference(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 16842895);
  }

  public RadioButtonPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    setWidgetLayoutResource(2130968687);
  }

  protected void onBindView(View paramView)
  {
    super.onBindView(paramView);
    TextView localTextView = (TextView)paramView.findViewById(16908310);
    if (localTextView != null)
    {
      localTextView.setSingleLine(false);
      localTextView.setMaxLines(3);
    }
  }

  public void onClick()
  {
    if (this.mListener != null)
      this.mListener.onRadioButtonClicked(this);
  }

  void setOnClickListener(OnClickListener paramOnClickListener)
  {
    this.mListener = paramOnClickListener;
  }

  public static abstract interface OnClickListener
  {
    public abstract void onRadioButtonClicked(RadioButtonPreference paramRadioButtonPreference);
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.location.RadioButtonPreference
 * JD-Core Version:    0.6.2
 */