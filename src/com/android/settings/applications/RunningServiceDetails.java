package com.android.settings.applications;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog.Builder;
import android.app.ApplicationErrorReport;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.provider.Settings.Global;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.android.settings.Utils;
import java.util.ArrayList;
import java.util.Collections;

public class RunningServiceDetails extends Fragment
  implements RunningState.OnRefreshUiListener
{
  final ArrayList<ActiveDetail> mActiveDetails = new ArrayList();
  ViewGroup mAllDetails;
  ActivityManager mAm;
  StringBuilder mBuilder = new StringBuilder(128);
  boolean mHaveData;
  LayoutInflater mInflater;
  RunningState.MergedItem mMergedItem;
  int mNumProcesses;
  int mNumServices;
  String mProcessName;
  TextView mProcessesHeader;
  View mRootView;
  TextView mServicesHeader;
  boolean mShowBackground;
  ViewGroup mSnippet;
  RunningProcessesView.ActiveItem mSnippetActiveItem;
  RunningProcessesView.ViewHolder mSnippetViewHolder;
  RunningState mState;
  int mUid;
  int mUserId;

  private void finish()
  {
    new Handler().post(new Runnable()
    {
      public void run()
      {
        Activity localActivity = RunningServiceDetails.this.getActivity();
        if (localActivity != null)
          localActivity.onBackPressed();
      }
    });
  }

  private void showConfirmStopDialog(ComponentName paramComponentName)
  {
    MyAlertDialogFragment localMyAlertDialogFragment = MyAlertDialogFragment.newConfirmStop(1, paramComponentName);
    localMyAlertDialogFragment.setTargetFragment(this, 0);
    localMyAlertDialogFragment.show(getFragmentManager(), "confirmstop");
  }

  ActiveDetail activeDetailForService(ComponentName paramComponentName)
  {
    for (int i = 0; i < this.mActiveDetails.size(); i++)
    {
      ActiveDetail localActiveDetail = (ActiveDetail)this.mActiveDetails.get(i);
      if ((localActiveDetail.mServiceItem != null) && (localActiveDetail.mServiceItem.mRunningService != null) && (paramComponentName.equals(localActiveDetail.mServiceItem.mRunningService.service)))
        return localActiveDetail;
    }
    return null;
  }

  void addDetailViews()
  {
    for (int i = -1 + this.mActiveDetails.size(); i >= 0; i--)
      this.mAllDetails.removeView(((ActiveDetail)this.mActiveDetails.get(i)).mRootView);
    this.mActiveDetails.clear();
    if (this.mServicesHeader != null)
    {
      this.mAllDetails.removeView(this.mServicesHeader);
      this.mServicesHeader = null;
    }
    if (this.mProcessesHeader != null)
    {
      this.mAllDetails.removeView(this.mProcessesHeader);
      this.mProcessesHeader = null;
    }
    this.mNumProcesses = 0;
    this.mNumServices = 0;
    if (this.mMergedItem != null)
    {
      if (this.mMergedItem.mUser != null)
      {
        ArrayList localArrayList;
        if (this.mShowBackground)
        {
          localArrayList = new ArrayList(this.mMergedItem.mChildren);
          Collections.sort(localArrayList, this.mState.mBackgroundComparator);
        }
        while (true)
        {
          for (int j = 0; j < localArrayList.size(); j++)
            addDetailsViews((RunningState.MergedItem)localArrayList.get(j), true, false);
          localArrayList = this.mMergedItem.mChildren;
        }
        for (int k = 0; k < localArrayList.size(); k++)
          addDetailsViews((RunningState.MergedItem)localArrayList.get(k), false, true);
      }
      addDetailsViews(this.mMergedItem, true, true);
    }
  }

  void addDetailsViews(RunningState.MergedItem paramMergedItem, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramMergedItem != null)
    {
      if (paramBoolean1)
        for (int j = 0; j < paramMergedItem.mServices.size(); j++)
          addServiceDetailsView((RunningState.ServiceItem)paramMergedItem.mServices.get(j), paramMergedItem, true, true);
      if (paramBoolean2)
      {
        if (paramMergedItem.mServices.size() > 0)
          break label91;
        if (paramMergedItem.mUserId == UserHandle.myUserId())
          break label85;
      }
    }
    label85: for (boolean bool2 = true; ; bool2 = false)
    {
      addServiceDetailsView(null, paramMergedItem, false, bool2);
      return;
    }
    label91: int i = -1;
    label94: if (i < paramMergedItem.mOtherProcesses.size())
      if (i >= 0)
        break label136;
    label136: for (RunningState.ProcessItem localProcessItem = paramMergedItem.mProcess; ; localProcessItem = (RunningState.ProcessItem)paramMergedItem.mOtherProcesses.get(i))
    {
      if ((localProcessItem == null) || (localProcessItem.mPid > 0))
        break label153;
      i++;
      break label94;
      break;
    }
    label153: if (i < 0);
    for (boolean bool1 = true; ; bool1 = false)
    {
      addProcessDetailsView(localProcessItem, bool1);
      break;
    }
  }

  void addProcessDetailsView(RunningState.ProcessItem paramProcessItem, boolean paramBoolean)
  {
    addProcessesHeader();
    ActiveDetail localActiveDetail = new ActiveDetail();
    View localView = this.mInflater.inflate(2130968703, this.mAllDetails, false);
    this.mAllDetails.addView(localView);
    localActiveDetail.mRootView = localView;
    localActiveDetail.mViewHolder = new RunningProcessesView.ViewHolder(localView);
    localActiveDetail.mActiveItem = localActiveDetail.mViewHolder.bind(this.mState, paramProcessItem, this.mBuilder);
    TextView localTextView = (TextView)localView.findViewById(2131231037);
    if (paramProcessItem.mUserId != UserHandle.myUserId())
      localTextView.setVisibility(8);
    label358: 
    while (true)
    {
      this.mActiveDetails.add(localActiveDetail);
      return;
      if (paramBoolean)
      {
        localTextView.setText(2131428494);
      }
      else
      {
        ActivityManager.RunningAppProcessInfo localRunningAppProcessInfo = paramProcessItem.mRunningProcessInfo;
        int i = localRunningAppProcessInfo.importanceReasonCode;
        Object localObject = null;
        int j = 0;
        switch (i)
        {
        default:
        case 1:
        case 2:
        }
        while (true)
        {
          if ((j == 0) || (localObject == null))
            break label358;
          localTextView.setText(getActivity().getString(j, new Object[] { localObject }));
          break;
          j = 2131428496;
          ComponentName localComponentName2 = localRunningAppProcessInfo.importanceReasonComponent;
          localObject = null;
          if (localComponentName2 != null)
            try
            {
              ProviderInfo localProviderInfo = getActivity().getPackageManager().getProviderInfo(localRunningAppProcessInfo.importanceReasonComponent, 0);
              CharSequence localCharSequence2 = RunningState.makeLabel(getActivity().getPackageManager(), localProviderInfo.name, localProviderInfo);
              localObject = localCharSequence2;
              continue;
              j = 2131428495;
              ComponentName localComponentName1 = localRunningAppProcessInfo.importanceReasonComponent;
              localObject = null;
              if (localComponentName1 != null)
                try
                {
                  ServiceInfo localServiceInfo = getActivity().getPackageManager().getServiceInfo(localRunningAppProcessInfo.importanceReasonComponent, 0);
                  CharSequence localCharSequence1 = RunningState.makeLabel(getActivity().getPackageManager(), localServiceInfo.name, localServiceInfo);
                  localObject = localCharSequence1;
                }
                catch (PackageManager.NameNotFoundException localNameNotFoundException1)
                {
                  localObject = null;
                }
            }
            catch (PackageManager.NameNotFoundException localNameNotFoundException2)
            {
              localObject = null;
            }
        }
      }
    }
  }

  void addProcessesHeader()
  {
    if (this.mNumProcesses == 0)
    {
      this.mProcessesHeader = ((TextView)this.mInflater.inflate(2130968705, this.mAllDetails, false));
      this.mProcessesHeader.setText(2131428487);
      this.mAllDetails.addView(this.mProcessesHeader);
    }
    this.mNumProcesses = (1 + this.mNumProcesses);
  }

  void addServiceDetailsView(RunningState.ServiceItem paramServiceItem, RunningState.MergedItem paramMergedItem, boolean paramBoolean1, boolean paramBoolean2)
  {
    Object localObject;
    label15: ActiveDetail localActiveDetail;
    TextView localTextView;
    if (paramBoolean1)
    {
      addServicesHeader();
      if (paramServiceItem == null)
        break label258;
      localObject = paramServiceItem;
      localActiveDetail = new ActiveDetail();
      View localView = this.mInflater.inflate(2130968704, this.mAllDetails, false);
      this.mAllDetails.addView(localView);
      localActiveDetail.mRootView = localView;
      localActiveDetail.mServiceItem = paramServiceItem;
      localActiveDetail.mViewHolder = new RunningProcessesView.ViewHolder(localView);
      localActiveDetail.mActiveItem = localActiveDetail.mViewHolder.bind(this.mState, (RunningState.BaseItem)localObject, this.mBuilder);
      if (!paramBoolean2)
        localView.findViewById(2131231036).setVisibility(8);
      if ((paramServiceItem != null) && (paramServiceItem.mRunningService.clientLabel != 0))
        localActiveDetail.mManageIntent = this.mAm.getRunningServiceControlPanel(paramServiceItem.mRunningService.service);
      localTextView = (TextView)localView.findViewById(2131231037);
      localActiveDetail.mStopButton = ((Button)localView.findViewById(2131231089));
      localActiveDetail.mReportButton = ((Button)localView.findViewById(2131231090));
      if ((!paramBoolean1) || (paramMergedItem.mUserId == UserHandle.myUserId()))
        break label264;
      localTextView.setVisibility(8);
      localView.findViewById(2131230852).setVisibility(8);
    }
    while (true)
    {
      this.mActiveDetails.add(localActiveDetail);
      return;
      if (paramMergedItem.mUserId == UserHandle.myUserId())
        break;
      addProcessesHeader();
      break;
      label258: localObject = paramMergedItem;
      break label15;
      label264: label314: int j;
      label350: Button localButton2;
      if ((paramServiceItem != null) && (paramServiceItem.mServiceInfo.descriptionRes != 0))
      {
        localTextView.setText(getActivity().getPackageManager().getText(paramServiceItem.mServiceInfo.packageName, paramServiceItem.mServiceInfo.descriptionRes, paramServiceItem.mServiceInfo.applicationInfo));
        localActiveDetail.mStopButton.setOnClickListener(localActiveDetail);
        Button localButton1 = localActiveDetail.mStopButton;
        Activity localActivity2 = getActivity();
        if (localActiveDetail.mManageIntent == null)
          break label586;
        j = 2131428489;
        localButton1.setText(localActivity2.getText(j));
        localActiveDetail.mReportButton.setOnClickListener(localActiveDetail);
        localActiveDetail.mReportButton.setText(17040389);
        if ((Settings.Global.getInt(getActivity().getContentResolver(), "send_action_app_error", 0) == 0) || (paramServiceItem == null))
          break label600;
        localActiveDetail.mInstaller = ApplicationErrorReport.getErrorReportReceiver(getActivity(), paramServiceItem.mServiceInfo.packageName, paramServiceItem.mServiceInfo.applicationInfo.flags);
        localButton2 = localActiveDetail.mReportButton;
        if (localActiveDetail.mInstaller == null)
          break label594;
      }
      label548: label586: label594: for (boolean bool = true; ; bool = false)
      {
        while (true)
        {
          localButton2.setEnabled(bool);
          break;
          if (paramMergedItem.mBackground)
          {
            localTextView.setText(2131428492);
            break label314;
          }
          if (localActiveDetail.mManageIntent == null)
            break label548;
          try
          {
            String str = getActivity().getPackageManager().getResourcesForApplication(paramServiceItem.mRunningService.clientPackage).getString(paramServiceItem.mRunningService.clientLabel);
            localTextView.setText(getActivity().getString(2131428493, new Object[] { str }));
          }
          catch (PackageManager.NameNotFoundException localNameNotFoundException)
          {
          }
        }
        break label314;
        Activity localActivity1 = getActivity();
        if (paramServiceItem != null);
        for (int i = 2131428490; ; i = 2131428491)
        {
          localTextView.setText(localActivity1.getText(i));
          break;
        }
        j = 2131428488;
        break label350;
      }
      label600: localActiveDetail.mReportButton.setEnabled(false);
    }
  }

  void addServicesHeader()
  {
    if (this.mNumServices == 0)
    {
      this.mServicesHeader = ((TextView)this.mInflater.inflate(2130968705, this.mAllDetails, false));
      this.mServicesHeader.setText(2131428486);
      this.mAllDetails.addView(this.mServicesHeader);
    }
    this.mNumServices = (1 + this.mNumServices);
  }

  void ensureData()
  {
    if (!this.mHaveData)
    {
      this.mHaveData = true;
      this.mState.resume(this);
      this.mState.waitForData();
      refreshUi(true);
    }
  }

  boolean findMergedItem()
  {
    ArrayList localArrayList;
    int i;
    label23: RunningState.MergedItem localMergedItem;
    if (this.mShowBackground)
    {
      localArrayList = this.mState.getCurrentBackgroundItems();
      localObject = null;
      if (localArrayList == null)
        break label142;
      i = 0;
      int j = localArrayList.size();
      localObject = null;
      if (i >= j)
        break label142;
      localMergedItem = (RunningState.MergedItem)localArrayList.get(i);
      if (localMergedItem.mUserId == this.mUserId)
        break label76;
    }
    label76: 
    while (((this.mUid >= 0) && (localMergedItem.mProcess != null) && (localMergedItem.mProcess.mUid != this.mUid)) || ((this.mProcessName != null) && ((localMergedItem.mProcess == null) || (!this.mProcessName.equals(localMergedItem.mProcess.mProcessName)))))
    {
      i++;
      break label23;
      localArrayList = this.mState.getCurrentMergedItems();
      break;
    }
    Object localObject = localMergedItem;
    label142: if (this.mMergedItem != localObject)
    {
      this.mMergedItem = localObject;
      return true;
    }
    return false;
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mUid = getArguments().getInt("uid", -1);
    this.mUserId = getArguments().getInt("user_id", 0);
    this.mProcessName = getArguments().getString("process", null);
    this.mShowBackground = getArguments().getBoolean("background", false);
    this.mAm = ((ActivityManager)getActivity().getSystemService("activity"));
    this.mInflater = ((LayoutInflater)getActivity().getSystemService("layout_inflater"));
    this.mState = RunningState.getInstance(getActivity());
  }

  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    View localView = paramLayoutInflater.inflate(2130968702, paramViewGroup, false);
    Utils.prepareCustomPreferencesList(paramViewGroup, localView, localView, false);
    this.mRootView = localView;
    this.mAllDetails = ((ViewGroup)localView.findViewById(2131230728));
    this.mSnippet = ((ViewGroup)localView.findViewById(2131231035));
    this.mSnippetViewHolder = new RunningProcessesView.ViewHolder(this.mSnippet);
    ensureData();
    return localView;
  }

  public void onPause()
  {
    super.onPause();
    this.mHaveData = false;
    this.mState.pause();
  }

  public void onRefreshUi(int paramInt)
  {
    if (getActivity() == null)
      return;
    switch (paramInt)
    {
    default:
      return;
    case 0:
      updateTimes();
      return;
    case 1:
      refreshUi(false);
      updateTimes();
      return;
    case 2:
    }
    refreshUi(true);
    updateTimes();
  }

  public void onResume()
  {
    super.onResume();
    ensureData();
  }

  void refreshUi(boolean paramBoolean)
  {
    if (findMergedItem())
      paramBoolean = true;
    if (paramBoolean)
    {
      if (this.mMergedItem == null)
        break label48;
      this.mSnippetActiveItem = this.mSnippetViewHolder.bind(this.mState, this.mMergedItem, this.mBuilder);
    }
    while (true)
    {
      addDetailViews();
      return;
      label48: if (this.mSnippetActiveItem == null)
        break;
      this.mSnippetActiveItem.mHolder.size.setText("");
      this.mSnippetActiveItem.mHolder.uptime.setText("");
      this.mSnippetActiveItem.mHolder.description.setText(2131428485);
    }
    finish();
  }

  void updateTimes()
  {
    if (this.mSnippetActiveItem != null)
      this.mSnippetActiveItem.updateTime(getActivity(), this.mBuilder);
    for (int i = 0; i < this.mActiveDetails.size(); i++)
      ((ActiveDetail)this.mActiveDetails.get(i)).mActiveItem.updateTime(getActivity(), this.mBuilder);
  }

  class ActiveDetail
    implements View.OnClickListener
  {
    RunningProcessesView.ActiveItem mActiveItem;
    ComponentName mInstaller;
    PendingIntent mManageIntent;
    Button mReportButton;
    View mRootView;
    RunningState.ServiceItem mServiceItem;
    Button mStopButton;
    RunningProcessesView.ViewHolder mViewHolder;

    ActiveDetail()
    {
    }

    // ERROR //
    public void onClick(View paramView)
    {
      // Byte code:
      //   0: aload_1
      //   1: aload_0
      //   2: getfield 42	com/android/settings/applications/RunningServiceDetails$ActiveDetail:mReportButton	Landroid/widget/Button;
      //   5: if_acmpne +533 -> 538
      //   8: new 44	android/app/ApplicationErrorReport
      //   11: dup
      //   12: invokespecial 45	android/app/ApplicationErrorReport:<init>	()V
      //   15: astore_2
      //   16: aload_2
      //   17: iconst_5
      //   18: putfield 49	android/app/ApplicationErrorReport:type	I
      //   21: aload_2
      //   22: aload_0
      //   23: getfield 51	com/android/settings/applications/RunningServiceDetails$ActiveDetail:mServiceItem	Lcom/android/settings/applications/RunningState$ServiceItem;
      //   26: getfield 57	com/android/settings/applications/RunningState$ServiceItem:mServiceInfo	Landroid/content/pm/ServiceInfo;
      //   29: getfield 63	android/content/pm/ServiceInfo:packageName	Ljava/lang/String;
      //   32: putfield 64	android/app/ApplicationErrorReport:packageName	Ljava/lang/String;
      //   35: aload_2
      //   36: aload_0
      //   37: getfield 66	com/android/settings/applications/RunningServiceDetails$ActiveDetail:mInstaller	Landroid/content/ComponentName;
      //   40: invokevirtual 72	android/content/ComponentName:getPackageName	()Ljava/lang/String;
      //   43: putfield 75	android/app/ApplicationErrorReport:installerPackageName	Ljava/lang/String;
      //   46: aload_2
      //   47: aload_0
      //   48: getfield 51	com/android/settings/applications/RunningServiceDetails$ActiveDetail:mServiceItem	Lcom/android/settings/applications/RunningState$ServiceItem;
      //   51: getfield 79	com/android/settings/applications/RunningState$ServiceItem:mRunningService	Landroid/app/ActivityManager$RunningServiceInfo;
      //   54: getfield 84	android/app/ActivityManager$RunningServiceInfo:process	Ljava/lang/String;
      //   57: putfield 87	android/app/ApplicationErrorReport:processName	Ljava/lang/String;
      //   60: aload_2
      //   61: invokestatic 93	java/lang/System:currentTimeMillis	()J
      //   64: putfield 97	android/app/ApplicationErrorReport:time	J
      //   67: iconst_1
      //   68: aload_0
      //   69: getfield 51	com/android/settings/applications/RunningServiceDetails$ActiveDetail:mServiceItem	Lcom/android/settings/applications/RunningState$ServiceItem;
      //   72: getfield 57	com/android/settings/applications/RunningState$ServiceItem:mServiceInfo	Landroid/content/pm/ServiceInfo;
      //   75: getfield 101	android/content/pm/ServiceInfo:applicationInfo	Landroid/content/pm/ApplicationInfo;
      //   78: getfield 106	android/content/pm/ApplicationInfo:flags	I
      //   81: iand
      //   82: ifeq +303 -> 385
      //   85: iconst_1
      //   86: istore_3
      //   87: aload_2
      //   88: iload_3
      //   89: putfield 110	android/app/ApplicationErrorReport:systemApp	Z
      //   92: new 112	android/app/ApplicationErrorReport$RunningServiceInfo
      //   95: dup
      //   96: invokespecial 113	android/app/ApplicationErrorReport$RunningServiceInfo:<init>	()V
      //   99: astore 4
      //   101: aload_0
      //   102: getfield 115	com/android/settings/applications/RunningServiceDetails$ActiveDetail:mActiveItem	Lcom/android/settings/applications/RunningProcessesView$ActiveItem;
      //   105: getfield 120	com/android/settings/applications/RunningProcessesView$ActiveItem:mFirstRunTime	J
      //   108: lconst_0
      //   109: lcmp
      //   110: iflt +280 -> 390
      //   113: aload 4
      //   115: invokestatic 125	android/os/SystemClock:elapsedRealtime	()J
      //   118: aload_0
      //   119: getfield 115	com/android/settings/applications/RunningServiceDetails$ActiveDetail:mActiveItem	Lcom/android/settings/applications/RunningProcessesView$ActiveItem;
      //   122: getfield 120	com/android/settings/applications/RunningProcessesView$ActiveItem:mFirstRunTime	J
      //   125: lsub
      //   126: putfield 128	android/app/ApplicationErrorReport$RunningServiceInfo:durationMillis	J
      //   129: new 68	android/content/ComponentName
      //   132: dup
      //   133: aload_0
      //   134: getfield 51	com/android/settings/applications/RunningServiceDetails$ActiveDetail:mServiceItem	Lcom/android/settings/applications/RunningState$ServiceItem;
      //   137: getfield 57	com/android/settings/applications/RunningState$ServiceItem:mServiceInfo	Landroid/content/pm/ServiceInfo;
      //   140: getfield 63	android/content/pm/ServiceInfo:packageName	Ljava/lang/String;
      //   143: aload_0
      //   144: getfield 51	com/android/settings/applications/RunningServiceDetails$ActiveDetail:mServiceItem	Lcom/android/settings/applications/RunningState$ServiceItem;
      //   147: getfield 57	com/android/settings/applications/RunningState$ServiceItem:mServiceInfo	Landroid/content/pm/ServiceInfo;
      //   150: getfield 131	android/content/pm/ServiceInfo:name	Ljava/lang/String;
      //   153: invokespecial 134	android/content/ComponentName:<init>	(Ljava/lang/String;Ljava/lang/String;)V
      //   156: astore 5
      //   158: aload_0
      //   159: getfield 27	com/android/settings/applications/RunningServiceDetails$ActiveDetail:this$0	Lcom/android/settings/applications/RunningServiceDetails;
      //   162: invokevirtual 140	com/android/settings/applications/RunningServiceDetails:getActivity	()Landroid/app/Activity;
      //   165: ldc 142
      //   167: invokevirtual 148	android/app/Activity:getFileStreamPath	(Ljava/lang/String;)Ljava/io/File;
      //   170: astore 6
      //   172: aconst_null
      //   173: astore 7
      //   175: new 150	java/io/FileOutputStream
      //   178: dup
      //   179: aload 6
      //   181: invokespecial 153	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
      //   184: astore 8
      //   186: aload 8
      //   188: invokevirtual 157	java/io/FileOutputStream:getFD	()Ljava/io/FileDescriptor;
      //   191: astore 30
      //   193: iconst_3
      //   194: anewarray 159	java/lang/String
      //   197: astore 31
      //   199: aload 31
      //   201: iconst_0
      //   202: ldc 161
      //   204: aastore
      //   205: aload 31
      //   207: iconst_1
      //   208: ldc 163
      //   210: aastore
      //   211: aload 31
      //   213: iconst_2
      //   214: aload 5
      //   216: invokevirtual 166	android/content/ComponentName:flattenToString	()Ljava/lang/String;
      //   219: aastore
      //   220: ldc 168
      //   222: aload 30
      //   224: aload 31
      //   226: invokestatic 174	android/os/Debug:dumpService	(Ljava/lang/String;Ljava/io/FileDescriptor;[Ljava/lang/String;)Z
      //   229: pop
      //   230: aload 8
      //   232: ifnull +509 -> 741
      //   235: aload 8
      //   237: invokevirtual 177	java/io/FileOutputStream:close	()V
      //   240: aconst_null
      //   241: astore 14
      //   243: new 179	java/io/FileInputStream
      //   246: dup
      //   247: aload 6
      //   249: invokespecial 180	java/io/FileInputStream:<init>	(Ljava/io/File;)V
      //   252: astore 15
      //   254: aload 6
      //   256: invokevirtual 185	java/io/File:length	()J
      //   259: l2i
      //   260: newarray byte
      //   262: astore 27
      //   264: aload 15
      //   266: aload 27
      //   268: invokevirtual 189	java/io/FileInputStream:read	([B)I
      //   271: pop
      //   272: aload 4
      //   274: new 159	java/lang/String
      //   277: dup
      //   278: aload 27
      //   280: invokespecial 192	java/lang/String:<init>	([B)V
      //   283: putfield 195	android/app/ApplicationErrorReport$RunningServiceInfo:serviceDetails	Ljava/lang/String;
      //   286: aload 15
      //   288: ifnull +450 -> 738
      //   291: aload 15
      //   293: invokevirtual 196	java/io/FileInputStream:close	()V
      //   296: aload 6
      //   298: invokevirtual 200	java/io/File:delete	()Z
      //   301: pop
      //   302: ldc 202
      //   304: new 204	java/lang/StringBuilder
      //   307: dup
      //   308: invokespecial 205	java/lang/StringBuilder:<init>	()V
      //   311: ldc 207
      //   313: invokevirtual 211	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   316: aload 4
      //   318: getfield 195	android/app/ApplicationErrorReport$RunningServiceInfo:serviceDetails	Ljava/lang/String;
      //   321: invokevirtual 211	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   324: invokevirtual 214	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   327: invokestatic 220	android/util/Log:i	(Ljava/lang/String;Ljava/lang/String;)I
      //   330: pop
      //   331: aload_2
      //   332: aload 4
      //   334: putfield 224	android/app/ApplicationErrorReport:runningServiceInfo	Landroid/app/ApplicationErrorReport$RunningServiceInfo;
      //   337: new 226	android/content/Intent
      //   340: dup
      //   341: ldc 228
      //   343: invokespecial 231	android/content/Intent:<init>	(Ljava/lang/String;)V
      //   346: astore 23
      //   348: aload 23
      //   350: aload_0
      //   351: getfield 66	com/android/settings/applications/RunningServiceDetails$ActiveDetail:mInstaller	Landroid/content/ComponentName;
      //   354: invokevirtual 235	android/content/Intent:setComponent	(Landroid/content/ComponentName;)Landroid/content/Intent;
      //   357: pop
      //   358: aload 23
      //   360: ldc 237
      //   362: aload_2
      //   363: invokevirtual 241	android/content/Intent:putExtra	(Ljava/lang/String;Landroid/os/Parcelable;)Landroid/content/Intent;
      //   366: pop
      //   367: aload 23
      //   369: ldc 242
      //   371: invokevirtual 246	android/content/Intent:addFlags	(I)Landroid/content/Intent;
      //   374: pop
      //   375: aload_0
      //   376: getfield 27	com/android/settings/applications/RunningServiceDetails$ActiveDetail:this$0	Lcom/android/settings/applications/RunningServiceDetails;
      //   379: aload 23
      //   381: invokevirtual 250	com/android/settings/applications/RunningServiceDetails:startActivity	(Landroid/content/Intent;)V
      //   384: return
      //   385: iconst_0
      //   386: istore_3
      //   387: goto -300 -> 87
      //   390: aload 4
      //   392: ldc2_w 251
      //   395: putfield 128	android/app/ApplicationErrorReport$RunningServiceInfo:durationMillis	J
      //   398: goto -269 -> 129
      //   401: astore 33
      //   403: goto -163 -> 240
      //   406: astore 9
      //   408: ldc 202
      //   410: new 204	java/lang/StringBuilder
      //   413: dup
      //   414: invokespecial 205	java/lang/StringBuilder:<init>	()V
      //   417: ldc 254
      //   419: invokevirtual 211	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   422: aload 5
      //   424: invokevirtual 257	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   427: invokevirtual 214	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   430: aload 9
      //   432: invokestatic 261	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   435: pop
      //   436: aload 7
      //   438: ifnull -198 -> 240
      //   441: aload 7
      //   443: invokevirtual 177	java/io/FileOutputStream:close	()V
      //   446: goto -206 -> 240
      //   449: astore 13
      //   451: goto -211 -> 240
      //   454: astore 10
      //   456: aload 7
      //   458: ifnull +8 -> 466
      //   461: aload 7
      //   463: invokevirtual 177	java/io/FileOutputStream:close	()V
      //   466: aload 10
      //   468: athrow
      //   469: astore 29
      //   471: goto -175 -> 296
      //   474: astore 16
      //   476: ldc 202
      //   478: new 204	java/lang/StringBuilder
      //   481: dup
      //   482: invokespecial 205	java/lang/StringBuilder:<init>	()V
      //   485: ldc_w 263
      //   488: invokevirtual 211	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   491: aload 5
      //   493: invokevirtual 257	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   496: invokevirtual 214	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   499: aload 16
      //   501: invokestatic 261	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   504: pop
      //   505: aload 14
      //   507: ifnull -211 -> 296
      //   510: aload 14
      //   512: invokevirtual 196	java/io/FileInputStream:close	()V
      //   515: goto -219 -> 296
      //   518: astore 20
      //   520: goto -224 -> 296
      //   523: astore 17
      //   525: aload 14
      //   527: ifnull +8 -> 535
      //   530: aload 14
      //   532: invokevirtual 196	java/io/FileInputStream:close	()V
      //   535: aload 17
      //   537: athrow
      //   538: aload_0
      //   539: getfield 265	com/android/settings/applications/RunningServiceDetails$ActiveDetail:mManageIntent	Landroid/app/PendingIntent;
      //   542: ifnull +62 -> 604
      //   545: aload_0
      //   546: getfield 27	com/android/settings/applications/RunningServiceDetails$ActiveDetail:this$0	Lcom/android/settings/applications/RunningServiceDetails;
      //   549: invokevirtual 140	com/android/settings/applications/RunningServiceDetails:getActivity	()Landroid/app/Activity;
      //   552: aload_0
      //   553: getfield 265	com/android/settings/applications/RunningServiceDetails$ActiveDetail:mManageIntent	Landroid/app/PendingIntent;
      //   556: invokevirtual 271	android/app/PendingIntent:getIntentSender	()Landroid/content/IntentSender;
      //   559: aconst_null
      //   560: ldc_w 272
      //   563: ldc_w 273
      //   566: iconst_0
      //   567: invokevirtual 277	android/app/Activity:startIntentSender	(Landroid/content/IntentSender;Landroid/content/Intent;III)V
      //   570: return
      //   571: astore 38
      //   573: ldc 202
      //   575: aload 38
      //   577: invokestatic 280	android/util/Log:w	(Ljava/lang/String;Ljava/lang/Throwable;)I
      //   580: pop
      //   581: return
      //   582: astore 36
      //   584: ldc 202
      //   586: aload 36
      //   588: invokestatic 280	android/util/Log:w	(Ljava/lang/String;Ljava/lang/Throwable;)I
      //   591: pop
      //   592: return
      //   593: astore 34
      //   595: ldc 202
      //   597: aload 34
      //   599: invokestatic 280	android/util/Log:w	(Ljava/lang/String;Ljava/lang/Throwable;)I
      //   602: pop
      //   603: return
      //   604: aload_0
      //   605: getfield 51	com/android/settings/applications/RunningServiceDetails$ActiveDetail:mServiceItem	Lcom/android/settings/applications/RunningState$ServiceItem;
      //   608: ifnull +9 -> 617
      //   611: aload_0
      //   612: iconst_0
      //   613: invokevirtual 284	com/android/settings/applications/RunningServiceDetails$ActiveDetail:stopActiveService	(Z)V
      //   616: return
      //   617: aload_0
      //   618: getfield 115	com/android/settings/applications/RunningServiceDetails$ActiveDetail:mActiveItem	Lcom/android/settings/applications/RunningProcessesView$ActiveItem;
      //   621: getfield 288	com/android/settings/applications/RunningProcessesView$ActiveItem:mItem	Lcom/android/settings/applications/RunningState$BaseItem;
      //   624: getfield 293	com/android/settings/applications/RunningState$BaseItem:mBackground	Z
      //   627: ifeq +34 -> 661
      //   630: aload_0
      //   631: getfield 27	com/android/settings/applications/RunningServiceDetails$ActiveDetail:this$0	Lcom/android/settings/applications/RunningServiceDetails;
      //   634: getfield 297	com/android/settings/applications/RunningServiceDetails:mAm	Landroid/app/ActivityManager;
      //   637: aload_0
      //   638: getfield 115	com/android/settings/applications/RunningServiceDetails$ActiveDetail:mActiveItem	Lcom/android/settings/applications/RunningProcessesView$ActiveItem;
      //   641: getfield 288	com/android/settings/applications/RunningProcessesView$ActiveItem:mItem	Lcom/android/settings/applications/RunningState$BaseItem;
      //   644: getfield 301	com/android/settings/applications/RunningState$BaseItem:mPackageInfo	Landroid/content/pm/PackageItemInfo;
      //   647: getfield 304	android/content/pm/PackageItemInfo:packageName	Ljava/lang/String;
      //   650: invokevirtual 309	android/app/ActivityManager:killBackgroundProcesses	(Ljava/lang/String;)V
      //   653: aload_0
      //   654: getfield 27	com/android/settings/applications/RunningServiceDetails$ActiveDetail:this$0	Lcom/android/settings/applications/RunningServiceDetails;
      //   657: invokestatic 312	com/android/settings/applications/RunningServiceDetails:access$100	(Lcom/android/settings/applications/RunningServiceDetails;)V
      //   660: return
      //   661: aload_0
      //   662: getfield 27	com/android/settings/applications/RunningServiceDetails$ActiveDetail:this$0	Lcom/android/settings/applications/RunningServiceDetails;
      //   665: getfield 297	com/android/settings/applications/RunningServiceDetails:mAm	Landroid/app/ActivityManager;
      //   668: aload_0
      //   669: getfield 115	com/android/settings/applications/RunningServiceDetails$ActiveDetail:mActiveItem	Lcom/android/settings/applications/RunningProcessesView$ActiveItem;
      //   672: getfield 288	com/android/settings/applications/RunningProcessesView$ActiveItem:mItem	Lcom/android/settings/applications/RunningState$BaseItem;
      //   675: getfield 301	com/android/settings/applications/RunningState$BaseItem:mPackageInfo	Landroid/content/pm/PackageItemInfo;
      //   678: getfield 304	android/content/pm/PackageItemInfo:packageName	Ljava/lang/String;
      //   681: invokevirtual 315	android/app/ActivityManager:forceStopPackage	(Ljava/lang/String;)V
      //   684: aload_0
      //   685: getfield 27	com/android/settings/applications/RunningServiceDetails$ActiveDetail:this$0	Lcom/android/settings/applications/RunningServiceDetails;
      //   688: invokestatic 312	com/android/settings/applications/RunningServiceDetails:access$100	(Lcom/android/settings/applications/RunningServiceDetails;)V
      //   691: return
      //   692: astore 11
      //   694: goto -228 -> 466
      //   697: astore 18
      //   699: goto -164 -> 535
      //   702: astore 17
      //   704: aload 15
      //   706: astore 14
      //   708: goto -183 -> 525
      //   711: astore 16
      //   713: aload 15
      //   715: astore 14
      //   717: goto -241 -> 476
      //   720: astore 10
      //   722: aload 8
      //   724: astore 7
      //   726: goto -270 -> 456
      //   729: astore 9
      //   731: aload 8
      //   733: astore 7
      //   735: goto -327 -> 408
      //   738: goto -442 -> 296
      //   741: goto -501 -> 240
      //
      // Exception table:
      //   from	to	target	type
      //   235	240	401	java/io/IOException
      //   175	186	406	java/io/IOException
      //   441	446	449	java/io/IOException
      //   175	186	454	finally
      //   408	436	454	finally
      //   291	296	469	java/io/IOException
      //   243	254	474	java/io/IOException
      //   510	515	518	java/io/IOException
      //   243	254	523	finally
      //   476	505	523	finally
      //   545	570	571	android/content/IntentSender$SendIntentException
      //   545	570	582	java/lang/IllegalArgumentException
      //   545	570	593	android/content/ActivityNotFoundException
      //   461	466	692	java/io/IOException
      //   530	535	697	java/io/IOException
      //   254	286	702	finally
      //   254	286	711	java/io/IOException
      //   186	230	720	finally
      //   186	230	729	java/io/IOException
    }

    void stopActiveService(boolean paramBoolean)
    {
      RunningState.ServiceItem localServiceItem = this.mServiceItem;
      if ((!paramBoolean) && ((0x1 & localServiceItem.mServiceInfo.applicationInfo.flags) != 0))
      {
        RunningServiceDetails.this.showConfirmStopDialog(localServiceItem.mRunningService.service);
        return;
      }
      RunningServiceDetails.this.getActivity().stopService(new Intent().setComponent(localServiceItem.mRunningService.service));
      if (RunningServiceDetails.this.mMergedItem == null)
      {
        RunningServiceDetails.this.mState.updateNow();
        RunningServiceDetails.this.finish();
        return;
      }
      if ((!RunningServiceDetails.this.mShowBackground) && (RunningServiceDetails.this.mMergedItem.mServices.size() <= 1))
      {
        RunningServiceDetails.this.mState.updateNow();
        RunningServiceDetails.this.finish();
        return;
      }
      RunningServiceDetails.this.mState.updateNow();
    }
  }

  public static class MyAlertDialogFragment extends DialogFragment
  {
    public static MyAlertDialogFragment newConfirmStop(int paramInt, ComponentName paramComponentName)
    {
      MyAlertDialogFragment localMyAlertDialogFragment = new MyAlertDialogFragment();
      Bundle localBundle = new Bundle();
      localBundle.putInt("id", paramInt);
      localBundle.putParcelable("comp", paramComponentName);
      localMyAlertDialogFragment.setArguments(localBundle);
      return localMyAlertDialogFragment;
    }

    RunningServiceDetails getOwner()
    {
      return (RunningServiceDetails)getTargetFragment();
    }

    public Dialog onCreateDialog(Bundle paramBundle)
    {
      int i = getArguments().getInt("id");
      switch (i)
      {
      default:
        throw new IllegalArgumentException("unknown id " + i);
      case 1:
      }
      final ComponentName localComponentName = (ComponentName)getArguments().getParcelable("comp");
      if (getOwner().activeDetailForService(localComponentName) == null)
        return null;
      return new AlertDialog.Builder(getActivity()).setTitle(getActivity().getString(2131428497)).setIconAttribute(16843605).setMessage(getActivity().getString(2131428498)).setPositiveButton(2131428420, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          RunningServiceDetails.ActiveDetail localActiveDetail = RunningServiceDetails.MyAlertDialogFragment.this.getOwner().activeDetailForService(localComponentName);
          if (localActiveDetail != null)
            localActiveDetail.stopActiveService(true);
        }
      }).setNegativeButton(2131428421, null).create();
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.applications.RunningServiceDetails
 * JD-Core Version:    0.6.2
 */