package com.android.settings.applications;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.AppOpsManager;
import android.app.Fragment;
import android.app.INotificationManager;
import android.app.INotificationManager.Stub;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.IPackageManager.Stub;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.NetworkPolicyManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFrameLayout;
import android.preference.PreferenceFrameLayout.LayoutParams;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.BidiFormatter;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView.RecyclerListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filter.FilterResults;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.android.internal.app.IMediaContainerService;
import com.android.internal.app.IMediaContainerService.Stub;
import com.android.settings.Settings.RunningServicesActivity;
import com.android.settings.Settings.StorageUseActivity;
import com.android.settings.Utils;
import com.android.settings.deviceinfo.StorageMeasurement;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ManageApplications extends Fragment
  implements DialogInterface.OnClickListener, DialogInterface.OnDismissListener, AppClickListener
{
  private boolean mActivityResumed;
  private ApplicationsState mApplicationsState;
  private CharSequence mComputingSizeStr;
  private final ServiceConnection mContainerConnection = new ServiceConnection()
  {
    public void onServiceConnected(ComponentName paramAnonymousComponentName, IBinder paramAnonymousIBinder)
    {
      ManageApplications.access$1402(ManageApplications.this, IMediaContainerService.Stub.asInterface(paramAnonymousIBinder));
      for (int i = 0; i < ManageApplications.this.mTabs.size(); i++)
        ((ManageApplications.TabInfo)ManageApplications.this.mTabs.get(i)).setContainerService(ManageApplications.this.mContainerService);
    }

    public void onServiceDisconnected(ComponentName paramAnonymousComponentName)
    {
      ManageApplications.access$1402(ManageApplications.this, null);
    }
  };
  private volatile IMediaContainerService mContainerService;
  private ViewGroup mContentContainer;
  TabInfo mCurTab = null;
  private String mCurrentPkgName;
  private int mDefaultListType = -1;
  private LayoutInflater mInflater;
  CharSequence mInvalidSizeStr;
  private int mNumTabs;
  private Menu mOptionsMenu;
  AlertDialog mResetDialog;
  private View mRootView;
  private boolean mShowBackground = false;
  private int mSortOrder = 4;
  private final ArrayList<TabInfo> mTabs = new ArrayList();
  private ViewPager mViewPager;

  private void startApplicationDetailsActivity()
  {
    Bundle localBundle = new Bundle();
    localBundle.putString("package", this.mCurrentPkgName);
    ((PreferenceActivity)getActivity()).startPreferencePanel(InstalledAppDetails.class.getName(), localBundle, 2131428365, null, this, 1);
  }

  private void updateNumTabs()
  {
    if (this.mApplicationsState.haveDisabledApps());
    for (int i = this.mTabs.size(); ; i = -1 + this.mTabs.size())
    {
      if (i != this.mNumTabs)
      {
        this.mNumTabs = i;
        if (this.mViewPager != null)
          this.mViewPager.getAdapter().notifyDataSetChanged();
      }
      return;
    }
  }

  void buildResetDialog()
  {
    if (this.mResetDialog == null)
    {
      AlertDialog.Builder localBuilder = new AlertDialog.Builder(getActivity());
      localBuilder.setTitle(2131428401);
      localBuilder.setMessage(2131428402);
      localBuilder.setPositiveButton(2131428403, this);
      localBuilder.setNegativeButton(2131427567, null);
      this.mResetDialog = localBuilder.show();
      this.mResetDialog.setOnDismissListener(this);
    }
  }

  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    if ((paramInt1 == 1) && (this.mCurrentPkgName != null))
      this.mApplicationsState.requestSize(this.mCurrentPkgName);
  }

  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    if (this.mResetDialog == paramDialogInterface)
    {
      final PackageManager localPackageManager = getActivity().getPackageManager();
      final IPackageManager localIPackageManager = IPackageManager.Stub.asInterface(ServiceManager.getService("package"));
      final INotificationManager localINotificationManager = INotificationManager.Stub.asInterface(ServiceManager.getService("notification"));
      final NetworkPolicyManager localNetworkPolicyManager = NetworkPolicyManager.from(getActivity());
      new AsyncTask()
      {
        protected Void doInBackground(Void[] paramAnonymousArrayOfVoid)
        {
          List localList = localPackageManager.getInstalledApplications(512);
          int i = 0;
          while (true)
          {
            ApplicationInfo localApplicationInfo;
            if (i < localList.size())
              localApplicationInfo = (ApplicationInfo)localList.get(i);
            try
            {
              localINotificationManager.setNotificationsEnabledForPackage(localApplicationInfo.packageName, localApplicationInfo.uid, true);
              label55: if ((!localApplicationInfo.enabled) && (localPackageManager.getApplicationEnabledSetting(localApplicationInfo.packageName) == 3))
                localPackageManager.setApplicationEnabledSetting(localApplicationInfo.packageName, 0, 1);
              i++;
              continue;
              try
              {
                localIPackageManager.resetPreferredActivities(UserHandle.myUserId());
                label111: this.val$aom.resetAllModes();
                int[] arrayOfInt = localNetworkPolicyManager.getUidsWithPolicy(1);
                int j = ActivityManager.getCurrentUser();
                int k = arrayOfInt.length;
                for (int m = 0; m < k; m++)
                {
                  int n = arrayOfInt[m];
                  if (UserHandle.getUserId(n) == j)
                    localNetworkPolicyManager.setUidPolicy(n, 0);
                }
                this.val$handler.post(new Runnable()
                {
                  public void run()
                  {
                    if ((ManageApplications.this.getActivity() != null) && (ManageApplications.this.mActivityResumed))
                    {
                      for (int i = 0; i < ManageApplications.this.mTabs.size(); i++)
                      {
                        ManageApplications.TabInfo localTabInfo = (ManageApplications.TabInfo)ManageApplications.this.mTabs.get(i);
                        if (localTabInfo.mApplications != null)
                          localTabInfo.mApplications.pause();
                      }
                      if (ManageApplications.this.mCurTab != null)
                        ManageApplications.this.mCurTab.resume(ManageApplications.this.mSortOrder);
                    }
                  }
                });
                return null;
              }
              catch (RemoteException localRemoteException1)
              {
                break label111;
              }
            }
            catch (RemoteException localRemoteException2)
            {
              break label55;
            }
          }
        }
      }
      .execute(new Void[0]);
    }
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setHasOptionsMenu(true);
    this.mApplicationsState = ApplicationsState.getInstance(getActivity().getApplication());
    Intent localIntent1 = getActivity().getIntent();
    String str1 = localIntent1.getAction();
    String str2;
    int i;
    if (getArguments() != null)
    {
      str2 = getArguments().getString("classname");
      if (str2 == null)
        str2 = localIntent1.getComponent().getClassName();
      if ((!str2.equals(Settings.RunningServicesActivity.class.getName())) && (!str2.endsWith(".RunningServices")))
        break label418;
      i = 1;
    }
    while (true)
    {
      label98: int j;
      if (paramBundle != null)
      {
        this.mSortOrder = paramBundle.getInt("sortOrder", this.mSortOrder);
        j = paramBundle.getInt("defaultListType", -1);
        if (j != -1)
          label133: this.mShowBackground = paramBundle.getBoolean("showBackground", false);
      }
      while (true)
      {
        this.mDefaultListType = j;
        Intent localIntent2 = new Intent().setComponent(StorageMeasurement.DEFAULT_CONTAINER_COMPONENT);
        getActivity().bindService(localIntent2, this.mContainerConnection, 1);
        this.mInvalidSizeStr = getActivity().getText(2131428438);
        this.mComputingSizeStr = getActivity().getText(2131428437);
        TabInfo localTabInfo1 = new TabInfo(this, this.mApplicationsState, getActivity().getString(2131428409), 0, this, paramBundle);
        this.mTabs.add(localTabInfo1);
        if (!Environment.isExternalStorageEmulated())
        {
          TabInfo localTabInfo2 = new TabInfo(this, this.mApplicationsState, getActivity().getString(2131428411), 2, this, paramBundle);
          this.mTabs.add(localTabInfo2);
        }
        TabInfo localTabInfo3 = new TabInfo(this, this.mApplicationsState, getActivity().getString(2131428410), 1, this, paramBundle);
        this.mTabs.add(localTabInfo3);
        TabInfo localTabInfo4 = new TabInfo(this, this.mApplicationsState, getActivity().getString(2131428407), 3, this, paramBundle);
        this.mTabs.add(localTabInfo4);
        TabInfo localTabInfo5 = new TabInfo(this, this.mApplicationsState, getActivity().getString(2131428408), 4, this, paramBundle);
        this.mTabs.add(localTabInfo5);
        this.mNumTabs = this.mTabs.size();
        return;
        str2 = null;
        break;
        label418: if ((str2.equals(Settings.StorageUseActivity.class.getName())) || ("android.intent.action.MANAGE_PACKAGE_STORAGE".equals(str1)) || (str2.endsWith(".StorageUse")))
        {
          this.mSortOrder = 5;
          i = 3;
          break label98;
        }
        if (!"android.settings.MANAGE_ALL_APPLICATIONS_SETTINGS".equals(str1))
          break label494;
        i = 3;
        break label98;
        j = i;
        break label133;
        j = i;
      }
      label494: i = 0;
    }
  }

  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    this.mOptionsMenu = paramMenu;
    paramMenu.add(0, 4, 1, 2131428396).setShowAsAction(0);
    paramMenu.add(0, 5, 2, 2131428397).setShowAsAction(0);
    paramMenu.add(0, 6, 3, 2131428398).setShowAsAction(1);
    paramMenu.add(0, 7, 3, 2131428399).setShowAsAction(1);
    paramMenu.add(0, 8, 4, 2131428400).setShowAsAction(0);
    updateOptionsMenu();
  }

  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    this.mInflater = paramLayoutInflater;
    View localView = this.mInflater.inflate(2130968644, paramViewGroup, false);
    this.mContentContainer = paramViewGroup;
    this.mRootView = localView;
    this.mViewPager = ((ViewPager)localView.findViewById(2131230737));
    MyPagerAdapter localMyPagerAdapter = new MyPagerAdapter();
    this.mViewPager.setAdapter(localMyPagerAdapter);
    this.mViewPager.setOnPageChangeListener(localMyPagerAdapter);
    ((PagerTabStrip)localView.findViewById(2131230738)).setTabIndicatorColorResource(17170450);
    if ((paramViewGroup instanceof PreferenceFrameLayout))
      ((PreferenceFrameLayout.LayoutParams)localView.getLayoutParams()).removeBorders = true;
    if ((paramBundle != null) && (paramBundle.getBoolean("resetDialog")))
      buildResetDialog();
    if (paramBundle == null);
    for (int i = 0; ; i++)
      if (i < this.mTabs.size())
      {
        if (((TabInfo)this.mTabs.get(i)).mListType == this.mDefaultListType)
          this.mViewPager.setCurrentItem(i);
      }
      else
        return localView;
  }

  public void onDestroy()
  {
    getActivity().unbindService(this.mContainerConnection);
    super.onDestroy();
  }

  public void onDestroyOptionsMenu()
  {
    this.mOptionsMenu = null;
  }

  public void onDestroyView()
  {
    super.onDestroyView();
    for (int i = 0; i < this.mTabs.size(); i++)
      ((TabInfo)this.mTabs.get(i)).detachView();
  }

  public void onDismiss(DialogInterface paramDialogInterface)
  {
    if (this.mResetDialog == paramDialogInterface)
      this.mResetDialog = null;
  }

  public void onItemClick(TabInfo paramTabInfo, AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
  {
    if ((paramTabInfo.mApplications != null) && (paramTabInfo.mApplications.getCount() > paramInt))
    {
      this.mCurrentPkgName = paramTabInfo.mApplications.getAppEntry(paramInt).info.packageName;
      startApplicationDetailsActivity();
    }
  }

  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    int i = paramMenuItem.getItemId();
    if ((i == 4) || (i == 5))
    {
      this.mSortOrder = i;
      if ((this.mCurTab != null) && (this.mCurTab.mApplications != null))
        this.mCurTab.mApplications.rebuild(this.mSortOrder);
    }
    while (true)
    {
      updateOptionsMenu();
      boolean bool = true;
      do
      {
        return bool;
        if (i == 6)
        {
          this.mShowBackground = false;
          if ((this.mCurTab == null) || (this.mCurTab.mRunningProcessesView == null))
            break;
          this.mCurTab.mRunningProcessesView.mAdapter.setShowBackground(false);
          break;
        }
        if (i == 7)
        {
          this.mShowBackground = true;
          if ((this.mCurTab == null) || (this.mCurTab.mRunningProcessesView == null))
            break;
          this.mCurTab.mRunningProcessesView.mAdapter.setShowBackground(true);
          break;
        }
        bool = false;
      }
      while (i != 8);
      buildResetDialog();
    }
  }

  public void onPause()
  {
    super.onPause();
    this.mActivityResumed = false;
    for (int i = 0; i < this.mTabs.size(); i++)
      ((TabInfo)this.mTabs.get(i)).pause();
  }

  public void onPrepareOptionsMenu(Menu paramMenu)
  {
    updateOptionsMenu();
  }

  public void onResume()
  {
    super.onResume();
    this.mActivityResumed = true;
    updateCurrentTab(this.mViewPager.getCurrentItem());
    updateNumTabs();
    updateOptionsMenu();
  }

  public void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    paramBundle.putInt("sortOrder", this.mSortOrder);
    if (this.mDefaultListType != -1)
      paramBundle.putInt("defaultListType", this.mDefaultListType);
    paramBundle.putBoolean("showBackground", this.mShowBackground);
    if (this.mResetDialog != null)
      paramBundle.putBoolean("resetDialog", true);
  }

  public void onStart()
  {
    super.onStart();
  }

  public void onStop()
  {
    super.onStop();
    if (this.mResetDialog != null)
    {
      this.mResetDialog.dismiss();
      this.mResetDialog = null;
    }
  }

  TabInfo tabForType(int paramInt)
  {
    for (int i = 0; i < this.mTabs.size(); i++)
    {
      TabInfo localTabInfo = (TabInfo)this.mTabs.get(i);
      if (localTabInfo.mListType == paramInt)
        return localTabInfo;
    }
    return null;
  }

  public void updateCurrentTab(int paramInt)
  {
    this.mCurTab = ((TabInfo)this.mTabs.get(paramInt));
    if (this.mActivityResumed)
    {
      this.mCurTab.build(this.mInflater, this.mContentContainer, this.mRootView);
      this.mCurTab.resume(this.mSortOrder);
    }
    while (true)
    {
      for (int i = 0; i < this.mTabs.size(); i++)
      {
        TabInfo localTabInfo = (TabInfo)this.mTabs.get(i);
        if (localTabInfo != this.mCurTab)
          localTabInfo.pause();
      }
      this.mCurTab.pause();
    }
    this.mCurTab.updateStorageUsage();
    updateOptionsMenu();
    Activity localActivity = getActivity();
    if (localActivity != null)
      localActivity.invalidateOptionsMenu();
  }

  void updateOptionsMenu()
  {
    int i = 1;
    if (this.mOptionsMenu == null)
      return;
    label166: boolean bool1;
    if ((this.mCurTab != null) && (this.mCurTab.mListType == i))
    {
      TabInfo localTabInfo = tabForType(i);
      boolean bool4;
      MenuItem localMenuItem3;
      if ((localTabInfo != null) && (localTabInfo.mRunningProcessesView != null))
      {
        bool4 = localTabInfo.mRunningProcessesView.mAdapter.getShowBackground();
        this.mOptionsMenu.findItem(4).setVisible(false);
        this.mOptionsMenu.findItem(5).setVisible(false);
        this.mOptionsMenu.findItem(6).setVisible(bool4);
        localMenuItem3 = this.mOptionsMenu.findItem(7);
        if (bool4)
          break label166;
      }
      while (true)
      {
        localMenuItem3.setVisible(i);
        this.mOptionsMenu.findItem(8).setVisible(false);
        return;
        bool4 = false;
        break;
        bool1 = false;
      }
    }
    MenuItem localMenuItem1 = this.mOptionsMenu.findItem(4);
    boolean bool2;
    MenuItem localMenuItem2;
    if (this.mSortOrder != 4)
    {
      bool2 = bool1;
      localMenuItem1.setVisible(bool2);
      localMenuItem2 = this.mOptionsMenu.findItem(5);
      if (this.mSortOrder == 5)
        break label293;
    }
    label293: for (boolean bool3 = bool1; ; bool3 = false)
    {
      localMenuItem2.setVisible(bool3);
      this.mOptionsMenu.findItem(6).setVisible(false);
      this.mOptionsMenu.findItem(7).setVisible(false);
      this.mOptionsMenu.findItem(8).setVisible(bool1);
      return;
      bool2 = false;
      break;
    }
  }

  static class ApplicationsAdapter extends BaseAdapter
    implements AbsListView.RecyclerListener, Filterable, ApplicationsState.Callbacks
  {
    private final ArrayList<View> mActive = new ArrayList();
    private ArrayList<ApplicationsState.AppEntry> mBaseEntries;
    private final Context mContext;
    CharSequence mCurFilterPrefix;
    private ArrayList<ApplicationsState.AppEntry> mEntries;
    private Filter mFilter = new Filter()
    {
      protected Filter.FilterResults performFiltering(CharSequence paramAnonymousCharSequence)
      {
        ArrayList localArrayList = ManageApplications.ApplicationsAdapter.this.applyPrefixFilter(paramAnonymousCharSequence, ManageApplications.ApplicationsAdapter.this.mBaseEntries);
        Filter.FilterResults localFilterResults = new Filter.FilterResults();
        localFilterResults.values = localArrayList;
        localFilterResults.count = localArrayList.size();
        return localFilterResults;
      }

      protected void publishResults(CharSequence paramAnonymousCharSequence, Filter.FilterResults paramAnonymousFilterResults)
      {
        ManageApplications.ApplicationsAdapter.this.mCurFilterPrefix = paramAnonymousCharSequence;
        ManageApplications.ApplicationsAdapter.access$602(ManageApplications.ApplicationsAdapter.this, (ArrayList)paramAnonymousFilterResults.values);
        ManageApplications.ApplicationsAdapter.this.notifyDataSetChanged();
        ManageApplications.ApplicationsAdapter.this.mTab.updateStorageUsage();
      }
    };
    private final int mFilterMode;
    private int mLastSortMode = -1;
    private boolean mResumed;
    private final ApplicationsState.Session mSession;
    private final ApplicationsState mState;
    private final ManageApplications.TabInfo mTab;
    private boolean mWaitingForData;
    private int mWhichSize = 0;

    public ApplicationsAdapter(ApplicationsState paramApplicationsState, ManageApplications.TabInfo paramTabInfo, int paramInt)
    {
      this.mState = paramApplicationsState;
      this.mSession = paramApplicationsState.newSession(this);
      this.mTab = paramTabInfo;
      this.mContext = paramTabInfo.mOwner.getActivity();
      this.mFilterMode = paramInt;
    }

    ArrayList<ApplicationsState.AppEntry> applyPrefixFilter(CharSequence paramCharSequence, ArrayList<ApplicationsState.AppEntry> paramArrayList)
    {
      Object localObject;
      if ((paramCharSequence == null) || (paramCharSequence.length() == 0))
        localObject = paramArrayList;
      while (true)
      {
        return localObject;
        String str1 = ApplicationsState.normalize(paramCharSequence.toString());
        String str2 = " " + str1;
        localObject = new ArrayList();
        for (int i = 0; i < paramArrayList.size(); i++)
        {
          ApplicationsState.AppEntry localAppEntry = (ApplicationsState.AppEntry)paramArrayList.get(i);
          String str3 = localAppEntry.getNormalizedLabel();
          if ((str3.startsWith(str1)) || (str3.indexOf(str2) != -1))
            ((ArrayList)localObject).add(localAppEntry);
        }
      }
    }

    public ApplicationsState.AppEntry getAppEntry(int paramInt)
    {
      return (ApplicationsState.AppEntry)this.mEntries.get(paramInt);
    }

    public int getCount()
    {
      if (this.mEntries != null)
        return this.mEntries.size();
      return 0;
    }

    public Filter getFilter()
    {
      return this.mFilter;
    }

    public Object getItem(int paramInt)
    {
      return this.mEntries.get(paramInt);
    }

    public long getItemId(int paramInt)
    {
      return ((ApplicationsState.AppEntry)this.mEntries.get(paramInt)).id;
    }

    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      AppViewHolder localAppViewHolder = AppViewHolder.createOrRecycle(this.mTab.mInflater, paramView);
      View localView = localAppViewHolder.rootView;
      while (true)
      {
        synchronized ((ApplicationsState.AppEntry)this.mEntries.get(paramInt))
        {
          localAppViewHolder.entry = ???;
          if (???.label != null)
            localAppViewHolder.appName.setText(???.label);
          this.mState.ensureIcon(???);
          if (???.icon != null)
            localAppViewHolder.appIcon.setImageDrawable(???.icon);
          localAppViewHolder.updateSizeText(this.mTab.mInvalidSizeStr, this.mWhichSize);
          if ((0x800000 & ???.info.flags) == 0)
          {
            localAppViewHolder.disabled.setVisibility(0);
            localAppViewHolder.disabled.setText(2131428413);
            if (this.mFilterMode != 2)
              break label279;
            localAppViewHolder.checkBox.setVisibility(0);
            CheckBox localCheckBox = localAppViewHolder.checkBox;
            int i = 0x40000 & ???.info.flags;
            boolean bool = false;
            if (i != 0)
              bool = true;
            localCheckBox.setChecked(bool);
            this.mActive.remove(localView);
            this.mActive.add(localView);
            return localView;
          }
          if (!???.info.enabled)
          {
            localAppViewHolder.disabled.setVisibility(0);
            localAppViewHolder.disabled.setText(2131428412);
          }
        }
        localAppViewHolder.disabled.setVisibility(8);
        continue;
        label279: localAppViewHolder.checkBox.setVisibility(8);
      }
    }

    public void onAllSizesComputed()
    {
      if (this.mLastSortMode == 5)
        rebuild(false);
      this.mTab.updateStorageUsage();
    }

    public void onMovedToScrapHeap(View paramView)
    {
      this.mActive.remove(paramView);
    }

    public void onPackageIconChanged()
    {
    }

    public void onPackageListChanged()
    {
      rebuild(false);
    }

    public void onPackageSizeChanged(String paramString)
    {
      for (int i = 0; ; i++)
      {
        AppViewHolder localAppViewHolder;
        if (i < this.mActive.size())
        {
          localAppViewHolder = (AppViewHolder)((View)this.mActive.get(i)).getTag();
          if (!localAppViewHolder.entry.info.packageName.equals(paramString));
        }
        else
        {
          synchronized (localAppViewHolder.entry)
          {
            localAppViewHolder.updateSizeText(this.mTab.mInvalidSizeStr, this.mWhichSize);
            if ((localAppViewHolder.entry.info.packageName.equals(this.mTab.mOwner.mCurrentPkgName)) && (this.mLastSortMode == 5))
              rebuild(false);
            this.mTab.updateStorageUsage();
            return;
          }
        }
      }
    }

    public void onRebuildComplete(ArrayList<ApplicationsState.AppEntry> paramArrayList)
    {
      if (ManageApplications.TabInfo.access$900(this.mTab).getVisibility() == 0)
      {
        ManageApplications.TabInfo.access$900(this.mTab).startAnimation(AnimationUtils.loadAnimation(this.mContext, 17432577));
        ManageApplications.TabInfo.access$800(this.mTab).startAnimation(AnimationUtils.loadAnimation(this.mContext, 17432576));
      }
      ManageApplications.TabInfo.access$800(this.mTab).setVisibility(0);
      ManageApplications.TabInfo.access$900(this.mTab).setVisibility(8);
      this.mWaitingForData = false;
      this.mBaseEntries = paramArrayList;
      this.mEntries = applyPrefixFilter(this.mCurFilterPrefix, this.mBaseEntries);
      notifyDataSetChanged();
      this.mTab.updateStorageUsage();
    }

    public void onRunningStateChanged(boolean paramBoolean)
    {
      this.mTab.mOwner.getActivity().setProgressBarIndeterminateVisibility(paramBoolean);
    }

    public void pause()
    {
      if (this.mResumed)
      {
        this.mResumed = false;
        this.mSession.pause();
      }
    }

    public void rebuild(int paramInt)
    {
      if (paramInt == this.mLastSortMode)
        return;
      this.mLastSortMode = paramInt;
      rebuild(true);
    }

    public void rebuild(boolean paramBoolean)
    {
      boolean bool = Environment.isExternalStorageEmulated();
      ApplicationsState.AppFilter localAppFilter;
      label48: Comparator localComparator;
      if (bool)
      {
        this.mWhichSize = 0;
        switch (this.mFilterMode)
        {
        default:
          localAppFilter = ApplicationsState.ALL_ENABLED_FILTER;
          switch (this.mLastSortMode)
          {
          default:
            localComparator = ApplicationsState.ALPHA_COMPARATOR;
          case 5:
          }
          break;
        case 1:
        case 2:
        case 3:
        }
      }
      ArrayList localArrayList;
      while (true)
      {
        localArrayList = this.mSession.rebuild(localAppFilter, localComparator);
        if ((localArrayList != null) || (paramBoolean))
          break label188;
        return;
        this.mWhichSize = 1;
        break;
        localAppFilter = ApplicationsState.THIRD_PARTY_FILTER;
        break label48;
        localAppFilter = ApplicationsState.ON_SD_CARD_FILTER;
        if (bool)
          break label48;
        this.mWhichSize = 2;
        break label48;
        localAppFilter = ApplicationsState.DISABLED_FILTER;
        break label48;
        switch (this.mWhichSize)
        {
        default:
          localComparator = ApplicationsState.SIZE_COMPARATOR;
          break;
        case 1:
          localComparator = ApplicationsState.INTERNAL_SIZE_COMPARATOR;
          break;
        case 2:
          localComparator = ApplicationsState.EXTERNAL_SIZE_COMPARATOR;
        }
      }
      label188: this.mBaseEntries = localArrayList;
      if (this.mBaseEntries != null);
      for (this.mEntries = applyPrefixFilter(this.mCurFilterPrefix, this.mBaseEntries); ; this.mEntries = null)
      {
        notifyDataSetChanged();
        this.mTab.updateStorageUsage();
        if (localArrayList != null)
          break;
        this.mWaitingForData = true;
        ManageApplications.TabInfo.access$800(this.mTab).setVisibility(4);
        ManageApplications.TabInfo.access$900(this.mTab).setVisibility(0);
        return;
      }
      ManageApplications.TabInfo.access$800(this.mTab).setVisibility(0);
      ManageApplications.TabInfo.access$900(this.mTab).setVisibility(8);
    }

    public void resume(int paramInt)
    {
      if (!this.mResumed)
      {
        this.mResumed = true;
        this.mSession.resume();
        this.mLastSortMode = paramInt;
        rebuild(true);
        return;
      }
      rebuild(paramInt);
    }
  }

  class MyPagerAdapter extends PagerAdapter
    implements ViewPager.OnPageChangeListener
  {
    int mCurPos = 0;

    MyPagerAdapter()
    {
    }

    public void destroyItem(ViewGroup paramViewGroup, int paramInt, Object paramObject)
    {
      paramViewGroup.removeView((View)paramObject);
    }

    public int getCount()
    {
      return ManageApplications.this.mNumTabs;
    }

    public int getItemPosition(Object paramObject)
    {
      return super.getItemPosition(paramObject);
    }

    public CharSequence getPageTitle(int paramInt)
    {
      return ((ManageApplications.TabInfo)ManageApplications.this.mTabs.get(paramInt)).mLabel;
    }

    public Object instantiateItem(ViewGroup paramViewGroup, int paramInt)
    {
      ManageApplications.TabInfo localTabInfo = (ManageApplications.TabInfo)ManageApplications.this.mTabs.get(paramInt);
      View localView = localTabInfo.build(ManageApplications.this.mInflater, ManageApplications.this.mContentContainer, ManageApplications.this.mRootView);
      paramViewGroup.addView(localView);
      localView.setTag(2131230837, localTabInfo);
      return localView;
    }

    public boolean isViewFromObject(View paramView, Object paramObject)
    {
      return paramView == paramObject;
    }

    public void onPageScrollStateChanged(int paramInt)
    {
      if (paramInt == 0)
        ManageApplications.this.updateCurrentTab(this.mCurPos);
    }

    public void onPageScrolled(int paramInt1, float paramFloat, int paramInt2)
    {
    }

    public void onPageSelected(int paramInt)
    {
      this.mCurPos = paramInt;
    }
  }

  public static class TabInfo
    implements AdapterView.OnItemClickListener
  {
    private long mAppStorage = 0L;
    public ManageApplications.ApplicationsAdapter mApplications;
    public final ApplicationsState mApplicationsState;
    public final AppClickListener mClickListener;
    private LinearColorBar mColorBar;
    public final CharSequence mComputingSizeStr;
    private IMediaContainerService mContainerService;
    public final int mFilter;
    private long mFreeStorage = 0L;
    private TextView mFreeStorageText;
    public LayoutInflater mInflater;
    public final CharSequence mInvalidSizeStr;
    public final CharSequence mLabel;
    private long mLastFreeStorage;
    private long mLastUsedStorage;
    private View mListContainer;
    public final int mListType;
    private ListView mListView;
    private View mLoadingContainer;
    public final ManageApplications mOwner;
    public View mRootView;
    final Runnable mRunningProcessesAvail = new Runnable()
    {
      public void run()
      {
        ManageApplications.TabInfo.this.handleRunningProcessesAvail();
      }
    };
    private RunningProcessesView mRunningProcessesView;
    private final Bundle mSavedInstanceState;
    private TextView mStorageChartLabel;
    private long mTotalStorage = 0L;
    private TextView mUsedStorageText;

    public TabInfo(ManageApplications paramManageApplications, ApplicationsState paramApplicationsState, CharSequence paramCharSequence, int paramInt, AppClickListener paramAppClickListener, Bundle paramBundle)
    {
      this.mOwner = paramManageApplications;
      this.mApplicationsState = paramApplicationsState;
      this.mLabel = paramCharSequence;
      this.mListType = paramInt;
      switch (paramInt)
      {
      case 1:
      case 3:
      default:
        this.mFilter = 0;
      case 0:
      case 2:
      case 4:
      }
      while (true)
      {
        this.mClickListener = paramAppClickListener;
        this.mInvalidSizeStr = paramManageApplications.getActivity().getText(2131428438);
        this.mComputingSizeStr = paramManageApplications.getActivity().getText(2131428437);
        this.mSavedInstanceState = paramBundle;
        return;
        this.mFilter = 1;
        continue;
        this.mFilter = 2;
        continue;
        this.mFilter = 3;
      }
    }

    void applyCurrentStorage()
    {
      if (this.mRootView == null);
      do
      {
        BidiFormatter localBidiFormatter;
        do
        {
          return;
          if (this.mTotalStorage <= 0L)
            break;
          localBidiFormatter = BidiFormatter.getInstance();
          this.mColorBar.setRatios((float)(this.mTotalStorage - this.mFreeStorage - this.mAppStorage) / (float)this.mTotalStorage, (float)this.mAppStorage / (float)this.mTotalStorage, (float)this.mFreeStorage / (float)this.mTotalStorage);
          long l = this.mTotalStorage - this.mFreeStorage;
          if (this.mLastUsedStorage != l)
          {
            this.mLastUsedStorage = l;
            String str2 = localBidiFormatter.unicodeWrap(Formatter.formatShortFileSize(this.mOwner.getActivity(), l));
            this.mUsedStorageText.setText(this.mOwner.getActivity().getResources().getString(2131428475, new Object[] { str2 }));
          }
        }
        while (this.mLastFreeStorage == this.mFreeStorage);
        this.mLastFreeStorage = this.mFreeStorage;
        String str1 = localBidiFormatter.unicodeWrap(Formatter.formatShortFileSize(this.mOwner.getActivity(), this.mFreeStorage));
        this.mFreeStorageText.setText(this.mOwner.getActivity().getResources().getString(2131428474, new Object[] { str1 }));
        return;
        this.mColorBar.setRatios(0.0F, 0.0F, 0.0F);
        if (this.mLastUsedStorage != -1L)
        {
          this.mLastUsedStorage = -1L;
          this.mUsedStorageText.setText("");
        }
      }
      while (this.mLastFreeStorage == -1L);
      this.mLastFreeStorage = -1L;
      this.mFreeStorageText.setText("");
    }

    public View build(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, View paramView)
    {
      if (this.mRootView != null)
        return this.mRootView;
      this.mInflater = paramLayoutInflater;
      int i;
      if (this.mListType == 1)
      {
        i = 2130968646;
        this.mRootView = paramLayoutInflater.inflate(i, null);
        this.mLoadingContainer = this.mRootView.findViewById(2131230900);
        this.mLoadingContainer.setVisibility(0);
        this.mListContainer = this.mRootView.findViewById(2131230895);
        if (this.mListContainer != null)
        {
          View localView = this.mListContainer.findViewById(16908292);
          ListView localListView = (ListView)this.mListContainer.findViewById(16908298);
          if (localView != null)
            localListView.setEmptyView(localView);
          localListView.setOnItemClickListener(this);
          localListView.setSaveEnabled(true);
          localListView.setItemsCanFocus(true);
          localListView.setTextFilterEnabled(true);
          this.mListView = localListView;
          this.mApplications = new ManageApplications.ApplicationsAdapter(this.mApplicationsState, this, this.mFilter);
          this.mListView.setAdapter(this.mApplications);
          this.mListView.setRecyclerListener(this.mApplications);
          this.mColorBar = ((LinearColorBar)this.mListContainer.findViewById(2131230896));
          this.mStorageChartLabel = ((TextView)this.mListContainer.findViewById(2131230898));
          this.mUsedStorageText = ((TextView)this.mListContainer.findViewById(2131230897));
          this.mFreeStorageText = ((TextView)this.mListContainer.findViewById(2131230899));
          Utils.prepareCustomPreferencesList(paramViewGroup, paramView, this.mListView, false);
          if (this.mFilter != 2)
            break label341;
          this.mStorageChartLabel.setText(this.mOwner.getActivity().getText(2131428416));
        }
      }
      while (true)
      {
        applyCurrentStorage();
        this.mRunningProcessesView = ((RunningProcessesView)this.mRootView.findViewById(2131230904));
        if (this.mRunningProcessesView != null)
          this.mRunningProcessesView.doCreate(this.mSavedInstanceState);
        return this.mRootView;
        i = 2130968643;
        break;
        label341: this.mStorageChartLabel.setText(this.mOwner.getActivity().getText(2131428415));
      }
    }

    public void detachView()
    {
      if (this.mRootView != null)
      {
        ViewGroup localViewGroup = (ViewGroup)this.mRootView.getParent();
        if (localViewGroup != null)
          localViewGroup.removeView(this.mRootView);
      }
    }

    void handleRunningProcessesAvail()
    {
      this.mLoadingContainer.startAnimation(AnimationUtils.loadAnimation(this.mOwner.getActivity(), 17432577));
      this.mRunningProcessesView.startAnimation(AnimationUtils.loadAnimation(this.mOwner.getActivity(), 17432576));
      this.mRunningProcessesView.setVisibility(0);
      this.mLoadingContainer.setVisibility(8);
    }

    public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
    {
      this.mClickListener.onItemClick(this, paramAdapterView, paramView, paramInt, paramLong);
    }

    public void pause()
    {
      if (this.mApplications != null)
        this.mApplications.pause();
      if (this.mRunningProcessesView != null)
        this.mRunningProcessesView.doPause();
    }

    public void resume(int paramInt)
    {
      if (this.mApplications != null)
        this.mApplications.resume(paramInt);
      if (this.mRunningProcessesView != null)
      {
        if (this.mRunningProcessesView.doResume(this.mOwner, this.mRunningProcessesAvail))
        {
          this.mRunningProcessesView.setVisibility(0);
          this.mLoadingContainer.setVisibility(4);
        }
      }
      else
        return;
      this.mLoadingContainer.setVisibility(0);
    }

    public void setContainerService(IMediaContainerService paramIMediaContainerService)
    {
      this.mContainerService = paramIMediaContainerService;
      updateStorageUsage();
    }

    void updateStorageUsage()
    {
      if (this.mOwner.getActivity() == null);
      while (this.mApplications == null)
        return;
      this.mFreeStorage = 0L;
      this.mAppStorage = 0L;
      this.mTotalStorage = 0L;
      if (this.mFilter == 2)
      {
        if (this.mContainerService != null);
        try
        {
          long[] arrayOfLong2 = this.mContainerService.getFileSystemStats(Environment.getExternalStorageDirectory().getPath());
          this.mTotalStorage = arrayOfLong2[0];
          this.mFreeStorage = arrayOfLong2[1];
          if (this.mApplications == null)
            break label326;
          int k = this.mApplications.getCount();
          for (int m = 0; m < k; m++)
          {
            ApplicationsState.AppEntry localAppEntry2 = this.mApplications.getAppEntry(m);
            this.mAppStorage += localAppEntry2.externalCodeSize + localAppEntry2.externalDataSize + localAppEntry2.externalCacheSize;
          }
        }
        catch (RemoteException localRemoteException2)
        {
          while (true)
            Log.w("ManageApplications", "Problem in container service", localRemoteException2);
        }
      }
      else
      {
        if (this.mContainerService != null);
        try
        {
          long[] arrayOfLong1 = this.mContainerService.getFileSystemStats(Environment.getDataDirectory().getPath());
          this.mTotalStorage = arrayOfLong1[0];
          this.mFreeStorage = arrayOfLong1[1];
          boolean bool = Environment.isExternalStorageEmulated();
          if (this.mApplications != null)
          {
            int i = this.mApplications.getCount();
            for (int j = 0; j < i; j++)
            {
              ApplicationsState.AppEntry localAppEntry1 = this.mApplications.getAppEntry(j);
              this.mAppStorage += localAppEntry1.codeSize + localAppEntry1.dataSize;
              if (bool)
                this.mAppStorage += localAppEntry1.externalCodeSize + localAppEntry1.externalDataSize;
            }
          }
        }
        catch (RemoteException localRemoteException1)
        {
          while (true)
            Log.w("ManageApplications", "Problem in container service", localRemoteException1);
          this.mFreeStorage += this.mApplicationsState.sumCacheSizes();
        }
      }
      label326: applyCurrentStorage();
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.applications.ManageApplications
 * JD-Core Version:    0.6.2
 */