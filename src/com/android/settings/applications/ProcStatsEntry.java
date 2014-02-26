package com.android.settings.applications;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.ArrayMap;
import android.util.Log;
import com.android.internal.app.ProcessMap;
import com.android.internal.app.ProcessStats;
import com.android.internal.app.ProcessStats.PackageState;
import com.android.internal.app.ProcessStats.ProcessDataCollection;
import com.android.internal.app.ProcessStats.ProcessState;
import com.android.internal.app.ProcessStats.ServiceState;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class ProcStatsEntry
  implements Parcelable
{
  public static final Parcelable.Creator<ProcStatsEntry> CREATOR = new Parcelable.Creator()
  {
    public ProcStatsEntry createFromParcel(Parcel paramAnonymousParcel)
    {
      return new ProcStatsEntry(paramAnonymousParcel);
    }

    public ProcStatsEntry[] newArray(int paramAnonymousInt)
    {
      return new ProcStatsEntry[paramAnonymousInt];
    }
  };
  private static boolean DEBUG = false;
  final long mAvgPss;
  final long mAvgUss;
  String mBestTargetPackage;
  final long mDuration;
  final long mMaxPss;
  final long mMaxUss;
  final String mName;
  final String mPackage;
  final ArrayList<String> mPackages = new ArrayList();
  ArrayMap<String, ArrayList<Service>> mServices = new ArrayMap(1);
  public String mUiBaseLabel;
  public String mUiLabel;
  public String mUiPackage;
  public ApplicationInfo mUiTargetApp;
  final int mUid;
  final long mWeight;

  public ProcStatsEntry(Parcel paramParcel)
  {
    this.mPackage = paramParcel.readString();
    this.mUid = paramParcel.readInt();
    this.mName = paramParcel.readString();
    paramParcel.readStringList(this.mPackages);
    this.mDuration = paramParcel.readLong();
    this.mAvgPss = paramParcel.readLong();
    this.mMaxPss = paramParcel.readLong();
    this.mAvgUss = paramParcel.readLong();
    this.mMaxUss = paramParcel.readLong();
    this.mWeight = paramParcel.readLong();
    this.mBestTargetPackage = paramParcel.readString();
    int i = paramParcel.readInt();
    if (i > 0)
    {
      this.mServices.ensureCapacity(i);
      for (int j = 0; j < i; j++)
      {
        String str = paramParcel.readString();
        ArrayList localArrayList = new ArrayList();
        paramParcel.readTypedList(localArrayList, Service.CREATOR);
        this.mServices.append(str, localArrayList);
      }
    }
  }

  public ProcStatsEntry(ProcessStats.ProcessState paramProcessState, String paramString, ProcessStats.ProcessDataCollection paramProcessDataCollection, boolean paramBoolean1, boolean paramBoolean2)
  {
    ProcessStats.computeProcessData(paramProcessState, paramProcessDataCollection, 0L);
    this.mPackage = paramProcessState.mPackage;
    this.mUid = paramProcessState.mUid;
    this.mName = paramProcessState.mName;
    this.mPackages.add(paramString);
    this.mDuration = paramProcessDataCollection.totalTime;
    this.mAvgPss = paramProcessDataCollection.avgPss;
    this.mMaxPss = paramProcessDataCollection.maxPss;
    this.mAvgUss = paramProcessDataCollection.avgUss;
    this.mMaxUss = paramProcessDataCollection.maxUss;
    long l1;
    if (paramBoolean2)
    {
      l1 = this.mDuration;
      if (!paramBoolean1)
        break label214;
    }
    label214: for (long l2 = this.mAvgUss; ; l2 = this.mAvgPss)
    {
      this.mWeight = (l2 * l1);
      if (DEBUG)
        Log.d("ProcStatsEntry", "New proc entry " + paramProcessState.mName + ": dur=" + this.mDuration + " avgpss=" + this.mAvgPss + " weight=" + this.mWeight);
      return;
      l1 = 1L;
      break;
    }
  }

  public void addPackage(String paramString)
  {
    this.mPackages.add(paramString);
  }

  public void addService(ProcessStats.ServiceState paramServiceState)
  {
    ArrayList localArrayList = (ArrayList)this.mServices.get(paramServiceState.mPackage);
    if (localArrayList == null)
    {
      localArrayList = new ArrayList();
      this.mServices.put(paramServiceState.mPackage, localArrayList);
    }
    localArrayList.add(new Service(paramServiceState));
  }

  public int describeContents()
  {
    return 0;
  }

  public void evaluateTargetPackage(PackageManager paramPackageManager, ProcessStats paramProcessStats, ProcessStats.ProcessDataCollection paramProcessDataCollection, Comparator<ProcStatsEntry> paramComparator, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.mBestTargetPackage = null;
    if (this.mPackages.size() == 1)
    {
      if (DEBUG)
        Log.d("ProcStatsEntry", "Eval pkg of " + this.mName + ": single pkg " + (String)this.mPackages.get(0));
      this.mBestTargetPackage = ((String)this.mPackages.get(0));
    }
    ArrayList localArrayList1;
    label584: label863: label875: 
    do
    {
      return;
      localArrayList1 = new ArrayList();
      int i = 0;
      if (i < this.mPackages.size())
      {
        ProcessStats.PackageState localPackageState = (ProcessStats.PackageState)paramProcessStats.mPackages.get((String)this.mPackages.get(i), this.mUid);
        if (DEBUG)
          Log.d("ProcStatsEntry", "Eval pkg of " + this.mName + ", pkg " + (String)this.mPackages.get(i) + ":");
        if (localPackageState == null)
          Log.w("ProcStatsEntry", "No package state found for " + (String)this.mPackages.get(i) + "/" + this.mUid + " in process " + this.mName);
        while (true)
        {
          i++;
          break;
          ProcessStats.ProcessState localProcessState = (ProcessStats.ProcessState)localPackageState.mProcesses.get(this.mName);
          if (localProcessState == null)
            Log.w("ProcStatsEntry", "No process " + this.mName + " found in package state " + (String)this.mPackages.get(i) + "/" + this.mUid);
          else
            localArrayList1.add(new ProcStatsEntry(localProcessState, localPackageState.mPackageName, paramProcessDataCollection, paramBoolean1, paramBoolean2));
        }
      }
      if (localArrayList1.size() > 1)
      {
        Collections.sort(localArrayList1, paramComparator);
        if (((ProcStatsEntry)localArrayList1.get(0)).mWeight > 3L * ((ProcStatsEntry)localArrayList1.get(1)).mWeight)
        {
          if (DEBUG)
            Log.d("ProcStatsEntry", "Eval pkg of " + this.mName + ": best pkg " + ((ProcStatsEntry)localArrayList1.get(0)).mPackage + " weight " + ((ProcStatsEntry)localArrayList1.get(0)).mWeight + " better than " + ((ProcStatsEntry)localArrayList1.get(1)).mPackage + " weight " + ((ProcStatsEntry)localArrayList1.get(1)).mWeight);
          this.mBestTargetPackage = ((ProcStatsEntry)localArrayList1.get(0)).mPackage;
          return;
        }
        long l1 = ((ProcStatsEntry)localArrayList1.get(0)).mWeight;
        long l2 = -1L;
        int j = 0;
        if (j < localArrayList1.size())
        {
          if (((ProcStatsEntry)localArrayList1.get(j)).mWeight >= l1 / 2L)
            break label707;
          if (DEBUG)
            Log.d("ProcStatsEntry", "Eval pkg of " + this.mName + ": pkg " + ((ProcStatsEntry)localArrayList1.get(j)).mPackage + " weight " + ((ProcStatsEntry)localArrayList1.get(j)).mWeight + " too small");
        }
        while (true)
        {
          j++;
          break label584;
          break;
          try
          {
            if (paramPackageManager.getApplicationInfo(((ProcStatsEntry)localArrayList1.get(j)).mPackage, 0).icon != 0)
              break label863;
            if (!DEBUG)
              continue;
            Log.d("ProcStatsEntry", "Eval pkg of " + this.mName + ": pkg " + ((ProcStatsEntry)localArrayList1.get(j)).mPackage + " has no icon");
          }
          catch (PackageManager.NameNotFoundException localNameNotFoundException)
          {
          }
          if (DEBUG)
          {
            Log.d("ProcStatsEntry", "Eval pkg of " + this.mName + ": pkg " + ((ProcStatsEntry)localArrayList1.get(j)).mPackage + " failed finding app info");
            continue;
            int k = 0;
            int m = this.mServices.size();
            int n = k;
            Object localObject = null;
            long l3;
            int i1;
            int i2;
            if (n < m)
            {
              ArrayList localArrayList2 = (ArrayList)this.mServices.valueAt(k);
              if (((Service)localArrayList2.get(0)).mPackage.equals(((ProcStatsEntry)localArrayList1.get(j)).mPackage))
                localObject = localArrayList2;
            }
            else
            {
              l3 = 0L;
              if (localObject != null)
              {
                i1 = 0;
                i2 = localObject.size();
              }
            }
            while (true)
            {
              if (i1 < i2)
              {
                Service localService = (Service)localObject.get(i1);
                if (localService.mDuration > l3)
                {
                  if (DEBUG)
                    Log.d("ProcStatsEntry", "Eval pkg of " + this.mName + ": pkg " + ((ProcStatsEntry)localArrayList1.get(j)).mPackage + " service " + localService.mName + " run time is " + localService.mDuration);
                  l3 = localService.mDuration;
                }
              }
              else
              {
                if (l3 <= l2)
                  break label1188;
                if (DEBUG)
                  Log.d("ProcStatsEntry", "Eval pkg of " + this.mName + ": pkg " + ((ProcStatsEntry)localArrayList1.get(j)).mPackage + " new best run time " + l3);
                this.mBestTargetPackage = ((ProcStatsEntry)localArrayList1.get(j)).mPackage;
                l2 = l3;
                break;
                k++;
                break label875;
              }
              i1++;
            }
            if (DEBUG)
              Log.d("ProcStatsEntry", "Eval pkg of " + this.mName + ": pkg " + ((ProcStatsEntry)localArrayList1.get(j)).mPackage + " run time " + l3 + " not as good as last " + l2);
          }
        }
      }
    }
    while (localArrayList1.size() != 1);
    label707: this.mBestTargetPackage = ((ProcStatsEntry)localArrayList1.get(0)).mPackage;
    label1188:
  }

  public void retrieveUiData(PackageManager paramPackageManager)
  {
    this.mUiTargetApp = null;
    String str1 = this.mName;
    this.mUiBaseLabel = str1;
    this.mUiLabel = str1;
    this.mUiPackage = this.mBestTargetPackage;
    if (this.mUiPackage != null);
    try
    {
      this.mUiTargetApp = paramPackageManager.getApplicationInfo(this.mUiPackage, 41472);
      String str3 = this.mUiTargetApp.loadLabel(paramPackageManager).toString();
      this.mUiBaseLabel = str3;
      label89: String[] arrayOfString;
      int i;
      int j;
      if (this.mName.equals(this.mUiPackage))
      {
        this.mUiLabel = str3;
        if (this.mUiTargetApp == null)
        {
          arrayOfString = paramPackageManager.getPackagesForUid(this.mUid);
          if (arrayOfString == null)
            break label419;
          i = arrayOfString.length;
          j = 0;
        }
      }
      while (true)
        while (true)
        {
          String str2;
          if (j < i)
            str2 = arrayOfString[j];
          try
          {
            PackageInfo localPackageInfo = paramPackageManager.getPackageInfo(str2, 41472);
            if (localPackageInfo.sharedUserLabel != 0)
            {
              this.mUiTargetApp = localPackageInfo.applicationInfo;
              CharSequence localCharSequence = paramPackageManager.getText(str2, localPackageInfo.sharedUserLabel, localPackageInfo.applicationInfo);
              if (localCharSequence != null)
              {
                this.mUiBaseLabel = localCharSequence.toString();
                this.mUiLabel = (this.mUiBaseLabel + " (" + this.mName + ")");
                return;
                if (this.mName.startsWith(this.mUiPackage))
                {
                  int k = this.mUiPackage.length();
                  if (this.mName.length() > k)
                    k++;
                  this.mUiLabel = (str3 + " (" + this.mName.substring(k) + ")");
                  break;
                }
                this.mUiLabel = (str3 + " (" + this.mName + ")");
                break;
              }
              this.mUiBaseLabel = this.mUiTargetApp.loadLabel(paramPackageManager).toString();
              this.mUiLabel = (this.mUiBaseLabel + " (" + this.mName + ")");
              return;
            }
          }
          catch (PackageManager.NameNotFoundException localNameNotFoundException1)
          {
            j++;
          }
        }
      label419: Log.i("ProcStatsEntry", "No package for uid " + this.mUid);
      return;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException2)
    {
      break label89;
    }
  }

  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.mPackage);
    paramParcel.writeInt(this.mUid);
    paramParcel.writeString(this.mName);
    paramParcel.writeStringList(this.mPackages);
    paramParcel.writeLong(this.mDuration);
    paramParcel.writeLong(this.mAvgPss);
    paramParcel.writeLong(this.mMaxPss);
    paramParcel.writeLong(this.mAvgUss);
    paramParcel.writeLong(this.mMaxUss);
    paramParcel.writeLong(this.mWeight);
    paramParcel.writeString(this.mBestTargetPackage);
    int i = this.mServices.size();
    paramParcel.writeInt(i);
    for (int j = 0; j < i; j++)
    {
      paramParcel.writeString((String)this.mServices.keyAt(j));
      paramParcel.writeTypedList((List)this.mServices.valueAt(j));
    }
  }

  public static final class Service
    implements Parcelable
  {
    public static final Parcelable.Creator<Service> CREATOR = new Parcelable.Creator()
    {
      public ProcStatsEntry.Service createFromParcel(Parcel paramAnonymousParcel)
      {
        return new ProcStatsEntry.Service(paramAnonymousParcel);
      }

      public ProcStatsEntry.Service[] newArray(int paramAnonymousInt)
      {
        return new ProcStatsEntry.Service[paramAnonymousInt];
      }
    };
    final long mDuration;
    final String mName;
    final String mPackage;
    final String mProcess;

    public Service(Parcel paramParcel)
    {
      this.mPackage = paramParcel.readString();
      this.mName = paramParcel.readString();
      this.mProcess = paramParcel.readString();
      this.mDuration = paramParcel.readLong();
    }

    public Service(ProcessStats.ServiceState paramServiceState)
    {
      this.mPackage = paramServiceState.mPackage;
      this.mName = paramServiceState.mName;
      this.mProcess = paramServiceState.mProcessName;
      this.mDuration = ProcessStats.dumpSingleServiceTime(null, null, paramServiceState, 0, -1, 0L, 0L);
    }

    public int describeContents()
    {
      return 0;
    }

    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeString(this.mPackage);
      paramParcel.writeString(this.mName);
      paramParcel.writeString(this.mProcess);
      paramParcel.writeLong(this.mDuration);
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.applications.ProcStatsEntry
 * JD-Core Version:    0.6.2
 */