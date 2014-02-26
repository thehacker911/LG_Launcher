package com.android.settings.applications;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Fragment;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserHandle;
import android.preference.PreferenceActivity;
import android.text.format.Formatter;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.android.settings.Utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ProcessStatsDetail extends Fragment
  implements View.OnClickListener
{
  static final Comparator<ProcStatsEntry.Service> sServiceCompare = new Comparator()
  {
    public int compare(ProcStatsEntry.Service paramAnonymousService1, ProcStatsEntry.Service paramAnonymousService2)
    {
      if (paramAnonymousService1.mDuration < paramAnonymousService2.mDuration)
        return 1;
      if (paramAnonymousService1.mDuration > paramAnonymousService2.mDuration)
        return -1;
      return 0;
    }
  };
  static final Comparator<ArrayList<ProcStatsEntry.Service>> sServicePkgCompare = new Comparator()
  {
    public int compare(ArrayList<ProcStatsEntry.Service> paramAnonymousArrayList1, ArrayList<ProcStatsEntry.Service> paramAnonymousArrayList2)
    {
      long l1;
      if (paramAnonymousArrayList1.size() > 0)
      {
        l1 = ((ProcStatsEntry.Service)paramAnonymousArrayList1.get(0)).mDuration;
        if (paramAnonymousArrayList2.size() <= 0)
          break label53;
      }
      label53: for (long l2 = ((ProcStatsEntry.Service)paramAnonymousArrayList2.get(0)).mDuration; ; l2 = 0L)
      {
        if (l1 >= l2)
          break label59;
        return 1;
        l1 = 0L;
        break;
      }
      label59: if (l1 > l2)
        return -1;
      return 0;
    }
  };
  private final BroadcastReceiver mCheckKillProcessesReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      Button localButton = ProcessStatsDetail.this.mForceStopButton;
      if (getResultCode() != 0);
      for (boolean bool = true; ; bool = false)
      {
        localButton.setEnabled(bool);
        return;
      }
    }
  };
  private ViewGroup mDetailsParent;
  private DevicePolicyManager mDpm;
  private ProcStatsEntry mEntry;
  private Button mForceStopButton;
  private long mMaxWeight;
  private PackageManager mPm;
  private Button mReportButton;
  private View mRootView;
  private ViewGroup mServicesParent;
  private TextView mTitleView;
  private long mTotalTime;
  private ViewGroup mTwoButtonsPanel;
  private boolean mUseUss;

  private void addDetailsItem(ViewGroup paramViewGroup, CharSequence paramCharSequence1, CharSequence paramCharSequence2)
  {
    ViewGroup localViewGroup = (ViewGroup)getActivity().getLayoutInflater().inflate(2130968661, null);
    paramViewGroup.addView(localViewGroup);
    TextView localTextView1 = (TextView)localViewGroup.findViewById(2131230892);
    TextView localTextView2 = (TextView)localViewGroup.findViewById(2131230928);
    localTextView1.setText(paramCharSequence1);
    localTextView2.setText(paramCharSequence2);
  }

  private void addPackageHeaderItem(ViewGroup paramViewGroup, String paramString)
  {
    ViewGroup localViewGroup = (ViewGroup)getActivity().getLayoutInflater().inflate(2130968700, null);
    paramViewGroup.addView(localViewGroup);
    ImageView localImageView = (ImageView)localViewGroup.findViewById(2131230755);
    TextView localTextView1 = (TextView)localViewGroup.findViewById(2131230837);
    TextView localTextView2 = (TextView)localViewGroup.findViewById(2131230838);
    try
    {
      ApplicationInfo localApplicationInfo = this.mPm.getApplicationInfo(paramString, 0);
      localImageView.setImageDrawable(localApplicationInfo.loadIcon(this.mPm));
      localTextView1.setText(localApplicationInfo.loadLabel(this.mPm));
      label94: localTextView2.setText(paramString);
      return;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      break label94;
    }
  }

  private void checkForceStop()
  {
    if ((this.mEntry.mUiPackage == null) || (this.mEntry.mUid < 10000))
    {
      this.mForceStopButton.setEnabled(false);
      return;
    }
    if (this.mDpm.packageHasActiveAdmins(this.mEntry.mUiPackage))
    {
      this.mForceStopButton.setEnabled(false);
      return;
    }
    try
    {
      if ((0x200000 & this.mPm.getApplicationInfo(this.mEntry.mUiPackage, 0).flags) == 0)
        this.mForceStopButton.setEnabled(true);
      label90: Intent localIntent = new Intent("android.intent.action.QUERY_PACKAGE_RESTART", Uri.fromParts("package", this.mEntry.mUiPackage, null));
      String[] arrayOfString = new String[1];
      arrayOfString[0] = this.mEntry.mUiPackage;
      localIntent.putExtra("android.intent.extra.PACKAGES", arrayOfString);
      localIntent.putExtra("android.intent.extra.UID", this.mEntry.mUid);
      localIntent.putExtra("android.intent.extra.user_handle", UserHandle.getUserId(this.mEntry.mUid));
      getActivity().sendOrderedBroadcast(localIntent, null, this.mCheckKillProcessesReceiver, null, 0, null, null);
      return;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      break label90;
    }
  }

  private void createDetails()
  {
    int i = (int)Math.ceil(100.0D * (this.mEntry.mWeight / this.mMaxWeight));
    String str = makePercentString(getResources(), this.mEntry.mDuration, this.mTotalTime);
    TextView localTextView = (TextView)this.mRootView.findViewById(16908304);
    localTextView.setText(this.mEntry.mName);
    localTextView.setVisibility(0);
    this.mTitleView = ((TextView)this.mRootView.findViewById(16908310));
    this.mTitleView.setText(this.mEntry.mUiBaseLabel);
    ((TextView)this.mRootView.findViewById(16908308)).setText(str);
    ((ProgressBar)this.mRootView.findViewById(16908301)).setProgress(i);
    ImageView localImageView = (ImageView)this.mRootView.findViewById(16908294);
    if (this.mEntry.mUiTargetApp != null)
      localImageView.setImageDrawable(this.mEntry.mUiTargetApp.loadIcon(this.mPm));
    this.mTwoButtonsPanel = ((ViewGroup)this.mRootView.findViewById(2131230929));
    this.mForceStopButton = ((Button)this.mRootView.findViewById(2131231090));
    this.mReportButton = ((Button)this.mRootView.findViewById(2131231089));
    this.mForceStopButton.setEnabled(false);
    this.mReportButton.setVisibility(4);
    this.mDetailsParent = ((ViewGroup)this.mRootView.findViewById(2131230930));
    this.mServicesParent = ((ViewGroup)this.mRootView.findViewById(2131230984));
    fillDetailsSection();
    fillServicesSection();
    if (this.mEntry.mUid >= 10000)
    {
      this.mForceStopButton.setText(2131428375);
      this.mForceStopButton.setTag(Integer.valueOf(1));
      this.mForceStopButton.setOnClickListener(this);
      this.mTwoButtonsPanel.setVisibility(0);
      return;
    }
    this.mTwoButtonsPanel.setVisibility(8);
  }

  private void doAction(int paramInt)
  {
    ((PreferenceActivity)getActivity());
    switch (paramInt)
    {
    default:
      return;
    case 1:
    }
    killProcesses();
  }

  private void fillDetailsSection()
  {
    ViewGroup localViewGroup1 = this.mDetailsParent;
    CharSequence localCharSequence1 = getResources().getText(2131428793);
    Activity localActivity1 = getActivity();
    long l1;
    ViewGroup localViewGroup2;
    CharSequence localCharSequence2;
    Activity localActivity2;
    if (this.mUseUss)
    {
      l1 = this.mEntry.mAvgUss;
      addDetailsItem(localViewGroup1, localCharSequence1, Formatter.formatShortFileSize(localActivity1, l1 * 1024L));
      localViewGroup2 = this.mDetailsParent;
      localCharSequence2 = getResources().getText(2131428794);
      localActivity2 = getActivity();
      if (!this.mUseUss)
        break label161;
    }
    label161: for (long l2 = this.mEntry.mMaxUss; ; l2 = this.mEntry.mMaxPss)
    {
      addDetailsItem(localViewGroup2, localCharSequence2, Formatter.formatShortFileSize(localActivity2, l2 * 1024L));
      addDetailsItem(this.mDetailsParent, getResources().getText(2131428795), makePercentString(getResources(), this.mEntry.mDuration, this.mTotalTime));
      return;
      l1 = this.mEntry.mAvgPss;
      break;
    }
  }

  private void fillServicesSection()
  {
    if (this.mEntry.mServices.size() > 0)
    {
      ArrayList localArrayList1 = new ArrayList();
      for (int i = 0; i < this.mEntry.mServices.size(); i++)
      {
        ArrayList localArrayList3 = (ArrayList)((ArrayList)this.mEntry.mServices.valueAt(i)).clone();
        Collections.sort(localArrayList3, sServiceCompare);
        localArrayList1.add(localArrayList3);
      }
      int j;
      if (this.mEntry.mServices.size() <= 1)
      {
        boolean bool = ((ProcStatsEntry.Service)((ArrayList)this.mEntry.mServices.valueAt(0)).get(0)).mPackage.equals(this.mEntry.mPackage);
        j = 0;
        if (bool);
      }
      else
      {
        j = 1;
        Collections.sort(localArrayList1, sServicePkgCompare);
      }
      for (int k = 0; k < localArrayList1.size(); k++)
      {
        ArrayList localArrayList2 = (ArrayList)localArrayList1.get(k);
        if (j != 0)
          addPackageHeaderItem(this.mServicesParent, ((ProcStatsEntry.Service)localArrayList2.get(0)).mPackage);
        for (int m = 0; m < localArrayList2.size(); m++)
        {
          ProcStatsEntry.Service localService = (ProcStatsEntry.Service)localArrayList2.get(m);
          String str = localService.mName;
          int n = str.lastIndexOf('.');
          if ((n >= 0) && (n < -1 + str.length()))
            str = str.substring(n + 1);
          double d = 100.0D * (localService.mDuration / this.mTotalTime);
          ViewGroup localViewGroup = this.mServicesParent;
          Resources localResources = getActivity().getResources();
          Object[] arrayOfObject = new Object[1];
          arrayOfObject[0] = Integer.valueOf((int)Math.ceil(d));
          addDetailsItem(localViewGroup, str, localResources.getString(2131429185, arrayOfObject));
        }
      }
    }
  }

  private void killProcesses()
  {
    ((ActivityManager)getActivity().getSystemService("activity")).forceStopPackage(this.mEntry.mUiPackage);
    checkForceStop();
  }

  public static String makePercentString(Resources paramResources, long paramLong1, long paramLong2)
  {
    double d = 100.0D * (paramLong1 / paramLong2);
    Object[] arrayOfObject = new Object[1];
    arrayOfObject[0] = Integer.valueOf((int)Math.round(d));
    return paramResources.getString(2131429185, arrayOfObject);
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
    Bundle localBundle = getArguments();
    this.mEntry = ((ProcStatsEntry)localBundle.getParcelable("entry"));
    this.mEntry.retrieveUiData(this.mPm);
    this.mUseUss = localBundle.getBoolean("use_uss");
    this.mMaxWeight = localBundle.getLong("max_weight");
    this.mTotalTime = localBundle.getLong("total_time");
  }

  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    View localView = paramLayoutInflater.inflate(2130968694, paramViewGroup, false);
    Utils.prepareCustomPreferencesList(paramViewGroup, localView, localView, false);
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
    checkForceStop();
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.applications.ProcessStatsDetail
 * JD-Core Version:    0.6.2
 */