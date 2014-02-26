package com.android.settings.applications;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManagerNative;
import android.app.ActivityThread;
import android.app.IActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.ComponentInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ServiceInfo;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.ConstantState;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.format.Formatter;
import android.util.Log;
import android.util.SparseArray;
import com.android.settings.users.UserUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class RunningState
{
  static Object sGlobalLock = new Object();
  static RunningState sInstance;
  final ArrayList<ProcessItem> mAllProcessItems = new ArrayList();
  final ActivityManager mAm;
  final Context mApplicationContext;
  final Comparator<MergedItem> mBackgroundComparator = new Comparator()
  {
    public int compare(RunningState.MergedItem paramAnonymousMergedItem1, RunningState.MergedItem paramAnonymousMergedItem2)
    {
      int i = 1;
      if (paramAnonymousMergedItem1.mUserId != paramAnonymousMergedItem2.mUserId)
        if (paramAnonymousMergedItem1.mUserId != RunningState.this.mMyUserId);
      label163: label183: label189: label194: label207: label240: label246: 
      do
      {
        ActivityManager.RunningAppProcessInfo localRunningAppProcessInfo1;
        ActivityManager.RunningAppProcessInfo localRunningAppProcessInfo2;
        do
        {
          do
          {
            do
            {
              do
              {
                return -1;
                if (paramAnonymousMergedItem2.mUserId == RunningState.this.mMyUserId)
                  return i;
              }
              while (paramAnonymousMergedItem1.mUserId < paramAnonymousMergedItem2.mUserId);
              return i;
              if (paramAnonymousMergedItem1.mProcess != paramAnonymousMergedItem2.mProcess)
                break;
              if (paramAnonymousMergedItem1.mLabel == paramAnonymousMergedItem2.mLabel)
                return 0;
            }
            while (paramAnonymousMergedItem1.mLabel == null);
            return paramAnonymousMergedItem1.mLabel.compareTo(paramAnonymousMergedItem2.mLabel);
          }
          while (paramAnonymousMergedItem1.mProcess == null);
          if (paramAnonymousMergedItem2.mProcess == null)
            return i;
          localRunningAppProcessInfo1 = paramAnonymousMergedItem1.mProcess.mRunningProcessInfo;
          localRunningAppProcessInfo2 = paramAnonymousMergedItem2.mProcess.mRunningProcessInfo;
          int j;
          int k;
          if (localRunningAppProcessInfo1.importance >= 400)
          {
            j = i;
            if (localRunningAppProcessInfo2.importance < 400)
              break label183;
            k = i;
            if (j == k)
              break label194;
            if (j == 0)
              break label189;
          }
          while (true)
          {
            return i;
            j = 0;
            break;
            k = 0;
            break label163;
            i = -1;
          }
          int m;
          if ((0x4 & localRunningAppProcessInfo1.flags) != 0)
          {
            m = i;
            if ((0x4 & localRunningAppProcessInfo2.flags) == 0)
              break label240;
          }
          for (int n = i; ; n = 0)
          {
            if (m == n)
              break label246;
            if (m != 0)
              break;
            return i;
            m = 0;
            break label207;
          }
          if (localRunningAppProcessInfo1.lru == localRunningAppProcessInfo2.lru)
            break;
        }
        while (localRunningAppProcessInfo1.lru < localRunningAppProcessInfo2.lru);
        return i;
        if (paramAnonymousMergedItem1.mProcess.mLabel == paramAnonymousMergedItem2.mProcess.mLabel)
          return 0;
        if (paramAnonymousMergedItem1.mProcess.mLabel == null)
          return i;
      }
      while (paramAnonymousMergedItem2.mProcess.mLabel == null);
      return paramAnonymousMergedItem1.mProcess.mLabel.compareTo(paramAnonymousMergedItem2.mProcess.mLabel);
    }
  };
  final BackgroundHandler mBackgroundHandler;
  ArrayList<MergedItem> mBackgroundItems = new ArrayList();
  long mBackgroundProcessMemory;
  final HandlerThread mBackgroundThread;
  long mForegroundProcessMemory;
  final Handler mHandler = new Handler()
  {
    int mNextUpdate = 0;

    public void handleMessage(Message paramAnonymousMessage)
    {
      switch (paramAnonymousMessage.what)
      {
      default:
      case 3:
      case 4:
      }
      do
      {
        return;
        if (paramAnonymousMessage.arg1 != 0);
        for (int i = 2; ; i = 1)
        {
          this.mNextUpdate = i;
          return;
        }
        synchronized (RunningState.this.mLock)
        {
          if (!RunningState.this.mResumed)
            return;
        }
        removeMessages(4);
        sendMessageDelayed(obtainMessage(4), 1000L);
      }
      while (RunningState.this.mRefreshUiListener == null);
      RunningState.this.mRefreshUiListener.onRefreshUi(this.mNextUpdate);
      this.mNextUpdate = 0;
    }
  };
  boolean mHaveData;
  final InterestingConfigChanges mInterestingConfigChanges = new InterestingConfigChanges();
  final ArrayList<ProcessItem> mInterestingProcesses = new ArrayList();
  ArrayList<BaseItem> mItems = new ArrayList();
  final Object mLock = new Object();
  ArrayList<MergedItem> mMergedItems = new ArrayList();
  final int mMyUserId;
  int mNumBackgroundProcesses;
  int mNumForegroundProcesses;
  int mNumServiceProcesses;
  final SparseArray<MergedItem> mOtherUserBackgroundItems = new SparseArray();
  final SparseArray<MergedItem> mOtherUserMergedItems = new SparseArray();
  final PackageManager mPm;
  final ArrayList<ProcessItem> mProcessItems = new ArrayList();
  OnRefreshUiListener mRefreshUiListener;
  boolean mResumed;
  final SparseArray<ProcessItem> mRunningProcesses = new SparseArray();
  int mSequence = 0;
  final ServiceProcessComparator mServiceProcessComparator = new ServiceProcessComparator();
  long mServiceProcessMemory;
  final SparseArray<HashMap<String, ProcessItem>> mServiceProcessesByName = new SparseArray();
  final SparseArray<ProcessItem> mServiceProcessesByPid = new SparseArray();
  final SparseArray<AppProcessInfo> mTmpAppProcesses = new SparseArray();
  final UserManager mUm;
  ArrayList<MergedItem> mUserBackgroundItems = new ArrayList();
  final SparseArray<UserState> mUsers = new SparseArray();
  boolean mWatchingBackgroundItems;

  private RunningState(Context paramContext)
  {
    this.mApplicationContext = paramContext.getApplicationContext();
    this.mAm = ((ActivityManager)this.mApplicationContext.getSystemService("activity"));
    this.mPm = this.mApplicationContext.getPackageManager();
    this.mUm = ((UserManager)this.mApplicationContext.getSystemService("user"));
    this.mMyUserId = UserHandle.myUserId();
    this.mResumed = false;
    this.mBackgroundThread = new HandlerThread("RunningState:Background");
    this.mBackgroundThread.start();
    this.mBackgroundHandler = new BackgroundHandler(this.mBackgroundThread.getLooper());
  }

  private void addOtherUserItem(Context paramContext, ArrayList<MergedItem> paramArrayList, SparseArray<MergedItem> paramSparseArray, MergedItem paramMergedItem)
  {
    MergedItem localMergedItem = (MergedItem)paramSparseArray.get(paramMergedItem.mUserId);
    int i;
    label69: UserInfo localUserInfo;
    if ((localMergedItem == null) || (localMergedItem.mCurSeq != this.mSequence))
    {
      i = 1;
      if (i != 0)
      {
        if (localMergedItem != null)
          break label249;
        localMergedItem = new MergedItem(paramMergedItem.mUserId);
        paramSparseArray.put(paramMergedItem.mUserId, localMergedItem);
        localMergedItem.mCurSeq = this.mSequence;
        UserState localUserState = (UserState)this.mUsers.get(paramMergedItem.mUserId);
        localMergedItem.mUser = localUserState;
        if (localUserState == null)
        {
          localMergedItem.mUser = new UserState();
          localUserInfo = this.mUm.getUserInfo(paramMergedItem.mUserId);
          localMergedItem.mUser.mInfo = localUserInfo;
          if (localUserInfo != null)
            localMergedItem.mUser.mIcon = UserUtils.getUserIcon(paramContext, this.mUm, localUserInfo, paramContext.getResources());
          if (localUserInfo == null)
            break label260;
        }
      }
    }
    label260: for (String str = localUserInfo.name; ; str = null)
    {
      if (str == null)
        str = Integer.toString(localUserInfo.id);
      localMergedItem.mUser.mLabel = paramContext.getResources().getString(2131428478, new Object[] { str });
      paramArrayList.add(localMergedItem);
      localMergedItem.mChildren.add(paramMergedItem);
      return;
      i = 0;
      break;
      label249: localMergedItem.mChildren.clear();
      break label69;
    }
  }

  static RunningState getInstance(Context paramContext)
  {
    synchronized (sGlobalLock)
    {
      if (sInstance == null)
        sInstance = new RunningState(paramContext);
      RunningState localRunningState = sInstance;
      return localRunningState;
    }
  }

  private boolean isInterestingProcess(ActivityManager.RunningAppProcessInfo paramRunningAppProcessInfo)
  {
    if ((0x1 & paramRunningAppProcessInfo.flags) != 0);
    while (((0x2 & paramRunningAppProcessInfo.flags) == 0) && (paramRunningAppProcessInfo.importance >= 100) && (paramRunningAppProcessInfo.importance < 170) && (paramRunningAppProcessInfo.importanceReasonCode == 0))
      return true;
    return false;
  }

  static CharSequence makeLabel(PackageManager paramPackageManager, String paramString, PackageItemInfo paramPackageItemInfo)
  {
    Object localObject;
    if ((paramPackageItemInfo != null) && ((paramPackageItemInfo.labelRes != 0) || (paramPackageItemInfo.nonLocalizedLabel != null)))
    {
      localObject = paramPackageItemInfo.loadLabel(paramPackageManager);
      if (localObject == null);
    }
    int i;
    do
    {
      return localObject;
      localObject = paramString;
      i = ((String)localObject).lastIndexOf('.');
    }
    while (i < 0);
    return ((String)localObject).substring(i + 1, ((String)localObject).length());
  }

  private void reset()
  {
    this.mServiceProcessesByName.clear();
    this.mServiceProcessesByPid.clear();
    this.mInterestingProcesses.clear();
    this.mRunningProcesses.clear();
    this.mProcessItems.clear();
    this.mAllProcessItems.clear();
    this.mUsers.clear();
  }

  private boolean update(Context paramContext, ActivityManager paramActivityManager)
  {
    PackageManager localPackageManager = paramContext.getPackageManager();
    this.mSequence = (1 + this.mSequence);
    boolean bool1 = false;
    List localList1 = paramActivityManager.getRunningServices(100);
    int i;
    int j;
    label43: ActivityManager.RunningServiceInfo localRunningServiceInfo3;
    if (localList1 != null)
    {
      i = localList1.size();
      j = 0;
      if (j >= i)
        break label138;
      localRunningServiceInfo3 = (ActivityManager.RunningServiceInfo)localList1.get(j);
      if ((localRunningServiceInfo3.started) || (localRunningServiceInfo3.clientLabel != 0))
        break label108;
      localList1.remove(j);
      j--;
      i--;
    }
    while (true)
    {
      j++;
      break label43;
      i = 0;
      break;
      label108: if ((0x8 & localRunningServiceInfo3.flags) != 0)
      {
        localList1.remove(j);
        j--;
        i--;
      }
    }
    label138: List localList2 = paramActivityManager.getRunningAppProcesses();
    if (localList2 != null);
    for (int k = localList2.size(); ; k = 0)
    {
      this.mTmpAppProcesses.clear();
      for (int m = 0; m < k; m++)
      {
        ActivityManager.RunningAppProcessInfo localRunningAppProcessInfo2 = (ActivityManager.RunningAppProcessInfo)localList2.get(m);
        SparseArray localSparseArray2 = this.mTmpAppProcesses;
        int i62 = localRunningAppProcessInfo2.pid;
        AppProcessInfo localAppProcessInfo4 = new AppProcessInfo(localRunningAppProcessInfo2);
        localSparseArray2.put(i62, localAppProcessInfo4);
      }
    }
    for (int n = 0; n < i; n++)
    {
      ActivityManager.RunningServiceInfo localRunningServiceInfo2 = (ActivityManager.RunningServiceInfo)localList1.get(n);
      if ((localRunningServiceInfo2.restarting == 0L) && (localRunningServiceInfo2.pid > 0))
      {
        AppProcessInfo localAppProcessInfo3 = (AppProcessInfo)this.mTmpAppProcesses.get(localRunningServiceInfo2.pid);
        if (localAppProcessInfo3 != null)
        {
          localAppProcessInfo3.hasServices = true;
          if (localRunningServiceInfo2.foreground)
            localAppProcessInfo3.hasForegroundServices = true;
        }
      }
    }
    int i1 = 0;
    if (i1 < i)
    {
      ActivityManager.RunningServiceInfo localRunningServiceInfo1 = (ActivityManager.RunningServiceInfo)localList1.get(i1);
      if ((localRunningServiceInfo1.restarting == 0L) && (localRunningServiceInfo1.pid > 0))
      {
        AppProcessInfo localAppProcessInfo1 = (AppProcessInfo)this.mTmpAppProcesses.get(localRunningServiceInfo1.pid);
        if ((localAppProcessInfo1 != null) && (!localAppProcessInfo1.hasForegroundServices) && (localAppProcessInfo1.info.importance < 300))
          for (AppProcessInfo localAppProcessInfo2 = (AppProcessInfo)this.mTmpAppProcesses.get(localAppProcessInfo1.info.importanceReasonPid); ; localAppProcessInfo2 = (AppProcessInfo)this.mTmpAppProcesses.get(localAppProcessInfo2.info.importanceReasonPid))
          {
            int i61 = 0;
            if (localAppProcessInfo2 != null)
            {
              if ((localAppProcessInfo2.hasServices) || (isInterestingProcess(localAppProcessInfo2.info)))
                i61 = 1;
            }
            else
            {
              if (i61 == 0)
                break label495;
              i1++;
              break;
            }
          }
      }
      label495: HashMap localHashMap2 = (HashMap)this.mServiceProcessesByName.get(localRunningServiceInfo1.uid);
      if (localHashMap2 == null)
      {
        localHashMap2 = new HashMap();
        this.mServiceProcessesByName.put(localRunningServiceInfo1.uid, localHashMap2);
      }
      String str2 = localRunningServiceInfo1.process;
      ProcessItem localProcessItem12 = (ProcessItem)localHashMap2.get(str2);
      if (localProcessItem12 == null)
      {
        bool1 = true;
        int i60 = localRunningServiceInfo1.uid;
        String str3 = localRunningServiceInfo1.process;
        localProcessItem12 = new ProcessItem(paramContext, i60, str3);
        String str4 = localRunningServiceInfo1.process;
        localHashMap2.put(str4, localProcessItem12);
      }
      if (localProcessItem12.mCurSeq != this.mSequence)
        if (localRunningServiceInfo1.restarting != 0L)
          break label752;
      label752: for (int i56 = localRunningServiceInfo1.pid; ; i56 = 0)
      {
        int i57 = localProcessItem12.mPid;
        if (i56 != i57)
        {
          bool1 = true;
          if (localProcessItem12.mPid != i56)
          {
            if (localProcessItem12.mPid != 0)
              this.mServiceProcessesByPid.remove(localProcessItem12.mPid);
            if (i56 != 0)
              this.mServiceProcessesByPid.put(i56, localProcessItem12);
            int i59 = i56;
            localProcessItem12.mPid = i59;
          }
        }
        localProcessItem12.mDependentProcesses.clear();
        int i58 = this.mSequence;
        localProcessItem12.mCurSeq = i58;
        bool1 |= localProcessItem12.updateService(paramContext, localRunningServiceInfo1);
        break;
      }
    }
    int i2 = 0;
    if (i2 < k)
    {
      ActivityManager.RunningAppProcessInfo localRunningAppProcessInfo1 = (ActivityManager.RunningAppProcessInfo)localList2.get(i2);
      ProcessItem localProcessItem11 = (ProcessItem)this.mServiceProcessesByPid.get(localRunningAppProcessInfo1.pid);
      if (localProcessItem11 == null)
      {
        localProcessItem11 = (ProcessItem)this.mRunningProcesses.get(localRunningAppProcessInfo1.pid);
        if (localProcessItem11 == null)
        {
          bool1 = true;
          int i54 = localRunningAppProcessInfo1.uid;
          String str1 = localRunningAppProcessInfo1.processName;
          localProcessItem11 = new ProcessItem(paramContext, i54, str1);
          int i55 = localRunningAppProcessInfo1.pid;
          localProcessItem11.mPid = i55;
          this.mRunningProcesses.put(localRunningAppProcessInfo1.pid, localProcessItem11);
        }
        localProcessItem11.mDependentProcesses.clear();
      }
      if (isInterestingProcess(localRunningAppProcessInfo1))
      {
        if (!this.mInterestingProcesses.contains(localProcessItem11))
        {
          bool1 = true;
          this.mInterestingProcesses.add(localProcessItem11);
        }
        int i53 = this.mSequence;
        localProcessItem11.mCurSeq = i53;
        localProcessItem11.mInteresting = true;
        localProcessItem11.ensureLabel(localPackageManager);
      }
      while (true)
      {
        int i52 = this.mSequence;
        localProcessItem11.mRunningSeq = i52;
        localProcessItem11.mRunningProcessInfo = localRunningAppProcessInfo1;
        i2++;
        break;
        localProcessItem11.mInteresting = false;
      }
    }
    int i3 = this.mRunningProcesses.size();
    int i4 = 0;
    while (i4 < i3)
    {
      ProcessItem localProcessItem9 = (ProcessItem)this.mRunningProcesses.valueAt(i4);
      if (localProcessItem9.mRunningSeq == this.mSequence)
      {
        int i51 = localProcessItem9.mRunningProcessInfo.importanceReasonPid;
        if (i51 != 0)
        {
          ProcessItem localProcessItem10 = (ProcessItem)this.mServiceProcessesByPid.get(i51);
          if (localProcessItem10 == null)
            localProcessItem10 = (ProcessItem)this.mRunningProcesses.get(i51);
          if (localProcessItem10 != null)
            localProcessItem10.mDependentProcesses.put(localProcessItem9.mPid, localProcessItem9);
        }
        while (true)
        {
          i4++;
          break;
          localProcessItem9.mClient = null;
        }
      }
      bool1 = true;
      this.mRunningProcesses.remove(this.mRunningProcesses.keyAt(i4));
      i3--;
    }
    int i5 = this.mInterestingProcesses.size();
    for (int i6 = 0; i6 < i5; i6++)
    {
      ProcessItem localProcessItem8 = (ProcessItem)this.mInterestingProcesses.get(i6);
      if ((!localProcessItem8.mInteresting) || (this.mRunningProcesses.get(localProcessItem8.mPid) == null))
      {
        bool1 = true;
        this.mInterestingProcesses.remove(i6);
        i6--;
        i5--;
      }
    }
    int i7 = this.mServiceProcessesByPid.size();
    for (int i8 = 0; i8 < i7; i8++)
    {
      ProcessItem localProcessItem7 = (ProcessItem)this.mServiceProcessesByPid.valueAt(i8);
      if (localProcessItem7.mCurSeq == this.mSequence)
        bool1 |= localProcessItem7.buildDependencyChain(paramContext, localPackageManager, this.mSequence);
    }
    ArrayList localArrayList1 = null;
    for (int i9 = 0; ; i9++)
    {
      int i10 = this.mServiceProcessesByName.size();
      if (i9 >= i10)
        break;
      HashMap localHashMap1 = (HashMap)this.mServiceProcessesByName.valueAt(i9);
      Iterator localIterator5 = localHashMap1.values().iterator();
      while (localIterator5.hasNext())
      {
        ProcessItem localProcessItem6 = (ProcessItem)localIterator5.next();
        if (localProcessItem6.mCurSeq == this.mSequence)
        {
          localProcessItem6.ensureLabel(localPackageManager);
          if (localProcessItem6.mPid == 0)
            localProcessItem6.mDependentProcesses.clear();
          Iterator localIterator6 = localProcessItem6.mServices.values().iterator();
          while (localIterator6.hasNext())
            if (((ServiceItem)localIterator6.next()).mCurSeq != this.mSequence)
            {
              bool1 = true;
              localIterator6.remove();
            }
        }
        else
        {
          bool1 = true;
          localIterator5.remove();
          if (localHashMap1.size() == 0)
          {
            if (localArrayList1 == null)
              localArrayList1 = new ArrayList();
            Integer localInteger = Integer.valueOf(this.mServiceProcessesByName.keyAt(i9));
            localArrayList1.add(localInteger);
          }
          if (localProcessItem6.mPid != 0)
            this.mServiceProcessesByPid.remove(localProcessItem6.mPid);
        }
      }
    }
    if (localArrayList1 != null)
      for (int i48 = 0; ; i48++)
      {
        int i49 = localArrayList1.size();
        if (i48 >= i49)
          break;
        int i50 = ((Integer)localArrayList1.get(i48)).intValue();
        this.mServiceProcessesByName.remove(i50);
      }
    ArrayList localArrayList3;
    ArrayList localArrayList4;
    if (bool1)
    {
      ArrayList localArrayList2 = new ArrayList();
      for (int i11 = 0; ; i11++)
      {
        int i12 = this.mServiceProcessesByName.size();
        if (i11 >= i12)
          break;
        Iterator localIterator3 = ((HashMap)this.mServiceProcessesByName.valueAt(i11)).values().iterator();
        while (localIterator3.hasNext())
        {
          ProcessItem localProcessItem5 = (ProcessItem)localIterator3.next();
          localProcessItem5.mIsSystem = false;
          localProcessItem5.mIsStarted = true;
          localProcessItem5.mActiveSince = 9223372036854775807L;
          Iterator localIterator4 = localProcessItem5.mServices.values().iterator();
          while (localIterator4.hasNext())
          {
            ServiceItem localServiceItem3 = (ServiceItem)localIterator4.next();
            if ((localServiceItem3.mServiceInfo != null) && ((0x1 & localServiceItem3.mServiceInfo.applicationInfo.flags) != 0))
              localProcessItem5.mIsSystem = true;
            if ((localServiceItem3.mRunningService != null) && (localServiceItem3.mRunningService.clientLabel != 0))
            {
              localProcessItem5.mIsStarted = false;
              if (localProcessItem5.mActiveSince > localServiceItem3.mRunningService.activeSince)
                localProcessItem5.mActiveSince = localServiceItem3.mRunningService.activeSince;
            }
          }
          localArrayList2.add(localProcessItem5);
        }
      }
      Collections.sort(localArrayList2, this.mServiceProcessComparator);
      localArrayList3 = new ArrayList();
      localArrayList4 = new ArrayList();
      this.mProcessItems.clear();
      int i13 = 0;
      int i14 = localArrayList2.size();
      if (i13 < i14)
      {
        ProcessItem localProcessItem4 = (ProcessItem)localArrayList2.get(i13);
        localProcessItem4.mNeedDivider = false;
        int i44 = this.mProcessItems.size();
        localProcessItem4.addDependentProcesses(localArrayList3, this.mProcessItems);
        localArrayList3.add(localProcessItem4);
        if (localProcessItem4.mPid > 0)
          this.mProcessItems.add(localProcessItem4);
        MergedItem localMergedItem7 = null;
        boolean bool2 = false;
        Iterator localIterator1 = localProcessItem4.mServices.values().iterator();
        while (localIterator1.hasNext())
        {
          ServiceItem localServiceItem2 = (ServiceItem)localIterator1.next();
          localServiceItem2.mNeedDivider = bool2;
          bool2 = true;
          localArrayList3.add(localServiceItem2);
          if (localServiceItem2.mMergedItem != null)
          {
            if (localMergedItem7 != null)
            {
              MergedItem localMergedItem8 = localServiceItem2.mMergedItem;
              if (localMergedItem7 == localMergedItem8);
            }
            localMergedItem7 = localServiceItem2.mMergedItem;
          }
        }
        if ((0 == 0) || (localMergedItem7 == null) || (localMergedItem7.mServices.size() != localProcessItem4.mServices.size()))
        {
          int i45 = localProcessItem4.mUserId;
          localMergedItem7 = new MergedItem(i45);
          Iterator localIterator2 = localProcessItem4.mServices.values().iterator();
          while (localIterator2.hasNext())
          {
            ServiceItem localServiceItem1 = (ServiceItem)localIterator2.next();
            localMergedItem7.mServices.add(localServiceItem1);
            localServiceItem1.mMergedItem = localMergedItem7;
          }
          localMergedItem7.mProcess = localProcessItem4;
          localMergedItem7.mOtherProcesses.clear();
          for (int i46 = i44; ; i46++)
          {
            int i47 = -1 + this.mProcessItems.size();
            if (i46 >= i47)
              break;
            localMergedItem7.mOtherProcesses.add(this.mProcessItems.get(i46));
          }
        }
        localMergedItem7.update(paramContext, false);
        if (localMergedItem7.mUserId != this.mMyUserId)
          addOtherUserItem(paramContext, localArrayList4, this.mOtherUserMergedItems, localMergedItem7);
        while (true)
        {
          i13++;
          break;
          localArrayList4.add(localMergedItem7);
        }
      }
      int i15 = this.mInterestingProcesses.size();
      int i16 = 0;
      if (i16 < i15)
      {
        ProcessItem localProcessItem3 = (ProcessItem)this.mInterestingProcesses.get(i16);
        if ((localProcessItem3.mClient == null) && (localProcessItem3.mServices.size() <= 0))
        {
          if (localProcessItem3.mMergedItem == null)
          {
            localProcessItem3.mMergedItem = new MergedItem(localProcessItem3.mUserId);
            localProcessItem3.mMergedItem.mProcess = localProcessItem3;
          }
          localProcessItem3.mMergedItem.update(paramContext, false);
          if (localProcessItem3.mMergedItem.mUserId == this.mMyUserId)
            break label2400;
          addOtherUserItem(paramContext, localArrayList4, this.mOtherUserMergedItems, localProcessItem3.mMergedItem);
        }
        while (true)
        {
          this.mProcessItems.add(localProcessItem3);
          i16++;
          break;
          label2400: localArrayList4.add(0, localProcessItem3.mMergedItem);
        }
      }
      int i17 = this.mOtherUserMergedItems.size();
      for (int i18 = 0; i18 < i17; i18++)
      {
        MergedItem localMergedItem6 = (MergedItem)this.mOtherUserMergedItems.valueAt(i18);
        if (localMergedItem6.mCurSeq == this.mSequence)
          localMergedItem6.update(paramContext, false);
      }
    }
    int i19;
    int i20;
    int i21;
    while (true)
    {
      ProcessItem localProcessItem2;
      synchronized (this.mLock)
      {
        this.mItems = localArrayList3;
        this.mMergedItems = localArrayList4;
        this.mAllProcessItems.clear();
        this.mAllProcessItems.addAll(this.mProcessItems);
        i19 = 0;
        i20 = 0;
        i21 = 0;
        int i22 = this.mRunningProcesses.size();
        int i23 = 0;
        if (i23 >= i22)
          break;
        localProcessItem2 = (ProcessItem)this.mRunningProcesses.valueAt(i23);
        if (localProcessItem2.mCurSeq == this.mSequence)
          break label2689;
        if (localProcessItem2.mRunningProcessInfo.importance >= 400)
        {
          i19++;
          this.mAllProcessItems.add(localProcessItem2);
          i23++;
        }
      }
      if (localProcessItem2.mRunningProcessInfo.importance <= 200)
      {
        i20++;
        this.mAllProcessItems.add(localProcessItem2);
      }
      else
      {
        Log.i("RunningState", "Unknown non-service process: " + localProcessItem2.mProcessName + " #" + localProcessItem2.mPid);
        continue;
        label2689: i21++;
      }
    }
    long l1 = 0L;
    long l2 = 0L;
    long l3 = 0L;
    Object localObject3 = null;
    int i24 = 0;
    try
    {
      int i33 = this.mAllProcessItems.size();
      arrayOfInt = new int[i33];
      for (int i34 = 0; i34 < i33; i34++)
        arrayOfInt[i34] = ((ProcessItem)this.mAllProcessItems.get(i34)).mPid;
      arrayOfLong = ActivityManagerNative.getDefault().getProcessPss(arrayOfInt);
      i35 = 0;
      i36 = 0;
      localObject7 = null;
    }
    catch (RemoteException localRemoteException1)
    {
      while (true)
      {
        try
        {
          int[] arrayOfInt;
          long[] arrayOfLong;
          int i35;
          int i37 = arrayOfInt.length;
          if (i36 < i37)
          {
            ProcessItem localProcessItem1 = (ProcessItem)this.mAllProcessItems.get(i36);
            bool1 |= localProcessItem1.updateSize(paramContext, arrayOfLong[i36], this.mSequence);
            if (localProcessItem1.mCurSeq == this.mSequence)
            {
              l3 += localProcessItem1.mSize;
              localObject3 = localObject7;
              continue;
            }
            if (localProcessItem1.mRunningProcessInfo.importance >= 400)
            {
              l1 += localProcessItem1.mSize;
              if (localObject7 != null)
              {
                int i38 = localProcessItem1.mUserId;
                localMergedItem4 = new MergedItem(i38);
                localProcessItem1.mMergedItem = localMergedItem4;
                localProcessItem1.mMergedItem.mProcess = localProcessItem1;
                if (localMergedItem4.mUserId != this.mMyUserId)
                {
                  i39 = 1;
                  i24 |= i39;
                  localObject7.add(localMergedItem4);
                  localObject3 = localObject7;
                  localMergedItem4.update(paramContext, true);
                  localMergedItem4.updateSize(paramContext);
                  i35++;
                  continue;
                }
                int i39 = 0;
                continue;
              }
              if ((i35 >= this.mBackgroundItems.size()) || (((MergedItem)this.mBackgroundItems.get(i35)).mProcess != localProcessItem1))
              {
                localObject3 = new ArrayList(i19);
                int i40 = 0;
                if (i40 < i35)
                {
                  MergedItem localMergedItem5 = (MergedItem)this.mBackgroundItems.get(i40);
                  if (localMergedItem5.mUserId == this.mMyUserId)
                    break label3655;
                  i43 = 1;
                  i24 |= i43;
                  ((ArrayList)localObject3).add(localMergedItem5);
                  i40++;
                  continue;
                }
                int i41 = localProcessItem1.mUserId;
                localMergedItem4 = new MergedItem(i41);
                localProcessItem1.mMergedItem = localMergedItem4;
                localProcessItem1.mMergedItem.mProcess = localProcessItem1;
                if (localMergedItem4.mUserId != this.mMyUserId)
                {
                  i42 = 1;
                  i24 |= i42;
                  ((ArrayList)localObject3).add(localMergedItem4);
                  continue;
                  localRemoteException1 = localRemoteException1;
                  if ((localObject3 != null) || (this.mBackgroundItems.size() <= i19))
                    continue;
                  localObject3 = new ArrayList(i19);
                  int i31 = 0;
                  if (i31 >= i19)
                    continue;
                  MergedItem localMergedItem3 = (MergedItem)this.mBackgroundItems.get(i31);
                  if (localMergedItem3.mUserId == this.mMyUserId)
                    continue;
                  i32 = 1;
                  i24 |= i32;
                  ((ArrayList)localObject3).add(localMergedItem3);
                  i31++;
                  continue;
                }
                int i42 = 0;
                continue;
              }
              MergedItem localMergedItem4 = (MergedItem)this.mBackgroundItems.get(i35);
              localObject3 = localObject7;
              continue;
            }
            else
            {
              if (localProcessItem1.mRunningProcessInfo.importance > 200)
                continue;
              long l4 = localProcessItem1.mSize;
              l2 += l4;
              localObject3 = localObject7;
              continue;
            }
          }
          else
          {
            localObject3 = localObject7;
            continue;
          }
          int i32 = 0;
          continue;
          Object localObject4 = null;
          if (localObject3 != null)
          {
            if (i24 == 0)
              localObject4 = localObject3;
          }
          else
          {
            int i25 = 0;
            int i26 = this.mMergedItems.size();
            if (i25 >= i26)
              continue;
            ((MergedItem)this.mMergedItems.get(i25)).updateSize(paramContext);
            i25++;
            continue;
          }
          localObject4 = new ArrayList();
          int i27 = ((ArrayList)localObject3).size();
          int i28 = 0;
          if (i28 < i27)
          {
            MergedItem localMergedItem2 = (MergedItem)((ArrayList)localObject3).get(i28);
            if (localMergedItem2.mUserId != this.mMyUserId)
            {
              SparseArray localSparseArray1 = this.mOtherUserBackgroundItems;
              addOtherUserItem(paramContext, (ArrayList)localObject4, localSparseArray1, localMergedItem2);
              i28++;
              continue;
            }
            ((ArrayList)localObject4).add(localMergedItem2);
            continue;
          }
          int i29 = this.mOtherUserBackgroundItems.size();
          int i30 = 0;
          if (i30 < i29)
          {
            MergedItem localMergedItem1 = (MergedItem)this.mOtherUserBackgroundItems.valueAt(i30);
            if (localMergedItem1.mCurSeq == this.mSequence)
            {
              localMergedItem1.update(paramContext, true);
              localMergedItem1.updateSize(paramContext);
            }
            i30++;
            continue;
          }
          continue;
          synchronized (this.mLock)
          {
            this.mNumBackgroundProcesses = i19;
            this.mNumForegroundProcesses = i20;
            this.mNumServiceProcesses = i21;
            this.mBackgroundProcessMemory = l1;
            this.mForegroundProcessMemory = l2;
            this.mServiceProcessMemory = l3;
            if (localObject3 != null)
            {
              this.mBackgroundItems = ((ArrayList)localObject3);
              this.mUserBackgroundItems = ((ArrayList)localObject4);
              if (this.mWatchingBackgroundItems)
                bool1 = true;
            }
            if (!this.mHaveData)
            {
              this.mHaveData = true;
              this.mLock.notifyAll();
            }
            return bool1;
          }
        }
        catch (RemoteException localRemoteException2)
        {
          int i36;
          localObject3 = localObject7;
          continue;
          localObject3 = localObject7;
          i36++;
          Object localObject7 = localObject3;
        }
        continue;
        label3655: int i43 = 0;
      }
    }
  }

  ArrayList<MergedItem> getCurrentBackgroundItems()
  {
    synchronized (this.mLock)
    {
      ArrayList localArrayList = this.mUserBackgroundItems;
      return localArrayList;
    }
  }

  ArrayList<MergedItem> getCurrentMergedItems()
  {
    synchronized (this.mLock)
    {
      ArrayList localArrayList = this.mMergedItems;
      return localArrayList;
    }
  }

  boolean hasData()
  {
    synchronized (this.mLock)
    {
      boolean bool = this.mHaveData;
      return bool;
    }
  }

  void pause()
  {
    synchronized (this.mLock)
    {
      this.mResumed = false;
      this.mRefreshUiListener = null;
      this.mHandler.removeMessages(4);
      return;
    }
  }

  void resume(OnRefreshUiListener paramOnRefreshUiListener)
  {
    synchronized (this.mLock)
    {
      this.mResumed = true;
      this.mRefreshUiListener = paramOnRefreshUiListener;
      if (this.mInterestingConfigChanges.applyNewConfig(this.mApplicationContext.getResources()))
      {
        this.mHaveData = false;
        this.mBackgroundHandler.removeMessages(1);
        this.mBackgroundHandler.removeMessages(2);
        this.mBackgroundHandler.sendEmptyMessage(1);
      }
      if (!this.mBackgroundHandler.hasMessages(2))
        this.mBackgroundHandler.sendEmptyMessage(2);
      this.mHandler.sendEmptyMessage(4);
      return;
    }
  }

  void setWatchingBackgroundItems(boolean paramBoolean)
  {
    synchronized (this.mLock)
    {
      this.mWatchingBackgroundItems = paramBoolean;
      return;
    }
  }

  void updateNow()
  {
    synchronized (this.mLock)
    {
      this.mBackgroundHandler.removeMessages(2);
      this.mBackgroundHandler.sendEmptyMessage(2);
      return;
    }
  }

  void waitForData()
  {
    synchronized (this.mLock)
    {
      while (true)
      {
        boolean bool = this.mHaveData;
        if (bool)
          break;
        try
        {
          this.mLock.wait(0L);
        }
        catch (InterruptedException localInterruptedException)
        {
        }
      }
      return;
    }
  }

  static class AppProcessInfo
  {
    boolean hasForegroundServices;
    boolean hasServices;
    final ActivityManager.RunningAppProcessInfo info;

    AppProcessInfo(ActivityManager.RunningAppProcessInfo paramRunningAppProcessInfo)
    {
      this.info = paramRunningAppProcessInfo;
    }
  }

  final class BackgroundHandler extends Handler
  {
    public BackgroundHandler(Looper arg2)
    {
      super();
    }

    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default:
        return;
      case 1:
        RunningState.this.reset();
        return;
      case 2:
      }
      synchronized (RunningState.this.mLock)
      {
        if (!RunningState.this.mResumed)
          return;
      }
      Message localMessage = RunningState.this.mHandler.obtainMessage(3);
      if (RunningState.this.update(RunningState.this.mApplicationContext, RunningState.this.mAm));
      for (int i = 1; ; i = 0)
      {
        localMessage.arg1 = i;
        RunningState.this.mHandler.sendMessage(localMessage);
        removeMessages(2);
        sendMessageDelayed(obtainMessage(2), 2000L);
        return;
      }
    }
  }

  static class BaseItem
  {
    long mActiveSince;
    boolean mBackground;
    int mCurSeq;
    String mCurSizeStr;
    String mDescription;
    CharSequence mDisplayLabel;
    final boolean mIsProcess;
    String mLabel;
    boolean mNeedDivider;
    PackageItemInfo mPackageInfo;
    long mSize;
    String mSizeStr;
    final int mUserId;

    public BaseItem(boolean paramBoolean, int paramInt)
    {
      this.mIsProcess = paramBoolean;
      this.mUserId = paramInt;
    }

    public Drawable loadIcon(Context paramContext, RunningState paramRunningState)
    {
      if (this.mPackageInfo != null)
        return this.mPackageInfo.loadIcon(paramRunningState.mPm);
      return null;
    }
  }

  static class MergedItem extends RunningState.BaseItem
  {
    final ArrayList<MergedItem> mChildren = new ArrayList();
    private int mLastNumProcesses = -1;
    private int mLastNumServices = -1;
    final ArrayList<RunningState.ProcessItem> mOtherProcesses = new ArrayList();
    RunningState.ProcessItem mProcess;
    final ArrayList<RunningState.ServiceItem> mServices = new ArrayList();
    RunningState.UserState mUser;

    MergedItem(int paramInt)
    {
      super(paramInt);
    }

    private void setDescription(Context paramContext, int paramInt1, int paramInt2)
    {
      int i;
      if ((this.mLastNumProcesses != paramInt1) || (this.mLastNumServices != paramInt2))
      {
        this.mLastNumProcesses = paramInt1;
        this.mLastNumServices = paramInt2;
        i = 2131428480;
        if (paramInt1 == 1)
          break label93;
        if (paramInt2 == 1)
          break label86;
        i = 2131428483;
      }
      while (true)
      {
        Resources localResources = paramContext.getResources();
        Object[] arrayOfObject = new Object[2];
        arrayOfObject[0] = Integer.valueOf(paramInt1);
        arrayOfObject[1] = Integer.valueOf(paramInt2);
        this.mDescription = localResources.getString(i, arrayOfObject);
        return;
        label86: i = 2131428482;
        continue;
        label93: if (paramInt2 != 1)
          i = 2131428481;
      }
    }

    public Drawable loadIcon(Context paramContext, RunningState paramRunningState)
    {
      if (this.mUser == null)
        return super.loadIcon(paramContext, paramRunningState);
      if (this.mUser.mIcon != null)
      {
        Drawable.ConstantState localConstantState = this.mUser.mIcon.getConstantState();
        if (localConstantState == null)
          return this.mUser.mIcon;
        return localConstantState.newDrawable();
      }
      return paramContext.getResources().getDrawable(17302328);
    }

    boolean update(Context paramContext, boolean paramBoolean)
    {
      this.mBackground = paramBoolean;
      if (this.mUser != null)
      {
        this.mPackageInfo = ((MergedItem)this.mChildren.get(0)).mProcess.mPackageInfo;
        if (this.mUser != null);
        int k;
        int m;
        for (String str = this.mUser.mLabel; ; str = null)
        {
          this.mLabel = str;
          this.mDisplayLabel = this.mLabel;
          k = 0;
          m = 0;
          this.mActiveSince = -1L;
          for (int n = 0; n < this.mChildren.size(); n++)
          {
            MergedItem localMergedItem = (MergedItem)this.mChildren.get(n);
            k += localMergedItem.mLastNumProcesses;
            m += localMergedItem.mLastNumServices;
            if ((localMergedItem.mActiveSince >= 0L) && (this.mActiveSince < localMergedItem.mActiveSince))
              this.mActiveSince = localMergedItem.mActiveSince;
          }
        }
        if (!this.mBackground)
          setDescription(paramContext, k, m);
        return false;
      }
      this.mPackageInfo = this.mProcess.mPackageInfo;
      this.mDisplayLabel = this.mProcess.mDisplayLabel;
      this.mLabel = this.mProcess.mLabel;
      if (!this.mBackground)
        if (this.mProcess.mPid <= 0)
          break label333;
      label333: for (int j = 1; ; j = 0)
      {
        setDescription(paramContext, j + this.mOtherProcesses.size(), this.mServices.size());
        this.mActiveSince = -1L;
        for (int i = 0; i < this.mServices.size(); i++)
        {
          RunningState.ServiceItem localServiceItem = (RunningState.ServiceItem)this.mServices.get(i);
          if ((localServiceItem.mActiveSince >= 0L) && (this.mActiveSince < localServiceItem.mActiveSince))
            this.mActiveSince = localServiceItem.mActiveSince;
        }
        break;
      }
    }

    boolean updateSize(Context paramContext)
    {
      if (this.mUser != null)
      {
        this.mSize = 0L;
        for (int j = 0; j < this.mChildren.size(); j++)
        {
          MergedItem localMergedItem = (MergedItem)this.mChildren.get(j);
          localMergedItem.updateSize(paramContext);
          this.mSize += localMergedItem.mSize;
        }
      }
      this.mSize = this.mProcess.mSize;
      for (int i = 0; i < this.mOtherProcesses.size(); i++)
        this.mSize += ((RunningState.ProcessItem)this.mOtherProcesses.get(i)).mSize;
      String str = Formatter.formatShortFileSize(paramContext, this.mSize);
      if (!str.equals(this.mSizeStr))
        this.mSizeStr = str;
      return false;
    }
  }

  static abstract interface OnRefreshUiListener
  {
    public abstract void onRefreshUi(int paramInt);
  }

  static class ProcessItem extends RunningState.BaseItem
  {
    long mActiveSince;
    ProcessItem mClient;
    final SparseArray<ProcessItem> mDependentProcesses = new SparseArray();
    boolean mInteresting;
    boolean mIsStarted;
    boolean mIsSystem;
    int mLastNumDependentProcesses;
    RunningState.MergedItem mMergedItem;
    int mPid;
    final String mProcessName;
    ActivityManager.RunningAppProcessInfo mRunningProcessInfo;
    int mRunningSeq;
    final HashMap<ComponentName, RunningState.ServiceItem> mServices = new HashMap();
    final int mUid;

    public ProcessItem(Context paramContext, int paramInt, String paramString)
    {
      super(UserHandle.getUserId(paramInt));
      this.mDescription = paramContext.getResources().getString(2131428477, new Object[] { paramString });
      this.mUid = paramInt;
      this.mProcessName = paramString;
    }

    void addDependentProcesses(ArrayList<RunningState.BaseItem> paramArrayList, ArrayList<ProcessItem> paramArrayList1)
    {
      int i = this.mDependentProcesses.size();
      for (int j = 0; j < i; j++)
      {
        ProcessItem localProcessItem = (ProcessItem)this.mDependentProcesses.valueAt(j);
        localProcessItem.addDependentProcesses(paramArrayList, paramArrayList1);
        paramArrayList.add(localProcessItem);
        if (localProcessItem.mPid > 0)
          paramArrayList1.add(localProcessItem);
      }
    }

    boolean buildDependencyChain(Context paramContext, PackageManager paramPackageManager, int paramInt)
    {
      int i = this.mDependentProcesses.size();
      boolean bool = false;
      for (int j = 0; j < i; j++)
      {
        ProcessItem localProcessItem = (ProcessItem)this.mDependentProcesses.valueAt(j);
        if (localProcessItem.mClient != this)
        {
          bool = true;
          localProcessItem.mClient = this;
        }
        localProcessItem.mCurSeq = paramInt;
        localProcessItem.ensureLabel(paramPackageManager);
        bool |= localProcessItem.buildDependencyChain(paramContext, paramPackageManager, paramInt);
      }
      if (this.mLastNumDependentProcesses != this.mDependentProcesses.size())
      {
        bool = true;
        this.mLastNumDependentProcesses = this.mDependentProcesses.size();
      }
      return bool;
    }

    void ensureLabel(PackageManager paramPackageManager)
    {
      if (this.mLabel != null)
        return;
      try
      {
        ApplicationInfo localApplicationInfo3 = paramPackageManager.getApplicationInfo(this.mProcessName, 8192);
        if (localApplicationInfo3.uid == this.mUid)
        {
          this.mDisplayLabel = localApplicationInfo3.loadLabel(paramPackageManager);
          this.mLabel = this.mDisplayLabel.toString();
          this.mPackageInfo = localApplicationInfo3;
          return;
        }
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException1)
      {
        String[] arrayOfString = paramPackageManager.getPackagesForUid(this.mUid);
        if (arrayOfString.length == 1)
          try
          {
            ApplicationInfo localApplicationInfo2 = paramPackageManager.getApplicationInfo(arrayOfString[0], 8192);
            this.mDisplayLabel = localApplicationInfo2.loadLabel(paramPackageManager);
            this.mLabel = this.mDisplayLabel.toString();
            this.mPackageInfo = localApplicationInfo2;
            return;
          }
          catch (PackageManager.NameNotFoundException localNameNotFoundException4)
          {
          }
        int i = arrayOfString.length;
        int j = 0;
        while (j < i)
        {
          String str = arrayOfString[j];
          try
          {
            PackageInfo localPackageInfo = paramPackageManager.getPackageInfo(str, 0);
            if (localPackageInfo.sharedUserLabel != 0)
            {
              CharSequence localCharSequence = paramPackageManager.getText(str, localPackageInfo.sharedUserLabel, localPackageInfo.applicationInfo);
              if (localCharSequence != null)
              {
                this.mDisplayLabel = localCharSequence;
                this.mLabel = localCharSequence.toString();
                this.mPackageInfo = localPackageInfo.applicationInfo;
                return;
              }
            }
          }
          catch (PackageManager.NameNotFoundException localNameNotFoundException3)
          {
            j++;
          }
        }
        if (this.mServices.size() > 0)
        {
          this.mPackageInfo = ((RunningState.ServiceItem)this.mServices.values().iterator().next()).mServiceInfo.applicationInfo;
          this.mDisplayLabel = this.mPackageInfo.loadLabel(paramPackageManager);
          this.mLabel = this.mDisplayLabel.toString();
          return;
        }
        try
        {
          ApplicationInfo localApplicationInfo1 = paramPackageManager.getApplicationInfo(arrayOfString[0], 8192);
          this.mDisplayLabel = localApplicationInfo1.loadLabel(paramPackageManager);
          this.mLabel = this.mDisplayLabel.toString();
          this.mPackageInfo = localApplicationInfo1;
          return;
        }
        catch (PackageManager.NameNotFoundException localNameNotFoundException2)
        {
        }
      }
    }

    boolean updateService(Context paramContext, ActivityManager.RunningServiceInfo paramRunningServiceInfo)
    {
      PackageManager localPackageManager = paramContext.getPackageManager();
      RunningState.ServiceItem localServiceItem = (RunningState.ServiceItem)this.mServices.get(paramRunningServiceInfo.service);
      boolean bool = false;
      String str2;
      long l;
      if (localServiceItem == null)
      {
        bool = true;
        localServiceItem = new RunningState.ServiceItem(this.mUserId);
        localServiceItem.mRunningService = paramRunningServiceInfo;
        try
        {
          localServiceItem.mServiceInfo = ActivityThread.getPackageManager().getServiceInfo(paramRunningServiceInfo.service, 8192, UserHandle.getUserId(paramRunningServiceInfo.uid));
          if (localServiceItem.mServiceInfo == null)
          {
            Log.d("RunningService", "getServiceInfo returned null for: " + paramRunningServiceInfo.service);
            return false;
          }
        }
        catch (RemoteException localRemoteException)
        {
          localServiceItem.mDisplayLabel = RunningState.makeLabel(localPackageManager, localServiceItem.mRunningService.service.getClassName(), localServiceItem.mServiceInfo);
          if (this.mDisplayLabel == null)
            break label318;
        }
        str2 = this.mDisplayLabel.toString();
        this.mLabel = str2;
        localServiceItem.mPackageInfo = localServiceItem.mServiceInfo.applicationInfo;
        this.mServices.put(paramRunningServiceInfo.service, localServiceItem);
      }
      else
      {
        localServiceItem.mCurSeq = this.mCurSeq;
        localServiceItem.mRunningService = paramRunningServiceInfo;
        if (paramRunningServiceInfo.restarting != 0L)
          break label324;
        l = paramRunningServiceInfo.activeSince;
        if (localServiceItem.mActiveSince != l)
        {
          localServiceItem.mActiveSince = l;
          bool = true;
        }
        if ((paramRunningServiceInfo.clientPackage == null) || (paramRunningServiceInfo.clientLabel == 0))
          break label343;
        if (localServiceItem.mShownAsStarted)
        {
          localServiceItem.mShownAsStarted = false;
          bool = true;
        }
      }
      while (true)
      {
        try
        {
          String str1 = localPackageManager.getResourcesForApplication(paramRunningServiceInfo.clientPackage).getString(paramRunningServiceInfo.clientLabel);
          localServiceItem.mDescription = paramContext.getResources().getString(2131428473, new Object[] { str1 });
          return bool;
          label318: str2 = null;
          break;
          label324: l = -1L;
        }
        catch (PackageManager.NameNotFoundException localNameNotFoundException)
        {
          localServiceItem.mDescription = null;
          continue;
        }
        label343: if (!localServiceItem.mShownAsStarted)
        {
          localServiceItem.mShownAsStarted = true;
          bool = true;
        }
        localServiceItem.mDescription = paramContext.getResources().getString(2131428472);
      }
    }

    boolean updateSize(Context paramContext, long paramLong, int paramInt)
    {
      this.mSize = (1024L * paramLong);
      if (this.mCurSeq == paramInt)
      {
        String str = Formatter.formatShortFileSize(paramContext, this.mSize);
        if (!str.equals(this.mSizeStr))
          this.mSizeStr = str;
      }
      return false;
    }
  }

  static class ServiceItem extends RunningState.BaseItem
  {
    RunningState.MergedItem mMergedItem;
    ActivityManager.RunningServiceInfo mRunningService;
    ServiceInfo mServiceInfo;
    boolean mShownAsStarted;

    public ServiceItem(int paramInt)
    {
      super(paramInt);
    }
  }

  class ServiceProcessComparator
    implements Comparator<RunningState.ProcessItem>
  {
    ServiceProcessComparator()
    {
    }

    public int compare(RunningState.ProcessItem paramProcessItem1, RunningState.ProcessItem paramProcessItem2)
    {
      int i = 1;
      if (paramProcessItem1.mUserId != paramProcessItem2.mUserId)
        if (paramProcessItem1.mUserId != RunningState.this.mMyUserId);
      do
      {
        do
        {
          do
          {
            return -1;
            if (paramProcessItem2.mUserId == RunningState.this.mMyUserId)
              return i;
          }
          while (paramProcessItem1.mUserId < paramProcessItem2.mUserId);
          return i;
          if (paramProcessItem1.mIsStarted == paramProcessItem2.mIsStarted)
            break;
        }
        while (paramProcessItem1.mIsStarted);
        return i;
        if (paramProcessItem1.mIsSystem != paramProcessItem2.mIsSystem)
        {
          if (paramProcessItem1.mIsSystem);
          while (true)
          {
            return i;
            i = -1;
          }
        }
        if (paramProcessItem1.mActiveSince == paramProcessItem2.mActiveSince)
          break;
      }
      while (paramProcessItem1.mActiveSince > paramProcessItem2.mActiveSince);
      return i;
      return 0;
    }
  }

  static class UserState
  {
    Drawable mIcon;
    UserInfo mInfo;
    String mLabel;
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.applications.RunningState
 * JD-Core Version:    0.6.2
 */