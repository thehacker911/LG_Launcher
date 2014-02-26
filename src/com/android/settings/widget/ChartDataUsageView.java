package com.android.settings.widget;

import android.content.Context;
import android.content.res.Resources;
import android.net.NetworkPolicy;
import android.net.NetworkStatsHistory;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.android.internal.util.Objects;
import java.util.Arrays;
import java.util.Calendar;

public class ChartDataUsageView extends ChartView
{
  private ChartNetworkSeriesView mDetailSeries;
  private ChartGridView mGrid;
  private Handler mHandler;
  private NetworkStatsHistory mHistory;
  private ChartSweepView.OnSweepListener mHorizListener = new ChartSweepView.OnSweepListener()
  {
    public void onSweep(ChartSweepView paramAnonymousChartSweepView, boolean paramAnonymousBoolean)
    {
      ChartDataUsageView.this.updatePrimaryRange();
      if ((paramAnonymousBoolean) && (ChartDataUsageView.this.mListener != null))
        ChartDataUsageView.this.mListener.onInspectRangeChanged();
    }

    public void requestEdit(ChartSweepView paramAnonymousChartSweepView)
    {
    }
  };
  private DataUsageChartListener mListener;
  private ChartNetworkSeriesView mSeries;
  private ChartSweepView mSweepLeft;
  private ChartSweepView mSweepLimit;
  private ChartSweepView mSweepRight;
  private ChartSweepView mSweepWarning;
  private ChartSweepView.OnSweepListener mVertListener = new ChartSweepView.OnSweepListener()
  {
    public void onSweep(ChartSweepView paramAnonymousChartSweepView, boolean paramAnonymousBoolean)
    {
      if (paramAnonymousBoolean)
      {
        ChartDataUsageView.this.clearUpdateAxisDelayed(paramAnonymousChartSweepView);
        ChartDataUsageView.this.updateEstimateVisible();
        if ((paramAnonymousChartSweepView == ChartDataUsageView.this.mSweepWarning) && (ChartDataUsageView.this.mListener != null))
          ChartDataUsageView.this.mListener.onWarningChanged();
        while ((paramAnonymousChartSweepView != ChartDataUsageView.this.mSweepLimit) || (ChartDataUsageView.this.mListener == null))
          return;
        ChartDataUsageView.this.mListener.onLimitChanged();
        return;
      }
      ChartDataUsageView.this.sendUpdateAxisDelayed(paramAnonymousChartSweepView, false);
    }

    public void requestEdit(ChartSweepView paramAnonymousChartSweepView)
    {
      if ((paramAnonymousChartSweepView == ChartDataUsageView.this.mSweepWarning) && (ChartDataUsageView.this.mListener != null))
        ChartDataUsageView.this.mListener.requestWarningEdit();
      while ((paramAnonymousChartSweepView != ChartDataUsageView.this.mSweepLimit) || (ChartDataUsageView.this.mListener == null))
        return;
      ChartDataUsageView.this.mListener.requestLimitEdit();
    }
  };
  private long mVertMax;

  public ChartDataUsageView(Context paramContext)
  {
    this(paramContext, null, 0);
  }

