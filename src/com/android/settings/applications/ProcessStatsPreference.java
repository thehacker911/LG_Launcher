package com.android.settings.applications;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.preference.Preference;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ProcessStatsPreference extends Preference
{
  private final ProcStatsEntry mEntry;
  private int mProgress;
  private CharSequence mProgressText;

  public ProcessStatsPreference(Context paramContext, Drawable paramDrawable, ProcStatsEntry paramProcStatsEntry)
  {
    super(paramContext);
    this.mEntry = paramProcStatsEntry;
    setLayoutResource(2130968586);
    if (paramDrawable != null);
    while (true)
    {
      setIcon(paramDrawable);
      return;
      paramDrawable = new ColorDrawable(0);
    }
  }

  public ProcStatsEntry getEntry()
  {
    return this.mEntry;
  }

  protected void onBindView(View paramView)
  {
    super.onBindView(paramView);
    ((ProgressBar)paramView.findViewById(16908301)).setProgress(this.mProgress);
    ((TextView)paramView.findViewById(16908308)).setText(this.mProgressText);
  }

  public void setPercent(double paramDouble1, double paramDouble2)
  {
    this.mProgress = ((int)Math.ceil(paramDouble1));
    Resources localResources = getContext().getResources();
    Object[] arrayOfObject = new Object[1];
    arrayOfObject[0] = Integer.valueOf((int)Math.round(paramDouble2));
    this.mProgressText = localResources.getString(2131429185, arrayOfObject);
    notifyChanged();
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.applications.ProcessStatsPreference
 * JD-Core Version:    0.6.2
 */