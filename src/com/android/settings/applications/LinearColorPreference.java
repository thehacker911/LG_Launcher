package com.android.settings.applications;

import android.content.Context;
import android.preference.Preference;
import android.view.View;

public class LinearColorPreference extends Preference
{
  int mColoredRegions = 7;
  float mGreenRatio;
  LinearColorBar.OnRegionTappedListener mOnRegionTappedListener;
  float mRedRatio;
  float mYellowRatio;

  public LinearColorPreference(Context paramContext)
  {
    super(paramContext);
    setLayoutResource(2130968679);
  }

  protected void onBindView(View paramView)
  {
    super.onBindView(paramView);
    LinearColorBar localLinearColorBar = (LinearColorBar)paramView.findViewById(16842755);
    localLinearColorBar.setShowIndicator(false);
    localLinearColorBar.setColors(-5615568, -5592528, -13587888);
    localLinearColorBar.setRatios(this.mRedRatio, this.mYellowRatio, this.mGreenRatio);
    localLinearColorBar.setColoredRegions(this.mColoredRegions);
    localLinearColorBar.setOnRegionTappedListener(this.mOnRegionTappedListener);
  }

  public void setColoredRegions(int paramInt)
  {
    this.mColoredRegions = paramInt;
    notifyChanged();
  }

  public void setOnRegionTappedListener(LinearColorBar.OnRegionTappedListener paramOnRegionTappedListener)
  {
    this.mOnRegionTappedListener = paramOnRegionTappedListener;
    notifyChanged();
  }

  public void setRatios(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    this.mRedRatio = paramFloat1;
    this.mYellowRatio = paramFloat2;
    this.mGreenRatio = paramFloat3;
    notifyChanged();
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.applications.LinearColorPreference
 * JD-Core Version:    0.6.2
 */