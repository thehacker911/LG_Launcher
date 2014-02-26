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
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageItemInfo;
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
        break label259;
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
        break label265;
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
      label259: localObject = paramMergedItem;
      break label15;
      label265: label315: int j;
      label351: Button localButton2;
      if ((paramServiceItem != null) && (paramServiceItem.mServiceInfo.descriptionRes != 0))
      {
        localTextView.setText(getActivity().getPackageManager().getText(paramServiceItem.mServiceInfo.packageName, paramServiceItem.mServiceInfo.descriptionRes, paramServiceItem.mServiceInfo.applicationInfo));
        localActiveDetail.mStopButton.setOnClickListener(localActiveDetail);
        Button localButton1 = localActiveDetail.mStopButton;
        Activity localActivity2 = getActivity();
        if (localActiveDetail.mManageIntent == null)
          break label587;
        j = 2131428489;
        localButton1.setText(localActivity2.getText(j));
        localActiveDetail.mReportButton.setOnClickListener(localActiveDetail);
        localActiveDetail.mReportButton.setText(17040389);
        if ((Settings.Global.getInt(getActivity().getContentResolver(), "send_action_app_error", 0) == 0) || (paramServiceItem == null))
          break label601;
        localActiveDetail.mInstaller = ApplicationErrorReport.getErrorReportReceiver(getActivity(), paramServiceItem.mServiceInfo.packageName, paramServiceItem.mServiceInfo.applicationInfo.flags);
        localButton2 = localActiveDetail.mReportButton;
        if (localActiveDetail.mInstaller == null)
          break label595;
      }
      label549: label587: label595: for (boolean bool = true; ; bool = false)
      {
        while (true)
        {
          localButton2.setEnabled(bool);
          break;
          if (paramMergedItem.mBackground)
          {
            localTextView.setText(2131428492);
            break label315;
          }
          if (localActiveDetail.mManageIntent == null)
            break label549;
          try
          {
            String str = getActivity().getPackageManager().getResourcesForApplication(paramServiceItem.mRunningService.clientPackage).getString(paramServiceItem.mRunningService.clientLabel);
            localTextView.setText(getActivity().getString(2131428493, new Object[] { str }));
          }
          catch (PackageManager.NameNotFoundException localNameNotFoundException)
          {
          }
        }
        break label315;
        Activity localActivity1 = getActivity();
        if (paramServiceItem != null);
        for (int i = 2131428490; ; i = 2131428491)
        {
          localTextView.setText(localActivity1.getText(i));
          break;
        }
        j = 2131428488;
        break label351;
      }
      label601: localActiveDetail.mReportButton.setEnabled(false);
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
      //   5: if_acmpne +534 -> 539
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
      //   29: getfield 63	android/content/pm/PackageItemInfo:packageName	Ljava/lang/String;
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
      //   75: getfield 103	android/content/pm/ComponentInfo:applicationInfo	Landroid/content/pm/ApplicationInfo;
      //   78: getfield 108	android/content/pm/ApplicationInfo:flags	I
      //   81: iand
      //   82: ifeq +303 -> 385
      //   85: iconst_1
      //   86: istore_3
      //   87: aload_2
      //   88: iload_3
      //   89: putfield 112	android/app/ApplicationErrorReport:systemApp	Z
      //   92: new 114	android/app/ApplicationErrorReport$RunningServiceInfo
      //   95: dup
      //   96: invokespecial 115	android/app/ApplicationErrorReport$RunningServiceInfo:<init>	()V
      //   99: astore 4
      //   101: aload_0
      //   102: getfield 117	com/android/settings/applications/RunningServiceDetails$ActiveDetail:mActiveItem	Lcom/android/settings/applications/RunningProcessesView$ActiveItem;
      //   105: getfield 122	com/android/settings/applications/RunningProcessesView$ActiveItem:mFirstRunTime	J
      //   108: lconst_0
      //   109: lcmp
      //   110: iflt +280 -> 390
      //   113: aload 4
      //   115: invokestatic 127	android/os/SystemClock:elapsedRealtime	()J
      //   118: aload_0
      //   119: getfield 117	com/android/settings/applications/RunningServiceDetails$ActiveDetail:mActiveItem	Lcom/android/settings/applications/RunningProcessesView$ActiveItem;
      //   122: getfield 122	com/android/settings/applications/RunningProcessesView$ActiveItem:mFirstRunTime	J
      //   125: lsub
      //   126: putfield 130	android/app/ApplicationErrorReport$RunningServiceInfo:durationMillis	J
      //   129: new 68	android/content/ComponentName
      //   132: dup
      //   133: aload_0
      //   134: getfield 51	com/android/settings/applications/RunningServiceDetails$ActiveDetail:mServiceItem	Lcom/android/settings/applications/RunningState$ServiceItem;
      //   137: getfield 57	com/android/settings/applications/RunningState$ServiceItem:mServiceInfo	Landroid/content/pm/ServiceInfo;
      //   140: getfield 63	android/content/pm/PackageItemInfo:packageName	Ljava/lang/String;
      //   143: aload_0
      //   144: getfield 51	com/android/settings/applications/RunningServiceDetails$ActiveDetail:mServiceItem	Lcom/android/settings/applications/RunningState$ServiceItem;
      //   147: getfield 57	com/android/settings/applications/RunningState$ServiceItem:mServiceInfo	Landroid/content/pm/ServiceInfo;
      //   150: getfield 133	android/content/pm/PackageItemInfo:name	Ljava/lang/String;
      //   153: invokespecial 136	android/content/ComponentName:<init>	(Ljava/lang/String;Ljava/lang/String;)V
      //   156: astore 5
      //   158: aload_0
      //   159: getfield 27	com/android/settings/applications/RunningServiceDetails$ActiveDetail:this$0	Lcom/android/settings/applications/RunningServiceDetails;
      //   162: invokevirtual 142	android/app/Fragment:getActivity	()Landroid/app/Activity;
      //   165: ldc 144
      //   167: invokevirtual 150	android/content/ContextWrapper:getFileStreamPath	(Ljava/lang/String;)Ljava/io/File;
      //   170: astore 6
      //   172: aconst_null
      //   173: astore 7
      //   175: new 152	java/io/FileOutputStream
      //   178: dup
      //   179: aload 6
      //   181: invokespecial 155	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
      //   184: astore 8
      //   186: aload 8
      //   188: invokevirtual 159	java/io/FileOutputStream:getFD	()Ljava/io/FileDescriptor;
      //   191: astore 30
      //   193: iconst_3
      //   194: anewarray 161	java/lang/String
      //   197: astore 31
      //   199: aload 31
      //   201: iconst_0
      //   202: ldc 163
      //   204: aastore
      //   205: aload 31
      //   207: iconst_1
      //   208: ldc 165
      //   210: aastore
      //   211: aload 31
      //   213: iconst_2
      //   214: aload 5
      //   216: invokevirtual 168	android/content/ComponentName:flattenToString	()Ljava/lang/String;
      //   219: aastore
      //   220: ldc 170
      //   222: aload 30
      //   224: aload 31
      //   226: invokestatic 176	android/os/Debug:dumpService	(Ljava/lang/String;Ljava/io/FileDescriptor;[Ljava/lang/String;)Z
      //   229: pop
      //   230: aload 8
      //   232: ifnull +510 -> 742
      //   235: aload 8
      //   237: invokevirtual 179	java/io/FileOutputStream:close	()V
      //   240: aconst_null
      //   241: astore 14
      //   243: new 181	java/io/FileInputStream
      //   246: dup
      //   247: aload 6
      //   249: invokespecial 182	java/io/FileInputStream:<init>	(Ljava/io/File;)V
      //   252: astore 15
      //   254: aload 6
      //   256: invokevirtual 187	java/io/File:length	()J
      //   259: l2i
      //   260: newarray byte
      //   262: astore 27
      //   264: aload 15
      //   266: aload 27
      //   268: invokevirtual 193	java/io/InputStream:read	([B)I
      //   271: pop
      //   272: aload 4
      //   274: new 161	java/lang/String
      //   277: dup
      //   278: aload 27
      //   280: invokespecial 196	java/lang/String:<init>	([B)V
      //   283: putfield 199	android/app/ApplicationErrorReport$RunningServiceInfo:serviceDetails	Ljava/lang/String;
      //   286: aload 15
      //   288: ifnull +451 -> 739
      //   291: aload 15
      //   293: invokevirtual 200	java/io/FileInputStream:close	()V
      //   296: aload 6
      //   298: invokevirtual 204	java/io/File:delete	()Z
      //   301: pop
      //   302: ldc 206
      //   304: new 208	java/lang/StringBuilder
      //   307: dup
      //   308: invokespecial 209	java/lang/StringBuilder:<init>	()V
      //   311: ldc 211
      //   313: invokevirtual 215	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   316: aload 4
      //   318: getfield 199	android/app/ApplicationErrorReport$RunningServiceInfo:serviceDetails	Ljava/lang/String;
      //   321: invokevirtual 215	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   324: invokevirtual 218	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   327: invokestatic 224	android/util/Log:i	(Ljava/lang/String;Ljava/lang/String;)I
      //   330: pop
      //   331: aload_2
      //   332: aload 4
      //   334: putfield 228	android/app/ApplicationErrorReport:runningServiceInfo	Landroid/app/ApplicationErrorReport$RunningServiceInfo;
      //   337: new 230	android/content/Intent
      //   340: dup
      //   341: ldc 232
      //   343: invokespecial 235	android/content/Intent:<init>	(Ljava/lang/String;)V
      //   346: astore 23
      //   348: aload 23
      //   350: aload_0
      //   351: getfield 66	com/android/settings/applications/RunningServiceDetails$ActiveDetail:mInstaller	Landroid/content/ComponentName;
      //   354: invokevirtual 239	android/content/Intent:setComponent	(Landroid/content/ComponentName;)Landroid/content/Intent;
      //   357: pop
      //   358: aload 23
      //   360: ldc 241
      //   362: aload_2
      //   363: invokevirtual 245	android/content/Intent:putExtra	(Ljava/lang/String;Landroid/os/Parcelable;)Landroid/content/Intent;
      //   366: pop
      //   367: aload 23
      //   369: ldc 246
      //   371: invokevirtual 250	android/content/Intent:addFlags	(I)Landroid/content/Intent;
      //   374: pop
      //   375: aload_0
      //   376: getfield 27	com/android/settings/applications/RunningServiceDetails$ActiveDetail:this$0	Lcom/android/settings/applications/RunningServiceDetails;
      //   379: aload 23
      //   381: invokevirtual 254	android/app/Fragment:startActivity	(Landroid/content/Intent;)V
      //   384: return
      //   385: iconst_0
      //   386: istore_3
      //   387: goto -300 -> 87
      //   390: aload 4
      //   392: ldc2_w 255
      //   395: putfield 130	android/app/ApplicationErrorReport$RunningServiceInfo:durationMillis	J
      //   398: goto -269 -> 129
      //   401: astore 33
      //   403: goto -163 -> 240
      //   406: astore 9
      //   408: ldc 206
      //   410: new 208	java/lang/StringBuilder
      //   413: dup
      //   414: invokespecial 209	java/lang/StringBuilder:<init>	()V
      //   417: ldc_w 258
      //   420: invokevirtual 215	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   423: aload 5
      //   425: invokevirtual 261	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   428: invokevirtual 218	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   431: aload 9
      //   433: invokestatic 265	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   436: pop
      //   437: aload 7
      //   439: ifnull -199 -> 240
      //   442: aload 7
      //   444: invokevirtual 179	java/io/FileOutputStream:close	()V
      //   447: goto -207 -> 240
      //   450: astore 13
      //   452: goto -212 -> 240
      //   455: astore 10
      //   457: aload 7
      //   459: ifnull +8 -> 467
      //   462: aload 7
      //   464: invokevirtual 179	java/io/FileOutputStream:close	()V
      //   467: aload 10
      //   469: athrow
      //   470: astore 29
      //   472: goto -176 -> 296
      //   475: astore 16
      //   477: ldc 206
      //   479: new 208	java/lang/StringBuilder
      //   482: dup
      //   483: invokespecial 209	java/lang/StringBuilder:<init>	()V
      //   486: ldc_w 267
      //   489: invokevirtual 215	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   492: aload 5
      //   494: invokevirtual 261	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   497: invokevirtual 218	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   500: aload 16
      //   502: invokestatic 265	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   505: pop
      //   506: aload 14
      //   508: ifnull -212 -> 296
      //   511: aload 14
      //   513: invokevirtual 200	java/io/FileInputStream:close	()V
      //   516: goto -220 -> 296
      //   519: astore 20
      //   521: goto -225 -> 296
      //   524: astore 17
      //   526: aload 14
      //   528: ifnull +8 -> 536
      //   531: aload 14
      //   533: invokevirtual 200	java/io/FileInputStream:close	()V
      //   536: aload 17
      //   538: athrow
      //   539: aload_0
      //   540: getfield 269	com/android/settings/applications/RunningServiceDetails$ActiveDetail:mManageIntent	Landroid/app/PendingIntent;
      //   543: ifnull +62 -> 605
      //   546: aload_0
      //   547: getfield 27	com/android/settings/applications/RunningServiceDetails$ActiveDetail:this$0	Lcom/android/settings/applications/RunningServiceDetails;
      //   550: invokevirtual 142	android/app/Fragment:getActivity	()Landroid/app/Activity;
      //   553: aload_0
      //   554: getfield 269	com/android/settings/applications/RunningServiceDetails$ActiveDetail:mManageIntent	Landroid/app/PendingIntent;
      //   557: invokevirtual 275	android/app/PendingIntent:getIntentSender	()Landroid/content/IntentSender;
      //   560: aconst_null
      //   561: ldc_w 276
      //   564: ldc_w 277
      //   567: iconst_0
      //   568: invokevirtual 283	android/app/Activity:startIntentSender	(Landroid/content/IntentSender;Landroid/content/Intent;III)V
      //   571: return
      //   572: astore 38
      //   574: ldc 206
      //   576: aload 38
      //   578: invokestatic 286	android/util/Log:w	(Ljava/lang/String;Ljava/lang/Throwable;)I
      //   581: pop
      //   582: return
      //   583: astore 36
      //   585: ldc 206
      //   587: aload 36
      //   589: invokestatic 286	android/util/Log:w	(Ljava/lang/String;Ljava/lang/Throwable;)I
      //   592: pop
      //   593: return
      //   594: astore 34
      //   596: ldc 206
      //   598: aload 34
      //   600: invokestatic 286	android/util/Log:w	(Ljava/lang/String;Ljava/lang/Throwable;)I
      //   603: pop
      //   604: return
      //   605: aload_0
      //   606: getfield 51	com/android/settings/applications/RunningServiceDetails$ActiveDetail:mServiceItem	Lcom/android/settings/applications/RunningState$ServiceItem;
      //   609: ifnull +9 -> 618
      //   612: aload_0
      //   613: iconst_0
      //   614: invokevirtual 290	com/android/settings/applications/RunningServiceDetails$ActiveDetail:stopActiveService	(Z)V
      //   617: return
      //   618: aload_0
      //   619: getfield 117	com/android/settings/applications/RunningServiceDetails$ActiveDetail:mActiveItem	Lcom/android/settings/applications/RunningProcessesView$ActiveItem;
      //   622: getfield 294	com/android/settings/applications/RunningProcessesView$ActiveItem:mItem	Lcom/android/settings/applications/RunningState$BaseItem;
      //   625: getfield 299	com/android/settings/applications/RunningState$BaseItem:mBackground	Z
      //   628: ifeq +34 -> 662
      //   631: aload_0
      //   632: getfield 27	com/android/settings/applications/RunningServiceDetails$ActiveDetail:this$0	Lcom/android/settings/applications/RunningServiceDetails;
      //   635: getfield 305	com/android/settings/applications/RunningServiceDetails:mAm	Landroid/app/ActivityManager;
      //   638: aload_0
      //   639: getfield 117	com/android/settings/applications/RunningServiceDetails$ActiveDetail:mActiveItem	Lcom/android/settings/applications/RunningProcessesView$ActiveItem;
      //   642: getfield 294	com/android/settings/applications/RunningProcessesView$ActiveItem:mItem	Lcom/android/settings/applications/RunningState$BaseItem;
      //   645: getfield 309	com/android/settings/applications/RunningState$BaseItem:mPackageInfo	Landroid/content/pm/PackageItemInfo;
      //   648: getfield 63	android/content/pm/PackageItemInfo:packageName	Ljava/lang/String;
      //   651: invokevirtual 314	android/app/ActivityManager:killBackgroundProcesses	(Ljava/lang/String;)V
      //   654: aload_0
      //   655: getfield 27	com/android/settings/applications/RunningServiceDetails$ActiveDetail:this$0	Lcom/android/settings/applications/RunningServiceDetails;
      //   658: invokestatic 317	com/android/settings/applications/RunningServiceDetails:access$100	(Lcom/android/settings/applications/RunningServiceDetails;)V
      //   661: return
      //   662: aload_0
      //   663: getfield 27	com/android/settings/applications/RunningServiceDetails$ActiveDetail:this$0	Lcom/android/settings/applications/RunningServiceDetails;
      //   666: getfield 305	com/android/settings/applications/RunningServiceDetails:mAm	Landroid/app/ActivityManager;
      //   669: aload_0
      //   670: getfield 117	com/android/settings/applications/RunningServiceDetails$ActiveDetail:mActiveItem	Lcom/android/settings/applications/RunningProcessesView$ActiveItem;
      //   673: getfield 294	com/android/settings/applications/RunningProcessesView$ActiveItem:mItem	Lcom/android/settings/applications/RunningState$BaseItem;
      //   676: getfield 309	com/android/settings/applications/RunningState$BaseItem:mPackageInfo	Landroid/content/pm/PackageItemInfo;
      //   679: getfield 63	android/content/pm/PackageItemInfo:packageName	Ljava/lang/String;
      //   682: invokevirtual 320	android/app/ActivityManager:forceStopPackage	(Ljava/lang/String;)V
      //   685: aload_0
      //   686: getfield 27	com/android/settings/applications/RunningServiceDetails$ActiveDetail:this$0	Lcom/android/settings/applications/RunningServiceDetails;
      //   689: invokestatic 317	com/android/settings/applications/RunningServiceDetails:access$100	(Lcom/android/settings/applications/RunningServiceDetails;)V
      //   692: return
      //   693: astore 11
      //   695: goto -228 -> 467
      //   698: astore 18
      //   700: goto -164 -> 536
      //   703: astore 17
      //   705: aload 15
      //   707: astore 14
      //   709: goto -183 -> 526
      //   712: astore 16
      //   714: aload 15
      //   716: astore 14
      //   718: goto -241 -> 477
      //   721: astore 10
      //   723: aload 8
      //   725: astore 7
      //   727: goto -270 -> 457
      //   730: astore 9
      //   732: aload 8
      //   734: astore 7
      //   736: goto -328 -> 408
      //   739: goto -443 -> 296
      //   742: goto -502 -> 240
      //
      // Exception table:
      //   from	to	target	type
      //   235	240	401	java/io/IOException
      //   175	186	406	java/io/IOException
      //   442	447	450	java/io/IOException
      //   175	186	455	finally
      //   408	437	455	finally
      //   291	296	470	java/io/IOException
      //   243	254	475	java/io/IOException
      //   511	516	519	java/io/IOException
      //   243	254	524	finally
      //   477	506	524	finally
      //   546	571	572	android/content/IntentSender$SendIntentException
      //   546	571	583	java/lang/IllegalArgumentException
      //   546	571	594	android/content/ActivityNotFoundException
      //   462	467	693	java/io/IOException
      //   531	536	698	java/io/IOException
      //   254	286	703	finally
      //   254	286	712	java/io/IOException
      //   186	230	721	finally
      //   186	230	730	java/io/IOException
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