package com.android.settings;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;

public class ProgressCategory extends ProgressCategoryBase
{
  private final int mEmptyTextRes;
  private boolean mNoDeviceFoundAdded;
  private Preference mNoDeviceFoundPreference;
  private boolean mProgress = false;

  public ProgressCategory(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet);
    setLayoutResource(2130968681);
    this.mEmptyTextRes = paramInt;
  }

  public void onBindView(View paramView)
  {
    super.onBindView(paramView);
    View localView = paramView.findViewById(2131230963);
    int i;
    int j;
    if ((getPreferenceCount() == 0) || ((getPreferenceCount() == 1) && (getPreference(0) == this.mNoDeviceFoundPreference)))
    {
      i = 1;
      if (!this.mProgress)
        break label95;
      j = 0;
      label51: localView.setVisibility(j);
      if ((!this.mProgress) && (i != 0))
        break label102;
      if (this.mNoDeviceFoundAdded)
      {
        removePreference(this.mNoDeviceFoundPreference);
        this.mNoDeviceFoundAdded = false;
      }
    }
    label95: label102: 
    while (this.mNoDeviceFoundAdded)
    {
      return;
      i = 0;
      break;
      j = 8;
      break label51;
    }
    if (this.mNoDeviceFoundPreference == null)
    {
      this.mNoDeviceFoundPreference = new Preference(getContext());
      this.mNoDeviceFoundPreference.setLayoutResource(2130968671);
      this.mNoDeviceFoundPreference.setTitle(this.mEmptyTextRes);
      this.mNoDeviceFoundPreference.setSelectable(false);
    }
    addPreference(this.mNoDeviceFoundPreference);
    this.mNoDeviceFoundAdded = true;
  }

  public void setProgress(boolean paramBoolean)
  {
    this.mProgress = paramBoolean;
    notifyChanged();
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.ProgressCategory
 * JD-Core Version:    0.6.2
 */