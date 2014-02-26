package com.android.settings.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import com.google.android.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;

public class PieChartView extends View
{
  private Matrix mMatrix = new Matrix();
  private int mOriginAngle;
  private Paint mPaintOutline = new Paint();
  private Path mPathOutline = new Path();
  private Path mPathSide = new Path();
  private Path mPathSideOutline = new Path();
  private int mSideWidth;
  private ArrayList<Slice> mSlices = Lists.newArrayList();

  public PieChartView(Context paramContext)
  {
    this(paramContext, null);
  }

  public PieChartView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }

  public PieChartView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    this.mPaintOutline.setColor(-16777216);
    this.mPaintOutline.setStyle(Paint.Style.STROKE);
    this.mPaintOutline.setStrokeWidth(3.0F * getResources().getDisplayMetrics().density);
    this.mPaintOutline.setAntiAlias(true);
    this.mSideWidth = ((int)(20.0F * getResources().getDisplayMetrics().density));
    setWillNotDraw(false);
  }

  private static Paint buildFillPaint(int paramInt, Resources paramResources)
  {
    Paint localPaint = new Paint();
    localPaint.setColor(paramInt);
    localPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    localPaint.setAntiAlias(true);
    return localPaint;
  }

  public void addSlice(long paramLong, int paramInt)
  {
    this.mSlices.add(new Slice(paramLong, paramInt));
  }

  public void generatePath()
  {
    long l = 0L;
    Iterator localIterator1 = this.mSlices.iterator();
    while (localIterator1.hasNext())
    {
      Slice localSlice2 = (Slice)localIterator1.next();
      localSlice2.path.reset();
      localSlice2.pathSide.reset();
      localSlice2.pathOutline.reset();
      l += localSlice2.value;
    }
    this.mPathSide.reset();
    this.mPathSideOutline.reset();
    this.mPathOutline.reset();
    if (l == 0L)
    {
      invalidate();
      return;
    }
    int i = getWidth();
    int j = getHeight();
    RectF localRectF1 = new RectF(0.0F, 0.0F, i, j);
    RectF localRectF2 = new RectF();
    localRectF2.set(localRectF1);
    localRectF2.offset(-this.mSideWidth, 0.0F);
    this.mPathSide.addOval(localRectF2, Path.Direction.CW);
    this.mPathSideOutline.addOval(localRectF2, Path.Direction.CW);
    this.mPathOutline.addOval(localRectF1, Path.Direction.CW);
    int k = this.mOriginAngle;
    Iterator localIterator2 = this.mSlices.iterator();
    if (localIterator2.hasNext())
    {
      Slice localSlice1 = (Slice)localIterator2.next();
      int m = (int)(360L * localSlice1.value / l);
      int n = k + m;
      float f1 = k % 360;
      float f2 = n % 360;
      int i1;
      label285: int i2;
      label304: float f3;
      if ((f1 > 90.0F) && (f1 < 270.0F))
      {
        i1 = 1;
        if ((f2 <= 90.0F) || (f2 >= 270.0F))
          break label605;
        i2 = 1;
        localSlice1.path.moveTo(localRectF1.centerX(), localRectF1.centerY());
        localSlice1.path.arcTo(localRectF1, k, m);
        localSlice1.path.lineTo(localRectF1.centerX(), localRectF1.centerY());
        if ((i1 != 0) || (i2 != 0))
        {
          if (i1 == 0)
            break label611;
          f3 = k;
          label376: if (i2 == 0)
            break label618;
        }
      }
      label605: label611: label618: for (float f4 = n; ; f4 = 270.0F)
      {
        float f5 = f4 - f3;
        localSlice1.pathSide.moveTo(localRectF1.centerX(), localRectF1.centerY());
        localSlice1.pathSide.arcTo(localRectF1, f3, 0.0F);
        localSlice1.pathSide.rLineTo(-this.mSideWidth, 0.0F);
        localSlice1.pathSide.arcTo(localRectF2, f3, f5);
        localSlice1.pathSide.rLineTo(this.mSideWidth, 0.0F);
        localSlice1.pathSide.arcTo(localRectF1, f4, -f5);
        localSlice1.pathOutline.moveTo(localRectF1.centerX(), localRectF1.centerY());
        localSlice1.pathOutline.arcTo(localRectF1, k, 0.0F);
        if (i1 != 0)
          localSlice1.pathOutline.rLineTo(-this.mSideWidth, 0.0F);
        localSlice1.pathOutline.moveTo(localRectF1.centerX(), localRectF1.centerY());
        localSlice1.pathOutline.arcTo(localRectF1, k + m, 0.0F);
        if (i2 != 0)
          localSlice1.pathOutline.rLineTo(-this.mSideWidth, 0.0F);
        k += m;
        break;
        i1 = 0;
        break label285;
        i2 = 0;
        break label304;
        f3 = 450.0F;
        break label376;
      }
    }
    invalidate();
  }

  protected void onDraw(Canvas paramCanvas)
  {
    paramCanvas.concat(this.mMatrix);
    Iterator localIterator1 = this.mSlices.iterator();
    while (localIterator1.hasNext())
    {
      Slice localSlice2 = (Slice)localIterator1.next();
      paramCanvas.drawPath(localSlice2.pathSide, localSlice2.paint);
    }
    paramCanvas.drawPath(this.mPathSideOutline, this.mPaintOutline);
    Iterator localIterator2 = this.mSlices.iterator();
    while (localIterator2.hasNext())
    {
      Slice localSlice1 = (Slice)localIterator2.next();
      paramCanvas.drawPath(localSlice1.path, localSlice1.paint);
      paramCanvas.drawPath(localSlice1.pathOutline, this.mPaintOutline);
    }
    paramCanvas.drawPath(this.mPathOutline, this.mPaintOutline);
  }

  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    float f1 = getWidth() / 2;
    float f2 = getHeight() / 2;
    this.mMatrix.reset();
    this.mMatrix.postScale(0.665F, 0.95F, f1, f2);
    this.mMatrix.postRotate(-40.0F, f1, f2);
    generatePath();
  }

  public void removeAllSlices()
  {
    this.mSlices.clear();
  }

  public void setOriginAngle(int paramInt)
  {
    this.mOriginAngle = paramInt;
  }

  public class Slice
  {
    public Paint paint;
    public Path path = new Path();
    public Path pathOutline = new Path();
    public Path pathSide = new Path();
    public long value;

    public Slice(long arg2, int arg4)
    {
      this.value = ???;
      int i;
      this.paint = PieChartView.buildFillPaint(i, PieChartView.this.getResources());
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.widget.PieChartView
 * JD-Core Version:    0.6.2
 */