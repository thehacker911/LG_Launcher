package com.android.settings.fuelgauge;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.CompatibilityInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Typeface;
import android.os.BatteryStats;
import android.os.BatteryStats.HistoryItem;
import android.os.SystemClock;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

public class BatteryHistoryChart extends View
{
  final Path mBatCriticalPath = new Path();
  final Path mBatGoodPath = new Path();
  int mBatHigh;
  final Path mBatLevelPath = new Path();
  int mBatLow;
  final Path mBatWarnPath = new Path();
  final Paint mBatteryBackgroundPaint = new Paint(1);
  final Paint mBatteryCriticalPaint = new Paint(1);
  final Paint mBatteryGoodPaint = new Paint(1);
  final Paint mBatteryWarnPaint = new Paint(1);
  String mChargingLabel;
  int mChargingOffset;
  final Paint mChargingPaint = new Paint();
  final Path mChargingPath = new Path();
  String mDurationString;
  int mDurationStringWidth;
  String mGpsOnLabel;
  int mGpsOnOffset;
  final Paint mGpsOnPaint = new Paint();
  final Path mGpsOnPath = new Path();
  boolean mHaveGps;
  boolean mHavePhoneSignal;
  boolean mHaveWifi;
  long mHistEnd;
  long mHistStart;
  boolean mLargeMode;
  int mLevelBottom;
  int mLevelOffset;
  int mLevelTop;
  int mLineWidth;
  int mNumHist;
  final ChartData mPhoneSignalChart = new ChartData();
  String mPhoneSignalLabel;
  int mPhoneSignalOffset;
  String mScreenOnLabel;
  int mScreenOnOffset;
  final Paint mScreenOnPaint = new Paint();
  final Path mScreenOnPath = new Path();
  BatteryStats mStats;
  long mStatsPeriod;
  int mTextAscent;
  int mTextDescent;
  final TextPaint mTextPaint = new TextPaint(1);
  int mThinLineWidth;
  String mTotalDurationString;
  int mTotalDurationStringWidth;
  String mWakeLockLabel;
  int mWakeLockOffset;
  final Paint mWakeLockPaint = new Paint();
  final Path mWakeLockPath = new Path();
  String mWifiRunningLabel;
  int mWifiRunningOffset;
  final Paint mWifiRunningPaint = new Paint();
  final Path mWifiRunningPath = new Path();

