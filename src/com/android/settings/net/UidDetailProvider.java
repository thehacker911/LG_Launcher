package com.android.settings.net;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.SparseArray;
import com.android.settings.Utils;
import com.android.settings.users.UserUtils;

public class UidDetailProvider
{
  private final Context mContext;
  private final SparseArray<UidDetail> mUidDetailCache;

  public UidDetailProvider(Context paramContext)
  {
    this.mContext = paramContext.getApplicationContext();
    this.mUidDetailCache = new SparseArray();
  }

  public static int buildKeyForUser(int paramInt)
  {
    return -(paramInt + 2000);
  }

  private UidDetail buildUidDetail(int paramInt)
  {
    Resources localResources = this.mContext.getResources();
    PackageManager localPackageManager = this.mContext.getPackageManager();
    UidDetail localUidDetail = new UidDetail();
    localUidDetail.label = localPackageManager.getNameForUid(paramInt);
    localUidDetail.icon = localPackageManager.getDefaultActivityIcon();
    switch (paramInt)
    {
    default:
      if (paramInt <= -2000)
      {
        int m = -2000 + -paramInt;
        UserManager localUserManager = (UserManager)this.mContext.getSystemService("user");
        UserInfo localUserInfo = localUserManager.getUserInfo(m);
        if (localUserInfo != null)
        {
          Object[] arrayOfObject = new Object[1];
          arrayOfObject[0] = localUserInfo.name;
          localUidDetail.label = localResources.getString(2131428478, arrayOfObject);
          localUidDetail.icon = UserUtils.getUserIcon(this.mContext, localUserManager, localUserInfo, localResources);
        }
      }
      break;
    case 1000:
    case -4:
    case -5:
    }
    while (true)
    {
      return localUidDetail;
      localUidDetail.label = localResources.getString(2131428783);
      localUidDetail.icon = localPackageManager.getDefaultActivityIcon();
      return localUidDetail;
      if (UserManager.supportsMultipleUsers());
      for (int i = 2131429125; ; i = 2131429124)
      {
        localUidDetail.label = localResources.getString(i);
        localUidDetail.icon = localPackageManager.getDefaultActivityIcon();
        return localUidDetail;
      }
      localUidDetail.label = localResources.getString(Utils.getTetheringLabel((ConnectivityManager)this.mContext.getSystemService("connectivity")));
      localUidDetail.icon = localPackageManager.getDefaultActivityIcon();
      return localUidDetail;
      String[] arrayOfString = localPackageManager.getPackagesForUid(paramInt);
      int j;
      if (arrayOfString != null)
      {
        j = arrayOfString.length;
        label287: if (j != 1)
          break label358;
      }
      try
      {
        ApplicationInfo localApplicationInfo2 = localPackageManager.getApplicationInfo(arrayOfString[0], 0);
        localUidDetail.label = localApplicationInfo2.loadLabel(localPackageManager).toString();
        localUidDetail.icon = localApplicationInfo2.loadIcon(localPackageManager);
        label329: 
        while (TextUtils.isEmpty(localUidDetail.label))
        {
          localUidDetail.label = Integer.toString(paramInt);
          return localUidDetail;
          j = 0;
          break label287;
          label358: if (j > 1)
          {
            localUidDetail.detailLabels = new CharSequence[j];
            for (int k = 0; k < j; k++)
            {
              String str = arrayOfString[k];
              PackageInfo localPackageInfo = localPackageManager.getPackageInfo(str, 0);
              ApplicationInfo localApplicationInfo1 = localPackageManager.getApplicationInfo(str, 0);
              localUidDetail.detailLabels[k] = localApplicationInfo1.loadLabel(localPackageManager).toString();
              if (localPackageInfo.sharedUserLabel != 0)
              {
                localUidDetail.label = localPackageManager.getText(str, localPackageInfo.sharedUserLabel, localPackageInfo.applicationInfo).toString();
                localUidDetail.icon = localApplicationInfo1.loadIcon(localPackageManager);
              }
            }
          }
        }
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException)
      {
        break label329;
      }
    }
  }

  public void clearCache()
  {
    synchronized (this.mUidDetailCache)
    {
      this.mUidDetailCache.clear();
      return;
    }
  }

  public UidDetail getUidDetail(int paramInt, boolean paramBoolean)
  {
    synchronized (this.mUidDetailCache)
    {
      UidDetail localUidDetail1 = (UidDetail)this.mUidDetailCache.get(paramInt);
      if (localUidDetail1 != null)
        return localUidDetail1;
    }
    if (!paramBoolean)
      return null;
    UidDetail localUidDetail2 = buildUidDetail(paramInt);
    synchronized (this.mUidDetailCache)
    {
      this.mUidDetailCache.put(paramInt, localUidDetail2);
      return localUidDetail2;
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.net.UidDetailProvider
 * JD-Core Version:    0.6.2
 */