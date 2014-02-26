package com.android.settings.deviceinfo;

import android.app.ActivityManagerNative;
import android.app.ActivityThread;
import android.app.IActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.IPackageManager;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.UserManager;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceGroup;
import android.provider.MediaStore.Images.Media;
import android.text.format.Formatter;
import android.util.SparseLongArray;
import com.android.settings.MediaFormat;
import com.android.settings.Settings.ManageApplicationsActivity;
import com.google.android.collect.Lists;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class StorageVolumePreferenceCategory extends PreferenceCategory
{
  private Preference mFormatPreference;
  private StorageItemPreference mItemApps;
  private StorageItemPreference mItemAvailable;
  private StorageItemPreference mItemCache;
  private StorageItemPreference mItemDcim;
  private StorageItemPreference mItemDownloads;
  private StorageItemPreference mItemMisc;
  private StorageItemPreference mItemMusic;
  private StorageItemPreference mItemTotal;
  private List<StorageItemPreference> mItemUsers = Lists.newArrayList();
  private final StorageMeasurement mMeasure;
  private Preference mMountTogglePreference;
  private StorageMeasurement.MeasurementReceiver mReceiver = new StorageMeasurement.MeasurementReceiver()
  {
    public void updateApproximate(StorageMeasurement paramAnonymousStorageMeasurement, long paramAnonymousLong1, long paramAnonymousLong2)
    {
      StorageVolumePreferenceCategory.this.mUpdateHandler.obtainMessage(1, new long[] { paramAnonymousLong1, paramAnonymousLong2 }).sendToTarget();
    }

    public void updateDetails(StorageMeasurement paramAnonymousStorageMeasurement, StorageMeasurement.MeasurementDetails paramAnonymousMeasurementDetails)
    {
      StorageVolumePreferenceCategory.this.mUpdateHandler.obtainMessage(2, paramAnonymousMeasurementDetails).sendToTarget();
    }
  };
  private final Resources mResources;
  private Preference mStorageLow;
  private final StorageManager mStorageManager;
  private long mTotalSize;
  private Handler mUpdateHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      switch (paramAnonymousMessage.what)
      {
      default:
        return;
      case 1:
        long[] arrayOfLong = (long[])paramAnonymousMessage.obj;
        StorageVolumePreferenceCategory.this.updateApproximate(arrayOfLong[0], arrayOfLong[1]);
        return;
      case 2:
      }
      StorageMeasurement.MeasurementDetails localMeasurementDetails = (StorageMeasurement.MeasurementDetails)paramAnonymousMessage.obj;
      StorageVolumePreferenceCategory.this.updateDetails(localMeasurementDetails);
    }
  };
  private UsageBarPreference mUsageBarPreference;
  private boolean mUsbConnected;
  private String mUsbFunction;
  private final UserManager mUserManager;
  private final StorageVolume mVolume;

  private StorageVolumePreferenceCategory(Context paramContext, StorageVolume paramStorageVolume)
  {
    super(paramContext);
    this.mVolume = paramStorageVolume;
    this.mMeasure = StorageMeasurement.getInstance(paramContext, paramStorageVolume);
    this.mResources = paramContext.getResources();
    this.mStorageManager = StorageManager.from(paramContext);
    this.mUserManager = ((UserManager)paramContext.getSystemService("user"));
    if (paramStorageVolume != null);
    for (Object localObject = paramStorageVolume.getDescription(paramContext); ; localObject = paramContext.getText(2131428415))
    {
      setTitle((CharSequence)localObject);
      return;
    }
  }

  public static StorageVolumePreferenceCategory buildForInternal(Context paramContext)
  {
    return new StorageVolumePreferenceCategory(paramContext, null);
  }

  public static StorageVolumePreferenceCategory buildForPhysical(Context paramContext, StorageVolume paramStorageVolume)
  {
    return new StorageVolumePreferenceCategory(paramContext, paramStorageVolume);
  }

  private StorageItemPreference buildItem(int paramInt1, int paramInt2)
  {
    return new StorageItemPreference(getContext(), paramInt1, paramInt2);
  }

  private String formatSize(long paramLong)
  {
    return Formatter.formatFileSize(getContext(), paramLong);
  }

  private List<UserInfo> getUsersExcluding(UserInfo paramUserInfo)
  {
    List localList = this.mUserManager.getUsers();
    Iterator localIterator = localList.iterator();
    while (localIterator.hasNext())
      if (((UserInfo)localIterator.next()).id == paramUserInfo.id)
        localIterator.remove();
    return localList;
  }

  private void measure()
  {
    this.mMeasure.invalidate();
    this.mMeasure.measure();
  }

  private static long totalValues(HashMap<String, Long> paramHashMap, String[] paramArrayOfString)
  {
    long l = 0L;
    int i = paramArrayOfString.length;
    for (int j = 0; j < i; j++)
    {
      String str = paramArrayOfString[j];
      if (paramHashMap.containsKey(str))
        l += ((Long)paramHashMap.get(str)).longValue();
    }
    return l;
  }

  private void updatePreference(StorageItemPreference paramStorageItemPreference, long paramLong)
  {
    if (paramLong > 0L)
    {
      paramStorageItemPreference.setSummary(formatSize(paramLong));
      int i = paramStorageItemPreference.getOrder();
      this.mUsageBarPreference.addEntry(i, (float)paramLong / (float)this.mTotalSize, paramStorageItemPreference.color);
      return;
    }
    removePreference(paramStorageItemPreference);
  }

  private void updatePreferencesFromState()
  {
    if (this.mVolume == null);
    label234: label381: 
    do
    {
      return;
      this.mMountTogglePreference.setEnabled(true);
      String str = this.mStorageManager.getVolumeState(this.mVolume.getPath());
      if ("mounted_ro".equals(str))
        this.mItemAvailable.setTitle(2131428130);
      while (true)
      {
        if ((!"mounted".equals(str)) && (!"mounted_ro".equals(str)))
          break label234;
        this.mMountTogglePreference.setEnabled(true);
        this.mMountTogglePreference.setTitle(this.mResources.getString(2131428140));
        this.mMountTogglePreference.setSummary(this.mResources.getString(2131428141));
        if ((!this.mUsbConnected) || ((!"mtp".equals(this.mUsbFunction)) && (!"ptp".equals(this.mUsbFunction))))
          break label381;
        this.mMountTogglePreference.setEnabled(false);
        if (("mounted".equals(str)) || ("mounted_ro".equals(str)))
          this.mMountTogglePreference.setSummary(this.mResources.getString(2131428149));
        if (this.mFormatPreference == null)
          break;
        this.mFormatPreference.setEnabled(false);
        this.mFormatPreference.setSummary(this.mResources.getString(2131428149));
        return;
        this.mItemAvailable.setTitle(2131428129);
      }
      if (("unmounted".equals(str)) || ("nofs".equals(str)) || ("unmountable".equals(str)))
      {
        this.mMountTogglePreference.setEnabled(true);
        this.mMountTogglePreference.setTitle(this.mResources.getString(2131428143));
        this.mMountTogglePreference.setSummary(this.mResources.getString(2131428144));
      }
      while (true)
      {
        removePreference(this.mUsageBarPreference);
        removePreference(this.mItemTotal);
        removePreference(this.mItemAvailable);
        break;
        this.mMountTogglePreference.setEnabled(false);
        this.mMountTogglePreference.setTitle(this.mResources.getString(2131428143));
        this.mMountTogglePreference.setSummary(this.mResources.getString(2131428142));
      }
    }
    while (this.mFormatPreference == null);
    this.mFormatPreference.setEnabled(true);
    this.mFormatPreference.setSummary(this.mResources.getString(2131428146));
  }

  public StorageVolume getStorageVolume()
  {
    return this.mVolume;
  }

  public void init()
  {
    Context localContext = getContext();
    removeAll();
    while (true)
    {
      try
      {
        UserInfo localUserInfo1 = ActivityManagerNative.getDefault().getCurrentUser();
        List localList = getUsersExcluding(localUserInfo1);
        if ((this.mVolume == null) && (localList.size() > 0))
        {
          i = 1;
          UsageBarPreference localUsageBarPreference = new UsageBarPreference(localContext);
          this.mUsageBarPreference = localUsageBarPreference;
          this.mUsageBarPreference.setOrder(-2);
          addPreference(this.mUsageBarPreference);
          this.mItemTotal = buildItem(2131428131, 0);
          this.mItemAvailable = buildItem(2131428129, 2131361795);
          addPreference(this.mItemTotal);
          addPreference(this.mItemAvailable);
          this.mItemApps = buildItem(2131428133, 2131361796);
          this.mItemDcim = buildItem(2131428136, 2131361798);
          this.mItemMusic = buildItem(2131428137, 2131361799);
          this.mItemDownloads = buildItem(2131428135, 2131361797);
          this.mItemCache = buildItem(2131428139, 2131361800);
          this.mItemMisc = buildItem(2131428138, 2131361801);
          this.mItemCache.setKey("cache");
          if ((this.mVolume != null) && (!this.mVolume.isPrimary()))
            break label468;
          j = 1;
          if (j == 0)
            break;
          if (i != 0)
          {
            PreferenceHeader localPreferenceHeader1 = new PreferenceHeader(localContext, localUserInfo1.name);
            addPreference(localPreferenceHeader1);
          }
          addPreference(this.mItemApps);
          addPreference(this.mItemDcim);
          addPreference(this.mItemMusic);
          addPreference(this.mItemDownloads);
          addPreference(this.mItemCache);
          addPreference(this.mItemMisc);
          if (i == 0)
            break;
          PreferenceHeader localPreferenceHeader2 = new PreferenceHeader(localContext, 2131428167);
          addPreference(localPreferenceHeader2);
          int m = 0;
          Iterator localIterator = localList.iterator();
          if (!localIterator.hasNext())
            break;
          UserInfo localUserInfo2 = (UserInfo)localIterator.next();
          int n = m + 1;
          if (m % 2 != 0)
            break label474;
          i1 = 2131361802;
          StorageItemPreference localStorageItemPreference = new StorageItemPreference(getContext(), localUserInfo2.name, i1, localUserInfo2.id);
          this.mItemUsers.add(localStorageItemPreference);
          addPreference(localStorageItemPreference);
          m = n;
          continue;
        }
      }
      catch (RemoteException localRemoteException1)
      {
        throw new RuntimeException("Failed to get current user");
      }
      int i = 0;
      continue;
      label468: int j = 0;
      continue;
      label474: int i1 = 2131361803;
    }
    boolean bool;
    if (this.mVolume != null)
    {
      bool = this.mVolume.isRemovable();
      Preference localPreference1 = new Preference(localContext);
      this.mMountTogglePreference = localPreference1;
      if (bool)
      {
        this.mMountTogglePreference.setTitle(2131428140);
        this.mMountTogglePreference.setSummary(2131428141);
        addPreference(this.mMountTogglePreference);
      }
      if (this.mVolume == null)
        break label708;
    }
    label708: for (int k = 1; ; k = 0)
    {
      if (k != 0)
      {
        Preference localPreference2 = new Preference(localContext);
        this.mFormatPreference = localPreference2;
        this.mFormatPreference.setTitle(2131428145);
        this.mFormatPreference.setSummary(2131428146);
        addPreference(this.mFormatPreference);
      }
      IPackageManager localIPackageManager = ActivityThread.getPackageManager();
      try
      {
        if (localIPackageManager.isStorageLow())
        {
          Preference localPreference3 = new Preference(localContext);
          this.mStorageLow = localPreference3;
          this.mStorageLow.setOrder(-1);
          this.mStorageLow.setTitle(2131428157);
          this.mStorageLow.setSummary(2131428158);
          addPreference(this.mStorageLow);
          return;
        }
        if (this.mStorageLow != null)
        {
          removePreference(this.mStorageLow);
          this.mStorageLow = null;
          return;
        }
      }
      catch (RemoteException localRemoteException2)
      {
      }
      return;
      bool = false;
      break;
    }
  }

  public Intent intentForClick(Preference paramPreference)
  {
    paramPreference.getKey();
    Intent localIntent1;
    if (paramPreference == this.mFormatPreference)
    {
      localIntent1 = new Intent("android.intent.action.VIEW");
      localIntent1.setClass(getContext(), MediaFormat.class);
      localIntent1.putExtra("storage_volume", this.mVolume);
    }
    StorageItemPreference localStorageItemPreference;
    do
    {
      return localIntent1;
      if (paramPreference == this.mItemApps)
      {
        Intent localIntent2 = new Intent("android.intent.action.MANAGE_PACKAGE_STORAGE");
        localIntent2.setClass(getContext(), Settings.ManageApplicationsActivity.class);
        return localIntent2;
      }
      if (paramPreference == this.mItemDownloads)
        return new Intent("android.intent.action.VIEW_DOWNLOADS").putExtra("android.app.DownloadManager.extra_sortBySize", true);
      if (paramPreference == this.mItemMusic)
      {
        Intent localIntent3 = new Intent("android.intent.action.GET_CONTENT");
        localIntent3.setType("audio/mp3");
        return localIntent3;
      }
      if (paramPreference == this.mItemDcim)
      {
        Intent localIntent4 = new Intent("android.intent.action.VIEW");
        localIntent4.putExtra("android.intent.extra.LOCAL_ONLY", true);
        localIntent4.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        return localIntent4;
      }
      localStorageItemPreference = this.mItemMisc;
      localIntent1 = null;
    }
    while (paramPreference != localStorageItemPreference);
    Intent localIntent5 = new Intent(getContext().getApplicationContext(), MiscFilesHandler.class);
    localIntent5.putExtra("storage_volume", this.mVolume);
    return localIntent5;
  }

  public boolean mountToggleClicked(Preference paramPreference)
  {
    return paramPreference == this.mMountTogglePreference;
  }

  public void onCacheCleared()
  {
    measure();
  }

  public void onMediaScannerFinished()
  {
    measure();
  }

  public void onPause()
  {
    this.mMeasure.cleanUp();
  }

  public void onResume()
  {
    this.mMeasure.setReceiver(this.mReceiver);
    measure();
  }

  public void onStorageStateChanged()
  {
    init();
    measure();
  }

  public void onUsbStateChanged(boolean paramBoolean, String paramString)
  {
    this.mUsbConnected = paramBoolean;
    this.mUsbFunction = paramString;
    measure();
  }

  public void updateApproximate(long paramLong1, long paramLong2)
  {
    this.mItemTotal.setSummary(formatSize(paramLong1));
    this.mItemAvailable.setSummary(formatSize(paramLong2));
    this.mTotalSize = paramLong1;
    long l = paramLong1 - paramLong2;
    this.mUsageBarPreference.clear();
    this.mUsageBarPreference.addEntry(0, (float)l / (float)paramLong1, -7829368);
    this.mUsageBarPreference.commit();
    updatePreferencesFromState();
  }

  public void updateDetails(StorageMeasurement.MeasurementDetails paramMeasurementDetails)
  {
    if ((this.mVolume == null) || (this.mVolume.isPrimary()));
    for (int i = 1; i == 0; i = 0)
      return;
    this.mItemTotal.setSummary(formatSize(paramMeasurementDetails.totalSize));
    this.mItemAvailable.setSummary(formatSize(paramMeasurementDetails.availSize));
    this.mUsageBarPreference.clear();
    updatePreference(this.mItemApps, paramMeasurementDetails.appsSize);
    HashMap localHashMap1 = paramMeasurementDetails.mediaSize;
    String[] arrayOfString1 = new String[3];
    arrayOfString1[0] = Environment.DIRECTORY_DCIM;
    arrayOfString1[1] = Environment.DIRECTORY_MOVIES;
    arrayOfString1[2] = Environment.DIRECTORY_PICTURES;
    long l1 = totalValues(localHashMap1, arrayOfString1);
    updatePreference(this.mItemDcim, l1);
    HashMap localHashMap2 = paramMeasurementDetails.mediaSize;
    String[] arrayOfString2 = new String[5];
    arrayOfString2[0] = Environment.DIRECTORY_MUSIC;
    arrayOfString2[1] = Environment.DIRECTORY_ALARMS;
    arrayOfString2[2] = Environment.DIRECTORY_NOTIFICATIONS;
    arrayOfString2[3] = Environment.DIRECTORY_RINGTONES;
    arrayOfString2[4] = Environment.DIRECTORY_PODCASTS;
    long l2 = totalValues(localHashMap2, arrayOfString2);
    updatePreference(this.mItemMusic, l2);
    HashMap localHashMap3 = paramMeasurementDetails.mediaSize;
    String[] arrayOfString3 = new String[1];
    arrayOfString3[0] = Environment.DIRECTORY_DOWNLOADS;
    long l3 = totalValues(localHashMap3, arrayOfString3);
    updatePreference(this.mItemDownloads, l3);
    updatePreference(this.mItemCache, paramMeasurementDetails.cacheSize);
    updatePreference(this.mItemMisc, paramMeasurementDetails.miscSize);
    Iterator localIterator = this.mItemUsers.iterator();
    while (localIterator.hasNext())
    {
      StorageItemPreference localStorageItemPreference = (StorageItemPreference)localIterator.next();
      updatePreference(localStorageItemPreference, paramMeasurementDetails.usersSize.get(localStorageItemPreference.userHandle));
    }
    this.mUsageBarPreference.commit();
  }

  public static class PreferenceHeader extends Preference
  {
    public PreferenceHeader(Context paramContext, int paramInt)
    {
      super(null, 16842892);
      setTitle(paramInt);
    }

    public PreferenceHeader(Context paramContext, CharSequence paramCharSequence)
    {
      super(null, 16842892);
      setTitle(paramCharSequence);
    }

    public boolean isEnabled()
    {
      return false;
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.deviceinfo.StorageVolumePreferenceCategory
 * JD-Core Version:    0.6.2
 */