  public BatteryHistoryChart(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.mBatteryBackgroundPaint.setARGB(255, 128, 128, 128);
    this.mBatteryBackgroundPaint.setStyle(Paint.Style.FILL);
    this.mBatteryGoodPaint.setARGB(128, 0, 255, 0);
    this.mBatteryGoodPaint.setStyle(Paint.Style.STROKE);
    this.mBatteryWarnPaint.setARGB(128, 255, 255, 0);
    this.mBatteryWarnPaint.setStyle(Paint.Style.STROKE);
    this.mBatteryCriticalPaint.setARGB(192, 255, 0, 0);
    this.mBatteryCriticalPaint.setStyle(Paint.Style.STROKE);
    this.mChargingPaint.setARGB(255, 0, 128, 0);
    this.mChargingPaint.setStyle(Paint.Style.STROKE);
    this.mScreenOnPaint.setStyle(Paint.Style.STROKE);
    this.mGpsOnPaint.setStyle(Paint.Style.STROKE);
    this.mWifiRunningPaint.setStyle(Paint.Style.STROKE);
    this.mWakeLockPaint.setStyle(Paint.Style.STROKE);
    this.mPhoneSignalChart.setColors(new int[] { 0, -6291456, -6250496, -8355808, -8355776, -8355744, -16744448 });
    this.mTextPaint.density = getResources().getDisplayMetrics().density;
    this.mTextPaint.setCompatibilityScaling(getResources().getCompatibilityInfo().applicationScale);
    TypedArray localTypedArray1 = paramContext.obtainStyledAttributes(paramAttributeSet, com.android.settings.R.styleable.BatteryHistoryChart, 0, 0);
    int i = 15;
    int j = -1;
    int k = -1;
    int m = localTypedArray1.getResourceId(0, -1);
    TypedArray localTypedArray2 = null;
    if (m != -1)
      localTypedArray2 = paramContext.obtainStyledAttributes(m, com.android.internal.R.styleable.TextAppearance);
    ColorStateList localColorStateList = null;
    if (localTypedArray2 != null)
    {
      int i4 = localTypedArray2.getIndexCount();
      int i5 = 0;
      if (i5 < i4)
      {
        int i6 = localTypedArray2.getIndex(i5);
        switch (i6)
        {
        default:
        case 3:
        case 0:
        case 1:
        case 2:
        }
        while (true)
        {
          i5++;
          break;
          localColorStateList = localTypedArray2.getColorStateList(i6);
          continue;
          i = localTypedArray2.getDimensionPixelSize(i6, i);
          continue;
          j = localTypedArray2.getInt(i6, -1);
          continue;
          k = localTypedArray2.getInt(i6, -1);
        }
      }
      localTypedArray2.recycle();
    }
    int n = 0;
    float f1 = 0.0F;
    float f2 = 0.0F;
    float f3 = 0.0F;
    int i1 = localTypedArray1.getIndexCount();
    int i2 = 0;
    if (i2 < i1)
    {
      int i3 = localTypedArray1.getIndex(i2);
      switch (i3)
      {
      default:
      case 5:
      case 6:
      case 7:
      case 8:
      case 4:
      case 1:
      case 2:
      case 3:
      }
      while (true)
      {
        i2++;
        break;
        n = localTypedArray1.getInt(i3, 0);
        continue;
        f1 = localTypedArray1.getFloat(i3, 0.0F);
        continue;
        f2 = localTypedArray1.getFloat(i3, 0.0F);
        continue;
        f3 = localTypedArray1.getFloat(i3, 0.0F);
        continue;
        localColorStateList = localTypedArray1.getColorStateList(i3);
        continue;
        i = localTypedArray1.getDimensionPixelSize(i3, i);
        continue;
        j = localTypedArray1.getInt(i3, j);
        continue;
        k = localTypedArray1.getInt(i3, k);
      }
    }
    localTypedArray1.recycle();
    this.mTextPaint.setColor(localColorStateList.getDefaultColor());
    this.mTextPaint.setTextSize(i);
    Typeface localTypeface = null;
    switch (j)
    {
    default:
    case 1:
    case 2:
    case 3:
    }
    while (true)
    {
      setTypeface(localTypeface, k);
      if (n != 0)
        this.mTextPaint.setShadowLayer(f3, f1, f2, n);
      return;
      localTypeface = Typeface.SANS_SERIF;
      continue;
      localTypeface = Typeface.SERIF;
      continue;
      localTypeface = Typeface.MONOSPACE;
    }
  }

  void finishPaths(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, Path paramPath1, int paramInt6, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, boolean paramBoolean5, Path paramPath2)
  {
    if (paramPath1 != null)
    {
      if ((paramInt6 >= 0) && (paramInt6 < paramInt1))
      {
        if (paramPath2 != null)
          paramPath2.lineTo(paramInt1, paramInt5);
        paramPath1.lineTo(paramInt1, paramInt5);
      }
      paramPath1.lineTo(paramInt1, paramInt3 + this.mLevelTop);
      paramPath1.lineTo(paramInt4, paramInt3 + this.mLevelTop);
      paramPath1.close();
    }
    if (paramBoolean1)
      this.mChargingPath.lineTo(paramInt1, paramInt2 - this.mChargingOffset);
    if (paramBoolean2)
      this.mScreenOnPath.lineTo(paramInt1, paramInt2 - this.mScreenOnOffset);
    if (paramBoolean3)
      this.mGpsOnPath.lineTo(paramInt1, paramInt2 - this.mGpsOnOffset);
    if (paramBoolean4)
      this.mWifiRunningPath.lineTo(paramInt1, paramInt2 - this.mWifiRunningOffset);
    if (paramBoolean5)
      this.mWakeLockPath.lineTo(paramInt1, paramInt2 - this.mWakeLockOffset);
    if (this.mHavePhoneSignal)
      this.mPhoneSignalChart.finish(paramInt1);
  }

