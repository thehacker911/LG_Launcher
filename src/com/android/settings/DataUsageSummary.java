package com.android.settings;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.INetworkStatsService;
import android.net.INetworkStatsService.Stub;
import android.net.INetworkStatsSession;
import android.net.NetworkPolicy;
import android.net.NetworkPolicyManager;
import android.net.NetworkStats;
import android.net.NetworkStats.Entry;
import android.net.NetworkStatsHistory;
import android.net.NetworkStatsHistory.Entry;
import android.net.NetworkTemplate;
import android.net.TrafficStats;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.INetworkManagementService;
import android.os.INetworkManagementService.Stub;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.preference.PreferenceActivity;
import android.provider.Settings.Global;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;
import com.android.internal.util.Preconditions;
import com.android.settings.drawable.InsetBoundsDrawable;
import com.android.settings.net.ChartData;
import com.android.settings.net.ChartDataLoader;
import com.android.settings.net.DataUsageMeteredSettings;
import com.android.settings.net.NetworkPolicyEditor;
import com.android.settings.net.SummaryForAllUidLoader;
import com.android.settings.net.UidDetail;
import com.android.settings.net.UidDetailProvider;
import com.android.settings.widget.ChartDataUsageView;
import com.android.settings.widget.ChartDataUsageView.DataUsageChartListener;
import com.android.settings.widget.PieChartView;
import com.google.android.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import libcore.util.Objects;

