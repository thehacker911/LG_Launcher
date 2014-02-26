package com.android.settings.widget;

import android.content.Context;
import android.content.res.CompatibilityInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.DynamicLayout;
import android.text.Layout;
import android.text.Layout.Alignment;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.MathUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewParent;
import com.android.internal.util.Preconditions;
import com.android.settings.R.styleable;

public class ChartSweepView extends View
{
  private ChartAxis mAxis;
  private View.OnClickListener mClickListener = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      ChartSweepView.this.dispatchRequestEdit();
    }
  };
  private Rect mContentOffset = new Rect();
  private long mDragInterval = 1L;
  private int mFollowAxis;
  private int mLabelColor;
  private DynamicLayout mLabelLayout;
  private int mLabelMinSize;
  private float mLabelOffset;
  private float mLabelSize;
  private SpannableStringBuilder mLabelTemplate;
  private int mLabelTemplateRes;
  private long mLabelValue;
  private OnSweepListener mListener;
  private Rect mMargins = new Rect();
  private float mNeighborMargin;
  private ChartSweepView[] mNeighbors = new ChartSweepView[0];
  private Paint mOutlinePaint = new Paint();
  private Drawable mSweep;
  private Point mSweepOffset = new Point();
  private Rect mSweepPadding = new Rect();
  private int mTouchMode = 0;
  private MotionEvent mTracking;
  private float mTrackingStart;
  private long mValidAfter;
  private ChartSweepView mValidAfterDynamic;
  private long mValidBefore;
  private ChartSweepView mValidBeforeDynamic;
  private long mValue;

  public ChartSweepView(Context paramContext)
  {
    this(paramContext, null);
  }

  public ChartSweepView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }

  public ChartSweepView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.ChartSweepView, paramInt, 0);
    setSweepDrawable(localTypedArray.getDrawable(0));
    setFollowAxis(localTypedArray.getInt(1, -1));
    setNeighborMargin(localTypedArray.getDimensionPixelSize(2, 0));
    setLabelMinSize(localTypedArray.getDimensionPixelSize(3, 0));
    setLabelTemplate(localTypedArray.getResourceId(4, 0));
    setLabelColor(localTypedArray.getColor(5, -16776961));
    setBackgroundResource(2130837552);
    this.mOutlinePaint.setColor(-65536);
    this.mOutlinePaint.setStrokeWidth(1.0F);
    this.mOutlinePaint.setStyle(Paint.Style.STROKE);
    localTypedArray.recycle();
    setClickable(true);
    setFocusable(true);
    setOnClickListener(this.mClickListener);
    setWillNotDraw(false);
  }

  private Rect buildClampRect(Rect paramRect, long paramLong1, long paramLong2, float paramFloat)
  {
    if ((this.mAxis instanceof InvertedChartAxis))
    {
      long l = paramLong2;
      paramLong2 = paramLong1;
      paramLong1 = l;
    }
    int i;
    int j;
    label60: float f1;
    float f2;
    Rect localRect;
    if ((paramLong1 != -9223372036854775808L) && (paramLong1 != 9223372036854775807L))
    {
      i = 1;
      if ((paramLong2 == -9223372036854775808L) || (paramLong2 == 9223372036854775807L))
        break label157;
      j = 1;
      f1 = paramFloat + this.mAxis.convertToPoint(paramLong1);
      f2 = this.mAxis.convertToPoint(paramLong2) - paramFloat;
      localRect = new Rect(paramRect);
      if (this.mFollowAxis != 1)
        break label163;
      if (j != 0)
        localRect.bottom = (localRect.top + (int)f2);
      if (i != 0)
        localRect.top = ((int)(f1 + localRect.top));
    }
    label157: label163: 
    do
    {
      return localRect;
      i = 0;
      break;
      j = 0;
      break label60;
      if (j != 0)
        localRect.right = (localRect.left + (int)f2);
    }
    while (i == 0);
    localRect.left = ((int)(f1 + localRect.left));
    return localRect;
  }

  private Rect computeClampRect(Rect paramRect)
  {
    Rect localRect = buildClampRect(paramRect, this.mValidAfter, this.mValidBefore, 0.0F);
    if (!localRect.intersect(buildClampRect(paramRect, getValidAfterDynamic(), getValidBeforeDynamic(), this.mNeighborMargin)))
      localRect.setEmpty();
    return localRect;
  }

  private void dispatchOnSweep(boolean paramBoolean)
  {
    if (this.mListener != null)
      this.mListener.onSweep(this, paramBoolean);
  }

  private void dispatchRequestEdit()
  {
    if (this.mListener != null)
      this.mListener.requestEdit(this);
  }

  public static float getLabelBottom(ChartSweepView paramChartSweepView)
  {
    return getLabelTop(paramChartSweepView) + paramChartSweepView.mLabelLayout.getHeight();
  }

  public static float getLabelTop(ChartSweepView paramChartSweepView)
  {
    return paramChartSweepView.getY() + paramChartSweepView.mContentOffset.top;
  }

  public static float getLabelWidth(ChartSweepView paramChartSweepView)
  {
    return Layout.getDesiredWidth(paramChartSweepView.mLabelLayout.getText(), paramChartSweepView.mLabelLayout.getPaint());
  }

  private Rect getParentContentRect()
  {
    View localView = (View)getParent();
    return new Rect(localView.getPaddingLeft(), localView.getPaddingTop(), localView.getWidth() - localView.getPaddingRight(), localView.getHeight() - localView.getPaddingBottom());
  }

  private float getTargetInset()
  {
    if (this.mFollowAxis == 1)
    {
      float f2 = this.mSweep.getIntrinsicHeight() - this.mSweepPadding.top - this.mSweepPadding.bottom;
      return this.mSweepPadding.top + f2 / 2.0F + this.mSweepOffset.y;
    }
    float f1 = this.mSweep.getIntrinsicWidth() - this.mSweepPadding.left - this.mSweepPadding.right;
    return this.mSweepPadding.left + f1 / 2.0F + this.mSweepOffset.x;
  }

  private float getTouchDistanceFromTarget(MotionEvent paramMotionEvent)
  {
    if (this.mFollowAxis == 0)
      return Math.abs(paramMotionEvent.getX() - (getX() + getTargetInset()));
    return Math.abs(paramMotionEvent.getY() - (getY() + getTargetInset()));
  }

  private long getValidAfterDynamic()
  {
    ChartSweepView localChartSweepView = this.mValidAfterDynamic;
    if ((localChartSweepView != null) && (localChartSweepView.isEnabled()))
      return localChartSweepView.getValue();
    return -9223372036854775808L;
  }

  private long getValidBeforeDynamic()
  {
    ChartSweepView localChartSweepView = this.mValidBeforeDynamic;
    if ((localChartSweepView != null) && (localChartSweepView.isEnabled()))
      return localChartSweepView.getValue();
    return 9223372036854775807L;
  }

  private void invalidateLabel()
  {
    if ((this.mLabelTemplate != null) && (this.mAxis != null))
    {
      this.mLabelValue = this.mAxis.buildLabel(getResources(), this.mLabelTemplate, this.mValue);
      setContentDescription(this.mLabelTemplate);
      invalidateLabelOffset();
      invalidate();
      return;
    }
    this.mLabelValue = this.mValue;
  }

  private void invalidateLabelTemplate()
  {
    if (this.mLabelTemplateRes != 0)
    {
      CharSequence localCharSequence = getResources().getText(this.mLabelTemplateRes);
      TextPaint localTextPaint = new TextPaint(1);
      localTextPaint.density = getResources().getDisplayMetrics().density;
      localTextPaint.setCompatibilityScaling(getResources().getCompatibilityInfo().applicationScale);
      localTextPaint.setColor(this.mLabelColor);
      localTextPaint.setShadowLayer(4.0F * localTextPaint.density, 0.0F, 0.0F, -16777216);
      this.mLabelTemplate = new SpannableStringBuilder(localCharSequence);
      this.mLabelLayout = new DynamicLayout(this.mLabelTemplate, localTextPaint, 1024, Layout.Alignment.ALIGN_RIGHT, 1.0F, 0.0F, false);
      invalidateLabel();
    }
    while (true)
    {
      invalidate();
      requestLayout();
      return;
      this.mLabelTemplate = null;
      this.mLabelLayout = null;
    }
  }

  public void addOnLayoutChangeListener(View.OnLayoutChangeListener paramOnLayoutChangeListener)
  {
  }

  public void addOnSweepListener(OnSweepListener paramOnSweepListener)
  {
    this.mListener = paramOnSweepListener;
  }

  protected void drawableStateChanged()
  {
    super.drawableStateChanged();
    if (this.mSweep.isStateful())
      this.mSweep.setState(getDrawableState());
  }

  public int getFollowAxis()
  {
    return this.mFollowAxis;
  }

  public long getLabelValue()
  {
    return this.mLabelValue;
  }

  public Rect getMargins()
  {
    return this.mMargins;
  }

  public float getPoint()
  {
    if (isEnabled())
      return this.mAxis.convertToPoint(this.mValue);
    return 0.0F;
  }

  public long getValue()
  {
    return this.mValue;
  }

  void init(ChartAxis paramChartAxis)
  {
    this.mAxis = ((ChartAxis)Preconditions.checkNotNull(paramChartAxis, "missing axis"));
  }

  public void invalidateLabelOffset()
  {
    int i = this.mFollowAxis;
    float f1 = 0.0F;
    if (i == 1)
    {
      if (this.mValidAfterDynamic == null)
        break label132;
      this.mLabelSize = Math.max(getLabelWidth(this), getLabelWidth(this.mValidAfterDynamic));
      float f3 = getLabelTop(this.mValidAfterDynamic) - getLabelBottom(this);
      boolean bool2 = f3 < 0.0F;
      f1 = 0.0F;
      if (bool2)
        f1 = f3 / 2.0F;
    }
    while (true)
    {
      this.mLabelSize = Math.max(this.mLabelSize, this.mLabelMinSize);
      if (f1 != this.mLabelOffset)
      {
        this.mLabelOffset = f1;
        invalidate();
        if (this.mValidAfterDynamic != null)
          this.mValidAfterDynamic.invalidateLabelOffset();
        if (this.mValidBeforeDynamic != null)
          this.mValidBeforeDynamic.invalidateLabelOffset();
      }
      return;
      label132: if (this.mValidBeforeDynamic != null)
      {
        this.mLabelSize = Math.max(getLabelWidth(this), getLabelWidth(this.mValidBeforeDynamic));
        float f2 = getLabelTop(this) - getLabelBottom(this.mValidBeforeDynamic);
        boolean bool1 = f2 < 0.0F;
        f1 = 0.0F;
        if (bool1)
          f1 = -f2 / 2.0F;
      }
      else
      {
        this.mLabelSize = getLabelWidth(this);
        f1 = 0.0F;
      }
    }
  }

  public boolean isTouchCloserTo(MotionEvent paramMotionEvent, ChartSweepView paramChartSweepView)
  {
    float f = getTouchDistanceFromTarget(paramMotionEvent);
    return paramChartSweepView.getTouchDistanceFromTarget(paramMotionEvent) < f;
  }

  public void jumpDrawablesToCurrentState()
  {
    super.jumpDrawablesToCurrentState();
    if (this.mSweep != null)
      this.mSweep.jumpToCurrentState();
  }

  protected void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    int i = getWidth();
    int j = getHeight();
    int k;
    if ((isEnabled()) && (this.mLabelLayout != null))
    {
      int m = paramCanvas.save();
      paramCanvas.translate(this.mLabelSize - 1024.0F + this.mContentOffset.left, this.mContentOffset.top + this.mLabelOffset);
      this.mLabelLayout.draw(paramCanvas);
      paramCanvas.restoreToCount(m);
      k = (int)this.mLabelSize;
      if (this.mFollowAxis != 1)
        break label153;
      this.mSweep.setBounds(k, this.mSweepOffset.y, i + this.mContentOffset.right, this.mSweepOffset.y + this.mSweep.getIntrinsicHeight());
    }
    while (true)
    {
      this.mSweep.draw(paramCanvas);
      return;
      k = 0;
      break;
      label153: this.mSweep.setBounds(this.mSweepOffset.x, k, this.mSweepOffset.x + this.mSweep.getIntrinsicWidth(), j + this.mContentOffset.bottom);
    }
  }

  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    invalidateLabelOffset();
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    label174: int j;
    int k;
    if ((isEnabled()) && (this.mLabelLayout != null))
    {
      int i4 = this.mSweep.getIntrinsicHeight();
      int i5 = this.mLabelLayout.getHeight();
      this.mSweepOffset.x = 0;
      this.mSweepOffset.y = 0;
      this.mSweepOffset.y = ((int)(i5 / 2 - getTargetInset()));
      setMeasuredDimension(this.mSweep.getIntrinsicWidth(), Math.max(i4, i5));
      if (this.mFollowAxis != 1)
        break label363;
      int i3 = this.mSweep.getIntrinsicHeight() - this.mSweepPadding.top - this.mSweepPadding.bottom;
      this.mMargins.top = (-(this.mSweepPadding.top + i3 / 2));
      this.mMargins.bottom = 0;
      this.mMargins.left = (-this.mSweepPadding.left);
      this.mMargins.right = this.mSweepPadding.right;
      this.mContentOffset.set(0, 0, 0, 0);
      j = getMeasuredWidth();
      k = getMeasuredHeight();
      if (this.mFollowAxis != 0)
        break label446;
      int i1 = j * 3;
      setMeasuredDimension(i1, k);
      this.mContentOffset.left = ((i1 - j) / 2);
      int i2 = 2 * this.mSweepPadding.bottom;
      Rect localRect3 = this.mContentOffset;
      localRect3.bottom -= i2;
      Rect localRect4 = this.mMargins;
      localRect4.bottom = (i2 + localRect4.bottom);
    }
    while (true)
    {
      this.mSweepOffset.offset(this.mContentOffset.left, this.mContentOffset.top);
      this.mMargins.offset(-this.mSweepOffset.x, -this.mSweepOffset.y);
      return;
      this.mSweepOffset.x = 0;
      this.mSweepOffset.y = 0;
      setMeasuredDimension(this.mSweep.getIntrinsicWidth(), this.mSweep.getIntrinsicHeight());
      break;
      label363: int i = this.mSweep.getIntrinsicWidth() - this.mSweepPadding.left - this.mSweepPadding.right;
      this.mMargins.left = (-(this.mSweepPadding.left + i / 2));
      this.mMargins.right = 0;
      this.mMargins.top = (-this.mSweepPadding.top);
      this.mMargins.bottom = this.mSweepPadding.bottom;
      break label174;
      label446: int m = k * 2;
      setMeasuredDimension(j, m);
      this.mContentOffset.offset(0, (m - k) / 2);
      int n = 2 * this.mSweepPadding.right;
      Rect localRect1 = this.mContentOffset;
      localRect1.right -= n;
      Rect localRect2 = this.mMargins;
      localRect2.right = (n + localRect2.right);
    }
  }

  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if (!isEnabled())
      return false;
    View localView = (View)getParent();
    switch (paramMotionEvent.getAction())
    {
    default:
      return false;
    case 0:
      int i;
      int j;
      MotionEvent localMotionEvent;
      ChartSweepView[] arrayOfChartSweepView;
      int k;
      if (this.mFollowAxis == 1)
        if (paramMotionEvent.getX() > getWidth() - 8 * this.mSweepPadding.right)
        {
          i = 1;
          if (this.mLabelLayout == null)
            break label181;
          if (paramMotionEvent.getX() >= this.mLabelLayout.getWidth())
            break label175;
          j = 1;
          localMotionEvent = paramMotionEvent.copy();
          localMotionEvent.offsetLocation(getLeft(), getTop());
          arrayOfChartSweepView = this.mNeighbors;
          k = arrayOfChartSweepView.length;
        }
      for (int m = 0; ; m++)
      {
        if (m >= k)
          break label267;
        if (isTouchCloserTo(localMotionEvent, arrayOfChartSweepView[m]))
        {
          return false;
          i = 0;
          break;
          j = 0;
          break label111;
          j = 0;
          break label111;
          if (paramMotionEvent.getY() > getHeight() - 8 * this.mSweepPadding.bottom)
          {
            i = 1;
            if (this.mLabelLayout == null)
              break label255;
            if (paramMotionEvent.getY() >= this.mLabelLayout.getHeight())
              break label249;
            j = 1;
          }
          while (true)
          {
            break;
            i = 0;
            break label214;
            j = 0;
            continue;
            j = 0;
          }
        }
      }
      if (i != 0)
      {
        if (this.mFollowAxis == 1);
        for (this.mTrackingStart = (getTop() - this.mMargins.top); ; this.mTrackingStart = (getLeft() - this.mMargins.left))
        {
          this.mTracking = paramMotionEvent.copy();
          this.mTouchMode = 1;
          if (!localView.isActivated())
            localView.setActivated(true);
          return true;
        }
      }
      if (j != 0)
      {
        this.mTouchMode = 2;
        return true;
      }
      this.mTouchMode = 0;
      return false;
    case 2:
      label111: label249: label255: if (this.mTouchMode == 2)
        return true;
      label175: label181: label214: getParent().requestDisallowInterceptTouchEvent(true);
      label267: Rect localRect1 = getParentContentRect();
      Rect localRect2 = computeClampRect(localRect1);
      if (localRect2.isEmpty())
        return true;
      float f4;
      if (this.mFollowAxis == 1)
      {
        float f3 = getTop() - this.mMargins.top;
        f4 = MathUtils.constrain(this.mTrackingStart + (paramMotionEvent.getRawY() - this.mTracking.getRawY()), localRect2.top, localRect2.bottom);
        setTranslationY(f4 - f3);
      }
      float f2;
      for (long l = this.mAxis.convertToValue(f4 - localRect1.top); ; l = this.mAxis.convertToValue(f2 - localRect1.left))
      {
        setValue(l - l % this.mDragInterval);
        dispatchOnSweep(false);
        return true;
        float f1 = getLeft() - this.mMargins.left;
        f2 = MathUtils.constrain(this.mTrackingStart + (paramMotionEvent.getRawX() - this.mTracking.getRawX()), localRect2.left, localRect2.right);
        setTranslationX(f2 - f1);
      }
    case 1:
    }
    if (this.mTouchMode == 2)
      performClick();
    while (true)
    {
      this.mTouchMode = 0;
      return true;
      if (this.mTouchMode == 1)
      {
        this.mTrackingStart = 0.0F;
        this.mTracking = null;
        this.mValue = this.mLabelValue;
        dispatchOnSweep(true);
        setTranslationX(0.0F);
        setTranslationY(0.0F);
        requestLayout();
      }
    }
  }

  public void removeOnLayoutChangeListener(View.OnLayoutChangeListener paramOnLayoutChangeListener)
  {
  }

  public void setDragInterval(long paramLong)
  {
    this.mDragInterval = paramLong;
  }

  public void setEnabled(boolean paramBoolean)
  {
    super.setEnabled(paramBoolean);
    setFocusable(paramBoolean);
    requestLayout();
  }

  public void setFollowAxis(int paramInt)
  {
    this.mFollowAxis = paramInt;
  }

  public void setLabelColor(int paramInt)
  {
    this.mLabelColor = paramInt;
    invalidateLabelTemplate();
  }

  public void setLabelMinSize(int paramInt)
  {
    this.mLabelMinSize = paramInt;
    invalidateLabelTemplate();
  }

  public void setLabelTemplate(int paramInt)
  {
    this.mLabelTemplateRes = paramInt;
    invalidateLabelTemplate();
  }

  public void setNeighborMargin(float paramFloat)
  {
    this.mNeighborMargin = paramFloat;
  }

  public void setNeighbors(ChartSweepView[] paramArrayOfChartSweepView)
  {
    this.mNeighbors = paramArrayOfChartSweepView;
  }

  public void setSweepDrawable(Drawable paramDrawable)
  {
    if (this.mSweep != null)
    {
      this.mSweep.setCallback(null);
      unscheduleDrawable(this.mSweep);
    }
    boolean bool;
    if (paramDrawable != null)
    {
      paramDrawable.setCallback(this);
      if (paramDrawable.isStateful())
        paramDrawable.setState(getDrawableState());
      if (getVisibility() == 0)
      {
        bool = true;
        paramDrawable.setVisible(bool, false);
        this.mSweep = paramDrawable;
        paramDrawable.getPadding(this.mSweepPadding);
      }
    }
    while (true)
    {
      invalidate();
      return;
      bool = false;
      break;
      this.mSweep = null;
    }
  }

  public void setValidRange(long paramLong1, long paramLong2)
  {
    this.mValidAfter = paramLong1;
    this.mValidBefore = paramLong2;
  }

  public void setValidRangeDynamic(ChartSweepView paramChartSweepView1, ChartSweepView paramChartSweepView2)
  {
    this.mValidAfterDynamic = paramChartSweepView1;
    this.mValidBeforeDynamic = paramChartSweepView2;
  }

  public void setValue(long paramLong)
  {
    this.mValue = paramLong;
    invalidateLabel();
  }

  public void setVisibility(int paramInt)
  {
    super.setVisibility(paramInt);
    Drawable localDrawable;
    if (this.mSweep != null)
    {
      localDrawable = this.mSweep;
      if (paramInt != 0)
        break label31;
    }
    label31: for (boolean bool = true; ; bool = false)
    {
      localDrawable.setVisible(bool, false);
      return;
    }
  }

  public int shouldAdjustAxis()
  {
    return this.mAxis.shouldAdjustAxis(getValue());
  }

  public void updateValueFromPosition()
  {
    Rect localRect = getParentContentRect();
    if (this.mFollowAxis == 1)
    {
      float f2 = getY() - this.mMargins.top - localRect.top;
      setValue(this.mAxis.convertToValue(f2));
      return;
    }
    float f1 = getX() - this.mMargins.left - localRect.left;
    setValue(this.mAxis.convertToValue(f1));
  }

  protected boolean verifyDrawable(Drawable paramDrawable)
  {
    return (paramDrawable == this.mSweep) || (super.verifyDrawable(paramDrawable));
  }

  public static abstract interface OnSweepListener
  {
    public abstract void onSweep(ChartSweepView paramChartSweepView, boolean paramBoolean);

    public abstract void requestEdit(ChartSweepView paramChartSweepView);
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.widget.ChartSweepView
 * JD-Core Version:    0.6.2
 */