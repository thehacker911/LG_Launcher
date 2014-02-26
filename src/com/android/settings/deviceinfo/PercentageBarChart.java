package com.android.settings.deviceinfo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;
import com.android.settings.R.styleable;
import java.util.Collection;
import java.util.Iterator;

public class PercentageBarChart extends View
{
  private final Paint mEmptyPaint = new Paint();
  private Collection<Entry> mEntries;
  private int mMinTickWidth = 1;

  public PercentageBarChart(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.PercentageBarChart);
    this.mMinTickWidth = localTypedArray.getDimensionPixelSize(1, 1);
    int i = localTypedArray.getColor(0, -16777216);
    localTypedArray.recycle();
    this.mEmptyPaint.setColor(i);
    this.mEmptyPaint.setStyle(Paint.Style.FILL);
  }

  public static Entry createEntry(int paramInt1, float paramFloat, int paramInt2)
  {
    Paint localPaint = new Paint();
    localPaint.setColor(paramInt2);
    localPaint.setStyle(Paint.Style.FILL);
    return new Entry(paramInt1, paramFloat, localPaint);
  }

  protected void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    int i = getPaddingLeft();
    int j = getWidth() - getPaddingRight();
    int k = getPaddingTop();
    int m = getHeight() - getPaddingBottom();
    int n = j - i;
    if (isLayoutRtl())
    {
      float f10 = j;
      if (this.mEntries != null)
      {
        Iterator localIterator2 = this.mEntries.iterator();
        while (localIterator2.hasNext())
        {
          Entry localEntry2 = (Entry)localIterator2.next();
          if (localEntry2.percentage == 0.0F);
          float f15;
          for (float f14 = 0.0F; ; f14 = Math.max(this.mMinTickWidth, n * localEntry2.percentage))
          {
            f15 = f10 - f14;
            if (f15 >= i)
              break;
            paramCanvas.drawRect(i, k, f10, m, localEntry2.paint);
            return;
          }
          paramCanvas.drawRect(f15, k, f10, m, localEntry2.paint);
          f10 = f15;
        }
      }
      float f11 = i;
      float f12 = k;
      float f13 = m;
      Paint localPaint3 = this.mEmptyPaint;
      paramCanvas.drawRect(f11, f12, f10, f13, localPaint3);
      return;
    }
    float f1 = i;
    if (this.mEntries != null)
    {
      Iterator localIterator1 = this.mEntries.iterator();
      while (localIterator1.hasNext())
      {
        Entry localEntry1 = (Entry)localIterator1.next();
        if (localEntry1.percentage == 0.0F);
        float f6;
        for (float f5 = 0.0F; ; f5 = Math.max(this.mMinTickWidth, n * localEntry1.percentage))
        {
          f6 = f1 + f5;
          if (f6 <= j)
            break;
          float f7 = k;
          float f8 = j;
          float f9 = m;
          Paint localPaint2 = localEntry1.paint;
          paramCanvas.drawRect(f1, f7, f8, f9, localPaint2);
          return;
        }
        paramCanvas.drawRect(f1, k, f6, m, localEntry1.paint);
        f1 = f6;
      }
    }
    float f2 = k;
    float f3 = j;
    float f4 = m;
    Paint localPaint1 = this.mEmptyPaint;
    paramCanvas.drawRect(f1, f2, f3, f4, localPaint1);
  }

  public void setBackgroundColor(int paramInt)
  {
    this.mEmptyPaint.setColor(paramInt);
  }

  public void setEntries(Collection<Entry> paramCollection)
  {
    this.mEntries = paramCollection;
  }

  public static class Entry
    implements Comparable<Entry>
  {
    public final int order;
    public final Paint paint;
    public final float percentage;

    protected Entry(int paramInt, float paramFloat, Paint paramPaint)
    {
      this.order = paramInt;
      this.percentage = paramFloat;
      this.paint = paramPaint;
    }

    public int compareTo(Entry paramEntry)
    {
      return this.order - paramEntry.order;
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.deviceinfo.PercentageBarChart
 * JD-Core Version:    0.6.2
 */