package com.android.settings.deviceinfo;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.IPackageDataObserver.Stub;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserManager;
import android.os.storage.IMountService;
import android.os.storage.IMountService.Stub;
import android.os.storage.StorageEventListener;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.google.android.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Memory extends SettingsPreferenceFragment
{
  private static String sClickedMountPoint;
  private static Preference sLastClickedMountToggle;
  private ArrayList<StorageVolumePreferenceCategory> mCategories = Lists.newArrayList();
  private final BroadcastReceiver mMediaScannerReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      String str1 = paramAnonymousIntent.getAction();
      if (str1.equals("android.hardware.usb.action.USB_STATE"))
      {
        boolean bool = paramAnonymousIntent.getBooleanExtra("connected", false);
        String str2 = Memory.this.mUsbManager.getDefaultFunction();
        Iterator localIterator2 = Memory.this.mCategories.iterator();
        while (localIterator2.hasNext())
          ((StorageVolumePreferenceCategory)localIterator2.next()).onUsbStateChanged(bool, str2);
      }
      if (str1.equals("android.intent.action.MEDIA_SCANNER_FINISHED"))
      {
        Iterator localIterator1 = Memory.this.mCategories.iterator();
        while (localIterator1.hasNext())
          ((StorageVolumePreferenceCategory)localIterator1.next()).onMediaScannerFinished();
      }
    }
  };
  private IMountService mMountService;
  StorageEventListener mStorageListener = new StorageEventListener()
  {
    public void onStorageStateChanged(String paramAnonymousString1, String paramAnonymousString2, String paramAnonymousString3)
    {
      Log.i("MemorySettings", "Received storage state changed notification that " + paramAnonymousString1 + " changed state from " + paramAnonymousString2 + " to " + paramAnonymousString3);
      Iterator localIterator = Memory.this.mCategories.iterator();
      while (localIterator.hasNext())
      {
        StorageVolumePreferenceCategory localStorageVolumePreferenceCategory = (StorageVolumePreferenceCategory)localIterator.next();
        StorageVolume localStorageVolume = localStorageVolumePreferenceCategory.getStorageVolume();
        if ((localStorageVolume != null) && (paramAnonymousString1.equals(localStorageVolume.getPath())))
          localStorageVolumePreferenceCategory.onStorageStateChanged();
      }
    }
  };
  private StorageManager mStorageManager;
  private UsbManager mUsbManager;

  private void addCategory(StorageVolumePreferenceCategory paramStorageVolumePreferenceCategory)
  {
    this.mCategories.add(paramStorageVolumePreferenceCategory);
    getPreferenceScreen().addPreference(paramStorageVolumePreferenceCategory);
    paramStorageVolumePreferenceCategory.init();
  }

  private void doUnmount()
  {
    Toast.makeText(getActivity(), 2131428154, 0).show();
    IMountService localIMountService = getMountService();
    try
    {
      sLastClickedMountToggle.setEnabled(false);
      sLastClickedMountToggle.setTitle(getString(2131428155));
      sLastClickedMountToggle.setSummary(getString(2131428156));
      localIMountService.unmountVolume(sClickedMountPoint, true, false);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      showDialogInner(2);
    }
  }

  private IMountService getMountService()
  {
    try
    {
      if (this.mMountService == null)
      {
        IBinder localIBinder = ServiceManager.getService("mount");
        if (localIBinder == null)
          break label36;
        this.mMountService = IMountService.Stub.asInterface(localIBinder);
      }
      while (true)
      {
        IMountService localIMountService = this.mMountService;
        return localIMountService;
        label36: Log.e("MemorySettings", "Can't get mount service");
      }
    }
    finally
    {
    }
  }

  private boolean hasAppsAccessingStorage()
    throws RemoteException
  {
    int[] arrayOfInt = getMountService().getStorageUsers(sClickedMountPoint);
    if ((arrayOfInt != null) && (arrayOfInt.length > 0));
    return true;
  }

  private boolean isMassStorageEnabled()
  {
    StorageVolume localStorageVolume = StorageManager.getPrimaryVolume(this.mStorageManager.getVolumeList());
    return (localStorageVolume != null) && (localStorageVolume.allowMassStorage());
  }

  private void mount()
  {
    IMountService localIMountService = getMountService();
    if (localIMountService != null);
    try
    {
      localIMountService.mountVolume(sClickedMountPoint);
      return;
      Log.e("MemorySettings", "Mount service is null, can't mount");
      return;
    }
    catch (RemoteException localRemoteException)
    {
    }
  }

  private void onCacheCleared()
  {
    Iterator localIterator = this.mCategories.iterator();
    while (localIterator.hasNext())
      ((StorageVolumePreferenceCategory)localIterator.next()).onCacheCleared();
  }

  private void showDialogInner(int paramInt)
  {
    removeDialog(paramInt);
    showDialog(paramInt);
  }

  private void unmount()
  {
    try
    {
      if (hasAppsAccessingStorage())
      {
        showDialogInner(1);
        return;
      }
      doUnmount();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.e("MemorySettings", "Is MountService running?");
      showDialogInner(2);
    }
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    Activity localActivity = getActivity();
    this.mUsbManager = ((UsbManager)getSystemService("usb"));
    this.mStorageManager = StorageManager.from(localActivity);
    this.mStorageManager.registerListener(this.mStorageListener);
    addPreferencesFromResource(2131034126);
    addCategory(StorageVolumePreferenceCategory.buildForInternal(localActivity));
    for (StorageVolume localStorageVolume : this.mStorageManager.getVolumeList())
      if (!localStorageVolume.isEmulated())
        addCategory(StorageVolumePreferenceCategory.buildForPhysical(localActivity, localStorageVolume));
    setHasOptionsMenu(true);
  }

  public Dialog onCreateDialog(int paramInt)
  {
    switch (paramInt)
    {
    default:
      return null;
    case 1:
      return new AlertDialog.Builder(getActivity()).setTitle(2131428150).setPositiveButton(2131428420, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          Memory.this.doUnmount();
        }
      }).setNegativeButton(2131427567, null).setMessage(2131428151).create();
    case 2:
    }
    return new AlertDialog.Builder(getActivity()).setTitle(2131428152).setNeutralButton(2131428420, null).setMessage(2131428153).create();
  }

  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    paramMenuInflater.inflate(2131755011, paramMenu);
  }

  public void onDestroy()
  {
    if ((this.mStorageManager != null) && (this.mStorageListener != null))
      this.mStorageManager.unregisterListener(this.mStorageListener);
    super.onDestroy();
  }

  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    switch (paramMenuItem.getItemId())
    {
    default:
      return super.onOptionsItemSelected(paramMenuItem);
    case 2131231270:
    }
    if ((getActivity() instanceof PreferenceActivity))
      ((PreferenceActivity)getActivity()).startPreferencePanel(UsbSettings.class.getCanonicalName(), null, 2131428160, null, this, 0);
    while (true)
    {
      return true;
      startFragment(this, UsbSettings.class.getCanonicalName(), -1, null);
    }
  }

  public void onPause()
  {
    super.onPause();
    getActivity().unregisterReceiver(this.mMediaScannerReceiver);
    Iterator localIterator = this.mCategories.iterator();
    while (localIterator.hasNext())
      ((StorageVolumePreferenceCategory)localIterator.next()).onPause();
  }

  public boolean onPreferenceTreeClick(PreferenceScreen paramPreferenceScreen, Preference paramPreference)
  {
    if ("cache".equals(paramPreference.getKey()))
    {
      ConfirmClearCacheFragment.show(this);
      return true;
    }
    Iterator localIterator = this.mCategories.iterator();
    while (true)
      if (localIterator.hasNext())
      {
        StorageVolumePreferenceCategory localStorageVolumePreferenceCategory = (StorageVolumePreferenceCategory)localIterator.next();
        Intent localIntent = localStorageVolumePreferenceCategory.intentForClick(paramPreference);
        if (localIntent != null)
        {
          if (Utils.isMonkeyRunning())
            break;
          try
          {
            startActivity(localIntent);
            return true;
          }
          catch (ActivityNotFoundException localActivityNotFoundException)
          {
            Log.w("MemorySettings", "No activity found for intent " + localIntent);
            return true;
          }
        }
        StorageVolume localStorageVolume = localStorageVolumePreferenceCategory.getStorageVolume();
        if ((localStorageVolume != null) && (localStorageVolumePreferenceCategory.mountToggleClicked(paramPreference)))
        {
          sLastClickedMountToggle = paramPreference;
          sClickedMountPoint = localStorageVolume.getPath();
          String str = this.mStorageManager.getVolumeState(localStorageVolume.getPath());
          if (("mounted".equals(str)) || ("mounted_ro".equals(str)))
          {
            unmount();
            return true;
          }
          mount();
          return true;
        }
      }
    return false;
  }

  public void onPrepareOptionsMenu(Menu paramMenu)
  {
    MenuItem localMenuItem = paramMenu.findItem(2131231270);
    UserManager localUserManager = (UserManager)getActivity().getSystemService("user");
    if ((!isMassStorageEnabled()) && (!localUserManager.hasUserRestriction("no_usb_file_transfer")));
    for (boolean bool = true; ; bool = false)
    {
      localMenuItem.setVisible(bool);
      return;
    }
  }

  public void onResume()
  {
    super.onResume();
    IntentFilter localIntentFilter1 = new IntentFilter("android.intent.action.MEDIA_SCANNER_STARTED");
    localIntentFilter1.addAction("android.intent.action.MEDIA_SCANNER_FINISHED");
    localIntentFilter1.addDataScheme("file");
    getActivity().registerReceiver(this.mMediaScannerReceiver, localIntentFilter1);
    IntentFilter localIntentFilter2 = new IntentFilter();
    localIntentFilter2.addAction("android.hardware.usb.action.USB_STATE");
    getActivity().registerReceiver(this.mMediaScannerReceiver, localIntentFilter2);
    Iterator localIterator = this.mCategories.iterator();
    while (localIterator.hasNext())
      ((StorageVolumePreferenceCategory)localIterator.next()).onResume();
  }

  private static class ClearCacheObserver extends IPackageDataObserver.Stub
  {
    private int mRemaining;
    private final Memory mTarget;

    public ClearCacheObserver(Memory paramMemory, int paramInt)
    {
      this.mTarget = paramMemory;
      this.mRemaining = paramInt;
    }

    public void onRemoveCompleted(String paramString, boolean paramBoolean)
    {
      try
      {
        int i = -1 + this.mRemaining;
        this.mRemaining = i;
        if (i == 0)
          this.mTarget.onCacheCleared();
        return;
      }
      finally
      {
      }
    }
  }

  public static class ConfirmClearCacheFragment extends DialogFragment
  {
    public static void show(Memory paramMemory)
    {
      if (!paramMemory.isAdded())
        return;
      ConfirmClearCacheFragment localConfirmClearCacheFragment = new ConfirmClearCacheFragment();
      localConfirmClearCacheFragment.setTargetFragment(paramMemory, 0);
      localConfirmClearCacheFragment.show(paramMemory.getFragmentManager(), "confirmClearCache");
    }

    public Dialog onCreateDialog(Bundle paramBundle)
    {
      final Activity localActivity = getActivity();
      AlertDialog.Builder localBuilder = new AlertDialog.Builder(localActivity);
      localBuilder.setTitle(2131428147);
      localBuilder.setMessage(getString(2131428148));
      localBuilder.setPositiveButton(17039370, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          Memory localMemory = (Memory)Memory.ConfirmClearCacheFragment.this.getTargetFragment();
          PackageManager localPackageManager = localActivity.getPackageManager();
          List localList = localPackageManager.getInstalledPackages(0);
          Memory.ClearCacheObserver localClearCacheObserver = new Memory.ClearCacheObserver(localMemory, localList.size());
          Iterator localIterator = localList.iterator();
          while (localIterator.hasNext())
            localPackageManager.deleteApplicationCacheFiles(((PackageInfo)localIterator.next()).packageName, localClearCacheObserver);
        }
      });
      localBuilder.setNegativeButton(17039360, null);
      return localBuilder.create();
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.deviceinfo.Memory
 * JD-Core Version:    0.6.2
 */