  protected void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    int i = getWidth();
    int j = getHeight();
    boolean bool = isLayoutRtl();
    int k;
    Paint.Align localAlign;
    if (bool)
    {
      k = i;
      TextPaint localTextPaint = this.mTextPaint;
      if (!bool)
        break label695;
      localAlign = Paint.Align.RIGHT;
      label45: localTextPaint.setTextAlign(localAlign);
      paramCanvas.drawPath(this.mBatLevelPath, this.mBatteryBackgroundPaint);
      if (!this.mLargeMode)
        break label703;
      int i3 = this.mTotalDurationStringWidth / 2;
      if (bool)
        i3 = -i3;
      paramCanvas.drawText(this.mDurationString, k, -this.mTextAscent + this.mLineWidth / 2, this.mTextPaint);
      paramCanvas.drawText(this.mTotalDurationString, i / 2 - i3, this.mLevelBottom - this.mTextAscent + this.mThinLineWidth, this.mTextPaint);
    }
    while (true)
    {
      if (!this.mBatGoodPath.isEmpty())
        paramCanvas.drawPath(this.mBatGoodPath, this.mBatteryGoodPaint);
      if (!this.mBatWarnPath.isEmpty())
        paramCanvas.drawPath(this.mBatWarnPath, this.mBatteryWarnPaint);
      if (!this.mBatCriticalPath.isEmpty())
        paramCanvas.drawPath(this.mBatCriticalPath, this.mBatteryCriticalPaint);
      if (this.mHavePhoneSignal)
      {
        int i2 = j - this.mPhoneSignalOffset - this.mLineWidth / 2;
        this.mPhoneSignalChart.draw(paramCanvas, i2, this.mLineWidth);
      }
      if (!this.mScreenOnPath.isEmpty())
        paramCanvas.drawPath(this.mScreenOnPath, this.mScreenOnPaint);
      if (!this.mChargingPath.isEmpty())
        paramCanvas.drawPath(this.mChargingPath, this.mChargingPaint);
      if ((this.mHaveGps) && (!this.mGpsOnPath.isEmpty()))
        paramCanvas.drawPath(this.mGpsOnPath, this.mGpsOnPaint);
      if ((this.mHaveWifi) && (!this.mWifiRunningPath.isEmpty()))
        paramCanvas.drawPath(this.mWifiRunningPath, this.mWifiRunningPaint);
      if (!this.mWakeLockPath.isEmpty())
        paramCanvas.drawPath(this.mWakeLockPath, this.mWakeLockPaint);
      if (!this.mLargeMode)
        return;
      if (this.mHavePhoneSignal)
        paramCanvas.drawText(this.mPhoneSignalLabel, k, j - this.mPhoneSignalOffset - this.mTextDescent, this.mTextPaint);
      if (this.mHaveGps)
        paramCanvas.drawText(this.mGpsOnLabel, k, j - this.mGpsOnOffset - this.mTextDescent, this.mTextPaint);
      if (this.mHaveWifi)
        paramCanvas.drawText(this.mWifiRunningLabel, k, j - this.mWifiRunningOffset - this.mTextDescent, this.mTextPaint);
      paramCanvas.drawText(this.mWakeLockLabel, k, j - this.mWakeLockOffset - this.mTextDescent, this.mTextPaint);
      paramCanvas.drawText(this.mChargingLabel, k, j - this.mChargingOffset - this.mTextDescent, this.mTextPaint);
      paramCanvas.drawText(this.mScreenOnLabel, k, j - this.mScreenOnOffset - this.mTextDescent, this.mTextPaint);
      paramCanvas.drawLine(0.0F, this.mLevelBottom + this.mThinLineWidth / 2, i, this.mLevelBottom + this.mThinLineWidth / 2, this.mTextPaint);
      paramCanvas.drawLine(0.0F, this.mLevelTop, 0.0F, this.mLevelBottom + this.mThinLineWidth / 2, this.mTextPaint);
      for (int n = 0; n < 10; n++)
      {
        int i1 = this.mLevelTop + n * (this.mLevelBottom - this.mLevelTop) / 10;
        paramCanvas.drawLine(0.0F, i1, 2 * this.mThinLineWidth, i1, this.mTextPaint);
      }
      k = 0;
      break;
      label695: localAlign = Paint.Align.LEFT;
      break label45;
      label703: int m = this.mDurationStringWidth / 2;
      if (bool)
        m = -m;
      paramCanvas.drawText(this.mDurationString, i / 2 - m, j / 2 - (this.mTextDescent - this.mTextAscent) / 2 - this.mTextAscent, this.mTextPaint);
    }
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, paramInt2);
    this.mDurationStringWidth = ((int)this.mTextPaint.measureText(this.mDurationString));
    this.mTotalDurationStringWidth = ((int)this.mTextPaint.measureText(this.mTotalDurationString));
    this.mTextAscent = ((int)this.mTextPaint.ascent());
    this.mTextDescent = ((int)this.mTextPaint.descent());
  }

  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    int i = this.mTextDescent - this.mTextAscent;
    this.mThinLineWidth = ((int)TypedValue.applyDimension(1, 2.0F, getResources().getDisplayMetrics()));
    label147: int i10;
    int i12;
    label341: int i14;
    label367: int n;
    label389: label422: int i1;
    int i2;
    int i3;
    int i4;
    int i6;
    Path localPath1;
    boolean bool2;
    boolean bool3;
    boolean bool4;
    boolean bool5;
    boolean bool6;
    Object localObject;
    BatteryStats.HistoryItem localHistoryItem;
    label608: int i7;
    int i9;
    Path localPath2;
    label713: label759: boolean bool7;
    label785: label808: label837: boolean bool8;
    label856: label885: boolean bool9;
    label904: label933: boolean bool10;
    label952: label981: boolean bool11;
    label1000: int i8;
    if (paramInt2 > i * 6)
    {
      this.mLargeMode = true;
      if (paramInt2 > i * 15)
      {
        this.mLineWidth = (i / 2);
        this.mLevelTop = (i + this.mLineWidth);
        this.mScreenOnPaint.setARGB(255, 32, 64, 255);
        this.mGpsOnPaint.setARGB(255, 32, 64, 255);
        this.mWifiRunningPaint.setARGB(255, 32, 64, 255);
        this.mWakeLockPaint.setARGB(255, 32, 64, 255);
        if (this.mLineWidth <= 0)
          this.mLineWidth = 1;
        this.mTextPaint.setStrokeWidth(this.mThinLineWidth);
        this.mBatteryGoodPaint.setStrokeWidth(this.mThinLineWidth);
        this.mBatteryWarnPaint.setStrokeWidth(this.mThinLineWidth);
        this.mBatteryCriticalPaint.setStrokeWidth(this.mThinLineWidth);
        this.mChargingPaint.setStrokeWidth(this.mLineWidth);
        this.mScreenOnPaint.setStrokeWidth(this.mLineWidth);
        this.mGpsOnPaint.setStrokeWidth(this.mLineWidth);
        this.mWifiRunningPaint.setStrokeWidth(this.mLineWidth);
        this.mWakeLockPaint.setStrokeWidth(this.mLineWidth);
        if (!this.mLargeMode)
          break label1193;
        i10 = i + this.mLineWidth;
        this.mChargingOffset = this.mLineWidth;
        this.mScreenOnOffset = (i10 + this.mChargingOffset);
        this.mWakeLockOffset = (i10 + this.mScreenOnOffset);
        this.mWifiRunningOffset = (i10 + this.mWakeLockOffset);
        int i11 = this.mWifiRunningOffset;
        if (!this.mHaveWifi)
          break label1175;
        i12 = i10;
        this.mGpsOnOffset = (i12 + i11);
        int i13 = this.mGpsOnOffset;
        if (!this.mHaveGps)
          break label1181;
        i14 = i10;
        this.mPhoneSignalOffset = (i14 + i13);
        int i15 = this.mPhoneSignalOffset;
        if (!this.mHavePhoneSignal)
          break label1187;
        this.mLevelOffset = (i15 + i10 + 3 * this.mLineWidth / 2);
        if (this.mHavePhoneSignal)
          this.mPhoneSignalChart.init(paramInt1);
        this.mBatLevelPath.reset();
        this.mBatGoodPath.reset();
        this.mBatWarnPath.reset();
        this.mBatCriticalPath.reset();
        this.mScreenOnPath.reset();
        this.mGpsOnPath.reset();
        this.mWifiRunningPath.reset();
        this.mWakeLockPath.reset();
        this.mChargingPath.reset();
        long l1 = this.mHistStart;
        long l2 = this.mHistEnd - this.mHistStart;
        int k = this.mBatLow;
        int m = this.mBatHigh - this.mBatLow;
        n = paramInt2 - this.mLevelOffset - this.mLevelTop;
        this.mLevelBottom = (n + this.mLevelTop);
        i1 = 0;
        i2 = -1;
        i3 = -1;
        i4 = 0;
        int i5 = this.mNumHist;
        boolean bool1 = this.mStats.startIteratingHistoryLocked();
        i6 = 0;
        localPath1 = null;
        bool2 = false;
        bool3 = false;
        bool4 = false;
        bool5 = false;
        bool6 = false;
        localObject = null;
        if (!bool1)
          break label1566;
        localHistoryItem = new BatteryStats.HistoryItem();
        if ((!this.mStats.getNextHistoryLocked(localHistoryItem)) || (i4 >= i5))
          break label1566;
        if (localHistoryItem.cmd != 1)
          break label1493;
        i1 = (int)((localHistoryItem.time - l1) * paramInt1 / l2);
        i7 = n + this.mLevelTop - (localHistoryItem.batteryLevel - k) * (n - 1) / m;
        if ((i2 != i1) && (i3 != i7))
        {
          i9 = localHistoryItem.batteryLevel;
          if (i9 > 14)
            break label1266;
          localPath2 = this.mBatCriticalPath;
          if (localPath2 == localObject)
            break label1291;
          if (localObject != null)
            localObject.lineTo(i1, i7);
          float f3 = i1;
          float f4 = i7;
          localPath2.moveTo(f3, f4);
          localObject = localPath2;
          if (localPath1 != null)
            break label1313;
          localPath1 = this.mBatLevelPath;
          localPath1.moveTo(i1, i7);
          i6 = i1;
          i2 = i1;
          i3 = i7;
        }
        if ((0x80000 & localHistoryItem.states) == 0)
          break label1327;
        bool7 = true;
        if (bool7 != bool2)
        {
          if (!bool7)
            break label1333;
          this.mChargingPath.moveTo(i1, paramInt2 - this.mChargingOffset);
          bool2 = bool7;
        }
        if ((0x100000 & localHistoryItem.states) == 0)
          break label1353;
        bool8 = true;
        if (bool8 != bool3)
        {
          if (!bool8)
            break label1359;
          this.mScreenOnPath.moveTo(i1, paramInt2 - this.mScreenOnOffset);
          bool3 = bool8;
        }
        if ((0x10000000 & localHistoryItem.states) == 0)
          break label1379;
        bool9 = true;
        if (bool9 != bool4)
        {
          if (!bool9)
            break label1385;
          this.mGpsOnPath.moveTo(i1, paramInt2 - this.mGpsOnOffset);
          bool4 = bool9;
        }
        if ((0x4000000 & localHistoryItem.states) == 0)
          break label1405;
        bool10 = true;
        if (bool10 != bool5)
        {
          if (!bool10)
            break label1411;
          this.mWifiRunningPath.moveTo(i1, paramInt2 - this.mWifiRunningOffset);
          bool5 = bool10;
        }
        if ((0x40000000 & localHistoryItem.states) == 0)
          break label1431;
        bool11 = true;
        if (bool11 != bool6)
        {
          if (!bool11)
            break label1437;
          this.mWakeLockPath.moveTo(i1, paramInt2 - this.mWakeLockOffset);
          label1029: bool6 = bool11;
        }
        if ((this.mLargeMode) && (this.mHavePhoneSignal))
        {
          if ((0xF00 & localHistoryItem.states) >> 8 != 3)
            break label1457;
          i8 = 0;
          label1066: this.mPhoneSignalChart.addTick(i1, i8);
        }
      }
    }
    while (true)
    {
      i4++;
      break label608;
      this.mLineWidth = (i / 3);
      break;
      this.mLargeMode = false;
      this.mLineWidth = this.mThinLineWidth;
      this.mLevelTop = 0;
      this.mScreenOnPaint.setARGB(255, 0, 0, 255);
      this.mGpsOnPaint.setARGB(255, 0, 0, 255);
      this.mWifiRunningPaint.setARGB(255, 0, 0, 255);
      this.mWakeLockPaint.setARGB(255, 0, 0, 255);
      break label147;
      label1175: i12 = 0;
      break label341;
      label1181: i14 = 0;
      break label367;
      label1187: i10 = 0;
      break label389;
      label1193: int j = this.mLineWidth;
      this.mWakeLockOffset = j;
      this.mWifiRunningOffset = j;
      this.mGpsOnOffset = j;
      this.mScreenOnOffset = j;
      this.mChargingOffset = (2 * this.mLineWidth);
      this.mPhoneSignalOffset = 0;
      this.mLevelOffset = (3 * this.mLineWidth);
      if (!this.mHavePhoneSignal)
        break label422;
      this.mPhoneSignalChart.init(0);
      break label422;
      label1266: if (i9 <= 29)
      {
        localPath2 = this.mBatWarnPath;
        break label713;
      }
      localPath2 = this.mBatGoodPath;
      break label713;
      label1291: float f1 = i1;
      float f2 = i7;
      localPath2.lineTo(f1, f2);
      break label759;
      label1313: localPath1.lineTo(i1, i7);
      break label785;
      label1327: bool7 = false;
      break label808;
      label1333: this.mChargingPath.lineTo(i1, paramInt2 - this.mChargingOffset);
      break label837;
      label1353: bool8 = false;
      break label856;
      label1359: this.mScreenOnPath.lineTo(i1, paramInt2 - this.mScreenOnOffset);
      break label885;
      label1379: bool9 = false;
      break label904;
      label1385: this.mGpsOnPath.lineTo(i1, paramInt2 - this.mGpsOnOffset);
      break label933;
      label1405: bool10 = false;
      break label952;
      label1411: this.mWifiRunningPath.lineTo(i1, paramInt2 - this.mWifiRunningOffset);
      break label981;
      label1431: bool11 = false;
      break label1000;
      label1437: this.mWakeLockPath.lineTo(i1, paramInt2 - this.mWakeLockOffset);
      break label1029;
      label1457: if ((0x8000000 & localHistoryItem.states) != 0)
      {
        i8 = 1;
        break label1066;
      }
      i8 = 2 + ((0xF0 & localHistoryItem.states) >> 4);
      break label1066;
      label1493: if ((localHistoryItem.cmd != 3) && (localPath1 != null))
      {
        finishPaths(i1 + 1, paramInt2, n, i6, i3, localPath1, i2, bool2, bool3, bool4, bool5, bool6, localObject);
        i3 = -1;
        i2 = i3;
        localPath1 = null;
        bool2 = false;
        bool3 = false;
        bool4 = false;
        bool6 = false;
        localObject = null;
      }
    }
    label1566: finishPaths(paramInt1, paramInt2, n, i6, i3, localPath1, i2, bool2, bool3, bool4, bool5, bool6, localObject);
  }

  void setStats(BatteryStats paramBatteryStats)
  {
    this.mStats = paramBatteryStats;
    this.mStatsPeriod = this.mStats.computeBatteryRealtime(1000L * SystemClock.elapsedRealtime(), 0);
    String str = Utils.formatElapsedTime(getContext(), this.mStatsPeriod / 1000L, true);
    this.mDurationString = getContext().getString(2131428719, new Object[] { str });
    this.mChargingLabel = getContext().getString(2131428721);
    this.mScreenOnLabel = getContext().getString(2131428722);
    this.mGpsOnLabel = getContext().getString(2131428723);
    this.mWifiRunningLabel = getContext().getString(2131428724);
    this.mWakeLockLabel = getContext().getString(2131428725);
    this.mPhoneSignalLabel = getContext().getString(2131428726);
    int i = 0;
    int j = -1;
    this.mBatLow = 0;
    this.mBatHigh = 100;
    int k = 1;
    boolean bool1 = paramBatteryStats.startIteratingHistoryLocked();
    int m = 0;
    int n = 0;
    if (bool1)
    {
      BatteryStats.HistoryItem localHistoryItem = new BatteryStats.HistoryItem();
      while (paramBatteryStats.getNextHistoryLocked(localHistoryItem))
      {
        i++;
        if (localHistoryItem.cmd == 1)
        {
          if (k != 0)
          {
            k = 0;
            this.mHistStart = localHistoryItem.time;
          }
          if ((localHistoryItem.batteryLevel != j) || (i == 1))
            j = localHistoryItem.batteryLevel;
          n = i;
          this.mHistEnd = localHistoryItem.time;
          m |= localHistoryItem.states;
        }
      }
    }
    this.mNumHist = n;
    boolean bool2;
    if ((0x10000000 & m) != 0)
    {
      bool2 = true;
      this.mHaveGps = bool2;
      if ((0x4000000 & m) == 0)
        break label386;
    }
    label386: for (boolean bool3 = true; ; bool3 = false)
    {
      this.mHaveWifi = bool3;
      if (!com.android.settings.Utils.isWifiOnly(getContext()))
        this.mHavePhoneSignal = true;
      if (this.mHistEnd <= this.mHistStart)
        this.mHistEnd = (1L + this.mHistStart);
      this.mTotalDurationString = Utils.formatElapsedTime(getContext(), this.mHistEnd - this.mHistStart, true);
      return;
      bool2 = false;
      break;
    }
  }

  public void setTypeface(Typeface paramTypeface, int paramInt)
  {
    if (paramInt > 0)
    {
      Typeface localTypeface;
      int i;
      label36: TextPaint localTextPaint2;
      if (paramTypeface == null)
      {
        localTypeface = Typeface.defaultFromStyle(paramInt);
        this.mTextPaint.setTypeface(localTypeface);
        if (localTypeface == null)
          break label110;
        i = localTypeface.getStyle();
        int j = paramInt & (i ^ 0xFFFFFFFF);
        TextPaint localTextPaint1 = this.mTextPaint;
        int k = j & 0x1;
        boolean bool = false;
        if (k != 0)
          bool = true;
        localTextPaint1.setFakeBoldText(bool);
        localTextPaint2 = this.mTextPaint;
        if ((j & 0x2) == 0)
          break label116;
      }
      label110: label116: for (float f = -0.25F; ; f = 0.0F)
      {
        localTextPaint2.setTextSkewX(f);
        return;
        localTypeface = Typeface.create(paramTypeface, paramInt);
        break;
        i = 0;
        break label36;
      }
    }
    this.mTextPaint.setFakeBoldText(false);
    this.mTextPaint.setTextSkewX(0.0F);
    this.mTextPaint.setTypeface(paramTypeface);
  }

  static class ChartData
  {
    int[] mColors;
    int mLastBin;
    int mNumTicks;
    Paint[] mPaints;
    int[] mTicks;

    void addTick(int paramInt1, int paramInt2)
    {
      if ((paramInt2 != this.mLastBin) && (this.mNumTicks < this.mTicks.length))
      {
        this.mTicks[this.mNumTicks] = (paramInt1 | paramInt2 << 16);
        this.mNumTicks = (1 + this.mNumTicks);
        this.mLastBin = paramInt2;
      }
    }

    void draw(Canvas paramCanvas, int paramInt1, int paramInt2)
    {
      int i = 0;
      int j = 0;
      int k = paramInt1 + paramInt2;
      for (int m = 0; m < this.mNumTicks; m++)
      {
        int n = this.mTicks[m];
        int i1 = n & 0xFFFF;
        int i2 = (0xFFFF0000 & n) >> 16;
        if (i != 0)
          paramCanvas.drawRect(j, paramInt1, i1, k, this.mPaints[i]);
        i = i2;
        j = i1;
      }
    }

    void finish(int paramInt)
    {
      if (this.mLastBin != 0)
        addTick(paramInt, 0);
    }

    void init(int paramInt)
    {
      if (paramInt > 0);
      for (this.mTicks = new int[paramInt * 2]; ; this.mTicks = null)
      {
        this.mNumTicks = 0;
        this.mLastBin = 0;
        return;
      }
    }

    void setColors(int[] paramArrayOfInt)
    {
      this.mColors = paramArrayOfInt;
      this.mPaints = new Paint[paramArrayOfInt.length];
      for (int i = 0; i < paramArrayOfInt.length; i++)
      {
        this.mPaints[i] = new Paint();
        this.mPaints[i].setColor(paramArrayOfInt[i]);
        this.mPaints[i].setStyle(Paint.Style.FILL);
      }
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.fuelgauge.BatteryHistoryChart
 * JD-Core Version:    0.6.2
 */