package com.android.settings.deviceinfo;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver.Stub;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.content.pm.UserInfo;
import android.os.Environment;
import android.os.Environment.UserEnvironment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.storage.StorageVolume;
import android.util.Log;
import android.util.SparseLongArray;
import com.android.internal.app.IMediaContainerService;
import com.android.internal.app.IMediaContainerService.Stub;
import com.google.android.collect.Maps;
import com.google.android.collect.Sets;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class StorageMeasurement
{
  public static final ComponentName DEFAULT_CONTAINER_COMPONENT;
  static final boolean LOGV = Log.isLoggable("StorageMeasurement", 2);
  private static HashMap<StorageVolume, StorageMeasurement> sInstances = Maps.newHashMap();
  private static final Set<String> sMeasureMediaTypes;
  private long mAvailSize;
  List<FileInfo> mFileInfoForMisc;
  private final MeasurementHandler mHandler;
  private final boolean mIsInternal;
  private final boolean mIsPrimary;
  private volatile WeakReference<MeasurementReceiver> mReceiver;
  private long mTotalSize;
  private final StorageVolume mVolume;

  static
  {
    DEFAULT_CONTAINER_COMPONENT = new ComponentName("com.android.defcontainer", "com.android.defcontainer.DefaultContainerService");
    String[] arrayOfString = new String[10];
    arrayOfString[0] = Environment.DIRECTORY_DCIM;
    arrayOfString[1] = Environment.DIRECTORY_MOVIES;
    arrayOfString[2] = Environment.DIRECTORY_PICTURES;
    arrayOfString[3] = Environment.DIRECTORY_MUSIC;
    arrayOfString[4] = Environment.DIRECTORY_ALARMS;
    arrayOfString[5] = Environment.DIRECTORY_NOTIFICATIONS;
    arrayOfString[6] = Environment.DIRECTORY_RINGTONES;
    arrayOfString[7] = Environment.DIRECTORY_PODCASTS;
    arrayOfString[8] = Environment.DIRECTORY_DOWNLOADS;
    arrayOfString[9] = "Android";
    sMeasureMediaTypes = Sets.newHashSet(arrayOfString);
  }

  private StorageMeasurement(Context paramContext, StorageVolume paramStorageVolume)
  {
    this.mVolume = paramStorageVolume;
    if (paramStorageVolume == null);
    for (boolean bool1 = true; ; bool1 = false)
    {
      this.mIsInternal = bool1;
      boolean bool2 = false;
      if (paramStorageVolume != null)
        bool2 = paramStorageVolume.isPrimary();
      this.mIsPrimary = bool2;
      HandlerThread localHandlerThread = new HandlerThread("MemoryMeasurement");
      localHandlerThread.start();
      this.mHandler = new MeasurementHandler(paramContext, localHandlerThread.getLooper());
      return;
    }
  }

  private static void addValue(SparseLongArray paramSparseLongArray, int paramInt, long paramLong)
  {
    paramSparseLongArray.put(paramInt, paramLong + paramSparseLongArray.get(paramInt));
  }

  private static long getDirectorySize(IMediaContainerService paramIMediaContainerService, File paramFile)
  {
    try
    {
      long l = paramIMediaContainerService.calculateDirectorySize(paramFile.toString());
      Log.d("StorageMeasurement", "getDirectorySize(" + paramFile + ") returned " + l);
      return l;
    }
    catch (Exception localException)
    {
      Log.w("StorageMeasurement", "Could not read memory from default container service for " + paramFile, localException);
    }
    return 0L;
  }

  public static StorageMeasurement getInstance(Context paramContext, StorageVolume paramStorageVolume)
  {
    synchronized (sInstances)
    {
      StorageMeasurement localStorageMeasurement = (StorageMeasurement)sInstances.get(paramStorageVolume);
      if (localStorageMeasurement == null)
      {
        localStorageMeasurement = new StorageMeasurement(paramContext.getApplicationContext(), paramStorageVolume);
        sInstances.put(paramStorageVolume, localStorageMeasurement);
      }
      return localStorageMeasurement;
    }
  }

  private long measureMisc(IMediaContainerService paramIMediaContainerService, File paramFile)
  {
    this.mFileInfoForMisc = new ArrayList();
    File[] arrayOfFile = paramFile.listFiles();
    if (arrayOfFile == null)
      return 0L;
    long l1 = 0L;
    long l2 = 0L;
    int i = arrayOfFile.length;
    int j = 0;
    if (j < i)
    {
      File localFile = arrayOfFile[j];
      String str1 = localFile.getAbsolutePath();
      String str2 = localFile.getName();
      if (sMeasureMediaTypes.contains(str2));
      while (true)
      {
        j++;
        break;
        if (localFile.isFile())
        {
          long l5 = localFile.length();
          List localList2 = this.mFileInfoForMisc;
          long l6 = l1 + 1L;
          localList2.add(new FileInfo(str1, l5, l1));
          l2 += l5;
          l1 = l6;
        }
        else if (localFile.isDirectory())
        {
          long l3 = getDirectorySize(paramIMediaContainerService, localFile);
          List localList1 = this.mFileInfoForMisc;
          long l4 = l1 + 1L;
          localList1.add(new FileInfo(str1, l3, l1));
          l2 += l3;
          l1 = l4;
        }
      }
    }
    Collections.sort(this.mFileInfoForMisc);
    return l2;
  }

  private void sendExactUpdate(MeasurementDetails paramMeasurementDetails)
  {
    if (this.mReceiver != null);
    for (MeasurementReceiver localMeasurementReceiver = (MeasurementReceiver)this.mReceiver.get(); localMeasurementReceiver == null; localMeasurementReceiver = null)
    {
      if (LOGV)
        Log.i("StorageMeasurement", "measurements dropped because receiver is null! wasted effort");
      return;
    }
    localMeasurementReceiver.updateDetails(this, paramMeasurementDetails);
  }

  private void sendInternalApproximateUpdate()
  {
    if (this.mReceiver != null);
    for (MeasurementReceiver localMeasurementReceiver = (MeasurementReceiver)this.mReceiver.get(); localMeasurementReceiver == null; localMeasurementReceiver = null)
      return;
    localMeasurementReceiver.updateApproximate(this, this.mTotalSize, this.mAvailSize);
  }

  public void cleanUp()
  {
    this.mReceiver = null;
    this.mHandler.removeMessages(1);
    this.mHandler.sendEmptyMessage(3);
  }

  public void invalidate()
  {
    this.mHandler.sendEmptyMessage(5);
  }

  public void measure()
  {
    if (!this.mHandler.hasMessages(1))
      this.mHandler.sendEmptyMessage(1);
  }

  public void setReceiver(MeasurementReceiver paramMeasurementReceiver)
  {
    if ((this.mReceiver == null) || (this.mReceiver.get() == null))
      this.mReceiver = new WeakReference(paramMeasurementReceiver);
  }

  static class FileInfo
    implements Comparable<FileInfo>
  {
    final String mFileName;
    final long mId;
    final long mSize;

    FileInfo(String paramString, long paramLong1, long paramLong2)
    {
      this.mFileName = paramString;
      this.mSize = paramLong1;
      this.mId = paramLong2;
    }

    public int compareTo(FileInfo paramFileInfo)
    {
      if ((this == paramFileInfo) || (this.mSize == paramFileInfo.mSize))
        return 0;
      if (this.mSize < paramFileInfo.mSize)
        return 1;
      return -1;
    }

    public String toString()
    {
      return this.mFileName + " : " + this.mSize + ", id:" + this.mId;
    }
  }

  public static class MeasurementDetails
  {
    public long appsSize;
    public long availSize;
    public long cacheSize;
    public HashMap<String, Long> mediaSize = Maps.newHashMap();
    public long miscSize;
    public long totalSize;
    public SparseLongArray usersSize = new SparseLongArray();
  }

  private class MeasurementHandler extends Handler
  {
    private volatile boolean mBound = false;
    private StorageMeasurement.MeasurementDetails mCached;
    private final WeakReference<Context> mContext;
    private final ServiceConnection mDefContainerConn = new ServiceConnection()
    {
      public void onServiceConnected(ComponentName paramAnonymousComponentName, IBinder paramAnonymousIBinder)
      {
        IMediaContainerService localIMediaContainerService = IMediaContainerService.Stub.asInterface(paramAnonymousIBinder);
        StorageMeasurement.MeasurementHandler.access$102(StorageMeasurement.MeasurementHandler.this, localIMediaContainerService);
        StorageMeasurement.MeasurementHandler.access$202(StorageMeasurement.MeasurementHandler.this, true);
        StorageMeasurement.MeasurementHandler.this.sendMessage(StorageMeasurement.MeasurementHandler.this.obtainMessage(2, localIMediaContainerService));
      }

      public void onServiceDisconnected(ComponentName paramAnonymousComponentName)
      {
        StorageMeasurement.MeasurementHandler.access$202(StorageMeasurement.MeasurementHandler.this, false);
        StorageMeasurement.MeasurementHandler.this.removeMessages(2);
      }
    };
    private IMediaContainerService mDefaultContainer;
    private Object mLock = new Object();

    public MeasurementHandler(Context paramLooper, Looper arg3)
    {
      super();
      this.mContext = new WeakReference(paramLooper);
    }

    private void measureApproximateStorage(IMediaContainerService paramIMediaContainerService)
    {
      String str;
      if (StorageMeasurement.this.mVolume != null)
        str = StorageMeasurement.this.mVolume.getPath();
      try
      {
        while (true)
        {
          long[] arrayOfLong = paramIMediaContainerService.getFileSystemStats(str);
          StorageMeasurement.access$502(StorageMeasurement.this, arrayOfLong[0]);
          StorageMeasurement.access$602(StorageMeasurement.this, arrayOfLong[1]);
          StorageMeasurement.this.sendInternalApproximateUpdate();
          return;
          str = Environment.getDataDirectory().getPath();
        }
      }
      catch (Exception localException)
      {
        while (true)
          Log.w("StorageMeasurement", "Problem in container service", localException);
      }
    }

    private void measureExactStorage(IMediaContainerService paramIMediaContainerService)
    {
      if (this.mContext != null);
      for (Context localContext = (Context)this.mContext.get(); localContext == null; localContext = null)
        return;
      StorageMeasurement.MeasurementDetails localMeasurementDetails = new StorageMeasurement.MeasurementDetails();
      Message localMessage = obtainMessage(4, localMeasurementDetails);
      localMeasurementDetails.totalSize = StorageMeasurement.this.mTotalSize;
      localMeasurementDetails.availSize = StorageMeasurement.this.mAvailSize;
      List localList1 = ((UserManager)localContext.getSystemService("user")).getUsers();
      int i = ActivityManager.getCurrentUser();
      Environment.UserEnvironment localUserEnvironment1 = new Environment.UserEnvironment(i);
      if (((StorageMeasurement.this.mIsInternal) && (Environment.isExternalStorageEmulated())) || (StorageMeasurement.this.mIsPrimary));
      for (int j = 1; j != 0; j = 0)
      {
        Iterator localIterator4 = StorageMeasurement.sMeasureMediaTypes.iterator();
        while (localIterator4.hasNext())
        {
          String str = (String)localIterator4.next();
          long l2 = StorageMeasurement.getDirectorySize(paramIMediaContainerService, localUserEnvironment1.getExternalStoragePublicDirectory(str));
          localMeasurementDetails.mediaSize.put(str, Long.valueOf(l2));
        }
      }
      if (j != 0)
        if (!StorageMeasurement.this.mIsInternal)
          break label308;
      label308: for (File localFile = localUserEnvironment1.getExternalStorageDirectory(); ; localFile = StorageMeasurement.this.mVolume.getPathFile())
      {
        localMeasurementDetails.miscSize = StorageMeasurement.this.measureMisc(paramIMediaContainerService, localFile);
        Iterator localIterator1 = localList1.iterator();
        while (localIterator1.hasNext())
        {
          UserInfo localUserInfo2 = (UserInfo)localIterator1.next();
          Environment.UserEnvironment localUserEnvironment2 = new Environment.UserEnvironment(localUserInfo2.id);
          long l1 = StorageMeasurement.getDirectorySize(paramIMediaContainerService, localUserEnvironment2.getExternalStorageDirectory());
          StorageMeasurement.addValue(localMeasurementDetails.usersSize, localUserInfo2.id, l1);
        }
      }
      PackageManager localPackageManager = localContext.getPackageManager();
      if ((StorageMeasurement.this.mIsInternal) || (StorageMeasurement.this.mIsPrimary))
      {
        List localList2 = localPackageManager.getInstalledApplications(8704);
        int k = localList1.size() * localList2.size();
        StorageMeasurement.StatsObserver localStatsObserver = new StorageMeasurement.StatsObserver(StorageMeasurement.this.mIsInternal, localMeasurementDetails, i, localMessage, k);
        Iterator localIterator2 = localList1.iterator();
        while (localIterator2.hasNext())
        {
          UserInfo localUserInfo1 = (UserInfo)localIterator2.next();
          Iterator localIterator3 = localList2.iterator();
          while (localIterator3.hasNext())
            localPackageManager.getPackageSizeInfo(((ApplicationInfo)localIterator3.next()).packageName, localUserInfo1.id, localStatsObserver);
        }
      }
      localMessage.sendToTarget();
    }

    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default:
      case 1:
        Context localContext2;
        do
        {
          return;
          if (this.mCached != null)
          {
            StorageMeasurement.this.sendExactUpdate(this.mCached);
            return;
          }
          WeakReference localWeakReference2 = this.mContext;
          localContext2 = null;
          if (localWeakReference2 != null)
            localContext2 = (Context)this.mContext.get();
        }
        while (localContext2 == null);
        while (true)
        {
          synchronized (this.mLock)
          {
            if (this.mBound)
            {
              removeMessages(3);
              sendMessage(obtainMessage(2, this.mDefaultContainer));
              return;
            }
          }
          localContext2.bindServiceAsUser(new Intent().setComponent(StorageMeasurement.DEFAULT_CONTAINER_COMPONENT), this.mDefContainerConn, 1, UserHandle.OWNER);
        }
      case 2:
        IMediaContainerService localIMediaContainerService = (IMediaContainerService)paramMessage.obj;
        measureApproximateStorage(localIMediaContainerService);
        measureExactStorage(localIMediaContainerService);
        return;
      case 3:
        Context localContext1;
        synchronized (this.mLock)
        {
          if (!this.mBound)
            break label257;
          WeakReference localWeakReference1 = this.mContext;
          localContext1 = null;
          if (localWeakReference1 != null)
            localContext1 = (Context)this.mContext.get();
          if (localContext1 == null)
            return;
        }
        this.mBound = false;
        localContext1.unbindService(this.mDefContainerConn);
        return;
      case 4:
        label257: this.mCached = ((StorageMeasurement.MeasurementDetails)paramMessage.obj);
        StorageMeasurement.this.sendExactUpdate(this.mCached);
        return;
      case 5:
      }
      this.mCached = null;
    }
  }

  public static abstract interface MeasurementReceiver
  {
    public abstract void updateApproximate(StorageMeasurement paramStorageMeasurement, long paramLong1, long paramLong2);

    public abstract void updateDetails(StorageMeasurement paramStorageMeasurement, StorageMeasurement.MeasurementDetails paramMeasurementDetails);
  }

  private static class StatsObserver extends IPackageStatsObserver.Stub
  {
    private final int mCurrentUser;
    private final StorageMeasurement.MeasurementDetails mDetails;
    private final Message mFinished;
    private final boolean mIsInternal;
    private int mRemaining;

    public StatsObserver(boolean paramBoolean, StorageMeasurement.MeasurementDetails paramMeasurementDetails, int paramInt1, Message paramMessage, int paramInt2)
    {
      this.mIsInternal = paramBoolean;
      this.mDetails = paramMeasurementDetails;
      this.mCurrentUser = paramInt1;
      this.mFinished = paramMessage;
      this.mRemaining = paramInt2;
    }

    private void addStatsLocked(PackageStats paramPackageStats)
    {
      if (this.mIsInternal)
      {
        long l1 = paramPackageStats.codeSize;
        long l2 = paramPackageStats.dataSize;
        long l3 = paramPackageStats.cacheSize;
        if (Environment.isExternalStorageEmulated())
        {
          l1 += paramPackageStats.externalCodeSize + paramPackageStats.externalObbSize;
          l2 += paramPackageStats.externalDataSize + paramPackageStats.externalMediaSize;
          l3 += paramPackageStats.externalCacheSize;
        }
        if (paramPackageStats.userHandle == this.mCurrentUser)
        {
          StorageMeasurement.MeasurementDetails localMeasurementDetails4 = this.mDetails;
          localMeasurementDetails4.appsSize = (l1 + localMeasurementDetails4.appsSize);
          StorageMeasurement.MeasurementDetails localMeasurementDetails5 = this.mDetails;
          localMeasurementDetails5.appsSize = (l2 + localMeasurementDetails5.appsSize);
        }
        StorageMeasurement.addValue(this.mDetails.usersSize, paramPackageStats.userHandle, l2);
        StorageMeasurement.MeasurementDetails localMeasurementDetails3 = this.mDetails;
        localMeasurementDetails3.cacheSize = (l3 + localMeasurementDetails3.cacheSize);
        return;
      }
      StorageMeasurement.MeasurementDetails localMeasurementDetails1 = this.mDetails;
      localMeasurementDetails1.appsSize += paramPackageStats.externalCodeSize + paramPackageStats.externalDataSize + paramPackageStats.externalMediaSize + paramPackageStats.externalObbSize;
      StorageMeasurement.MeasurementDetails localMeasurementDetails2 = this.mDetails;
      localMeasurementDetails2.cacheSize += paramPackageStats.externalCacheSize;
    }

    public void onGetStatsCompleted(PackageStats paramPackageStats, boolean paramBoolean)
    {
      StorageMeasurement.MeasurementDetails localMeasurementDetails = this.mDetails;
      if (paramBoolean);
      try
      {
        addStatsLocked(paramPackageStats);
        int i = -1 + this.mRemaining;
        this.mRemaining = i;
        if (i == 0)
          this.mFinished.sendToTarget();
        return;
      }
      finally
      {
      }
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.deviceinfo.StorageMeasurement
 * JD-Core Version:    0.6.2
 */