public class DataUsageSummary extends Fragment
{
  private static final StringBuilder sBuilder = new StringBuilder(50);
  private static final java.util.Formatter sFormatter = new java.util.Formatter(sBuilder, Locale.getDefault());
  private DataUsageAdapter mAdapter;
  private TextView mAppBackground;
  private View mAppDetail;
  private TextView mAppForeground;
  private ImageView mAppIcon;
  private PieChartView mAppPieChart;
  private CheckBox mAppRestrict;
  private View.OnClickListener mAppRestrictListener = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      if (!DataUsageSummary.this.mAppRestrict.isChecked());
      for (int i = 1; i != 0; i = 0)
      {
        DataUsageSummary.ConfirmAppRestrictFragment.show(DataUsageSummary.this);
        return;
      }
      DataUsageSummary.this.setAppRestrictBackground(false);
    }
  };
  private View mAppRestrictView;
  private Button mAppSettings;
  private Intent mAppSettingsIntent;
  private View.OnClickListener mAppSettingsListener = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      if (!DataUsageSummary.this.isAdded())
        return;
      DataUsageSummary.this.startActivity(DataUsageSummary.this.mAppSettingsIntent);
    }
  };
  private LinearLayout mAppSwitches;
  private ViewGroup mAppTitles;
  private boolean mBinding;
  private ChartDataUsageView mChart;
  private ChartData mChartData;
  private final LoaderManager.LoaderCallbacks<ChartData> mChartDataCallbacks = new LoaderManager.LoaderCallbacks()
  {
    public Loader<ChartData> onCreateLoader(int paramAnonymousInt, Bundle paramAnonymousBundle)
    {
      return new ChartDataLoader(DataUsageSummary.this.getActivity(), DataUsageSummary.this.mStatsSession, paramAnonymousBundle);
    }

    public void onLoadFinished(Loader<ChartData> paramAnonymousLoader, ChartData paramAnonymousChartData)
    {
      DataUsageSummary.access$1702(DataUsageSummary.this, paramAnonymousChartData);
      DataUsageSummary.this.mChart.bindNetworkStats(DataUsageSummary.this.mChartData.network);
      DataUsageSummary.this.mChart.bindDetailNetworkStats(DataUsageSummary.this.mChartData.detail);
      DataUsageSummary.this.updatePolicy(true);
      DataUsageSummary.this.updateAppDetail();
      if (DataUsageSummary.this.mChartData.detail != null)
        DataUsageSummary.this.mListView.smoothScrollToPosition(0);
    }

    public void onLoaderReset(Loader<ChartData> paramAnonymousLoader)
    {
      DataUsageSummary.access$1702(DataUsageSummary.this, null);
      DataUsageSummary.this.mChart.bindNetworkStats(null);
      DataUsageSummary.this.mChart.bindDetailNetworkStats(null);
    }
  };
  private ChartDataUsageView.DataUsageChartListener mChartListener = new ChartDataUsageView.DataUsageChartListener()
  {
    public void onInspectRangeChanged()
    {
      DataUsageSummary.this.updateDetailData();
    }

    public void onLimitChanged()
    {
      DataUsageSummary.this.setPolicyLimitBytes(DataUsageSummary.this.mChart.getLimitBytes());
    }

    public void onWarningChanged()
    {
      DataUsageSummary.this.setPolicyWarningBytes(DataUsageSummary.this.mChart.getWarningBytes());
    }

    public void requestLimitEdit()
    {
      DataUsageSummary.LimitEditorFragment.show(DataUsageSummary.this);
    }

    public void requestWarningEdit()
    {
      DataUsageSummary.WarningEditorFragment.show(DataUsageSummary.this);
    }
  };
  private ConnectivityManager mConnService;
  private AppItem mCurrentApp = null;
  private String mCurrentTab = null;
  private CycleAdapter mCycleAdapter;
  private AdapterView.OnItemSelectedListener mCycleListener = new AdapterView.OnItemSelectedListener()
  {
    public void onItemSelected(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
    {
      DataUsageSummary.CycleItem localCycleItem = (DataUsageSummary.CycleItem)paramAnonymousAdapterView.getItemAtPosition(paramAnonymousInt);
      if ((localCycleItem instanceof DataUsageSummary.CycleChangeItem))
      {
        DataUsageSummary.CycleEditorFragment.show(DataUsageSummary.this);
        DataUsageSummary.this.mCycleSpinner.setSelection(0);
        return;
      }
      DataUsageSummary.this.mChart.setVisibleRange(localCycleItem.start, localCycleItem.end);
      DataUsageSummary.this.updateDetailData();
    }

    public void onNothingSelected(AdapterView<?> paramAnonymousAdapterView)
    {
    }
  };
  private Spinner mCycleSpinner;
  private View mCycleView;
  private Switch mDataEnabled;
  private CompoundButton.OnCheckedChangeListener mDataEnabledListener = new CompoundButton.OnCheckedChangeListener()
  {
    public void onCheckedChanged(CompoundButton paramAnonymousCompoundButton, boolean paramAnonymousBoolean)
    {
      if (DataUsageSummary.this.mBinding)
        return;
      if ("mobile".equals(DataUsageSummary.this.mCurrentTab))
      {
        if (!paramAnonymousBoolean)
          break label47;
        DataUsageSummary.this.setMobileDataEnabled(true);
      }
      while (true)
      {
        DataUsageSummary.this.updatePolicy(false);
        return;
        label47: DataUsageSummary.ConfirmDataDisableFragment.show(DataUsageSummary.this);
      }
    }
  };
  private View mDataEnabledView;
  private CheckBox mDisableAtLimit;
  private View.OnClickListener mDisableAtLimitListener = new View.OnClickListener()
  {
    public void onClick(View paramAnonymousView)
    {
      if (!DataUsageSummary.this.mDisableAtLimit.isChecked());
      for (int i = 1; i != 0; i = 0)
      {
        DataUsageSummary.ConfirmLimitFragment.show(DataUsageSummary.this);
        return;
      }
      DataUsageSummary.this.setPolicyLimitBytes(-1L);
    }
  };
  private View mDisableAtLimitView;
  private TextView mEmpty;
  private TabHost.TabContentFactory mEmptyTabContent = new TabHost.TabContentFactory()
  {
    public View createTabContent(String paramAnonymousString)
    {
      return new View(DataUsageSummary.this.mTabHost.getContext());
    }
  };
  private ViewGroup mHeader;
  private int mInsetSide = 0;
  private String mIntentTab = null;
  private AdapterView.OnItemClickListener mListListener = new AdapterView.OnItemClickListener()
  {
    public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
    {
      paramAnonymousView.getContext();
      DataUsageSummary.AppItem localAppItem = (DataUsageSummary.AppItem)paramAnonymousAdapterView.getItemAtPosition(paramAnonymousInt);
      if ((DataUsageSummary.this.mUidDetailProvider == null) || (localAppItem == null))
        return;
      UidDetail localUidDetail = DataUsageSummary.this.mUidDetailProvider.getUidDetail(localAppItem.key, true);
      DataUsageSummary.AppDetailsFragment.show(DataUsageSummary.this, localAppItem, localUidDetail.label);
    }
  };
  private ListView mListView;
  private MenuItem mMenuAutoSync;
  private MenuItem mMenuDataRoaming;
  private MenuItem mMenuRestrictBackground;
  private Boolean mMobileDataEnabled;
  private INetworkManagementService mNetworkService;
  private LinearLayout mNetworkSwitches;
  private ViewGroup mNetworkSwitchesContainer;
  private NetworkPolicyEditor mPolicyEditor;
  private NetworkPolicyManager mPolicyManager;
  private SharedPreferences mPrefs;
  private boolean mShowEthernet = false;
  private boolean mShowWifi = false;
  private INetworkStatsService mStatsService;
  private INetworkStatsSession mStatsSession;
  private final LoaderManager.LoaderCallbacks<NetworkStats> mSummaryCallbacks = new LoaderManager.LoaderCallbacks()
  {
    private void updateEmptyVisible()
    {
      int i;
      TextView localTextView;
      int j;
      if ((DataUsageSummary.this.mAdapter.isEmpty()) && (!DataUsageSummary.this.isAppDetailMode()))
      {
        i = 1;
        localTextView = DataUsageSummary.this.mEmpty;
        j = 0;
        if (i == 0)
          break label50;
      }
      while (true)
      {
        localTextView.setVisibility(j);
        return;
        i = 0;
        break;
        label50: j = 8;
      }
    }

    public Loader<NetworkStats> onCreateLoader(int paramAnonymousInt, Bundle paramAnonymousBundle)
    {
      return new SummaryForAllUidLoader(DataUsageSummary.this.getActivity(), DataUsageSummary.this.mStatsSession, paramAnonymousBundle);
    }

    public void onLoadFinished(Loader<NetworkStats> paramAnonymousLoader, NetworkStats paramAnonymousNetworkStats)
    {
      int[] arrayOfInt = DataUsageSummary.this.mPolicyManager.getUidsWithPolicy(1);
      DataUsageSummary.this.mAdapter.bindStats(paramAnonymousNetworkStats, arrayOfInt);
      updateEmptyVisible();
    }

    public void onLoaderReset(Loader<NetworkStats> paramAnonymousLoader)
    {
      DataUsageSummary.this.mAdapter.bindStats(null, new int[0]);
      updateEmptyVisible();
    }
  };
  private TabHost mTabHost;
  private TabHost.OnTabChangeListener mTabListener = new TabHost.OnTabChangeListener()
  {
    public void onTabChanged(String paramAnonymousString)
    {
      DataUsageSummary.this.updateBody();
    }
  };
  private TabWidget mTabWidget;
  private ViewGroup mTabsContainer;
  private NetworkTemplate mTemplate;
  private UidDetailProvider mUidDetailProvider;
  private TextView mUsageSummary;

  private static LayoutTransition buildLayoutTransition()
  {
    LayoutTransition localLayoutTransition = new LayoutTransition();
    localLayoutTransition.setAnimateParentHierarchy(false);
    return localLayoutTransition;
  }

  private TabHost.TabSpec buildTabSpec(String paramString, int paramInt)
  {
    return this.mTabHost.newTabSpec(paramString).setIndicator(getText(paramInt)).setContent(this.mEmptyTabContent);
  }

  private static String computeTabFromIntent(Intent paramIntent)
  {
    NetworkTemplate localNetworkTemplate = (NetworkTemplate)paramIntent.getParcelableExtra("android.net.NETWORK_TEMPLATE");
    if (localNetworkTemplate == null)
      return null;
    switch (localNetworkTemplate.getMatchRule())
    {
    default:
      return null;
    case 2:
      return "3g";
    case 3:
      return "4g";
    case 1:
      return "mobile";
    case 4:
    }
    return "wifi";
  }

  private void ensureLayoutTransitions()
  {
    if (this.mChart.getLayoutTransition() != null)
      return;
    this.mTabsContainer.setLayoutTransition(buildLayoutTransition());
    this.mHeader.setLayoutTransition(buildLayoutTransition());
    this.mNetworkSwitchesContainer.setLayoutTransition(buildLayoutTransition());
    LayoutTransition localLayoutTransition = buildLayoutTransition();
    localLayoutTransition.disableTransitionType(2);
    localLayoutTransition.disableTransitionType(3);
    this.mChart.setLayoutTransition(localLayoutTransition);
  }

  public static String formatDateRange(Context paramContext, long paramLong1, long paramLong2)
  {
    synchronized (sBuilder)
    {
      sBuilder.setLength(0);
      String str = DateUtils.formatDateRange(paramContext, sFormatter, paramLong1, paramLong2, 65552, null).toString();
      return str;
    }
  }

  private static String getActiveSubscriberId(Context paramContext)
  {
    return SystemProperties.get("test.subscriberid", TelephonyManager.from(paramContext).getSubscriberId());
  }

  private boolean getAppRestrictBackground()
  {
    int i = this.mCurrentApp.key;
    return (0x1 & this.mPolicyManager.getUidPolicy(i)) != 0;
  }

  private boolean getDataRoaming()
  {
    int i = Settings.Global.getInt(getActivity().getContentResolver(), "data_roaming", 0);
    boolean bool = false;
    if (i != 0)
      bool = true;
    return bool;
  }

  public static boolean hasReadyMobile4gRadio(Context paramContext)
  {
    return false;
  }

  public static boolean hasReadyMobileRadio(Context paramContext)
  {
    ConnectivityManager localConnectivityManager = ConnectivityManager.from(paramContext);
    TelephonyManager localTelephonyManager = TelephonyManager.from(paramContext);
    boolean bool1 = localConnectivityManager.isNetworkSupported(0);
    boolean bool2 = false;
    if (bool1)
    {
      int i = localTelephonyManager.getSimState();
      bool2 = false;
      if (i == 5)
        bool2 = true;
    }
    return bool2;
  }

  public static boolean hasWifiRadio(Context paramContext)
  {
    return ConnectivityManager.from(paramContext).isNetworkSupported(1);
  }

  private static View inflateAppTitle(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, CharSequence paramCharSequence)
  {
    TextView localTextView = (TextView)paramLayoutInflater.inflate(2130968611, paramViewGroup, false);
    localTextView.setText(paramCharSequence);
    return localTextView;
  }

  private static View inflatePreference(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, View paramView)
  {
    View localView = paramLayoutInflater.inflate(2130968664, paramViewGroup, false);
    ((LinearLayout)localView.findViewById(16908312)).addView(paramView, new LinearLayout.LayoutParams(-2, -2));
    return localView;
  }

  private static void insetListViewDrawables(ListView paramListView, int paramInt)
  {
    Drawable localDrawable1 = paramListView.getSelector();
    Drawable localDrawable2 = paramListView.getDivider();
    ColorDrawable localColorDrawable = new ColorDrawable(0);
    paramListView.setSelector(localColorDrawable);
    paramListView.setDivider(localColorDrawable);
    paramListView.setSelector(new InsetBoundsDrawable(localDrawable1, paramInt));
    paramListView.setDivider(new InsetBoundsDrawable(localDrawable2, paramInt));
  }

  private boolean isAppDetailMode()
  {
    return this.mCurrentApp != null;
  }

  private boolean isBandwidthControlEnabled()
  {
    try
    {
      boolean bool = this.mNetworkService.isBandwidthControlEnabled();
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      Log.w("DataUsage", "problem talking with INetworkManagementService: " + localRemoteException);
    }
    return false;
  }

  private boolean isMobileDataEnabled()
  {
    if (this.mMobileDataEnabled != null)
      return this.mMobileDataEnabled.booleanValue();
    return this.mConnService.getMobileDataEnabled();
  }

  @Deprecated
  private boolean isMobilePolicySplit()
  {
    Activity localActivity = getActivity();
    if (hasReadyMobileRadio(localActivity))
    {
      TelephonyManager.from(localActivity);
      return this.mPolicyEditor.isMobilePolicySplit(getActiveSubscriberId(localActivity));
    }
    return false;
  }

  private boolean isNetworkPolicyModifiable(NetworkPolicy paramNetworkPolicy)
  {
    return (paramNetworkPolicy != null) && (isBandwidthControlEnabled()) && (this.mDataEnabled.isChecked()) && (ActivityManager.getCurrentUser() == 0);
  }

  private void setAppRestrictBackground(boolean paramBoolean)
  {
    int i = this.mCurrentApp.key;
    NetworkPolicyManager localNetworkPolicyManager = this.mPolicyManager;
    if (paramBoolean);
    for (int j = 1; ; j = 0)
    {
      localNetworkPolicyManager.setUidPolicy(i, j);
      this.mAppRestrict.setChecked(paramBoolean);
      return;
    }
  }

  private void setDataRoaming(boolean paramBoolean)
  {
    ContentResolver localContentResolver = getActivity().getContentResolver();
    if (paramBoolean);
    for (int i = 1; ; i = 0)
    {
      Settings.Global.putInt(localContentResolver, "data_roaming", i);
      this.mMenuDataRoaming.setChecked(paramBoolean);
      return;
    }
  }

  private void setMobileDataEnabled(boolean paramBoolean)
  {
    this.mConnService.setMobileDataEnabled(paramBoolean);
    this.mMobileDataEnabled = Boolean.valueOf(paramBoolean);
    updatePolicy(false);
  }

  @Deprecated
  private void setMobilePolicySplit(boolean paramBoolean)
  {
    Activity localActivity = getActivity();
    if (hasReadyMobileRadio(localActivity))
    {
      TelephonyManager.from(localActivity);
      this.mPolicyEditor.setMobilePolicySplit(getActiveSubscriberId(localActivity), paramBoolean);
    }
  }

  private void setPolicyLimitBytes(long paramLong)
  {
    this.mPolicyEditor.setPolicyLimitBytes(this.mTemplate, paramLong);
    updatePolicy(false);
  }

  private void setPolicyWarningBytes(long paramLong)
  {
    this.mPolicyEditor.setPolicyWarningBytes(this.mTemplate, paramLong);
    updatePolicy(false);
  }

  private static void setPreferenceSummary(View paramView, CharSequence paramCharSequence)
  {
    TextView localTextView = (TextView)paramView.findViewById(16908304);
    localTextView.setVisibility(0);
    localTextView.setText(paramCharSequence);
  }

  private static void setPreferenceTitle(View paramView, int paramInt)
  {
    ((TextView)paramView.findViewById(16908310)).setText(paramInt);
  }

  private void updateAppDetail()
  {
    boolean bool = true;
    Activity localActivity = getActivity();
    PackageManager localPackageManager = localActivity.getPackageManager();
    LayoutInflater localLayoutInflater = getActivity().getLayoutInflater();
    int i;
    UidDetail localUidDetail;
    if (isAppDetailMode())
    {
      this.mAppDetail.setVisibility(0);
      this.mCycleAdapter.setChangeVisible(false);
      this.mChart.bindNetworkPolicy(null);
      i = this.mCurrentApp.key;
      localUidDetail = this.mUidDetailProvider.getUidDetail(i, bool);
      this.mAppIcon.setImageDrawable(localUidDetail.icon);
      this.mAppTitles.removeAllViews();
      if (localUidDetail.detailLabels != null)
        for (CharSequence localCharSequence : localUidDetail.detailLabels)
          this.mAppTitles.addView(inflateAppTitle(localLayoutInflater, this.mAppTitles, localCharSequence));
    }
    else
    {
      this.mAppDetail.setVisibility(8);
      this.mCycleAdapter.setChangeVisible(bool);
      this.mChart.bindDetailNetworkStats(null);
      return;
    }
    this.mAppTitles.addView(inflateAppTitle(localLayoutInflater, this.mAppTitles, localUidDetail.label));
    String[] arrayOfString = localPackageManager.getPackagesForUid(i);
    int k;
    if ((arrayOfString != null) && (arrayOfString.length > 0))
    {
      this.mAppSettingsIntent = new Intent("android.intent.action.MANAGE_NETWORK_USAGE");
      this.mAppSettingsIntent.addCategory("android.intent.category.DEFAULT");
      int j = arrayOfString.length;
      k = 0;
      if (k >= j)
        break label417;
      String str = arrayOfString[k];
      this.mAppSettingsIntent.setPackage(str);
      if (localPackageManager.resolveActivity(this.mAppSettingsIntent, 0) == null);
    }
    while (true)
    {
      this.mAppSettings.setEnabled(bool);
      this.mAppSettings.setVisibility(0);
      while (true)
      {
        updateDetailData();
        if ((!UserHandle.isApp(i)) || (this.mPolicyManager.getRestrictBackground()) || (!isBandwidthControlEnabled()) || (!hasReadyMobileRadio(localActivity)))
          break label407;
        setPreferenceTitle(this.mAppRestrictView, 2131429102);
        setPreferenceSummary(this.mAppRestrictView, getString(2131429103));
        this.mAppRestrictView.setVisibility(0);
        this.mAppRestrict.setChecked(getAppRestrictBackground());
        return;
        k++;
        break;
        this.mAppSettingsIntent = null;
        this.mAppSettings.setVisibility(8);
      }
      label407: this.mAppRestrictView.setVisibility(8);
      return;
      label417: bool = false;
    }
  }

  private void updateBody()
  {
    boolean bool = true;
    this.mBinding = bool;
    if (!isAdded())
      return;
    Activity localActivity = getActivity();
    String str = this.mTabHost.getCurrentTabTag();
    if (ActivityManager.getCurrentUser() == 0);
    while (str == null)
    {
      Log.w("DataUsage", "no tab selected; hiding body");
      this.mListView.setVisibility(8);
      return;
      bool = false;
    }
    this.mListView.setVisibility(0);
    if (!str.equals(this.mCurrentTab));
    this.mCurrentTab = str;
    View localView = this.mDataEnabledView;
    int i;
    if (bool)
    {
      i = 0;
      localView.setVisibility(i);
      TelephonyManager.from(localActivity);
      if (!"mobile".equals(str))
        break label197;
      setPreferenceTitle(this.mDataEnabledView, 2131429098);
      setPreferenceTitle(this.mDisableAtLimitView, 2131429087);
      this.mTemplate = NetworkTemplate.buildTemplateMobileAll(getActiveSubscriberId(localActivity));
    }
    while (true)
    {
      getLoaderManager().restartLoader(2, ChartDataLoader.buildArgs(this.mTemplate, this.mCurrentApp), this.mChartDataCallbacks);
      getActivity().invalidateOptionsMenu();
      this.mBinding = false;
      return;
      i = 8;
      break;
      label197: if ("3g".equals(str))
      {
        setPreferenceTitle(this.mDataEnabledView, 2131429099);
        setPreferenceTitle(this.mDisableAtLimitView, 2131429089);
        this.mTemplate = NetworkTemplate.buildTemplateMobile3gLower(getActiveSubscriberId(localActivity));
      }
      else if ("4g".equals(str))
      {
        setPreferenceTitle(this.mDataEnabledView, 2131429100);
        setPreferenceTitle(this.mDisableAtLimitView, 2131429088);
        this.mTemplate = NetworkTemplate.buildTemplateMobile4g(getActiveSubscriberId(localActivity));
      }
      else if ("wifi".equals(str))
      {
        this.mDataEnabledView.setVisibility(8);
        this.mDisableAtLimitView.setVisibility(8);
        this.mTemplate = NetworkTemplate.buildTemplateWifiWildcard();
      }
      else
      {
        if (!"ethernet".equals(str))
          break label361;
        this.mDataEnabledView.setVisibility(8);
        this.mDisableAtLimitView.setVisibility(8);
        this.mTemplate = NetworkTemplate.buildTemplateEthernet();
      }
    }
    label361: throw new IllegalStateException("unknown tab: " + str);
  }

  private void updateCycleList(NetworkPolicy paramNetworkPolicy)
  {
    CycleItem localCycleItem = (CycleItem)this.mCycleSpinner.getSelectedItem();
    this.mCycleAdapter.clear();
    Context localContext = this.mCycleSpinner.getContext();
    long l2;
    long l1;
    if (this.mChartData != null)
    {
      l2 = this.mChartData.network.getStart();
      l1 = this.mChartData.network.getEnd();
    }
    while (true)
    {
      long l3 = System.currentTimeMillis();
      if (l2 == 9223372036854775807L);
      for (long l4 = l3; ; l4 = l2)
      {
        if (l1 == -9223372036854775808L);
        for (long l5 = l3 + 1L; ; l5 = l1)
        {
          int i;
          if (paramNetworkPolicy != null)
          {
            long l8 = NetworkPolicyManager.computeNextCycleBoundary(l5, paramNetworkPolicy);
            i = 0;
            while (l8 > l4)
            {
              long l9 = NetworkPolicyManager.computeLastCycleBoundary(l8, paramNetworkPolicy);
              Log.d("DataUsage", "generating cs=" + l9 + " to ce=" + l8 + " waiting for hs=" + l4);
              this.mCycleAdapter.add(new CycleItem(localContext, l9, l8));
              i = 1;
              l8 = l9;
            }
            this.mCycleAdapter.setChangePossible(isNetworkPolicyModifiable(paramNetworkPolicy));
          }
          while (true)
          {
            if (i == 0)
            {
              long l7;
              for (long l6 = l5; l6 > l4; l6 = l7)
              {
                l7 = l6 - 2419200000L;
                this.mCycleAdapter.add(new CycleItem(localContext, l7, l6));
              }
              this.mCycleAdapter.setChangePossible(false);
            }
            if (this.mCycleAdapter.getCount() > 0)
            {
              int j = this.mCycleAdapter.findNearestPosition(localCycleItem);
              this.mCycleSpinner.setSelection(j);
              if (!Objects.equal((CycleItem)this.mCycleAdapter.getItem(j), localCycleItem))
              {
                this.mCycleListener.onItemSelected(this.mCycleSpinner, null, j, 0L);
                return;
              }
              updateDetailData();
              return;
            }
            updateDetailData();
            return;
            i = 0;
          }
        }
      }
      l1 = -9223372036854775808L;
      l2 = 9223372036854775807L;
    }
  }

  private void updateDetailData()
  {
    long l1 = this.mChart.getInspectStart();
    long l2 = this.mChart.getInspectEnd();
    long l3 = System.currentTimeMillis();
    Activity localActivity = getActivity();
    NetworkStatsHistory.Entry localEntry1;
    long l4;
    label237: String str1;
    String str2;
    if ((isAppDetailMode()) && (this.mChartData != null) && (this.mChartData.detail != null))
    {
      NetworkStatsHistory.Entry localEntry2 = this.mChartData.detailDefault.getValues(l1, l2, l3, null);
      long l5 = localEntry2.rxBytes + localEntry2.txBytes;
      NetworkStatsHistory.Entry localEntry3 = this.mChartData.detailForeground.getValues(l1, l2, l3, localEntry2);
      long l6 = localEntry3.rxBytes + localEntry3.txBytes;
      this.mAppPieChart.setOriginAngle(175);
      this.mAppPieChart.removeAllSlices();
      this.mAppPieChart.addSlice(l6, Color.parseColor("#d88d3a"));
      this.mAppPieChart.addSlice(l5, Color.parseColor("#666666"));
      this.mAppPieChart.generatePath();
      this.mAppBackground.setText(android.text.format.Formatter.formatFileSize(localActivity, l5));
      this.mAppForeground.setText(android.text.format.Formatter.formatFileSize(localActivity, l6));
      localEntry1 = this.mChartData.detail.getValues(l1, l2, l3, null);
      getLoaderManager().destroyLoader(3);
      if (localEntry1 == null)
        break label387;
      l4 = localEntry1.rxBytes + localEntry1.txBytes;
      str1 = android.text.format.Formatter.formatFileSize(localActivity, l4);
      str2 = formatDateRange(localActivity, l1, l2);
      if ((!"mobile".equals(this.mCurrentTab)) && (!"3g".equals(this.mCurrentApp)) && (!"4g".equals(this.mCurrentApp)))
        break label393;
    }
    label387: label393: for (int i = 2131429128; ; i = 2131429127)
    {
      this.mUsageSummary.setText(getString(i, new Object[] { str1, str2 }));
      ensureLayoutTransitions();
      return;
      ChartData localChartData = this.mChartData;
      localEntry1 = null;
      if (localChartData != null)
        localEntry1 = this.mChartData.network.getValues(l1, l2, l3, null);
      getLoaderManager().restartLoader(3, SummaryForAllUidLoader.buildArgs(this.mTemplate, l1, l2), this.mSummaryCallbacks);
      break;
      l4 = 0L;
      break label237;
    }
  }

  private void updatePolicy(boolean paramBoolean)
  {
    boolean bool = true;
    NetworkPolicy localNetworkPolicy;
    if (isAppDetailMode())
    {
      this.mNetworkSwitches.setVisibility(8);
      if ("mobile".equals(this.mCurrentTab))
      {
        this.mBinding = bool;
        this.mDataEnabled.setChecked(isMobileDataEnabled());
        this.mBinding = false;
      }
      localNetworkPolicy = this.mPolicyEditor.getPolicy(this.mTemplate);
      if (!isNetworkPolicyModifiable(localNetworkPolicy))
        break label148;
      this.mDisableAtLimitView.setVisibility(0);
      CheckBox localCheckBox = this.mDisableAtLimit;
      if ((localNetworkPolicy == null) || (localNetworkPolicy.limitBytes == -1L))
        break label143;
      label101: localCheckBox.setChecked(bool);
      if (!isAppDetailMode())
        this.mChart.bindNetworkPolicy(localNetworkPolicy);
    }
    while (true)
    {
      if (paramBoolean)
        updateCycleList(localNetworkPolicy);
      return;
      this.mNetworkSwitches.setVisibility(0);
      break;
      label143: bool = false;
      break label101;
      label148: this.mDisableAtLimitView.setVisibility(8);
      this.mChart.bindNetworkPolicy(null);
    }
  }

  private void updateTabs()
  {
    int i = 1;
    Activity localActivity = getActivity();
    this.mTabHost.clearAllTabs();
    if ((isMobilePolicySplit()) && (hasReadyMobile4gRadio(localActivity)))
    {
      this.mTabHost.addTab(buildTabSpec("3g", 2131429095));
      this.mTabHost.addTab(buildTabSpec("4g", 2131429094));
    }
    int j;
    label137: label148: label161: label201: label234: label239: label246: 
    do
    {
      break label201;
      if ((this.mShowWifi) && (hasWifiRadio(localActivity)))
        this.mTabHost.addTab(buildTabSpec("wifi", 2131429091));
      if ((this.mShowEthernet) && (hasEthernet(localActivity)))
        this.mTabHost.addTab(buildTabSpec("ethernet", 2131429092));
      int k;
      if (this.mTabWidget.getTabCount() == 0)
      {
        j = i;
        if (this.mTabWidget.getTabCount() <= i)
          break label234;
        TabWidget localTabWidget = this.mTabWidget;
        k = 0;
        if (i == 0)
          break label239;
        localTabWidget.setVisibility(k);
        if (this.mIntentTab == null)
          continue;
        if (!Objects.equal(this.mIntentTab, this.mTabHost.getCurrentTabTag()))
          break label246;
        updateBody();
      }
      while (true)
      {
        this.mIntentTab = null;
        return;
        if (!hasReadyMobileRadio(localActivity))
          break;
        this.mTabHost.addTab(buildTabSpec("mobile", 2131429093));
        break;
        j = 0;
        break label137;
        i = 0;
        break label148;
        k = 8;
        break label161;
        this.mTabHost.setCurrentTabByTag(this.mIntentTab);
      }
    }
    while (j == 0);
    updateBody();
  }

  public boolean hasEthernet(Context paramContext)
  {
    boolean bool = ConnectivityManager.from(paramContext).isNetworkSupported(9);
    if (this.mStatsSession != null);
    while (true)
    {
      try
      {
        long l2 = this.mStatsSession.getSummaryForNetwork(NetworkTemplate.buildTemplateEthernet(), -9223372036854775808L, 9223372036854775807L).getTotalBytes();
        l1 = l2;
        if ((!bool) || (l1 <= 0L))
          break;
        return true;
      }
      catch (RemoteException localRemoteException)
      {
        throw new RuntimeException(localRemoteException);
      }
      long l1 = 0L;
    }
    return false;
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    Activity localActivity = getActivity();
    this.mNetworkService = INetworkManagementService.Stub.asInterface(ServiceManager.getService("network_management"));
    this.mStatsService = INetworkStatsService.Stub.asInterface(ServiceManager.getService("netstats"));
    this.mPolicyManager = NetworkPolicyManager.from(localActivity);
    this.mConnService = ConnectivityManager.from(localActivity);
    this.mPrefs = getActivity().getSharedPreferences("data_usage", 0);
    this.mPolicyEditor = new NetworkPolicyEditor(this.mPolicyManager);
    this.mPolicyEditor.read();
    try
    {
      if (!this.mNetworkService.isBandwidthControlEnabled())
      {
        Log.w("DataUsage", "No bandwidth control; leaving");
        getActivity().finish();
      }
    }
    catch (RemoteException localRemoteException1)
    {
      try
      {
        while (true)
        {
          this.mStatsSession = this.mStatsService.openSession();
          this.mShowWifi = this.mPrefs.getBoolean("show_wifi", false);
          this.mShowEthernet = this.mPrefs.getBoolean("show_ethernet", false);
          if (!hasReadyMobileRadio(localActivity))
          {
            this.mShowWifi = true;
            this.mShowEthernet = true;
          }
          setHasOptionsMenu(true);
          return;
          localRemoteException1 = localRemoteException1;
          Log.w("DataUsage", "No bandwidth control; leaving");
          getActivity().finish();
        }
      }
      catch (RemoteException localRemoteException2)
      {
        throw new RuntimeException(localRemoteException2);
      }
    }
  }

  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    paramMenuInflater.inflate(2131755008, paramMenu);
  }

  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    Context localContext = paramLayoutInflater.getContext();
    View localView = paramLayoutInflater.inflate(2130968619, paramViewGroup, false);
    this.mUidDetailProvider = new UidDetailProvider(localContext);
    this.mTabHost = ((TabHost)localView.findViewById(16908306));
    this.mTabsContainer = ((ViewGroup)localView.findViewById(2131230812));
    this.mTabWidget = ((TabWidget)localView.findViewById(16908307));
    this.mListView = ((ListView)localView.findViewById(16908298));
    if (this.mListView.getScrollBarStyle() == 33554432);
    while (true)
    {
      this.mInsetSide = 0;
      Utils.prepareCustomPreferencesList(paramViewGroup, localView, this.mListView, false);
      this.mTabHost.setup();
      this.mTabHost.setOnTabChangedListener(this.mTabListener);
      this.mHeader = ((ViewGroup)paramLayoutInflater.inflate(2130968617, this.mListView, false));
      this.mHeader.setClickable(true);
      this.mListView.addHeaderView(new View(localContext), null, true);
      this.mListView.addHeaderView(this.mHeader, null, true);
      this.mListView.setItemsCanFocus(true);
      if (this.mInsetSide > 0)
      {
        insetListViewDrawables(this.mListView, this.mInsetSide);
        this.mHeader.setPaddingRelative(this.mInsetSide, 0, this.mInsetSide, 0);
      }
      this.mNetworkSwitchesContainer = ((ViewGroup)this.mHeader.findViewById(2131230809));
      this.mNetworkSwitches = ((LinearLayout)this.mHeader.findViewById(2131230810));
      this.mDataEnabled = new Switch(paramLayoutInflater.getContext());
      this.mDataEnabledView = inflatePreference(paramLayoutInflater, this.mNetworkSwitches, this.mDataEnabled);
      this.mDataEnabled.setOnCheckedChangeListener(this.mDataEnabledListener);
      this.mNetworkSwitches.addView(this.mDataEnabledView);
      this.mDisableAtLimit = new CheckBox(paramLayoutInflater.getContext());
      this.mDisableAtLimit.setClickable(false);
      this.mDisableAtLimit.setFocusable(false);
      this.mDisableAtLimitView = inflatePreference(paramLayoutInflater, this.mNetworkSwitches, this.mDisableAtLimit);
      this.mDisableAtLimitView.setClickable(true);
      this.mDisableAtLimitView.setFocusable(true);
      this.mDisableAtLimitView.setOnClickListener(this.mDisableAtLimitListener);
      this.mNetworkSwitches.addView(this.mDisableAtLimitView);
      this.mCycleView = this.mHeader.findViewById(2131230800);
      this.mCycleSpinner = ((Spinner)this.mCycleView.findViewById(2131230801));
      this.mCycleAdapter = new CycleAdapter(localContext);
      this.mCycleSpinner.setAdapter(this.mCycleAdapter);
      this.mCycleSpinner.setOnItemSelectedListener(this.mCycleListener);
      this.mChart = ((ChartDataUsageView)this.mHeader.findViewById(2131230791));
      this.mChart.setListener(this.mChartListener);
      this.mChart.bindNetworkPolicy(null);
      this.mAppDetail = this.mHeader.findViewById(2131230802);
      this.mAppIcon = ((ImageView)this.mAppDetail.findViewById(2131230735));
      this.mAppTitles = ((ViewGroup)this.mAppDetail.findViewById(2131230803));
      this.mAppPieChart = ((PieChartView)this.mAppDetail.findViewById(2131230806));
      this.mAppForeground = ((TextView)this.mAppDetail.findViewById(2131230804));
      this.mAppBackground = ((TextView)this.mAppDetail.findViewById(2131230805));
      this.mAppSwitches = ((LinearLayout)this.mAppDetail.findViewById(2131230808));
      this.mAppSettings = ((Button)this.mAppDetail.findViewById(2131230807));
      this.mAppSettings.setOnClickListener(this.mAppSettingsListener);
      this.mAppRestrict = new CheckBox(paramLayoutInflater.getContext());
      this.mAppRestrict.setClickable(false);
      this.mAppRestrict.setFocusable(false);
      this.mAppRestrictView = inflatePreference(paramLayoutInflater, this.mAppSwitches, this.mAppRestrict);
      this.mAppRestrictView.setClickable(true);
      this.mAppRestrictView.setFocusable(true);
      this.mAppRestrictView.setOnClickListener(this.mAppRestrictListener);
      this.mAppSwitches.addView(this.mAppRestrictView);
      this.mUsageSummary = ((TextView)this.mHeader.findViewById(2131230811));
      this.mEmpty = ((TextView)this.mHeader.findViewById(16908292));
      this.mAdapter = new DataUsageAdapter(this.mUidDetailProvider, this.mInsetSide);
      this.mListView.setOnItemClickListener(this.mListListener);
      this.mListView.setAdapter(this.mAdapter);
      return localView;
    }
  }

  public void onDestroy()
  {
    this.mDataEnabledView = null;
    this.mDisableAtLimitView = null;
    this.mUidDetailProvider.clearCache();
    this.mUidDetailProvider = null;
    TrafficStats.closeQuietly(this.mStatsSession);
    if (isRemoving())
      getFragmentManager().popBackStack("appDetails", 1);
    super.onDestroy();
  }

  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    switch (paramMenuItem.getItemId())
    {
    default:
      return false;
    case 2131231257:
      if (!paramMenuItem.isChecked());
      for (int j = 1; j != 0; j = 0)
      {
        ConfirmDataRoamingFragment.show(this);
        return true;
      }
      setDataRoaming(false);
      return true;
    case 2131231258:
      if (!paramMenuItem.isChecked());
      for (int i = 1; i != 0; i = 0)
      {
        ConfirmRestrictFragment.show(this);
        return true;
      }
      setRestrictBackground(false);
      return true;
    case 2131231259:
      boolean bool7 = paramMenuItem.isChecked();
      boolean bool8 = false;
      if (!bool7)
        bool8 = true;
      setMobilePolicySplit(bool8);
      paramMenuItem.setChecked(isMobilePolicySplit());
      updateTabs();
      return true;
    case 2131231261:
      boolean bool5 = paramMenuItem.isChecked();
      boolean bool6 = false;
      if (!bool5)
        bool6 = true;
      this.mShowWifi = bool6;
      this.mPrefs.edit().putBoolean("show_wifi", this.mShowWifi).apply();
      paramMenuItem.setChecked(this.mShowWifi);
      updateTabs();
      return true;
    case 2131231262:
      boolean bool3 = paramMenuItem.isChecked();
      boolean bool4 = false;
      if (!bool3)
        bool4 = true;
      this.mShowEthernet = bool4;
      this.mPrefs.edit().putBoolean("show_ethernet", this.mShowEthernet).apply();
      paramMenuItem.setChecked(this.mShowEthernet);
      updateTabs();
      return true;
    case 2131231263:
      ((PreferenceActivity)getActivity()).startPreferencePanel(DataUsageMeteredSettings.class.getCanonicalName(), null, 2131429129, null, this, 0);
      return true;
    case 2131231260:
    }
    if (ActivityManager.isUserAMonkey())
    {
      Log.d("SyncState", "ignoring monkey's attempt to flip global sync state");
      return true;
    }
    boolean bool1 = paramMenuItem.isChecked();
    boolean bool2 = false;
    if (!bool1)
      bool2 = true;
    ConfirmAutoSyncChangeFragment.show(this, bool2);
    return true;
  }

  public void onPrepareOptionsMenu(Menu paramMenu)
  {
    boolean bool1 = true;
    Activity localActivity = getActivity();
    boolean bool2 = isAppDetailMode();
    boolean bool3;
    boolean bool4;
    label56: boolean bool5;
    label119: boolean bool6;
    label186: boolean bool7;
    label227: MenuItem localMenuItem5;
    boolean bool9;
    label282: label304: MenuItem localMenuItem6;
    boolean bool8;
    label338: label360: MenuItem localMenuItem7;
    if (ActivityManager.getCurrentUser() == 0)
    {
      bool3 = bool1;
      this.mMenuDataRoaming = paramMenu.findItem(2131231257);
      MenuItem localMenuItem1 = this.mMenuDataRoaming;
      if ((!hasReadyMobileRadio(localActivity)) || (bool2))
        break label446;
      bool4 = bool1;
      localMenuItem1.setVisible(bool4);
      this.mMenuDataRoaming.setChecked(getDataRoaming());
      this.mMenuRestrictBackground = paramMenu.findItem(2131231258);
      MenuItem localMenuItem2 = this.mMenuRestrictBackground;
      if ((!hasReadyMobileRadio(localActivity)) || (!bool3) || (bool2))
        break label452;
      bool5 = bool1;
      localMenuItem2.setVisible(bool5);
      this.mMenuRestrictBackground.setChecked(this.mPolicyManager.getRestrictBackground());
      this.mMenuAutoSync = paramMenu.findItem(2131231260);
      this.mMenuAutoSync.setChecked(ContentResolver.getMasterSyncAutomatically());
      MenuItem localMenuItem3 = this.mMenuAutoSync;
      if (bool2)
        break label458;
      bool6 = bool1;
      localMenuItem3.setVisible(bool6);
      MenuItem localMenuItem4 = paramMenu.findItem(2131231259);
      if ((!hasReadyMobile4gRadio(localActivity)) || (!bool3) || (bool2))
        break label464;
      bool7 = bool1;
      localMenuItem4.setVisible(bool7);
      localMenuItem4.setChecked(isMobilePolicySplit());
      localMenuItem5 = paramMenu.findItem(2131231261);
      if ((!hasWifiRadio(localActivity)) || (!hasReadyMobileRadio(localActivity)))
        break label476;
      if (bool2)
        break label470;
      bool9 = bool1;
      localMenuItem5.setVisible(bool9);
      localMenuItem5.setChecked(this.mShowWifi);
      localMenuItem6 = paramMenu.findItem(2131231262);
      if ((!hasEthernet(localActivity)) || (!hasReadyMobileRadio(localActivity)))
        break label494;
      if (bool2)
        break label488;
      bool8 = bool1;
      localMenuItem6.setVisible(bool8);
      localMenuItem6.setChecked(this.mShowEthernet);
      localMenuItem7 = paramMenu.findItem(2131231263);
      if ((!hasReadyMobileRadio(localActivity)) && (!hasWifiRadio(localActivity)))
        break label511;
      if (bool2)
        break label506;
      label390: localMenuItem7.setVisible(bool1);
    }
    MenuItem localMenuItem8;
    while (true)
    {
      localMenuItem8 = paramMenu.findItem(2131231264);
      String str = getResources().getString(2131429256);
      if (TextUtils.isEmpty(str))
        break label523;
      HelpUtils.prepareHelpMenuItem(localActivity, localMenuItem8, str);
      return;
      bool3 = false;
      break;
      label446: bool4 = false;
      break label56;
      label452: bool5 = false;
      break label119;
      label458: bool6 = false;
      break label186;
      label464: bool7 = false;
      break label227;
      label470: bool9 = false;
      break label282;
      label476: localMenuItem5.setVisible(false);
      break label304;
      label488: bool8 = false;
      break label338;
      label494: localMenuItem6.setVisible(false);
      break label360;
      label506: bool1 = false;
      break label390;
      label511: localMenuItem7.setVisible(false);
    }
    label523: localMenuItem8.setVisible(false);
  }

  public void onResume()
  {
    super.onResume();
    this.mIntentTab = computeTabFromIntent(getActivity().getIntent());
    updateTabs();
    new AsyncTask()
    {
      protected Void doInBackground(Void[] paramAnonymousArrayOfVoid)
      {
        try
        {
          Thread.sleep(2000L);
          DataUsageSummary.this.mStatsService.forceUpdate();
          label18: return null;
        }
        catch (RemoteException localRemoteException)
        {
          break label18;
        }
        catch (InterruptedException localInterruptedException)
        {
          break label18;
        }
      }

      protected void onPostExecute(Void paramAnonymousVoid)
      {
        if (DataUsageSummary.this.isAdded())
          DataUsageSummary.this.updateBody();
      }
    }
    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
  }

  public void setRestrictBackground(boolean paramBoolean)
  {
    this.mPolicyManager.setRestrictBackground(paramBoolean);
    this.mMenuRestrictBackground.setChecked(paramBoolean);
  }

  public static class AppDetailsFragment extends Fragment
  {
    public static void show(DataUsageSummary paramDataUsageSummary, DataUsageSummary.AppItem paramAppItem, CharSequence paramCharSequence)
    {
      if (!paramDataUsageSummary.isAdded())
        return;
      Bundle localBundle = new Bundle();
      localBundle.putParcelable("app", paramAppItem);
      AppDetailsFragment localAppDetailsFragment = new AppDetailsFragment();
      localAppDetailsFragment.setArguments(localBundle);
      localAppDetailsFragment.setTargetFragment(paramDataUsageSummary, 0);
      FragmentTransaction localFragmentTransaction = paramDataUsageSummary.getFragmentManager().beginTransaction();
      localFragmentTransaction.add(localAppDetailsFragment, "appDetails");
      localFragmentTransaction.addToBackStack("appDetails");
      localFragmentTransaction.setBreadCrumbTitle(paramCharSequence);
      localFragmentTransaction.commitAllowingStateLoss();
    }

    public void onStart()
    {
      super.onStart();
      DataUsageSummary localDataUsageSummary = (DataUsageSummary)getTargetFragment();
      DataUsageSummary.access$2502(localDataUsageSummary, (DataUsageSummary.AppItem)getArguments().getParcelable("app"));
      localDataUsageSummary.updateBody();
    }

    public void onStop()
    {
      super.onStop();
      DataUsageSummary localDataUsageSummary = (DataUsageSummary)getTargetFragment();
      DataUsageSummary.access$2502(localDataUsageSummary, null);
      localDataUsageSummary.updateBody();
    }
  }

  public static class AppItem
    implements Comparable<AppItem>, Parcelable
  {
    public static final Parcelable.Creator<AppItem> CREATOR = new Parcelable.Creator()
    {
      public DataUsageSummary.AppItem createFromParcel(Parcel paramAnonymousParcel)
      {
        return new DataUsageSummary.AppItem(paramAnonymousParcel);
      }

      public DataUsageSummary.AppItem[] newArray(int paramAnonymousInt)
      {
        return new DataUsageSummary.AppItem[paramAnonymousInt];
      }
    };
    public final int key;
    public boolean restricted;
    public long total;
    public SparseBooleanArray uids = new SparseBooleanArray();

    public AppItem(int paramInt)
    {
      this.key = paramInt;
    }

    public AppItem(Parcel paramParcel)
    {
      this.key = paramParcel.readInt();
      this.uids = paramParcel.readSparseBooleanArray();
      this.total = paramParcel.readLong();
    }

    public void addUid(int paramInt)
    {
      this.uids.put(paramInt, true);
    }

    public int compareTo(AppItem paramAppItem)
    {
      return Long.compare(paramAppItem.total, this.total);
    }

    public int describeContents()
    {
      return 0;
    }

    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeInt(this.key);
      paramParcel.writeSparseBooleanArray(this.uids);
      paramParcel.writeLong(this.total);
    }
  }

  public static class ConfirmAppRestrictFragment extends DialogFragment
  {
    public static void show(DataUsageSummary paramDataUsageSummary)
    {
      if (!paramDataUsageSummary.isAdded())
        return;
      ConfirmAppRestrictFragment localConfirmAppRestrictFragment = new ConfirmAppRestrictFragment();
      localConfirmAppRestrictFragment.setTargetFragment(paramDataUsageSummary, 0);
      localConfirmAppRestrictFragment.show(paramDataUsageSummary.getFragmentManager(), "confirmAppRestrict");
    }

    public Dialog onCreateDialog(Bundle paramBundle)
    {
      AlertDialog.Builder localBuilder = new AlertDialog.Builder(getActivity());
      localBuilder.setTitle(2131429105);
      localBuilder.setMessage(2131429106);
      localBuilder.setPositiveButton(17039370, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          DataUsageSummary localDataUsageSummary = (DataUsageSummary)DataUsageSummary.ConfirmAppRestrictFragment.this.getTargetFragment();
          if (localDataUsageSummary != null)
            localDataUsageSummary.setAppRestrictBackground(true);
        }
      });
      localBuilder.setNegativeButton(17039360, null);
      return localBuilder.create();
    }
  }

  public static class ConfirmAutoSyncChangeFragment extends DialogFragment
  {
    private boolean mEnabling;

    public static void show(DataUsageSummary paramDataUsageSummary, boolean paramBoolean)
    {
      if (!paramDataUsageSummary.isAdded())
        return;
      ConfirmAutoSyncChangeFragment localConfirmAutoSyncChangeFragment = new ConfirmAutoSyncChangeFragment();
      localConfirmAutoSyncChangeFragment.mEnabling = paramBoolean;
      localConfirmAutoSyncChangeFragment.setTargetFragment(paramDataUsageSummary, 0);
      localConfirmAutoSyncChangeFragment.show(paramDataUsageSummary.getFragmentManager(), "confirmAutoSyncChange");
    }

    public Dialog onCreateDialog(Bundle paramBundle)
    {
      Activity localActivity = getActivity();
      if (paramBundle != null)
        this.mEnabling = paramBundle.getBoolean("enabling");
      AlertDialog.Builder localBuilder = new AlertDialog.Builder(localActivity);
      if (!this.mEnabling)
      {
        localBuilder.setTitle(2131429110);
        localBuilder.setMessage(2131429111);
      }
      while (true)
      {
        localBuilder.setPositiveButton(17039370, new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            ContentResolver.setMasterSyncAutomatically(DataUsageSummary.ConfirmAutoSyncChangeFragment.this.mEnabling);
          }
        });
        localBuilder.setNegativeButton(17039360, null);
        return localBuilder.create();
        localBuilder.setTitle(2131429108);
        localBuilder.setMessage(2131429109);
      }
    }

    public void onSaveInstanceState(Bundle paramBundle)
    {
      super.onSaveInstanceState(paramBundle);
      paramBundle.putBoolean("enabling", this.mEnabling);
    }
  }

  public static class ConfirmDataDisableFragment extends DialogFragment
  {
    public static void show(DataUsageSummary paramDataUsageSummary)
    {
      if (!paramDataUsageSummary.isAdded())
        return;
      ConfirmDataDisableFragment localConfirmDataDisableFragment = new ConfirmDataDisableFragment();
      localConfirmDataDisableFragment.setTargetFragment(paramDataUsageSummary, 0);
      localConfirmDataDisableFragment.show(paramDataUsageSummary.getFragmentManager(), "confirmDataDisable");
    }

    public Dialog onCreateDialog(Bundle paramBundle)
    {
      AlertDialog.Builder localBuilder = new AlertDialog.Builder(getActivity());
      localBuilder.setMessage(2131429086);
      localBuilder.setPositiveButton(17039370, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          DataUsageSummary localDataUsageSummary = (DataUsageSummary)DataUsageSummary.ConfirmDataDisableFragment.this.getTargetFragment();
          if (localDataUsageSummary != null)
            localDataUsageSummary.setMobileDataEnabled(false);
        }
      });
      localBuilder.setNegativeButton(17039360, null);
      return localBuilder.create();
    }
  }

  public static class ConfirmDataRoamingFragment extends DialogFragment
  {
    public static void show(DataUsageSummary paramDataUsageSummary)
    {
      if (!paramDataUsageSummary.isAdded())
        return;
      ConfirmDataRoamingFragment localConfirmDataRoamingFragment = new ConfirmDataRoamingFragment();
      localConfirmDataRoamingFragment.setTargetFragment(paramDataUsageSummary, 0);
      localConfirmDataRoamingFragment.show(paramDataUsageSummary.getFragmentManager(), "confirmDataRoaming");
    }

    public Dialog onCreateDialog(Bundle paramBundle)
    {
      Activity localActivity = getActivity();
      AlertDialog.Builder localBuilder = new AlertDialog.Builder(localActivity);
      localBuilder.setTitle(2131427582);
      if (Utils.hasMultipleUsers(localActivity))
        localBuilder.setMessage(2131427581);
      while (true)
      {
        localBuilder.setPositiveButton(17039370, new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            DataUsageSummary localDataUsageSummary = (DataUsageSummary)DataUsageSummary.ConfirmDataRoamingFragment.this.getTargetFragment();
            if (localDataUsageSummary != null)
              localDataUsageSummary.setDataRoaming(true);
          }
        });
        localBuilder.setNegativeButton(17039360, null);
        return localBuilder.create();
        localBuilder.setMessage(2131427580);
      }
    }
  }

  public static class ConfirmLimitFragment extends DialogFragment
  {
    public static void show(DataUsageSummary paramDataUsageSummary)
    {
      if (!paramDataUsageSummary.isAdded())
        return;
      Resources localResources = paramDataUsageSummary.getResources();
      long l1 = ()(1.2F * (float)paramDataUsageSummary.mPolicyEditor.getPolicy(paramDataUsageSummary.mTemplate).warningBytes);
      String str1 = paramDataUsageSummary.mCurrentTab;
      String str2;
      long l2;
      if ("3g".equals(str1))
      {
        str2 = localResources.getString(2131429118);
        l2 = Math.max(5368709120L, l1);
      }
      while (true)
      {
        Bundle localBundle = new Bundle();
        localBundle.putCharSequence("message", str2);
        localBundle.putLong("limitBytes", l2);
        ConfirmLimitFragment localConfirmLimitFragment = new ConfirmLimitFragment();
        localConfirmLimitFragment.setArguments(localBundle);
        localConfirmLimitFragment.setTargetFragment(paramDataUsageSummary, 0);
        localConfirmLimitFragment.show(paramDataUsageSummary.getFragmentManager(), "confirmLimit");
        return;
        if ("4g".equals(str1))
        {
          str2 = localResources.getString(2131429118);
          l2 = Math.max(5368709120L, l1);
        }
        else
        {
          if (!"mobile".equals(str1))
            break;
          str2 = localResources.getString(2131429118);
          l2 = Math.max(5368709120L, l1);
        }
      }
      throw new IllegalArgumentException("unknown current tab: " + str1);
    }

    public Dialog onCreateDialog(Bundle paramBundle)
    {
      Activity localActivity = getActivity();
      CharSequence localCharSequence = getArguments().getCharSequence("message");
      final long l = getArguments().getLong("limitBytes");
      AlertDialog.Builder localBuilder = new AlertDialog.Builder(localActivity);
      localBuilder.setTitle(2131429117);
      localBuilder.setMessage(localCharSequence);
      localBuilder.setPositiveButton(17039370, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          DataUsageSummary localDataUsageSummary = (DataUsageSummary)DataUsageSummary.ConfirmLimitFragment.this.getTargetFragment();
          if (localDataUsageSummary != null)
            localDataUsageSummary.setPolicyLimitBytes(l);
        }
      });
      return localBuilder.create();
    }
  }

  public static class ConfirmRestrictFragment extends DialogFragment
  {
    public static void show(DataUsageSummary paramDataUsageSummary)
    {
      if (!paramDataUsageSummary.isAdded())
        return;
      ConfirmRestrictFragment localConfirmRestrictFragment = new ConfirmRestrictFragment();
      localConfirmRestrictFragment.setTargetFragment(paramDataUsageSummary, 0);
      localConfirmRestrictFragment.show(paramDataUsageSummary.getFragmentManager(), "confirmRestrict");
    }

    public Dialog onCreateDialog(Bundle paramBundle)
    {
      Activity localActivity = getActivity();
      AlertDialog.Builder localBuilder = new AlertDialog.Builder(localActivity);
      localBuilder.setTitle(2131429119);
      if (Utils.hasMultipleUsers(localActivity))
        localBuilder.setMessage(2131429121);
      while (true)
      {
        localBuilder.setPositiveButton(17039370, new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            DataUsageSummary localDataUsageSummary = (DataUsageSummary)DataUsageSummary.ConfirmRestrictFragment.this.getTargetFragment();
            if (localDataUsageSummary != null)
              localDataUsageSummary.setRestrictBackground(true);
          }
        });
        localBuilder.setNegativeButton(17039360, null);
        return localBuilder.create();
        localBuilder.setMessage(2131429120);
      }
    }
  }

  public static class CycleAdapter extends ArrayAdapter<DataUsageSummary.CycleItem>
  {
    private final DataUsageSummary.CycleChangeItem mChangeItem;
    private boolean mChangePossible = false;
    private boolean mChangeVisible = false;

    public CycleAdapter(Context paramContext)
    {
      super(17367048);
      setDropDownViewResource(17367049);
      this.mChangeItem = new DataUsageSummary.CycleChangeItem(paramContext);
    }

    private void updateChange()
    {
      remove(this.mChangeItem);
      if ((this.mChangePossible) && (this.mChangeVisible))
        add(this.mChangeItem);
    }

    public int findNearestPosition(DataUsageSummary.CycleItem paramCycleItem)
    {
      if (paramCycleItem != null)
      {
        int i = -1 + getCount();
        if (i >= 0)
        {
          DataUsageSummary.CycleItem localCycleItem = (DataUsageSummary.CycleItem)getItem(i);
          if ((localCycleItem instanceof DataUsageSummary.CycleChangeItem));
          while (localCycleItem.compareTo(paramCycleItem) < 0)
          {
            i--;
            break;
          }
          return i;
        }
      }
      return 0;
    }

    public void setChangePossible(boolean paramBoolean)
    {
      this.mChangePossible = paramBoolean;
      updateChange();
    }

    public void setChangeVisible(boolean paramBoolean)
    {
      this.mChangeVisible = paramBoolean;
      updateChange();
    }
  }

  public static class CycleChangeItem extends DataUsageSummary.CycleItem
  {
    public CycleChangeItem(Context paramContext)
    {
      super();
    }
  }

  public static class CycleEditorFragment extends DialogFragment
  {
    public static void show(DataUsageSummary paramDataUsageSummary)
    {
      if (!paramDataUsageSummary.isAdded())
        return;
      Bundle localBundle = new Bundle();
      localBundle.putParcelable("template", paramDataUsageSummary.mTemplate);
      CycleEditorFragment localCycleEditorFragment = new CycleEditorFragment();
      localCycleEditorFragment.setArguments(localBundle);
      localCycleEditorFragment.setTargetFragment(paramDataUsageSummary, 0);
      localCycleEditorFragment.show(paramDataUsageSummary.getFragmentManager(), "cycleEditor");
    }

    public Dialog onCreateDialog(Bundle paramBundle)
    {
      Activity localActivity = getActivity();
      final DataUsageSummary localDataUsageSummary = (DataUsageSummary)getTargetFragment();
      final NetworkPolicyEditor localNetworkPolicyEditor = localDataUsageSummary.mPolicyEditor;
      AlertDialog.Builder localBuilder = new AlertDialog.Builder(localActivity);
      View localView = LayoutInflater.from(localBuilder.getContext()).inflate(2130968614, null, false);
      final NumberPicker localNumberPicker = (NumberPicker)localView.findViewById(2131230799);
      final NetworkTemplate localNetworkTemplate = (NetworkTemplate)getArguments().getParcelable("template");
      int i = localNetworkPolicyEditor.getPolicyCycleDay(localNetworkTemplate);
      localNumberPicker.setMinValue(1);
      localNumberPicker.setMaxValue(31);
      localNumberPicker.setValue(i);
      localNumberPicker.setWrapSelectorWheel(true);
      localBuilder.setTitle(2131429112);
      localBuilder.setView(localView);
      localBuilder.setPositiveButton(2131429114, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          localNumberPicker.clearFocus();
          int i = localNumberPicker.getValue();
          String str = new Time().timezone;
          localNetworkPolicyEditor.setPolicyCycleDay(localNetworkTemplate, i, str);
          localDataUsageSummary.updatePolicy(true);
        }
      });
      return localBuilder.create();
    }
  }

  public static class CycleItem
    implements Comparable<CycleItem>
  {
    public long end;
    public CharSequence label;
    public long start;

    public CycleItem(Context paramContext, long paramLong1, long paramLong2)
    {
      this.label = DataUsageSummary.formatDateRange(paramContext, paramLong1, paramLong2);
      this.start = paramLong1;
      this.end = paramLong2;
    }

    CycleItem(CharSequence paramCharSequence)
    {
      this.label = paramCharSequence;
    }

    public int compareTo(CycleItem paramCycleItem)
    {
      return Long.compare(this.start, paramCycleItem.start);
    }

    public boolean equals(Object paramObject)
    {
      boolean bool1 = paramObject instanceof CycleItem;
      boolean bool2 = false;
      if (bool1)
      {
        CycleItem localCycleItem = (CycleItem)paramObject;
        boolean bool3 = this.start < localCycleItem.start;
        bool2 = false;
        if (!bool3)
        {
          boolean bool4 = this.end < localCycleItem.end;
          bool2 = false;
          if (!bool4)
            bool2 = true;
        }
      }
      return bool2;
    }

    public String toString()
    {
      return this.label.toString();
    }
  }

  public static class DataUsageAdapter extends BaseAdapter
  {
    private final int mInsetSide;
    private ArrayList<DataUsageSummary.AppItem> mItems = Lists.newArrayList();
    private long mLargest;
    private final UidDetailProvider mProvider;

    public DataUsageAdapter(UidDetailProvider paramUidDetailProvider, int paramInt)
    {
      this.mProvider = ((UidDetailProvider)Preconditions.checkNotNull(paramUidDetailProvider));
      this.mInsetSide = paramInt;
    }

    public void bindStats(NetworkStats paramNetworkStats, int[] paramArrayOfInt)
    {
      this.mItems.clear();
      int i = ActivityManager.getCurrentUser();
      SparseArray localSparseArray = new SparseArray();
      NetworkStats.Entry localEntry = null;
      int j;
      int k;
      label36: int i2;
      int i3;
      if (paramNetworkStats != null)
      {
        j = paramNetworkStats.size();
        k = 0;
        if (k >= j)
          break label214;
        localEntry = paramNetworkStats.getValues(k, localEntry);
        i2 = localEntry.uid;
        if (!UserHandle.isApp(i2))
          break label185;
        if (UserHandle.getUserId(i2) != i)
          break label172;
        i3 = i2;
      }
      while (true)
      {
        DataUsageSummary.AppItem localAppItem2 = (DataUsageSummary.AppItem)localSparseArray.get(i3);
        if (localAppItem2 == null)
        {
          localAppItem2 = new DataUsageSummary.AppItem(i3);
          this.mItems.add(localAppItem2);
          localSparseArray.put(localAppItem2.key, localAppItem2);
        }
        localAppItem2.addUid(i2);
        localAppItem2.total += localEntry.rxBytes + localEntry.txBytes;
        k++;
        break label36;
        j = 0;
        break;
        label172: i3 = UidDetailProvider.buildKeyForUser(UserHandle.getUserId(i2));
        continue;
        label185: if ((i2 == -4) || (i2 == -5))
          i3 = i2;
        else
          i3 = 1000;
      }
      label214: int m = paramArrayOfInt.length;
      int n = 0;
      if (n < m)
      {
        int i1 = paramArrayOfInt[n];
        if (UserHandle.getUserId(i1) != i);
        while (true)
        {
          n++;
          break;
          DataUsageSummary.AppItem localAppItem1 = (DataUsageSummary.AppItem)localSparseArray.get(i1);
          if (localAppItem1 == null)
          {
            localAppItem1 = new DataUsageSummary.AppItem(i1);
            localAppItem1.total = -1L;
            this.mItems.add(localAppItem1);
            localSparseArray.put(localAppItem1.key, localAppItem1);
          }
          localAppItem1.restricted = true;
        }
      }
      Collections.sort(this.mItems);
      if (this.mItems.size() > 0);
      for (long l = ((DataUsageSummary.AppItem)this.mItems.get(0)).total; ; l = 0L)
      {
        this.mLargest = l;
        notifyDataSetChanged();
        return;
      }
    }

    public int getCount()
    {
      return this.mItems.size();
    }

    public Object getItem(int paramInt)
    {
      return this.mItems.get(paramInt);
    }

    public long getItemId(int paramInt)
    {
      return ((DataUsageSummary.AppItem)this.mItems.get(paramInt)).key;
    }

    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      if (paramView == null)
      {
        paramView = LayoutInflater.from(paramViewGroup.getContext()).inflate(2130968618, paramViewGroup, false);
        if (this.mInsetSide > 0)
          paramView.setPaddingRelative(this.mInsetSide, 0, this.mInsetSide, 0);
      }
      Context localContext = paramViewGroup.getContext();
      TextView localTextView = (TextView)paramView.findViewById(16908308);
      ProgressBar localProgressBar = (ProgressBar)paramView.findViewById(16908301);
      DataUsageSummary.AppItem localAppItem = (DataUsageSummary.AppItem)this.mItems.get(paramInt);
      DataUsageSummary.UidDetailTask.bindView(this.mProvider, localAppItem, paramView);
      if ((localAppItem.restricted) && (localAppItem.total <= 0L))
      {
        localTextView.setText(2131429085);
        localProgressBar.setVisibility(8);
      }
      while (true)
      {
        boolean bool = this.mLargest < 0L;
        int i = 0;
        if (bool)
          i = (int)(100L * localAppItem.total / this.mLargest);
        localProgressBar.setProgress(i);
        return paramView;
        localTextView.setText(android.text.format.Formatter.formatFileSize(localContext, localAppItem.total));
        localProgressBar.setVisibility(0);
      }
    }
  }

  public static class DeniedRestrictFragment extends DialogFragment
  {
    public Dialog onCreateDialog(Bundle paramBundle)
    {
      AlertDialog.Builder localBuilder = new AlertDialog.Builder(getActivity());
      localBuilder.setTitle(2131429102);
      localBuilder.setMessage(2131429107);
      localBuilder.setPositiveButton(17039370, null);
      return localBuilder.create();
    }
  }

  public static class LimitEditorFragment extends DialogFragment
  {
    public static void show(DataUsageSummary paramDataUsageSummary)
    {
      if (!paramDataUsageSummary.isAdded())
        return;
      Bundle localBundle = new Bundle();
      localBundle.putParcelable("template", paramDataUsageSummary.mTemplate);
      LimitEditorFragment localLimitEditorFragment = new LimitEditorFragment();
      localLimitEditorFragment.setArguments(localBundle);
      localLimitEditorFragment.setTargetFragment(paramDataUsageSummary, 0);
      localLimitEditorFragment.show(paramDataUsageSummary.getFragmentManager(), "limitEditor");
    }

    public Dialog onCreateDialog(Bundle paramBundle)
    {
      Activity localActivity = getActivity();
      final DataUsageSummary localDataUsageSummary = (DataUsageSummary)getTargetFragment();
      final NetworkPolicyEditor localNetworkPolicyEditor = localDataUsageSummary.mPolicyEditor;
      AlertDialog.Builder localBuilder = new AlertDialog.Builder(localActivity);
      View localView = LayoutInflater.from(localBuilder.getContext()).inflate(2130968612, null, false);
      final NumberPicker localNumberPicker = (NumberPicker)localView.findViewById(2131230790);
      final NetworkTemplate localNetworkTemplate = (NetworkTemplate)getArguments().getParcelable("template");
      long l1 = localNetworkPolicyEditor.getPolicyWarningBytes(localNetworkTemplate);
      long l2 = localNetworkPolicyEditor.getPolicyLimitBytes(localNetworkTemplate);
      localNumberPicker.setMaxValue(2147483647);
      if ((l1 != -1L) && (l2 > 0L))
        localNumberPicker.setMinValue(1 + (int)(l1 / 1048576L));
      while (true)
      {
        localNumberPicker.setValue((int)(l2 / 1048576L));
        localNumberPicker.setWrapSelectorWheel(false);
        localBuilder.setTitle(2131429116);
        localBuilder.setView(localView);
        localBuilder.setPositiveButton(2131429114, new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            localNumberPicker.clearFocus();
            long l = 1048576L * localNumberPicker.getValue();
            localNetworkPolicyEditor.setPolicyLimitBytes(localNetworkTemplate, l);
            localDataUsageSummary.updatePolicy(false);
          }
        });
        return localBuilder.create();
        localNumberPicker.setMinValue(0);
      }
    }
  }

  private static class UidDetailTask extends AsyncTask<Void, Void, UidDetail>
  {
    private final DataUsageSummary.AppItem mItem;
    private final UidDetailProvider mProvider;
    private final View mTarget;

    private UidDetailTask(UidDetailProvider paramUidDetailProvider, DataUsageSummary.AppItem paramAppItem, View paramView)
    {
      this.mProvider = ((UidDetailProvider)Preconditions.checkNotNull(paramUidDetailProvider));
      this.mItem = ((DataUsageSummary.AppItem)Preconditions.checkNotNull(paramAppItem));
      this.mTarget = ((View)Preconditions.checkNotNull(paramView));
    }

    private static void bindView(UidDetail paramUidDetail, View paramView)
    {
      ImageView localImageView = (ImageView)paramView.findViewById(16908294);
      TextView localTextView = (TextView)paramView.findViewById(16908310);
      if (paramUidDetail != null)
      {
        localImageView.setImageDrawable(paramUidDetail.icon);
        localTextView.setText(paramUidDetail.label);
        return;
      }
      localImageView.setImageDrawable(null);
      localTextView.setText(null);
    }

    public static void bindView(UidDetailProvider paramUidDetailProvider, DataUsageSummary.AppItem paramAppItem, View paramView)
    {
      UidDetailTask localUidDetailTask = (UidDetailTask)paramView.getTag();
      if (localUidDetailTask != null)
        localUidDetailTask.cancel(false);
      UidDetail localUidDetail = paramUidDetailProvider.getUidDetail(paramAppItem.key, false);
      if (localUidDetail != null)
      {
        bindView(localUidDetail, paramView);
        return;
      }
      paramView.setTag(new UidDetailTask(paramUidDetailProvider, paramAppItem, paramView).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]));
    }

    protected UidDetail doInBackground(Void[] paramArrayOfVoid)
    {
      return this.mProvider.getUidDetail(this.mItem.key, true);
    }

    protected void onPostExecute(UidDetail paramUidDetail)
    {
      bindView(paramUidDetail, this.mTarget);
    }

    protected void onPreExecute()
    {
      bindView(null, this.mTarget);
    }
  }

  public static class WarningEditorFragment extends DialogFragment
  {
    public static void show(DataUsageSummary paramDataUsageSummary)
    {
      if (!paramDataUsageSummary.isAdded())
        return;
      Bundle localBundle = new Bundle();
      localBundle.putParcelable("template", paramDataUsageSummary.mTemplate);
      WarningEditorFragment localWarningEditorFragment = new WarningEditorFragment();
      localWarningEditorFragment.setArguments(localBundle);
      localWarningEditorFragment.setTargetFragment(paramDataUsageSummary, 0);
      localWarningEditorFragment.show(paramDataUsageSummary.getFragmentManager(), "warningEditor");
    }

    public Dialog onCreateDialog(Bundle paramBundle)
    {
      Activity localActivity = getActivity();
      final DataUsageSummary localDataUsageSummary = (DataUsageSummary)getTargetFragment();
      final NetworkPolicyEditor localNetworkPolicyEditor = localDataUsageSummary.mPolicyEditor;
      AlertDialog.Builder localBuilder = new AlertDialog.Builder(localActivity);
      View localView = LayoutInflater.from(localBuilder.getContext()).inflate(2130968612, null, false);
      final NumberPicker localNumberPicker = (NumberPicker)localView.findViewById(2131230790);
      final NetworkTemplate localNetworkTemplate = (NetworkTemplate)getArguments().getParcelable("template");
      long l1 = localNetworkPolicyEditor.getPolicyWarningBytes(localNetworkTemplate);
      long l2 = localNetworkPolicyEditor.getPolicyLimitBytes(localNetworkTemplate);
      localNumberPicker.setMinValue(0);
      if (l2 != -1L)
        localNumberPicker.setMaxValue(-1 + (int)(l2 / 1048576L));
      while (true)
      {
        localNumberPicker.setValue((int)(l1 / 1048576L));
        localNumberPicker.setWrapSelectorWheel(false);
        localBuilder.setTitle(2131429115);
        localBuilder.setView(localView);
        localBuilder.setPositiveButton(2131429114, new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            localNumberPicker.clearFocus();
            long l = 1048576L * localNumberPicker.getValue();
            localNetworkPolicyEditor.setPolicyWarningBytes(localNetworkTemplate, l);
            localDataUsageSummary.updatePolicy(false);
          }
        });
        return localBuilder.create();
        localNumberPicker.setMaxValue(2147483647);
      }
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.DataUsageSummary
 * JD-Core Version:    0.6.2
 */