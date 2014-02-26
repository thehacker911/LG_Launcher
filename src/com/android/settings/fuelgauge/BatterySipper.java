package com.android.settings.fuelgauge;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.BatteryStats.Uid;
import android.os.Handler;
import java.util.ArrayList;
import java.util.HashMap;

public class BatterySipper
  implements Comparable<BatterySipper>
{
  static final HashMap<String, UidToDetail> sUidCache = new HashMap();
  long cpuFgTime;
  long cpuTime;
  String defaultPackageName;
  PowerUsageDetail.DrainType drainType;
  long gpsTime;
  Drawable icon;
  int iconId;
  final Context mContext;
  final Handler mHandler;
  String[] mPackages;
  final ArrayList<BatterySipper> mRequestQueue;
  long mobileRxBytes;
  long mobileTxBytes;
  String name;
  double noCoveragePercent;
  double percent;
  BatteryStats.Uid uidObj;
  long usageTime;
  double value;
  double[] values;
  long wakeLockTime;
  long wifiRunningTime;
  long wifiRxBytes;
  long wifiTxBytes;

  BatterySipper(Context paramContext, ArrayList<BatterySipper> paramArrayList, Handler paramHandler, String paramString, PowerUsageDetail.DrainType paramDrainType, int paramInt, BatteryStats.Uid paramUid, double[] paramArrayOfDouble)
  {
    this.mContext = paramContext;
    this.mRequestQueue = paramArrayList;
    this.mHandler = paramHandler;
    this.values = paramArrayOfDouble;
    this.name = paramString;
    this.drainType = paramDrainType;
    if (paramInt > 0)
      this.icon = this.mContext.getResources().getDrawable(paramInt);
    if (paramArrayOfDouble != null)
      this.value = paramArrayOfDouble[0];
    if (((paramString == null) || (paramInt == 0)) && (paramUid != null))
      getQuickNameIconForUid(paramUid);
    this.uidObj = paramUid;
  }

  public int compareTo(BatterySipper paramBatterySipper)
  {
    return Double.compare(paramBatterySipper.getSortValue(), getSortValue());
  }

  public Drawable getIcon()
  {
    return this.icon;
  }

  void getQuickNameIconForUid(BatteryStats.Uid paramUid)
  {
    int i = paramUid.getUid();
    String str = Integer.toString(i);
    if (sUidCache.containsKey(str))
    {
      UidToDetail localUidToDetail = (UidToDetail)sUidCache.get(str);
      this.defaultPackageName = localUidToDetail.packageName;
      this.name = localUidToDetail.name;
      this.icon = localUidToDetail.icon;
    }
    do
    {
      return;
      PackageManager localPackageManager = this.mContext.getPackageManager();
      String[] arrayOfString = localPackageManager.getPackagesForUid(i);
      this.icon = localPackageManager.getDefaultActivityIcon();
      if (arrayOfString == null)
      {
        if (i == 0)
          this.name = this.mContext.getResources().getString(2131428783);
        while (true)
        {
          this.iconId = 2130837599;
          this.icon = this.mContext.getResources().getDrawable(this.iconId);
          return;
          if ("mediaserver".equals(this.name))
            this.name = this.mContext.getResources().getString(2131428784);
        }
      }
    }
    while (this.mHandler == null);
    synchronized (this.mRequestQueue)
    {
      this.mRequestQueue.add(this);
      return;
    }
  }

  double getSortValue()
  {
    return this.value;
  }

  public void loadNameAndIcon()
  {
    if (this.uidObj == null)
      return;
    PackageManager localPackageManager = this.mContext.getPackageManager();
    int i = this.uidObj.getUid();
    Drawable localDrawable = localPackageManager.getDefaultActivityIcon();
    this.mPackages = localPackageManager.getPackagesForUid(i);
    if (this.mPackages == null)
    {
      this.name = Integer.toString(i);
      return;
    }
    String[] arrayOfString1 = new String[this.mPackages.length];
    System.arraycopy(this.mPackages, 0, arrayOfString1, 0, this.mPackages.length);
    int j = 0;
    label83: if ((j >= arrayOfString1.length) || (arrayOfString1[j].equals(this.name)));
    label408: 
    while (true)
    {
      try
      {
        ApplicationInfo localApplicationInfo = localPackageManager.getApplicationInfo(arrayOfString1[j], 0);
        CharSequence localCharSequence2 = localApplicationInfo.loadLabel(localPackageManager);
        if (localCharSequence2 != null)
          arrayOfString1[j] = localCharSequence2.toString();
        if (localApplicationInfo.icon != 0)
        {
          this.defaultPackageName = this.mPackages[j];
          this.icon = localApplicationInfo.loadIcon(localPackageManager);
          if (this.icon == null)
            this.icon = localDrawable;
          if (arrayOfString1.length != 1)
            break label292;
          this.name = arrayOfString1[0];
          String str2 = Integer.toString(this.uidObj.getUid());
          UidToDetail localUidToDetail = new UidToDetail();
          localUidToDetail.name = this.name;
          localUidToDetail.icon = this.icon;
          localUidToDetail.packageName = this.defaultPackageName;
          sUidCache.put(str2, localUidToDetail);
          if (this.mHandler == null)
            break;
          this.mHandler.sendMessage(this.mHandler.obtainMessage(1, this));
          return;
        }
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException2)
      {
        j++;
      }
      break label83;
      label292: String[] arrayOfString2 = this.mPackages;
      int k = arrayOfString2.length;
      int m = 0;
      while (true)
        while (true)
        {
          if (m >= k)
            break label408;
          String str1 = arrayOfString2[m];
          try
          {
            PackageInfo localPackageInfo = localPackageManager.getPackageInfo(str1, 0);
            if (localPackageInfo.sharedUserLabel != 0)
            {
              CharSequence localCharSequence1 = localPackageManager.getText(str1, localPackageInfo.sharedUserLabel, localPackageInfo.applicationInfo);
              if (localCharSequence1 != null)
              {
                this.name = localCharSequence1.toString();
                if (localPackageInfo.applicationInfo.icon == 0)
                  break;
                this.defaultPackageName = str1;
                this.icon = localPackageInfo.applicationInfo.loadIcon(localPackageManager);
              }
            }
          }
          catch (PackageManager.NameNotFoundException localNameNotFoundException1)
          {
            m++;
          }
        }
    }
  }

  static class UidToDetail
  {
    Drawable icon;
    String name;
    String packageName;
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.fuelgauge.BatterySipper
 * JD-Core Version:    0.6.2
 */