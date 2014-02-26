package com.android.settings.fuelgauge;

import android.app.Activity;
import android.content.Context;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.BatteryStats;
import android.os.BatteryStats.Timer;
import android.os.BatteryStats.Uid;
import android.os.BatteryStats.Uid.Proc;
import android.os.BatteryStats.Uid.Sensor;
import android.os.BatteryStats.Uid.Wakelock;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.UserManager;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.ContextThemeWrapper;
import com.android.internal.app.IBatteryStats;
import com.android.internal.app.IBatteryStats.Stub;
import com.android.internal.os.BatteryStatsImpl;
import com.android.internal.os.PowerProfile;
import com.android.internal.util.FastPrintWriter;
import com.android.settings.Utils;
import com.android.settings.users.UserUtils;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class BatteryStatsHelper
{
  private static final String TAG = BatteryStatsHelper.class.getSimpleName();
  private static BatteryStatsImpl sStatsXfer;
  private Activity mActivity;
  private long mAppWifiRunning;
  private IBatteryStats mBatteryInfo;
  private double mBluetoothPower;
  private final List<BatterySipper> mBluetoothSippers = new ArrayList();
  private Handler mHandler;
  private double mMaxPower = 1.0D;
  private PowerProfile mPowerProfile;
  private ArrayList<BatterySipper> mRequestQueue = new ArrayList();
  private NameAndIconLoader mRequestThread;
  private BatteryStatsImpl mStats;
  private long mStatsPeriod = 0L;
  private int mStatsType = 0;
  private double mTotalPower;
  private UserManager mUm;
  private final List<BatterySipper> mUsageList = new ArrayList();
  private final SparseArray<Double> mUserPower = new SparseArray();
  private final SparseArray<List<BatterySipper>> mUserSippers = new SparseArray();
  private double mWifiPower;
  private final List<BatterySipper> mWifiSippers = new ArrayList();

  public BatteryStatsHelper(Activity paramActivity, Handler paramHandler)
  {
    this.mActivity = paramActivity;
    this.mHandler = paramHandler;
  }

  private void addBluetoothUsage(long paramLong)
  {
    long l = this.mStats.getBluetoothOnTime(paramLong, this.mStatsType) / 1000L;
    double d = l * this.mPowerProfile.getAveragePower("bluetooth.on") / 1000.0D + this.mStats.getBluetoothPingCount() * this.mPowerProfile.getAveragePower("bluetooth.at") / 1000.0D;
    aggregateSippers(addEntry(this.mActivity.getString(2131428739), PowerUsageDetail.DrainType.BLUETOOTH, l, 2130837605, d + this.mBluetoothPower), this.mBluetoothSippers, "Bluetooth");
  }

  private BatterySipper addEntry(String paramString, PowerUsageDetail.DrainType paramDrainType, long paramLong, int paramInt, double paramDouble)
  {
    if (paramDouble > this.mMaxPower)
      this.mMaxPower = paramDouble;
    this.mTotalPower = (paramDouble + this.mTotalPower);
    BatterySipper localBatterySipper = new BatterySipper(this.mActivity, this.mRequestQueue, this.mHandler, paramString, paramDrainType, paramInt, null, new double[] { paramDouble });
    localBatterySipper.usageTime = paramLong;
    localBatterySipper.iconId = paramInt;
    this.mUsageList.add(localBatterySipper);
    return localBatterySipper;
  }

  private void addIdleUsage(long paramLong)
  {
    long l = (paramLong - this.mStats.getScreenOnTime(paramLong, this.mStatsType)) / 1000L;
    double d = l * this.mPowerProfile.getAveragePower("cpu.idle") / 1000.0D;
    addEntry(this.mActivity.getString(2131428742), PowerUsageDetail.DrainType.IDLE, l, 2130837619, d);
  }

  private void addPhoneUsage(long paramLong)
  {
    long l = this.mStats.getPhoneOnTime(paramLong, this.mStatsType) / 1000L;
    double d = this.mPowerProfile.getAveragePower("radio.active") * l / 1000.0D;
    addEntry(this.mActivity.getString(2131428741), PowerUsageDetail.DrainType.PHONE, l, 2130837624, d);
  }

  private void addRadioUsage(long paramLong)
  {
    double d1 = 0.0D;
    long l1 = 0L;
    for (int i = 0; i < 5; i++)
    {
      long l2 = this.mStats.getPhoneSignalStrengthTime(i, paramLong, this.mStatsType) / 1000L;
      d1 += l2 / 1000L * this.mPowerProfile.getAveragePower("radio.on", i);
      l1 += l2;
    }
    double d2 = d1 + this.mStats.getPhoneSignalScanningTime(paramLong, this.mStatsType) / 1000L / 1000L * this.mPowerProfile.getAveragePower("radio.scanning");
    BatterySipper localBatterySipper = addEntry(this.mActivity.getString(2131428740), PowerUsageDetail.DrainType.CELL, l1, 2130837607, d2);
    if (l1 != 0L)
      localBatterySipper.noCoveragePercent = (100.0D * (this.mStats.getPhoneSignalStrengthTime(0, paramLong, this.mStatsType) / 1000L) / l1);
  }

  private void addScreenUsage(long paramLong)
  {
    long l = this.mStats.getScreenOnTime(paramLong, this.mStatsType) / 1000L;
    double d1 = 0.0D + l * this.mPowerProfile.getAveragePower("screen.on");
    double d2 = this.mPowerProfile.getAveragePower("screen.full");
    for (int i = 0; i < 5; i++)
      d1 += d2 * (0.5F + i) / 5.0D * (this.mStats.getScreenBrightnessTime(i, paramLong, this.mStatsType) / 1000L);
    double d3 = d1 / 1000.0D;
    addEntry(this.mActivity.getString(2131428737), PowerUsageDetail.DrainType.SCREEN, l, 2130837611, d3);
  }

  private void addUserUsage()
  {
    int i = 0;
    if (i < this.mUserSippers.size())
    {
      int j = this.mUserSippers.keyAt(i);
      List localList = (List)this.mUserSippers.valueAt(i);
      UserInfo localUserInfo = this.mUm.getUserInfo(j);
      String str2;
      label83: String str1;
      Drawable localDrawable1;
      label126: Double localDouble;
      if (localUserInfo != null)
      {
        Drawable localDrawable2 = UserUtils.getUserIcon(this.mActivity, this.mUm, localUserInfo, this.mActivity.getResources());
        if (localUserInfo != null)
        {
          str2 = localUserInfo.name;
          if (str2 == null)
            str2 = Integer.toString(localUserInfo.id);
          str1 = this.mActivity.getResources().getString(2131428478, new Object[] { str2 });
          localDrawable1 = localDrawable2;
          localDouble = (Double)this.mUserPower.get(j);
          if (localDouble == null)
            break label216;
        }
      }
      label216: for (double d = localDouble.doubleValue(); ; d = 0.0D)
      {
        BatterySipper localBatterySipper = addEntry(str1, PowerUsageDetail.DrainType.USER, 0L, 0, d);
        localBatterySipper.icon = localDrawable1;
        aggregateSippers(localBatterySipper, localList, "User");
        i++;
        break;
        str2 = null;
        break label83;
        str1 = this.mActivity.getResources().getString(2131428479);
        localDrawable1 = null;
        break label126;
      }
    }
  }

  private void addWiFiUsage(long paramLong)
  {
    long l1 = this.mStats.getWifiOnTime(paramLong, this.mStatsType) / 1000L;
    long l2 = this.mStats.getGlobalWifiRunningTime(paramLong, this.mStatsType) / 1000L - this.mAppWifiRunning;
    if (l2 < 0L)
      l2 = 0L;
    double d = (0L * l1 * this.mPowerProfile.getAveragePower("wifi.on") + l2 * this.mPowerProfile.getAveragePower("wifi.on")) / 1000.0D;
    aggregateSippers(addEntry(this.mActivity.getString(2131428738), PowerUsageDetail.DrainType.WIFI, l2, 2130837625, d + this.mWifiPower), this.mWifiSippers, "WIFI");
  }

  private void aggregateSippers(BatterySipper paramBatterySipper, List<BatterySipper> paramList, String paramString)
  {
    for (int i = 0; i < paramList.size(); i++)
    {
      BatterySipper localBatterySipper = (BatterySipper)paramList.get(i);
      paramBatterySipper.cpuTime += localBatterySipper.cpuTime;
      paramBatterySipper.gpsTime += localBatterySipper.gpsTime;
      paramBatterySipper.wifiRunningTime += localBatterySipper.wifiRunningTime;
      paramBatterySipper.cpuFgTime += localBatterySipper.cpuFgTime;
      paramBatterySipper.wakeLockTime += localBatterySipper.wakeLockTime;
      paramBatterySipper.mobileRxBytes += localBatterySipper.mobileRxBytes;
      paramBatterySipper.mobileTxBytes += localBatterySipper.mobileTxBytes;
      paramBatterySipper.wifiRxBytes += localBatterySipper.wifiRxBytes;
      paramBatterySipper.wifiTxBytes += localBatterySipper.wifiTxBytes;
    }
  }

  private double getMobilePowerPerByte()
  {
    double d = this.mPowerProfile.getAveragePower("radio.active") / 3600.0D;
    long l1 = this.mStats.getNetworkActivityCount(0, this.mStatsType) + this.mStats.getNetworkActivityCount(1, this.mStatsType);
    long l2 = this.mStats.getRadioDataUptime() / 1000L;
    if (l2 != 0L);
    for (long l3 = 1000L * (8L * l1) / l2; ; l3 = 200000L)
      return d / (l3 / 8L);
  }

  private double getWifiPowerPerByte()
  {
    return this.mPowerProfile.getAveragePower("wifi.active") / 3600.0D / 125000.0D;
  }

  private void load()
  {
    try
    {
      byte[] arrayOfByte = this.mBatteryInfo.getStatistics();
      Parcel localParcel = Parcel.obtain();
      localParcel.unmarshall(arrayOfByte, 0, arrayOfByte.length);
      localParcel.setDataPosition(0);
      this.mStats = ((BatteryStatsImpl)BatteryStatsImpl.CREATOR.createFromParcel(localParcel));
      this.mStats.distributeWorkLocked(0);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e(TAG, "RemoteException:", localRemoteException);
    }
  }

  private void processAppUsage(boolean paramBoolean)
  {
    SensorManager localSensorManager = (SensorManager)this.mActivity.getSystemService("sensor");
    int i = this.mStatsType;
    int j = this.mPowerProfile.getNumSpeedSteps();
    double[] arrayOfDouble1 = new double[j];
    long[] arrayOfLong = new long[j];
    for (int k = 0; k < j; k++)
      arrayOfDouble1[k] = this.mPowerProfile.getAveragePower("cpu.active", k);
    double d1 = getMobilePowerPerByte();
    double d2 = getWifiPowerPerByte();
    long l1 = this.mStats.computeBatteryRealtime(1000L * SystemClock.elapsedRealtime(), i);
    long l2 = 0L;
    Object localObject1 = null;
    this.mStatsPeriod = l1;
    SparseArray localSparseArray = this.mStats.getUidStats();
    int m = localSparseArray.size();
    int n = 0;
    if (n < m)
    {
      BatteryStats.Uid localUid = (BatteryStats.Uid)localSparseArray.valueAt(n);
      double d5 = 0.0D;
      double d6 = 0.0D;
      Map localMap = localUid.getProcessStats();
      long l5 = 0L;
      long l6 = 0L;
      long l7 = 0L;
      long l8 = 0L;
      int i1 = localMap.size();
      String str = null;
      if (i1 > 0)
      {
        Iterator localIterator4 = localMap.entrySet().iterator();
        while (localIterator4.hasNext())
        {
          Map.Entry localEntry = (Map.Entry)localIterator4.next();
          BatteryStats.Uid.Proc localProc = (BatteryStats.Uid.Proc)localEntry.getValue();
          long l16 = localProc.getUserTime(i);
          long l17 = localProc.getSystemTime(i);
          l6 += 10L * localProc.getForegroundTime(i);
          long l18 = 10L * (l16 + l17);
          int i7 = 0;
          for (int i8 = 0; i8 < j; i8++)
          {
            arrayOfLong[i8] = localProc.getTimeAtCpuSpeedStep(i8, i);
            i7 = (int)(i7 + arrayOfLong[i8]);
          }
          if (i7 == 0)
            i7 = 1;
          double d13 = 0.0D;
          for (int i9 = 0; i9 < j; i9++)
            d13 += arrayOfLong[i9] / i7 * l18 * arrayOfDouble1[i9];
          l5 += l18;
          d5 += d13;
          if ((str == null) || (str.startsWith("*")))
          {
            d6 = d13;
            str = (String)localEntry.getKey();
          }
          else if ((d6 < d13) && (!((String)localEntry.getKey()).startsWith("*")))
          {
            d6 = d13;
            str = (String)localEntry.getKey();
          }
        }
      }
      if (l6 > l5)
        l5 = l6;
      double d7 = d5 / 1000.0D;
      Iterator localIterator1 = localUid.getWakelockStats().entrySet().iterator();
      while (localIterator1.hasNext())
      {
        BatteryStats.Timer localTimer = ((BatteryStats.Uid.Wakelock)((Map.Entry)localIterator1.next()).getValue()).getWakeTime(0);
        if (localTimer != null)
          l7 += localTimer.getTotalTimeLocked(l1, i);
      }
      long l9 = l7 / 1000L;
      l2 += l9;
      double d8 = d7 + l9 * this.mPowerProfile.getAveragePower("cpu.awake") / 1000.0D;
      long l10 = localUid.getNetworkActivityCount(0, this.mStatsType);
      long l11 = localUid.getNetworkActivityCount(1, this.mStatsType);
      double d9 = d8 + d1 * (l10 + l11);
      long l12 = localUid.getNetworkActivityCount(2, this.mStatsType);
      long l13 = localUid.getNetworkActivityCount(3, this.mStatsType);
      double d10 = d9 + d2 * (l12 + l13);
      long l14 = localUid.getWifiRunningTime(l1, i) / 1000L;
      this.mAppWifiRunning = (l14 + this.mAppWifiRunning);
      double d11 = d10 + l14 * this.mPowerProfile.getAveragePower("wifi.on") / 1000.0D + localUid.getWifiScanTime(l1, i) / 1000L * this.mPowerProfile.getAveragePower("wifi.scan") / 1000.0D;
      for (int i2 = 0; i2 < 5; i2++)
        d11 += localUid.getWifiBatchedScanTime(i2, l1, i) / 1000L * this.mPowerProfile.getAveragePower("wifi.batchedscan", i2);
      Iterator localIterator2 = localUid.getSensorStats().entrySet().iterator();
      if (localIterator2.hasNext())
      {
        BatteryStats.Uid.Sensor localSensor = (BatteryStats.Uid.Sensor)((Map.Entry)localIterator2.next()).getValue();
        int i6 = localSensor.getHandle();
        long l15 = localSensor.getSensorTime().getTotalTimeLocked(l1, i) / 1000L;
        double d12 = 0.0D;
        switch (i6)
        {
        default:
          Iterator localIterator3 = localSensorManager.getSensorList(-1).iterator();
          while (localIterator3.hasNext())
          {
            Sensor localSensor1 = (Sensor)localIterator3.next();
            if (localSensor1.getHandle() == i6)
              d12 = localSensor1.getPower();
          }
        case -10000:
        }
        while (true)
        {
          d11 += d12 * l15 / 1000.0D;
          break;
          d12 = this.mPowerProfile.getAveragePower("gps.on");
          l8 = l15;
        }
      }
      int i3 = 0;
      int i4 = UserHandle.getUserId(localUid.getUid());
      BatterySipper localBatterySipper;
      if ((d11 == 0.0D) && (!paramBoolean))
      {
        int i5 = localUid.getUid();
        i3 = 0;
        if (i5 != 0);
      }
      else
      {
        localBatterySipper = new BatterySipper(this.mActivity, this.mRequestQueue, this.mHandler, str, PowerUsageDetail.DrainType.APP, 0, localUid, new double[] { d11 });
        localBatterySipper.cpuTime = l5;
        localBatterySipper.gpsTime = l8;
        localBatterySipper.wifiRunningTime = l14;
        localBatterySipper.cpuFgTime = l6;
        localBatterySipper.wakeLockTime = l9;
        localBatterySipper.mobileRxBytes = l10;
        localBatterySipper.mobileTxBytes = l11;
        localBatterySipper.wifiRxBytes = l12;
        localBatterySipper.wifiTxBytes = l13;
        if (localUid.getUid() != 1010)
          break label1202;
        this.mWifiSippers.add(localBatterySipper);
        label1151: if (localUid.getUid() == 0)
          localObject1 = localBatterySipper;
      }
      if ((d11 != 0.0D) || (paramBoolean))
      {
        if (localUid.getUid() != 1010)
          break label1326;
        this.mWifiPower = (d11 + this.mWifiPower);
      }
      while (true)
      {
        n++;
        break;
        label1202: if (localUid.getUid() == 1002)
        {
          this.mBluetoothSippers.add(localBatterySipper);
          i3 = 0;
          break label1151;
        }
        if ((i4 != UserHandle.myUserId()) && (UserHandle.getAppId(localUid.getUid()) >= 10000))
        {
          i3 = 1;
          Object localObject2 = (List)this.mUserSippers.get(i4);
          if (localObject2 == null)
          {
            localObject2 = new ArrayList();
            this.mUserSippers.put(i4, localObject2);
          }
          ((List)localObject2).add(localBatterySipper);
          break label1151;
        }
        this.mUsageList.add(localBatterySipper);
        i3 = 0;
        break label1151;
        label1326: if (localUid.getUid() == 1002)
        {
          this.mBluetoothPower = (d11 + this.mBluetoothPower);
        }
        else
        {
          if (i3 != 0)
          {
            Double localDouble1 = (Double)this.mUserPower.get(i4);
            if (localDouble1 == null);
            for (Double localDouble2 = Double.valueOf(d11); ; localDouble2 = Double.valueOf(d11 + localDouble1.doubleValue()))
            {
              this.mUserPower.put(i4, localDouble2);
              break;
            }
          }
          if (d11 > this.mMaxPower)
            this.mMaxPower = d11;
          this.mTotalPower = (d11 + this.mTotalPower);
        }
      }
    }
    if (localObject1 != null)
    {
      long l3 = this.mStats.computeBatteryUptime(1000L * SystemClock.uptimeMillis(), i) / 1000L - (l2 + this.mStats.getScreenOnTime(SystemClock.elapsedRealtime(), i) / 1000L);
      if (l3 > 0L)
      {
        double d3 = l3 * this.mPowerProfile.getAveragePower("cpu.awake") / 1000.0D;
        long l4 = l3 + localObject1.wakeLockTime;
        localObject1.wakeLockTime = l4;
        double d4 = d3 + localObject1.value;
        localObject1.value = d4;
        double[] arrayOfDouble2 = localObject1.values;
        arrayOfDouble2[0] = (d3 + arrayOfDouble2[0]);
        if (localObject1.value > this.mMaxPower)
          this.mMaxPower = localObject1.value;
        this.mTotalPower = (d3 + this.mTotalPower);
      }
    }
  }

  private void processMiscUsage()
  {
    int i = this.mStatsType;
    long l1 = 1000L * SystemClock.elapsedRealtime();
    long l2 = this.mStats.computeBatteryRealtime(l1, i);
    addUserUsage();
    addPhoneUsage(l2);
    addScreenUsage(l2);
    addWiFiUsage(l2);
    addBluetoothUsage(l2);
    addIdleUsage(l2);
    if (!Utils.isWifiOnly(this.mActivity))
      addRadioUsage(l2);
  }

  public void clearStats()
  {
    this.mStats = null;
  }

  public void create(Bundle paramBundle)
  {
    if (paramBundle != null)
      this.mStats = sStatsXfer;
    this.mBatteryInfo = IBatteryStats.Stub.asInterface(ServiceManager.getService("batterystats"));
    this.mUm = ((UserManager)this.mActivity.getSystemService("user"));
    this.mPowerProfile = new PowerProfile(this.mActivity);
  }

  public void destroy()
  {
    if (this.mActivity.isChangingConfigurations())
    {
      sStatsXfer = this.mStats;
      return;
    }
    BatterySipper.sUidCache.clear();
  }

  public double getMaxPower()
  {
    return this.mMaxPower;
  }

  public PowerProfile getPowerProfile()
  {
    return this.mPowerProfile;
  }

  public BatteryStatsImpl getStats()
  {
    if (this.mStats == null)
      load();
    return this.mStats;
  }

  public double getTotalPower()
  {
    return this.mTotalPower;
  }

  public List<BatterySipper> getUsageList()
  {
    return this.mUsageList;
  }

  public void pause()
  {
    if (this.mRequestThread != null)
      this.mRequestThread.abort();
  }

  public void refreshStats(boolean paramBoolean)
  {
    getStats();
    this.mMaxPower = 0.0D;
    this.mTotalPower = 0.0D;
    this.mWifiPower = 0.0D;
    this.mBluetoothPower = 0.0D;
    this.mAppWifiRunning = 0L;
    this.mUsageList.clear();
    this.mWifiSippers.clear();
    this.mBluetoothSippers.clear();
    this.mUserSippers.clear();
    this.mUserPower.clear();
    processAppUsage(paramBoolean);
    processMiscUsage();
    Collections.sort(this.mUsageList);
    if (this.mHandler != null)
      synchronized (this.mRequestQueue)
      {
        if (!this.mRequestQueue.isEmpty())
        {
          if (this.mRequestThread != null)
            this.mRequestThread.abort();
          this.mRequestThread = new NameAndIconLoader();
          this.mRequestThread.setPriority(1);
          this.mRequestThread.start();
          this.mRequestQueue.notify();
        }
        return;
      }
  }

  public void startBatteryDetailPage(PreferenceActivity paramPreferenceActivity, BatterySipper paramBatterySipper, boolean paramBoolean)
  {
    getStats();
    Bundle localBundle = new Bundle();
    localBundle.putString("title", paramBatterySipper.name);
    localBundle.putInt("percent", (int)Math.ceil(100.0D * paramBatterySipper.getSortValue() / this.mTotalPower));
    localBundle.putInt("gauge", (int)Math.ceil(100.0D * paramBatterySipper.getSortValue() / this.mMaxPower));
    localBundle.putLong("duration", this.mStatsPeriod);
    localBundle.putString("iconPackage", paramBatterySipper.defaultPackageName);
    localBundle.putInt("iconId", paramBatterySipper.iconId);
    localBundle.putDouble("noCoverage", paramBatterySipper.noCoveragePercent);
    if (paramBatterySipper.uidObj != null)
      localBundle.putInt("uid", paramBatterySipper.uidObj.getUid());
    localBundle.putSerializable("drainType", paramBatterySipper.drainType);
    localBundle.putBoolean("showLocationButton", paramBoolean);
    int[] arrayOfInt;
    double[] arrayOfDouble;
    switch (1.$SwitchMap$com$android$settings$fuelgauge$PowerUsageDetail$DrainType[paramBatterySipper.drainType.ordinal()])
    {
    default:
      arrayOfInt = new int[] { 2131428755 };
      arrayOfDouble = new double[1];
      arrayOfDouble[0] = paramBatterySipper.usageTime;
    case 1:
    case 2:
    case 3:
    case 4:
    case 5:
    }
    while (true)
    {
      localBundle.putIntArray("types", arrayOfInt);
      localBundle.putDoubleArray("values", arrayOfDouble);
      paramPreferenceActivity.startPreferencePanel(PowerUsageDetail.class.getName(), localBundle, 2131428733, null, null, 0);
      return;
      BatteryStats.Uid localUid = paramBatterySipper.uidObj;
      arrayOfInt = new int[] { 2131428743, 2131428744, 2131428745, 2131428746, 2131428747, 2131428750, 2131428749, 2131428752, 2131428751, 2131428753, 2131428754 };
      arrayOfDouble = new double[11];
      arrayOfDouble[0] = paramBatterySipper.cpuTime;
      arrayOfDouble[1] = paramBatterySipper.cpuFgTime;
      arrayOfDouble[2] = paramBatterySipper.wakeLockTime;
      arrayOfDouble[3] = paramBatterySipper.gpsTime;
      arrayOfDouble[4] = paramBatterySipper.wifiRunningTime;
      arrayOfDouble[5] = paramBatterySipper.mobileRxBytes;
      arrayOfDouble[6] = paramBatterySipper.mobileTxBytes;
      arrayOfDouble[7] = paramBatterySipper.wifiRxBytes;
      arrayOfDouble[8] = paramBatterySipper.wifiTxBytes;
      arrayOfDouble[9] = 0.0D;
      arrayOfDouble[10] = 0.0D;
      if (paramBatterySipper.drainType == PowerUsageDetail.DrainType.APP)
      {
        StringWriter localStringWriter1 = new StringWriter();
        FastPrintWriter localFastPrintWriter1 = new FastPrintWriter(localStringWriter1, false, 1024);
        this.mStats.dumpLocked(localFastPrintWriter1, "", this.mStatsType, localUid.getUid());
        localFastPrintWriter1.flush();
        localBundle.putString("report_details", localStringWriter1.toString());
        StringWriter localStringWriter2 = new StringWriter();
        FastPrintWriter localFastPrintWriter2 = new FastPrintWriter(localStringWriter2, false, 1024);
        this.mStats.dumpCheckinLocked(localFastPrintWriter2, this.mStatsType, localUid.getUid());
        localFastPrintWriter2.flush();
        localBundle.putString("report_checkin_details", localStringWriter2.toString());
        continue;
        arrayOfInt = new int[] { 2131428755, 2131428756 };
        arrayOfDouble = new double[2];
        arrayOfDouble[0] = paramBatterySipper.usageTime;
        arrayOfDouble[1] = paramBatterySipper.noCoveragePercent;
        continue;
        arrayOfInt = new int[] { 2131428747, 2131428743, 2131428744, 2131428745, 2131428750, 2131428749, 2131428752, 2131428751 };
        arrayOfDouble = new double[8];
        arrayOfDouble[0] = paramBatterySipper.usageTime;
        arrayOfDouble[1] = paramBatterySipper.cpuTime;
        arrayOfDouble[2] = paramBatterySipper.cpuFgTime;
        arrayOfDouble[3] = paramBatterySipper.wakeLockTime;
        arrayOfDouble[4] = paramBatterySipper.mobileRxBytes;
        arrayOfDouble[5] = paramBatterySipper.mobileTxBytes;
        arrayOfDouble[6] = paramBatterySipper.wifiRxBytes;
        arrayOfDouble[7] = paramBatterySipper.wifiTxBytes;
        continue;
        arrayOfInt = new int[] { 2131428755, 2131428743, 2131428744, 2131428745, 2131428750, 2131428749, 2131428752, 2131428751 };
        arrayOfDouble = new double[8];
        arrayOfDouble[0] = paramBatterySipper.usageTime;
        arrayOfDouble[1] = paramBatterySipper.cpuTime;
        arrayOfDouble[2] = paramBatterySipper.cpuFgTime;
        arrayOfDouble[3] = paramBatterySipper.wakeLockTime;
        arrayOfDouble[4] = paramBatterySipper.mobileRxBytes;
        arrayOfDouble[5] = paramBatterySipper.mobileTxBytes;
        arrayOfDouble[6] = paramBatterySipper.wifiRxBytes;
        arrayOfDouble[7] = paramBatterySipper.wifiTxBytes;
      }
    }
  }

  private class NameAndIconLoader extends Thread
  {
    private boolean mAbort = false;

    public NameAndIconLoader()
    {
      super();
    }

    public void abort()
    {
      this.mAbort = true;
    }

    public void run()
    {
      synchronized (BatteryStatsHelper.this.mRequestQueue)
      {
        if ((BatteryStatsHelper.this.mRequestQueue.isEmpty()) || (this.mAbort))
        {
          BatteryStatsHelper.this.mHandler.sendEmptyMessage(2);
          return;
        }
        BatterySipper localBatterySipper = (BatterySipper)BatteryStatsHelper.this.mRequestQueue.remove(0);
        localBatterySipper.loadNameAndIcon();
      }
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.fuelgauge.BatteryStatsHelper
 * JD-Core Version:    0.6.2
 */