  public ChartDataUsageView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }

  public ChartDataUsageView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    init(new TimeAxis(), new InvertedChartAxis(new DataAxis()));
    this.mHandler = new Handler()
    {
      public void handleMessage(Message paramAnonymousMessage)
      {
        ChartSweepView localChartSweepView = (ChartSweepView)paramAnonymousMessage.obj;
        ChartDataUsageView.this.updateVertAxisBounds(localChartSweepView);
        ChartDataUsageView.this.updateEstimateVisible();
        ChartDataUsageView.this.sendUpdateAxisDelayed(localChartSweepView, true);
      }
    };
  }

  private void clearUpdateAxisDelayed(ChartSweepView paramChartSweepView)
  {
    this.mHandler.removeMessages(100, paramChartSweepView);
  }

  private long getHistoryEnd()
  {
    if (this.mHistory != null)
      return this.mHistory.getEnd();
    return -9223372036854775808L;
  }

  private long getHistoryStart()
  {
    if (this.mHistory != null)
      return this.mHistory.getStart();
    return 9223372036854775807L;
  }

  private static long roundUpToPowerOfTwo(long paramLong)
  {
    long l1 = paramLong - 1L;
    long l2 = l1 | l1 >>> 1;
    long l3 = l2 | l2 >>> 2;
    long l4 = l3 | l3 >>> 4;
    long l5 = l4 | l4 >>> 8;
    long l6 = l5 | l5 >>> 16;
    long l7 = 1L + (l6 | l6 >>> 32);
    if (l7 > 0L)
      return l7;
    return 9223372036854775807L;
  }

  private void sendUpdateAxisDelayed(ChartSweepView paramChartSweepView, boolean paramBoolean)
  {
    if ((paramBoolean) || (!this.mHandler.hasMessages(100, paramChartSweepView)))
      this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(100, paramChartSweepView), 250L);
  }

  private static void setText(SpannableStringBuilder paramSpannableStringBuilder, Object paramObject, CharSequence paramCharSequence, String paramString)
  {
    int i = paramSpannableStringBuilder.getSpanStart(paramObject);
    int j = paramSpannableStringBuilder.getSpanEnd(paramObject);
    if (i == -1)
    {
      i = TextUtils.indexOf(paramSpannableStringBuilder, paramString);
      j = i + paramString.length();
      paramSpannableStringBuilder.setSpan(paramObject, i, j, 18);
    }
    paramSpannableStringBuilder.replace(i, j, paramCharSequence);
  }

  private void updateEstimateVisible()
  {
    long l1 = this.mSeries.getMaxEstimate();
    long l2 = 9223372036854775807L;
    if (this.mSweepWarning.isEnabled())
    {
      l2 = this.mSweepWarning.getValue();
      if (l2 < 0L)
        l2 = 9223372036854775807L;
      if (l1 < 7L * l2 / 10L)
        break label88;
    }
    label88: for (boolean bool = true; ; bool = false)
    {
      this.mSeries.setEstimateVisible(bool);
      return;
      if (!this.mSweepLimit.isEnabled())
        break;
      l2 = this.mSweepLimit.getValue();
      break;
    }
  }

  private void updatePrimaryRange()
  {
    long l1 = this.mSweepLeft.getValue();
    long l2 = this.mSweepRight.getValue();
    if (this.mDetailSeries.getVisibility() == 0)
    {
      this.mDetailSeries.setPrimaryRange(l1, l2);
      this.mSeries.setPrimaryRange(0L, 0L);
      return;
    }
    this.mSeries.setPrimaryRange(l1, l2);
  }

  private void updateVertAxisBounds(ChartSweepView paramChartSweepView)
  {
    long l1 = this.mVertMax;
    long l2 = 0L;
    int i;
    if (paramChartSweepView != null)
    {
      i = paramChartSweepView.shouldAdjustAxis();
      if (i <= 0)
        break label213;
      l2 = 11L * l1 / 10L;
    }
    while (true)
    {
      long l3 = Math.max(this.mSweepWarning.getValue(), this.mSweepLimit.getValue());
      long l4 = Math.max(Math.max(12L * Math.max(Math.max(this.mSeries.getMaxVisible(), this.mDetailSeries.getMaxVisible()), l3) / 10L, 52428800L), l2);
      if (l4 != this.mVertMax)
      {
        this.mVertMax = l4;
        boolean bool = this.mVert.setBounds(0L, l4);
        this.mSweepWarning.setValidRange(0L, l4);
        this.mSweepLimit.setValidRange(0L, l4);
        if (bool)
        {
          this.mSeries.invalidatePath();
          this.mDetailSeries.invalidatePath();
        }
        this.mGrid.invalidate();
        if (paramChartSweepView != null)
          paramChartSweepView.updateValueFromPosition();
        if (this.mSweepLimit != paramChartSweepView)
          layoutSweep(this.mSweepLimit);
        if (this.mSweepWarning != paramChartSweepView)
          layoutSweep(this.mSweepWarning);
      }
      return;
      label213: if (i < 0)
        l2 = 9L * l1 / 10L;
      else
        l2 = l1;
    }
  }

  public void bindDetailNetworkStats(NetworkStatsHistory paramNetworkStatsHistory)
  {
    this.mDetailSeries.bindNetworkStats(paramNetworkStatsHistory);
    ChartNetworkSeriesView localChartNetworkSeriesView = this.mDetailSeries;
    if (paramNetworkStatsHistory != null);
    for (int i = 0; ; i = 8)
    {
      localChartNetworkSeriesView.setVisibility(i);
      if (this.mHistory != null)
        this.mDetailSeries.setEndTime(this.mHistory.getEnd());
      updateVertAxisBounds(null);
      updateEstimateVisible();
      updatePrimaryRange();
      requestLayout();
      return;
    }
  }

  public void bindNetworkPolicy(NetworkPolicy paramNetworkPolicy)
  {
    if (paramNetworkPolicy == null)
    {
      this.mSweepLimit.setVisibility(4);
      this.mSweepLimit.setValue(-1L);
      this.mSweepWarning.setVisibility(4);
      this.mSweepWarning.setValue(-1L);
      return;
    }
    if (paramNetworkPolicy.limitBytes != -1L)
    {
      this.mSweepLimit.setVisibility(0);
      this.mSweepLimit.setEnabled(true);
      this.mSweepLimit.setValue(paramNetworkPolicy.limitBytes);
      if (paramNetworkPolicy.warningBytes == -1L)
        break label152;
      this.mSweepWarning.setVisibility(0);
      this.mSweepWarning.setValue(paramNetworkPolicy.warningBytes);
    }
    while (true)
    {
      updateVertAxisBounds(null);
      requestLayout();
      invalidate();
      return;
      this.mSweepLimit.setVisibility(0);
      this.mSweepLimit.setEnabled(false);
      this.mSweepLimit.setValue(-1L);
      break;
      label152: this.mSweepWarning.setVisibility(4);
      this.mSweepWarning.setValue(-1L);
    }
  }

  public void bindNetworkStats(NetworkStatsHistory paramNetworkStatsHistory)
  {
    this.mSeries.bindNetworkStats(paramNetworkStatsHistory);
    this.mHistory = paramNetworkStatsHistory;
    updateVertAxisBounds(null);
    updateEstimateVisible();
    updatePrimaryRange();
    requestLayout();
  }

  public long getInspectEnd()
  {
    return this.mSweepRight.getValue();
  }

  public long getInspectStart()
  {
    return this.mSweepLeft.getValue();
  }

  public long getLimitBytes()
  {
    return this.mSweepLimit.getLabelValue();
  }

  public long getWarningBytes()
  {
    return this.mSweepWarning.getLabelValue();
  }

  protected void onFinishInflate()
  {
    super.onFinishInflate();
    this.mGrid = ((ChartGridView)findViewById(2131230792));
    this.mSeries = ((ChartNetworkSeriesView)findViewById(2131230793));
    this.mDetailSeries = ((ChartNetworkSeriesView)findViewById(2131230794));
    this.mDetailSeries.setVisibility(8);
    this.mSweepLeft = ((ChartSweepView)findViewById(2131230795));
    this.mSweepRight = ((ChartSweepView)findViewById(2131230796));
    this.mSweepLimit = ((ChartSweepView)findViewById(2131230798));
    this.mSweepWarning = ((ChartSweepView)findViewById(2131230797));
    this.mSweepLeft.setValidRangeDynamic(null, this.mSweepRight);
    this.mSweepRight.setValidRangeDynamic(this.mSweepLeft, null);
    this.mSweepWarning.setValidRangeDynamic(null, this.mSweepLimit);
    this.mSweepLimit.setValidRangeDynamic(this.mSweepWarning, null);
    ChartSweepView localChartSweepView1 = this.mSweepLeft;
    ChartSweepView[] arrayOfChartSweepView1 = new ChartSweepView[1];
    arrayOfChartSweepView1[0] = this.mSweepRight;
    localChartSweepView1.setNeighbors(arrayOfChartSweepView1);
    ChartSweepView localChartSweepView2 = this.mSweepRight;
    ChartSweepView[] arrayOfChartSweepView2 = new ChartSweepView[1];
    arrayOfChartSweepView2[0] = this.mSweepLeft;
    localChartSweepView2.setNeighbors(arrayOfChartSweepView2);
    ChartSweepView localChartSweepView3 = this.mSweepLimit;
    ChartSweepView[] arrayOfChartSweepView3 = new ChartSweepView[3];
    arrayOfChartSweepView3[0] = this.mSweepWarning;
    arrayOfChartSweepView3[1] = this.mSweepLeft;
    arrayOfChartSweepView3[2] = this.mSweepRight;
    localChartSweepView3.setNeighbors(arrayOfChartSweepView3);
    ChartSweepView localChartSweepView4 = this.mSweepWarning;
    ChartSweepView[] arrayOfChartSweepView4 = new ChartSweepView[3];
    arrayOfChartSweepView4[0] = this.mSweepLimit;
    arrayOfChartSweepView4[1] = this.mSweepLeft;
    arrayOfChartSweepView4[2] = this.mSweepRight;
    localChartSweepView4.setNeighbors(arrayOfChartSweepView4);
    this.mSweepLeft.addOnSweepListener(this.mHorizListener);
    this.mSweepRight.addOnSweepListener(this.mHorizListener);
    this.mSweepWarning.addOnSweepListener(this.mVertListener);
    this.mSweepLimit.addOnSweepListener(this.mVertListener);
    this.mSweepWarning.setDragInterval(5242880L);
    this.mSweepLimit.setDragInterval(5242880L);
    this.mSweepLeft.setClickable(false);
    this.mSweepLeft.setFocusable(false);
    this.mSweepRight.setClickable(false);
    this.mSweepRight.setFocusable(false);
    this.mGrid.init(this.mHoriz, this.mVert);
    this.mSeries.init(this.mHoriz, this.mVert);
    this.mDetailSeries.init(this.mHoriz, this.mVert);
    this.mSweepLeft.init(this.mHoriz);
    this.mSweepRight.init(this.mHoriz);
    this.mSweepWarning.init(this.mVert);
    this.mSweepLimit.init(this.mVert);
    setActivated(false);
  }

  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if (isActivated())
      return false;
    switch (paramMotionEvent.getAction())
    {
    default:
      return false;
    case 0:
      return true;
    case 1:
    }
    setActivated(true);
    return true;
  }

  public void setListener(DataUsageChartListener paramDataUsageChartListener)
  {
    this.mListener = paramDataUsageChartListener;
  }

  public void setVisibleRange(long paramLong1, long paramLong2)
  {
    boolean bool = this.mHoriz.setBounds(paramLong1, paramLong2);
    this.mGrid.setBounds(paramLong1, paramLong2);
    this.mSeries.setBounds(paramLong1, paramLong2);
    this.mDetailSeries.setBounds(paramLong1, paramLong2);
    long l1 = getHistoryStart();
    long l2 = getHistoryEnd();
    if (l1 == 9223372036854775807L)
      if (l2 != -9223372036854775808L)
        break label180;
    label180: for (long l3 = paramLong2; ; l3 = Math.min(paramLong2, l2))
    {
      this.mSweepLeft.setValidRange(paramLong1, paramLong2);
      this.mSweepRight.setValidRange(paramLong1, paramLong2);
      ((paramLong2 + paramLong1) / 2L);
      long l4 = l3;
      long l5 = Math.max(paramLong1, l4 - 604800000L);
      this.mSweepLeft.setValue(l5);
      this.mSweepRight.setValue(l4);
      requestLayout();
      if (bool)
      {
        this.mSeries.invalidatePath();
        this.mDetailSeries.invalidatePath();
      }
      updateVertAxisBounds(null);
      updateEstimateVisible();
      updatePrimaryRange();
      return;
      Math.max(paramLong1, l1);
      break;
    }
  }

  public static class DataAxis
    implements ChartAxis
  {
    private static final Object sSpanSize = new Object();
    private static final Object sSpanUnit = new Object();
    private long mMax;
    private long mMin;
    private float mSize;

    public long buildLabel(Resources paramResources, SpannableStringBuilder paramSpannableStringBuilder, long paramLong)
    {
      CharSequence localCharSequence;
      long l;
      double d1;
      String str;
      if (paramLong < 1048576000L)
      {
        localCharSequence = paramResources.getText(17039434);
        l = 1048576L;
        d1 = paramLong / l;
        if (d1 >= 10.0D)
          break label123;
        Object[] arrayOfObject2 = new Object[1];
        arrayOfObject2[0] = Double.valueOf(d1);
        str = String.format("%.1f", arrayOfObject2);
      }
      for (double d2 = l * Math.round(10.0D * d1) / 10L; ; d2 = l * Math.round(d1))
      {
        ChartDataUsageView.setText(paramSpannableStringBuilder, sSpanSize, str, "^1");
        ChartDataUsageView.setText(paramSpannableStringBuilder, sSpanUnit, localCharSequence, "^2");
        return ()d2;
        localCharSequence = paramResources.getText(17039435);
        l = 1073741824L;
        break;
        label123: Object[] arrayOfObject1 = new Object[1];
        arrayOfObject1[0] = Double.valueOf(d1);
        str = String.format("%.0f", arrayOfObject1);
      }
    }

    public float convertToPoint(long paramLong)
    {
      return this.mSize * (float)(paramLong - this.mMin) / (float)(this.mMax - this.mMin);
    }

    public long convertToValue(float paramFloat)
    {
      return ()((float)this.mMin + paramFloat * (float)(this.mMax - this.mMin) / this.mSize);
    }

    public float[] getTickPoints()
    {
      long l1 = this.mMax - this.mMin;
      long l2 = ChartDataUsageView.roundUpToPowerOfTwo(l1 / 16L);
      float[] arrayOfFloat = new float[(int)(l1 / l2)];
      long l3 = this.mMin;
      for (int i = 0; i < arrayOfFloat.length; i++)
      {
        arrayOfFloat[i] = convertToPoint(l3);
        l3 += l2;
      }
      return arrayOfFloat;
    }

    public int hashCode()
    {
      Object[] arrayOfObject = new Object[3];
      arrayOfObject[0] = Long.valueOf(this.mMin);
      arrayOfObject[1] = Long.valueOf(this.mMax);
      arrayOfObject[2] = Float.valueOf(this.mSize);
      return Objects.hashCode(arrayOfObject);
    }

    public boolean setBounds(long paramLong1, long paramLong2)
    {
      if ((this.mMin != paramLong1) || (this.mMax != paramLong2))
      {
        this.mMin = paramLong1;
        this.mMax = paramLong2;
        return true;
      }
      return false;
    }

    public boolean setSize(float paramFloat)
    {
      if (this.mSize != paramFloat)
      {
        this.mSize = paramFloat;
        return true;
      }
      return false;
    }

    public int shouldAdjustAxis(long paramLong)
    {
      float f = convertToPoint(paramLong);
      if (f < 0.1D * this.mSize)
        return -1;
      if (f > 0.85D * this.mSize)
        return 1;
      return 0;
    }
  }

  public static abstract interface DataUsageChartListener
  {
    public abstract void onInspectRangeChanged();

    public abstract void onLimitChanged();

    public abstract void onWarningChanged();

    public abstract void requestLimitEdit();

    public abstract void requestWarningEdit();
  }

  public static class TimeAxis
    implements ChartAxis
  {
    private static final int FIRST_DAY_OF_WEEK = -1 + Calendar.getInstance().getFirstDayOfWeek();
    private long mMax;
    private long mMin;
    private float mSize;

    public TimeAxis()
    {
      long l = System.currentTimeMillis();
      setBounds(l - 2592000000L, l);
    }

    public long buildLabel(Resources paramResources, SpannableStringBuilder paramSpannableStringBuilder, long paramLong)
    {
      paramSpannableStringBuilder.replace(0, paramSpannableStringBuilder.length(), Long.toString(paramLong));
      return paramLong;
    }

    public float convertToPoint(long paramLong)
    {
      return this.mSize * (float)(paramLong - this.mMin) / (float)(this.mMax - this.mMin);
    }

    public long convertToValue(float paramFloat)
    {
      return ()((float)this.mMin + paramFloat * (float)(this.mMax - this.mMin) / this.mSize);
    }

    public float[] getTickPoints()
    {
      float[] arrayOfFloat = new float[32];
      int i = 0;
      Time localTime = new Time();
      localTime.set(this.mMax);
      localTime.monthDay -= localTime.weekDay - FIRST_DAY_OF_WEEK;
      localTime.second = 0;
      localTime.minute = 0;
      localTime.hour = 0;
      localTime.normalize(true);
      for (long l = localTime.toMillis(true); l > this.mMin; l = localTime.toMillis(true))
      {
        if (l <= this.mMax)
        {
          int j = i + 1;
          arrayOfFloat[i] = convertToPoint(l);
          i = j;
        }
        localTime.monthDay = (-7 + localTime.monthDay);
        localTime.normalize(true);
      }
      return Arrays.copyOf(arrayOfFloat, i);
    }

    public int hashCode()
    {
      Object[] arrayOfObject = new Object[3];
      arrayOfObject[0] = Long.valueOf(this.mMin);
      arrayOfObject[1] = Long.valueOf(this.mMax);
      arrayOfObject[2] = Float.valueOf(this.mSize);
      return Objects.hashCode(arrayOfObject);
    }

    public boolean setBounds(long paramLong1, long paramLong2)
    {
      if ((this.mMin != paramLong1) || (this.mMax != paramLong2))
      {
        this.mMin = paramLong1;
        this.mMax = paramLong2;
        return true;
      }
      return false;
    }

    public boolean setSize(float paramFloat)
    {
      if (this.mSize != paramFloat)
      {
        this.mSize = paramFloat;
        return true;
      }
      return false;
    }

    public int shouldAdjustAxis(long paramLong)
    {
      return 0;
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.widget.ChartDataUsageView
 * JD-Core Version:    0.6.2
 */