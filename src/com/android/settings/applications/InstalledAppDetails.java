package com.android.settings.applications;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.INotificationManager;
import android.app.INotificationManager.Stub;
import android.app.admin.DevicePolicyManager;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver.Stub;
import android.content.pm.IPackageMoveObserver.Stub;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.hardware.usb.IUsbManager;
import android.hardware.usb.IUsbManager.Stub;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.preference.PreferenceActivity;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.text.style.BulletSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AppSecurityPermissions;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import com.android.internal.telephony.ISms;
import com.android.internal.telephony.ISms.Stub;
import com.android.settings.Utils;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class InstalledAppDetails extends Fragment
  implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, ApplicationsState.Callbacks
{
  private Button mActivitiesButton;
  private ApplicationsState.AppEntry mAppEntry;
  private TextView mAppSize;
  private TextView mAppVersion;
  private AppWidgetManager mAppWidgetManager;
  private CheckBox mAskCompatibilityCB;
  private TextView mCacheSize;
  private CanBeOnSdCardChecker mCanBeOnSdCardChecker;
  private boolean mCanClearData = true;
  private final BroadcastReceiver mCheckKillProcessesReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      InstalledAppDetails localInstalledAppDetails = InstalledAppDetails.this;
      if (getResultCode() != 0);
      for (boolean bool = true; ; bool = false)
      {
        localInstalledAppDetails.updateForceStopButton(bool);
        return;
      }
    }
  };
  private Button mClearCacheButton;
  private ClearCacheObserver mClearCacheObserver;
  private Button mClearDataButton;
  private ClearUserDataObserver mClearDataObserver;
  private CharSequence mComputingStr;
  private TextView mDataSize;
  private boolean mDisableAfterUninstall;
  private DevicePolicyManager mDpm;
  private CheckBox mEnableCompatibilityCB;
  private TextView mExternalCodeSize;
  private TextView mExternalDataSize;
  private Button mForceStopButton;
  private Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      if (InstalledAppDetails.this.getView() == null)
        return;
      switch (paramAnonymousMessage.what)
      {
      case 2:
      default:
        return;
      case 1:
        InstalledAppDetails.this.processClearMsg(paramAnonymousMessage);
        return;
      case 3:
        InstalledAppDetails.this.mState.requestSize(InstalledAppDetails.this.mAppEntry.info.packageName);
        return;
      case 4:
      }
      InstalledAppDetails.this.processMoveMsg(paramAnonymousMessage);
    }
  };
  private boolean mHaveSizes = false;
  private final HashSet<String> mHomePackages = new HashSet();
  private boolean mInitialized;
  private CharSequence mInvalidSizeStr;
  private long mLastCacheSize = -1L;
  private long mLastCodeSize = -1L;
  private long mLastDataSize = -1L;
  private long mLastExternalCodeSize = -1L;
  private long mLastExternalDataSize = -1L;
  private long mLastTotalSize = -1L;
  private View mMoreControlButtons;
  private Button mMoveAppButton;
  private boolean mMoveInProgress = false;
  private CompoundButton mNotificationSwitch;
  private PackageInfo mPackageInfo;
  private PackageMoveObserver mPackageMoveObserver;
  private PackageManager mPm;
  private View mRootView;
  private View mScreenCompatSection;
  private ApplicationsState.Session mSession;
  private boolean mShowUninstalled;
  private ISms mSmsManager;
  private Button mSpecialDisableButton;
  private ApplicationsState mState;
  private TextView mTotalSize;
  private Button mUninstallButton;
  private boolean mUpdatedSysApp = false;
  private IUsbManager mUsbManager;
  private UserManager mUserManager;

  private void checkForceStop()
  {
    if (this.mDpm.packageHasActiveAdmins(this.mPackageInfo.packageName))
    {
      updateForceStopButton(false);
      return;
    }
    if ((0x200000 & this.mAppEntry.info.flags) == 0)
    {
      updateForceStopButton(true);
      return;
    }
    Intent localIntent = new Intent("android.intent.action.QUERY_PACKAGE_RESTART", Uri.fromParts("package", this.mAppEntry.info.packageName, null));
    String[] arrayOfString = new String[1];
    arrayOfString[0] = this.mAppEntry.info.packageName;
    localIntent.putExtra("android.intent.extra.PACKAGES", arrayOfString);
    localIntent.putExtra("android.intent.extra.UID", this.mAppEntry.info.uid);
    localIntent.putExtra("android.intent.extra.user_handle", UserHandle.getUserId(this.mAppEntry.info.uid));
    getActivity().sendOrderedBroadcastAsUser(localIntent, UserHandle.CURRENT, null, this.mCheckKillProcessesReceiver, null, 0, null, null);
  }

  private void forceStopPackage(String paramString)
  {
    ((ActivityManager)getActivity().getSystemService("activity")).forceStopPackage(paramString);
    this.mState.invalidatePackage(paramString);
    ApplicationsState.AppEntry localAppEntry = this.mState.getEntry(paramString);
    if (localAppEntry != null)
      this.mAppEntry = localAppEntry;
    checkForceStop();
  }

  private CharSequence getMoveErrMsg(int paramInt)
  {
    switch (paramInt)
    {
    default:
      return "";
    case -1:
      return getActivity().getString(2131428445);
    case -2:
      return getActivity().getString(2131428446);
    case -4:
      return getActivity().getString(2131428447);
    case -5:
      return getActivity().getString(2131428448);
    case -3:
      return getActivity().getString(2131428449);
    case -6:
    }
    return "";
  }

  private int getPremiumSmsPermission(String paramString)
  {
    try
    {
      if (this.mSmsManager != null)
      {
        int i = this.mSmsManager.getPremiumSmsPermission(paramString);
        return i;
      }
    }
    catch (RemoteException localRemoteException)
    {
    }
    return 0;
  }

  private String getSizeStr(long paramLong)
  {
    if (paramLong == -1L)
      return this.mInvalidSizeStr.toString();
    return Formatter.formatFileSize(getActivity(), paramLong);
  }

  private boolean handleDisableable(Button paramButton)
  {
    if ((this.mHomePackages.contains(this.mAppEntry.info.packageName)) || (isThisASystemPackage()))
    {
      paramButton.setText(2131428384);
      return false;
    }
    if (this.mAppEntry.info.enabled)
    {
      paramButton.setText(2131428384);
      return true;
    }
    paramButton.setText(2131428385);
    return true;
  }

  private void initDataButtons()
  {
    if ((this.mAppEntry.info.manageSpaceActivityName == null) && (((0x41 & this.mAppEntry.info.flags) == 1) || (this.mDpm.packageHasActiveAdmins(this.mPackageInfo.packageName))))
    {
      this.mClearDataButton.setText(2131428386);
      this.mClearDataButton.setEnabled(false);
      this.mCanClearData = false;
      return;
    }
    if (this.mAppEntry.info.manageSpaceActivityName != null)
      this.mClearDataButton.setText(2131428404);
    while (true)
    {
      this.mClearDataButton.setOnClickListener(this);
      return;
      this.mClearDataButton.setText(2131428386);
    }
  }

  private void initMoveButton()
  {
    if (Environment.isExternalStorageEmulated())
    {
      this.mMoveAppButton.setVisibility(4);
      return;
    }
    int i;
    int j;
    if ((this.mPackageInfo == null) && (this.mAppEntry != null))
    {
      i = 1;
      j = 1;
      if (i == 0)
        break label65;
      this.mMoveAppButton.setText(2131428441);
    }
    while (true)
      if (j != 0)
      {
        this.mMoveAppButton.setEnabled(false);
        return;
        i = 0;
        break;
        label65: if ((0x40000 & this.mAppEntry.info.flags) != 0)
        {
          this.mMoveAppButton.setText(2131428442);
          j = 0;
        }
        else
        {
          this.mMoveAppButton.setText(2131428443);
          this.mCanBeOnSdCardChecker.init();
          if (!this.mCanBeOnSdCardChecker.check(this.mAppEntry.info));
          for (j = 1; ; j = 0)
            break;
        }
      }
    this.mMoveAppButton.setOnClickListener(this);
    this.mMoveAppButton.setEnabled(true);
  }

  private void initNotificationButton()
  {
    INotificationManager localINotificationManager = INotificationManager.Stub.asInterface(ServiceManager.getService("notification"));
    boolean bool1 = true;
    try
    {
      boolean bool2 = localINotificationManager.areNotificationsEnabledForPackage(this.mAppEntry.info.packageName, this.mAppEntry.info.uid);
      bool1 = bool2;
      label43: this.mNotificationSwitch.setChecked(bool1);
      if (isThisASystemPackage())
      {
        this.mNotificationSwitch.setEnabled(false);
        return;
      }
      this.mNotificationSwitch.setEnabled(true);
      this.mNotificationSwitch.setOnCheckedChangeListener(this);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      break label43;
    }
  }

  private void initUninstallButtons()
  {
    boolean bool1;
    boolean bool2;
    int j;
    if ((0x80 & this.mAppEntry.info.flags) != 0)
    {
      bool1 = true;
      this.mUpdatedSysApp = bool1;
      bool2 = true;
      if (!this.mUpdatedSysApp)
        break label154;
      this.mUninstallButton.setText(2131428387);
      int i = 0x1 & this.mAppEntry.info.flags;
      boolean bool3 = false;
      if (i != 0)
      {
        bool3 = handleDisableable(this.mSpecialDisableButton);
        this.mSpecialDisableButton.setOnClickListener(this);
      }
      View localView = this.mMoreControlButtons;
      j = 0;
      if (!bool3)
        break label147;
      label95: localView.setVisibility(j);
    }
    while (true)
    {
      if (this.mDpm.packageHasActiveAdmins(this.mPackageInfo.packageName))
        bool2 = false;
      this.mUninstallButton.setEnabled(bool2);
      if (bool2)
        this.mUninstallButton.setOnClickListener(this);
      return;
      bool1 = false;
      break;
      label147: j = 8;
      break label95;
      label154: this.mMoreControlButtons.setVisibility(8);
      if ((0x1 & this.mAppEntry.info.flags) != 0)
      {
        bool2 = handleDisableable(this.mUninstallButton);
      }
      else if (((0x800000 & this.mPackageInfo.applicationInfo.flags) == 0) && (this.mUserManager.getUsers().size() >= 2))
      {
        this.mUninstallButton.setText(2131428381);
        bool2 = false;
      }
      else
      {
        this.mUninstallButton.setText(2131428381);
      }
    }
  }

  private void initiateClearUserData()
  {
    this.mClearDataButton.setEnabled(false);
    String str = this.mAppEntry.info.packageName;
    Log.i("InstalledAppDetails", "Clearing user data for package : " + str);
    if (this.mClearDataObserver == null)
      this.mClearDataObserver = new ClearUserDataObserver();
    if (!((ActivityManager)getActivity().getSystemService("activity")).clearApplicationUserData(str, this.mClearDataObserver))
    {
      Log.i("InstalledAppDetails", "Couldnt clear application user data for package:" + str);
      showDialogInner(4, 0);
      return;
    }
    this.mClearDataButton.setText(2131428417);
  }

  private boolean isThisASystemPackage()
  {
    try
    {
      PackageInfo localPackageInfo1 = this.mPm.getPackageInfo("android", 64);
      PackageInfo localPackageInfo2 = this.mPackageInfo;
      boolean bool1 = false;
      if (localPackageInfo2 != null)
      {
        Signature[] arrayOfSignature = this.mPackageInfo.signatures;
        bool1 = false;
        if (arrayOfSignature != null)
        {
          boolean bool2 = localPackageInfo1.signatures[0].equals(this.mPackageInfo.signatures[0]);
          bool1 = false;
          if (bool2)
            bool1 = true;
        }
      }
      return bool1;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
    }
    return false;
  }

  private void processClearMsg(Message paramMessage)
  {
    int i = paramMessage.arg1;
    String str = this.mAppEntry.info.packageName;
    this.mClearDataButton.setText(2131428386);
    if (i == 1)
    {
      Log.i("InstalledAppDetails", "Cleared user data for package : " + str);
      this.mState.requestSize(this.mAppEntry.info.packageName);
    }
    while (true)
    {
      checkForceStop();
      return;
      this.mClearDataButton.setEnabled(true);
    }
  }

  private void processMoveMsg(Message paramMessage)
  {
    int i = paramMessage.arg1;
    String str = this.mAppEntry.info.packageName;
    this.mMoveInProgress = false;
    if (i == 1)
    {
      Log.i("InstalledAppDetails", "Moved resources for " + str);
      this.mState.requestSize(this.mAppEntry.info.packageName);
    }
    while (true)
    {
      refreshUi();
      return;
      showDialogInner(6, i);
    }
  }

  private void refreshButtons()
  {
    if (!this.mMoveInProgress)
    {
      initUninstallButtons();
      initDataButtons();
      initMoveButton();
      initNotificationButton();
      return;
    }
    this.mMoveAppButton.setText(2131428444);
    this.mMoveAppButton.setEnabled(false);
    this.mUninstallButton.setEnabled(false);
    this.mSpecialDisableButton.setEnabled(false);
  }

  private void refreshSizeInfo()
  {
    if ((this.mAppEntry.size == -2L) || (this.mAppEntry.size == -1L))
    {
      this.mLastTotalSize = -1L;
      this.mLastCacheSize = -1L;
      this.mLastDataSize = -1L;
      this.mLastCodeSize = -1L;
      if (!this.mHaveSizes)
      {
        this.mAppSize.setText(this.mComputingStr);
        this.mDataSize.setText(this.mComputingStr);
        this.mCacheSize.setText(this.mComputingStr);
        this.mTotalSize.setText(this.mComputingStr);
      }
      this.mClearDataButton.setEnabled(false);
      this.mClearCacheButton.setEnabled(false);
      return;
    }
    this.mHaveSizes = true;
    long l1 = this.mAppEntry.codeSize;
    long l2 = this.mAppEntry.dataSize;
    long l3;
    if (Environment.isExternalStorageEmulated())
    {
      l1 += this.mAppEntry.externalCodeSize;
      l2 += this.mAppEntry.externalDataSize;
      if (this.mLastCodeSize != l1)
      {
        this.mLastCodeSize = l1;
        this.mAppSize.setText(getSizeStr(l1));
      }
      if (this.mLastDataSize != l2)
      {
        this.mLastDataSize = l2;
        this.mDataSize.setText(getSizeStr(l2));
      }
      l3 = this.mAppEntry.cacheSize + this.mAppEntry.externalCacheSize;
      if (this.mLastCacheSize != l3)
      {
        this.mLastCacheSize = l3;
        this.mCacheSize.setText(getSizeStr(l3));
      }
      if (this.mLastTotalSize != this.mAppEntry.size)
      {
        this.mLastTotalSize = this.mAppEntry.size;
        this.mTotalSize.setText(getSizeStr(this.mAppEntry.size));
      }
      if ((this.mAppEntry.dataSize + this.mAppEntry.externalDataSize > 0L) && (this.mCanClearData))
        break label455;
      this.mClearDataButton.setEnabled(false);
    }
    while (true)
    {
      if (l3 > 0L)
        break label474;
      this.mClearCacheButton.setEnabled(false);
      return;
      if (this.mLastExternalCodeSize != this.mAppEntry.externalCodeSize)
      {
        this.mLastExternalCodeSize = this.mAppEntry.externalCodeSize;
        this.mExternalCodeSize.setText(getSizeStr(this.mAppEntry.externalCodeSize));
      }
      if (this.mLastExternalDataSize == this.mAppEntry.externalDataSize)
        break;
      this.mLastExternalDataSize = this.mAppEntry.externalDataSize;
      this.mExternalDataSize.setText(getSizeStr(this.mAppEntry.externalDataSize));
      break;
      label455: this.mClearDataButton.setEnabled(true);
      this.mClearDataButton.setOnClickListener(this);
    }
    label474: this.mClearCacheButton.setEnabled(true);
    this.mClearCacheButton.setOnClickListener(this);
  }

  private boolean refreshUi()
  {
    if (this.mMoveInProgress);
    while (true)
    {
      return true;
      String str1 = retrieveAppEntry();
      if (this.mAppEntry == null)
        return false;
      if (this.mPackageInfo == null)
        return false;
      ArrayList localArrayList1 = new ArrayList();
      this.mPm.getHomeActivities(localArrayList1);
      this.mHomePackages.clear();
      for (int i = 0; i < localArrayList1.size(); i++)
      {
        ResolveInfo localResolveInfo = (ResolveInfo)localArrayList1.get(i);
        String str6 = localResolveInfo.activityInfo.packageName;
        this.mHomePackages.add(str6);
        Bundle localBundle = localResolveInfo.activityInfo.metaData;
        if (localBundle != null)
        {
          String str7 = localBundle.getString("android.app.home.alternate");
          if (signaturesMatch(str7, str6))
            this.mHomePackages.add(str7);
        }
      }
      ArrayList localArrayList2 = new ArrayList();
      ArrayList localArrayList3 = new ArrayList();
      this.mPm.getPreferredActivities(localArrayList3, localArrayList2, str1);
      try
      {
        boolean bool4 = this.mUsbManager.hasDefaults(str1, UserHandle.myUserId());
        bool1 = bool4;
        bool2 = this.mAppWidgetManager.hasBindAppWidgetPermission(this.mAppEntry.info.packageName);
        localTextView1 = (TextView)this.mRootView.findViewById(2131230873);
        localTextView2 = (TextView)this.mRootView.findViewById(2131230874);
        if ((localArrayList2.size() > 0) || (bool1))
        {
          j = 1;
          if ((j != 0) || (bool2))
            break label625;
          resetLaunchDefaultsUi(localTextView1, localTextView2);
          ((ActivityManager)getActivity().getSystemService("activity")).getPackageScreenCompatMode(str1);
          this.mScreenCompatSection.setVisibility(8);
          localLinearLayout1 = (LinearLayout)this.mRootView.findViewById(2131230879);
          AppSecurityPermissions localAppSecurityPermissions = new AppSecurityPermissions(getActivity(), str1);
          int n = getPremiumSmsPermission(str1);
          if ((localAppSecurityPermissions.getPermissionCount() <= 0) && (n == 0))
            break label931;
          localLinearLayout1.setVisibility(0);
          localTextView3 = (TextView)localLinearLayout1.findViewById(2131230880);
          localLinearLayout2 = (LinearLayout)localLinearLayout1.findViewById(2131230881);
          if (n == 0)
            break label941;
          localTextView3.setVisibility(0);
          localLinearLayout2.setVisibility(0);
          Spinner localSpinner = (Spinner)localLinearLayout1.findViewById(2131230882);
          ArrayAdapter localArrayAdapter = ArrayAdapter.createFromResource(getActivity(), 2131165281, 17367048);
          localArrayAdapter.setDropDownViewResource(17367049);
          localSpinner.setAdapter(localArrayAdapter);
          localSpinner.setSelection(n - 1);
          localSpinner.setOnItemSelectedListener(new PremiumSmsSelectionListener(str1, this.mSmsManager));
          if (localAppSecurityPermissions.getPermissionCount() <= 0)
            break label1090;
          LinearLayout localLinearLayout3 = (LinearLayout)localLinearLayout1.findViewById(2131230884);
          localLinearLayout3.removeAllViews();
          localLinearLayout3.addView(localAppSecurityPermissions.getPermissionsViewWithRevokeButtons());
          String[] arrayOfString = this.mPm.getPackagesForUid(this.mPackageInfo.applicationInfo.uid);
          if ((arrayOfString == null) || (arrayOfString.length <= 1))
            break label1090;
          localArrayList4 = new ArrayList();
          for (int i3 = 0; ; i3++)
          {
            if (i3 >= arrayOfString.length)
              break label989;
            str5 = arrayOfString[i3];
            if (!this.mPackageInfo.packageName.equals(str5))
              break;
          }
        }
      }
      catch (RemoteException localRemoteException)
      {
        ArrayList localArrayList4;
        while (true)
        {
          boolean bool2;
          TextView localTextView1;
          TextView localTextView2;
          LinearLayout localLinearLayout1;
          TextView localTextView3;
          LinearLayout localLinearLayout2;
          String str5;
          Log.e("InstalledAppDetails", "mUsbManager.hasDefaults", localRemoteException);
          boolean bool1 = false;
          continue;
          int j = 0;
          continue;
          label625: int k;
          label638: SpannableString localSpannableString2;
          label651: label743: SpannableString localSpannableString1;
          if ((bool2) && (j != 0))
          {
            k = 1;
            if (!bool2)
              break label853;
            localTextView1.setText(2131428368);
            int m = getResources().getDimensionPixelSize(2131558411);
            localCharSequence1 = null;
            if (j != 0)
            {
              CharSequence localCharSequence3 = getText(2131428388);
              localSpannableString2 = new SpannableString(localCharSequence3);
              if (k != 0)
                localSpannableString2.setSpan(new BulletSpan(m), 0, localCharSequence3.length(), 0);
              if (0 != 0)
                break label864;
              localCharSequence1 = TextUtils.concat(new CharSequence[] { localSpannableString2, "\n" });
            }
            if (bool2)
            {
              CharSequence localCharSequence2 = getText(2131428389);
              localSpannableString1 = new SpannableString(localCharSequence2);
              if (k != 0)
                localSpannableString1.setSpan(new BulletSpan(m), 0, localCharSequence2.length(), 0);
              if (localCharSequence1 != null)
                break label897;
            }
          }
          label897: for (CharSequence localCharSequence1 = TextUtils.concat(new CharSequence[] { localSpannableString1, "\n" }); ; localCharSequence1 = TextUtils.concat(new CharSequence[] { localCharSequence1, "\n", localSpannableString1, "\n" }))
          {
            localTextView2.setText(localCharSequence1);
            this.mActivitiesButton.setEnabled(true);
            this.mActivitiesButton.setOnClickListener(this);
            break;
            k = 0;
            break label638;
            label853: localTextView1.setText(2131428367);
            break label651;
            label864: localCharSequence1 = TextUtils.concat(new CharSequence[] { null, "\n", localSpannableString2, "\n" });
            break label743;
          }
          label931: localLinearLayout1.setVisibility(8);
          continue;
          label941: localTextView3.setVisibility(8);
          localLinearLayout2.setVisibility(8);
          continue;
          try
          {
            localArrayList4.add(this.mPm.getApplicationInfo(str5, 0).loadLabel(this.mPm));
          }
          catch (PackageManager.NameNotFoundException localNameNotFoundException2)
          {
          }
        }
        label989: int i4 = localArrayList4.size();
        Resources localResources;
        String str4;
        if (i4 > 0)
        {
          localResources = getActivity().getResources();
          if (i4 != 1)
            break label1157;
          str4 = ((CharSequence)localArrayList4.get(0)).toString();
        }
        while (true)
        {
          TextView localTextView4 = (TextView)this.mRootView.findViewById(2131230883);
          Object[] arrayOfObject2 = new Object[2];
          arrayOfObject2[0] = this.mPackageInfo.applicationInfo.loadLabel(this.mPm);
          arrayOfObject2[1] = str4;
          localTextView4.setText(localResources.getString(2131428430, arrayOfObject2));
          label1090: checkForceStop();
          setAppLabelAndIcon(this.mPackageInfo);
          refreshButtons();
          refreshSizeInfo();
          if (this.mInitialized)
            break;
          this.mInitialized = true;
          int i2 = 0x800000 & this.mAppEntry.info.flags;
          boolean bool3 = false;
          if (i2 == 0)
            bool3 = true;
          this.mShowUninstalled = bool3;
          return true;
          label1157: if (i4 == 2)
          {
            Object[] arrayOfObject4 = new Object[2];
            arrayOfObject4[0] = localArrayList4.get(0);
            arrayOfObject4[1] = localArrayList4.get(1);
            str4 = localResources.getString(2131428431, arrayOfObject4);
          }
          else
          {
            String str2 = ((CharSequence)localArrayList4.get(i4 - 2)).toString();
            int i5 = i4 - 3;
            String str3 = str2;
            int i6 = i5;
            if (i6 >= 0)
            {
              if (i6 == 0);
              for (int i7 = 2131428433; ; i7 = 2131428434)
              {
                Object[] arrayOfObject3 = new Object[2];
                arrayOfObject3[0] = localArrayList4.get(i6);
                arrayOfObject3[1] = str3;
                str3 = localResources.getString(i7, arrayOfObject3);
                i6--;
                break;
              }
            }
            Object[] arrayOfObject1 = new Object[2];
            arrayOfObject1[0] = str3;
            arrayOfObject1[1] = localArrayList4.get(i4 - 1);
            str4 = localResources.getString(2131428432, arrayOfObject1);
          }
        }
        try
        {
          ApplicationInfo localApplicationInfo = getActivity().getPackageManager().getApplicationInfo(this.mAppEntry.info.packageName, 8704);
          if (!this.mShowUninstalled)
          {
            int i1 = localApplicationInfo.flags;
            if ((i1 & 0x800000) == 0)
              return false;
          }
        }
        catch (PackageManager.NameNotFoundException localNameNotFoundException1)
        {
        }
      }
    }
    return false;
  }

  private void resetLaunchDefaultsUi(TextView paramTextView1, TextView paramTextView2)
  {
    paramTextView1.setText(2131428367);
    paramTextView2.setText(2131428390);
    this.mActivitiesButton.setEnabled(false);
  }

  private String retrieveAppEntry()
  {
    Bundle localBundle = getArguments();
    String str;
    if (localBundle != null)
      str = localBundle.getString("package");
    while (true)
    {
      Intent localIntent;
      if (str == null)
      {
        if (localBundle != null)
          break label97;
        localIntent = getActivity().getIntent();
        if (localIntent != null)
          str = localIntent.getData().getSchemeSpecificPart();
      }
      this.mAppEntry = this.mState.getEntry(str);
      if (this.mAppEntry != null)
        try
        {
          this.mPackageInfo = this.mPm.getPackageInfo(this.mAppEntry.info.packageName, 8768);
          return str;
          str = null;
          continue;
          label97: localIntent = (Intent)localBundle.getParcelable("intent");
        }
        catch (PackageManager.NameNotFoundException localNameNotFoundException)
        {
          Log.e("InstalledAppDetails", "Exception when retrieving package:" + this.mAppEntry.info.packageName, localNameNotFoundException);
          return str;
        }
    }
    Log.w("InstalledAppDetails", "Missing AppEntry; maybe reinstalling?");
    this.mPackageInfo = null;
    return str;
  }

  private void setAppLabelAndIcon(PackageInfo paramPackageInfo)
  {
    View localView = this.mRootView.findViewById(2131230729);
    localView.setPaddingRelative(0, localView.getPaddingTop(), 0, localView.getPaddingBottom());
    ImageView localImageView = (ImageView)localView.findViewById(2131230735);
    this.mState.ensureIcon(this.mAppEntry);
    localImageView.setImageDrawable(this.mAppEntry.icon);
    ((TextView)localView.findViewById(2131230736)).setText(this.mAppEntry.label);
    this.mAppVersion = ((TextView)localView.findViewById(2131230902));
    if ((paramPackageInfo != null) && (paramPackageInfo.versionName != null))
    {
      this.mAppVersion.setVisibility(0);
      TextView localTextView = this.mAppVersion;
      Activity localActivity = getActivity();
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = String.valueOf(paramPackageInfo.versionName);
      localTextView.setText(localActivity.getString(2131428440, arrayOfObject));
      return;
    }
    this.mAppVersion.setVisibility(4);
  }

  private void setIntentAndFinish(boolean paramBoolean1, boolean paramBoolean2)
  {
    Intent localIntent = new Intent();
    localIntent.putExtra("chg", paramBoolean2);
    ((PreferenceActivity)getActivity()).finishPreferencePanel(this, -1, localIntent);
  }

  private void setNotificationsEnabled(boolean paramBoolean)
  {
    String str = this.mAppEntry.info.packageName;
    INotificationManager localINotificationManager = INotificationManager.Stub.asInterface(ServiceManager.getService("notification"));
    CompoundButton localCompoundButton;
    try
    {
      this.mNotificationSwitch.isChecked();
      localINotificationManager.setNotificationsEnabledForPackage(str, this.mAppEntry.info.uid, paramBoolean);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      localCompoundButton = this.mNotificationSwitch;
      if (paramBoolean);
    }
    for (boolean bool = true; ; bool = false)
    {
      localCompoundButton.setChecked(bool);
      return;
    }
  }

  private void showDialogInner(int paramInt1, int paramInt2)
  {
    MyAlertDialogFragment localMyAlertDialogFragment = MyAlertDialogFragment.newInstance(paramInt1, paramInt2);
    localMyAlertDialogFragment.setTargetFragment(this, 0);
    localMyAlertDialogFragment.show(getFragmentManager(), "dialog " + paramInt1);
  }

  private boolean signaturesMatch(String paramString1, String paramString2)
  {
    if ((paramString1 != null) && (paramString2 != null))
      try
      {
        int i = this.mPm.checkSignatures(paramString1, paramString2);
        if (i >= 0)
          return true;
      }
      catch (Exception localException)
      {
      }
    return false;
  }

  private void uninstallPkg(String paramString, boolean paramBoolean1, boolean paramBoolean2)
  {
    Intent localIntent = new Intent("android.intent.action.UNINSTALL_PACKAGE", Uri.parse("package:" + paramString));
    localIntent.putExtra("android.intent.extra.UNINSTALL_ALL_USERS", paramBoolean1);
    startActivityForResult(localIntent, 1);
    this.mDisableAfterUninstall = paramBoolean2;
  }

  private void updateForceStopButton(boolean paramBoolean)
  {
    this.mForceStopButton.setEnabled(paramBoolean);
    this.mForceStopButton.setOnClickListener(this);
  }

  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
    if (paramInt1 == 1)
      if (this.mDisableAfterUninstall)
        this.mDisableAfterUninstall = false;
    try
    {
      if ((0x80 & getActivity().getPackageManager().getApplicationInfo(this.mAppEntry.info.packageName, 8704).flags) == 0)
      {
        DisableChanger localDisableChanger = new DisableChanger(this, this.mAppEntry.info, 3);
        Object[] arrayOfObject = new Object[1];
        arrayOfObject[0] = ((Object)null);
        localDisableChanger.execute(arrayOfObject);
      }
      label97: if (!refreshUi())
        setIntentAndFinish(true, true);
      return;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      break label97;
    }
  }

  public void onAllSizesComputed()
  {
  }

  public void onCheckedChanged(CompoundButton paramCompoundButton, boolean paramBoolean)
  {
    int i = 1;
    String str = this.mAppEntry.info.packageName;
    ActivityManager localActivityManager = (ActivityManager)getActivity().getSystemService("activity");
    if (paramCompoundButton == this.mAskCompatibilityCB)
      localActivityManager.setPackageAskScreenCompat(str, paramBoolean);
    do
    {
      return;
      if (paramCompoundButton == this.mEnableCompatibilityCB)
      {
        if (paramBoolean);
        while (true)
        {
          localActivityManager.setPackageScreenCompatMode(str, i);
          return;
          i = 0;
        }
      }
    }
    while (paramCompoundButton != this.mNotificationSwitch);
    if (!paramBoolean)
    {
      showDialogInner(8, 0);
      return;
    }
    setNotificationsEnabled(i);
  }

  public void onClick(View paramView)
  {
    int i = 2;
    String str = this.mAppEntry.info.packageName;
    if (paramView == this.mUninstallButton)
      if (this.mUpdatedSysApp)
        showDialogInner(i, 0);
    label327: 
    do
    {
      do
      {
        return;
        if ((0x1 & this.mAppEntry.info.flags) != 0)
        {
          if (this.mAppEntry.info.enabled)
          {
            showDialogInner(7, 0);
            return;
          }
          DisableChanger localDisableChanger = new DisableChanger(this, this.mAppEntry.info, 0);
          Object[] arrayOfObject = new Object[1];
          arrayOfObject[0] = ((Object)null);
          localDisableChanger.execute(arrayOfObject);
          return;
        }
        if ((0x800000 & this.mAppEntry.info.flags) == 0)
        {
          uninstallPkg(str, true, false);
          return;
        }
        uninstallPkg(str, false, false);
        return;
        if (paramView == this.mSpecialDisableButton)
        {
          showDialogInner(9, 0);
          return;
        }
        if (paramView == this.mActivitiesButton)
        {
          this.mPm.clearPackagePreferredActivities(str);
          try
          {
            this.mUsbManager.clearDefaults(str, UserHandle.myUserId());
            this.mAppWidgetManager.setBindAppWidgetPermission(str, false);
            resetLaunchDefaultsUi((TextView)this.mRootView.findViewById(2131230873), (TextView)this.mRootView.findViewById(2131230874));
            return;
          }
          catch (RemoteException localRemoteException)
          {
            while (true)
              Log.e("InstalledAppDetails", "mUsbManager.clearDefaults", localRemoteException);
          }
        }
        if (paramView != this.mClearDataButton)
          break label327;
        if (this.mAppEntry.info.manageSpaceActivityName == null)
          break;
      }
      while (Utils.isMonkeyRunning());
      Intent localIntent = new Intent("android.intent.action.VIEW");
      localIntent.setClassName(this.mAppEntry.info.packageName, this.mAppEntry.info.manageSpaceActivityName);
      startActivityForResult(localIntent, i);
      return;
      showDialogInner(1, 0);
      return;
      if (paramView == this.mClearCacheButton)
      {
        if (this.mClearCacheObserver == null)
          this.mClearCacheObserver = new ClearCacheObserver();
        this.mPm.deleteApplicationCacheFiles(str, this.mClearCacheObserver);
        return;
      }
      if (paramView == this.mForceStopButton)
      {
        showDialogInner(5, 0);
        return;
      }
    }
    while (paramView != this.mMoveAppButton);
    if (this.mPackageMoveObserver == null)
      this.mPackageMoveObserver = new PackageMoveObserver();
    if ((0x40000 & this.mAppEntry.info.flags) != 0)
      i = 1;
    this.mMoveInProgress = true;
    refreshButtons();
    this.mPm.movePackage(this.mAppEntry.info.packageName, this.mPackageMoveObserver, i);
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mState = ApplicationsState.getInstance(getActivity().getApplication());
    this.mSession = this.mState.newSession(this);
    this.mPm = getActivity().getPackageManager();
    this.mUserManager = ((UserManager)getActivity().getSystemService("user"));
    this.mUsbManager = IUsbManager.Stub.asInterface(ServiceManager.getService("usb"));
    this.mAppWidgetManager = AppWidgetManager.getInstance(getActivity());
    this.mDpm = ((DevicePolicyManager)getActivity().getSystemService("device_policy"));
    this.mSmsManager = ISms.Stub.asInterface(ServiceManager.getService("isms"));
    this.mCanBeOnSdCardChecker = new CanBeOnSdCardChecker();
    this.mSession.resume();
    retrieveAppEntry();
    setHasOptionsMenu(true);
  }

  public void onCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater)
  {
    paramMenu.add(0, 1, 1, 2131428382).setShowAsAction(0);
  }

  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    View localView1 = paramLayoutInflater.inflate(2130968635, paramViewGroup, false);
    Utils.prepareCustomPreferencesList(paramViewGroup, localView1, localView1, false);
    this.mRootView = localView1;
    this.mComputingStr = getActivity().getText(2131428437);
    this.mTotalSize = ((TextView)localView1.findViewById(2131230857));
    this.mAppSize = ((TextView)localView1.findViewById(2131230859));
    this.mDataSize = ((TextView)localView1.findViewById(2131230864));
    this.mExternalCodeSize = ((TextView)localView1.findViewById(2131230861));
    this.mExternalDataSize = ((TextView)localView1.findViewById(2131230866));
    if (Environment.isExternalStorageEmulated())
    {
      ((View)this.mExternalCodeSize.getParent()).setVisibility(8);
      ((View)this.mExternalDataSize.getParent()).setVisibility(8);
    }
    View localView2 = localView1.findViewById(2131230852);
    this.mForceStopButton = ((Button)localView2.findViewById(2131231089));
    this.mForceStopButton.setText(2131428375);
    this.mUninstallButton = ((Button)localView2.findViewById(2131231090));
    this.mForceStopButton.setEnabled(false);
    this.mMoreControlButtons = localView1.findViewById(2131230853);
    this.mMoreControlButtons.findViewById(2131231089).setVisibility(4);
    this.mSpecialDisableButton = ((Button)this.mMoreControlButtons.findViewById(2131231090));
    this.mMoreControlButtons.setVisibility(8);
    View localView3 = localView1.findViewById(2131230867);
    this.mClearDataButton = ((Button)localView3.findViewById(2131231090));
    this.mMoveAppButton = ((Button)localView3.findViewById(2131231089));
    this.mCacheSize = ((TextView)localView1.findViewById(2131230871));
    this.mClearCacheButton = ((Button)localView1.findViewById(2131230872));
    this.mActivitiesButton = ((Button)localView1.findViewById(2131230875));
    this.mScreenCompatSection = localView1.findViewById(2131230876);
    this.mAskCompatibilityCB = ((CheckBox)localView1.findViewById(2131230877));
    this.mEnableCompatibilityCB = ((CheckBox)localView1.findViewById(2131230878));
    this.mNotificationSwitch = ((CompoundButton)localView1.findViewById(2131230854));
    return localView1;
  }

  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
  {
    if (paramMenuItem.getItemId() == 1)
    {
      uninstallPkg(this.mAppEntry.info.packageName, true, false);
      return true;
    }
    return false;
  }

  public void onPackageIconChanged()
  {
  }

  public void onPackageListChanged()
  {
    refreshUi();
  }

  public void onPackageSizeChanged(String paramString)
  {
    if (paramString.equals(this.mAppEntry.info.packageName))
      refreshSizeInfo();
  }

  public void onPause()
  {
    super.onPause();
    this.mSession.pause();
  }

  public void onPrepareOptionsMenu(Menu paramMenu)
  {
    boolean bool = true;
    if (this.mUpdatedSysApp)
      bool = false;
    while (true)
    {
      paramMenu.findItem(1).setVisible(bool);
      return;
      if (this.mAppEntry == null)
        bool = false;
      else if ((0x1 & this.mAppEntry.info.flags) != 0)
        bool = false;
      else if ((this.mPackageInfo == null) || (this.mDpm.packageHasActiveAdmins(this.mPackageInfo.packageName)))
        bool = false;
      else if (UserHandle.myUserId() != 0)
        bool = false;
      else if (this.mUserManager.getUsers().size() < 2)
        bool = false;
    }
  }

  public void onRebuildComplete(ArrayList<ApplicationsState.AppEntry> paramArrayList)
  {
  }

  public void onResume()
  {
    super.onResume();
    this.mSession.resume();
    if (!refreshUi())
      setIntentAndFinish(true, true);
  }

  public void onRunningStateChanged(boolean paramBoolean)
  {
  }

  class ClearCacheObserver extends IPackageDataObserver.Stub
  {
    ClearCacheObserver()
    {
    }

    public void onRemoveCompleted(String paramString, boolean paramBoolean)
    {
      Message localMessage = InstalledAppDetails.this.mHandler.obtainMessage(3);
      if (paramBoolean);
      for (int i = 1; ; i = 2)
      {
        localMessage.arg1 = i;
        InstalledAppDetails.this.mHandler.sendMessage(localMessage);
        return;
      }
    }
  }

  class ClearUserDataObserver extends IPackageDataObserver.Stub
  {
    ClearUserDataObserver()
    {
    }

    public void onRemoveCompleted(String paramString, boolean paramBoolean)
    {
      int i = 1;
      Message localMessage = InstalledAppDetails.this.mHandler.obtainMessage(i);
      if (paramBoolean);
      while (true)
      {
        localMessage.arg1 = i;
        InstalledAppDetails.this.mHandler.sendMessage(localMessage);
        return;
        i = 2;
      }
    }
  }

  static class DisableChanger extends AsyncTask<Object, Object, Object>
  {
    final WeakReference<InstalledAppDetails> mActivity;
    final ApplicationInfo mInfo;
    final PackageManager mPm;
    final int mState;

    DisableChanger(InstalledAppDetails paramInstalledAppDetails, ApplicationInfo paramApplicationInfo, int paramInt)
    {
      this.mPm = paramInstalledAppDetails.mPm;
      this.mActivity = new WeakReference(paramInstalledAppDetails);
      this.mInfo = paramApplicationInfo;
      this.mState = paramInt;
    }

    protected Object doInBackground(Object[] paramArrayOfObject)
    {
      this.mPm.setApplicationEnabledSetting(this.mInfo.packageName, this.mState, 0);
      return null;
    }
  }

  public static class MyAlertDialogFragment extends DialogFragment
  {
    public static MyAlertDialogFragment newInstance(int paramInt1, int paramInt2)
    {
      MyAlertDialogFragment localMyAlertDialogFragment = new MyAlertDialogFragment();
      Bundle localBundle = new Bundle();
      localBundle.putInt("id", paramInt1);
      localBundle.putInt("moveError", paramInt2);
      localMyAlertDialogFragment.setArguments(localBundle);
      return localMyAlertDialogFragment;
    }

    InstalledAppDetails getOwner()
    {
      return (InstalledAppDetails)getTargetFragment();
    }

    public Dialog onCreateDialog(Bundle paramBundle)
    {
      int i = getArguments().getInt("id");
      int j = getArguments().getInt("moveError");
      switch (i)
      {
      default:
        throw new IllegalArgumentException("unknown id " + i);
      case 1:
        return new AlertDialog.Builder(getActivity()).setTitle(getActivity().getText(2131428418)).setIconAttribute(16843605).setMessage(getActivity().getText(2131428419)).setPositiveButton(2131428420, new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            InstalledAppDetails.MyAlertDialogFragment.this.getOwner().initiateClearUserData();
          }
        }).setNegativeButton(2131428421, null).create();
      case 2:
        return new AlertDialog.Builder(getActivity()).setTitle(getActivity().getText(2131428425)).setIconAttribute(16843605).setMessage(getActivity().getText(2131428426)).setPositiveButton(2131428420, new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            InstalledAppDetails.MyAlertDialogFragment.this.getOwner().uninstallPkg(InstalledAppDetails.MyAlertDialogFragment.this.getOwner().mAppEntry.info.packageName, false, false);
          }
        }).setNegativeButton(2131428421, null).create();
      case 3:
        return new AlertDialog.Builder(getActivity()).setTitle(getActivity().getText(2131428422)).setIconAttribute(16843605).setMessage(getActivity().getText(2131428422)).setNeutralButton(getActivity().getText(2131428420), new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            InstalledAppDetails.MyAlertDialogFragment.this.getOwner().setIntentAndFinish(true, true);
          }
        }).create();
      case 4:
        return new AlertDialog.Builder(getActivity()).setTitle(getActivity().getText(2131428427)).setIconAttribute(16843605).setMessage(getActivity().getText(2131428428)).setNeutralButton(2131428420, new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            InstalledAppDetails.MyAlertDialogFragment.this.getOwner().mClearDataButton.setEnabled(false);
            InstalledAppDetails.MyAlertDialogFragment.this.getOwner().setIntentAndFinish(false, false);
          }
        }).create();
      case 5:
        return new AlertDialog.Builder(getActivity()).setTitle(getActivity().getText(2131428450)).setIconAttribute(16843605).setMessage(getActivity().getText(2131428451)).setPositiveButton(2131428420, new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            InstalledAppDetails.MyAlertDialogFragment.this.getOwner().forceStopPackage(InstalledAppDetails.MyAlertDialogFragment.this.getOwner().mAppEntry.info.packageName);
          }
        }).setNegativeButton(2131428421, null).create();
      case 6:
        Activity localActivity = getActivity();
        Object[] arrayOfObject = new Object[1];
        arrayOfObject[0] = getOwner().getMoveErrMsg(j);
        String str = localActivity.getString(2131428453, arrayOfObject);
        return new AlertDialog.Builder(getActivity()).setTitle(getActivity().getText(2131428452)).setIconAttribute(16843605).setMessage(str).setNeutralButton(2131428420, null).create();
      case 7:
        return new AlertDialog.Builder(getActivity()).setTitle(getActivity().getText(2131428456)).setIconAttribute(16843605).setMessage(getActivity().getText(2131428457)).setPositiveButton(2131428420, new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            InstalledAppDetails.DisableChanger localDisableChanger = new InstalledAppDetails.DisableChanger(InstalledAppDetails.MyAlertDialogFragment.this.getOwner(), InstalledAppDetails.MyAlertDialogFragment.this.getOwner().mAppEntry.info, 3);
            Object[] arrayOfObject = new Object[1];
            arrayOfObject[0] = ((Object)null);
            localDisableChanger.execute(arrayOfObject);
          }
        }).setNegativeButton(2131428421, null).create();
      case 8:
        return new AlertDialog.Builder(getActivity()).setTitle(getActivity().getText(2131428460)).setIconAttribute(16843605).setMessage(getActivity().getText(2131428461)).setPositiveButton(2131428420, new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            InstalledAppDetails.MyAlertDialogFragment.this.getOwner().setNotificationsEnabled(false);
          }
        }).setNegativeButton(2131428421, new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            InstalledAppDetails.MyAlertDialogFragment.this.getOwner().mNotificationSwitch.setChecked(true);
          }
        }).create();
      case 9:
      }
      return new AlertDialog.Builder(getActivity()).setTitle(getActivity().getText(2131428458)).setIconAttribute(16843605).setMessage(getActivity().getText(2131428459)).setPositiveButton(2131428420, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          InstalledAppDetails.MyAlertDialogFragment.this.getOwner().uninstallPkg(InstalledAppDetails.MyAlertDialogFragment.this.getOwner().mAppEntry.info.packageName, false, true);
        }
      }).setNegativeButton(2131428421, null).create();
    }
  }

  class PackageMoveObserver extends IPackageMoveObserver.Stub
  {
    PackageMoveObserver()
    {
    }

    public void packageMoved(String paramString, int paramInt)
      throws RemoteException
    {
      Message localMessage = InstalledAppDetails.this.mHandler.obtainMessage(4);
      localMessage.arg1 = paramInt;
      InstalledAppDetails.this.mHandler.sendMessage(localMessage);
    }
  }

  private static class PremiumSmsSelectionListener
    implements AdapterView.OnItemSelectedListener
  {
    private final String mPackageName;
    private final ISms mSmsManager;

    PremiumSmsSelectionListener(String paramString, ISms paramISms)
    {
      this.mPackageName = paramString;
      this.mSmsManager = paramISms;
    }

    private void setPremiumSmsPermission(String paramString, int paramInt)
    {
      try
      {
        if (this.mSmsManager != null)
          this.mSmsManager.setPremiumSmsPermission(paramString, paramInt);
        return;
      }
      catch (RemoteException localRemoteException)
      {
      }
    }

    public void onItemSelected(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
    {
      if ((paramInt >= 0) && (paramInt < 3))
      {
        Log.d("InstalledAppDetails", "Selected premium SMS policy " + paramInt);
        setPremiumSmsPermission(this.mPackageName, paramInt + 1);
        return;
      }
      Log.e("InstalledAppDetails", "Error: unknown premium SMS policy " + paramInt);
    }

    public void onNothingSelected(AdapterView<?> paramAdapterView)
    {
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.applications.InstalledAppDetails
 * JD-Core Version:    0.6.2
 */