package com.android.settings;

import android.app.Activity;
import android.content.ContextWrapper;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import com.android.internal.app.IUsageStats;
import com.android.internal.app.IUsageStats.Stub;
import com.android.internal.os.PkgUsageStats;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsageStats extends Activity
  implements AdapterView.OnItemSelectedListener
{
  private UsageStatsAdapter mAdapter;
  private LayoutInflater mInflater;
  private ListView mListView;
  private PackageManager mPm;
  private Spinner mTypeSpinner;
  private IUsageStats mUsageStatsService;

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mUsageStatsService = IUsageStats.Stub.asInterface(ServiceManager.getService("usagestats"));
    if (this.mUsageStatsService == null)
    {
      Log.e("UsageStatsActivity", "Failed to retrieve usagestats service");
      return;
    }
    this.mInflater = ((LayoutInflater)getSystemService("layout_inflater"));
    this.mPm = getPackageManager();
    setContentView(2130968721);
    this.mTypeSpinner = ((Spinner)findViewById(2131231091));
    this.mTypeSpinner.setOnItemSelectedListener(this);
    this.mListView = ((ListView)findViewById(2131231092));
    this.mAdapter = new UsageStatsAdapter();
    this.mListView.setAdapter(this.mAdapter);
  }

  public void onItemSelected(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
  {
    this.mAdapter.sortList(paramInt);
  }

  public void onNothingSelected(AdapterView<?> paramAdapterView)
  {
  }

  public static class AppNameComparator
    implements Comparator<PkgUsageStats>
  {
    Map<String, CharSequence> mAppLabelList;

    AppNameComparator(Map<String, CharSequence> paramMap)
    {
      this.mAppLabelList = paramMap;
    }

    public final int compare(PkgUsageStats paramPkgUsageStats1, PkgUsageStats paramPkgUsageStats2)
    {
      return ((CharSequence)this.mAppLabelList.get(paramPkgUsageStats1.packageName)).toString().compareTo(((CharSequence)this.mAppLabelList.get(paramPkgUsageStats2.packageName)).toString());
    }
  }

  static class AppViewHolder
  {
    TextView launchCount;
    TextView pkgName;
    TextView usageTime;
  }

  public static class LaunchCountComparator
    implements Comparator<PkgUsageStats>
  {
    public final int compare(PkgUsageStats paramPkgUsageStats1, PkgUsageStats paramPkgUsageStats2)
    {
      return paramPkgUsageStats2.launchCount - paramPkgUsageStats1.launchCount;
    }
  }

  class UsageStatsAdapter extends BaseAdapter
  {
    private UsageStats.AppNameComparator mAppLabelComparator;
    private HashMap<String, CharSequence> mAppLabelMap = new HashMap();
    private int mDisplayOrder = 0;
    private UsageStats.LaunchCountComparator mLaunchCountComparator;
    private List<PkgUsageStats> mUsageStats = new ArrayList();
    private UsageStats.UsageTimeComparator mUsageTimeComparator;

    UsageStatsAdapter()
    {
      PkgUsageStats[] arrayOfPkgUsageStats;
      try
      {
        arrayOfPkgUsageStats = UsageStats.this.mUsageStatsService.getAllPkgUsageStats();
        if (arrayOfPkgUsageStats == null)
          return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.e("UsageStatsActivity", "Failed initializing usage stats service");
        return;
      }
      int i = arrayOfPkgUsageStats.length;
      int j = 0;
      while (true)
        if (j < i)
        {
          PkgUsageStats localPkgUsageStats = arrayOfPkgUsageStats[j];
          this.mUsageStats.add(localPkgUsageStats);
          try
          {
            CharSequence localCharSequence = UsageStats.this.mPm.getApplicationInfo(localPkgUsageStats.packageName, 0).loadLabel(UsageStats.this.mPm);
            localObject = localCharSequence;
            this.mAppLabelMap.put(localPkgUsageStats.packageName, localObject);
            j++;
          }
          catch (PackageManager.NameNotFoundException localNameNotFoundException)
          {
            while (true)
              Object localObject = localPkgUsageStats.packageName;
          }
        }
      this.mLaunchCountComparator = new UsageStats.LaunchCountComparator();
      this.mUsageTimeComparator = new UsageStats.UsageTimeComparator();
      this.mAppLabelComparator = new UsageStats.AppNameComparator(this.mAppLabelMap);
      sortList();
    }

    private void sortList()
    {
      if (this.mDisplayOrder == 0)
        Collections.sort(this.mUsageStats, this.mUsageTimeComparator);
      while (true)
      {
        notifyDataSetChanged();
        return;
        if (this.mDisplayOrder == 1)
          Collections.sort(this.mUsageStats, this.mLaunchCountComparator);
        else if (this.mDisplayOrder == 2)
          Collections.sort(this.mUsageStats, this.mAppLabelComparator);
      }
    }

    public int getCount()
    {
      return this.mUsageStats.size();
    }

    public Object getItem(int paramInt)
    {
      return this.mUsageStats.get(paramInt);
    }

    public long getItemId(int paramInt)
    {
      return paramInt;
    }

    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      UsageStats.AppViewHolder localAppViewHolder;
      if (paramView == null)
      {
        paramView = UsageStats.this.mInflater.inflate(2130968722, null);
        localAppViewHolder = new UsageStats.AppViewHolder();
        localAppViewHolder.pkgName = ((TextView)paramView.findViewById(2131231093));
        localAppViewHolder.launchCount = ((TextView)paramView.findViewById(2131231094));
        localAppViewHolder.usageTime = ((TextView)paramView.findViewById(2131231095));
        paramView.setTag(localAppViewHolder);
      }
      while (true)
      {
        PkgUsageStats localPkgUsageStats = (PkgUsageStats)this.mUsageStats.get(paramInt);
        if (localPkgUsageStats == null)
          break;
        CharSequence localCharSequence = (CharSequence)this.mAppLabelMap.get(localPkgUsageStats.packageName);
        localAppViewHolder.pkgName.setText(localCharSequence);
        localAppViewHolder.launchCount.setText(String.valueOf(localPkgUsageStats.launchCount));
        localAppViewHolder.usageTime.setText(String.valueOf(localPkgUsageStats.usageTime) + " ms");
        return paramView;
        localAppViewHolder = (UsageStats.AppViewHolder)paramView.getTag();
      }
      Log.w("UsageStatsActivity", "No usage stats info for package:" + paramInt);
      return paramView;
    }

    void sortList(int paramInt)
    {
      if (this.mDisplayOrder == paramInt)
        return;
      this.mDisplayOrder = paramInt;
      sortList();
    }
  }

  public static class UsageTimeComparator
    implements Comparator<PkgUsageStats>
  {
    public final int compare(PkgUsageStats paramPkgUsageStats1, PkgUsageStats paramPkgUsageStats2)
    {
      long l = paramPkgUsageStats1.usageTime - paramPkgUsageStats2.usageTime;
      if (l == 0L)
        return 0;
      if (l < 0L)
        return 1;
      return -1;
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.UsageStats
 * JD-Core Version:    0.6.2
 */