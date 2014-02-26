package com.android.settings.widget;

import android.content.Context;
import android.content.res.CompatibilityInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import com.android.internal.util.Preconditions;
import com.android.settings.DataUsageSummary;
import com.android.settings.R.styleable;

public class ChartGridView extends View
{
  private Drawable mBorder;
  private ChartAxis mHoriz;
  private int mLabelColor;
  private Layout mLayoutEnd;
  private Layout mLayoutStart;
  private Drawable mPrimary;
  private Drawable mSecondary;
  private ChartAxis mVert;

  public ChartGridView(Context paramContext)
  {
    this(paramContext, null, 0);
  }

  public ChartGridView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }

  public ChartGridView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    setWillNotDraw(false);
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.ChartGridView, paramInt, 0);
    this.mPrimary = localTypedArray.getDrawable(1);
    this.mSecondary = localTypedArray.getDrawable(2);
    this.mBorder = localTypedArray.getDrawable(3);
    this.mLabelColor = localTypedArray.getColor(0, -65536);
    localTypedArray.recycle();
  }

  private Layout makeLayout(CharSequence paramCharSequence)
  {
    Resources localResources = getResources();
    TextPaint localTextPaint = new TextPaint(1);
    localTextPaint.density = localResources.getDisplayMetrics().density;
    localTextPaint.setCompatibilityScaling(localResources.getCompatibilityInfo().applicationScale);
    localTextPaint.setColor(this.mLabelColor);
    localTextPaint.setTextSize(TypedValue.applyDimension(2, 10.0F, localResources.getDisplayMetrics()));
    return new StaticLayout(paramCharSequence, localTextPaint, (int)Math.ceil(Layout.getDesiredWidth(paramCharSequence, localTextPaint)), Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
  }

  void init(ChartAxis paramChartAxis1, ChartAxis paramChartAxis2)
  {
    this.mHoriz = ((ChartAxis)Preconditions.checkNotNull(paramChartAxis1, "missing horiz"));
    this.mVert = ((ChartAxis)Preconditions.checkNotNull(paramChartAxis2, "missing vert"));
  }

  protected void onDraw(Canvas paramCanvas)
  {
    int i = getWidth();
    int j = getHeight();
    Drawable localDrawable1 = this.mSecondary;
    int k = this.mSecondary.getIntrinsicHeight();
    for (float f2 : this.mVert.getTickPoints())
    {
      int i6 = (int)Math.min(f2 + k, j);
      localDrawable1.setBounds(0, (int)f2, i, i6);
      localDrawable1.draw(paramCanvas);
    }
    Drawable localDrawable2 = this.mPrimary;
    int i1 = this.mPrimary.getIntrinsicWidth();
    this.mPrimary.getIntrinsicHeight();
    for (float f1 : this.mHoriz.getTickPoints())
    {
      int i5 = (int)Math.min(f1 + i1, i);
      localDrawable2.setBounds((int)f1, 0, i5, j);
      localDrawable2.draw(paramCanvas);
    }
    this.mBorder.setBounds(0, 0, i, j);
    this.mBorder.draw(paramCanvas);
    if (this.mLayoutStart != null);
    for (int i4 = this.mLayoutStart.getHeight() / 8; ; i4 = 0)
    {
      Layout localLayout1 = this.mLayoutStart;
      if (localLayout1 != null)
      {
        paramCanvas.save();
        paramCanvas.translate(0.0F, j + i4);
        localLayout1.draw(paramCanvas);
        paramCanvas.restore();
      }
      Layout localLayout2 = this.mLayoutEnd;
      if (localLayout2 != null)
      {
        paramCanvas.save();
        paramCanvas.translate(i - localLayout2.getWidth(), j + i4);
        localLayout2.draw(paramCanvas);
        paramCanvas.restore();
      }
      return;
    }
  }

  void setBounds(long paramLong1, long paramLong2)
  {
    Context localContext = getContext();
    this.mLayoutStart = makeLayout(DataUsageSummary.formatDateRange(localContext, paramLong1, paramLong1));
    this.mLayoutEnd = makeLayout(DataUsageSummary.formatDateRange(localContext, paramLong2, paramLong2));
    invalidate();
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.widget.ChartGridView
 * JD-Core Version:    0.6.2
 */