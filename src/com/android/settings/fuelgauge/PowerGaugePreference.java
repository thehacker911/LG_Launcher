package com.android.settings.fuelgauge;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.preference.Preference;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class PowerGaugePreference extends Preference
{
  private BatterySipper mInfo;
  private int mProgress;
  private CharSequence mProgressText;

  public PowerGaugePreference(Context paramContext, Drawable paramDrawable, BatterySipper paramBatterySipper)
  {
    super(paramContext);
    setLayoutResource(2130968586);
    if (paramDrawable != null);
    while (true)
    {
      setIcon(paramDrawable);
      this.mInfo = paramBatterySipper;
      return;
      paramDrawable = new ColorDrawable(0);
    }
  }

  BatterySipper getInfo()
  {
    return this.mInfo;
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
    arrayOfObject[0] = Integer.valueOf((int)Math.ceil(paramDouble2));
    this.mProgressText = localResources.getString(2131429185, arrayOfObject);
    notifyChanged();
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.fuelgauge.PowerGaugePreference
 * JD-Core Version:    0.6.2
 */