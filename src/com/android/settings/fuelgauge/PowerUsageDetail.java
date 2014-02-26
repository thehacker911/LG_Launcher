package com.android.settings.fuelgauge;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ApplicationErrorReport;
import android.app.ApplicationErrorReport.BatteryInfo;
import android.app.Fragment;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Process;
import android.os.UserHandle;
import android.preference.PreferenceActivity;
import android.provider.Settings.Global;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.android.settings.DisplaySettings;
import com.android.settings.WirelessSettings;
import com.android.settings.applications.InstalledAppDetails;
import com.android.settings.bluetooth.BluetoothSettings;
import com.android.settings.location.LocationSettings;
import com.android.settings.wifi.WifiSettings;

public class PowerUsageDetail extends Fragment
  implements View.OnClickListener
{
  private static int[] sDrainTypeDesciptions = { 2131428764, 2131428765, 2131428763, 2131428769, 2131428771, 2131428767, 2131428774, 2131428778 };
  ApplicationInfo mApp;
  private Drawable mAppIcon;
  private final BroadcastReceiver mCheckKillProcessesReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      Button localButton = PowerUsageDetail.this.mForceStopButton;
      if (getResultCode() != 0);
      for (boolean bool = true; ; bool = false)
      {
        localButton.setEnabled(bool);
        return;
      }
    }
  };
  private ViewGroup mControlsParent;
  private ViewGroup mDetailsParent;
  private DevicePolicyManager mDpm;
  private DrainType mDrainType;
  private Button mForceStopButton;
  ComponentName mInstaller;
  private double mNoCoverage;
  private String[] mPackages;
  private PackageManager mPm;
  private Button mReportButton;
  private View mRootView;
  private boolean mShowLocationButton;
  private long mStartTime;
  private String mTitle;
  private TextView mTitleView;
  private ViewGroup mTwoButtonsPanel;
  private int[] mTypes;
  private int mUid;
  private int mUsageSince;
  private boolean mUsesGps;
  private double[] mValues;

  private void addControl(int paramInt1, int paramInt2, int paramInt3)
  {
    Resources localResources = getResources();
    ViewGroup localViewGroup = (ViewGroup)getActivity().getLayoutInflater().inflate(2130968660, null);
    this.mControlsParent.addView(localViewGroup);
    Button localButton = (Button)localViewGroup.findViewById(2131230835);
    TextView localTextView = (TextView)localViewGroup.findViewById(2131230772);
    localButton.setText(localResources.getString(paramInt1));
    localTextView.setText(localResources.getString(paramInt2));
    localButton.setOnClickListener(this);
    localButton.setTag(new Integer(paramInt3));
  }

  private void checkForceStop()
  {
    if ((this.mPackages == null) || (this.mUid < 10000))
    {
      this.mForceStopButton.setEnabled(false);
      return;
    }
    for (int i = 0; i < this.mPackages.length; i++)
      if (this.mDpm.packageHasActiveAdmins(this.mPackages[i]))
      {
        this.mForceStopButton.setEnabled(false);
        return;
      }
    int j = 0;
    while (true)
    {
      if (j < this.mPackages.length);
      try
      {
        if ((0x200000 & this.mPm.getApplicationInfo(this.mPackages[j], 0).flags) == 0)
        {
          this.mForceStopButton.setEnabled(true);
          Intent localIntent = new Intent("android.intent.action.QUERY_PACKAGE_RESTART", Uri.fromParts("package", this.mPackages[0], null));
          localIntent.putExtra("android.intent.extra.PACKAGES", this.mPackages);
          localIntent.putExtra("android.intent.extra.UID", this.mUid);
          localIntent.putExtra("android.intent.extra.user_handle", UserHandle.getUserId(this.mUid));
          getActivity().sendOrderedBroadcast(localIntent, null, this.mCheckKillProcessesReceiver, null, 0, null, null);
          return;
        }
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException)
      {
        j++;
      }
    }
  }

  private void createDetails()
  {
    Bundle localBundle = getArguments();
    this.mTitle = localBundle.getString("title");
    int i = localBundle.getInt("percent", 1);
    int j = localBundle.getInt("gauge", 1);
    this.mUsageSince = localBundle.getInt("since", 1);
    this.mUid = localBundle.getInt("uid", 0);
    this.mDrainType = ((DrainType)localBundle.getSerializable("drainType"));
    this.mNoCoverage = localBundle.getDouble("noCoverage", 0.0D);
    String str = localBundle.getString("iconPackage");
    int k = localBundle.getInt("iconId", 0);
    this.mShowLocationButton = localBundle.getBoolean("showLocationButton");
    if (!TextUtils.isEmpty(str));
    try
    {
      PackageManager localPackageManager = getActivity().getPackageManager();
      ApplicationInfo localApplicationInfo = localPackageManager.getPackageInfo(str, 0).applicationInfo;
      if (localApplicationInfo != null)
        this.mAppIcon = localApplicationInfo.loadIcon(localPackageManager);
      while (true)
      {
        label153: if (this.mAppIcon == null)
          this.mAppIcon = getActivity().getPackageManager().getDefaultActivityIcon();
        TextView localTextView1 = (TextView)this.mRootView.findViewById(16908304);
        localTextView1.setText(getDescriptionForDrainType());
        localTextView1.setVisibility(0);
        this.mTypes = localBundle.getIntArray("types");
        this.mValues = localBundle.getDoubleArray("values");
        this.mTitleView = ((TextView)this.mRootView.findViewById(16908310));
        this.mTitleView.setText(this.mTitle);
        TextView localTextView2 = (TextView)this.mRootView.findViewById(16908308);
        Object[] arrayOfObject = new Object[1];
        arrayOfObject[0] = Integer.valueOf(i);
        localTextView2.setText(getString(2131429185, arrayOfObject));
        this.mTwoButtonsPanel = ((ViewGroup)this.mRootView.findViewById(2131230929));
        this.mForceStopButton = ((Button)this.mRootView.findViewById(2131231089));
        this.mReportButton = ((Button)this.mRootView.findViewById(2131231090));
        this.mForceStopButton.setEnabled(false);
        ((ProgressBar)this.mRootView.findViewById(16908301)).setProgress(j);
        ((ImageView)this.mRootView.findViewById(16908294)).setImageDrawable(this.mAppIcon);
        this.mDetailsParent = ((ViewGroup)this.mRootView.findViewById(2131230930));
        this.mControlsParent = ((ViewGroup)this.mRootView.findViewById(2131230931));
        fillDetailsSection();
        fillPackagesSection(this.mUid);
        fillControlsSection(this.mUid);
        if (this.mUid >= 10000)
        {
          this.mForceStopButton.setText(2131428375);
          this.mForceStopButton.setTag(Integer.valueOf(7));
          this.mForceStopButton.setOnClickListener(this);
          this.mReportButton.setText(17040389);
          this.mReportButton.setTag(Integer.valueOf(8));
          this.mReportButton.setOnClickListener(this);
          if ((Settings.Global.getInt(getActivity().getContentResolver(), "send_action_app_error", 0) != 0) && ((this.mPackages == null) || (this.mPackages.length <= 0)));
        }
        try
        {
          this.mApp = getActivity().getPackageManager().getApplicationInfo(this.mPackages[0], 0);
          this.mInstaller = ApplicationErrorReport.getErrorReportReceiver(getActivity(), this.mPackages[0], this.mApp.flags);
          label594: Button localButton = this.mReportButton;
          if (this.mInstaller != null);
          for (boolean bool = true; ; bool = false)
          {
            localButton.setEnabled(bool);
            return;
            if (k == 0)
              break;
            this.mAppIcon = getActivity().getResources().getDrawable(k);
            break;
          }
          this.mTwoButtonsPanel.setVisibility(8);
          return;
          this.mTwoButtonsPanel.setVisibility(8);
          return;
        }
        catch (PackageManager.NameNotFoundException localNameNotFoundException1)
        {
          break label594;
        }
      }
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException2)
    {
      break label153;
    }
  }

  private void doAction(int paramInt)
  {
    PreferenceActivity localPreferenceActivity = (PreferenceActivity)getActivity();
    switch (paramInt)
    {
    default:
      return;
    case 1:
      localPreferenceActivity.startPreferencePanel(DisplaySettings.class.getName(), null, 2131427995, null, null, 0);
      return;
    case 2:
      localPreferenceActivity.startPreferencePanel(WifiSettings.class.getName(), null, 2131427810, null, null, 0);
      return;
    case 3:
      localPreferenceActivity.startPreferencePanel(BluetoothSettings.class.getName(), null, 2131427703, null, null, 0);
      return;
    case 4:
      localPreferenceActivity.startPreferencePanel(WirelessSettings.class.getName(), null, 2131427572, null, null, 0);
      return;
    case 5:
      startApplicationDetailsActivity();
      return;
    case 6:
      localPreferenceActivity.startPreferencePanel(LocationSettings.class.getName(), null, 2131427618, null, null, 0);
      return;
    case 7:
      killProcesses();
      return;
    case 8:
    }
    reportBatteryUse();
  }

  private void fillControlsSection(int paramInt)
  {
    PackageManager localPackageManager = getActivity().getPackageManager();
    String[] arrayOfString = localPackageManager.getPackagesForUid(paramInt);
    if (arrayOfString != null);
    try
    {
      PackageInfo localPackageInfo2 = localPackageManager.getPackageInfo(arrayOfString[0], 0);
      localPackageInfo1 = localPackageInfo2;
      label43: int i;
      if (localPackageInfo1 != null)
      {
        i = 1;
        switch (2.$SwitchMap$com$android$settings$fuelgauge$PowerUsageDetail$DrainType[this.mDrainType.ordinal()])
        {
        default:
        case 1:
        case 2:
        case 3:
        case 4:
        case 5:
        }
      }
      while (true)
      {
        if (i != 0)
          this.mControlsParent.setVisibility(8);
        return;
        localPackageInfo1 = null;
        break;
        break label43;
        if ((arrayOfString != null) && (arrayOfString.length == 1))
        {
          addControl(2131428758, 2131428775, 5);
          i = 0;
        }
        if ((this.mUsesGps) && (this.mShowLocationButton))
        {
          addControl(2131427618, 2131428776, 6);
          i = 0;
          continue;
          addControl(2131428042, 2131428768, 1);
          i = 0;
          continue;
          addControl(2131427810, 2131428770, 2);
          i = 0;
          continue;
          addControl(2131427703, 2131428772, 3);
          i = 0;
          continue;
          if (this.mNoCoverage > 10.0D)
          {
            addControl(2131427572, 2131428766, 4);
            i = 0;
          }
        }
      }
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      while (true)
        PackageInfo localPackageInfo1 = null;
    }
  }

  private void fillDetailsSection()
  {
    LayoutInflater localLayoutInflater = getActivity().getLayoutInflater();
    if ((this.mTypes != null) && (this.mValues != null))
    {
      int i = 0;
      while (i < this.mTypes.length)
        if (this.mValues[i] <= 0.0D)
        {
          i++;
        }
        else
        {
          String str1 = getString(this.mTypes[i]);
          switch (this.mTypes[i])
          {
          case 2131428747:
          case 2131428748:
          case 2131428753:
          case 2131428754:
          case 2131428755:
          default:
          case 2131428749:
          case 2131428750:
          case 2131428751:
          case 2131428752:
          case 2131428756:
          case 2131428746:
          }
          while (true)
          {
            String str2 = Utils.formatElapsedTime(getActivity(), this.mValues[i], true);
            while (true)
            {
              ViewGroup localViewGroup = (ViewGroup)localLayoutInflater.inflate(2130968661, null);
              this.mDetailsParent.addView(localViewGroup);
              TextView localTextView1 = (TextView)localViewGroup.findViewById(2131230892);
              TextView localTextView2 = (TextView)localViewGroup.findViewById(2131230928);
              localTextView1.setText(str1);
              localTextView2.setText(str2);
              break;
              long l = ()this.mValues[i];
              str2 = Formatter.formatFileSize(getActivity(), l);
              continue;
              int j = (int)Math.floor(this.mValues[i]);
              Activity localActivity = getActivity();
              Object[] arrayOfObject = new Object[1];
              arrayOfObject[0] = Integer.valueOf(j);
              str2 = localActivity.getString(2131429185, arrayOfObject);
            }
            this.mUsesGps = true;
          }
        }
    }
  }

  private void fillPackagesSection(int paramInt)
  {
    if (paramInt < 1)
      removePackagesSection();
    ViewGroup localViewGroup1;
    do
    {
      return;
      localViewGroup1 = (ViewGroup)this.mRootView.findViewById(2131230934);
    }
    while (localViewGroup1 == null);
    LayoutInflater localLayoutInflater = getActivity().getLayoutInflater();
    PackageManager localPackageManager = getActivity().getPackageManager();
    this.mPackages = localPackageManager.getPackagesForUid(paramInt);
    if ((this.mPackages == null) || (this.mPackages.length < 2))
    {
      removePackagesSection();
      return;
    }
    int i = 0;
    while (i < this.mPackages.length)
      try
      {
        CharSequence localCharSequence = localPackageManager.getApplicationInfo(this.mPackages[i], 0).loadLabel(localPackageManager);
        if (localCharSequence != null)
          this.mPackages[i] = localCharSequence.toString();
        ViewGroup localViewGroup2 = (ViewGroup)localLayoutInflater.inflate(2130968663, null);
        localViewGroup1.addView(localViewGroup2);
        ((TextView)localViewGroup2.findViewById(2131230892)).setText(this.mPackages[i]);
        label166: i++;
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException)
      {
        break label166;
      }
  }

  private String getDescriptionForDrainType()
  {
    return getResources().getString(sDrainTypeDesciptions[this.mDrainType.ordinal()]);
  }

  private void killProcesses()
  {
    if (this.mPackages == null)
      return;
    ActivityManager localActivityManager = (ActivityManager)getActivity().getSystemService("activity");
    for (int i = 0; i < this.mPackages.length; i++)
      localActivityManager.forceStopPackage(this.mPackages[i]);
    checkForceStop();
  }

  private void removePackagesSection()
  {
    View localView1 = this.mRootView.findViewById(2131230933);
    if (localView1 != null)
      localView1.setVisibility(8);
    View localView2 = this.mRootView.findViewById(2131230934);
    if (localView2 != null)
      localView2.setVisibility(8);
  }

  private void reportBatteryUse()
  {
    if (this.mPackages == null)
      return;
    ApplicationErrorReport localApplicationErrorReport = new ApplicationErrorReport();
    localApplicationErrorReport.type = 3;
    localApplicationErrorReport.packageName = this.mPackages[0];
    localApplicationErrorReport.installerPackageName = this.mInstaller.getPackageName();
    localApplicationErrorReport.processName = this.mPackages[0];
    localApplicationErrorReport.time = System.currentTimeMillis();
    int i = 0x1 & this.mApp.flags;
    boolean bool = false;
    if (i != 0)
      bool = true;
    localApplicationErrorReport.systemApp = bool;
    Bundle localBundle = getArguments();
    ApplicationErrorReport.BatteryInfo localBatteryInfo = new ApplicationErrorReport.BatteryInfo();
    localBatteryInfo.usagePercent = localBundle.getInt("percent", 1);
    localBatteryInfo.durationMicros = localBundle.getLong("duration", 0L);
    localBatteryInfo.usageDetails = localBundle.getString("report_details");
    localBatteryInfo.checkinDetails = localBundle.getString("report_checkin_details");
    localApplicationErrorReport.batteryInfo = localBatteryInfo;
    Intent localIntent = new Intent("android.intent.action.APP_ERROR");
    localIntent.setComponent(this.mInstaller);
    localIntent.putExtra("android.intent.extra.BUG_REPORT", localApplicationErrorReport);
    localIntent.addFlags(268435456);
    startActivity(localIntent);
  }

  private void startApplicationDetailsActivity()
  {
    Bundle localBundle = new Bundle();
    localBundle.putString("package", this.mPackages[0]);
    ((PreferenceActivity)getActivity()).startPreferencePanel(InstalledAppDetails.class.getName(), localBundle, 2131428365, null, null, 0);
  }

  public void onClick(View paramView)
  {
    doAction(((Integer)paramView.getTag()).intValue());
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mPm = getActivity().getPackageManager();
    this.mDpm = ((DevicePolicyManager)getActivity().getSystemService("device_policy"));
  }

  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    View localView = paramLayoutInflater.inflate(2130968662, paramViewGroup, false);
    com.android.settings.Utils.prepareCustomPreferencesList(paramViewGroup, localView, localView, false);
    this.mRootView = localView;
    createDetails();
    return localView;
  }

  public void onPause()
  {
    super.onPause();
  }

  public void onResume()
  {
    super.onResume();
    this.mStartTime = Process.getElapsedCpuTime();
    checkForceStop();
  }

  static enum DrainType
  {
    static
    {
      CELL = new DrainType("CELL", 1);
      PHONE = new DrainType("PHONE", 2);
      WIFI = new DrainType("WIFI", 3);
      BLUETOOTH = new DrainType("BLUETOOTH", 4);
      SCREEN = new DrainType("SCREEN", 5);
      APP = new DrainType("APP", 6);
      USER = new DrainType("USER", 7);
      DrainType[] arrayOfDrainType = new DrainType[8];
      arrayOfDrainType[0] = IDLE;
      arrayOfDrainType[1] = CELL;
      arrayOfDrainType[2] = PHONE;
      arrayOfDrainType[3] = WIFI;
      arrayOfDrainType[4] = BLUETOOTH;
      arrayOfDrainType[5] = SCREEN;
      arrayOfDrainType[6] = APP;
      arrayOfDrainType[7] = USER;
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.fuelgauge.PowerUsageDetail
 * JD-Core Version:    0.6.2
 */