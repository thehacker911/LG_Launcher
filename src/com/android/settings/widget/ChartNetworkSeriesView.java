package com.android.settings.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.net.NetworkStatsHistory;
import android.net.NetworkStatsHistory.Entry;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import com.android.internal.util.Preconditions;
import com.android.settings.R.styleable;

public class ChartNetworkSeriesView extends View
{
  private long mEnd;
  private long mEndTime = -9223372036854775808L;
  private boolean mEstimateVisible = false;
  private ChartAxis mHoriz;
  private long mMax;
  private long mMaxEstimate;
  private Paint mPaintEstimate;
  private Paint mPaintFill;
  private Paint mPaintFillSecondary;
  private Paint mPaintStroke;
  private Path mPathEstimate;
  private Path mPathFill;
  private Path mPathStroke;
  private boolean mPathValid = false;
  private long mPrimaryLeft;
  private long mPrimaryRight;
  private long mStart;
  private NetworkStatsHistory mStats;
  private ChartAxis mVert;

  public ChartNetworkSeriesView(Context paramContext)
  {
    this(paramContext, null, 0);
  }

  public ChartNetworkSeriesView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }

  public ChartNetworkSeriesView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.ChartNetworkSeriesView, paramInt, 0);
    setChartColor(localTypedArray.getColor(0, -65536), localTypedArray.getColor(1, -65536), localTypedArray.getColor(2, -65536));
    setWillNotDraw(false);
    localTypedArray.recycle();
    this.mPathStroke = new Path();
    this.mPathFill = new Path();
    this.mPathEstimate = new Path();
  }

  private void generatePath()
  {
    this.mMax = 0L;
    this.mPathStroke.reset();
    this.mPathFill.reset();
    this.mPathEstimate.reset();
    this.mPathValid = true;
    if ((this.mStats == null) || (this.mStats.size() < 2))
      return;
    getWidth();
    int i = getHeight();
    float f1 = 0.0F;
    float f2 = i;
    long l1 = this.mHoriz.convertToValue(0.0F);
    this.mPathStroke.moveTo(0.0F, f2);
    this.mPathFill.moveTo(0.0F, f2);
    long l2 = 0L;
    NetworkStatsHistory.Entry localEntry = null;
    int j = this.mStats.getIndexBefore(this.mStart);
    int k = this.mStats.getIndexAfter(this.mEnd);
    int m = j;
    if (m <= k)
    {
      localEntry = this.mStats.getValues(m, localEntry);
      long l3 = localEntry.bucketStart;
      long l4 = l3 + localEntry.bucketDuration;
      float f3 = this.mHoriz.convertToPoint(l3);
      float f4 = this.mHoriz.convertToPoint(l4);
      if (f4 < 0.0F);
      while (true)
      {
        m++;
        break;
        l2 += localEntry.rxBytes + localEntry.txBytes;
        float f5 = f2;
        float f6 = this.mVert.convertToPoint(l2);
        if (l1 != l3)
        {
          this.mPathStroke.lineTo(f3, f5);
          this.mPathFill.lineTo(f3, f5);
        }
        this.mPathStroke.lineTo(f4, f6);
        this.mPathFill.lineTo(f4, f6);
        f1 = f4;
        f2 = f6;
        l1 = l4;
      }
    }
    if (l1 < this.mEndTime)
    {
      f1 = this.mHoriz.convertToPoint(this.mEndTime);
      this.mPathStroke.lineTo(f1, f2);
      this.mPathFill.lineTo(f1, f2);
    }
    this.mPathFill.lineTo(f1, i);
    this.mPathFill.lineTo(0.0F, i);
    this.mMax = l2;
    invalidate();
  }

  public void bindNetworkStats(NetworkStatsHistory paramNetworkStatsHistory)
  {
    this.mStats = paramNetworkStatsHistory;
    invalidatePath();
    invalidate();
  }

  public long getMaxEstimate()
  {
    return this.mMaxEstimate;
  }

  public long getMaxVisible()
  {
    if (this.mEstimateVisible);
    for (long l = this.mMaxEstimate; ; l = this.mMax)
    {
      if ((l <= 0L) && (this.mStats != null))
      {
        NetworkStatsHistory.Entry localEntry = this.mStats.getValues(this.mStart, this.mEnd, null);
        l = localEntry.rxBytes + localEntry.txBytes;
      }
      return l;
    }
  }

  void init(ChartAxis paramChartAxis1, ChartAxis paramChartAxis2)
  {
    this.mHoriz = ((ChartAxis)Preconditions.checkNotNull(paramChartAxis1, "missing horiz"));
    this.mVert = ((ChartAxis)Preconditions.checkNotNull(paramChartAxis2, "missing vert"));
  }

  public void invalidatePath()
  {
    this.mPathValid = false;
    this.mMax = 0L;
    invalidate();
  }

  protected void onDraw(Canvas paramCanvas)
  {
    if (!this.mPathValid)
      generatePath();
    float f1 = this.mHoriz.convertToPoint(this.mPrimaryLeft);
    float f2 = this.mHoriz.convertToPoint(this.mPrimaryRight);
    if (this.mEstimateVisible)
    {
      int m = paramCanvas.save();
      paramCanvas.clipRect(0, 0, getWidth(), getHeight());
      paramCanvas.drawPath(this.mPathEstimate, this.mPaintEstimate);
      paramCanvas.restoreToCount(m);
    }
    int i = paramCanvas.save();
    paramCanvas.clipRect(0.0F, 0.0F, f1, getHeight());
    paramCanvas.drawPath(this.mPathFill, this.mPaintFillSecondary);
    paramCanvas.restoreToCount(i);
    int j = paramCanvas.save();
    paramCanvas.clipRect(f2, 0.0F, getWidth(), getHeight());
    paramCanvas.drawPath(this.mPathFill, this.mPaintFillSecondary);
    paramCanvas.restoreToCount(j);
    int k = paramCanvas.save();
    paramCanvas.clipRect(f1, 0.0F, f2, getHeight());
    paramCanvas.drawPath(this.mPathFill, this.mPaintFill);
    paramCanvas.drawPath(this.mPathStroke, this.mPaintStroke);
    paramCanvas.restoreToCount(k);
  }

  public void setBounds(long paramLong1, long paramLong2)
  {
    this.mStart = paramLong1;
    this.mEnd = paramLong2;
  }

  public void setChartColor(int paramInt1, int paramInt2, int paramInt3)
  {
    this.mPaintStroke = new Paint();
    this.mPaintStroke.setStrokeWidth(4.0F * getResources().getDisplayMetrics().density);
    this.mPaintStroke.setColor(paramInt1);
    this.mPaintStroke.setStyle(Paint.Style.STROKE);
    this.mPaintStroke.setAntiAlias(true);
    this.mPaintFill = new Paint();
    this.mPaintFill.setColor(paramInt2);
    this.mPaintFill.setStyle(Paint.Style.FILL);
    this.mPaintFill.setAntiAlias(true);
    this.mPaintFillSecondary = new Paint();
    this.mPaintFillSecondary.setColor(paramInt3);
    this.mPaintFillSecondary.setStyle(Paint.Style.FILL);
    this.mPaintFillSecondary.setAntiAlias(true);
    this.mPaintEstimate = new Paint();
    this.mPaintEstimate.setStrokeWidth(3.0F);
    this.mPaintEstimate.setColor(paramInt3);
    this.mPaintEstimate.setStyle(Paint.Style.STROKE);
    this.mPaintEstimate.setAntiAlias(true);
    this.mPaintEstimate.setPathEffect(new DashPathEffect(new float[] { 10.0F, 10.0F }, 1.0F));
  }

  public void setEndTime(long paramLong)
  {
    this.mEndTime = paramLong;
  }

  public void setEstimateVisible(boolean paramBoolean)
  {
    this.mEstimateVisible = false;
    invalidate();
  }

  public void setPrimaryRange(long paramLong1, long paramLong2)
  {
    this.mPrimaryLeft = paramLong1;
    this.mPrimaryRight = paramLong2;
    invalidate();
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.widget.ChartNetworkSeriesView
 * JD-Core Version:    0.6.2
 */