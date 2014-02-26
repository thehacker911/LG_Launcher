package com.android.settings.applications;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class LinearColorBar extends LinearLayout
{
  final Paint mColorGradientPaint = new Paint();
  final Path mColorPath = new Path();
  private int mColoredRegions = 7;
  final Paint mEdgeGradientPaint = new Paint();
  final Path mEdgePath = new Path();
  private float mGreenRatio;
  int mLastInterestingLeft;
  int mLastInterestingRight;
  int mLastLeftDiv;
  int mLastRegion;
  int mLastRightDiv;
  private int mLeftColor = -16737844;
  int mLineWidth;
  private int mMiddleColor = -16737844;
  private OnRegionTappedListener mOnRegionTappedListener;
  final Paint mPaint = new Paint();
  final Rect mRect = new Rect();
  private float mRedRatio;
  private int mRightColor = -7829368;
  private boolean mShowIndicator = true;
  private boolean mShowingGreen;
  private float mYellowRatio;

  public LinearColorBar(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    setWillNotDraw(false);
    this.mPaint.setStyle(Paint.Style.FILL);
    this.mColorGradientPaint.setStyle(Paint.Style.FILL);
    this.mColorGradientPaint.setAntiAlias(true);
    this.mEdgeGradientPaint.setStyle(Paint.Style.STROKE);
    if (getResources().getDisplayMetrics().densityDpi >= 240);
    for (int i = 2; ; i = 1)
    {
      this.mLineWidth = i;
      this.mEdgeGradientPaint.setStrokeWidth(this.mLineWidth);
      this.mEdgeGradientPaint.setAntiAlias(true);
      return;
    }
  }

  private int pickColor(int paramInt1, int paramInt2)
  {
    if ((isPressed()) && ((paramInt2 & this.mLastRegion) != 0))
      paramInt1 = -1;
    while ((paramInt2 & this.mColoredRegions) != 0)
      return paramInt1;
    return -11184811;
  }

  private void updateIndicator()
  {
    int i = getPaddingTop() - getPaddingBottom();
    if (i < 0)
      i = 0;
    this.mRect.top = i;
    this.mRect.bottom = getHeight();
    if (!this.mShowIndicator)
      return;
    if (this.mShowingGreen)
      this.mColorGradientPaint.setShader(new LinearGradient(0.0F, 0.0F, 0.0F, i - 2, 0xFFFFFF & this.mRightColor, this.mRightColor, Shader.TileMode.CLAMP));
    while (true)
    {
      this.mEdgeGradientPaint.setShader(new LinearGradient(0.0F, 0.0F, 0.0F, i / 2, 10526880, -6250336, Shader.TileMode.CLAMP));
      return;
      this.mColorGradientPaint.setShader(new LinearGradient(0.0F, 0.0F, 0.0F, i - 2, 0xFFFFFF & this.mMiddleColor, this.mMiddleColor, Shader.TileMode.CLAMP));
    }
  }

  protected void dispatchSetPressed(boolean paramBoolean)
  {
    invalidate();
  }

  protected void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    int i = getWidth();
    int j = 0 + (int)(i * this.mRedRatio);
    int k = j + (int)(i * this.mYellowRatio);
    int m = k + (int)(i * this.mGreenRatio);
    int n;
    if (this.mShowingGreen)
      n = k;
    for (int i1 = m; ; i1 = k)
    {
      if ((this.mLastInterestingLeft != n) || (this.mLastInterestingRight != i1))
      {
        this.mColorPath.reset();
        this.mEdgePath.reset();
        if ((this.mShowIndicator) && (n < i1))
        {
          int i4 = this.mRect.top;
          this.mColorPath.moveTo(n, this.mRect.top);
          this.mColorPath.cubicTo(n, 0.0F, -2.0F, i4, -2.0F, 0.0F);
          this.mColorPath.lineTo(-1 + (i + 2), 0.0F);
          this.mColorPath.cubicTo(-1 + (i + 2), i4, i1, 0.0F, i1, this.mRect.top);
          this.mColorPath.close();
          float f = 0.5F + this.mLineWidth;
          this.mEdgePath.moveTo(-2.0F + f, 0.0F);
          this.mEdgePath.cubicTo(-2.0F + f, i4, f + n, 0.0F, f + n, this.mRect.top);
          this.mEdgePath.moveTo(-1 + (i + 2) - f, 0.0F);
          this.mEdgePath.cubicTo(-1 + (i + 2) - f, i4, i1 - f, 0.0F, i1 - f, this.mRect.top);
        }
        this.mLastInterestingLeft = n;
        this.mLastInterestingRight = i1;
      }
      if (!this.mEdgePath.isEmpty())
      {
        paramCanvas.drawPath(this.mEdgePath, this.mEdgeGradientPaint);
        paramCanvas.drawPath(this.mColorPath, this.mColorGradientPaint);
      }
      int i2 = 0;
      if (j < 0)
      {
        this.mRect.left = 0;
        this.mRect.right = j;
        this.mPaint.setColor(pickColor(this.mLeftColor, 1));
        paramCanvas.drawRect(this.mRect, this.mPaint);
        i -= j - 0;
        i2 = j;
      }
      this.mLastLeftDiv = j;
      this.mLastRightDiv = k;
      if (i2 < k)
      {
        this.mRect.left = i2;
        this.mRect.right = k;
        this.mPaint.setColor(pickColor(this.mMiddleColor, 2));
        paramCanvas.drawRect(this.mRect, this.mPaint);
        i -= k - i2;
        i2 = k;
      }
      int i3 = i2 + i;
      if (i2 < i3)
      {
        this.mRect.left = i2;
        this.mRect.right = i3;
        this.mPaint.setColor(pickColor(this.mRightColor, 4));
        paramCanvas.drawRect(this.mRect, this.mPaint);
      }
      return;
      n = j;
    }
  }

  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    updateIndicator();
  }

  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if (this.mOnRegionTappedListener != null);
    switch (paramMotionEvent.getAction())
    {
    default:
      return super.onTouchEvent(paramMotionEvent);
    case 0:
    }
    int i = (int)paramMotionEvent.getX();
    if (i < this.mLastLeftDiv)
      this.mLastRegion = 1;
    while (true)
    {
      invalidate();
      break;
      if (i < this.mLastRightDiv)
        this.mLastRegion = 2;
      else
        this.mLastRegion = 4;
    }
  }

  public boolean performClick()
  {
    if ((this.mOnRegionTappedListener != null) && (this.mLastRegion != 0))
    {
      this.mOnRegionTappedListener.onRegionTapped(this.mLastRegion);
      this.mLastRegion = 0;
    }
    return super.performClick();
  }

  public void setColoredRegions(int paramInt)
  {
    this.mColoredRegions = paramInt;
    invalidate();
  }

  public void setColors(int paramInt1, int paramInt2, int paramInt3)
  {
    this.mLeftColor = paramInt1;
    this.mMiddleColor = paramInt2;
    this.mRightColor = paramInt3;
    updateIndicator();
    invalidate();
  }

  public void setOnRegionTappedListener(OnRegionTappedListener paramOnRegionTappedListener)
  {
    if (paramOnRegionTappedListener != this.mOnRegionTappedListener)
    {
      this.mOnRegionTappedListener = paramOnRegionTappedListener;
      if (paramOnRegionTappedListener == null)
        break label25;
    }
    label25: for (boolean bool = true; ; bool = false)
    {
      setClickable(bool);
      return;
    }
  }

  public void setRatios(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    this.mRedRatio = paramFloat1;
    this.mYellowRatio = paramFloat2;
    this.mGreenRatio = paramFloat3;
    invalidate();
  }

  public void setShowIndicator(boolean paramBoolean)
  {
    this.mShowIndicator = paramBoolean;
    updateIndicator();
    invalidate();
  }

  public void setShowingGreen(boolean paramBoolean)
  {
    if (this.mShowingGreen != paramBoolean)
    {
      this.mShowingGreen = paramBoolean;
      updateIndicator();
      invalidate();
    }
  }

  public static abstract interface OnRegionTappedListener
  {
    public abstract void onRegionTapped(int paramInt);
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.applications.LinearColorBar
 * JD-Core Version:    0.6.2
 */