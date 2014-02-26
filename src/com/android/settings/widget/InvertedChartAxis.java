package com.android.settings.widget;

import android.content.res.Resources;
import android.text.SpannableStringBuilder;

public class InvertedChartAxis
  implements ChartAxis
{
  private float mSize;
  private final ChartAxis mWrapped;

  public InvertedChartAxis(ChartAxis paramChartAxis)
  {
    this.mWrapped = paramChartAxis;
  }

  public long buildLabel(Resources paramResources, SpannableStringBuilder paramSpannableStringBuilder, long paramLong)
  {
    return this.mWrapped.buildLabel(paramResources, paramSpannableStringBuilder, paramLong);
  }

  public float convertToPoint(long paramLong)
  {
    return this.mSize - this.mWrapped.convertToPoint(paramLong);
  }

  public long convertToValue(float paramFloat)
  {
    return this.mWrapped.convertToValue(this.mSize - paramFloat);
  }

  public float[] getTickPoints()
  {
    float[] arrayOfFloat = this.mWrapped.getTickPoints();
    for (int i = 0; i < arrayOfFloat.length; i++)
      arrayOfFloat[i] = (this.mSize - arrayOfFloat[i]);
    return arrayOfFloat;
  }

  public boolean setBounds(long paramLong1, long paramLong2)
  {
    return this.mWrapped.setBounds(paramLong1, paramLong2);
  }

  public boolean setSize(float paramFloat)
  {
    this.mSize = paramFloat;
    return this.mWrapped.setSize(paramFloat);
  }

  public int shouldAdjustAxis(long paramLong)
  {
    return this.mWrapped.shouldAdjustAxis(paramLong);
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.widget.InvertedChartAxis
 * JD-Core Version:    0.6.2
 */