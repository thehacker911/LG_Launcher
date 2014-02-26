package com.android.settings.applications;

import android.app.AppOpsManager;
import android.app.AppOpsManager.OpEntry;
import android.app.AppOpsManager.PackageOps;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.SparseArray;
import java.io.File;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class AppOpsState
{
  public static final OpsTemplate[] ALL_TEMPLATES = arrayOfOpsTemplate;
  public static final Comparator<AppOpEntry> APP_OP_COMPARATOR = new Comparator()
  {
    private final Collator sCollator = Collator.getInstance();

    public int compare(AppOpsState.AppOpEntry paramAnonymousAppOpEntry1, AppOpsState.AppOpEntry paramAnonymousAppOpEntry2)
    {
      if (paramAnonymousAppOpEntry1.getSwitchOrder() != paramAnonymousAppOpEntry2.getSwitchOrder())
        if (paramAnonymousAppOpEntry1.getSwitchOrder() >= paramAnonymousAppOpEntry2.getSwitchOrder());
      do
      {
        do
        {
          return -1;
          return 1;
          if (paramAnonymousAppOpEntry1.isRunning() == paramAnonymousAppOpEntry2.isRunning())
            break;
        }
        while (paramAnonymousAppOpEntry1.isRunning());
        return 1;
        if (paramAnonymousAppOpEntry1.getTime() == paramAnonymousAppOpEntry2.getTime())
          break;
      }
      while (paramAnonymousAppOpEntry1.getTime() > paramAnonymousAppOpEntry2.getTime());
      return 1;
      return this.sCollator.compare(paramAnonymousAppOpEntry1.getAppEntry().getLabel(), paramAnonymousAppOpEntry2.getAppEntry().getLabel());
    }
  };
  public static final OpsTemplate DEVICE_TEMPLATE;
  public static final OpsTemplate LOCATION_TEMPLATE = new OpsTemplate(new int[] { 0, 1, 2, 10, 12, 41, 42 }, new boolean[] { 1, 1, 0, 0, 0, 0, 0 });
  public static final OpsTemplate MEDIA_TEMPLATE;
  public static final OpsTemplate MESSAGING_TEMPLATE;
  public static final OpsTemplate PERSONAL_TEMPLATE = new OpsTemplate(new int[] { 4, 5, 6, 7, 8, 9, 29, 30 }, new boolean[] { 1, 1, 1, 1, 1, 1, 0, 0 });
  final AppOpsManager mAppOps;
  final Context mContext;
  final CharSequence[] mOpLabels;
  final CharSequence[] mOpSummaries;
  final PackageManager mPm;

  static
  {
    MESSAGING_TEMPLATE = new OpsTemplate(new int[] { 14, 16, 17, 18, 19, 15, 20, 21, 22 }, new boolean[] { 1, 1, 1, 1, 1, 1, 1, 1, 1 });
    MEDIA_TEMPLATE = new OpsTemplate(new int[] { 3, 26, 27, 28, 31, 32, 33, 34, 35, 36, 37, 38, 39 }, new boolean[] { 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 });
    DEVICE_TEMPLATE = new OpsTemplate(new int[] { 11, 25, 13, 23, 24, 40 }, new boolean[] { 0, 1, 1, 1, 1, 1 });
    OpsTemplate[] arrayOfOpsTemplate = new OpsTemplate[5];
    arrayOfOpsTemplate[0] = LOCATION_TEMPLATE;
    arrayOfOpsTemplate[1] = PERSONAL_TEMPLATE;
    arrayOfOpsTemplate[2] = MESSAGING_TEMPLATE;
    arrayOfOpsTemplate[3] = MEDIA_TEMPLATE;
    arrayOfOpsTemplate[4] = DEVICE_TEMPLATE;
  }

  public AppOpsState(Context paramContext)
  {
    this.mContext = paramContext;
    this.mAppOps = ((AppOpsManager)paramContext.getSystemService("appops"));
    this.mPm = paramContext.getPackageManager();
    this.mOpSummaries = paramContext.getResources().getTextArray(2131165236);
    this.mOpLabels = paramContext.getResources().getTextArray(2131165237);
  }

  private void addOp(List<AppOpEntry> paramList, AppOpsManager.PackageOps paramPackageOps, AppEntry paramAppEntry, AppOpsManager.OpEntry paramOpEntry, boolean paramBoolean, int paramInt)
  {
    if ((paramBoolean) && (paramList.size() > 0))
    {
      AppOpEntry localAppOpEntry2 = (AppOpEntry)paramList.get(-1 + paramList.size());
      if (localAppOpEntry2.getAppEntry() == paramAppEntry)
      {
        int i;
        if (localAppOpEntry2.getTime() != 0L)
        {
          i = 1;
          if (paramOpEntry.getTime() == 0L)
            break label89;
        }
        label89: for (int j = 1; ; j = 0)
        {
          if (i != j)
            break label95;
          localAppOpEntry2.addOp(paramOpEntry);
          return;
          i = 0;
          break;
        }
      }
    }
    label95: AppOpEntry localAppOpEntry1 = paramAppEntry.getOpSwitch(paramOpEntry.getOp());
    if (localAppOpEntry1 != null)
    {
      localAppOpEntry1.addOp(paramOpEntry);
      return;
    }
    paramList.add(new AppOpEntry(paramPackageOps, paramOpEntry, paramAppEntry, paramInt));
  }

  private AppEntry getAppEntry(Context paramContext, HashMap<String, AppEntry> paramHashMap, String paramString, ApplicationInfo paramApplicationInfo)
  {
    AppEntry localAppEntry = (AppEntry)paramHashMap.get(paramString);
    if ((localAppEntry != null) || (paramApplicationInfo == null));
    try
    {
      ApplicationInfo localApplicationInfo = this.mPm.getApplicationInfo(paramString, 8704);
      paramApplicationInfo = localApplicationInfo;
      localAppEntry = new AppEntry(this, paramApplicationInfo);
      localAppEntry.loadLabel(paramContext);
      paramHashMap.put(paramString, localAppEntry);
      return localAppEntry;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      Log.w("AppOpsState", "Unable to find info for package " + paramString);
    }
    return null;
  }

  public List<AppOpEntry> buildState(OpsTemplate paramOpsTemplate)
  {
    return buildState(paramOpsTemplate, 0, null);
  }

  public List<AppOpEntry> buildState(OpsTemplate paramOpsTemplate, int paramInt, String paramString)
  {
    Context localContext = this.mContext;
    HashMap localHashMap = new HashMap();
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    ArrayList localArrayList3 = new ArrayList();
    int[] arrayOfInt = new int[43];
    for (int i = 0; ; i++)
    {
      int j = paramOpsTemplate.ops.length;
      if (i >= j)
        break;
      if (paramOpsTemplate.showPerms[i] != 0)
      {
        String str = AppOpsManager.opToPermission(paramOpsTemplate.ops[i]);
        if ((str != null) && (!localArrayList2.contains(str)))
        {
          localArrayList2.add(str);
          localArrayList3.add(Integer.valueOf(paramOpsTemplate.ops[i]));
          arrayOfInt[paramOpsTemplate.ops[i]] = i;
        }
      }
    }
    List localList;
    int i5;
    label171: AppOpsManager.PackageOps localPackageOps2;
    AppEntry localAppEntry2;
    if (paramString != null)
    {
      localList = this.mAppOps.getOpsForPackage(paramInt, paramString, paramOpsTemplate.ops);
      if (localList == null)
        break label338;
      i5 = 0;
      int i6 = localList.size();
      if (i5 >= i6)
        break label338;
      localPackageOps2 = (AppOpsManager.PackageOps)localList.get(i5);
      localAppEntry2 = getAppEntry(localContext, localHashMap, localPackageOps2.getPackageName(), null);
      if (localAppEntry2 != null)
        break label244;
    }
    label244: int i7;
    int i8;
    do
    {
      i5++;
      break label171;
      localList = this.mAppOps.getPackagesForOps(paramOpsTemplate.ops);
      break;
      i7 = 0;
      i8 = localPackageOps2.getOps().size();
    }
    while (i7 >= i8);
    AppOpsManager.OpEntry localOpEntry2 = (AppOpsManager.OpEntry)localPackageOps2.getOps().get(i7);
    boolean bool2;
    if (paramString == null)
    {
      bool2 = true;
      label290: if (paramString != null)
        break label325;
    }
    label325: for (int i9 = 0; ; i9 = arrayOfInt[localOpEntry2.getOp()])
    {
      addOp(localArrayList1, localPackageOps2, localAppEntry2, localOpEntry2, bool2, i9);
      i7++;
      break;
      bool2 = false;
      break label290;
    }
    label338: Object localObject;
    if (paramString != null)
      localObject = new ArrayList();
    try
    {
      PackageInfo localPackageInfo2 = this.mPm.getPackageInfo(paramString, 4096);
      ((List)localObject).add(localPackageInfo2);
      label374: int k = 0;
      label377: int m = ((List)localObject).size();
      if (k < m)
      {
        PackageInfo localPackageInfo1 = (PackageInfo)((List)localObject).get(k);
        AppEntry localAppEntry1 = getAppEntry(localContext, localHashMap, localPackageInfo1.packageName, localPackageInfo1.applicationInfo);
        if (localAppEntry1 == null);
        ArrayList localArrayList4;
        AppOpsManager.PackageOps localPackageOps1;
        int n;
        int i1;
        do
        {
          do
          {
            k++;
            break label377;
            String[] arrayOfString = new String[localArrayList2.size()];
            localArrayList2.toArray(arrayOfString);
            localObject = this.mPm.getPackagesHoldingPermissions(arrayOfString, 0);
            break;
            localArrayList4 = null;
            localPackageOps1 = null;
          }
          while (localPackageInfo1.requestedPermissions == null);
          n = 0;
          i1 = localPackageInfo1.requestedPermissions.length;
        }
        while (n >= i1);
        if ((localPackageInfo1.requestedPermissionsFlags != null) && ((0x2 & localPackageInfo1.requestedPermissionsFlags[n]) == 0));
        int i2;
        int i3;
        do
        {
          n++;
          break;
          i2 = 0;
          i3 = localArrayList2.size();
        }
        while (i2 >= i3);
        if (!((String)localArrayList2.get(i2)).equals(localPackageInfo1.requestedPermissions[n]));
        while (localAppEntry1.hasOp(((Integer)localArrayList3.get(i2)).intValue()))
        {
          i2++;
          break;
        }
        if (localArrayList4 == null)
        {
          localArrayList4 = new ArrayList();
          localPackageOps1 = new AppOpsManager.PackageOps(localPackageInfo1.packageName, localPackageInfo1.applicationInfo.uid, localArrayList4);
        }
        AppOpsManager.OpEntry localOpEntry1 = new AppOpsManager.OpEntry(((Integer)localArrayList3.get(i2)).intValue(), 0, 0L, 0L, 0);
        localArrayList4.add(localOpEntry1);
        boolean bool1;
        if (paramString == null)
        {
          bool1 = true;
          label679: if (paramString != null)
            break label711;
        }
        label711: for (int i4 = 0; ; i4 = arrayOfInt[localOpEntry1.getOp()])
        {
          addOp(localArrayList1, localPackageOps1, localAppEntry1, localOpEntry1, bool1, i4);
          break;
          bool1 = false;
          break label679;
        }
      }
      Collections.sort(localArrayList1, APP_OP_COMPARATOR);
      return localArrayList1;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      break label374;
    }
  }

  public static class AppEntry
  {
    private final File mApkFile;
    private Drawable mIcon;
    private final ApplicationInfo mInfo;
    private String mLabel;
    private boolean mMounted;
    private final SparseArray<AppOpsState.AppOpEntry> mOpSwitches = new SparseArray();
    private final SparseArray<AppOpsManager.OpEntry> mOps = new SparseArray();
    private final AppOpsState mState;

    public AppEntry(AppOpsState paramAppOpsState, ApplicationInfo paramApplicationInfo)
    {
      this.mState = paramAppOpsState;
      this.mInfo = paramApplicationInfo;
      this.mApkFile = new File(paramApplicationInfo.sourceDir);
    }

    public void addOp(AppOpsState.AppOpEntry paramAppOpEntry, AppOpsManager.OpEntry paramOpEntry)
    {
      this.mOps.put(paramOpEntry.getOp(), paramOpEntry);
      this.mOpSwitches.put(AppOpsManager.opToSwitch(paramOpEntry.getOp()), paramAppOpEntry);
    }

    public ApplicationInfo getApplicationInfo()
    {
      return this.mInfo;
    }

    public Drawable getIcon()
    {
      if (this.mIcon == null)
      {
        if (this.mApkFile.exists())
        {
          this.mIcon = this.mInfo.loadIcon(this.mState.mPm);
          return this.mIcon;
        }
        this.mMounted = false;
      }
      do
      {
        return this.mState.mContext.getResources().getDrawable(17301651);
        if (this.mMounted)
          break;
      }
      while (!this.mApkFile.exists());
      this.mMounted = true;
      this.mIcon = this.mInfo.loadIcon(this.mState.mPm);
      return this.mIcon;
      return this.mIcon;
    }

    public String getLabel()
    {
      return this.mLabel;
    }

    public AppOpsState.AppOpEntry getOpSwitch(int paramInt)
    {
      return (AppOpsState.AppOpEntry)this.mOpSwitches.get(AppOpsManager.opToSwitch(paramInt));
    }

    public boolean hasOp(int paramInt)
    {
      return this.mOps.indexOfKey(paramInt) >= 0;
    }

    void loadLabel(Context paramContext)
    {
      if ((this.mLabel == null) || (!this.mMounted))
      {
        if (!this.mApkFile.exists())
        {
          this.mMounted = false;
          this.mLabel = this.mInfo.packageName;
        }
      }
      else
        return;
      this.mMounted = true;
      CharSequence localCharSequence = this.mInfo.loadLabel(paramContext.getPackageManager());
      if (localCharSequence != null);
      for (String str = localCharSequence.toString(); ; str = this.mInfo.packageName)
      {
        this.mLabel = str;
        return;
      }
    }

    public String toString()
    {
      return this.mLabel;
    }
  }

  public static class AppOpEntry
  {
    private final AppOpsState.AppEntry mApp;
    private final ArrayList<AppOpsManager.OpEntry> mOps = new ArrayList();
    private final AppOpsManager.PackageOps mPkgOps;
    private final ArrayList<AppOpsManager.OpEntry> mSwitchOps = new ArrayList();
    private final int mSwitchOrder;

    public AppOpEntry(AppOpsManager.PackageOps paramPackageOps, AppOpsManager.OpEntry paramOpEntry, AppOpsState.AppEntry paramAppEntry, int paramInt)
    {
      this.mPkgOps = paramPackageOps;
      this.mApp = paramAppEntry;
      this.mSwitchOrder = paramInt;
      this.mApp.addOp(this, paramOpEntry);
      this.mOps.add(paramOpEntry);
      this.mSwitchOps.add(paramOpEntry);
    }

    private static void addOp(ArrayList<AppOpsManager.OpEntry> paramArrayList, AppOpsManager.OpEntry paramOpEntry)
    {
      for (int i = 0; i < paramArrayList.size(); i++)
      {
        AppOpsManager.OpEntry localOpEntry = (AppOpsManager.OpEntry)paramArrayList.get(i);
        if (localOpEntry.isRunning() != paramOpEntry.isRunning())
        {
          if (paramOpEntry.isRunning())
            paramArrayList.add(i, paramOpEntry);
        }
        else if (localOpEntry.getTime() < paramOpEntry.getTime())
        {
          paramArrayList.add(i, paramOpEntry);
          return;
        }
      }
      paramArrayList.add(paramOpEntry);
    }

    private CharSequence getCombinedText(ArrayList<AppOpsManager.OpEntry> paramArrayList, CharSequence[] paramArrayOfCharSequence)
    {
      if (paramArrayList.size() == 1)
        return paramArrayOfCharSequence[((AppOpsManager.OpEntry)paramArrayList.get(0)).getOp()];
      StringBuilder localStringBuilder = new StringBuilder();
      for (int i = 0; i < paramArrayList.size(); i++)
      {
        if (i > 0)
          localStringBuilder.append(", ");
        localStringBuilder.append(paramArrayOfCharSequence[((AppOpsManager.OpEntry)paramArrayList.get(i)).getOp()]);
      }
      return localStringBuilder.toString();
    }

    public void addOp(AppOpsManager.OpEntry paramOpEntry)
    {
      this.mApp.addOp(this, paramOpEntry);
      addOp(this.mOps, paramOpEntry);
      if (this.mApp.getOpSwitch(AppOpsManager.opToSwitch(paramOpEntry.getOp())) == null)
        addOp(this.mSwitchOps, paramOpEntry);
    }

    public AppOpsState.AppEntry getAppEntry()
    {
      return this.mApp;
    }

    public AppOpsManager.OpEntry getOpEntry(int paramInt)
    {
      return (AppOpsManager.OpEntry)this.mOps.get(paramInt);
    }

    public AppOpsManager.PackageOps getPackageOps()
    {
      return this.mPkgOps;
    }

    public CharSequence getSummaryText(AppOpsState paramAppOpsState)
    {
      return getCombinedText(this.mOps, paramAppOpsState.mOpSummaries);
    }

    public int getSwitchOrder()
    {
      return this.mSwitchOrder;
    }

    public CharSequence getSwitchText(AppOpsState paramAppOpsState)
    {
      if (this.mSwitchOps.size() > 0)
        return getCombinedText(this.mSwitchOps, paramAppOpsState.mOpLabels);
      return getCombinedText(this.mOps, paramAppOpsState.mOpLabels);
    }

    public long getTime()
    {
      return ((AppOpsManager.OpEntry)this.mOps.get(0)).getTime();
    }

    public CharSequence getTimeText(Resources paramResources, boolean paramBoolean)
    {
      if (isRunning())
        return paramResources.getText(2131428463);
      if (getTime() > 0L)
        return DateUtils.getRelativeTimeSpanString(getTime(), System.currentTimeMillis(), 60000L, 262144);
      if (paramBoolean)
        return paramResources.getText(2131428464);
      return "";
    }

    public boolean isRunning()
    {
      return ((AppOpsManager.OpEntry)this.mOps.get(0)).isRunning();
    }

    public String toString()
    {
      return this.mApp.getLabel();
    }
  }

  public static class OpsTemplate
    implements Parcelable
  {
    public static final Parcelable.Creator<OpsTemplate> CREATOR = new Parcelable.Creator()
    {
      public AppOpsState.OpsTemplate createFromParcel(Parcel paramAnonymousParcel)
      {
        return new AppOpsState.OpsTemplate(paramAnonymousParcel);
      }

      public AppOpsState.OpsTemplate[] newArray(int paramAnonymousInt)
      {
        return new AppOpsState.OpsTemplate[paramAnonymousInt];
      }
    };
    public final int[] ops;
    public final boolean[] showPerms;

    OpsTemplate(Parcel paramParcel)
    {
      this.ops = paramParcel.createIntArray();
      this.showPerms = paramParcel.createBooleanArray();
    }

    public OpsTemplate(int[] paramArrayOfInt, boolean[] paramArrayOfBoolean)
    {
      this.ops = paramArrayOfInt;
      this.showPerms = paramArrayOfBoolean;
    }

    public int describeContents()
    {
      return 0;
    }

    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeIntArray(this.ops);
      paramParcel.writeBooleanArray(this.showPerms);
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.applications.AppOpsState
 * JD-Core Version:    0.6.2
 */