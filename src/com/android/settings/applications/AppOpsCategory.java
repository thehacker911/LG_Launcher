package com.android.settings.applications;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.AsyncTaskLoader;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.pm.ApplicationInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import java.util.List;

public class AppOpsCategory extends ListFragment
  implements LoaderManager.LoaderCallbacks<List<AppOpsState.AppOpEntry>>
{
  AppListAdapter mAdapter;
  String mCurrentPkgName;
  AppOpsState mState;

  public AppOpsCategory()
  {
  }

  public AppOpsCategory(AppOpsState.OpsTemplate paramOpsTemplate)
  {
    Bundle localBundle = new Bundle();
    localBundle.putParcelable("template", paramOpsTemplate);
    setArguments(localBundle);
  }

  private void startApplicationDetailsActivity()
  {
    Bundle localBundle = new Bundle();
    localBundle.putString("package", this.mCurrentPkgName);
    ((PreferenceActivity)getActivity()).startPreferencePanel(AppOpsDetails.class.getName(), localBundle, 2131428462, null, this, 1);
  }

  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    setEmptyText("No applications");
    setHasOptionsMenu(true);
    this.mAdapter = new AppListAdapter(getActivity(), this.mState);
    setListAdapter(this.mAdapter);
    setListShown(false);
    getLoaderManager().initLoader(0, null, this);
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mState = new AppOpsState(getActivity());
  }

  public Loader<List<AppOpsState.AppOpEntry>> onCreateLoader(int paramInt, Bundle paramBundle)
  {
    Bundle localBundle = getArguments();
    AppOpsState.OpsTemplate localOpsTemplate = null;
    if (localBundle != null)
      localOpsTemplate = (AppOpsState.OpsTemplate)localBundle.getParcelable("template");
    return new AppListLoader(getActivity(), this.mState, localOpsTemplate);
  }

  public void onListItemClick(ListView paramListView, View paramView, int paramInt, long paramLong)
  {
    AppOpsState.AppOpEntry localAppOpEntry = this.mAdapter.getItem(paramInt);
    if (localAppOpEntry != null)
    {
      this.mCurrentPkgName = localAppOpEntry.getAppEntry().getApplicationInfo().packageName;
      startApplicationDetailsActivity();
    }
  }

  public void onLoadFinished(Loader<List<AppOpsState.AppOpEntry>> paramLoader, List<AppOpsState.AppOpEntry> paramList)
  {
    this.mAdapter.setData(paramList);
    if (isResumed())
    {
      setListShown(true);
      return;
    }
    setListShownNoAnimation(true);
  }

  public void onLoaderReset(Loader<List<AppOpsState.AppOpEntry>> paramLoader)
  {
    this.mAdapter.setData(null);
  }

  public static class AppListAdapter extends BaseAdapter
  {
    private final LayoutInflater mInflater;
    List<AppOpsState.AppOpEntry> mList;
    private final Resources mResources;
    private final AppOpsState mState;

    public AppListAdapter(Context paramContext, AppOpsState paramAppOpsState)
    {
      this.mResources = paramContext.getResources();
      this.mInflater = ((LayoutInflater)paramContext.getSystemService("layout_inflater"));
      this.mState = paramAppOpsState;
    }

    public int getCount()
    {
      if (this.mList != null)
        return this.mList.size();
      return 0;
    }

    public AppOpsState.AppOpEntry getItem(int paramInt)
    {
      return (AppOpsState.AppOpEntry)this.mList.get(paramInt);
    }

    public long getItemId(int paramInt)
    {
      return paramInt;
    }

    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      if (paramView == null);
      for (View localView = this.mInflater.inflate(2130968584, paramViewGroup, false); ; localView = paramView)
      {
        AppOpsState.AppOpEntry localAppOpEntry = getItem(paramInt);
        ((ImageView)localView.findViewById(2131230735)).setImageDrawable(localAppOpEntry.getAppEntry().getIcon());
        ((TextView)localView.findViewById(2131230736)).setText(localAppOpEntry.getAppEntry().getLabel());
        ((TextView)localView.findViewById(2131230732)).setText(localAppOpEntry.getSummaryText(this.mState));
        ((TextView)localView.findViewById(2131230733)).setText(localAppOpEntry.getTimeText(this.mResources, false));
        return localView;
      }
    }

    public void setData(List<AppOpsState.AppOpEntry> paramList)
    {
      this.mList = paramList;
      notifyDataSetChanged();
    }
  }

  public static class AppListLoader extends AsyncTaskLoader<List<AppOpsState.AppOpEntry>>
  {
    List<AppOpsState.AppOpEntry> mApps;
    final AppOpsCategory.InterestingConfigChanges mLastConfig = new AppOpsCategory.InterestingConfigChanges();
    AppOpsCategory.PackageIntentReceiver mPackageObserver;
    final AppOpsState mState;
    final AppOpsState.OpsTemplate mTemplate;

    public AppListLoader(Context paramContext, AppOpsState paramAppOpsState, AppOpsState.OpsTemplate paramOpsTemplate)
    {
      super();
      this.mState = paramAppOpsState;
      this.mTemplate = paramOpsTemplate;
    }

    public void deliverResult(List<AppOpsState.AppOpEntry> paramList)
    {
      if ((isReset()) && (paramList != null))
        onReleaseResources(paramList);
      this.mApps = paramList;
      if (isStarted())
        super.deliverResult(paramList);
      if (paramList != null)
        onReleaseResources(paramList);
    }

    public List<AppOpsState.AppOpEntry> loadInBackground()
    {
      return this.mState.buildState(this.mTemplate);
    }

    public void onCanceled(List<AppOpsState.AppOpEntry> paramList)
    {
      super.onCanceled(paramList);
      onReleaseResources(paramList);
    }

    protected void onReleaseResources(List<AppOpsState.AppOpEntry> paramList)
    {
    }

    protected void onReset()
    {
      super.onReset();
      onStopLoading();
      if (this.mApps != null)
      {
        onReleaseResources(this.mApps);
        this.mApps = null;
      }
      if (this.mPackageObserver != null)
      {
        getContext().unregisterReceiver(this.mPackageObserver);
        this.mPackageObserver = null;
      }
    }

    protected void onStartLoading()
    {
      onContentChanged();
      if (this.mApps != null)
        deliverResult(this.mApps);
      if (this.mPackageObserver == null)
        this.mPackageObserver = new AppOpsCategory.PackageIntentReceiver(this);
      boolean bool = this.mLastConfig.applyNewConfig(getContext().getResources());
      if ((takeContentChanged()) || (this.mApps == null) || (bool))
        forceLoad();
    }

    protected void onStopLoading()
    {
      cancelLoad();
    }
  }

  public static class InterestingConfigChanges
  {
    final Configuration mLastConfiguration = new Configuration();
    int mLastDensity;

    boolean applyNewConfig(Resources paramResources)
    {
      int i = this.mLastConfiguration.updateFrom(paramResources.getConfiguration());
      if (this.mLastDensity != paramResources.getDisplayMetrics().densityDpi);
      for (int j = 1; ; j = 0)
      {
        boolean bool;
        if (j == 0)
        {
          int k = i & 0x304;
          bool = false;
          if (k == 0);
        }
        else
        {
          this.mLastDensity = paramResources.getDisplayMetrics().densityDpi;
          bool = true;
        }
        return bool;
      }
    }
  }

  public static class PackageIntentReceiver extends BroadcastReceiver
  {
    final AppOpsCategory.AppListLoader mLoader;

    public PackageIntentReceiver(AppOpsCategory.AppListLoader paramAppListLoader)
    {
      this.mLoader = paramAppListLoader;
      IntentFilter localIntentFilter1 = new IntentFilter("android.intent.action.PACKAGE_ADDED");
      localIntentFilter1.addAction("android.intent.action.PACKAGE_REMOVED");
      localIntentFilter1.addAction("android.intent.action.PACKAGE_CHANGED");
      localIntentFilter1.addDataScheme("package");
      this.mLoader.getContext().registerReceiver(this, localIntentFilter1);
      IntentFilter localIntentFilter2 = new IntentFilter();
      localIntentFilter2.addAction("android.intent.action.EXTERNAL_APPLICATIONS_AVAILABLE");
      localIntentFilter2.addAction("android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE");
      this.mLoader.getContext().registerReceiver(this, localIntentFilter2);
    }

    public void onReceive(Context paramContext, Intent paramIntent)
    {
      this.mLoader.onContentChanged();
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.applications.AppOpsCategory
 * JD-Core Version:    0.6.2
 */