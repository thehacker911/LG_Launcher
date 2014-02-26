package com.android.settings.accessibility;

import android.content.Context;
import android.widget.Switch;

public class ToggleSwitch extends Switch
{
  private OnBeforeCheckedChangeListener mOnBeforeListener;

  public ToggleSwitch(Context paramContext)
  {
    super(paramContext);
  }

  public void setChecked(boolean paramBoolean)
  {
    if ((this.mOnBeforeListener != null) && (this.mOnBeforeListener.onBeforeCheckedChanged(this, paramBoolean)))
      return;
    super.setChecked(paramBoolean);
  }

  public void setCheckedInternal(boolean paramBoolean)
  {
    super.setChecked(paramBoolean);
  }

  public void setOnBeforeCheckedChangeListener(OnBeforeCheckedChangeListener paramOnBeforeCheckedChangeListener)
  {
    this.mOnBeforeListener = paramOnBeforeCheckedChangeListener;
  }

  public static abstract interface OnBeforeCheckedChangeListener
  {
    public abstract boolean onBeforeCheckedChanged(ToggleSwitch paramToggleSwitch, boolean paramBoolean);
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.accessibility.ToggleSwitch
 * JD-Core Version:    0.6.2
 */